package com.ashstudios.safana.ui.generate_qr;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BookmarkDatastore {
    private FirebaseFirestore db;

    public BookmarkDatastore() {
        db = FirebaseFirestore.getInstance();
    }

    public void getBookmarksAsMarkers(MapView view, Consumer<List<Marker>> onMarkersReady) {
        List<Marker> markers = new ArrayList<>();
        db.collection("QR_Location").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Marker m = new Marker(view);
                    m.setId(document.getId());
                    m.setTitle(document.getString("title"));
                    m.setSubDescription(document.getString("description"));
                    GeoPoint position = document.getGeoPoint("position");
                    if (position != null) {
                        m.setPosition(new org.osmdroid.util.GeoPoint(position.getLatitude(), position.getLongitude()));
                        m.setSnippet(m.getPosition().toDoubleString());
                        markers.add(m);
                    }
                }
                onMarkersReady.accept(markers); // Gửi danh sách markers đã được tải về callback
            } else {
                Log.w("Firestore", "Error getting documents.", task.getException());
            }
        });
    }


    public void addBookmark(Marker bookmark) {
        if (bookmark.getId() == null || bookmark.getId().isEmpty()) {
            bookmark.setId("Location");
        }
        bookmark.setId("Location");
        GeoPoint geoPoint = new GeoPoint(bookmark.getPosition().getLatitude(), bookmark.getPosition().getLongitude());
        Bookmark newBookmark = new Bookmark(bookmark.getId(), geoPoint, bookmark.getTitle(), bookmark.getSubDescription());
        Log.d("Firestore", "Trying to write: " + newBookmark);

        db.collection("QR_Location").document(bookmark.getId())
                .set(newBookmark)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }


    public void removeBookmark(String id) {
        db.collection("QR_Location").document(id).delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting document", e));
    }

    public void removeAllBookmarks(Runnable onCompletion) {
        // Giả sử bạn xóa các bookmarks thông qua Firestore
        db.collection("QR_Location").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    db.collection("QR_Location").document(document.getId()).delete();
                }
                if (onCompletion != null) {
                    onCompletion.run();
                }
            } else {
                Log.w("Firestore", "Error getting documents.", task.getException());
            }
        });
    }

    static class Bookmark {
        private String id;
        private GeoPoint position;
        private String title;
        private String description;

        public Bookmark(String id, GeoPoint position, String title, String description) {
            this.id = id;
            this.position = position;
            this.title = title;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public GeoPoint getPosition() {
            return position;
        }

        public void setPosition(GeoPoint position) {
            this.position = position;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public void close() {
        // Không cần thực hiện gì vì Firebase Firestore quản lý tài nguyên tự động
    }
}
