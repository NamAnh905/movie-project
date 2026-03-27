import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { MovieShowtimeResponse, CinemaShowtimeResponse } from '../../models/showtime.model';

@Injectable({ providedIn: 'root' })
export class ShowtimeService {
  private base = `${environment.baseUrl}/showtimes/public`;

  constructor(private http: HttpClient) {}

  getPublicShowtimes(cinemaId: number, date: string): Observable<ApiResponse<MovieShowtimeResponse[]>> {
    let p = new HttpParams().set('cinemaId', cinemaId).set('date', date);
    return this.http.get<ApiResponse<MovieShowtimeResponse[]>>(this.base, { params: p });
  }

  getPublicByMovie(movieId: number, date: string): Observable<ApiResponse<CinemaShowtimeResponse[]>> {
    let p = new HttpParams().set('movieId', movieId).set('date', date);
    return this.http.get<ApiResponse<CinemaShowtimeResponse[]>>(`${this.base}/by-movie`, { params: p });
  }

  resolveId(cinemaId: number, movieId: number, date: string, hhmm: string): Observable<ApiResponse<number>> {
    let p = new HttpParams()
      .set('cinemaId', String(cinemaId))
      .set('movieId', String(movieId))
      .set('date', date)
      .set('time', hhmm);

    // BE của ông có endpoint resolve ở ShowtimeController/Service rồi, mình gọi vào đây:
    return this.http.get<ApiResponse<number>>(`${environment.baseUrl}/showtimes/resolve`, { params: p });
  }
}
