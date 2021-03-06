package br.gov.mec.aghu.model;

// Generated 11/03/2011 15:06:24 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AacProcedHospEspecialidades generated by hbm2java
 */
@Entity
@Table(name = "AAC_PROCED_HOSP_ESPECIALIDADES", schema = "AGH")
public class AacProcedHospEspecialidades extends BaseEntityId<AacProcedHospEspecialidadesId> implements java.io.Serializable {

	private static final long serialVersionUID = 2293139957704595175L;
	private AacProcedHospEspecialidadesId id;
	private Boolean consulta;
	private String operacao;
	private String rotina;
	private AghEspecialidades especialidade;
	private FatProcedHospInternos procedHospInterno;

	public AacProcedHospEspecialidades() {
	}

	public AacProcedHospEspecialidades(AacProcedHospEspecialidadesId id,
			Boolean consulta) {
		this.id = id;
		this.consulta = consulta;
	}

	public AacProcedHospEspecialidades(AacProcedHospEspecialidadesId id,
			Boolean consulta, String operacao, String rotina) {
		this.id = id;
		this.consulta = consulta;
		this.operacao = operacao;
		this.rotina = rotina;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "espSeq", column = @Column(name = "ESP_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "phiSeq", column = @Column(name = "PHI_SEQ", nullable = false, precision = 6, scale = 0)) })
	public AacProcedHospEspecialidadesId getId() {
		return this.id;
	}

	public void setId(AacProcedHospEspecialidadesId id) {
		this.id = id;
	}

	@Column(name = "IND_CONSULTA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getConsulta() {
		return this.consulta;
	}

	public void setConsulta(Boolean consulta) {
		this.consulta = consulta;
	}

	@Column(name = "IND_OPERACAO", length = 1)
	public String getOperacao() {
		return this.operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	@Column(name = "ROTINA", length = 60)
	public String getRotina() {
		return this.rotina;
	}

	public void setRotina(String rotina) {
		this.rotina = rotina;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", insertable=false, updatable=false)
	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", insertable=false, updatable=false)
	public FatProcedHospInternos getProcedHospInterno() {
		return this.procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AacProcedHospEspecialidades other = (AacProcedHospEspecialidades) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public static enum Fields {
				CONSULTA("consulta"), ROTINA("rotina"), PROCED_HOSP_INTERNO("procedHospInterno"),
				PHI_SEQ("procedHospInterno.seq"), PHI_DESCRICAO("procedHospInterno.descricao"), 
				PHI_SITUACAO("procedHospInterno.situacao"), PHI("procedHospInterno"),ESPECIALIDADE("especialidade"), 
				ESPECIALIDADE_SEQ("especialidade.seq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
