package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class SceRelacionamentoMaterialFornecedorVO implements BaseBean  {

	private static final long serialVersionUID = 878116036442894435L;

	private Long seq;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String codigoMaterialFornecedor;
	private String descricaoMaterialFornecedor;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private Long cgcFornecedor;
	private Long cpfFornecedor;
	private DominioSituacao situacao;
	private DominioOrigemMaterialFornecedor origem;
	private String criadoPor;
	private Date dataCriacao;
	private String alteradoPor;
	private Date dataAlteracao;

	public enum Fields {
		SEQ("seq"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		CODIGO_MATERIAL_FORNECEDOR("codigoMaterialFornecedor"),
		DESCRICAO_MATERIAL_FORNECEDOR("descricaoMaterialFornecedor"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor"),
		CGC_FORNECEDOR("cgcFornecedor"),
		CPF_FORNECEDOR("cpfFornecedor"),
		SITUACAO("situacao"),
		ORIGEM("origem"),
		CRIADO_POR("criadoPor"),
		DATA_CRIACAO("dataCriacao"),
		ALTERADO_POR("alteradoPor"),
		DATA_ALTERACAO("dataAlteracao");

		private String value;

		private Fields(String value){
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
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

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
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

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	public Long getCgcFornecedor() {
		return cgcFornecedor;
	}

	public void setCgcFornecedor(Long cgcFornecedor) {
		this.cgcFornecedor = cgcFornecedor;
	}

	public Long getCpfFornecedor() {
		return cpfFornecedor;
	}

	public void setCpfFornecedor(Long cpfFornecedor) {
		this.cpfFornecedor = cpfFornecedor;
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

	public String getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
}
