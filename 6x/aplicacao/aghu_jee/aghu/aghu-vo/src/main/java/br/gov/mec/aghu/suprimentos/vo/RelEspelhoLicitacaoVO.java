/**
 * 
 */
package br.gov.mec.aghu.suprimentos.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

/**
 * @author bruno.mourao
 *
 */

public class RelEspelhoLicitacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3557207127602524878L;
	private ScoItemLicitacao item;
	private ScoMaterial material;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoSolicitacaoServico solicitacaoServico;
	
	public ScoItemLicitacao getItem() {
		return item;
	}
	public void setItem(ScoItemLicitacao item) {
		this.item = item;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}
	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}
	
	public ScoLicitacao getLicitacao(){
		return null;
//		return item.getScoLicitacao();
	}
	
	/**
	 * Método para retornar o número do documento de licitação.<br>
	 * Se só tiver o ano, retorna só ano, se só tiver o número, retorna só numero, se tiver os dois, retorna numero/ano, se não tiver nada retorna null.
	 * @return
	 * @author bruno.mourao
	 * @since 03/06/2011
	 */
	public String getNumLicit(){
		if(getLicitacao().getNumDocLicit() == null && getLicitacao().getAnoComplemento() == null){
			return null;
		}
		else{
			StringBuffer numLicit = new StringBuffer();
			//Se só tiver ano, retorna só ano, se só tiver numero, retorna soh numero, se tiver os dois, retorna numero/ano
			if(getLicitacao().getNumDocLicit() !=null){
				numLicit.append(getLicitacao().getNumDocLicit());
			}
			if(getLicitacao().getAnoComplemento() != null){
				if(numLicit.length() > 0){
					numLicit.append('/');
				}
				numLicit.append(getLicitacao().getAnoComplemento());
			}			
			return numLicit.toString();
		}
	}
	
	/**
	 * Método para retornar o número do edital.<br>
	 * Se só tiver o ano, retorna só ano, se só tiver o número, retorna só numero, se tiver os dois, retorna numero/ano, se não tiver nada retorna null.
	 * @return
	 * @author bruno.mourao
	 * @since 03/06/2011
	 */
	public String getNumEdital(){
		if(getLicitacao().getNumEdital() == null && getLicitacao().getAnoComplemento() == null){
			return null;
		}
		else{
			StringBuffer numEdital = new StringBuffer();
			//Se só tiver ano, retorna só ano, se só tiver numero, retorna soh numero, se tiver os dois, retorna numero/ano
			if(getLicitacao().getNumEdital() !=null){
				numEdital.append(getLicitacao().getNumEdital());
			}
			if(getLicitacao().getAnoComplemento() != null){
				if(numEdital.length() > 0){
					numEdital.append('/');
				}
				numEdital.append(getLicitacao().getAnoComplemento());
			}
			
			return numEdital.toString();
		}
	}
	
	public Integer getNumero(){
		if(solicitacaoCompra != null){
			return solicitacaoCompra.getNumero();
		}
		else{
			if(solicitacaoServico != null){
				return solicitacaoServico.getNumero();
			}
			else{
				return null;
			}
		}
	}
	
	public BigDecimal getValorUnitario(){
		if(item.getValorUnitario() == null){
			return BigDecimal.ZERO;
		}
		else{
			return item.getValorUnitario();
		}
	}
	
	public Integer getQuantidade(){
//		if(solicitacaoCompra != null){
//			return solicitacaoCompra.getQtdeAprovada().intValue();
//		}
//		else{
//			if(solicitacaoServico != null){
//				return (solicitacaoServico.getQtdeSolicitada() * item.getScoLicitacao().getFrequenciaEntrega());
//			}
//			else{
				return 0;
//			}
//		}
	}
	
	public String getUnidade(){
		if(solicitacaoCompra != null){
			return solicitacaoCompra.getMaterial().getUmdCodigo();
		}
		else{
			return null;
		}
	}
	
	public Integer getCodigo(){
		if(solicitacaoCompra != null){
			return solicitacaoCompra.getMaterial().getCodigo();
		}
		else{
			if(solicitacaoServico != null){
				return solicitacaoServico.getServico().getCodigo();
			}
			else{
				return null;
			}
		}
	}
	
	/**
	 * Obtém a especificação do material.<BR>
	 * Se for uma solicitacao de compra: <BR>
	 * retorna o nome do material + descricao do material (se houver) + descricao da solicitacao (se houver) <br>
	 * Se for uma solicitacao de serviço: <BR>
	 * retorna o nome do serviço + descricao do serviço(se houver) + descricao da solicitacao (se houver)
	 * @return
	 * @author bruno.mourao
	 * @since 03/06/2011
	 */
	public String getEspecificacaoMaterial(){
		StringBuffer especificacao = new StringBuffer();
		if(solicitacaoCompra != null){
			especificacao.append(solicitacaoCompra.getMaterial().getNome());
			if(solicitacaoCompra.getMaterial().getDescricao() != null){
				especificacao.append('\n').append(solicitacaoCompra.getMaterial().getDescricao());
			}
			if(solicitacaoCompra.getDescricao() != null){
				especificacao.append('\n').append(solicitacaoCompra.getDescricao());
			}
		}
		else{
			if(solicitacaoServico != null){
				especificacao.append(solicitacaoServico.getServico().getNome());
				if(solicitacaoServico.getServico().getDescricao() != null){
					especificacao.append('\n').append(solicitacaoServico.getServico().getDescricao());
				}
				if(solicitacaoServico.getDescricao() != null){
					especificacao.append('\n').append(solicitacaoServico.getDescricao());
				}
			}
			else{
				especificacao = null;
			}
		}
		return especificacao != null ? especificacao.toString() : null;
	}
}
