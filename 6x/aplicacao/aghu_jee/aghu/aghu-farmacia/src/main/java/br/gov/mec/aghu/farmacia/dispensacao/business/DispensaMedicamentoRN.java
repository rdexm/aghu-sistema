package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DispensaMedicamentoRN extends BaseBusiness implements Serializable {

	private static final Log LOG = LogFactory.getLog(DispensaMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6501153127036910304L;

	public enum DispensaMedicamentoRNExceptionCode implements
	BusinessExceptionCode {
		MPM_01708, MPM_01709, MPM_01710;
	}
	
	/**
	 * @ORADB MPMC_GET_LCAL_DISP_2 
	 *  
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public Short getLocalDispensa2(Integer atendimentoSeq, Integer medMatCodigo, BigDecimal dose, Integer fdsSeq, Short farmPadrao) throws ApplicationBusinessException{
		AghUnidadesFuncionais unidadeFuncional = null;
		Short unfSeqDisp = null;
		LocalDispensaVO localDispensaVO = this.getPrescricaoMedicaFacade().obterLocalDispensaVO(medMatCodigo, atendimentoSeq);
		if(localDispensaVO==null){
			AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNF_FARM_DISP);
			farmPadrao = (param != null && param.getVlrNumerico() != null)?param.getVlrNumerico().shortValue():null;
			if(farmPadrao == null){
				throw new ApplicationBusinessException(
						DispensaMedicamentoRNExceptionCode.MPM_01708);
			}
			return farmPadrao;
		}
		Integer diaDaSemana = Calendar.DAY_OF_WEEK;
		
		if(diaDaSemana.equals(1)||diaDaSemana.equals(7)){
			return localDispensaVO.getUnidadeFuncionalDispAlternativa();
		}
		
		AghFeriados feriado = this.getAghuFacade().obterFeriado(new Date());
		if(feriado!=null){
			return localDispensaVO.getUnidadeFuncionalDispAlternativa();
		}
		
		AfaFormaDosagem formaDosagem = this.getFarmaciaFacade().obterAfaFormaDosagem(fdsSeq);
		if(formaDosagem==null){
			throw new ApplicationBusinessException(
					DispensaMedicamentoRNExceptionCode.MPM_01709);
		}
		BigDecimal fatorConversaoUp = formaDosagem.getFatorConversaoUp();

		BigDecimal doseUp = null;
		if(dose != null){
			doseUp = dose.divide(fatorConversaoUp);
			unfSeqDisp = localDispensaVO.getUnidadeFuncionalDispDoseInt();
		}
		try {
			doseUp.toBigIntegerExact();
		} catch (ArithmeticException e) {
			unfSeqDisp = localDispensaVO.getUnidadeFuncionalDispDoseFrac();
		}
		if(unfSeqDisp !=null){
			unidadeFuncional = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeqDisp);	
		}
		if(unidadeFuncional == null){
			throw new ApplicationBusinessException(
					DispensaMedicamentoRNExceptionCode.MPM_01710);
		}
		
		if((new Date().after(unidadeFuncional.getHrioInicioAtendimento())||new Date().equals(unidadeFuncional.getHrioInicioAtendimento()))&&
		   (new Date().before(unidadeFuncional.getHrioFimAtendimento())||new Date().equals(unidadeFuncional.getHrioInicioAtendimento()))){
			return unfSeqDisp;
		} else {
			localDispensaVO.getUnidadeFuncionalDispAlternativa();
		}
		return null;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
//	protected AghFeriadosDAO getAghFeriadosDAO() {
//		return aghFeriadosDAO;
//	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}
	
//	protected AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO() {
//		return aghUnidadesFuncionaisDAO;
//	}
}
