package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

/**
 * Regras de geração do aprazamento para o relatório de itens confirmados da
 * prescrição enfermagem.
 * 
 * @ORADB EPEK_APRZ
 * 
 * @author gzapalaglio
 * 
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class AprazamentoPrescricaoEnfermagemRN extends BaseBusiness {
	
	private static final String ESPACO_EM_BRANCO_RELATORIO_RETRATO = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	
	private static final String ESPACO_EM_BRANCO_RELATORIO_PAISAGEM = ESPACO_EM_BRANCO_RELATORIO_RETRATO 
						+ ESPACO_EM_BRANCO_RELATORIO_RETRATO;
	
	private static final Log LOG = LogFactory.getLog(AprazamentoPrescricaoEnfermagemRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4568316680581110494L;
	
	/**
	 * Gera o aprazamento para um cuidado, medicamento ou solução
	 * 
	 * @ORADB EPEK_APRZ.EPEC_GET_APRZ_REPORT
	 * 
	 * @param prescricaoEnfermagem
	 * @param frequencia
	 * @return
	 */
	public List<String> gerarAprazamentoPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem,
			Date dthrInicioItem, Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			TipoItemAprazamento tipoItem, Date dtHrInicioTratamento,
			Boolean indNecessario, Short frequencia) {

		List<String> aprazamento = null;
		if (frequencia == null) {
			aprazamento = this.gerarAprazamentoSemFrequencia(prescricaoEnfermagem,
					dthrInicioItem, dthrFimItem, tipoFrequenciaAprazamento,
					tipoItem, indNecessario);
		} else {
			aprazamento = this.gerarAprazamentoComFrequencia(prescricaoEnfermagem,
					dthrInicioItem, dthrFimItem, tipoFrequenciaAprazamento,
					frequencia, dtHrInicioTratamento, tipoItem, indNecessario);
		}

		return aprazamento;

	}

	/**
	 * 
	 * Gera o aprazamento para um cuidado, medicamento ou solução que não tenha
	 * uma frequencia especificada.
	 * 
	 * 
	 * @oradb EPEK_APRZ.MPMC_APRZ_SEM_FREQ
	 * 
	 * @param prescricaoEnfermagem
	 * @param dthrInicioItem
	 * @param dthrFimItem
	 * @param tipoFrequenciaAprazamento
	 * @param tipoItem
	 * @param indNecessario
	 * @return
	 * @throws AGHUNegocioException 
	 */
	private List<String> gerarAprazamentoSemFrequencia(
			EpePrescricaoEnfermagem prescricaoEnfermagem, Date dthrInicioItem,
			Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			TipoItemAprazamento tipoItem, Boolean indNecessario) {


		if (indNecessario != null && indNecessario) {
			return null;
		}

		if (tipoFrequenciaAprazamento.getIndFormaAprazamento() == DominioFormaCalculoAprazamento.I) {
			return null;
		}

		List<String> aprazamento = new ArrayList<String>();
		if (formaAprazamentoContinuo(tipoFrequenciaAprazamento)) {
			if (tipoItem == TipoItemAprazamento.MEDICAMENTO || tipoItem == TipoItemAprazamento.SOLUCAO) {
				aprazamento.add("I=        T=        I=        T=        I=        T=        ");
			} else {
				aprazamento.add("I=          T=          ");
			}
			return aprazamento;
		}

		List<MpmAprazamentoFrequencia> listAprazamentosFrequencia = prescricaoMedicaFacade
				.listarAprazamentosFrequenciaPorTipo(tipoFrequenciaAprazamento);

		if (listAprazamentosFrequencia.size() == 1) {
			return null;
		}
		
		return defineAprazamentoFrequencia(listAprazamentosFrequencia);
		
	}
	
	private Boolean formaAprazamentoContinuo(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento.getIndFormaAprazamento() == DominioFormaCalculoAprazamento.C;
	}
	
	private List<String> defineAprazamentoFrequencia(List<MpmAprazamentoFrequencia> listAprazamentosFrequencia) {
		List<String> aprazamento = new ArrayList<String>(); 
		Boolean primeiraVez = Boolean.TRUE;
		
		for (MpmAprazamentoFrequencia aprazamentoFrequencia : listAprazamentosFrequencia) {
			if (primeiraVez) {
				primeiraVez = Boolean.FALSE;
				aprazamento.add(aprazamentoFrequencia.getDescricao());
			} else {
				adicionaAprazamentoFrequencia(aprazamento, aprazamentoFrequencia);
			}
		}
		return aprazamento;
	}
	
	private void adicionaAprazamentoFrequencia(List<String> aprazamento, MpmAprazamentoFrequencia aprazamentoFrequencia) {
		String descricao = aprazamentoFrequencia.getDescricao();
		if (relatorioModeloPaisagem()) {
			aprazamento.add(ESPACO_EM_BRANCO_RELATORIO_PAISAGEM + ESPACO_EM_BRANCO_RELATORIO_PAISAGEM + descricao);
		} else {
			aprazamento.add(ESPACO_EM_BRANCO_RELATORIO_RETRATO + descricao);
		}
	}
	
	private Boolean relatorioModeloPaisagem() {
		String relatorioModeloPaisagem = recuperaParametroRelatorioPaisagem().getVlrTexto();
		return "S".equalsIgnoreCase(relatorioModeloPaisagem);
	}
	
	private AghParametros recuperaParametroRelatorioPaisagem() {
		try {
			return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_RELATORIO_PRESCRICAO_ENFERMAGEM_PAISAGEM);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuperar Parâmetro " + AghuParametrosEnum.P_RELATORIO_PRESCRICAO_ENFERMAGEM_PAISAGEM);
		}
		return null;
			
	}

	/**
	 * Gera o aprazamento para um cuidado, medicamento ou solução que tenha uma
	 * frequencia especificada.
	 * 
	 * @oradb EPEK_APRZ.MPMC_APRZ_COM_FREQ
	 * 
	 * @param prescricaoEnfermagem
	 * @param dthrInicioItem
	 * @param dthrFimItem
	 * @param tipoFrequenciaAprazamento
	 * @param tipoItem
	 * @param indNecessario
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private List<String> gerarAprazamentoComFrequencia(
			EpePrescricaoEnfermagem prescricaoEnfermagem, Date dthrInicioItem,
			Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			Short frequencia, Date dtHrInicioTratamento,
			TipoItemAprazamento tipoItem, Boolean indNecessario) {

		if (indNecessario != null && indNecessario) {
			return null;
		}
		
		

		Boolean moverDado = true;
		Date horarioInicio = null;
		Date horario = null;
		Date dataReferencia = null;
		Date dataInicio = null;
		Date dataFim = null;
		int cont = 0;
		boolean primeiraVez = true;
		List<String> aprazamento = new ArrayList<String>();
		Float vezes = null;
		Float quantidade = null;

		AghAtendimentos atendimento = prescricaoEnfermagem.getAtendimento();
		AghUnidadesFuncionais unidade = atendimento.getUnidadeFuncional();

		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidade.getSeq(), ConstanteAghCaractUnidFuncionais.NAO_APRAZA_CUIDADO_PEN);
		if (tipoItem == TipoItemAprazamento.CUIDADO && possuiCaracteristica) {
				moverDado = false;
		}

		horarioInicio = prescricaoMedicaFacade.obterHoraInicialPorTipoFrequenciaAprazamento(unidade,
						tipoFrequenciaAprazamento, frequencia);

		if (horarioInicio == null) {
			moverDado = false;
		}

		if (tipoFrequenciaAprazamento.getIndFormaAprazamento() == DominioFormaCalculoAprazamento.I) {
			vezes = frequencia
					* tipoFrequenciaAprazamento.getFatorConversaoHoras()
							.floatValue();
			if (vezes <= 0) {
				return null;
			}

			if ((24 / vezes < 1)) {
				return null;
			}

			quantidade = (24 / vezes);

		} else if (tipoFrequenciaAprazamento.getIndFormaAprazamento() == DominioFormaCalculoAprazamento.V) {
			quantidade = frequencia.floatValue();
		} else {
			return null;
		}

		if (quantidade == null || quantidade == 0) {
			return null;
		}

		if (dtHrInicioTratamento != null) {
			horario = dtHrInicioTratamento;
		} else {
			horario = horarioInicio;
		}

		if (unidade.getHrioValidadePen() == null) {
			return null;
		}

		if (horario == null) {
			horario = unidade.getHrioValidadePen();
		}

		if (DateUtil.validaHoraMaior(unidade.getHrioValidadePen(), horario)) {
			dataReferencia = DateUtil.comporDiaHora(prescricaoEnfermagem.getDthrFim(),
					horario);
		} else if (DateUtil.truncaData(prescricaoEnfermagem.getDthrInicio()).equals(
				DateUtil.truncaData(prescricaoEnfermagem.getDthrFim()))) {
			dataReferencia = DateUtil.comporDiaHora(DateUtil.adicionaDias(
					prescricaoEnfermagem.getDthrFim(), -1), horario);
		} else {
			dataReferencia = DateUtil.comporDiaHora(prescricaoEnfermagem.getDthrInicio(),
					horario);
		}

		if (DateUtil.validaHoraMaior(prescricaoEnfermagem.getDthrInicio(),dthrInicioItem)) {
			dataInicio = prescricaoEnfermagem.getDthrInicio();
		} else {
			dataInicio = dthrInicioItem;
		}

		if (dthrFimItem == null
				|| (dthrFimItem != null &&  DateUtil.validaHoraMaior(dthrFimItem,prescricaoEnfermagem.getDthrFim()))) {
			dataFim = prescricaoEnfermagem.getDthrFim();
		} else {
			dataFim = dthrFimItem;
		}

		if (quantidade > 50) {
			moverDado = false;
		}

		if (DateUtil.diffInDays(DateUtil.truncaData(dataFim), DateUtil
				.truncaData(dataInicio)) == 1) {

			while (DateUtil.validaDataMaiorIgual(dataReferencia, dataInicio)) {
				dataReferencia = DateUtil.adicionaDiasFracao(dataReferencia,
						new Float(-((24 / quantidade) / 24)));
			}
			dataReferencia = DateUtil.adicionaDiasFracao(dataReferencia,
					new Float(((24 / quantidade) / 24)));

		}

	
		while (DateValidator.validaDataMenor(dataReferencia, dataFim)) {
			if (DateUtil.validaDataMaiorIgual(dataReferencia, dataInicio)) {
				cont++;
				if (cont < 50) {
					if (primeiraVez) {
						primeiraVez = false;
						if (moverDado) {
							aprazamento.add(getSimpleDateFormat(dataReferencia).format(dataReferencia));
						} 
					} else {
						if (moverDado) {
							aprazamento.add("          "
									+ getSimpleDateFormat(dataReferencia).format(dataReferencia));
						} 
					}

				}
				
			}
			dataReferencia = DateUtil.adicionaDiasFracao(dataReferencia,
					new Float(((24 / quantidade) / 24)));

		}

		return aprazamento;

	}

	/**
	 * Retorna o formato da data dependendo do horário. 
	 * Se os minutos forem diferentes de zero, então o formato é HH:mm, Senão é somente HH 
	 * @param dataReferencia Data que vai ser formatada.
	 * @return DateFormat
	 */
	private DateFormat getSimpleDateFormat(Date dataReferencia) {
		DateFormat sdf = new SimpleDateFormat("HH:mm"); //PADRÃO
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataReferencia);
		
		return sdf;
	}
}