package br.gov.mec.aghu.model;

import java.io.Serializable;

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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "SCE_ALMOX_COMPOSICAO")
@SequenceGenerator(name="sceAlmcSq1", sequenceName="AGH.SCE_ALC_SQ1")
public class SceAlmoxarifadoComposicao extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7241631012550267611L;
	private Integer seq;
	private String descricao;
	private RapServidores servidorInclusao;
	private SceAlmoxarifado almoxarifado;
	private Integer version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="sceAlmcSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALM_SEQ")
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorInclusao() {
		return servidorInclusao;
	}

	public void setServidorInclusao(RapServidores servidorInclusao) {
		this.servidorInclusao = servidorInclusao;
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
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof SceAlmoxarifadoComposicao)) {
			return false;
		}
		
		SceAlmoxarifadoComposicao castOther = (SceAlmoxarifadoComposicao) other;
		
		return new EqualsBuilder().append(this.getSeq(), castOther.getSeq()).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getSeq()).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), SERVIDOR("servidorInclusao"), DESCRICAO("descricao"), ALMOXARIFADO("almoxarifado");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}