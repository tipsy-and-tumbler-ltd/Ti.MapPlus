# Titanium Map Module [![Build Status](https://travis-ci.org/appcelerator-modules/ti.map.svg)](https://travis-ci.org/appcelerator-modules/ti.map)

This is the Map Module for Titanium extended by TileOverlays. 


<img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/watercolor.png" width=400 /> <img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/Screenshot_20170219-112805.png" width=400 /> 
<img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/Screenshot_20170219-114755.png" width=400 /> <img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/osmsea.png" width=400 />


##Usage

### Using of TileOverlays
```javascript
Ti.Map = require("ti.map");
var mapView = Ti.Map.createView();
var weatherOverlay =  Ti.Map.createTileOverlay({
    tileProvider : "OpenWeatherMap/RainClassic"
    accessToken : ACCESS_TOKEN, // only for MapBox
    opacity:0.7
});
mapView.addTileOverlay(weatherOverlay);
```
### Exploring database of TileProviders
For retreiving all possible variants of TileProviders and variants:
```javascript
var providerList = Ti.Map.createTileProviderFactory();

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
    image : Ti.Map.createTileProviderFactory().getTileImage({
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
var offlineOverlay =  Ti.Map.createTileOverlay({
   	mbtiles : Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory,"germany.mbtiles").nativePath,
});
mapView.addOverlay(offlineOverlay);
```

You can use this module for display deep zoom images:

var imageOverlay =  Ti.Map.createTileOverlay({
	url : "https://raw.githubusercontent.com/alfarisi/leaflet-deepzoom/master/example/DeepZoomImage/hubble_files/{z}/{x}_{y}.jpg"
});
 
You can create images with [zoomify](https://www.macupdate.com/app/mac/58319/zoomify/download).
 
Microsofts [DeepzoommImages](https://en.wikipedia.org/wiki/Deep_Zoom) will currently  not supported. 


Because the offline Maps work with sqlite database you have to close the connection after map work:

```javascript
offlineOverlay.destroy();
```
This prevent memory leaks!

##Heatmaps

<img src="https://i.stack.imgur.com/FkVco.jpg" width=400 />
```javascript
var heatMap  = Ti.Map.createHeatmapOverlay({
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

##Using of encoded polylines

The Ti.Map.createRoute() point property accepts now encoded polylines.

```javascript
Ti.Map.createRoute({
	points : "_p~iF~ps|U_ulLnnqC_mqNvxq`@",
	color : "#8f00",
	width: 5
});
```

##Pattern in routes (dotted, dashed …)

<img src="https://raw.githubusercontent.com/AppWerft/ti.map/master/screens/dotted.png" width=400 />
```javascript
var patternItem = Ti.Map.createPatternItem({
	dashLength : 20,
	gapLength :20,
	pattern : "-"  // dashed line
});
mapView.addRoute(Ti.Map.createRoute({
	points : "_p~iF~ps|U_ulLnnqC_mqNvxq`@",
	patternItem : patternItem,
	color : "red",
	jointType : Ti.Map.JOINT_TYPE_BEVEL, // JOINT_TYPE_BEVEL,JOINT_TYPE_ROUND, JOINT_TYPE_DEFAULT,
	with : 5,
}));
mapView.addRoute(Ti.Map.createRoute({
	points : "_pa1e3wf~iF~pstzadasdalLnnqC_mqNvxq`@",
	patternItem : Ti.Map.createPatternItem({
		pattern : "."  // dotted line
	}),
	color : "orange",
	with : 5,
}));

var Route = Ti.Map.createRoute({
	points : "_p~iF~ps|U_ulLnnqC_mqNvxq`@",
	color : "red",
	with : 5,
});
mapView.addRoute(Route);
Route.start({
	type : Ti.Map.TYPE_MARCHING_ANTS
});
// later:
Route.stop();

```
##Custome styles maps
<img src="https://developers.google.com/maps/documentation/android-api/images/style-night.png" width=300/>
You can use the same json as for web. Here is the [wizard to do this](https://mapstyle.withgoogle.com/)

###Usage

```javascript
Ti.Map = require("ti.map");
var mapView = Ti.Map.createView({
	mapStyle : JSONSTRING,
	region: {
	},
	mapType : Ti.Map.MAP_TYPE_NORMAL
});
```

Example for JSONSTRING:
```javascript
[
  {
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#212121"
      }
    ]
  },
  {
    "elementType": "labels.icon",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#212121"
      }
    ]
  },
  {
    "featureType": "administrative",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "administrative.country",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  },
  {
    "featureType": "administrative.land_parcel",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#bdbdbd"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#181818"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#1b1b1b"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry.fill",
    "stylers": [
      {
        "color": "#2c2c2c"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#8a8a8a"
      }
    ]
  },
  {
    "featureType": "road.arterial",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#373737"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#3c3c3c"
      }
    ]
  },
  {
    "featureType": "road.highway.controlled_access",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#4e4e4e"
      }
    ]
  },
  {
    "featureType": "road.local",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "featureType": "transit",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#000000"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#3d3d3d"
      }
    ]
  }
]

```