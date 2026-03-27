import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, NonNullableFormBuilder, Validators, FormGroup } from '@angular/forms';
import { CinemaService } from '../../../api/public/cinema.service';
import { AdminCinemaService } from '../../../api/admin/admin-cinema.service';
import { Cinema } from '../../../models/cinema.model';

@Component({
  standalone: true,
  selector: 'app-admin-cinema',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './admin-cinema.component.html',
  styleUrls: ['./admin-cinema.component.scss']
})
export class AdminCinemaComponent implements OnInit {
  // --- STATE LIST ---
  items: Cinema[] = [];
  q = ''; page = 0; size = 10; totalPages = 0; totalElements = 0;
  loading = false;

  // --- STATE FORM ---
  isFormMode = false;
  editingId: number | null = null;
  form: FormGroup;

  constructor(
    private publicApi: CinemaService,
    private adminApi: AdminCinemaService,
    private fb: NonNullableFormBuilder
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      address: ['', [Validators.maxLength(500)]],
      status: ['ACTIVE', Validators.required]
    });
  }

  ngOnInit(): void { this.load(0); }
  get f() { return this.form.controls; }

  // ===================== LOGIC LIST =====================
  load(p: number = this.page) {
    this.loading = true;
    this.publicApi.list({ q: this.q || undefined, page: p, size: this.size }).subscribe({
      next: (res) => {
        this.items = res.result?.items || [];
        this.page = res.result?.currentPage || 0;
        this.totalPages = res.result?.totalPages || 0;
        this.totalElements = res.result?.totalElements || 0;
      },
      complete: () => this.loading = false
    });
  }

  onSearch() { this.load(0); }
  goTo(p: number) { if (p >= 0 && p < this.totalPages) this.load(p); }
  prev() { if (this.page > 0) this.goTo(this.page - 1); }
  next() { if (this.page < this.totalPages - 1) this.goTo(this.page + 1); }

  get pager(): number[] {
    const maxButtons = 5, total = this.totalPages || 0;
    if (total <= 1) return [];
    let start = Math.max(0, this.page - Math.floor(maxButtons / 2));
    let end = Math.min(total - 1, start + maxButtons - 1);
    start = Math.max(0, end - maxButtons + 1);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }

  // ===================== LOGIC FORM =====================
  openForm(cinema?: Cinema) {
    this.isFormMode = true;
    if (cinema) {
      this.editingId = cinema.id;
      this.form.patchValue(cinema);
    } else {
      this.editingId = null;
      this.form.reset({ status: 'ACTIVE' });
    }
  }

  closeForm() { this.isFormMode = false; }

  submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;

    // (Lưu ý: Bạn cần khai báo thêm hàm update và create trong CinemaService nhé)
    const request$ = this.editingId
      ? this.adminApi.update(this.editingId, this.form.value)
      : this.adminApi.create(this.form.value);

    request$.subscribe({
      next: () => {
        alert('Lưu thành công');
        this.closeForm();
        this.load();
      },
      error: (e: any) => alert('❌ Lỗi: ' + e.error?.message),
      complete: () => this.loading = false
    });
  }

  remove(id: number) {
    if(!confirm('Bạn có chắc muốn xóa rạp này?')) return;
    this.adminApi.remove(id).subscribe(() => this.load(0));
  }
}
