import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../../models/common.model';
import { BookingCreationRequest, BookingResponse, ShowtimeAvailabilityResponse } from '../../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  // Trỏ đúng vào RequestMapping("/bookings") của BookingController.java
  private base = `${environment.baseUrl}/bookings`;

  constructor(private http: HttpClient) {}

  // Lấy số ghế trống
  getAvailability(showtimeId: number): Observable<ApiResponse<ShowtimeAvailabilityResponse>> {
    return this.http.get<ApiResponse<ShowtimeAvailabilityResponse>>(`${this.base}/showtimes/${showtimeId}/availability`);
  }

  // Khách bấm đặt vé
  create(request: BookingCreationRequest): Observable<ApiResponse<BookingResponse>> {
    return this.http.post<ApiResponse<BookingResponse>>(this.base, request);
  }

  // Xem chi tiết vé (yêu cầu login)
  getOne(id: number): Observable<ApiResponse<BookingResponse>> {
    return this.http.get<ApiResponse<BookingResponse>>(`${this.base}/${id}`);
  }

  // Lấy danh sách vé của tôi (yêu cầu login)
  getMyBookings(): Observable<ApiResponse<BookingResponse[]>> {
    return this.http.get<ApiResponse<BookingResponse[]>>(`${this.base}/mine`);
  }
}
