// test/acceptance/user/SignUpAcceptanceTest.kt
class SignUpAcceptanceTest {

    @Test
    fun `회원가입 후 유저가 로그인할 수 있다`() {
        val signUpRequest = ...
        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(signUpRequest)
            .post("/api/v1/signup")

        assertEquals(response.statusCode, 200)
    }
}
