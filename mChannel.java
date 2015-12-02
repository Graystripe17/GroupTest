package gaga.minty.com.pocketcommunicate;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class mChannel extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    String nameOfChannel;
    String phoneNumber;
    String p1, p2, p3, p4, p5;
    final static String DEBUG_TAG = "mChannel";
    private GestureDetectorCompat mDetector;
    private int mActivePointerId;
    private int abortCounter = 0;
    private long timeSinceLastAbortFling;
    private long timeSinceLastPulse;
    static ArrayList<String> currentNumberListeners = new ArrayList<String>();
    ArrayList<Boolean> message = new ArrayList<Boolean>();

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        nameOfChannel = getIntent().getStringExtra("nameOfChannel");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        String[] phoneListeners = new String[5];
        phoneListeners[0] = getIntent().getStringExtra("p1");
        phoneListeners[1] = getIntent().getStringExtra("p2");
        phoneListeners[2] = getIntent().getStringExtra("p3");
        phoneListeners[3] = getIntent().getStringExtra("p4");
        phoneListeners[4] = getIntent().getStringExtra("p5");

        for(String key : phoneListeners) {
            if(!key.isEmpty()) {
                currentNumberListeners.add(key);
            }
        }

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        broadcastJoin();



        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    private void broadcastMessage() {
        String sb = "";
        for(Boolean bit : message) {
            if(bit) {
                sb += "-";
            } else {
                sb += ".";
            }
        }
        if(!sb.isEmpty()) {
            SmsManager smsManager = SmsManager.getDefault();
            for (String targetNumber : currentNumberListeners) {
                // If the number is your own, do not send a text
                if(phoneNumber.compareTo(targetNumber) != 0) {
                    smsManager.sendTextMessage(targetNumber, null, sb, null, null);
                }
            }
        }
    }

    private void broadcastJoin() {
        SmsManager smsManager = SmsManager.getDefault();
        for(String targetNumber : currentNumberListeners) {
            if(phoneNumber.compareTo(targetNumber) != 0) {
                smsManager.sendTextMessage(targetNumber, null, "New User " + nameOfChannel + " "+ targetNumber, null, null);
            }
        }
    }

    public void abort() {
        SmsManager smsManager = SmsManager.getDefault();
        for(String targetNumber : currentNumberListeners) {
            // You should text yourself abort to mute all vibrations
            smsManager.sendTextMessage(targetNumber, null, "ABORT", null, null);
        }
        super.onBackPressed();
        System.exit(0);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);

        mActivePointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(mActivePointerId);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);


        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }



    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        // onFling terminates message
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());

        broadcastMessage();

        Toast boast = Toast.makeText(this,
                "MESSAGESENT" + message, Toast.LENGTH_LONG);
        boast.show();

        message.clear();
        return true;
    }


    @Override
    public void onLongPress(MotionEvent event) {
        // Long Press sends bool code 1
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        message.add(true);
    }



    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        // Single Tap sends a bool code 0
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        message.add(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Abort

        if(System.currentTimeMillis() - timeSinceLastAbortFling > 3000) {
            // Now minus last fling is more than 3 seconds
            abortCounter = 0;
        }
        timeSinceLastAbortFling = System.currentTimeMillis();
        abortCounter++;
        if(abortCounter > 2) {
            abort();
        }
        super.onBackPressed();
    }








    // USELESS

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // USELESS
        // Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        // USELESS
        // Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        // USELESS
        // Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        // Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        // USELESS
        // Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        // Double Tap does nothing
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

}