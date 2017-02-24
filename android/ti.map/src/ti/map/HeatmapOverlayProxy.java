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

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;

// from: https://github.com/googlemaps/android-maps-utils
import com.google.maps.android.heatmaps.HeatmapTileProvider;

@Kroll.proxy(creatableInModule = MapModule.class)
public class HeatmapOverlayProxy extends KrollProxy {

	private float opacity = 1.0f;
	private int zIndex = 0;
	public String LCAT = MapModule.LCAT;
	HeatmapTileProvider heatmapTileProvider;
	int radius = 0;
	Gradient gradient;
	List<LatLng> pointList = null;

	public HeatmapOverlayProxy() {
		super();
	}

	private void addLocation(Object loc, ArrayList<LatLng> locationArray) {
		if (loc instanceof HashMap) {
			HashMap<String, String> point = (HashMap<String, String>) loc;
			Object latitude = point.get(TiC.PROPERTY_LATITUDE);
			Object longitude = point.get(TiC.PROPERTY_LONGITUDE);
			if (longitude != null && latitude != null) {
				LatLng location = new LatLng(TiConvert.toDouble(latitude),
						TiConvert.toDouble(longitude));
				locationArray.add(location);
			}
		}
	}

	private ArrayList<LatLng> processPoints(Object points) {
		ArrayList<LatLng> locationArray = new ArrayList<LatLng>();
		if (points instanceof Object[]) {
			Object[] pointsArray = (Object[]) points;
			for (int i = 0; i < pointsArray.length; i++) {
				Object obj = pointsArray[i];
				addLocation(obj, locationArray);
			}
			return locationArray;
		}
		addLocation(points, locationArray);
		return locationArray;
	}

	// http://stackoverflow.com/questions/23806348/blurred-custom-tiles-on-android-maps-v2
	@Override
	public void handleCreationDict(KrollDict o) {
		super.handleCreationDict(o);
		if (o.containsKeyAndNotNull(TiC.PROPERTY_OPACITY)) {
			opacity = TiConvert.toFloat(o.getDouble(TiC.PROPERTY_OPACITY));
		}
		if (o.containsKeyAndNotNull(TiC.PROPERTY_ZINDEX)) {
			zIndex = o.getInt(TiC.PROPERTY_ZINDEX);
		}
		if (o.containsKeyAndNotNull(MapModule.PROPERTY_POINTS)) {
			pointList = processPoints(getProperty(MapModule.PROPERTY_POINTS));
		}
	}

	public TileOverlayOptions getOptions() {
		heatmapTileProvider = new HeatmapTileProvider.Builder().data(pointList)
				.opacity(opacity).radius(radius).gradient(gradient).build();
		return new TileOverlayOptions().tileProvider(heatmapTileProvider);
	}
}
