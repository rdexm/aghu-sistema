package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * The persistent class for the sco_origem_pareceres_tecnicos database table.
 * 
 */
@Entity
@SequenceGenerator(name="scoOptSq1", sequenceName="AGH.SCO_OPT_SQ1", allocationSize = 1)
@Table(name = "SCO_ORIGEM_PARECERES_TECNICOS")
public class ScoOrigemParecerTecnico extends BaseEntityCodigo<Integer> implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8643592439962875966L;
	
    private Integer codigo;
	private Integer version;
	private ScoAgrupamentoMaterial scoAgrupamentoMateriais;
	private FccCentroCustos fccCentroCusto;
	private Set<ScoMaterial> scoMateriais;

	public ScoOrigemParecerTecnico() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoOptSq1")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
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

	// bi-directional many-to-one association to ScoAgrupamentoMaterial
	@ManyToOne
	@JoinColumn(name = "AGM_CODIGO")
	public ScoAgrupamentoMaterial getScoAgrupamentoMateriais() {
		return this.scoAgrupamentoMateriais;
	}

	public void setScoAgrupamentoMateriais(
			ScoAgrupamentoMaterial scoAgrupamentoMateriais) {
		this.scoAgrupamentoMateriais = scoAgrupamentoMateriais;
	}

	// bi-directional many-to-one association to FccCentroCusto
	@ManyToOne
	@JoinColumn(name = "CCT_CODIGO")
	public FccCentroCustos getFccCentroCusto() {
		return this.fccCentroCusto;
	}

	public void setFccCentroCusto(FccCentroCustos fccCentroCusto) {
		this.fccCentroCusto = fccCentroCusto;
	}

	// bi-directional one-to-many association to ScoOrigemParecerTecnico
	@OneToMany(mappedBy = "origemParecerTecnico")
	public Set<ScoMaterial> getScoMateriais() {
		return this.scoMateriais;
	}

	public void setScoMateriais(Set<ScoMaterial> scoMateriais) {
		this.scoMateriais = scoMateriais;
	}

	@Transient
	public String getDescricao() {
		// veirifica se o parecer técnico está relacionado a
		// agrupamentoMateriais
		// ou a fccCentro de custo. Ele não vai estar relacionado aos dois ao
		// mesmo tempo.
		if (getScoAgrupamentoMateriais() != null) {
			return getScoAgrupamentoMateriais().getDescricao();
		} else {

			return getFccCentroCusto().getDescricao();
		}
	}

	public enum Fields {
		CODIGO("codigo"), 
		VERSION("version"), 
		AGRUPAMENTO_MATERIAIS("scoAgrupamentoMateriais"), 
		CENTRO_CUSTO("fccCentroCusto"), 
		MATERIAIS("scoMateriais");

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
		if (!(obj instanceof ScoOrigemParecerTecnico)) {
			return false;
		}
		ScoOrigemParecerTecnico other = (ScoOrigemParecerTecnico) obj;
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