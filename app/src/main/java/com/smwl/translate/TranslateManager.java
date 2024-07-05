package com.smwl.translate;

import android.content.Context;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslateManager {

    private final Translator englishToChineseTranslator;

    public TranslateManager(String sourceLanguage, String targetLanguage) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build();
        englishToChineseTranslator = Translation.getClient(options);
    }

    public void downloadModel(final ModelDownloadCallback callback) {

        englishToChineseTranslator.downloadModelIfNeeded()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onModelDownloaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onModelDownloadFailed(e);
                    }
                });
    }

    public void translate(String input, final TranslationCallback callback) {
        englishToChineseTranslator.translate(input)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedText) {
                        callback.onTranslationSuccess(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        callback.onTranslationFailed(e);
                    }
                });
    }

    public void close() {
        englishToChineseTranslator.close();
    }

    public interface ModelDownloadCallback {
        void onModelDownloaded();
        void onModelDownloadFailed(Exception e);
    }

    public interface TranslationCallback {
        void onTranslationSuccess(String translatedText);
        void onTranslationFailed(Exception e);
    }
}
