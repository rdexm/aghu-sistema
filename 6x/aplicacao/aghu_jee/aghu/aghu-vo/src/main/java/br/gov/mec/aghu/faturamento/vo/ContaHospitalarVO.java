package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.commons.BaseBean;


public class ContaHospitalarVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3205514905369230779L;
	private Integer seq;
	private String ssmSolicitado;
	private String ssmRealizado;
	private String financiamentoSolicitado;
	private String financiamentoRealizado;
	private Long aih;
	private Date dtInternacao;
	private Date dtAlta;
	private String leito;
	private DominioSituacaoConta situacaoConta;
	private String situacaoGestor;
	private Boolean selected;

	public ContaHospitalarVO() {

	}

	public ContaHospitalarVO(final Integer seq, final String ssmSolicitado,
			final String ssmRealizado, final String financiamentoSolicitado,
			final String financiamentoRealizado, final Long aih, final Date dtInternacao,
			final Date dtAlta, final String leito, final DominioSituacaoConta situacaoConta,
			final String situacaoGestor) {
		this.seq = seq;
		this.ssmSolicitado = ssmSolicitado;
		this.ssmRealizado = ssmRealizado;
		this.financiamentoSolicitado = financiamentoSolicitado;
		this.financiamentoRealizado = financiamentoRealizado;
		this.aih = aih;
		this.dtInternacao = dtInternacao;
		this.dtAlta = dtAlta;
		this.leito = leito;
		this.situacaoConta = situacaoConta;
		this.situacaoGestor = situacaoGestor;
	}

	public ContaHospitalarVO(final VFatContaHospitalarPac vFatContaHospitalarPac,
			final String ssmSolicitado, final String ssmRealizado,
			final String financiamentoSolicitado, final String financiamentoRealizado,
			final String leito, final String situacaoGestor) {
		this.seq = vFatContaHospitalarPac.getCthSeq();
		this.ssmSolicitado = ssmSolicitado;
		this.ssmRealizado = ssmRealizado;
		this.financiamentoSolicitado = financiamentoSolicitado;
		this.financiamentoRealizado = financiamentoRealizado;
		this.aih = vFatContaHospitalarPac.getAih() != null ? vFatContaHospitalarPac
				.getAih().getNroAih()
				: null;
				this.dtInternacao = vFatContaHospitalarPac.getCthDtIntAdministrativa();
				this.dtAlta = vFatContaHospitalarPac.getCthDtAltaAdministrativa();
				this.leito = leito;
				this.situacaoConta = vFatContaHospitalarPac.getIndSituacao();
				this.situacaoGestor = situacaoGestor;

	}

	public boolean isDifSsm() {
		if (this.ssmSolicitado == null) {
			return (this.ssmRealizado != null);
		}
		return !this.ssmSolicitado.equals(this.ssmRealizado);
	}

	public boolean isDifFinanciamento() {
		if (this.financiamentoSolicitado == null) {
			return (this.financiamentoRealizado != null);
		}
		return !this.financiamentoSolicitado
		.equals(this.financiamentoRealizado);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ContaHospitalarVO other = (ContaHospitalarVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public Integer getSeq() {
		return seq;
	}

	public String getSsmSolicitado() {
		return ssmSolicitado;
	}

	public String getSsmRealizado() {
		return ssmRealizado;
	}

	public String getFinanciamentoSolicitado() {
		return financiamentoSolicitado;
	}

	public String getFinanciamentoRealizado() {
		return financiamentoRealizado;
	}

	public Long getAih() {
		return aih;
	}

	public Date getDtInternacao() {
		return dtInternacao;
	}

	public Date getDtAlta() {
		return dtAlta;
	}

	public String getLeito() {
		return leito;
	}

	public void setSeq(final Integer seq) {
		this.seq = seq;
	}

	public void setSsmSolicitado(final String ssmSolicitado) {
		this.ssmSolicitado = ssmSolicitado;
	}

	public void setSsmRealizado(final String ssmRealizado) {
		this.ssmRealizado = ssmRealizado;
	}

	public void setFinanciamentoSolicitado(final String financiamentoSolicitado) {
		this.financiamentoSolicitado = financiamentoSolicitado;
	}

	public void setFinanciamentoRealizado(final String financiamentoRealizado) {
		this.financiamentoRealizado = financiamentoRealizado;
	}

	public void setAih(final Long aih) {
		this.aih = aih;
	}

	public void setDtInternacao(final Date dtInternacao) {
		this.dtInternacao = dtInternacao;
	}

	public void setDtAlta(final Date dtAlta) {
		this.dtAlta = dtAlta;
	}

	public void setLeito(final String leito) {
		this.leito = leito;
	}

	public DominioSituacaoConta getSituacaoConta() {
		return situacaoConta;
	}

	public String getSituacaoGestor() {
		return situacaoGestor;
	}

	public void setSituacaoConta(final DominioSituacaoConta situacaoConta) {
		this.situacaoConta = situacaoConta;
	}

	public void setSituacaoGestor(final String situacaoGestor) {
		this.situacaoGestor = situacaoGestor;
	}

	public boolean isEncerrada() {
		return this.situacaoConta.equals(DominioSituacaoConta.E);
	}

	public void setSelected(final Boolean selected) {
		this.selected = selected;
	}

	public Boolean getSelected() {
		return selected;
	}
}
