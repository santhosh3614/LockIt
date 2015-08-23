package com.smartminds.lockit.others;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.smartminds.lockit.R;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.BackgroundThemeProvider;
import com.smartminds.lockit.locklib.common.lock.LockData;
import com.smartminds.lockit.locklib.common.lock.LockView;
import com.smartminds.lockit.locklib.common.lock.NumLockView;
import com.smartminds.lockit.locklib.common.lockscreen.background.BackgroundTheme;

import java.io.File;

public abstract class BaseLockViewImpl extends FrameLayout implements NumLockView.OnDrawNumLockView, LockView {

    private BackgroundThemeProvider backgroundThemeProvider;
    private DisplayImageOptions options;

    protected BaseLockViewImpl(Context context) {
        super(context);
        init();
    }

    public BaseLockViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseLockViewImpl(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseLockViewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public boolean isLockDataCompatible(LockData data) {
        return false;
    }

    private void init() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initBackground() {
        backgroundThemeProvider = AppLockLib.getInstance().getBackgroundThemeProvider();
        BackgroundTheme selectedBackgroundTheme = backgroundThemeProvider.getSelectedBackgroundTheme();
        Drawable drawable = new BitmapDrawable(getResources(), ImageLoader.getInstance().
                loadImageSync(Uri.decode(Uri.fromFile(
                        new File(selectedBackgroundTheme.getImagePath())).
                        toString()), new ImageSize(getWidth(), getHeight()), options));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }
}
