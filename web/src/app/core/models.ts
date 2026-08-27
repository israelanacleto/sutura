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
  novo?: boolean;
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
