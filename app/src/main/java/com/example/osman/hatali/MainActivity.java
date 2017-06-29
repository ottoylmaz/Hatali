package com.example.osman.hatali;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMİSSİON=2084;
    TextView tv;
    TextView tv1;
    EditText et;
    Button buton;
    InputStream is;
    String[] string_dizi;

    protected void onStart() {
        super.onStart();








    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){

            Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,CODE_DRAW_OVER_OTHER_APP_PERMİSSİON);


        }
        else {
            initializeView();


        }



    }


    private void initializeView(){
        findViewById(R.id.notify_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this,FloatingViewService.class));
                finish();

            }
        });


    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==CODE_DRAW_OVER_OTHER_APP_PERMİSSİON){
            if(resultCode==RESULT_OK){
                initializeView();
            }
            else {
                Toast.makeText(this,"Uygulamayı kull...",Toast.LENGTH_SHORT).show();
                finish();

            }

        }
        else {
            super.onActivityResult(requestCode,resultCode,data);

        }


    }




}
