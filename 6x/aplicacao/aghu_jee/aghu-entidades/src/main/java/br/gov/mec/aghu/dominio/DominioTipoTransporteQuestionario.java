package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Tipo Transporte da entidade AelExQuestionarioOrigens.
 * 
 * @author twickert
 *
 */
public enum DominioTipoTransporteQuestionario implements Dominio {
	D, C, L, M, A, O, T
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	
	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Deambulando";
		case C:
			return "Cadeira";
		case L:
			return "Leito";
		case M:
			return "Maca";
		case A:
			return "Cama";
		case O:
			return "Colo";
		case T:
			return "Todos os tipos";
		default:
			return "";
		}
	}

	public static DominioTipoTransporteQuestionario getDominioTipoTransporteQuestionario(String value) {
		if (D.toString().equals(value)) {
			return D;
		}
		else if (C.toString().equals(value)) {
			return C;
		}
		else if (L.toString().equals(value)) {
			return L;
		}
		else if (M.toString().equals(value)) {
			return M;
		}
		else if (A.toString().equals(value)) {
			return A;
		}
		else if (O.toString().equals(value)) {
			return O;
		}
		else if (T.toString().equals(value)) {
			return T;
		}
		else {
			return null;
		}
	}
}
