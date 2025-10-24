import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private http = inject(HttpClient);
  private BASE = (environment as any)?.baseUrl ?? 'http://localhost:8080';
  private API = `${this.BASE}/api`;

  getAvailability(showtimeId: number): Observable<any> {
    return this.http.get(`${this.API}/showtimes/${showtimeId}/availability`);
  }

  createBooking(payload: {
    showtimeId: number;
    quantity: number;
    customerName?: string;
    customerEmail?: string;
  }): Observable<any> {
    return this.http.post(`${this.API}/bookings`, payload);
  }

  getOne(id: number): Observable<any> {
    return this.http.get(`${this.API}/bookings/${id}`);
  }

  getMyBookings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API}/bookings/mine`);
  }
}
