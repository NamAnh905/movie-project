import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

export interface PageResponse<T>{ items:T[]; page:number; size:number; total:number; totalPages:number; }
export interface ListItem{
  id:number; status:string; quantity:number; totalPrice:number;
  paymentMethod?:string; paymentTxnId?:string;
  customerName?:string; customerEmail?:string;
  createdAt:string; paidAt?:string;
  showtimeId:number; startTime:string;
  cinemaId:number; cinemaName:string;
  movieId:number; movieTitle:string;
}
export interface Detail extends ListItem{
  unitPrice:number; showtimePrice:number;
  userId?:number;
  timeline:{ type:string; at:string; note:string }[];
}

@Injectable({ providedIn:'root' })
export class AdminBookingService {
  private base = location.port==='4200' ? location.origin.replace(':4200',':8080') : location.origin;
  private headers() {
    const token = localStorage.getItem('token') || '';
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
  }
  constructor(private http: HttpClient) {}

  list(params: {
    from?: string; to?: string; cinemaId?: number; movieId?: number; showtimeId?: number;
    status?: string; paymentMethod?: string; q?: string; page?: number; size?: number;
  }) {
    let p = new HttpParams();
    Object.entries(params).forEach(([k,v]) => { if (v!==undefined && v!==null && v!=='') p = p.set(k, String(v)); });
    return this.http.get<{success:boolean; data: PageResponse<ListItem> }>(`${this.base}/api/admin/bookings`, { params: p, headers: this.headers() });
  }

  get(id:number){
    return this.http.get<{success:boolean; data: Detail}>(`${this.base}/api/admin/bookings/${id}`, { headers: this.headers() });
  }
}
