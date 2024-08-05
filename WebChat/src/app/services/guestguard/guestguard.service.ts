import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class GuestGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser) {
      this.router.navigate(['/chats', JSON.parse(currentUser).id]);
      return false;
    }
    return true;
  }
}
