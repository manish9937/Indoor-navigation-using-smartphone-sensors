package com.example.rishabh.nevdijsktra;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.graphics.Canvas;
import android.graphics.Bitmap;

import com.example.rishabh.nevdijsktra.ToolBox.SensorData;
import com.example.rishabh.nevdijsktra.ToolBox.wifiData;
import com.example.rishabh.nevdijsktra.stepDetection.StepTrigger;

import java.io.IOException;
import java.util.List;


public class Capture extends SurfaceView implements SurfaceHolder.Callback,
        OnTouchListener,StepTrigger {

    public int temp=0;
    private Context ctx;
    SurfaceHolder holder;
    public Bitmap map;
    private Bitmap arrow;
    public static float x;

    public static float y;
    private PaintThread draw;
    AlertDialog.Builder dialoga;
    private WifiManager wify;
    public wifiData wifidata;
    private SensorData sensorData;
    private boolean wifi_registered = false;
    public static boolean scan_end = false;
    private boolean data_proc = false;
    private AlertDialog.Builder dialogStart, dialogEnd;
    private PointF startPoint, endPoint;
    private PaintThread painter;
    private wifiSampler wifiSamp;

    String msg = "Android : ";
    @SuppressLint("ClickableViewAccessibility")
    public Capture(Context context, AttributeSet attrs) {
        super(context,attrs);


        this.ctx = context;
        this.setOnTouchListener(this);

        holder = getHolder();
        holder.addCallback(this);

        map = inventory.getMap(ctx);

        arrow = inventory.getArrow(ctx);
        wify = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifidata = new wifiData(wify, context);

        sensorData = new SensorData(context);
        sensorData.loadCompass();
        sensorData.loadAcc();
        sensorData.loadMagnetic();


    }

    // Draw everything here; make other classes accessible to this
    @Override
    public void onDraw(Canvas c) {

        drawMap(c);
        drawArrow(c);
        drawPoints(inventory.drawnpoints, c);

        /*c.drawText("Steps :" + SensorData.stepCounter, 50, 800,
                inventory.text50BLUE());
        c.drawText("Acc data X:" + SensorData.lastAcc[0] + ", ", 50, 850,
                inventory.text50MAG());
        c.drawText("Acc data Y:" + SensorData.lastAcc[1] + ", ", 50, 900,
                inventory.text50MAG());
        c.drawText("Acc data Z:" + SensorData.lastAcc[2] + ", ", 50, 950,
                inventory.text50());*/

    }
    void drawMap(Canvas c) {
        if (map == null) {
            map = inventory.getMap(ctx);
        }
        c.drawBitmap(map, 0, 0, null);
    }
    void drawArrow(Canvas c) {

        // log values of sensor s
        /*c.drawText(String.valueOf(SensorData.getCompassValue()), 50, 50,
                inventory.text50());
        c.drawText(String.valueOf(SensorData.getMagneticValue()), 50, 100,
                inventory.text50());
        c.drawText("X: " + String.valueOf(inventory.X), 50, 150,
                inventory.text50BLUE());
        c.drawText("Y: " + String.valueOf(inventory.Y), 50, 250,
                inventory.text50BLUE());*/

        // setting boundary
        if (inventory.X >= 720)
            inventory.X = 720 - arrow.getWidth();
        if (inventory.Y >= 1200)
            inventory.Y = 1200 - arrow.getHeight();

        if (inventory.X <= 0)
            inventory.X = 0 + arrow.getWidth();
        if (inventory.Y <= 0)
            inventory.Y = 0 + arrow.getHeight();

        Matrix m = new Matrix();
        m.setRotate((float) (SensorData.getCompassValue()),
                arrow.getWidth() / 2.0f, arrow.getHeight() / 2.0f);


        m.postTranslate(inventory.X - arrow.getWidth() / 2.0f, inventory.Y
                - arrow.getHeight() / 2.0f);
        c.drawBitmap(arrow, m, null);

    }
    public void drawPoints(List<PointF> points, Canvas c) {

        // loop through points and plot circles
        if (points != null && points.size() != 0) {

            int size = points.size();

            for (int i = 0; i <= (size - 1); i++) {


                float px = points.get(i).x;
                float py = points.get(i).y;
                if(i==0&&temp==0){
                    inventory.X =px;
                    inventory.Y =py;
                    drawArrow(c);
                    temp=1;
                }
                // c.drawCircle(points.get(i).x, points.get(i).y, 10, pa);
                RectF r = new RectF();
                r.set(px-10, py-10, px + 10.0f, py + 10.0f);
                c.drawRect(r, inventory.redPaint());

                // draw line; draw from previous to current
                if (size > 1 && i != 0) {
                    float x = points.get(i - 1).x;
                    float y = points.get(i - 1).y;
                    c.drawLine(x, y, points.get(i).x, points.get(i).y,
                            inventory.bluePaint());



                }
            }

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        draw = new PaintThread(holder, this);
        draw.setRun(true);
        draw.start();
        setWillNotDraw(false);// this is done to prevent the surfaceview from
        // ignoring postinvalidate() call
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    /*empty*/
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
// we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        draw.setRun(false);

        map = null;
        while (retry) {
            try {
                draw.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        AlertDialog.Builder dg;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                //Log.d(msg, "The onCreate() event rishabh");
                dg = new AlertDialog.Builder(ctx);

                dg.setMessage("@ x:" + x + " y:" + y);
                //dg.show();
                return true;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return false;
    }
    // register wifi function
    public void registerReciever() {
        ctx.registerReceiver(wifidata, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wify.startScan();
    }
    public void unregisterReciever() {
        ctx.unregisterReceiver(wifidata);// stops reading data
    }
    // save recording to database
    public void writeMappingtoFile() {
        try {
            wifidata.writetofile(inventory.locationPoints);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void trigger(long now_ms, double compDir) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dataHookAcc(long now_ms, double x, double y, double z) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dataHookComp(long now_ms, double x, double y, double z) {
        // TODO Auto-generated method stub

    }

    @Override
    public void timedDataHook(long now_ms, double[] acc, double[] comp) {
        // TODO Auto-generated method stub

    }
}
class wifiSampler extends BroadcastReceiver {

    int Strongest;
    String StrongestAP;
    Context ctx;
    WifiManager wm;

    public wifiSampler(WifiManager wifi, Context context) {
        this.wm = wifi;
        this.ctx = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<ScanResult> results = wm.getScanResults();
        inventory.toast("Reading data", context, true);
        for (ScanResult result : results) {

            // compute the strongest ap
            if (Math.abs(result.level) > Strongest) {
                Strongest = result.level;
                StrongestAP = result.BSSID;
            }
        }

        wm.startScan();
    }

}
