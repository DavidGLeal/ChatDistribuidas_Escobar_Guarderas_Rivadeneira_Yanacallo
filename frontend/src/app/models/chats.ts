import { Message } from "@stomp/stompjs";
export interface Chat{
    id: number;
    title: string;
    messages: Message[];
}