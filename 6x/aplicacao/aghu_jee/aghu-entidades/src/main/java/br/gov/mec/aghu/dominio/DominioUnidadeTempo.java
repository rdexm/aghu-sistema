package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeTempo implements Dominio {

	
	/**
	 * Hora.
	 */
	H,
	/**
	 * Dia.
	 */
	D,
	/**
	 * Meses.
	 */
	M,
	/**
	 * Anos.
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case H:
			return "Horas";
		case D:
			return "Dia";
		case M:
			return "Meses";
		case A:
			return "Anos";
		default:
			return "";
		}
	}
	
	public static DominioUnidadeTempo getInstance(String valor) {
		if("H".equalsIgnoreCase(valor)){
			return DominioUnidadeTempo.H;

		} else if("D".equalsIgnoreCase(valor)){
			return DominioUnidadeTempo.D;
			
		} else if("M".equalsIgnoreCase(valor)){
			return DominioUnidadeTempo.M;
			
		} else if("A".equalsIgnoreCase(valor)){
			return DominioUnidadeTempo.A;
		}
		
		return null;
	}
}
