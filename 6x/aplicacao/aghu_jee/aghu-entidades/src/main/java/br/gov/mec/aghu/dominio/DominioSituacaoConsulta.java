package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioSituacaoConsulta implements Dominio{

	M,
	L,
	F,
	R,
	G,
	A,
	B,
	C,
	P,
	D,
	E,
	H,
	I,
	J;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		// TODO Auto-generated method stub
		switch(this){
		case M:
			return "Marcada";
		case L:
			return "Livre";
		case F:
			return "Bloqueio - Feriado";
		case R:
			return "Reservada";
		case G:
			return "Gerada";
		case A:
			return "Bloqueio - Ausência de Profissional";
		case B:
			return "Bloqueio";
		case C:
			return "Bloqueio - Congresso Profissional";
		case P:
			return "Bloqueio - Particular Profissional";
		case D:
			return "Bloqueio - Doença Profissional";
		case E:
			return "Bloqueio - Férias";
		case H:
			return "Bloqueio - Aula";
		case I:
			return "Bloqueio - Licença Gestante";
		case J:
			return "Bloqueio - Afastamento";
		default:
			return "";
		}
	}
}
