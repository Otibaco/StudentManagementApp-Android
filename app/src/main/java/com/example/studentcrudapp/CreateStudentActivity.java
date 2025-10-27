package com.example.studentcrudapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class CreateStudentActivity extends AppCompatActivity {
    private EditText etName, etEmail, etAge;
    private Button btnSubmit, btnRead, btnUpdate, btnDelete;
    private ProgressBar progressBar;
    private TextView tvResponse;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
        tvResponse = findViewById(R.id.tvResponse);
        btnUpdate = findViewById(R.id.btnNavigate2);
        btnDelete = findViewById(R.id.btnNavigate3);
        btnRead = findViewById(R.id.btnNavigate);

        apiService = RetrofitClient.getAPIService();

        btnSubmit.setOnClickListener(this::onClick);
        btnRead.setOnClickListener(v -> startActivity(new Intent(this, ReadStudentActivity.class)));
        btnUpdate.setOnClickListener(v -> startActivity(new Intent(this, UpdateStudentActivity.class)));
        btnDelete.setOnClickListener(v -> startActivity(new Intent(this, DeleteStudentActivity.class)));
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void onClick(View v) {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ageString = etAge.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || ageString.isEmpty()) {
            showErrorDialog("Please fill in all the fields!");
            return;
        }

        // Make sure you have a NetworkUtil.isConnectedToInternet(this) or replace with your own check
        if (!NetworkUtil.isConnectedToInternet(this)) {
            showErrorDialog("No internet connection. Please check your network and try again.");
            return;
        }

        final int age;
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException nfe) {
            showErrorDialog("Please enter a valid number for age.");
            return;
        }

        Student student = new Student(name, email, age);

        showProgressBar();

        // CORRECT: use enqueue(...) to perform the async request and get callbacks
        apiService.addStudent(student).enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                hideProgressBar();
                if (response.isSuccessful() && response.body() != null) {
                    tvResponse.setText("Student Added: " + response.body().getName());
                    Toast.makeText(CreateStudentActivity.this, "Student added", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown server error";
//                        showErrorDialog(errorMessage); instead of showing server response errorBody show a simple message Request Failed
                        showErrorDialog("Request Failed");
                    } catch (Exception e) {
                        showErrorDialog("Failed to process the server response!");
                    }
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                hideProgressBar();
                if (t instanceof UnknownHostException) {
                    showErrorDialog("No internet connection. Please check your network.");
                } else {
                    showErrorDialog("Request failed: " + t.getMessage());
                }
            }
        });
    }
}