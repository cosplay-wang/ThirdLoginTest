package demo.wang.com.cosplay.thirdlogintest.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;


/**
 * Created by zhiwei.wang on 2016/7/18.
 */
public class BaseUiListener implements IUiListener {
public static  String QQLOG = "QQLOG" ;

    Context context;
    Handler handler;

    public BaseUiListener(Handler handler ,Context context) {

        this.context = context;
        this.handler = handler;

    }

    @Override
    public void onComplete(Object response) {
        Log.i(QQLOG,"---------onComplete:-----");
        Message message = handler.obtainMessage();
        message.obj = response;
        message.what = 1;
        handler.sendMessage(message);
        Toast.makeText(context,"---------onComplete:-----",Toast.LENGTH_SHORT).show();
        doComplete(response);
    }
    protected void doComplete(Object values) {
        Message message = handler.obtainMessage();
        message.obj = values;
        message.what = 1;
        handler.sendMessage(message);

        Toast.makeText(context,"---------doComplete:-----"+ values,Toast.LENGTH_SHORT).show();
        Log.i(QQLOG,"---------doComplete:-----"+ values);
    }
    @Override
    public void onError(UiError e) {
        Message message = handler.obtainMessage();
        message.obj = e.toString();
        message.what = 1;
        handler.sendMessage(message);
        Toast.makeText(context,"onError:-----"+e.toString(),Toast.LENGTH_SHORT).show();
        Log.i(QQLOG,"onError:-----"+e.toString());
    }
    @Override
    public void onCancel() {
        Message message = handler.obtainMessage();
        message.obj =".onCancel";
        message.what = 1;
        handler.sendMessage(message);
        Toast.makeText(context,"onCancel:-----",Toast.LENGTH_SHORT).show();
        Log.i(QQLOG,"---------onCancel:-----");

    }


}
