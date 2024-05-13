package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import objectData.request.RequestAccount;
import objectData.response.ResponseAccountSucces;
import objectData.response.ResponseTokenSucces;
import org.codehaus.groovy.transform.SourceURIASTTransformation;
import org.testng.Assert;
import org.testng.annotations.Test;
import propertiesUtility.PropertiesUtility;

public class CreateAccountTest {

    public RequestAccount requestAccountBody;

    public String token;

    public String userID;

    @Test

    public void testMethod() {
        System.out.println("=== STEP 1: CREATE NEW ACCOUNT ===");
        createAccount();
        System.out.println("=== STEP 2: CREATE TOKEN ===");
        generateToken();
        System.out.println("=== STEP 3: CHECK ACCOUNT ===");
        checkAccPrecence();
        System.out.println("=== STEP 4: STERGEM USERUL ===");
        deleteUser();

    }

    public void createAccount (){
        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        //pregatim requestul

        PropertiesUtility propertiesUtility = new PropertiesUtility("Request/CreateAccountData");
        requestAccountBody = new RequestAccount(propertiesUtility.getAllData());

        //executam requestul
        requestSpecification.body(requestAccountBody);
        Response response = requestSpecification.post("Account/v1/User");

        //valiram raspuns
        System.out.println(response.getStatusLine());
        Assert.assertTrue(response.getStatusLine().contains("201"));
        Assert.assertTrue(response.getStatusLine().contains("Created"));

        ResponseAccountSucces responseAccountSucces = response.body().as(ResponseAccountSucces.class);
        //responseBody.prettyPrint();

        userID = responseAccountSucces.getUserId();

        Assert.assertTrue(responseAccountSucces.getUsername().equals(requestAccountBody.getUserName()));
        System.out.println(responseAccountSucces.getUserId());

    }

    public void generateToken (){
        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");


        //executam requestul
        requestSpecification.body(requestAccountBody);
        Response response = requestSpecification.post("/Account/v1/GenerateToken");
        System.out.println(response.getStatusLine());

        Assert.assertTrue(response.getStatusLine().contains("200"));
        Assert.assertTrue(response.getStatusLine().contains("OK"));

        ResponseTokenSucces responseTokenSucces = response.body().as(ResponseTokenSucces.class);
        token = responseTokenSucces.getToken();
        Assert.assertEquals(responseTokenSucces.getStatus(), "Success");
        Assert.assertEquals(responseTokenSucces.getResult(), "User authorized successfully.");

    }

    public void checkAccPrecence(){

        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        //ne autorizam pe baza de token
        requestSpecification.header("Authorization","Bearer" + token);

        //executam requestul
        Response response = requestSpecification.get("/Account/v1/User/" + userID);

        System.out.println(response.getStatusLine());

        Assert.assertTrue(response.getStatusLine().contains("200"));
        Assert.assertTrue(response.getStatusLine().contains("OK"));
    }

    public void deleteUser (){

        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        //ne autorizam pe baza de token
        requestSpecification.header("Authorization","Bearer" + token);

        //executam requestul
        Response response = requestSpecification.get("/Account/v1/User/" + userID);
        System.out.println(response.getStatusLine());

    }

}
