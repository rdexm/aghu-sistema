package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmCidUnidFuncional;
import br.gov.mec.aghu.model.MpmCidUnidFuncionalId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidUnidFuncionalDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCidUsualPorUnidadeRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ManterCidUsualPorUnidadeRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmCidUnidFuncionalDAO mpmCidUnidFuncionalDAO;
	
	public enum ManterCidUsualPorUnidadeRNExceptionCode implements BusinessExceptionCode {
		CID_POR_UNIDADE_DUPLICADA;

	}
	
	private static final long serialVersionUID = 5537443901395824615L;

	protected MpmCidUnidFuncionalDAO getCidUnidFuncionalDAO() {
		return mpmCidUnidFuncionalDAO;
	}

	public void persistirCidUnidadeFuncional(MpmCidUnidFuncional mpmCidUnidFuncional) throws ApplicationBusinessException {
		if(getCidUnidFuncionalDAO().verificaCidUnidadeFuncional(mpmCidUnidFuncional)){
			throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.CID_POR_UNIDADE_DUPLICADA);				
		}else{
			prePersistirCidUnidadeFuncional(mpmCidUnidFuncional);
			getCidUnidFuncionalDAO().persistir(mpmCidUnidFuncional);
			getCidUnidFuncionalDAO().flush();
			
		}
	}

	public void prePersistirCidUnidadeFuncional(MpmCidUnidFuncional mpmCidUnidFuncional) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		mpmCidUnidFuncional.setServidor(servidorLogado);		
	}
	
	public void remover(MpmCidUnidFuncionalId id) {
		MpmCidUnidFuncional mpmCidUnidFuncional = getCidUnidFuncionalDAO().obterPorChavePrimaria(id);
		getCidUnidFuncionalDAO().remover(mpmCidUnidFuncional);
		getCidUnidFuncionalDAO().flush();
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
