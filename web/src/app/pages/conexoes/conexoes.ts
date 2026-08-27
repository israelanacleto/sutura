import { DecimalPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SISTEMAS } from '../../core/mock-data';
import { SourceSystem, StatusConexao } from '../../core/models';
import { SuturaStore } from '../../core/sutura-store';

@Component({
  selector: 'app-conexoes',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './conexoes.html',
  styleUrl: './conexoes.css',
})
export class Conexoes {
  protected readonly store = inject(SuturaStore);
  protected readonly sistemas = SISTEMAS;

  protected readonly novosDetectados = computed(() =>
    this.store.fila().some((c) => c.novo),
  );

  protected rotuloStatus(status: StatusConexao): string {
    return status === 'conectado' ? 'Conectado' : status === 'atencao' ? 'Atenção' : 'Offline';
  }

  protected classeStatus(status: StatusConexao): string {
    return status === 'conectado' ? 'badge-ok' : status === 'atencao' ? 'badge-warn' : 'badge-danger';
  }

  protected sincronizando(c: SourceSystem): boolean {
    return this.store.sincronizando() === c.id;
  }
}
