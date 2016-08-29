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
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GerarListaTransferenciaAutoAlmoxarifadoBean extends BaseBusiness implements GerarListaTransferenciaAutoAlmoxarifadoBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2606853487084227686L;
	
	private static final Log LOG = LogFactory.getLog(GerarListaTransferenciaAutoAlmoxarifadoBean.class);

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
		ERRO_AO_TENTAR_INSERIR_TRANSFERENCIA_AUTOMATICA,
		ERRO_AO_TENTAR_REMOVER_TRANSFERENCIA_AUTOMATICA,ERRO_AO_TENTAR_GERAR_LISTA_TRANSFERENCIA_AUTOMATICA,
		ERRO_AO_TENTAR_REMOVER_ITEM_TRANSFERENCIA_AUTOMATICA;
	}


	@Override
	public void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		try {
			this.getEstoqueFacade().persistirTransferenciaAutoAlmoxarifado(transferencia);
			this.getSceNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_INSERIR_TRANSFERENCIA_AUTOMATICA);
		}
	}
	
	@Override
	public void removerTransferenciaAutoAlmoxarifado(Integer seq) throws BaseException {
		try {
			SceTransferencia transferencia = getEstoqueFacade().obterTransferenciaPorSeq(seq);
			this.getEstoqueFacade().removerTransferenciaAutoAlmoxarifado(transferencia);
			this.getSceNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_REMOVER_TRANSFERENCIA_AUTOMATICA);
		}
	}
	
	@Override
	public void removerTransferenciaAutomaticaNaoEfetivadaAlmoxarifadoDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento,Long numeroClassifMatNiv5) throws BaseException {
		try {
			this.getEstoqueFacade().removerTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento,numeroClassifMatNiv5);
			this.getSceNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_REMOVER_TRANSFERENCIA_AUTOMATICA);
		}
	}
	
	@Override
	public void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		try {
			this.getEstoqueFacade().gerarListaTransferenciaAutoAlmoxarifado(transferencia);
			this.getSceNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_GERAR_LISTA_TRANSFERENCIA_AUTOMATICA);
		}
	}
	
	@Override
	public void removerItemTransferenciaAutoAlmoxarifado(final Integer ealSeq, final Integer trfSeq) throws BaseException {
		try {
			final SceItemTransferencia itemTransferencia = getEstoqueFacade().obterItemTransferenciaPorChave(ealSeq, trfSeq);
			this.getEstoqueFacade().removerItemTransferenciaAutoAlmoxarifado(itemTransferencia);
			this.getSceNotaRecebimentoDAO().flush();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(GerarNotaRecebimentoBeanExceptionCode.ERRO_AO_TENTAR_REMOVER_ITEM_TRANSFERENCIA_AUTOMATICA);
		}
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}

}
