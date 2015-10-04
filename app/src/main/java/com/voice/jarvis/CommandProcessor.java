package com.voice.jarvis;

import java.util.List;
/**
 * Created by Sushil on 10/3/2015.
 */
public interface CommandProcessor {
	
	public void openObject(List<String> mResult) throws  Exception;
	
	public void writeMessage(List<String> mResult) throws Exception;
	
	public void searchText(List<String> mResult) throws Exception;
	
	public void callPhoneNumber(List<String> mResult) throws Exception;
	
	public void playMusic(List<String> mResult) throws Exception;

}
