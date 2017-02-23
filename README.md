# Titanium Map Module [![Build Status](https://travis-ci.org/appcelerator-modules/ti.map.svg)](https://travis-ci.org/appcelerator-modules/ti.map)

This is the Map Module for Titanium extended by TileOverlays. 


<img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/watercolor.png" width=400 /> <img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/Screenshot_20170219-112805.png" width=400 /> 
<img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/Screenshot_20170219-114755.png" width=400 /> <img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/osmsea.png" width=400 />


##Usage
### Using of TileOverlays
```javascript
var map = require("ti.map");
var mapView = map.createView();
var weatherOverlay =  map.createTileOverlay({
    tileProvider : "OpenWeatherMap/RainClassic"
    accessToken : ACCESS_TOKEN, // only for MapBox
    opacity:0.7
});
mapView.addTileOverlay(weatherOverlay);
```
### Exploring database of TileProviders
For retreiving all possible variants of TileProviders and variants:
```javascript
var providerList = map.createTileProviderFactory();

providerList.getAllProviderNames(); 
// ["OpenStreetMap","OpenSeaMap","OpenTopoMap","Thunderforest","OpenMapSurfer","Hydda","MapBox","Stamen","Esri","OpenWeatherMap","FreeMapSK","MtbMap","CartoDB","HikeBike","BasemapAT","NASAGIBS","NLS"]

var variants = factory.getAllVariantNamesByProvider("Stamen");  // gives list of all variants
//  ["Toner","TonerBackground","TonerHybrid","TonerLines","TonerLabels","TonerLite","Watercolor","Terrain","TerrainBackground","TopOSMRelief","TopOSMFeatures"]

var variant = factory.getVariant("Stamen","WaterColor");
```

### Getting static tiles
```javascript

Ti.UI.createImageView({
    width : 256,
    height : 256,
    image : map.getTileUrl({
        tileProvider : "Stamen/WaterColor"
        lat : 53.55,
        lng : 10.01,
        zoom : 12
    })
});

```

### Offline tiles

With the [Perl script](http://search.cpan.org/~rotkraut/Geo-OSM-Tiles-0.01/downloadosmtiles.pl) you can download all tiles from a region. This script generates folders and download all. After this you can use [mbutil](https://github.com/mapbox/mbutil/) for converting in mbtiles format. This sqlite format is basic for offline maps. Now you can call:
```javascript
var offlineOverlay =  map.createTileOverlay({
   	mbtiles : Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory,"germany.mbtiles").nativePath,
});
mapView.addOverlay(offlineOverlay);
```

Because the offline Maps work with sqlite database you have to close the connection after map work:

```javascript
offlineOverlay.destroy();
```
This prevent memory leaks!

##Heatmaps

<img src="https://i.stack.imgur.com/FkVco.jpg" width=400 />
```javascript
var heatMap  = map.createHeatmapOverlay({
    points : [
        {"lat" : -37.1886, "lng" : 145.708 } ,
        {"lat" : -37.8361, "lng" : 144.845 } ,
        {"lat" : -38.4034, "lng" : 144.192 } ,
        {"lat" : -38.7597, "lng" : 143.67 } ,
        {"lat" : -36.9672, "lng" : 141.083 }
        ],
    opacity : 0.9,
    gradient : {
        colors : ["#ff0000","#0000ff"],
        startPoints : [0.2,1.0]
    }
});
mapView.addHeatmapOverlay(heatMap);
heatMap.setPoints(/* new data */);
```


