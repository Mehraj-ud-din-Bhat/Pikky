package API;


import ModalClasses.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface API {

   // @FormUrlEncoded
    @POST("user/login/{useremail}/{password}")
    Call<LoginResponse> userLogin(
            @Path("useremail") String email,
            @Path("password") String password

    );



//    @GET("songs")
//    Call<List<Song>> getSongs();

}
