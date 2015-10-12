package com.example.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Provice;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity{

	public static final int LEAVEL_PROVICE = 0;
	public static final int LEAVEL_CITY = 1;
	public static final int LEAVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView textView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	//ʡ�б�
	private List<Provice> proviceList;
	//���б�
	private List<City> cityList;
	//���б�
	private List<County> countyList;
	//ѡ�е�ʡ��
	private Provice selectProvice;
	//ѡ�еĳ���
	private City selectCity;
	//ѡ�еļ���
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		textView = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel == LEAVEL_PROVICE)
				{
					selectProvice = proviceList.get(position);
					queryCity();
				}
				else if(currentLevel == LEAVEL_CITY)
				{
					selectCity = cityList.get(position);
					queryCounty();
				} else if(currentLevel == LEAVEL_COUNTY)
				{
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
			
		});
		queryProvice();
	}
	
	//��ѯȫ��ʡ�����û�е���������ѯ
	private void queryProvice()
	{
		proviceList = coolWeatherDB.loadProvices();
		if(proviceList.size()>0)
		{
			dataList.clear();
			for(Provice provice:proviceList)
			{
				dataList.add(provice.getProviceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("�й�");
			currentLevel = LEAVEL_PROVICE;
		}
		else
		{
			queryFromServer(null,"provice");
		}
	}
	//��ѯ���У����û�е���������ѯ
	private void queryCity()
	{
		cityList = coolWeatherDB.loadCitys(selectProvice.getId());
		if(cityList.size()>0)
		{
			dataList.clear();
			for(City city:cityList)
			{
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectProvice.getProviceName());
			currentLevel = LEAVEL_CITY;
		}
		else
		{
			queryFromServer(selectProvice.getProviceCode(),"city");
		}
	}
	
	//��ѯ�سǣ����û�е���������ѯ
	private void queryCounty()
	{
		countyList = coolWeatherDB.loadCountys(selectCity.getId());
		if(countyList.size()>0)
		{
			dataList.clear();
			for(County county: countyList)
			{
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectCity.getCityName());
			currentLevel = LEAVEL_COUNTY;
		}
		else
		{
			queryFromServer(selectCity.getCityCode(),"county");
		}
	}
	
	//�ӷ�������ѯ����
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code))
		{
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		else
		{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("provice".equals(type))
				{
					result = Utility.handleProvicesResponse(coolWeatherDB, response);
				} else if("city".equals(type)){
					result = Utility.handleCitiesResponse(coolWeatherDB, response, selectProvice.getId());
				} else if("county".equals(type)){
					result = Utility.handleCountieResponse(coolWeatherDB, response, selectCity.getId());
				} 
				if(result)
				{
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if("provice".equals(type))
							{
								queryProvice();
							} else if("city".equals(type)){
								queryCity();
							} else if("county".equals(type)){
								queryCounty();
							}
						}
					});
				}
				
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {				
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}					
		} );
	}
	//��ʾ���ȿ�
	private void showProgressDialog()
	{
		if(progressDialog==null)
		{
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	//�رնԻ���
	private void closeProgressDialog()
	{
		if(progressDialog!=null)
		{
			progressDialog.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel == LEAVEL_COUNTY){
			queryCity();
		} else if(currentLevel == LEAVEL_CITY)
		{
			queryProvice();
		} else {
			finish();
		}
	}
	
	
}
