package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

public class DescricaoCirurgiaPdtSalaVO {
	
	private Integer crgSeq;
	private Short dcgSeqp;
	private Date dataCrg;
	private Short sciSeqp;
	private Integer pacCodigo;
	private String sala;
	private Date dthrInicioCirg; 
	private Date dthrFimCirg;
	private String nomeUsual;
	private String nome;
	private String equipe;
	private Integer serMatriculaProf;
	private Short serVinCodigoProf;
	private Integer ddtSeq;
	private Date dthrInicioPdt;
	private Date dthrFimPdt;

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	public Date getDataCrg() {
		return dataCrg;
	}

	public void setDataCrg(Date dataCrg) {
		this.dataCrg = dataCrg;
	}

	public Short getSciSeqp() {
		return sciSeqp;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public Date getDthrInicioCirg() {
		return dthrInicioCirg;
	}

	public void setDthrInicioCirg(Date dthrInicioCirg) {
		this.dthrInicioCirg = dthrInicioCirg;
	}

	public Date getDthrFimCirg() {
		return dthrFimCirg;
	}

	public void setDthrFimCirg(Date dthrFimCirg) {
		this.dthrFimCirg = dthrFimCirg;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public Integer getSerMatriculaProf() {
		return serMatriculaProf;
	}

	public void setSerMatriculaProf(Integer serMatriculaProf) {
		this.serMatriculaProf = serMatriculaProf;
	}

	public Short getSerVinCodigoProf() {
		return serVinCodigoProf;
	}

	public void setSerVinCodigoProf(Short serVinCodigoProf) {
		this.serVinCodigoProf = serVinCodigoProf;
	}
	
	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public Date getDthrInicioPdt() {
		return dthrInicioPdt;
	}

	public void setDthrInicioPdt(Date dthrInicioPdt) {
		this.dthrInicioPdt = dthrInicioPdt;
	}

	public Date getDthrFimPdt() {
		return dthrFimPdt;
	}

	public void setDthrFimPdt(Date dthrFimPdt) {
		this.dthrFimPdt = dthrFimPdt;
	}


	public enum Fields {
		
		CRG_SEQ("crgSeq"),
		DCG_SEQP("dcgSeqp"),
		DATA_CRG("dataCrg"),
		SCI_SEQP("sciSeqp"),
		PAC_CODIGO("pacCodigo"),
		SALA("sala"),
		DTHR_INICIO_CIRG("dthrInicioCirg"), 
		DTHR_FIM_CIRG("dthrFimCirg"),
		NOME_USUAL("nomeUsual"),
		NOME("nome"),
		SER_MATRICULA_PROF("serMatriculaProf"),
		SER_VIN_CODIGO_PROF("serVinCodigoProf"),
		DDT_SEQ("ddtSeq"),
		DTHR_INICIO_PDT("dthrInicioPdt"),
		DTHR_FIM_PDT("dthrFimPdt");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}
