import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../../models/common.model';
import { Cinema } from '../../models/cinema.model';

@Injectable({ providedIn: 'root' })
export class CinemaService {
  // Đổi thành /api/cinemas nếu BE của ông có set context-path=/api
  private base = `${environment.baseUrl}/cinemas`;

  constructor(private http: HttpClient) {}

  list(params?: { q?: string; page?: number; size?: number }): Observable<ApiResponse<PageResponse<Cinema>>> {
    let p = new HttpParams();
    if (params?.q) p = p.set('q', params.q);
    if (params?.page != null) p = p.set('page', params.page);
    if (params?.size != null) p = p.set('size', params.size);
    return this.http.get<ApiResponse<PageResponse<Cinema>>>(this.base, { params: p });
  }

  listPublic(): Observable<ApiResponse<Cinema[]>> {
    return this.http.get<ApiResponse<Cinema[]>>(`${this.base}/public`);
  }

  getById(id: number): Observable<ApiResponse<Cinema>> {
    return this.http.get<ApiResponse<Cinema>>(`${this.base}/${id}`);
  }
}
