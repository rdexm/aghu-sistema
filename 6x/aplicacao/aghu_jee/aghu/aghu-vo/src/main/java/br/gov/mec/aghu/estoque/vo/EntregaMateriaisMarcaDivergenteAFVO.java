package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.commons.BaseBean;

public class EntregaMateriaisMarcaDivergenteAFVO implements BaseBean {
	
	private Integer nr;
	private Date dtGeracao;
	private Integer codGrupoMaterial;
	private String descGrupoMaterial;
	private Integer codMaterial;
	private String material;
	private Integer codMarcaEntrada;
	private String descMarcaEntrada;
	private Integer codMarcaAF;
	private String descMarcaAF;
	private Integer af;
	private Short complemento;
	private Short itemAf;
	private Long cnpj;
	private Long cpf;
	private String fornecedor;
	
	
	//Filtro
	private Date dataInicial;
	private Date dataFinal;
	private Integer nrSeq;
	private ScoMarcaComercial marcaComercial;
	private MaterialMDAFVO materialVO;
	private FornecedorVO fornecedorVO;
	private ScoGrupoMaterial grupoMaterial;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5503589777121334487L;

	public enum Fields {
			
		NR("nr"),
		DT_GERACAO("dtGeracao"),
		COD_GRUPO_MATERIAL("codGrupoMaterial"),
		DESC_GRUPO_MATERIAL("descGrupoMaterial"),
		COD_MATERIAL("codMaterial"),
		MATERIAl("material"),
		COD_MARCA_ENTRADA("codMarcaEntrada"),
		DESC_MARCA_ENTRADA("descMarcaEntrada"),
		COD_MARCA_AF("codMarcaAF"),
		DESC_MARCA_AF("descMarcaAF"),
		AF("af"),
		COMPLEMENTO("complemento"),
		ITEM_AF("itemAf"),
		CNPJ("cnpj"),
		CPF("cpf"),
		FORNECEDOR("fornecedor");
	
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/**
	 * @return the nr
	 */
	public Integer getNr() {
		return nr;
	}

	/**
	 * @param nr the nr to set
	 */
	public void setNr(Integer nr) {
		this.nr = nr;
	}

	/**
	 * @return the dtGeracao
	 */
	public Date getDtGeracao() {
		return dtGeracao;
	}

	/**
	 * @param dtGeracao the dtGeracao to set
	 */
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	/**
	 * @return the codGrupoMaterial
	 */
	public Integer getCodGrupoMaterial() {
		return codGrupoMaterial;
	}

	/**
	 * @param codGrupoMaterial the codGrupoMaterial to set
	 */
	public void setCodGrupoMaterial(Integer codGrupoMaterial) {
		this.codGrupoMaterial = codGrupoMaterial;
	}

	/**
	 * @return the descGrupoMaterial
	 */
	public String getDescGrupoMaterial() {
		return descGrupoMaterial;
	}

	/**
	 * @param descGrupoMaterial the descGrupoMaterial to set
	 */
	public void setDescGrupoMaterial(String descGrupoMaterial) {
		this.descGrupoMaterial = descGrupoMaterial;
	}

	/**
	 * @return the codMaterial
	 */
	public Integer getCodMaterial() {
		return codMaterial;
	}

	/**
	 * @param codMaterial the codMaterial to set
	 */
	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * @return the codMarcaEntrada
	 */
	public Integer getCodMarcaEntrada() {
		return codMarcaEntrada;
	}

	/**
	 * @param codMarcaEntrada the codMarcaEntrada to set
	 */
	public void setCodMarcaEntrada(Integer codMarcaEntrada) {
		this.codMarcaEntrada = codMarcaEntrada;
	}

	/**
	 * @return the descMarcaEntrada
	 */
	public String getDescMarcaEntrada() {
		return descMarcaEntrada;
	}

	/**
	 * @param descMarcaEntrada the descMarcaEntrada to set
	 */
	public void setDescMarcaEntrada(String descMarcaEntrada) {
		this.descMarcaEntrada = descMarcaEntrada;
	}

	/**
	 * @return the codMarcaAF
	 */
	public Integer getCodMarcaAF() {
		return codMarcaAF;
	}

	/**
	 * @param codMarcaAF the codMarcaAF to set
	 */
	public void setCodMarcaAF(Integer codMarcaAF) {
		this.codMarcaAF = codMarcaAF;
	}

	/**
	 * @return the descMarcaAF
	 */
	public String getDescMarcaAF() {
		return descMarcaAF;
	}

	/**
	 * @param descMarcaAF the descMarcaAF to set
	 */
	public void setDescMarcaAF(String descMarcaAF) {
		this.descMarcaAF = descMarcaAF;
	}

	/**
	 * @return the af
	 */
	public Integer getAf() {
		return af;
	}

	/**
	 * @param af the af to set
	 */
	public void setAf(Integer af) {
		this.af = af;
	}

	/**
	 * @return the complemento
	 */
	public Short getComplemento() {
		return complemento;
	}

	/**
	 * @param complemento the complemento to set
	 */
	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	/**
	 * @return the itemAf
	 */
	public Short getItemAf() {
		return itemAf;
	}

	/**
	 * @param itemAf the itemAf to set
	 */
	public void setItemAf(Short itemAf) {
		this.itemAf = itemAf;
	}

	/**
	 * @return the cnpj
	 */
	public Long getCnpj() {
		return cnpj;
	}

	/**
	 * @param cnpj the cnpj to set
	 */
	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	/**
	 * @return the cpf
	 */
	public Long getCpf() {
		return cpf;
	}

	/**
	 * @param cpf the cpf to set
	 */
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	/**
	 * @return the fornecedor
	 */
	public String getFornecedor() {
		return fornecedor;
	}

	/**
	 * @param fornecedor the fornecedor to set
	 */
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	
	//Filtro
	/**
	 * @return the dataInicial
	 */
	public Date getDataInicial() {
		return dataInicial;
	}

	/**
	 * @param dataInicial the dataInicial to set
	 */
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	/**
	 * @return the dataFinal
	 */
	public Date getDataFinal() {
		return dataFinal;
	}

	/**
	 * @param dataFinal the dataFinal to set
	 */
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	/**
	 * @return the nrSeq
	 */
	public Integer getNrSeq() {
		return nrSeq;
	}

	/**
	 * @param nrSeq the nrSeq to set
	 */
	public void setNrSeq(Integer nrSeq) {
		this.nrSeq = nrSeq;
	}

	/**
	 * @return the marcaComercial
	 */
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	/**
	 * @param marcaComercial the marcaComercial to set
	 */
	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	
	/**
	 * @return the materialVO
	 */
	public MaterialMDAFVO getMaterialVO() {
		return materialVO;
	}

	/**
	 * @param materialVO the materialVO to set
	 */
	public void setMaterialVO(MaterialMDAFVO materialVO) {
		this.materialVO = materialVO;
	}

	/**
	 * @return the fornecedorVO
	 */
	public FornecedorVO getFornecedorVO() {
		return fornecedorVO;
	}

	/**
	 * @param fornecedorVO the fornecedorVO to set
	 */
	public void setFornecedorVO(FornecedorVO fornecedorVO) {
		this.fornecedorVO = fornecedorVO;
	}

	/**
	 * @return the grupoMaterial
	 */
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	/**
	 * @param grupoMaterial the grupoMaterial to set
	 */
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	
}
