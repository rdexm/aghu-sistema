package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioTipoNotificacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "EPE_NOTIFICACOES", schema = "AGH")
@SequenceGenerator(name = "epeNtfSq1", sequenceName = "AGH.EPE_NTF_SQ1", allocationSize = 1)
public class EpeNotificacao extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -332405486149279885L;

	private Integer seq;

	private Date criadoEm;
	private Date data;
	private DominioTipoNotificacao tipo;

	private AghAtendimentos atendimento;
	private RapServidores servidor;

	// construtores

	public EpeNotificacao() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 8, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "epeNtfSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DATA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "TIPO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoNotificacao getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoNotificacao tipo) {
		this.tipo = tipo;
	}

	@ManyToOne
	@JoinColumn(name = "ATD_SEQ", referencedColumnName = "SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EpeNotificacao)) {
			return false;
		}
		EpeNotificacao castOther = (EpeNotificacao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), CRIADO_EM("criadoEm"), DATA("data"), TIPO("tipo"),
		AGH_ATENDIMENTOS("atendimento"), SERVIDOR("servidor");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}