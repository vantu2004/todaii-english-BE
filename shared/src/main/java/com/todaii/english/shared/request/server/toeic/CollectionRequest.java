package com.todaii.english.shared.request.server.toeic;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CollectionRequest {

    @NotNull(message = "Collection name cannot be null")
    @Length(max = 191, message = "Collection name must not exceed 191 characters")
    private String name;

    @NotNull(message = "Collection description cannot be null")
    @Length(max = 1024, message = "Collection description must not exceed 1024 characters")
    private String description;
}
