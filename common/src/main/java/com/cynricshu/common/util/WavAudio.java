package com.cynricshu.common.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

/**
 * WavAudio
 */
@Slf4j
public class WavAudio {
    /**
     * header template
     */
    private static final byte[] WAV_HEAD_TEMP = new byte[] {82, 73, 70, 70, 110, 118, 0, 0, 87, 65, 86, 69,
            102, 109, 116, 32, 16, 0, 0, 0, 1, 0, 1, 0, -128, 62, 0, 0, 0, 125, 0, 0, 2, 0, 16,
            0, 100, 97, 116, 97, 0, 0, 0, 0};
    /**
     * ASCII RIFF
     */
    private static final int RIFF_ID = 1380533830;
    /**
     * ASCII WAVE
     */
    private static final int RIFF_TYPE = 1463899717;
    /**
     * ASCII fmt (注意有空格)
     */
    private static final int FORMAT_ID = 1718449184;

    // riff chunk
    private int riffId;
    private int riffSize;
    private int riffType;
    // format chunk
    private int formatId;
    private int formatSize;
    private short audioFormat;
    private short numChannels;
    private int sampleRate;
    private int byteRate;
    private short blockAlign;
    private short bitsPerSample;
    // data chunk
    private int dataId;
    private int dataSize;
    private byte[] data;
    private int dataIdOffset;
    private int headerOffset;
    private byte[] header;

    public WavAudio(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        // riff chunk
        riffId = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt(0x00);
        riffSize = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt(0x04);
        riffType = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt(0x08);
        // format chunk
        formatId = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt(0x0C);
        formatSize = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt(0x10);
        audioFormat = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getShort(0x14);
        numChannels = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getShort(0x16);
        sampleRate = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt(0x18);
        byteRate = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt(0x1C);
        blockAlign = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getShort(0x20);
        bitsPerSample = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getShort(0x22);
        // int dataId
        int dataIdOffset;
        int nextDataIdOffset = 0x24;

        // data标识非固定在wav头24H-27H处，需要搜索
        String dataId;
        do {
            dataIdOffset = nextDataIdOffset;
            dataId = getDataId(byteBuffer, dataIdOffset);
            dataSize = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt(dataIdOffset + 4);
            // next dataId offset
            nextDataIdOffset += (8 + dataSize);
        } while (!"data".equals(dataId));

        this.dataIdOffset = dataIdOffset;
        this.headerOffset = dataIdOffset + 8;
        data = Arrays.copyOfRange(bytes, dataIdOffset + 8, bytes.length);
        this.header = new byte[0x2c];
        System.arraycopy(bytes, 0, this.header, 0, 0x24);
        System.arraycopy(bytes, dataIdOffset, this.header, 0x24, 8);
        validate();
    }

    public static byte[] normalize(byte[] audio, short numChannels, int sampleRate, short sampleBits) {
        if (validateHeader(audio)) {
            return audio;
        }
        log.debug("accept abnormal audio attach header ");
        return attachHeader(audio, numChannels, sampleRate, sampleBits);
    }

    public static boolean validateHeader(byte[] audio) {
        // todo 校验采样率等其他信息
        byte[] header = new byte[0x2C];
        if (audio.length < 0x2C) {
            return false;
        }
        System.arraycopy(audio, 0, header, 0, 0x2C);
        var byteBuffer = ByteBuffer.wrap(header);
        // riff chunk
        var riffId = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt(0x00);
        var riffType = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt(0x08);
        // format chunk
        var formatId = byteBuffer.order(ByteOrder.BIG_ENDIAN).getInt(0x0C);
        return riffId == RIFF_ID && riffType == RIFF_TYPE && formatId == FORMAT_ID;
    }

    private static byte[] attachHeader(byte[] audio, short numChannels, int sampleRate, short sampleBits) {

        var bytes = new byte[0x2C + audio.length];
        var byteRate = numChannels * sampleRate * sampleBits / 8;

        // copy head template
        System.arraycopy(WAV_HEAD_TEMP, 0, bytes, 0, 44);
        // set channel number
        System.arraycopy(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(numChannels).array(), 0, bytes, 0x16, 2);
        // set sample rate
        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(sampleRate).array(), 0, bytes, 0x18, 4);
        // set sample bits
        System.arraycopy(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                .putShort(sampleBits).array(), 0, bytes, 0x22, 2);
        // set byte rate
        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(byteRate).array(), 0, bytes, 0x1c, 4);
        // set data size
        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(audio.length).array(), 0, bytes, 0x28, 4);

        // copy segment audio data
        System.arraycopy(audio, 0, bytes, 0x2C, audio.length);
        return bytes;
    }

    /**
     * 从buffer中获得offset - (offset+4)长度的数据
     */
    private String getDataId(ByteBuffer byteBuffer, int offset) {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            id.append((char) byteBuffer.get(i + offset));
        }
        return id.toString();
    }

    private void validate() {
        // TODO: more verification
        if (dataSize != data.length) {
            dataSize = data.length;
        }
        if (byteRate <= 0) {
            byteRate = numChannels * sampleRate * bitsPerSample / 8;
        }
        if (byteRate <= 0) {
            throw new IllegalArgumentException("Can not initialize wav audio: byte rate must be greater than 0");
        }
    }

    /**
     * 音频body字节数组增加文件头
     */
    private byte[] addHeaderBytes(byte[] bytes) {

        int offset = 0x2c;
        var res = new byte[offset + bytes.length];
        System.arraycopy(header, 0, res, 0, offset);
        System.arraycopy(bytes, 0, res, offset, bytes.length);

        // modify segment audio data size
        System.arraycopy(ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(bytes.length).array(), 0, res, offset - 4, 4);

        // modify segment audio riff size
        System.arraycopy(ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(bytes.length + offset - 8).array(), 0, res, 0x04, 4);
        return res;
    }

    /**
     * 对音频进行按照时间切分，返回对应的字节数组（不包含音频头）
     */
    private byte[] slice(double startTime, double endTime) {
        if (endTime < startTime) {
            throw new IllegalArgumentException("startTime > endTime");
        }

        var startOffset = mod((int) (byteRate * startTime), 4, false);
        var endOffset = mod((int) (byteRate * endTime), 4, true);

        if (endOffset > dataSize) {
            endOffset = dataSize;
        }
        return allocate(startOffset, endOffset);
    }

    /**
     * 输入input，输出mod的最小倍数，且这个倍数最接近input
     */
    private int mod(int input, int mod, boolean upper) {
        int left = input % mod;
        if (left == 0) {
            return input;
        }
        return upper ? input - left + mod : input - left;
    }

    /**
     * 按照起、止字节位置切分音频
     */
    private byte[] allocate(int startOffset, int endOffset) {
        var bytes = new byte[endOffset - startOffset];
        System.arraycopy(data, startOffset, bytes, 0, endOffset - startOffset);
        return bytes;
    }
}
