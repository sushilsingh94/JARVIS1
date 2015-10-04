package com.voice.jarvis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.voice.java.service.BootUpReceiver;
import com.voice.java.service.CommandService;
import com.voice.java.service.CommandVoiceListner;
import com.voice.java.service.LocationService;
import com.voice.java.service.MyService;
import com.voice.speedogauge.gauge.SpeedometerGauge;

import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Sushil on 10/3/2015.
 */
public class JARVISActivity extends ActionBarActivity /*implements RecognitionListener*/{

	private static TextView returnedText;
	private static TextView speedTextView;
	private TextView displayText;
	private static TextView txtSpeechLoading;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private boolean mResultAvailable;
    private List<String> mResults = new ArrayList<String>();;
    private CommandProcessorImpl commandProcessorImpl;
    private CommandVoiceListner commandVoiceListner = new CommandVoiceListner(this);
	Intent myserviceIntent;
	Location mLocation;
	private static SpeedometerGauge speedometer;
	BootUpReceiver mMessageReceiver = new BootUpReceiver();
	public static boolean isJarvisActivityVisible = true;

    public static JARVISActivity getInstance(){
    	return new JARVISActivity();
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jarvis);
		isJarvisActivityVisible = true;
		returnedText = (TextView) findViewById(R.id.txtSpeechInput);
		txtSpeechLoading = (TextView) findViewById(R.id.txtSpeechLoading);
		displayText = (TextView) findViewById(R.id.textDisplay);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

		speedometer = (SpeedometerGauge) findViewById(R.id.speedometer);
		speedometer.setMaxSpeed(300);
		speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
			@Override
			public String getLabelFor(double progress, double maxProgress) {
				return String.valueOf((int) Math.round(progress));
			}
		});
		speedometer.setMaxSpeed(300);
		speedometer.setMajorTickStep(30);
		speedometer.setMinorTicks(2);
		speedometer.addColoredRange(30, 140, Color.GREEN);
		speedometer.addColoredRange(140, 180, Color.YELLOW);
		speedometer.addColoredRange(180, 400, Color.RED);


		speedTextView = (TextView)findViewById(R.id.speedView);

        returnedText.setMovementMethod(new ScrollingMovementMethod());
		mNoSpeechCountDown.start();

		//new MyService(this);
		//myserviceIntent = new Intent(this,MyService.class);
		//startService(myserviceIntent);

		startService(new Intent(this, LocationService.class));

        toggleButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*new CommandService(JARVISActivity.this);
			    startService(new Intent(JARVISActivity.this, CommandService.class));*/

				stopService(myserviceIntent);
			}
		});
 
	}

	public static void setSpeechLoadingTXT(String text){
		txtSpeechLoading.setText(text);
	}
	public static void setSpeechInputTXT(String text){
		returnedText.setText(text);
	}
	public static void setSpeedText(double speed, int i){
		if(speedometer !=null) {
			speedometer.setSpeed(speed, 1000, 300);
		}
		speedTextView.setText(String.valueOf(new BigDecimal(speed).setScale(2, BigDecimal.ROUND_CEILING)) + " Kmph ");
		if(speed > 60){
			speedTextView.setBackgroundColor(Color.RED);
		}else{
			speedTextView.setBackgroundColor(Color.WHITE);
		}
	}
	protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000)
	{
		@Override
		public void onTick(long millisUntilFinished)
		{

		}

		@Override
		public void onFinish()
		{
			commandVoiceListner.startListening();
			mNoSpeechCountDown.start();
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jarvi, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            //openSearch();
	            return true;
	        case R.id.action_talkback:
	            startActivity(new Intent(this, AboutMeActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true); 
		//super.onBackPressed();
	}

	@Override
    public void onResume() {
        super.onResume();
		isJarvisActivityVisible = true;
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter());
    }
	
	@Override
    protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
		isJarvisActivityVisible = false;
        /*if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }*/
 
    }
	
	/*
	public List<RunningServiceInfo> gerRunningServices(){
		ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
        
        return rs;
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK))
		{

		}if ((keyCode == KeyEvent.KEYCODE_HOME))
		{

		}
		return true;
	}

}
