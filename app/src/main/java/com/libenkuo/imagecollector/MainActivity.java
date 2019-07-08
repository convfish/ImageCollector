package com.libenkuo.imagecollector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 999;

    private LinearLayout layoutTakePhoto;
    private Button btnTakePhoto;
    private ImageView imagePhoto;

    private LinearLayout layoutDashboard;
    private TextView textDashBoard;

    private LinearLayout layoutSettings;

    private File photo;

    private Uri desUri;
    private String storagePath;
    private File storageDir;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_take_photo:
                    layoutTakePhoto.setVisibility(View.VISIBLE);
                    layoutDashboard.setVisibility(View.GONE);
                    layoutSettings.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    layoutTakePhoto.setVisibility(View.GONE);
                    layoutDashboard.setVisibility(View.VISIBLE);
                    layoutSettings.setVisibility(View.GONE);

                    File[] childFiles = storageDir.listFiles();
                    textDashBoard.setText(String.valueOf(childFiles.length));

                    System.out.println("-----------");
                    for (File file : childFiles) {
                        System.out.println(file.getName());
                    }

                    return true;
                case R.id.navigation_settings:
                    layoutTakePhoto.setVisibility(View.GONE);
                    layoutDashboard.setVisibility(View.GONE);
                    layoutSettings.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    private static Uri getFileUri(Context context, String filePath) {
        Uri uri;

        File file = new File(filePath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, "com.libenkuo.imagecollector", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo:
                Intent openCameraIntent = new Intent();
                openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                try {

                    photo = File.createTempFile(timeStamp, ".jpg", storageDir);
                } catch (IOException exp) {
                    exp.printStackTrace();
                }

                desUri = getFileUri(this, photo.getPath());

                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
                startActivityForResult(openCameraIntent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(desUri));
                imagePhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException exp) {
                exp.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        storagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        storageDir = new File(storagePath);
        storageDir.mkdirs();


        layoutTakePhoto = findViewById(R.id.layout_take_photo);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(this);
        imagePhoto = findViewById(R.id.image_photo);

        layoutDashboard = findViewById(R.id.layout_dashboard);
        textDashBoard = findViewById(R.id.text_dashboard);


        layoutSettings = findViewById(R.id.layout_settings);



        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
