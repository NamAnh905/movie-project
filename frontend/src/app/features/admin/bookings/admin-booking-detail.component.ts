import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { AdminBookingService, Detail } from './admin-booking.service';

@Component({
  standalone: true,
  selector: 'app-admin-booking-detail',
  imports: [CommonModule],
  templateUrl: './admin-booking-detail.component.html',
  styleUrls: ['./admin-booking-detail.component.scss']
})
export class AdminBookingDetailComponent implements OnInit {
  id!: number;
  loading = false; error = '';
  data?: Detail;

  constructor(private api: AdminBookingService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(){
    this.loading = true; this.error = '';
    this.api.get(this.id).subscribe({
      next: r => { this.data = r.data; this.loading = false; },
      error: e => { this.error = e?.error?.message || 'Lỗi tải chi tiết'; this.loading = false; }
    });
  }

  vnd(n?: number){ return (n ?? 0).toLocaleString('vi-VN'); }
}
