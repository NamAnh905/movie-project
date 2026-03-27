import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MovieService } from '../../../api/public/movie.service';
import { AdminMovieService } from '../../../api/admin/admin-movie.service';
import { GenreService } from '../../../api/public/genre.service';
import { Genre } from '../../../models/genre.model';
import { Movie } from '../../../models/movie.model';

@Component({
  standalone: true,
  selector: 'app-admin-movie',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './admin-movie.component.html',
  styleUrls: ['./admin-movie.component.scss']
})
export class AdminMovieComponent implements OnInit {
  // --- STATE LIST ---
  items: Movie[] = [];
  q = ''; page = 0; size = 5; totalPages = 0; totalElements = 0;
  genres: Genre[] = []; selectedGenreId: number | null = null;
  loading = false;

  // --- STATE FORM ---
  isFormMode = false;
  editingId: number | null = null;
  f!: FormGroup;
  uploading = false; posterPreview: string | null = null;
  private lastObjectURL: string | null = null;

  constructor(
    private publicApi: MovieService,      // Inject API Khách (để lấy danh sách)
    private adminApi: AdminMovieService,  // Inject API Admin (để Thêm/Sửa/Xóa)
    private genreSvc: GenreService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.genreSvc.all().subscribe(res => this.genres = res.result || []);
    this.initForm();
    this.load(0);
  }

  initForm() {
    this.f = this.fb.group({
      title: ['', Validators.required],
      posterUrl: [''],
      duration: [120, Validators.required],
      status: ['RELEASED', Validators.required],
      releaseDate: [''],
      language: [''],
      country: [''],
      ageRating: [''],
      description: [''],
      genreIds: [[]]
    });
  }

  // ===================== LOGIC LIST =====================
  load(p: number = this.page) {
    this.loading = true;
    const params = { q: this.q || undefined, genreId: this.selectedGenreId || undefined, page: p, size: this.size };

    // Gọi API Khách để lấy danh sách
    this.publicApi.list(params).subscribe({
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
    const max = 5, total = this.totalPages || 0;
    if (total <= 1) return [];
    let start = Math.max(0, this.page - Math.floor(max / 2));
    let end = Math.min(total - 1, start + max - 1);
    start = Math.max(0, end - max + 1);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }

  // ===================== LOGIC FORM =====================
  openForm(movie?: Movie) {
    this.isFormMode = true;
    this.posterPreview = null;
    if (movie) {
      this.editingId = movie.id;
      // Lấy chi tiết bằng API Khách
      this.publicApi.getById(movie.id).subscribe(res => {
        const m = res.result;
        this.f.patchValue({ ...m, genreIds: m?.genreIds || [] });
        this.posterPreview = m?.posterUrl || null;
      });
    } else {
      this.editingId = null;
      this.f.reset({ status: 'RELEASED', duration: 120, genreIds: [] });
    }
  }

  closeForm() { this.isFormMode = false; }

  onPosterPicked(ev: Event) {
    const inputEl = ev.target as HTMLInputElement;
    if (!inputEl.files?.length) return;
    const file = inputEl.files[0];
    if (this.lastObjectURL) URL.revokeObjectURL(this.lastObjectURL);
    this.lastObjectURL = URL.createObjectURL(file);
    this.posterPreview = this.lastObjectURL;

    const form = new FormData(); form.append('file', file);
    this.uploading = true;

    // Gọi API Admin để upload
    this.adminApi.uploadPoster(form).subscribe({
      next: (res) => {
        this.f.get('posterUrl')?.setValue(res.result?.url);
        this.uploading = false;
      },
      error: () => { this.uploading = false; alert('Lỗi tải ảnh!'); }
    });
  }

  onToggleGenre(id: number, ev: Event) {
    const checked = (ev.target as HTMLInputElement).checked;
    const set = new Set<number>(this.f.value.genreIds || []);
    if (checked) set.add(id); else set.delete(id);
    this.f.get('genreIds')?.setValue(Array.from(set));
  }

  trackById = (_: number, g: Genre) => g.id;

  save() {
    if (this.f.invalid) return;

    // Gọi API Admin để Thêm/Sửa
    const req = this.editingId
        ? this.adminApi.update(this.editingId, this.f.value)
        : this.adminApi.create(this.f.value);

    req.subscribe({
      next: () => { alert('✅ Lưu phim thành công!'); this.closeForm(); this.load(); },
      error: (e) => alert('❌ Lỗi: ' + e.error?.message)
    });
  }

  remove(id: number) {
    if (!confirm('Xóa phim này?')) return;
    // Gọi API Admin để xóa
    this.adminApi.remove(id).subscribe(() => this.load(0));
  }
}
