package com.example.spots_enhancing_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Set the user's email
        TextView userDetails = view.findViewById(R.id.user_details);
        if (user != null) {
            userDetails.setText(user.getEmail());
        }

        // Handle logout button click
        Button logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out from Firebase
                FirebaseAuth.getInstance().signOut();
                // Redirect to the login activity
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Find the buttons and set OnClickListeners
        Button tutorialsButton = view.findViewById(R.id.button_tutorials);
        Button drillsButton = view.findViewById(R.id.button_drills);

        tutorialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Tutorials page
                Fragment tutorialFragment = new TutorialFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, tutorialFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        drillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Drills page
                Fragment drillsFragment = new DrillsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, drillsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
