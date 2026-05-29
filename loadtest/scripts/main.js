/**
 * MealFit 메인 부하 테스트
 *
 * 시나리오: 회원가입(setup) → 로그인 → 프로필 조회 → 식단 요청(구현 시 활성화)
 * 단계:     50 VU → 100 VU → 200 VU, 각 단계 1분 유지
 */
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import { signup, login, authHeaders, BASE_URL } from '../helpers/auth.js';

// ── 커스텀 메트릭 ──────────────────────────────────────────────────────────
const loginDuration        = new Trend('login_duration', true);         // histogram
const profileDuration      = new Trend('profile_request_duration', true);
const loginErrors          = new Rate('login_error_rate');
const totalErrors          = new Counter('error_count');

// ── 부하 단계 설정 ─────────────────────────────────────────────────────────
export const options = {
  stages: [
    { duration: '30s', target: 50  }, // ramp-up → 50 VU
    { duration: '1m',  target: 50  }, // 50 VU 유지
    { duration: '30s', target: 100 }, // ramp-up → 100 VU
    { duration: '1m',  target: 100 }, // 100 VU 유지
    { duration: '30s', target: 200 }, // ramp-up → 200 VU
    { duration: '1m',  target: 200 }, // 200 VU 유지
    { duration: '30s', target: 0   }, // ramp-down
  ],
  thresholds: {
    // 전체 P95 500ms 이하, P99 1000ms 이하
    http_req_duration:                    ['p(95)<500', 'p(99)<1000'],
    // 엔드포인트별 P95 500ms 이하
    'http_req_duration{name:login}':      ['p(95)<500'],
    'http_req_duration{name:user-profile}': ['p(95)<500'],
    // 에러율 1% 이하
    http_req_failed:                      ['rate<0.01'],
    login_error_rate:                     ['rate<0.01'],
  },
};

// ── 사전 준비: 테스트 계정 생성 (setup은 한 번만 실행) ──────────────────────
export function setup() {
  const totalUsers = 200; // 최대 VU 수 이상
  const users = [];

  console.log(`[setup] 테스트 계정 ${totalUsers}명 생성 중...`);

  for (let i = 0; i < totalUsers; i++) {
    const user = {
      username: `lt_user_${i}`,
      email:    `lt_${i}@mealfit.test`,
      password: 'LoadTest@1234',
    };
    const res = signup(user.username, user.email, user.password);
    // 200(생성), 400(이미 가입) 모두 사용 가능 계정으로 취급
    if (res.status === 200 || res.status === 201 || res.status === 400) {
      users.push(user);
    }
  }

  console.log(`[setup] 준비 완료: ${users.length}명`);
  return users;
}

// ── 메인 VU 함수 ──────────────────────────────────────────────────────────
export default function (users) {
  // VU 번호 기반으로 고정 계정 할당 (VU당 동일 계정 사용 → 로그인 부하 안정화)
  const userIndex = (__VU - 1) % users.length;
  const user = users[userIndex];

  // ── Step 1: 로그인 ───────────────────────────────────────────────────
  group('1. 로그인', () => {
    const t0 = Date.now();
    const token = login(user.email, user.password);
    loginDuration.add(Date.now() - t0);

    if (!token) {
      loginErrors.add(1);
      totalErrors.add(1);
      return; // 로그인 실패 시 이후 스텝 스킵
    }
    loginErrors.add(0);

    const headers = authHeaders(token);

    // ── Step 2: 프로필/유저 정보 조회 (인증 필요 엔드포인트) ──────────
    group('2. 프로필 조회', () => {
      const t1 = Date.now();
      const res = http.get(`${BASE_URL}/api/user/list`, {
        headers,
        tags: { name: 'user-profile' },
      });
      profileDuration.add(Date.now() - t1);

      const ok = check(res, { '프로필 조회 200': (r) => r.status === 200 });
      if (!ok) totalErrors.add(1);
    });

    // ── Step 3: 식단 추천 요청 ── 엔드포인트 구현 후 아래 주석 해제 ──
    // group('3. 식단 추천 요청', () => {
    //   const res = http.post(
    //     `${BASE_URL}/api/diet/recommend`,
    //     JSON.stringify({ /* surveyId, preferences, ... */ }),
    //     { headers, tags: { name: 'diet-recommend' } }
    //   );
    //   check(res, { '추천 요청 200': (r) => r.status === 200 });
    // });

    // ── Step 4: 식단 추천 결과 조회 ── 엔드포인트 구현 후 활성화 ───
    // group('4. 결과 조회', () => {
    //   const res = http.get(
    //     `${BASE_URL}/api/diet/result/latest`,
    //     { headers, tags: { name: 'diet-result' } }
    //   );
    //   check(res, { '결과 조회 200': (r) => r.status === 200 });
    // });
  });

  // 실제 사용자 행동 간격 시뮬레이션 (1~3초)
  sleep(1 + Math.random() * 2);
}

// ── 사후 정리 ──────────────────────────────────────────────────────────────
export function teardown() {
  console.log('[teardown] 테스트 완료.');
  console.log('  테스트 계정 정리:');
  console.log("  DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email LIKE 'lt_%@mealfit.test');");
  console.log("  DELETE FROM users WHERE email LIKE 'lt_%@mealfit.test';");
}
