import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Cinema {
  id: number;
  name: string;
  address?: string | null;
  status?: string | null;
}

export interface Movie {
  id: number;
  title: string;
  duration?: number | null;
  age_rating?: string | null;
  poster_url?: string | null;
  status?: string | null;
}

export interface Showtime {
  id: number;
  movie_id: number;
  cinema_id: number;
  start_time: string;          // ISO string từ BE
  end_time?: string | null;
  price?: number | null;
  status?: string | null;
  movie?: Movie;               // BE public có thể embed movie
}

/** Payload tạo/cập nhật rạp: cho phép null để khớp form Angular (string|null). */
export type CinemaUpsert = Partial<{
  name: string | null;
  address: string | null;
  status: string | null;
}>;

const makeAbs = (base: string, url?: string | null) => {
  if (!url) return null;
  if (/^https?:\/\//i.test(url)) return url;
  const b = base.replace(/\/+$/, '');
  const p = url.replace(/^\/+/, '');
  return `${b}/${p}`;
};

@Injectable({ providedIn: 'root' })
export class CinemaService {
  private http = inject(HttpClient);
  private BASE = (environment as any)?.baseUrl ?? 'http://localhost:8080';
  private API = `${this.BASE}/api`;

  // ========= ADMIN / COMMON CRUD =========

  list(params?: { q?: string; page?: number; size?: number }): Observable<Cinema[]> {
    let httpParams = new HttpParams();
    if (params?.q) httpParams = httpParams.set('q', params.q);
    if (params?.page != null) httpParams = httpParams.set('page', params.page);
    if (params?.size != null) httpParams = httpParams.set('size', params.size);
    return this.http.get<Cinema[]>(`${this.API}/cinemas`, { params: httpParams });
  }

  create(data: CinemaUpsert): Observable<Cinema> {
    return this.http.post<Cinema>(`${this.API}/cinemas`, data);
  }

  update(id: number, data: CinemaUpsert): Observable<Cinema> {
    return this.http.put<Cinema>(`${this.API}/cinemas/${id}`, data);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/cinemas/${id}`);
  }

  // ========= PUBLIC (USER) =========

  /** Danh sách rạp public (trang người dùng). */
  getCinemasPublic(): Observable<Cinema[]> {
    return this.http.get<Cinema[]>(`${this.API}/cinemas/public`).pipe(map(r => r ?? []));
  }

   getMoviesByStatus(status: string) {
    return this.http
      .get<Movie[]>(`${this.API}/movies/status/${status}/all`)
      .pipe(map(rs => Array.isArray(rs) ? rs : []));
  }

  getShowtimesByCinema(cinemaId: number, date?: string) {
    let params = new HttpParams().set('cinemaId', cinemaId);
    if (date) params = params.set('date', date);

    const abs = (url?: string | null) => {
      if (!url) return null;
      if (/^https?:\/\//i.test(url)) return url;
      const base = this.API.replace(/\/+api.*$/i, ''); // http://localhost:8080
      const path = url.startsWith('/') ? url : '/' + url;
      return `${base}${path}`;
    };

    return this.http.get<any[]>(`${this.API}/showtimes/public`, { params }).pipe(
      map(res => Array.isArray(res) ? res : []),

      // ✅ NHẬN DIỆN KIỂU MỚI: [{movieId, movieTitle, posterUrl, times:[]}]
      map(list => {
        const f = list[0];
        if (f && Array.isArray(f.times)) {
          return list.map(x => ({
            movieId:   x.movieId,
            movieTitle:x.movieTitle,
            posterUrl: abs(x.posterUrl),
            times:     (x.times || []).map((t:any)=> String(t).slice(0,5)) // "HH:mm"
          }));
        }
        return list; // chưa phải grouped → để nhánh sau xử lý
      }),

      // 🧩 KIỂU CŨ (mỗi item là 1 suất chiếu) → normalize như bạn đang làm
      map(list => {
        if (list[0] && Array.isArray((list[0] as any).times)) return list; // đã grouped
        return list.map(x => {
          const mv = (x && typeof x.movie === 'object') ? x.movie : null;

          const movie_id  = x?.movie_id  ?? x?.movieId  ?? mv?.id ?? 0;
          const cinema_id = x?.cinema_id ?? x?.cinemaId ?? x?.cinema?.id ?? 0;
          const start_time = x?.start_time ?? x?.startTime ?? null;

          const posterRaw =
            x?.poster ?? x?.poster_url ??
            mv?.poster ?? mv?.poster_url ?? mv?.posterUrl ??
            mv?.image ?? mv?.imageUrl ?? mv?.thumbnail ?? null;

          const posterAbs = abs(posterRaw);
          const movieTitle =
            x?.movie_title ?? x?.movieTitle ?? mv?.title ?? mv?.name ?? null;

          return {
            id: x?.id,
            movie_id, cinema_id, start_time,
            end_time: x?.end_time ?? x?.endTime ?? null,
            price: x?.price ?? null,
            status: x?.status ?? 'ACTIVE',
            movie: mv || undefined,

            movieId: movie_id,
            cinemaId: cinema_id,
            startTime: start_time,
            poster: posterAbs,
            posterUrl: posterAbs,
            movieTitle
          };
        });
      })
    );
  }

}
