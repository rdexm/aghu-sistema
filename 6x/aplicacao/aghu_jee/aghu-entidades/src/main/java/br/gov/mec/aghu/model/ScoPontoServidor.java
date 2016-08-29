package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_pontos_servidores database table.
 * 
 */
@Entity
@Table(name="SCO_PONTOS_SERVIDORES")
public class ScoPontoServidor extends BaseEntityId<ScoPontoServidorId> implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 2350704167976591796L;
	
	private ScoPontoServidorId id;
	private ScoPontoParadaSolicitacao pontoParadaSolicitacao;
	private RapServidores servidor;
	private Integer version;

    public ScoPontoServidor() {
    }

    @EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "codigoPontoParadaSolicitacao", column = @Column(name = "PPS_CODIGO", nullable = false, scale = 0)),
		@AttributeOverride(name = "vinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, scale = 0)),
		@AttributeOverride(name = "matricula", column = @Column(name = "SER_MATRICULA", nullable = false, scale = 0))})
	public ScoPontoServidorId getId() {
		return this.id;
	}

	public void setId(ScoPontoServidorId id) {
		this.id = id;
	}
	
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="PPS_CODIGO", referencedColumnName="CODIGO", insertable = false, updatable = false)
	public ScoPontoParadaSolicitacao getPontoParadaSolicitacao() {
		return pontoParadaSolicitacao;
	}

	public void setPontoParadaSolicitacao(
			ScoPontoParadaSolicitacao scoPontosParadaSolicitacoes) {
		this.pontoParadaSolicitacao = scoPontosParadaSolicitacoes;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA", insertable=false, updatable=false),
				  @JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO", insertable=false, updatable=false)})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {
	    CODIGO_PP_SOLICITACAO("id.codigoPontoParadaSolicitacao"), 
	    DESCRICAO_PP_SOLICITACAO("pontoParadaSolicitacao.descricao"),
	    MATRICULA("servidor.id.matricula"), 
	    VIN_CODIGO("servidor.id.vinCodigo"), 
	    VERSION("version"),
	    PONTO_PARADA_SOLICITACAO("pontoParadaSolicitacao"),
	    SERVIDOR("servidor"),
	    PPS_CODIGO("pontoParadaSolicitacao.codigo");
	    
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
		if (!(obj instanceof ScoPontoServidor)) {
			return false;
		}
		ScoPontoServidor other = (ScoPontoServidor) obj;
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