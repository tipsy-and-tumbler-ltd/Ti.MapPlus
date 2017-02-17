# Titanium Map Module [![Build Status](https://travis-ci.org/appcelerator-modules/ti.map.svg)](https://travis-ci.org/appcelerator-modules/ti.map)

This is the Map Module for Titanium. Please use [JIRA](http://jira.appcelerator.org) to report issues or ask our [TiSlack community](http://tislack.org) for help! :rocket:

This is a fork for supporting TileOverlays. 

##Usage
```javascript
var map = require("ti.map");
var mapView = map.createView();
var weatherOverlay =  map.createTileOverlay({
    tileProvider : map.OVERLAY_OWM,
    variant : "clouds"
    opacity:0.7
});
mapView.addTileOverlay(weatherOverlay);
```
Additiona you can use every tile url from [leaflet providers](http://leaflet-extras.github.io/leaflet-providers/).

With the [Perl script](http://search.cpan.org/~rotkraut/Geo-OSM-Tiles-0.01/downloadosmtiles.pl) you can download all tiles from a region. This script generates folders and download all. After this you can use [mbutil](https://github.com/mapbox/mbutil/) for converting in mbtiles format. This sqlite format is basic for offline maps. Now you can call:
```javascript
var offlineOverlay =  map.createTileOverlay({
    tileProvider : Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory,"germany.mbtiles").nativePath,
});
mapView.addOverlay(offlineOverlay);
```

Because the offline Maps work with sqlite database you have to close the connection after map work:

```javascript
offlineOverlay.destroy();
```
This prevent memory leaks!

