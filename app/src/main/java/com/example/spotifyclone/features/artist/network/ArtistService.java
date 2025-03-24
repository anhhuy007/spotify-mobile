package com.example.spotifyclone.features.artist.network;

import com.example.spotifyclone.features.artist.model.FansAlsoLike;
import com.example.spotifyclone.features.artist.model.Item;
import com.example.spotifyclone.features.artist.model.ItemDiscographyAlbum;
import com.example.spotifyclone.features.artist.model.ItemDiscographyEP;
import com.example.spotifyclone.features.artist.model.PopularSong;
import com.example.spotifyclone.shared.model.APIResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArtistService {
    @GET("artist/listArtist")
    Call<APIResponse<List<Item>>> getListArtist();
    @GET("artist/getArtist/{id}")
    Call<APIResponse<Item>> getArtistDetail(@Path("id") String artistId);

    @GET("artist/listDiscographyEP/{id}")
    Call<APIResponse<List<ItemDiscographyEP>>> getListDiscographyEP(@Path("id") String artistId);

    @GET("artist/listDiscographyHave/{id}")
    Call<APIResponse<List<ItemDiscographyEP>>> getListDiscographyHave(@Path("id") String artistId);

    @GET("artist/listDiscographyCollection/{id}")
    Call<APIResponse<List<ItemDiscographyAlbum>>> getListDiscographyCollection(@Path("id") String artistId);

    @GET("artist/listDiscographyAlbum/{id}")
    Call<APIResponse<List<ItemDiscographyAlbum>>> getListDiscographyAlbum(@Path("id") String artistId);

    @GET("artist/getListFansAlsoLikeArtistDetail/{id}")
    Call<APIResponse<List<FansAlsoLike>>> getListFansAlsoLikeArtistDetail(@Path("id") String artistId);


    @GET("artist/getListPopularArtistDetail/{id}")
    Call<APIResponse<List<PopularSong>>> getListPopularArtistDetail(@Path("id") String artistId);



}
