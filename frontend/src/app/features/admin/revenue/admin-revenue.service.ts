import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

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
  private base = location.port==='4200'
    ? location.origin.replace(':4200', ':8080')
    : location.origin;

  constructor(private http: HttpClient) {}

  getOverview(opts: {
    from?: string; to?: string;
    cinemaId?: number; movieId?: number;
    onlyPaid?: boolean; groupBy?: 'DAY'|'MONTH';
  }) {
    let params = new HttpParams();
    if (opts.from)     params = params.set('from', opts.from);
    if (opts.to)       params = params.set('to', opts.to);
    if (opts.cinemaId) params = params.set('cinemaId', String(opts.cinemaId));
    if (opts.movieId)  params = params.set('movieId', String(opts.movieId));
    if (opts.onlyPaid) params = params.set('onlyPaid', 'true');
    params = params.set('groupBy', opts.groupBy ?? 'DAY');

    const token = localStorage.getItem('token') || '';
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;

    return this.http.get<{ success: boolean; data: RevenueOverview }>(
      `${this.base}/api/admin/revenue/overview`, { params, headers }
    );
  }
}
