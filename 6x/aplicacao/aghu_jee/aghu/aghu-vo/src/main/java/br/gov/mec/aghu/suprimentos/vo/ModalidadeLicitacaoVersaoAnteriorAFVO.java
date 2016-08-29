package br.gov.mec.aghu.suprimentos.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class ModalidadeLicitacaoVersaoAnteriorAFVO {

	private String mlcCodigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private String razaoSocial;
	
	
	public String getMlcCodigo() {
		return mlcCodigo;
	}
	public void setMlcCodigo(String mlcCodigo) {
		this.mlcCodigo = mlcCodigo;
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
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	

}
