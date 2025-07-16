package com.louis.myjni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.louis.myjni.databinding.ActivityMainBinding;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'myjni' library on application startup.
    static {
        System.loadLibrary("myjni");
    }

    private ActivityMainBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        String str = stringFromJNI();
        Log.e("TAG", "onCreate: str=" + str);

        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onCreate(owner);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onDestroy(owner);
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onPause(owner);
            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onResume(owner);
            }

            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onStart(owner);
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onStop(owner);
            }
        });

//        this.setCallback(new Callback() {
//
//            @Override
//            public void onSuccess(int data) {
//                Log.e("TAG", "onCreate: onSuccess data=" + data);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                Log.e("TAG", "onCreate: onFailure msg=" + msg);
//            }
//        });
        String [] ss = new String[]{
                "zw", "zz"
        };
       String name1 =  ss.getClass().getName();
       String name2 =  ss.getClass().getCanonicalName();
       String name3 =  ss.getClass().getSimpleName();
        Log.e("TAG", "Java 打印 doSometing name1=" + name1);
        Log.e("TAG", "Java 打印 doSometing name2=" + name2);
        Log.e("TAG", "Java 打印 doSometing name3=" + name3);

        boolean booleanValue = doSometing(9);
        //Java 打印 doSometing 方法返回 booleanValue 值是：true
        Log.e("TAG", "Java 打印 doSometing 方法返回 booleanValue 值是：" + booleanValue);
    }

    /**
     * A native method that is implemented by the 'myjni' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    public native boolean doSometing(int intValue);

    public String onSometing(float floatValue) {
        //Java 打印 onSometing 方法参数 floatValue 值是：11.6
        Log.e("TAG", "Java 打印 onSometing 方法参数 floatValue 值是：" + floatValue);
        return "floatValue=" + floatValue;
    }

    public void setCallback(Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //
                int data = new Random().nextInt();
                if (data % 2 == 0) {
                    callback.onSuccess(data);
                } else {
                    callback.onFailure("有错");
                }
            }
        }).start();
    }

}