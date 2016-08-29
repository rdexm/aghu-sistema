package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe contendo funções utilitárias para regras do FORMS de #22460: Agendar procedimentos eletivo, urgência ou emergência Classe criada para suprir as violações de PMD (tamanho
 * de classe) que ocorriam na classe AgendaProcedimentosON
 * 
 * @author aghu
 * 
 */

@Stateless
public class AgendaProcedimentosFuncoesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosFuncoesON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	/**
	 * 
	 */
	private static final long serialVersionUID = -3412120936520932569L;

	/**
	 * Concatena uma data com um horário informado
	 * 
	 * @param data
	 * @param horario
	 * @return
	 */
	public Date getConcatenacaoDataHorario(final Date data, final Date horario) {
		Calendar resultado = Calendar.getInstance(new Locale("pt", "BR"));
		resultado.setTime(data); // Seta o valor do parâmetro data
		if (horario != null) {
			Calendar calendarHoraMinuto = Calendar.getInstance();
			calendarHoraMinuto.setTime(horario);

			// Seta os valores do parâmetro dthrPrevInicio
			resultado.set(Calendar.HOUR_OF_DAY, calendarHoraMinuto.get(Calendar.HOUR_OF_DAY));
			resultado.set(Calendar.MINUTE, calendarHoraMinuto.get(Calendar.MINUTE));
			resultado.set(Calendar.SECOND, calendarHoraMinuto.get(Calendar.SECOND));
			resultado.set(Calendar.MILLISECOND, calendarHoraMinuto.get(Calendar.MILLISECOND));
		} else {

			// Seta os valores da hora com ZERO
			resultado.set(Calendar.HOUR_OF_DAY, 0);
			resultado.set(Calendar.MINUTE, 0);
			resultado.set(Calendar.SECOND, 0);
			resultado.set(Calendar.MILLISECOND, 0);
		}

		return resultado.getTime();
	}

	/**
	 * Obtem o valor da data e hora da previsão do FINAL DA CIRURGIA
	 * 
	 * @param data
	 * @param tempoPrevistoHoras
	 * @param tempoPrevistoMinutos
	 * @return
	 */
	public Date obterDataHoraPrevisaoFim(final Date data, Short tempoPrevistoHoras, Byte tempoPrevistoMinutos) {

		Calendar resultado = Calendar.getInstance();
		resultado.setTime(data);

		tempoPrevistoHoras = (Short) CoreUtil.nvl(tempoPrevistoHoras, Short.valueOf("0"));
		tempoPrevistoMinutos = (Byte) CoreUtil.nvl(tempoPrevistoMinutos, Byte.valueOf("0"));

		resultado.add(Calendar.HOUR_OF_DAY, tempoPrevistoHoras);
		resultado.add(Calendar.MINUTE, tempoPrevistoMinutos);

		return resultado.getTime();
	}

	/**
	 * Calcula o valor do tempo mínimo
	 * 
	 * @param valorTempoMinimo
	 * @return
	 */
	public Integer getValorTempoMinimo(Short valorTempoMinimo) {
		String lpad = StringUtil.adicionaZerosAEsquerda(valorTempoMinimo, 4);
		String substr1 = lpad.substring(0, 2);
		String substr2 = lpad.substring(2, 4);
		Short valor1 = Short.valueOf(substr1);
		Short valor2 = Short.valueOf(substr2);
		return valor1 * 60 + valor2;
	}

	/**
	 * Calcula o valor do tempo mínimo em horas
	 * 
	 * @param valorTempoMinimo
	 * @return
	 */
	public Short getValorTempoMinimoHoras(Short valorTempoMinimo) {
		final Integer valor = this.getValorTempoMinimo(valorTempoMinimo);
		final double divisao = valor / 60;
		BigDecimal trunc = NumberUtil.truncate(new BigDecimal(divisao), 0);
		String lpad = StringUtil.adicionaZerosAEsquerda(trunc, 2);
		return Short.valueOf(lpad);
	}

	/**
	 * Calcula o valor do tempo mínimo dos minutos
	 * 
	 * @param valorTempoMinimo
	 * @return
	 */
	public Byte getValorTempoMinimoMinuto(Short valorTempoMinimo) {
		final Integer valor = this.getValorTempoMinimo(valorTempoMinimo);
		final double restoDivisao = valor % 60;
		BigDecimal trunc = NumberUtil.truncate(new BigDecimal(restoDivisao), 0);
		String lpad = StringUtil.adicionaZerosAEsquerda(trunc, 2);
		return Byte.valueOf(lpad);
	}

	/**
	 * Resgata a mensagem de paciente com cirurgia já agendada
	 * 
	 * @param cirurgiaAgendada
	 * @return
	 */
	public String getMensagemPacienteComCirurgiaJaAgendada(MbcCirurgias cirurgiaAgendada) {

		String situacao = null;

		switch (cirurgiaAgendada.getSituacao()) {
		case AGND:
			situacao = "possui Cirurgia Agendada";
			break;
		case CANC:
			situacao = "possui Cirurgia Cancelada";
			break;
		case RZDA:
			situacao = "possui Cirurgia Realizada";
			break;
		case TRAN:
			situacao = "se encontra em Trans-Operatório";
			break;
		case PREP:
			situacao = "se encontra em Sala de Preparo";
			break;
		case CHAM:
			situacao = "foi chamado para a Cirurgia";
			break;
		default:
			break;
		}

		String horario = DateUtil.obterDataFormatada(cirurgiaAgendada.getDataPrevisaoInicio(), DateConstants.DATE_PATTERN_HORA_MINUTO);
		return "Paciente já " + situacao + " às " + horario + " na sala " + cirurgiaAgendada.getSalaCirurgica().getId().getSeqp() + ". Deseja Continuar?";
	}

	/**
	 * Converte o tempo previsto da cirurgia em horas
	 * 
	 * @param tempoPrevistoHoras
	 * @param tempoPrevistoMinutos
	 * @return
	 */
	public Date converterTempoPrevistoHoras(Short tempoPrevistoHoras, Byte tempoPrevistoMinutos) {
		if(tempoPrevistoHoras == null){
			tempoPrevistoHoras = 0;
		}
		if(tempoPrevistoMinutos == null){
			tempoPrevistoMinutos = 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tempoPrevistoHoras));
		calendar.set(Calendar.MINUTE, Integer.valueOf(tempoPrevistoMinutos));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
