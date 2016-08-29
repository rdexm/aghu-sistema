package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoCentroCusto;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sco_caract_usuario_ccustos database table.
 * 
 */
@Entity
@SequenceGenerator(name="scoCusSq1", sequenceName="AGH.SCO_CUS_SQ1", allocationSize = 1)
@Table(name="SCO_CARACT_USUARIO_CCUSTOS")
@SuppressWarnings("PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity")
public class ScoCaracteristicaUsuarioCentroCusto extends BaseEntitySeq<Integer> implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 7366834647595985247L;
	
	private Integer seq;
	private FccCentroCustos centroCusto;
	private DominioSimNao hierarquiaCcusto;
	private DominioTipoCentroCusto tipoCcusto;
	private RapServidores servidor;
	private ScoCaracteristica caracteristica;
	private Integer version;

    public ScoCaracteristicaUsuarioCentroCusto() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="scoCusSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CCT_CODIGO")
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}


	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}


	@Column(name="IND_HIERARQUIA_CCUSTO")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getHierarquiaCcusto() {
		return hierarquiaCcusto;
	}


	public void setHierarquiaCcusto(DominioSimNao hierarquiaCcusto) {
		this.hierarquiaCcusto = hierarquiaCcusto;
	}

	@Transient
	public boolean isSlcHierarquiaCcusto() {
		if (getHierarquiaCcusto() != null) {
			return getHierarquiaCcusto() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcHierarquiaCcusto(boolean valor) {
		setHierarquiaCcusto(DominioSimNao.getInstance(valor));
	}

	public void setTipoCcusto(DominioTipoCentroCusto tipoCcusto) {
		this.tipoCcusto = tipoCcusto;
	}

	@Column(name="IND_TIPO_CCUSTO")
	@Enumerated(EnumType.STRING)
	public DominioTipoCentroCusto getTipoCcusto() {
		return tipoCcusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
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

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CRT_CODIGO")
	public ScoCaracteristica getCaracteristica() {
		return caracteristica;
	}


	public void setCaracteristica(ScoCaracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}



	/**
	 * 
	 *
	 */
	public enum Fields {
		
		SEQ("seq"), 
		CENTRO_CUSTO("centroCusto"),
		CCT_CODIGO("centroCusto.codigo"),
		CARACTERISTICA("caracteristica"),
		TIPO_CCUSTO("tipoCcusto"),
		SERVIDOR("servidor"),		
		MATRICULA_SERVIDOR("servidor.id.matricula"),
		VINCULO_SERVIDOR("servidor.id.vinCodigo"),
		IND_HIERARQUIA("hierarquiaCcusto");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof ScoCaracteristicaUsuarioCentroCusto)) {
			return false;
		}
		ScoCaracteristicaUsuarioCentroCusto other = (ScoCaracteristicaUsuarioCentroCusto) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}