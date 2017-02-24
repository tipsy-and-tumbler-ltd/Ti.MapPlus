/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

import android.os.Message;

import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

@Kroll.proxy(creatableInModule = MapModule.class)
public class PatternItemProxy extends KrollProxy {
	private static final int COLOR_BLACK_ARGB = 0xff000000;
	private static final int COLOR_WHITE_ARGB = 0xffffffff;
	private static final int COLOR_GREEN_ARGB = 0xff388E3C;
	private static final int COLOR_PURPLE_ARGB = 0xff81C784;
	private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
	private static final int COLOR_BLUE_ARGB = 0xffF9A825;

	private static final int POLYGON_STROKE_WIDTH_PX = 8;
	private static final int PATTERN_DASH_LENGTH_PX = 20;
	private static final int PATTERN_GAP_LENGTH_PX = 20;
	private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
	private static final PatternItem DOT = new Dot();
	private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
	private float strokeWidth = 5f;
	private float dashLength = 20f;
	private float gapLength = 20f;

	private String patternString = ".";
	private List<PatternItem> patternItems;

	public PatternItemProxy() {
		super();
	}

	public void handleCreateDict(KrollDict opts) {
		if (opts.containsKeyAndNotNull(MapModule.PROPERTY_PATTERN)) {
			patternString = opts.getString(MapModule.PROPERTY_PATTERN);
		}
		if (opts.containsKeyAndNotNull(MapModule.PROPERTY_STROKE_WIDTH)) {
			strokeWidth = TiConvert
					.toFloat(getProperty(MapModule.PROPERTY_STROKE_WIDTH));
		}
		if (opts.containsKeyAndNotNull(MapModule.PROPERTY_DASH_LENGTH)) {
			dashLength = TiConvert
					.toFloat(getProperty(MapModule.PROPERTY_DASH_LENGTH));
		}
		if (opts.containsKeyAndNotNull(MapModule.PROPERTY_GAP_LENGTH)) {
			gapLength = TiConvert
					.toFloat(getProperty(MapModule.PROPERTY_GAP_LENGTH));
		}
		createPattern();
	}

	public void createPattern() {
		final List<PatternItem> patternItems = new ArrayList<PatternItem>();
		for (String item : patternString.split("")) {
			switch (item) {
			case " ":
				patternItems.add(new Gap(gapLength));
				break;
			case ".":
				patternItems.add(new Dot());
				break;
			case "-":
				patternItems.add(new Dash(dashLength));
				break;
			}
		}
	}
}
