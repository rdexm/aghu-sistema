package br.gov.mec.aghu.dominio;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioRepeticaoRetorno implements Dominio {
	S,
	Q,
	M,
	B,
	T,
	E,
	A,
	I;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
			case S:
				return "Semanal";
			case Q:
				return "Quinzenal";
			case M:
				return "Mensal";
			case B:
				return "Bimestral";
			case T:
				return "Trimestral";
			case E:
				return "Semestral";
			case A:
				return "Anual";
			case I:
				return "Bienal";
			default:
				return "";
		}
	}
	
	public String getDescricaoComSigla() {

		switch (this) {
			case S:
				return "S - Semanal";
			case Q:
				return "Q - Quinzenal";
			case M:
				return "M - Mensal";
			case B:
				return "B - Bimestral";
			case T:
				return "T - Trimestral";
			case E:
				return "E - Semestral";
			case A:
				return "A - Anual";
			case I:
				return "I - Bienal";
			default:
				return "";
		}
	}
	
	public static Object[] obterListDominioPorDescricao(String texto){
		List<DominioRepeticaoRetorno> listRetorno = new ArrayList<DominioRepeticaoRetorno>();
		
		if (StringUtils.containsIgnoreCase(S.getDescricao(), texto)){
			listRetorno.add(S);
		}

		if (StringUtils.containsIgnoreCase(Q.getDescricao(), texto)){
			listRetorno.add(Q);
		}
		
		if (StringUtils.containsIgnoreCase(M.getDescricao(), texto)){
			listRetorno.add(M);
		}
		
		if (StringUtils.containsIgnoreCase(B.getDescricao(), texto)){
			listRetorno.add(B);
		}
		
		if (StringUtils.containsIgnoreCase(T.getDescricao(), texto)){
			listRetorno.add(T);
		}
		
		if (StringUtils.containsIgnoreCase(E.getDescricao(), texto)){
			listRetorno.add(E);
		}
		
		if (StringUtils.containsIgnoreCase(A.getDescricao(), texto)){
			listRetorno.add(A);
		}
		
		if (StringUtils.containsIgnoreCase(I.getDescricao(), texto)){
			listRetorno.add(I);
		}

		return listRetorno.toArray();
	}
	
	public Integer getQuantidadeDias(){
		switch (this) {
		case S:
			return 7;
		case Q:
			return 15;
		case M:
			return 30;
		case B:
			return 60;
		case T:
			return 90;
		case E:
			return 180;
		case A:
			return 365;
		case I:
			return 730;
		default:
			return 0;
		}
	}
}
