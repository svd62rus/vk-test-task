package oauth;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;

import java.util.HashMap;

/**
 * Класс взаимодействия с VK для получения code
 *
 * @author Sushkov Denis
 * @version 1.0
 * @since 2023-12-23
 */
public class OauthApi {

    private static final RequestSpecification requestSpecification;

    static {
        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }

    @SneakyThrows
    public static String getGrantAccessLocationFromOauth(String url, HashMap<String, String> cookies) {
        Response response = RestAssured.given().
                when()
                .spec(requestSpecification)
                .cookies(cookies)
                .redirects().follow(false)
                .get(url);
        return response.getHeader("location");
    }

    @SneakyThrows
    public static String getCodeLocationFromLogin(String url, HashMap<String, String> cookies) {
        Response response = RestAssured.given().
                when()
                .spec(requestSpecification)
                .cookies(cookies)
                .redirects().follow(false)
                .get(url);
        response.prettyPrint();
        return response.getHeader("location");
    }
}
