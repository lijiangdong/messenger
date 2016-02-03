package com.ljd.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessengerService extends Service {

    private final Messenger mMessenger = new Messenger(new ServiceHandler());
    private class ServiceHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Messenger clientMessenger = msg.replyTo;
                    Message replyMessage = Message.obtain();
                    replyMessage.what = 1;
                    Bundle bundle = new Bundle();
                    //将接收到的字符串转换为大写后发送给客户端
                    bundle.putString("service",
                            msg.getData().containsKey("client")?msg.getData().getString("client").toUpperCase():"");
                    replyMessage.setData(bundle);
                    try {
                        clientMessenger.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
