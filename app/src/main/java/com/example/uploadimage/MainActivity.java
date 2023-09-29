package com.example.uploadimage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;  // Declaring an ImageView variable called 'imageView'
    private Button uploadImage, User_Image;  // Declaring Button variables called 'uploadImage' and 'User_Image'
    private Bitmap bitmap;  // Declaring a Bitmap variable called 'bitmap'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.Image);  // Initializing 'imageView' with the ImageView from the layout with the id 'Image'
        uploadImage = findViewById(R.id.Upload_Image);  // Initializing 'uploadImage' with the Button from the layout with the id 'Upload_Image'
        User_Image = findViewById(R.id.user_image);  // Initializing 'User_Image' with the Button from the layout with the id 'user_image'

        User_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));  // Start RecyclerViewActivity when 'User_Image' Button is clicked
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);  // Retrieve the selected image as a Bitmap
                        imageView.setImageBitmap(bitmap);  // Set the selected image Bitmap to the ImageView
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);  // Launch an image picker when the ImageView is clicked
        });

        uploadImage.setOnClickListener(v -> {
            if (bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);  // Compress the selected image Bitmap into JPEG format
                byte[] bytes = byteArrayOutputStream.toByteArray();  // Convert the compressed image to byte array
                final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);  // Encode the byte array to Base64 string

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "https://thelightsurprise.com/upload.php";  // URL for image upload

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                    if (response.equals("success")) {
                        Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();  // Show success message
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response, Toast.LENGTH_SHORT).show();  // Show error message
                    }
                }, error -> {
                    Toast.makeText(MainActivity.this, "" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();  // Show error message
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> paramV = new HashMap<>();
                        paramV.put("image", base64Image);  // Add the Base64 image string as a parameter
                        return paramV;
                    }
                };

                queue.add(stringRequest);  // Add the request to the request queue
            } else {
                Toast.makeText(MainActivity.this, "Select the Image first", Toast.LENGTH_SHORT).show();  // Show a message to select an image first
            }
        });
    }
}