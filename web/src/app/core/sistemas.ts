import { SystemId, SystemMeta } from './models';

/**
 * Identidade visual dos sistemas de origem: sigla e cor.
 *
 * É a única coisa que sobrou do antigo mock-data.ts — os dados propriamente ditos
 * (conexões, registros, eventos) agora vêm do backend. Sigla e cor são decisão de
 * apresentação e não têm por que trafegar pela API a cada requisição.
 */
export const SISTEMAS: Record<SystemId, SystemMeta> = {
  mv: { id: 'mv', sigla: 'MV', nome: 'MV SOUL', cor: '#2563eb' },
  tasy: { id: 'tasy', sigla: 'TASY', nome: 'Philips Tasy', cor: '#7c3aed' },
  lab: { id: 'lab', sigla: 'LAB', nome: 'Lab Alpha (LIS)', cor: '#c2410c' },
  legado: { id: 'legado', sigla: 'CSV', nome: 'SGH Legado', cor: '#64748b' },
};
