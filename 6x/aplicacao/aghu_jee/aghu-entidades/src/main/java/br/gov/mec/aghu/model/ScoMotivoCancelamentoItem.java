package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "SCO_MOTIVOS_CANCEL_ITENS_PAC", schema = "AGH")
public class ScoMotivoCancelamentoItem extends BaseEntityCodigo<String> implements java.io.Serializable {

	private static final long serialVersionUID = 647315848818204079L;

	/**
	 * Chave primária na base de dados.
	 */
	private String codigo;
	
	/**
	 * Descrição do motivo do cancelamento.
	 */
	private String descricao;
	/**
	 * 
	 */
	private DominioSituacao indAtivo;

	private Integer version;

	public ScoMotivoCancelamentoItem() {
		
	}
	public ScoMotivoCancelamentoItem(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public ScoMotivoCancelamentoItem(String codigo, String descricao,
			DominioSituacao indAtivo) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.indAtivo = indAtivo;
	}
	
	@Id
	@Column(name = "CODIGO", unique = true, nullable = false)
	@Length(max = 3)
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "IND_SITUACAO",nullable = false, length=1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndAtivo() {
		return indAtivo;
	}
	public void setIndAtivo(DominioSituacao indAtivo) {
		this.indAtivo = indAtivo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false, length=9)
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * Campo sintético criado para mapear diretamente este dominio booleano em
	 * um componente selectOneCheckBox.
	 * 
	 * @author joao.gloria
	 * @return
	 */
	@Transient
	public boolean isAtivo() {
		if (getIndAtivo() != null) {
			return getIndAtivo() == DominioSituacao.A;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setAtivo(boolean valor) {
		setIndAtivo(DominioSituacao.getInstance(valor));
	}

	@Transient
	public String getDescricaoAtivo() {
		return isAtivo() ? "Sim" : "Não";
	}

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao"), IND_ATIVO(
				"indAtivo");

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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof ScoMotivoCancelamentoItem) ){
			return false;
		}
		ScoMotivoCancelamentoItem other = (ScoMotivoCancelamentoItem) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}

}