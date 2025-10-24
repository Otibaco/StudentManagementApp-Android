package com.example.studentcrudapp;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    // Create Student (POST)
    @POST("/students")
    Call<Student> addStudent(@Body Student student);

    // Get All Students (GET)
    @GET("/students")
    Call<List<Student>> getAllStudents();

    // Get Student by ID (GET)
    @GET("/students/{id}")
    Call<Student> getStudentById(@Path("id") Long id);

    // Update Student (PUT)
    @PUT("/students/{id}")
    Call<Student> updateStudent(@Path("id") Long id, @Body Student student);

    // Delete Student (DELETE)
    @DELETE("/students/{id}")
    Call<Map<String, String>> deleteStudent(@Path("id") Long id);
}