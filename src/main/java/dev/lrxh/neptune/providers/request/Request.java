package dev.lrxh.neptune.providers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Request {
    private final UUID sender;
}
