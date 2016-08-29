package br.gov.mec.aghu.sig.custos.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ObjetoCustoAssociadoAtividadeVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2966044511334974766L;
	
	private String nome;
	private Integer nroVersao;
	private DominioSituacaoVersoesCustos indSituacao;
	private Integer cctCodigo;
	private String cctDescricao;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getNroVersao() {
		return nroVersao;
	}
	public void setNroVersao(Integer nroVersao) {
		this.nroVersao = nroVersao;
	}
	public DominioSituacaoVersoesCustos getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacaoVersoesCustos indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	public String getCctDescricao() {
		return cctDescricao;
	}
	public void setCctDescricao(String cctDescricao) {
		this.cctDescricao = cctDescricao;
	}
	
	
	
}
