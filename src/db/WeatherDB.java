package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDB
{
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "weather";
	/**
	 * 数据库版本名称
	 */
	public static final int VERSION = 1;
	private static WeatherDB weatherDB;
	private SQLiteDatabase db;

	/**
	 * 私有化构造方法
	 * 
	 * @param context
	 */
	private WeatherDB(Context context)
	{
		WeatherOpenHelper helper = new WeatherOpenHelper(context, DB_NAME,
				null, VERSION);
		db = helper.getWritableDatabase();
	}

	/**
	 * 获取WeatherDB的实例化对象
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static WeatherDB getInstance(Context context)
	{

		if (weatherDB == null)
		{
			weatherDB = new WeatherDB(context);
		}
		return weatherDB;
	}

	/**
	 * 将Province实例化存储到数据库中
	 * 
	 * @param province
	 */
	public void saveProvince(Province province)
	{
		if (province != null)
		{
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	/**
	 * 将City实例存储到数据库中
	 * 
	 * @param city
	 */
	public void saveCity(City city)
	{
		if (city != null)
		{
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库读取某个省下面所有的城市信息
	 * 
	 * @param provinceId
	 * @return
	 */
	public List<City> loadCities(int provinceId)
	{
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?", new String[]
		{ String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst())
		{
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor
					.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor
					.getColumnIndex("city_code")));
			city.setProvinceId(provinceId);
			list.add(city);
		}
		while (cursor.moveToLast())
			;
		return list;
	}

	/**
	 * 将County实例化存储到数据库中
	 * 
	 * @param county
	 */
	public void saveCounty(County county)
	{
		if (county != null)
		{
			ContentValues values = new ContentValues();
			values.put("count_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}

	/**
	 * 从数据库中读取区、县信息
	 * @param cityId
	 * @return
	 */
	public List<County> loadCounties(int cityId)
	{
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id=?", new String[]
		{ String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst())
		{
			do
			{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCityId(cityId);
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 从数据库中读取全国所有的省份信息
	 * 
	 * @return
	 */
	public List<Province> loadProvinces()
	{
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst())
		{
			do
			{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}

}
