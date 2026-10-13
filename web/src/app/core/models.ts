export type SystemId = 'mv' | 'tasy' | 'lab' | 'legado';

export type StatusConexao = 'conectado' | 'atencao' | 'offline';

export interface SystemMeta {
  id: SystemId;
  sigla: string;
  nome: string;
  cor: string;
}

export interface SourceSystem {
  id: SystemId;
  nome: string;
  fornecedor: string;
  unidade: string;
  protocolo: string;
  status: StatusConexao;
  ultimaSync: string;
  registros: number;
  observacao: string;
}

export type SituacaoCampo = 'igual' | 'divergente' | 'ausente';

export interface ComparacaoCampo {
  campo: string;
  a: string;
  b: string;
  situacao: SituacaoCampo;
}

export interface RegistroOrigem {
  sistema: SystemId;
  nome: string;
  identificador: string;
  unidade: string;
}

export type Recomendacao = 'costurar' | 'revisar' | 'separar';

export interface Candidato {
  id: string;
  score: number;
  recomendacao: Recomendacao;
  justificativa: string;
  ladoA: RegistroOrigem;
  ladoB: RegistroOrigem;
  campos: ComparacaoCampo[];
}

export type CategoriaEvento = 'infusao' | 'consulta' | 'exame' | 'cirurgia';

export interface EventoClinico {
  id: string;
  data: string;
  titulo: string;
  categoria: CategoriaEvento;
  sistema: SystemId;
  unidade: string;
  detalhe: string;
  ciclo?: string;
}

export interface CadastroFragmentado {
  sistema: SystemId;
  nome: string;
  identificador: string;
  cns: string;
  cpf: string;
  unidade: string;
  /** Os eventos que este sistema — e apenas ele — enxerga. */
  eventos: EventoClinico[];
}

export interface Paciente {
  id: string;
  nome: string;
  nascimento: string;
  idade: number;
  cns: string;
  cpf: string;
  convenio: string;
  carteirinha: string;
  diagnostico: string;
  fontes: SystemId[];
  cadastros: CadastroFragmentado[];
  eventos: EventoClinico[];
}

// ------------------------------------------------------------------ glosa

export type Severidade = 'ALTA' | 'MEDIA' | 'BAIXA';

/**
 * Uma regra violada por uma guia. Quando `campo` é nulo não há o que corrigir na guia —
 * o problema está na identificação, e a tela oferece o caminho para a fila em vez de um
 * botão que não resolveria nada.
 */
export interface AlertaGlosa {
  codigo: string;
  descricao: string;
  orientacao: string;
  severidade: Severidade;
  campo: string | null;
  valorNaGuia: string | null;
  valorNoCadastro: string | null;
  similaridadeNome: number | null;
  corrigivel: boolean;
}

export interface GuiaResumo {
  id: string;
  numeroGuia: string;
  paciente: string;
  convenio: string;
  procedimento: string;
  valor: string;
  dataEmissao: string;
  severidade: Severidade;
  alertas: string[];
  bloqueadaPorIdentificacao: boolean;
}

export interface PainelGuias {
  valorEmRisco: string;
  guiasComAlerta: number;
  guiasProntas: number;
  guias: GuiaResumo[];
}

export interface GuiaDetalhe {
  id: string;
  numeroGuia: string;
  paciente: string;
  convenio: string;
  procedimento: string;
  valor: string;
  dataEmissao: string;
  status: string;
  sistema: SystemId;
  unidade: string;
  /** O par na fila de identificação que está travando esta guia, se houver. */
  parPendenteId: string | null;
  alertas: AlertaGlosa[];
  campos: ComparacaoCampo[];
}
