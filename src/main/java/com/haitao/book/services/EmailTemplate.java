package com.haitao.book.services;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACTIVATE_ACCOUNT("activation_account");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
