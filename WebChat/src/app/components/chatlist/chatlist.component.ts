import { Component, OnInit } from '@angular/core';
import { UsersService } from '../../services/users/users.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { ProfileComponent } from '../profile/profile.component';
import { ChatService } from '../../services/chat/chat.service';
import { Chat } from '../../models/chats';

@Component({
  selector: 'app-chatlist',
  standalone: true,
  imports: [FormsModule,
    CommonModule, ProfileComponent],
  templateUrl: './chatlist.component.html',
  styleUrl: './chatlist.component.scss'
})
export class ChatlistComponent implements OnInit{
  chatList: any[] = [];
  jsoncurrent: any = null;
  toCreate: boolean = false;
  deleteWish: boolean = false;
  toDelete:number = 0;
  notifications: any[] = [];


  constructor(private chatService:ChatService, private authService:AuthService, private router:Router){}

  ngOnInit(): void {
    let current = localStorage.getItem("currentUser");
    this.jsoncurrent = current ? JSON.parse(current) : null;
    this.getChats();
    this.chatService.getNotificationsSubject().subscribe((notifications: any[]) => {
      notifications.forEach((not: any) => {
        not.chatName = this.chatList.find(i => i.id === not.chatId)?.title;
      });
      this.notifications = notifications;
      setTimeout(async ()=>{
        if(this.notifications.length>0){
          this.notifications.shift();
        }
      },10000)
    
      console.log(this.notifications);
    });

  }

  getChats(){
    this.chatService.getChats().subscribe(
      (data: any[]) =>{
          this.chatList = data || [];
        },
      (error) =>{
        console.error('Error al recuperar lista de chats', error);
      }
    );
  }

  switchCreate(){
    this.toCreate===true ? this.toCreate = false : this.toCreate = true;
  }

  openChat(chatId: number){
    this.router.navigate([`/chat/${chatId}`])
  }

  createChat(title:string){
    if(title != '' && title != undefined  && title != null){
      this.chatService.createChat(title).subscribe(
      (res:Chat) =>{
        this.chatList.push(res);
        this.switchCreate();
      });
    }
    else{
      alert("Ingrese un nombre de sala válido");
    }
  }


}
