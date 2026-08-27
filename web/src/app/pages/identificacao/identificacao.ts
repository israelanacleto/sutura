import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SISTEMAS } from '../../core/sistemas';
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
  /** ?abrir=<id do par> já abre aquele candidato — serve para capturas e links diretos. */
  private readonly rota = inject(ActivatedRoute);
  protected readonly expandido = signal<string | null>(
    this.rota.snapshot.queryParamMap.get('abrir'),
  );

  protected alternar(id: string): void {
    this.expandido.update((atual) => (atual === id ? null : id));
  }

  protected async decidir(c: Candidato, acao: Decisao): Promise<void> {
    if (this.expandido() === c.id) this.expandido.set(null);
    await this.store.decidir(c, acao);
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
}
