package com.example.demo.utils;

import com.example.demo.event.dto.BaseEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelperClass {
    private static final ObjectMapper mapper = new ObjectMapper();
    public <T> BaseEvent<T> fromByteToEvent(byte[] bytes, Class<T> klass) throws IOException {
        TypeReference<BaseEvent<T>> typeRef =
                new TypeReference<>() {
                    @Override
                    public Type getType() {
                        return mapper.getTypeFactory().constructParametricType(BaseEvent.class, klass);
                    }
                };
        return mapper.readValue(bytes, typeRef);
    }
}
