package com.tanmaybuilds.lnmtrack.ApiInterface;

import com.tanmaybuilds.lnmtrack.DataModels.ApiResponse;
import com.tanmaybuilds.lnmtrack.DataModels.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/attendance")
    Call<ApiResponse> getAttendance(@Body LoginRequest request);
}
