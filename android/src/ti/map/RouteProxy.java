/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

import android.os.Message;

import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

@Kroll.proxy(creatableInModule = MapModule.class, propertyAccessors = {
		MapModule.PROPERTY_POINTS, TiC.PROPERTY_COLOR, TiC.PROPERTY_WIDTH })
public class RouteProxy extends KrollProxy {

	private PolylineOptions options;
	private Polyline route;

	private static final int MSG_FIRST_ID = KrollProxy.MSG_LAST_ID + 1;

	private static final int MSG_SET_POINTS = MSG_FIRST_ID + 400;
	private static final int MSG_SET_COLOR = MSG_FIRST_ID + 401;
	private static final int MSG_SET_WIDTH = MSG_FIRST_ID + 402;
	private static final int MSG_SET_PATTERN = MSG_FIRST_ID + 403;

	final String LCAT = MapModule.LCAT;

	public RouteProxy() {
		super();
	}

	@Override
	public boolean handleMessage(Message msg) {
		AsyncResult result = null;
		switch (msg.what) {

		case MSG_SET_POINTS: {
			result = (AsyncResult) msg.obj;
			route.setPoints(processPoints(result.getArg(), true));
			result.setResult(null);
			return true;
		}
		case MSG_SET_PATTERN: {
			result = (AsyncResult) msg.obj;
			Log.d(LCAT, " receive Message");
			try {
				List<PatternItem> pattern = processPattern(result.getArg());
				Log.d(LCAT,
						".-.-.-.-.-.-.-.-.-.-.-.-Polyline should have nice pattern. enjoy it!");
				route.setPattern(pattern);
				Log.d(LCAT,
						".-.-.-.-.-.-.-.-.-.-.-.-Polyline has now nice pattern. enjoy it!");
				result.setResult(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		case MSG_SET_COLOR: {
			result = (AsyncResult) msg.obj;
			route.setColor((Integer) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_SET_WIDTH: {
			result = (AsyncResult) msg.obj;
			route.setWidth((Float) result.getArg());
			result.setResult(null);
			return true;
		}
		default: {
			return super.handleMessage(msg);
		}
		}
	}

	public void processOptions() {
		options = new PolylineOptions();
		options.jointType(JointType.ROUND);

		if (hasProperty(MapModule.PROPERTY_PATTERN)) {
			options.pattern(processPattern(getProperty(MapModule.PROPERTY_PATTERN)));
		}
		if (hasProperty(MapModule.PROPERTY_POINTS)) {
			processPoints(getProperty(MapModule.PROPERTY_POINTS), false);
		}
		if (hasProperty(TiC.PROPERTY_WIDTH)) {
			options.width(TiConvert.toFloat(getProperty(TiC.PROPERTY_WIDTH)));
		}
		if (hasProperty(TiC.PROPERTY_COLOR)) {
			options.color(TiConvert
					.toColor((String) getProperty(TiC.PROPERTY_COLOR)));
		}
	}

	public void addLocation(Object loc, ArrayList<LatLng> locationArray,
			boolean list) {
		if (loc instanceof HashMap) {
			HashMap<String, String> point = (HashMap<String, String>) loc;
			Object latitude = point.get(TiC.PROPERTY_LATITUDE);
			Object longitude = point.get(TiC.PROPERTY_LONGITUDE);
			if (longitude != null && latitude != null) {
				LatLng location = new LatLng(TiConvert.toDouble(latitude),
						TiConvert.toDouble(longitude));
				if (list) {
					locationArray.add(location);
				} else {
					options.add(location);
				}
			}
		}
	}

	public ArrayList<LatLng> processPoints(Object points, boolean list) {
		ArrayList<LatLng> locationArray = new ArrayList<LatLng>();
		// encoded (result from routing API)
		if (points instanceof String) {
			List<LatLng> locationList = PolyUtil.decode((String) points);
			return new ArrayList<LatLng>(locationList);
		}
		// multiple points
		if (points instanceof Object[]) {
			Object[] pointsArray = (Object[]) points;
			for (int i = 0; i < pointsArray.length; i++) {
				Object obj = pointsArray[i];
				addLocation(obj, locationArray, list);
			}
			return locationArray;
		}
		// single point
		addLocation(points, locationArray, list);
		return locationArray;
	}

	public List<PatternItem> processPattern(Object patternProxy) {
		Log.d(LCAT, "processPattern >>>>>>>");
		List<PatternItem> patternItems = null;
		if (patternProxy instanceof PatternItemProxy) {
			patternItems = ((PatternItemProxy) patternProxy).getPatternItems();
			if (patternItems != null) {
				Log.d(LCAT, patternItems.toString());
				return patternItems;
			} else
				Log.e(LCAT, "patternItems was null");
		} else
			Log.e(LCAT,
					"patternItem is not really a patternItem, cannot add to map …");
		return patternItems;
	}

	public PolylineOptions getOptions() {
		return options;
	}

	public void setRoute(Polyline r) {
		route = r;
	}

	public Polyline getRoute() {
		return route;
	}

	@Override
	public void onPropertyChanged(String name, Object value) {
		super.onPropertyChanged(name, value);
		if (route == null) {
			return;
		}

		else if (name.equals(MapModule.PROPERTY_POINTS)) {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_SET_POINTS), value);
		}

		else if (name.equals(MapModule.PROPERTY_PATTERN)) {
			Log.d(LCAT, "sendBlockingMainMessage");
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_SET_PATTERN), value);

		} else if (name.equals(TiC.PROPERTY_COLOR)) {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_SET_COLOR),
					TiConvert.toColor((String) value));
		}

		else if (name.equals(TiC.PROPERTY_WIDTH)) {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_SET_WIDTH),
					TiConvert.toFloat(value));
		}

	}

	@Override
	public boolean hasProperty(String name) {
		return (super.getProperty(name) != null);
	}

}
