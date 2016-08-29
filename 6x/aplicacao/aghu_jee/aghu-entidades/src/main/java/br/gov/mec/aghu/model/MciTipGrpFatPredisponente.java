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
@SequenceGenerator(name="mciTgfSq1", sequenceName="AGH.MCI_TGF_SQ1", allocationSize = 1)
@Table(name = "MCI_TIP_GRP_FAT_PREDISPONENTES", schema = "AGH")
public class MciTipGrpFatPredisponente extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3078959434415285749L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidoresByMciTgfSerFk1;
	private RapServidores rapServidoresByMciTgfSerFk2;
	private String descricao;
	private String indSituacao;
	private Date criadoEm;
	private Date alteradoEm;
	private Set<MciGrupoFatorPredisponente> mciGrupoFatorPredisponentees = new HashSet<MciGrupoFatorPredisponente>(0);

	public MciTipGrpFatPredisponente() {
	}

	public MciTipGrpFatPredisponente(Short seq, RapServidores rapServidoresByMciTgfSerFk1, String descricao, String indSituacao,
			Date criadoEm) {
		this.seq = seq;
		this.rapServidoresByMciTgfSerFk1 = rapServidoresByMciTgfSerFk1;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MciTipGrpFatPredisponente(Short seq, RapServidores rapServidoresByMciTgfSerFk1, RapServidores rapServidoresByMciTgfSerFk2,
			String descricao, String indSituacao, Date criadoEm, Date alteradoEm,
			Set<MciGrupoFatorPredisponente> mciGrupoFatorPredisponentees) {
		this.seq = seq;
		this.rapServidoresByMciTgfSerFk1 = rapServidoresByMciTgfSerFk1;
		this.rapServidoresByMciTgfSerFk2 = rapServidoresByMciTgfSerFk2;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.mciGrupoFatorPredisponentees = mciGrupoFatorPredisponentees;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciTgfSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
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
	public RapServidores getRapServidoresByMciTgfSerFk1() {
		return this.rapServidoresByMciTgfSerFk1;
	}

	public void setRapServidoresByMciTgfSerFk1(RapServidores rapServidoresByMciTgfSerFk1) {
		this.rapServidoresByMciTgfSerFk1 = rapServidoresByMciTgfSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMciTgfSerFk2() {
		return this.rapServidoresByMciTgfSerFk2;
	}

	public void setRapServidoresByMciTgfSerFk2(RapServidores rapServidoresByMciTgfSerFk2) {
		this.rapServidoresByMciTgfSerFk2 = rapServidoresByMciTgfSerFk2;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciTipGrpFatPredisponente")
	public Set<MciGrupoFatorPredisponente> getMciGrupoFatorPredisponentees() {
		return this.mciGrupoFatorPredisponentees;
	}

	public void setMciGrupoFatorPredisponentees(Set<MciGrupoFatorPredisponente> mciGrupoFatorPredisponentees) {
		this.mciGrupoFatorPredisponentees = mciGrupoFatorPredisponentees;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES_BY_MCI_TGF_SER_FK1("rapServidoresByMciTgfSerFk1"),
		RAP_SERVIDORES_BY_MCI_TGF_SER_FK2("rapServidoresByMciTgfSerFk2"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		MCI_GRUPO_FATOR_PREDISPONENTEES("mciGrupoFatorPredisponentees");

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
		if (!(obj instanceof MciTipGrpFatPredisponente)) {
			return false;
		}
		MciTipGrpFatPredisponente other = (MciTipGrpFatPredisponente) obj;
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
