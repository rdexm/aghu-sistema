package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghParametros;

public class InformacaoAgendaVO {
	
	private Integer seq;
	private Date dtAgenda;
	private DominioSituacaoAgendas indSituacao;
	private Integer pacCodigo;
	private Integer eprPciSeq;
	private Short eprEspSeq;
	private Short cspCnvCodigo;
	private Byte cspSeq;
	private Short cnvSus;
	private Byte cspIntSeq;
	private Date dthrInclusao;
	private Date dtBase;
	private Integer qtdDiasLimiteCirg;
	
	
	public InformacaoAgendaVO(Integer seq, Date dtAgenda,
			DominioSituacaoAgendas indSituacao, Integer pacCodigo, Integer eprPciSeq,
			Short eprEspSeq, Short cspCnvCodigo, Byte cspSeq,
			Short cnvSus, Byte cspIntSeq, Date dthrInclusao,
			Integer qtdDiasLimiteCirgSoma, Date dtBase,
			Integer qtdDiasLimiteCirg) {
		super();
		this.seq = seq;
		this.dtAgenda = dtAgenda;
		this.indSituacao = indSituacao;
		this.pacCodigo = pacCodigo;
		this.eprPciSeq = eprPciSeq;
		this.eprEspSeq = eprEspSeq;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.cnvSus = cnvSus;
		this.cspIntSeq = cspIntSeq;
		this.dthrInclusao = dthrInclusao;
		this.dtBase = dtBase;
		this.qtdDiasLimiteCirg = qtdDiasLimiteCirg;
	}

	public InformacaoAgendaVO() {}
	
	public InformacaoAgendaVO(CadastroPlanejamentoVO cadastroPlanejamentoVO, AghParametros convenioSusPadrao, AghParametros susPlanoInternacao) {
		//InformacaoAgendaVO vo = new InformacaoAgendaVO();
		if (cadastroPlanejamentoVO.getPaciente() != null) {
			this.setPacCodigo(cadastroPlanejamentoVO.getPaciente().getCodigo());
		}
		this.setCspCnvCodigo(cadastroPlanejamentoVO.getFatConvenioSaude() == null ? convenioSusPadrao.getVlrNumerico().shortValue() : cadastroPlanejamentoVO.getFatConvenioSaude().getCodigo());
		this.setCspSeq(cadastroPlanejamentoVO.getConvenioSaudePlano() == null ? susPlanoInternacao.getVlrNumerico().byteValue() : cadastroPlanejamentoVO.getConvenioSaudePlano().getId().getSeq());
		this.setCnvSus(convenioSusPadrao.getVlrNumerico().shortValue());
		this.setCspIntSeq(susPlanoInternacao.getVlrNumerico().byteValue());
		if (cadastroPlanejamentoVO.getUnidadeFuncional() != null) {
			this.setQtdDiasLimiteCirg( cadastroPlanejamentoVO.getUnidadeFuncional().getQtdDiasLimiteCirg().intValue());
		}

	}

	public Integer getSeq() {
		return seq;
	}


	public Date getDtAgenda() {
		return dtAgenda;
	}


	public DominioSituacaoAgendas getIndSituacao() {
		return indSituacao;
	}


	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public Integer getEprPciSeq() {
		return eprPciSeq;
	}


	public Short getEprEspSeq() {
		return eprEspSeq;
	}


	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}


	public Byte getCspSeq() {
		return cspSeq;
	}


	public Short getCnvSus() {
		return cnvSus;
	}


	public Byte getCspIntSeq() {
		return cspIntSeq;
	}


	public Date getDthrInclusao() {
		return dthrInclusao;
	}


	public Date getDtBase() {
		return dtBase;
	}


	public Integer getQtdDiasLimiteCirg() {
		return qtdDiasLimiteCirg;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public void setDtAgenda(Date dtAgenda) {
		this.dtAgenda = dtAgenda;
	}


	public void setIndSituacao(DominioSituacaoAgendas indSituacao) {
		this.indSituacao = indSituacao;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}


	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}


	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}


	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}


	public void setCnvSus(Short cnvSus) {
		this.cnvSus = cnvSus;
	}


	public void setCspIntSeq(Byte cspIntSeq) {
		this.cspIntSeq = cspIntSeq;
	}


	public void setDthrInclusao(Date dthrInclusao) {
		this.dthrInclusao = dthrInclusao;
	}


	public void setDtBase(Date dtBase) {
		this.dtBase = dtBase;
	}


	public void setQtdDiasLimiteCirg(Integer qtdDiasLimiteCirg) {
		this.qtdDiasLimiteCirg = qtdDiasLimiteCirg;
	}

	public enum Fields {
		
		SEQ("seq"), 
		DT_AGENDA("dtAgenda"),
		IND_SITUACAO("indSituacao"),
		PAC_CODIGO("pacCodigo"),
		EPR_PCI_SEQ("eprPciSeq"),
		EPR_ESP_SEQ("eprEspSeq"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		CNV_SUS("cnvSus"),
		CSP_INT_SEQ("cspIntSeq"),
		DTHR_INCLUSAO("dthrInclusao"),
		QTD_DIAS_LIMITE_CIRG("qtdDiasLimiteCirg"),
		DT_BASE("dtBase");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
}
