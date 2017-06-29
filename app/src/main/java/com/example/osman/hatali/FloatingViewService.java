package com.example.osman.hatali;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Osman on 24.05.2017.
 */

public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    InputStream is;
    String[] string_dizi;




    public FloatingViewService(){
    }

    @Override


    public IBinder onBind(Intent intent){return null;}
    public void onCreate(){
        super.onCreate();










        mFloatingView= LayoutInflater.from(this).inflate(R.layout.layout_floating_widget,null);

        final WindowManager.LayoutParams params=new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity= Gravity.TOP | Gravity.LEFT;
        params.x=0;
        params.y=100;
        mWindowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView,params);

        final View collapsedView=mFloatingView.findViewById(R.id.collapse_view);
        final View expandedView=mFloatingView.findViewById(R.id.expanded_container);





        final android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                    if (clipboard.hasPrimaryClip()) {
                        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                        TextView tv = (TextView) mFloatingView.findViewById(R.id.tv);
                        TextView tv1 = (TextView) mFloatingView.findViewById(R.id.tv1);

                        tv1.setText(showString(translate()));
                    }


    }
});















        ImageView closeButtonCollapsed=(ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        ImageView closeButton=(ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                stopSelf();
            }
        });




        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        initialX=params.x;
                        initialY=params.y;
                        initialTouchX=event.getRawX();
                        initialTouchY=event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff=(int) (event.getRawX()-initialTouchX);
                        int Ydiff=(int) (event.getRawY()-initialTouchY);
                        if (Xdiff<10 &&Ydiff<10){
                            if (isViewCollapsed()){
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x=initialX+(int) (event.getRawX()-initialTouchX);
                        params.y=initialY+(int) (event.getRawY()-initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView,params);
                        return true;


                }


                return false;
            }
        });

    }

    private boolean isViewCollapsed(){
        return mFloatingView==null || mFloatingView.findViewById(R.id.collapse_view).getVisibility()==View.VISIBLE;
    }
    public void onDestroy(){
        super.onDestroy();
        if (mFloatingView!=null) mWindowManager.removeViewImmediate(mFloatingView);
    }
    public  String showString(String s){ // bu fonksiyon sadece ilk anlamını göstermek için ilk text view
        return s.split(",")[0];          // de gözüküyo tüm anlamlar diğer textviewde gözükecek
    }
    public String translate(){          //burda excel arayıp buluyo sonucu string döndürüyo

        is = getResources().openRawResource(R.raw.cvs_test);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String reading_line,result = "no result";
        String[] reading_line_array;
        String userinput = getInput(); //burda clipboarddan input alan fonksiyonu çağırıyo
        try {
            while ((reading_line = reader.readLine()) != null) {
                reading_line_array = reading_line.split(";");
                if (reading_line_array[1].equals(userinput))
                    result = reading_line_array[2];

            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
        /*String result_array[] = new String[result.split(",").length];
        result_array = result.split(",");*/
        return result;

    }
    public String getInput(){   //clipboarddan veri alan fonksiyon string döndürüyo
        ClipData.Item item;

        String userinput = "no input yet";
        android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(clipboard.hasPrimaryClip()) {

            item = clipboard.getPrimaryClip().getItemAt(0);
            userinput = item.getText().toString();


        }

        return userinput.toLowerCase(); //burada hepsini küçük yaptım yoksa araken bulamıyodu door u buluyo DOOR u bulmuyo mesela
    }
}
