package com.smartminds.lockit.settings;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartminds.lockit.R;
import com.smartminds.lockit.common.BaseActivity;
import com.smartminds.lockit.locklib.AppLockLib;
import com.smartminds.lockit.locklib.common.lockscreen.LockScreenProvider;
import com.smartminds.lockit.locklib.services.LockUnInstallReceiver;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by santhosh on 21/6/15.
 */
public class SettingsActivity extends BaseActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int REQUEST_ANTI_UNINSTALL = 156;
    @InjectView(R.id.change_email_textview)
    TextView changeEmailTextView;
    @InjectView(R.id.deviceadmin_checkbox)
    CheckBox deviceAdminCheckBox;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.device_admin_title)
    TextView deviceAdminTitle;
    private ComponentName componentName;
    @InjectView(R.id.locktype_txtview)
    TextView lockTypeTextview;
    @InjectView(R.id.bg_theme_txtview)
    TextView bgthemeTextview;
    @InjectView(R.id.feedback_txtview)
    TextView feedBackTextview;
    @InjectView(R.id.share_txtview)
    TextView shareTextview;
    @InjectView(R.id.user_profile_txtview)
    TextView userProfileTxtview;
    private LockScreenProvider lockScreenProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lockScreenProvider = new LockScreenProvider();
        initDeviceAdmin();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void initDeviceAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, LockUnInstallReceiver.class);
        deviceAdminCheckBox.setChecked(devicePolicyManager.isAdminActive(componentName));
    }

    @OnClick({R.id.change_email_textview, R.id.locktype_txtview, R.id.bg_theme_txtview, R.id.feedback_txtview, R.id.share_txtview, R.id.user_profile_txtview})
    void onClickListener(View view) {
        if (view.getId() == R.id.change_email_textview) {
            pushChangeEmailDialog();
        } else if (view.getId() == R.id.locktype_txtview) {
            pushLockTypeDialog();
        } else if (view.getId() == R.id.bg_theme_txtview) {
//            startActivity(new Intent(this, BackGroundThemeActivity.class));
        } else if (view.getId() == R.id.feedback_txtview) {
//          startActivity(new Intent(this, LockItFeedBackActivity.class));
            sendFeedback();
        } else if (view.getId() == R.id.share_txtview) {
            shareLockIt();
        } else if (view.getId() == R.id.user_profile_txtview) {
//            startActivity(new Intent(this, UserProfileActivity.class));
        }
    }

    private void sendFeedback() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"dev.santhosh@appsforbb.net"});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        Intent intent = Intent.createChooser(Email, getString(R.string.feedback_label));
        startActivity(intent);
    }

    private void pushLockTypeDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        RadioGroup radioGrup = (RadioGroup) LayoutInflater.from(this).inflate(R.layout.locktype_dilaog, null);
//        builder.setTitle(getString(R.string.locktype_label)).setView(radioGrup);
//        radioGrup.check(getCheckedId());
//        final AlertDialog alertDialog = builder.create();
//        radioGrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Intent intent = new Intent(SettingsActivity.this, LockSettings.class);
//                LockScreen.LockType lockType = null;
//                switch (checkedId) {
//                    case R.id.number_radiobtn:
//                        lockType = LockScreen.LockType.NUMBER_LOCK;
//                        break;
//                    case R.id.pattern_radiobtn:
//                        lockType = LockScreen.LockType.PATTERN_LOCK;
//                        break;
//                    case R.id.charter_radiobtn:
//                        lockType = LockScreen.LockType.PASSWORD_LOCK;
//                        break;
//                    case R.id.gesture_radiobtn:
//                        lockType = LockScreen.LockType.GESTURE_LOCK;
//                        break;
//                }
//                intent.putExtra(LockSettings.LOCK_TYPE, lockType.ordinal());
//                startActivity(intent);
//                alertDialog.cancel();
//            }
//        });
//        alertDialog.show();
    }


//    private int getCheckedId() {
//        switch (lockScreenProvider.getLockScreenType()) {
//            case NUMBER_LOCK:
//                return R.id.number_radiobtn;
//            case PASSWORD_LOCK:
//                return R.id.charter_radiobtn;
//            case PATTERN_LOCK:
//                return R.id.pattern_radiobtn;
//            case GESTURE_LOCK:
//                return R.id.gesture_radiobtn;
//        }
//        return -1;
//    }


    private void shareLockIt() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        String sAux = "\nLet me recommend you this application\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(i, getString(R.string.share_label)));
    }

    void pushChangeEmailDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.change_email_dialog, null);
        builder.setTitle(getString(R.string.change_email_label)).setNegativeButton(
                getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final EditText oldEmailIdEditText = (EditText) view.findViewById(R.id.mail_edittxt);
                String emailId = oldEmailIdEditText.getText().toString();
                if (emailId.equals(AppLockLib.getInstance().getRecoveryEmail())) {
                    return;
                }
                AppLockLib.getInstance().setRecoveryEmail(emailId);
                Toast.makeText(SettingsActivity.this, getString(R.string.gmail_updated_succesfully),
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @OnCheckedChanged(R.id.deviceadmin_checkbox)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.deviceadmin_checkbox) {
            if (isChecked) {
                startUninstallService();
            } else {
                ((DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE)).removeActiveAdmin(componentName);
            }
        }
    }

    /**
     * Used to start uninstall service
     */
    private void startUninstallService() {
        boolean adminActive = ((DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE)).isAdminActive(componentName);
        if (!adminActive) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_uninstall_label));
            startActivityForResult(intent, REQUEST_ANTI_UNINSTALL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ANTI_UNINSTALL) {
            if (resultCode != RESULT_OK) {
                deviceAdminCheckBox.setChecked(false);
            }
        }
    }
}
