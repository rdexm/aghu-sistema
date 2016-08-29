/**
 * 
 */
package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author bruno.mourao
 *
 */

public class RelatorioEspelhoPACVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1074815299232413315L;


	public enum Fields{
		NUMERO_LICITACAO("numeroLicitacao"),
		NUMERO_DOCUMENTO_LICITACAO("numeroDocumentoLicitacao"),
		NUMERO_EDITAL("numeroEdital"),
		NUMERO_ITEM_LICITACAO("numeroItemLicitacao"),
		ANO_COMPLEMENTO("anoComplemento"),		
		QUANTIDADE_APROVADA("quantidadeAprovada"),
		CODIGO_UNIDADE("codigoUnidade"),
		CODIGO_MATERIAL_SERVICO("codigoMaterialServico"),
		NUMERO_SOLICITACAO("numeroSolicitacao"),
		NOME_MATERIAL_SERVICO("nomeMaterialServico"),
		DESCRICAO_MATERIAL_SERVICO("descricaoMaterialServico"),
		DESCRICAO_SOLICITACAO("descricaoSolicitacao"),
		VALOR_UNITARIO("valorUnitario"),
		DESCRICAO_MODALIDADE_LIC("descricaoModalidadeLicitacao"),
		NUMERO_LOTE("numeroLote");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/*Campos da solicitação (Cabeçalho do relatório)*/
	private Integer numeroLicitacao;//
	private Integer numeroDocumentoLicitacao;//
	private Integer numeroEdital;//
	private Integer anoComplemento;//
	
	/*Campos referentes a cada linha do relatório*/
	private Short numeroItemLicitacao;//
	private Long quantidadeAprovada;//
	private String codigoUnidade;//
	private Integer numeroSolicitacao;//
	private Integer codigoMaterialServico;//
	private String nomeMaterialServico;//
	private String descricaoMaterialServico;//
	private String descricaoSolicitacao;//
	private BigDecimal valorUnitario;//
	
	private String descricaoModalidadeLicitacao;
	private Short numeroLote;
	
	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}
	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}
	public Integer getNumeroDocumentoLicitacao() {
		return numeroDocumentoLicitacao;
	}
	public void setNumeroDocumentoLicitacao(Integer numeroDocumentoLicitacao) {
		this.numeroDocumentoLicitacao = numeroDocumentoLicitacao;
	}
	public Integer getNumeroEdital() {
		return numeroEdital;
	}
	public void setNumeroEdital(Integer numeroEdital) {
		this.numeroEdital = numeroEdital;
	}
	public Integer getAnoComplemento() {
		return anoComplemento;
	}
	public void setAnoComplemento(Integer anoComplemento) {
		this.anoComplemento = anoComplemento;
	}	
	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}
	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}
	public Long getQuantidadeAprovada() {
		if(quantidadeAprovada == null)
		{
			return 0L;
		}
		return quantidadeAprovada;
	}
	public void setQuantidadeAprovada(Long quantidadeAprovada) {
		this.quantidadeAprovada = quantidadeAprovada;
	}
	public String getCodigoUnidade() {
		return codigoUnidade;
	}
	public void setCodigoUnidade(String codigoUnidade) {
		this.codigoUnidade = codigoUnidade;
	}
	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}
	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}
	public Integer getCodigoMaterialServico() {
		return codigoMaterialServico;
	}
	public void setCodigoMaterialServico(Integer codigoMaterialServico) {
		this.codigoMaterialServico = codigoMaterialServico;
	}
	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}
	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}
	public String getDescricaoMaterialServico() {
		return descricaoMaterialServico;
	}
	public void setDescricaoMaterialServico(String descricaoMaterialServico) {
		this.descricaoMaterialServico = descricaoMaterialServico;
	}
	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}
	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}
	public BigDecimal getValorUnitario() {
		if (valorUnitario == null){
			return BigDecimal.ZERO;
		}
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	
	/**
	 * Método para retornar o número do documento de licitação.<br>
	 * Se só tiver o ano, retorna só ano, se só tiver o número, retorna só numero, 
	 * se tiver os dois, retorna numero/ano, se não tiver nada retorna null.
	 * @return Número da licitação formatado
	 * @author bruno.mourao, luis.braga
	 * @since 03/06/2011
	 */
	public String getNumeroDocumentoLicitacaoFormatado(){
		if(getNumeroDocumentoLicitacao() == null && getAnoComplemento() == null){
			return null;
		}
		else{
			StringBuffer numLicit = new StringBuffer();
			//Se só tiver ano, retorna só ano, se só tiver numero, retorna soh numero, se tiver os dois, retorna numero/ano
			if(getAnoComplemento() != null){
				numLicit.append(getAnoComplemento());
			}
			if(getNumeroDocumentoLicitacao() !=null){
				if(numLicit.length() == 0){
					numLicit.append(getNumeroDocumentoLicitacao());
				}
				else{ 
					numLicit.append('/').append(getNumeroDocumentoLicitacao());
				}
			}
			return numLicit.toString();
		}
	}
	
	/**
	 * Método para retornar o número do edital.<br>
	 * Se só tiver o ano, retorna só ano, se só tiver o número, retorna só numero, se tiver os dois, 
	 * retorna numero/ano, se não tiver nada retorna null.
	 * @return
	 * @author bruno.mourao
	 * @since 03/06/2011
	 */
	public String getNumeroEditalFormatado(){
		if(getNumeroEdital() == null && getAnoComplemento() == null){
			return null;
		}
		else{
			StringBuffer numEdital = new StringBuffer();
			//Se só tiver ano, retorna só ano, se só tiver numero, retorna soh numero, se tiver os dois, retorna numero/ano
			if(getAnoComplemento() != null){
				numEdital.append(getAnoComplemento());
			}
			if(getNumeroEdital() !=null){
				if(numEdital.length() == 0){
					numEdital.append(getNumeroEdital());
				}
				else{
					numEdital.append('/').append(getNumeroEdital());
				}
			}
			return numEdital.toString();
		}
	}
	
	
	/**
	 * Obtém a especificação do material.<BR>
	 * Se for uma solicitacao de compra: <BR>
	 * 		retorna o nome do material + descricao do material (se houver) + 
	 * 		descricao da solicitacao (se houver) <br>
	 * Se for uma solicitacao de serviço: <BR>
	 * 		retorna o nome do serviço + descricao do serviço(se houver) + 
	 * 		descricao da solicitacao (se houver)
	 * @return
	 * @author bruno.mourao
	 * @since 03/06/2011
	 */
	public String getEspecificacaoMaterial(){
		StringBuffer especificacao = new StringBuffer();
		if(getNomeMaterialServico() != null)
		{
			especificacao.append(getNomeMaterialServico()).append('\n');
		}
		if(getDescricaoMaterialServico() != null)
		{
			especificacao.append(getDescricaoMaterialServico()).append('\n');
		}
		if(getDescricaoSolicitacao() != null)
		{
			especificacao.append(getDescricaoSolicitacao());
		}
		return especificacao.toString();
	}
	public String getDescricaoModalidadeLicitacao() {
		return descricaoModalidadeLicitacao;
	}
	public void setDescricaoModalidadeLicitacao(String descricaoModalidadeLicitacao) {
		this.descricaoModalidadeLicitacao = descricaoModalidadeLicitacao;
	}
	public Short getNumeroLote() {
		return numeroLote;
	}
	public void setNumeroLote(Short numeroLote) {
		this.numeroLote = numeroLote;
	}
}
