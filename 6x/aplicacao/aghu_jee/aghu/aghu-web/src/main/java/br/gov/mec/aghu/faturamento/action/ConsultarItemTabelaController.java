package br.gov.mec.aghu.faturamento.action;

import javax.annotation.PostConstruct;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class ConsultarItemTabelaController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6743685410990750880L;

	public enum ConsultarItemTabelaControllerExceptionCode implements BusinessExceptionCode {
		ITEM_CONTA_CODIGO_OBRIGATORIO, TABELA_OBRIGATORIO;
	}
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}
}
