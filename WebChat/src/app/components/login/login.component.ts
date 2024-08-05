import { Component } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private authService:AuthService,private router: Router){};

  onSubmit() {
    this.authService.login(this.username,this.password)
    .subscribe( response => {this.router.navigate([`/chats/${response.id}`]);},
    error =>{
      console.log(error);
      alert('Inicio de sesión fallido');
    }
    );
  }
}
