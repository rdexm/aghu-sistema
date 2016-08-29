package br.gov.mec.aghu.model;

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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the AEL_TXT_DESC_MATS database table.
 * 
 */
@Entity
@Table(name="AEL_TXT_DESC_MATS", schema = "AGH")
public class AelTxtDescMats  extends BaseEntityId<AelTxtDescMatsId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2761327557462714326L;
	
	private AelTxtDescMatsId id;
	private AelGrpTxtDescMats aelGrpTxtDescMats;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private String apelido;
	private Integer version;
	private Set<AelGrpDescMatLacunas> aelGrpDescMatLacuna = new HashSet<AelGrpDescMatLacunas>(
			0);

	public AelTxtDescMats() {
	}

	public AelTxtDescMats(AelTxtDescMatsId id,
			AelGrpTxtDescMats aelGrpTxtDescMats, String descricao,
			DominioSituacao indSituacao, Date criadoEm, Integer serMatricula,
			Short serVinCodigo) {
		this.id = id;
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public AelTxtDescMats(AelTxtDescMatsId id,
			AelGrpTxtDescMats aelGrpTxtDescMats, String descricao,
			DominioSituacao indSituacao, Date criadoEm, String apelido,
			Set<AelGrpDescMatLacunas> aelGrpDescMatLacuna) {
		this.id = id;
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.apelido = apelido;
		this.aelGrpDescMatLacuna = aelGrpDescMatLacuna;
	}
	
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "gtmSeq", column = @Column(name = "GTM_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	@NotNull
	public AelTxtDescMatsId getId() {
		return this.id;
	}

	public void setId(AelTxtDescMatsId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GTM_SEQ", nullable = false, insertable = false, updatable = false)
	@NotNull
	public AelGrpTxtDescMats getAelGrpTxtDescMats() {
		return this.aelGrpTxtDescMats;
	}

	public void setAelGrpTxtDescMats(
			AelGrpTxtDescMats aelGrpTxtDescMats) {
		this.aelGrpTxtDescMats = aelGrpTxtDescMats;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	@NotNull
	@Length(max = 2000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull		
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "APELIDO", length = 50)
	@Length(max = 50)
	public String getApelido() {
		return this.apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelTxtDescMats")
	public Set<AelGrpDescMatLacunas> getAelGrpDescMatLacunas() {
		return this.aelGrpDescMatLacuna;
	}

	public void setAelGrpDescMatLacunas(
			Set<AelGrpDescMatLacunas> aelGrpDescMatLacuna) {
		this.aelGrpDescMatLacuna = aelGrpDescMatLacuna;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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
		AelTxtDescMats other = (AelTxtDescMats) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	

	public enum Fields {
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		APELIDO("apelido"),
		GTM_SEQ("id.gtmSeq"),
		SEQP("id.seqp"),
		AEL_GRP_TXT_DESC_MATS("aelGrpTxtDescMats"),
		AEL_GRP_TXT_DESC_MATS_SEQ("aelGrpTxtDescMats.seq"),
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
}