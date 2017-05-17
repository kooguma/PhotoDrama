package com.loopeer.android.photodrama4android.ui.activity;

import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.loopeer.android.photodrama4android.model.Voice;
import io.reactivex.Flowable;
import java.util.List;


public class MusicSelectActivity extends PhotoDramaBaseActivity {

    // private MediaPlayer mPlayer;
    // private MusicClipAddAdapter mMusicSelectAdapter;
    // private AudioFetchHelper mAudioFetchHelper;
    // private MusicClipView mMusicClipView;
    // private TimerTask mTimerTask;
    // private Timer mTimer = new Timer();
    // private boolean mIsPrepared;
    // private float mStartPos;
    // private float mEndPos;
    // private MusicClipView.IndicatorMoveListener mIndicatorMoveListener
    //     = new MusicClipView.IndicatorMoveListener() {
    //     @Override public void onLeftIndicatorMove(float position) {
    //         mStartPos = position;
    //         if (mIsPrepared) {
    //             final int mesc = (int) (position * mPlayer.getDuration());
    //             mPlayer.seekTo(mesc);
    //         }
    //     }
    //
    //     @Override public void onRightIndicatorMove(float position) {
    //         mEndPos = position;
    //     }
    // };
    //
    // @Override protected void onCreate(Bundle savedInstanceState) {
    //     super.onCreate(savedInstanceState);
    //     setContentView(R.layout.fragment_simple_list);
    //     mAudioFetchHelper = new AudioFetchHelper(this);
    //     mMusicSelectAdapter = new MusicClipAddAdapter(this);
    //     mMusicSelectAdapter.setIMusicAdapter(this);
    //     mTimerTask = new TimerTask() {
    //         @Override public void run() {
    //             runOnUiThread(new Runnable() {
    //                 @Override public void run() {
    //                     if (mPlayer.isPlaying()) {
    //                         final int duration = mPlayer.getDuration();
    //                         final int curPosition = mPlayer.getCurrentPosition();
    //                         final float progress = (float) curPosition / duration;
    //                         if (progress >= mEndPos) { //check if end
    //                             final int mesc = (int) (mStartPos * mPlayer.getDuration());
    //                             mPlayer.seekTo(mesc);
    //                         } else {
    //                             mMusicClipView.setDotProgress((int) (progress * 100));
    //                         }
    //                     }
    //
    //                 }
    //             });
    //         }
    //     };
    //     setupMediaPlayer();
    // }
    //
    // @Override protected void onPostCreate(Bundle savedInstanceState) {
    //     super.onPostCreate(savedInstanceState);
    //     setCenterTitle(R.string.label_music_select);
    // }
    //
    // private void setupMediaPlayer() {
    //     mPlayer = new MediaPlayer();
    //     mPlayer.reset();
    //     mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    //     mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
    //         @Override public void onPrepared(MediaPlayer mp) {
    //             mIsPrepared = true;
    //             mStartPos = 0f;
    //             mEndPos = 1.0f;
    //             mp.start();
    //             mTimer.schedule(mTimerTask, 0, 100);
    //         }
    //     });
    //     mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
    //         @Override public boolean onError(MediaPlayer mp, int what, int extra) {
    //             return false;
    //         }
    //     });
    // }
    //
    // @Override protected void onResume() {
    //     super.onResume();
    //     mPlayer.start();
    // }
    //
    // @Override public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
    //     return mMusicSelectAdapter;
    // }
    //
    // @Override public Flowable<BaseResponse<List<Voice>>> requestData(String page, String pageSize) {
    //     return VoiceService.INSTANCE.voices("1");
    // }
    //
    // @Override public int getExtraItemCount() {
    //     return 0;
    // }
    //
    // @Override public void onAddAudioClick(Voice voice) {
    //     MusicClip clip = new MusicClip();
    //     clip.path = FileManager.getAudioPath(this, voice);
    //     clip.startTime = (int) (mStartPos * mPlayer.getDuration());
    //     clip.showTime = (int) ((mEndPos - mStartPos) * mPlayer.getDuration());
    //     clip.musicType = MusicClip.MusicType.BGM;
    //     Log.e("TAG", "clip = " + clip.toString());
    // }
    //
    // @Override public void onDownloadClick(Voice voice, View v) {
    //     final Button btn = (Button) v;
    //     mAudioFetchHelper.getAudio(voice, status -> {
    //         btn.setText(status.getPercent());
    //     }, throwable -> {
    //         Toaster.showToast("下载失败：" + throwable.getMessage());
    //         btn.setText("失败");
    //     }, () -> {
    //         Toaster.showToast("下载完成");
    //         ((Button) v).setText("已下载");
    //         btn.setEnabled(false);
    //     });
    // }
    //
    // @Override public void onAudioPlayClick(String path, MusicClipView clipView) {
    //     Log.e("TAG", "path = " + path);
    //     mMusicClipView = clipView;
    //     mMusicClipView.setIndicatorMoveListener(mIndicatorMoveListener);
    //     if (!mIsPrepared) {
    //         try {
    //             mPlayer.setDataSource(path);
    //             mPlayer.prepareAsync();
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     } else {
    //         mPlayer.start();
    //     }
    // }
    //
    // @Override public void onAudioPauseClick(String path, MusicClipView clipView) {
    //     mMusicClipView = clipView;
    //     mPlayer.pause();
    // }
    //
    // @Override protected void onPause() {
    //     super.onPause();
    //     mPlayer.pause();
    // }
    //
    // @Override protected void onDestroy() {
    //     super.onDestroy();
    //     mAudioFetchHelper.unSubscribe();
    //     mPlayer.release();
    //     mTimerTask.cancel();
    //     mTimer.cancel();
    // }

}
