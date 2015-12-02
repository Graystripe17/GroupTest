package gaga.minty.com.pocketcommunicate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    final int GAP_TIME = 200;
    final int SHORT_TIME = 400;
    final int LONG_TIME = 800;

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    boolean stopAllVibrations = false;

    public void onReceive(Context context, Intent intent) {

        // Show Alert
        int duration = Toast.LENGTH_LONG;


        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);


                    Toast boast = Toast.makeText(context,
                            "senderNum: " + senderNum + ", message: " + message, duration);
                    boast.show();

                    if(stopAllVibrations) {
                        break;
                    } else if(message.startsWith("New User")) {
                        mChannel.currentNumberListeners.add(message.substring(9));
                        break;
                    } else if (message.startsWith("ABORT")) {
                        stopAllVibrations = true;
                        mChannel.currentNumberListeners.clear();
                        break;
                    } else {
                        // Turn dots and dashes into vibration pattern
                        long[] vibSequence = new long[message.length() * 2];
                        for (int index = 0; index < message.length() * 2; index++) {
                            vibSequence[index++] = GAP_TIME;
                            if (message.charAt(index / 2) == '.') {
                                vibSequence[index] = SHORT_TIME;
                            } else if (message.charAt(index / 2) == '-') {
                                vibSequence[index] = LONG_TIME;
                            }
                        }
                        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(vibSequence, -1);
                    }
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }


}
