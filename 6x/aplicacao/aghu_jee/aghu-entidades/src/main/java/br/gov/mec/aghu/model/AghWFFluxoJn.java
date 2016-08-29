package br.gov.mec.aghu.model;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AGH_WF_FLUXOS_JN", schema = "AGH")
@SequenceGenerator(name = "AghWFFluxoSequenceJn", sequenceName = "AGH.AGH_WFL_JN_SEQ")
public class AghWFFluxoJn extends BaseJournal implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8760736243740023685L;
	private Integer seq;
	private AghWFTemplateFluxo templateFluxo;
	private Integer version;
	private Date dataInicio;
	private Date dataFim;	

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AghWFFluxoSequenceJn")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	@NotNull
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
	@NotNull
	@JoinColumn(name="WTF_SEQ",  referencedColumnName="SEQ")
	public AghWFTemplateFluxo getTemplateFluxo() {
		return templateFluxo;
	}

	public void setTemplateFluxo(AghWFTemplateFluxo templateFluxo) {
		this.templateFluxo = templateFluxo;
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

	public enum Fields {
		SEQ("seq"), 
		WTF_SEQ("templateFluxo"),
		DT_INICIO("dataInicio"),
		DT_FIM("dataFim"),
		VERSION("version"),
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
		AghWFFluxoJn other = (AghWFFluxoJn) obj;
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