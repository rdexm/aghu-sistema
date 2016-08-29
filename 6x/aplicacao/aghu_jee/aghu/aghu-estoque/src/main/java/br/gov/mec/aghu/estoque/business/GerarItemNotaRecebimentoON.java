package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.ItemNotaRecebimentoVO;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class GerarItemNotaRecebimentoON  extends BaseBusiness{

@EJB
private GerarSolicitacaoCompraAutomaticaRN gerarSolicitacaoCompraAutomaticaRN;
@EJB
private SceItemNotaRecebimentoRN sceItemNotaRecebimentoRN;

private static final Log LOG = LogFactory.getLog(GerarItemNotaRecebimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;

@EJB
private IComprasFacade comprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1848164494672131043L;
	
	/**
	 * Gera uma Item de Nota de Recebimento
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	public void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador, boolean flush)
			throws BaseException {
		this.getSceItemNotaRecebimentoRN().inserir(itemNotaRecebimento, nomeMicrocomputador, flush);
	}
	
	/**
	 * Gera uma Item de Nota de Recebimento com Solicitação de Compra Automática
	 * @param itemNotaRecebimento
	 * @param marcaComercial
	 * @param fatorConversao
	 * @throws BaseException
	 */
	public void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento, Short numeroItem, ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException{
		// Gera uma Solicitação de Compra Automática através de um Item de Nota de Recebimento
		this.getGerarSolicitacaoCompraAutomaticaRN().gerarSolicitacaoCompraAutomaticaItemNotaRecebimento(itemNotaRecebimento, numeroItem, marcaComercial, fatorConversao);
		this.getSceItemNotaRecebimentoRN().inserirComSolicitacaoDeCompraAutomatica(itemNotaRecebimento, nomeMicrocomputador);
	}
	
	/**
	 * 
	 * @param seqNotaRecebimento
	 * @return
	 */
	public List<ItemNotaRecebimentoVO> pesquisarItensNotaRecebimento(Integer seqNotaRecebimento) {
		List<ItemNotaRecebimentoVO> lista = getSceNotaRecebimentoDAO().pesquisarItensNotaRecebimento(seqNotaRecebimento);		
		if(lista != null) {
			Integer codigoMarca = null;
			for(ItemNotaRecebimentoVO item : lista) {
				if(item.getCodigoMarca() != null) {
					codigoMarca = item.getCodigoMarca();
					ScoMarcaComercial marcaComercial = getComprasFacade().obterScoMarcaComercialPorChavePrimaria(codigoMarca);
					if(marcaComercial != null) {
						item.setNomeMarcaComercial(marcaComercial.getDescricao());
					}
				}
			}
		}
		return lista;
	}
	
	/**
	 * Getters para RNs e DAOs
	 */
	
	protected SceItemNotaRecebimentoRN getSceItemNotaRecebimentoRN(){
		return sceItemNotaRecebimentoRN;
	}	
	
	protected GerarSolicitacaoCompraAutomaticaRN getGerarSolicitacaoCompraAutomaticaRN(){
		return gerarSolicitacaoCompraAutomaticaRN;
	}
	
	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO(){
		return sceNotaRecebimentoDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

}
