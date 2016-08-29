package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;




@Entity
@Table(name = "AGH_WF_ETAPAS", schema = "AGH")
@SequenceGenerator(name = "AghWFEtapaSequence", sequenceName ="AGH.AGH_WET_SEQ", allocationSize = 1)
public class AghWFEtapa extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	
	private static final long serialVersionUID = 8224850986862426340L;
	private Integer seq;
	private AghWFFluxo fluxo;
	private AghWFTemplateFluxo templateFluxo;	
	private AghWFTemplateEtapa templateEtapa;
	private AghWFEtapa etapaAnterior;
	private Date dataInicio;
	private Date dataFim;
	private Short prazoDias;
	private Boolean indUnanime;
	private Short sequencia;
	private Integer version;
	private List<AghWFExecutor> executores;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator ="AghWFEtapaSequence")
	@Column(name = "SEQ", nullable = false)	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	} 
	
	@ManyToOne(fetch = FetchType.LAZY)		
	@JoinColumn(name="WTF_SEQ", referencedColumnName="SEQ", nullable = false)
	public AghWFTemplateFluxo getTemplateFluxo() {
		return templateFluxo;
	}
    
	public void setTemplateFluxo(AghWFTemplateFluxo templateFluxo) {
		this.templateFluxo = templateFluxo;
	}

	
	@Column(name = "PRAZO_DIAS")
	public Short getPrazoDias() {
		return prazoDias;
	}

	public void setPrazoDias(Short prazoDias) {
		this.prazoDias = prazoDias;
	}
	
	@Column(name = "IND_UNANIME", nullable = false, length = 1)	
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUnanime() {
		return indUnanime;
	}

	public void setIndUnanime(Boolean indUnanime) {
		this.indUnanime = indUnanime;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INICIO")
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_FIM")
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	@Column(name = "SEQUENCIA")
	public Short getSequencia() {
		return sequencia;
	}

	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WFL_SEQ", referencedColumnName="SEQ", nullable = false)
	public AghWFFluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(AghWFFluxo fluxo) {
		this.fluxo = fluxo;
	}

	public void setTemplateEtapa(AghWFTemplateEtapa templateEtapa) {
		this.templateEtapa = templateEtapa;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WTE_SEQ",  referencedColumnName="SEQ", nullable = false)
	public AghWFTemplateEtapa getTemplateEtapa() {
		return templateEtapa;
	}

	public void setEtapaAnterior(AghWFEtapa etapaAnterior) {
		this.etapaAnterior = etapaAnterior;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WET_SEQ_ETAPA_ANTERIOR", referencedColumnName="SEQ")
	public AghWFEtapa getEtapaAnterior() {
		return etapaAnterior;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "etapa")
	public List<AghWFExecutor> getExecutores() {
		return executores;
	}
	
	public void setExecutores(List<AghWFExecutor> executores) {
		this.executores = executores;
	}
	
	public enum Fields {
		SEQ("seq"),
		WFL_SEQ("fluxo"),
		WFL_SEQ_SEQ("fluxo.seq"),
		WTF_SEQ("templateFluxo"),
		WTE_SEQ("templateEtapa"),
		WET_SEQ_ETAPA_ANTERIOR("etapaAnterior"),
		DT_INICIO("dataInicio"),
		DT_FIM("dataFim"),
		PRAZO_DIAS("prazoDias"),
		IND_UNANIME("indUnanime"),
		SEQUENCIA("sequencia"),
		VERSION("version"),
		EXECUTORES("executores"),
		TEMPLATE_ETAPA("templateEtapa")
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		AghWFEtapa other = (AghWFEtapa) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
