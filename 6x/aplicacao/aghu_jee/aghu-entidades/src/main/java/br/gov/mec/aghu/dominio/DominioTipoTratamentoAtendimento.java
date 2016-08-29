package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio para indicar o tipo de tratamento permitido para os atendimentos.
 */
public enum DominioTipoTratamentoAtendimento implements Dominio {

	/**
	 * valor 4
	 */
	VALOR_4(4),
	/**
	 * valor 8
	 */
	VALOR_8(8),
	/**
	 * valor 11
	 */
	VALOR_11(11),
	/**
	 * valor 13
	 */
	VALOR_13(13),
	/**
	 * valor 14
	 */
	VALOR_14(14),
	/**
	 * valor 19
	 */
	VALOR_19(19),
	/**
	 * valor 26
	 */
	VALOR_26(26),
	/**
	 * valor 27
	 */
	VALOR_27(27),
	/**
	 * valor 28
	 */
	VALOR_28(28),
	/**
	 * valor 29
	 */
	VALOR_29(29),
	/**
	 * valor 30
	 */
	VALOR_30(30),
	/**
	 * valor 32
	 */
	VALOR_32(32),
	/**
	 * valor 33
	 */
	VALOR_33(33),
	/**
	 * valor 35
	 */
	VALOR_35(35),
	/**
	 * valor 36
	 */
	VALOR_36(36),
	/**
	 * valor 38
	 */
	VALOR_38(38);

	private int value;

	private DominioTipoTratamentoAtendimento(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}
	
	public byte getCodigoByte() {
		return (byte) value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case VALOR_4:
			return "4";
		case VALOR_8:
			return "8";
		case VALOR_11:
			return "11";
		case VALOR_13:
			return "13";
		case VALOR_14:
			return "14";
		case VALOR_19:
			return "19";
		case VALOR_26:
			return "26";
		case VALOR_27:
			return "27";
		case VALOR_28:
			return "28";
		case VALOR_29:
			return "29";
		case VALOR_30:
			return "30";
		case VALOR_32:
			return "32";
		case VALOR_33:
			return "33";
		case VALOR_35:
			return "35";
		case VALOR_36:
			return "36";
		case VALOR_38:
			return "38";
		default:
			return "";
		}
	}

}