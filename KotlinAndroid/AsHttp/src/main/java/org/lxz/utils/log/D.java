package org.lxz.utils.log;


import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Lin on 2017/4/23.
 */

public class D {
    private static boolean isShow=true;
    private static Context context;
    private static Handler handler=new Handler();
    public static void init(Context context){
        D.context=context;
    };
    public static void test(final String str){
       if(isShow) handler.post(new Runnable() {
           @Override
           public void run() {
               Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
           }
       });
    }

    public static void show(final String str) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void show(final int resID) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,context.getString(resID),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
