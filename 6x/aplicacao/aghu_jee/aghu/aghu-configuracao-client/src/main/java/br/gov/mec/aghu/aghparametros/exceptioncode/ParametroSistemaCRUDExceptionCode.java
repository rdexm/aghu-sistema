package br.gov.mec.aghu.aghparametros.exceptioncode;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public enum ParametroSistemaCRUDExceptionCode implements BusinessExceptionCode {
	
	ERRO_PERSISTIR_PARAMETRO_SISTEMA, ERRO_REMOVER_PARAMETRO_SISTEMA, AGH_00010
	, ERRO_ENCONTRADO_REGISTROS_DEPENDENTES
	, ERRO_REMOVER_PARAMETRO, AGH_00016
	;


}
