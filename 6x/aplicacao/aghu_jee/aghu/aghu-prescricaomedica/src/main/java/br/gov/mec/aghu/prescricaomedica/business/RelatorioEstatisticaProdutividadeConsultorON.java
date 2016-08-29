package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemRelatorioEstatisticaProdutividadeConsultorVO;
import br.gov.mec.aghu.prescricaomedica.vo.NumeroHorasUteisVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioEstatisticaProdutividadeConsultorVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * #3855 Prescrição: Emitir relatório de estatísica da produtividade do
 * consultor
 * 
 * @author aghu
 *
 */
@Stateless
public class RelatorioEstatisticaProdutividadeConsultorON extends BaseBusiness {

	private static final long serialVersionUID = -5757824040007930031L;

	private static final Log LOG = LogFactory.getLog(RelatorioEstatisticaProdutividadeConsultorON.class);

	private static final int MINUTOS_24_HORAS = 1440; // Minutos em 24Hrs

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final String CONSULTORIA = "CONSULTORIA";
	private static final String CONSULTORIA_URG = "CONSULTORIA URG.";
	private static final String AVAL_PRE_CIRUR = "AVAL. PRE-CIRUR";
	private static final String AVAL_URGENTE = "AVAL. URGENTE";
	private static final String ALTA_HOSPITALAR = "ALTA HOSPITALAR";
	private static final Integer LIMITE_PERIODO_IMPRESSAO = 720;

	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	private enum RelatorioEstatisticaProdutividadeConsultorONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL, MENSAGEM_PERIODO_IMPRESSAO_MENOR_2ANOS
	}

	/**
	 * Pesquisa C2
	 * 
	 * @param espSeq
	 * @param dtInicio
	 * @param dtFim
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public RelatorioEstatisticaProdutividadeConsultorVO pesquisarRelatorioEstatisticaProdutividadeConsultor(final Short espSeq, final Date dtInicio, final Date dtFim) throws ApplicationBusinessException {

		// Validações: RN02 e RN03
		validarParametrosPessquisa(dtInicio, dtFim);

		// Pesquisa C2
		List<ItemRelatorioEstatisticaProdutividadeConsultorVO> resultados = this.mpmSolicitacaoConsultoriaDAO.pesquisarRelatorioEstatisticaProdutividadeConsultor(espSeq, dtInicio, dtFim);

		// Horas e minutos do TEMPO MÉDIO GERAL DO CONHECIMENTO
		int somaHorasTempoMedioGeralConhecimento = 0;
		int somaMinutosTempoMedioGeralConhecimento = 0;

		// Horas e minutos do TEMPO GERAL DE RESPOSTA
		int somaHorasTempoMedioGeralResposta = 0;
		int somaMinutostempoMedioGeralResposta = 0;

		for (ItemRelatorioEstatisticaProdutividadeConsultorVO vo : resultados) {

			/*
			 * Determina o tipo de consultoria
			 */
			String tipoConsultoria = StringUtils.EMPTY;
			if (DominioTipoSolicitacaoConsultoria.C.equals(vo.getTipo())) {
				tipoConsultoria = DominioSimNao.N.equals(vo.getIndUrgencia()) ? CONSULTORIA : CONSULTORIA_URG;
			} else if (DominioTipoSolicitacaoConsultoria.A.equals(vo.getTipo())) {
				tipoConsultoria = DominioSimNao.N.equals(vo.getIndUrgencia()) ? AVAL_PRE_CIRUR : AVAL_URGENTE;
			}
			vo.setTipoConsultoria(tipoConsultoria);

			/*
			 * Determina o valor da data de resposta
			 */
			String dthrResposta = StringUtils.EMPTY;
			if (vo.getDthrResposta() == null) {
				dthrResposta = DominioPacAtendimento.N.equals(vo.getIndPacAtendimento()) ? ALTA_HOSPITALAR : StringUtils.EMPTY;
			} else {
				dthrResposta = DateUtil.dataToString(vo.getDthrResposta(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
			}
			vo.setDataResposta(dthrResposta);

			/*
			 * Calcular o tempo de resposta através do período da data e hora de
			 * solicitação até a data e hora da resposta
			 */
			if (vo.getDataSolicitacao() != null && vo.getDthrResposta() != null) {
				NumeroHorasUteisVO tempoResposta = calcularNumeroHorasUteis(vo.getDataSolicitacao(), vo.getDthrResposta());
				vo.setTempoResposta(tempoResposta.toString());
			}

			/*
			 * Soma horas e minutos do TEMPO MÉDIO GERAL DO CONHECIMENTO
			 */
			NumeroHorasUteisVO parteGeralConhecimento = calcularNumeroHorasUteis(vo.getDataSolicitacao(), vo.getDataConhecimento());
			somaHorasTempoMedioGeralConhecimento += parteGeralConhecimento.getHoras();
			somaMinutosTempoMedioGeralConhecimento += parteGeralConhecimento.getMinutos();

			/*
			 * Soma horas e minutos do TEMPO GERAL DE RESPOSTA
			 */
			if (vo.getDataSolicitacao() != null && vo.getDthrResposta() != null) {
				NumeroHorasUteisVO parteGeralResposta = calcularNumeroHorasUteis(vo.getDataSolicitacao(), vo.getDthrResposta());
				somaHorasTempoMedioGeralResposta += parteGeralResposta.getHoras();
				somaMinutostempoMedioGeralResposta += parteGeralResposta.getMinutos();
			}

		}

		// Calcula a MÉDIA do tempo médio do TEMPO MÉDIO GERAL DO CONHECIMENTO
		String tempoMedioGeralConhecimento = calcularMediaTotal(somaHorasTempoMedioGeralConhecimento, somaMinutosTempoMedioGeralConhecimento, resultados.size());

		// Calcula a MÉDIA do tempo médio do TEMPO GERAL DE RESPOSTA
		String tempoMedioGeralResposta = calcularMediaTotal(somaHorasTempoMedioGeralResposta, somaMinutostempoMedioGeralResposta, resultados.size());

		// Retorna resultados da pesquisa e médias
		return new RelatorioEstatisticaProdutividadeConsultorVO(resultados, tempoMedioGeralConhecimento, tempoMedioGeralResposta);

	}

	/**
	 * Valida parâmetros e regras da consulta
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @throws ApplicationBusinessException
	 */
	private void validarParametrosPessquisa(final Date dtInicio, final Date dtFim) throws ApplicationBusinessException {

		if (DateUtil.validaDataMaior(dtInicio, dtFim)) {
			// Data incial não deve ser maior que final
			throw new ApplicationBusinessException(RelatorioEstatisticaProdutividadeConsultorONExceptionCode.MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL);
		}

		final int periodoSelecionado = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(dtInicio, dtFim);
		if (periodoSelecionado >= LIMITE_PERIODO_IMPRESSAO) {
			// RN02 e RN03: Limite de impressão é de 720 dias
			throw new ApplicationBusinessException(RelatorioEstatisticaProdutividadeConsultorONExceptionCode.MENSAGEM_PERIODO_IMPRESSAO_MENOR_2ANOS);
		}
	}

	/**
	 * ORADB FUNCTION MPMC_NHORAS_UTEIS
	 * 
	 * @param dataHoraInicio
	 * @param dataHoraFim
	 * @return
	 */
	private NumeroHorasUteisVO calcularNumeroHorasUteis(Date dataHoraInicio, Date dataHoraFim) throws ApplicationBusinessException {

		// Trunca datas
		dataHoraInicio = DateUtils.truncate(dataHoraInicio, Calendar.MINUTE);
		dataHoraFim = DateUtils.truncate(dataHoraFim, Calendar.MINUTE);

		// Data atual do intervalo que será percorrido
		Calendar cDataAtualIntervalo = Calendar.getInstance();
		cDataAtualIntervalo.setTime(DateUtils.truncate(dataHoraInicio, Calendar.DATE));

		// Data limite do intervalo que será percorrido
		Date dataLimiteIntervalo = DateUtils.truncate(dataHoraFim, Calendar.DATE);

		// ATENÇÃO: Avança o dia inicial, pois se a data inicial é do final de
		// semana seus valores devem ser considerados
		cDataAtualIntervalo.add(Calendar.DAY_OF_YEAR, 1);

		// Acumulador com os minutos que serão descontados dos finais de semana
		int descontoMinutosFinaisSemana = 0;

		// Percorre intervalo
		while (cDataAtualIntervalo.getTime().before(dataLimiteIntervalo)) {

			final int diaSemana = cDataAtualIntervalo.get(Calendar.DAY_OF_WEEK);
			if (diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY) {
				descontoMinutosFinaisSemana += MINUTOS_24_HORAS;
			}

			// Avança 1 dia
			cDataAtualIntervalo.add(Calendar.DAY_OF_YEAR, 1);
		}

		// Calcula o intervalo em minutos entre as datas e subtrai o desconto
		// dos minutos em finais de semana
		final int minutosTotais = Math.round(Minutes.minutesBetween(new DateTime(dataHoraInicio), new DateTime(dataHoraFim)).getMinutes()) - descontoMinutosFinaisSemana;

		// Converte os minutos totais em horas através da divisão
		final int horas = minutosTotais / 60; // Horas

		// Converte os minutos totais em minutos através do resto da divisão
		final int minutos = minutosTotais % 60; // Minutos/Resto

		// Instancia retorno
		NumeroHorasUteisVO retorno = new NumeroHorasUteisVO();
		retorno.setHoras(horas);
		retorno.setMinutos(minutos);

		return retorno;
	}

	/**
	 * Calcula média
	 * 
	 * @param horas
	 * @param minutos
	 * @param totalItens
	 * @return
	 */
	private String calcularMediaTotal(final int horas, final int minutos, int totalItens) {
		double minutosTotais = (horas * 60) + minutos;
		double minuto = minutosTotais / totalItens;
		double inteira = minuto / 60;
		double resto = Math.round(minuto % 60);
		return (int) inteira + ":" + String.format("%02d", (int) resto);
	}

}