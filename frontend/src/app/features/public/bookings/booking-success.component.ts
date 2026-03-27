import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-booking-success',
  imports: [CommonModule, RouterModule],
  templateUrl: './booking-success.component.html',
  styleUrls: ['./booking-success.component.scss']
})
export class BookingSuccessComponent implements OnInit {
  id?: number;
  status: 'PAID' | 'FAILED' | 'PENDING' = 'PENDING';
  data: any;
  loading = true;
  error = '';

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {}

  private apiBase() {
    return location.port === '4200' ? location.origin.replace(':4200', ':8080') : location.origin;
  }

  ngOnInit(): void {
    const qp = this.route.snapshot.queryParamMap;
    const pp = this.route.snapshot.paramMap;

    const idStr = qp.get('id') || pp.get('id');
    const st = (qp.get('status') || '').toUpperCase();
    if (st === 'PAID' || st === 'FAILED') this.status = st as any;

    this.id = idStr ? Number(idStr) : undefined;

    if (this.id) {
      this.http.get<any>(`${this.apiBase()}/api/bookings/${this.id}`).subscribe({
        next: d => {
          this.data = d;
          // nếu BE trả trạng thái, dùng luôn (đảm bảo đúng thực tế)
          if (d?.status) this.status = String(d.status).toUpperCase() as any;
          this.loading = false;
        },
        error: err => {
          // nếu 401 hay lỗi khác, vẫn để status theo query để không "giả fail"
          this.error = 'Không lấy được thông tin đơn.';
          this.loading = false;
        }
      });
    } else {
      this.loading = false;
      this.error = 'Thiếu mã đơn.';
    }
  }

  toDetail(){ if (this.id) this.router.navigate(['/bookings', this.id]); }
  toHistory(){ this.router.navigate(['/bookings']); }
}
