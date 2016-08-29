package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que mapeia os tipos de documentos dos prontuários que estão sendo
 * digitalizados para o POL
 * 
 * @author ghernandez
 * 
 */
public enum DominioTiposDocsDigitalizados implements Dominio {
	// Tipo 1
	TIPO1,
	// Tipo 2
	TIPO2,
	// Tipo 3
	TIPO3,
	// Tipo 4
	TIPO4
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case TIPO1:
			return "Tipo 1";
		case TIPO2:
			return "Tipo 2";
		case TIPO3:
			return "Tipo 3";
		case TIPO4:
			return "Tipo 4";
		default:
			return "";
		}
	}

}
