package com.example.spots_enhancing_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateProfileFragment extends Fragment {

    private static final String TAG = "UpdateProfileFragment";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText currentEmailInput, updateEmailInput, updatePasswordInput, currentPasswordInput;
    private Button updateProfileButton;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        currentEmailInput = view.findViewById(R.id.current_email_input);
        updateEmailInput = view.findViewById(R.id.update_email_input);
        updatePasswordInput = view.findViewById(R.id.update_password_input);
        currentPasswordInput = view.findViewById(R.id.current_password_input);
        updateProfileButton = view.findViewById(R.id.update_profile_button);

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentEmail = currentEmailInput.getText().toString().trim();
                String newEmail = updateEmailInput.getText().toString().trim();
                String newPassword = updatePasswordInput.getText().toString().trim();
                String currentPassword = currentPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(currentEmail) || TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(getActivity(), "Current email and password are required for authentication", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if user is logged in
                if (user == null) {
                    Toast.makeText(getActivity(), "No user is logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Re-authenticate user
                AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Re-authentication successful");

                        if (!TextUtils.isEmpty(newEmail)) {
                            user.updateEmail(newEmail).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "Email updated successfully to: " + newEmail);
                                    Toast.makeText(getActivity(), "Email updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "Failed to update email: " + task1.getException().getMessage());
                                    Toast.makeText(getActivity(), "Failed to update email: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        if (!TextUtils.isEmpty(newPassword)) {
                            user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "Password updated successfully");
                                    Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "Failed to update password: " + task1.getException().getMessage());
                                    Toast.makeText(getActivity(), "Failed to update password: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Re-authentication failed: " + task.getException().getMessage());
                        Toast.makeText(getActivity(), "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}
