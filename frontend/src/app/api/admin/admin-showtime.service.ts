import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../../models/common.model';
import { ShowtimeResponse, AdminShowtimeRequest } from '../../models/showtime.model';

@Injectable({ providedIn: 'root' })
export class AdminShowtimeService {
  private base = `${environment.baseUrl}/admin/showtimes`;

  constructor(private http: HttpClient) {}

  search(params: { movieId?: number; cinemaId?: number; state?: string; date?: string; page?: number; size?: number }): Observable<ApiResponse<PageResponse<ShowtimeResponse>>> {
    let p = new HttpParams();
    if (params.movieId) p = p.set('movieId', params.movieId);
    if (params.cinemaId) p = p.set('cinemaId', params.cinemaId);
    if (params.state) p = p.set('state', params.state);
    if (params.date) p = p.set('date', params.date);
    if (params.page != null) p = p.set('page', params.page);
    if (params.size != null) p = p.set('size', params.size);
    return this.http.get<ApiResponse<PageResponse<ShowtimeResponse>>>(this.base, { params: p });
  }

  createBatch(data: AdminShowtimeRequest): Observable<ApiResponse<{ createdIds: number[], skippedTimes: string[] }>> {
    return this.http.post<ApiResponse<any>>(`${this.base}/batch`, data);
  }

  remove(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/${id}`);
  }
}
