package com.haitao.book.services.mapers;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACTIVATE_ACCOUNT("activation_account");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
