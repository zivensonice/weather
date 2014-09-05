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
						// �ص�onFinish()����
						Log.d("Choose", "listener.onFinish");
						listener.onFinish(response.toString());
					}
					// String test =
					// "01|����,02|�Ϻ�,03|���,04|����,05|������,06|����,07|����,08|���ɹ�,09|�ӱ�,10|ɽ��,11|����,12|ɽ��,13|�½�,14|����,15|�ຣ,16|����,17|����,18|����,19|����,20|����,21|�㽭,22|����,23|����,24|����,25|����,26|����,27|�Ĵ�,28|�㶫,29|����,30|����,31|����,32|���,33|����,34|̨��";
					// listener.onFinish(test);
				} catch (Exception e)
				{
					if (listener != null)
					{
						// �ص�onError()����
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