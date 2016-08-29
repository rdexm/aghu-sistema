package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AtendimentoPacientesCCIHVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3970290055657640674L;
	
	private Integer prontuario;
	private Integer codPaciente;
	private Integer atdSeq;
	private String nome;
	private Date dataHoraIngresso;
	private String leitoId;
	private String unfDescricao;
	private Short unfSeq;
	private Date dtNascimento;
	private String idade;
	private Date dtInicioInternacao;

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
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

	public Date getDataHoraIngresso() {
		return dataHoraIngresso;
	}

	public void setDataHoraIngresso(Date dataHoraIngresso) {
		this.dataHoraIngresso = dataHoraIngresso;
	}

	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Date getDtInicioInternacao() {
		return dtInicioInternacao;
	}

	public void setDtInicioInternacao(Date dtInicioInternacao) {
		this.dtInicioInternacao = dtInicioInternacao;
	}

	public enum Fields {
		PRONTUARIO("prontuario"),
		COD_PACIENTE("codPaciente"), 
		ATD_SEQ("atdSeq"),
		NOME("nome"),
		DATA_INGRESSO("dataHoraIngresso"),
		LEITO_ID("leitoId"),
		UNF_DESCRICAO("unfDescricao"),
		UNF_SEQ("unfSeq"),
		DT_NASCIMENTO("dtNascimento"),
		DT_INICIO_INTERNACAO("dtInicioInternacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
