package org.devberat.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootEntity<T> {
    private boolean result;
    private String errorMessage;
    private T data;

    public static <T> RootEntity<T> ok(T data) {
        RootEntity<T> rootEntity = new RootEntity<>();
        rootEntity.setData(data);
        rootEntity.setResult(true);
        return rootEntity;
    }

    public static <T> RootEntity<T> error(String errorMessage) {
        RootEntity<T> rootEntity = new RootEntity<>();
        rootEntity.setResult(false);
        rootEntity.setErrorMessage(errorMessage);
        return rootEntity;
    }
}