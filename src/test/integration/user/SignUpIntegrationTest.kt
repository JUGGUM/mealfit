// test/integration/user/SignUpIntegrationTest.kt
@SpringBootTest
@AutoConfigureMockMvc
class SignUpIntegrationTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `회원가입 API 통합 테스트`() {
        val json = """{ "email": "test@example.com", ... }"""

        mockMvc.perform(post("/api/v1/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
    }
}
