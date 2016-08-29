/**
 * 
 */
package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RelatorioResumoVerbaGrupoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2151455988074602397L;

	private Integer numero;
	private String mlcCodigo;
	private String mlcDescricao;
	private String modalidadePac;
	private Date dtDigitacao;
	private BigDecimal valorTotal;
	private Integer codigoModalidadeEmpenho;
	private String descricaoModalidadeEmpenho;
	private String modalidadeEmpenho;
	private List<VerbaGrupoSolicitacaoVO> listaCompras = new ArrayList<VerbaGrupoSolicitacaoVO>();
	private List<VerbaGrupoSolicitacaoVO> listaServicos = new ArrayList<VerbaGrupoSolicitacaoVO>();
	
	
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getMlcCodigo() {
		return mlcCodigo;
	}
	public void setMlcCodigo(String mlcCodigo) {
		this.mlcCodigo = mlcCodigo;
	}
	public String getMlcDescricao() {
		return mlcDescricao;
	}
	public void setMlcDescricao(String mlcDescricao) {
		this.mlcDescricao = mlcDescricao;
	}
	public Date getDtDigitacao() {
		return dtDigitacao;
	}
	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}
	public List<VerbaGrupoSolicitacaoVO> getListaCompras() {
		return listaCompras;
	}
	public void setListaCompras(List<VerbaGrupoSolicitacaoVO> listaCompras) {
		this.listaCompras = listaCompras;
	}
	public List<VerbaGrupoSolicitacaoVO> getListaServicos() {
		return listaServicos;
	}
	public void setListaServicos(List<VerbaGrupoSolicitacaoVO> listaServicos) {
		this.listaServicos = listaServicos;
	}
	public Integer getCodigoModalidadeEmpenho() {
		return codigoModalidadeEmpenho;
	}
	public void setCodigoModalidadeEmpenho(Integer codigoModalidadeEmpenho) {
		this.codigoModalidadeEmpenho = codigoModalidadeEmpenho;
	}
	public String getDescricaoModalidadeEmpenho() {
		return descricaoModalidadeEmpenho;
	}
	public void setDescricaoModalidadeEmpenho(String descricaoModalidadeEmpenho) {
		this.descricaoModalidadeEmpenho = descricaoModalidadeEmpenho;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getModalidadePac() {
		return modalidadePac;
	}
	public void setModalidadePac(String modalidadePac) {
		this.modalidadePac = modalidadePac;
	}
	public String getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}
	public void setModalidadeEmpenho(String modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	
}
