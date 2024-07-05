// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.smwl.translate;

import static java.lang.Math.max;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.benjaminwan.ocrlibrary.Point;
//import com.benjaminwan.ocrlibrary.TextBlock;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//import com.benjaminwan.ocrlibrary.OcrEngine;
//import com.benjaminwan.ocrlibrary.OcrResult;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView mImageView;
    private Bitmap mSelectedImage;
    private GraphicOverlay mGraphicOverlay;
    private FloatingBallLayout floatingBallLayout;
    private Spinner dropdown;
    private TranslateManager translateManager;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);

        dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Image 1", "Image 2", "Image 3", "Image 4", "Image 5", "Image 6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        displayFloatingBall();
        translateManager = new TranslateManager();
        translateManager.downloadModel(new TranslateManager.ModelDownloadCallback() {
            @Override
            public void onModelDownloaded() {
                Log.d("TranslateManager", "Model downloaded");
            }

            @Override
            public void onModelDownloadFailed(Exception e) {
                Log.d("TranslateManager", "Failed to download model", e);
            }
        });
    }

    private void displayFloatingBall() {
        final WindowManager.LayoutParams floatLayoutParams = new WindowManager.LayoutParams();
        floatLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        floatLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        floatLayoutParams.gravity = Gravity.START | Gravity.TOP;
        floatLayoutParams.x = 0;
        floatLayoutParams.y = 0;
        floatLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        floatLayoutParams.format = PixelFormat.TRANSPARENT;

        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        floatingBallLayout = new FloatingBallLayout((Context) this, floatLayoutParams, new Runnable() {
            @Override
            public void run() {
                runMLKitTR();
            }
        });

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                windowManager.addView(floatingBallLayout, floatLayoutParams);
            }
        });
    }

//    private void runOcrLiteTR() {
//        Log.d("OcrEngine", "runOcrEngine");
//        OcrEngine ocrEngine = new OcrEngine(this.getApplicationContext());
//
//        Bitmap boxImg = Bitmap.createBitmap(mSelectedImage.getWidth(), mSelectedImage.getHeight(), Bitmap.Config.ARGB_8888);
//        int maxSize = max(mSelectedImage.getWidth(), mSelectedImage.getHeight());
//        OcrResult result = ocrEngine.detect(mSelectedImage, boxImg, maxSize);
//        /*
//        Bitmap appScreenShot = captureScreenshot();
//        Bitmap boxImg = Bitmap.createBitmap(appScreenShot.getWidth(), appScreenShot.getHeight(), Bitmap.Config.ARGB_8888);
//        int maxSize = max(appScreenShot.getWidth(), appScreenShot.getHeight());
//        OcrResult result = ocrEngine.detect(appScreenShot, boxImg, maxSize);
//
//         */
//
//        // mImageView.setImageBitmap(boxImg); // show bboxed image
//        Log.d("OcrResult", result.toString());
//
//        float floatingBallX = floatingBallLayout.getLastX();
//        float floatingBallY = floatingBallLayout.getLastY();
//        Log.d("FloatingBall", "X: " + floatingBallX + " Y: " + floatingBallY);
//        // draw the detected text blocks
//        for (TextBlock block : result.getTextBlocks()) {
//            List<Point> boxPoints = block.getBoxPoint();
//            RectF rect = new RectF(boxPoints.get(0).getX(), boxPoints.get(0).getY(), boxPoints.get(2).getX(), boxPoints.get(2).getY());
//            String text = block.getText();
//            Log.d("OcrTextBlock", text);
//            Log.d("OcrTextBlock", rect.toString());
//            if (true | rect.contains(floatingBallX, floatingBallY)) {
//                // RectF(467.0, 741.0, 553.0, 773.0)
//                // String text = block.getText();
//                Log.d("OcrTextBlock", text);
//                GraphicOverlay.Graphic textGraphic = new OcrLiteTextGraphic(mGraphicOverlay, block);
//                mGraphicOverlay.add(textGraphic);
//            }
//        }
//    }

    public void runMLKitTR() {
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
        dropdown.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                dropdown.setEnabled(true);
                                processMKKitTResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                dropdown.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processMKKitTResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        mGraphicOverlay.clear();
        float floatingBallX = floatingBallLayout.getLastX();
        float floatingBallY = floatingBallLayout.getLastY()-100;
        final float horizontal_margin = 20;
        final float vertical_margin = 10;
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {

                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    RectF rect = new RectF(elements.get(k).getBoundingBox());
                    rect.left -= horizontal_margin;
                    rect.top -= vertical_margin;
                    rect.right += horizontal_margin;
                    rect.bottom += vertical_margin;
                    if (rect.contains(floatingBallX, floatingBallY)) {
                        Text.Element textElement = elements.get(k);
                        String text = textElement.getText();
                        translateManager.translate(text, new TranslateManager.TranslationCallback() {
                            @Override
                            public void onTranslationSuccess(String translatedText) {
                                Log.d("MLKit", "Translated text: " + translatedText);
                                GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, textElement, translatedText);
                                mGraphicOverlay.add(textGraphic);
                            }
                            @Override
                            public void onTranslationFailed(Exception e) {
                                Log.d("MLKit", "Failed to translate text", e);
                            }
                        });
                    }
                }
                /*
                GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, lines.get(j));
                mGraphicOverlay.add(textGraphic);
                 */
            }
        }
    }

    // Method to convert a Bitmap to Grayscale
    private Bitmap convertToGrayscale(Bitmap original) {
        int width, height;
        height = original.getHeight();
        width = original.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(original, 0, 0, paint);
        return bmpGrayscale;
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height  for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }
        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    private void resizeBitmap() {
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e("getBitmapFromAsset", "Error opening bitmap: " + filePath, e);
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromSDCard(String fileName) {
        File sdCardDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(sdCardDirectory, fileName);

        if (!imageFile.exists()) {
            Log.e("getBitmapFromSDCard", "File not found: " + imageFile.getAbsolutePath());
            return null;
        }

        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public Bitmap captureScreenshot() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return screenshot;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mGraphicOverlay.clear();
        switch (position) {
            case 0:
                mSelectedImage = getBitmapFromAsset(this, "input.png");
                break;
            case 1:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "new_game.png");
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "android_book.png");
                break;
            case 3:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "android_book_2.png");
                break;
            case 4:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "wikipedia.jpg");
                break;
            case 5:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "game3.png");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        translateManager.close();
    }
}
