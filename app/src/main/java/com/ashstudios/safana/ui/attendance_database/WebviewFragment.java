package com.ashstudios.safana.ui.attendance_database;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ashstudios.safana.R;

public class WebviewFragment extends Fragment {

    private WebView webView;
    private static final String PREF_IS_SIGNED_IN = "is_signed_in";
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        webView = view.findViewById(R.id.webView2);
        configureWebView();

        String url = "https://console.firebase.google.com/project/managee-7344f/database/managee-7344f-default-rtdb/data";

        if (requiresGoogleSignIn(url) && !isSignedIn()) {
            signInWithGoogle();
            return view;
        }

        loadUrl(url);
        return view;
    }

    // Check if the URL requires Google Sign-In
    private boolean requiresGoogleSignIn(String url) {
        return url.contains("https://accounts.google.com/");
    }

    // Check if the user is signed in
    private boolean isSignedIn() {
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getBoolean(PREF_IS_SIGNED_IN, false);
    }

    private void configureWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
    }

    // Implement your sign-in logic
    private void signInWithGoogle() {
        // Handle the sign-in process and set the isSignedIn state accordingly.
        // After a successful sign-in, you can load the WebView with the desired URL.
    }

}
