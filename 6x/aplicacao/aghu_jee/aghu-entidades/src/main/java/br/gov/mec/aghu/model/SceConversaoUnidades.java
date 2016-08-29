package br.gov.mec.aghu.model;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCE_CONVERSAO_UNIDADES", schema = "AGH")
public class SceConversaoUnidades extends BaseEntityId<SceConversaoUnidadesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3711798150565423872L;
	
	
	private SceConversaoUnidadesId id;
	private Date dtGeracao;
	private RapServidores servidor;
	private Integer version;
	
	public SceConversaoUnidades() {
	}

	public SceConversaoUnidades(SceConversaoUnidadesId id) {
		this.id = id;
	}
	

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "umdCodigo", column = @Column(name = "UMD_CODIGO", nullable = false)),
			@AttributeOverride(name = "umdCodigoDestino", column = @Column(name = "UMD_CODIGO_DESTINO", nullable = false)),
			@AttributeOverride(name = "fatorConversao", column = @Column(name = "FATOR_CONVERSAO", nullable = false))})
	public SceConversaoUnidadesId getId() {
		return this.id;
	}

	public void setId(SceConversaoUnidadesId id) {
		this.id = id;
	}

	@Column(name="DT_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	public enum Fields {
		ID("id"),
		UMD_CODIGO("id.umdCodigo"),
		UMD_CODIGO_DESTINO("id.umdCodigoDestino"),
		FATOR_CONVERSAO("id.fatorConversao"),
		DT_GERACAO("dtGeracao"),
		SERVIDOR("servidor");
		
		private String fields;

		private Fields(String fields) {
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
		if (!(obj instanceof SceConversaoUnidades)) {
			return false;
		}
		SceConversaoUnidades other = (SceConversaoUnidades) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
}
