import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; // 1. IMPORTANTE: Debes importar esto

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet], // 2. IMPORTANTE: Debes agregarlo dentro de los corchetes
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {
  title = 'frontend';
}