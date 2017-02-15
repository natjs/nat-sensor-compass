package com.nat.sensor_compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xuqinchao on 17/2/6.
 * Copyright (c) 2017 Nat. All rights reserved.
 */

public class HLCompassModule{

    private HLModuleResultListener mListener;
    private float mHeading;
    private Timer timer;
    int interval = 100;
    private SensorManager mWatchSensorManager;

    private Context mContext;
    private static volatile HLCompassModule instance = null;

    private HLCompassModule(Context context){
        mContext = context;
    }

    public static HLCompassModule getInstance(Context context) {
        if (instance == null) {
            synchronized (HLCompassModule.class) {
                if (instance == null) {
                    instance = new HLCompassModule(context);
                }
            }
        }

        return instance;
    }

    public void get(final HLModuleResultListener listener){
        if (listener == null)return;
        final SensorManager sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        int sensorType = Sensor.TYPE_ORIENTATION;
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
                    float heading = sensorEvent.values[0];

                    HashMap<String, Float> result = new HashMap<String, Float>();
                    result.put("heading", heading);
                    listener.onResult(result);
                    sm.unregisterListener(this);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, sm.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void watch(HashMap<String, Integer> option, final HLModuleResultListener listener){

        if (mWatchSensorManager != null)return;
        mListener = listener;
        if (option.containsKey("interval")) {
            interval = option.get("interval");
        }

        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, interval);
        mWatchSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        int sensorType = Sensor.TYPE_ORIENTATION;
        mWatchSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
                    if (mClearWatch) {
                        mWatchSensorManager.unregisterListener(this);
                        mClearWatch = false;
                        if (timer != null) {
                            timer.cancel();
                        }
                        mWatchSensorManager = null;
                        return;
                    }
                    float heading = sensorEvent.values[0];
                    mHeading = heading;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, mWatchSensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
    }

    boolean mClearWatch = false;
    public void clearWatch(HLModuleResultListener listener){
        if (listener == null || mWatchSensorManager == null)return;
        mClearWatch = true;
        listener.onResult(null);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mListener != null) {
                HashMap<String, Float> result = new HashMap<String, Float>();
                result.put("heading", mHeading);
                if (mHeading != 0) {
                    mListener.onResult(result);
                }
            }
        }
    }

}
