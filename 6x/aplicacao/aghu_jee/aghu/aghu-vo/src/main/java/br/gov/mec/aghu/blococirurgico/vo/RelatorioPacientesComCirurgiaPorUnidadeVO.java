package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;


public class RelatorioPacientesComCirurgiaPorUnidadeVO implements Serializable {
	
	private static final long serialVersionUID = -962601384321968243L;

	private String fInternacao;				//2
	private String quarto; 					//10
	private Date dtHrCirurgia;				//11
	private Short sciSeqp;					//12
	private Short convenio;					//13
	private Integer pacProntuario;			//14
	private String pacProntuarioString;			//14
	private String pacNome;					//15
//	private String nciDescricao;			//16 concatenado em requisições
	private Short nroAgenda;				//18
//	private String fSangue;					//19 concatenado em requisições
	private String cirurgiao;				//21	
	private String anestesista;				//23
	private String procedimento;			//25
	private Date dataHora;
	private String titulo;
	private String requisicoes;
	private Integer pacCodigo;
	private Integer seqCirurgia;
	
	//Campos ordenação
	private Short atdUnfSeq;
	private Integer quartoOrder;
	
	
	public RelatorioPacientesComCirurgiaPorUnidadeVO() {
		
	}
	
	public enum Fields {

		ATD_UNF_SEQ("atdUnfSeq"),
		QUARTO("quarto"),
		QUARTO_ORDER("quartoOrder"),
		F_INTERNACAO("fInternacao"),
		DTHR_CIRURGIA("dtHrCirurgia"),
		SCI_SEQP("sciSeqp"),
		CONVENIO("convenio"),
		PAC_PRONTUARIO("pacProntuario"),
		PAC_NOME("pacNome"),
		NRO_AGENDA("nroAgenda"),
		ANESTESISTA("anestesista"),
		PROCEDIMENTO("procedimento"),
		DATA_HORA("dataHora"),
		TITULO("titulo"),
		REQUISICOES("requisicoes"),
		PAC_CODIGO("pacCodigo"),
		SEQ_CIRURGIA("seqCirurgia"),
		CIRURGIAO("cirurgiao")
		
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

	//Getters and Setters
	
	public String getfInternacao() {
		return fInternacao;
	}

	public void setfInternacao(String fInternacao) {
		this.fInternacao = fInternacao;
	}

	public String getQuarto() {
		return quarto;
	}

	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}

	public Date getDtHrCirurgia() {
		return dtHrCirurgia;
	}

	public void setDtHrCirurgia(Date dtHrCirurgia) {
		this.dtHrCirurgia = dtHrCirurgia;
	}

	public Short getSciSeqp() {
		return sciSeqp;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public Short getConvenio() {
		return convenio;
	}

	public void setConvenio(Short convenio) {
		this.convenio = convenio;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Short getNroAgenda() {
		return nroAgenda;
	}

	public void setNroAgenda(Short nroAgenda) {
		this.nroAgenda = nroAgenda;
	}

	public String getCirurgiao() {
		return cirurgiao;
	}

	public void setCirurgiao(String cirurgiao) {
		this.cirurgiao = cirurgiao;
	}

	public String getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(String anestesista) {
		this.anestesista = anestesista;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setQuartoOrder(Integer quartoOrder) {
		this.quartoOrder = quartoOrder;
	}

	public Integer getQuartoOrder() {
		return quartoOrder;
	}

	public void setRequisicoes(String requisicoes) {
		this.requisicoes = requisicoes;
	}

	public String getRequisicoes() {
		return requisicoes;
	}

	public void setAtdUnfSeq(Short atdUnfSeq) {
		this.atdUnfSeq = atdUnfSeq;
	}

	public Short getAtdUnfSeq() {
		return atdUnfSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getSeqCirurgia() {
		return seqCirurgia;
	}

	public void setSeqCirurgia(Integer seqCirurgia) {
		this.seqCirurgia = seqCirurgia;
	}

	public String getPacProntuarioString() {
		return pacProntuarioString;
	}

	public void setPacProntuarioString(String pacProntuarioString) {
		this.pacProntuarioString = pacProntuarioString;
	}
}