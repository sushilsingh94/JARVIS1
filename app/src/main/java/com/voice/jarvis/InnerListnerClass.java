package com.voice.jarvis;

import android.os.AsyncTask;
import android.os.Bundle;

public class InnerListnerClass extends AsyncTask<String, String, String>{
	
	InnerListnerClass(Bundle results){
		
	}

	@Override
	protected String doInBackground(String... arg0) {
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

}
