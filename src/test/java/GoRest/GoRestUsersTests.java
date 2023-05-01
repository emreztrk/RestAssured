package GoRest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.client.methods.RequestBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {
    Faker randomProducer=new Faker();
    int userID;

    RequestSpecification requestSpec;

    @BeforeClass
    public void setup(){
        baseURI = "https://gorest.co.in/public/v2/users";  // baseUri RequestSpecification den önce tanımlanmalı

        requestSpec=new RequestSpecBuilder()
                .addHeader("Authorization","Bearer 0140835f288d434b61fd0f1bb5befd1267162ce7908f79275b0910383b1236bc")
                .setContentType(ContentType.JSON)
                .setBaseUri(baseURI)
                .build();

    }

    @Test(enabled = false)
    public void createUserJSON(){
        // POST : https://gorest.co.in/public/v2/users
        // Authorization : Bearer 0140835f288d434b61fd0f1bb5befd1267162ce7908f79275b0910383b1236bc
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}
        String rndFullname=randomProducer.name().fullName();
        String rndEmail=randomProducer.internet().emailAddress();

        userID=
                given()
                        .spec(requestSpec)
                        .body("{\"name\":\""+rndFullname+"\", \"gender\":\"male\", \"email\":\""+rndEmail+"\", \"status\":\"active\"}")
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
                ;
    }

    @Test
    public void createUserMap(){ // 2. YÖNTEM

        System.out.println("baseURI = " + baseURI);

        String rndFullname=randomProducer.name().fullName();
        String rndEmail=randomProducer.internet().emailAddress();

        Map<String,String > newUser=new HashMap<>();
        newUser.put("name", rndFullname);
        newUser.put("gender", "male");
        newUser.put("email", rndEmail);
        newUser.put("status", "active");

        userID=
                given()
                        .spec(requestSpec)
                        .body(newUser)
                        .log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
    }
    @Test(enabled = false)
    public void createUserClass(){ // 3. YÖNTEM
        String rndFullname=randomProducer.name().fullName();
        String rndEmail=randomProducer.internet().emailAddress();

        User newUser=new User();
        newUser.name=rndFullname;
        newUser.gender="male";
        newUser.email=rndEmail;
        newUser.status="active";

        userID=
                given()
                        .spec(requestSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createUserMap")
    public void getUserByID(){

        given()
                .spec(requestSpec)

                .when()
                .get(""+userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userID))

                ;
    }

    @Test(dependsOnMethods ="getUserByID")
    public void updateUser(){

        Map<String, String> updateUser=new HashMap<>();
        updateUser.put("name", "John Marston");

        given()
                .spec(requestSpec)
                .body(updateUser)

                .when()
                .put(""+userID)

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
                .body("name", equalTo("John Marston"))
                ;
    }

    @Test(dependsOnMethods = "updateUser")
    public void deleteUser(){

        given()

                .spec(requestSpec)
                .when()
                .delete(""+userID)

                .then()
                .log().body()
                .statusCode(204)

                ;

    }

    @Test(dependsOnMethods = "deleteUser")
    public void deleteUserNegative(){

        given()
                .spec(requestSpec)
                .when()
                .delete(""+userID)

                .then()
                .log().all()
                .statusCode(404)
                ;

    }
}
