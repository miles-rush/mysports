package com.mysports.android.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.mysports.android.R;
//总界面2 发现 播放相关运动视频
public class MediaFragment extends Fragment {
    private View mView;

    private VideoView videoView;
    private MediaController mc;
    private ImageView play;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.media_fragment,container,false);
            videoView = mView.findViewById(R.id.sport_media);
            mc = new MediaController(getContext());


            videoView.setMediaController(mc);
            videoView.canPause();
            mc.setMediaPlayer(videoView);


            play = mView.findViewById(R.id.play_media);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        Toast.makeText(getContext(),"暂停播放",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(),"加载资源",Toast.LENGTH_SHORT).show();
                        String url="http://223.110.243.172/PLTV/3/224/3221227166/index.m3u8";
                        videoView.setVideoPath(url);
                        videoView.requestFocus();
                        videoView.start();
                    }

                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            });
        }

        return mView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dip2px(getContext(),235f));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        }

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mView) {
            ((ViewGroup) mView.getParent()).removeView(mView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //videoView.stopPlayback();
    }

    @Override
    public void onStop() {
        super.onStop();
        //videoView.stopPlayback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (videoView != null) {
            videoView.stopPlayback();
        }

    }
}
