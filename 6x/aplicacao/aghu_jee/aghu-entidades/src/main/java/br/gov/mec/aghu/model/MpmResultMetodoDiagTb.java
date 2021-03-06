package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MpmResultMetodoDiagTb generated by hbm2java
 */
@Entity
@Table(name = "MPM_RESULT_METODO_DIAG_TBS", schema = "AGH")
public class MpmResultMetodoDiagTb extends BaseEntityId<MpmResultMetodoDiagTbId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4207400916149297427L;
	private MpmResultMetodoDiagTbId id;
	private MpmMetodoDiagnosticoTb metodoDiagnosticoTuberculose;
	private RapServidores servidor;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao situacao;
	private Set<MpmMetodoDiagTbAlta> metodosDiagnosticoTuberculoseAlta = new HashSet<MpmMetodoDiagTbAlta>(
			0);

	public MpmResultMetodoDiagTb() {
	}

	public MpmResultMetodoDiagTb(MpmResultMetodoDiagTbId id,
			MpmMetodoDiagnosticoTb metodoDiagnosticoTuberculose, RapServidores servidor, String descricao, Date criadoEm,
			DominioSituacao situacao) {
		this.id = id;
		this.metodoDiagnosticoTuberculose = metodoDiagnosticoTuberculose;
		this.servidor = servidor;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
	}

	public MpmResultMetodoDiagTb(MpmResultMetodoDiagTbId id,
			MpmMetodoDiagnosticoTb metodoDiagnosticoTuberculose, RapServidores servidor, String descricao, Date criadoEm,
			DominioSituacao situacao,
			Set<MpmMetodoDiagTbAlta> metodosDiagnosticoTuberculoseAlta) {
		this.id = id;
		this.metodoDiagnosticoTuberculose = metodoDiagnosticoTuberculose;
		this.servidor = servidor;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
		this.metodosDiagnosticoTuberculoseAlta = metodosDiagnosticoTuberculoseAlta;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "mdtSeq", column = @Column(name = "MDT_SEQ", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 2, scale = 0)) })
	public MpmResultMetodoDiagTbId getId() {
		return this.id;
	}

	public void setId(MpmResultMetodoDiagTbId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDT_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmMetodoDiagnosticoTb getMetodoDiagnosticoTuberculose() {
		return this.metodoDiagnosticoTuberculose;
	}

	public void setMetodoDiagnosticoTuberculose(
			MpmMetodoDiagnosticoTb metodoDiagnosticoTuberculose) {
		this.metodoDiagnosticoTuberculose = metodoDiagnosticoTuberculose;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpmResultMetodoDiagTbs")
	public Set<MpmMetodoDiagTbAlta> getMetodosDiagnosticoTuberculoseAlta() {
		return this.metodosDiagnosticoTuberculoseAlta;
	}

	public void setMetodosDiagnosticoTuberculoseAlta(
			Set<MpmMetodoDiagTbAlta> metodosDiagnosticoTuberculoseAlta) {
		this.metodosDiagnosticoTuberculoseAlta = metodosDiagnosticoTuberculoseAlta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MpmResultMetodoDiagTb other = (MpmResultMetodoDiagTb) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {
		if (this.situacao == null) {
			this.situacao = DominioSituacao.A;
		}
	}
	
	public enum Fields {
		
		MDT_SEQ("id.mdtSeq"),
		SEQP("id.seqp"),
		METODO_DIAGNOSTICO_TUBERCULOSE("metodoDiagnosticoTuberculose"),
		SERVIDOR("servidor"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SITUACAO("situacao"),
		METODOS_DIAGNOSTICO_TUBERCULOSE_ALTA("metodosDiagnosticoTuberculoseAlta");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
		
	}
	
}
