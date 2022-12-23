package autotests;

import base.BaseTest;
import com.vk.api.sdk.actions.Likes;
import com.vk.api.sdk.actions.Wall;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.Type;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;


/**
 * Тесты
 *
 * @author Sushkov Denis
 * @version 1.0
 * @since 2023-12-23
 */
public class LikesAddDeletePositiveTest extends BaseTest {

    private static int itemId = 0;

    @Test(description = "Тест likes.add на посте", groups = "positive")
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
            softly.assertThat(validateJson(response.getContent(), "schema_likes_add.json"))
                    .as("Response json schema").isTrue();
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("likes").getAsInt())
                    .as("likes").isEqualTo(1);
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Ошибка в выполнении теста: " + e.getMessage());
        }
    }


    @Test(description = "Тест likes.delete на посте", dependsOnMethods = {"addLikePositiveTest"}, groups = "positive")
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
            softly.assertThat(validateJson(response.getContent(), "schema_likes_delete.json"))
                    .as("Response json schema").isTrue();
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("likes").getAsInt())
                    .as("likes").isEqualTo(0);
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Ошибка в выполнении теста: " + e.getMessage());
        }
    }


    @BeforeClass(description = "Готовим пост для теста")
    public void prepareItem() {
        try {
            itemId = new Wall(vk)
                    .post(actor)
                    .message("тест")
                    .execute()
                    .getPostId();
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Ошибка в выполнении теста: " + e.getMessage());
        }
    }

    @AfterClass(description = "Удаляем пост после тестов")
    public void deleteItem() {
        try {
            if (itemId != 0) {
                new Wall(vk)
                        .delete(actor)
                        .postId(itemId)
                        .execute();
            }
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Ошибка в выполнении теста: " + e.getMessage());
        }
    }
}
