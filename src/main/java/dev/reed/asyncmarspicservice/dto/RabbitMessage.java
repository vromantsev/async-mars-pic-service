package dev.reed.asyncmarspicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMessage implements Serializable {

    private int sol;
    private Long commandId;

    public static RabbitMessage of(int sol, Long commandId) {
        return new RabbitMessage(sol, commandId);
    }
}
