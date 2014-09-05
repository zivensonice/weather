package com.weather.app.util;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weather.app.db.WeatherDB;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;
import com.weather.app.model.weatherinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility
{
	public static boolean handleCountiesResponse(WeatherDB db, String response,
			int cityId)
	{
		if (!TextUtils.isEmpty(response))
		{
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length != 0)
			{
				for (String p : allCounties)
				{
					String[] array = p.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					db.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean handleCitiesResponse(WeatherDB db, String response,
			int provinceId)
	{
		if (!TextUtils.isEmpty(response))
		{
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length != 0)
			{
				for (String p : allCities)
				{
					String[] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					db.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleProvincesResponse(WeatherDB db,
			String response)
	{
		if (!TextUtils.isEmpty(response))
		{
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length != 0)
			{
				for (String p : allProvinces)
				{
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// ���������������ݴ洢��Province����
					db.saveProvince(province);
				}
				Log.d("DATABASE", "saveProvince is OK");
				return true;
			}
		}
		return false;
	}

	// �������������ص�JSON���ݣ�����������ɵ����ݱ��浽����
	public static void handleWeatherResponse(Context context, String response)
	{
		Gson gson = new Gson();
		List<weatherinfo> list = gson.fromJson(response,
				new TypeToken<List<weatherinfo>>()
				{
				}.getType());

	}

	// �����������ص�����������Ϣ�洢��SharedPreferences�ļ���
	public static void saveWeatherInfo(Context context, weatherinfo info)
	{
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy��M��d��",
				Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", info.getCity());
		editor.putString("temp1", info.getTemp1());
		editor.putString("temp2", info.getTemp2());
		editor.putString("weather_info", info.getWeather());
		editor.putString("current_date", dataFormat.format(new Date()));
		editor.putString("publish_time", info.getPtime());
		editor.putString("weather_code", info.getCityid());
		editor.commit();
	}
}
