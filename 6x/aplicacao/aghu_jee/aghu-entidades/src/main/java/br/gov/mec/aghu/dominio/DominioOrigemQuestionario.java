package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a origem x questionario de um atendimento.
 * 
 * @author guilherme.finotti
 * 
 */
public enum DominioOrigemQuestionario implements Dominio {
	A("Ambulatório", false),
	I("Internação", true),
	U("Urgência", true),
	X("Paciente Externo", false),
	D("Doação de sangue", false), 
	H("Hospital dia", true),
	C("Cirurgia", false),
	T("Todas as origens", false),
	;
		
	private String descricao;
	private boolean permitePrescricao;
	
	private DominioOrigemQuestionario(String descricao, boolean permitePrescricao) {
		this.descricao = descricao;
		this.permitePrescricao = permitePrescricao;
	}	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}
	
	public String getDescricaoAtdTipo() {
		switch (this) {	
		case A:
			return "Consulta";
		default:
			return getDescricao();
		}			
	}

	public boolean permitePrescricao() {
		return permitePrescricao;
	}
	
	
	public static DominioOrigemQuestionario getInstance(String valor) {
		if (DominioOrigemQuestionario.A.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.A;
		} else if (DominioOrigemQuestionario.I.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.I;
		} else if (DominioOrigemQuestionario.U.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.U;
		} else if (DominioOrigemQuestionario.X.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.X;
		} else if (DominioOrigemQuestionario.D.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.D;
		} else if (DominioOrigemQuestionario.H.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.H;
		} else if (DominioOrigemQuestionario.C.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.C;
		} else if (DominioOrigemQuestionario.T.toString().equalsIgnoreCase(valor)) {
			return DominioOrigemQuestionario.T;
		} else {
			return null;
		}
	}



}
