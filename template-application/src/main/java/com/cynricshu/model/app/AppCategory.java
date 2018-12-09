// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.model.app;

import lombok.Getter;

/**
 * AppCategory
 *
 * @author Shu Lingjie(shulingjie@baidu.com)
 */
public enum AppCategory {
    ASR("语音识别(Automatic Speech Recognition)"),
    TTS("语音合成(Text-to-Speech)");

    @Getter
    private String text;

    AppCategory(String text) {
        this.text = text;
    }
}
