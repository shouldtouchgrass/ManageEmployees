package com.ashstudios.safana.ui.scane_qr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.WebviewActivity;
import com.ashstudios.safana.others.SharedPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeScannerFragment extends Fragment {
    private DecoratedBarcodeView barcodeView;
    private CaptureManager captureManager;
    private static final int CAMERA_PERMISSION_REQUEST = 123;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final String TAG = "QRCodeScannerActivity"; // Add a tag for your logs

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode_scanner, container, false);
        SharedPref sharedPref = new SharedPref(getContext());
        String currentUserId = sharedPref.getEMP_ID();
        String currentUserName = sharedPref.getNAME();
        barcodeView = view.findViewById(R.id.scannerView);
        captureManager = new CaptureManager(getActivity(), barcodeView);
        captureManager.initializeFromIntent(getActivity().getIntent(), savedInstanceState);
        captureManager.decode();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }

        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String qrData = result.getText();
                String decryptedData = customDecrypt(qrData);
                String dateString = decryptedData.replace("http://", "");
                Toast.makeText(getActivity(), dateString, Toast.LENGTH_SHORT).show();
                checkLocationAndPostData(currentUserId, currentUserName, dateString);

                if (decryptedData != null && decryptedData.startsWith("http")) {
                    // Modify the Intent to use "url" as the key
                    //Intent webViewIntent = new Intent(getActivity(), WebviewActivity.class);
                    //webViewIntent.putExtra("url", decryptedData); // Pass the scanned URL as an extra
                    //startActivity(webViewIntent);
                } else {
                    Toast.makeText(getActivity(), "Scanned QR Code: Its not Correct QR Code", Toast.LENGTH_SHORT).show();
                }
                barcodeView.decodeSingle(this);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    return view;
    }

    private String customDecrypt(String encryptedData) {
        String decryptionKey = "AAAWEWEWWEERTYUI";
        char[] encryptedChars = encryptedData.toCharArray();
        char[] keyChars = decryptionKey.toCharArray();
        char[] decryptedChars = new char[encryptedChars.length];

        for (int i = 0; i < encryptedChars.length; i++) {
            decryptedChars[i] = (char) (encryptedChars[i] ^ keyChars[i % keyChars.length]);
        }

        return new String(decryptedChars);
    }

    @Override
    public void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureManager.initializeFromIntent(getActivity().getIntent(), null);
                captureManager.decode();
            } else {
                Toast.makeText(getActivity(), "Camera permission is required to scan QR codes.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void postData(String id, String name, String dateString) {
        try {
            // Định dạng ngày bạn nhận được, giả sử là "yyyy-MM-dd"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate date = LocalDate.parse(dateString, formatter);
            LocalDate today = LocalDate.now();

            // Kiểm tra nếu ngày đã qua
            if (date.isBefore(today)) {
                Toast.makeText(getActivity(), "QR đã hết hạn.", Toast.LENGTH_LONG).show();
                return; // Không tiếp tục lưu dữ liệu
            }

            // Nếu ngày hợp lệ, tiếp tục lưu dữ liệu như bình thường
            DatabaseReference dateRef = database.getReference("userScans").child(dateString);
            String scanId = dateRef.push().getKey();
            LocalTime now = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = now.format(timeFormatter);

            Map<String, Object> scanData = new HashMap<>();
            scanData.put("userId", id);
            scanData.put("userName", name);
            scanData.put("scanTime", formattedTime);
            scanData.put("state", "Present");

            if (scanId != null) {
                dateRef.child(scanId).setValue(scanData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Failed to save data", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        } catch (DateTimeParseException e) {
            Toast.makeText(getActivity(), "Invalid date format", Toast.LENGTH_LONG).show();
        }
    }
    private void checkLocationAndPostData(String id, String name, String date) {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("QR_Location").document("Location").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            GeoPoint geoPoint = document.getGeoPoint("position");
                            if (geoPoint != null) {
                                Location targetLocation = new Location(""); //provider name is unnecessary
                                targetLocation.setLatitude(geoPoint.getLatitude());
                                targetLocation.setLongitude(geoPoint.getLongitude());

                                float distance = location.distanceTo(targetLocation);
                                if (distance <= 100) {
                                    postData(id, name, date);
                                } else {
                                    Toast.makeText(getActivity(), "Bạn không ở trong khu vực cho phép.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Không thể tìm thấy thông tin vị trí.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Lỗi khi truy vấn vị trí: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Không thể lấy vị trí hiện tại.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Lỗi khi lấy vị trí: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

}