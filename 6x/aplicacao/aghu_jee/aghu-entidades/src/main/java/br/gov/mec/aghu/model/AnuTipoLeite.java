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
@SequenceGenerator(name="anuTleSq1", sequenceName="AGH.ANU_TLE_SQ1", allocationSize = 1)
@Table(name = "ANU_TIPO_LEITES", schema = "AGH")
public class AnuTipoLeite extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1781101589898691054L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private Date criadoEm;
	private String descricao;
	private String indSituacao;
	private Set<AnuLeiteMamadeira> anuLeiteMamadeiraes = new HashSet<AnuLeiteMamadeira>(0);

	public AnuTipoLeite() {
	}

	public AnuTipoLeite(Short seq, RapServidores rapServidores, Date criadoEm, String descricao, String indSituacao) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
	}

	public AnuTipoLeite(Short seq, RapServidores rapServidores, Date criadoEm, String descricao, String indSituacao,
			Set<AnuLeiteMamadeira> anuLeiteMamadeiraes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.anuLeiteMamadeiraes = anuLeiteMamadeiraes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "anuTleSq1")
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
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuTipoLeite")
	public Set<AnuLeiteMamadeira> getAnuLeiteMamadeiraes() {
		return this.anuLeiteMamadeiraes;
	}

	public void setAnuLeiteMamadeiraes(Set<AnuLeiteMamadeira> anuLeiteMamadeiraes) {
		this.anuLeiteMamadeiraes = anuLeiteMamadeiraes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		ANU_LEITE_MAMADEIRAES("anuLeiteMamadeiraes");

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
		if (!(obj instanceof AnuTipoLeite)) {
			return false;
		}
		AnuTipoLeite other = (AnuTipoLeite) obj;
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
