// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.exception;

import lombok.Getter;

/**
 * ConsoleException
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public class ConsoleException extends Exception {
    @Getter
    private ConsoleErrorCode errorCode;

    public ConsoleException(ConsoleErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ConsoleException(String message, ConsoleErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
