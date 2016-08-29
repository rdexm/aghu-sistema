package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;


/**
 * Filtro para busca de usuário através do web service.
 * 
 * @author Flavio Rutkowski
 * 
 */
public class UnidadeFuncionalFiltro  implements Serializable {

	private static final long serialVersionUID = -4430746314063751488L;
	
	private Short seq;
	private Boolean indSitUnidFunc;
	private String descricao;
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public Boolean getIndSitUnidFunc() {
		return indSitUnidFunc;
	}
	public void setIndSitUnidFunc(Boolean indSitUnidFunc) {
		this.indSitUnidFunc = indSitUnidFunc;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
