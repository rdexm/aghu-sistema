package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class CursorVerificacaoDtNascDtInternacaoVO implements Serializable {

	private static final long serialVersionUID = -8394111277893692298L;

	private Date dataNascimento;
	private Date dataInternacao;

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public enum Fields {
		DATA_NASCIMENTO("dataNascimento"),
		DATA_INTERNACAO("dataInternacao")
		;

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
