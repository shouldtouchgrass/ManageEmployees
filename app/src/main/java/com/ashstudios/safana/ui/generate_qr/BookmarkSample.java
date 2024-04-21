package com.ashstudios.safana.ui.generate_qr;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.ashstudios.safana.R;


import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.UUID;

/**
 * created on 2/11/2018.
 * TODO it would be nice to have the ability to select an icon for the location
 *
 * @author Alex O'Ree
 */

public class BookmarkSample extends BaseSampleFragment implements LocationListener {
    public static double zoom ;

    private LocationManager lm;
    private BookmarkDatastore datastore = null;
    private MyLocationNewOverlay mMyLocationOverlay = null;
    private Location currentLocation = null;


    @Override
    public String getSampleTitle() {
        return "Bookmark Sample";
    }


    AlertDialog addBookmark = null;

    @Override
    public void addOverlays() {
        super.addOverlays();
        if (datastore == null)
            datastore = new BookmarkDatastore();
        //add all our bookmarks to the view
        datastore.getBookmarksAsMarkers(mMapView, markers -> {
            // Trong lambda này, 'markers' là danh sách các marker đã sẵn sàng
            mMapView.getOverlayManager().addAll(markers); // Thêm tất cả các marker vào mapView
            mMapView.invalidate(); // Yêu cầu mapView cập nhật để hiển thị các thay đổi
        });
        this.mMyLocationOverlay = new MyLocationNewOverlay(mMapView);
        mMyLocationOverlay.setEnabled(true);


        this.mMapView.getOverlays().add(mMyLocationOverlay);
        //support long press to add a bookmark

        //TODO menu item to
        MapEventsOverlay events = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {


                showDialog(p);
                return true;
            }
        });
        mMapView.getOverlayManager().add(events);

    }

    private void showDialog(GeoPoint p) {
        if (addBookmark != null)
            addBookmark.dismiss();

        //TODO prompt for user input
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = View.inflate(getContext(), R.layout.bookmark_add_dialog, null);
        builder.setView(view);
        final EditText lat = view.findViewById(R.id.bookmark_lat);
        lat.setText(p.getLatitude() + "");
        final EditText lon = view.findViewById(R.id.bookmark_lon);
        lon.setText(p.getLongitude() + "");
        final EditText title = view.findViewById(R.id.bookmark_title);
        final EditText description = view.findViewById(R.id.bookmark_description);

        view.findViewById(R.id.bookmark_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookmark.dismiss();
            }
        });
        view.findViewById(R.id.bookmark_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean valid = true;
                double latD = 0;
                double lonD = 0;
                //basic validate input
                try {
                    latD = Double.parseDouble(lat.getText().toString());
                } catch (Exception ex) {
                    valid = false;
                }
                try {
                    lonD = Double.parseDouble(lon.getText().toString());
                } catch (Exception ex) {
                    valid = false;
                }

                if (!mMapView.getTileSystem().isValidLatitude(latD))
                    valid = false;
                if (!mMapView.getTileSystem().isValidLongitude(lonD))
                    valid = false;

                if (valid) {
                    //Xóa tất cả bookmark hiện tại
                   // datastore.removeAllBookmarks();
                    // Xóa tất cả markers trên bản đồ
                    mMapView.getOverlayManager().clear();
                    mMapView.invalidate();

                    Marker m = new Marker(mMapView);
                    m.setId(UUID.randomUUID().toString());
                    m.setTitle(title.getText().toString());
                    m.setSubDescription(description.getText().toString());

                    m.setPosition(new GeoPoint(latD, lonD));
                    m.setSnippet(m.getPosition().toDoubleString());
                    datastore.removeAllBookmarks(() -> {
                        mMapView.getOverlayManager().add(m);
                        datastore.addBookmark(m);
                        mMapView.invalidate();
                        Toast.makeText(getContext(), "Bookmark added", Toast.LENGTH_SHORT).show();
                    });
                }
                addBookmark.dismiss();
            }
        });

        addBookmark = builder.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            lm.removeUpdates(this);
        } catch (Exception ex) {
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            //this fails on AVD 19s, even with the appcompat check, says no provided named gps is available
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        try {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (datastore != null)
            datastore.close();
        datastore = null;
        if (addBookmark != null)
            addBookmark.dismiss();
        addBookmark = null;
    }


    private static final int MENU_BOOKMARK_MY_LOCATION = Menu.FIRST;

    private static int MENU_LAST_ID = Menu.FIRST;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.add(0, MENU_BOOKMARK_MY_LOCATION, Menu.NONE, "Bookmark Current Location").setCheckable(false);
        MENU_LAST_ID++;
        try {
            mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_BOOKMARK_MY_LOCATION + 1, mMapView);
        } catch (NullPointerException npe) {
            //can happen during CI tests and very rapid fragment switching
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {

            mMapView.getOverlayManager().onPrepareOptionsMenu(menu, MENU_LAST_ID, mMapView);
        } catch (NullPointerException npe) {
            //can happen during CI tests and very rapid fragment switching
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_BOOKMARK_MY_LOCATION) {
            //TODO
            if (currentLocation != null) {
                GeoPoint pt = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMapView.getController().setCenter(pt);
                showDialog(pt);
                return true;
            }


        } else if (mMapView.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, mMapView)) {
            return true;
        }
        return false;
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        //mMyLocationOverlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}