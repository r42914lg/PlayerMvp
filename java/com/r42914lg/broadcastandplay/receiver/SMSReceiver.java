package com.r42914lg.broadcastandplay.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.r42914lg.broadcastandplay.Constants;
import com.r42914lg.broadcastandplay.service.DownloadService;

public class SMSReceiver extends BroadcastReceiver {
    public static final String KEY = "LOAD_AUDIO=";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (intent.getAction() != SMS_RECEIVED)
            return;

        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (int i = 0; i < messages.length; i++) {
            String smsText = messages[i].getMessageBody();
            if (smsText.contains(KEY)) {
                kickService(smsText.substring(11, smsText.length()));
                break;
            }
        }
    }

    private void kickService(String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.URL_TO_LOAD, url);
        context.startService(intent);
    }
}
