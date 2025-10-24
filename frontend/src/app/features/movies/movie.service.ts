import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface UploadResponse { url: string; }

@Injectable({ providedIn: 'root' })
export class MovieService {
  private moviesBase = `${environment.baseUrl}${environment.api.movies}`;
  private filesBase  = `${environment.baseUrl}${environment.api.files}`;

  constructor(private http: HttpClient) {}

  // ✅ API phân trang cũ (giữ lại nếu cần)
  list(params?: { q?: string; status?: string; genreId?: number; page?: number; size?: number }) {
    const p = new HttpParams({
      fromObject: {
        ...(params?.q ? { q: params.q } : {}),
        ...(params?.status ? { status: params.status } : {}),
        ...(params?.genreId != null ? { genreId: String(params.genreId) } : {}),
        ...(params?.page != null ? { page: String(params.page) } : {}),
        ...(params?.size != null ? { size: String(params.size) } : {}),
      }
    });
    return this.http.get<any>(this.moviesBase, { params: p });
  }

  // ✅ API mới – lấy toàn bộ phim theo trạng thái, không phân trang
  listAllByStatus(status: string) {
    return this.http.get<any[]>(`${this.moviesBase}/status/${status}/all`);
  }

  get(id: number)               { return this.http.get<any>(`${this.moviesBase}/${id}`); }
  create(data: any)             { return this.http.post<any>(this.moviesBase, data); }
  update(id: number, data: any) { return this.http.put<any>(`${this.moviesBase}/${id}`, data); }
  remove(id: number)            { return this.http.delete(`${this.moviesBase}/${id}`); }

  uploadPoster(fileOrForm: File | FormData): Observable<UploadResponse> {
    const fd = fileOrForm instanceof FormData ? fileOrForm : (() => {
      const f = new FormData();
      f.append('file', fileOrForm);
      return f;
    })();
    return this.http.post<UploadResponse>(`${this.filesBase}/upload`, fd);
  }
}
