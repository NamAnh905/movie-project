import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { GenreService, Genre } from '../genre.service';
import { catchError, of, timeout } from 'rxjs';

type Mode = 'LIST' | 'CREATE' | 'EDIT';

@Component({
  standalone: true,
  selector: 'app-genre-list',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './genre-list.component.html',
  styleUrls: ['./genre-list.component.scss']
})
export class GenreListComponent implements OnInit {
  items: Genre[] = [];
  loading = false;
  errorMsg = '';

  // Search/filter
  q = '';

  // Tabs
  mode: Mode = 'LIST';

  // Form
  editing: Genre | null = null;
  form: Partial<Genre> = {};

  constructor(
    private svc: GenreService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Đọc q + mode từ URL (đồng bộ giống movie-list)
    this.route.queryParamMap.subscribe(p => {
      this.q = (p.get('q') || '').trim();
      const md = (p.get('mode') || 'LIST').toUpperCase() as Mode;
      this.mode = (md === 'CREATE' || md === 'EDIT' || md === 'LIST') ? md : 'LIST';
      this.load();
    });
  }

  setMode(m: Mode) {
    // Đổi tab qua URL, giữ nguyên q
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { mode: m, q: this.q || null },
      queryParamsHandling: 'merge'
    });
  }

  search() {
    // Khi search vẫn giữ tab hiện tại
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { q: this.q || null, mode: this.mode },
      queryParamsHandling: 'merge'
    });
  }

  load() {
    this.loading = true;
    this.errorMsg = '';
    this.svc.list({ q: this.q || undefined, page: 0, size: 100 })
      .pipe(
        timeout(8000),
        catchError(err => {
          console.error(err);
          this.errorMsg = err?.status
            ? `Lỗi tải danh sách thể loại (${err.status}).`
            : 'Không thể kết nối máy chủ.';
          this.items = [];
          this.loading = false;
          return of([]);
        })
      )
      .subscribe((res: any) => {
        this.items = (res?.content ?? res) || [];
        this.loading = false;
      });
  }

  startCreate() {
    this.editing = null;
    this.form = {};
    this.setMode('CREATE');
  }

  startEdit(g: Genre) {
    this.editing = g;
    this.form = { ...g };
    this.setMode('EDIT');
  }

  cancelForm() {
    this.editing = null;
    this.form = {};
    this.setMode('LIST');
  }

  save() {
    const obs = this.editing
      ? this.svc.update(this.editing.id!, this.form)
      : this.svc.create(this.form);
    obs.subscribe(() => {
      this.cancelForm();
      this.load();
    });
  }

  remove(id: number) {
    if (!confirm('Xóa thể loại này?')) return;
    this.svc.remove(id).subscribe(() => this.load());
  }
}
