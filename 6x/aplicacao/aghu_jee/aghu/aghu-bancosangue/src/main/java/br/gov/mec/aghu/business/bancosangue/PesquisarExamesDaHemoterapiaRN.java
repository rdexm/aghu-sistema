package br.gov.mec.aghu.business.bancosangue;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsExameComponenteVisualPrescricaoDAO;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisarExamesDaHemoterapiaRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(PesquisarExamesDaHemoterapiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsExameComponenteVisualPrescricaoDAO absExameComponenteVisualPrescricaoDAO;
	
	private static final long serialVersionUID = 5537443901395824615L;
	
	public AbsExameComponenteVisualPrescricaoDAO getAbsExameComponenteVisualPrescricaoDAO(){
		return absExameComponenteVisualPrescricaoDAO;
	}	
	
	public enum ManterCidUsualPorUnidadeRNExceptionCode implements BusinessExceptionCode {
		EXAME_DA_HEMOTERAPIA_DUPLICADA, ABS_ECV_CK2,ABS_ECV_CK3,ABS_ECV_CK6, ABS_ECV_CK4,ABS_ECV_CK5;
	}

	public void persistirExamesDaHemoterapia(AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		prePersistir(absExameComponenteVisualPrescricao);
		if(getAbsExameComponenteVisualPrescricaoDAO().verificaAbsExameComponenteVisualPrescricaoDuplicado(absExameComponenteVisualPrescricao)){
			throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.EXAME_DA_HEMOTERAPIA_DUPLICADA);				
		}else{
			//absExameComponenteVisualPrescricao.setSeq(15);
			absExameComponenteVisualPrescricao.setServidor(servidorLogado);
			absExameComponenteVisualPrescricao.setCriadoEm(new Date());
			getAbsExameComponenteVisualPrescricaoDAO().persistir(absExameComponenteVisualPrescricao);
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void prePersistir(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException {
		if((absExameComponenteVisualPrescricao.getValorMinimo() != null && absExameComponenteVisualPrescricao.getValorMaximo() == null) ||
				(absExameComponenteVisualPrescricao.getValorMinimo() == null && absExameComponenteVisualPrescricao.getValorMaximo() != null)){
			throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.ABS_ECV_CK2);	
		}
		
		if((absExameComponenteVisualPrescricao.getIdadeMinima() != null && absExameComponenteVisualPrescricao.getIdadeMaxima() == null) ||
				(absExameComponenteVisualPrescricao.getIdadeMinima() == null && absExameComponenteVisualPrescricao.getIdadeMinima() != null)){
			throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.ABS_ECV_CK3);	
		}
		
		if(absExameComponenteVisualPrescricao.getValorMinimo() != null && absExameComponenteVisualPrescricao.getValorMaximo() != null){
			if(absExameComponenteVisualPrescricao.getValorMinimo() > absExameComponenteVisualPrescricao.getValorMaximo()){
				throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.ABS_ECV_CK6);	
			}
		}
		
		if(absExameComponenteVisualPrescricao.getIdadeMinima() != null && absExameComponenteVisualPrescricao.getIdadeMaxima() != null){
			if(absExameComponenteVisualPrescricao.getIdadeMinima() > absExameComponenteVisualPrescricao.getIdadeMaxima()){
				throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.ABS_ECV_CK4);	
			}
		}
		
		if( ((absExameComponenteVisualPrescricao.getValorMinimo() != null && absExameComponenteVisualPrescricao.getValorMaximo() != null) &&
				absExameComponenteVisualPrescricao.getQuantidadeCasasDecimais() == null) ||
				((absExameComponenteVisualPrescricao.getValorMinimo() == null && absExameComponenteVisualPrescricao.getValorMaximo() == null) &&
						absExameComponenteVisualPrescricao.getQuantidadeCasasDecimais() != null)){
			throw new ApplicationBusinessException(ManterCidUsualPorUnidadeRNExceptionCode.ABS_ECV_CK5);	
		}
		
	}

	public void remover(Integer seqExameComponenteVisualPrescricao) {
		AbsExameComponenteVisualPrescricao exame = absExameComponenteVisualPrescricaoDAO.obterPorChavePrimaria(seqExameComponenteVisualPrescricao);
		absExameComponenteVisualPrescricaoDAO.remover(exame);
	}

	public void atualizarExamesDaHemoterapia(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		prePersistir(absExameComponenteVisualPrescricao);
		absExameComponenteVisualPrescricao.setServidorAlterado(servidorLogado);
		absExameComponenteVisualPrescricao.setAlteradoEm(new Date());
		getAbsExameComponenteVisualPrescricaoDAO().merge(absExameComponenteVisualPrescricao);
		
		
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
