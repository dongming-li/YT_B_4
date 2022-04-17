package com.wristband.yt_b_4.wristbandclient.app;


import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;




/**
 * Created by Mike on 10/30/2017.
 */

 public class QRGenerator  extends Activity {
    private String text2Qr;
    MultiFormatWriter multiFormatWriter;

    public QRGenerator() {
        this.text2Qr = null;
        this.multiFormatWriter = null;
    }
    public QRGenerator(String username) {
        this.text2Qr = username;
        this.multiFormatWriter =  new MultiFormatWriter();;

    }
    public Bitmap createQR() {
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
