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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MediaAudioEncoder extends MediaEncoder {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "MediaAudioEncoder";

    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 128000;
    private static final int CHANNEL_COUNT = 2;
    private long mRecordStartTime = 0;

    private AudioMixerThread mAudioMixerThread = null;

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

        if (mAudioMixerThread == null) {
            mAudioMixerThread = new AudioMixerThread();
            mAudioMixerThread.start();
        }
    }

    @Override
    protected void release() {
        mAudioMixerThread = null;
        super.release();
    }

    private class AudioMixerThread extends Thread implements MediaDecoder.DecodeProgressCallback {

        private int mDecodingFileCount;
        private Object mSync = new Object();

        @Override
        public void run() {
            List<MediaAudioDecoder> mediaAudioDecoders = new ArrayList<>();
            for (MusicClip musicClip : mAudioGroup.musicClips) {
                MediaAudioDecoder mediaAudioDecoder = new MediaAudioDecoder(musicClip, mAudioMixerThread);
                if (mediaAudioDecoders.contains(mediaAudioDecoder)) continue;
                mediaAudioDecoders.add(mediaAudioDecoder);
            }
            mAudioMixerThread.setDecodingFileCount(mediaAudioDecoders.size());
            for (MediaAudioDecoder mediaAudioDecoder :
                    mediaAudioDecoders) {
                mediaAudioDecoder.startDecode();
            }
            frameAvailableSoon();

            while (true) {
                synchronized (mSync) {
                    if (mDecodingFileCount > 0) {
                        try {
                            mSync.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
            new AudioMixer(mAudioGroup.musicClips, (data, length, presentationTimeUs) -> {
                if (data == null) {
                    encode(null, length, presentationTimeUs + mRecordStartTime);
                    frameAvailableSoon();
                    return;
                }
                if (DEBUG) Log.e(TAG, "mix encode data :   " + data[0] + " : " + data[data.length - 1] +  " time" + presentationTimeUs);
                encode(data, length, presentationTimeUs + mRecordStartTime);
                frameAvailableSoon();
            }).startMux();
        }

        @Override
        public void onFinish() {
            setDecodingFileCount(--mDecodingFileCount);
            synchronized (mSync) {
                mSync.notify();
            }
        }

        public void setDecodingFileCount(int decodeingFileCount) {
            synchronized (mSync) {
                mDecodingFileCount = decodeingFileCount;
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


    protected void encode(byte[] buffer, final int length, final long presentationTimeUs) {
        if (!mIsCapturing) return;
        if (DEBUG) Log.e(TAG, "encode length and presentationTimeUs    : " + length + "  : " + presentationTimeUs);
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        while (mIsCapturing) {
            final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            if (DEBUG) Log.e(TAG, "encode inputBufferIndex : " + inputBufferIndex);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
                if (length <= 0) {
                    mIsEOS = true;
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0,
                            presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length,
                            presentationTimeUs, 0);
                }
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            }
        }
    }


    protected void drain() {
        if (mMediaCodec == null) return;
        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        int encoderStatus, count = 0;
        final MediaMuxerWrapper muxer = mWeakMuxer.get();
        if (muxer == null) {
            Log.w(TAG, "muxer is unexpectedly null");
            return;
        }
        LOOP:
        while (mIsCapturing) {
            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!mIsEOS) {
                    if (++count > 5)
                        break LOOP;
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                if (mMuxerStarted) {    // second time request is error
                    throw new RuntimeException("format changed twice");
                }
                final MediaFormat format = mMediaCodec.getOutputFormat(); // API >= 16
                mTrackIndex = muxer.addTrack(format);
                mMuxerStarted = true;
                if (!muxer.start()) {
                    synchronized (muxer) {
                        while (!muxer.isStarted())
                            try {
                                muxer.wait(100);
                            } catch (final InterruptedException e) {
                                break LOOP;
                            }
                    }
                }
            } else if (encoderStatus < 0) {
                if (DEBUG)
                    Log.w(TAG, "drain:unexpected result from encoder#dequeueOutputBuffer: " + encoderStatus);
            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    if (DEBUG) Log.d(TAG, "drain:BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    count = 0;
                    if (!mMuxerStarted) {
                        throw new RuntimeException("drain:muxer hasn't started");
                    }
                    mBufferInfo.presentationTimeUs = getPTSUs();
                    if (DEBUG) Log.e(TAG, "write presentationTimeUs : " + mBufferInfo.presentationTimeUs);
                    muxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                }
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    mIsCapturing = false;
                    break;      // out of while
                }
            }
        }
    }
}
