package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.core.utils.StringUtil;

@Entity
@Immutable
@Table(name = "ael_notas_adicionais", schema = "hist")
public class AelNotasAdicionaisHist extends BaseEntityId<AelNotasAdicionaisHistId> implements java.io.Serializable {

	private static final long serialVersionUID = 8603393238648210998L;
	private AelNotasAdicionaisHistId id;
	private AelItemSolicExameHist itemSolicitacaoExame;
	private RapServidores servidor;
	private String notasAdicionais;
	private Date criadoEm;
	
	public AelNotasAdicionaisHist() {
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "iseSoeSeq", column = @Column(name = "ISE_SOE_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "iseSeqp", column = @Column(name = "ISE_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)) })
	public AelNotasAdicionaisHistId getId() {
		return this.id;
	}

	public void setId(AelNotasAdicionaisHistId id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelItemSolicExameHist getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(AelItemSolicExameHist itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "NOTAS_ADICIONAIS", length = 4000)
	public String getNotasAdicionais() {
		return notasAdicionais;
	}

	public void setNotasAdicionais(String notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}
	
	@Transient
	public String getNotasAdicionaisTrunc(Long size) {
		return StringUtil.trunc(getNotasAdicionais(),Boolean.TRUE,size);
	}
	

	public enum Fields {

		ID("id"),
		ISE_SOE_SEQ("id.iseSoeSeq"),
		ISE_SEQP("id.iseSeqp"),
		SEQP("id.seqp"),
		ITEM_SOLICITACAO_EXAME("itemSolicitacaoExame"),
		CRIADO_EM("criadoEm"),
		NOTAS_ADICIONAIS("notasAdicionais"),
		SERVIDOR("servidor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof AelNotaAdicional)) {
			return false;
		}
		AelNotaAdicional castOther = (AelNotaAdicional) other;
		return new EqualsBuilder().append(this.getId(), castOther.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getId()).toHashCode();
	}	

}
