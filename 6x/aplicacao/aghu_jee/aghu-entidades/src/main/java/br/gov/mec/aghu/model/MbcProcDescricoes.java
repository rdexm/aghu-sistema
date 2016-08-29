package br.gov.mec.aghu.model;

// Generated 19/04/2012 16:57:27 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_PROC_DESCRICOES", schema = "AGH")
public class MbcProcDescricoes extends BaseEntityId<MbcProcDescricoesId> implements java.io.Serializable {

	private static final long serialVersionUID = -37018339204592692L;
	private MbcProcDescricoesId id;
	private Integer version;
	private DominioIndContaminacao indContaminacao;
	private String complemento;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private Date criadoEm;
	private RapServidores servidor;
	private MbcDescricaoCirurgica mbcDescricaoCirurgica;
	private Set<MciPista> mciPistaes = new HashSet<MciPista>(0);
	
	public MbcProcDescricoes() {
	}

	public MbcProcDescricoes(MbcProcDescricoesId id, DominioIndContaminacao indContaminacao,
			MbcProcedimentoCirurgicos procedimentoCirurgico, Date criadoEm, RapServidores servidor) {
		this.id = id;
		this.indContaminacao = indContaminacao;
		this.procedimentoCirurgico = procedimentoCirurgico;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	public MbcProcDescricoes(MbcProcDescricoesId id, DominioIndContaminacao indContaminacao,
			String complemento, MbcProcedimentoCirurgicos procedimentoCirurgico, Date criadoEm, RapServidores servidor) {
		this.id = id;
		this.indContaminacao = indContaminacao;
		this.complemento = complemento;
		this.procedimentoCirurgico = procedimentoCirurgico;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "dcgCrgSeq", column = @Column(name = "DCG_CRG_SEQ", nullable = false)),
			@AttributeOverride(name = "dcgSeqp", column = @Column(name = "DCG_SEQP", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MbcProcDescricoesId getId() {
		return this.id;
	}

	public void setId(MbcProcDescricoesId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_CONTAMINACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndContaminacao getIndContaminacao() {
		return this.indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	@Column(name = "COMPLEMENTO", length = 500)
	@Length(max = 500)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	public void setProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false)
	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DCG_CRG_SEQ", referencedColumnName = "CRG_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DCG_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MbcDescricaoCirurgica getMbcDescricaoCirurgica() {
		return this.mbcDescricaoCirurgica;
	}

	public void setMbcDescricaoCirurgica(MbcDescricaoCirurgica mbcDescricaoCirurgica) {
		this.mbcDescricaoCirurgica = mbcDescricaoCirurgica;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcProcDescricoes")
	public Set<MciPista> getMciPistaes() {
		return this.mciPistaes;
	}

	public void setMciPistaes(Set<MciPista> mciPistaes) {
		this.mciPistaes = mciPistaes;
	}

	public enum Fields {
		DCG_CRG_SEQ("id.dcgCrgSeq"),
		DCG_SEQP("id.dcgSeqp"),
		SEQP("id.seqp"),
		PROCEDIMENTO_CIRURGICO("procedimentoCirurgico"),
		PCI_SEQ("procedimentoCirurgico.seq"),
		IND_CONTAMINACAO("indContaminacao"),
		DESCRICAO_CIRURGICA("mbcDescricaoCirurgica");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof MbcProcDescricoes)) {
			return false;
		}
		MbcProcDescricoes other = (MbcProcDescricoes) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
