package com.loopeer.android.photodrama4android.media.audio.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioParams {

    public Params mParams;

    private AudioParams(Params params) {
        this.mParams = params;
    }

    public static class Params {

        int mSteamType;                                  // AudioManager.STREAM_XXX
        int mFrequency;                                  // 采样率
        int mChannel;                                    // 声道
        int mSampleBit;                                  // 采样精读
        int mMode;

        public Params(int mSteamType,
                      int mFrequency,
                      int mChannel,
                      int mSampleBit,
                      int mMode) {
            this.mSteamType = mSteamType;
            this.mFrequency = mFrequency;
            this.mChannel = mChannel;
            this.mSampleBit = mSampleBit;
            this.mMode = mMode;
        }
    }

    public static class Builder {

        int mSteamType;                                  // AudioManager.STREAM_XXX
        int mFrequency;                                  // 采样率
        int mChannel;                                    // 声道
        int mSampleBit;                                  // 采样精读
        int mMode;

        public Builder() {
            mSteamType = AudioManager.STREAM_MUSIC;
            mFrequency = 44100;
            mChannel = AudioFormat.CHANNEL_IN_STEREO;
            mSampleBit = AudioFormat.ENCODING_PCM_16BIT;
            mMode = AudioTrack.MODE_STREAM;
        }

        public Builder setSteamType(int steamType) {
            mSteamType = steamType;
            return this;
        }

        public Builder setFrequency(int frequency) {
            mFrequency = frequency;
            return this;
        }

        public Builder setChannel(int channel) {
            mChannel = channel;
            return this;
        }

        public Builder setSampleBit(int sampleBit) {
            mSampleBit = sampleBit;
            return this;
        }

        public Builder setMode(int mode) {
            mMode = mode;
            return this;
        }

        public AudioParams build() {
            return new AudioParams(
                new Params(mSteamType, mFrequency, mChannel, mSampleBit, mMode)
            );
        }
    }
}
