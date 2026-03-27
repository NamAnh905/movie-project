import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { Genre, AdminGenreRequest } from '../../models/genre.model';

@Injectable({ providedIn: 'root' })
export class AdminGenreService {
  private base = `${environment.baseUrl}/admin/genres`;

  constructor(private http: HttpClient) {}

  create(data: AdminGenreRequest): Observable<ApiResponse<Genre>> {
    return this.http.post<ApiResponse<Genre>>(this.base, data);
  }

  update(id: number, data: AdminGenreRequest): Observable<ApiResponse<Genre>> {
    return this.http.put<ApiResponse<Genre>>(`${this.base}/${id}`, data);
  }

  remove(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/${id}`);
  }
}
