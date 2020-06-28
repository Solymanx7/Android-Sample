

class MapService(
    var activity: AppCompatActivity,
    var fragment: Fragment,
    var mapId: Int,
    var mapView: View,
    var latitude: Double,
    var longitude: Double,
    var markerText: String) : OnMapReadyCallback, OnCameraMoveStartedListener {
    var locationManager: LocationManager? = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val context = fragment.context
    lateinit var mapFragment: SupportMapFragment
    var ownerMapFragment: MutableLiveData<SupportMapFragment> = MutableLiveData()
    private var mMap: GoogleMap? = null
    lateinit var localView: View
    private val LOCATION_SETTINGS_REQUEST = 9999
    lateinit var marker: String
    var latLng: MutableLiveData<LatLng> = MutableLiveData()
    var localLatitude: MutableLiveData<Double> = MutableLiveData()
    var localLongitude: MutableLiveData<Double> = MutableLiveData()
    var addressString: MutableLiveData<String> = MutableLiveData()

    //Geocoding
    lateinit var geocoder: Geocoder
    lateinit var addresses: List<Address>

    @SuppressLint("CheckResult")
    fun startMapService() {
        mapView.setOnTouchListener { view, motionEvent ->
            Log.d("MapService", "startMapService: hey2")
            mapView.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        //1. Get Location permissions
        //2. Ask to enable GPS
        //3. MapReady CallBack
        val rxPermissions = RxPermissions(fragment)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) // ask single or multiple permission once
            .subscribe { granted: Boolean ->
                if (granted) {
                    Log.d("SOrderPickup", "onCreateView: Accepted")
                    mapFragment = activity.supportFragmentManager.findFragmentById(mapId) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    ownerMapFragment.value = mapFragment
                    askToEnableGPS()
                    //If needed in feature
                    /* val intent = Intent("android.location.GPS_ENABLED_CHANGE")
                    intent.putExtra("enabled", true)
                    activity.sendBroadcast(intent)*/
                }
                else {
                    Toast.makeText(context, "Location access denied !", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap
        googleMap?.setOnCameraMoveStartedListener(this)

        mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        mMap?.uiSettings?.setAllGesturesEnabled(true)

        val myLatLng = LatLng(26.8205528, 30.8024979)
        // Add a marker in Egypt and move the camera.

        marker = "Default Marker in Egypt"
        mMap?.addMarker(MarkerOptions().position(myLatLng).title(marker))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))

        mMap?.setOnMapLongClickListener {
            moveToDeviceMarker(it.latitude, it.longitude)
        }

        //TODO Improve this one
        mMap?.setOnMyLocationButtonClickListener {
            getDeviceLocationV2()
            false
        }
        //mMap?.setOnMapLoadedCallback { getDeviceLocation() }
    }

    private fun askToEnableGPS() {
        val mLocationRequest: LocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000).setFastestInterval(1 * 1000)

        val settingsBuilder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)


        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(activity).checkLocationSettings(settingsBuilder.build())

        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                getDeviceLocationV2()
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        Log.d("SOrderPickup", "locationRequest: Gps is Disabled")
                        val resolvableApiException: ResolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(context as Activity, LOCATION_SETTINGS_REQUEST)
                        getDeviceLocationV2()
                    } catch (e: IntentSender.SendIntentException) {

                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocationV2() {
        if (latitude != 0.0 && longitude != 0.0) {
            moveToDeviceMarker(latitude, longitude)
            latitude = 0.0
            longitude = 0.0
        }
        else {
            locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
        }

    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("SOrderPickup", "onLocationChanged: changed")
            moveToDeviceLocation(location)
            /* var address = addresses[0]
             localView.address_tf.text = address.getAddressLine(0)*/
            locationManager?.removeUpdates(this)


        }

        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun moveToDeviceLocation(location: Location) {
        geocode(latitude = location.latitude, longitude = location.longitude)
        val cam = CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f)
        //mMap?.moveCamera(cam)
        marker = "My " + markerText + " Location"
        mMap?.animateCamera(cam)
        mMap?.clear()
        mMap?.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title(marker))
    }

    fun moveToDeviceMarker(
        latitude: Double,
        longitude: Double) {
        geocode(latitude = latitude, longitude = longitude)
        val cam = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15.0f)
        //mMap?.moveCamera(cam)
        marker = "My " + markerText + " Location"
        mMap?.animateCamera(cam)
        mMap?.clear()
        mMap?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title(marker))
    }

    private fun geocode(
        latitude: Double,
        longitude: Double) {
        latLng.value = LatLng(latitude, longitude)
        localLatitude.value = latitude
        localLongitude.value = longitude
        geocoder = Geocoder(activity, Locale.ENGLISH)
        addresses = geocoder.getFromLocation(latitude, longitude, 5)
        if (addresses.isNotEmpty()) {
            Log.d("SOrderPickup", "geocode: ${addresses[0].getAddressLine(0)}")
            var address = addresses[0]
            Log.d("MapService", "geocode: URL ${address.url}")
            Log.d("SOrderPickup", "geocode:locality ${address.locality}")
            Log.d("SOrderPickup", "geocode:subLocality ${address.subLocality}")
            Log.d("SOrderPickup", "geocode:adminArea ${address.adminArea}")
            Log.d("SOrderPickup", "geocode:subAdminArea ${address.subAdminArea}")
            Log.d("SOrderPickup", "geocode:locale ${address.locale}")
            Log.d("SOrderPickup", "geocode:featureName ${address.featureName}")
            Log.d("SOrderPickup", "geocode:extras ${address.extras}")
            Log.d("SOrderPickup", "geocode:thoroughfare ${address.thoroughfare}")
            addressString.value = "${address.thoroughfare}, ${address.locality}"
        }

    }

    override fun onCameraMoveStarted(p0: Int) {
        //fragment.parentLayout.requestDisallowInterceptTouchEvent(true)
        Log.d("MapService", "onCameraMoveStarted: Hey")
        mapView.parent.requestDisallowInterceptTouchEvent(true)

    }


}