import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../../models/user';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private url = 'http://localhost:8001/users';
  

  constructor(private http: HttpClient,private router:Router) {}

  login(username:string,password:string): Observable<User> {
    
    let us = {name: username,password:password,connected:true};
    return this.http.post<User>(this.url+"/login", us).pipe(
      tap(resUser => {
        localStorage.setItem('currentUser',JSON.stringify(resUser));
      })
    )
  }

  register(username:string,password:string): Observable<User> {
    
    let us = {name: username,password:password,connected:true};
    return this.http.post<User>(this.url+"/register", us).pipe(
      tap(resUser => {
        console.log(resUser)
        localStorage.setItem('currentUser',JSON.stringify(resUser));
      })
    )
  }

  logout(){
      let currentUser = localStorage.getItem('currentUser');
      let parsedCurrentUser = currentUser ? JSON.parse(currentUser) : null;
      this.http.put<User>(this.url+"/"+parsedCurrentUser.id+"/disconnect",null).subscribe();
      localStorage.removeItem('currentUser');
  }

  getCurrentUser() {
    const currentUser = localStorage.getItem('currentUser');
    return currentUser  ? JSON.parse(currentUser): null;  
  }

}
