package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * ORADB PROCEDURE MBCP_ATUALIZA_AGENDA
 * 
 * @author aghu
 * 
 */
@Stateless
public class AtualizarAgendaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AtualizarAgendaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;


	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8113183301384868094L;

	public enum AtualizarAgendaRNExceptionCode implements BusinessExceptionCode {
		MBC_00948, MBC_01022,MBC_01837,MBC_01838;
	}

	/**
	 * ORADB PROCEDURE MBCP_ATUALIZA_AGENDA
	 * 
	 * @param operacaoBanco
	 * @param agenda
	 * @param cirurgia
	 * @param profCirurgias
	 * @param procedimentoVO
	 * @param qtdeProc
	 * @param servidorLogado
	 * @return agenda inserida ou atualizada
	 * @throws BaseException
	 */
	public MbcAgendas atualizarAgenda(DominioOperacaoBanco operacaoBanco, MbcAgendas agenda, final AipPacientes paciente, final AghUnidadesFuncionais unidadesFuncional,
			final AghEspecialidades especialidade, final MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs, final CirurgiaTelaProcedimentoVO procedimentoVO,
			final Short tempoPrevistoHoras, final Byte tempoPrevistoMinutos, final DominioOrigemPacienteCirurgia regime, final Boolean precaucaoEspecial,
			final MbcSalaCirurgica salaCirurgica, final Date dataPrevisaoInicio, final FatConvenioSaudePlano convenioSaudePlano, final Date dataAgenda, final Byte qtdeProc) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (unidadesFuncional == null) {
			// Erro ao recuperar o tempo de intervalo para escala em MBCP_ATUALIZA_ESCALA.
			throw new ApplicationBusinessException(AtualizarAgendaRNExceptionCode.MBC_00948);
		}

		final Byte valorIntervaloEscalaCirurgia = unidadesFuncional.getIntervaloEscalaCirurgia();
		final Byte valorIntervaloEscalaProced = unidadesFuncional.getIntervaloEscalaProced();

		if (procedimentoVO == null) {
			// Erro ao recuperar o tipo de procedimento para escala em MBCP_ATUALIZA_ESCALA. Contate GSIS.
			throw new ApplicationBusinessException(AtualizarAgendaRNExceptionCode.MBC_01022);
		}

		DominioTipoProcedimentoCirurgico valorPciTipo = procedimentoVO.getProcedimentoCirurgico().getTipo();

		Byte valorIntervaloEscala = null;

		if (DominioTipoProcedimentoCirurgico.CIRURGIA.equals(valorPciTipo)) {
			if(valorIntervaloEscalaCirurgia != null){				
				valorIntervaloEscala = valorIntervaloEscalaCirurgia;
			}else{
				// Erro ao devido não haver intervalos de tempo de cirurgia, cadastro de unidade de internação  
				throw new ApplicationBusinessException(AtualizarAgendaRNExceptionCode.MBC_01838,unidadesFuncional.getSeq());
			}
		} else {
			if(valorIntervaloEscalaProced != null){
				valorIntervaloEscala = valorIntervaloEscalaProced;				
			}else{
				// Erro ao devido não haver intervalos de tempo de procedimento, cadastro de unidade de internação  
				throw new ApplicationBusinessException(AtualizarAgendaRNExceptionCode.MBC_01837,unidadesFuncional.getSeq());
			}
		}


		final Date valorTempoSala = this.obterTempoSala(tempoPrevistoHoras, tempoPrevistoMinutos, valorIntervaloEscala);
		final Date valordthrPrevInicio = this.obterDthrPrevInicio(dataAgenda, dataPrevisaoInicio);
		final Date valorDthrPrevFim = this.obterDthrPrevFim(valordthrPrevInicio, valorTempoSala);

		if (DominioOperacaoBanco.INS.equals(operacaoBanco)) { // INCLUSÃO

			// Cria uma nova instância
			agenda = new MbcAgendas();

			agenda.setPaciente(paciente);
			agenda.setUnidadeFuncional(salaCirurgica.getUnidadeFuncional());
			agenda.setEspecialidade(especialidade);
			agenda.setProfAtuaUnidCirgs(mbcProfAtuaUnidCirgs);

			DominioRegimeProcedimentoCirurgicoSus regimeSelecionado = DominioRegimeProcedimentoCirurgicoSus.getRegimePorOrigemPacienteCirurgia(regime);
			agenda.setRegime(regimeSelecionado);

			agenda.setIndSituacao(DominioSituacaoAgendas.ES);
			agenda.setIndExclusao(false);
			agenda.setEspProcCirgs(procedimentoVO.getMbcEspecialidadeProcCirgs());
			agenda.setLadoCirurgia(procedimentoVO.getLado());
			agenda.setTempoSala(valorTempoSala);
			agenda.setIndPrioridade(false);
			agenda.setIndPrecaucaoEspecial(precaucaoEspecial);
			agenda.setSalaCirurgica(salaCirurgica);
			agenda.setDthrPrevInicio(valordthrPrevInicio);
			agenda.setDthrPrevFim(valorDthrPrevFim);
			agenda.setConvenioSaudePlano(convenioSaudePlano);
			agenda.setIndGeradoSistema(true);
			agenda.setDtAgenda(dataAgenda);
			if (qtdeProc != null) {
				agenda.setQtdeProc(qtdeProc.shortValue());
			}
			agenda.setIntervaloEscala(valorIntervaloEscala);

			// INSERIR AGENDA
			this.getMbcAgendasRN().persistirAgenda(agenda, servidorLogado);

		} else if (DominioOperacaoBanco.UPD.equals(operacaoBanco)) { // ALTERAÇÃO

			agenda = this.getMbcAgendasDAO().obterPorChavePrimaria(agenda.getSeq());
			
			agenda.setPaciente(paciente);
			agenda.setUnidadeFuncional(salaCirurgica.getUnidadeFuncional());
			agenda.setEspecialidade(especialidade);
			agenda.setProfAtuaUnidCirgs(mbcProfAtuaUnidCirgs);
			DominioRegimeProcedimentoCirurgicoSus regimeSelecionado = DominioRegimeProcedimentoCirurgicoSus.getRegimePorOrigemPacienteCirurgia(regime);
			agenda.setRegime(regimeSelecionado);
			agenda.setEspProcCirgs(procedimentoVO.getMbcEspecialidadeProcCirgs());
			agenda.setLadoCirurgia(procedimentoVO.getLado());
			agenda.setTempoSala(valorTempoSala);
			agenda.setIndPrecaucaoEspecial(precaucaoEspecial);
			agenda.setSalaCirurgica(salaCirurgica);
			agenda.setDthrPrevInicio(valordthrPrevInicio);
			agenda.setDthrPrevFim(valorDthrPrevFim);
			agenda.setConvenioSaudePlano(convenioSaudePlano);
			agenda.setDtAgenda(dataAgenda);
			if (qtdeProc != null) {
				agenda.setQtdeProc(qtdeProc.shortValue());
			}
			agenda.setIntervaloEscala(valorIntervaloEscala);

			// ATUALIZAR AGENDA
			this.getMbcAgendasRN().persistirAgenda(agenda, servidorLogado);

			// TODO MBCK_CRG_RN.VALIDA_REGRA_BRU := 'S';
		}

		return agenda;
	}

	/**
	 * Obtém o tempo na sala
	 * 
	 * @param tempoPrevistoHoras
	 * @param tempoPrevistoMinutos
	 * @param valorIntervaloEscala
	 * @return
	 */
	private Date obterTempoSala(Short tempoPrevistoHoras, Byte tempoPrevistoMinutos, final Byte valorIntervaloEscala) {

		tempoPrevistoHoras = (Short) CoreUtil.nvl(tempoPrevistoHoras, Short.valueOf("0"));
		tempoPrevistoMinutos = (Byte) CoreUtil.nvl(tempoPrevistoMinutos, Byte.valueOf("0"));

		// Calcula a diferença entre o tempo previsto em minutos e o intervalo na escala
		Integer diferencaMinutos = tempoPrevistoMinutos - valorIntervaloEscala;

		Calendar resultado = Calendar.getInstance(new Locale("pt", "BR"));
		resultado.setTime(new Date());

		resultado.set(Calendar.HOUR_OF_DAY, tempoPrevistoHoras);
		resultado.set(Calendar.MINUTE, diferencaMinutos);

		return resultado.getTime();
	}

	/**
	 * Obtém a data e hora da previsão de início
	 * 
	 * @param dataAgenda
	 * @param horaCirurgia
	 * @return
	 */
	private Date obterDthrPrevInicio(Date dataAgenda, Date horaCirurgia) {

		Calendar resultado = Calendar.getInstance(new Locale("pt", "BR"));
		resultado.setTime(DateUtil.truncaData(dataAgenda));

		Calendar calendarHoraCirurgia = Calendar.getInstance(new Locale("pt", "BR"));
		calendarHoraCirurgia.setTime(horaCirurgia);

		// Setando horas e minutos!
		resultado.set(Calendar.HOUR_OF_DAY, calendarHoraCirurgia.get(Calendar.HOUR_OF_DAY));
		resultado.set(Calendar.MINUTE, calendarHoraCirurgia.get(Calendar.MINUTE));

		return resultado.getTime();
	}

	/**
	 * Obtém a data e hora da previsão do fim
	 * 
	 * @param valordthrPrevInicio
	 * @param valorTempoSala
	 * @return
	 */
	private Date obterDthrPrevFim(final Date valordthrPrevInicio, final Date valorTempoSala) {

		Calendar resultado = Calendar.getInstance(new Locale("pt", "BR"));
		resultado.setTime(valordthrPrevInicio);

		Calendar calendarTempoSala = Calendar.getInstance(new Locale("pt", "BR"));
		calendarTempoSala.setTime(valorTempoSala);

		// Acrescentando horas e minutos!
		resultado.add(Calendar.HOUR_OF_DAY, calendarTempoSala.get(Calendar.HOUR_OF_DAY));
		resultado.add(Calendar.MINUTE, calendarTempoSala.get(Calendar.MINUTE));

		return resultado.getTime();
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcAgendasRN getMbcAgendasRN() {
		return mbcAgendasRN;
	}

	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}

}
