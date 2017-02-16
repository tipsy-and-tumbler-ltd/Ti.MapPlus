/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.net.MalformedURLException;
import java.net.URL;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.view.TiUIView;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import android.app.Activity;

@Kroll.proxy(creatableInModule = MapModule.class, propertyAccessors = { MapModule.PROPERTY_TILE_PROVIDER })
public class TileOverlayProxy extends ViewProxy {
	private TileProvider tileProvider;

	public TileOverlayProxy() {
		super();
	}

	// from:
	// http://www.survivingwithandroid.com/2015/03/android-google-map-add-weather-data-tile-2.html
	private TileProvider createTilePovider(String url) {
		TileProvider tileProvider = new UrlTileProvider(256, 256) {
			@Override
			public URL getTileUrl(int x, int y, int zoom) {
				String fUrl = String.format(url, zoom, x, y);
				;
				URL url = null;
				try {
					url = new URL(fUrl);
				} catch (MalformedURLException mfe) {
					mfe.printStackTrace();
				}

				return url;
			}
		};
		TileOverlayOptions opts = new TileOverlayOptions();
		TileOverlay tileOver = map.addTileOverlay(opts);
		return tileProvider;

	}

}
