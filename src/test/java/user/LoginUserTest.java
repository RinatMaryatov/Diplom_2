package user;

import helpers.UrlAdresses;
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
    private static final String EMAIL = "rinat" + new Random().nextInt(10000) + "@yandex.ru";
    private static final String PASSWORD = "test_password";
    private static final String NAME = "rinat";
    String accessToken = null;

    @Before
    public void setUp() {
        UrlAdresses.URI();
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Создание пользователя с последующим логином под ним")
    public void loginUser() {
          User user = new User(EMAIL, PASSWORD, NAME);
            UserMethods.createUser(user);
        Response response = UserMethods.loginUser(new User(EMAIL, PASSWORD));
        accessToken = UserMethods.loginUser(user).then().extract().path("accessToken").toString();
        response.then().assertThat().statusCode(200)
                .and()
                .body("user.email", equalTo(EMAIL))
                .and()
                .body("user.name", equalTo(NAME))
                .and()
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Создание пользователя с последующим логином под ним, но с неверным email")
    public void loginUserWithWrongEmail() {
        User user = new User(EMAIL, PASSWORD, NAME);
        UserMethods.createUser(user);
        Response response = UserMethods.loginUser(new User("wrong" + EMAIL, PASSWORD));
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
