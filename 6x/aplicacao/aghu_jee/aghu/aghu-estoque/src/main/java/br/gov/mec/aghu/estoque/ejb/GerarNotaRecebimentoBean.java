package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class GerarNotaRecebimentoBean extends BaseBusiness implements GerarNotaRecebimentoBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7434364127744679344L;
	
	private static final Log LOG = LogFactory.getLog(GerarNotaRecebimentoBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;
	
	@Resource
	protected SessionContext ctx;
	
	public enum GerarNotaRecebimentoBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_GERAR_NOTA_RECEBIMENTO, ERRO_AO_TENTAR_ATUALIZAR_NOTA_RECEBIMENTO;
	}
	
	
	@Override
	public void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		
		try {
			
			this.getEstoqueFacade().gerarNotaRecebimento(notaRecebimento, nomeMicrocomputador);
			this.getSceNotaRecebimentoDAO().flush();
			
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_GERAR_NOTA_RECEBIMENTO);
		}
		
	}
	
	@Override
	public void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		
		try {
			this.getEstoqueFacade().gerarNotaRecebimentoSolicitacaoCompraAutomatica(notaRecebimento, nomeMicrocomputador);
			this.getSceNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_GERAR_NOTA_RECEBIMENTO);
		}
		
	}
	
	@Override
	public void atualizarImpressaoNotaRecebimento(final Integer seq,  String nomeMicrocomputador) throws BaseException {
		
		try {
			 SceNotaRecebimento notaRecebimentoOriginal = this.getSceNotaRecebimentoDAO().obterOriginal(seq);
			 
			 SceNotaRecebimento notaRecebimento = this.getSceNotaRecebimentoDAO().obterPorChavePrimaria(seq);
			 notaRecebimento.setIndImpresso(Boolean.TRUE);
			
			this.getEstoqueFacade().atualizarNotaRecebimento(notaRecebimento, notaRecebimentoOriginal, nomeMicrocomputador, true);
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_ATUALIZAR_NOTA_RECEBIMENTO);
		}
		
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}

}
