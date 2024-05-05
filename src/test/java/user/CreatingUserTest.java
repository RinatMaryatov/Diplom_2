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
import static org.hamcrest.Matchers.notNullValue;

public class CreatingUserTest {
    private static final String EMAIL = "rinat_maryatov" + new Random().nextInt(10000) + "@yandex.ru";
    private static final String PASSWORD = "password" + new Random().nextInt(10000);
    private static final String NAME = "Rinat";
    String accessToken = null;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Создание пользователя со всеми заполненными полями")
    public void createUser() {
        User user = new User(EMAIL, PASSWORD, NAME);
        Response response = UserMethods.createUser(user);
        this.accessToken = response.path("accessToken");
        response.then().assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(EMAIL))
                .and()
                .body("user.name", equalTo(NAME))
                .and()
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя без логина")
    @Description("Создание пользователя с указанием только имени и пароля")
    public void createUserWithoutLogin() {
        User user = new User(PASSWORD, NAME);
        Response response = UserMethods.createUser(user);
        response.then().assertThat().statusCode(403)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Создание пользователя с указанием только имени и логина")
    public void createUserWithoutPassword() {
        User user = new User(EMAIL, NAME);
        Response response = UserMethods.createUser(user);
        response.then().assertThat().statusCode(403)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Создание пользователя с указанием валидных данных, затем создание пользователя с теми же данными")
    public void createDuplicateUser() {
        User user = new User(EMAIL, PASSWORD, NAME);
        Response firstResponse = UserMethods.createUser(user);
        this.accessToken = firstResponse.path("accessToken");
        Response secondResponse = UserMethods.createUser(user);

        secondResponse.then().assertThat().statusCode(403)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }
    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserMethods.deleteUser(accessToken);
        }
    }
}
