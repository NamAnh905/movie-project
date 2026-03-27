import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../../models/common.model';
import { Movie } from '../../models/movie.model';

@Injectable({ providedIn: 'root' })
export class MovieService {
  // Thêm /api nếu BE có cấu hình context-path
  private base = `${environment.baseUrl}/movies`;

  constructor(private http: HttpClient) {}

  list(params?: { q?: string; genreId?: number; status?: string; page?: number; size?: number }): Observable<ApiResponse<PageResponse<Movie>>> {
    let p = new HttpParams();
    if (params?.q) p = p.set('q', params.q);
    if (params?.genreId) p = p.set('genreId', params.genreId.toString());
    if (params?.status) p = p.set('status', params.status);
    if (params?.page != null) p = p.set('page', params.page);
    if (params?.size != null) p = p.set('size', params.size);

    return this.http.get<ApiResponse<PageResponse<Movie>>>(this.base, { params: p });
  }

  getById(id: number): Observable<ApiResponse<Movie>> {
    return this.http.get<ApiResponse<Movie>>(`${this.base}/${id}`);
  }

  listAllByStatus(status: string): Observable<ApiResponse<Movie[]>> {
    return this.http.get<ApiResponse<Movie[]>>(`${this.base}/status/${status}/all`);
  }
}
