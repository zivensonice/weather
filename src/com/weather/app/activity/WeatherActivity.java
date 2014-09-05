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
	// 城市名
	private TextView cityText;
	// 第一时间
	private TextView temp1Text;
	// 第二时间
	private TextView temp2Text;
	// 天气描述信息
	private TextView weatherInfoText;
	// 发布时间
	private TextView ptimeText;
	// 当前时间
	private TextView currentDateText;
	// 切换城市按钮
	private Button switchCity;
	// 更新天气按钮
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// 判断是否选中了一个城市，如果选中执行选择操作
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		// 初始化
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
			ptimeText.setText(" 同步中..");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else
		{
			// 如果没有县区代号时就直接显示本地天气信息
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
			ptimeText.setText(" 同步中...");
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
	 * 查询县级代号所对应的天气代号。
	 */
	private void queryWeatherCode(String countyCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
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
						// 从服务器返回的数据中解析出天气的带好
						String[] array = response.split("\\|");
						if (array != null && array.length == 2)
						{
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type))
				{
					// 处理服务器返回的天气信息
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
						ptimeText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 查询天气代号所对应的天气。
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
		ptimeText.setText("今天" + prefs.getString("publish_time", "") + "发布");
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
