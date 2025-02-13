package com.example.headache_classification.API;


import com.example.headache_classification.Modals.LoginModal;
import com.example.headache_classification.Modals.RegistrationModal;
import com.example.headache_classification.Modals.UploadImageModal;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    @FormUrlEncoded
    @POST("api/save_user/")
    Call<RegistrationModal> save_user(
            @Field("name") String name,
            @Field("contact") String contact,
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password,
            @Field("address") String address);

    @FormUrlEncoded
    @POST("api/user_login/")
    Call<LoginModal> login(
            @Field("username") String username,
            @Field("password") String password);

    @Multipart
    @POST("api/save_image/")
    Call<UploadImageModal> save_image(
            @Part MultipartBody.Part image);

}
