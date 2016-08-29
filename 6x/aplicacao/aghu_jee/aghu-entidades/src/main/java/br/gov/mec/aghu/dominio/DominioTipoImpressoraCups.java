package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio com os tipos de impressoras.
 */
public enum DominioTipoImpressoraCups implements DominioString {

	/**
	 * Aprovado.
	 */
	MATRICIAL("01"),
	
	/**
	 * Arquivado.
	 */
	LASER_PCL("02"),
	
	/**
	 * Diligência.
	 */
	COD_BARRAS("03"),

	;
	
	private String value;
	
	private DominioTipoImpressoraCups(String value) {
		this.value = value;
	}

	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MATRICIAL:
			return "Matricial";
		case LASER_PCL:
			return "Laser PCL";
		case COD_BARRAS:
			return "Cod. Barras";
		default:
			return "";
		}
	}

}
