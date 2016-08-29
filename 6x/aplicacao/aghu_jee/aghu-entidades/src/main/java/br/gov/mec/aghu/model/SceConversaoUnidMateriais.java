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
@Table(name = "SCE_CONVERSAO_UNID_MATERIAIS", schema = "AGH")
public class SceConversaoUnidMateriais extends BaseEntityId<SceConversaoUnidMateriaisId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3711798121565423872L;
	
	
	private SceConversaoUnidMateriaisId id;
	private SceConversaoUnidades conversaoUnidade;
	private Date dtGeracao;
	private RapServidores servidor;
	private Integer version;
	
	public SceConversaoUnidMateriais() {
	}

	public SceConversaoUnidMateriais(SceConversaoUnidMateriaisId id) {
		this.id = id;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "matCodigo", column = @Column(name = "MAT_CODIGO", nullable = false)),
			@AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false))})
	public SceConversaoUnidMateriaisId getId() {
		return this.id;
	}

	public void setId(SceConversaoUnidMateriaisId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "CVU_UMD_CODIGO", referencedColumnName = "UMD_CODIGO"),
		@JoinColumn(name = "CVU_UMD_CODIGO_DESTINO", referencedColumnName = "UMD_CODIGO_DESTINO"),
		@JoinColumn(name = "CVU_FATOR_CONVERSAO", referencedColumnName = "FATOR_CONVERSAO")})
	public SceConversaoUnidades getConversaoUnidade() {
		return conversaoUnidade;
	}

	public void setConversaoUnidade(SceConversaoUnidades conversaoUnidade) {
		this.conversaoUnidade = conversaoUnidade;
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
		ID_MAT_CODIGO("id.matCodigo"),
		ID_NUMERO("id.numero"),
		CONVERSAO_UNIDADE("conversaoUnidade"),
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
		if (!(obj instanceof SceConversaoUnidMateriais)) {
			return false;
		}
		SceConversaoUnidMateriais other = (SceConversaoUnidMateriais) obj;
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
