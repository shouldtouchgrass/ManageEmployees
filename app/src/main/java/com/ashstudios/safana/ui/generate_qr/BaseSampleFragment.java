package com.ashstudios.safana.ui.generate_qr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;

import androidx.fragment.app.Fragment;

import com.ashstudios.safana.R;

public abstract class BaseSampleFragment extends Fragment {
    private static int MENU_LAST_ID = Menu.FIRST; // Always set to last unused id
    public static final String TAG = "osmBaseFrag";

    AlertDialog gotoLocationDialog = null;

    public abstract String getSampleTitle();

    // ===========================================================
    // Fields
    // ===========================================================

    protected MapView mMapView;

    public MapView getmMapView() {

        return mMapView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMapView = new MapView(inflater.getContext());
        mMapView.getController().setZoom(15.5);
        GeoPoint startPoint = new GeoPoint(10.7667518, 106.6951052);
        mMapView.getController().setCenter(startPoint);
        String myUserAgent = "ManageEmployee";
        Configuration.getInstance().setUserAgentValue(myUserAgent);
        mMapView.setOnGenericMotionListener(new View.OnGenericMotionListener() {

            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_SCROLL:
                            if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                                mMapView.getController().zoomOut();
                            else {
                                mMapView.getController().zoomIn();
                            }
                            return true;
                    }
                }
                return false;
            }
        });
        Log.d(TAG, "onCreateView");
        return mMapView;
    }


    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        if (mMapView != null) {
            addOverlays();

            final Context context = this.getActivity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();

            CopyrightOverlay copyrightOverlay = new CopyrightOverlay(getActivity());
            copyrightOverlay.setTextSize(10);

            mMapView.getOverlays().add(copyrightOverlay);
            mMapView.setMultiTouchControls(true);
            mMapView.setTilesScaledToDpi(true);
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDetach");
        if (mMapView != null)
            mMapView.onDetach();
        mMapView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (gotoLocationDialog != null)
            gotoLocationDialog.dismiss();
    }

    int MENU_VERTICAL_REPLICATION = 0;
    int MENU_HORIZTONAL_REPLICATION = 0;
    int MENU_ROTATE_CLOCKWISE = 0;
    int MENU_ROTATE_COUNTER_CLOCKWISE = 0;
    int MENU_SCALE_TILES = 0;
    int MENU_GOTO = 0;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add = menu.add("Run Tests");
        MENU_LAST_ID++;
        MENU_GOTO = MENU_LAST_ID;
        menu.add(0, MENU_GOTO, Menu.NONE, "Go To");
        // Put overlay items first
        try {
            mMapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mMapView);
        } catch (NullPointerException npe) {
            //can happen during CI tests and very rapid fragment switching
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem item = menu.findItem(MENU_VERTICAL_REPLICATION);
            item.setChecked(mMapView.isVerticalMapRepetitionEnabled());
            item = menu.findItem(MENU_HORIZTONAL_REPLICATION);
            item.setChecked(mMapView.isHorizontalMapRepetitionEnabled());

            item = menu.findItem(MENU_SCALE_TILES);
            item.setChecked(mMapView.isTilesScaledToDpi());
            mMapView.getOverlayManager().onPrepareOptionsMenu(menu, MENU_LAST_ID, mMapView);
        } catch (NullPointerException npe) {
            //can happen during CI tests and very rapid fragment switching
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_GOTO) {
            //TODO dialog with lat/lon prompt
            Log.d(TAG, "Selected menu item ID: " + item.getItemId());
            //prompt for input params
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = View.inflate(getActivity(), R.layout.gotolocation, null);

            final EditText lat = (EditText) view.findViewById(R.id.latlonPicker_latitude);
            final EditText lon = (EditText) view.findViewById(R.id.latlonPicker_longitude);
            final Button cancel = (Button) view.findViewById(R.id.latlonPicker_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationDialog.dismiss();
                }
            });

            Button ok = (Button) view.findViewById(R.id.latlonPicker_ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationDialog.dismiss();
                    try {
                        double latd = Double.parseDouble(lat.getText().toString());
                        if (latd < TileSystem.MinLatitude || latd > TileSystem.MaxLatitude)
                            throw new Exception();
                        double lond = Double.parseDouble(lon.getText().toString());
                        if (lond < TileSystem.MinLongitude || lond > TileSystem.MaxLongitude)
                            throw new Exception();
                        GeoPoint pt = new GeoPoint(latd, lond);
                        mMapView.getController().animateTo(pt);
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setView(view);
            builder.setCancelable(true);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    gotoLocationDialog.dismiss();
                }
            });
            gotoLocationDialog = builder.create();
            gotoLocationDialog.show();

        } else if (mMapView.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, mMapView)) {
            return true;
        }
        return false;
    }

    /**
     * An appropriate place to override and add overlays.
     */
    protected void addOverlays() {


    }

}