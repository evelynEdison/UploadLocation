package com.xutong.uploadlocation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Utilities {

	public static byte[] getByteArrayFromFile(String fileName) {
		File file = null;
		try {
			file = new File(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (!file.exists() || !file.isFile() || !file.canRead()) {
			LogUtil.i("Grace", "!file.exists() || !file.isFile() || !file.canRead()");
			return null;
		}

		byte[] byteArray = null;

		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int count;
			byte buffer[] = new byte[1024];
			while ((count = fis.read(buffer)) > 0) {
				baos.write(buffer, 0, count);
			}
			byteArray = baos.toByteArray();
			fis.close();
			baos.flush();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return byteArray;
	}

	public static String loadJSONFromResource(Context context, int resource) {
		if (resource <= 0)
			return null;

		String json = null;
		InputStream is = context.getResources().openRawResource(resource);
		try {
			if (is != null) {
				int size = is.available();
				byte[] buffer = new byte[size];
				is.read(buffer);
				json = new String(buffer, "UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return json;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		Rect frame = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.heightPixels - statusBarHeight;
	}

	public static String getHexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		return hex;
	}

	public static String getHexString(byte[] b) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			builder.append(hex.toUpperCase() + " ");
		}
		return builder.toString();
	}

	public static final void checkBoundsofArray(int arrayLength, int offset,
			int count) {
		if ((offset | count) < 0) {
			throw new IndexOutOfBoundsException("length=" + arrayLength
					+ "; regionStart=" + offset + "; regionLength=" + count);
		}

		if (offset > arrayLength) {
			throw new IndexOutOfBoundsException("length=" + arrayLength
					+ "; regionStart=" + offset + "; regionLength=" + count);
		}

		if (arrayLength - offset >= count) {
			return;
		}

		throw new IndexOutOfBoundsException("length=" + arrayLength
				+ "; regionStart=" + offset + "; regionLength=" + count);
	}

	public static Object getResponse(String method) {
		LinkedHashMap<String, String> params = null;
		return getResponse(params, method);
	}

	public static Object getResponse(List<PropertyInfo> params, String method) {
		SoapObject rpc = new SoapObject(Constants.WEB_SERVICE_NAMESPACE, method);
		LogUtil.i("Grace", "method = "+method);
		if (params != null && params.size() > 0) {
			for (PropertyInfo info : params) {
				rpc.addProperty(info);
				LogUtil.i("Grace", "add para name =" + info.name + " value = "
						+ info.getValue() + " type = " + info.type);
			}
		}

		HttpTransportSE ht = new HttpTransportSE(Utilities.getWebServieUrl());
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		// 一定加这句，不然会报错
		new MarshalBase64().register(envelope);

		try {
			ht.call(null, envelope);
			return envelope.bodyIn;

		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static Object getResponse(LinkedHashMap<String, String> params,
			String method) {
		SoapObject rpc = new SoapObject(Constants.WEB_SERVICE_NAMESPACE, method);

		if (params != null) {
			Iterator<Map.Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				rpc.addProperty(entry.getKey(), entry.getValue());
				LogUtil.i("Grace", "key = " + entry.getKey() + " value = "
						+ entry.getValue());

			}
		}

		HttpTransportSE ht = new HttpTransportSE(Utilities.getWebServieUrl());
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		try {
			ht.call(null, envelope);
			return envelope.bodyIn;

		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static Object getResponseNotCatchException(String method)
			throws XmlPullParserException, IOException {
		LinkedHashMap<String, String> params = null;
		return getResponseNotCatchException(params, method);
	}

	public static Object getResponseWithNoCatch(List<PropertyInfo> params,
			String method) throws XmlPullParserException, IOException {
		SoapObject rpc = new SoapObject(Constants.WEB_SERVICE_NAMESPACE, method);
		LogUtil.i("Grace", "method = "+method);
		if (params != null && params.size() > 0) {
			for (PropertyInfo info : params) {
				rpc.addProperty(info);
				LogUtil.i("Grace", "add para name =" + info.name + " value = "
						+ info.getValue() + " type = " + info.type);
			}
		}

		HttpTransportSE ht = new HttpTransportSE(Utilities.getWebServieUrl());
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		// 一定加这句，不然会报错
		new MarshalBase64().register(envelope);

		ht.call(null, envelope);
		return envelope.bodyIn;

	}

	public static Object getResponseNotCatchException(
			List<PropertyInfo> params, String method)
			throws HttpResponseException, IOException, XmlPullParserException {
		SoapObject rpc = new SoapObject(Constants.WEB_SERVICE_NAMESPACE, method);
		LogUtil.i("Grace", "method = "+method);
		if (params != null && params.size() > 0) {
			for (PropertyInfo info : params) {
				rpc.addProperty(info);
				LogUtil.i("Grace", "add para name =" + info.name + " value = "
						+ info.getValue() + " type = " + info.type);
			}
		}

		HttpTransportSE ht = new HttpTransportSE(Utilities.getWebServieUrl());
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		// 一定加这句，不然会报错
		new MarshalBase64().register(envelope);

		ht.call(null, envelope);
		return envelope.bodyIn;
	}

	public static Object getResponseNotCatchException(
			LinkedHashMap<String, String> params, String method)
			throws HttpResponseException, IOException, XmlPullParserException {
		SoapObject rpc = new SoapObject(Constants.WEB_SERVICE_NAMESPACE, method);
		LogUtil.i("Grace", "method = "+method);
		if (params != null) {
			Iterator<Map.Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				rpc.addProperty(entry.getKey(), entry.getValue());
				LogUtil.i("Grace", "key = " + entry.getKey() + " value = "
						+ entry.getValue());

			}
		}

		HttpTransportSE ht = new HttpTransportSE(Utilities.getWebServieUrl());
		ht.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		ht.call(null, envelope);
		return envelope.bodyIn;
	}

	public static PropertyInfo getPropertyInfo(String name, String value) {
		return getPropertyInfo(name, value, true);
	}

	public static PropertyInfo getPropertyInfo(String name, int value) {
		PropertyInfo pInfo = new PropertyInfo();
		pInfo.setName(name);
		pInfo.setType(PropertyInfo.INTEGER_CLASS);
		pInfo.setValue(value);
		return pInfo;
	}

	public static PropertyInfo getPropertyInfo(String name, String value,
			boolean tranByStr) {
		PropertyInfo pInfo = new PropertyInfo();

		pInfo.setName(name);
		if (tranByStr) {
			pInfo.setType(PropertyInfo.STRING_CLASS);
			pInfo.setValue(value);
		} else {
			pInfo.setType(MarshalBase64.BYTE_ARRAY_CLASS);
			try {
				pInfo.setValue(value.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return pInfo;
	}
	
	public static PropertyInfo getPropertyInfo(String name,byte[] bytes){
		PropertyInfo pInfo = new PropertyInfo();
		pInfo.setName(name);
		pInfo.setType(MarshalBase64.BYTE_ARRAY_CLASS);
		pInfo.setValue(bytes);
		return pInfo;
	}

	// map转换为json字符串
	public static String hashMapToJson(HashMap map) {
		String string = "{";
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			string += "'" + e.getKey() + "':";
			string += "'" + e.getValue() + "',";
		}
		string = string.substring(0, string.lastIndexOf(","));
		string += "}";
		return string;
	}
	
	public static String getWebServieUrl(){
		SharedPreferences preferences = UploadApp.getInstance().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
		String ip = preferences.getString(Constants.KEY_IP, Constants.DEFAULT_IP_ADDRESS);
		String port = preferences.getString(Constants.KEY_PORT, Constants.DEFAULT_PORT);
		String url = "http://"+ip+":"+port+"/getLocation.ws";
		LogUtil.i("Grace", "url = "+url);
		return url;
	}


	public static String getCurTime(){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 String nowTime=format.format(new Date());
		 return nowTime;
	}
	
	public static String formatTime(long curTime){
		String measurementTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", curTime)
				.toString();
		return measurementTime;
	}
}
