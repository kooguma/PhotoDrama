package com.loopeer.android.photodrama4android.media.recorder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.AudioGroup;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import java.io.IOException;

public class MediaAudioEncoder extends MediaEncoder {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "MediaAudioEncoder";

    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 128000;
    private static final int CHANNEL_COUNT = 2;
    private long mRecordStartTime = 0;

    private AudioThread mAudioThread = null;

    private AudioGroup mAudioGroup;

    public MediaAudioEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, AudioGroup audioGroup) {
        super(muxer, listener);
        mAudioGroup = audioGroup;
    }

    @Override
    protected void prepare() throws IOException {
        if (DEBUG) Log.v(TAG, "prepare:");
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;
        final MediaCodecInfo audioCodecInfo = selectAudioCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }

        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, MIME_TYPE);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, CHANNEL_COUNT);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        if (DEBUG) Log.i(TAG, "prepare finishing");
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                Log.e(TAG, "prepare:", e);
            }
        }
    }

    @Override
    protected void startRecording() {
        super.startRecording();
        mRecordStartTime = System.nanoTime() / 1000;
        if (mAudioThread == null) {
            mAudioThread = new AudioThread();
            mAudioThread.start();
        }
    }

    @Override
    protected void release() {
        mAudioThread = null;
        super.release();
    }

    private class AudioThread extends Thread {
        @Override
        public void run() {
            MusicClip musicClip = mAudioGroup.musicClips.get(0);
            MediaAudioDecoder mediaAudioDecoder = new MediaAudioDecoder(musicClip);
            try {
                mediaAudioDecoder.decode((buffer, length, presentationTimeUs) -> {
                    encode(buffer, length, presentationTimeUs + mRecordStartTime);
                    frameAvailableSoon();
                });


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static final MediaCodecInfo selectAudioCodec(final String mimeType) {
        MediaCodecInfo result = null;
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (DEBUG) Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (result == null) {
                        result = codecInfo;
                        break LOOP;
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected long getPTSUs() {
        if (prevOutputPTSUs > mBufferInfo.presentationTimeUs) {
            return prevOutputPTSUs;
        }
        return mBufferInfo.presentationTimeUs;
    }


    protected void signalEndOfInputStream() {
        if (DEBUG) Log.d(TAG, "sending EOS to encoder");
        if (!mIsEOS) {
            encode(null, 0, getPTSUs());
        }
    }
}
