// src/app/features/admin/admin.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

function getRawToken(): string | null {
  let raw: string | null =
    localStorage.getItem('token') ||
    sessionStorage.getItem('token') ||
    null;
  if (!raw) return null;

  // TH lỡ lưu JSON {"token":"..."}
  if (raw.trim().startsWith('{')) {
    try {
      const obj = JSON.parse(raw);
      raw = (obj.token || obj.accessToken || obj.jwt || '') as string;
    } catch { /* ignore */ }
  }
  return raw || null;
}

function decodePayload(t: string | null): any | null {
  if (!t || t.split('.').length !== 3) return null;
  try {
    const b64url = t.split('.')[1];
    const b64 = b64url.replace(/-/g, '+').replace(/_/g, '/');
    const pad = b64.length % 4;
    const padded = b64 + (pad ? '='.repeat(4 - pad) : '');
    return JSON.parse(atob(padded));
  } catch { return null; }
}

function extractRoles(p: any): string[] {
  if (!p) return [];
  // authorities: ["ROLE_ADMIN"] OR [{authority:"ROLE_ADMIN"}] OR "ROLE_ADMIN ROLE_USER"
  if (Array.isArray(p.authorities)) {
    return typeof p.authorities[0] === 'string'
      ? p.authorities
      : p.authorities.map((a: any) => a?.authority).filter(Boolean);
  }
  if (typeof p.authorities === 'string') return p.authorities.split(/\s+/);
  if (Array.isArray(p.roles)) return p.roles;
  if (typeof p.role === 'string') return [p.role];
  if (typeof p.scope === 'string') return p.scope.split(/\s+/);
  return [];
}

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean | UrlTree {
    const token = getRawToken();
    if (!token) return this.router.parseUrl('/login');

    const payload = decodePayload(token);
    if (!payload) return this.router.parseUrl('/login');

    // Hết hạn
    if (payload.exp && Date.now()/1000 > payload.exp) {
      localStorage.removeItem('token');
      return this.router.parseUrl('/login');
    }

    // Chuẩn hoá roles
    let roles = extractRoles(payload).map(r => String(r || '').toUpperCase());
    // nếu chỉ có "ADMIN" thì coi như có "ROLE_ADMIN"
    if (roles.includes('ADMIN') && !roles.includes('ROLE_ADMIN')) {
      roles = [...roles, 'ROLE_ADMIN'];
    }

    // console.debug('[AdminGuard] roles=', roles);
    return roles.includes('ROLE_ADMIN') ? true : this.router.parseUrl('/');
  }
}
