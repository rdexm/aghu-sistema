package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class CursorCTicketRetornoVO implements Serializable {

	
	private static final long serialVersionUID = -1431239652782699608L;

	private Integer conNumeroRetorno;
	private String indQtdeMeses;
	private Short qtdeMeses;
	private String indAposExames;
	private String indData;
	private Date dataRetorno;
	private String indSeNecessario;
	private String indDiaSemana;
	private Short diaSemana;
	private Integer eqpSeq;
	private Short espSeq;
	private Integer codigo;
	private Integer prontuario;	
	private String nome;
	
	public enum Fields {
		CON_NUMERO_RETORNO("conNumeroRetorno"),
		IND_QTDE_MESES("indQtdeMeses"),
		QTDE_MESES("qtdeMeses"),
		IND_APOS_EXAMES("indAposExames"),
		IND_DATA("indData"),
		DATA_RETORNO("dataRetorno"),
		IND_SE_NECESSARIO("indSeNecessario"),
		IND_DIA_SEMANA("indDiaSemana"),
		DIA_SEMANA("diaSemana"),
		EQP_SEQ("eqpSeq"),
		ESP_SEQ("espSeq"),
		CODIGO("codigo"),
		PRONTUARIO("prontuario"),
		NOME("nome")
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public String getIndQtdeMeses() {
		return indQtdeMeses;
	}

	public void setIndQtdeMeses(String indQtdeMeses) {
		this.indQtdeMeses = indQtdeMeses;
	}

	public Short getQtdeMeses() {
		return qtdeMeses;
	}

	public void setQtdeMeses(Short qtdeMeses) {
		this.qtdeMeses = qtdeMeses;
	}

	public String getIndAposExames() {
		return indAposExames;
	}

	public void setIndAposExames(String indAposExames) {
		this.indAposExames = indAposExames;
	}

	public String getIndData() {
		return indData;
	}

	public void setIndData(String indData) {
		this.indData = indData;
	}

	public Date getDataRetorno() {
		return dataRetorno;
	}

	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}

	public String getIndSeNecessario() {
		return indSeNecessario;
	}

	public void setIndSeNecessario(String indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}

	public String getIndDiaSemana() {
		return indDiaSemana;
	}

	public void setIndDiaSemana(String indDiaSemana) {
		this.indDiaSemana = indDiaSemana;
	}

	public Short getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(Short diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Integer getEqpSeq() {
		return eqpSeq;
	}

	public void setEqpSeq(Integer eqpSeq) {
		this.eqpSeq = eqpSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
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

	public Integer getConNumeroRetorno() {
		return conNumeroRetorno;
	}

	public void setConNumeroRetorno(Integer conNumeroRetorno) {
		this.conNumeroRetorno = conNumeroRetorno;
	}
}