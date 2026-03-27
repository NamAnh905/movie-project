import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';

export interface RevenueSummary { revenue: number; tickets: number; bookings: number; }
export interface RevenuePoint { period: string; revenue: number; tickets: number; bookings: number; }
export interface RevenueByCinema { cinemaId: number; cinemaName: string; revenue: number; tickets: number; bookings: number; }
export interface RevenueByMovie  { movieId: number; movieTitle: string; revenue: number; tickets: number; bookings: number; }

export interface RevenueOverview {
  summary: RevenueSummary;
  series: RevenuePoint[];
  byCinema: RevenueByCinema[];
  byMovie: RevenueByMovie[];
}

@Injectable({ providedIn: 'root' })
export class AdminRevenueService {
  private base = `${environment.baseUrl}/admin/revenue`;

  constructor(private http: HttpClient) {}

  getOverview(opts: {
    from?: string; to?: string;
    cinemaId?: number; movieId?: number;
    onlyPaid?: boolean; groupBy?: 'DAY'|'MONTH';
  }): Observable<ApiResponse<RevenueOverview>> {
    let params = new HttpParams();
    if (opts.from)     params = params.set('from', opts.from);
    if (opts.to)       params = params.set('to', opts.to);
    if (opts.cinemaId) params = params.set('cinemaId', String(opts.cinemaId));
    if (opts.movieId)  params = params.set('movieId', String(opts.movieId));
    if (opts.onlyPaid !== undefined) params = params.set('onlyPaid', String(opts.onlyPaid));
    params = params.set('groupBy', opts.groupBy ?? 'DAY');

    return this.http.get<ApiResponse<RevenueOverview>>(`${this.base}/overview`, { params });
  }
}
