import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GenreService } from '../../../api/public/genre.service';
import { AdminGenreService } from '../../../api/admin/admin-genre.service';
import { Genre } from '../../../models/genre.model';

@Component({
  standalone: true,
  selector: 'app-admin-genre',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-genre.component.html',
  styleUrls: ['./admin-genre.component.scss']
})
export class AdminGenreComponent implements OnInit {
  items: Genre[] = [];
  loading = false;
  q = ''; page = 0; size = 10; totalPages = 0; totalElements = 0;

  // Form State
  isFormMode = false;
  editingId: number | null = null;
  form: Partial<Genre> = {};

  constructor(
    private publicApi: GenreService,
    private adminApi: AdminGenreService
  ) {}

  ngOnInit(): void { this.load(); }

  // ==== ĐỌC (PUBLIC API) ====
  load(p: number = this.page) {
    this.loading = true;
    this.publicApi.list({ q: this.q || undefined, page: p, size: this.size }).subscribe({
      next: (res) => {
        this.items = res.result?.items || [];
        this.page = res.result?.currentPage || 0;
        this.totalPages = res.result?.totalPages || 0;
        this.totalElements = res.result?.totalElements || 0;
      },
      error: () => { alert('Lỗi tải danh sách thể loại!'); this.loading = false; },
      complete: () => this.loading = false
    });
  }

  search() { this.load(0); }
  goTo(p: number) { if (p >= 0 && p < this.totalPages) this.load(p); }

  // ==== GHI (ADMIN API) ====
  openForm(g?: Genre) {
    this.isFormMode = true;
    if (g) {
      this.editingId = g.id;
      this.form = { ...g };
    } else {
      this.editingId = null;
      this.form = { name: '', slug: '' };
    }
  }

  closeForm() { this.isFormMode = false; }

  save() {
    if (!this.form.name) { alert('Tên thể loại không được để trống'); return; }

    this.loading = true;
    const req = { name: this.form.name, slug: this.form.slug || undefined };

    const request$ = this.editingId
      ? this.adminApi.update(this.editingId, req)
      : this.adminApi.create(req);

    request$.subscribe({
      next: () => { alert('✅ Lưu thành công!'); this.closeForm(); this.load(); },
      error: (e) => { alert('❌ Lỗi: ' + e.error?.message); this.loading = false; }
    });
  }

  remove(id: number) {
    if(!confirm('Bạn có chắc muốn xóa thể loại này?')) return;
    this.adminApi.remove(id).subscribe(() => this.load(0));
  }
}
