package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;


public class EnvioContratoSiconVO implements Serializable {
	
	private static final long serialVersionUID = -5930445470288307560L;
	private Integer numeroContrato;
	private String situacao;
	private String tipoContrato;
	private String codInternoUasg;
	private String modalidadeLicitacao;
	private String inciso;
	private Integer processo; // ScoLicitacao.numero
	private String fornecedor; 
	private Date dtInicioVigencia;
	private Date dtFimVigencia;
	private BigDecimal valorTotal;
	private Integer uasgSubrog;
	private Integer uasgLicit;
	private String objetoContrato;
	private String fundamentoLegal;  
	private Date dtAssinatura;
	private Date dtPublicacao;
	private List<ItemContratoVO> item;
	private Date dtEnvio;
	private String acao;
	private String usuarioResponsavel;
	private String origemDados;        
	
	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}

	public String getCodInternoUasg() {
		return codInternoUasg;
	}

	public void setCodInternoUasg(String codInternoUasg) {
		this.codInternoUasg = codInternoUasg;
	}

	public String getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(String modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public String getInciso() {
		return inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public Integer getProcesso() {
		return processo;
	}

	public void setProcesso(Integer processo) {
		this.processo = processo;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Date getDtInicioVigencia() {
		return dtInicioVigencia;
	}

	public void setDtInicioVigencia(Date data) {
		this.dtInicioVigencia = data;
	}

	public Date getDtFimVigencia() {
		return dtFimVigencia;
	}

	public void setDtFimVigencia(Date data) {
		this.dtFimVigencia = data;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Integer getUasgSubrog() {
		return uasgSubrog;
	}

	public void setUasgSubrog(Integer uasgSubrog) {
		this.uasgSubrog = uasgSubrog;
	}

	public Integer getUasgLicit() {
		return uasgLicit;
	}

	public void setUasgLicit(Integer uasgLicit) {
		this.uasgLicit = uasgLicit;
	}

	public String getObjetoContrato() {
		return objetoContrato;
	}

	public void setObjetoContrato(String objetoContrato) {
		this.objetoContrato = objetoContrato;
	}

	public String getFundamentoLegal() {
		return fundamentoLegal;
	}

	public void setFundamentoLegal(String fundamentoLegal) {
		this.fundamentoLegal = fundamentoLegal;
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

	public List<ItemContratoVO> getItem() {
		return item;
	}

	public void setItem(List<ItemContratoVO> item) {
		this.item = item;
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

	public String getOrigemDados() {
		return origemDados;
	}

	public void setOrigemDados(String origemDados) {
		this.origemDados = origemDados;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numeroContrato == null) ? 0 : numeroContrato.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		EnvioContratoSiconVO other = (EnvioContratoSiconVO) obj;
		if (other != null) {
			return new EqualsBuilder().append(this.numeroContrato, other.numeroContrato).isEquals();
		}
				return false;
	}
	
	
			
}
