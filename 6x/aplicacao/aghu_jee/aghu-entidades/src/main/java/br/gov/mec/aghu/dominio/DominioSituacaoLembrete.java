package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * IndSituacao da MbcAgendas
 * 
 * @author cristiane barbado
 *
 */
public enum DominioSituacaoLembrete implements Dominio {
	 X	
	,P			
	,V	
	,A
	,E
	,R
	,C
	;
	 
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		// TODO Auto-generated method stub
		return null;
	}

}
