package br.gov.mec.aghu.faturamento.vo;

import java.sql.Timestamp;

public class AihPorProcedimentoVO {

	// br.gov.mec.aghu.faturamento.vo.AihPorProcedimentoVO
	
	private Integer codigo;

	private Integer prontuario;

	private String nome;

	private String cidade;

	private Long aih;

	private String procedimento;

	private Timestamp internacao;

	private Timestamp alta;

	public enum Fields {
		
		CODIGO("codigo"),
		PRONTUARIO("prontuario"),
		NOME("nome"),
		CIDADE("cidade"),
		AIH("aih"),
		PROCEDIMENTO("procedimento"),
		INTERNACAO("internacao"),
		ALTA("alta"),
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

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Long getAih() {
		return aih;
	}

	public void setAih(Long aih) {
		this.aih = aih;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public Timestamp getInternacao() {
		return internacao;
	}

	public void setInternacao(Timestamp internacao) {
		this.internacao = internacao;
	}

	public Timestamp getAlta() {
		return alta;
	}

	public void setAlta(Timestamp alta) {
		this.alta = alta;
	}
}