package it.unimib.greenway.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import static it.unimib.greenway.util.Constants.URI_STRING_MAPS;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import it.unimib.greenway.R;
import it.unimib.greenway.model.Route;
import it.unimib.greenway.util.ConverterUtil;

public class RoutesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnItemClickListener {
        void onRouteItemClick(Route route);
    }

    private final List<Route> routeList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;
    private final ConverterUtil converterUtil = new ConverterUtil();


    public RoutesRecyclerViewAdapter(List<Route> routeList, Application application,
                                     OnItemClickListener onItemClickListener) {
        this.routeList = routeList;
        this.onItemClickListener = onItemClickListener;
        this.application = application;

    }

    @Override
    public int getItemViewType(int position) {
        if (routeList.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.route_card_item, parent, false);
        return new RoutesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoutesViewHolder) {
            ((RoutesViewHolder) holder).bind(routeList.get(position));

        }
    }


    @Override
    public int getItemCount() {
        if (routeList != null) {
            return routeList.size();
        }
        return 0;
    }

    public class RoutesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView routeDistance;
        private final TextView routeDuration;
        private final Button buttonNavigation;
        private final TextView co2Value;

        public RoutesViewHolder(@NonNull View itemView) {
            super(itemView);
            routeDuration = itemView.findViewById(R.id.time_card_route);
            routeDistance = itemView.findViewById(R.id.distance_value);
            buttonNavigation = itemView.findViewById(R.id.buttonNavigate);
            co2Value = itemView.findViewById(R.id.co2Value);
            itemView.setOnClickListener(this);
        }

        public void bind(Route route) {

            String polyline = "enc:" + route.getPolyline().getEncodedPolyline();
            LatLng start = route.getStart();
            LatLng destination = route.getDestination();

            String uri = URI_STRING_MAPS + start.latitude+ ","+ start.longitude  + "&destination=" +
                    destination.latitude + "," + destination.longitude + "&polyline=" + polyline;

            String distanceString = converterUtil.convertMeter(route.getDistanceMeters()) + "km";

            routeDuration.setText(converterUtil.convertSecond(Integer.valueOf(route.getDuration().substring(0, route.getDuration().length() - 1))));
            routeDistance.setText(distanceString);
            co2Value.setText(String.valueOf(converterUtil.co2Calculator(route)));
            buttonNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse(uri);
                    Context context = v.getContext();
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps"); // Specifica che vuoi aprire l'app Google Maps
                    context.startActivity(mapIntent);
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void clear() {
        routeList.clear();
    }

    public void addAll(List<Route> newList) {
        routeList.addAll(newList);
    }
}
