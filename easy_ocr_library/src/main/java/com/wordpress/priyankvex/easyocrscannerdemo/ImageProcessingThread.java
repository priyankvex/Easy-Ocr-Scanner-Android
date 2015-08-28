package com.wordpress.priyankvex.easyocrscannerdemo;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Priyank(@priyankvex) on 27/8/15.
 *
 * Async Task to process the image and scan the image using tesseract library.
 * Equipped with proper callbacks.
 */
public class ImageProcessingThread extends AsyncTask<Void, Void, Void> {

    private EasyOcrScannerListener mOcrScannerListener;
    private String filePath;
    private Bitmap mBitmap;
    private String scannedText;
    // trained data file used by Tesseract will be copied in directoryPath/tessdata
    private String directoryPath;
    private String absoluteDirectoryPath;
    private Activity mActivity;
    String trainedDataCode;

    public ImageProcessingThread(EasyOcrScannerListener ocrScannerListener, String filePath,
                                 String directoryPath, Activity activity, String trainedDataCode) {
        this.mOcrScannerListener = ocrScannerListener;
        this.filePath = filePath;
        this.directoryPath = directoryPath;
        this.absoluteDirectoryPath = FileUtils.getDirectory(this.directoryPath);
        this.mActivity = activity;
        this.trainedDataCode = trainedDataCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mOcrScannerListener.onOcrScanStarted(this.filePath);
    }

    @Override
    protected Void doInBackground(Void... params) {
        processImage();
        makeTessdataReady();
        scannedText = scanImage();
        Log.d(Config.TAG, "Scanned test : " + scannedText);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOcrScannerListener.onOcrScanFinished(mBitmap, scannedText);
    }

    private void processImage() {
        int imageOrientationCode = getImageOrientation();
        Bitmap rawBitmap = getBitmapFromPath();
        // Getting the bitmap in right orientation.
        this.mBitmap = rotateBitmap(rawBitmap, imageOrientationCode);
    }

    private Bitmap getBitmapFromPath() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(this.filePath, bmOptions);
        return bitmap;
    }

    private int getImageOrientation() {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(this.filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert exif != null;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        return orientation;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation){

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private String scanImage(){
        TessBaseAPI baseApi = new TessBaseAPI();
        Log.d(Config.TAG, "Data path : " + FileUtils.getDirectory(this.directoryPath));
        baseApi.init(FileUtils.getDirectory(this.directoryPath) + "/", this.trainedDataCode);
        baseApi.setImage(this.mBitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        return recognizedText;
    }

    private void makeTessdataReady(){

        // created tessdata directory if necessary under absoluteDirectoryPath and returns its absolute path.
        String tessdirectoryPath = FileUtils.getTessdataDirectory(this.absoluteDirectoryPath);

        if (!(new File(tessdirectoryPath+ "/" + this.trainedDataCode + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = mActivity.getAssets();
                InputStream in = assetManager.open("tessdata/" + this.trainedDataCode + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                // Output stream with the location where we have to write the eng.traineddata file.
                OutputStream out = new FileOutputStream(tessdirectoryPath + "/"  + this.trainedDataCode
                        + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(Config.TAG, "Copied " + " traineddata");
            } catch (IOException e) {
                Log.e(Config.TAG, "Was unable to copy " + " traineddata " + e.toString());
            }
        }
        else{
            Log.d(Config.TAG, "tessdata already present");
        }
    }

}







