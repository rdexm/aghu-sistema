package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioPeleMamografia implements Dominio {
	
	/*1 – Normal (default)
	2 – Espessa
	3 – Retraída*/
	
	NORMAL(1),
	ESPESSA(2),
	RETRAIDA(3);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioPeleMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case NORMAL:
				return "Normal";	
			case ESPESSA:
				return "Espessa";
			case RETRAIDA:
				return "Retraída";							
			default:
				return "";
		}
	}

	public static DominioPeleMamografia getDominioPorCodigo(int codigo) {
		for (DominioPeleMamografia dominio : DominioPeleMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return DominioPeleMamografia.NORMAL;		
	}
}
