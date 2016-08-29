package br.gov.mec.aghu.prescricaomedica.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAprazamentoFrequenciasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmHorarioInicAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * Regras de geração do aprazamento para o relatório de itens confirmados da
 * prescrição.
 * 
 * @ORADB MPMK_APRZ
 * 
 * @author gmneto
 * 
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class AprazamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AprazamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MpmHorarioInicAprazamentoDAO mpmHorarioInicAprazamentoDAO;
	
	@Inject
	private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;

	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3020801383420917608L;

	/**
	 * Gera o aprazamento para um cuidado, medicamento ou solução
	 * 
	 * @ORADB MPMK_APRZ.MPMC_GET_APRZ_REPORT
	 * 
	 * @param prescricao
	 * @param frequencia
	 * @return
	 */
	public List<String> gerarAprazamento(Integer atdSeq, Integer prescSeq, Date dthrInicioItem, Date dthrFimItem,
			Short seqTipoFrequenciaAprazamento, TipoItemAprazamento tipoItem, Date dtHrInicioTratamento,
			Boolean indNecessario, Short frequencia) {
		
		MpmPrescricaoMedica prescricao = mpmPrescricaoMedicaDAO.obterPorChavePrimaria(new MpmPrescricaoMedicaId(atdSeq, prescSeq));
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(seqTipoFrequenciaAprazamento);
		
		List<String> aprazamento = null;
		if (frequencia == null) {
			aprazamento = this.gerarAprazamentoSemFrequencia(prescricao,
					dthrInicioItem, dthrFimItem, tipoFrequenciaAprazamento,
					tipoItem, indNecessario);
		} else {
			aprazamento = this.gerarAprazamentoComFrequencia(prescricao,
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
	 * @oradb MPMK_APRZ.MPMC_APRZ_SEM_FREQ
	 * 
	 * @param prescricao
	 * @param dthrInicioItem
	 * @param dthrFimItem
	 * @param tipoFrequenciaAprazamento
	 * @param tipoItem
	 * @param indNecessario
	 * @return
	 */
	private List<String> gerarAprazamentoSemFrequencia(
			MpmPrescricaoMedica prescricao, Date dthrInicioItem,
			Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			TipoItemAprazamento tipoItem, Boolean indNecessario) {

		List<String> aprazamento = new ArrayList<String>();
		// Date dataInicio = null;
		// Date dataFim = null;
		boolean primeiraVez = true;

		if (indNecessario != null && indNecessario) {
			return null;
		}

		if (tipoFrequenciaAprazamento.getIndFormaAprazamento() == DominioFormaCalculoAprazamento.I) {
			return null;
		}

		if (tipoFrequenciaAprazamento.getIndFormaAprazamento() == DominioFormaCalculoAprazamento.C) {
			if (tipoItem == TipoItemAprazamento.MEDICAMENTO
					|| tipoItem == TipoItemAprazamento.SOLUCAO) {
				aprazamento
						.add("I=        T=        I=        T=        I=        T=        ");
			} else {
				aprazamento.add("I=          T=          ");
			}
			return aprazamento;
		}

		// if (DateUtil
		// .validaDataMaior(prescricao.getDthrInicio(), dthrInicioItem)) {
		// dataInicio = prescricao.getDthrInicio();
		// } else {
		// dataInicio = dthrInicioItem;
		// }
		//
		// if (dthrFimItem == null
		// || (dthrFimItem != null && DateValidator.validaDataMenor(prescricao
		// .getDthrFim(), dthrFimItem))) {
		// dataFim = prescricao.getDthrFim();
		// } else {
		// dataFim = dthrFimItem;
		// }

		List<MpmAprazamentoFrequencia> listAprazamentosFrequencia = this
				.getMpmAprazamentoFrequenciasDAO()
				.listarAprazamentosFrequenciaPorTipo(tipoFrequenciaAprazamento);

		if (listAprazamentosFrequencia.size() == 1) {
			return null;
		}

		for (MpmAprazamentoFrequencia aprazamentoFrequencia : listAprazamentosFrequencia) {
			if (primeiraVez) {
				primeiraVez = false;
				aprazamento.add(aprazamentoFrequencia.getDescricao());
			} else {
				aprazamento.add("          "
						+ aprazamentoFrequencia.getDescricao());

			}

		}

		return aprazamento;
	}

	/**
	 * Gera o aprazamento para um cuidado, medicamento ou solução que tenha uma
	 * frequencia especificada.
	 * 
	 * @oradb MPMK_APRZ.MPMC_APRZ_COM_FREQ
	 * 
	 * @param prescricao
	 * @param dthrInicioItem
	 * @param dthrFimItem
	 * @param tipoFrequenciaAprazamento
	 * @param tipoItem
	 * @param indNecessario
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private List<String> gerarAprazamentoComFrequencia(
			MpmPrescricaoMedica prescricao, Date dthrInicioItem,
			Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			Short frequencia, Date dtHrInicioTratamento,
			TipoItemAprazamento tipoItem, Boolean indNecessario) {

		if (indNecessario != null && indNecessario) {
			return null;
		}
		
		if (dtHrInicioTratamento != null){
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
//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Float vezes = null;
		Float quantidade = null;

		AghAtendimentos atendimento = prescricao.getAtendimento();
		AghUnidadesFuncionais unidade = atendimento.getUnidadeFuncional();

		if (tipoItem == TipoItemAprazamento.MEDICAMENTO || tipoItem == TipoItemAprazamento.SOLUCAO) {
			
			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidade.getSeq(), ConstanteAghCaractUnidFuncionais.NAO_APRAZA_MDTO_PME);
			if (possuiCaracteristica) {
				moverDado = false;
			}
			
		} else if (tipoItem == TipoItemAprazamento.CUIDADO) {
			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidade.getSeq(), ConstanteAghCaractUnidFuncionais.NAO_APRAZA_CUIDADO_PME);
			if (possuiCaracteristica) {
				moverDado = false;
			}
		}

		horarioInicio = getMpmHorarioInicAprazamentoDAO()
				.obterHoraInicialPorTipoFrequenciaAprazamento(unidade,
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

		if (unidade.getHrioValidadePme() == null) {
			return null;
		}

		if (horario == null) {
			horario = unidade.getHrioValidadePme();
		}

		if (horario.before(unidade.getHrioValidadePme())) {
			dataReferencia = DateUtil.comporDiaHora(prescricao.getDthrFim(),
					horario);
		} else if (DateUtil.truncaData(prescricao.getDthrInicio()).equals(
				DateUtil.truncaData(prescricao.getDthrFim()))) {
			dataReferencia = DateUtil.comporDiaHora(DateUtil.adicionaDias(
					prescricao.getDthrFim(), -1), horario);
		} else {
			dataReferencia = DateUtil.comporDiaHora(prescricao.getDthrInicio(),
					horario);
		}

		if (prescricao.getDthrInicio().after(dthrInicioItem)) {
			dataInicio = prescricao.getDthrInicio();
		} else {
			dataInicio = dthrInicioItem;
		}

		if (dthrFimItem == null
				|| (dthrFimItem != null && prescricao.getDthrFim().before(
						dthrFimItem))) {
			dataFim = prescricao.getDthrFim();

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
//						else {
//							aprazamento.append("          ");
//						}
					} else {
						if (moverDado) {
							aprazamento.add("          "
									+ getSimpleDateFormat(dataReferencia).format(dataReferencia));
						} 
//						else {
//							aprazamento.append("          ");
//						}
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
		if (cal.get(Calendar.MINUTE) == 0) { //Se minuto igual a 0 então retorna somente hora
			sdf = new SimpleDateFormat("HH");
		}
		
		return sdf;
	}

	protected MpmHorarioInicAprazamentoDAO getMpmHorarioInicAprazamentoDAO() {
		return mpmHorarioInicAprazamentoDAO;
	}

	protected MpmAprazamentoFrequenciasDAO getMpmAprazamentoFrequenciasDAO() {
		return mpmAprazamentoFrequenciasDAO;
	}

	public MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	public MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
}
