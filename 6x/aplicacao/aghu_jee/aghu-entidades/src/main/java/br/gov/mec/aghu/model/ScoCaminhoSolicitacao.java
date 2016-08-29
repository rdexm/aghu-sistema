package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * The persistent class for the sco_caminhos_solicitacoes database table.
 * 
 */
@Entity
@Table(name="SCO_CAMINHOS_SOLICITACOES", schema = "AGH")
public class ScoCaminhoSolicitacao extends BaseEntityId<ScoCaminhoSolicitacaoID> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 164091851771824291L;
	
	private ScoCaminhoSolicitacaoID id;
	private Integer version;
	
	private ScoPontoParadaSolicitacao pontoParadaOrigem;
	private ScoPontoParadaSolicitacao pontoParadaDestino;

    public ScoCaminhoSolicitacao() {
    }

    @EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "ppsCodigoInicio", column = @Column(name = "PPS_CODIGO_INICIO", nullable = false)),
		@AttributeOverride(name = "ppsCodigo", column = @Column(name = "PPS_CODIGO", nullable = false)) })
	public ScoCaminhoSolicitacaoID getId() {
		return this.id;
	}

	public void setId(ScoCaminhoSolicitacaoID id) {
		this.id = id;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
	    ID("id"),  
	    PPS_CODIGO_INICIO("id.ppsCodigoInicio"), 
	    PPS_CODIGO("id.ppsCodigo"),
	    PONTO_PARADA_ORIGEM("pontoParadaOrigem"),
	    PONTO_PARADA_DESTINO("pontoParadaDestino");
	    
	    private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof ScoCaminhoSolicitacao)) {
			return false;
		}
		ScoCaminhoSolicitacao other = (ScoCaminhoSolicitacao) obj;
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

	public void setPontoParadaDestino(ScoPontoParadaSolicitacao pontoParadaDestino) {
		this.pontoParadaDestino = pontoParadaDestino;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PPS_CODIGO", nullable = false, insertable = false, updatable = false)
	public ScoPontoParadaSolicitacao getPontoParadaDestino() {
		return pontoParadaDestino;
	}

	public void setPontoParadaOrigem(ScoPontoParadaSolicitacao pontoParadaOrigem) {
		this.pontoParadaOrigem = pontoParadaOrigem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PPS_CODIGO_INICIO", nullable = false, insertable = false, updatable = false)
	public ScoPontoParadaSolicitacao getPontoParadaOrigem() {
		return pontoParadaOrigem;
	} 
}