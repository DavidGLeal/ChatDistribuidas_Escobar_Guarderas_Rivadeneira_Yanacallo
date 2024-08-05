import { Component, NgModule, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { Message } from '../../models/chat-message';
import { ChatService } from '../../services/chat/chat.service';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { UsersService } from '../../services/users/users.service';
import { ProfileComponent } from '../profile/profile.component';
import { HtmlParser } from '@angular/compiler';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    ProfileComponent
  ],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, AfterViewChecked {

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  messageInput: string = "";
  chatId:number = 0;
  chat: any  = {};
  messageList: any[] = [];
  currentUser: any = {};
  usersList: any[] =[];
  newUser: string= '';
  oldUser: string= '';
  userJoined: boolean = false;
  userLeft: boolean = false;
  

  constructor(
    private chatService: ChatService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private usersService: UsersService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params =>{
      this.chatId = params['chatId'];
      this.initializeChat();
    });
  }
  
  
  initializeChat(){
    this.currentUser = localStorage.getItem('currentUser');
    this.currentUser = this.currentUser ? JSON.parse(this.currentUser) : null;
    console.log(this.currentUser);
    this.chatService.joinRoom(this.chatId,this.currentUser);
    this.getMessages();
    this.listenMessage();
    this.getConnectedUsers();
  }



  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  getMessages() {
    this.chatService.getChat(this.chatId).subscribe(
      (res) => {
        this.chat = res
        this.messageList = res.messages.map((item: Message) => ({
          ...item,
          message_side: item.userId === this.currentUser.id ? 'sender' : 'receiver',
          sender: ""
        }));
        this.scrollToBottom();  // Asegurar scroll al final al obtener mensajes existentes
      }
    );
  }

  sendMessage() {
    const chatMessage = {
      messageText: this.messageInput,
      userId: this.currentUser.id,
      type:'CHAT'
    } as Message;
    this.chatService.sendMessage(this.chatId, chatMessage);
    this.messageInput = '';
  }

  listenMessage() {
    this.chatService.getMessageSubject().subscribe((messages: Message[]) => {
      messages.forEach((item: Message) => {

        if(item.type==='JOIN'){
          if(!this.usersList.find(u => u.userId === item.userId)){
            this.chatService.getOnUsers(this.chatId).subscribe(users =>{
              this.usersList = users;
            });
            console.log(item.userName+" se ha unido a la sala.");
            /*this.newUser = item.userName;
            this.userJoined = true;
            setTimeout(()=>{
              this.userJoined = false;
              this.newUser = '';
            },5000);*/
          }
        }
        else if(item.type==='LEAVE'){
          this.chatService.getOnUsers(this.chatId).subscribe(users=>{
            this.usersList = users;            
          })
          console.log(item.userName + " ha abandonado el chat");
          /*this.oldUser = item.userName;
          this.userLeft = true;
            setTimeout(()=>{
              this.userLeft = false;
              this.oldUser = '';
            },5000);*/
        }
        else{
          const existingMessage = this.messageList.find(m => m.id === item.id);
          if (!existingMessage) {
            this.messageList.push({
              ...item,
              message_side: item.userId === this.currentUser.id ? 'sender' : 'receiver',
              sender: item.userName
            });
            console.log(item.userName)
          }
        }
      });
      this.scrollToBottom(); 
    });
  }

  private scrollToBottom(): void {
    try {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }

  getConnectedUsers(){
    this.chatService.getUsersSubject().subscribe((users: any[]) => {
      this.usersList = users;
      console.log("Usuarios conectados: ", this.usersList);
    })
  }


  exit(){
    this.chatService.disconnect(this.chatId,this.currentUser.name);
    this.router.navigate([`/chats/${this.currentUser.id}`])
  }

}
