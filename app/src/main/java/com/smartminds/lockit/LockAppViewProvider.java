package com.smartminds.lockit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.smartminds.lockit.locklib.BasicLockInfo;
import com.smartminds.lockit.locklib.UserProfile;
import com.smartminds.lockit.locklib.common.lockscreen.AppLockSearchableAdapter;
import com.smartminds.lockit.locklib.common.lockscreen.ViewProvider;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by santhosh on 14/6/15.
 */
public class LockAppViewProvider implements ViewProvider<BasicLockInfo> {

    private final UserProfile userProfile;
    private final AppLockSearchableAdapter<BasicLockInfo> appLockSearchableAdapter;
    private Context context;

    public LockAppViewProvider(Context context, AppLockSearchableAdapter<BasicLockInfo> appLockSearchableAdapter,
                               UserProfile userProfile) {
        this.context = context;
        this.userProfile = userProfile;
        this.appLockSearchableAdapter = appLockSearchableAdapter;
    }

    static class Holder {
        @InjectView(R.id.appName_txtview)
        TextView appNameTxtView;
        @InjectView(R.id.app_desc_txtview)
        TextView appDescTextView;
        @InjectView(R.id.lock_switch)
        Switch lockSwitch;
        @InjectView(R.id.app_icon_imageview)
        ImageView launcherImageView;

        Holder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private Drawable getDefaultDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(R.drawable.ic_launcher, null);
        }
        return context.getResources().getDrawable(R.drawable.ic_launcher);
    }

    @Override
    public View getHeaderView() {
        View headerView = LayoutInflater.from(context).inflate(R.layout.header_applock_item, null);
        return headerView;
    }

    @Override
    public void fillHeaderView(View view, String comment) {
        ((TextView) view.findViewById(R.id.header_title_txtview)).setText(comment);
    }

    @Override
    public View getChildView() {
        View childView = LayoutInflater.from(context).inflate(R.layout.lockapp_list_item, null);
        childView.setTag(new Holder(childView));
        return childView;
    }

    @Override
    public void fillChildView(View view, final BasicLockInfo lockAppInfo, String constraint) {
        final Holder holder = (Holder) view.getTag();

        CharSequence label = lockAppInfo.getLabel();
        if (!TextUtils.isEmpty(constraint)) {
//            final SpannableStringBuilder sb = new SpannableStringBuilder("your text here");
//            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158));
//            String str= (String) label;
//            label = sb;
        }
        holder.appNameTxtView.setText(label);
        holder.lockSwitch.setOnCheckedChangeListener(null);
        holder.lockSwitch.setChecked(lockAppInfo.isLocked());
        holder.appDescTextView.setText(lockAppInfo.getLockDescription());

        AppIconAsynTask appIconAsynTask = (AppIconAsynTask) holder.launcherImageView.getTag();
        if (appIconAsynTask != null && appIconAsynTask.getStatus() == AsyncTask.Status.RUNNING) {
            appIconAsynTask.cancel(true);
        }
        holder.launcherImageView.setImageDrawable(getDefaultDrawable());
        appIconAsynTask = new AppIconAsynTask(holder.launcherImageView);
        appIconAsynTask.execute(lockAppInfo);
        holder.lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appLockSearchableAdapter.lockApp(lockAppInfo, isChecked);
            }
        });
    }



    static class AppIconAsynTask extends AsyncTask<BasicLockInfo, Void, Drawable> {

        WeakReference<ImageView> imageViewWeakReference;

        AppIconAsynTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
            imageView.setTag(this);
        }

        @Override
        protected Drawable doInBackground(BasicLockInfo... params) {
            return params[0].getIcon();
        }

        @Override
        protected void onPostExecute(Drawable appIcon) {
            if (appIcon != null && !isCancelled()) {
                if (imageViewWeakReference.get() != null) {
                    imageViewWeakReference.get().setImageDrawable(appIcon);
                }
            }
        }
    }
}
