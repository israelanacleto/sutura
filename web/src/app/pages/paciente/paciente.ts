import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SISTEMAS } from '../../core/mock-data';
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

  /** ?modo=antes abre direto na visão fragmentada — útil para capturas e para a demo. */
  private readonly rota = inject(ActivatedRoute);
  protected readonly modo = signal<Modo>(
    this.rota.snapshot.queryParamMap.get('modo') === 'antes' ? 'antes' : 'depois',
  );
  protected readonly ocultos = signal<SystemId[]>([]);

  protected readonly paciente = computed(() => this.store.paciente());

  protected readonly eventos = computed(() =>
    this.paciente().eventos.filter((e) => !this.ocultos().includes(e.sistema)),
  );

  protected readonly infusoes = computed(
    () => this.paciente().eventos.filter((e) => e.categoria === 'infusao').length,
  );

  /** Eventos agrupados por sistema — usado na visão "antes da Sutura". */
  protected readonly porSistema = computed(() =>
    this.paciente().cadastros.map((cadastro) => ({
      cadastro,
      eventos: this.paciente().eventos.filter((e) => e.sistema === cadastro.sistema),
    })),
  );

  protected alternarSistema(id: SystemId): void {
    this.ocultos.update((atual) =>
      atual.includes(id) ? atual.filter((s) => s !== id) : [...atual, id],
    );
  }

  protected oculto(id: SystemId): boolean {
    return this.ocultos().includes(id);
  }

  protected contarNoSistema(id: SystemId): number {
    return this.paciente().eventos.filter((e) => e.sistema === id).length;
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
