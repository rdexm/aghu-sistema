package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;

// --- dados da APAC para associar itens
public class FatPeriodoApacVO {
	private Integer atdSeq;
	private DominioTipoTratamentoAtendimento indTipoTratamento;
	private Integer codigoPaciente;
	private Date dtInicioValidadeContaApac;
	private Date dtObitoAltaContaApac;
	private Date dtFimValidadeContaApac;
	private DominioSituacaoCompetencia indSituacaoCompetencia;
	private Date vlrDataParametro;
	private Date dtHrFimCompetencia;

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public DominioTipoTratamentoAtendimento getIndTipoTratamento() {
		return indTipoTratamento;
	}

	public void setIndTipoTratamento(DominioTipoTratamentoAtendimento indTipoTratamento) {
		this.indTipoTratamento = indTipoTratamento;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Date getDtInicioValidadeContaApac() {
		return dtInicioValidadeContaApac;
	}

	public void setDtInicioValidadeContaApac(Date dtInicioValidadeContaApac) {
		this.dtInicioValidadeContaApac = dtInicioValidadeContaApac;
	}

	public Date getDtObitoAltaContaApac() {
		return dtObitoAltaContaApac;
	}

	public void setDtObitoAltaContaApac(Date dtObitoAltaContaApac) {
		this.dtObitoAltaContaApac = dtObitoAltaContaApac;
	}

	public Date getDtFimValidadeContaApac() {
		return dtFimValidadeContaApac;
	}

	public void setDtFimValidadeContaApac(Date dtFimValidadeContaApac) {
		this.dtFimValidadeContaApac = dtFimValidadeContaApac;
	}

	public DominioSituacaoCompetencia getIndSituacaoCompetencia() {
		return indSituacaoCompetencia;
	}

	public void setIndSituacaoCompetencia(DominioSituacaoCompetencia indSituacaoCompetencia) {
		this.indSituacaoCompetencia = indSituacaoCompetencia;
	}

	public Date getVlrDataParametro() {
		return vlrDataParametro;
	}

	public void setVlrDataParametro(Date vlrDataParametro) {
		this.vlrDataParametro = vlrDataParametro;
	}

	public Date getDtHrFimCompetencia() {
		return dtHrFimCompetencia;
	}

	public void setDtHrFimCompetencia(Date dtHrFimCompetencia) {
		this.dtHrFimCompetencia = dtHrFimCompetencia;
	}

	public Date getDataFimValidade() {
		if (getDtObitoAltaContaApac() == null) {
			if (getDtFimValidadeContaApac() == null) { // -- Milena -
														// Outubro/2004 (PAR.)
				if (DominioSituacaoCompetencia.A.equals(getIndSituacaoCompetencia())) { // --
																						// Milena
																						// -
																						// JANEIRO/2005
																						// (DECODE)
					return getVlrDataParametro();
				} else {
					return getDtHrFimCompetencia();
				}
			} else {
				return getDtFimValidadeContaApac();
			}
		} else {
			return getDtObitoAltaContaApac();
		}
	}
}
