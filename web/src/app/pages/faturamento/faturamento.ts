import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SISTEMAS } from '../../core/sistemas';
import { GuiaDetalhe, Severidade, SituacaoCampo } from '../../core/models';
import { SuturaStore } from '../../core/sutura-store';

/** Rótulos curtos para as etiquetas da lista. O texto longo vive na tabela regra_glosa. */
const ROTULOS: Record<string, string> = {
  PACIENTE_NAO_COSTURADO: 'Paciente não identificado',
  DUPLICIDADE_PENDENTE: 'Duplicidade pendente',
  CNS_AUSENTE: 'CNS ausente',
  CNS_DIVERGENTE: 'CNS divergente',
  CARTEIRINHA_DIVERGENTE: 'Carteirinha divergente',
  NASCIMENTO_DIVERGENTE: 'Nascimento divergente',
  NOME_DIVERGENTE: 'Nome divergente',
};

const CAMPOS: Record<string, string> = {
  nome: 'Nome',
  cns: 'CNS',
  cpf: 'CPF',
  carteirinha: 'Carteirinha',
  nascimento: 'Data de nascimento',
};

@Component({
  selector: 'app-faturamento',
  imports: [RouterLink],
  templateUrl: './faturamento.html',
  styleUrl: './faturamento.css',
})
export class Faturamento {
  protected readonly store = inject(SuturaStore);
  protected readonly sistemas = SISTEMAS;

  /**
   * Os campos que o operador marcou para aplicar. Começa VAZIO de propósito: nada é
   * corrigido por padrão. Pré-marcar tudo recriaria na prática o "aceitar tudo" que o
   * desenho recusa.
   */
  protected readonly selecionados = signal<Set<string>>(new Set());

  constructor() {
    // ?abrir=<id da guia> já abre aquela guia — serve para capturas e links diretos.
    const abrir = inject(ActivatedRoute).snapshot.queryParamMap.get('abrir');
    if (abrir) void this.abrir(abrir);
  }

  protected async abrir(id: string): Promise<void> {
    this.selecionados.set(new Set());
    await this.store.abrirGuia(id);
  }

  // Aceitam `string | null` em vez de confiar que o compilador estreita o tipo dentro do
  // @if do template. Alerta sem campo simplesmente não é selecionável.
  protected marcado(campo: string | null): boolean {
    return campo !== null && this.selecionados().has(campo);
  }

  protected alternarCampo(campo: string | null): void {
    if (campo === null) return;
    this.selecionados.update((atual) => {
      const novo = new Set(atual);
      if (novo.has(campo)) {
        novo.delete(campo);
      } else {
        novo.add(campo);
      }
      return novo;
    });
  }

  protected async corrigir(guia: GuiaDetalhe): Promise<void> {
    await this.store.corrigir(guia, [...this.selecionados()]);
    this.selecionados.set(new Set());
  }

  protected rotulo(codigo: string): string {
    return ROTULOS[codigo] ?? codigo;
  }

  protected nomeDoCampo(campo: string | null): string {
    return campo === null ? '' : (CAMPOS[campo] ?? campo);
  }

  protected classeSeveridade(s: Severidade): string {
    return s === 'ALTA' ? 'badge-danger' : s === 'MEDIA' ? 'badge-warn' : 'badge-neutro';
  }

  protected simbolo(s: SituacaoCampo): string {
    return s === 'igual' ? '=' : s === 'divergente' ? '≠' : '—';
  }

  /** Tem ao menos um campo que dá para aplicar a partir do cadastro. */
  protected temCorrigivel(guia: GuiaDetalhe): boolean {
    return guia.alertas.some((a) => a.corrigivel);
  }

  /** Tem ao menos um alerta que só se resolve na fila de identificação. */
  protected bloqueada(guia: GuiaDetalhe): boolean {
    return guia.alertas.some((a) => a.campo === null);
  }

  /** O link para a fila precisa de um par; sem ele resta apontar a tela inteira. */
  protected parametrosDaFila(guia: GuiaDetalhe): Record<string, string> {
    return guia.parPendenteId ? { abrir: guia.parPendenteId } : {};
  }
}
