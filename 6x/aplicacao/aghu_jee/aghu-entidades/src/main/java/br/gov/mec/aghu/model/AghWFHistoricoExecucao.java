package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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


import br.gov.mec.aghu.dominio.DominioAcaoHistoricoWF;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "AGH_WF_HIST_EXECUCOES", schema = "AGH")
@SequenceGenerator(name = "AghWFHistoricoExecucaoSequence", sequenceName ="AGH.AGH_WHE_SEQ", allocationSize = 1)
public class AghWFHistoricoExecucao extends BaseEntitySeq<Integer> implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6036323060023218923L;
	private Integer seq;
	private AghWFFluxo fluxo;
	private AghWFEtapa etapa;
	private AghWFExecutor executor;
	private Date dataRegistro;
	private String justificativa;
	private String observacao;
	private DominioAcaoHistoricoWF acao;	
	private Integer version;
	
	

	public AghWFHistoricoExecucao(AghWFFluxo fluxo,
			AghWFEtapa etapa, AghWFExecutor executor, Date dataRegistro, String justificativa, String observacao,
			DominioAcaoHistoricoWF acao) {
		super();
		this.fluxo = fluxo;
		this.etapa = etapa;
		this.executor = executor;		
		this.dataRegistro = dataRegistro;
		this.justificativa = justificativa;
		this.observacao = observacao;
		this.acao = acao;		
	}
	
	public AghWFHistoricoExecucao(){}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator ="AghWFHistoricoExecucaoSequence")	
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
	@JoinColumn(name="WFL_SEQ", nullable=false, referencedColumnName="SEQ")
	public AghWFFluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(AghWFFluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WET_SEQ",  nullable=false, referencedColumnName="SEQ")
	public AghWFEtapa getEtapa() {
		return etapa;
	}

	public void setEtapa(AghWFEtapa etapa) {
		this.etapa = etapa;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WEX_SEQ", referencedColumnName="SEQ")
	public AghWFExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(AghWFExecutor executor) {
		this.executor = executor;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_REGISTRO")
	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}
	
	@Column(name = "JUSTIFICATIVA", length = 200)
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
	@Column(name = "OBSERVACAO", length = 200)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	@Column(name = "ACAO", length = 10)
	@Enumerated(EnumType.STRING)
	public DominioAcaoHistoricoWF getAcao() {
		return acao;
	}

	public void setAcao(DominioAcaoHistoricoWF  acao) {
		this.acao = acao;
	}


	public enum Fields {
		
		SEQ("seq"),
		FLUXO("fluxo"),
		WET_SEQ("etapa"),
		WEX_SEQ("executor"),
		DT_REGISTRO("dataRegistro"),
		JUSTIFICATIVA("justificativa"),
		OBSERVACAO("observacao"),
		ACAO("acao"),
		VERSION("version"),
		ETAPA("etapa"),
		FLUXO_SEQ("fluxo.seq");	
		
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
		AghWFHistoricoExecucao other = (AghWFHistoricoExecucao) obj;
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
