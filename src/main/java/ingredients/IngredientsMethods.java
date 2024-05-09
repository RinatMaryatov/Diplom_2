package ingredients;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.stream.Collectors;

import static helpers.UrlAdresses.INGREDIENTS;
import static io.restassured.RestAssured.given;

public class IngredientsMethods {
    @Step("Получение списка ингредиентов")
    public IngredientsResponse getIngredients() {
        return given()
                .when()
                .get(INGREDIENTS)
                .as(IngredientsResponse.class);
    }

    @Step("Получение списка хэш-кодов ингредиентов")
    public List<String> getHashes() {
        return getIngredients().getData().stream()
                .map(Ingredients::get_id)
                .collect(Collectors.toList());
    }
}
