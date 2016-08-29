package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the ael_resultados_codificados database table.
 * 
 */
@Entity
@Table(name="AEL_RESUL_GRUPO_PESQUISAS", schema = "AGH")
public class AelResultadoGrupoPesquisa extends BaseEntityId<AelResultadoGrupoPesquisaId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3505275481571895064L;
	
	
	private AelResultadoGrupoPesquisaId id;
	private Integer version;
	private Date criadoEm;
	private RapServidores servidor;
	
	private AelResultadoCodificado resultadoCodificado;
	private AelGrupoPesquisa grupoPesquisa;

    public AelResultadoGrupoPesquisa() {
    }


	@EmbeddedId
	public AelResultadoGrupoPesquisaId getId() {
		return this.id;
	}

	public void setId(AelResultadoGrupoPesquisaId id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="GPQ_SEQ", insertable = false, updatable = false)
    public AelGrupoPesquisa getGrupoPesquisa() {
		return grupoPesquisa;
	}


	public void setGrupoPesquisa(AelGrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}
	
	@JoinColumns( {
		@JoinColumn(name = "RCD_GTC_SEQ", referencedColumnName = "GTC_SEQ", insertable = false, updatable = false),
		@JoinColumn(name = "RCD_SEQP", referencedColumnName = "SEQP", insertable = false, updatable = false) })
	public AelResultadoCodificado getResultadoCodificado() {
		return resultadoCodificado;
	}


	public void setResultadoCodificado(AelResultadoCodificado resultadoCodificado) {
		this.resultadoCodificado = resultadoCodificado;
	}		
	

	public enum Fields {

		ID("id"), //
		SEQ("id.rcdSeqp"),//
		GTC_SEQ("id.rcdGtcSeq"),//
		GPQ_SEQ("id.gpqSeq"),//
		CRIADO_EM("criadoEm"),//
		SERVIDOR("servidor"),
		RESULTADO_CODIFICADO("resultadoCodificado");

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
		if (!(obj instanceof AelResultadoGrupoPesquisa)) {
			return false;
		}
		AelResultadoGrupoPesquisa other = (AelResultadoGrupoPesquisa) obj;
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