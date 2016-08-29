package br.gov.mec.aghu.bancosangue.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricao;


public class SolicitacaoHemoterapicaVO {

	private Integer atdSeq;
	private Integer seq;
	private DominioIndPendenteItemPrescricao indPendente;
	private Date dthrFim;
	private Date dthrSolicitacao;
	private Boolean indPacTransplantado;
	private Boolean indColetarAmostra;
	private Boolean indTransfAnteriores;
	private String justificativa;
	private DominioSituacao indSituacao;
	private String observacao;
	private Boolean indUrgente;
	private DominioResponsavelColeta indResponsavelColeta;
	private DominioSituacaoColeta indSituacaoColeta;
	private Date dthrSitEmColeta;
	private Date dthrCancelamentoColeta;
	private Short motivoCancelaColetaSeq;
	private Integer matriculaCancelaColeta;
	private Short vinCodigoCancelaColeta;
	private DominioSituacaoItemPrescricao indSituacaoItem;
	private Boolean indAltAndamento;
	private Short ordemImpressao;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	
	
	public SolicitacaoHemoterapicaVO() {
	}

	public SolicitacaoHemoterapicaVO(Integer atdSeq, Integer seq,
			DominioIndPendenteItemPrescricao indPendente, Date dthrFim,
			Date dthrSolicitacao, Boolean indPacTransplantado,
			Boolean indColetarAmostra, Boolean indTransfAnteriores,
			String justificativa, DominioSituacao indSituacao,
			String observacao, Boolean indUrgente,
			DominioResponsavelColeta indResponsavelColeta,
			DominioSituacaoColeta indSituacaoColeta, Date dthrSitEmColeta,
			Date dthrCancelamentoColeta, Short motivoCancelaColetaSeq,
			Integer matriculaCancelaColeta, Short vinCodigoCancelaColeta,
			DominioSituacaoItemPrescricao indSituacaoItem,
			Boolean indAltAndamento, Short ordemImpressao, Integer iseSoeSeq,
			Short iseSeqp) {
		super();
		this.atdSeq = atdSeq;
		this.seq = seq;
		this.indPendente = indPendente;
		this.dthrFim = dthrFim;
		this.dthrSolicitacao = dthrSolicitacao;
		this.indPacTransplantado = indPacTransplantado;
		this.indColetarAmostra = indColetarAmostra;
		this.indTransfAnteriores = indTransfAnteriores;
		this.justificativa = justificativa;
		this.indSituacao = indSituacao;
		this.observacao = observacao;
		this.indUrgente = indUrgente;
		this.indResponsavelColeta = indResponsavelColeta;
		this.indSituacaoColeta = indSituacaoColeta;
		this.dthrSitEmColeta = dthrSitEmColeta;
		this.dthrCancelamentoColeta = dthrCancelamentoColeta;
		this.motivoCancelaColetaSeq = motivoCancelaColetaSeq;
		this.matriculaCancelaColeta = matriculaCancelaColeta;
		this.vinCodigoCancelaColeta = vinCodigoCancelaColeta;
		this.indSituacaoItem = indSituacaoItem;
		this.indAltAndamento = indAltAndamento;
		this.ordemImpressao = ordemImpressao;
		this.iseSoeSeq = iseSoeSeq;
		this.iseSeqp = iseSeqp;
	}



	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}


	public Integer getSeq() {
		return seq;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public Date getDthrSolicitacao() {
		return dthrSolicitacao;
	}


	public void setDthrSolicitacao(Date dthrSolicitacao) {
		this.dthrSolicitacao = dthrSolicitacao;
	}


	public Boolean getIndPacTransplantado() {
		return indPacTransplantado;
	}


	public void setIndPacTransplantado(Boolean indPacTransplantado) {
		this.indPacTransplantado = indPacTransplantado;
	}


	public Boolean getIndColetarAmostra() {
		return indColetarAmostra;
	}


	public void setIndColetarAmostra(Boolean indColetarAmostra) {
		this.indColetarAmostra = indColetarAmostra;
	}


	public Boolean getIndTransfAnteriores() {
		return indTransfAnteriores;
	}


	public void setIndTransfAnteriores(Boolean indTransfAnteriores) {
		this.indTransfAnteriores = indTransfAnteriores;
	}


	public String getJustificativa() {
		return justificativa;
	}


	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}


	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	public String getObservacao() {
		return observacao;
	}


	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}


	public Boolean getIndUrgente() {
		return indUrgente;
	}


	public void setIndUrgente(Boolean indUrgente) {
		this.indUrgente = indUrgente;
	}


	public DominioResponsavelColeta getIndResponsavelColeta() {
		return indResponsavelColeta;
	}


	public void setIndResponsavelColeta(
			DominioResponsavelColeta indResponsavelColeta) {
		this.indResponsavelColeta = indResponsavelColeta;
	}


	public DominioSituacaoColeta getIndSituacaoColeta() {
		return indSituacaoColeta;
	}


	public void setIndSituacaoColeta(DominioSituacaoColeta indSituacaoColeta) {
		this.indSituacaoColeta = indSituacaoColeta;
	}


	public Date getDthrSitEmColeta() {
		return dthrSitEmColeta;
	}


	public void setDthrSitEmColeta(Date dthrSitEmColeta) {
		this.dthrSitEmColeta = dthrSitEmColeta;
	}


	public Date getDthrCancelamentoColeta() {
		return dthrCancelamentoColeta;
	}


	public void setDthrCancelamentoColeta(Date dthrCancelamentoColeta) {
		this.dthrCancelamentoColeta = dthrCancelamentoColeta;
	}


	public Short getMotivoCancelaColetaSeq() {
		return motivoCancelaColetaSeq;
	}


	public void setMotivoCancelaColetaSeq(Short motivoCancelaColetaSeq) {
		this.motivoCancelaColetaSeq = motivoCancelaColetaSeq;
	}


	public Integer getMatriculaCancelaColeta() {
		return matriculaCancelaColeta;
	}


	public void setMatriculaCancelaColeta(Integer matriculaCancelaColeta) {
		this.matriculaCancelaColeta = matriculaCancelaColeta;
	}


	public Short getVinCodigoCancelaColeta() {
		return vinCodigoCancelaColeta;
	}


	public void setVinCodigoCancelaColeta(Short vinCodigoCancelaColeta) {
		this.vinCodigoCancelaColeta = vinCodigoCancelaColeta;
	}


	public DominioSituacaoItemPrescricao getIndSituacaoItem() {
		return indSituacaoItem;
	}


	public void setIndSituacaoItem(DominioSituacaoItemPrescricao indSituacaoItem) {
		this.indSituacaoItem = indSituacaoItem;
	}


	public Boolean getIndAltAndamento() {
		return indAltAndamento;
	}


	public void setIndAltAndamento(Boolean indAltAndamento) {
		this.indAltAndamento = indAltAndamento;
	}


	public Short getOrdemImpressao() {
		return ordemImpressao;
	}


	public void setOrdemImpressao(Short ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}


	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}


	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}


	public Short getIseSeqp() {
		return iseSeqp;
	}


	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
	
	public DominioIndPendenteItemPrescricao getIndPendente() {
		return indPendente;
	}

	public void setIndPendente(DominioIndPendenteItemPrescricao indPendente) {
		this.indPendente = indPendente;
	}



	public enum Fields {
		ATD_SEQ("atdSeq"),
		SEQ("seq"),
		IND_PENDENTE("indPendente"),
		DTHR_SOLICITACAO("dthrSolicitacao"),
		DTHR_FIM("dthrFim"),
		IND_PAC_TRANSPLANTADO("indPacTransplantado"),
		IND_COLTERA_AMOSTRA("indColetarAmostra"),
		IND_TRANSF_ANTERIORES("indTransfAnteriores"),
		JUSTIFICATIVA("justificativa"),
		IND_SITUACAO("indSituacao"),
		OBSERVACAO("observacao"),
		IND_URGENTE("indUrgente"),
		IND_RESP_COLETA("indResponsavelColeta"),
		IND_SIT_COLETA("indSituacaoColeta"),
		DTHR_SIT_COLETA("dthrSitEmColeta"),
		DTHR_CANC_COLETA("dthrCancelamentoColeta"),
		MOTIVO_CANC_COLT_SEQ("motivoCancelaColetaSeq"),
		MAT_CANC_COLETA("matriculaCancelaColeta"),
		VIN_CANC_COLETA("vinCodigoCancelaColeta"),
		IND_SIT_ITEM("indSituacaoItem"),
		IND_ALT_ANDAMENTO("indAltAndamento"),		
		ISE_SOE_SEQ("iseSoeSeq"),
		ISE_SEQP("iseSeqp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
