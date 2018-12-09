// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.exception;

import lombok.Getter;

/**
 * ConsoleRuntimeException
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public class ConsoleRuntimeException extends RuntimeException {
    @Getter
    private ConsoleErrorCode errorCode;

    public ConsoleRuntimeException(ConsoleErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ConsoleRuntimeException(String message, ConsoleErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
