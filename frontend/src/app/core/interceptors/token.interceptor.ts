import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

function isJwtExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1] || ''));
    if (!payload?.exp) return false;
    return Date.now() / 1000 >= payload.exp;
  } catch { return false; }
}

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.method === 'OPTIONS') return next.handle(req);

    let path = '';
    try {
      const url = req.url.startsWith('http') ? new URL(req.url) : new URL(req.url, location.origin);
      path = url.pathname || '';
    } catch { return next.handle(req); }

    // SỬA Ở ĐÂY: Chỉ lọc các API public không cần token (login, register, refresh)
    const isAuthPublic = [
      /^\/api\/auth\/login(?:\/|$)/,
      /^\/api\/auth\/register(?:\/|$)/,
      /^\/api\/auth\/refresh(?:\/|$)/
    ].some(r => r.test(path));

    const isSwagger = path.startsWith('/swagger-ui') || path.startsWith('/v3/api-docs');
    const isPublic = [
      /^\/api\/cinemas\/public(?:\/|$)/,
      /^\/api\/showtimes\/public(?:\/|$)/,
      /^\/api\/movies(?:\/|$)/,
      /^\/api\/genres(?:\/|$)/,
      /^\/uploads(?:\/|$)/
    ].some(r => r.test(path));

    // Dùng isAuthPublic thay vì isAuth cũ (cái cũ chặn sạch /api/auth/)
    if (isAuthPublic || isSwagger || isPublic) {
      return next.handle(req);
    }

    // Đổi 'token' thành 'accessToken'
    const raw = (localStorage.getItem('accessToken') || '').trim();

    // Tạm thời bỏ isJwtExpired(raw) ở đây, cứ gửi token lên để Backend tự soi và báo lỗi 401 nếu hết hạn
    if (!raw || raw === 'null' || raw === 'undefined') {
      return next.handle(req);
    }

    const authReq = req.clone({ setHeaders: { Authorization: `Bearer ${raw}` } });
    return next.handle(authReq);
  }
}
