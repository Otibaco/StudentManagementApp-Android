package com.example.studentcrudapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadStudentActivity extends AppCompatActivity {
    private TextView tvStudents;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_student);
        tvStudents = findViewById(R.id.tvStudents);
        apiService = RetrofitClient.getAPIService();
        loadStudents();
    }

    private void loadStudents() {
        // IMPORTANT: use enqueue(...) not equals(...)
        apiService.getAllStudents().enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder builder = new StringBuilder();
                    for (Student student : response.body()) {
                        builder.append("Name: ").append(student.getName())
                                .append(", Email: ").append(student.getEmail())
                                .append(", Age: ").append(student.getAge())
                                .append("\n\n");
                    }
                    tvStudents.setText(builder.toString());
                } else {
                    tvStudents.setText("Failed to load students. Check internet connection or server.");
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                tvStudents.setText("Error: " + t.getMessage());
            }
        });
    }
}