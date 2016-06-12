package com.nextdever.boxlidexample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BoxLid.OnBoxLidOpenedListener, BoxLid.OnBoxLidClosedListener {

    private BoxLid vBoxLid;

    private int keyBackClickCount = 0;

    private static final int HANDLER_BOXLID_LOADING_COMPLETE = 0;
    private static final int HANDLER_BOXLID_OPEN = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_BOXLID_LOADING_COMPLETE:
                    vBoxLid.loadingComplete();
                    sendEmptyMessageDelayed(HANDLER_BOXLID_OPEN, 200);
                    break;
                case HANDLER_BOXLID_OPEN:
                    vBoxLid.open();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vBoxLid = (BoxLid) findViewById(R.id.main_boxlid);
        //初始化BoxLid
        vBoxLid.setTopHalfOrnamental(R.mipmap.img_tattoo_fly_white_downward);
        vBoxLid.setBoxLidTitle(getResources().getString(R.string.app_name));
        vBoxLid.setBottomHalfOrnamental(R.mipmap.img_tattoo_fly_white_upward);
        vBoxLid.setBoxLidTitleTextSize(R.dimen.textSize_body);
        vBoxLid.setCenterOrnamental(R.mipmap.img_tattoo_flower_white);
        vBoxLid.setBoxLidColor(getResources().getColor(R.color.titleBarBackgroundColor));
        vBoxLid.setBoxLidTitleColor(getResources().getColor(R.color.titleColor));
        vBoxLid.setOnBoxLidOpenedListener(this);
        vBoxLid.setOnBoxLidClosedListener(this);

        vBoxLid.loading();
        mHandler.sendEmptyMessageDelayed(HANDLER_BOXLID_LOADING_COMPLETE, 2000);
    }

    @Override
    public void onBoxLidOpened() {
        vBoxLid.setVisibility(View.INVISIBLE);
        //打开盒子后重置返回键统计
        keyBackClickCount = 0;
    }

    @Override
    public void onBoxLidClosed() {
        //如果在Closing状态中连按2次以上返回键则直接退出
        if (keyBackClickCount >= 2) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//连按两次退出程序，使用关闭盒子的时间
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (vBoxLid.getBoxLidStatus() == BoxLid.BOXLID_STATUS_OPENING)
                return true;
            keyBackClickCount++;
            //App启动加载数据时，点击返回直接退出
            if (vBoxLid.getBoxLidStatus() == BoxLid.BOXLID_STATUS_LOADING) {
                finish();
            } else if (vBoxLid.getBoxLidStatus() != BoxLid.BOXLID_STATUS_CLOSING && vBoxLid.getBoxLidStatus() != BoxLid.BOXLID_STATUS_CLOSED) {
                //第一次点击的时候提示，并关闭盒子
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.exit_to_app), Toast.LENGTH_SHORT).show();
                vBoxLid.setVisibility(View.VISIBLE);
                vBoxLid.close();
            } else if (vBoxLid.getBoxLidStatus() == BoxLid.BOXLID_STATUS_CLOSED) {
                //关闭盒子状态下，点击返回则退出
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
