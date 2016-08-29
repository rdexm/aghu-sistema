package br.gov.mec.aghu.model;

// Generated 15/10/2010 18:34:03 by Hibernate Tools 3.2.5.Beta

import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@MappedSuperclass
public abstract class VAipPolMdtosBase extends BaseEntityId<VAipPolMdtosBaseId> {

	private static final long serialVersionUID = -8317173554813722880L;

	private VAipPolMdtosBaseId id;

	private Date dataInicio;
	private Date dataFim;
	private String medicamento;
	private Boolean indQuimioterapico;
	private Boolean indAntimicrobiano;
	private Boolean indTuberculostatico;
	private Integer qtdeDiasPrcr;

	public VAipPolMdtosBase() {
	}

	public VAipPolMdtosBase(VAipPolMdtosBaseId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", precision = 8, scale = 0)),
			@AttributeOverride(name = "imePmdAtdSeq", column = @Column(name = "IME_PMD_ATD_SEQ", precision = 7, scale = 0)),
			@AttributeOverride(name = "imePmdSeq", column = @Column(name = "IME_PMD_SEQ", precision = 17, scale = 0)),
			@AttributeOverride(name = "imeMedMatCodigo", column = @Column(name = "IME_MED_MAT_CODIGO", precision = 6, scale = 0)),
			@AttributeOverride(name = "imeSeqp", column = @Column(name = "IME_SEQP", precision = 22, scale = 0)) })
	public VAipPolMdtosBaseId getId() {
		return this.id;
	}

	public void setId(VAipPolMdtosBaseId id) {
		this.id = id;
	}

	@Column(name = "DATA_INICIO", length = 7)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Column(name = "DATA_FIM", length = 7)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "MEDICAMENTO", length = 4000)
	@Length(max = 4000)
	public String getMedicamento() {
		return this.medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	@Column(name = "IND_QUIMIOTERAPICO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndQuimioterapico() {
		return this.indQuimioterapico;
	}

	public void setIndQuimioterapico(Boolean indQuimioterapico) {
		this.indQuimioterapico = indQuimioterapico;
	}

	@Column(name = "IND_ANTIMICROBIANO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAntimicrobiano() {
		return this.indAntimicrobiano;
	}

	public void setIndAntimicrobiano(Boolean indAntimicrobiano) {
		this.indAntimicrobiano = indAntimicrobiano;
	}

	@Column(name = "IND_TUBERCULOSTATICO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTuberculostatico() {
		return this.indTuberculostatico;
	}

	public void setIndTuberculostatico(Boolean indTuberculostatico) {
		this.indTuberculostatico = indTuberculostatico;
	}
	
	@Column(name = "QTDE_DIAS_PRCR")
	public Integer getQtdeDiasPrcr() {
		return qtdeDiasPrcr;
	}

	public void setQtdeDiasPrcr(Integer qtdeDiasPrcr) {
		this.qtdeDiasPrcr = qtdeDiasPrcr;
	}
	
	@Transient
	public Calendar getDataInicioSemHora(){
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.setTime(this.getDataInicio());
		
		dataInicio.set(dataInicio.get(Calendar.YEAR), dataInicio.get(Calendar.MONTH),
				dataInicio.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		
		return dataInicio;
	}
	
	@Transient
	public Calendar getDataFimSemHora(){

		Calendar dataFim = Calendar.getInstance();
		
		if (this.dataFim != null){
			dataFim.setTime(this.getDataFim());
			
			dataFim.set(dataFim.get(Calendar.YEAR), dataFim.get(Calendar.MONTH),
					dataFim.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		}else {
			dataFim = null;
		}
		return dataFim;
	}

	public enum Fields {
		COD_PACIENTE("id.pacCodigo"), ATD_SEQ("id.imePmdAtdSeq"), DATA_INICIO("dataInicio"), IND_QUIMIOTERAPICO(
				"indQuimioterapico"), IND_ANTIMICROBIANO("indAntimicrobiano"), IND_TUBERCULOSTATICO(
				"indTuberculostatico");

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
		if (!(obj instanceof VAipPolMdtosBase)) {
			return false;
		}
		VAipPolMdtosBase other = (VAipPolMdtosBase) obj;
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
