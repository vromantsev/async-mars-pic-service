package dev.reed.asyncmarspicservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory,
                                         final MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public RestClient restClient(final AppProperties appProperties) {
        return RestClient.create(appProperties.getBaseUrl());
    }

    @Bean
    public Queue queue() {
        return new Queue(rabbitMQProperties.getPicQueue());
    }

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.directExchange(rabbitMQProperties.getExchange()).build();
    }

    @Bean
    public Binding binding(final Queue queue, final Exchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(rabbitMQProperties.getRoutingKey())
                .noargs();
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("dev.reed.asyncmarspicservice.dto");
        converter.setClassMapper(typeMapper);
        return converter;
    }
}
