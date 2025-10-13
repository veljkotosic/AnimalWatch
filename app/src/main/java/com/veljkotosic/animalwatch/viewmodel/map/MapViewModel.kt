package com.veljkotosic.animalwatch.viewmodel.map

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoFireUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.maps.android.compose.CameraPositionState
import com.veljkotosic.animalwatch.data.auth.repository.AuthRepository
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarker
import com.veljkotosic.animalwatch.data.marker.entity.WatchMarkerSeverity
import com.veljkotosic.animalwatch.data.marker.repository.WatchMarkerRepository
import com.veljkotosic.animalwatch.data.storage.StorageRepository
import com.veljkotosic.animalwatch.data.user.entity.User
import com.veljkotosic.animalwatch.data.user.repository.UserRepository
import com.veljkotosic.animalwatch.uistate.map.MapUiState
import com.veljkotosic.animalwatch.uistate.map.WatchMarkerFilterUiState
import com.veljkotosic.animalwatch.uistate.map.WatchMarkerUiState
import com.veljkotosic.animalwatch.uistate.processing.ProcessingUiState
import com.veljkotosic.animalwatch.utility.addDays
import com.veljkotosic.animalwatch.utility.toGeoLocation
import com.veljkotosic.animalwatch.utility.toGeoPoint
import com.veljkotosic.animalwatch.utility.toLatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel (
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val markerRepository: WatchMarkerRepository,
    private val storageRepository: StorageRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {
    private val _filterUiState = MutableStateFlow(WatchMarkerFilterUiState())
    val filterUiState: StateFlow<WatchMarkerFilterUiState> = _filterUiState.asStateFlow()

    private val _processingUiState = MutableStateFlow(ProcessingUiState())
    val processingUiState: StateFlow<ProcessingUiState> = _processingUiState.asStateFlow()

    private val _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState: StateFlow<MapUiState> = _mapUiState.asStateFlow()

    private val _newMarkerUiState = MutableStateFlow(WatchMarkerUiState())
    val newMarkerUiState: StateFlow<WatchMarkerUiState> = _newMarkerUiState.asStateFlow()

    private val _updateMarkerUiState = MutableStateFlow(WatchMarkerUiState())
    val updateMarkerUiState: StateFlow<WatchMarkerUiState> = _updateMarkerUiState.asStateFlow()

    private val _loadedMarkers = MutableStateFlow<List<WatchMarker>>(emptyList())
    val loadedMarkers: StateFlow<List<WatchMarker>> = _loadedMarkers.asStateFlow()

    private val _filteredMarkers = MutableStateFlow<List<WatchMarker>>(emptyList())
    val filteredMarkers: StateFlow<List<WatchMarker>> = _filteredMarkers.asStateFlow()

    private val _selectedMarker = MutableStateFlow<WatchMarker?>(null)
    val selectedMarker: StateFlow<WatchMarker?> = _selectedMarker.asStateFlow()

    private val _baseMarker = MutableStateFlow<WatchMarker?>(null)
    val baseMarker: StateFlow<WatchMarker?> = _baseMarker.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    fun onNewMarkerTitleChanged(newTitle: String) {
        _newMarkerUiState.update { it.copy(title = newTitle) }
    }

    fun onNewMarkerDescriptionChanged(newDescription: String) {
        _newMarkerUiState.update { it.copy(description = newDescription) }
    }

    fun onNewMarkerSeverityChanged(newSeverity: WatchMarkerSeverity) {
        _newMarkerUiState.update { it.copy(severity = newSeverity) }
    }

    fun onNewMarkerImageUriChanged(newImageUri: Uri?) {
        _newMarkerUiState.update { it.copy(imageUri = newImageUri) }
    }

    fun onNewMarkerTagAdded(tag: String) {
        if (!_newMarkerUiState.value.tags.contains(tag)) {
            _newMarkerUiState.update { it.copy(tags = it.tags + tag) }
        }
    }

    fun onNewMarkerTagRemoved(tag: String) {
        _newMarkerUiState.update { it.copy(tags = it.tags - tag) }
    }

    fun onUpdateMarkerTitleChanged(newTitle: String) {
        _updateMarkerUiState.update { it.copy(title = newTitle) }
    }

    fun onUpdateMarkerDescriptionChanged(newDescription: String) {
        _updateMarkerUiState.update { it.copy(description = newDescription) }
    }

    fun onUpdateMarkerSeverityChanged(newSeverity: WatchMarkerSeverity) {
        _updateMarkerUiState.update { it.copy(severity = newSeverity) }
    }

    fun onUpdateMarkerImageUriChanged(newImageUri: Uri?) {
        _updateMarkerUiState.update { it.copy(imageUri = newImageUri) }
    }

    fun onUpdateMarkerTagAdded(tag: String) {
        if (!_updateMarkerUiState.value.tags.contains(tag)) {
            _updateMarkerUiState.update { it.copy(tags = it.tags + tag) }
        }
    }

    fun onUpdateMarkerTagRemoved(tag: String) {
        _updateMarkerUiState.update { it.copy(tags = it.tags - tag) }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getCurrentLocation() {
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    _currentLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
    }

    fun moveCameraToMarker(marker: WatchMarker, cameraPositionState: CameraPositionState) {
        CoroutineScope(Dispatchers.Main).launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(marker.position.toLatLng(), 18f),
                durationMs = 1000,
            )
        }
    }

    fun selectMarker(newMarker: WatchMarker) {
        _selectedMarker.value = newMarker
    }

    fun deselectMarker() {
        _selectedMarker.value = null
    }

    fun openMarkerCreator() {
        _mapUiState.update { it.copy(markerCreatorOpen = true) }
    }

    fun closeMarkerCreator() {
        _mapUiState.update { it.copy(markerCreatorOpen = false) }
    }

    fun openMarkerUpdater(baseMarker: WatchMarker) {
        _updateMarkerUiState.update { it.copy(title = baseMarker.title, severity = baseMarker.severity) }
        setBaseMarker(baseMarker)
    }

    fun closeMarkerUpdater() {
        resetBaseMarker()
    }

    fun promptMarkerDelete() {
        _mapUiState.update { it.copy(confirmDeleteAlertOpen = true) }
    }

    fun closeMarkerDeletePrompt() {
        _mapUiState.update { it.copy(confirmDeleteAlertOpen = false) }
    }

    fun onCameraRequested() {
        _newMarkerUiState.update { it.copy(userRequestedCamera = true) }
    }

    fun resetCameraRequest() {
        _newMarkerUiState.update { it.copy(userRequestedCamera = false) }
    }

    fun clearError() {
        _processingUiState.update { it.copy(errorMessage = null) }
    }

    fun resetNewMarkerUiState() {
        _newMarkerUiState.value = WatchMarkerUiState()
    }

    fun resetUpdateMarkerUiState() {
        val title = _updateMarkerUiState.value.title
        _updateMarkerUiState.value = WatchMarkerUiState()
        _updateMarkerUiState.update { it.copy(title = title) }
    }

    fun setBaseMarker(marker: WatchMarker) {
        _baseMarker.value = marker
    }

    fun resetBaseMarker() {
        _baseMarker.value = null
    }

    fun clearFilters() {
        _filterUiState.value = WatchMarkerFilterUiState()
        _filteredMarkers.value = _loadedMarkers.value
    }

    fun addSeverityFilter(severity: WatchMarkerSeverity) {
        _filterUiState.update { it.copy(severities = it.severities + severity) }
    }

    fun removeSeverityFilter(severity: WatchMarkerSeverity) {
        _filterUiState.update { it.copy(severities = it.severities - severity) }
    }

    fun changeOwnerFilter(owner: String) {
        _filterUiState.update { it.copy(owner = owner) }
    }

    fun addTagFilter(tag: String) {
        if (!_filterUiState.value.tags.contains(tag)) {
            _filterUiState.update { it.copy(tags = it.tags + tag) }
        }
    }

    fun removeTagFilter(tag: String) {
        _filterUiState.update { it.copy(tags = it.tags - tag) }
    }

    fun changeCreatedAfterFilter(timestamp: Timestamp) {
        _filterUiState.update { it.copy(createdAfter = timestamp) }
    }

    fun changeCreatedBeforeFilter(timestamp: Timestamp) {
        _filterUiState.update { it.copy(createdBefore = timestamp) }
    }

    fun resetDistanceParameter() {
        _mapUiState.update { it.copy(searchRadiusMeters = 5000.0) }
    }

    fun onDistanceParameterChanged(newDistance: Double) {
        if (newDistance > 0) {
            _mapUiState.update { it.copy(searchRadiusMeters = newDistance) }
        }
    }

    fun openFilters() {
        _mapUiState.update { it.copy(filtersOpen = true) }
    }

    fun closeFilters() {
        _mapUiState.update { it.copy(filtersOpen = false) }
    }

    fun openMarkerTable() {
        _mapUiState.update { it.copy(markerTableOpen = true) }
    }

    fun closeMarkerTable() {
        _mapUiState.update { it.copy(markerTableOpen = false) }
    }

    fun applyFilters() {
        val filter = _filterUiState.value
        _filteredMarkers.value = _loadedMarkers.value.filter { marker ->
            (filter.severities.isEmpty() || filter.severities.contains(marker.severity)) &&
            (filter.owner.isBlank() || filter.owner == marker.ownerUserName) &&
            (filter.tags.isEmpty() || marker.tags.containsAll(filter.tags)) &&
            (marker.createdOn >= filter.createdAfter) &&
            (marker.createdOn <= filter.createdBefore)
        }
    }

    private fun isNewMarkerDataValid(): Boolean {
        if (_newMarkerUiState.value.title.isBlank()) {
            _processingUiState.update { it.copy(errorMessage = "Title cannot be empty.") }
            return false
        }
        if (_newMarkerUiState.value.description.isBlank()) {
            _processingUiState.update { it.copy(errorMessage = "Description cannot be empty.") }
            return false
        }
        if (_newMarkerUiState.value.severity == WatchMarkerSeverity.Undefined) {
            _processingUiState.update { it.copy(errorMessage = "Please select a marker severity.") }
            return false
        }
        if (_newMarkerUiState.value.imageUri == null) {
            _processingUiState.update { it.copy(errorMessage = "Please take a picture.") }
            return false
        }

        return true
    }

    private fun isUpdateMarkerDataValid(): Boolean {
        if (_updateMarkerUiState.value.title.isBlank()) {
            _processingUiState.update { it.copy(errorMessage = "Title cannot be empty.") }
            return false
        }
        if (_updateMarkerUiState.value.description.isBlank()) {
            _processingUiState.update { it.copy(errorMessage = "Description cannot be empty.") }
            return false
        }
        if (_updateMarkerUiState.value.severity == WatchMarkerSeverity.Undefined) {
            _processingUiState.update { it.copy(errorMessage = "Please select a marker severity.") }
            return false
        }
        if (_updateMarkerUiState.value.imageUri == null) {
            _processingUiState.update { it.copy(errorMessage = "Please take a picture.") }
            return false
        }

        return true
    }

    fun createMarker(context: Context, cameraPositionState: CameraPositionState) = viewModelScope.launch {
        if (isNewMarkerDataValid()) {
            clearError()
            _processingUiState.update { it.copy(isLoading = true) }
            try {
                val mostRecentLocation = _currentLocation.value ?: throw Exception("Location unavailable")

                val newMarker = WatchMarker(
                    ownerId = authRepository.getCurrentUserId()!!,
                    ownerUserName = _userState.value?.displayName ?: "",
                    title = _newMarkerUiState.value.title,
                    description = _newMarkerUiState.value.description,
                    tags = _newMarkerUiState.value.tags,
                    severity = _newMarkerUiState.value.severity,
                    position = mostRecentLocation.toGeoPoint()
                )
                val imageUrl = storageRepository.uploadMarkerImage(
                    newMarker.id,
                    _newMarkerUiState.value.imageUri!!,
                    context
                )

                val newMarkerWithImage = newMarker.copy(
                    imageUri = imageUrl,
                    expiresOn = newMarker.createdOn.addDays(7),
                    positionHash = GeoFireUtils.getGeoHashForLocation(newMarker.position.toGeoLocation())
                )

                markerRepository.createMarker(newMarkerWithImage)

                _loadedMarkers.update { it + newMarkerWithImage }

                moveCameraToMarker(newMarker, cameraPositionState)

                _processingUiState.update { it.copy(isSuccess = true) }
                closeMarkerCreator()
                resetNewMarkerUiState()
            } catch (e: Exception) {
                _processingUiState.update { it.copy(isSuccess = false, errorMessage = e.message) }
            } finally {
                _processingUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun removeMarker() = viewModelScope.launch {
        if (_selectedMarker.value != null) {
            try {
                _processingUiState.update { it.copy(isLoading = true) }

                markerRepository.removeMarker(_selectedMarker.value!!)
                _loadedMarkers.update { it - _selectedMarker.value!! }
                deselectMarker()

                _processingUiState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _processingUiState.update { it.copy(isSuccess = false, errorMessage = e.message) }
            } finally {
                _processingUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun editMarker() = viewModelScope.launch {

    }

    fun updateMarker(context: Context, oldMarker: WatchMarker) = viewModelScope.launch {
        if (isUpdateMarkerDataValid()) {
            _processingUiState.update { it.copy(isLoading = true) }
            try {

                val updatedMarker = WatchMarker(
                    ownerId = authRepository.getCurrentUserId()!!,
                    ownerUserName = _userState.value?.displayName ?: "",
                    title = oldMarker.title,
                    description = _updateMarkerUiState.value.description,
                    tags = _updateMarkerUiState.value.tags,
                    severity = _updateMarkerUiState.value.severity,
                    position = oldMarker.position,
                    positionHash = oldMarker.positionHash,
                    positionInThread = oldMarker.positionInThread + 1,
                    baseMarkerId = oldMarker.id,
                )

                val imageUrl = storageRepository.uploadMarkerImage(
                    updatedMarker.id,
                    _updateMarkerUiState.value.imageUri!!,
                    context
                )

                val updatedMarkerWithImage = updatedMarker.copy(
                    imageUri = imageUrl,
                    expiresOn = updatedMarker.createdOn.addDays(7),
                )

                markerRepository.updateMarker(updatedMarkerWithImage, oldMarker)

                _loadedMarkers.update { it - oldMarker + updatedMarkerWithImage }
                _processingUiState.update { it.copy(isSuccess = true) }

                closeMarkerUpdater()
                resetUpdateMarkerUiState()
            } catch (e: Exception) {
                _processingUiState.update { it.copy(isSuccess = false, errorMessage = e.message) }
            } finally {
                _processingUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun getMarkerLocationsInArea(center: LatLng) {
        markerRepository.getMarkerLocationsInArea(
            center.latitude,
            center.longitude,
            _mapUiState.value.searchRadiusMeters
        ) {
            _loadedMarkers.value = it
            applyFilters()
        }
    }

    fun getBaseMarker(baseMarkerId: String) = viewModelScope.launch {
        try {
            _processingUiState.update { it.copy(isLoading = true) }

            var loaded = _loadedMarkers.value.find { it.id == baseMarkerId }
            if (loaded == null) {
                loaded = markerRepository.getMarker(baseMarkerId)
                _loadedMarkers.update { it + loaded }
            }
            deselectMarker()
            selectMarker(loaded)

            _processingUiState.update { it.copy(isSuccess = true) }
        } catch (e: Exception) {
            _processingUiState.update { it.copy(isSuccess = false, errorMessage = e.message) }
        } finally {
            _processingUiState.update { it.copy(isLoading = false) }
        }
    }

    fun ownsMarker() : Boolean {
        return _selectedMarker.value?.ownerId == _userState.value?.uid
    }

    private fun loadUser() = viewModelScope.launch {
        val uid = authRepository.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                _userState.value = userRepository.getUser(uid)

                _currentLocation.value = _userState.value?.lastKnownLocation?.toLatLng()
                _mapUiState.update { it.copy(userLoaded = true) }
            }
        }
    }

    fun saveUserLastLocation() = viewModelScope.launch {
        val mostRecentLocation = _currentLocation.value

        if (mostRecentLocation != null) {
            userRepository.updateLastKnownLocation(
                _userState.value!!.uid,
                mostRecentLocation.latitude,
                mostRecentLocation.longitude
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            Log.i("Location", "Location updated")
            p0.lastLocation?.let { location ->
                _currentLocation.update { LatLng(location.latitude, location.longitude) }
                Log.i("Location", "New location ${location.latitude},${location.longitude}")
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000).build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun startObservingMarkers(center: LatLng) {
        markerRepository.observeMarkersInArea(center.latitude, center.longitude, _mapUiState.value.searchRadiusMeters) {
            _loadedMarkers.value = it
            applyFilters()
        }
    }

    private fun stopObservingMarkers() {
        markerRepository.stopObservingMarkers()
    }

    init {
        loadUser()

        waitForLocation()
    }

    private fun waitForLocation() {
        viewModelScope.launch {
            val initialLocation = _currentLocation.filterNotNull().first()

            startObservingMarkers(initialLocation)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopObservingMarkers()
    }
}