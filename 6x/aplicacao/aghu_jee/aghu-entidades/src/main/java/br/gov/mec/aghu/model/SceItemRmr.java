package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the SCE_ITEM_RMRS database table.
 * 
 */
@Entity
@Table(name="SCE_ITEM_RMRS")

public class SceItemRmr extends BaseEntityId<SceItemRmrId> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4010804319989521829L;
	private SceItemRmrId id;
	private Date dtRetorno;
	private String indRetorno;
	private String observacao;
	private BigDecimal qtdeEnviada;
	private BigDecimal qtdeRequisitada;
	private BigDecimal qtdeRetorno;
	private BigDecimal serMatricula;
	private BigDecimal serVinCodigo;
	private String umdCodigo;
	private Set<SceItemRmps> sceItemRmps;
	private SceEstoqueAlmoxarifado SceEstoqueAlmoxarifado;

    public SceItemRmr() {
    }

	public SceItemRmr(SceItemRmrId id, Date dtRetorno, String indRetorno,
			String observacao, BigDecimal qtdeEnviada,
			BigDecimal qtdeRequisitada, BigDecimal qtdeRetorno,
			BigDecimal serMatricula, BigDecimal serVinCodigo, String umdCodigo,
			Set<SceItemRmps> sceItemRmps, SceEstoqueAlmoxarifado SceEstoqueAlmoxarifado) {
		super();
		this.id = id;
		this.dtRetorno = dtRetorno;
		this.indRetorno = indRetorno;
		this.observacao = observacao;
		this.qtdeEnviada = qtdeEnviada;
		this.qtdeRequisitada = qtdeRequisitada;
		this.qtdeRetorno = qtdeRetorno;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.umdCodigo = umdCodigo;
		this.sceItemRmps = sceItemRmps;
		this.SceEstoqueAlmoxarifado = SceEstoqueAlmoxarifado;
	}


	@EmbeddedId
	public SceItemRmrId getId() {
		return this.id;
	}

	public void setId(SceItemRmrId id) {
		this.id = id;
	}
	

    @Temporal( TemporalType.DATE)
	@Column(name="DT_RETORNO")
	public Date getDtRetorno() {
		return this.dtRetorno;
	}

	public void setDtRetorno(Date dtRetorno) {
		this.dtRetorno = dtRetorno;
	}


	@Column(name="IND_RETORNO", nullable=false, length=1)
	public String getIndRetorno() {
		return this.indRetorno;
	}

	public void setIndRetorno(String indRetorno) {
		this.indRetorno = indRetorno;
	}


	@Column(length=120)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}


	@Column(name="QTDE_ENVIADA", precision=7)
	public BigDecimal getQtdeEnviada() {
		return this.qtdeEnviada;
	}

	public void setQtdeEnviada(BigDecimal qtdeEnviada) {
		this.qtdeEnviada = qtdeEnviada;
	}


	@Column(name="QTDE_REQUISITADA", nullable=false, precision=7)
	public BigDecimal getQtdeRequisitada() {
		return this.qtdeRequisitada;
	}

	public void setQtdeRequisitada(BigDecimal qtdeRequisitada) {
		this.qtdeRequisitada = qtdeRequisitada;
	}


	@Column(name="QTDE_RETORNO", precision=7)
	public BigDecimal getQtdeRetorno() {
		return this.qtdeRetorno;
	}

	public void setQtdeRetorno(BigDecimal qtdeRetorno) {
		this.qtdeRetorno = qtdeRetorno;
	}


	@Column(name="SER_MATRICULA", precision=7)
	public BigDecimal getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(BigDecimal serMatricula) {
		this.serMatricula = serMatricula;
	}


	@Column(name="SER_VIN_CODIGO", precision=3)
	public BigDecimal getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(BigDecimal serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	@Column(name="UMD_CODIGO", nullable=false, length=3)
	public String getUmdCodigo() {
		return this.umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}


	//bi-directional many-to-one association to SceItemRmps
	@OneToMany(mappedBy="itemRmr")
	public Set<SceItemRmps> getSceItemRmps() {
		return this.sceItemRmps;
	}

	public void setSceItemRmps(Set<SceItemRmps> sceItemRmps) {
		this.sceItemRmps = sceItemRmps;
	}
	

	//bi-directional many-to-one association to SceEstoqueAlmoxarifado
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="EAL_SEQ", nullable=false, insertable=false, updatable=false)
	public SceEstoqueAlmoxarifado getSceEstoqueAlmoxarifado() {
		return this.SceEstoqueAlmoxarifado;
	}

	public void setSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado SceEstoqueAlmoxarifado) {
		this.SceEstoqueAlmoxarifado = SceEstoqueAlmoxarifado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		SceItemRmr other = (SceItemRmr) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public enum Fields {

		ID("id"),
		DT_RETORNO("dtRetorno"),
		IND_RETORNO("indRetorno"),
		OBSERVACAO("observacao"),
		QTDE_ENVIADA("qtdeEnviada"),
		QTDE_REQUISITADA("qtdeRequisitada"),
		QTDE_RETORNO("qtdeRetorno"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		UMD_CODIGO("umdCodigo"),
		SCE_ITEM_RMPS("sceItemRmps"),
		_SCE_ESTOQUE_ALMOXARIFADO("SceEstoqueAlmoxarifado");

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