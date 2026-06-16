import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
  styles: [`
    .active-link {
      background: rgba(255,255,255,0.2) !important;
    }
  `]
})
export class App {
  protected readonly title = signal('TP-Metodos-Agiles-2026');
}