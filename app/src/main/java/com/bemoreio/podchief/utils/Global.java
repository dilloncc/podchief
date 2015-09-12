package com.bemoreio.podchief.utils;

import android.graphics.Bitmap;

/**
 * Created by Cody on 9/10/15.
 */
public class Global {
    public static Global global;

    private Bitmap output;
    private int screenOrientation;

    public static Global getInstance() {

        if (global == null) {
            global = new Global();
        }

        return global;
    }

    public Bitmap getOutput() {
        return output;
    }

    public void setOutput(Bitmap output) {
        this.output = output;
    }

    public int getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(int screenOrientation) {
        this.screenOrientation = screenOrientation;
    }
}
