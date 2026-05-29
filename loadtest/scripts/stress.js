/**
 * MealFit 스트레스 테스트 — 한계 부하 탐색
 * 임계치를 넘으면 테스트 자동 중단 (abortOnFail)
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { signup, login, authHeaders, BASE_URL } from '../helpers/auth.js';

export const options = {
  stages: [
    { duration: '1m',  target: 100 }, // 0 → 100
    { duration: '2m',  target: 100 }, // 100 유지
    { duration: '1m',  target: 300 }, // 100 → 300
    { duration: '2m',  target: 300 }, // 300 유지
    { duration: '1m',  target: 500 }, // 300 → 500
    { duration: '2m',  target: 500 }, // 500 유지
    { duration: '1m',  target: 0   }, // ramp-down
  ],
  thresholds: {
    // 스트레스 테스트 — threshold 완화 (한계 탐색 목적)
    http_req_duration: [
      { threshold: 'p(95)<2000', abortOnFail: false },
      { threshold: 'p(99)<3000', abortOnFail: false },
    ],
    http_req_failed: [
      { threshold: 'rate<0.05', abortOnFail: true }, // 5% 에러 시 즉시 중단
    ],
  },
};

const TOTAL_USERS = 500;

export function setup() {
  const users = [];
  for (let i = 0; i < TOTAL_USERS; i++) {
    const user = { username: `stress_${i}`, email: `stress_${i}@mealfit.test`, password: 'Stress@1234' };
    const res = signup(user.username, user.email, user.password);
    if (res.status === 200 || res.status === 201 || res.status === 400) {
      users.push(user);
    }
  }
  return users;
}

export default function (users) {
  const user = users[(__VU - 1) % users.length];

  const token = login(user.email, user.password);
  if (!token) { sleep(1); return; }

  const res = http.get(`${BASE_URL}/api/user/list`, {
    headers: authHeaders(token),
    tags: { name: 'user-profile' },
  });
  check(res, { '프로필 조회 200': (r) => r.status === 200 });

  sleep(0.5 + Math.random());
}

export function teardown() {
  console.log('[teardown] 스트레스 테스트 완료.');
  console.log("  DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email LIKE 'stress_%@mealfit.test');");
  console.log("  DELETE FROM users WHERE email LIKE 'stress_%@mealfit.test';");
}
