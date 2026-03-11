package org.devberat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private MessageType messageType;
    private String ofStatic;

    public String prepareErrorMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageType.getMessage());

        if (ofStatic != null) {
            stringBuilder.append(" : ").append(ofStatic);
        }

        return stringBuilder.toString();
    }
}