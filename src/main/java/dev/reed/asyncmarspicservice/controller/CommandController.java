package dev.reed.asyncmarspicservice.controller;

import dev.reed.asyncmarspicservice.dto.CreateCommandRequest;
import dev.reed.asyncmarspicservice.dto.CreateCommandResponse;
import dev.reed.asyncmarspicservice.service.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/mars/pictures/largest/command")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    @PostMapping
    public ResponseEntity<CreateCommandResponse> create(@RequestBody final CreateCommandRequest request) {
        var response = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .pathSegment(String.valueOf(response.commandId()))
                                .build()
                                .toUri()
                )
                .body(response);
    }

    @GetMapping(value = "/{commandId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getLargestPic(@PathVariable("commandId") final Long commandId) {
        return ResponseEntity.ok(commandService.findPicByCommandId(commandId));
    }
}
