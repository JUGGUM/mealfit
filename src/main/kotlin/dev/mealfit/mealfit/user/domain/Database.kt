package dev.mealfit.mealfit.user.domain

import jdk.internal.vm.StackChunk.init
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom // 무작위 지연 시간을 위한 클래스

// (1) 계좌 정보를 담는 데이터 클래스 toString(), equals(), hashCode(). copy() 등을 자동으로 생성
data class Account(
    val id: Long,
    var balance: Long
)

@Component
// (2) 인메모리 데이터베이스 클래스
class Database {

    // (3) 계좌 정보를 저장하는 ConcurrentHashMap. 멀티스레드 환경에서 안전합니다.
    private val accounts: MutableMap<Long, Account> = ConcurrentHashMap()

    // (4) 초기 계좌 데이터 설정 (선택 사항)
    init {
        accounts[1L] = Account(1L, 100000L) // 초기 잔고 10만원
        accounts[2L] = Account(2L, 50000L)  // 초기 잔고 5만원
    }

    /**
     * (5) 잔고 조회 메서드
     * 인메모리 맵에서 계좌를 찾아 반환합니다.
     * 의도적으로 지연을 주어 경쟁 조건을 유발합니다.
     */
    fun balance(id: Long): Account {
        // 실제 데이터베이스 접근이나 네트워크 통신을 시뮬레이션하기 위한 인위적인 지연
        // 50ms ~ 150ms 사이의 무작위 지연
        Thread.sleep(ThreadLocalRandom.current().nextLong(50, 151))

        /***
         * 이 지연 때문에, 여러 스레드가 거의 동시에 balance를 호출하고 그 결과를 가지고 balance (잔고 업데이트)를
         * 다시 호출할 때, 데이터가 덮어씌워지거나 잘못 계산되는 경쟁 조건(Race Condition)이 발생할 수 있습니다.
         * 예를 들어, 잔고 1000원에서 A가 100원 입금 위해 1000을 읽고,
         * B도 동시에 1000을 읽고 각자 1100으로 업데이트하면, 최종 잔고가 1200원이 되어야 할 것이 1100원이 될 수 있습니다.
         */

        // 해당 ID의 계좌가 없으면 예외 발생 (혹은 기본 계좌 생성 등)
        return accounts[id] ?: throw IllegalArgumentException("Account with ID $id not found")
    }

    /**
     * (6) 잔고 업데이트 메서드 (오버로드)
     * 특정 계좌의 잔고를 업데이트하고, 업데이트된 계좌 객체를 반환합니다.
     * 이 메서드도 의도적으로 지연을 주어 경쟁 조건을 유발합니다.
     */
    fun balance(id: Long, newBalance: Long): Account {
        // 실제 데이터베이스 업데이트를 시뮬레이션하기 위한 인위적인 지연
        // 50ms ~ 150ms 사이의 무작위 지연
        Thread.sleep(ThreadLocalRandom.current().nextLong(50, 151))

        // (7) Map의 compute 함수를 사용하여 스레드 안전하게 업데이트
        // 이 람다 함수는 synchronized 블록 안에서 호출되므로, 외부 동기화(Controller의 synchronized)와 함께 사용되어야 합니다.
        val updatedAccount = accounts.compute(id) { _, existingAccount ->
            existingAccount?.apply {
                balance = newBalance // 기존 계좌 객체의 잔고 업데이트
            } ?: throw IllegalArgumentException("Account with ID $id not found for update")
        }
        return updatedAccount!! // null이 아님을 보장
    }

    // (옵션) 테스트용 초기화 메서드
    fun clearAccounts() {
        accounts.clear()
        init() // 초기 상태로 복원
    }
}