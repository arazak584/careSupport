package com.khrc.caresupport.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.khrc.caresupport.Activity.MainActivity;
import com.khrc.caresupport.R;
import com.khrc.caresupport.ViewModel.LogViewModel;
import com.khrc.caresupport.ViewModel.UsersViewModel;
import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.importredcap.UserProfile;
import com.khrc.caresupport.importredcap.UsersApiClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnantFragmentWomanLogin extends Fragment {

    private SharedPreferences sharedPreferences;
    private Users profile;
    private TextInputEditText username, password;
    private ProgressDialog progressDialog;
    public static final String USER_DATA = "com.khrc.caresupport.Activity.MainActivity.USER_DATA";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pregnant_woman_login, container, false);

        final UsersViewModel profileViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        final LogViewModel logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
        final MaterialButton start = view.findViewById(R.id.btnLoginPregnantWoman);
        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        username = view.findViewById(R.id.edtEmailPregnantWoman);
        password = view.findViewById(R.id.edtPasswordPregnantWoman);

        start.setOnClickListener(v -> {
            String phoneNumber = username.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                username.setError("Invalid Phone Number");
                Toast.makeText(getActivity(), "Please provide a valid phone number", Toast.LENGTH_LONG).show();
                return;
            }

            if (password.getText().toString().isEmpty()) {
                password.setError("Invalid User PIN");
                Toast.makeText(getActivity(), "Please provide a valid user PIN", Toast.LENGTH_LONG).show();
                return;
            }

            final String myuser = phoneNumber;
            final String mypass = password.getText().toString().trim();
            savePhoneNumber(myuser);

            // Show progress dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            // Check if user exists in local database
            new Thread(() -> {
                try {
                    profile = profileViewModel.finds(myuser);
                } catch (ExecutionException | InterruptedException e) {
                    Log.e("LoginError", "Error checking user existence", e);
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                if (profile == null) {
                    // User does not exist, try to import user
                    importUser(phoneNumber, new UsersApiClient.RedcapApiCallback() {
                        @Override
                        public void onSuccess(List<UserProfile> result) {
                            // User imported successfully, try to login again
                            new Thread(() -> {
                                try {
                                    profile = profileViewModel.find(myuser, mypass);
                                    if (profile != null) {
                                        getActivity().runOnUiThread(() -> handleLoginSuccess(profile, logViewModel, myuser));
                                    } else {
                                        getActivity().runOnUiThread(() -> {
                                            username.setError("User Not Registered");
                                            Toast.makeText(getActivity(), "User Not Registered", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                } catch (ExecutionException | InterruptedException e) {
                                    Log.e("LoginError", "Error finding user after import", e);
                                    getActivity().runOnUiThread(() -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onError(String error) {
                            // Handle error, if needed
                            Log.d("Download", "Download Error: " + error);
                            getActivity().runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Failed to import user", Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                } else {
                    // User exists, check password
                    if (profile.getPin().equals(mypass)) {
                        getActivity().runOnUiThread(() -> handleLoginSuccess(profile, logViewModel, myuser));
                    } else {
                        getActivity().runOnUiThread(() -> {
                            password.setError("Invalid Password");
                            Toast.makeText(getActivity(), "Invalid Password", Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }).start();
        });

        return view;
    }

    private void handleLoginSuccess(Users profile, LogViewModel logViewModel, String myuser) {
        if (profile != null && profile.getUstatus() != 1) {
            username.setError("Access Denied");
            Toast.makeText(getActivity(), "Access Denied", Toast.LENGTH_LONG).show();
            return;
        }

        LogBook log = new LogBook();
        log.setTel(myuser);
        log.logdate = new Date();
        logViewModel.add(log);

        username.setError(null);
        username.setText(null);
        password.setText(null);

        Intent i = new Intent(getActivity(), MainActivity.class);
        i.putExtra(USER_DATA, profile);
        startActivity(i);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void importUser(String phoneNumber, UsersApiClient.RedcapApiCallback callback) {
        UsersApiClient usersApiClient = new UsersApiClient(requireActivity());
        usersApiClient.loadAndInsertProfileData(phoneNumber, callback, requireActivity());
    }

    private void savePhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }
}