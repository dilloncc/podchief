package com.bemoreio.podchief.ui.activities;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bemoreio.podchief.R;

/**
 * Created by Cody on 3/24/15.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayoutId());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
//        getSpiceManager().start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
//        getSpiceManager().shouldStop();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

//        ButterKnife.inject(this);
    }

    /**
     * Called when a server error has returned
     *
     * @param message
     */
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        if (!isFinishing()) {
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    protected void addFragment(Fragment fragment) {
        addFragment(getContainerViewId(), fragment);
    }

    protected void addFragment(int containerViewId, Fragment fragment)
    {
        getFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment)
                .commit();
    }

    protected int getContainerViewId() {
        return R.id.content;
    }

    /**
     * Return the layout id to be set in {@link #onCreate(Bundle)} using {@link #setContentView(int)}
     */
    protected int getActivityLayoutId() {
        return R.layout.activity_content;
    }

    public static class IntentBuilder {

    }
}
