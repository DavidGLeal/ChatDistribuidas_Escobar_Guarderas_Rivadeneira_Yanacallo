import { Injectable } from '@angular/core';
import { Stomp } from '@stomp/stompjs';
import { Message } from '../../models/chat-message';
import { Chat } from '../../models/chats';
import { HttpClient } from '@angular/common/http';

import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private stompClient: any
  private messageSubject: BehaviorSubject<Message[]> = new BehaviorSubject<Message[]>([]);
  private usersSubject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  private notificationsSubject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  private reconnectInterval: number = 5000;
  private isReconnecting: boolean = false;

  constructor(private http:HttpClient){
    this.initConnectionSocket();
  }

  initConnectionSocket(){
    const url =   '//localhost:3000/chat-socket';
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(() =>socket);
    this.stompClient.connect({}, ()=>this.onConnect(),(error:any)=> this.onError(error));

    
  }

  private onConnect() {
    console.log("Conexión WebSocket iniciada");
    this.isReconnecting = false;


    this.stompClient.subscribe('/topic/notifications',(notification:any)=>{
      const notificationContent = JSON.parse(notification.body);
      const currentNotifications = this.notificationsSubject.getValue();
      currentNotifications.push(notificationContent);
      this.notificationsSubject.next(currentNotifications);
    })
  }

  getNotificationsSubject(): Observable<any[]> {
    return this.notificationsSubject.asObservable();
  }

  private onError(error: any) {
    console.error("Conexión WebSocket error:", error.message || error);
    this.onDisconnect();
  }
  

  private onDisconnect() {
    console.log("Conexión WebSocket perdida. Intentando reconectar...");
    this.isReconnecting = true;
    this.reconnect();
  }

  private reconnect() {
    if (this.isReconnecting) {
      setTimeout(() => {
        console.log("Intentando reconectar...");
        this.initConnectionSocket();
      }, this.reconnectInterval);
    }
  }

  createChat(title:string):Observable<Chat>{
    let chat = {title: title}
    return this.http.post<Chat>("http://localhost:8002/chats",chat);
  }


joinRoom(chatId: number,user:User){
  if(this.stompClient.connected){
    this.subscribeToRoom(chatId,user);
  }else{
    this.stompClient.connect({},()=>{
      this.subscribeToRoom(chatId,user);
    })
  }
}

  subscribeToRoom(chatId:number,user:User){
      this.stompClient.subscribe(`/topic/${chatId}`,(messages:any) =>{
        const messageContent = JSON.parse(messages.body);
        const currentMessage = this.messageSubject.getValue();
        currentMessage.push(messageContent);
        this.messageSubject.next(currentMessage);
      });

      this.stompClient.subscribe(`/topic/${chatId}/users`,(users: any)=>{
        const usersList = JSON.parse(users.body);
        this.usersSubject.next(Object.entries(usersList).map(([userName,userId])=>({userId,userName})));
      });

      this.stompClient.send(`/app/chat/${chatId}/addUser`,{},JSON.stringify({userId: user.id,userName: user.name, type:'JOIN'}));
      this.getOnUsers(chatId);
  }

  

  getOnUsers(chatId: number): Observable<any[]> {
    return new Observable(observer => {
      this.stompClient.send(`/app/chat/${chatId}/getUsers`, {});
      this.stompClient.subscribe(`/topic/${chatId}/users`, (users: any) => {
        const usersList = JSON.parse(users.body);
        observer.next(Object.entries(usersList).map(([userName, userId]) => ({ userId, userName })));
      });
    });
  }
  

  deleteChat(chatId:number):Observable<any>{
    return this.http.delete(`http://localhost:8002/chats/${chatId}`);
  }

  getMessages(chatId:number):Observable<any>{
    return this.http.get(`http://localhost:8002/chats/${chatId}/messages`);
  }

  getChats():Observable<any[]>{
    return this.http.get<any[]>(`http://localhost:8002/chats`)
  }

  getChat(chatId:number):Observable<any>{
    return this.http.get<any>(`http://localhost:8002/chats/${chatId}`);
  }


  sendMessage(chatId:number,message:Message){
    this.stompClient.send(`/app/chat/${chatId}`,{},JSON.stringify(message));
    this.stompClient.send(`/app/chat/${chatId}/notify`,{},JSON.stringify(message)) 
  }

  getMessageSubject(){
    return this.messageSubject.asObservable();  
  }

  getUsersSubject(){
    return this.usersSubject.asObservable();
  }

  disconnect(chatId:number,user: User){
    this.stompClient.send(`/app/chat/${chatId}/leaveUser`,{},JSON.stringify({userId: user.id,userName: user.name}))
  }

} 
