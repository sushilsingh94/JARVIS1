package com.voice.java.service;

import java.util.ArrayList;
import java.util.List;

import com.voice.jarvis.CommandProcessorImpl;
import com.voice.jarvis.JARVISActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

public class CommandVoiceListner implements RecognitionListener{
	private static Context ctx;
	public CommandVoiceListner(){	}
	
	public CommandVoiceListner(Context ctx){
		this.ctx = ctx;
	}

	private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "CommandVoiceListner";
    private List<String> mResults;
    private CommandProcessorImpl commandProcessorImpl;
	private boolean isRecognitionServiceAvailable = false;

	public void startListening(){
		Log.i(LOG_TAG, "context in listner : " + ctx);
		if (SpeechRecognizer.isRecognitionAvailable(ctx)) {
			Log.i(LOG_TAG, "context in listner1 : " + ctx);
			if (isRecognitionServiceAvailable){//(speech!=null){
				Log.i(LOG_TAG, "context in listner2 : " + ctx);
				speech.startListening(recognizerIntent);
				mResults = new ArrayList<String>();
			}
			else {
				startSR();
			}
		}
	}
	    
	public void startSR(){
			Log.i(LOG_TAG, "context in listner3 : " + ctx);
				isRecognitionServiceAvailable = true;
			if(speech != null) {
				speech.stopListening();
				speech.destroy();
			}
	        speech = SpeechRecognizer.createSpeechRecognizer(ctx);
	        speech.setRecognitionListener(this);
	        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
	                "en");
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
	                ctx.getPackageName());
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
	        speech.startListening(recognizerIntent);

	}
	
	
	@Override
	public void onBeginningOfSpeech() {
		Log.i(LOG_TAG, "onBeginningOfSpeech");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(LOG_TAG, "onEndOfSpeech");
		JARVISActivity.setSpeechLoadingTXT("Waiting...");
	}

	@Override
	public void onError(int errorCode) {
		isRecognitionServiceAvailable = false;
		String errorMessage = getErrorText(errorCode);
		Log.d(LOG_TAG, "FAILED " + errorMessage);
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		Log.i(LOG_TAG, "onPartialResults");
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
		Log.i(LOG_TAG, "onReadyForSpeech");
		JARVISActivity.setSpeechLoadingTXT("Listening....");
	}

	@Override
	public void onResults(Bundle results) {
        Log.d(LOG_TAG, "onResults " + results);
		isRecognitionServiceAvailable = false;
        mResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.i(LOG_TAG, "starting activity from listner");
        commandProcessorImpl = new CommandProcessorImpl(ctx);
        try {
        	//commandProcessorImpl.setmResults(mResults);
			commandProcessorImpl.filterUserInputText(mResults);
        	//new Thread(commandProcessorImpl).start();
		}catch (Exception e) {
			Log.d(LOG_TAG, "error occured onResult : " + e);
		}
        startListening();
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		//Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
	}
	
	public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
        case SpeechRecognizer.ERROR_AUDIO:
            message = "Audio recording error";
            break;
        case SpeechRecognizer.ERROR_CLIENT:
            message = "Client side error";
            startListening();
            break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            message = "Insufficient permissions";
            break;
        case SpeechRecognizer.ERROR_NETWORK:
            message = "Network error";
            startListening();
            break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            message = "Network timeout";
            startListening();
            break;
        case SpeechRecognizer.ERROR_NO_MATCH:
            message = "No match";
            speech.startListening(recognizerIntent);
            break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            message = "RecognitionService busy";
			speech.stopListening();
			speech.cancel();
			speech.destroy();
            //startListening();
            break;
        case SpeechRecognizer.ERROR_SERVER:
            message = "error from server";
            startListening();
            break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            message = "No speech input";
            startListening();
            break;
        default:
            message = "Didn't understand, please try again.";
            startListening();
            break;
        }
        return message;
    }

}
