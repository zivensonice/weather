package com.weather.app.activity;

import com.weather.app.R;
import com.weather.app.service.AutoUpdateService;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener
{
	private LinearLayout weatherInfoLayout;
	// ������
	private TextView cityText;
	// ��һʱ��
	private TextView temp1Text;
	// �ڶ�ʱ��
	private TextView temp2Text;
	// ����������Ϣ
	private TextView weatherInfoText;
	// ����ʱ��
	private TextView ptimeText;
	// ��ǰʱ��
	private TextView currentDateText;
	// �л����а�ť
	private Button switchCity;
	// ����������ť
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// �ж��Ƿ�ѡ����һ�����У����ѡ��ִ��ѡ�����
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		// ��ʼ��
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityText = (TextView) findViewById(R.id.tv_city_name);
		ptimeText = (TextView) findViewById(R.id.publish_text);
		weatherInfoText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.btn_switch_city);
		refreshWeather = (Button) findViewById(R.id.btn_refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode))
		{
			ptimeText.setText(" ͬ����..");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else
		{
			// ���û����������ʱ��ֱ����ʾ����������Ϣ
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_refresh_weather:
			ptimeText.setText(" ͬ����...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("county_code", "");
			if (!TextUtils.isEmpty(weatherCode))
			{
				queryWeatherInfo(weatherCode);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * ��ѯ�ؼ���������Ӧ���������š�
	 */
	private void queryWeatherCode(String countyCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ��
	 */
	private void queryFromServer(final String address, final String type)
	{
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
		{

			@Override
			public void onFinish(String response)
			{
				if ("countycode".equals(type))
				{
					if (!TextUtils.isEmpty(response))
					{
						// �ӷ��������ص������н����������Ĵ���
						String[] array = response.split("\\|");
						if (array != null && array.length == 2)
						{
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type))
				{
					// ������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e)
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						ptimeText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��ѯ������������Ӧ��������
	 */
	private void queryWeatherInfo(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void showWeather()
	{
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherInfoText.setText(prefs.getString("weather_info", ""));
		ptimeText.setText("����" + prefs.getString("publish_time", "") + "����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

}
