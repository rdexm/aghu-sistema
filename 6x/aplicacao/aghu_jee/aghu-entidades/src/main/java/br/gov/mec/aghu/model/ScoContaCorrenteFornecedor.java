package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_conta_corrente_fornecedor database table.
 * 
 */
@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name="SCO_CONTA_CORRENTE_FORNECEDOR")
public class ScoContaCorrenteFornecedor extends BaseEntityId<ScoContaCorrenteFornecedorId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 7381489609240786697L;
private ScoContaCorrenteFornecedorId id;
	private DominioSimNao indPreferencial;
	private Integer version;
	private ScoFornecedor scoFornecedor; 
	private FcpBanco banco;

    public ScoContaCorrenteFornecedor() {
    }


	@EmbeddedId
	public ScoContaCorrenteFornecedorId getId() {
		return this.id;
	}

	public void setId(ScoContaCorrenteFornecedorId id) {
		this.id = id;
	}
	

	@Column(name="IND_PREFERENCIAL")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPreferencial() {
		return this.indPreferencial;
	}

	public void setIndPreferencial(DominioSimNao indPreferencial) {
		this.indPreferencial = indPreferencial;
	}


	public Integer getVersion() {
		return this.version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FRN_NUMERO", referencedColumnName="NUMERO", insertable = false, updatable = false)
	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGB_BCO_CODIGO", referencedColumnName = "CODIGO", nullable = false, insertable = false, updatable = false)
	public FcpBanco getBanco() {
		return banco;
	}
	
	public void setBanco(FcpBanco banco) {
		this.banco = banco;
	}
	public enum Fields {
		AGB_BCO_CODIGO("id.agbBcoCodigo"),
		AGB_CODIGO("id.agbCodigo"),
		IND_PREFERENCIAL("indPreferencial"),
		CONTA_CORRENTE("id.contaCorrente"),
		SCO_FORNECEDOR("scoFornecedor"),
		SCO_FORNECEDOR_NUMERO("scoFornecedor.numero"),
		BANCO("banco");
		
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
		if (!(obj instanceof ScoContaCorrenteFornecedor)) {
			return false;
		}
		ScoContaCorrenteFornecedor other = (ScoContaCorrenteFornecedor) obj;
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