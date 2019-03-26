package okhttp3.sample;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpContributors {
  private static final String ENDPOINT = "https://api.github.com/repos/square/okhttp/contributors";
  private static final Moshi MOSHI = new Moshi.Builder().build();
  private static final JsonAdapter<List<Contributor>> CONTRIBUTORS_JSON_ADAPTER = MOSHI.adapter(
      Types.newParameterizedType(List.class, Contributor.class));

  static class Contributor {
    String login;
    int contributions;
  }

  public static void main(String... args) throws Exception {
    OkHttpClient.Builder builder = new OkHttpClient.Builder().callTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15,TimeUnit.SECONDS)
            .cookieJar(new CookieJar() {
              @Override
              public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {

              }

              @NotNull
              @Override
              public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                return null;
              }
            });
    OkHttpClient client = builder.build();

    // Create request for remote resource.
    Request request = new Request.Builder()
        .url(ENDPOINT)
        .build();

    // Execute the request and retrieve the response.
    try (Response response = client.newCall(request).execute()) {
      // Deserialize HTTP response to concrete type.
      ResponseBody body = response.body();
      List<Contributor> contributors = CONTRIBUTORS_JSON_ADAPTER.fromJson(body.source());

      // Sort list by the most contributions.
      Collections.sort(contributors, (c1, c2) -> c2.contributions - c1.contributions);

      // Output list of contributors.
      for (Contributor contributor : contributors) {
        System.out.println(contributor.login + ": " + contributor.contributions);
      }
    }
  }

  private OkHttpContributors() {
    // No instances.
  }
}
