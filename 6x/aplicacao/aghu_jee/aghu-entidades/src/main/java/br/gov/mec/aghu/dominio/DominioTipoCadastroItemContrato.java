package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCadastroItemContrato implements Dominio {

	M,
	S;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.dominio.Dominio#getDescricao()
	 */
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
