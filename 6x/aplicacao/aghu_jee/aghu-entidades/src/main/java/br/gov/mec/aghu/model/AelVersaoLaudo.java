package br.gov.mec.aghu.model;

// Generated 18/03/2011 10:47:15 by Hibernate Tools 3.2.5.Beta

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

import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AelVersaoLaudos generated by hbm2java
 */
@Entity
@Table(name = "AEL_VERSAO_LAUDOS", schema = "AGH")
public class AelVersaoLaudo extends BaseEntityId<AelVersaoLaudoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5701046893993375131L;
	private AelVersaoLaudoId id;
	private Integer version;
	private AelVersaoLaudo versaoLaudo;
	private Date criadoEm;
	private DominioSituacaoVersaoLaudo situacao;	
	private String nomeDesenho;
	private RapServidores servidor;
	private Boolean imprimeNomeExame;
	private Boolean usaObjetoValorReferencia;
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private Set<AelVersaoLaudo> aelVersaoLaudoses = new HashSet<AelVersaoLaudo>(0);


	@EmbeddedId
	@AttributeOverrides( {  
			@AttributeOverride(name = "emaExaSigla", column = @Column(name = "EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "emaManSeq", column = @Column(name = "EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AelVersaoLaudoId getId() {
		return this.id;
	}

	public void setId(AelVersaoLaudoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = true)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "VEL_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA"),
			@JoinColumn(name = "VEL_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ"),
			@JoinColumn(name = "VEL_SEQP", referencedColumnName = "SEQP") })
	public AelVersaoLaudo getVersaoLaudo() {
		return versaoLaudo;
	}

	public void setVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		this.versaoLaudo = versaoLaudo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoVersaoLaudo getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersaoLaudo situacao) {
		this.situacao = situacao;
	}

	@Column(name = "NOME_DESENHO", nullable = false, length = 60)
	@Length(max = 60)
	public String getNomeDesenho() {
		return this.nomeDesenho;
	}

	public void setNomeDesenho(String nomeDesenho) {
		this.nomeDesenho = nomeDesenho;
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

	@Column(name = "IND_IMPRIME_NOME_EXAME")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getImprimeNomeExame() {
		return imprimeNomeExame;
	}

	public void setImprimeNomeExame(Boolean imprimeNomeExame) {
		this.imprimeNomeExame = imprimeNomeExame;
	}

	@Column(name = "IND_USA_OBJETO_VALREF")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getUsaObjetoValorReferencia() {
		return usaObjetoValorReferencia;
	}

	public void setUsaObjetoValorReferencia(Boolean usaObjetoValorReferencia) {
		this.usaObjetoValorReferencia = usaObjetoValorReferencia;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "versaoLaudo")
	public Set<AelVersaoLaudo> getAelVersaoLaudoses() {
		return this.aelVersaoLaudoses;
	}

	public void setAelVersaoLaudoses(Set<AelVersaoLaudo> aelVersaoLaudoses) {
		this.aelVersaoLaudoses = aelVersaoLaudoses;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)  
	@JoinColumns( {
			@JoinColumn(name = "EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA", insertable = false, updatable = false),
			@JoinColumn(name = "EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", insertable = false, updatable = false) })
	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(
			AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}
	
	public enum Fields {
		SEQP("id.seqp"),
		EMA_EXA_SIGLA("id.emaExaSigla"),
		EMA_MAN_SEQ("id.emaManSeq"),
		SITUACAO("situacao"),
		VERSAO_LAUDO("versaoLaudo"),
		CRIADO_EM("criadoEm"),
		NOME_DESENHO("nomeDesenho"),
		SERVIDOR("servidor"),
		IMPRIME_NOME_EXAME("imprimeNomeExame"),
		USA_OBJETO_VALOR_REFERENCIA("usaObjetoValorReferencia"),
		EXAME_MATERIAL_ANALISE("exameMaterialAnalise");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AelVersaoLaudo)) {
			return false;
		}
		AelVersaoLaudo other = (AelVersaoLaudo) obj;
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
