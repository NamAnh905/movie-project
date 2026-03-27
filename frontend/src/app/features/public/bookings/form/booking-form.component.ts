import { environment } from '../../../../../environments/environment';
import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';

type ShowtimeInfo = {
  id: number;
  movieTitle: string;
  cinemaName: string;
  startTime: string;
  unitPrice: number;
  posterUrl?: string | null;
};

@Component({
  standalone: true,
  selector: 'app-booking-form',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './booking-form.component.html',
  styleUrls: ['./booking-form.component.scss']
})
export class BookingFormComponent implements OnInit {
  qty = signal(1);                         // ⬅️ số vé dạng signal
  discount = signal(0);                    // giữ nguyên
  total = computed(() => {                 // ⬅️ total phụ thuộc qty + discount + info
    const u = this.info()?.unitPrice ?? 0;
    const q = this.qty();
    return Math.max(0, u * q - this.discount());
  });

  submitting = signal(false);
  showtimeId = signal<number | null>(null);
  info = signal<ShowtimeInfo | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  private fb = inject(FormBuilder);

  form = this.fb.group({
    quantity: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
    coupon: [''],
    paymentMethod: ['VNPAY', Validators.required],
    customerName: [''],   // nếu chưa login
    customerEmail: [''],  // nếu chưa login
  });

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('showtimeId') || 0);
    if (!id) { this.error.set('Thiếu showtimeId'); return; }
    this.showtimeId.set(id);
    this.loadShowtime(id);

    this.form.controls.quantity.valueChanges.subscribe(v => {
      const cur = Number(v ?? 1);
      const clamped = Math.min(10, Math.max(1, cur));
      if (clamped !== cur) {
        this.form.patchValue({ quantity: clamped }, { emitEvent: false });
      }
      this.qty.set(clamped);      // 🔁 kích hoạt tính lại total
      this.applyCoupon();         // để mã giảm giá (nếu có) bám theo số vé
    });
  }

  private apiBase() {
    return location.port==='4200' ? location.origin.replace(':4200', ':8080') : location.origin;
  }

  private loadShowtime(id: number) {
    this.loading.set(true);
    this.http.get<any>(`${this.apiBase()}/api/showtimes/${id}`).subscribe({
      next: x => {
        const mv = x?.movie ?? {};   // vẫn giữ phòng khi sau này BE trả nested
        const cn = x?.cinema ?? {};

        this.info.set({
          id: Number(x?.id ?? id),
          // ✅ ưu tiên field phẳng từ BE
          movieTitle: String(x?.movieTitle ?? x?.movie_title ?? mv?.title ?? mv?.name ?? ''),
          cinemaName: String(x?.cinemaName ?? x?.cinema_name ?? cn?.name ?? ''),
          startTime: String(x?.startTime ?? x?.start_time ?? ''),
          unitPrice: Number(x?.price ?? 0),
          posterUrl: this.fullPoster(x?.posterUrl ?? mv?.posterUrl ?? null),
        });

        this.loading.set(false);
      },
      error: _ => { this.error.set('Không tải được thông tin suất chiếu'); this.loading.set(false); }
    });
  }


  fullPoster(p?: string | null) {
    if (!p) return 'assets/posters/banner.png';
    const b = this.apiBase().replace(/\/+$/,'');
    const path = p.startsWith('/') ? p : '/'+p;
    return b + path;
  }

  applyCoupon() {
    const code = (this.form.value.coupon || '').trim().toUpperCase();
    const u = this.info()?.unitPrice ?? 0;
    const q = this.form.value.quantity ?? 1;
    if (!code) { this.discount.set(0); return; }
    if (code === 'SALE10') this.discount.set(Math.floor(u*q*0.10));
    else if (code === 'SALE50K') this.discount.set(50000);
    else this.discount.set(0);
  }

  submit() {
    if (this.form.invalid) return;

    const url = `${environment.baseUrl}/payments/vnpay/create`;
    const body = {
      showtimeId: this.showtimeId(),
      quantity: this.qty(),
      customerName: this.form.value.customerName,
      customerEmail: this.form.value.customerEmail,
      paymentMethod: this.form.value.paymentMethod
    };

    this.submitting.set(true);
    this.http.post<{ bookingId: number; paymentUrl: string }>(url, body).subscribe({
      next: (res) => {
        if (!res?.paymentUrl) {
          this.error.set('Không tạo được phiên thanh toán.');
          this.submitting.set(false);
          return;
        }
        window.location.href = res.paymentUrl;
      },
      error: () => {
        this.error.set('Lỗi khi tạo phiên thanh toán.');
        this.submitting.set(false);
      }
    });
  }

  decQty() {
    const next = Math.max(1, (this.form.value.quantity ?? 1) - 1);
    this.form.patchValue({ quantity: next }); // valueChanges sẽ tự chạy applyCoupon & qty.set
  }

  incQty() {
    const next = Math.min(10, (this.form.value.quantity ?? 1) + 1);
    this.form.patchValue({ quantity: next });
  }

  // fallback ảnh
  onPosterError(ev: Event) {
    const img = ev.target as HTMLImageElement;
    if (img) img.src = 'assets/posters/banner.png';
  }

  // (tuỳ chọn) nút "Làm mới"
  recalculate() {
    // Trong trường hợp bạn vừa đổi nhiều ô, muốn bấm nút cập nhật:
    const q = Number(this.form.value.quantity ?? 1);
    this.qty.set(Math.min(10, Math.max(1, q)));
    this.applyCoupon();
  }
}
