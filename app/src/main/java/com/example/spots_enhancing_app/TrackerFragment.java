package com.example.spots_enhancing_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrackerFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private TableLayout scoreTable;

    public TrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);

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

        // Initialize TableLayout
        scoreTable = view.findViewById(R.id.score_table);

        // Load scores from Firebase
        loadScores();

        return view;
    }

    private void loadScores() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("drill_results");

        databaseReference.orderByKey().limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> scores = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String score = snapshot.getValue(String.class);
                    if (score != null) {
                        scores.add(score);
                    }
                }

                // Reverse the list to display the latest scores first
                displayScores(reverseList(scores));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private List<String> reverseList(List<String> list) {
        List<String> reversed = new ArrayList<>(list);
        java.util.Collections.reverse(reversed);
        return reversed;
    }

    private void displayScores(List<String> scores) {
        int attemptNumber = 1;
        for (String score : scores) {
            TableRow row = new TableRow(getActivity());

            TextView attemptView = new TextView(getActivity());
            attemptView.setText(String.valueOf(attemptNumber++));
            attemptView.setPadding(8, 8, 8, 8);

            TextView scoreView = new TextView(getActivity());
            scoreView.setText(score);
            scoreView.setPadding(8, 8, 8, 8);

            row.addView(attemptView);
            row.addView(scoreView);

            scoreTable.addView(row);
        }
    }
}

