package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que mapeia as fichas de documentos dos prontuários que estão sendo
 * digitalizados para o POL
 * 
 * @author ghernandez
 * 
 */
public enum DominioFichasDocsDigitalizados implements Dominio {
	ATIVOS,
	INATIVOS,
	ADMINISTRATIVOS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ATIVOS:
			return "ativos";
		case INATIVOS:
			return "inativos";
		case ADMINISTRATIVOS:
			return "administrativos";
		default:
			return "";
		}
	}

}
