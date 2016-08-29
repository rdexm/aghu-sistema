package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class OcorrenciaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(OcorrenciaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AfaTipoOcorDispensacaoDAO afaTipoOcorDispensacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4775609288176934564L;

	public enum OcorrenciaRNExceptionCode implements BusinessExceptionCode {
		AFA_00169, MPM_00774, AFA_00172;
	}
	
	
	/**
	 * @ORADB trigger AFAT_TOD_BRI
	 * @param ocorrencia
	 * @throws ApplicationBusinessException 
	 */
	public void prePersistirOcorrencia(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException{
		//Seta valores automáticos
		this.verificarAtribuirCriadoEm(ocorrencia);
		this.verificarAtribuirServidor(ocorrencia);
	}
	
	protected void verificarAtribuirCriadoEm(AfaTipoOcorDispensacao ocorrencia){
		if(ocorrencia != null && ocorrencia.getCriadoEm() == null) {
			ocorrencia.setCriadoEm(new Date());
		}
	}
	
	protected void verificarAtribuirServidor(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException{
		if(ocorrencia != null && ocorrencia.getServidor() == null){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			ocorrencia.setServidor(servidorLogado);
			if (ocorrencia.getServidor() == null){
				throw new ApplicationBusinessException(OcorrenciaRNExceptionCode.AFA_00169);
			}
		}
	}
	
	/**
	 * @ORADB trigger AFAT_TOD_BRU
	 * @param ocorrencia
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateOcorrencia(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException {
		this.verificarAtribuirServidor(ocorrencia);
		this.validarDescricaoAlterada(ocorrencia);
	}
	
	
	/**
	 * @ORADB Trigger AFAT_TOD_BRD
	 * @param ocorrencia
	 *  
	 * @throws ApplicationBusinessException 
	 */
	public void preDeleteOcorrencia(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException {
		IParametroFacade parametroFacade = getParametroFacade();
		AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_DIAS_PERM_DEL_AFA;
		AghParametros parametroDias = parametroFacade.buscarAghParametro(parametroEnum);
		//Calcula a diferença em dias
		verificarDiferencaDias(parametroDias, ocorrencia.getCriadoEm());
		
	}
	
	/**
	 * @ORADB procedure AFAK_RN.RN_AFAP_VER_DEL
	 * @param parametroDias
	 * @param criadoEm
	 * @throws ApplicationBusinessException
	 */
	protected void verificarDiferencaDias(AghParametros parametroDias, Date criadoEm) throws ApplicationBusinessException{
		Float diferenca = CoreUtil.diferencaEntreDatasEmDias(Calendar.getInstance().getTime(), criadoEm);
		if (diferenca > parametroDias.getVlrNumerico().floatValue()) {
			throw new ApplicationBusinessException(OcorrenciaRNExceptionCode.AFA_00172);
		}
	}

	
	/**
	 * Verifica se a descrição da ocorrência foi alterada
	 * @ORADB procedure afak_tod_rn.rn_todp_ver_altera
	 * @param ocorrencia
	 * @throws ApplicationBusinessException 
	 */
	protected void validarDescricaoAlterada(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException {
		String descricaoAnterior = getAfaTipoOcorDispensacaoDAO().obterDescricaoAnterior(ocorrencia.getSeq());
		
		if (!descricaoAnterior.equals(ocorrencia.getDescricao())){
			//A descrição não deve ser alterada
			throw new ApplicationBusinessException(OcorrenciaRNExceptionCode.MPM_00774);
		}
		
	}
	
	protected AfaTipoOcorDispensacaoDAO getAfaTipoOcorDispensacaoDAO() {
		return afaTipoOcorDispensacaoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}