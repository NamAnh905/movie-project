import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { Movie, AdminMovieRequest } from '../../models/movie.model';

@Injectable({ providedIn: 'root' })
export class AdminMovieService {
  private base = `${environment.baseUrl}/admin/movies`;

  constructor(private http: HttpClient) {}

  create(data: AdminMovieRequest): Observable<ApiResponse<Movie>> {
    return this.http.post<ApiResponse<Movie>>(this.base, data);
  }

  update(id: number, data: AdminMovieRequest): Observable<ApiResponse<Movie>> {
    return this.http.put<ApiResponse<Movie>>(`${this.base}/${id}`, data);
  }

  remove(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/${id}`);
  }

  // Lưu ý: BE của ông chưa thấy API upload ảnh. Giả định gọi vào endpoint này:
  uploadPoster(formData: FormData): Observable<ApiResponse<{url: string}>> {
    return this.http.post<ApiResponse<{url: string}>>(`${environment.baseUrl}/admin/files/upload`, formData);
  }
}
