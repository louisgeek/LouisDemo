package com.louis.louishook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    /*private InvocationHandler mInvocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke();
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        Toast.makeText(getApplicationContext(), "第一次提示", Toast.LENGTH_SHORT).show();

        try {
            //目的是为了 Hook Toast#sService，只需要处理一次
            //如果直接 Hook Toast#mNextView，需要针对每个 Toast 的实例都处理一次，太麻烦了，直接 Hook Toast#mTN 也是同样问题
            //先调用一次 Toast#getService 让静态变量 Toast#sService 先初始化
            //从 Toast 反射获取静态变量 sService
            Method getServiceMethod = Toast.class.getDeclaredMethod("getService", null);
            getServiceMethod.setAccessible(true);
            //invoke 执行 getService 有返回 INotificationManager
            Object service = getServiceMethod.invoke(null);
            //打印 android.app.INotificationManager$Stub$Proxy@10c594e
            Log.e(TAG, "onCreate service: " + service);
            //
            Field sServiceField = Toast.class.getDeclaredField("sService");
            sServiceField.setAccessible(true);
            //或者直接从 Toast 中取 sService 静态变量（需要先初始化再获取）（所以传 null）
            Object sService = sServiceField.get(null);
            Log.e(TAG, "onCreate sService: " + sService);
            //
            Object sServiceProxy = Proxy.newProxyInstance(TextView.class.getClassLoader(),
                    sService.getClass().getInterfaces(), new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            //service.enqueueToast(pkg, mToken, tn, mDuration, displayId)
                            Log.e(TAG, "invoke: method " + method);
                            if ("enqueueToast".equals(method.getName())) {
                                Log.d(TAG, "enqueueToast");
                                //android.widget.Toast.TN 类名在不同版本是否一致？
                                Class<?> tnClass = Class.forName(Toast.class.getName() + "$TN");
                                Field mNextViewField = tnClass.getDeclaredField("mNextView");
                                mNextViewField.setAccessible(true);
                                //不同版本 TN 参数位置不同
                                Object tn = null;
                                for (int i = 0; i < args.length; i++) {
                                    Object arg = args[i];
                                    Log.e(TAG, i + "invoke: arg " + arg);
                                    if ((Toast.class.getName() + "$TN").equals(arg.getClass().getName())) {
                                        tn = arg;
                                        Log.e(TAG, i + "invoke: tn " + tn);
                                    }
                                }
                                if (tn != null) {
                                    //从 tn 对象中获取 mNextView 对象
                                    Object mNextViewObject = mNextViewField.get(tn);
                                    Log.e(TAG, "invoke: mNextView " + mNextViewObject);
                                    if (mNextViewObject instanceof LinearLayout) {
                                        LinearLayout mNextView = (LinearLayout) mNextViewObject;
                                        for (int i = 0; i < mNextView.getChildCount(); i++) {
                                            View view = mNextView.getChildAt(i);
                                            if (view instanceof TextView) {
                                                TextView textView = (TextView) view;
                                                String toastText = textView.getText().toString();
                                                String appName = textView.getContext().getString(R.string.app_name);
                                                if (toastText.contains(appName)) {
                                                    //空格 中文冒号 英文冒号
                                                    toastText = toastText.replace(appName + " ", "")
                                                            .replace(appName + "：", "")
                                                            .replace(appName + ":", "")
                                                            //最短的放后面
                                                            .replace(appName, "");
//                                                    toastText = toastText.substring(appName.length() + 1);
                                                    textView.setText(toastText);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //调用 sService 本身的方法
                            return method.invoke(sService, args);
//                            return null;

                        }
                    });

            //
            //Toast#sService 是静态对象所以可以为 null
            sServiceField.set(null, sServiceProxy);

        } catch (Exception e) {
            e.printStackTrace();
        }


        //
        Toast.makeText(getApplicationContext(), "第二次提示", Toast.LENGTH_SHORT).show();





        /*try {



            Class<?> clazz = Proxy.getProxyClass(Toast.class.getClassLoader(), Toast.class);
            Toast toastProxy = (Toast) clazz.getConstructor(InvocationHandler.class)
                    .newInstance(mInvocationHandler);
            //
            Toast toastProxy2 = (Toast) Proxy.newProxyInstance(Toast.class.getClassLoader(),
                    new Class<?>[]{Toast.class}, mInvocationHandler);




        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }
}