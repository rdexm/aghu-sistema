package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Domínio que indica o tipo de item da nota de recebimento
 * 
 * @author Stanley Araujo
 * 
 */
public enum DominioTipoItemNotaRecebimento implements Dominio {
	
	 /**
	 * Material
	 */
	M,
	 /**
	 * Serviço
	 **/
	S;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Material";
		case S:
			return "Serviço";
		default:
			return "";

		}
	}

}
