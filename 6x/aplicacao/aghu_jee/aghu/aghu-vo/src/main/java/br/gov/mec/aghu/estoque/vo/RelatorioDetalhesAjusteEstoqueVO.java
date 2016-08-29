/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

/**
 * @author fabio.winck
 */

public class RelatorioDetalhesAjusteEstoqueVO implements Serializable{
	private static final long serialVersionUID = 2621130080316335570L;

	private Short almoxarifadoSeq;
	private String almoxarifadoDesc;

	private Integer materialCodigo;
	private String materialDesc;

	private Integer fornecedorNumero;
	private String fornecedorDesc;

	private Short tipoMovimentoSeq;
	private Integer tipoMovimentoCompl;
	private String tipoMovimentoDesc;

	private Short motivoMovimentoSeq;
	private String motivoMovimentoDesc;

	private String geradoEm;
	private String geradoPor;
	private Integer quantidade;
	private String unidade;

	public Short getAlmoxarifadoSeq() {
		return almoxarifadoSeq;
	}
	public void setAlmoxarifadoSeq(Short almoxarifadoSeq) {
		this.almoxarifadoSeq = almoxarifadoSeq;
	}
	public String getAlmoxarifadoDesc() {
		return almoxarifadoDesc;
	}
	public void setAlmoxarifadoDesc(String almoxarifadoDesc) {
		this.almoxarifadoDesc = almoxarifadoDesc;
	}
	public Integer getMaterialCodigo() {
		return materialCodigo;
	}
	public void setMaterialCodigo(Integer materialCodigo) {
		this.materialCodigo = materialCodigo;
	}
	public String getMaterialDesc() {
		return materialDesc;
	}
	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}
	public Integer getFornecedorNumero() {
		return fornecedorNumero;
	}
	public void setFornecedorNumero(Integer fornecedorNumero) {
		this.fornecedorNumero = fornecedorNumero;
	}
	public String getFornecedorDesc() {
		return fornecedorDesc;
	}
	public void setFornecedorDesc(String fornecedorDesc) {
		this.fornecedorDesc = fornecedorDesc;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Short getTipoMovimentoSeq() {
		return tipoMovimentoSeq;
	}
	public void setTipoMovimentoSeq(Short tipoMovimentoSeq) {
		this.tipoMovimentoSeq = tipoMovimentoSeq;
	}
	public Integer getTipoMovimentoCompl() {
		return tipoMovimentoCompl;
	}
	public void setTipoMovimentoCompl(Integer tipoMovimentoCompl) {
		this.tipoMovimentoCompl = tipoMovimentoCompl;
	}
	public String getTipoMovimentoDesc() {
		return tipoMovimentoDesc;
	}
	public void setTipoMovimentoDesc(String tipoMovimentoDesc) {
		this.tipoMovimentoDesc = tipoMovimentoDesc;
	}
	public Short getMotivoMovimentoSeq() {
		return motivoMovimentoSeq;
	}
	public void setMotivoMovimentoSeq(Short motivoMovimentoSeq) {
		this.motivoMovimentoSeq = motivoMovimentoSeq;
	}
	public String getMotivoMovimentoDesc() {
		return motivoMovimentoDesc;
	}
	public void setMotivoMovimentoDesc(String motivoMovimentoDesc) {
		this.motivoMovimentoDesc = motivoMovimentoDesc;
	}
	public String getGeradoEm() {
		return geradoEm;
	}
	public void setGeradoEm(String geradoEm) {
		this.geradoEm = geradoEm;
	}
	public String getGeradoPor() {
		return geradoPor;
	}
	public void setGeradoPor(String geradoPor) {
		this.geradoPor = geradoPor;
	}

}