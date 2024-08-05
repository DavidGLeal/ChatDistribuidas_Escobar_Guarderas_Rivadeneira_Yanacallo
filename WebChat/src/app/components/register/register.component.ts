import { Component } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  username:string = '';
  password:string = '';
  confirmPassword:string = '';

  constructor(private authService:AuthService,private router: Router){};

  checkPassword(){
    if(this.password === this.confirmPassword){
      return true;
    }else{
      alert("Error. Las contraseñas no coinciden.");
      return false;
    }
  }

  onSubmit(){
    if(this.checkPassword()){
      this.authService.register(this.username,this.password)
      .subscribe(response => {this.router.navigate([`/chats/${response.id}`]);},
      error =>{
        alert('Registro fallido');
        console.log(error);
      })
    }
  }


}
