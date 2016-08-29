package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de procedimento cirurgico.
 * 
 * @author cristiane barbado
 * 
 */
public enum DominioTipoProcedimentoCirurgico implements Dominio {

	/**
	 * Cirurgia
	 */
	CIRURGIA(1),
	
	/**
	 * Procedimento Diagnóstico e/ou Terapêutico
	 */
	PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO(2),
	
	/**
	 * Procedimento Anestésico
	 */
	PROCEDIMENTO_ANESTESICO(3),
	
	/**
	 * Procedimento Diagnóstico e/ou Terapêutico
	 */
	EXAME_COMPLEMENTAR(4),
	
	/**
	 * Procedimento Obstétrico
	 */
	PROCEDIMENTO_OBSTETRICO(5);
	
	
	private Integer valor;
	
	private DominioTipoProcedimentoCirurgico(Integer valor) {
		this.valor = valor;
	}	
		
	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CIRURGIA:
			return "Cirurgia";
		case PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO:
			return "Procedimento Diagnóstico e/ou Terapêutico";
		case PROCEDIMENTO_ANESTESICO:
			return "Procedimento Anestésico";
		case EXAME_COMPLEMENTAR:
			return "Exame Complementar";
		case PROCEDIMENTO_OBSTETRICO:
			return "Procedimento Obstétrico";
		default:
			return "";
		}
	}

}
