/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
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

@Kroll.proxy(name = "Circle", creatableInModule = MapModule.class)
public class TileProviderListProxy extends KrollProxy {
	JSONObject providers;

	public TileProviderListProxy() {
		super();
		try {
			providers = new JSONObject(getClass().getClassLoader()
					.getResource("assets/TileProvider.json").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Kroll.method
	public Object[] getAllProviders() {
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
	public Object[] getVariantsOfProvider(String p) {
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
	public String getEndpoint(String provider, String variant) {
		String endpoint = null;
		return endpoint;
	}

}
