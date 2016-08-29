package br.gov.mec.aghu.compras.autfornecimento.action;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * Controller responsável por exibir quantidade de itens de nota de recebimento
 * provisório.
 */


public class QtdeItemNotaRecebProvisorioController extends ActionController {

	private static final long serialVersionUID = 5808750925638993664L;
	
	/**
	 * Cor usada para indicar a existência de uma ou mais notas de recebimento
	 * provisório.
	 */
	private static final String COR_DESTAQUE = "FFE773";

	@EJB
	private IComprasFacade comprasFacade;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	/**
	 * Obtem notas de recebimento de um item de AF.
	 * 
	 * @param itemAf Item de AF
	 * @return Notas de Recebimento
	 */
	public String getRps(QtdeRpVO qtdeRp) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		for (QtdeRpVO.NotaRecebimentoProvisorio rp : qtdeRp.getNotasRecebimento()) {
			i ++;
			String key = getBundle().getString("TOOLTIP_PESQ_GRID_ITENS_AUT_RP");
			String data = DateUtil.dataToString(rp.getDataGeracao(), "dd/MM/yyyy");			
			String msg = MessageFormat.format(key, rp.getId().toString(), data);
			
			sb.append(msg);
			
			if (rp.getDocumentoFiscalEntrada() != null){
				String nf = getBundle().getString("TOOLTIP_PESQ_GRID_ITENS_RP_NF");
				String documentoFiscalEntrada = rp.getDocumentoFiscalEntrada().getNumero().toString();
				
				String razaoSocial = "";
				ScoFornecedor forn = comprasFacade.obterFornecedorPorChavePrimaria(rp.getFornecedor().getNumero());
				if (forn != null) {
					Integer tamanhoRazaoSocial = forn.getRazaoSocial().length();
					if (tamanhoRazaoSocial <= 10){
						razaoSocial = forn.getRazaoSocial().subSequence(0, tamanhoRazaoSocial).toString();
					} else {
						razaoSocial = forn.getRazaoSocial().subSequence(0, 10).toString();
					}
				}
				sb.append(' ').append(nf).append(' ').append(documentoFiscalEntrada);
				sb.append(' ').append(razaoSocial);
			}
			
			if (rp.getIndEstorno() != null && rp.getIndEstorno()) {
				sb.append(" (Estornado)").append("<br />");
			} else {
				sb.append("<br />");
			}
			
			if (i == QtdeRpVO.MAX_RPS) {
				break;
			}
		}
		
		if (qtdeRp.getNotasRecebimento().size() > QtdeRpVO.MAX_RPS) {
			sb.append("...");
		}
		
		return sb.toString();
	}
	
	/** Obtem cor destaque. */
	public String getCorDestaque() {
		return COR_DESTAQUE;
	}
}
