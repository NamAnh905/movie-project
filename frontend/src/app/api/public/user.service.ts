import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { UserResponse } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private base = `${environment.baseUrl}/users`;

  constructor(private http: HttpClient) {}

  getMe(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.base}/me`);
  }

  updateMe(data: { fullName?: string; email?: string }): Observable<ApiResponse<UserResponse>> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.base}/me`, data);
  }
}
