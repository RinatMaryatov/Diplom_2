package order;

import com.google.gson.Gson;
import ingredients.Ingredients;
import ingredients.IngredientsMethods;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserMethods;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static helpers.UrlAdresses.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GettingOrdersTest {
    private static final String EMAIL = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
    private static final String PASSWORD = "test_password" + new Random().nextInt(10000);
    private static final String NAME = "testname";
    private String accessToken = null;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        createUser();
        createTestOrder();
    }

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

    public void createTestOrder() {
        List<String> ingredients = new IngredientsMethods().getHashes();
        Order order = new Order(ingredients.subList(0, 3));
        Response orderResponse = OrderMethods.createOrderAuth(order, accessToken);
        List<String> sentIngredientIds = ingredients.subList(0, 3);
        orderResponse.then().assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.ingredients._id", equalTo(sentIngredientIds));
    }

    @Test
    @DisplayName("Получение заказов конкретного авторизованного пользователя")
    @Description("Создаем пользователя, после чего используем его токен для получения его же заказов")
    public void getOrdersByAuthUser() {
        Response response = OrderMethods.getOrdersByUser(accessToken);
        response.then().assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("orders", notNullValue())
                .and()
                .body("total", notNullValue())
                .and()
                .body("totalToday", notNullValue())
                .and()
                .body("orders[0].ingredients", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getOrdersNoAuth() {
        Response response = OrderMethods.getOrdersByUser("");
        response.then().assertThat().statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        UserMethods.deleteUser(accessToken);
    }

}
