package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static helpers.UrlAdresses.ORDER;
import static io.restassured.RestAssured.given;

public class OrderMethods {
    @Step("Создание заказа без авторизации")
    public static Response createOrderNoAuthNoIngredients(Order order) {
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(order)
                .when()
                .post(ORDER);
    }

    @Step("Создание заказа с авторизацией")
    public static Response createOrderAuth(Order order, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .and()
                .header("Content-Type", "application/json")
                .and()
                .body(order)
                .when()
                .post(ORDER);
    }

    @Step("Получение заказов конкретного пользователя")
    public static Response getOrdersByUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(ORDER);
    }
}
