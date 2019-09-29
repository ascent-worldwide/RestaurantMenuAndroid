package com.clairvoyant.naijamenu;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.clairvoyant.naijamenu.utils.Constants;
import com.clairvoyant.naijamenu.utils.DownloadVideoService;
import com.clairvoyant.naijamenu.utils.PreferencesUtils;
import com.clairvoyant.naijamenu.utils.Utils;

public class SplashScreen extends AppCompatActivity {

    private static String TAG = SplashScreen.class.getSimpleName();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, " onCreate()");
        mContext = this;
        Utils.setOrientation(mContext);
        setContentView(R.layout.activity_splash_screen);
        initialiseViews();
    }

    private void initialiseViews() {
        checkBrandVideoOnServer();
        startThread();
    }

    private void checkBrandVideoOnServer() {
        boolean loggineIn = PreferencesUtils.getBoolean(mContext, Constants.LOGGED);

        if (Utils.isOnline(this)) {
            if (loggineIn) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, DownloadVideoService.class));
                } else {
                    startService(new Intent(this, DownloadVideoService.class));
                }
            }
        }
    }

    private void startThread() {
        /** set time to splash out */
        final int _slashTime = 3000;
        /** create a thread to show splash up to splash time */
        Thread splashThread = new Thread() {

            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < _slashTime) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    /**
                     * Called after splash times up. Do some action after splash
                     * times up. Here we moved to another main activity class
                     */
                    boolean loggedIn = PreferencesUtils.getBoolean(mContext, Constants.LOGGED);
                    Log.d(TAG, "Video File Name" + PreferencesUtils.getString(mContext, Constants.VIDEO_FILE_NAME));
                    if (loggedIn) {
                        startActivity(new Intent(mContext, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(mContext, LoginActivity.class));
                        stopThread(Thread.currentThread());
                        finish();
                    }
//	        	    startActivity(new Intent(SplashScreenActivity.this, TutorialScreenActivity.class));
//	 				stopThread(Thread.currentThread());
//					finish();
                }
            }
        };
        splashThread.start();
    }

    public synchronized void stopThread(Thread runner) {
        if (runner != null) runner.interrupt();
    }
}
