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
import java.util.List;

import org.appcelerator.kroll.KrollDict;
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

	private String shuffle(String input) {
		List<Character> characters = new ArrayList<Character>();
		for (char c : input.toCharArray()) {
			characters.add(c);
		}
		StringBuilder output = new StringBuilder(input.length());
		while (characters.size() != 0) {
			int randPicker = (int) (Math.random() * characters.size());
			output.append(characters.remove(randPicker));
		}
		return output.toString();
	}

	@Kroll.method
	public String getEndpoint(String p, String v, boolean randomized) {
		if (providers == null)
			return null;
		String endpoint = null;
		if (providers.has(p)) {
			try {
				String ext = "";
				String subdomains = null;
				JSONObject provider = providers.getJSONObject(p);
				endpoint = provider.getString("url");
				if (provider.has("options")) {
					JSONObject options = provider.getJSONObject("options");
					if (options.has("ext"))
						ext = options.getString("ext");
					if (options.has("subdomains")) {
						subdomains = options.getString("subdomains");
					}
				}
				if (provider.has("variants")) {
					JSONObject variants = provider.getJSONObject("variants");
					Iterator<?> vkeys = variants.keys();
					while (vkeys.hasNext()) {
						String vkey = (String) vkeys.next();
						if (vkey.equals(v)) {
							Object variantO = variants.get(v);
							if (variantO instanceof String) {
								endpoint = endpoint.replace("{variant}",
										(String) variantO);
							}
							if (variantO instanceof JSONObject) {
								JSONObject options = ((JSONObject) variantO)
										.getJSONObject("options");
								String variant = options.getString("variant");
								if (options.has("ext"))
									ext = options.getString("ext");
								endpoint = endpoint.replace("{variant}",
										variant).replace("{ext}", ext);
								if (subdomains != null)
									endpoint = endpoint
											.replace("{s}", shuffle(subdomains)
													.substring(0, 1));
							}

						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return endpoint;
	}

	public KrollDict getXYZfromLatLng(KrollDict position) {
		double lat = 0f;
		double lng = 0f;
		int zoom = 0;
		KrollDict kd = new KrollDict();
		if (position.containsKeyAndNotNull("lat")) {
			lat = position.getDouble("lat");
			kd.put("y",
					(Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1
							/ Math.cos(lat * Math.PI / 180))
							/ Math.PI)
							/ 2 * Math.pow(2, zoom))));
			kd.put("x", (Math.floor((lng + 180) / 360 * Math.pow(2, zoom))));
		}
		if (position.containsKeyAndNotNull("lng")) {
			lng = position.getDouble("lng");
		}
		return kd;
	}
}
