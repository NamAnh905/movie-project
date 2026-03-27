import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../../models/common.model';
import { BookingListItem, BookingDetail } from '../../models/booking.model';

@Injectable({ providedIn: 'root' })
export class AdminBookingService {
  private base = `${environment.baseUrl}/api/admin/bookings`;

  constructor(private http: HttpClient) {}

  list(params: {
    from?: string; to?: string; cinemaId?: number; movieId?: number; showtimeId?: number;
    status?: string; paymentMethod?: string; q?: string; page?: number; size?: number;
  }): Observable<ApiResponse<PageResponse<BookingListItem>>> {
    let p = new HttpParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') p = p.set(k, String(v));
    });

    // Interceptor tự động gắn Bearer Token rồi, cứ gọi thẳng API thôi
    return this.http.get<ApiResponse<PageResponse<BookingListItem>>>(this.base, { params: p });
  }

  get(id: number): Observable<ApiResponse<BookingDetail>> {
    return this.http.get<ApiResponse<BookingDetail>>(`${this.base}/${id}`);
  }
}
