import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}
  getMe(){ return this.http.get('/api/users/me'); }
  updateMe(body:{fullName:string; email:string}){ return this.http.put('/api/users/me', body); }
}
