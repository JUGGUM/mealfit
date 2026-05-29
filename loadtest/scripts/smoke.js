/**
 * MealFit 스모크 테스트 — 배포 후 빠른 기능 검증
 * VU: 5명, 시간: 1분
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { signup, login, authHeaders, BASE_URL } from '../helpers/auth.js';

export const options = {
  vus: 5,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed:   ['rate<0.01'],
  },
};

export function setup() {
  const users = [];
  for (let i = 0; i < 5; i++) {
    const user = { username: `smoke_${i}`, email: `smoke_${i}@mealfit.test`, password: 'Smoke@1234' };
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
  if (!token) return;

  const res = http.get(`${BASE_URL}/api/user/list`, {
    headers: authHeaders(token),
    tags: { name: 'user-profile' },
  });

  check(res, { '프로필 조회 200': (r) => r.status === 200 });
  sleep(1);
}
