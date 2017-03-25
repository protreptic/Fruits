package name.peterbukhal.android.fruit.service.fruits;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.Credentials;

/**
 * TODO Доработать документацию
 *
 * @author Peter Bukhal (peter.bukhal@gmail.com)
 */
public final class FruitAuthenticator implements Authenticator {

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return response.request()
                .newBuilder()
                .header("Authorization", Credentials.basic("123", "123"))
                .build();
    }

}
