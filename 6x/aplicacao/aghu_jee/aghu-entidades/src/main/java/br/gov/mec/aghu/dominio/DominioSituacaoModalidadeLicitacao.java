/**
 * 
 */
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * @author bruno.mourao
 *
 */
public enum DominioSituacaoModalidadeLicitacao implements DominioString {
	A
	;

	
	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch(this){
			case A : return "Aprovada";
			default: return null;
		}
	}

}
