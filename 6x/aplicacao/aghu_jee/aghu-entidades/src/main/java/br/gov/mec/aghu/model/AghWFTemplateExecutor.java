package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "AGH_WF_TEMPLATE_EXECUTORES", schema = "AGH")
@SequenceGenerator(name = "AghWFTemplateExecutorSequence", sequenceName ="AGH.AGH_WTX_SEQ")
public class AghWFTemplateExecutor extends BaseEntitySeq<Integer> implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7514073534843789719L;
	private Integer seq;
	private Integer version;
	private RapServidores rapServidor;
	private Boolean indRecebeNotificacao;
	private AghWFTemplateFluxo templateFluxo;
	private AghWFTemplateEtapa templateEtapa;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator ="AghWFTemplateExecutorSequence")
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
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({
	@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
	@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })	
	public RapServidores getRapServidor() {
		return rapServidor;
	}
	
	public void setRapServidor(RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}
	
	
	@Column(name = "IND_RECEBE_NOTIF", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRecebeNotificacao() {
		return indRecebeNotificacao;
	}

	public void setIndRecebeNotificacao(Boolean indRecebeNotificacao) {
		this.indRecebeNotificacao = indRecebeNotificacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WTF_SEQ",  nullable=false, referencedColumnName="SEQ")
	public AghWFTemplateFluxo getTemplateFluxo() {
		return templateFluxo;
	}
	
	public void setTemplateFluxo(AghWFTemplateFluxo templateFluxo) {
		this.templateFluxo = templateFluxo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WTE_SEQ", nullable=false, referencedColumnName="SEQ")
	public AghWFTemplateEtapa getTemplateEtapa() {
		return templateEtapa;
	}
	

	public void setTemplateEtapa(AghWFTemplateEtapa templateEtapa) {
		this.templateEtapa = templateEtapa;
	}	
	

	public enum Fields {
		
		SEQ("seq"), 
		VERSION("version"),
		WTE_SEQ("templateEtapa.seq"),
		WTF_SEQ("templateFluxo.seq"),
		SERVIDOR("rapServidor"),
		SERVIDOR_MATRICULA("rapServidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("rapServidor.id.vinCodigo"),
		IND_RECEBE_NOTIF("indRecebeNotificacao");
		
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
		AghWFTemplateExecutor other = (AghWFTemplateExecutor) obj;
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
