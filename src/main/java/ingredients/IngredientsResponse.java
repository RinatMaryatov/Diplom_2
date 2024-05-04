package ingredients;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientsResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private List<Ingredients> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Ingredients> getData() {
        return data;
    }

    public void setData(List<Ingredients> data) {
        this.data = data;
    }
}
