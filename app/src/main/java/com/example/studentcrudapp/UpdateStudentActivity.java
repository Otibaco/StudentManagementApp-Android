package com.example.studentcrudapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateStudentActivity extends AppCompatActivity {
    private static final String TAG = "UpdateStudentActivity";

    private EditText etId, etName, etEmail, etAge;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private TextView tvResponse;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);

        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        btnUpdate = findViewById(R.id.btnUpdate);
        progressBar = findViewById(R.id.progressBar);
        tvResponse = findViewById(R.id.tvResponse);
        apiService = RetrofitClient.getAPIService();

        btnUpdate.setOnClickListener(v -> handleUpdate());
    }

    private void handleUpdate() {
        String idString = etId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ageString = etAge.getText().toString().trim();

        if (idString.isEmpty() || name.isEmpty() || email.isEmpty() || ageString.isEmpty()) {
            showErrorDialog("Please fill in all the fields!");
            return;
        }

        final Long id;
        final int age;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid numeric ID.");
            return;
        }
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for age.");
            return;
        }

        Student updatedStudent = new Student(name, email, age);

        // UI: show progress / disable button
        showProgressBar();
        btnUpdate.setEnabled(false);
        tvResponse.setText("");

        // IMPORTANT: call enqueue(...) to actually run the network request asynchronously
        apiService.updateStudent(id, updatedStudent).enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                hideProgressBar();
                btnUpdate.setEnabled(true);

                Log.d(TAG, "onResponse code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    tvResponse.setText("Student Updated: " + response.body().getName());
                    Toast.makeText(UpdateStudentActivity.this, "Student updated", Toast.LENGTH_SHORT).show();
                } else {
                    String serverMessage = "Server error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            serverMessage = response.errorBody().string();
                        }
                    } catch (Exception ex) {
                        Log.w(TAG, "error reading errorBody", ex);
                    }
//                    instead of showing server response errorBody show a simple message Request Failed
                    showErrorDialog("Request Failed");
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                hideProgressBar();
                btnUpdate.setEnabled(true);
                Log.e(TAG, "onFailure", t);
                if (t instanceof UnknownHostException) {
                    showErrorDialog("No internet connection. Please check your network.");
                } else {
                    showErrorDialog("Request failed: " + t.getMessage());
                }
            }
        });
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(ProgressBar.GONE);
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}