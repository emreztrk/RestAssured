import Model.ToDo;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class Tasks {

    /** Task 1

     create a request to https://jsonplaceholder.typicode.com/todos/2
     expect status 200
     Converting Into POJO*/

    @Test
    public void task2(){

        ToDo toDo=

        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .log().body()
                .statusCode(200)
                .extract().body().as(ToDo.class);

        System.out.println("toDo = " + toDo);
        System.out.println("toDo.getTitle() = " + toDo.getTitle());
    }

    @Test
    public void task3(){
        /**
         Task 3
         create a request to https://jsonplaceholder.typicode.com/todos/2
         expect status 200
         expect content type JSON
         expect title in response body to be "quis ut nam facilis et officia qui"
         */

        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", equalTo("quis ut nam facilis et officia qui"))
                ;
    }

    @Test
    public void task4(){
        /**
         Task 4
         create a request to https://jsonplaceholder.typicode.com/todos/2
         expect status 200
         expect content type JSON
         expect response completed status to be false
         extract completed field and testNG assertion
         */
        //1. Yöntem hamcrest

                given()

                        .when()
                        .get("https://jsonplaceholder.typicode.com/todos/2")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("completed", equalTo(false));

        //2. Yöntem TestNG
        Boolean completed=
        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("completed", equalTo(false))
                .extract().path("completed")
                ;

        Assert.assertFalse(completed);
        ;

    }
}
