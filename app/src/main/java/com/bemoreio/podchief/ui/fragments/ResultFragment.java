package com.bemoreio.podchief.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bemoreio.podchief.MainActivity;
import com.bemoreio.podchief.R;
import com.bemoreio.podchief.services.EmailService;
import com.bemoreio.podchief.utils.Global;
import com.bemoreio.podchief.utils.ImageUtils;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.File;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by Cody on 9/9/15.
 */
public class ResultFragment extends BaseFragment {

    @Bind(R.id.imageView)
    protected SubsamplingScaleImageView imageView;

    private byte[] imageUri;
    private String pdfUri;

    private boolean emailPending;
    private String toEmail;
    private String toPhoneNumber;

    private boolean textPending;
    private boolean faxPending;

    @InjectService
    public EmailService emailService;
    private MaterialDialog progressDialog;

    public static ResultFragment newInstance() {
        return new ResultFragment();
    }

    public static ResultFragment newInstance(byte[] bitmap) {

        Bundle args = new Bundle();
        args.putByteArray("imageUri", bitmap);

        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(args);

        return resultFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncService.inject(this);

//        imageUri = getArguments().getByteArray("imageUri");

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Bitmap bmp = BitmapFactory.decodeByteArray(imageUri, 0, imageUri.length);
        Bitmap bmp = Global.getInstance().getOutput();
        imageView.setImage(ImageSource.bitmap(bmp));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_email:

                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                        .input("Enter Email", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                                Toast.makeText(getActivity(), "Sending email...", Toast.LENGTH_SHORT).show();
                                toEmail = charSequence.toString();
                                if (pdfUri != null) {
                                    sendEmail();
                                }
                                else {
                                    emailPending = true;
                                }
                            }
                        })
                        .positiveText("Send Email")
                        .negativeText("Cancel");

                builder.show();

                break;

            case R.id.action_text:

                MaterialDialog.Builder builder1 = new MaterialDialog.Builder(getActivity())
                        .input("Enter phone number", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                                Toast.makeText(getActivity(), "Sending mms message...", Toast.LENGTH_SHORT).show();
                                toPhoneNumber = charSequence.toString();
                                if (pdfUri != null) {
                                    sendText();
                                }
                                else {
                                    textPending = true;
                                }
                            }
                        })
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .positiveText("Send Message")
                        .negativeText("Cancel");

                builder1.show();

                break;

            case R.id.action_fax:

                Toast.makeText(getActivity(), "Sending fax...", Toast.LENGTH_SHORT).show();
                if (pdfUri != null) {
                    sendFax();
                }
                else {
                    faxPending = true;
                }

                break;

            case R.id.action_finish:

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);

                break;

            default:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnMessage(from = OnMessage.Sender.ALL)
    public void onPdfCreated(Uri pdfUri) {
        this.pdfUri = pdfUri.getPath();

        if (emailPending) {
            sendEmail();
        }
        if (textPending) {
            sendText();
        }
        if (faxPending) {
            sendFax();
        }
    }

    @OnMessage
    void onEmailResult(Boolean success) {
        if (isAdded() && getActivity() != null) {
            if (success) {
                Toast.makeText(getActivity(), "Email sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error sending email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_result;
    }

    private void sendEmail() {
        emailPending = false;
        emailService.send(toEmail, pdfUri);
    }

    private void sendFax() {
        faxPending = false;

        File file = new File(pdfUri);
        final ParseFile parseFile = new ParseFile("pod.pdf", ImageUtils.toByteArray(file));
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("PODChief", e.getMessage());
                } else {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("pdfUrl", parseFile.getUrl());

                    ParseCloud.callFunctionInBackground("sendFax", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e != null) {
                                e.printStackTrace();

                                new MaterialDialog.Builder(getActivity())
                                        .title("Error")
                                        .content(e.getMessage())
                                        .show();
                            } else {
                                new MaterialDialog.Builder(getActivity())
                                        .title("Success")
                                        .content("Fax sent successfully")
                                        .show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendText() {
        textPending = false;

        File file = new File(pdfUri);
        final ParseFile parseFile = new ParseFile("pod.pdf", ImageUtils.toByteArray(file));
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("PODChief", e.getMessage());
                }
                else {

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("to", toPhoneNumber);
                    params.put("mediaUrl", parseFile.getUrl());

                    ParseCloud.callFunctionInBackground("sendMms", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if (e != null) {
                                e.printStackTrace();

                                new MaterialDialog.Builder(getActivity())
                                        .title("Error")
                                        .content(e.getMessage())
                                        .show();
                            }
                            else {
                                new MaterialDialog.Builder(getActivity())
                                        .title("Success")
                                        .content("Mms message sent successfully")
                                        .show();
                            }
                        }
                    });
                }
            }
        });
    }
}
