package com.R4B.client.api;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

final class ApiTypes {
    private ApiTypes() {}

    static JavaType list(Class<?> elementType) {
        return TypeFactory.defaultInstance().constructCollectionType(List.class, elementType);
    }
}
