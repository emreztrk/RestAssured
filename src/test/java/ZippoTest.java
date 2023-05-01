
import Model.Location;
import Model.Place;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class ZippoTest {

    @Test
    public void test(){

        given()
                // Hazırlık işlemleri : (token,send body, parametreler)

                .when()
                // endpoint (url), metodu

                .then()
        // assertion, test, data işlemleri
        ;
    }

    @Test
    public void ilkTest(){

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()       // dönen body json data sı , log().all() da denebilir
                .statusCode(200) // dönüş kod 200 mü?

                ;
    }

    @Test
    public void contentTypeTest(){

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)  // dönen sonuç json mı?

        ;
    }

    @Test
    public void checkCountryInResponseBody(){

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("country", equalTo("United States"))  // body nin counrtry değişkeni "United States" e eşit mi?

                // pm.response.json().id ->body.id (postman da böyleydi)

        ;
    }

    @Test
    public void checkStateInResponseBody(){

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .statusCode(200)
                .body("places[0].state", equalTo("California"))

        ;
    }

    @Test
    public void checkHasItem(){

        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                //.log().body() // yoruma alımca da çalışıyor fakat itemleri göstermiyor.
                .statusCode(200)
                .body("places.'place name'", hasItem("Dörtağaç Köyü"))
                // bütün place name lerin herhangi birinde Dörtağaç Köyü var mı?
        ;
    }

    @Test
    public void bodyArrayHasSizeTest(){

        given()

                .when()
                .get("https://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void combiningTest(){
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places", hasSize(1))  // size ı 1 mi
                .body("places.state", hasItem("California")) // verilen path deki list bu item e sahip mi
                .body("places[0].'place name'", equalTo("Beverly Hills")) // verilen path deki değer buna eşit mi
        ;
    }

    @Test
    public void pathParamTest(){
        given()
                .pathParam("ulke", "us")
                .pathParam("postaKod", 90210)
                .log().uri() // request Link

                .when()
                .get("http://api.zippopotam.us/{ulke}/{postaKod}")

                .then()
                .statusCode(200)
                //.log().body()
        ;
    }

    @Test
    public void queryParamTest()
    {
        // https://gorest.co.in/public/v1/users?page=3

        given()
                .param("page",1)  // ?page=1  şeklinde linke ekleniyor
                .log().uri() // request Link

                .when()
                .get("https://gorest.co.in/public/v1/users")  // ?page=1

                .then()
                .statusCode(200)
                .log().body()
        ;
    }

    @Test
    public void queryParamTest2()
    {
        // https://gorest.co.in/public/v1/users?page=3
        // bu linkteki 1 den 10 kadar sayfaları çağırdığınızda response daki donen page degerlerinin
        // çağrılan page nosu ile aynı olup olmadığını kontrol ediniz.

        for (int i = 1; i < 10; i++) {
            given()
                    .param("page",i)  // ?page=1  şeklinde linke ekleniyor
                    .log().uri() // request Link

                    .when()
                    .get("https://gorest.co.in/public/v1/users")  // ?page=1

                    .then()
                    .statusCode(200)
                    .log().body()
                    .body("meta.pagination.page", equalTo(i))
            ;
        }

    }

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void Setup(){

        baseURI= "https://gorest.co.in/public/v1";

        requestSpec= new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec= new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void requestResponseSpecification()
    {
        // https://gorest.co.in/public/v1/users?page=3

        given()
                .param("page",1)  // ?page=1  şeklinde linke ekleniyor
                .spec(requestSpec)

                .when()
                .get("/users")  // ?page=1      // baseUri den ana linki alıyor peşine /user bölümünü ekliyor.

                .then()
                .spec(responseSpec)
                ;
        }

    @Test
    public void extractingJsonPath(){

        String countryName=
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().path("country")
                ;

        System.out.println("countryName = " + countryName);
        Assert.assertEquals(countryName,"United States");
    }

    @Test
    public void extractingJsonPath2() {
        //placeName
        String placeName=
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().path("places[0].'place name'")  //places[0]['place name'] de olurdu
                ;

        System.out.println("placeName = " + placeName);
        Assert.assertEquals(placeName, "Beverly Hills");

    }

    @Test
    public void extractingJsonPath3() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.
        int limit=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")


                        .then()
                        .log().body()
                        .extract().path("meta.pagination.limit")  // log.body ile çalıştırıp kodu gördükten sonra,
                                                                    // süslü parantezleri nokta kabul edip pathi bulduk.
                ;

        System.out.println("limit = " + limit);

    }

    @Test
    public void extractingJsonPath4() {
        // https://gorest.co.in/public/v1/users bütün id leri yazdırınız.

        List<Integer> idList=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")


                        .then()
                        .statusCode(200)
                        //.log().body()
                        .extract().path("data.id")
                ;

        System.out.println("idList = " + idList);
    }

    @Test
    public void extractingJsonPath5() {
        // https://gorest.co.in/public/v1/users bütün name leri yazdırınız.

        List<String> nameList=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")


                        .then()
                        .statusCode(200)
                        //.log().body()
                        .extract().path("data.name")
                ;

        System.out.println("nameList = " + nameList);
    }

    @Test
    public void extractingJsonResponseAll() {
        // https://gorest.co.in/public/v1/users bütün id leri yazdırınız.

        Response donenData=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")


                        .then()
                        .statusCode(200)
                        //.log().body()
                        .extract().response(); // dönen tüm datayı verir.

        List<Integer> idList= donenData.path("data.id");
        List<String> nameList= donenData.path("data.name");
        int limit= donenData.path("meta.pagination.limit");

        System.out.println("idList = " + idList);
        System.out.println("nameList = " + nameList);
        System.out.println("limit = " + limit);

        Assert.assertTrue(nameList.contains("Dakshayani Pandey"));
        Assert.assertTrue(idList.contains(1203769));
        Assert.assertEquals(limit, 10, "test sonucu hatalı");
    }

    @Test
    public void extractingJsonAll_POJO() {
        // POJO : JSON nesnesi : locationObject

        Location locationObject=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .extract().body().as(Location.class)
        
        ;

        System.out.println("locationObject.getCountry() = " + locationObject.getCountry());


        for (Place p : locationObject.getPlaces())
            System.out.println("p = " + p);

        System.out.println(locationObject.getPlaces().get(0).getPlacename());
        System.out.println(locationObject.getPlaces().get(0).getLongitude());
        System.out.println(locationObject.getPlaces().get(0).getState());
    }

    @Test
    public void extractPOJO_Soru(){
        // aşağıdaki endpointte(link) Dörtağaç Köyü ai diğer bilgileri yazdırınız.

        Location adana=
                 given()

                        .when()
                        .get("http://api.zippopotam.us/tr/01000")

                         .then()
                         //.log().body()
                         .statusCode(200)
                         .extract().body().as(Location.class)
                ;

        for (Place p : adana.getPlaces())
           if (p.getPlacename().equalsIgnoreCase("Dörtağaç Köyü")){
               System.out.println("p = " + p);
           }
    }

}
