package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoPrescricaoQuimioterapia implements Dominio {
	
	/**
	 * Procedimentos
	 */
	POSITIVO_16(16),
	
	/**
	 * Nutrição Parenteral
	 */
	POSITIVO_14(14),
	
	/**
	 * Hemoterapia
	 */
	POSITIVO_12(12),
	
	/**
	 * Consultoria
	 */
	POSITIVO_10(10),
	
	/**
	 * Soluções
	 */
	POSITIVO_8(8),
	
	/**
	 * Medicamentos
	 */
	POSITIVO_6(6),
	
	/**
	 * Cuidados
	 */
	POSITIVO_4(4),
	
	/**
	 * Dietas
	 */
	POSITIVO_2(2);
	
	private int value;

	private DominioTipoPrescricaoQuimioterapia(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_16:
			return "PROCEDIMENTOS ESPECIAIS";
		case POSITIVO_14:
			return "NUTRIÇÕES PARENTERAIS";
		case POSITIVO_12:
			return "HEMOTERAPIAS";
		case POSITIVO_10:
			return "CONSULTORIAS";
		case POSITIVO_8:
			return "SOLUÇÕES";
		case POSITIVO_6:
			return "MEDICAMENTOS";
		case POSITIVO_4:
			return "CUIDADOS";
		case POSITIVO_2:
			return "DIETAS";
		default:
			return "";
		}
	}

	@Override
	public String toString() {
		return getDescricao();
	}
	
	public static DominioTipoPrescricaoQuimioterapia getTipoPrescricaoQuimioterapia(Integer cod){
		switch (cod) {
		case 16:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_16;
		case 14:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_14;
		case 12:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_12;
		case 10:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_10;
		case 8:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_8;
		case 6:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_6;
		case 4:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_4;
		case 2:
			return DominioTipoPrescricaoQuimioterapia.POSITIVO_2;
		default:
			return null;
		}
	}

}