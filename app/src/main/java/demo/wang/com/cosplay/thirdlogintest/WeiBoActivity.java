package demo.wang.com.cosplay.thirdlogintest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.tencent.connect.common.Constants;


public class WeiBoActivity extends Activity implements View.OnClickListener,IWeiboHandler.Response{
    TextView weibo_login,weibo_login_out,getWeibo_login_show,weibo_login_info,share_to_weibo;



    //注册成功之后的APPKEY
    public static final String SINA_APPKEY = "184435329";
    //注册成功之后的REDIRECT_URL
    public static final String SINA_REDIRECT_URL = "http://www.sina.com" ;
    public static final String SINA_SCOPE = "all";
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    public SsoHandler mSsoHandler;
    public AuthInfo mAuthInfo;
    public Oauth2AccessToken mAccessToken;
    RequestListener mListener;
    IWeiboShareAPI mWeiboShareAPI;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String  token = msg.toString();
            getWeibo_login_show.setText(token+"--");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_bo);
        initView();
    }
    private void initView(){
        weibo_login = (TextView) findViewById(R.id.weibo_login);
        weibo_login_out = (TextView) findViewById(R.id.weibo_login_out);
        getWeibo_login_show = (TextView) findViewById(R.id.weibo_login_show);
        weibo_login_info = (TextView) findViewById(R.id.weibo_login_info);
        share_to_weibo = (TextView) findViewById(R.id.weibo_share_show);
        weibo_login.setOnClickListener(this);
        weibo_login_out.setOnClickListener(this);
        getWeibo_login_show.setOnClickListener(this);
        weibo_login_info.setOnClickListener(this);
        share_to_weibo.setOnClickListener(this);
        mAuthInfo = new AuthInfo(WeiBoActivity.this,
                SINA_APPKEY,SINA_REDIRECT_URL ,
                SINA_SCOPE);
        mSsoHandler = new SsoHandler(WeiBoActivity. this, mAuthInfo);
    //获取用户信息的回调
        mListener = new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils. isEmpty(response)) {
                    Message message =  handler.obtainMessage();
                    message.obj = response;
                    message.what = 1;
                    handler.sendMessage(message
                    );
                    // 调用 User#parse 将JSON串解析成User对象，所有的用户信息全部在这里面
//                    User user = User. parse(response);
//                    thirdUser.setNickName(user. name); // 昵称
//                    thirdUser.setIcon(user. avatar_hd); // 头像
//                    thirdUser.setGender(user. gender.equals( "m") ? "男" : "女" );
//                    ThirdUserVerify. verifyUser(LoginActivity.this, thirdUser, 2);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
//                ErrorInfo info = ErrorInfo. parse(e.getMessage());
//                ToastUtil. showShort(LoginActivity.this, info.toString());
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.weibo_login:
            {
                mSsoHandler.authorize( new WeiboListener());
                break;
            }

            case R.id.weibo_login_out:
            {

                break;
            }
            case R.id.weibo_login_info:
            {
                // 获取用户信息接口
                //mAccessToken可以是你之前授权获取的那个，也可以
                // 获取当前已保存过的 Token
                //Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(this);
                UsersAPI mUsersAPI = new UsersAPI(WeiBoActivity.this, SINA_APPKEY, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
                break;
            }
            case R.id.weibo_login_show:
            {

                break;
            }
            case R.id.weibo_share_show:
            {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(WeiBoActivity.this,SINA_APPKEY);
                        mWeiboShareAPI.registerApp();
                        sendMultiMessage();
                    }
                }).start();
                break;
            }
        }
    }

    private void sendMultiMessage() {
        TextObject textObject = new TextObject();
        textObject.text = "要分享的文字";
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        weiboMessage. textObject = textObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(WeiBoActivity.this,request);//发送请求消息到微博，唤起微博分享界面
    }

    class WeiboListener  implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
           Message message =  handler.obtainMessage();
            message.obj = values;
            message.what = 1;
            handler.sendMessage(message
            );
            if (mAccessToken.isSessionValid()) {


//                MyProgressDialog.showDialogWithFalse(LoginActivity.this, "登陆",
//                        "正在获取用户信息");
//                thirdUser = new ThirdUserInfo();
//                thirdUser.setThirdID(mAccessToken.getUid());  //mAccessToken.getUid() ，获取到UID，作为身份的唯一标示
//                UsersAPI mUsersAPI = new UsersAPI(LoginActivity.this, APPConstants.SINA_APPKEY, mAccessToken);

              //  mUsersAPI.show(uid, mListener); //获取用户基本信息
            } else {
                Toast.makeText(getApplicationContext(), "登出QQ"+values.getString("code"), Toast.LENGTH_SHORT).show();
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                // String code = values.getString("code");
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(), "登出QQ"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent,this); //当前应用唤起微博分享后，返回当前应用
    }
    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(getApplicationContext(), "成功weiboQQ", Toast.LENGTH_SHORT).show();

                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(getApplicationContext(), "shibai", Toast.LENGTH_SHORT).show();

                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(getApplicationContext(), "shibai", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
