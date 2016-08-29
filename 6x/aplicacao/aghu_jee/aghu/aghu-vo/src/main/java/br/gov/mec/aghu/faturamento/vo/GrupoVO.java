package br.gov.mec.aghu.faturamento.vo;

public class GrupoVO {
	
	private Short codigo;
	private String descricao;
	
	public GrupoVO() {
	}	

	public GrupoVO(Short codigo, String descricao) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
	}


	public Short getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
