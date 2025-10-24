import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserRow{
  id:number; username:string; fullName?:string; email?:string;
  status:string; role:'ADMIN'|'USER'; enabled:boolean; createdAt?:string;
}

@Injectable({providedIn:'root'})
export class AdminUserService{
  private base = '/api/admin/users';
  constructor(private http:HttpClient){}

  list(opt?:{q?:string,page?:number,size?:number}):Observable<any>{
    let params = new HttpParams();
    if (opt?.q) params = params.set('q', opt.q);
    if (opt?.page!=null) params = params.set('page', opt.page);
    if (opt?.size!=null) params = params.set('size', opt.size);
    return this.http.get<any>(this.base, { params });
  }

  updateRole(id:number, role:'ADMIN'|'USER'){
    return this.http.put(`${this.base}/${id}/role`, { role });
  }
}
