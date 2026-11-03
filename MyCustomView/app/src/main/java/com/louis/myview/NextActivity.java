package com.louis.myview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NextActivity extends AppCompatActivity {

    boolean isPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Button idbtnpassword = (Button) findViewById(R.id.id_btn_password);
        SeekBar idsb = (SeekBar) findViewById(R.id.id_sb);
        CircleLikeProgressbar clp = (CircleLikeProgressbar) findViewById(R.id.clp);
        MyRoundImageView idmriv = (MyRoundImageView) findViewById(R.id.id_mriv);
        Button idbtnhuangdong = (Button) findViewById(R.id.id_btn_huangdong);
        final ClearEditText idcet = (ClearEditText) findViewById(R.id.id_cet);
        EditTextWithDel idetwd = (EditTextWithDel) findViewById(R.id.id_etwd);
        final EditText idet = (EditText) findViewById(R.id.id_et);
        Button idbtnaddemoji = (Button) findViewById(R.id.id_btn_add_emoji);
        final TextView idtvnew = (TextView) findViewById(R.id.id_tv_new);
        idtvnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idtvnew.setText("长文本长文本文本长文本长文本文本");
            }
        });


        idbtnaddemoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmoji(idet);
            }
        });

        idbtnhuangdong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //idcet.requestFocus();
                idcet.setShakeAnimation();
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        idmriv.setBitmap(bitmap);


        idsb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int secondaryProgress = progress + 20;
                if (secondaryProgress > 100) {
                    secondaryProgress = 100;
                }
                seekBar.setSecondaryProgress(secondaryProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        idbtnpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPass) {
                    idet.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    idet.setSelection(idet.getText().length());
                    isPass = false;
                } else {
                    idet.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    idet.setSelection(idet.getText().length());
                    isPass = true;
                }
            }
        });
    }

    private void addEmoji(EditText idet) {
        SpannableString spanStr = new SpannableString("imge");
        //图片资源
        Drawable drawable = getResources().getDrawable(R.drawable.emoji);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //ImageSpan  用ImageSpan代替文本
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //setSpan
        spanStr.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int cursor = idet.getSelectionStart();
        idet.getText().insert(cursor, spanStr);
    }
}
