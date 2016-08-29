package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a origem de um atendimento.
 * 
 * @author gmneto
 * 
 */
public enum DominioProgramacaoExecExames implements Dominio {
	
	A("Exame de Rotina/Urgência"),
	R("Exame de Rotina"),
	U("Exame de Urgência"),
	;
		
	private String descricao;
	
	private DominioProgramacaoExecExames(String descricao) {
		this.descricao = descricao;
		
	}	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}

	
	
	public static DominioProgramacaoExecExames getInstance(String valor) {
		if (DominioProgramacaoExecExames.A.toString().equalsIgnoreCase(valor)) {
			return DominioProgramacaoExecExames.A;
		} else if (DominioProgramacaoExecExames.R.toString().equalsIgnoreCase(valor)) {
			return DominioProgramacaoExecExames.R;
		} else if (DominioProgramacaoExecExames.U.toString().equalsIgnoreCase(valor)) {
			return DominioProgramacaoExecExames.U;
		} else {
			return null;
		}
	}



}
