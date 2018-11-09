package com.example.enemy.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.enemy.myapplication.utils.Permission.RxPermissions;
import com.example.enemy.myapplication.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    String m1, m2, m3, m4;
    EditText e1, e2, e3, e4;
    Button cam, save;
    ImageView image;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.editText1);
        e2 = findViewById(R.id.editText2);
        e3 = findViewById(R.id.editText3);
        e4 = findViewById(R.id.editText4);
        cam = findViewById(R.id.upload);
        image = findViewById(R.id.photo);
        save = findViewById(R.id.Submit);


        cam.setOnClickListener(view -> getRxPermissions()
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Utility utility = new Utility();
                        utility.selectImage(this);
                    }
                }));

        save.setOnClickListener(v -> {
            m1 = e1.getText().toString();
            m2 = e2.getText().toString();
            m3 = e3.getText().toString();
            m4 = e4.getText().toString();

            PDFWriter mPDFWriter = new PDFWriter(PaperSize.FOLIO_WIDTH, PaperSize.FOLIO_HEIGHT);
            mPDFWriter.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_BOLD, StandardFonts.WIN_ANSI_ENCODING);
            mPDFWriter.addText(85, 75, 18, m1+m2+m3+m4);
            mPDFWriter.addImage(400,600,imageBitmap);
            String pdfContent = mPDFWriter.asString();
            outputToFile("Information.pdf", pdfContent, "ISO-8859-1");
            

        });

    }

    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    public void tp(View view) {
        Intent a = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (a.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(a, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultData, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultData == RESULT_OK) {
            onCaptureImageResult(data);
        }
    }

    public RxPermissions getRxPermissions() {
        return new RxPermissions(this);
    }

    private void outputToFile(String fileName, String pdfContent, String encoding) {
        File downloads = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        if (!downloads.exists() && !downloads.mkdirs())
            throw new RuntimeException("Could not create download folder");

        File newFile = new File(downloads, fileName);
        Log.e("PDF", "Writing file to " + newFile);

        try {
            newFile.createNewFile();
            try {
                FileOutputStream pdfFile = new FileOutputStream(newFile);
                pdfFile.write(pdfContent.getBytes(encoding));
                pdfFile.close();

                Toast.makeText(getBaseContext(),"Saved Successfully Location :"+newFile.getCanonicalPath(),Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                Log.e("PDF", e.getMessage());
            }
        } catch (IOException e) {
            Log.e("PDF", e.getMessage());
        }
    }

    private void onCaptureImageResult(Intent data) {

        imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (imageBitmap != null) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setImageBitmap(imageBitmap);
    }
}