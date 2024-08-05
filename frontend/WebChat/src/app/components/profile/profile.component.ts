import { Component, OnInit } from '@angular/core';
import { UsersService } from '../../services/users/users.service';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ChatlistComponent } from '../chatlist/chatlist.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {

  currentUser: any = {}

  constructor(private usersService:UsersService, private authService:AuthService, public router:Router){}


  ngOnInit(): void {
    let cur = localStorage.getItem('currentUser');
    this.currentUser = cur ? JSON.parse(cur) : null;
  }


  logout() {
    this.authService.logout();
    this.router.navigate(['/login']); // Redirige a la página de login después de cerrar sesión
  }

}
