package dev.reed.asyncmarspicservice.service;

import dev.reed.asyncmarspicservice.config.RabbitMQProperties;
import dev.reed.asyncmarspicservice.dto.CreateCommandRequest;
import dev.reed.asyncmarspicservice.dto.CreateCommandResponse;
import dev.reed.asyncmarspicservice.dto.RabbitMessage;
import dev.reed.asyncmarspicservice.entity.Command;
import dev.reed.asyncmarspicservice.reposisory.CommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandService {

    private final CommandRepository commandRepository;
    private final NasaClientService nasaClientService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    @Transactional
    public CreateCommandResponse createCommand(final CreateCommandRequest request) {
        var command = new Command();
        Command savedCommand = this.commandRepository.save(command);
        this.rabbitTemplate.convertAndSend(
                rabbitMQProperties.getExchange(),
                rabbitMQProperties.getRoutingKey(),
                RabbitMessage.of(request.sol(), savedCommand.getCommandId())
        );
        return new CreateCommandResponse(savedCommand.getCommandId());
    }

    @Transactional(readOnly = true)
    public byte[] findPicByCommandId(final Long commandId) {
        Objects.requireNonNull(commandId);
        Command command = this.commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Command with id=%d not found!".formatted(commandId)));
        return this.nasaClientService.getNasaPicture(command.getUrl());
    }

    @RabbitListener(queues = "nasa-pic-queue")
    @Transactional
    public void findLargestPic(final RabbitMessage rabbitMessage) {
        String picUrl = nasaClientService.findLargestNasaPic(rabbitMessage.getSol());
        this.commandRepository.findById(rabbitMessage.getCommandId())
                .ifPresent(command -> {
                    command.setUrl(picUrl);
                    this.commandRepository.save(command);
                });
    }
}
