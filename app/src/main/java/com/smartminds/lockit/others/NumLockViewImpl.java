package com.smartminds.lockit.others;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.appsforbb.common.annotations.NonNull;
import com.smartminds.lockit.R;
import com.smartminds.lockit.locklib.common.lock.LockData;
import com.smartminds.lockit.locklib.common.lock.LockScreen;
import com.smartminds.lockit.locklib.common.lock.NumLockView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NumLockViewImpl extends NumLockView implements View.OnClickListener {

    @InjectView(R.id.no_1_image)
    Button mNum1;
    @InjectView(R.id.no_2_image)
    Button mNum2;
    @InjectView(R.id.no_3_image)
    Button mNum3;
    @InjectView(R.id.no_4_image)
    Button mNum4;
    @InjectView(R.id.no_5_image)
    Button mNum5;
    @InjectView(R.id.no_6_image)
    Button mNum6;
    @InjectView(R.id.no_7_image)
    Button mNum7;
    @InjectView(R.id.no_8_image)
    Button mNum8;
    @InjectView(R.id.no_9_image)
    Button mNum9;
    @InjectView(R.id.no_0_image)
    Button mNum0;
    @InjectView(R.id.back_image)
    Button back;
    @InjectView(R.id.delete)
    Button mDelete;

    private Paint paint = new Paint();

    CheckBox passcode1ImageView;
    CheckBox passcode2ImageView;
    CheckBox passcode3ImageView;
    CheckBox passcode4ImageView;
    CheckBox[] passcodeBtns;

    public NumLockViewImpl(Context context) {
        super(context);
        init(context);
    }

    public NumLockViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumLockViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public NumLockViewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context mContext) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.widget_numkeyboard, this);
        ButterKnife.inject(this);
        initBackground();
        passcode1ImageView = (CheckBox) findViewById(R.id.dot_1);
        passcode2ImageView = (CheckBox) findViewById(R.id.dot_2);
        passcode3ImageView = (CheckBox) findViewById(R.id.dot_3);
        passcode4ImageView = (CheckBox) findViewById(R.id.dot_4);
        passcodeBtns = new CheckBox[]{passcode1ImageView, passcode2ImageView, passcode3ImageView, passcode4ImageView};
    }

    @OnClick({R.id.no_1_image, R.id.no_2_image, R.id.no_3_image,
            R.id.no_4_image, R.id.no_5_image, R.id.no_6_image,
            R.id.no_7_image, R.id.no_8_image, R.id.no_9_image, R.id.delete, R.id.cancel_button})
    @Override
    public void onClick(View widget) {
        if (widget.getId() == R.id.no_1_image) {
            addDigit(1);
        } else if (widget.getId() == R.id.no_2_image) {
            addDigit(2);
        } else if (widget.getId() == R.id.no_3_image) {
            addDigit(3);
        } else if (widget.getId() == R.id.no_4_image) {
            addDigit(4);
        } else if (widget.getId() == R.id.no_5_image) {
            addDigit(5);
        } else if (widget.getId() == R.id.no_6_image) {
            addDigit(6);
        } else if (widget.getId() == R.id.no_7_image) {
            addDigit(7);
        } else if (widget.getId() == R.id.no_8_image) {
            addDigit(8);
        } else if (widget.getId() == R.id.no_9_image) {
            addDigit(9);
        } else if (widget.getId() == R.id.no_0_image) {
            addDigit(0);
        } else if (widget.getId() == R.id.delete) {
            deleteDigit();
        } else if (widget.getId() == R.id.cancel_button) {
            cancelInput();
        }
    }

//
//    @Override
//    public void onDrawNumLockView(Canvas canvas, String input) {
//        String text = "";
//        for (int i = 0; i < input.length(); i++) {
//            text = text + "*";
//        }
//        paint.setTextAlign(Paint.Align.CENTER);
////        canvas.drawColor(Color.CYAN);
//        paint.setTextSize(nmView.getHeight() / 6);
//        canvas.drawText(text, nmView.getWidth() / 2, nmView.getHeight() / 2, paint);
//    }

    @Override
    public void resetInput() {
        super.resetInput();


    }

    @Override
    public void cancelInput() {
//        nmView.cancelInput();
    }

    @Override
    public void validateInput() {
//        nmView.validateInput();
    }

}