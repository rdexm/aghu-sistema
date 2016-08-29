package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class ProtocoloAihVO {

	private Integer prontuario;
	private String nome;
	private Integer cthSeq;
	private Date dataInternacao;
	private Date dataAlta;
	private String tipo;
	private Long codTabela;
	private String descricao;
	
	public ProtocoloAihVO() {
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

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Date getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {
				NOME("nome"), PROCEDIMENTO("descricao"), CTH_SEQ(
				"cthSeq"), DATA_INTERNACAO("dataInternacao"), DATA_ALTA("dataAlta"), PRONTUARIO(
				"prontuario"), TIPO("tipo"), COD_TABELA("codTabela");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
