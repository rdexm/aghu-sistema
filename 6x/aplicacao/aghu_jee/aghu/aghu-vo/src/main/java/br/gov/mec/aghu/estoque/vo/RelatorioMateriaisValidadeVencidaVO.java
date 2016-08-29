/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Rodrigo.Figueiredo
 *
 */

public class RelatorioMateriaisValidadeVencidaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 930107915784689292L;
	public enum Fields{
		GMT_CODIGO("codigoGrupo"),
		GMT_DESCRICAO("gmtDescricao"),
		NOME_MATERIAL("nomeMaterial"),
		CODIGO_MATERIAL("codigoMaterial"),
		ALMOX("almoxarifado"),
		ALMOX_DESCRICAO("almoxDescricao"),
		UNID("estoqueAlmoxUmdCodigo"),
		ENDE("estoqueAlmoxEndereco"),
		QTDE_DISP("estoqueAlmoxQuantidadeDisponivel"),
		VALIDADE("validade"),
		QTDE_VALD("quantidadeDisponivelValidade"),
		FRN_NUMERO("numeroFornecedor"),
		RAZAO_SOCIAL("razaoSocial"),
		SITUACAO("situacao");
	
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/*Campos do grupo principal (Cabeçalho do relatório)*/
	private String nomeMaterial;
	private Integer codigoGrupo;
	private String gmtDescricao;
	private Integer codigoMaterial;
	private Short almoxarifado;
	private String almoxDescricao;
	private String estoqueAlmoxUmdCodigo;
	private String estoqueAlmoxEndereco;
	private Integer estoqueAlmoxQuantidadeDisponivel;
	private Date validade;
	private Integer quantidadeDisponivelValidade;
	private Integer numeroFornecedor;
	private String razaoSocial;
	private Boolean valido;
	
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public Integer getCodigo() {
		return codigoGrupo;
	}
	public void setCodigo(Integer codigo) {
		this.codigoGrupo = codigo;
	}
	public String getGmtDescricao() {
		return gmtDescricao;
	}
	public void setGmtDescricao(String gmtDescricao) {
		this.gmtDescricao = gmtDescricao;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public Short getAlmoxarifado() {
		return almoxarifado;
	}
	public void setAlmoxerifado(Short almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	public String getAlmoxDescricao() {
		return almoxDescricao;
	}
	public void setAlmoxDescricao(String almoxDescricao) {
		this.almoxDescricao = almoxDescricao;
	}
	public String getEstoqueAlmoxUmdCodigo() {
		return estoqueAlmoxUmdCodigo;
	}
	public void setEstoqueAlmoxUmdCodigo(String estoqueAlmoxUmdCodigo) {
		this.estoqueAlmoxUmdCodigo = estoqueAlmoxUmdCodigo;
	}
	public String getEstoqueAlmoxEndereco() {
		return estoqueAlmoxEndereco;
	}
	public void setEstoqueAlmoxEndereco(String estoqueAlmoxEndereco) {
		this.estoqueAlmoxEndereco = estoqueAlmoxEndereco;
	}
	public Integer getEstoqueAlmoxQuantidadeDisponivel() {
		return estoqueAlmoxQuantidadeDisponivel;
	}
	public void setEstoqueAlmoxQuantidadeDisponivel(
			Integer estoqueAlmoxQuantidadeDisponivel) {
		this.estoqueAlmoxQuantidadeDisponivel = estoqueAlmoxQuantidadeDisponivel;
	}
	public Date getValidade() {
		return validade;
	}
	public void setValidade(Date validade) {
		this.validade = validade;
	}
	public Integer getQuantidadeDisponivelValidade() {
		return quantidadeDisponivelValidade;
	}
	public void setQuantidadeDisponivelValidade(Integer quantidadeDisponivelValidade) {
		this.quantidadeDisponivelValidade = quantidadeDisponivelValidade;
	}
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public Boolean getValido() {
		return valido;
	}
	public void setValido(Boolean valido) {
		this.valido = valido;
	}
}