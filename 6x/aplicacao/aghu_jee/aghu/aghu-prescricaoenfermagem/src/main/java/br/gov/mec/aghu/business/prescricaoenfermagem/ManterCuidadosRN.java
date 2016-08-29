package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.prescricaoenfermagem.ManterCuidadosON.ManterCuidadosONExceptionCode;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoUnf;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeItemCuidadoSumario;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoUnfDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeItemCuidadoSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterCuidadosRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ManterCuidadosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EpeCuidadoUnfDAO epeCuidadoUnfDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private EpeCuidadosDAO epeCuidadosDAO;
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@Inject
	private EpeItemCuidadoSumarioDAO epeItemCuidadoSumarioDAO;
	
	@Inject
	private EpeCuidadoDiagnosticoDAO epeCuidadoDiagnosticoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	private static final long serialVersionUID = -1503686634568119264L;
	
	/**
	 * @ORADB TRIGGER AGH.EPET_CUI_BRU
	 * BEFORE UPDATE
 	 * ON EPE_CUIDADOS
	 * @param original
	 * @param modificado
	 * @param login 
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesDeAtualizar(EpeCuidados original, EpeCuidados modificado) throws ApplicationBusinessException {
		
		if(modificado != null && modificado.getTipoFrequenciaAprazamento() != null || modificado.getFrequencia() != null) {
			verificarFrequencia(modificado.getTipoFrequenciaAprazamento().getSeq(), modificado.getFrequencia());
		}		
		
		if(CoreUtil.modificados(modificado.getDescricao(), original.getDescricao())) {
			cadastrosBasicosPrescricaoMedicaFacade.updateFatProcedHospInternosDescr(null, null, null, null, null, 
					modificado.getDescricao(), null, null, modificado.getSeq(), null);
		}
		
		if(CoreUtil.modificados(modificado.getIndSituacao(), original.getIndSituacao())) {
			cadastrosBasicosPrescricaoMedicaFacade.updateFatProcedHospInternosSituacao(null, null, null, null, null, 
					modificado.getIndSituacao(), null, null, modificado.getSeq(), null);
		}

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		modificado.setServidor(servidorLogado);
	}

	/**
	 * @ORADB: EPEK_CUI_RN.RN_CUIP_VER_FREQ
	 * @param tipoFrequenciaSeq
	 * @param frequenciaSeq
	 * @throws ApplicationBusinessException 
	 */
	public void verificarFrequencia(Short tipoFrequenciaSeq, Short frequenciaSeq) throws ApplicationBusinessException {
		
		if(tipoFrequenciaSeq == null || tipoFrequenciaSeq.intValue() == 0) {
			if(frequenciaSeq != null && frequenciaSeq.intValue() > 0) {
				throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.EPE_00228);
			}			
		} else {
			MpmTipoFrequenciaAprazamento tipoFrequencia = prescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(tipoFrequenciaSeq);		
			if(tipoFrequencia == null) {
				throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.EPE_00229);
			} else {				
				if(tipoFrequencia.getIndDigitaFrequencia() == Boolean.FALSE) {
					if(frequenciaSeq != null && frequenciaSeq.intValue() > 0) {
						throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.EPE_00230);
					}					
				} else {
					if(frequenciaSeq == null || frequenciaSeq.intValue() == 0) {
						throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.EPE_00231);
					}
				}
			}
		}		
	}
	
	/**
	 * @ORADB: EPEF_MAN_CUIDADOS.CHK_EPE_CUIDADOS
	 * @param cuidadoSeq
	 * @throws BaseListException 
	 */
	public void verificarAntesDeExcluir(Short cuidadoSeq) throws BaseListException {

		BaseListException listaException = new BaseListException();
		EpeCuidados cuidado = epeCuidadosDAO.obterPorChavePrimaria(cuidadoSeq);
		
		if(cuidado != null) {
			if(verificarEpeCuidadoDiagnosticoRelacionado(cuidadoSeq)) {
				adicionarException(ManterCuidadosONExceptionCode.MSG_CUIDADO_DIAGNOSTICO_RELACIONADO, listaException);
			}
			if(verificarEpeCuidadoUnfRelacionado(cuidadoSeq)) {
				adicionarException(ManterCuidadosONExceptionCode.MSG_UNIDADE_FUNCIONAL_RELACIONADO, listaException);
			}			
			if(verificarEpeItemCuidadoSumarioRelacionado(cuidadoSeq)) {
				adicionarException(ManterCuidadosONExceptionCode.MSG_ITEM_CUIDADO_SUMARIO_RELACIONADO, listaException);
			}
			if(verificarEpePrescricoesCuidadosRelacionado(cuidadoSeq)) {
				adicionarException(ManterCuidadosONExceptionCode.MSG_PRESCRICAO_CUIDADO_RELACIONADO, listaException);
			}
			/*if(existeEpeCuidadoMedicamentoRelacionado(cuidadoSeq)) {
				addException(ManterCuidadosONExceptionCode.MSG_CUIDADO_MEDICAMENTO_RELACIONADO, listaException);
			}
			if(existeEpeCuidadoExameRelacionado(cuidadoSeq)) {
				addException(ManterCuidadosONExceptionCode.MSG_CUIDADO_EXAME_RELACIONADO, listaException);
			}*/
		}
		if(listaException.hasException()) {
			throw listaException;
		}
	}
	
	/**
	 * @ORADB: EPEF_MAN_CUIDADOS.CHK_EPE_CUIDADOS
	 * @param dataReferencia
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void verificarPeriodo(Date dataReferencia) throws ApplicationBusinessException {

		Date dataAtual = Calendar.getInstance().getTime();
		int qtdeDias = DateUtil.calcularDiasEntreDatas(dataAtual, dataReferencia);

		AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_EPE);
		int valorNumerico = 0;
		if(param != null) {
			if(param.getVlrNumerico() != null) {
				valorNumerico = param.getVlrNumerico().intValue();
			}
		}
		if(qtdeDias > valorNumerico) {
			throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.EPE_00225);
		}
	}
	
	public Boolean verificarEpeCuidadoDiagnosticoRelacionado(Short cuidadoSeq) {
		List<EpeCuidadoDiagnostico> listaDiagnostico = epeCuidadoDiagnosticoDAO.obterEpeCuidadoDiagnosticoPorEpeCuidadoSeq(cuidadoSeq);
		if(listaDiagnostico != null && listaDiagnostico.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public Boolean verificarEpeCuidadoUnfRelacionado(Short cuidadoSeq) {
		List<EpeCuidadoUnf> listaUnidadeFuncional = epeCuidadoUnfDAO.obterEpeCuidadoUnfPorEpeCuidadoSeq(cuidadoSeq);
		if(listaUnidadeFuncional != null && listaUnidadeFuncional.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public Boolean verificarEpeItemCuidadoSumarioRelacionado(Short cuidadoSeq) {
		List<EpeItemCuidadoSumario> listaItemCuidadoSumario = epeItemCuidadoSumarioDAO.obterEpeItemCuidadoSumarioPorEpeCuidadoSeq(cuidadoSeq);
		if(listaItemCuidadoSumario != null && listaItemCuidadoSumario.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public boolean verificarEpePrescricoesCuidadosRelacionado(Short cuidadoSeq) {
		List<EpePrescricoesCuidados> listaPrescricaoCuidado =  epePrescricoesCuidadosDAO.obterEpePrescricoesCuidadosPorEpeCuidadoSeq(cuidadoSeq);
		if(listaPrescricaoCuidado != null && listaPrescricaoCuidado.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private void adicionarException(ManterCuidadosONExceptionCode code, BaseListException listaException) {
		   listaException.add(new ApplicationBusinessException(code));
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
}
