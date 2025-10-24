import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Chuẩn hoá URL để nhận ra cả '/api/auth/..' và 'http://localhost:8080/api/auth/..'
    const url = req.url.startsWith('http') ? new URL(req.url) : new URL(req.url, location.origin);
    const path = url.pathname;

    // Bỏ qua các endpoint auth + swagger
    const skip =
      path.startsWith('/api/auth/') ||
      path.startsWith('/swagger-ui') ||
      path.startsWith('/v3/api-docs');

    if (skip) return next.handle(req);

    const token = localStorage.getItem('token');
    if (!token) return next.handle(req);

    return next.handle(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
  }
}
