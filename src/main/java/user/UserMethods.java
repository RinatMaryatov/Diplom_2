package user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import static helpers.UrlAdresses.*;
import static io.restassured.RestAssured.given;

public class UserMethods {
    @Step("Создание пользователя")
    public static Response createUser(User user) {
        Response response = given()
                .header("Content-Type", "application/json")
                .and()
                .body(user)
                .when()
                .post(REGISTER)
                .then()
                .extract().response();

        return response;
    }

    @Step("Логин пользователя")
    public static Response loginUser(User user) {
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(user)
                .when()
                .post(LOGIN);
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String bearerToken) {
        if (bearerToken != null && !bearerToken.isEmpty()) {
            given()
                    .header("Authorization", bearerToken)
                    .when()
                    .delete(USER);
        } else {
            System.out.println("Bearer token is empty or null. User is not deleted");
        }
    }

    @Step("Изменение данных авторизованным пользователем")
    public static Response updateAuthorizedUser(User user, String bearerToken) {
        return given()
                .header("Authorization", bearerToken)
                .contentType("application/json")
                .body(user)
                .when()
                .patch(USER);
    }

    @Step("Изменение данных неавторизованным пользователем")
    public static Response updateUnauthorizedUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .patch(USER);
    }
}