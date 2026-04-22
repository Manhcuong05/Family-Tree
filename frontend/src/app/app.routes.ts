import { Routes } from '@angular/router';
import { AuthComponent } from './pages/auth/auth.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MembersComponent } from './pages/members/members.component';
import { TreeComponent } from './pages/tree/tree.component';
import { AdminComponent } from './pages/admin/admin.component';

export const routes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'auth', component: AuthComponent },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'members', component: MembersComponent },
    { path: 'tree', component: TreeComponent },
    { path: 'admin', component: AdminComponent }
];
