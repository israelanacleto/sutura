import { HttpClient, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  Candidato,
  GuiaDetalhe,
  Paciente,
  PainelGuias,
  SourceSystem,
  SystemId,
} from './models';

export type Decisao = 'costurado' | 'separado';

/** Uma correção aplicada nesta sessão, guardada só para dar retorno visual na tela. */
export interface CorrecaoFeita {
  id: string;
  numeroGuia: string;
  paciente: string;
  campos: string[];
}

/** Uma decisão tomada nesta sessão, guardada só para dar retorno visual na tela. */
export interface DecisaoTomada {
  id: string;
  acao: Decisao;
  nomeA: string;
  nomeB: string;
  ehDaPacienteDoHistorico: boolean;
}

const API = 'http://localhost:8080/v1';

/**
 * Estado da aplicação, servido pelo backend.
 *
 * Usa httpResource (experimental desde a 19.2) para manter tudo em signals: cada recurso
 * expõe valor, carregamento e erro sem subscribe manual. A superfície pública deste
 * serviço é a mesma de quando os dados eram fixos — as telas não sabem que a fonte mudou.
 */
@Injectable({ providedIn: 'root' })
export class SuturaStore {
  private readonly http = inject(HttpClient);

  /** Incrementado após cada mutação; as requisições dependem dele e refazem sozinhas. */
  private readonly versao = signal(0);

  private readonly recursoConexoes = httpResource<SourceSystem[]>(
    () => {
      this.versao();
      return `${API}/conexoes`;
    },
    { defaultValue: [] },
  );

  private readonly recursoCandidatos = httpResource<Candidato[]>(
    () => {
      this.versao();
      return `${API}/candidatos`;
    },
    { defaultValue: [] },
  );

  private readonly recursoPaciente = httpResource<Paciente | undefined>(() => {
    this.versao();
    return `${API}/pacientes/1`;
  });

  private readonly recursoGuias = httpResource<PainelGuias>(
    () => {
      this.versao();
      return `${API}/guias`;
    },
    { defaultValue: { valorEmRisco: '—', guiasComAlerta: 0, guiasProntas: 0, guias: [] } },
  );

  // ---------------------------------------------------------------- leitura

  readonly conexoes = this.recursoConexoes.value;
  readonly fila = this.recursoCandidatos.value;
  readonly paciente = this.recursoPaciente.value;
  readonly painel = this.recursoGuias.value;

  readonly carregandoConexoes = this.recursoConexoes.isLoading;
  readonly carregandoFila = this.recursoCandidatos.isLoading;
  readonly carregandoPaciente = this.recursoPaciente.isLoading;
  readonly carregandoGuias = this.recursoGuias.isLoading;

  readonly erroConexoes = this.recursoConexoes.error;
  readonly erroFila = this.recursoCandidatos.error;
  readonly erroPaciente = this.recursoPaciente.error;
  readonly erroGuias = this.recursoGuias.error;

  readonly guias = computed(() => this.painel().guias);
  readonly guiasComAlerta = computed(() => this.painel().guiasComAlerta);
  readonly valorEmRisco = computed(() => this.painel().valorEmRisco);

  readonly pendentes = computed(() => this.fila().length);
  readonly conectados = computed(
    () => this.conexoes().filter((c) => c.status === 'conectado').length,
  );
  readonly totalRegistros = computed(() =>
    this.conexoes().reduce((total, c) => total + c.registros, 0),
  );

  // ------------------------------------------------- decisões desta sessão

  private readonly decisoesDaSessao = signal<DecisaoTomada[]>([]);

  readonly decisoes = this.decisoesDaSessao.asReadonly();
  readonly costurados = computed(
    () => this.decisoesDaSessao().filter((d) => d.acao === 'costurado').length,
  );
  readonly separados = computed(
    () => this.decisoesDaSessao().filter((d) => d.acao === 'separado').length,
  );

  // ---------------------------------------------------------------- ações

  readonly sincronizando = signal<SystemId | null>(null);
  readonly ultimoResultadoDeSync = signal<string | null>(null);
  readonly decidindo = signal<string | null>(null);
  readonly erroDeAcao = signal<string | null>(null);

  /**
   * Dispara uma ingestão real do Bundle FHIR de demonstração. Não é simulação: o
   * documento é parseado, gravado com o payload original e a fila de identificação é
   * reavaliada pelo banco.
   */
  async sincronizar(sistema: SystemId): Promise<void> {
    if (this.sincronizando()) return;
    this.sincronizando.set(sistema);
    this.erroDeAcao.set(null);
    this.ultimoResultadoDeSync.set(null);

    try {
      const resumo = await firstValueFrom(
        this.http.post<{
          registrosCriados: number;
          registrosJaExistentes: number;
          eventosCriados: number;
        }>(`${API}/ingest/exemplo?sistema=${sistema.toUpperCase()}`, null),
      );

      this.ultimoResultadoDeSync.set(
        resumo.registrosCriados > 0
          ? `${resumo.registrosCriados} registros novos e ${resumo.eventosCriados} eventos ingeridos.`
          : `Nada novo: os ${resumo.registrosJaExistentes} registros do lote já haviam sido ingeridos.`,
      );
      this.recarregar();
    } catch {
      this.erroDeAcao.set('Não foi possível sincronizar. O backend está no ar?');
    } finally {
      this.sincronizando.set(null);
    }
  }

  async decidir(candidato: Candidato, acao: Decisao): Promise<void> {
    this.decidindo.set(candidato.id);
    this.erroDeAcao.set(null);

    try {
      await firstValueFrom(
        this.http.post<void>(`${API}/candidatos/${candidato.id}/decisao`, {
          decisao: acao.toUpperCase(),
          usuario: 'israel.anacleto',
          justificativa: candidato.justificativa,
        }),
      );

      this.decisoesDaSessao.update((lista) => [
        {
          id: candidato.id,
          acao,
          nomeA: candidato.ladoA.nome,
          nomeB: candidato.ladoB.nome,
          ehDaPacienteDoHistorico: candidato.ladoA.nome.includes('SOUZA'),
        },
        ...lista,
      ]);
      this.recarregar();
    } catch {
      this.erroDeAcao.set(
        'Não foi possível registrar a decisão. O par pode já ter sido decidido.',
      );
    } finally {
      this.decidindo.set(null);
    }
  }

  // ------------------------------------------------------------- faturamento

  readonly guiaAberta = signal<GuiaDetalhe | null>(null);
  readonly carregandoDetalhe = signal<string | null>(null);
  readonly corrigindo = signal<string | null>(null);
  readonly correcoesDaSessao = signal<CorrecaoFeita[]>([]);

  /**
   * Abre a guia buscando o detalhe sob demanda. A lista não carrega as evidências de
   * todas as guias de uma vez: a comparação campo a campo só interessa na que está aberta.
   */
  async abrirGuia(id: string): Promise<void> {
    if (this.guiaAberta()?.id === id) {
      this.guiaAberta.set(null);
      return;
    }
    this.guiaAberta.set(null);
    this.carregandoDetalhe.set(id);
    this.erroDeAcao.set(null);

    try {
      this.guiaAberta.set(
        await firstValueFrom(this.http.get<GuiaDetalhe>(`${API}/guias/${id}`)),
      );
    } catch {
      this.erroDeAcao.set('Não foi possível abrir a guia. O backend está no ar?');
    } finally {
      this.carregandoDetalhe.set(null);
    }
  }

  /**
   * Aplica na guia os campos escolhidos. Não existe "corrigir tudo": a lista de campos
   * vem da seleção do operador, pelo mesmo motivo que a costura exige decisão humana.
   */
  async corrigir(guia: GuiaDetalhe, campos: string[]): Promise<void> {
    if (campos.length === 0) return;
    this.corrigindo.set(guia.id);
    this.erroDeAcao.set(null);

    try {
      await firstValueFrom(
        this.http.post<void>(`${API}/guias/${guia.id}/corrigir`, {
          campos,
          usuario: 'israel.anacleto',
        }),
      );

      this.correcoesDaSessao.update((lista) => [
        { id: guia.id, numeroGuia: guia.numeroGuia, paciente: guia.paciente, campos },
        ...lista,
      ]);
      this.guiaAberta.set(null);
      this.recarregar();
    } catch {
      this.erroDeAcao.set(
        'Não foi possível aplicar a correção. O cadastro unificado pode não ter o dado.',
      );
    } finally {
      this.corrigindo.set(null);
    }
  }

  /** Refaz todas as consultas ao backend. */
  recarregar(): void {
    this.versao.update((v) => v + 1);
  }

  /** Limpa apenas o retorno visual da sessão. O estado real vive no banco. */
  limparSessao(): void {
    this.decisoesDaSessao.set([]);
    this.ultimoResultadoDeSync.set(null);
    this.erroDeAcao.set(null);
    this.recarregar();
  }
}
