import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../../models/common.model';
import { Genre } from '../../models/genre.model';

@Injectable({ providedIn: 'root' })
export class GenreService {
  private base = `${environment.baseUrl}/genres`;

  constructor(private http: HttpClient) {}

  list(params?: { q?: string; page?: number; size?: number }): Observable<ApiResponse<PageResponse<Genre>>> {
    let p = new HttpParams();
    if (params?.q) p = p.set('q', params.q);
    if (params?.page != null) p = p.set('page', params.page);
    if (params?.size != null) p = p.set('size', params.size);
    return this.http.get<ApiResponse<PageResponse<Genre>>>(this.base, { params: p });
  }

  all(): Observable<ApiResponse<Genre[]>> {
    return this.http.get<ApiResponse<Genre[]>>(`${this.base}/all`);
  }

  getById(id: number): Observable<ApiResponse<Genre>> {
    return this.http.get<ApiResponse<Genre>>(`${this.base}/${id}`);
  }
}
