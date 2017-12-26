package com.stewardbank.omnichannel.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.stewardbank.omnichannel.R;
import com.stewardbank.omnichannel.activity.utils.PermissionsActivity;
import com.stewardbank.omnichannel.business.rest.PushService;
import com.stewardbank.omnichannel.business.util.AppUtil;

/**
 * @uthor Tasu Muzinda
 */
public class BaseActivity extends AppCompatActivity{

    private Toolbar toolbar;
    Context context = this;
    ProgressDialog progressDialog;
    public Menu menu;

    public Toolbar getToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        return toolbar;
    }

    public void startPermissionsActivity(String[] permission) {
        PermissionsActivity.startActivityForResult(this, 0, permission);
    }

    public void syncAppData() {
        if (AppUtil.isNetworkAvailable(context)) {
            progressDialog = ProgressDialog.show(this, "Please wait", "Syncing with Server...", true);
            progressDialog.setCancelable(true);
            Intent intent = new Intent(this, PushService.class);
            startService(intent);
        } else {
            AppUtil.createShortNotification(this, "No Internet, Check Connectivity!");
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (bundle != null) {
                int resultCode = bundle.getInt(PushService.RESULT);
                if (resultCode == RESULT_OK) {
                    createNotificationDataSync("Sync Success", "Data uploaded to server");
                    AppUtil.createShortNotification(context, "Data uploaded to server");
                } else {
                    createNotificationDataSync("Sync Fail", "Data upload failed");
                    AppUtil.createShortNotification(context, "Data upload failed");
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(PushService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void createNotificationDataSync(String title, String msg) {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(msg).setSmallIcon(R.mipmap.logo)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent = null;

        if (android.R.id.home == id) {
            onBackPressed();
        }

        if (id == android.R.id.home) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.action_logout) {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppUtil.removePreferences(getApplicationContext());
                            Intent out = new Intent(context, MainActivity.class);
                            out.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(out);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        if (id == R.id.action_exit) {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
