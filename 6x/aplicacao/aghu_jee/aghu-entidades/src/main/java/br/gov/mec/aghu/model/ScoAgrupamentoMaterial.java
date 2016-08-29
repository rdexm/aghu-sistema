package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * The persistent class for the sco_agrupamento_materiais database table.
 * 
 */
@Entity
@SequenceGenerator(name="scoAgmSq1", sequenceName="AGH.SCO_AGM_SQ1", allocationSize = 1)
@Table(name = "SCO_AGRUPAMENTO_MATERIAIS")
public class ScoAgrupamentoMaterial extends BaseEntityCodigo<Short> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 4005056148481756730L;
	
    private Short codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer version;
	private Set<ScoOrigemParecerTecnico> scoOrigemPareceresTecnicos;

	public ScoAgrupamentoMaterial() {
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 4, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoAgmSq1")
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
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
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// bi-directional many-to-one association to ScoOrigemParecerTecnico
	@OneToMany(mappedBy = "scoAgrupamentoMateriais")
	public Set<ScoOrigemParecerTecnico> getScoOrigemPareceresTecnicos() {
		return this.scoOrigemPareceresTecnicos;
	}

	public void setScoOrigemPareceresTecnicos(
			Set<ScoOrigemParecerTecnico> scoOrigemPareceresTecnicos) {
		this.scoOrigemPareceresTecnicos = scoOrigemPareceresTecnicos;
	}

	public enum Fields {
		CODIGO("codigo"), 
		VERSION("version"), 
		ORIGENS_PARECERES_TECNICOS("scoOrigemPareceresTecnicos"), 
		DESCRICAO("descricao"), 
		INDICADOR_SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof ScoAgrupamentoMaterial)) {
			return false;
		}
		ScoAgrupamentoMaterial other = (ScoAgrupamentoMaterial) obj;
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