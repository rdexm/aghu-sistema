package br.gov.mec.aghu.business.exception;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class AghuCSVBaseException {

	public enum AghuCSVBaseExceptionExceptionCode implements BusinessExceptionCode {
		ERRO_GERACAO_ARQUIVO_CSV, ERRO_GERACAO_ARQUIVO_BPA_DATA_SUS, ERRO_GERACAO_ARQUIVO_PDF, ERRO_ABRIR_ARQUIVO_LOG, ERRO_HISTORICO_MARCADO;
	}
	
}
