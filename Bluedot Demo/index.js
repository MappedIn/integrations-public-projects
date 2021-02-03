let mapview, venue;

const options = {
  mapview: {
    antialias: true,
    alpha: true,
    mode: Mappedin.modes.TEST,
    loadOptions: { mode: 'json'}
  },
  venue: {
    clientId: "5e56dce991b055001a68e973",
    clientSecret: "arsKT1tuCJY9Af6ij7ROHtDs7dx1V0HbVCBjf0xiNEuTWeO1",
    perspective: "Website",
    things: {
      venue: ["slug", "name"],
      categories: ["name"],
      maps: ["name", "elevation", "shortName"],
    },
    venue: "mappedin-demo-mall",
  },
};


function initializeMapOptions() {
  mapview.labelAllLocations({smartLabels: true})
  mapview.addInteractivePolygonsForAllLocations()

  //Enable blue dot (allowImplicitFloorLevel as true shows bluedot on all floor levels)
  mapview.blueDotCore.enableBlueDot({allowImplicitFloorLevel: true})

  //Listen for changes on nearestNode
  mapview.blueDotCore.on("nearestNodeChanged", (newNearestNode)=> {

      //Get the directions to a location from the nearest node
      newNearestNode.newValue.directionsTo(venue.locations[5], {directionsProvider: "offline"}, async (err, directions)=> {
          mapview.removeAllPaths()
          mapview.drawPath(directions.path)
      })
  })
}


const div = document.getElementById("mapView");

Mappedin.initialize(options, div).then((data) => {
  mapview = data.mapview;
  venue = data.venue;
  initializeMapOptions()
});