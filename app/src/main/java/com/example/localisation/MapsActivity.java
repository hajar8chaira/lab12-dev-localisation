package com.example.localisation;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity {

    private MapView map;
    private RequestQueue requestQueue;
    private final String showUrl = "http://10.0.2.2/localisation/showPosition.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cette ligne est indispensable pour qu'OpenStreetMap accepte de vous envoyer les images
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue("com.example.localisation");

        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        loadMarkersFromServer();
    }

    private void loadMarkersFromServer() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                showUrl,
                null,
                response -> {
                    try {
                        JSONArray positions = response.getJSONArray("positions");

                        if (positions.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Aucune position en base de donnees", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        IMapController mapController = map.getController();
                        mapController.setZoom(15.0);
                        GeoPoint lastPoint = null;

                        for (int i = 0; i < positions.length(); i++) {
                            JSONObject position = positions.getJSONObject(i);
                            double lat = position.getDouble("latitude");
                            double lon = position.getDouble("longitude");
                            String date = position.getString("date");
                            String imei = position.getString("imei");

                            GeoPoint point = new GeoPoint(lat, lon);
                            lastPoint = point;

                            Marker marker = new Marker(map);
                            marker.setPosition(point);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setTitle("Appareil : " + imei);
                            marker.setSubDescription("Date : " + date);

                            map.getOverlays().add(marker);
                        }

                        if (lastPoint != null) {
                            mapController.setCenter(lastPoint);
                        }

                        map.invalidate();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Erreur lors de l'analyse JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Erreur serveur : " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
