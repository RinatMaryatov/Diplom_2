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

public class UpdatingUserTest {

    private final String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
    private final String newEmail = "new_" + email;
    private final String password = "test_password";
    private final String newPassword = "new_" + password;
    private final String name = "testname";
    private final String newName = "new_" + name;
    String accessToken = null;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Обновление почты авторизованным пользователем")
    @Description("Создаем пользователя, после чего обновляем его почту")
    public void updateEmailByAuthorizedUser() {
        User user = new User(email, password, name);
        Response registeredUser = UserMethods.createUser(user);
        this.accessToken = registeredUser.path("accessToken");
        Response response = UserMethods.updateAuthorizedUser(new User(newEmail), accessToken);
        response.then().assertThat().statusCode(200)
                .and()
                .body("user.email", equalTo(newEmail))
                .and()
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Обновление имени авторизованным пользователем")
    @Description("Создаем пользователя, после чего обновляем его имя")
    public void updateNameByAuthorizedUser() {
        User user = new User(email, password, name);
        Response registeredUser = UserMethods.createUser(user);
        this.accessToken = registeredUser.path("accessToken");
        Response response = UserMethods.updateAuthorizedUser(new User(newEmail, password, newName), accessToken);
        response.then().assertThat().statusCode(200)
                .and()
                .body("user.email", equalTo(newEmail))
                .and()
                .body("user.name", equalTo(newName));
    }


    @Test
    @DisplayName("Обновление почты неавторизованным пользователем")
    @Description("Создаем пользователя, после чего обновляем его почту неавторизованным пользователем")
    public void updateEmailByUnauthorizedUser() {
        User user = new User(email, password, name);
        UserMethods.createUser(user);
        Response response = UserMethods.updateUnauthorizedUser(new User(newEmail));
        response.then().assertThat().statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Обновление пользователя такой же почтой")
    @Description("Создаем пользователя, после чего обновляем его почту на ту же самую")
    public void updateEmailWithSameEmail() {
        User user1 = new User(email, password, name);
        Response registeredUser1 = UserMethods.createUser(user1);
        this.accessToken = registeredUser1.path("accessToken");
        User user2 = new User(newEmail, newPassword, newName);
        UserMethods.createUser(user2);

        Response response = UserMethods.updateAuthorizedUser(new User(newEmail, password, name), accessToken);
        response.then().assertThat().statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User with such email already exists"));
    }

    @After
    public void cleanUp() {
        if (accessToken != null ) {
            UserMethods.deleteUser(accessToken);
        }
    }
}