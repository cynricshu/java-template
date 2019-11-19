package com.cynricshu.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.io.IOUtils;

/**
 * 2019/11/19 21:28
 *
 * @author Cynric Shu
 */
public class AudioConvertUtil {

    public static byte[] pcm8kToAlaw(byte[] sourceBytes) throws IOException {
        AudioFormat audioFormat = new AudioFormat(8000, 16, 1, true, false);
        AudioInputStream sourceAudioInputStream = new AudioInputStream(
                new ByteArrayInputStream(sourceBytes), audioFormat, sourceBytes.length);

        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.ALAW,
                8000, 8, 1, audioFormat.getFrameSize() / 2, audioFormat.getFrameRate(), false);
        AudioInputStream targetAudioInputStream = AudioSystem.getAudioInputStream(targetFormat, sourceAudioInputStream);

        return IOUtils.toByteArray(targetAudioInputStream);
    }
}
