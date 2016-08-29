package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras para finalização de uma prescrição de enfermagem
 * 
 * @ORADB EPEK_CONFIRMA
 * 
 * @author tfelini
 */

@Stateless
public class FinalizacaoPrescricaoEnfermagemRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FinalizacaoPrescricaoEnfermagemRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4555286704276655766L;

	public enum FinalizacaoPrescricaoEnfermagemRNExceptionCode implements BusinessExceptionCode {
		EPE_00281, EPE_00282, EPE_00291;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	/**
	 * atualizar os dados da prescrição de enfermagem após a confirmação o processo é quem chama esta procedure 
	 * 
	 * @ORADB EPEK_CONFIRMA.EPEP_CONFIRMA
	 *  
	 * @param prescricaoEnfermagem
	 * @throws BaseException 
	 */
	public void confirmarPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) throws BaseException{
		Date dthrUsado = new Date();
		if (prescricaoEnfermagem == null){
			FinalizacaoPrescricaoEnfermagemRNExceptionCode.EPE_00281.throwException(); 
		}
		if (!DominioSituacaoPrescricao.U.equals(prescricaoEnfermagem.getSituacao())){
			FinalizacaoPrescricaoEnfermagemRNExceptionCode.EPE_00282.throwException();
		}
		if (prescricaoEnfermagem.getDthrMovimento()==null){
			FinalizacaoPrescricaoEnfermagemRNExceptionCode.EPE_00291.throwException();
		}
		if (prescricaoEnfermagem.getDthrInicioMvtoPendente() == null){
			dthrUsado = prescricaoEnfermagem.getDthrMovimento();
		}else{
			dthrUsado = prescricaoEnfermagem.getDthrInicioMvtoPendente();			
		}		
		confirmarCuidados(prescricaoEnfermagem,dthrUsado);
		confirmarTiraDeUso(prescricaoEnfermagem);
	}
	
	/** Esta procedure libera a prescrição médica após confirmação. 
	 * 
	 * @ORADB EPEK_CONFIRMA.EPEP_CONF_TIRA_USO
	 * 
	 * @param prescricaoEnfermagem
	 * @throws BaseException 
	 */
	private void confirmarTiraDeUso(EpePrescricaoEnfermagem prescricaoEnfermagem) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		EpePrescricaoEnfermagem prescricaoEnfermagemOld = getPrescricaoEnfermagemFacade().clonarPrescricaoEnfermagem(prescricaoEnfermagem);
		if (prescricaoEnfermagem.getServidorValida() == null){
			prescricaoEnfermagem.setServidorValida(servidorLogado);
		}
		prescricaoEnfermagem.setDthrMovimento(null);
		prescricaoEnfermagem.setDthrInicioMvtoPendente(null);
		prescricaoEnfermagem.setSituacao(DominioSituacaoPrescricao.L);
		getPrescricaoEnfermagemFacade().atualizarPrescricao(prescricaoEnfermagemOld, prescricaoEnfermagem, true);
	}
	
	/** Atualiza os dados dos cuidados do paciente após a confirmação esta rotina executa a atualização 
	 * da prescrição de cuidados em uma confirmação de uma prescrição de enfermagem. 
	 * 
	 * @ORADB EPEK_CONFIRMA.EPEP_CONFIRMA_CUID
	 * 
	 * @param prescricaoEnfermagem
	 * @param dthrAtual
	 * @param dthrFim
	 * @param servidor
	 * @throws BaseException 
	 */
	private void confirmarCuidados(EpePrescricaoEnfermagem prescricaoEnfermagem, Date dthrAtual) throws BaseException{
		
		List<EpePrescricoesCuidados> prescricaoCuidados = getEpePrescricoesCuidadosDAO()
				.pesquisarCuidadosPendentes(prescricaoEnfermagem.getId().getAtdSeq(), prescricaoEnfermagem.getId().getSeq(), dthrAtual, prescricaoEnfermagem.getDthrFim());
		
		for (EpePrescricoesCuidados cuidado : prescricaoCuidados){
			getEpePrescricoesCuidadosDAO().refresh(cuidado);
			if (DominioIndPendentePrescricoesCuidados.E.equals(cuidado.getPendente())){
				confirmarExclusaoPendente(cuidado, dthrAtual);
			}else if (DominioIndPendentePrescricoesCuidados.P.equals(cuidado.getPendente())){
				confirmarPendente(cuidado, dthrAtual);
			}
		}
	}
	
	private void confirmarExclusaoPendente(EpePrescricoesCuidados prescricaoCuidado, Date dthrAtual) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (prescricaoCuidado.getPrescricaoCuidado()==null){
			prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.N);
			prescricaoCuidado.setServidorMvtoValida(servidorLogado);
			prescricaoCuidado.setDthrValidaMovimento(new Date());
		}else{
			Boolean validar = false;
			validar = validarExclusaoCuidadoAnterior(prescricaoCuidado.getPrescricaoCuidado(), dthrAtual, validar);
			if (validar){
				prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.N);
				prescricaoCuidado.setServidorMvtoValida(servidorLogado);
				prescricaoCuidado.setDthrValidaMovimento(new Date());
			}
		}
		this.getEpePrescricoesCuidadosDAO().merge(prescricaoCuidado);
		this.getEpePrescricoesCuidadosDAO().flush();
	}
	
	private Boolean validarExclusaoCuidadoAnterior(EpePrescricoesCuidados prescricaoCuidado, Date dthrAtual, Boolean validar){
		if (dthrAtual.after(prescricaoCuidado.getCriadoEm())){
			validar = true;
		}else{
			if (prescricaoCuidado.getPrescricaoCuidado() == null){
				validar = false;
			}else{
				validarExclusaoCuidadoAnterior(prescricaoCuidado.getPrescricaoCuidado(),dthrAtual, validar);
			}
		}		
		return validar;
	}
	
	private void confirmarPendente(EpePrescricoesCuidados prescricaoCuidado, Date dthrAtual) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		prescricaoCuidado.setServidorValida(servidorLogado);
		prescricaoCuidado.setDthrValida(new Date());
		if (prescricaoCuidado.getPrescricaoCuidado()==null){
			prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.N);
			prescricaoCuidado.setServidorMvtoValida(servidorLogado);
			prescricaoCuidado.setDthrValidaMovimento(new Date());
		}else{
			Boolean validar = false;
			validar = validarPendenteCuidadoAnterior(prescricaoCuidado, dthrAtual, validar);
			if (validar){
				prescricaoCuidado.setServidorMvtoValida(servidorLogado);
				prescricaoCuidado.setDthrValidaMovimento(new Date());
			}
		}
		prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.N);
		this.getEpePrescricoesCuidadosDAO().merge(prescricaoCuidado);
		this.getEpePrescricoesCuidadosDAO().flush();
	}
	
	private Boolean validarPendenteCuidadoAnterior(EpePrescricoesCuidados prescricaoCuidado, Date dthrAtual, Boolean validar){
		if (dthrAtual.after(prescricaoCuidado.getCriadoEm()) || DominioIndPendentePrescricoesCuidados.A.equals(prescricaoCuidado.getPendente())){
			validar = true;
		}else{
			if (prescricaoCuidado.getPrescricaoCuidado() == null){
				validar = false;
			}else{
				validarPendenteCuidadoAnterior(prescricaoCuidado.getPrescricaoCuidado(), dthrAtual, validar);
			}
		}		
		return validar;
	}
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade(){
		return prescricaoEnfermagemFacade;
	}	
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO(){
		return epePrescricoesCuidadosDAO; 
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
