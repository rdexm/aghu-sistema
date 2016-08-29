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
@SequenceGenerator(name="mamToaSq1", sequenceName="AGH.MAM_TOA_SQ1", allocationSize = 1)
@Table(name = "MAM_TIPO_ALTAS", schema = "AGH")
public class MamTipoAlta extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5471887843941918155L;
	private Integer seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private String indSituacao;
	private Date dthrCriacao;
	
	// FIXME Implementar este relacionamento
//	private Set<MamAltaSumario> mamAltaSumarioes = new HashSet<MamAltaSumario>(0);

	public MamTipoAlta() {
	}

	public MamTipoAlta(Integer seq, RapServidores rapServidores, String descricao, String indSituacao, Date dthrCriacao) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.dthrCriacao = dthrCriacao;
	}

//	public MamTipoAlta(Integer seq, RapServidores rapServidores, String descricao, String indSituacao, Date dthrCriacao,
//			Set<MamAltaSumario> mamAltaSumarioes) {
//		this.seq = seq;
//		this.rapServidores = rapServidores;
//		this.descricao = descricao;
//		this.indSituacao = indSituacao;
//		this.dthrCriacao = dthrCriacao;
//		this.mamAltaSumarioes = mamAltaSumarioes;
//	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamToaSq1")
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 100)
	@Length(max = 100)
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
	@Column(name = "DTHR_CRIACAO", nullable = false, length = 29)
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTipoAlta")
//	public Set<MamAltaSumario> getMamAltaSumarioes() {
//		return this.mamAltaSumarioes;
//	}
//
//	public void setMamAltaSumarioes(Set<MamAltaSumario> mamAltaSumarioes) {
//		this.mamAltaSumarioes = mamAltaSumarioes;
//	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		DTHR_CRIACAO("dthrCriacao"),
//		MAM_ALTA_SUMARIOES("mamAltaSumarioes")
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
		if (!(obj instanceof MamTipoAlta)) {
			return false;
		}
		MamTipoAlta other = (MamTipoAlta) obj;
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
