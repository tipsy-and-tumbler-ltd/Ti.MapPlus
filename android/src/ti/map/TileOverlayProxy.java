/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

import com.cocoahero.android.gmaps.addons.mapbox.MapBoxOfflineTileProvider;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

@Kroll.proxy(creatableInModule = MapModule.class, propertyAccessors = { MapModule.PROPERTY_TILE_PROVIDER })
public class TileOverlayProxy extends KrollProxy {

	private TileOverlay tileOverlay;
	private TileOverlayOptions tileOverlayOptions;
	private float opacity = 1.0f;
	public String LCAT = MapModule.LCAT;
	MapBoxOfflineTileProvider mbOfflineTileProvider;

	public TileOverlayProxy() {
		super();
	}

	@Override
	public void handleCreationDict(KrollDict o) {
		super.handleCreationDict(o);
		String endpointOfTileProvider = null;
		String provider = "Thunderforest";
		String variant = "OpenCycleMap";
		if (o.containsKeyAndNotNull(TiC.PROPERTY_OPACITY)) {
			opacity = TiConvert.toFloat(o.getDouble(TiC.PROPERTY_OPACITY));
		}

		if (o.containsKeyAndNotNull(MapModule.PROPERTY_TILE_PROVIDER)) {
			provider = o.getString(MapModule.PROPERTY_TILE_PROVIDER);
		}

		if (o.containsKeyAndNotNull(MapModule.PROPERTY_TILE_VARIANT)) {
			variant = o.getString(MapModule.PROPERTY_TILE_VARIANT);
		}
		TileProviderDatabaseProxy providerList = new TileProviderDatabaseProxy();
		endpointOfTileProvider = providerList.getEndpoint(provider, variant,
				true);
		getTileOverlayOptions(endpointOfTileProvider);
	}

	// from:
	// http://www.survivingwithandroid.com/2015/03/android-google-map-add-weather-data-tile-2.html
	private TileOverlayOptions getTileOverlayOptions(
			final String endpointOfTileProvider) {
		Log.d(LCAT, "TileURL = " + endpointOfTileProvider);
		this.tileOverlayOptions = new TileOverlayOptions();
		TileProvider tileProvider = null;
		if (endpointOfTileProvider.substring(0, 4).equals("http")) {
			tileProvider = new UrlTileProvider(256, 256) {
				@Override
				public URL getTileUrl(int x, int y, int zoom) {
					URL tileUrl = null;
					String fUrl = endpointOfTileProvider
							.replace("{z}", "" + zoom).replace("{x}", "" + x)
							.replace("{y}", "" + y).replace("{s}", "");
					Log.e("LCAT", fUrl);
					try {
						tileUrl = new URL(fUrl);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					return tileUrl;
				}
			};
		} else if (endpointOfTileProvider.substring(0, 4).equals("file")) {
			File mbtilesFile = new File(endpointOfTileProvider.replace(
					"file://", ""));
			if (mbtilesFile.exists()) {
				MapBoxOfflineTileProvider mbOfflineTileProvider = new MapBoxOfflineTileProvider(
						mbtilesFile);
				tileOverlayOptions.tileProvider(mbOfflineTileProvider);
			} else
				Log.e("LCAT", "mb file not found " + mbtilesFile);
		}
		if (tileProvider != null) {
			tileOverlayOptions.tileProvider(tileProvider).transparency(
					1.0f - opacity);
			Log.d(LCAT, tileOverlayOptions.toString());
			return tileOverlayOptions;
		} else {
			Log.e(LCAT, "no tileProvider available");
			return null;
		}
	}

	public void processOptions() {
		return;
		/*
		 * String endpointOfTileProvider = null; String provider =
		 * "Thunderforest"; String variant = "OpenCycleMap"; if
		 * (hasProperty(TiC.PROPERTY_OPACITY)) { opacity =
		 * TiConvert.toFloat(getProperty(TiC.PROPERTY_OPACITY)); }
		 * TileProviderDatabaseProxy providerList = new
		 * TileProviderDatabaseProxy(); if
		 * (hasProperty(MapModule.PROPERTY_TILE_PROVIDER)) { provider = (String)
		 * getProperty(MapModule.PROPERTY_TILE_PROVIDER); }
		 * 
		 * if (hasProperty(MapModule.PROPERTY_TILE_VARIANT)) { variant =
		 * TiConvert .toString(getProperty(MapModule.PROPERTY_TILE_VARIANT)); }
		 * tileOverlayOptions = getTileOverlayOptions(providerList.getEndpoint(
		 * provider, variant, true)); Log.d(LCAT,
		 * tileOverlayOptions.toString());
		 */
	}

	public TileOverlayOptions getOptions() {
		return tileOverlayOptions;
	}

	public TileOverlay getTileOverlay() {
		return tileOverlay;
	}

	@Kroll.method
	public void destroy() {
		if (mbOfflineTileProvider != null) {
			mbOfflineTileProvider.close();
		}
	}

	public void setTileOverlay(TileOverlay tileOverlay) {
		this.tileOverlay = tileOverlay;
	}

}
