package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.ScoMarcaComercial;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class GerarItemNotaRecebimentoBean extends BaseBusiness implements GerarItemNotaRecebimentoBeanLocal {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7434364127744679344L;
	
	private static final Log LOG = LogFactory.getLog(GerarItemNotaRecebimentoBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;
	
	@Resource
	protected SessionContext ctx;
	
	public enum GerarItemNotaRecebimentoBeanCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_GERAR_ITEM_NOTA_RECEBIMENTO;
	}
	
	@Override
	public void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException {
		
		try {
			this.getEstoqueFacade().gerarItemNotaRecebimento(itemNotaRecebimento, nomeMicrocomputador, false);
			this.getSceItemNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarItemNotaRecebimentoBeanCode.ERRO_AO_TENTAR_GERAR_ITEM_NOTA_RECEBIMENTO);
		}
		
	}
	
	@Override
	public void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento,  Short numeroItem, ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException {
		
		try {
			this.getEstoqueFacade().gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(itemNotaRecebimento, numeroItem, marcaComercial, fatorConversao, nomeMicrocomputador);
			this.getSceItemNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarItemNotaRecebimentoBeanCode.ERRO_AO_TENTAR_GERAR_ITEM_NOTA_RECEBIMENTO);
		}
		
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO() {
		return sceItemNotaRecebimentoDAO;
	}

}
