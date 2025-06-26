package dev.mealfit.mealfit.user.presentation

import dev.mealfit.mealfit.user.domain.Account
import dev.mealfit.mealfit.user.domain.Database
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap

@RestController // 이 클래스가 REST API 컨트롤러임을 나타냅니다.
@RequestMapping("/account") // 이 컨트롤러의 모든 엔드포인트 URL은 "/account"로 시작합니다.
class BalanceController(
    private val db: Database // (1) Database 의존성 주입 (생성자 주입)
) {

    // synchronized 블록을 사용하여 동시성 문제를 해결하기 위한 잠금객체들

    // (2) 계좌별 락 객체를 관리하는 맵. 동시성 문제를 해결하기 위해 사용됩니다.
    // ConcurrentHashMap 은 멀티스레드 환경에서 안전하게 사용할 수 있습니다. 여러 스레드가 동시에 접근해도 안전하도록 설계
    private val accountLocks: MutableMap<Long, Any> = ConcurrentHashMap()

    // (3) 입금이 동시에 중복 처리되는 것을 막기 위한 맵.
    // 특정 계좌에 대한 입금 처리 중 여부를 나타냅니다.
    private val depositing: MutableMap<Long, Boolean> = ConcurrentHashMap()


    // (4) 계정별 락 객체를 얻거나 새로 생성하는 헬퍼 함수
    private fun getLockForAccount(id: Long): Any {
        // computeIfAbsent는 id에 해당하는 락 객체가 없으면 새로 생성하여 맵에 추가하고 반환하며,
        // 이미 존재하면 기존 객체를 반환합니다. 이 작업 자체도 스레드 안전하게 처리됩니다.
        return accountLocks.computeIfAbsent(id) { Object() }
    }

    /**
     * (5) 잔고 조회 API
     * GET 요청: /account/{id}/balance
     * {id}는 조회할 계좌의 ID입니다.
     */
    @GetMapping("{id}/balance")
    fun balance(@PathVariable id: Long): Account {
        // Database 객체의 balance 메서드를 호출하여 계좌 잔고를 조회하고 반환합니다.
        return db.balance(id)
    }

    /**
     * (6) 입금 API - 동시 입금 요청 시 실패 처리
     * POST 요청: /account/{id}/deposit
     * 요청 본문(RequestBody)으로 입금액(amount)을 받습니다.
     */
    @PostMapping("{id}/deposit")
    fun deposit(@PathVariable id: Long, @RequestBody amount: Long): ResponseEntity<*> {
        // (6-1) 입금액이 0 이하인 경우 잘못된 요청으로 처리합니다.
        if (amount <= 0) {
            return ResponseEntity.badRequest() // HTTP 400 Bad Request 상태 코드를 설정합니다.
                .body(mapOf("error" to "입금액은 0보다 커야 합니다.")) // 오류 메시지를 본문에 담아 보냅니다.
        }

        // (6-2) 해당 계좌 ID에 대한 락 객체를 가져옵니다.
        val lock = getLockForAccount(id)

        // (6-3) 동시성 제어: synchronized 블록
        // synchronized 블록은 lock 객체에 대한 락을 획득한 스레드만 내부 코드를 실행할 수 있도록 보장합니다.
        // 다른 스레드는 락이 해제될 때까지 대기합니다.
        synchronized(lock) {
            // (6-4) 중복 입금 요청 방지: depositing 맵 확인
            // putIfAbsent는 키가 이미 존재하면 값을 넣지 않고 기존 값을 반환하며,
            // 키가 없으면 값을 넣고 null을 반환합니다.
            // 이미 'true'가 반환되면 다른 스레드가 이미 입금 처리 중이라는 의미입니다.
            if (depositing.putIfAbsent(id, true) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT) // HTTP 409 Conflict 상태 코드를 설정합니다.
                    .body(mapOf("error" to "이미 입금 처리 중입니다. 잠시 후 다시 시도하세요.")) // 오류 메시지
            }

            try {
                // (6-5) 현재 계좌 잔고 조회
                val currentAccount = db.balance(id)
                // (6-6) 새로운 잔고 계산
                val newBalance = currentAccount.balance + amount
                // (6-7) 데이터베이스에 업데이트된 잔고 저장
                val updatedAccount = db.balance(id, newBalance)
                // (6-8) 성공 응답 반환
                return ResponseEntity.ok(updatedAccount) // HTTP 200 OK 상태 코드와 업데이트된 계좌 정보 반환
            } finally {
                // (6-9) 입금 처리 완료 후 depositing 맵에서 해당 계좌 ID를 제거합니다.
                // 이는 다른 입금 요청이 다시 들어올 수 있도록 상태를 초기화하는 것입니다.
                depositing.remove(id)
            }
        }
    }

    /**
     * (7) 출금 API - 잔액 부족 시 실패, 동시 출금 요청은 순차 처리
     * POST 요청: /account/{id}/withdraw
     * 요청 본문(RequestBody)으로 출금액(amount)을 받습니다.
     */
    @PostMapping("{id}/withdraw")
    fun withdraw(@PathVariable id: Long, @RequestBody amount: Long): ResponseEntity<*> {
        // (7-1) 출금액이 0 이하인 경우 잘못된 요청으로 처리합니다.
        if (amount <= 0) {
            return ResponseEntity.badRequest().body(mapOf("error" to "출금액은 0보다 커야 합니다."))
        }

        // (7-2) 해당 계좌 ID에 대한 락 객체를 가져옵니다.
        val lock = getLockForAccount(id)

        // (7-3) 동시성 제어: synchronized 블록
        // 입금과 마찬가지로, 출금 작업도 해당 계좌에 대해 한 번에 하나의 스레드만 실행되도록 보장합니다.
        synchronized(lock) {
            // (7-4) 현재 계좌 잔고 조회
            val currentAccount = db.balance(id)

            // (7-5) 잔액 부족 여부 확인
            if (currentAccount.balance < amount) {
                return ResponseEntity.badRequest() // HTTP 400 Bad Request
                    .body(
                        mapOf(
                            "error" to "잔액이 부족합니다.",
                            "currentBalance" to currentAccount.balance,
                            "requestedAmount" to amount
                        )
                    )
            }

            // (7-6) 새로운 잔고 계산
            val newBalance = currentAccount.balance - amount
            // (7-7) 데이터베이스에 업데이트된 잔고 저장
            val updatedAccount = db.balance(id, newBalance)
            // (7-8) 성공 응답 반환
            return ResponseEntity.ok(updatedAccount) // HTTP 200 OK
        }
    }
}