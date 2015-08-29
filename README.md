# Easy-Ocr-Scanner-Android
Easiest and simplest OCR scanner library for Android built using Tesseract and Leptonica. 

<h2>About</h2>

Easy OCR Library is made by having only one goal in mind: Making OCR as easy as possible. (Don't you just love when things actually mean what they show). 

Easy OCR uses a fork of tesseract, <a href="https://github.com/rmtheis/tess-two">Tess Two</a>.
But deals with all the pain of setting up and building the library using NDK.

<h2>Usage</h2>

Using EasyOcrLibrary is as simple as it can get.

<b>Step 0</b>

Copy your trained data file into the assets/tessdata folder.
You can download the required .traineddata file from <a href="https://code.google.com/p/tesseract-ocr/downloads/list">here</a>.

<b>Step 1</b>

NOTE : "eng" is the name of the traineddata file as here we are using eng.traineddata .

```java
 // initialize EasyOcrScanner instance.
mEasyOcrScanner = new EasyOcrScanner(MainActivity.this, "EasyOcrScanner",
        Config.REQUEST_CODE_CAPTURE_IMAGE, "eng");
```

<b>Step 2</b>

Implement ```java EasyOcrScannerListener```.

```java
implements EasyOcrScannerListener
```
Then define the callbacks.

<b>Step 3</b>

Start the scan!

```java
mEasyOcrScanner.takePicture();
```

<b>Step 4</b>

Call ```java onImageTaken()``` in ```java onActivityResult()```

```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Call onImageTaken() in onActivityResult.
        if (resultCode == RESULT_OK && requestCode == Config.REQUEST_CODE_CAPTURE_IMAGE){
            mEasyOcrScanner.onImageTaken();
        }
    }
```

<b>And you are done!</b>

Get the scaned text in the callback ```java onOcrScanFinished()```.

<em>For more info check the sample app in the app module.</em>