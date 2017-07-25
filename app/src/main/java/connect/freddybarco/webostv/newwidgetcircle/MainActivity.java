package connect.freddybarco.webostv.newwidgetcircle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements IWidgetActivity{

    private static final String TAG =MainActivity.class.getSimpleName();
    private CircularSlider CS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         CS = (CircularSlider) findViewById(R.id.circular);
         CS.setOnSliderRangeMovedListener(this);
    }



    @Override
    public void valuechange(int angle) {
        Log.d(TAG,"Angle : "+ angle);
    }
}
