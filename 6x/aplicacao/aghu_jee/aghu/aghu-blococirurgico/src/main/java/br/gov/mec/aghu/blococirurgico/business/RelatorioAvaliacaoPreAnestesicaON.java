package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioAvaliacaoPreAnestesicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioAvaliacaoPreAnestesicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	private static final long serialVersionUID = 8547923667963924546L;

	public enum RelatorioAvaliacaoPreAnestesicaONExceptionCode implements
			BusinessExceptionCode {
		MBC_01348, VALOR_PARAMETRO_INVALIDO
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	private Integer obterAghuParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		Integer parametro = 0;
		try {
			parametro = getParametroFacade().buscarValorInteiro(parametrosEnum);

			if (parametro != null & parametro > 0) {
				return parametro;
			}else{																		
				throw new ApplicationBusinessException(RelatorioAvaliacaoPreAnestesicaONExceptionCode.VALOR_PARAMETRO_INVALIDO);
			}
			
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(e.getCode(), RelatorioAvaliacaoPreAnestesicaONExceptionCode.VALOR_PARAMETRO_INVALIDO);
		}
	}
	
	private Date obterHoraAntes(Integer horaAntes) {
		if(horaAntes < 1) {
			return DateUtil.adicionaHoras(DateUtil.obterDataComHoraInical(null), - horaAntes);
		} else {
			return  DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(null), - horaAntes);
		}
	}
	
	private Date obterHoraDepois(Integer horaDepois) {
		if(horaDepois < 1) {
			return DateUtil.adicionaHoras(DateUtil.obterDataComHoraFinal(null), horaDepois);
		} else {
			return DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(null), horaDepois);
		}
	}
	
	/**
	 * ORADB p_mostra_aval_anes_cirg 
	 * @param crgSelecionada
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public Boolean validarAvalicaoPreAnestesica(Integer crgSelecionada) throws ApplicationBusinessException {

		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSelecionada);

		if (cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) || cirurgia.getSituacao().equals(DominioSituacaoCirurgia.RZDA)) {
			return Boolean.FALSE;
		}

		Date horaAntes = obterHoraAntes(obterAghuParametro(AghuParametrosEnum.P_HORAS_IMP_AVAL_ANTES) / 24);
		Date horaDepois = obterHoraDepois(obterAghuParametro(AghuParametrosEnum.P_HORAS_IMP_AVAL_DEPOIS) / 24);

		if (!(DateUtil.validaDataMaiorIgual(cirurgia.getDataInicioCirurgia(), horaAntes)
				& DateUtil.validaDataMenorIgual(cirurgia.getDataInicioCirurgia(), horaDepois))) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;

	}
	
	/**
	 * ORADB p_imprime_aval_anestesia
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Map<AghAtendimentos, Boolean> validarExitenciaAvalicaoPreAnestesica(final Integer paciente) throws ApplicationBusinessException {
		
		final Short especiliada = obterAghuParametro(AghuParametrosEnum.P_AGHU_COD_ESP_AVALIACAO_PRE_ANESTESICA).shortValue();
		final Date dataCorrente = new Date();
		
		Map<AghAtendimentos, Boolean> avalicoesPreAnestesica = new HashMap<AghAtendimentos, Boolean>();
		
		List<AacConsultas> listaCursosAna = getAmbulatorioFacade()
				.obterConsultaAnamnesesPorDataConsPacEspIndPendente(dataCorrente, paciente, especiliada, DominioIndPendenteAmbulatorio.V);
		
		if(listaCursosAna.isEmpty()){
			
			throw new ApplicationBusinessException(RelatorioAvaliacaoPreAnestesicaONExceptionCode.MBC_01348);
			
		} else {
			
			for (AacConsultas consultaAna : listaCursosAna) {
				
				for(AghAtendimentos atendimento : consultaAna.getAtendimentos()) {
					avalicoesPreAnestesica.put(atendimento, Boolean.TRUE);
				}
				
				List<AacConsultas> listaCursosEvo = getAmbulatorioFacade()
						.obterConsultaEvolucoesPorDataConsPacEspIndPendente(consultaAna.getDtConsulta(), paciente, especiliada, DominioIndPendenteAmbulatorio.V);
				
				for (AacConsultas consultaEvo : listaCursosEvo) {
					
					for(AghAtendimentos atendimento : consultaEvo.getAtendimentos()) {
						avalicoesPreAnestesica.put(atendimento, Boolean.TRUE);
					}
				}
			}
		}
		
		return avalicoesPreAnestesica;
	}
}
