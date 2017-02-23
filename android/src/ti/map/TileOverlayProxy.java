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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

		private UrlTileProviderHandler(int w, int h,
				String endpointOfTileProvider) {
			super(w, h);
			this.endpointOfTileProvider = endpointOfTileProvider;
			Log.d(LCAT, "Endpoint: " + endpointOfTileProvider + "X" + w + "  "
					+ h);
		}

		@Override
		public synchronized URL getTileUrl(int x, int y, int zoom) {
			URL tileUrl = null;
			// first the right tile depending on xyz
			String fUrl = endpointOfTileProvider.replace("{z}", "" + zoom)
					.replace("{x}", "" + x).replace("{y}", "" + y);
			Log.d(LCAT, fUrl);
			// loadbalancing:
			if (tileProviderParams.containsKey("subdomains")) {
				// same tile => same subdomain
				Log.d(LCAT, "Loadbalancer started >>>>>>>>>>>>>>>>>>>>>>>>");
				List<String> subdomainlist = Arrays.asList(tileProviderParams
						.getStringArray("subdomains"));
				int ndx = (x + y + zoom) % subdomainlist.size();
				Collections.shuffle(subdomainlist);
				String subdomain = subdomainlist.get(ndx);
				Log.d(LCAT, subdomain);
				fUrl = fUrl.replace("{s}", subdomain);
			}
			Log.d(LCAT, "URL=" + fUrl);
			try {
				tileUrl = new URL(fUrl.replace("{s}", "a"));
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
	private KrollDict tileProviderParams;
	private String mbtiles;

	public TileOverlayProxy() {
		super();
	}

	// http://stackoverflow.com/questions/23806348/blurred-custom-tiles-on-android-maps-v2
	@Override
	public void handleCreationDict(KrollDict o) {
		super.handleCreationDict(o);
		String providerString = null;
		String url;

		if (o.containsKeyAndNotNull(TiC.PROPERTY_URL)) {
			url = TiConvert.toString(o.getDouble(TiC.PROPERTY_URL));
		}
		if (o.containsKeyAndNotNull(TiC.PROPERTY_OPACITY)) {
			opacity = TiConvert.toFloat(o.getDouble(TiC.PROPERTY_OPACITY));
		}
		if (o.containsKeyAndNotNull(TiC.PROPERTY_ZINDEX)) {
			zIndex = o.getInt(TiC.PROPERTY_ZINDEX);
		}
		if (o.containsKeyAndNotNull(MapModule.PROPERTY_MBTILES)) {
			mbtiles = o.getString(MapModule.PROPERTY_MBTILES);
		}
		if (o.containsKeyAndNotNull(MapModule.PROPERTY_TILE_PROVIDER)) {
			providerString = o.getString(MapModule.PROPERTY_TILE_PROVIDER);
			Log.d(LCAT, "with " + providerString + " we get:");
			TileProviderFactoryProxy providerList = new TileProviderFactoryProxy();
			tileProviderParams = providerList.getTileProvider(providerString);
			Log.d(LCAT, tileProviderParams.toString());
		}
		if (providerString == null && mbtiles == null) {
			Log.e(LCAT, "no mbtiles, no tileProvider");
		} else
			initTileOverlayOptions();
	}

	private TileOverlayOptions initTileOverlayOptions() {
		tileOverlayOptions = new TileOverlayOptions();
		TileProvider tileProvider = null;
		if (tileProviderParams.containsKey("endpoint")) {
			String url = tileProviderParams.getString("endpoint");
			Log.d(LCAT, "url=" + url);
			/* Online Tiles */
			tileProvider = new UrlTileProviderHandler(TILE_WIDTH, TILE_HEIGHT,
					url);
			tileProvider = new CanvasTileProvider(tileProvider);

			/* offline maps (MBtiles) */
		} else if (mbtiles != null) {
			File mbtilesFile = new File(mbtiles.replace("file://", ""));
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
