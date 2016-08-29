package br.gov.mec.aghu.model;

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
import javax.persistence.Version;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "AGH_WF_TEMPLATE_ETAPAS", schema = "AGH")
@SequenceGenerator(name = "AghWFTemplateEtapaSequence", sequenceName = "AGH.AGH_WTE_SEQ", allocationSize = 1)
public class AghWFTemplateEtapa extends BaseEntitySeq<Integer> implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8059328927806222977L;
	private Integer seq;
	private AghWFTemplateFluxo templateFluxo;
	private Short sequenciaBase;
	private String codigo;
	private String descricao;
	private Short prazoDias;
	private Boolean indUnanime;
	private String url;
	private Integer version;	
	private List<AghWFTemplateExecutor> executores;	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator ="AghWFTemplateEtapaSequence")	
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
	@JoinColumn(name="WTF_SEQ", nullable=false, referencedColumnName="SEQ")
	public AghWFTemplateFluxo getTemplateFluxo() {
		return templateFluxo;
	}
    
	public void setTemplateFluxo(AghWFTemplateFluxo templateFluxo) {
		this.templateFluxo = templateFluxo;
	}	
	
	@Column(name = "SEQUENCIA_BASE")
	public Short getSequenciaBase() {
		return sequenciaBase;
	}

	public void setSequenciaBase(Short sequenciaBase) {
		this.sequenciaBase = sequenciaBase;
	}
	
	@Column(name = "CODIGO", length = 30)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "DESCRICAO", length = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	
	@Column(name = "URL", length = 300)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setExecutores(List<AghWFTemplateExecutor> executores) {
		this.executores = executores;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "templateEtapa")
	public List<AghWFTemplateExecutor> getExecutores() {
		return executores;
	}

	public enum Fields {
		SEQ("seq"), 
		VERSION("version"),
		WTF_SEQ("templateFluxo"),
		SEQUENCIA_BASE("sequenciaBase"),
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		PRAZO_DIAS("prazoDias"),
		IND_UNANIME("indUnanime"),
		URL("url"),
		EXECUTORES("executores")
		
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
		AghWFTemplateEtapa other = (AghWFTemplateEtapa) obj;
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
