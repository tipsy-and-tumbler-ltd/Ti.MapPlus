/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import com.cocoahero.android.gmaps.addons.mapbox.MapBoxOfflineTileProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

@Kroll.proxy(creatableInModule = MapModule.class, propertyAccessors = { MapModule.PROPERTY_TILE_PROVIDER })
public class TileOverlayProxy extends KrollProxy {
	private TileProvider tileProvider;
	private TileOverlay tileOverlay;
	private String endpoint;
	private TileOverlayOptions opts;
	private float opacity = 1.0f;
	private final String OMW = "http://tile.openweathermap.org/map/{omw}/{z}{x}{y}.png";
	private String omwtype = "cloud";

	public TileOverlayProxy() {
		super();
	}

	// from:
	// http://www.survivingwithandroid.com/2015/03/android-google-map-add-weather-data-tile-2.html
	private TileProvider createTilePovider() {
		TileProvider tileProvider = new UrlTileProvider(256, 256) {
			@Override
			public URL getTileUrl(int x, int y, int zoom) {
				String fUrl = endpoint.replace("{z}", "" + zoom)
						.replace("{x}", "" + x).replace("{y}", "" + y);
				// String.format(endpoint, zoom, x, y);
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

	private void mbtiles(String mbtilesFilename) {
		// Create new TileOverlayOptions instance.
		TileOverlayOptions opts = new TileOverlayOptions();

		// Get a File reference to the MBTiles file.
		File myMBTiles = new File(mbtilesFilename);

		// Create an instance of MapBoxOfflineTileProvider.
		MapBoxOfflineTileProvider provider = new MapBoxOfflineTileProvider(
				myMBTiles);

		// Set the tile provider on the TileOverlayOptions.
		opts.tileProvider(provider);

		// Sometime later when the map view is destroyed, close the provider.
		// This is important to prevent a leak of the backing SQLiteDatabase.
		provider.close();
	}

	public void processOptions() {
		if (hasProperty(MapModule.PROPERTY_TILE_PROVIDER))
			endpoint = (String) getProperty(MapModule.PROPERTY_TILE_PROVIDER);
		if (hasProperty(TiC.PROPERTY_OPACITY))
			opacity = TiConvert.toFloat(getProperty(TiC.PROPERTY_OPACITY));
		if (hasProperty(MapModule.PROPERTY_OMW)) {
			omwtype = TiConvert.toString(getProperty(MapModule.PROPERTY_OMW));
			endpoint = OMW.replace("omw", omwtype);
		}
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
