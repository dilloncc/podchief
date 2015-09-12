package com.bemoreio.podchief.services;

import android.graphics.Bitmap;
import android.net.Uri;

import com.bemoreio.podchief.utils.ImageUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.joanzapata.android.asyncservice.api.annotation.AsyncService;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Cody on 9/9/15.
 */
@AsyncService
public class PdfService {
    public Uri generatePdf(Bitmap bitmap, int width, int height) {

        byte[] bytes = ImageUtils.toByteArray(bitmap);

        File pdfFile = null;
        Document document = new Document(new Rectangle(width, height), 0, 0, 0, 0);

        try {
            pdfFile = ImageUtils.getOutputPdfFile(ImageUtils.FILE_TYPE_PDF);

            PdfWriter.getInstance(document,
                    new FileOutputStream(pdfFile));
            document.open();

            Image image1 = Image.getInstance(bytes);

            document.add(image1);

            document.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        return Uri.fromFile(pdfFile);
    }
}
