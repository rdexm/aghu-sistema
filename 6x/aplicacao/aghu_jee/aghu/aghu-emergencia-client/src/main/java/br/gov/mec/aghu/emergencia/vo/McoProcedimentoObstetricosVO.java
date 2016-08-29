package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * @author betolima
 *
 */
public class McoProcedimentoObstetricosVO {

	private Short seq;
	private String descricao;
	private Integer codigoPHI;
	private DominioSituacao dominioSituacao;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Boolean situacao;
	private boolean situacaoChkBox;
	
	public McoProcedimentoObstetricosVO() {
		super();
	}
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getCodigoPHI() {
		return codigoPHI;
	}
	public void setCodigoPHI(Integer codigoPHI) {
		this.codigoPHI = codigoPHI;
	}
	public DominioSituacao getDominioSituacao() {
		return dominioSituacao;
	}
	public void setDominioSituacao(DominioSituacao dominioSituacao) {
		this.dominioSituacao = dominioSituacao;
	}
	public Boolean getSituacao() {
		return situacao;
	}
	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public boolean isSituacaoChkBox() {
		return situacaoChkBox;
	}
	public void setSituacaoChkBox(boolean situacaoChkBox) {
		this.situacaoChkBox = situacaoChkBox;
	}
}
