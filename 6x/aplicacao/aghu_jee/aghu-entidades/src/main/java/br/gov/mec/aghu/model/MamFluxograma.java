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

@Entity
@Table(name = "MAM_FLUXOGRAMAS", schema = "AGH")
@SequenceGenerator(name = "mamFlxSeq", sequenceName = "AGH.MAM_FLX_SQ1", allocationSize = 1)
public class MamFluxograma extends BaseEntitySeq<Integer> implements Serializable {
	private static final long serialVersionUID = 2575588185259542878L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer ordem;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private MamProtClassifRisco protClassifRisco;
	private Integer version;

	private Set<MamDescritor> mamDescritores = new HashSet<MamDescritor>(0);

	public MamFluxograma() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamFlxSeq")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PCR_SEQ", nullable = false)
	public MamProtClassifRisco getProtClassifRisco() {
		return protClassifRisco;
	}

	public void setProtClassifRisco(MamProtClassifRisco protClassifRisco) {
		this.protClassifRisco = protClassifRisco;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxograma")
	public Set<MamDescritor> getMamDescritores() {
		return mamDescritores;
	}

	public void setMamDescritores(Set<MamDescritor> mamDescritores) {
		this.mamDescritores = mamDescritores;
	}

	public enum Fields {

		SEQ("seq"), //
		DESCRICAO("descricao"), //
		IND_SITUACAO("indSituacao"), //
		ORDEM("ordem"), //
		PROT_CLASSIF_RISCO("protClassifRisco"),
		DESCRITORES("mamDescritores"), //
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof MamFluxograma)) {
			return false;
		}
		MamFluxograma other = (MamFluxograma) obj;
		if (descricao == null) {
			if (other.descricao != null) {
				return false;
			}
		} else if (!descricao.equals(other.descricao)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
}
