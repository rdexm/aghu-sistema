package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;

public class ConsultaItensAFProgramacaoManualVO implements Serializable {

	private static final long serialVersionUID = 2754239634711226991L;

	private Integer lctNumero;
	private Short nroComplemento;
	private Integer numeroItem;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String razaoSocial;
	private Integer qtdeSolicitada;
	private Long qtde;
	private Long saldo;
	private Integer fatorConversao;
	private Byte diaFavEntgMaterialFornecedor;
	private DominioDiaSemanaMes diaFavEntgMaterialGrupoMaterial;
	private Integer slcNumero;
	private Double valorUnitario; 
	private Integer afnNumero;
	private Boolean selecionado;
	private Boolean possuiParcela;
	private Short itlNumero;

	public ConsultaItensAFProgramacaoManualVO() {
		super();
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public Long getQtde() {
		return qtde;
	}

	public void setQtde(Long qtde) {
		this.qtde = qtde;
	}

	public Long getSaldo() {
		return saldo;
	}

	public void setSaldo(Long saldo) {
		this.saldo = saldo;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Byte getDiaFavEntgMaterialFornecedor() {
		return diaFavEntgMaterialFornecedor;
	}

	public void setDiaFavEntgMaterialFornecedor(Byte diaFavEntgMaterialFornecedor) {
		this.diaFavEntgMaterialFornecedor = diaFavEntgMaterialFornecedor;
	}

	public DominioDiaSemanaMes getDiaFavEntgMaterialGrupoMaterial() {
		return diaFavEntgMaterialGrupoMaterial;
	}

	public void setDiaFavEntgMaterialGrupoMaterial(
			DominioDiaSemanaMes diaFavEntgMaterialGrupoMaterial) {
		this.diaFavEntgMaterialGrupoMaterial = diaFavEntgMaterialGrupoMaterial;
	}

	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getPossuiParcela() {
		return possuiParcela;
	}

	public void setPossuiParcela(Boolean possuiParcela) {
		this.possuiParcela = possuiParcela;
	}

	public Short getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	public enum Fields {
		PFR_LCT_NUMERO("lctNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		NUMERO_DO_ITEM("numeroItem"),
		NOME_MATERIAL("nomeMaterial"),
		CODIGO_MATERIAL("codigoMaterial"),
		RAZAO_SOCIAL("razaoSocial"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		QTDE("qtde"),
		SALDO("saldo"),
		FATOR_CONVERSAO("fatorConversao"),
		DIA_FAV_ENTG_MAT_FORN("diaFavEntgMaterialFornecedor"),
		DIA_FAV_ENTG_MAT_GRUPO_MAT("diaFavEntgMaterialGrupoMaterial"),
		SLC_NUMERO("slcNumero"),
		VALOR_UNITARIO("valorUnitario"),
		AFN_NUMERO("afnNumero"),
		SELECIONADO("selecionado"),
		ITL_NUMERO("itlNumero");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	
	
}