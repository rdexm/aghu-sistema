package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the AEL_GRP_TXT_DESC_MATS database table.
 * 
 */
@Entity
@Table(name = "AEL_GRP_TXT_DESC_MATS", schema = "AGH")
@SequenceGenerator(name = "aelGtmSq1", sequenceName = "AEL_GTM_SQ1", allocationSize = 1)
public class AelGrpTxtDescMats extends BaseEntitySeq<Short> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7446767299217461653L;
	
	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private Set<AelTxtDescMats> aelTxtDescMats = new HashSet<AelTxtDescMats>();

	public AelGrpTxtDescMats() {
	}

	public AelGrpTxtDescMats(Short seq, String descricao,
			DominioSituacao indSituacao, Date criadoEm, Integer serMatricula,
			Short serVinCodigo) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public AelGrpTxtDescMats(Short seq, String descricao,
			DominioSituacao indSituacao, Date criadoEm, Integer serMatricula,
			Short serVinCodigo, Set<AelTxtDescMats> aelTxtDescMats) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.aelTxtDescMats = aelTxtDescMats;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelGtmSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 500)
	@NotNull
	@Length(max = 500)
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
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelGrpTxtDescMats")
	public Set<AelTxtDescMats> getAelTxtDescMats() {
		return this.aelTxtDescMats;
	}

	public void setAelTxtDescMats(
			Set<AelTxtDescMats> aelTxtDescMats) {
		this.aelTxtDescMats = aelTxtDescMats;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		AelGrpTxtDescMats other = (AelGrpTxtDescMats) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao")
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