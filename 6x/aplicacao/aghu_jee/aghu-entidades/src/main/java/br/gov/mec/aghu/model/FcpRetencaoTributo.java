package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "FCP_RETENCAO_TRIBUTOS", schema = "AGH")
public class FcpRetencaoTributo extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -68293284232425558L;
	
	private Integer version;
	private Integer codigo;
	
	private DominioTipoTributo tipoTributo;

	public FcpRetencaoTributo() {
		
	}

	public FcpRetencaoTributo(Integer codigo, DominioTipoTributo tipoTributo) {
		this.codigo = codigo;
		this.tipoTributo = tipoTributo;
	}
	
	@Id
	@Override
	@Column(name = "CODIGO", nullable = false)
	public Integer getCodigo() {
		return codigo;
	}
	
	@Column(name = "TIPO_TRIBUTO", nullable = false, length=1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTributo getTipoTributo() {
		return tipoTributo;
	}

	public void setTipoTributo(DominioTipoTributo tipoTributo) {
		this.tipoTributo = tipoTributo;
	}

	@Override
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	@Transient
	public String getDescricaoTipoTributo() {
		
		if(getTipoTributo() != null){
			return getTipoTributo().getDescricao();
		}		
		return null;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		TIPO_TRIBUTO("tipoTributo"),
		VERSION("version"),
		CODIGO("codigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
