package br.gov.mec.aghu.dominio;
import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioQuimioterapico implements Dominio{
	/**
	 * Padronizados
	 */
	P,
	/**
	 * Não Padronizados 
	 */
	N,
	/**
	 * Quimioterápicos
	 */
	Q;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {	
		case P:
			return "Padronizados";
		case N:
			return "Não Padronizados";
		case Q:
			return "Quimioterápicos";	
		
		default:
			return "";
		}
	}
	
	

}
