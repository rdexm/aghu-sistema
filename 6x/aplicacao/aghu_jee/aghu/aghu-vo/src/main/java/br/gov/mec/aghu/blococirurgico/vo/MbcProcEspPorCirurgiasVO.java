package br.gov.mec.aghu.blococirurgico.vo;

import java.util.List;


public class MbcProcEspPorCirurgiasVO {
	
	private Boolean indPrincipal;
	private Integer codigo;
	//private CirurgiaTelaProcedimentoVO cirurgiaTelaProcedimentoVO;
	private List<CirurgiaCodigoProcedimentoSusVO> listaCodigoProcedimentos;
	private Integer procedimentoCirurgicoSeq;
	private String especialidade;
	private String descricao;
	private Integer quantidade;	
	
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Boolean getIndPrincipal() {
		return indPrincipal;
	}
	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public List<CirurgiaCodigoProcedimentoSusVO> getListaCodigoProcedimentos() {
		return listaCodigoProcedimentos;
	}
	public void setListaCodigoProcedimentos(
			List<CirurgiaCodigoProcedimentoSusVO> listaCodigoProcedimentos) {
		this.listaCodigoProcedimentos = listaCodigoProcedimentos;
	}
	public Integer getProcedimentoCirurgicoSeq() {
		return procedimentoCirurgicoSeq;
	}
	public void setProcedimentoCirurgicoSeq(Integer procedimentoCirurgicoSeq) {
		this.procedimentoCirurgicoSeq = procedimentoCirurgicoSeq;
	}
	
	
}
