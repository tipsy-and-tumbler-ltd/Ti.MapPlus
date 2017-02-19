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

@Kroll.proxy(creatableInModule = MapModule.class)
public class TileOverlayProxy extends KrollProxy {

	private final class UrlTileProviderHandler extends UrlTileProvider {
		private final String endpointOfTileProvider;

		private UrlTileProviderHandler(int arg0, int arg1,
				String endpointOfTileProvider) {
			super(arg0, arg1);
			this.endpointOfTileProvider = endpointOfTileProvider;
			Log.d(LCAT, "Endoint:\n====================\n"
					+ endpointOfTileProvider);
		}

		@Override
		public synchronized URL getTileUrl(int x, int y, int zoom) {
			URL tileUrl = null;
			String fUrl = endpointOfTileProvider.replace("{z}", "" + zoom)
					.replace("{x}", "" + x).replace("{y}", "" + y);
			try {
				tileUrl = new URL(fUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return tileUrl;
		}
	}

	private TileOverlay tileOverlay;
	private TileOverlayOptions tileOverlayOptions;
	private float opacity = 1.0f;
	private int zIndex = 0;
	public String LCAT = MapModule.LCAT;
	MapBoxOfflineTileProvider mbOfflineTileProvider;
	private static int TILE_WIDTH = 512;
	private static int TILE_HEIGHT = TILE_WIDTH;

	public TileOverlayProxy() {
		super();
	}

	// http://stackoverflow.com/questions/23806348/blurred-custom-tiles-on-android-maps-v2
	@Override
	public void handleCreationDict(KrollDict o) {
		super.handleCreationDict(o);
		String endpointOfTileProvider = null;
		String provider = "Thunderforest";
		String variant = "OpenCycleMap";
		if (o.containsKeyAndNotNull(TiC.PROPERTY_OPACITY)) {
			opacity = TiConvert.toFloat(o.getDouble(TiC.PROPERTY_OPACITY));
		}
		if (o.containsKeyAndNotNull(TiC.PROPERTY_ZINDEX)) {
			zIndex = o.getInt(TiC.PROPERTY_ZINDEX);
		}
		if (o.containsKeyAndNotNull(MapModule.PROPERTY_TILE_PROVIDER)) {
			provider = o.getString(MapModule.PROPERTY_TILE_PROVIDER);
		}
		if (o.containsKeyAndNotNull(MapModule.PROPERTY_TILE_VARIANT)) {
			variant = o.getString(MapModule.PROPERTY_TILE_VARIANT);
		}
		if (o.containsKeyAndNotNull(TiC.PROPERTY_URL)) {
			endpointOfTileProvider = o.getString(TiC.PROPERTY_URL);
		}
		if (endpointOfTileProvider == null) {
			TileProviderDatabaseProxy providerList = new TileProviderDatabaseProxy();
			endpointOfTileProvider = providerList.getEndpoint(provider,
					variant, true);
		}
		getTileOverlayOptions(endpointOfTileProvider);
	}

	// http://www.survivingwithandroid.com/2015/03/android-google-map-add-weather-data-tile-2.html
	private TileOverlayOptions getTileOverlayOptions(
			final String endpointOfTileProvider) {

		tileOverlayOptions = new TileOverlayOptions();
		TileProvider tileProvider = null;
		if (endpointOfTileProvider.substring(0, 4).equals("http")) {
			/* Online Tiles */
			tileProvider = new UrlTileProviderHandler(TILE_WIDTH, TILE_HEIGHT,
					endpointOfTileProvider);
			tileProvider = new CanvasTileProvider(tileProvider);

			/* offline maps (MBtiles) */
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
			tileOverlayOptions.tileProvider(tileProvider)
					.transparency(1.0f - opacity).zIndex(zIndex);
			Log.d(LCAT, tileOverlayOptions.toString());
			return tileOverlayOptions;
		} else {
			Log.e(LCAT, "no tileProvider available");
			return null;
		}
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
