package com.evguenidamaskine.remotenotifierclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity {

    private final boolean bLOG = true;
    private final String sTAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bLOG) {
            Log.i(sTAG, "onCreate");
        }
        setContentView(R.layout.activity_main);
    }

    // EOD - stopped means not visible
    @Override
    protected void onStart() {
        super.onStart();
        if (bLOG) {
            Log.i(sTAG, "onStart");
        }
        startService(new Intent(this, RemoteNotifierService.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bLOG) {
            Log.i(sTAG, "onSaveInstanceState");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bLOG) {
            Log.i(sTAG, "onPause");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (bLOG) {
            Log.i(sTAG, "onRestart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bLOG) {
            Log.i(sTAG, "onResume");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (bLOG) {
            Log.i(sTAG, "onStop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bLOG) {
            Log.i(sTAG, "onDestroy");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (bLOG) {
            Log.i(sTAG, "onRestoreInstanceState");
        }
    }


}
