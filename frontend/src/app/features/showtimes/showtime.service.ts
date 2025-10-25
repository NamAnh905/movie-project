import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, forkJoin, map } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface MovieBrief {
  id: number; title: string; duration?: number | null;
}
export interface CinemaBrief {
  id: number; name: string;
}
export interface ShowtimeItem {
  id: number;
  movieId: number;
  cinemaId: number;
  startTime: string;
  endTime?: string | null;
  status?: string | null;
  movie?: MovieBrief | null;
  cinema?: CinemaBrief | null;
}

export interface ShowtimeByMovieDTO {
  cinemaId: number;
  cinemaName: string;
  times: string[]; // "HH:mm"
}

export interface ShowtimeCreate {
  movieId: number;
  cinemaId: number;
  startTime: string; // 'yyyy-MM-ddTHH:mm:ss'
  start_time?: string;
}

@Injectable({ providedIn: 'root' })
export class ShowtimeService {
  private http = inject(HttpClient);
  private BASE = environment.baseUrl;
  private API = `${this.BASE}/api`;

  private normalize = (x: any): ShowtimeItem => {
    const start = x?.startTime ?? x?.start_time;
    const movie = x?.movie ? {
      id: x.movie.id,
      title: x.movie.title,
      duration: x.movie.duration ?? null
    } : null;
    const cinema = x?.cinema ? { id: x.cinema.id, name: x.cinema.name } : null;
    return {
      id: x.id,
      movieId: x.movieId ?? x.movie_id ?? movie?.id ?? 0,
      cinemaId: x.cinemaId ?? x.cinema_id ?? cinema?.id ?? 0,
      startTime: start,
      endTime: x?.endTime ?? x?.end_time ?? null,
      status: x?.status ?? null,
      movie, cinema
    };
  };

  /** Admin list (có phân trang) */
  list(params?: { date?: string; movieId?: number; cinemaId?: number; page?: number; size?: number }): Observable<ShowtimeItem[]> {
    let p = new HttpParams();
    if (params?.date)     p = p.set('date', params.date);
    if (params?.movieId)  p = p.set('movieId', String(params.movieId));
    if (params?.cinemaId) p = p.set('cinemaId', String(params.cinemaId));
    if (params?.page != null) p = p.set('page', String(params.page));
    if (params?.size != null) p = p.set('size', String(params.size));

    return this.http.get<any>(`${this.API}/showtimes`, { params: p }).pipe(
      map(res => (res?.content ?? res ?? []) as any[]),
      map(arr => arr.map(this.normalize))
    );
  }

  /** Public theo phim: /showtimes/public/by-movie */
  getPublicByMovie(movieId: number, dateISO: string) {
    const params = new HttpParams().set('movieId', String(movieId)).set('date', dateISO);
    return this.http.get<ShowtimeByMovieDTO[]>(`${this.API}/showtimes/public/by-movie`, { params });
  }

  /** Public generic: /showtimes/public */
  getPublic(params: { movieId?: number; cinemaId?: number; date?: string }) {
    let p = new HttpParams();
    if (params.movieId)  p = p.set('movieId', String(params.movieId));
    if (params.cinemaId) p = p.set('cinemaId', String(params.cinemaId));
    if (params.date)     p = p.set('date', params.date);
    return this.http.get<any[]>(`${this.API}/showtimes/public`, { params: p });
  }

  /** Xóa 1 lịch chiếu */
  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/showtimes/${id}`);
  }

  /** Tạo nhiều lịch chiếu (POST từng cái) */
  createMany(list: ShowtimeCreate[]): Observable<any[]> {
    if (!list?.length) return new Observable(sub => { sub.next([]); sub.complete(); });
    const reqs = list.map(x => this.http.post(`${this.API}/showtimes`, { ...x, start_time: x.startTime }));
    return forkJoin(reqs).pipe(map(res => res || []));
  }

  /** Tạo 1 suất */
  create(data: {
    movieId?: number | null;
    cinemaId?: number | null;
    startTime?: string | null;
    start_time?: string | null;
    price?: number | null;
  }) {
    const body = { ...data, start_time: data.start_time ?? data.startTime ?? null };
    return this.http.post(`${this.API}/showtimes`, body);
  }

  /** Cập nhật 1 suất */
  update(id: number, data: {
    movieId?: number | null;
    cinemaId?: number | null;
    startTime?: string | null;
    start_time?: string | null;
    price?: number | null;
  }) {
    const body = { ...data, start_time: data.start_time ?? data.startTime ?? null };
    return this.http.put(`${this.API}/showtimes/${id}`, body);
  }
}
