/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import android.app.Activity;

@Kroll.proxy(creatableInModule = MapModule.class, propertyAccessors = { MapModule.PROPERTY_TILE_PROVIDER })
public class TileOverlayProxy extends KrollProxy {
	private TileProvider tileProvider;
	private TileOverlay tileOverlay;
	private String endpoint;
	private TileOverlayOptions opts;
	private float opacity = 1.0f;

	public TileOverlayProxy() {
		super();
	}

	// from:
	// http://www.survivingwithandroid.com/2015/03/android-google-map-add-weather-data-tile-2.html
	private TileProvider createTilePovider() {
		TileProvider tileProvider = new UrlTileProvider(256, 256) {
			@Override
			public URL getTileUrl(int x, int y, int zoom) {
				String fUrl = String.format(endpoint, zoom, x, y);
				URL url = null;
				try {
					url = new URL(fUrl);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return url;
			}
		};
		opts = new TileOverlayOptions();
		opts.tileProvider(this.tileProvider);
		opts.transparency(1 - opacity);

		// TileOverlay tileOverlay = map.addTileOverlay(opts);
		return tileProvider;

	}

	public void processOptions() {
		if (hasProperty(MapModule.PROPERTY_TILE_PROVIDER))
			endpoint = (String) getProperty(MapModule.PROPERTY_TILE_PROVIDER);
		if (hasProperty(TiC.PROPERTY_OPACITY))
			opacity = TiConvert.toFloat(getProperty(TiC.PROPERTY_OPACITY));
		createTilePovider();

	}

	public TileOverlayOptions getOptions() {
		return opts;
	}

	public TileOverlay getTileOverlay() {
		return tileOverlay;
	}

	public void setTileOverlay(TileOverlay o) {

		tileOverlay = o;
	}
	// c.setTileOverlay(map.addTileOverlay(c.getOptions()));

}
