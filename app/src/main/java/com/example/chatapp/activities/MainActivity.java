package com.example.chatapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.bumptech.glide.Glide;
import com.example.chatapp.R;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "chatapp_prefs";
    private static final String PREF_LAST_KEY = "last_uploaded_key";

    private Button openGalleryButton, uploadButton, downloadButton;
    private ImageView downloadedImageView;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    uploadButton.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureAmplify();

        openGalleryButton = findViewById(R.id.openGalleryButton);
        uploadButton = findViewById(R.id.uploadButton);
        downloadButton = findViewById(R.id.downloadButton);
        downloadedImageView = findViewById(R.id.downloadedImage);

        openGalleryButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        uploadButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadPhoto(selectedImageUri);
            }
        });

        downloadButton.setOnClickListener(v -> downloadPhoto());
    }

    private void configureAmplify() {
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "Amplify configured successfully");
        } catch (AmplifyException e) {
            Log.e(TAG, "Could not initialize Amplify", e);
        }
    }


    private void uploadPhoto(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "InputStream is null");
                return;
            }

            String photoKey = "photo_" + System.currentTimeMillis() + ".jpg";
            StorageUploadInputStreamOptions options = StorageUploadInputStreamOptions.builder()
                    .contentType("image/jpeg") // hoặc "image/png" nếu là ảnh PNG
                    .build();

            Amplify.Storage.uploadInputStream(
                    photoKey,
                    inputStream,
                    options,
                    result -> {
                        Log.i(TAG, "Upload succeeded: " + result.getKey());
                        saveLastUploadedKey(photoKey);
                        runOnUiThread(() -> {
                            uploadButton.setVisibility(View.GONE);
                            downloadButton.setVisibility(View.VISIBLE);
                        });
                    },
                    error -> Log.e(TAG, "Upload failed", error)
            );
        } catch (Exception e) {
            Log.e(TAG, "Exception opening stream", e);
        }
    }

    private void downloadPhoto() {
        String photoKey = getLastUploadedKey();
        if (photoKey == null) {
            Log.e(TAG, "No uploaded key found");
            return;
        }

        File localFile = new File(getFilesDir(), "downloaded-image.jpg");

        Amplify.Storage.downloadFile(
                photoKey,
                localFile,
                result -> {
                    Log.i(TAG, "Download succeeded: " + result.getFile().getPath());
                    runOnUiThread(() -> {
                        downloadButton.setVisibility(View.GONE);
                        downloadedImageView.setVisibility(View.VISIBLE);
                        Glide.with(this).load(result.getFile()).into(downloadedImageView);
                    });
                },
                error -> Log.e(TAG, "Download failed", error)
        );
    }

    private void saveLastUploadedKey(String key) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(PREF_LAST_KEY, key)
                .apply();
    }

    private String getLastUploadedKey() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(PREF_LAST_KEY, null);
    }
}
