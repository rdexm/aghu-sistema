package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatRetornaAihDAO;
import br.gov.mec.aghu.model.FatRetornaAih;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatRetornaAihRN extends BaseBusiness {

	private static final long serialVersionUID = -8278118426204933446L;
	
	private static final Log LOG = LogFactory.getLog(FatRetornaAihRN.class);

	@Inject
	private FatRetornaAihDAO dao;
 
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FatRetornaAihRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA
	}
	
	public void persistir(FatRetornaAih fatRetornaAih) throws ApplicationBusinessException { 
		
		if(fatRetornaAih == null){
			throw new ApplicationBusinessException(FatRetornaAihRNExceptionCode.ERRO_PERSISTENCIA);
		}
		
		if (fatRetornaAih.getId() != null) { 
			FatRetornaAih retornaAih = dao.obterOriginal(fatRetornaAih.getId());
			
			if(retornaAih == null) {
				inserir(fatRetornaAih);
				dao.flush();
			}
		}
	}

	private void inserir(FatRetornaAih fatRetornaAih) throws ApplicationBusinessException {
		dao.persistir(fatRetornaAih);
	}
	

}
