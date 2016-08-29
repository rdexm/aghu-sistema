package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@SequenceGenerator(name="mamCeqSq1", sequenceName="AGH.MAM_CEQ_SQ1", allocationSize = 1)
@Table(name = "MAM_CRIT_ESC_QUESTS", schema = "AGH")
public class MamCritEscQuest extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 589437644260126145L;
	private Integer seq;
	private Integer version;
	private AghEspecialidades aghEspecialidades;
	private AghClinicas aghClinicas;
	private RapServidores rapServidores;
	private FccCentroCustos fccCentroCustosByCctCodigoAtua;
	private FccCentroCustos fccCentroCustosByCctCodigoLota;
	private String descricao;
	private String titulo;
	private String indSituacao;
	private Date criadoEm;
	private Set<MamCritEscXQuest> mamCritEscXQuestes = new HashSet<MamCritEscXQuest>(0);

	public MamCritEscQuest() {
	}

	public MamCritEscQuest(Integer seq, RapServidores rapServidores, String descricao, String titulo, String indSituacao, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.titulo = titulo;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MamCritEscQuest(Integer seq, AghEspecialidades aghEspecialidades, AghClinicas aghClinicas, RapServidores rapServidores,
			FccCentroCustos fccCentroCustosByCctCodigoAtua, FccCentroCustos fccCentroCustosByCctCodigoLota, String descricao,
			String titulo, String indSituacao, Date criadoEm, Set<MamCritEscXQuest> mamCritEscXQuestes) {
		this.seq = seq;
		this.aghEspecialidades = aghEspecialidades;
		this.aghClinicas = aghClinicas;
		this.rapServidores = rapServidores;
		this.fccCentroCustosByCctCodigoAtua = fccCentroCustosByCctCodigoAtua;
		this.fccCentroCustosByCctCodigoLota = fccCentroCustosByCctCodigoLota;
		this.descricao = descricao;
		this.titulo = titulo;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.mamCritEscXQuestes = mamCritEscXQuestes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamCeqSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ")
	public AghEspecialidades getAghEspecialidades() {
		return this.aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLC_CODIGO")
	public AghClinicas getAghClinicas() {
		return this.aghClinicas;
	}

	public void setAghClinicas(AghClinicas aghClinicas) {
		this.aghClinicas = aghClinicas;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_ATUA")
	public FccCentroCustos getFccCentroCustosByCctCodigoAtua() {
		return this.fccCentroCustosByCctCodigoAtua;
	}

	public void setFccCentroCustosByCctCodigoAtua(FccCentroCustos fccCentroCustosByCctCodigoAtua) {
		this.fccCentroCustosByCctCodigoAtua = fccCentroCustosByCctCodigoAtua;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_LOTA")
	public FccCentroCustos getFccCentroCustosByCctCodigoLota() {
		return this.fccCentroCustosByCctCodigoLota;
	}

	public void setFccCentroCustosByCctCodigoLota(FccCentroCustos fccCentroCustosByCctCodigoLota) {
		this.fccCentroCustosByCctCodigoLota = fccCentroCustosByCctCodigoLota;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "TITULO", nullable = false, length = 25)
	@Length(max = 25)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamCritEscQuest")
	public Set<MamCritEscXQuest> getMamCritEscXQuestes() {
		return this.mamCritEscXQuestes;
	}

	public void setMamCritEscXQuestes(Set<MamCritEscXQuest> mamCritEscXQuestes) {
		this.mamCritEscXQuestes = mamCritEscXQuestes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AGH_ESPECIALIDADES("aghEspecialidades"),
		AGH_CLINICAS("aghClinicas"),
		RAP_SERVIDORES("rapServidores"),
		FCC_CENTRO_CUSTOS_BY_CCT_CODIGO_ATUA("fccCentroCustosByCctCodigoAtua"),
		FCC_CENTRO_CUSTOS_BY_CCT_CODIGO_LOTA("fccCentroCustosByCctCodigoLota"),
		DESCRICAO("descricao"),
		TITULO("titulo"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		MAM_CRIT_ESC_X_QUESTES("mamCritEscXQuestes");

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
		if (!(obj instanceof MamCritEscQuest)) {
			return false;
		}
		MamCritEscQuest other = (MamCritEscQuest) obj;
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
