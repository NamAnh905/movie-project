// src/app/features/admin/movies/admin-movie-form.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MovieService } from '../../movies/movie.service';
import { GenreService, Genre } from '../../genres/genre.service';
import { toAbs } from '../../../core/utils/url.util';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './admin-movie-form.component.html',
  styleUrls: ['./admin-movie-form.component.scss']
})
export class AdminMovieFormComponent implements OnInit, OnDestroy {
  id: number | null = null;
  f!: FormGroup;

  uploading = false;
  posterPreview: string | null = null;
  private lastObjectURL: string | null = null;

  // ⬇️ Danh sách thể loại để hiển thị checkbox
  genres: Genre[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private svc: MovieService,
    private genreSvc: GenreService
  ) {}

  ngOnInit(): void {
    this.f = this.fb.group({
      title: ['', Validators.required],
      posterUrl: [''],
      duration: [null, [Validators.required, Validators.min(1)]],
      status: ['RELEASED', Validators.required],
      releaseDate: [''],
      language: [''],
      country: [''],
      ageRating: [''],
      description: [''],
      // ⬇️ NEW: mảng id thể loại
      genreIds: this.fb.control<number[]>([])
    });

    // Nạp danh sách thể loại cho checkbox
    this.genreSvc.all().subscribe(list => this.genres = list || []);

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam && idParam !== 'new') {
      this.id = +idParam;
      this.svc.get(this.id).subscribe(m => {
        // server trả MovieDTO (đã có genreIds/genreNames)
        this.f.patchValue({
          title: m?.title,
          posterUrl: m?.posterUrl || '',
          duration: m?.duration,
          status: m?.status || 'RELEASED',
          releaseDate: m?.releaseDate || '',
          language: m?.language || '',
          country: m?.country || '',
          ageRating: m?.ageRating || '',
          description: m?.description || '',
          genreIds: m?.genreIds || []               // ⬅️ điền sẵn thể loại khi sửa
        });
        this.posterPreview = toAbs(m?.posterUrl);
      });
    }
  }

  ngOnDestroy(): void {
    if (this.lastObjectURL) {
      URL.revokeObjectURL(this.lastObjectURL);
      this.lastObjectURL = null;
    }
  }

  // Chọn file -> upload -> set posterUrl tương đối + preview tuyệt đối
  onPosterPicked(ev: Event) {
    const inputEl = ev.target as HTMLInputElement;
    const file = inputEl.files?.[0];
    if (!file || this.uploading) return;

    if (this.lastObjectURL) URL.revokeObjectURL(this.lastObjectURL);
    this.lastObjectURL = URL.createObjectURL(file);
    this.posterPreview = this.lastObjectURL;

    const form = new FormData();
    form.append('file', file);

    this.uploading = true;
    this.svc.uploadPoster(form).subscribe({
      next: (res) => {
        this.f.get('posterUrl')?.setValue(res.url); // lưu tương đối
        this.posterPreview = toAbs(res.url);        // hiển thị tuyệt đối
        this.uploading = false;
        inputEl.value = '';
        if (this.lastObjectURL) { URL.revokeObjectURL(this.lastObjectURL); this.lastObjectURL = null; }
      },
      error: () => {
        this.uploading = false;
        alert('Tải ảnh thất bại. Vui lòng thử lại.');
        if (this.lastObjectURL) { URL.revokeObjectURL(this.lastObjectURL); this.lastObjectURL = null; }
      }
    });
  }

  // ⬇️ NEW: toggle check/uncheck 1 thể loại
  onToggleGenre(id: number, ev: Event) {
    const checked = (ev.target as HTMLInputElement).checked;
    const set = new Set<number>(this.f.value.genreIds || []);
    if (checked) set.add(id); else set.delete(id);
    this.f.get('genreIds')?.setValue(Array.from(set));
    this.f.get('genreIds')?.markAsDirty();
  }

  trackById = (_: number, g: Genre) => g.id;

  save(): void {
    if (this.f.invalid || this.uploading) return;
    const data = this.f.value; // bao gồm genreIds
    if (this.id) {
      this.svc.update(this.id, data).subscribe(() => this.router.navigate(['/admin/movies']));
    } else {
      this.svc.create(data).subscribe(() => this.router.navigate(['/admin/movies']));
    }
  }
}
