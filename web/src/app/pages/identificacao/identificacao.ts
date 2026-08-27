import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SISTEMAS } from '../../core/mock-data';
import { Candidato, Recomendacao, SituacaoCampo } from '../../core/models';
import { Decisao, SuturaStore } from '../../core/sutura-store';

@Component({
  selector: 'app-identificacao',
  imports: [RouterLink],
  templateUrl: './identificacao.html',
  styleUrl: './identificacao.css',
})
export class Identificacao {
  protected readonly store = inject(SuturaStore);
  protected readonly sistemas = SISTEMAS;
  protected readonly expandido = signal<string | null>('c1');

  /** Pares que envolvem a paciente usada na tela de histórico. */
  private readonly paresDaMaria = ['c1', 'c2'];

  protected alternar(id: string): void {
    this.expandido.update((atual) => (atual === id ? null : id));
  }

  protected decidir(c: Candidato, acao: Decisao): void {
    this.store.decidir(c.id, acao);
    if (this.expandido() === c.id) this.expandido.set(null);
  }

  protected classeScore(score: number): string {
    return score >= 90 ? 'alto' : score >= 70 ? 'medio' : 'baixo';
  }

  protected rotuloRecomendacao(r: Recomendacao): string {
    return r === 'costurar'
      ? 'Recomendado costurar'
      : r === 'separar'
        ? 'Recomendado manter separado'
        : 'Requer revisão humana';
  }

  protected classeRecomendacao(r: Recomendacao): string {
    return r === 'costurar' ? 'badge-ok' : r === 'separar' ? 'badge-danger' : 'badge-warn';
  }

  protected simbolo(s: SituacaoCampo): string {
    return s === 'igual' ? '=' : s === 'divergente' ? '≠' : '—';
  }

  protected levaAoHistorico(id: string): boolean {
    return this.paresDaMaria.includes(id);
  }
}
