package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioColunaExcel implements Dominio {

	A, 
	B,
	C,
	D,
	E,
	F,
	G,
	H,
	I,
	J,
	K,
	L,
	M,
	N,
	O,
	P,
	Q,
	R,
	S,
	T,
	U,
	V,
	W,
	X,
	Y,
	Z;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Coluna A";
		case B:
			return "Coluna B";
		case C:
			return "Coluna C";
		case D:
			return "Coluna D";
		case E:
			return "Coluna E";
		case F:
			return "Coluna F";
		case G:
			return "Coluna G";
		case H:
			return "Coluna H";
		case I:
			return "Coluna I";
		case J:
			return "Coluna J";
		case K:
			return "Coluna K";
		case L:
			return "Coluna L";
		case M:
			return "Coluna M";
		case N:
			return "Coluna N";
		case O:
			return "Coluna O";
		case P:
			return "Coluna P";
		case Q:
			return "Coluna Q";
		case R:
			return "Coluna R";
		case S:
			return "Coluna S";
		case T:
			return "Coluna T";
		case U:
			return "Coluna U";
		case V:
			return "Coluna V";
		case W:
			return "Coluna W";
		case X:
			return "Coluna X";
		case Y:
			return "Coluna Y";
		case Z:
			return "Coluna Z";
		default:
			return "";
		}
	}
	
	
	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * @return
	 */
	public Integer getNumeroColuna(){
		switch (this) {
		case A:
			return 0;
		case B:
			return 1;
		case C:
			return 2;
		case D:
			return 3;
		case E:
			return 4;
		case F:
			return 5;
		case G:
			return 6;
		case H:
			return 7;
		case I:
			return 8;
		case J:
			return 9;
		case K:
			return 10;
		case L:
			return 11;
		case M:
			return 12;
		case N:
			return 13;
		case O:
			return 14;
		case P:
			return 15;
		case Q:
			return 16;
		case R:
			return 17;
		case S:
			return 18;
		case T:
			return 19;
		case U:
			return 20;
		case V:
			return 21;
		case W:
			return 22;
		case X:
			return 23;
		case Y:
			return 24;
		case Z:
			return 25;
		default:
			return -1;
		}
	}
	
}
