/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONException;
import org.json.JSONObject;

import ti.map.Shape.IShape;
import android.graphics.Color;
import android.os.Message;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

@Kroll.proxy(name = "TileProviderDatabase", creatableInModule = MapModule.class)
public class TileProviderDatabaseProxy extends KrollProxy {
	JSONObject providers = null;
	final String LCAT = MapModule.LCAT;

	public TileProviderDatabaseProxy() {
		super();
		final String asset = "assets/TileProvider";
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			InputStream in = classLoader.getResourceAsStream(asset);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			in.close();
			String json = new String(buffer, "UTF-8");
			providers = new JSONObject(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String loadJSONFromAsset(String asset) {
		String json = null;
		try {
			InputStream inStream = TiFileFactory.createTitaniumFile(
					new String[] { asset }, false).getInputStream();
			byte[] buffer = new byte[inStream.available()];
			inStream.read(buffer);
			inStream.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	@Kroll.method
	public Object[] getAllProviders() {
		if (providers == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		Iterator<?> keys = providers.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				if (providers.get(key) instanceof JSONObject) {
					list.add(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list.toArray();
	}

	@Kroll.method
	public String getUrl(String p, String v, boolean randomized) {
		if (providers == null)
			return null;
		try {
			JSONObject provider;

			provider = providers.getJSONObject(p);

			String url = null;
			if (provider.has("url")) {
				url = provider.getString("url");
				if (url.contains("{variant}")) {
					JSONObject variants = provider.getJSONObject("variants");
					if (variants.has(v)) {
						url = url.replace("{variants}", variants.getString(v));
					}
				}
				if (url.contains("{s}")) {
					url = url.replace("{s}", "");
					return url;
				}
			}
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Kroll.method
	public String getVariant(String p, String v) throws JSONException {
		if (providers == null)
			return null;
		JSONObject provider;
		provider = providers.getJSONObject(p);
		if (provider.has("variants")) {
			JSONObject variants = provider.getJSONObject("variants");
			if (variants.has(v)) {
				return variants.getString(v);
			}
		}
		return null;
	}

	@Kroll.method
	public Object[] getVariantsOfProvider(String p) {
		if (providers == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		Iterator<?> pkeys = providers.keys();
		while (pkeys.hasNext()) {
			String pkey = (String) pkeys.next();
			try {
				if (providers.get(pkey) instanceof JSONObject && pkey.equals(p)) {
					if (providers.getJSONObject(pkey).has("variants")) {
						JSONObject variants = providers.getJSONObject(pkey)
								.getJSONObject("variants");
						Iterator<?> vkeys = variants.keys();
						while (vkeys.hasNext()) {
							String vkey = (String) vkeys.next();
							list.add(vkey);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list.toArray();
	}

	@Kroll.method
	public String getEndpoint(String provider, String variant) {
		if (providers == null)
			return null;
		String endpoint = null;
		return endpoint;
	}

}
