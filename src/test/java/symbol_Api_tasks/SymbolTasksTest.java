package symbol_Api_tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.ConfigurationReader;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SymbolTasksTest {

    static Response response;
    static List<SymbolPojo> list;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        RestAssured.baseURI = ConfigurationReader.get("api_url");
        response = given().accept(ContentType.JSON)
                .when().get(baseURI);
        String body = response.body().asString();
        ObjectMapper mapper = new ObjectMapper();
        list = mapper.readValue(body, new TypeReference<>() {
        });
    }

    @DisplayName("Verify status and content type")
    @Test
    void statusContentType() {
        assertEquals(200, response.statusCode());
        assertEquals("application/json; charset=utf-8", response.contentType());
    }

    @DisplayName("Verify all symbols are concatenation of baseAsset and quoteAsset")
    @Test
    void symbolTest() {
        list.forEach(
                s -> assertEquals(s.getBaseAsset() + s.getQuoteAsset(), s.getSymbol(), "all symbols are concatenation of baseAsset and quoteAsset")
        );
    }

    @DisplayName("Verify each high price is equal or more than low price")
    @Test
    void lowPriceHighPriceTest() {
        list.forEach(
                s -> assertTrue(Double.parseDouble(s.getHighPrice()) >= Double.parseDouble(s.getLowPrice()), "each high price is equal or more than low price")
        );
    }

    @DisplayName("Verify each open price is equal or more than low price")
    @Test
    void lowPriceTest() {
        list.forEach(
                s -> assertTrue(Double.parseDouble(s.getOpenPrice()) >= Double.parseDouble(s.getLowPrice()),"open price: "+ s.getOpenPrice()+ " is less than low price: "+ s.getLowPrice())
        );
    }

    @DisplayName("Verify each open price is equal or less than high price")
    @Test
    void highPriceTest() {
        list.forEach(
                s -> assertTrue(Double.parseDouble(s.getOpenPrice()) <= Double.parseDouble(s.getHighPrice()),"open price: "+ s.getOpenPrice()+ " is higher than high price: "+ s.getHighPrice())
        );
    }

    @DisplayName("Verify each askPrice is equal or greater than bidPrice")
    @Test
    void bidPriceAskPriceTest() {
        list.forEach(
                s -> assertTrue(Double.parseDouble(s.getAskPrice()) >= Double.parseDouble(s.getBidPrice()),"open askPrice is less than bidPrice")
        );
    }

    @DisplayName("Get all symbols whose open price is less than low price")
    @Test
    void getOpenPricesLessThanLowPrices() {
        System.out.println("\u001B[34m" + "Getting all symbols whose open price is less than low price:" + "\u001B[0m");
        List<SymbolPojo> openAndLowPrices = list.stream()
                .filter(s -> Double.parseDouble(s.getOpenPrice()) < Double.parseDouble(s.getLowPrice()))
                .collect(Collectors.toList());

        if (openAndLowPrices.size() > 0) {
            openAndLowPrices.forEach(
                    s -> System.out.println(s.getSymbol() + " " + s.getOpenPrice() + " " + s.getLowPrice())
            );
        } else {
            System.out.println("There is no symbols whose open price is less than low price");
        }
    }

    @DisplayName("Get all such symbols and baseAssets whose volume are more than average of all volumes in the list")
    @Test
    void getVolumesMoreThanAverageVolumes() {
        System.out.println("\u001B[34m" + "Getting all such symbols and baseAssets whose volume are more than average of all volumes in the list:" + "\u001B[0m");
        double averageVolume = list.stream()
                .mapToDouble(s -> Double.parseDouble(s.getVolume()))
                .average().orElseThrow();

        System.out.println("Symbol\tBaseAsset");
        list.stream()
                .filter(s -> Double.parseDouble(s.getVolume()) > averageVolume)
                .forEach(
                        s -> System.out.println(s.getSymbol() + "\t" + s.getBaseAsset())
                );
    }

    @DisplayName("Find 5 such assets/symbol which has least differences between bidPrice and askPrice")
    @Test
    void findAssetsSymbolsComparingBidPriceAndAskPrice() {
        System.out.println("\u001B[34m" + "Finding 5 such assets/symbol which has least differences between bidPrice and askPrice:" + "\u001B[0m");
        list.stream()
                .sorted(Comparator.comparing(s -> Math.abs(Double.parseDouble(s.getBidPrice()) - Double.parseDouble(s.getAskPrice()))))
                .limit(5)
                .map(SymbolPojo::getSymbol)
                .forEach(System.out::println);
    }
}
