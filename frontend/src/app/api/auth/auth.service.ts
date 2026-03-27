import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { tap, catchError, map, switchMap, finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { AuthRequest, AuthResponse, RegisterRequest, UserResponse } from '../../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private authUrl = `${environment.baseUrl}${environment.api.auth}`;

  private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Tự động phục hồi trạng thái đăng nhập khi reload trang (F5)
    if (this.isLoggedIn()) {
      this.fetchProfile().subscribe();
    }
  }

  get currentUserValue(): UserResponse | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  isAdmin(): boolean {
    const user = this.currentUserValue;
    return user?.authorities?.includes('ROLE_ADMIN') ?? false;
  }

  login(request: AuthRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.authUrl}/login`, request).pipe(
      tap(res => {
        if (res.result) {
          localStorage.setItem('accessToken', res.result.accessToken);
          localStorage.setItem('refreshToken', res.result.refreshToken);
        }
      }),
      // Lấy thông tin user ngay sau khi lưu token thành công
      switchMap(res => this.fetchProfile().pipe(map(() => res)))
    );
  }

  // Khai báo kiểu trả về tường minh hơn thay vì any
  register(request: RegisterRequest): Observable<ApiResponse<{ id: number; username: string; email: string }>> {
    return this.http.post<ApiResponse<{ id: number; username: string; email: string }>>(`${this.authUrl}/register`, request);
  }

  fetchProfile(): Observable<UserResponse | null> {
    if (!this.isLoggedIn()) return of(null);

    return this.http.get<ApiResponse<UserResponse>>(`${this.authUrl}/me`).pipe(
      map(res => {
        const user = res.result || null;
        this.currentUserSubject.next(user);
        return user;
      }),
      catchError(() => {
        this.logoutLocal(); // Xóa local nếu token lỗi/hết hạn
        return of(null);
      })
    );
  }

  refreshToken(): Observable<ApiResponse<AuthResponse>> {
    const refreshStr = localStorage.getItem('refreshToken');
    if (!refreshStr) return throwError(() => new Error('No refresh token'));

    return this.http.post<ApiResponse<AuthResponse>>(`${this.authUrl}/refresh?token=${refreshStr}`, {}).pipe(
      tap(res => {
        if (res.result) {
          localStorage.setItem('accessToken', res.result.accessToken);
          localStorage.setItem('refreshToken', res.result.refreshToken);
        }
      })
    );
  }

  logout(): Observable<any> {
    const token = localStorage.getItem('accessToken');

    // Nếu không có token thì dọn dẹp nội bộ luôn
    if (!token) {
      this.logoutLocal();
      return of(null);
    }

    return this.http.post<ApiResponse<void>>(`${this.authUrl}/logout`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      catchError(() => of(null)), // API BE có sập thì FE vẫn phải cho user đăng xuất
      finalize(() => this.logoutLocal()) // Đảm bảo dọn dẹp localStorage vào phút chót
    );
  }

  logoutLocal(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.currentUserSubject.next(null);
  }
}
