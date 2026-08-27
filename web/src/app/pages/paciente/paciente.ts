import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SISTEMAS } from '../../core/sistemas';
import { CategoriaEvento, EventoClinico, SystemId } from '../../core/models';
import { SuturaStore } from '../../core/sutura-store';

type Modo = 'antes' | 'depois';

@Component({
  selector: 'app-paciente',
  imports: [RouterLink],
  templateUrl: './paciente.html',
  styleUrl: './paciente.css',
})
export class PacienteView {
  protected readonly store = inject(SuturaStore);
  protected readonly sistemas = SISTEMAS;
  protected readonly ocultos = signal<SystemId[]>([]);

  /** ?modo=antes abre direto na visão fragmentada — útil para capturas e para a demo. */
  private readonly rota = inject(ActivatedRoute);
  protected readonly modo = signal<Modo>(
    this.rota.snapshot.queryParamMap.get('modo') === 'antes' ? 'antes' : 'depois',
  );

  protected readonly paciente = this.store.paciente;

  protected readonly todosOsEventos = computed<EventoClinico[]>(
    () => this.paciente()?.eventos ?? [],
  );

  protected readonly eventos = computed(() =>
    this.todosOsEventos().filter((e) => !this.ocultos().includes(e.sistema)),
  );

  private readonly infusoesEmOrdem = computed(() =>
    this.todosOsEventos()
      .filter((e) => e.categoria === 'infusao')
      .map((e) => e.data)
      .sort((a, b) => this.paraData(a).getTime() - this.paraData(b).getTime()),
  );

  protected readonly infusoes = computed(() => this.infusoesEmOrdem().length);
  protected readonly primeiraInfusao = computed(() => this.infusoesEmOrdem().at(0) ?? '—');
  protected readonly ultimaInfusao = computed(() => this.infusoesEmOrdem().at(-1) ?? '—');

  /** O protocolo é de 28 em 28 dias; a próxima é projetada a partir da última aplicada. */
  protected readonly proximaInfusao = computed(() => {
    const ultima = this.infusoesEmOrdem().at(-1);
    if (!ultima) return '—';
    const data = this.paraData(ultima);
    data.setDate(data.getDate() + 28);
    return this.paraTexto(data);
  });

  private paraData(texto: string): Date {
    const [dia, mes, ano] = texto.split('/').map(Number);
    return new Date(ano, mes - 1, dia);
  }

  private paraTexto(data: Date): string {
    const doisDigitos = (n: number) => String(n).padStart(2, '0');
    return `${doisDigitos(data.getDate())}/${doisDigitos(data.getMonth() + 1)}/${data.getFullYear()}`;
  }

  /** Cada cadastro já vem do backend com os eventos que só aquele sistema enxerga. */
  protected readonly porSistema = computed(() => this.paciente()?.cadastros ?? []);

  protected readonly fontes = computed(() => this.paciente()?.fontes ?? []);

  protected alternarSistema(id: SystemId): void {
    this.ocultos.update((atual) =>
      atual.includes(id) ? atual.filter((s) => s !== id) : [...atual, id],
    );
  }

  protected oculto(id: SystemId): boolean {
    return this.ocultos().includes(id);
  }

  protected contarNoSistema(id: SystemId): number {
    return this.todosOsEventos().filter((e) => e.sistema === id).length;
  }

  protected rotuloCategoria(c: CategoriaEvento): string {
    return c === 'infusao'
      ? 'Infusão'
      : c === 'consulta'
        ? 'Consulta'
        : c === 'exame'
          ? 'Exame'
          : 'Cirurgia';
  }

  protected destacado(e: EventoClinico): boolean {
    return e.categoria === 'infusao';
  }
}
