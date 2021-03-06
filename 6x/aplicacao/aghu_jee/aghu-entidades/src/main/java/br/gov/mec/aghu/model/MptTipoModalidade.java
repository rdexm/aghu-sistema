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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * MptTipoModalidade generated by hbm2java
 */
@Entity
@Table(name = "MPT_TIPO_MODALIDADES", schema = "AGH")
public class MptTipoModalidade extends BaseEntityCodigo<String> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4227201931275503642L;
	private String codigo;
	private Integer version;
	private RapServidores rapServidores;
	private MptAreaRealizacaoSessao mptAreaRealizacaoSessao;
	private String descricao;
	private String descReduzida;
	private String classificacao;
	private Date criadoEm;
	private String indSituacao;
	private Set<MptItemPrcrModalidade> mptItemPrcrModalidadees = new HashSet<MptItemPrcrModalidade>(0);
	private Set<MptGradeSessao> mptGradeSessaoes = new HashSet<MptGradeSessao>(0);

	public MptTipoModalidade() {
	}

	public MptTipoModalidade(String codigo, RapServidores rapServidores, MptAreaRealizacaoSessao mptAreaRealizacaoSessao,
			String descricao, String descReduzida, String classificacao, Date criadoEm, String indSituacao) {
		this.codigo = codigo;
		this.rapServidores = rapServidores;
		this.mptAreaRealizacaoSessao = mptAreaRealizacaoSessao;
		this.descricao = descricao;
		this.descReduzida = descReduzida;
		this.classificacao = classificacao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MptTipoModalidade(String codigo, RapServidores rapServidores, MptAreaRealizacaoSessao mptAreaRealizacaoSessao,
			String descricao, String descReduzida, String classificacao, Date criadoEm, String indSituacao,
			Set<MptItemPrcrModalidade> mptItemPrcrModalidadees, Set<MptGradeSessao> mptGradeSessaoes) {
		this.codigo = codigo;
		this.rapServidores = rapServidores;
		this.mptAreaRealizacaoSessao = mptAreaRealizacaoSessao;
		this.descricao = descricao;
		this.descReduzida = descReduzida;
		this.classificacao = classificacao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.mptItemPrcrModalidadees = mptItemPrcrModalidadees;
		this.mptGradeSessaoes = mptGradeSessaoes;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, length = 5)
	@Length(max = 5)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ARS_SEQ", nullable = false)
	public MptAreaRealizacaoSessao getMptAreaRealizacaoSessao() {
		return this.mptAreaRealizacaoSessao;
	}

	public void setMptAreaRealizacaoSessao(MptAreaRealizacaoSessao mptAreaRealizacaoSessao) {
		this.mptAreaRealizacaoSessao = mptAreaRealizacaoSessao;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DESC_REDUZIDA", nullable = false, length = 25)
	@Length(max = 25)
	public String getDescReduzida() {
		return this.descReduzida;
	}

	public void setDescReduzida(String descReduzida) {
		this.descReduzida = descReduzida;
	}

	@Column(name = "CLASSIFICACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getClassificacao() {
		return this.classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mptTipoModalidade")
	public Set<MptItemPrcrModalidade> getMptItemPrcrModalidadees() {
		return this.mptItemPrcrModalidadees;
	}

	public void setMptItemPrcrModalidadees(Set<MptItemPrcrModalidade> mptItemPrcrModalidadees) {
		this.mptItemPrcrModalidadees = mptItemPrcrModalidadees;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mptTipoModalidade")
	public Set<MptGradeSessao> getMptGradeSessaoes() {
		return this.mptGradeSessaoes;
	}

	public void setMptGradeSessaoes(Set<MptGradeSessao> mptGradeSessaoes) {
		this.mptGradeSessaoes = mptGradeSessaoes;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MPT_AREA_REALIZACAO_SESSOES("mptAreaRealizacaoSessao"),
		DESCRICAO("descricao"),
		DESC_REDUZIDA("descReduzida"),
		CLASSIFICACAO("classificacao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MPT_ITEM_PRCR_MODALIDADEES("mptItemPrcrModalidadees"),
		MPT_GRADE_SESSAOES("mptGradeSessaoes");

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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof MptTipoModalidade)) {
			return false;
		}
		MptTipoModalidade other = (MptTipoModalidade) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
