package com.example.studentcrudapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteStudentActivity extends AppCompatActivity {

    private EditText etId;
    private Button btnDelete;
    private ProgressBar progressBar;
    private TextView tvResponse;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_student);

        etId = findViewById(R.id.etId);
        btnDelete = findViewById(R.id.btnDelete);
        progressBar = findViewById(R.id.progressBar);
        tvResponse = findViewById(R.id.tvResponse);

        apiService = RetrofitClient.getAPIService();

        btnDelete.setOnClickListener(v -> {
            String idString = etId.getText().toString().trim();

            if (idString.isEmpty()) {
                showErrorDialog("Please enter the Student ID!");
                return;
            }

            if (!NetworkUtil.isConnectedToInternet(this)) {
                showErrorDialog("No internet connection. Please check your network and try again.");
                return;
            }

            Long id = Long.parseLong(idString);

            showProgressBar();
            apiService.deleteStudent(id).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    hideProgressBar();
                    if (response.isSuccessful() && response.body() != null) {
                        String message = response.body().get("message"); // Extract message from response
                        tvResponse.setText(message);
                    } else {
                        try {
                            String errorMessage = response.errorBody().string(); // Parse error response if needed
                            showErrorDialog(errorMessage);
                        } catch (Exception e) {
                            showErrorDialog("Failed to delete student!");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    hideProgressBar();
                    if (t instanceof java.net.UnknownHostException) {
                        showErrorDialog("No internet connection. Please check your network.");
                    } else {
                        showErrorDialog("Error: " + t.getMessage());
                    }
                }
            });

        });
    }

    private void showProgressBar() {
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