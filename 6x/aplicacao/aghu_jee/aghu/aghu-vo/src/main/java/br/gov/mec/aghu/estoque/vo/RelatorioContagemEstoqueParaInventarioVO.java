/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

/**
 * @author rodrigo.figueiredo
 *
 */

public class RelatorioContagemEstoqueParaInventarioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2146358152626081374L;
	public enum Fields{
		CODIGO_GRUPO_MATERIAL("codigoGrupoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		CODIGO_MATERIAL("codigoMaterial"),
		UNIDADE_MEDIDA_CODIGO("unidadeMedidaCodigo"),
		ENDERECO_ESTOQUE_ALMOX("enderecoEstoqueAlmox"),
		QUANTIDADE_BLOQUEADA_ESTOQUE_ALMOX("quantidadeBloqueadaEstoqueAlmox"),
		QUANTIDADE_DISPONIVEL_ESTOQUE_ALMOX("quantidadeDisponivelEstoqueAlmox"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		
		ORDEM("ordem");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	private Integer codigoGrupoMaterial;//GR
	private String nomeMaterial;
	private Integer codigoMaterial;	
	private String unidadeMedidaCodigo;
	private String enderecoEstoqueAlmox;
	private Integer quantidadeBloqueadaEstoqueAlmox;
	private Integer quantidadeDisponivelEstoqueAlmox;
	private Integer numeroFornecedor;
	
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getSaldo() {
		Integer saldo = null;
		if(getQuantidadeBloqueadaEstoqueAlmox() != null)
		{
			saldo = getQuantidadeBloqueadaEstoqueAlmox();
		}
		if(getQuantidadeDisponivelEstoqueAlmox() != null)
		{
			if (saldo != null){
				saldo += getQuantidadeDisponivelEstoqueAlmox();
			}else{
				saldo = getQuantidadeDisponivelEstoqueAlmox();
			}
		}
		
		return saldo;
	}
	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}
	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getUnidadeMedidaCodigo() {
		return unidadeMedidaCodigo;
	}
	public void setUnidadeMedidaCodigo(String unidadeMedidaCodigo) {
		this.unidadeMedidaCodigo = unidadeMedidaCodigo;
	}
	public String getEnderecoEstoqueAlmox() {
		return enderecoEstoqueAlmox;
	}
	public void setEnderecoEstoqueAlmox(String enderecoEstoqueAlmox) {
		this.enderecoEstoqueAlmox = enderecoEstoqueAlmox;
	}
	public Integer getQuantidadeBloqueadaEstoqueAlmox() {
		return quantidadeBloqueadaEstoqueAlmox;
	}
	public void setQuantidadeBloqueadaEstoqueAlmox(
			Integer quantidadeBloqueadaEstoqueAlmox) {
		this.quantidadeBloqueadaEstoqueAlmox = quantidadeBloqueadaEstoqueAlmox;
	}
	public Integer getQuantidadeDisponivelEstoqueAlmox() {
		return quantidadeDisponivelEstoqueAlmox;
	}
	public void setQuantidadeDisponivelEstoqueAlmox(
			Integer quantidadeDisponivelEstoqueAlmox) {
		this.quantidadeDisponivelEstoqueAlmox = quantidadeDisponivelEstoqueAlmox;
	}
}
	
	
	
	