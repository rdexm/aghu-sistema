package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class AacConsultasVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2615739463019165468L;
	
	private Date dataConsulta;
	private String descricaoCondAtendimento;
	private String descricaoSituacao;
	private Integer numero;
	private Integer prontuario;
	private Integer codigoPaciente;
	private String nomePaciente;
	private String descricaoRetorno;
	private Date alteradoEm;
	private String nomeServidor;
	
	
	public enum Fields {
		DATA_CONSULTA("dataConsulta"),
		DESC_COD_ANTEND("descricaoCondAtendimento"),
		DESC_SITUACAO("descricaoSituacao"),
		NUMERO("numero"),
		PRONTUARIO("prontuario"),
		CODIGO_PAC("codigoPaciente"),
		NOME_PAC("nomePaciente"),
		DESCRICAO_RETORNO("descricaoRetorno"),
		ALTERADO_EM("alteradoEm"),
		NOME_SERVIDOR("nomeServidor");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}


	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getDescricaoCondAtendimento() {
		return descricaoCondAtendimento;
	}

	public void setDescricaoCondAtendimento(String descricaoCondAtendimento) {
		this.descricaoCondAtendimento = descricaoCondAtendimento;
	}

	public String getDescricaoSituacao() {
		return descricaoSituacao;
	}

	public void setDescricaoSituacao(String descricaoSituacao) {
		this.descricaoSituacao = descricaoSituacao;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
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

	public String getDescricaoRetorno() {
		return descricaoRetorno;
	}

	public void setDescricaoRetorno(String descricaoRetorno) {
		this.descricaoRetorno = descricaoRetorno;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}
}