package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghHorariosUnidFuncionalId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterHorarioFuncionamentoUnidadeRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterHorarioFuncionamentoUnidadeRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2810162012292221630L;

	public enum ManterHorarioFuncionamentoUnidadeRNExceptionCode implements BusinessExceptionCode {
		ERRO_DATA_INICIAL_MAIOR, REGISTRO_DUPLICADO;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	/**
	 * Cria um novo registro
	 * ORADB AGHT_HRF_BRI
	 * @param horariosUnidFuncional
	 * @throws ApplicationBusinessException  
	 */
	public void inserirHorarioFuncionamentoUnidade(AghHorariosUnidFuncional horariosUnidFuncional) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		validarItemDuplicado(horariosUnidFuncional.getId());
		validarHora(horariosUnidFuncional.getId().getHrInicial(), horariosUnidFuncional.getHrFinal());
		horariosUnidFuncional.setServidor(servidorLogado);
		getAghuFacade().inserirAghHorariosUnidFuncional(horariosUnidFuncional, true);
		
	}
	
	/**
	 * Atualiza o registro
	 * @param horariosUnidFuncional
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarHorarioFuncionamentoUnidade(AghHorariosUnidFuncional horariosUnidFuncional) throws ApplicationBusinessException {

		//validarItemDuplicado(horariosUnidFuncional.getId());
		validarHora(horariosUnidFuncional.getId().getHrInicial(), horariosUnidFuncional.getHrFinal());

		IAghuFacade aghuFacace = getAghuFacade();
		AghHorariosUnidFuncional horRotExc = getAghuFacade().obterHorarioUnidadeFuncionalPorId(
				horariosUnidFuncional.getId().getUnidadeFuncional(), horariosUnidFuncional.getId().getTipoDia(),
				DateFormatUtil.formataHoraMinuto(horariosUnidFuncional.getId().getHrInicial()));
		aghuFacace.removerAghHorariosUnidFuncional(horRotExc.getId());
		aghuFacace.inserirAghHorariosUnidFuncional(horariosUnidFuncional, true);

	}
	
	/**
	 * Remove o registro
	 * @param horariosUnidFuncional
	 */
	public void removerHorarioFuncionamentoUnidade(AghHorariosUnidFuncionalId id) throws ApplicationBusinessException{
		getAghuFacade().removerAghHorariosUnidFuncional(id);
	}
	
	/**
	 * Valida insert e update
	 * @param horariosUnidFuncional
	 * @throws ApplicationBusinessException 
	 */
	private void validarHora(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		
		if (DateUtil.validaHoraMenorIgual(dataFinal, dataInicial)) {
			
			ManterHorarioFuncionamentoUnidadeRNExceptionCode.ERRO_DATA_INICIAL_MAIOR.throwException();
			
		}
		
	}
	
	/**
	 * Valida se o registro está duplicado
	 * @param id
	 * @throws ApplicationBusinessException 
	 */
	private void validarItemDuplicado(AghHorariosUnidFuncionalId id) throws ApplicationBusinessException {
		
		if (getAghuFacade().obterHorarioUnidadeFuncionalPorId(
				id.getUnidadeFuncional(), id.getTipoDia(),
				DateFormatUtil.formataHoraMinuto(id.getHrInicial())) != null) {
			
			ManterHorarioFuncionamentoUnidadeRNExceptionCode.REGISTRO_DUPLICADO.throwException();
			
		}
		
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
