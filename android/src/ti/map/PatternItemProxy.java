/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013-2016 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.util.TiConvert;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.PatternItem;

@Kroll.proxy(creatableInModule = MapModule.class)
public class PatternItemProxy extends KrollProxy {
	private float strokeWidth = 5f;
	private float dashLength = 20f;
	private float gapLength = 20f;
	private int interval = 0;
	private Timer cron;

	private String patternString = ".";

	List<PatternItem> patternItems;

	public PatternItemProxy() {
		super();
	}

	public List<PatternItem> getPattern() {
		return patternItems;
	}

	public void handleCreateDict(KrollDict opts) {
		if (opts.containsKeyAndNotNull(MapModule.PROPERTY_INTERVAL)) {
			interval = opts.getInt(MapModule.PROPERTY_INTERVAL);
		}
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
		patternItems = new ArrayList<PatternItem>();
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
		if (interval > 0) {
			cron.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					Collections.rotate(patternItems, 1);
				}
			}, 0, interval);
		}

	}

	//
}
