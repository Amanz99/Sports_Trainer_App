package com.example.spots_enhancing_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DrillsFragment extends Fragment {

    private EditText resultInput;
    private Button submitButton;
    private DatabaseReference databaseReference;

    public DrillsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drills, container, false);

        // Initialize the WebView and load the YouTube video
        WebView webView = view.findViewById(R.id.webView);
        String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Asmzihl4qww\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadData(video, "text/html", "utf-8");

        // Handle back button click to go to home
        TextView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Home page
                Fragment homeFragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("drill_results");

        // Find views
        resultInput = view.findViewById(R.id.result_input);
        submitButton = view.findViewById(R.id.submit_button);

        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResult();
            }
        });

        return view;
    }

    private void submitResult() {
        String result = resultInput.getText().toString().trim();

        if (TextUtils.isEmpty(result)) {
            Toast.makeText(getActivity(), "Please enter a result", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique key for each result
        String key = databaseReference.push().getKey();

        if (key != null) {
            databaseReference.child(key).setValue(result)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Result submitted", Toast.LENGTH_SHORT).show();
                            resultInput.setText(""); // Clear the input field
                        } else {
                            Toast.makeText(getActivity(), "Failed to submit result", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Error generating key", Toast.LENGTH_SHORT).show();
        }
    }
}


