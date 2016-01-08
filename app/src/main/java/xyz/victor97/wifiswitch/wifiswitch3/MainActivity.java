package xyz.victor97.wifiswitch.wifiswitch3;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int h, m;
    private TextView mTextview;
    private ProgressView mProgressView;
    private SocketService mSocketService;
    private FloatingActionButton FAB;

    private List<FloatingActionButton> fabList = new ArrayList<>();

    private static Calendar c;

    private boolean lighton = false;

    private int maxtime;
    private int starttime;

    private boolean runflag = false;

    private int animp = 0;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = Calendar.getInstance();

        mSocketService = new SocketService(this);
        mSocketService.StartSocket("192.168.4.1", 6789);

        mTextview = (TextView) findViewById(R.id.textview);
        mProgressView = (ProgressView) findViewById(R.id.progressview);

        mProgressView.setProgress(0);

        fabList.add((FloatingActionButton) findViewById(R.id.sfab1));
        fabList.add((FloatingActionButton) findViewById(R.id.sfab2));
        fabList.add((FloatingActionButton) findViewById(R.id.sfab3));
        fabList.add((FloatingActionButton) findViewById(R.id.sfab4));
        fabList.add((FloatingActionButton) findViewById(R.id.sfab5));
        fabList.add((FloatingActionButton) findViewById(R.id.sfab6));

        for (FloatingActionButton f : fabList) {
            f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#505050")));
        }

        mTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog TPD = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        h = hourOfDay;
                        m = minute;
                        mTextview.setText("");
                        if (h < 10) mTextview.append(" ");
                        mTextview.append(String.valueOf(h));
                        mTextview.append(":");
                        if (m < 10) mTextview.append("0");
                        mTextview.append(String.valueOf(m));
//                        mSocketService.SendMsg(mTextview.getText().toString());
                    }
                }, 0, 0, true);
                TPD.show();
            }
        });

        FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nh = c.get(Calendar.HOUR_OF_DAY);
                int nm = c.get(Calendar.MINUTE);
                int ns = c.get(Calendar.SECOND);
                int nt = (nh * 60 + nm) * 60 + ns;
                int st = (h * 60 + m) * 60;
                if (st < nt) st += 86400;
                starttime = nt;
                maxtime = st - nt;
                int dt = (st - nt) * 1000;

                byte[] buffer = new byte[7];
                if (lighton) buffer[0] = 0x00; else buffer[0] = 0x01;
                buffer[1] = (byte)((dt >> 24) & 0xFF);
                buffer[2] = (byte)((dt >> 16) & 0xFF);
                buffer[3] = (byte)((dt >> 8) & 0xFF);
                buffer[4] = (byte)(dt & 0xFF);
                buffer[5] = 0x0D;
                buffer[6] = 0x0A;
                mSocketService.SendMsg(buffer);
                runflag = true;
                handler.postDelayed(timerun, 100);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(SocketService.CONNECTED);
        filter.addAction(SocketService.RECEIVEDATA);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocketService.StopSocket();
        unregisterReceiver(mBroadcastReceiver);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case SocketService.CONNECTED:
                    break;
                case SocketService.RECEIVEDATA:
                    String s = intent.getStringExtra("data");
                    if (s.startsWith("ON")) {
                        lighton = true;
                        mTextview.setBackground(getResources().getDrawable(R.drawable.oval));
                        runflag = false;
                        mProgressView.setProgress(0);
                    }
                    if (s.startsWith("OFF")) {
                        lighton = false;
                        mTextview.setBackground(getResources().getDrawable(R.drawable.oval_off));
                        runflag = false;
                        mProgressView.setProgress(0);
                    }
                    if (s.startsWith("hello")) {
                        animp = 5;
                        handler.post(animrun);
                    }
                    break;
            }
        }
    };

    Runnable timerun = new Runnable() {
        @Override
        public void run() {
            if (!runflag) return;
            c = Calendar.getInstance();
            int nh = c.get(Calendar.HOUR_OF_DAY);
            int nm = c.get(Calendar.MINUTE);
            int ns = c.get(Calendar.SECOND);
            int nt = (nh * 60 + nm) * 60 + ns;
            int dt = nt - starttime;
            if (dt < 0) dt += 86400;

            mProgressView.setProgress(360 - dt * 360 / maxtime);

            handler.postDelayed(timerun, 500);
        }
    };


    Runnable animrun = new Runnable() {
        @Override
        public void run() {
            fabList.get(animp).setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            animp--;
            if (animp >= 0) handler.postDelayed(animrun, 500);
        }
    };

}
