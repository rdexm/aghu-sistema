package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que mapeia as origens de documentos do HCPA que estão sendo
 * digitalizados para o POL
 * 
 * @author ghernandez
 * 
 */
public enum DominioOrigemDocsDigitalizados implements Dominio {
	// Internação
	INT,
	// Ambulatório
	AMB,
	// Emergência
	EME,
	// Exames
	EXE,
	// Documentos Legais
	DOC_LGL
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case INT:
			return "Internação";
		case AMB:
			return "Ambulatório";
		case EME:
			return "Emergência";
		case EXE:
			return "Exames";
		case DOC_LGL:
			return "Documentos Legais";
		default:
			return "";
		}
	}

}
