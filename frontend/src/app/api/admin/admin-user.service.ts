import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../../models/common.model';
import { AdminUserResponse } from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private base = `${environment.baseUrl}/admin/users`;

  constructor(private http: HttpClient) {}

  list(params?: { q?: string; page?: number; size?: number }): Observable<ApiResponse<PageResponse<AdminUserResponse>>> {
    let p = new HttpParams();
    if (params?.q) p = p.set('q', params.q);
    if (params?.page != null) p = p.set('page', params.page);
    if (params?.size != null) p = p.set('size', params.size);
    return this.http.get<ApiResponse<PageResponse<AdminUserResponse>>>(this.base, { params: p });
  }

  updateRole(id: number, role: string): Observable<ApiResponse<AdminUserResponse>> {
    return this.http.put<ApiResponse<AdminUserResponse>>(`${this.base}/${id}/role`, { role });
  }
}
