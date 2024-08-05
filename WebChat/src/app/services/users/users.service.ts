import { HttpClient } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class UsersService{

  private url = 'http://localhost:8001/users';
  
  constructor(private http:HttpClient) { }

  getUsers(): Observable<any[]>{
    return this.http.get<any[]>(this.url);
  }

  getUsersById(id:number): Observable<any>{
    return this.http.get<any>(this.url+"/"+id);
  }

  getName(id:number): string{
    let name: string = "";
    this.http.get<User>(this.url+"/"+id).subscribe(
      (res) =>{name = res.name}
    )
    return name
  }

}
