package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoItemPrescricaoSumario implements Dominio {
	
	//Os significados de cada item foram informados pelo Fred Fink, da CGTI.
	
	/**
	 * Diálise (No momento da implementacao, não existiam registros com esse valor na tabela.)
	 */
	POSITIVO_18(18),
	
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

	private DominioTipoItemPrescricaoSumario(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_18:
			return "Diálise";
		case POSITIVO_16:
			return "Procedimentos";
		case POSITIVO_14:
			return "Nutrição Parenteral";
		case POSITIVO_12:
			return "Hemoterapia";
		case POSITIVO_10:
			return "Consultoria";
		case POSITIVO_8:
			return "Soluções";
		case POSITIVO_6:
			return "Medicamentos";
		case POSITIVO_4:
			return "Cuidados";
		case POSITIVO_2:
			return "Dietas";
		default:
			return "";
		}
	}

	@Override
	public String toString() {
		return getDescricao();
	}
	
	public static DominioTipoItemPrescricaoSumario getTipoItemPrescSum(Integer cod){
		switch (cod) {
		case 18:
			return DominioTipoItemPrescricaoSumario.POSITIVO_18;
		case 16:
			return DominioTipoItemPrescricaoSumario.POSITIVO_16;
		case 14:
			return DominioTipoItemPrescricaoSumario.POSITIVO_14;
		case 12:
			return DominioTipoItemPrescricaoSumario.POSITIVO_12;
		case 10:
			return DominioTipoItemPrescricaoSumario.POSITIVO_10;
		case 8:
			return DominioTipoItemPrescricaoSumario.POSITIVO_8;
		case 6:
			return DominioTipoItemPrescricaoSumario.POSITIVO_6;
		case 4:
			return DominioTipoItemPrescricaoSumario.POSITIVO_4;
		case 2:
			return DominioTipoItemPrescricaoSumario.POSITIVO_2;
		default:
			return null;
		}
	}

}