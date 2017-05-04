package com.loopeer.android.photodrama4android.media.recorder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.util.LinkedList;

public class MediaVideoEncoder extends MediaEncoder {
	private static final boolean DEBUG = false;	// TODO set false on release
	private static final String TAG = "MediaVideoEncoder";


	private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
	private static final int FRAME_RATE = 30;               // 30fps
	private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames

    private final int mWidth;
	private final int mHeight;
	private final int mBitRate;
    private Surface mInputSurface;
	private long time = 0;
    private LinkedList<Long> mUsedTimes = new LinkedList<>();

	public MediaVideoEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, final int width, final int height, final int bitRate) {
		super(muxer, listener);
		if (DEBUG) Log.i(TAG, "MediaVideoEncoder: ");
		mWidth = width;
		mHeight = height;
		mBitRate = bitRate;
	}

	@Override
	protected void prepare() throws IOException {
		if (DEBUG) Log.i(TAG, "prepare: ");
		mTrackIndex = -1;
		mMuxerStarted = mIsEOS = false;

		MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
		format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
				MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
		format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);//码率(kbps)=文件大小(字节)X8 /时间(秒)/1000
		format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
		format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
		mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
		mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		mInputSurface = mMediaCodec.createInputSurface();
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
    protected void signalEndOfInputStream() {
		if (DEBUG) Log.d(TAG, "sending EOS to encoder");
		mMediaCodec.signalEndOfInputStream();	// API >= 18
		mIsEOS = true;
	}

	public Surface getInputSurface() {
		return mInputSurface;
	}

	@Override
	protected long getPTSUs() {
        long result;
        synchronized (mSync) {
            result = mUsedTimes.poll();
        }
		if (result < prevOutputPTSUs || result == prevOutputPTSUs) {
			if (DEBUG) Log.e(TAG, "time => video : " + prevOutputPTSUs + " : " + mBufferInfo.presentationTimeUs);
			result = prevOutputPTSUs;
		}
		return result;
	}

    public void putTime(long useTime) {
        synchronized (mSync) {
            mUsedTimes.add(useTime);
        }
    }
}
