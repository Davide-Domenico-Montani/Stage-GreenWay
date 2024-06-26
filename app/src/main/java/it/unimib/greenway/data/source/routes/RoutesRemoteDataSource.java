package it.unimib.greenway.data.source.routes;

import static it.unimib.greenway.BuildConfig.MAPS_API_KEY;
import static it.unimib.greenway.BuildConfig.MAPS_API_KEY;
import static it.unimib.greenway.util.Constants.DRIVE_CONSTANT;
import static it.unimib.greenway.util.Constants.ERROR_RETRIEVING_ROUTES;
import static it.unimib.greenway.util.Constants.FIELDMASK_ROUTE;
import static it.unimib.greenway.util.Constants.TRANSIT_CONSTANT;
import static it.unimib.greenway.util.Constants.WALK_CONSTANT;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import it.unimib.greenway.data.service.RoutesApiService;
import it.unimib.greenway.model.Route;
import it.unimib.greenway.model.RoutesApiResponse;
import it.unimib.greenway.model.RoutesResponse;
import it.unimib.greenway.util.ConverterUtil;
import it.unimib.greenway.util.ServiceLocator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutesRemoteDataSource extends BaseRoutesRemoteDataSource{

    int count;
    private final RoutesApiService routesApiService;
    public RoutesRemoteDataSource() {
        this.routesApiService = ServiceLocator.getInstance().getRoutesApiService();

    }

    @Override
    public void getRoutes(double latStart, double lonStart, double latEnd, double lonEnd, double co2Car) {
        ConverterUtil converterUtil = new ConverterUtil();
            String transport;
            Log.d("ciccio2", "" + co2Car);
            count= 0;
            List<Route> routeList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if(i == 0) {
                transport = DRIVE_CONSTANT;
            }else if(i == 1) {
                transport = TRANSIT_CONSTANT;
            } else {
                transport = WALK_CONSTANT;
            }

            String body = "{\"origin\": {\n" +
                    "   \"location\":{ \n" +
                    "               \"latLng\": {\n" +
                    "                    \"latitude\" :  "+ latStart +",\n" +
                    "                    \"longitude\":  "+ lonStart +"\n" +
                    "                    } \n" +
                    "            } \n" +
                    "  },\n" +
                    "  \"destination\": {\n" +
                    "    \"location\":{ \n" +
                    "               \"latLng\": {\n" +
                    "                    \"latitude\" :  "+latEnd + ",\n" +
                    "                    \"longitude\":  "+ lonEnd+"\n" +
                    "                    } \n" +
                    "            } \n" +
                    "  },\n" +
                    "  \"travelMode\" :\"" + transport + "\",\n" +
                    "  \"computeAlternativeRoutes\": true\n" +
                    "}";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);

            Call<RoutesApiResponse> call = routesApiService.createRoute(requestBody, FIELDMASK_ROUTE, MAPS_API_KEY);
            String finalDrive = transport;

            call.enqueue(new Callback<RoutesApiResponse>() {
                @Override
                public void onResponse(Call<RoutesApiResponse> call, Response<RoutesApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getRoutes() != null){
                            List<Route> routelist = response.body().getRoutes();

                            for(Route route : routelist){
                                route.setTravelMode(finalDrive);
                                route.setStart(new LatLng(latStart, lonStart));
                                route.setDestination(new LatLng(latEnd, lonEnd));

                                String co2 = converterUtil.co2Calculator(route, co2Car);
                                co2= co2.replace(",", ".");
                                route.setCo2(Double.valueOf(co2.substring(0, co2.length()-2)));
                            }
                            routeList.addAll(routelist);
                        }
                       
                        if(count == 2){
                            routesCallBack.onSuccessFromRemote(routeList);
                        }
                        count++;
                    } else {



                        routesCallBack.onFailureFromRemote(ERROR_RETRIEVING_ROUTES);
                    }
                }

                @Override
                public void onFailure(Call<RoutesApiResponse> call, Throwable t) {
                    // handle network errors
                    Log.d("routeProva", "onFailure", t);
                }
            });
        }
    }
}
