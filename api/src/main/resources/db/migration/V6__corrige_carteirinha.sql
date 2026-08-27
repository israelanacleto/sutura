-- Corrige a máscara da carteirinha do convênio, transcrita errada no seed:
-- estava '0041882733900 5' e o formato do plano é '0041 8827 3390 05'.
-- Dado fictício, mas a máscara errada aparece na ficha do paciente.

UPDATE paciente_mestre
   SET carteirinha = '0041 8827 3390 05'
 WHERE id = 1
   AND carteirinha = '0041882733900 5';

COMMIT;
