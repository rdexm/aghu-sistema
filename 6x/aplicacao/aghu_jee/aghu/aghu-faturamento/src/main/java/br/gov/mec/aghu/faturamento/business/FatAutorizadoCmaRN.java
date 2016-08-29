package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatAutorizadoCmaDAO;
import br.gov.mec.aghu.model.FatAutorizadoCma;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatAutorizadoCmaRN extends BaseBusiness {

	private static final long serialVersionUID = 233664863654555180L;
	private static final Log LOG = LogFactory.getLog(FatAutorizadoCmaRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private FatAutorizadoCmaDAO dao;
	
	private enum FatAutorizadoCmaRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA
	}
	
	public void persistir(FatAutorizadoCma entity) throws ApplicationBusinessException { 
		
		if(entity == null){
			throw new ApplicationBusinessException(FatAutorizadoCmaRNExceptionCode.ERRO_PERSISTENCIA);
		}
		
		if (entity.getId() != null) { 
			FatAutorizadoCma autorizadoCma = dao.obterOriginal(entity.getId());
			
			if(autorizadoCma == null) {
				inserir(entity);
			} else {
				update(entity);
			}
			
		} else {
			throw new ApplicationBusinessException(FatAutorizadoCmaRNExceptionCode.ERRO_PERSISTENCIA);
		}
	}

	private void update(FatAutorizadoCma entity) throws ApplicationBusinessException {
		FatAutorizadoCma entidadeAtualizada = dao.obterPorChavePrimaria(entity.getId());
		entidadeAtualizada.setQtdProc(entity.getQtdProc());
		entidadeAtualizada.setCriadoPor(obterLoginUsuarioLogado());
		dao.atualizar(entidadeAtualizada);
	}

	/**
	 * @ORADB TRIGGER AGH.FATT_FCMA_BRI
	 * @param entity
	 * @throws ApplicationBusinessException
	 */
	private void inserir(FatAutorizadoCma entity) throws ApplicationBusinessException {
		entity.setCriadoEm(new Date());
		entity.setCriadoPor(obterLoginUsuarioLogado());	  
		dao.persistir(entity);
	}

}
