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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.util.Set;

@Listeners(BaseTestListener.class)
public class BaseTest {
    protected static VkApiClient vk;
    protected static UserActor actor;

    private static final int APP_ID = 51507314;
    private static final String CLIENT_SECRET = "m2MgTapQYpTkYhJzBeoD";
    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private static final String OFFLINE_CODE = "3284fb53913639d40d";
    //Для получения оффлайн-кода я воспользовался инструкцией https://vk.com/dev/authcode_flow_group
    //Генерирую новый руками при каждом запуске и вставляю в OFFLINE_CODE

    private static final Logger logger = LogManager.getRootLogger();

    protected int notFoundedItemId = 1111;

    @BeforeSuite
    public void initApi(){
        logger.info("START AUTOTESTS");
        TransportClient transportClient = new HttpTransportClient();
        vk = new VkApiClient(transportClient);

        try {
            UserAuthResponse authResponse= vk.oAuth()
                    .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET,REDIRECT_URI,OFFLINE_CODE)
                    .execute();
            actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Error in authorization code flow: " + e.getMessage());
        }

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

    protected static JsonObject getJsonObject(String json){
        return new Gson().fromJson(json, JsonObject.class);
    }
}
