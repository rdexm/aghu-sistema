package br.gov.mec.aghu.model;

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


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "AGH_WF_ROTAS", schema = "AGH")
@SequenceGenerator(name = "AghWFRotaSequence", sequenceName = "AGH.AGH_WRO_SEQ")
public class AghWFRota extends BaseEntitySeq<Integer> implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3738515618066037904L;
	private Integer seq;
	private AghWFTemplateFluxo templateFluxo;
	private AghWFRotaCriterio rotaCriterio;
	private AghWFTemplateEtapa templateEtapaOrigem;
	private AghWFTemplateEtapa templateEtapaDestino;
	private Integer version;
	


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator ="AghWFRotaSequence")
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
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WRC_SEQ", referencedColumnName="SEQ", nullable = false)
	public AghWFRotaCriterio getRotaCriterio() {
		return rotaCriterio;
	}

	public void setRotaCriterio(AghWFRotaCriterio rotaCriterio) {
		this.rotaCriterio = rotaCriterio;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WTE_SEQ_ORIGEM", referencedColumnName="SEQ", nullable = false)
	public AghWFTemplateEtapa getTemplateEtapaOrigem() {
		return templateEtapaOrigem;
	}
	

	public void setTemplateEtapaOrigem(AghWFTemplateEtapa templateEtapaOrigem) {
		this.templateEtapaOrigem = templateEtapaOrigem;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WTE_SEQ_DESTINO", referencedColumnName="SEQ")
	public AghWFTemplateEtapa getTemplateEtapaDestino() {
		return templateEtapaDestino;
	}

	public void setTemplateEtapaDestino(AghWFTemplateEtapa templateEtapaDestino) {
		this.templateEtapaDestino = templateEtapaDestino;
	}

	public enum Fields {		
		
		SEQ("seq"),
		WTF_SEQ("templateFluxo"),
		WRC_SEQ("rotaCriterio"),
		WTE_SEQ_ORIGEM("templateEtapaOrigem"),
		WTE_SEQ_DESTINO("templateEtapaDestino"),
		VERSION("version");
		
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
		AghWFRota other = (AghWFRota) obj;
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
