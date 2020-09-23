

let mapview, venue;

    const options = {
      mapview: {
        antialias: "AUTO",
        mode: Mappedin.modes.TEST,
        onDataLoaded: initializeMapOptions,
      },
      venue: {
        clientId: "5e2766b0a5fdf5001a6b96a3",
        clientSecret: "twsNkWrmYieNIUewwSZJDrva6IIH9f1KVoXy1bK4jsfchBJT",
        perspective: "Website",
        things: {
          venue: ["slug", "name"],
          categories: ["name"],
          maps: ["name", "elevation", "shortName"],
        },
        venue: "iss-nasa-demo",
      },
    };

    

// make each header link to polygon
    function listAllLocations() {
        const detailsContainer = document.getElementById("location-details");
        detailsContainer.innerHTML = "";

        for ( i = 0 ; i < venue.locations.length; i++ ) {
            const button = document.createElement("button");
            button.setAttribute('data-id', venue.locations[i].id);
            button.onclick = this.selectLocation;
            button.href = "#";
            button.textContent = venue.locations[i].name; 
            detailsContainer.append(button);
        }
    }
    function selectLocation(event) {
        if (event.target) {
            var locationID = event.target.getAttribute("data-id");
            var location = venue.locations.find(o => o.id === locationID);

            mapview.focusOn({polygons: location.polygons});
        }
        
    }
    
    
    function initializeMapOptions() {
      mapview.labelAllLocations({ smartLabels: true });
      mapview.setBackgroundColor(1);
      mapview.addInteractivePolygonsForAllLocations();
      listAllLocations();
    }

    const div = document.getElementById("mapView");

    Mappedin.initialize(options, div).then((data) => {
      mapview = data.mapview;
      venue = data.venue;
      initializeMapOptions();
    });