package com.example.mobdev_3.fruits.service.fruits;

import com.example.mobdev_3.fruits.service.fruits.model.Fruit;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Fruits {

    @GET("fruit")
    Observable<Response<List<Fruit>>> getAllFruits();

    @GET("fruit/{id}")
    Observable<Response<Fruit>> getFruitById(@Path("id") int fruitId);

    @POST("fruit/{id}")
    Observable<Response<Fruit>> editFruit(@Path("id") int fruitId, @Body Fruit fruit);

    @PUT("fruit/{id}")
    Observable<Response<Fruit>> addFruit(@Path("id") int fruitId, @Body Fruit fruit);

    @DELETE("fruit/{id}")
    Observable<Response<Void>> removeFruit(@Path("id") int fruitId);

}
