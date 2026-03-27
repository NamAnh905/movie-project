import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { Cinema } from '../../models/cinema.model';

@Injectable({ providedIn: 'root' })
export class AdminCinemaService {
  // Đổi thành /api/admin/cinemas nếu BE có set context-path=/api
  private base = `${environment.baseUrl}/admin/cinemas`;

  constructor(private http: HttpClient) {}

  create(data: { name: string; address: string; status: string }): Observable<ApiResponse<Cinema>> {
    return this.http.post<ApiResponse<Cinema>>(this.base, data);
  }

  update(id: number, data: { name: string; address: string; status: string }): Observable<ApiResponse<Cinema>> {
    return this.http.put<ApiResponse<Cinema>>(`${this.base}/${id}`, data);
  }

  remove(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/${id}`);
  }
}
