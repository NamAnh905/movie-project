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
    // 1) Bỏ qua preflight
    if (req.method === 'OPTIONS') return next.handle(req);

    // 2) Lấy path an toàn
    let path = '';
    try {
      const url = req.url.startsWith('http')
        ? new URL(req.url)
        : new URL(req.url, location.origin);
      path = url.pathname || '';
    } catch {
      // Nếu không parse được URL thì cứ để request đi tiếp
      return next.handle(req);
    }

    // 3) Bỏ qua các endpoint public / auth / swagger
    const isAuth = path.startsWith('/api/auth/');
    const isSwagger = path.startsWith('/swagger-ui') || path.startsWith('/v3/api-docs');
    const isPublic = [
      /^\/api\/cinemas\/public(?:\/|$)/,
      /^\/api\/showtimes\/public(?:\/|$)/,
      /^\/api\/movies\/public(?:\/|$)/,   // nếu bạn có route này
      /^\/uploads(?:\/|$)/                // file tĩnh
    ].some(r => r.test(path));

    if (isAuth || isSwagger || isPublic) {
      return next.handle(req);
    }

    // 4) Gắn Bearer chỉ khi token hợp lệ và chưa hết hạn
    const raw = (localStorage.getItem('token') || '').trim();
    if (!raw || raw === 'null' || raw === 'undefined' || isJwtExpired(raw)) {
      // token rỗng/hết hạn -> không gắn header
      return next.handle(req);
    }

    const authReq = req.clone({ setHeaders: { Authorization: `Bearer ${raw}` } });
    return next.handle(authReq);
  }
}
