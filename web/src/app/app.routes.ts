import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'conexoes' },
  {
    path: 'conexoes',
    title: 'Conexões · Sutura',
    loadComponent: () => import('./pages/conexoes/conexoes').then((m) => m.Conexoes),
  },
  {
    path: 'identificacao',
    title: 'Identificação de pacientes · Sutura',
    loadComponent: () =>
      import('./pages/identificacao/identificacao').then((m) => m.Identificacao),
  },
  {
    path: 'paciente',
    title: 'Histórico unificado · Sutura',
    loadComponent: () => import('./pages/paciente/paciente').then((m) => m.PacienteView),
  },
  { path: '**', redirectTo: 'conexoes' },
];
