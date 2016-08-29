package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * 
 *  VO utilizado para listagem da persistencia de materiais relativos ao fornecedor.
 * @author mesias
 *
 */

public class SceMaterialPorFornecedorVO implements BaseBean  {

	private static final long serialVersionUID = 6378896166315946244L;
	private Integer fornecedor; // Numero do fornecedor
	private String  razaoSocial; // razao social do fornecedo
	private Long cnpj;          // cnpj do fornecedor
	private Long cpf;           // cpf do fornecedor
	private Integer codigo;     // codigo do material
	private String material;    // nome do material
	private String codigoMaterialForn;  // codigo do material no fornecedor
	private String descrMaterialForn;   // descricao do material
	private DominioSituacao situacao;   // situacao
	private Date criacao;               // data de criacao
	private String criadoPor;           // pessoa que criou o registro
	private DominioOrigemMaterialFornecedor origem; // origem da associacao
	private Date alteradoEm;            // data da ultima alteracao
	private String alteradoPor;         // pessoa que fez a ultima alteracao
	private Long dePara;             // pk da relacao material fornecedor
	
	/** Campos */
	public static enum Field {
		NUMERO_FORNECEDOR("fornecedor"), 
		RAZAO_SOCIAL("razaoSocial"), 
		CGC("cnpj"), 
		CPF("cpf"), 
		MAT_CODIGO("codigo"),
		NOME("material"),
		COD_MAT_FORN("codigoMaterialForn"),
		DESC_MAT_FORN("descrMaterialForn"),
		IND_SITUACAO("situacao"),
		DT_CRIACAO("criacao"),
		NOME_CRIACAO("criadoPor"),
		IND_ORIGEM("origem"),
		DT_ALTERACAO("alteradoEm"),
		NOME_ALTERACAO("alteradoPor"),
		SEQ("dePara")
		;
		
		private String field;
		
		Field(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return field;
		}
	}
	
	
	public Integer getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(Integer fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public Long getCnpj() {
		return cnpj;
	}
	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getCodigoMaterialForn() {
		return codigoMaterialForn;
	}
	public void setCodigoMaterialForn(String codigoMaterialForn) {
		this.codigoMaterialForn = codigoMaterialForn;
	}
	public String getDescrMaterialForn() {
		return descrMaterialForn;
	}
	public void setDescrMaterialForn(String descrMaterialForn) {
		this.descrMaterialForn = descrMaterialForn;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public Date getCriacao() {
		return criacao;
	}
	public void setCriacao(Date criacao) {
		this.criacao = criacao;
	}
	public String getCriadoPor() {
		return criadoPor;
	}
	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}
	public DominioOrigemMaterialFornecedor getOrigem() {
		return origem;
	}
	public void setOrigem(DominioOrigemMaterialFornecedor origem) {
		this.origem = origem;
	}
	public Date getAlteradoEm() {
		return alteradoEm;
	}
	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	public String getAlteradoPor() {
		return alteradoPor;
	}
	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}
	public Long getDePara() {
		return dePara;
	}
	public void setDePara(Long dePara) {
		this.dePara = dePara;
	}
	
}
