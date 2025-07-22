package dev.mealfit.mealfit.core // 실제 프로젝트의 common 패키지 경로로 변경하세요.

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * BaseEntity는 JPA Auditing 기능을 활용하여
 * 모든 엔티티의 공통 감사(Audit) 필드를 관리하는 추상 클래스입니다.
 *
 * 이 클래스를 상속받는 모든 엔티티는 자동으로 생성일시, 수정일시,
 * 생성자, 수정자 필드를 가지게 되며, 이 필드들은 Spring Data JPA에 의해 자동으로 채워집니다.
 *
 * @MappedSuperclass: 이 클래스는 데이터베이스 테이블과 직접 매핑되지 않으며,
 * 자식 엔티티 클래스들에게 매핑 정보를 상속하기 위한 것입니다.
 * @EntityListeners(AuditingEntityListener::class): Spring Data JPA의 Auditing 기능을 활성화합니다.
 * 엔티티의 생명주기 이벤트를 감지하여 필드를 자동 업데이트합니다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    /**
     * 엔티티가 처음 생성될 때의 일시를 기록합니다.
     * @CreatedDate: Spring Data JPA Auditing에 의해 자동으로 현재 시간으로 설정됩니다.
     * @Column(updatable = false): 이 컬럼은 한 번 생성되면 업데이트되지 않도록 설정합니다.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now() // 기본값으로 현재 시간을 설정 (Auditing이 덮어씀)

    /**
     * 엔티티가 마지막으로 수정된 일시를 기록합니다.
     * @LastModifiedDate: Spring Data JPA Auditing에 의해 엔티티가 업데이트될 때마다 현재 시간으로 설정됩니다.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now() // 기본값으로 현재 시간을 설정 (Auditing이 덮어씀)

    /**
     * 엔티티를 생성한 사용자의 식별자(ID 또는 이름)를 기록합니다.
     * @CreatedBy: Spring Data JPA Auditing과 AuditorAware<String> 빈에 의해 자동으로 설정됩니다.
     * @Column(updatable = false): 이 컬럼은 한 번 생성되면 업데이트되지 않도록 설정합니다.
     *
     * 중요: User 엔티티 타입 대신 String 또는 Long 타입으로 지정하여 순환 참조를 방지합니다.
     * 여기서는 사용자 이름(username)을 String으로 저장하는 것을 가정합니다.
     */
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    var createdBy: String = "anonymous" // 기본값 설정 (AuditorAware에서 실제 사용자 ID를 제공)

    /**
     * 엔티티를 마지막으로 수정한 사용자의 식별자(ID 또는 이름)를 기록합니다.
     * @LastModifiedBy: Spring Data JPA Auditing과 AuditorAware<String> 빈에 의해 자동으로 설정됩니다.
     *
     * 중요: User 엔티티 타입 대신 String 또는 Long 타입으로 지정하여 순환 참조를 방지합니다.
     * 여기서는 사용자 이름(username)을 String으로 저장하는 것을 가정합니다.
     */
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    var updatedBy: String = "anonymous" // 기본값 설정 (AuditorAware에서 실제 사용자 ID를 제공)

    // Auditing 필드에 대한 Getter는 필요에 따라 추가하거나, Kotlin의 기본 속성 접근자를 사용합니다.
    // (Kotlin에서는 var/val 속성 자체가 getter/setter를 제공하므로 명시적인 getter는 필수는 아님)
}
