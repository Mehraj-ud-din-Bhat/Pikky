package API;


import com.xscoder.pikky.loginSignUp.ModalClasses.LoginResponse;

import retrofit2.Call;
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
