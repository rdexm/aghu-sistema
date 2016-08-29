package br.gov.mec.aghu.estoque.ejb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class AtualizaTransferenciaAutoAlmoxarifadoBean extends BaseBusiness implements AtualizaTransferenciaAutoAlmoxarifadoBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5506145209111954031L;
	
	private static final Log LOG = LogFactory.getLog(AtualizaTransferenciaAutoAlmoxarifadoBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Resource
	protected SessionContext ctx;
	
	public enum EfetivarRequisicaoMaterialBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_EFETIVAR_TRS_ALMOXARIFADO;
	}

	@Override
	public void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO>  listaItemTransferenciaAutomaticaVO, String nomeMicrocomputador) throws BaseException {
		
		try {
		
			getEstoqueFacade().atualizarItensTransfAutoAlmoxarifados(listaItemTransferenciaAutomaticaVO, nomeMicrocomputador);
		
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(EfetivarRequisicaoMaterialBeanExceptionCode.ERRO_AO_TENTAR_EFETIVAR_TRS_ALMOXARIFADO);
		}
	
	}
	
	@Override
	public void efetivarTransferenciaAutoAlmoxarifado(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException {
		
		try {
		
			transferencia.setEfetivada(Boolean.TRUE);
			getEstoqueFacade().atualizarTransferenciaAutoAlmoxarifado(transferencia, nomeMicrocomputador);
		
		} catch (BaseException e) {
			transferencia.setEfetivada(Boolean.FALSE);
			transferencia.setServidorEfetivado(null);
			transferencia.setDtEfetivacao(null);
			
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(EfetivarRequisicaoMaterialBeanExceptionCode.ERRO_AO_TENTAR_EFETIVAR_TRS_ALMOXARIFADO);
		}
	
	}
	
	@Override
	public void estornarTransferenciaAutoAlmoxarifado(Integer seqTransferencia, String nomeMicrocomputador) throws BaseException {
		
		try {
			SceTransferencia  transferencia = getEstoqueFacade().obterTransferenciaPorId(seqTransferencia);
			SceTransferencia  oldTransferencia = getEstoqueFacade().obterTransferenciaOriginal(seqTransferencia);
			
			transferencia.setEstorno(Boolean.TRUE);
			getEstoqueFacade().atualizarTransferenciaAutoAlmoxarifado(transferencia, oldTransferencia, nomeMicrocomputador);
		
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			throw e;
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(EfetivarRequisicaoMaterialBeanExceptionCode.ERRO_AO_TENTAR_EFETIVAR_TRS_ALMOXARIFADO);
		}
	
	}
	
	@Override
	public void atualizarTransferenciaAutoAlmoxarifado(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException {
		
		try {
		
			getEstoqueFacade().atualizarTransferenciaAutoAlmoxarifado(transferencia, nomeMicrocomputador);
		
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(EfetivarRequisicaoMaterialBeanExceptionCode.ERRO_AO_TENTAR_EFETIVAR_TRS_ALMOXARIFADO);
		}
	
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
}