package com.loopeer.android.photodrama4android.media.recorder;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.MD5Util;
import com.loopeer.android.photodrama4android.utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaExtractor.SEEK_TO_CLOSEST_SYNC;

public class MediaAudioDecoder extends MediaDecoder {

    private static final String TAG = "MediaAudioDecoder";

    private MusicClip mMusicClip;
    public String mTempOutPath;

    MediaAudioDecoder(MusicClip musicClip, DecodeProgressCallback callback) {
        super(callback);
        mMusicClip = musicClip;
        mTempOutPath = MD5Util.getMD5Str(musicClip.path + "_" + musicClip.musicStartOffset + "_" + musicClip.musicSelectedLength);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MediaAudioDecoder) {
            return ((MediaAudioDecoder) obj).mTempOutPath.equals(this.mTempOutPath);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            decode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decode() throws IOException {
        File file = new File(FileManager.getInstance().getDecodeAudioFilePath(mMusicClip));
        if (file.exists()) {
            mCallback.onFinish();
        }
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

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        FileOutputStream fosDecoder = new FileOutputStream(FileManager.getInstance().getDecodeAudioFilePath(mMusicClip));
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        try {
            extractor.seekTo(mMusicClip.getSelectStartUs(), SEEK_TO_CLOSEST_SYNC);
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "extractor extract time : " + mMusicClip.getSelectStartUs() + " : " + mMusicClip.getSelectEndUs());
            }
            while (!sawOutputEOS) {
                if (!sawInputEOS) {
                    int inputBufIndex = codec.dequeueInputBuffer(TIMEOUT_USEC);
                    if (inputBufIndex >= 0) {
                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                        int sampleSize = extractor.readSampleData(dstBuf, 0);
                        if (sampleSize < 0) {
                            sawInputEOS = true;
                            codec.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        } else if (extractor.getSampleTime() > mMusicClip.getSelectEndUs()) {
                            sawInputEOS = true;
                            codec.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        } else {
                            long presentationTimeUs = extractor.getSampleTime();
                            codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, 0);
                            extractor.advance();
                        }
                    }
                }
                int res = codec.dequeueOutputBuffer(info, TIMEOUT_USEC);
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
                        fosDecoder.write(data);
                    }

                    codec.releaseOutputBuffer(outputBufIndex, false);

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true;
                        mCallback.onFinish();
                    }

                } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    codecOutputBuffers = codec.getOutputBuffers();
                } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                }
            }
        } finally {
            fosDecoder.close();
            codec.stop();
            codec.release();
            extractor.release();
        }

    }
}
