package autotests;

import base.BaseTest;
import com.google.gson.JsonObject;
import com.vk.api.sdk.actions.Likes;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.Type;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GetLikesIsLikedTest extends BaseTest {

    //DDT
    @Test(description = "Test get likes count from post", dataProvider = "itemIdsWithLikesCount", groups = "positive")
    public void getLikesListPositiveTest(int itemId, int countOfLikes) {
        try {
            ClientResponse response = new Likes(vk)
                    .getList(actor, Type.POST)
                    .itemId(itemId)
                    .executeAsRaw();

            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode())
                    .as("Status code").isEqualTo(200);
            softly.assertThat(response.getHeaders())
                    .as("Headers").containsKey("Content-Type");
            softly.assertThat(response.getHeaders().get("Content-Type"))
                    .as("Content-Type").isEqualTo("application/json; charset=utf-8");
            softly.assertThat(response.getContent())
                    .as("Response content").doesNotContain("error");
            softly.assertThat(validateJson(response.getContent(), "SchemaGetLikesList.json"))
                    .as("Response json schema").isTrue();
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("count").getAsInt())
                    .as("count").isEqualTo(countOfLikes);
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }

    @Test(description = "Test get likes count from not founded post", groups = "negative")
    public void getLikesListNegativeTest() {
        try {
            ClientResponse response = new Likes(vk)
                    .getList(actor, Type.POST)
                    .itemId(notFoundedItemId)
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
                    .as("error_code").isEqualTo(15);
            softly.assertThat(error.get("error_msg").getAsString())
                    .as("error_msg").isEqualTo("Access denied: this post does not exist");
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }

    @Test(description = "Test is liked method on post", groups = "positive")
    public void isLikedPositiveTest(){
        try {
            final int POST_OWNER_ID = -72495085;
            final int POST_ID = 1347881;
            ClientResponse response = new Likes(vk)
                    .isLiked(actor, Type.POST, POST_ID)
                    .ownerId(POST_OWNER_ID)
                    .executeAsRaw();

            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode())
                    .as("Status code").isEqualTo(200);
            softly.assertThat(response.getHeaders())
                    .as("Headers").containsKey("Content-Type");
            softly.assertThat(response.getHeaders().get("Content-Type"))
                    .as("Content-Type").isEqualTo("application/json; charset=utf-8");
            softly.assertThat(response.getContent())
                    .as("Response content").doesNotContain("error");
            softly.assertThat(validateJson(response.getContent(), "SchemaIsLiked.json"))
                    .as("Response json schema").isTrue();
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("liked").getAsInt())
                    .as("liked").isEqualTo(1);
            softly.assertThat(getJsonObject(response.getContent()).getAsJsonObject("response").get("copied").getAsInt())
                    .as("copied").isEqualTo(1); //тест падает, хотя репост item_id=1347881 есть у меня на стене
            softly.assertAll();
        } catch (ClientException | IOException e) {
            throw new RuntimeException("Error in test: " + e.getMessage());
        }
    }

    @Test(description = "Test is liked method on post without access to video", groups = "negative")
    public void isLikedNegativeTest(){
        try {
            final int VIDEO_ID = 456239410;
            ClientResponse response = new Likes(vk)
                    .isLiked(actor, Type.VIDEO, VIDEO_ID)
                    .executeAsRaw();

            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode())
                    .as("Status code").isEqualTo(200);
            softly.assertThat(response.getHeaders())
                    .as("Headers").containsKey("Content-Type");
            softly.assertThat(response.getHeaders().get("Content-Type"))
                    .as("Content-Type").isEqualTo("application/json; charset=utf-8");
            softly.assertThat(validateJson(response.getContent(), "SchemaLikesError.json"))
                    .as("Response json schema").isTrue(); //схема не совпадает, возможно я не понял требования, либо здесь ошибка, но доступа к VIDEO я не выдавал
            softly.assertAll();

            JsonObject error = getJsonObject(response.getContent()).getAsJsonObject("error");
            assertThat(error).as("error").isNotNull();

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

    @DataProvider(name = "itemIdsWithLikesCount")
    public Object[][] getItemIdsWithLikesCount() {
        return new Object[][]{{11318, 14}, {11294, 10}};
    }
}
