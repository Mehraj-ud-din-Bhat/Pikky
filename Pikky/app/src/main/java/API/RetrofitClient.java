package API;

import retrofit2.converter.gson.GsonConverterFactory;
import API.API;
public class RetrofitClient {
    API myAPI;

    public RetrofitClient() {

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(API_URLS.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
      this.myAPI = retrofit.create(API.class);
    }

    public API getMyAPI() {
        return myAPI;
    }
}
