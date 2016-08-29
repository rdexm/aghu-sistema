package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

public class NodoEspecialidadeEmergenciaPOLVO {

	//atendimento
	private Integer atdSeq;
	private Integer pacCodigo;
	private Integer prontuario;
	private Short espSeq;
	private Date dataOrd;
	
	//especialidade
	private String nomeReduzido;
	private String nomeEspecialidade;
	private String sigla;
	private Short espSeqPai;
	
	//gradeAgendamentos
	private Short serVinCodigo;
	private Integer serMatricula;
	
	
	public NodoEspecialidadeEmergenciaPOLVO(){	}
	
	public NodoEspecialidadeEmergenciaPOLVO(Integer atdSeq,Integer pacCodigo,Integer prontuario,
											Short espSeq,Date dataOrd,String nomeReduzido,String nomeEspecialidade,
											String sigla,Short espSeqPai,Short serVinCodigo,Integer serMatricula){
		
		super();
		this.atdSeq = atdSeq;
		this.pacCodigo = pacCodigo;
		this.prontuario = prontuario;
		this.espSeq = espSeq;
		this.dataOrd = dataOrd;
		this.nomeReduzido = nomeReduzido;
		this.nomeEspecialidade = nomeEspecialidade;
		this.sigla = sigla;
		this.espSeqPai = espSeqPai;
		this.serVinCodigo = serVinCodigo;
		this.serMatricula = serMatricula;
	}

	
	public enum Fields {

		ATD_SEQ("atdSeq"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		ESPECIALIDADE("espSeq"),
		DTHR_INICIO("dataOrd"),
		NOME_REDUZIDO("nomeReduzido"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		SIGLA("sigla"),
		SEQ("espSeqPai"),
		PRE_SER_VIN_CODIGO("serVinCodigo"),
		PRE_SER_MATRICULA("serMatricula")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public Date getDataOrd() {
		return dataOrd;
	}
	public void setDataOrd(Date dataOrd) {
		this.dataOrd = dataOrd;
	}
	public String getNomeReduzido() {
		return nomeReduzido;
	}
	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}
	public String getNomeEspecialiadade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialiadade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public Short getEspSeqPai() {
		return espSeqPai;
	}
	public void setEspSeqPai(Short espSeqPai) {
		this.espSeqPai = espSeqPai;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	
	
	
}
