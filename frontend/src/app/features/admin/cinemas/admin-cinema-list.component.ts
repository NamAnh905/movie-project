import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface Cinema {
  id: number;
  name: string;
  address?: string;
  status: 'ACTIVE' | 'INACTIVE';
}

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-cinema-list.component.html',
  styleUrls: ['./admin-cinema-list.component.scss']
})
export class AdminCinemaListComponent implements OnInit {
  base = environment.baseUrl + environment.api.cinemas;

  items: Cinema[] = [];
  loading = false;

  q = '';
  page = 0;
  size = 5;
  totalPages = 0;
  totalElements = 0;

  constructor(private http: HttpClient) {}

  ngOnInit(): void { this.load(0); }

  private pickPageResponse(res: any, p: number) {
    if (res && typeof res === 'object' && ('content' in res || 'data' in res)) {
      const payload = res?.data ?? res;
      this.items = payload?.content ?? [];
      this.totalElements = payload?.totalElements ?? this.items.length ?? 0;
      this.totalPages = payload?.totalPages ?? Math.ceil(this.totalElements / this.size);
      this.page = payload?.number ?? p ?? 0;
    } else {
      const list: Cinema[] = Array.isArray(res) ? res : [];
      this.totalElements = list.length;
      this.totalPages = Math.max(1, Math.ceil(this.totalElements / this.size));
      this.page = Math.min(p, this.totalPages - 1);
      const start = this.page * this.size;
      this.items = list.slice(start, start + this.size);
    }
  }

  load(p: number = this.page) {
    this.loading = true;
    let params = new HttpParams().set('page', p).set('size', this.size);
    if (this.q?.trim()) params = params.set('q', this.q.trim());

    this.http.get<any>(this.base, { params }).subscribe({
      next: (r) => this.pickPageResponse(r, p),
      error: () => {},
      complete: () => (this.loading = false)
    });
  }

  onSearch() { this.page = 0; this.load(0); }
  goTo(p: number) { if (p >= 0 && p < this.totalPages) this.load(p); }
  prev() { if (this.page > 0) this.goTo(this.page - 1); }
  next() { if (this.page < this.totalPages - 1) this.goTo(this.page + 1); }
  get pager(): number[] {
    const maxButtons = 5, total = this.totalPages || 0;
    if (total <= 1) return [];
    let start = Math.max(0, this.page - Math.floor(maxButtons / 2));
    let end = Math.min(total - 1, start + maxButtons - 1);
    start = Math.max(0, end - maxButtons + 1);
    const arr: number[] = [];
    for (let i = start; i <= end; i++) arr.push(i);
    return arr;
  }

  update(c: Cinema) {
    this.http.put(`${this.base}/${c.id}`, c).subscribe(() => this.load(this.page));
  }

  remove(id: number) {
    if (!confirm('Xóa rạp này?')) return;
    this.http.delete(`${this.base}/${id}`).subscribe(() => {
      const willBeEmpty = this.items.length === 1 && this.page > 0;
      this.load(willBeEmpty ? this.page - 1 : this.page);
    });
  }
}
