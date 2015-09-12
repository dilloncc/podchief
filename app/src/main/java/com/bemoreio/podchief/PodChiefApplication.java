package com.bemoreio.podchief;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.parse.Parse;

/**
 * Created by Cody on 9/10/15.
 */
public class PodChiefApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "KU4K1wgyaPLMqJkewYPKrkX5ftCGArGAZcO7mloD", "R0S7mKfL8Ada2GG4Ww5ShCl5auFQZA70tKMPOWBX");
        Iconify.with(new FontAwesomeModule());
    }
}
