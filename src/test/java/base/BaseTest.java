package base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import oauth.OauthApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.util.Set;

import static base.CookieLoader.getCookiesHashMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Базовый класс для создания тестовых классов
 *
 * @author Sushkov Denis
 * @version 1.0
 * @since 2023-12-23
 */
@Listeners(BaseTestListener.class)
public class BaseTest {
    protected static VkApiClient vk;
    protected static UserActor actor;

    private final int APP_ID = 51507314;
    private final String CLIENT_SECRET = "m2MgTapQYpTkYhJzBeoD";
    private final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private final String AUTHORIZE = "https://oauth.vk.com/authorize";

    private static final Logger logger = LogManager.getRootLogger();

    protected int notFoundedItemId = 1111;

    @BeforeSuite
    public void initApi() {
        TransportClient transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);

        logger.info("Получение code\n\n");
        String code = getOfflineCode();

        logger.info("Получение access_token\n\n");
        try {
            UserAuthResponse authResponse = vk.oAuth()
                    .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
                    .execute();
            actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Ошибка в Authorization code flow: " + e.getMessage());
        }
        logger.info("Старт автотестов\n\n");
    }

    @BeforeMethod
    public void waitTimeout() throws InterruptedException {
        //Во избежание ошибки "error_code":6
        Thread.sleep(100);
    }

    protected static boolean validateJson(String json, String name) throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = factory.getSchema(BaseTest.class.getResourceAsStream("/" + name));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        logger.info("schema: " + jsonSchema.toString());
        logger.info("json: " + json);
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
        errors.forEach(logger::error);
        return errors.isEmpty();
    }

    protected static JsonObject getJsonObject(String json) {
        return new Gson().fromJson(json, JsonObject.class);
    }

    private String getOfflineCode() {
        //URL для получения доступа
        String grantAccessLocation = OauthApi
                .getGrantAccessLocationFromOauth(getAuthorizedUrl(),
                        getCookiesHashMap("grant_access_cookies.properties"));

        //URL c кодом
        String codeLocation = OauthApi
                .getCodeLocationFromLogin(getReShieldCodeLocation(grantAccessLocation),
                        getCookiesHashMap("get_code_cookies.properties"));

        assertThat(codeLocation).as("code url").contains("code");
        return codeLocation.split("=")[1];
    }

    private String getAuthorizedUrl() {
        return String.format("%s?client_id=%d&display=page&redirect_uri=%s&scope=offline,wall", AUTHORIZE, APP_ID, REDIRECT_URI);
    }

    private String getReShieldCodeLocation(String shieldCodeLocation) {
        return shieldCodeLocation.replace("%3A", ":").replace("%2F", "/");
    }
}
