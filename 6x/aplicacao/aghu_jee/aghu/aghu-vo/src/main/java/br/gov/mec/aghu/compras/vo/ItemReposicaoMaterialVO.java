package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioInclusaoLoteReposicao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.core.commons.BaseBean;


public class ItemReposicaoMaterialVO implements BaseBean{
	
	private static final long serialVersionUID = 2952427833661102069L;
	private Integer matCodigo;
	private String nomeMaterial;
	private String descricaoMaterial;
	private Integer codigoGrupoMaterial;
	private String nomeGrupoMaterial;
	private String tipoMaterial;
	private BigDecimal custoMedio;
	private Integer pontoPedido;
	private Integer tempoReposicao;
	private Integer qtdOriginal;
	private Integer qtd;
	private Integer slcGerada;
	private DominioInclusaoLoteReposicao indInclusao;
	private Boolean confirmada;
	private BigDecimal valor;
	private Integer seqItem;
	private List<SolReposicaoMaterialVO> listaScRelacionada;
	private FccCentroCustos ccAlmoxLocalEstoque;
	

	public FccCentroCustos getCcAlmoxLocalEstoque() {
				return ccAlmoxLocalEstoque;
			}
			public void setCcAlmoxLocalEstoque(FccCentroCustos ccAlmoxLocalEstoque) {
				this.ccAlmoxLocalEstoque = ccAlmoxLocalEstoque;
			}
	
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public String getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(String tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public BigDecimal getCustoMedio() {
		return custoMedio;
	}

	public void setCustoMedio(BigDecimal custoMedio) {
		this.custoMedio = custoMedio;
	}

	public Integer getPontoPedido() {
		return pontoPedido;
	}

	public void setPontoPedido(Integer pontoPedido) {
		this.pontoPedido = pontoPedido;
	}

	public Integer getTempoReposicao() {
		return tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}

	public Integer getQtdOriginal() {
		return qtdOriginal;
	}

	public void setQtdOriginal(Integer qtdOriginal) {
		this.qtdOriginal = qtdOriginal;
	}

	public Integer getQtd() {
		return qtd;
	}

	public void setQtd(Integer qtd) {
		this.qtd = qtd;
	}

	public Integer getSlcGerada() {
		return slcGerada;
	}

	public void setSlcGerada(Integer slcGerada) {
		this.slcGerada = slcGerada;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matCodigo == null) ? 0 : matCodigo.hashCode());
		return result;
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
		ItemReposicaoMaterialVO other = (ItemReposicaoMaterialVO) obj;
		if (matCodigo == null) {
			if (other.matCodigo != null){
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)){
			return false;
		}
		return true;
	}
	
	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public String getNomeGrupoMaterial() {
		return nomeGrupoMaterial;
	}

	public void setNomeGrupoMaterial(String nomeGrupoMaterial) {
		this.nomeGrupoMaterial = nomeGrupoMaterial;
	}

	public Boolean getConfirmada() {
		return confirmada;
	}

	public void setConfirmada(Boolean confirmada) {
		this.confirmada = confirmada;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public List<SolReposicaoMaterialVO> getListaScRelacionada() {
		return listaScRelacionada;
	}

	public void setListaScRelacionada(List<SolReposicaoMaterialVO> listaScRelacionada) {
		this.listaScRelacionada = listaScRelacionada;
	}

	public DominioInclusaoLoteReposicao getIndInclusao() {
		return indInclusao;
	}

	public void setIndInclusao(DominioInclusaoLoteReposicao indInclusao) {
		this.indInclusao = indInclusao;
	}

	public Integer getSeqItem() {
		return seqItem;
	}

	public void setSeqItem(Integer seqItem) {
		this.seqItem = seqItem;
	}

	public enum Fields {
		
		MAT_CODIGO("matCodigo"),
		NOME_MATERIAL("nomeMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		CODIGO_GRUPO("codigoGrupoMaterial"),
		NOME_GRUPO("nomeGrupoMaterial"),
		TIPO_MATERIAL("tipoMaterial"),
		CUSTO_MEDIO("custoMedio"),
		PONTO_PEDIDO("pontoPedido"),
		TEMPO_REPOSICAO("tempoReposicao"),
		QTDE_ORIGINAL("qtdOriginal"),
		QTDE("qtd"),
		SEQ_ITEM("seqItem"),
		SLC_GERADA("slcGerada"),
		IND_INCLUSAO("indInclusao"),
		VALOR("valor"),
		CC_ALMOX("ccAlmoxLocalEstoque"),
		CONFIRMADA("confirmada");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
