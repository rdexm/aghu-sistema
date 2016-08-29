package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;


/**
 * 
 * Filtro da pesquisa no cadastro basico de Material Fornecedor
 * @author mesias
 *
 */
public class FiltroMaterialFornecedorVO implements Serializable {
	
	private static final long serialVersionUID = -2394484523090797759L;

	private ScoFornecedor fornecedor;
	private ScoMaterial   material;
	private String codigoMaterialFornecedor;
	private String  descricaoMaterialFornecedor;
	private DominioSituacao situacao;
	private DominioOrigemMaterialFornecedor origem;
	private Paginacao paginacao;

	public static class Paginacao {
		private Integer firstResult;
		private Integer maxResult;
		private String orderProperty;
		private boolean asc;
		
		public Paginacao() {
			super();
		}

		public Paginacao(Integer firstResult, Integer maxResult,
				String orderProperty, boolean asc) {
			super();
			this.firstResult = firstResult;
			this.maxResult = maxResult;
			this.orderProperty = orderProperty;
			this.asc = asc;
		}

		public Integer getFirstResult() {
			return firstResult;
		}

		public void setFirstResult(Integer firstResult) {
			this.firstResult = firstResult;
		}

		public Integer getMaxResult() {
			return maxResult;
		}

		public void setMaxResult(Integer maxResult) {
			this.maxResult = maxResult;
		}

		public String getOrderProperty() {
			return orderProperty;
		}

		public void setOrderProperty(String orderProperty) {
			this.orderProperty = orderProperty;
		}

		public boolean isAsc() {
			return asc;
		}

		public void setAsc(boolean asc) {
			this.asc = asc;
		}
	}

	public FiltroMaterialFornecedorVO(){
		this.setPaginacao(new Paginacao());
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public String getCodigoMaterialFornecedor() {
		return codigoMaterialFornecedor;
	}

	public void setCodigoMaterialFornecedor(String codigoMaterialFornecedor) {
		this.codigoMaterialFornecedor = codigoMaterialFornecedor;
	}

	public String getDescricaoMaterialFornecedor() {
		return descricaoMaterialFornecedor;
	}

	public void setDescricaoMaterialFornecedor(String descricaoMaterialFornecedor) {
		this.descricaoMaterialFornecedor = descricaoMaterialFornecedor;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioOrigemMaterialFornecedor getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemMaterialFornecedor origem) {
		this.origem = origem;
	}

	public Paginacao getPaginacao() {
		return paginacao;
	}

	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}	
}