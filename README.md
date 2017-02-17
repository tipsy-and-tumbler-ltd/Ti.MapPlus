# Titanium Map Module [![Build Status](https://travis-ci.org/appcelerator-modules/ti.map.svg)](https://travis-ci.org/appcelerator-modules/ti.map)

This is the Map Module for Titanium. Please use [JIRA](http://jira.appcelerator.org) to report issues or ask our [TiSlack community](http://tislack.org) for help! :rocket:

This is a fork for supporting TileOverlays. 

##Usage
```javascript
var map = require("ti.map");
var mapView = map.createView();
var weatherOverlay =  map.createTileOverlay({
    tileProvider : map.OWM_CLOUDS,
    opacity:0.7
});
mapView.addTileOverlay(weatherOverlay);
```
Other constants are `OWM_PRECIPITATION`, `OWM_RAIN`, `OWM_WIND`, `OWM_PRESSURE`
Additiona you can use every tile url from [leaflet providers](http://leaflet-extras.github.io/leaflet-providers/).

With the [Perl script](http://search.cpan.org/~rotkraut/Geo-OSM-Tiles-0.01/downloadosmtiles.pl) you can download all tiles from a region. This script generates folders and download all. After this you can use [mbutil](https://github.com/mapbox/mbutil/) for converting in mbtiles format. This sqlite format is basic for offline maps.
