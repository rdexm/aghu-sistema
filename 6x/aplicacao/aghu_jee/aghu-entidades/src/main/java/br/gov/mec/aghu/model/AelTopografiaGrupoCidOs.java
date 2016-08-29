package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.List;

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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the AEL_TOPOGRAFIA_GRUPO_CIDOS database table.
 * 
 * Alias: AEG
 */
@Entity
@Table(name="AEL_TOPOGRAFIA_GRUPO_CIDOS")
@SequenceGenerator(name="AelAegSq1", sequenceName="AEL_AEG_SQ1")
public class AelTopografiaGrupoCidOs extends BaseEntitySeq<Long> {
	public enum Fields {		
		CRIADO_EM("criadoEm"), 
		DESCRICAO("descricao"),
		SEQ("seq"),
		GRUPO_SEQ("grupo.seq"),
		GRUPO("grupo"),
		SERVIDOR("servidor"),
		SIGLA("sigla"),
		SITUACAO("indSituacao"),
		VERSION("version");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2048580809412140552L;
	private Date criadoEm;
	private String descricao;
	private AelTopografiaGrupoCidOs grupo;
	private DominioSituacao indSituacao;
	private Long seq;
	private RapServidores servidor;
	private String sigla;
	private List<AelTopografiaGrupoCidOs> subGrupos;

	
	private Integer version;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AelTopografiaGrupoCidOs)) {
			return false;
		}
		AelTopografiaGrupoCidOs other = (AelTopografiaGrupoCidOs) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM", nullable=false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	@Column(nullable=false, length=120)
	public String getDescricao() {
		return this.descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AEG_SEQ", nullable = true)
	public AelTopografiaGrupoCidOs getGrupo() {
		return grupo;
	}

	@Column(name = "IND_SITUACAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}


    @Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="AelAegSq1")
	@Column(unique=true, nullable=false, precision=5)
	public Long getSeq() {
		return this.seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	public RapServidores getServidor() {
		return this.servidor;
	}


	@Column(nullable=false, length=10)
	public String getSigla() {
		return this.sigla;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "grupo")
	public List<AelTopografiaGrupoCidOs> getSubGrupos() {
		return subGrupos;
	}


	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}


	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void setGrupo(AelTopografiaGrupoCidOs grupo) {
		this.grupo = grupo;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	
	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public void setSubGrupos(List<AelTopografiaGrupoCidOs> subGrupos) {
		this.subGrupos = subGrupos;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}


}