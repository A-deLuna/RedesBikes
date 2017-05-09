package com.example.mapwithmarker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String IP = "192.168.1.66";
    private static final int PORT = 26968;
    private Map<Integer, Marker> markerMap;
    GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        markerMap = new HashMap<Integer, Marker>();
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        this.googleMap = googleMap;
        LatLng sydney = new LatLng(32.656, -115.4086);
        //googleMap.addMarker(new MarkerOptions().position(sydney)
        //        .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(17.5f));
        new MessageTask().execute();

    }
    public void setMarkers(Map<Integer, Pair<Float, Float>> map) {
        Log.d("TONY", "setMarkers");
        for (Map.Entry<Integer, Pair<Float, Float>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            Pair<Float, Float> pair = entry.getValue();
            LatLng l = new LatLng(pair.first, pair.second);

            if(markerMap.containsKey(key)) {
                Marker  m = markerMap.get(key);
                m.setPosition(l);
            }
            else {
                Marker m = googleMap.addMarker(new MarkerOptions().position(l)
                        .title("Bike #" + entry.getKey()));
                markerMap.put(key, m);
            }
        }
    }
    class MessageTask extends AsyncTask<Void, Map<Integer, Pair<Float,Float>>, Void> {
        @Override
        protected Void doInBackground(Void... nothing) {
            Socket socket = null;
            Log.d("TONY", "about to open socket");

            try {
                InetAddress serverAddr = InetAddress.getByName(IP);
                socket = new Socket(serverAddr, PORT);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                out.println("Hola!");
                Log.d("TONY", "about to send message to socket");

                out.flush();
                Log.d("TONY", "about to enter while loop");

                while ( true ) {
                    Log.d("TONY", "reading message with readLine");
                    String serverMessage = in.readLine();

                    Log.d("TONY", serverMessage);
                    if(serverMessage.equals("EOT")) {
                        socket.close();
                        break;
                    }
                    Log.d("TONY", "parsing");
                    Map<Integer, Pair<Float, Float>> map = JsonParser.parse(serverMessage);
                    Log.d("TONY", "publish progress");
                    publishProgress(map);
                }

            }
            catch(Exception e){
                Log.d("TONY", e.getLocalizedMessage());
            }

            return null;
        }
        @Override
        protected void onProgressUpdate(Map<Integer, Pair<Float, Float>>... maps) {
            setMarkers(maps[0]);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("TONY", "ENDED");
        }
    }
}

