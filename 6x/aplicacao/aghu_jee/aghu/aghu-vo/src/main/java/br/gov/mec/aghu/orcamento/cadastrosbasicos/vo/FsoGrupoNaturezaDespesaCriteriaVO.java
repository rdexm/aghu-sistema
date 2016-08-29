package br.gov.mec.aghu.orcamento.cadastrosbasicos.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * Classe VO responsÃ¡vel pelos critÃ©rios de busca por grupos de natureza de despesas.
 * 
 * @author mlcruz
 */
public class FsoGrupoNaturezaDespesaCriteriaVO implements java.io.Serializable {
	private static final long serialVersionUID = 1861269482944348551L;
	
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private Object filtro;
	
	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Object getFiltro() {
		return filtro;
	}

	public void setFiltro(Object filtro) {
		this.filtro = filtro;
	}
}