package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class ListaOcorrenciaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1906437550474439327L;

	private String descricao02;// ocorrencia
		
	private String descricao03; // medicamento 
	
	private String descricao04;// unidadeSolicitante	
	
	private Integer prontuario;
	
	private String local;
	
	private String nome;	 
	
	private String qtdeDispensada;
	
	private BigDecimal qtdeDispensada1;	 
	
	private BigDecimal qtdeEstornada;	 
	
	private String descricaoOcorrencia;
	
	private Date dthrTriado;
	
	private String descricaoMedicamento;
	
	private BigDecimal concentracao;
	
	private String descricaoUnidMedMedicas;
	
	private String andar;
	
	private String indAla;
	
	private String siglaUnidSolicitante;
	
	private String descricaoUnidSolicitante;
	
	private String siglaApresMed;
	
	private String leitoID;
	
	private Short numeroQuarto;
	
	private Short seqUF;
	
	private Integer seqAtd;
	
	
	public enum Fields {
		NOME("nome"),
		PRONTUARIO("prontuario"),
		DESCRICAO_OCORRENCIA("descricaoOcorrencia"),
		DT_HR_TRIADO("dthrTriado"),
		DESCRICAO_MEDICAMENTO("descricaoMedicamento"),
		CONCENTRACAO("concentracao"),
		DESCRICAO_UNID_MED_MEDICAS("descricaoUnidMedMedicas"),
		ANDAR("andar"),
		IND_ALA("indAla"),
		SIGLA_UNID_SOLICITANTE("siglaUnidSolicitante"),
		DESCRICAO_UNID_SOLICITANTE("descricaoUnidSolicitante"),
		SIGLA_APRES_MED("siglaApresMed"),
		LEITO_ID("leitoID"),
		NUMERO_QUARTO("numeroQuarto"),
		QTDE_DISPENSADA1("qtdeDispensada1"),
		QTDE_ESTORNADA("qtdeEstornada"),
		SEQ_UF("seqUF"),
		SEQ_ATD("seqAtd");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
		
	// GETTERS and SETTERS

	public String getDescricao02() {
		return descricao02;
	}

	public void setDescricao02(String descricao02) {
		this.descricao02 = descricao02;
	}

	public String getDescricao03() {
		return descricao03;
	}

	public void setDescricao03(String descricao03) {
		this.descricao03 = descricao03;
	}

	public String getDescricao04() {
		return descricao04;
	}

	public void setDescricao04(String descricao04) {
		this.descricao04 = descricao04;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getQtdeDispensada() {
		return qtdeDispensada;
	}

	public void setQtdeDispensada(String qtdeDispensada) {
		this.qtdeDispensada = qtdeDispensada;
	}

	public BigDecimal getQtdeDispensada1() {
		return qtdeDispensada1;
	}

	public void setQtdeDispensada1(BigDecimal qtdeDispensada1) {
		this.qtdeDispensada1 = qtdeDispensada1;
	}

	public String getDescricaoOcorrencia() {
		return descricaoOcorrencia;
	}

	public void setDescricaoOcorrencia(String descricaoOcorrencia) {
		this.descricaoOcorrencia = descricaoOcorrencia;
	}

	public Date getDthrTriado() {
		return dthrTriado;
	}

	public void setDthrTriado(Date dthrTriado) {
		this.dthrTriado = dthrTriado;
	}

	public String getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public void setDescricaoMedicamento(String descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public BigDecimal getConcentracao() {
		return concentracao;
	}

	public void setConcentracao(BigDecimal concentracao) {
		this.concentracao = concentracao;
	}

	public String getDescricaoUnidMedMedicas() {
		return descricaoUnidMedMedicas;
	}

	public void setDescricaoUnidMedMedicas(String descricaoUnidMedMedicas) {
		this.descricaoUnidMedMedicas = descricaoUnidMedMedicas;
	}

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}

	public String getDescricaoUnidSolicitante() {
		return descricaoUnidSolicitante;
	}

	public void setDescricaoUnidSolicitante(String descricaoUnidSolicitante) {
		this.descricaoUnidSolicitante = descricaoUnidSolicitante;
	}

	public String getSiglaApresMed() {
		return siglaApresMed;
	}

	public void setSiglaApresMed(String siglaApresMed) {
		this.siglaApresMed = siglaApresMed;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	public Short getSeqUF() {
		return seqUF;
	}

	public void setSeqUF(Short seqUF) {
		this.seqUF = seqUF;
	}

	public String getSiglaUnidSolicitante() {
		return siglaUnidSolicitante;
	}

	public void setSiglaUnidSolicitante(String siglaUnidSolicitante) {
		this.siglaUnidSolicitante = siglaUnidSolicitante;
	}

	public Integer getSeqAtd() {
		return seqAtd;
	}

	public void setSeqAtd(Integer seqAtd) {
		this.seqAtd = seqAtd;
	}

	public BigDecimal getQtdeEstornada() {
		return qtdeEstornada;
	}

	public void setQtdeEstornada(BigDecimal qtdeEstornada) {
		this.qtdeEstornada = qtdeEstornada;
	}

	public String getIndAla() {
		return indAla;
	}

	public void setIndAla(String indAla) {
		this.indAla = indAla;
	}
}
