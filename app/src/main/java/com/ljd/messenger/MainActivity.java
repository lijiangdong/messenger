package com.ljd.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.messenger_linear)
    LinearLayout mShowLinear;

    private Messenger mMessenger;
    private final String LETTER_CHAR = "abcdefghijkllmnopqrstuvwxyz";




    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    TextView textView = new TextView(MainActivity.this);
                    textView.setText("convert ==>:"
                            + (msg.getData().containsKey("service")?msg.getData().getString("service"):""));
                    mShowLinear.addView(textView);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindService();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bindService();
    }

    @OnClick({R.id.test_button,R.id.clear_button})
    public void onClickButton(View v){
        switch (v.getId()){
            case R.id.test_button:
                sendToService();
                break;
            case R.id.clear_button:
                mShowLinear.removeAllViews();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        ButterKnife.unbind(this);
    }

    private void bindService(){
        Intent intent = new Intent(MainActivity.this,MessengerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendToService(){
        Message messageClient = Message.obtain(null,0);
        //用于传递给服务端回复的Messenger
        Messenger replyMessenger = new Messenger(new ClientHandler());
        Bundle bundle = new Bundle();
        bundle.putString("client",generateMixString());
        messageClient.setData(bundle);
        //通过Message的replyTo属性将Messenger对象传递到服务端
        messageClient.replyTo = replyMessenger;
        TextView textView = new TextView(MainActivity.this);
        textView.setText("send:" + (bundle.getString("client")));
        mShowLinear.addView(textView);
        try {
            mMessenger.send(messageClient);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 随机生成10位小写字母的字符串
     * @return
     */
    public String generateMixString() {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(LETTER_CHAR.charAt(random.nextInt(LETTER_CHAR.length())));
        }
        return sb.toString();
    }
}
