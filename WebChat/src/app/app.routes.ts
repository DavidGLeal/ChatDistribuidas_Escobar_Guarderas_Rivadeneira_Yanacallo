import { Routes } from '@angular/router';
import { ChatComponent } from './components/chat/chat.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuardService } from './services/authguard/authguard.service';
import { GuestGuard } from './services/guestguard/guestguard.service';
import { ChatlistComponent } from './components/chatlist/chatlist.component';

export const routes: Routes = [
    {path: 'chats/:userId',component: ChatlistComponent, canActivate:[AuthGuardService]},
    {path: 'chat/:chatId',component: ChatComponent, canActivate:[AuthGuardService]},
    {path: 'login',component:LoginComponent, canActivate: [GuestGuard]},
    {path: 'register',component: RegisterComponent, canActivate: [GuestGuard]},
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
