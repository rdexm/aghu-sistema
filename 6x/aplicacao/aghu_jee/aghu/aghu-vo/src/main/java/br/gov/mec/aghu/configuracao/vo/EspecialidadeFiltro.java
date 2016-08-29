package br.gov.mec.aghu.configuracao.vo;

import java.io.Serializable;


/**
 * Filtro para busca de Especialidade através do web service.
 * 
 * @author Flavio Rutkowski
 * 
 */
public class EspecialidadeFiltro  implements Serializable {

	private static final long serialVersionUID = -7616157059882174982L;
	
	private Short seq;
	private Boolean indSituacao;
	private String nomeEspecialidade;
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public Boolean getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}	
	
}
