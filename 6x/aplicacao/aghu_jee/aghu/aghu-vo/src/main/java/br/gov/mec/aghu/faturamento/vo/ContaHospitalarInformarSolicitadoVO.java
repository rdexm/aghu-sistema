package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.core.commons.BaseBean;


public class ContaHospitalarInformarSolicitadoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7547697864840336946L;

	private Integer seqContaHospitalar;

	private BigDecimal seqInternacao;
	
	private Integer prontuarioPaciente;
	
	private Integer codigoPaciente;

	private String nomePaciente;

	private Long numeroAih;

	private String leitoId;

	private DominioSituacaoConta situacaoContaHospitalar;

	private Date dataHoraInternacao;
	
	public Integer getSeqContaHospitalar() {
		return seqContaHospitalar;
	}

	public void setSeqContaHospitalar(Integer seqContaHospitalar) {
		this.seqContaHospitalar = seqContaHospitalar;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Long getNumeroAih() {
		return numeroAih;
	}

	public void setNumeroAih(Long numeroAih) {
		this.numeroAih = numeroAih;
	}

	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	public DominioSituacaoConta getSituacaoContaHospitalar() {
		return situacaoContaHospitalar;
	}

	public void setSituacaoContaHospitalar(DominioSituacaoConta situacaoContaHospitalar) {
		this.situacaoContaHospitalar = situacaoContaHospitalar;
	}

	public Date getDataHoraInternacao() {
		return dataHoraInternacao;
	}

	public void setDataHoraInternacao(Date dataHoraInternacao) {
		this.dataHoraInternacao = dataHoraInternacao;
	}

	public BigDecimal getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(BigDecimal seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public enum Fields {

		CTH_SEQ("seqContaHospitalar"),
		INT_SEQ("seqInternacao"),
		PAC_PRONTUARIO("prontuarioPaciente"),
		PAC_CODIGO("codigoPaciente"),
		PAC_NOME("nomePaciente"),
		CTH_NRO_AIH("numeroAih"),
		INT_LTO_ID("leitoId"),
		CTH_IND_SITUACAO("situacaoContaHospitalar"),
		DTHR_INTERNACAO("dataHoraInternacao");
		
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
