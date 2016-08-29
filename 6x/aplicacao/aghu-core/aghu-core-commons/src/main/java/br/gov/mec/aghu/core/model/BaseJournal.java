package br.gov.mec.aghu.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntity;

/**
 * Classe abstrata que todo Journal deve extender. Contém os campos comuns a
 * todos e também a PK -> SEQ_JN.
 * 
 * @author riccosta
 */

@MappedSuperclass
public abstract class BaseJournal implements BaseEntity {
	
	private static final long serialVersionUID = 2260661982370229504L;
	
	
	private enum BaseJournalExceptionCode implements BusinessExceptionCode {
		ERRO_NOME_USUARIO_INFORMADO
		, ERRO_OPERACAO_INFORMADO
	}

	private Integer seqJn;
	private String nomeUsuario;
	private Date dataAlteracao;
	private DominioOperacoesJournal operacao;

	public BaseJournal() {
		super();
		this.dataAlteracao = new Date();
	}

	@Column(name = "SEQ_JN", insertable = false, updatable = false)
	public Integer getSeqJn() {
		return seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	@NotNull
	@Length(max = 30, message = "Nome de usuário tem mais de 30 caracteres.")
	public String getNomeUsuario() {
		return this.nomeUsuario;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioOperacoesJournal getOperacao() {
		return this.operacao;
	}

	@Column(name = "JN_DATE_TIME", nullable = false, length = 7)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	public Date getDataAlteracao() {
		return this.dataAlteracao;
	}

	public void setNomeUsuario(String nomeUsuario) {
		if (this.nomeUsuario != null && !this.nomeUsuario.equals(nomeUsuario)) {
			throw new BaseRuntimeException(
					BaseJournalExceptionCode.ERRO_NOME_USUARIO_INFORMADO);
		}
		this.nomeUsuario = nomeUsuario;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		if (this.operacao != null && !this.operacao.equals(operacao)) {
			throw new BaseRuntimeException(
					BaseJournalExceptionCode.ERRO_OPERACAO_INFORMADO);
		}
		this.operacao = operacao;
	}

	/**
	 * Responsabilidade de manutencao da sub-classe.
	 * @param seqJn
	 */
	void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}
	
	/**
	 * Setado no construtor desta classe.
	 * @param dataAlteracao
	 */
	void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public enum Fields {
		DATA_ALTERACAO("dataAlteracao"), OPERACAO("operacao"), NOME_USUARIO(
				"nomeUsuario");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataAlteracao == null) ? 0 : dataAlteracao.hashCode());
		result = prime * result
				+ ((nomeUsuario == null) ? 0 : nomeUsuario.hashCode());
		result = prime * result
				+ ((operacao == null) ? 0 : operacao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BaseJournal other = (BaseJournal) obj;
		if (dataAlteracao == null) {
			if (other.dataAlteracao != null) {
				return false;
			}
		} else if (!dataAlteracao.equals(other.dataAlteracao)) {
			return false;
		}
		if (nomeUsuario == null) {
			if (other.nomeUsuario != null) {
				return false;
			}
		} else if (!nomeUsuario.equals(other.nomeUsuario)) {
			return false;
		}
		if (operacao == null) {
			if (other.operacao != null) {
				return false;
			}
		} else if (!operacao.equals(other.operacao)) {
			return false;
		}
		return true;
	}

}
