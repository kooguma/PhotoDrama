package com.loopeer.android.photodrama4android.media.recorder;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaExtractor.SEEK_TO_CLOSEST_SYNC;

public class MediaAudioRealTimeDecoder {

    private static final String TAG = "MediaAudioDecoder";
    private final static long audioBytesPerSample = 44100 * 16 / 8;

    private MusicClip mMusicClip;

    MediaAudioRealTimeDecoder(MusicClip musicClip) {
        mMusicClip = musicClip;
    }

    public void decode(DecodeCallback callback) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(mMusicClip.path);

        MediaFormat mediaFormat = null;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                extractor.selectTrack(i);
                mediaFormat = format;
                break;
            }
        }

        if (mediaFormat == null) {
            Log.e(TAG, "not a valid file with audio track..");
            extractor.release();
        }
        String mediaMime = mediaFormat.getString(MediaFormat.KEY_MIME);
        MediaCodec codec = MediaCodec.createDecoderByType(mediaMime);
        codec.configure(mediaFormat, null, null, 0);
        codec.start();

        ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
        ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

        final long kTimeOutUs = 5000;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int rawFileSizeCount = 0;
        long audioTimeUs = 0;
        try {
            extractor.seekTo(1000 * 1000 * 36, SEEK_TO_CLOSEST_SYNC);

            while (!sawOutputEOS) {
                if (!sawInputEOS) {
                    int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                    if (inputBufIndex >= 0) {
                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                        int sampleSize = extractor.readSampleData(dstBuf, 0);
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "extractor.readSampleData size : " + sampleSize);
                        }
                        if (sampleSize < 0) {
                            sawInputEOS = true;
                            codec.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        } else if (extractor.getSampleTime() / 1000 > 1000 * 48) {
                            sawInputEOS = true;
                            codec.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        } else {
                            long presentationTimeUs = extractor.getSampleTime();
                            codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, 0);
                            extractor.advance();
                        }
                    }
                }
                int res = codec.dequeueOutputBuffer(info, kTimeOutUs);
                if (res >= 0) {

                    int outputBufIndex = res;
                    if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        codec.releaseOutputBuffer(outputBufIndex, false);
                        continue;
                    }

                    if (info.size != 0) {

                        ByteBuffer outBuf = codecOutputBuffers[outputBufIndex];

                        outBuf.position(info.offset);
                        outBuf.limit(info.offset + info.size);

                        byte[] data = new byte[info.size];
                        outBuf.get(data);
                        if (BuildConfig.DEBUG) Log.e(TAG, "OutputBuffers info size: " + info.size);
                        int offset = 0;
                        while (offset < data.length) {
                            int length = data.length - offset > 4096 ? 4096 : data.length - offset;
                            ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                            byteBuffer.put(data, offset, length);
                            byteBuffer.flip();
                            callback.onDecode(byteBuffer, length, audioTimeUs);
                            audioTimeUs = (long) (1000000 * (rawFileSizeCount / 2.0) / audioBytesPerSample);
                            offset += length;
                            rawFileSizeCount += length;
                        }
                    }

                    codec.releaseOutputBuffer(outputBufIndex, false);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true;
                        callback.onDecode(null, 0, audioTimeUs);
                    }

                } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    codecOutputBuffers = codec.getOutputBuffers();
                } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat oformat = codec.getOutputFormat();
                }
            }
        } finally {
            codec.stop();
            codec.release();
            extractor.release();
        }

    }

    public interface DecodeCallback {
        void onDecode(final ByteBuffer buffer, final int length, final long presentationTimeUs);
    }
}
