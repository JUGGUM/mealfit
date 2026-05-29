import http from 'k6/http';
import { check } from 'k6';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';

/**
 * 테스트 계정 생성. 이미 가입된 경우(400)는 그냥 통과.
 */
export function signup(username, email, password) {
  return http.post(
    `${BASE_URL}/api/auth/signup`,
    JSON.stringify({ username, email, password }),
    { headers: { 'Content-Type': 'application/json' }, tags: { name: 'signup' } }
  );
}

/**
 * 로그인 후 JWT 토큰 반환. 실패 시 null 반환.
 */
export function login(email, password) {
  const res = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ email, password }),
    { headers: { 'Content-Type': 'application/json' }, tags: { name: 'login' } }
  );

  const ok = check(res, {
    '로그인 200': (r) => r.status === 200,
    'JWT 토큰 포함': (r) => {
      try { return !!JSON.parse(r.body).token; } catch (_) { return false; }
    },
  });

  if (!ok) return null;
  try { return JSON.parse(res.body).token; } catch (_) { return null; }
}

/**
 * Authorization 헤더 포함 기본 헤더 반환
 */
export function authHeaders(token) {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}
