package br.gov.mec.aghu.paciente.prontuario.business.exception;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Contem as mensagems mais genericas para que sejam utilizadas em mais de um lugar.
 */

public enum ProntuarioExceptionCode implements BusinessExceptionCode {
	//Chaves de Liberar Prontuario
	AIP_00466, AIN_00611, AIN_00613, AIN_00615,
	ERRO_PERMISSAO_GERAR_PRONTUARIO, ERRO_ACESSO_GED;
}
