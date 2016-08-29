package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

public class MbcCirurgiasCursorCirVO {

	private Integer crgSeq;

	private Short dcgSeqp;

	private Date data;

	private Short sciSeqp;

	private Integer pacCodigo;
	
	private String sala;

	private Date dthrInicioCirg;
	
	private Date dthrFimCirg;
	
	private String nomeUsual;

	private String nome;
	
	private String equipSe;

	private Integer serMatriculaProf;

	private Short serVinCodigoProf;
	
	private Integer ddtSeq;

	
	public enum Fields {

		CRG_SEQ("crgSeq"), 
		DCG_SEQP("dcgSeqp"),
		DATA("data"), 
		SCI_SEQP("sciSeqp"), 
		PAC_CODIGO("pacCodigo"), 
		SALA("sala"),
		DTHR_INICIO_CIRG("dthrInicioCirg"),
		DTHR_FIM_CIRG("dthrFimCirg"),
		NOME_USUAL("nomeUsual"),
		NOME("nome"),
		SER_MATRICULA_PROF("serMatriculaProf"), 
		SER_VIN_CODIGO_PROF("serVinCodigoProf"), 
		DDT_SEQ("ddtSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getEquipSe() {
		if(nomeUsual != null){
			return nomeUsual;
		} else {
			if(nome != null && nome.length() > 15){
				return nome.substring(0,15);
			}
		}
		
		return equipSe;
	}

	public void setEquipSe(String equipSe) {
		this.equipSe = equipSe;
	}

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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

}