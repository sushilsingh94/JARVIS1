package com.voice.jarvis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.voice.java.utils.KeyWords;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sushil on 10/3/2015.
 */
public class CommandProcessorImpl implements CommandProcessor,Runnable {
	private static Context ctx;
	private String LOG_TAG = "CommandProcessorImpl";
	private List<String> mResults;
	
	public CommandProcessorImpl(){}
	public CommandProcessorImpl(Context ctx){
		this.ctx = ctx;
	}
	
	public CommandProcessorImpl getInstance(){
		return new CommandProcessorImpl();
	}
	
	
	private ApplicationInfo getApplicationInfo(String applicationName)
	{
		final PackageManager pm = ctx.getPackageManager();
		//get a list of installed apps.
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) {
		    System.out.println(packageInfo.packageName+ " ====" + pm.getLaunchIntentForPackage(packageInfo.packageName));
		    if(applicationName.equalsIgnoreCase((String) pm.getApplicationLabel(packageInfo)))
		    {
		    	return packageInfo;
		    }
		}
		return null;			
	}

	@Override
	public void openObject(List<String> mResult) throws Exception {
		PackageManager manager = ctx.getPackageManager();
		ApplicationInfo applicationInfo = getApplicationInfo(mResult.get(1).toString());
		System.out.println(applicationInfo.packageName);
		Intent intent = new Intent();
		intent = manager.getLaunchIntentForPackage(applicationInfo.packageName);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		Toast.makeText(ctx, "Opening up.."+applicationInfo.packageName, Toast.LENGTH_LONG).show();
		ctx.startActivity(intent);
		
	}

	@Override
	public void writeMessage(List<String> mResult) throws Exception {
		/*SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(getPhoneNumber(mResult.get(3), ctx), null, mResult.get(5).toString(), null, null);*/
		//Toast.makeText(ctx, "SMS sent to : "+mResult.get(3), Toast.LENGTH_LONG).show();


		////////////////////
		/*Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setData(Uri.parse("smsto:"));
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.putExtra("address", getPhoneNumber(mResult.get(3), ctx));
		smsIntent.putExtra("sms_body",mResult.get(5).toString());
		ctx.startActivity(smsIntent);
*/
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(getPhoneNumber(mResult.get(3), ctx), null, mResult.get(5).toString(), null, null);
	}

	@Override
	public void searchText(List<String> mResult) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void callPhoneNumber(List<String> mResult) throws Exception {
		String phoneNumber ="";
		StringBuilder sb = new StringBuilder();
		Iterator<String> itr = mResult.iterator();
		while(itr.hasNext()){
			String temp = itr.next();
			if(!temp.equals(KeyWords.CALL))
				sb.append(temp);
		}
		Log.i(LOG_TAG, "callPhoneNumber calling : " + sb.toString());
		phoneNumber = getPhoneNumber(sb.toString(), ctx);
		if(!phoneNumber.equalsIgnoreCase("Unsaved")){
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			Toast.makeText(ctx, "Calling first match.."+sb.toString(), Toast.LENGTH_LONG).show();
			AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setSpeakerphoneOn(true);
			//startActivity(getPackageManager().getLaunchIntentForPackage("com.skype.android"));
			ctx.startActivity(intent);
		}
		
	}

	@Override
	public void playMusic(List<String> mResult) throws Exception {
		//playMusic();
		// Read Mp3 file present under SD card
		/*MediaPlayer mPlayer;
		Log.i(LOG_TAG, "playMusic playing music : " );
		Toast.makeText(ctx, "playMusic playing music", Toast.LENGTH_LONG).show();
		Uri myUri1 = Uri.parse("file:///sdcard/songs/Dagabaaz Re - Dabangg 2.mp3");
		mPlayer  = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setDataSource(ctx, myUri1);
			mPlayer.prepare();
			mPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
*/

		PackageManager manager = ctx.getPackageManager();
		ApplicationInfo applicationInfo = getApplicationInfo(mResult.get(1).toString());
		System.out.println(applicationInfo.packageName);
		Intent intent = new Intent();
		intent = manager.getLaunchIntentForPackage(applicationInfo.packageName);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setAction(Intent.ACTION_VIEW);
		Toast.makeText(ctx, "Opening up.."+applicationInfo.packageName, Toast.LENGTH_LONG).show();
		ctx.startActivity(intent);
	}
	
	public String getPhoneNumber(String name, Context context) {
	    String ret = null;
	    String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
	            + " like'%" + name + "%'";
	    String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
	    Cursor c = context.getContentResolver().query(
	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
	            selection, null, null);
	    if (c.moveToFirst()) {
	        ret = c.getString(0);
	    }
	    c.close();
	    if (ret == null)
	        ret = "Unsaved";
	    return ret;
	}
	
	public void filterUserInputText(List<String> mResult) throws Exception{
		StringTokenizer stringTokenizer = new StringTokenizer(mResult.get(0));
		List<String> mResultList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		while (stringTokenizer.hasMoreTokens()) {
			String temp = stringTokenizer.nextToken();
            mResultList.add(temp);
            sb.append(temp+" ");
        }
		Toast.makeText(ctx, sb.toString(), Toast.LENGTH_LONG).show();
		JARVISActivity.setSpeechLoadingTXT("Waiting...");
		JARVISActivity.setSpeechInputTXT(sb.toString());
		if(mResultList.contains(KeyWords.OPEN)){
			openObject(mResultList);
		}if(mResultList.contains(KeyWords.SEND) && mResultList.contains(KeyWords.SMS)){
			if(mResultList.contains(KeyWords.TO)){
				if(mResultList.contains(KeyWords.SAYING)){
					writeMessage(mResultList);
				}else
					Toast.makeText(ctx, "Missing Parameter.. Text", Toast.LENGTH_LONG).show();
			}else
				Toast.makeText(ctx, "Missing parameters.. TO", Toast.LENGTH_LONG).show();
		}if(mResultList.contains(KeyWords.SEARCH)){
			searchText(mResultList);
		}if(mResultList.contains(KeyWords.CALL)){
			callPhoneNumber(mResultList);
		}if(mResultList.contains(KeyWords.PLAY) && mResultList.contains(KeyWords.MUSIC)){
			playMusic(mResultList);
		}if(mResultList.contains(KeyWords.CLOSE)){
			JARVISActivity.getInstance().finish();
		}
		
	}
	
	// Play Music
    protected void playMusic(){
        // Read Mp3 file present under SD card
    	MediaPlayer mPlayer;
    	Log.i(LOG_TAG, "playMusic playing music : " );
        Uri myUri1 = Uri.parse("file:///sdcard/songs/Dagabaaz Re - Dabangg 2.mp3");
        mPlayer  = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(ctx.getApplicationContext(), myUri1);
            mPlayer.prepare();
            // Start playing the Music file
            mPlayer.start();
                
        } catch (Exception e) {
            
        }
    }

	@Override
	public void run() {
		try {
			filterUserInputText(mResults);
			
		} catch (Exception e) {
			Log.i(LOG_TAG, "error executing task : " + e);
		}			
	}
	public List<String> getmResults() {
		return mResults;
	}
	public void setmResults(List<String> mResults) {
		this.mResults = mResults;
	}
	
	
	
}
