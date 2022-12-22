package autotests;

import base.BaseTest;
import com.google.gson.JsonObject;
import com.vk.api.sdk.actions.Likes;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.Type;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import java.io.IOException;

public class LikesAddDeleteNegativeTest extends BaseTest {

    @Test(description = "Test add like to not founded post", groups = "negative")
    public void addLikeNegativeTest() {
        try {
            ClientResponse response = new Likes(vk)
                    .add(actor, Type.POST, notFoundedItemId)
                    .executeAsRaw();

            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode())
                    .as("Status code").isEqualTo(200);
            softly.assertThat(response.getHeaders())
                    .as("Headers").containsKey("Content-Type");
            softly.assertThat(response.getHeaders().get("Content-Type"))
                    .as("Content-Type").isEqualTo("application/json; charset=utf-8");
            softly.assertThat(response.getContent())
                    .as("Response content").isNotNull();
            softly.assertThat(validateJson(response.getContent(), "SchemaLikesError.json"))
                    .as("Response json schema").isTrue();
            JsonObject error = getJsonObject(response.getContent()).getAsJsonObject("error");
            softly.assertThat(error.get("error_code").getAsInt())
                    .as("error_code").isEqualTo(100);
            softly.assertThat(error.get("error_msg").getAsString())
                    .as("error_msg").isEqualTo("One of the parameters specified was missing or invalid: object not found");
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }

    @Test(description = "Test delete like from photo without access to photo", groups = "negative")
    public void deleteLikeNegativeTest(){
        try {
            final int PHOTO_ID = 457245427;
            ClientResponse response = new Likes(vk)
                    .delete(actor, Type.PHOTO, PHOTO_ID)
                    .executeAsRaw();

            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode())
                    .as("Status code").isEqualTo(200);
            softly.assertThat(response.getHeaders())
                    .as("Headers").containsKey("Content-Type");
            softly.assertThat(response.getHeaders().get("Content-Type"))
                    .as("Content-Type").isEqualTo("application/json; charset=utf-8");
            softly.assertThat(validateJson(response.getContent(), "SchemaLikesError.json"))
                    .as("Response json schema").isTrue();
            JsonObject error = getJsonObject(response.getContent()).getAsJsonObject("error");
            softly = new SoftAssertions();
            softly.assertThat(error.get("error_code").getAsInt())
                    .as("error_code").isEqualTo(15);
            softly.assertThat(error.get("error_msg").getAsString())
                    .as("error_msg").isEqualTo("Access denied");
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }
}
