package com.example.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Provice;

public class Utility {

	//解析和处理服务器返回的数据 provice
	public synchronized static boolean handleProvicesResponse(CoolWeatherDB coolWeatherDB,
			String response){
		if(!TextUtils.isEmpty(response))
		{
			String [] allProvices = response.split(",");
			if(allProvices != null && allProvices.length > 0)
			{
				for(String p:allProvices)
				{
					String [] array = p.split("\\|");
					Provice provice = new Provice();
					provice.setProviceCode(array[0]);
					provice.setProviceName(array[1]);
					coolWeatherDB.saveProvice(provice);
					
				}
				return true;
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的数据 city
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response,int proviceId){
		if(!TextUtils.isEmpty(response))
		{
			String [] allProvices = response.split(",");
			if(allProvices != null && allProvices.length > 0)
			{
				for(String p:allProvices)
				{
					String [] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProviceId(proviceId);
					coolWeatherDB.saveCity(city);
					
				}
				return true;
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的数据   county
	public synchronized static boolean handleCountieResponse(CoolWeatherDB coolWeatherDB,
			String response,int cityId){
		if(!TextUtils.isEmpty(response))
		{
			String [] allProvices = response.split(",");
			if(allProvices != null && allProvices.length > 0)
			{
				for(String p:allProvices)
				{
					String [] array = p.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
					
				}
				return true;
			}
		}
		return false;
	}
	
	//解析json数据
		public static void handleWeather(Context context,String response){
			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
				String cityName = weatherInfo.getString("city");
				String weatherCode = weatherInfo.getString("cityid");
				String temp1 = weatherInfo.getString("temp1");
				String temp2 = weatherInfo.getString("temp2");
				String weatherDesp = weatherInfo.getString("weather");
				String publishTime = weatherInfo.getString("ptime");
				saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1
				,String temp2,String weatherDesp,String publishTime)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
			SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putBoolean("city_selected", true);
			editor.putString("city_name", cityName);
			editor.putString("weather_code", weatherCode);
			editor.putString("temp1", temp1);
			editor.putString("temp2", temp2);
			editor.putString("weather_desp", weatherDesp);
			editor.putString("publish_time", publishTime);
			editor.putString("current_data", sdf.format(new Date()));
			editor.commit();
		}
}
