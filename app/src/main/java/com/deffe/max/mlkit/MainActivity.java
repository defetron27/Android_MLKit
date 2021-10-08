package com.deffe.max.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private ImageView imageView;
    private Button snap,detect;
    private TextView textView;
    private static final int REQUEST_CODE = 100;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_view);
        snap = findViewById(R.id.snap_button);
        detect = findViewById(R.id.detect_button);

        snap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,REQUEST_CODE);
            }
        });

        detect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
                {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText)
                    {
                        List<FirebaseVisionText.Block> blockText = firebaseVisionText.getBlocks();

                        if (blockText.size() == 0)
                        {
                            textView.setText("No Text Detected");
                        }
                        else
                        {
                            String text = null;

                            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
                            {
                                text = block.getText();
                            }

                            textView.setText(text);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MainActivity.this, "Text Detection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == REQUEST_CODE)
            {
                if (data != null)
                {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
