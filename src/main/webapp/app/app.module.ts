import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { LibrarySharedModule } from 'app/shared/shared.module';
import { LibraryCoreModule } from 'app/core/core.module';
import { LibraryAppRoutingModule } from './app-routing.module';
import { LibraryHomeModule } from './home/home.module';
import { LibraryEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    LibrarySharedModule,
    LibraryCoreModule,
    LibraryHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    LibraryEntityModule,
    LibraryAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class LibraryAppModule {}
