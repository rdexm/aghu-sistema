package br.gov.mec.aghu.comissoes.business.exception;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Códigos de exceção do módulo COMISSÕES
 * 
 * @author lcmoura
 * 
 */
public enum ComissoesExceptionCode implements BusinessExceptionCode {

	/**
	 * Erro na recuperação do parâmetro P_DIAS_PERM_DEL na
	 * MPMK_MPM_RN.RN_MPMP_VER_DEL
	 */
	MPM_00680,

	/**
	 * Não é possível excluir o registro por estar fora do período permitido
	 * para exclusão
	 */
	MPM_00681,

	/**
	 * O motivo de reinternação estando inativo não pode ser ativado novamente
	 */
	MPM_02685,

	/**
	 * Não é permitido alterar a descrição do motivo da reinternação
	 */
	MPM_02831,

	/**
	 * Para ativar o indicador outros, o indicador de exige complemento deve
	 * estar ativo.
	 */
	MPM_02851,

	/**
	 * Não existe Servidor cadastrado com matricula/vinculo
	 */
	RAP_00175;
}
