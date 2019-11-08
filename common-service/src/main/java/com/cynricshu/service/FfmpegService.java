// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * FfmpegService
 *
 * @author Zhai Yao(zhaiyao@baidu.com)
 */
@Slf4j
@Service
public class FfmpegService {
    @Value("${ffmpeg.command:ffmpeg}")
    private String ffmpegCommand;

    public void doFfmpeg(String sourceFile, String targetFile) {
        try {
            Process process = Runtime.getRuntime().exec(new String[] {
                    ffmpegCommand,
                    "-i",
                    sourceFile,
                    "-acodec",
                    "pcm_s16le",
                    "-ar",
                    "16000",
                    "-ac",
                    "1",
                    targetFile
            });
            process.waitFor();
        } catch (Exception e) {
            log.error("failed to execute ffmpeg command", e);
        }
    }
}
