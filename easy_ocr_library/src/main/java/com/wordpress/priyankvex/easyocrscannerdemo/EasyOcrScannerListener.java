package com.wordpress.priyankvex.easyocrscannerdemo;

import android.graphics.Bitmap;

/**
 * Created by Priyank(@priyankvex) on 27/8/15.
 *
 * Interface for the callbacks for {@link EasyOcrScanner}.
 */
public interface EasyOcrScannerListener {

    public void onOcrScanStarted(String filePath);

    public void onOcrScanFinished(Bitmap bitmap, String recognizedText);
}
