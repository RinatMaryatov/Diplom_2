package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import static helpers.UrlAdresses.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {
    private static final String email = "rinat" + new Random().nextInt(10000) + "@yandex.ru";
    private static final String password = "test_password";
    private static final String name = "rinat";
    String accessToken = null;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Создание пользователя с последующим логином под ним")
    public void loginUser() {
          User user = new User(email, password, name);
            UserMethods.createUser(user);
        Response response = UserMethods.loginUser(new User(email, password));
        accessToken = UserMethods.loginUser(user).then().extract().path("accessToken").toString();
        response.then().assertThat().statusCode(200)
                .and()
                .body("user.email", equalTo(email))
                .and()
                .body("user.name", equalTo(name))
                .and()
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Создание пользователя с последующим логином под ним, но с неверным email")
    public void loginUserWithWrongEmail() {
        User user = new User(email, password, name);
        UserMethods.createUser(user);
        Response response = UserMethods.loginUser(new User("wrong" + email, password));
        accessToken = UserMethods.loginUser(user).then().extract().path("accessToken").toString();
        response.then().assertThat().statusCode(401)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserMethods.deleteUser(accessToken);
        }
    }
}
