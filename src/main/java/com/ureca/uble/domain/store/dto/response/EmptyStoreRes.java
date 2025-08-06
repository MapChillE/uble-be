package com.ureca.uble.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EmptyStoreRes implements Serializable {
    private final boolean empty = true;
}
