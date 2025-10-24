import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Genre {
  id: number;
  name: string;
  slug?: string;
}

@Injectable({ providedIn: 'root' })
export class GenreService {
  private base = '/api/genres';

  constructor(private http: HttpClient) {}

  list(params?: { q?: string; page?: number; size?: number }): Observable<any> {
    let p = new HttpParams();
    if (params?.q) p = p.set('q', params.q);
    if (params?.page != null) p = p.set('page', params.page);
    if (params?.size != null) p = p.set('size', params.size);
    return this.http.get<any>(this.base, { params: p });
  }

  all(): Observable<Genre[]> {
    // tiện cho dropdown filter (trả tất cả, có thể dùng /api/genres?page=0&size=1000)
    return this.http.get<Genre[]>(`${this.base}/all`);
  }

  create(body: Partial<Genre>) { return this.http.post(this.base, body); }
  update(id: number, body: Partial<Genre>) { return this.http.put(`${this.base}/${id}`, body); }
  remove(id: number) { return this.http.delete(`${this.base}/${id}`); }
}
