package org.lxz.utils.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.security.Key;

public class AndroidShare {

	protected String filename;
	protected Context context;
	protected SharedPreferences settings;
	

	public AndroidShare(Context context,String filename) {
		super();
		this.filename = filename;
		this.context=context;
		this.settings = context.getSharedPreferences(filename,
					Context.MODE_PRIVATE);
	}
	
	public  void put(String key,String value) {
		
		SharedPreferences.Editor localEditor = settings.edit();
        localEditor.putString(key, value);
        
		localEditor.commit();
	}
	public  void put(String key,Integer value) {

		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putInt(key, value);
		localEditor.commit();
	}

	public void put(String key,boolean value)
	{
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putBoolean(key, value);
		localEditor.commit();
	}

	public void put(String key,Float value)
	{
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putFloat(key, value);
		localEditor.commit();
	}


	public void put(String key,Long value)
	{
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putLong(key, value);
		localEditor.commit();
	}



	public SharedPreferences getSettings() {
		return settings;
	}


	public  String getString(String key) {
		return  settings.getString(key,null);
	}

	public  String getString(String key,String value) {
		return  settings.getString(key,value);
	}

	public Float getFloat(String key,float value){
        return settings.getFloat(key,value);
	}
	public Float getFloat(String key){
		return settings.getFloat(key,0f);
	}

	public Integer getInt(String key,Integer value){
		return settings.getInt(key,value);
	}
	public Integer getInt(String key){
		return settings.getInt(key,0);
	}

	public Long getLong(String key,Long value){
		return settings.getLong(key,value);
	}
	public Long getLong(String key){
		return settings.getLong(key,0l);
	}

	public Boolean getBoolean(String key,Boolean value){
		return settings.getBoolean(key,value);
	}
	public Boolean getBoolean(String key){
		return settings.getBoolean(key,false);
	}

	public void clear(Context c, String filename) {
		SharedPreferences settings = c.getSharedPreferences(filename,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.clear()
		.commit();
	}
	
	

}
