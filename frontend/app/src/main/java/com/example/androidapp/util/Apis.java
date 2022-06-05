package com.example.androidapp.util;

import kotlin.ExperimentalMultiplatform;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Apis {
    @POST("/post/init/")
    Call<ResponseBody> upload(@Body MultipartBody file);

    @POST("/draft/init/")
    Call<ResponseBody> uploadDraft(@Body FormBody body);

    @POST("/draft/edit/")
    Call<ResponseBody> editDraft(@Body FormBody body);
}
