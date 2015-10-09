package com.example.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Provice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	//数据库名
	public static final String DB_NAME = "cool_weather";
	//版本号
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	//获取CoolWeatherDB实例
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if(coolWeatherDB == null)
		{
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	//存储provice数据
	public void saveProvice(Provice provice)
	{
		if(provice != null)
		{
			 ContentValues values = new ContentValues();
			 values.put("provice_name", provice.getProviceName());
			 values.put("provice_code", provice.getProviceCode());
			 db.insert("Province", null, values);
		}
	}
	
	//查询provice数据
	public List<Provice> loadProvices()
	{
		List<Provice> list = new ArrayList<Provice>();
		Cursor cursor = db.query("Provice", null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				Provice provice = new Provice();
				provice.setId(cursor.getInt(cursor.getColumnIndex("id")));
				provice.setProviceName(cursor.getString(cursor.getColumnIndex("provice_name")));
				provice.setProviceCode(cursor.getString(cursor.getColumnIndex("cursor")));
				list.add(provice);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	//存储city数据
	public void saveCity(City city)
	{
		if(city != null)
		{
			 ContentValues values = new ContentValues();
			 values.put("city_name", city.getCityName());
			 values.put("city_code", city.getCityCode());
			 values.put("provice_id", city.getProviceId());
			 db.insert("City", null, values);
		}
	}
	
	//查询provice数据
	public List<City> loadCitys(int proviceId)
	{
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "provice_id=?", new String[] {String.valueOf(proviceId)}, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProviceId(cursor.getInt(cursor.getColumnIndex("provice_id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	//存储county数据
	public void saveCounty(County county)
	{
		if(county != null)
		{
			 ContentValues values = new ContentValues();
			 values.put("county_name", county.getCountyName());
			 values.put("county_code", county.getCountyCode());
			 values.put("city_id", county.getCityId());
			 db.insert("County", null, values);
		}
	}
	
	//查询provice数据
	public List<County> loadCountys(int cityId)
	{
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id=?", new String[] {String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
	}
}
