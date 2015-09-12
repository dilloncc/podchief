package com.bemoreio.podchief;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bemoreio.podchief.services.PdfService;
import com.bemoreio.podchief.services.ProcessImageService;
import com.bemoreio.podchief.ui.activities.PodCameraActivity;
import com.bemoreio.podchief.ui.activities.ResultActivity;
import com.bemoreio.podchief.utils.Global;
import com.bemoreio.podchief.utils.ImageUtils;
import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.scanlibrary.IScanner;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.scanlibrary.ScanFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends ScanActivity implements IScanner {

    @Bind(R.id.camera_button)
    protected IconTextView cameraButton;

    private File imageFile;
    private String imagePath;
    private String outputImagePath;


    private static final int REQUEST_PORTRAIT_RFC=1337;
    private static final int REQUEST_PORTRAIT_FFC=REQUEST_PORTRAIT_RFC+1;
    private static final int REQUEST_LANDSCAPE_RFC=REQUEST_PORTRAIT_RFC+2;
    private static final int REQUEST_LANDSCAPE_FFC=REQUEST_PORTRAIT_RFC+3;

    public static final int CAMERA_REQUEST = 1888;
    public static final int PICK_IMAGE = 1999;

    private int scannedWidth;
    private int scannedHeight;

    private Bitmap outputBitmap;

    @InjectService
    public ProcessImageService processImageService;

    @InjectService
    public PdfService pdfService;

    protected MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncService.inject(this);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString("imagePath");
            outputImagePath = savedInstanceState.getString("outputImagePath");
        }
        else {
            imageFile = ImageUtils.getOutputMediaFile(ImageUtils.MEDIA_TYPE_IMAGE_PNG); // create a file to save the image
            imagePath = imageFile.getAbsolutePath();
        }

        Intent i = new PodCameraActivity.IntentBuilder(this)
                .skipConfirm()
                .facing(AbstractCameraActivity.Facing.BACK)
                .to(imageFile)
                .updateMediaStore()
                .debug()
                .build();

        startActivityForResult(i, REQUEST_PORTRAIT_RFC);
    }

    @OnClick(R.id.camera_button)
    public void onClick(View view) {

        //save image to gallery
        imageFile = ImageUtils.getOutputMediaFile(ImageUtils.MEDIA_TYPE_IMAGE_PNG); // create a file to save the image
        imagePath = imageFile.getAbsolutePath();

        Intent i = new PodCameraActivity.IntentBuilder(this)
                .skipConfirm()
                .facing(AbstractCameraActivity.Facing.BACK)
                .to(imageFile)
                .updateMediaStore()
                .debug()
                .build();

        startActivityForResult(i, REQUEST_PORTRAIT_RFC);

//        int REQUEST_CODE = 99;
//        int preference = ScanConstants.OPEN_CAMERA;
//        Intent intent = new Intent(this, ScanActivity.class);
//        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
//        startActivityForResult(intent, REQUEST_CODE);

//        takePhoto();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("imagePath", imagePath);
        outState.putString("outputImagePath", outputImagePath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Take photo
     */
    private void takePhoto()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //save image to gallery
        imageFile = ImageUtils.getOutputMediaFile(ImageUtils.MEDIA_TYPE_IMAGE_PNG); // create a file to save the image
        imagePath = imageFile.getAbsolutePath();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile)); // set the image file name

        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case CAMERA_REQUEST:

                if (resultCode == Activity.RESULT_OK) {
//                    byte[] b = ImageUtils.rotateAndScaleImage(imagePath, PaperSize.LETTER_WIDTH * 2, PaperSize.LETTER_HEIGHT * 2);
//                    processImage(b);

//                    progressDialog = new MaterialDialog.Builder(this)
////                            .title("Proces")
//                            .content("Processing Image...")
//                            .progress(true, 0)
////                            .progress(true, 100, true)
////                            .progressIndeterminateStyle(true)
//                            .build();

//                    progressDialog.show();

//                    processImageService.run(imagePath);
                    onImageProcessed(imagePath);
                }

                break;

            case REQUEST_PORTRAIT_RFC:

                if (resultCode == Activity.RESULT_OK) {

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    progressDialog = new MaterialDialog.Builder(this)
                            .content("Processing Image...")
                            .progress(true, 0)
                            .icon(new IconDrawable(this, FontAwesomeIcons.fa_crop))
                            .show();

                    int screenOrientation = Global.getInstance().getScreenOrientation();
                    processImageService.process(imagePath, screenOrientation);
                }

                break;
        }
    }

    @OnMessage
    void onImageProcessed(Bitmap bitmap) {

        progressDialog.dismiss();

        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, bitmap);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
        fragmentTransaction.commit();
    }

    @OnMessage
    void onImageProcessed(String imagePath) {

        progressDialog.dismiss();

        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, Uri.fromFile(new File(imagePath)));
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onBitmapSelect(Uri uri) {

    }

    @Override
    public void onScanFinish(Uri uri) {
//        if (uri != null) {
//            outputImagePath = uri.getPath();
//            pdfService.generatePdf(uri, scannedWidth, scannedHeight);
//        }
    }

    @Override
    public void onScanFinish(Bitmap bitmap) {

        scannedWidth = bitmap.getWidth();
        scannedHeight = bitmap.getHeight();

        outputBitmap = bitmap;

        progressDialog = new MaterialDialog.Builder(this)
                .content("Generating pdf...")
                .progress(true, 0)
                .build();

//        progressDialog.show();

        pdfService.generatePdf(bitmap, scannedWidth, scannedHeight);

        Intent intent = new Intent(this, ResultActivity.class);

        Global.getInstance().setOutput(bitmap);
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//        byte[] byteArray = bStream.toByteArray();
//
//        intent.putExtra("imageUri", byteArray);

        startActivity(intent);
    }

    @OnMessage
    void onFileSaved(File file) {
        onScanFinish(Uri.fromFile(file));
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
}
