package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis valores para o campo ind_sitaucao da entidade MamLaudoAih.
 *
 */
public enum DominioIndSituacaoLaudoAih implements Dominio{
	
	H,
	G,
	E,
	A,
	J,
	I,
	M,
	S,
	L,
	Z,
	R,
	P,
	C,
	U,
	O;
		
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case H:
			return "Aguarda Revisão Hospital";
		case G:
			return "Aguarda Envio para o Gestor";
		case E:
			return "Enviado para o Gestor";
		case A:
			return "Autorizado pelo Gestor";
		case J:
			return "Rejeitada pelo Gestor";
		case I:
			return "Aguarda revisão de laudo incompleto";
		case M:
			return "Em alteração pelo Médico Revisor";
		case S:
			return "Em alteração pelo Médico Solicitante";
		case L:
			return "Aguarda autorização pelo gestor";
		case Z:
			return "Aguarda realização da internação";			
		case R:
			return "Internação realizada";
		case P:
			return "Aguarda cancelamento pelo gestor";			
		case C:
			return "Cancelado";
		case U:
			return "Alta de Internação";
		case O:
			return "O";			
		default:
			return "";
		}
	}

}