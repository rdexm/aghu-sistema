package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;

/**
 * Os dados armazenados nesse objeto representam os Itens de uma Licitação
 * 
 * @author Lilian
 */
public class ItensPACVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;

	private Integer numeroLicitacao;
	
	private String dtDigitacao;
	
	private Date dataDigitacao;
	
	private String dtLimiteAceiteProposta;
	
	private Date dataLimiteAceiteProposta;
	
	private String descricaoLicitacaoModalidade;
	
	private String descricaoLicitacao;
	
	private String descricaoModalidade;
	
	private String dthrAberturaProposta;
	
	private Date datahrAberturaProposta;
	
	private Short numeroItem;
	
	private Integer codigoMaterial;
	
	private String nomeMaterial;
	
	private Long somaQtdeAprovada;
	
	private String descricaoMaterial;
	
	private String codigoUnidadeMedida;
	
	private String descricaoUnidadeMedida;
	
	private Integer numeroSolicitacaoCompra;
	
	private String descricaoSolicitacaoCompra;
	
	private DominioTipoFaseSolicitacao tipoFaseSolicitacao;
	
	private Short numeroLote;
	
	private BigDecimal valorUnitario;
	
	private Boolean exclusao;
	
	// GETs and SETs	

	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public String getDescricaoLicitacaoModalidade() {
		return descricaoLicitacaoModalidade;
	}

	public void setDescricaoLicitacaoModalidade(String descricaoLicitacaoModalidade) {
		this.descricaoLicitacaoModalidade = descricaoLicitacaoModalidade;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public Long getSomaQtdeAprovada() {
		return somaQtdeAprovada;
	}

	public void setSomaQtdeAprovada(Long somaQtdeAprovada) {
		this.somaQtdeAprovada = somaQtdeAprovada;
	}	

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public String getDescricaoUnidadeMedida() {
		return descricaoUnidadeMedida;
	}

	public void setDescricaoUnidadeMedida(String descricaoUnidadeMedida) {
		this.descricaoUnidadeMedida = descricaoUnidadeMedida;
	}

	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}

	public String getDescricaoSolicitacaoCompra() {
		return descricaoSolicitacaoCompra;
	}

	public void setDescricaoSolicitacaoCompra(String descricaoSolicitacaoCompra) {
		this.descricaoSolicitacaoCompra = descricaoSolicitacaoCompra;
	}
	
	public String getDescricaoLicitacao() {
		return descricaoLicitacao;
	}

	public void setDescricaoLicitacao(String descricaoLicitacao) {
		this.descricaoLicitacao = descricaoLicitacao;
	}

	public String getDescricaoModalidade() {
		return descricaoModalidade;
	}

	public void setDescricaoModalidade(String descricaoModalidade) {
		this.descricaoModalidade = descricaoModalidade;
	}	
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterialServico(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	
	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

	public enum Fields {
		NRO_LICITACAO("numeroLicitacao"),
		DATA_DIGITACAO("dataDigitacao"),
		DATA_LIMITE_ACEITE_PROPOSTA("dataLimiteAceiteProposta"),
		DESCRICAO_LICITACAO("descricaoLicitacao"),
		DESCRICAO_MODALIDADE("descricaoModalidade"),
		DATA_HR_ABERTURA_PROPOSTA("datahrAberturaProposta"),
		NUMERO_ITEM("numeroItem"),
		CODIGO_MATERIAL("codigoMaterial"),
		SOMA_QTDE_APROVADA("somaQtdeAprovada"),	
		NOME_MATERIAL("nomeMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		CODIGO_UNIDADE_MEDIDA("codigoUnidadeMedida"),
		DESCRICAO_UNIDADE_MEDIDA("descricaoUnidadeMedida"),
		NRO_SOLICITACAO_COMPRA("numeroSolicitacaoCompra"),
		DESCRICAO_SOLICITACAO_COMPRA("descricaoSolicitacaoCompra"),
		TIPO_FASE_SOLICITACAO("tipoFaseSolicitacao"),
		NUMERO_LOTE("numeroLote"),
		VALOR_UNITARIO("valorUnitario"),
		EXCLUSAO("exclusao");

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
		
		result = prime * result
				+ ((numeroItem == null) ? 0 : numeroItem.hashCode());
		result = prime * result
				+ ((numeroLicitacao == null) ? 0 : numeroLicitacao.hashCode());
		
		return result;
	}
	public boolean equalsMaterial(ItensPACVO other){
		if (codigoMaterial == null) {
			if (other.codigoMaterial != null){
				return false;
			}
		} else if (!codigoMaterial.equals(other.codigoMaterial)){
			return false;
		}
		if (codigoUnidadeMedida == null) {
			if (other.codigoUnidadeMedida != null){
				return false;
			}
		} else if (!codigoUnidadeMedida.equals(other.codigoUnidadeMedida)){
			return false;
		}
		if (descricaoMaterial == null) {
			if (other.descricaoMaterial != null){
				return false;
			}
		} else if (!descricaoMaterial
				.equals(other.descricaoMaterial)){
			return false;
		}
		if (nomeMaterial == null) {
			if (other.nomeMaterial != null){
				return false;
			}
		} else if (!nomeMaterial.equals(other.nomeMaterial)){
			return false;
		}
		return true;		
	}
	
	public boolean equalsDatas(ItensPACVO other){
		if (dataDigitacao == null) {
			if (other.dataDigitacao != null){
				return false;
			}
		} else if (!dataDigitacao.equals(other.dataDigitacao)){
			return false;
		}
		if (dataLimiteAceiteProposta == null) {
			if (other.dataLimiteAceiteProposta != null){
				return false;
			}
		} else if (!dataLimiteAceiteProposta
				.equals(other.dataLimiteAceiteProposta)){
			return false;
		}
		if (datahrAberturaProposta == null) {
			if (other.datahrAberturaProposta != null){
				return false;
			}
		} else if (!datahrAberturaProposta.equals(other.datahrAberturaProposta)){
			return false;
		}	
		if (dtDigitacao == null) {
			if (other.dtDigitacao != null){
				return false;
			}
		} else if (!dtDigitacao.equals(other.dtDigitacao)){
			return false;
		}			
		if (!equalsDatasProposta(other)){
			return false;
		}
		return true;
	}
	public boolean equalsDatasProposta(ItensPACVO other){
		if (dtLimiteAceiteProposta == null) {
			if (other.dtLimiteAceiteProposta != null){
				return false;
			}
		} else if (!dtLimiteAceiteProposta.equals(other.dtLimiteAceiteProposta)){
			return false;
		}
		if (dthrAberturaProposta == null) {
			if (other.dthrAberturaProposta != null){
				return false;
			}
		} else if (!dthrAberturaProposta.equals(other.dthrAberturaProposta)){
			return false;
		}		
		return true;
		
	}
	public boolean equalsDescricao(ItensPACVO other){
		if (descricaoLicitacao == null) {
			if (other.descricaoLicitacao != null){
				return false;
			}
		} else if (!descricaoLicitacao.equals(other.descricaoLicitacao)){
			return false;
		}
		if (descricaoLicitacaoModalidade == null) {
			if (other.descricaoLicitacaoModalidade != null){
				return false;
			}
		} else if (!descricaoLicitacaoModalidade
				.equals(other.descricaoLicitacaoModalidade)){
			return false;
		}
		
		if (descricaoModalidade == null) {
			if (other.descricaoModalidade != null){
				return false;
			}
		} else if (!descricaoModalidade.equals(other.descricaoModalidade)){
			return false;
		}
	
		if (!this.equalsDescricaoSCUnidadeMedida(other)){
			return false;
		}
		
		return true;
	}
	
	public boolean equalsDescricaoSCUnidadeMedida(ItensPACVO other){
		if (descricaoSolicitacaoCompra == null) {
			if (other.descricaoSolicitacaoCompra != null){
				return false;
			}
		} else if (!descricaoSolicitacaoCompra
				.equals(other.descricaoSolicitacaoCompra)){
			return false;
		}
		if (descricaoUnidadeMedida == null) {
			if (other.descricaoUnidadeMedida != null){
				return false;
			}
		} else if (!descricaoUnidadeMedida.equals(other.descricaoUnidadeMedida)){
			return false;
		}
		return true;
	}
	public boolean equalsNumeros(ItensPACVO other){
		if (numeroItem == null) {
			if (other.numeroItem != null){
				return false;
			}
		} else if (!numeroItem.equals(other.numeroItem)){
			return false;
		}
		if (numeroLicitacao == null) {
			if (other.numeroLicitacao != null){
				return false;
			}
		} else if (!numeroLicitacao.equals(other.numeroLicitacao)){
			return false;
		}
		if (numeroSolicitacaoCompra == null) {
			if (other.numeroSolicitacaoCompra != null){
				return false;
			}
		} else if (!numeroSolicitacaoCompra
				.equals(other.numeroSolicitacaoCompra)){
			return false;
		}
		return true;
	}
	public boolean equalsTipoFaseSolicitacao(ItensPACVO other){
		if (tipoFaseSolicitacao == null) {
			if (other.tipoFaseSolicitacao != null){
				return false;
			}
		} else if (!tipoFaseSolicitacao.equals(other.tipoFaseSolicitacao)){
			return false;
		}
		return true;
	}
	
	@Override	
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ItensPACVO other = (ItensPACVO) obj;
		
		
        if (!this.equalsMaterial(other) ||
        	!this.equalsDatas(other) ||
        	!this.equalsDescricao(other) ||
        	!this.equalsNumeros(other) ||
        	!this.equalsTipoFaseSolicitacao(other)){
        	return false;
        }  
        		
		
		if (somaQtdeAprovada == null) {
			if (other.somaQtdeAprovada != null){
				return false;
			}
		} else if (!somaQtdeAprovada.equals(other.somaQtdeAprovada)){
			return false;
		}
		return true;
	}

	public String getDtDigitacao() {
		return dtDigitacao;
	}

	public void setDtDigitacao(String dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	public Date getDataDigitacao() {
		return dataDigitacao;
	}

	public void setDataDigitacao(Date dataDigitacao) {
		this.dataDigitacao = dataDigitacao;
	}

	public String getDtLimiteAceiteProposta() {
		return dtLimiteAceiteProposta;
	}

	public void setDtLimiteAceiteProposta(String dtLimiteAceiteProposta) {
		this.dtLimiteAceiteProposta = dtLimiteAceiteProposta;
	}

	public Date getDataLimiteAceiteProposta() {
		return dataLimiteAceiteProposta;
	}

	public void setDataLimiteAceiteProposta(Date dataLimiteAceiteProposta) {
		this.dataLimiteAceiteProposta = dataLimiteAceiteProposta;
	}

	public String getDthrAberturaProposta() {
		return dthrAberturaProposta;
	}

	public void setDthrAberturaProposta(String dthrAberturaProposta) {
		this.dthrAberturaProposta = dthrAberturaProposta;
	}

	public Date getDatahrAberturaProposta() {
		return datahrAberturaProposta;
	}

	public void setDatahrAberturaProposta(Date datahrAberturaProposta) {
		this.datahrAberturaProposta = datahrAberturaProposta;
	}

	public DominioTipoFaseSolicitacao getTipoFaseSolicitacao() {
		return tipoFaseSolicitacao;
	}

	public void setTipoFaseSolicitacao(
			DominioTipoFaseSolicitacao tipoFaseSolicitacao) {
		this.tipoFaseSolicitacao = tipoFaseSolicitacao;
	}
	public Short getNumeroLote() {
		return numeroLote;
	}
	public void setNumeroLote(Short numeroLote) {
		this.numeroLote = numeroLote;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
			
}
