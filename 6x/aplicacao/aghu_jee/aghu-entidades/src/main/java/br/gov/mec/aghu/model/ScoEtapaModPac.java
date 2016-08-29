package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * @author fernando.friedrich
 * 
 */

@Entity
@Table(name = "SCO_ETAPA_MOD_PAC", schema = "AGH")
@SequenceGenerator(name = "scoEmpSq1", sequenceName = "AGH.SCO_EMP_SQ1", allocationSize = 1)
public class ScoEtapaModPac extends BaseEntityCodigo<Integer> implements Serializable, Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2005159038095043542L;

	private Integer codigo;
	private DominioObjetoDoPac objetoPac;
	private String descricao;
	private Short numeroDias;
	private DominioSituacao situacao;
	private Integer version;
	private String descricaoObjetoPac;
	private ScoLocalizacaoProcesso localizacaoProcesso;
	private ScoModalidadeLicitacao modalidadeLicitacao;

	// construtores

	public ScoEtapaModPac() {
	}

	public ScoEtapaModPac(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	// getters & setters
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "scoEmpSq1")
    @Column(name = "CODIGO", unique = true, nullable = false, precision = 14, scale = 0)
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "NUM_DIAS", length = 3)
	public Short getNumeroDias() {
		return numeroDias;
	}

	public void setNumeroDias(Short numeroDias) {
		this.numeroDias = numeroDias;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Transient
	public boolean isAtivo() {
		if (getSituacao() != null) {
			return getSituacao() == DominioSituacao.A;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setAtivo(boolean valor) {
		setSituacao(DominioSituacao.getInstance(valor));
	}

	@Column(name = "OBJETO_PAC", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioObjetoDoPac getObjetoPac() {
		return objetoPac;
	}

	public void setObjetoPac(DominioObjetoDoPac objetoPac) {
		this.objetoPac = objetoPac;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LCP_CODIGO", referencedColumnName = "CODIGO")
	public ScoLocalizacaoProcesso getLocalizacaoProcesso() {
		return localizacaoProcesso;
	}

	public void setLocalizacaoProcesso(ScoLocalizacaoProcesso localizacaoProcesso) {
		this.localizacaoProcesso = localizacaoProcesso;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MLC_CODIGO", referencedColumnName = "CODIGO")
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoEtapaModPac)) {
			return false;
		}
		ScoEtapaModPac castOther = (ScoEtapaModPac) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	public enum Fields {
		CODIGO("codigo"), 
		SERVIDOR("servidor"), 
		OBJETO_PAC("objetoPac"), 
		SITUACAO("situacao"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		MLC_CODIGO("modalidadeLicitacao.codigo"), 
		LOCALIZACAO_PROCESSO("localizacaoProcesso"), 
		LCP_CODIGO("localizacaoProcesso.codigo"), 
		LCP_DESCRICAO("localizacaoProcesso.descricao"), 
		VERSION("version"), 
		NUM_DIAS("numeroDias"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Transient
	public String getDescricaoObjetoPac() {
		return descricaoObjetoPac;
	}

	public void setDescricaoObjetoPac(String descricaoObjetoPac) {
		this.descricaoObjetoPac = descricaoObjetoPac;
	}


	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}
