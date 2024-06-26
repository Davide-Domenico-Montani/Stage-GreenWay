package it.unimib.greenway.ui.main;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unimib.greenway.data.repository.airQuality.IAirQualityRepositoryWithLiveData;
import it.unimib.greenway.model.AirQuality;
import it.unimib.greenway.model.Result;

public class AirQualityViewModel extends ViewModel {

    private static final String TAG = AirQualityViewModel.class.getSimpleName();

    private final IAirQualityRepositoryWithLiveData airQualityRepositoryWithLiveData;
    private MutableLiveData<Result> airQualityListLiveData;


    public AirQualityViewModel(IAirQualityRepositoryWithLiveData airQualityRepositoryWithLiveData) {
        this.airQualityRepositoryWithLiveData = airQualityRepositoryWithLiveData;
    }

    public MutableLiveData<Result> getAirQuality(long lastUpdate, View view) {
        if (airQualityListLiveData == null) {
            fetchAirQuality(lastUpdate, view);
        }
        return airQualityListLiveData;
    }

    public void fetchAirQuality(long lastUpdate, View view) {
        airQualityListLiveData = airQualityRepositoryWithLiveData.fetchAllAirQUality(lastUpdate, view);
    }

    public List<AirQuality> getAirQualityList() throws ExecutionException, InterruptedException {

         return airQualityRepositoryWithLiveData.getAirQualityList();
    }


}
