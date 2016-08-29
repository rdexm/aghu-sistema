package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis valores para o campo ind_pendente da entidade MamLaudoAih.
 *
 */
public enum DominioIndPendenteLaudoAih implements Dominio {
	
	X,
	P,
	V,
	A,
	E,
	R,
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case X:
			return "Item não Utilizado";
		case P:
			return "Pendente";
		case V:
			return "Validado";
		case A:
			return "Alteração não Validada";
		case E:
			return "Exclusão não Validada";
		case R:
			return "Rascunho";
		case C:
			return "Excluído após Validação";
		default:
			return "";
		}
	}

}