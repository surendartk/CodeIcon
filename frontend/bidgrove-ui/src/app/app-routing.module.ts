import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { UserHomeComponent } from './components/user-home/user-home.component';
import { AdminHomeComponent } from './components/admin-home/admin-home.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';
import { MyProfileComponent } from './pages/my-profile/my-profile.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'my-profile',
    component: MyProfileComponent,
    canActivate: [AuthGuard],
  },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'user-home',
    component: UserHomeComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'USER' },
  },
  {
    path: 'admin-home',
    component: AdminHomeComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' },
  },
  { path: '**', redirectTo: '/login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
