package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EnvioAditivoContratoSiconVO implements Serializable {

	private static final long serialVersionUID = -7802737651300346031L;
	private Integer numeroContrato;
	private String fornecedor;
	private BigDecimal valorTotal;
	private String processo;

	private Integer numeroAditivo;
	private String tipoContrato;
	private Date dtInicioVigencia;
	private Date dtFimVigencia;
	private Date dataRescisao;
	private String tipoAditivo;
	private BigDecimal valorAditivado;
	private String objeto;
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

	public String getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
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

	public Integer getNumeroAditivo() {
		return numeroAditivo;
	}

	public void setNumeroAditivo(Integer numeroAditivo) {
		this.numeroAditivo = numeroAditivo;
	}

	public Date getDtInicioVigencia() {
		return dtInicioVigencia;
	}

	public void setDtInicioVigencia(Date dtInicioVigencia) {
		this.dtInicioVigencia = dtInicioVigencia;
	}

	public Date getDtFimVigencia() {
		return dtFimVigencia;
	}

	public void setDtFimVigencia(Date dtFimVigencia) {
		this.dtFimVigencia = dtFimVigencia;
	}

	public Date getDataRescisao() {
		return dataRescisao;
	}

	public void setDataRescisao(Date dataRescisao) {
		this.dataRescisao = dataRescisao;
	}

	public String getTipoAditivo() {
		return tipoAditivo;
	}

	public void setTipoAditivo(String tipoAditivo) {
		this.tipoAditivo = tipoAditivo;
	}

	public BigDecimal getValorAditivado() {
		return valorAditivado;
	}

	public void setValorAditivado(BigDecimal valorAditivado) {
		this.valorAditivado = valorAditivado;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(numeroAditivo).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.numeroAditivo,
				((EnvioAditivoContratoSiconVO) obj).numeroAditivo).isEquals();
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

}
