package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@SequenceGenerator(name="scoStmSq1", sequenceName="AGH.SCO_STM_SQ1", allocationSize = 1)
@Table(name = "SCO_TRIBUTOS_GRP_MAT_SERVICOS", schema = "AGH")
public class ScoTributoGrpMatServico extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3809291568059365686L;
	private Integer seq;
	private Integer version;
	private ScoGrupoMaterial scoGrupoMaterial;
	private RapServidores rapServidores;
	private ScoServico scoServico;
	private Integer friCodigo;
	private String classificacao;
	private Date dtFinalValidade;
	private Date criadoEm;
	private Date alteradoEm;
	private String indUsoMaterial;

	public ScoTributoGrpMatServico() {
	}

	public ScoTributoGrpMatServico(Integer seq, RapServidores rapServidores, Integer friCodigo, Date criadoEm, Date alteradoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.friCodigo = friCodigo;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	public ScoTributoGrpMatServico(Integer seq, ScoGrupoMaterial scoGrupoMaterial, RapServidores rapServidores,
			ScoServico scoServico, Integer friCodigo, String classificacao, Date dtFinalValidade, Date criadoEm, Date alteradoEm,
			String indUsoMaterial) {
		this.seq = seq;
		this.scoGrupoMaterial = scoGrupoMaterial;
		this.rapServidores = rapServidores;
		this.scoServico = scoServico;
		this.friCodigo = friCodigo;
		this.classificacao = classificacao;
		this.dtFinalValidade = dtFinalValidade;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.indUsoMaterial = indUsoMaterial;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoStmSq1")
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
	@JoinColumn(name = "GMT_CODIGO")
	public ScoGrupoMaterial getScoGrupoMaterial() {
		return this.scoGrupoMaterial;
	}

	public void setScoGrupoMaterial(ScoGrupoMaterial scoGrupoMaterial) {
		this.scoGrupoMaterial = scoGrupoMaterial;
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
	@JoinColumn(name = "SRV_CODIGO")
	public ScoServico getScoServico() {
		return this.scoServico;
	}

	public void setScoServico(ScoServico scoServico) {
		this.scoServico = scoServico;
	}

	@Column(name = "FRI_CODIGO", nullable = false)
	public Integer getFriCodigo() {
		return this.friCodigo;
	}

	public void setFriCodigo(Integer friCodigo) {
		this.friCodigo = friCodigo;
	}

	@Column(name = "CLASSIFICACAO", length = 1)
	@Length(max = 1)
	public String getClassificacao() {
		return this.classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FINAL_VALIDADE", length = 29)
	public Date getDtFinalValidade() {
		return this.dtFinalValidade;
	}

	public void setDtFinalValidade(Date dtFinalValidade) {
		this.dtFinalValidade = dtFinalValidade;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", nullable = false, length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_USO_MATERIAL", length = 1)
	@Length(max = 1)
	public String getIndUsoMaterial() {
		return this.indUsoMaterial;
	}

	public void setIndUsoMaterial(String indUsoMaterial) {
		this.indUsoMaterial = indUsoMaterial;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		SCO_GRUPO_MATERIAL("scoGrupoMaterial"),
		RAP_SERVIDORES("rapServidores"),
		SCO_SERVICO("scoServico"),
		FRI_CODIGO("friCodigo"),
		CLASSIFICACAO("classificacao"),
		DT_FINAL_VALIDADE("dtFinalValidade"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		IND_USO_MATERIAL("indUsoMaterial");

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
		if (!(obj instanceof ScoTributoGrpMatServico)) {
			return false;
		}
		ScoTributoGrpMatServico other = (ScoTributoGrpMatServico) obj;
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
