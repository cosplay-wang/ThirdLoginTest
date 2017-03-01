package demo.wang.com.cosplay.thirdlogintest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends Activity implements View.OnClickListener{
    TextView qq_tv,wechat_tv,weibo_tv,share_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        qq_tv = (TextView) findViewById(R.id.qq);
        wechat_tv = (TextView) findViewById(R.id.wechat);
        weibo_tv  = (TextView) findViewById(R.id.weibo);
        share_tv = (TextView) findViewById(R.id.share_sdk);
        qq_tv.setOnClickListener(this);
        wechat_tv.setOnClickListener(this);
        weibo_tv.setOnClickListener(this);
        share_tv.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.qq:
                intent.setClass(Main2Activity.this,MainActivity.class);
                break;
            case R.id.wechat:
                intent.setClass(Main2Activity.this,MainActivity.class);
                break;
            case R.id.weibo:
                intent.setClass(Main2Activity.this,WeiBoActivity.class);
                break;
            case R.id.share_sdk:
               // intent.setClass(Main2Activity.this,UmengLoginActivity.class);
                break;

            default:
                intent.setClass(Main2Activity.this,MainActivity.class);

                break;

        }
        Main2Activity.this.startActivity(intent);
    }
}
