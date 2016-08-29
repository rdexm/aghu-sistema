package br.gov.mec.aghu.dominio;


import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que tem os nomes dos modulos do aghu.
 * 
 * @author aghu
 *
 */
public enum DominioModulo implements Dominio {

	PRESCRICAO_MEDICA,
	PRESCRICAO_ENFERMAGEM,
	EXAMES_LAUDOS,
	BLOCO_CIRURGICO,
	PERINATOLOGIA
	;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PRESCRICAO_ENFERMAGEM:
			return "prescricaoEnfermagem";
		case PRESCRICAO_MEDICA:
			return "prescricaomedica";
		case EXAMES_LAUDOS:
			return "exameslaudos";	
		case BLOCO_CIRURGICO:
			return "blococirurgico";
		case PERINATOLOGIA:
			return "perinatologia";
		default:
			return "";
		}
	}
}
