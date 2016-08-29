package br.gov.mec.aghu.ambulatorio.vo;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.BaseBean;

public class TransferirExamesVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8340652310834494993L;
	
	private Integer seq;
	private Integer consultaNumero;
	private Integer prontuario;
	private Integer codigoPaciente;
	private String nomePaciente;
	private Short unidade;
	private String descricao;

	public enum Fields {

		SEQ("seq"),
		CONSULTA_NUMERO("consultaNumero"),
		PRONTUARIO("prontuario"),
		CODIGO_PACIENTE("codigoPaciente"),
		NOME_PACIENTE("nomePaciente"),
		UNIDADE("unidade"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getConsultaNumero() {
		return consultaNumero;
	}
	public void setConsultaNumero(Integer consultaNumero) {
		this.consultaNumero = consultaNumero;
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
	public String codigoNome() {
		return (codigoPaciente == null ? StringUtils.EMPTY : codigoPaciente + " - ") + (nomePaciente == null ? StringUtils.EMPTY : nomePaciente);
	}
	
	//C3
	public Short getUnidade() {
		return unidade;
	}
	public void setUnidade(Short unidade) {
		this.unidade = unidade;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
