package com.weather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil
{

	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				HttpURLConnection connection = null;
				Log.d("Choose", "run");
				try
				{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null)
					{
						response.append(line);
					}
					if (listener != null)
					{
						// 回调onFinish()方法
						Log.d("Choose", "listener.onFinish");
						listener.onFinish(response.toString());
					}
					// String test =
					// "01|北京,02|上海,03|天津,04|重庆,05|黑龙江,06|吉林,07|辽宁,08|内蒙古,09|河北,10|山西,11|陕西,12|山东,13|新疆,14|西藏,15|青海,16|甘肃,17|宁夏,18|河南,19|江苏,20|湖北,21|浙江,22|安徽,23|福建,24|江西,25|湖南,26|贵州,27|四川,28|广东,29|云南,30|广西,31|海南,32|香港,33|澳门,34|台湾";
					// listener.onFinish(test);
				} catch (Exception e)
				{
					if (listener != null)
					{
						// 回调onError()方法
						Log.d("Choose", e.toString());
						listener.onError(e);
					}
				} finally
				{
					if (connection != null)
					{
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}