import { Injectable, computed, signal } from '@angular/core';
import { CANDIDATOS, CANDIDATOS_NOVOS, CONEXOES, PACIENTE } from './mock-data';
import { Candidato, SourceSystem, SystemId } from './models';

export type Decisao = 'costurado' | 'separado';

/**
 * Estado do protótipo. Tudo em memória — o backend (Java + Spring Boot / Oracle ADB)
 * entra no lugar deste serviço na próxima fase.
 */
@Injectable({ providedIn: 'root' })
export class SuturaStore {
  readonly conexoes = signal<SourceSystem[]>(CONEXOES.map((c) => ({ ...c })));
  readonly sincronizando = signal<SystemId | null>(null);
  readonly fila = signal<Candidato[]>([...CANDIDATOS]);
  readonly decisoes = signal<Record<string, Decisao>>({});
  readonly paciente = signal(PACIENTE);

  readonly pendentes = computed(
    () => this.fila().filter((c) => !this.decisoes()[c.id]).length,
  );
  readonly costurados = computed(
    () => Object.values(this.decisoes()).filter((d) => d === 'costurado').length,
  );
  readonly separados = computed(
    () => Object.values(this.decisoes()).filter((d) => d === 'separado').length,
  );
  readonly conectados = computed(
    () => this.conexoes().filter((c) => c.status === 'conectado').length,
  );
  readonly totalRegistros = computed(() =>
    this.conexoes().reduce((total, c) => total + c.registros, 0),
  );

  private novosInjetados = false;

  sincronizar(id: SystemId): void {
    if (this.sincronizando()) return;
    this.sincronizando.set(id);

    setTimeout(() => {
      const novos = id === 'mv' ? 312 : id === 'tasy' ? 148 : id === 'lab' ? 96 : 41;
      this.conexoes.update((lista) =>
        lista.map((c) =>
          c.id === id
            ? {
                ...c,
                registros: c.registros + novos,
                ultimaSync: 'agora mesmo',
                status: 'conectado' as const,
                observacao:
                  c.id === 'legado'
                    ? 'Layout normalizado pelo conector — 0 linhas rejeitadas.'
                    : c.observacao,
              }
            : c,
        ),
      );

      if (!this.novosInjetados) {
        this.novosInjetados = true;
        this.fila.update((f) => [...CANDIDATOS_NOVOS, ...f]);
      }

      this.sincronizando.set(null);
    }, 1600);
  }

  decidir(id: string, acao: Decisao): void {
    this.decisoes.update((d) => ({ ...d, [id]: acao }));
  }

  desfazer(id: string): void {
    this.decisoes.update((d) => {
      const { [id]: _removido, ...resto } = d;
      return resto;
    });
  }

  decisaoDe(id: string): Decisao | undefined {
    return this.decisoes()[id];
  }

  reiniciar(): void {
    this.novosInjetados = false;
    this.conexoes.set(CONEXOES.map((c) => ({ ...c })));
    this.fila.set([...CANDIDATOS]);
    this.decisoes.set({});
    this.sincronizando.set(null);
  }
}
