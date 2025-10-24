// src/app/core/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface AuthResponse {
  token?: string;
  accessToken?: string;
  jwt?: string;
  data?: { token?: string; accessToken?: string; jwt?: string };
  [k: string]: any;
}
export interface LoginPayload { username?: string; email?: string; password: string; }
export interface RegisterPayload { username: string; password: string; email?: string; }
export interface JwtPayload {
  role?: string;
  roles?: string | string[];
  scope?: string;
  scopes?: string | string[];
  authorities?: Array<string | { authority: string }>;
  auth?: Array<string | { authority: string }>;
  [k: string]: any;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly key = 'token';
  private readonly baseUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  /** Đăng nhập: bắt mọi tên token thường gặp và lưu vào localStorage */
  login(body: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, body).pipe(
      map(res => {
        const token =
          res?.token ??
          res?.accessToken ??
          res?.jwt ??
          res?.data?.token ??
          res?.data?.accessToken ??
          res?.data?.jwt;
        if (!token) throw new Error('Missing token in login response');
        this.setToken(token);
        return res;
      })
    );
  }

  register(body: RegisterPayload): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/register`, body);
  }

  // ========== Token helpers ==========
  get token(): string | null { return localStorage.getItem(this.key); }
  private setToken(token: string): void { localStorage.setItem(this.key, token); }
  logout(): void { localStorage.removeItem(this.key); }
  isLoggedIn(): boolean { return !!this.token; }

  // ========== Decode & role checks ==========
  private decode(): JwtPayload | null {
    const t = this.token;
    if (!t) return null;
    const parts = t.split('.');
    if (parts.length < 2) return null;
    try {
      const b64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(b64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
      );
      return JSON.parse(json) as JwtPayload;
    } catch { return null; }
  }

  private allRolesUpper(): Set<string> {
    const p = this.decode();
    const set = new Set<string>();
    const add = (v: any) => {
      if (!v) return;
      if (Array.isArray(v)) { v.forEach(add); return; }
      if (typeof v === 'string') { v.split(/[,\s]+/).forEach(s => s && set.add(s.toUpperCase())); return; }
      if (typeof v === 'object' && 'authority' in v) set.add(String((v as any).authority || '').toUpperCase());
    };
    if (!p) return set;
    add(p.role); add(p.roles); add(p.scope); add(p.scopes); add(p.authorities); add(p.auth);
    return set;
  }

  hasRole(role: string): boolean {
    const up = role.toUpperCase();
    const set = this.allRolesUpper();
    return set.has(up) || set.has(`ROLE_${up}`);
  }

  isAdmin(): boolean { return this.hasRole('ADMIN'); }
}
