package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lalegre
 *
 */
public enum DominioNaturezaExame implements Dominio {

	/**
	 * Exame Complementar
	 */
	EXAME_COMPLEMENTAR(1),
	
	/**
	 * Procedimento Diagnóstico e/ou Terapêutico
	 */
	PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO(2);
	
		
	private int value;

	private DominioNaturezaExame(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case EXAME_COMPLEMENTAR:
			return "Exame Complementar";
		case PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO:
			return "Procedimento Diagnóstico e/ou Terapêutico";
		default:
			return "";
		}
	}
	
}
