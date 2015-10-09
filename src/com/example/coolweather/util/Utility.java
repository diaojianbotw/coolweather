package com.example.coolweather.util;

import android.text.TextUtils;

import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Provice;

public class Utility {

	//解析和处理服务器返回的数据
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
					return true;
				}
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的数据
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
					return true;
				}
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的数据
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
					return true;
				}
			}
		}
		return false;
	}
}
