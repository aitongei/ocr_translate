//// Copyright 2018 Google LLC
////
//// Licensed under the Apache License, Version 2.0 (the "License");
//// you may not use this file except in compliance with the License.
//// You may obtain a copy of the License at
////
////      http://www.apache.org/licenses/LICENSE-2.0
////
//// Unless required by applicable law or agreed to in writing, software
//// distributed under the License is distributed on an "AS IS" BASIS,
//// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//// See the License for the specific language governing permissions and
//// limitations under the License.
//
//package com.smwl.translate;
//
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.util.Log;
//import com.smwl.translate.GraphicOverlay.Graphic;
////import com.benjaminwan.ocrlibrary.Point;
////import com.benjaminwan.ocrlibrary.TextBlock;
//
//import java.util.ArrayList;
//
///**
// * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
// * overlay view.
// */
//public class OcrLiteTextGraphic extends Graphic {
//
//    private static final String TAG = "TextGraphic";
//    private static final int TEXT_COLOR = Color.RED;
//    private static final float TEXT_SIZE = 30.0f;
//    private static final float STROKE_WIDTH = 4.0f;
//
//    private final Paint rectPaint;
//    private final Paint textPaint;
//    private final TextBlock element;
//
//    OcrLiteTextGraphic(GraphicOverlay overlay, TextBlock element) {
//        super(overlay);
//
//        this.element = element;
//
//        rectPaint = new Paint();
//        // rectPaint.setColor(TEXT_COLOR);
//        // rectPaint.setStyle(Paint.Style.STROKE);
//        rectPaint.setColor(Color.YELLOW);
//        rectPaint.setStyle(Paint.Style.FILL);
//        rectPaint.setStrokeWidth(STROKE_WIDTH);
//
//        textPaint = new Paint();
//        textPaint.setColor(TEXT_COLOR);
//        textPaint.setTextSize(TEXT_SIZE);
//        // Redraw the overlay, as this graphic has been added.
//        postInvalidate();
//    }
//
//    /**
//     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
//     */
//    @Override
//    public void draw(Canvas canvas) {
//        Log.d(TAG, "on draw text graphic");
//        if (element == null) {
//            throw new IllegalStateException("Attempting to draw a null text.");
//        }
//
//        // Draws the bounding box around the TextBlock.
//        ArrayList<Point> boxPoints = element.getBoxPoint();
//        RectF rect = new RectF(boxPoints.get(0).getX(), boxPoints.get(0).getY(), boxPoints.get(2).getX(), boxPoints.get(2).getY());
//        canvas.drawRect(rect, rectPaint);
//
//        // Renders the text at the bottom of the box.
//        canvas.drawText(element.getText(), rect.left, rect.bottom, textPaint);
//    }
//}
