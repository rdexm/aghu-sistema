package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;

public class MateriaisProcedimentoOPMEVO implements Serializable {

	private static final long serialVersionUID = 2834237044315568431L;

	// atributos da tela
	private String materialSUS;
	private String licitado;
	private Integer qtdeAut;
	private BigDecimal valorUnitario;
	private BigDecimal valorTotal;
	private String materialHosp;

	// atributos da consulta
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Long codTabela;
	private String descricao;
	private Boolean indCompativel;
	private DominioRequeridoItemRequisicao requerido;
	private String requeridoString;
	private Integer quantidadeSolicitada;
	private Integer quantidadeAutorizadaHospital;
	private BigDecimal valorUnitarioIph;
	protected Integer phiSeq;
	private Integer materialCodigo;
	private String materialMarca;
	private Integer materialQuantidade;
	private String nome;
	private String unidadeMedidaCodigo;
	private String solicitacaoNovoMaterial;
	private byte[] anexoOrcamento;
	private Integer slcNumero;
	private Short materialItemSeq;
			

	public String getMaterialSUS() {
		return materialSUS;
	}

	public String getLicitado() {
		return licitado;
	}

	public Integer getQtdeAut() {
		return qtdeAut;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public String getMaterialHosp() {
		return materialHosp;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public Boolean getIndCompativel() {
		return indCompativel;
	}

	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public Integer getQuantidadeAutorizadaHospital() {
		return quantidadeAutorizadaHospital;
	}

	public BigDecimal getValorUnitarioIph() {
		return valorUnitarioIph;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public Integer getMaterialCodigo() {
		return materialCodigo;
	}

	public String getNome() {
		return nome;
	}

	public String getUnidadeMedidaCodigo() {
		return unidadeMedidaCodigo;
	}

	public void setMaterialSUS(String materialSUS) {
		this.materialSUS = materialSUS;
	}

	public void setLicitado(String licitado) {
		this.licitado = licitado;
	}

	public void setQtdeAut(Integer qtdeAut) {
		this.qtdeAut = qtdeAut;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public void setMaterialHosp(String materialHosp) {
		this.materialHosp = materialHosp;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setIndCompativel(Boolean indCompativel) {
		this.indCompativel = indCompativel;
	}

	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	public void setQuantidadeAutorizadaHospital(
			Integer quantidadeAutorizadaHospital) {
		this.quantidadeAutorizadaHospital = quantidadeAutorizadaHospital;
	}

	public void setValorUnitarioIph(BigDecimal valorUnitarioIph) {
		this.valorUnitarioIph = valorUnitarioIph;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public void setMaterialCodigo(Integer materialCodigo) {
		this.materialCodigo = materialCodigo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setUnidadeMedidaCodigo(String unidadeMedidaCodigo) {
		this.unidadeMedidaCodigo = unidadeMedidaCodigo;
	}

	public String getSolicitacaoNovoMaterial() {
		return solicitacaoNovoMaterial;
	}

	public void setSolicitacaoNovoMaterial(String solicitacaoNovoMaterial) {
		this.solicitacaoNovoMaterial = solicitacaoNovoMaterial;
	}

	public void setAnexoOrcamento(byte[] anexoOrcamento) {
		this.anexoOrcamento = anexoOrcamento;
	}

	public byte[] getAnexoOrcamento() {
		return anexoOrcamento;
	}

	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	public Short getMaterialItemSeq() {
		return materialItemSeq;
	}

	public void setMaterialItemSeq(Short materialItemSeq) {
		this.materialItemSeq = materialItemSeq;
	}


	public enum Fields {
		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_SEQ("iphSeq"),
		COD_TABELA("codTabela"),
		DESCRICAO("descricao"),
		IND_COMPATIVEL("indCompativel"),
		REQUERIDO("requerido"),
		QUANTIDADE_SOLICITADA("quantidadeSolicitada"),
		QUANTIDADE_AUTORIZADA_HOSPITAL("quantidadeAutorizadaHospital"),
		VALOR_UNITARIO_IPH("valorUnitarioIph"),
		PHI_SEQ("phiSeq"),
		MATERIAL_CODIGO("materialCodigo"),
		MATERIAL_MARCA("materialMarca"),
		MATERIAL_QUANTIDADE("materialQuantidade"),
		NOME("nome"),
		UNIDADE_MEDIDA_CODIGO("unidadeMedidaCodigo"),
		SOLICITACAO_NOVO_MATERIAL("solicitacaoNovoMaterial"),
		ANEXO_ORCAMENTO("anexoOrcamento"),
		REQUERIDO_STRING("requeridoString"),
		SLC_NUMERO("slcNumero"),
		MATERIAL_ITEM_SEQ("materialItemSeq");

		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public DominioRequeridoItemRequisicao getRequerido() {
		return requerido;
	}

	public void setRequerido(DominioRequeridoItemRequisicao requerido) {
		this.requerido = requerido;
	}

	public String getMaterialMarca() {
		return materialMarca;
	}

	public void setMaterialMarca(String materialMarca) {
		this.materialMarca = materialMarca;
	}

	public Integer getMaterialQuantidade() {
		return materialQuantidade;
	}

	public void setMaterialQuantidade(Integer materialQuantidade) {
		this.materialQuantidade = materialQuantidade;
	}

	public String getRequeridoString() {
		return requeridoString;
	}

	public void setRequeridoString(String requeridoString) {
		this.requeridoString = requeridoString;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		if(codTabela == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + codTabela.hashCode();
		}
		if(iphPhoSeq == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + iphPhoSeq.hashCode(); 
		}
		if(iphSeq == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + iphSeq.hashCode(); 
		}
		
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
		if (!(obj instanceof MateriaisProcedimentoOPMEVO)) {
			return false;
		}
		MateriaisProcedimentoOPMEVO other = (MateriaisProcedimentoOPMEVO) obj;
		if (codTabela == null) {
			if (other.codTabela != null) {
				return false;
			}
		} else if (!codTabela.equals(other.codTabela)) {
			return false;
		}
		if (iphPhoSeq == null) {
			if (other.iphPhoSeq != null) {
				return false;
			}
		} else if (!iphPhoSeq.equals(other.iphPhoSeq)) {
			return false;
		}
		if (iphSeq == null) {
			if (other.iphSeq != null) {
				return false;
			}
		} else if (!iphSeq.equals(other.iphSeq)) {
			return false;
		}
		return true;
	}
	
}