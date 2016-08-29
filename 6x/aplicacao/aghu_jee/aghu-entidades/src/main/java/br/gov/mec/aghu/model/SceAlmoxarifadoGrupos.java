package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "SCE_ALMOX_GRUPOS")
@SequenceGenerator(name="sceAlmgSq1", sequenceName="AGH.SCE_ALMG_SQ1")
public class SceAlmoxarifadoGrupos extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7241631012550267611L;
	private Integer seq;
	private SceAlmoxarifadoComposicao composicao;
	private ScoGrupoMaterial grupoMaterial;
	private Integer version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="sceAlmgSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GMT_CODIGO")
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALC_SEQ")
	public SceAlmoxarifadoComposicao getComposicao() {
		return composicao;
	}

	public void setComposicao(SceAlmoxarifadoComposicao composicao) {
		this.composicao = composicao;
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
		if (!(other instanceof SceAlmoxarifadoGrupos)) {
			return false;
		}
		
		SceAlmoxarifadoGrupos castOther = (SceAlmoxarifadoGrupos) other;
		
		return new EqualsBuilder().append(this.getSeq(), castOther.getSeq()).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getSeq()).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), COMPOSICAO("composicao"), GRUPO_MATERIAL("grupoMaterial"),
		SEQ_COMPOSICAO("composicao.seq");

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