package com.bemoreio.podchief.ui.activities;

import android.os.Bundle;

import com.bemoreio.podchief.ui.fragments.ResultFragment;

/**
 * Created by Cody on 9/9/15.
 */
public class ResultActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        byte[] imageUri = getIntent().getExtras().getByteArray("imageUri");
//        String pdfUri = getIntent().getExtras().getString("pdfUri");

        addFragment(ResultFragment.newInstance());
    }
}
