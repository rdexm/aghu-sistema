package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

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
@Table(name = "RAP_PPP_TMP_LOCAIS_CARGOS", schema = "AGH")
public class RapPppTmpLocalCargo extends BaseEntityId<RapPppTmpLocalCargoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7786343872991029462L;
	private RapPppTmpLocalCargoId id;
	private Integer version;
	private Date dataFim;
	private Integer codOrganograma;
	private String local;
	private Integer codClh;
	private String cargo;
	private String codCbo2002;

	public RapPppTmpLocalCargo() {
	}

	public RapPppTmpLocalCargo(RapPppTmpLocalCargoId id) {
		this.id = id;
	}

	public RapPppTmpLocalCargo(RapPppTmpLocalCargoId id, Date dataFim, Integer codOrganograma, String local, Integer codClh,
			String cargo, String codCbo2002) {
		this.id = id;
		this.dataFim = dataFim;
		this.codOrganograma = codOrganograma;
		this.local = local;
		this.codClh = codClh;
		this.cargo = cargo;
		this.codCbo2002 = codCbo2002;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "codStarh", column = @Column(name = "COD_STARH", nullable = false)),
			@AttributeOverride(name = "dataInicio", column = @Column(name = "DATA_INICIO", nullable = false, length = 29)) })
	public RapPppTmpLocalCargoId getId() {
		return this.id;
	}

	public void setId(RapPppTmpLocalCargoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FIM", length = 29)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "COD_ORGANOGRAMA")
	public Integer getCodOrganograma() {
		return this.codOrganograma;
	}

	public void setCodOrganograma(Integer codOrganograma) {
		this.codOrganograma = codOrganograma;
	}

	@Column(name = "LOCAL", length = 60)
	@Length(max = 60)
	public String getLocal() {
		return this.local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	@Column(name = "COD_CLH")
	public Integer getCodClh() {
		return this.codClh;
	}

	public void setCodClh(Integer codClh) {
		this.codClh = codClh;
	}

	@Column(name = "CARGO", length = 100)
	@Length(max = 100)
	public String getCargo() {
		return this.cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Column(name = "COD_CBO_2002", length = 8)
	@Length(max = 8)
	public String getCodCbo2002() {
		return this.codCbo2002;
	}

	public void setCodCbo2002(String codCbo2002) {
		this.codCbo2002 = codCbo2002;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		DATA_FIM("dataFim"),
		COD_ORGANOGRAMA("codOrganograma"),
		LOCAL("local"),
		COD_CLH("codClh"),
		CARGO("cargo"),
		COD_CBO2002("codCbo2002");

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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof RapPppTmpLocalCargo)) {
			return false;
		}
		RapPppTmpLocalCargo other = (RapPppTmpLocalCargo) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
