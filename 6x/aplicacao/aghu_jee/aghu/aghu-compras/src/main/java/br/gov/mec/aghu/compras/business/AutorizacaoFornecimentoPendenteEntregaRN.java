package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AutorizacaoFornecimentoPendenteEntregaRN extends BaseBusiness {

	private static final long serialVersionUID = -7448185725170795466L;
	
	public enum AutorizacaoFornecimentoPendenteEntregaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_PROG_ENTREGA_NAO_ENCONTRADO
	}
	
	private static final Log LOG = LogFactory.getLog(AutorizacaoFornecimentoPendenteEntregaRN.class);
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public void gravarParcelaAfPendenteEntrega(ParcelaAfPendenteEntregaVO vo) throws ApplicationBusinessException {
		
		ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId(vo.getNumeroIafAfnNumero(), vo.getNumeroIaf(), vo.getSeq(), vo.getParcela());
		ScoProgEntregaItemAutorizacaoFornecimento item = getAutFornecimentoFacade().obterProgEntregaPorChavePrimaria(id);
		
		if (item == null) {
			throw new ApplicationBusinessException(AutorizacaoFornecimentoPendenteEntregaRNExceptionCode.MENSAGEM_ERRO_PROG_ENTREGA_NAO_ENCONTRADO);
		}
		
		item.setDtPrevEntregaAposAtraso(vo.getDataPrevEntregaAposAtraso());
		item.setObservacao(vo.getObservacao());
		getAutFornecimentoFacade().persistirProgEntregaItemAf(item);
	}

	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
}
