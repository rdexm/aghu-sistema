package br.gov.mec.aghu.model;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mcoCdtSq1", sequenceName="AGH.MCO_CDT_SQ1", allocationSize = 1)
@Table(name = "MCO_CONDUTAS", schema = "AGH")
public class McoConduta extends BaseEntitySeq<Long> implements java.io.Serializable {

	private static final long serialVersionUID = -689210762132118922L;
	
	private Long seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer cod;
	
	// FIXME Implementar este relacionamento
//	private Set<McoPlanoIniciais> mcoPlanoIniciaises = new HashSet<McoPlanoIniciais>(0);

	public McoConduta() {
	}

	public McoConduta(Long seq, RapServidores rapServidores, String descricao, DominioSituacao indSituacao, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public McoConduta(Long seq, RapServidores rapServidores, String descricao, DominioSituacao indSituacao, Date criadoEm, Integer cod
//			, Set<McoPlanoIniciais> mcoPlanoIniciaises
			) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.cod = cod;
//		this.mcoPlanoIniciaises = mcoPlanoIniciaises;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mcoCdtSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 17, scale = 17)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
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

	@Column(name = "COD")
	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mcoConduta")
//	public Set<McoPlanoIniciais> getMcoPlanoIniciaises() {
//		return this.mcoPlanoIniciaises;
//	}
//
//	public void setMcoPlanoIniciaises(Set<McoPlanoIniciais> mcoPlanoIniciaises) {
//		this.mcoPlanoIniciaises = mcoPlanoIniciaises;
//	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		COD("cod"),
//		MCO_PLANO_INICIAISES("mcoPlanoIniciaises")
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


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof McoConduta)) {
			return false;
		}
		McoConduta other = (McoConduta) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
