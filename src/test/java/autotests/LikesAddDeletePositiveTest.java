package autotests;

import base.BaseTest;
import com.vk.api.sdk.actions.Likes;
import com.vk.api.sdk.actions.Wall;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;


public class LikesAddDeletePositiveTest extends BaseTest {

    private static int itemId = 0;

    @Test(description = "Test add like to post", groups = "positive")
    public void addLikePositiveTest() {
        try {
            ClientResponse response = new Likes(vk)
                    .add(actor, Type.POST, itemId)
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
            softly.assertThat(response.getContent())
                    .as("Response content").doesNotContain("error");
            softly.assertThat(validateJson(response.getContent(), "SchemaLikesAdd.json"))
                    .as("Response json schema").isTrue();
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("likes").getAsInt())
                    .as("likes").isEqualTo(1);
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }


    @Test(description = "Test delete like from post", dependsOnMethods = {"addLikePositiveTest"}, groups = "positive")
    public void deleteLikePositiveTest() {
        try {
            ClientResponse response = new Likes(vk)
                    .delete(actor, Type.POST, itemId)
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
            softly.assertThat(response.getContent())
                    .as("Response content").doesNotContain("error");
            softly.assertThat(validateJson(response.getContent(), "SchemaLikesDelete.json"))
                    .as("Response json schema").isTrue();
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("likes").getAsInt())
                    .as("likes").isEqualTo(0);
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }


    @BeforeClass
    public void prepareItem() {
        try {
            itemId = new Wall(vk)
                    .post(actor)
                    .message("тест")
                    .execute()
                    .getPostId();
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }

    @AfterClass
    public void deleteItem() {
        try {
            if (itemId != 0) {
                new Wall(vk)
                        .delete(actor)
                        .postId(itemId)
                        .execute();
            }
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }
}
