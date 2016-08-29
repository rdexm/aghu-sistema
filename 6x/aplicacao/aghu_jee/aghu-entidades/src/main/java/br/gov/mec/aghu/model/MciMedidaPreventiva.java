package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "MCI_MEDIDA_PREVENTIVAS", schema = "AGH")
public class MciMedidaPreventiva extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8038335907311314514L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private String link;
	private Date criadoEm;
	private String indSituacao;
	private Set<MciMedidaPreventivaCid> mciMedidaPreventivaCides = new HashSet<MciMedidaPreventivaCid>(0);
	private Set<MciMedidaPreventivaFator> mciMedidaPreventivaFator = new HashSet<MciMedidaPreventivaFator>(0);

	public MciMedidaPreventiva() {
	}

	public MciMedidaPreventiva(Short seq, RapServidores rapServidores, String descricao, String link, Date criadoEm,
			String indSituacao) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.link = link;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MciMedidaPreventiva(Short seq, RapServidores rapServidores, String descricao, String link, Date criadoEm,
			String indSituacao, Set<MciMedidaPreventivaCid> mciMedidaPreventivaCides,
			Set<MciMedidaPreventivaFator> mciMedidaPreventivaFator) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.link = link;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.mciMedidaPreventivaCides = mciMedidaPreventivaCides;
		this.mciMedidaPreventivaFator = mciMedidaPreventivaFator;
	}

	@Id
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

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "LINK", nullable = false, length = 240)
	@Length(max = 240)
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciMedidaPreventiva")
	public Set<MciMedidaPreventivaCid> getMciMedidaPreventivaCides() {
		return this.mciMedidaPreventivaCides;
	}

	public void setMciMedidaPreventivaCides(Set<MciMedidaPreventivaCid> mciMedidaPreventivaCides) {
		this.mciMedidaPreventivaCides = mciMedidaPreventivaCides;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciMedidaPreventiva")
	public Set<MciMedidaPreventivaFator> getMciMedidaPreventivaFator() {
		return this.mciMedidaPreventivaFator;
	}

	public void setMciMedidaPreventivaFator(Set<MciMedidaPreventivaFator> mciMedidaPreventivaFator) {
		this.mciMedidaPreventivaFator = mciMedidaPreventivaFator;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		LINK("link"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MCI_MEDIDA_PREVENTIVA_CIDES("mciMedidaPreventivaCides"),
		MCI_MEDIDA_PREVENTIVA_FATORES("mciMedidaPreventivaFator");

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
		if (!(obj instanceof MciMedidaPreventiva)) {
			return false;
		}
		MciMedidaPreventiva other = (MciMedidaPreventiva) obj;
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
