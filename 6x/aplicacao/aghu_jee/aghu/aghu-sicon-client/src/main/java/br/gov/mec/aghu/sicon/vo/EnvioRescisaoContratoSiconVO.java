package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EnvioRescisaoContratoSiconVO implements Serializable {

	private static final long serialVersionUID = -1158522066050497433L;
	private Integer numeroContrato;
	private String fornecedor;
	private BigDecimal valorTotal;
	private String processo;

	private String tipoRescisao;
	private String justificativa;
	private Date dtAssinatura;
	private Date dtPublicacao;

	private Date dtEnvio;
	private String acao;
	private String usuarioResponsavel;

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Date getDtAssinatura() {
		return dtAssinatura;
	}

	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}

	public Date getDtPublicacao() {
		return dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}

	public Date getDtEnvio() {
		return dtEnvio;
	}

	public void setDtEnvio(Date dtEnvio) {
		this.dtEnvio = dtEnvio;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getUsuarioResponsavel() {
		return usuarioResponsavel;
	}

	public void setUsuarioResponsavel(String usuarioResponsavel) {
		this.usuarioResponsavel = usuarioResponsavel;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public String getTipoRescisao() {
		return tipoRescisao;
	}

	public void setTipoRescisao(String tipoRescisao) {
		this.tipoRescisao = tipoRescisao;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(numeroContrato).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.numeroContrato,
				((EnvioRescisaoContratoSiconVO) obj).numeroContrato).isEquals();
	}

}
