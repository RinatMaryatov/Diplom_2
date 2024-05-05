package order;

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
import static helpers.UrlAdresses.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingOrderTest {
    private static final String EMAIL = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
    private static final String PASSWORD = "test_password" + new Random().nextInt(10000);
    private static final String NAME = "testname";
    private String accessToken = null;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        createUser();
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

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Создаем пользователя, после чего используем его токен для создания заказа с ингредиентами")
    public void createOrderWithAuthorizationAndIngredients() {
        List<String> ingredients = new IngredientsMethods().getHashes();
        Order order = new Order(ingredients.subList(0, 3));
        Response orderResponse = OrderMethods.createOrderAuth(order, accessToken);
        List<String> sentIngredientIds = ingredients.subList(0, 3);
        orderResponse.then().assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.ingredients._id", equalTo(sentIngredientIds));
    }

    @Test
    @DisplayName("Создание заказа без авторизации, но с ингредиентами")
    @Description("Создаем заказ без создания пользователя, но с ингредиентами")
    public void createOrderNoAuthWithIngredients() {
        List<String> ingredients = new IngredientsMethods().getHashes();
        Order order = new Order(ingredients.subList(0, 3));
        Response orderResponse = OrderMethods.createOrderNoAuthNoIngredients(order);
        orderResponse.then().assertThat().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов, но с авторизацией")
    @Description("Создаем пользователя, после чего используем его токен для создания заказа, но игредиенты не передаем")
    public void createOrderNoIngredientsWithAuth() {
        Order order = new Order (List.of());
        Response orderResponse = OrderMethods.createOrderAuth(order, accessToken);
        orderResponse.then().assertThat().statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Создаем заказ без создания пользователя и без ингредиентов")
    public void createOrderNoAuthNoIngredients() {
        Order order = new Order (List.of());
        Response orderResponse = OrderMethods.createOrderNoAuthNoIngredients(order);
        orderResponse.then().assertThat().statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Создаем пользователя, затем делаем заказ, но передаем неверные хеши ингредиентов")
    public void createOrderWithInvalidIngredientHashes() {
        List<String> invalidIngredientsHashes = List.of("invalid_hash_1", "invalid_hash_2", "invalid_hash_3");
        Order order = new Order(invalidIngredientsHashes);
        Response orderResponse = OrderMethods.createOrderAuth(order, accessToken);
        orderResponse.then().assertThat().statusCode(500);
    }

    @After
    public void tearDown() {
        UserMethods.deleteUser(accessToken);
    }
}

