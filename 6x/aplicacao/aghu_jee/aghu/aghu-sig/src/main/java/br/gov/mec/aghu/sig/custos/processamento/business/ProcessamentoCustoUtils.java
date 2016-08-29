package br.gov.mec.aghu.sig.custos.processamento.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.sig.custos.business.SigNoEnumOperationException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável a dar suporte as classes de processamento, fornecendo vários metodos genéricos.
 * 
 * @author rmalvezzi
 */
@Stateless
public class ProcessamentoCustoUtils extends ProcessamentoCustoDependenciasEtapaUtils {

	private static final Log LOG = LogFactory.getLog(ProcessamentoCustoUtils.class);
	
	/**
	 * Lança uma exceção se é para abortar o processamento.
	 * 
	 * @author rmalvezzi
	 * @param pararProcessamento
	 *            Sim se é para abortar.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se for para abortar o processamento.
	 */
	public static void abortarProcessamento(Boolean pararProcessamento) throws ApplicationBusinessException {
		if (pararProcessamento != null && pararProcessamento) {
			LOG.debug("PMSigCustos: ABORTAR PROCESSAMENTO");
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ABORTA_PROCESSAMENTO);
		} else {
			LOG.debug("PMSigCustos: CONTINUAR PROCESSAMENTO");
		}
	}

	/**
	 * Loga a diferença entre a data atual e a data passada por parametro.
	 * 
	 * @author rmalvezzi
	 * @param tempoInicio
	 *            Tempo de início em milisegundos.
	 */
	public static void calcularLogarTempoExecucao(long tempoInicio) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		LOG.debug("PMSigCustos: Tempo processamento = " + df.format(new Date(System.currentTimeMillis() - tempoInicio)));
	}

	/**
	 * Este método calcula a diferença entre data inicial e final e retorna o
	 * valor em dias
	 * 
	 * @author rogeriovieira
	 * @param dataInicial
	 *            A data inicial.
	 * @param dataFinal
	 *            A data final.
	 * @return A diferença em dias entre as datas informadas por parametros.
	 */
	public static Double calcularEmDiasDiferencaEntreDatas(Date dataInicial, Date dataFinal) {
		double result = 0;
		long diferenca = dataFinal.getTime() - dataInicial.getTime();
		double diferencaEmDias = (diferenca / 1000) / 60 / 60 / 24; // resultado
																	// é
																	// diferença
																	// entre as
																	// datas em
																	// dias
		long horasRestantes = (diferenca / 1000) / 60 / 60 % 24; // calcula as
																	// horas
																	// restantes
		result = diferencaEmDias + (horasRestantes / 24d); // transforma as
															// horas restantes
															// em fração de dias
		return result;
	}

	

	/**
	 * Método para verificar se um lista está ou não vazia.
	 * 
	 * @author rmalvezzi
	 * @param lista
	 *            A lista a ser analisada.
	 * @return True se a lista não está vazia e False se a lista é nula ou
	 *         vazia.
	 */
	public static boolean verificarListaNaoVazia(List<?> lista) {
		return lista != null && !lista.isEmpty();
	}

	/**
	 * Cria um valor BigDecimal a partir de um campo Double definindo o scale
	 * para 4, pois é o utilizado nos campos do processamento
	 * 
	 * @author rogeriovieira
	 * @param valor
	 *            valor double
	 * @return BigDecimal correspondente ao valor
	 */
	public static BigDecimal criarBigDecimal(Double valor) {
		return new BigDecimal(valor.toString()).setScale(4, RoundingMode.HALF_UP);
	}

	/**
	 * Divide o primeiro valor pelo segundo usando o BigDecimal
	 * 
	 * @author rogeriovieira
	 * @param valor1
	 * @param valor2
	 * @return Resultado da divisão
	 */
	public static BigDecimal dividir(BigDecimal valor1, BigDecimal valor2) {
		return valor1.divide(valor2, 4, RoundingMode.HALF_UP);
	}

	/**
	 * Define qual o passo a ser utilizado dependendo do parametro passado.
	 * 
	 * @author rmalvezzi
	 * @param tipoObjetoCusto
	 *            Tipo do OC.
	 * @param seqOpcaoAssistencial
	 *            Seq do passo assitencial.
	 * @param seqOpcaoApoio
	 *            Seq do passo de apoio.
	 * @return O parametro seqOpcaoAssistencial se o Tipo do OC é AS ou
	 *         seqOpcaoApoio se o Tipo for AP.
	 */
	public static Integer definirPassoUtilizado(DominioTipoObjetoCusto tipoObjetoCusto, Integer seqOpcaoAssistencial, Integer seqOpcaoApoio) {
		if (tipoObjetoCusto != null) {
			switch (tipoObjetoCusto) {
			case AS:
				return seqOpcaoAssistencial;
			case AP:
				return seqOpcaoApoio;
			default:
				throw new SigNoEnumOperationException();
			}
		}
		return null;
	}

	/**
	 * Obtem a data, que representa a competencia, do processamento do mês
	 * corrente - 1.
	 * 
	 * @author rmalvezzi
	 * @return Data no primeiro dia do mês, do mês -1 e do ano corrente.
	 */
	public static Date obterCompetenciaProcessamento() {
		Calendar dtCompetencia = Calendar.getInstance();
		dtCompetencia.set(Calendar.DAY_OF_MONTH, 1);
		dtCompetencia.add(Calendar.MONTH, -1);
		dtCompetencia.set(Calendar.HOUR_OF_DAY, 0);
		dtCompetencia.set(Calendar.MINUTE, 0);
		dtCompetencia.set(Calendar.SECOND, 0);
		dtCompetencia.set(Calendar.MILLISECOND, 0);
		return dtCompetencia.getTime();
	}

	/**
	 * Cria Data para a competencia do mês atual.
	 * 
	 * @author rmalvezzi
	 * @return				Data no primeiro dia do mes e hora, minuto e segundo zerados.
	 */
	public static Date obterCompetenciaMesAtual() {
		Calendar dtCompetencia = Calendar.getInstance();
		dtCompetencia.setTime(new Date());
		dtCompetencia.set(Calendar.DAY_OF_MONTH, 1);
		dtCompetencia.set(Calendar.HOUR_OF_DAY, 0);
		dtCompetencia.set(Calendar.MINUTE, 0);
		dtCompetencia.set(Calendar.SECOND, 0);
		dtCompetencia.set(Calendar.MILLISECOND, 0);
		return dtCompetencia.getTime();
	}
}