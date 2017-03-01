package demo.wang.com.cosplay.thirdlogintest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import demo.wang.com.cosplay.thirdlogintest.control.BaseUiListener;
import demo.wang.com.cosplay.thirdlogintest.model.QQLogin;

public class MainActivity extends Activity {
    TextView login_in_tv, show_tv, login_out_tv, login_info_tv ,share_info;
    Tencent mTencent;
    String TAG = "TAG";
    public static String APPID = "1105475673";
    public static String SCOPE = "all";
    String openid, token;
    String expires_in;
    QQAuth mQQAuth;
    BaseUiListener listener;
    QQLogin mQQLogin = new QQLogin();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String msg2 = msg.toString();
            JSONObject response = (JSONObject) msg.obj;
            show_tv.setText(msg2);
            try {
                openid = response.getString("openid");
                token = response.getString("access_token");
                expires_in = response.getString("expires_in");
                mQQLogin.setOpenid(response.getString("openid"));
                mQQLogin.setAccess_token(response.getString("access_token"));
                //设置身份的token
                mTencent.setAccessToken(token, expires_in);
                mTencent.setOpenId(openid);
            } catch (Exception e) {

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login_in_tv = (TextView) findViewById(R.id.click_login);
        login_out_tv = (TextView) findViewById(R.id.click_login_out);
        show_tv = (TextView) findViewById(R.id.click_login_show);
        login_info_tv = (TextView) findViewById(R.id.click_login_info);
        share_info = (TextView) findViewById(R.id.click_share_show);
        listener = new BaseUiListener(handler, MainActivity.this);

        login_in_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mTencent = Tencent.createInstance(APPID, getApplicationContext());
                        mTencent.login(MainActivity.this, SCOPE, listener);
                    }
                }).start();
            }
        });
        login_out_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTencent = Tencent.createInstance(APPID, getApplicationContext());
                mTencent.logout(MainActivity.this);
                Toast.makeText(getApplicationContext(), "登出QQ", Toast.LENGTH_SHORT).show();
            }
        });
        login_info_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QQToken qqToken = mTencent.getQQToken();
                        mQQAuth = QQAuth.createInstance(APPID, MainActivity.this);

                        UserInfo info = new UserInfo(getApplicationContext(), mTencent.getQQToken());
                        info.getUserInfo(new IUiListener() {
                            @Override
                            public void onComplete(Object o) {

                                Message msguser = new Message();
                                msguser.obj = o;
                                msguser.what = 0;
                                handler.sendMessage(msguser);
                            }

                            @Override
                            public void onError(UiError uiError) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
                }).start();
            }
        });
        share_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //onClickShare();
                        shareToQzone ();
                    }
                }).start();

            }
        });
    }

    /**
     * 分享到qq
     */
    private void onClickShare() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用222222");
        mTencent.shareToQQ(MainActivity.this, params, listener);
    }

    /**
     * 分享到qqzore
*/
    private void shareToQzone() {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);//（图文）
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>());//wiki上写着选填，但不填会出错
        mTencent.shareToQzone(MainActivity.this, params, listener);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, listener);

        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, listener);
            }
        }
    }


}
