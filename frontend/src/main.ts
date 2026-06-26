import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
// IMPORTANTE: Cambiamos { App } por { AppComponent }
import { App } from './app/app'; 

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));