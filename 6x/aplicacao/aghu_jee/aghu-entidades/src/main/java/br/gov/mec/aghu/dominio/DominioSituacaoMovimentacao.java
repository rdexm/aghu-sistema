package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação da movimentação do prontuário.
 * 
 * @author Ricardo Costa
 * 
 */
public enum DominioSituacaoMovimentacao implements Dominio {

	/**
	 * 1° Grau Completo
	 */
	D(1),

	N(2),

	P(3),

	R(4),

	S(5);

	private int value;

	private DominioSituacaoMovimentacao(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Devolvidos";
		case N:
			return "Não localizados";
		case P:
			return "Solicitados";
		case R:
			return "Retirados";
		case S:
			return "Separados";
		default:
			return "";
		}
	}

	public Short getShort() {
		switch (this) {
		case D:
			return 'D';
		case N:
			return 'N';
		case P:
			return 'P';
		case R:
			return 'R';
		case S:
			return 'S';
		default:
			return null;
		}
	}
}
