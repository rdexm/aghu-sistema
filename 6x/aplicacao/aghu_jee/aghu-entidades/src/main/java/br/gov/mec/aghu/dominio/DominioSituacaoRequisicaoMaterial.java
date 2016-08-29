package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de uma entidade, se ativa ou inativa.
 * 
 * @author Fábio Winck
 * 
 */
public enum DominioSituacaoRequisicaoMaterial implements Dominio {
	/**
	 * Cancelada
	 */
	A,
	/**
	 * Confirmada
	 */
	C,	
	/**
	 * Gerada
	 */
	G,
	/**
	 * Efetivada
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Cancelada";
		case C:
			return "Confirmada";
		case G:
			return "Gerada";
		case E:
			return "Efetivada";
		default:
			return "";
		}
	}

	public static DominioSituacaoRequisicaoMaterial getInstance(String valor){
		if (valor.equalsIgnoreCase("A")) {
			return DominioSituacaoRequisicaoMaterial.A;
		} else if (valor.equalsIgnoreCase("C")) {
			return DominioSituacaoRequisicaoMaterial.C;
		} else if (valor.equalsIgnoreCase("G")) {
			return DominioSituacaoRequisicaoMaterial.G;
		} else if (valor.equalsIgnoreCase("E")) {
			return DominioSituacaoRequisicaoMaterial.E;
		}else{
			return DominioSituacaoRequisicaoMaterial.A;	
		}
	}
}