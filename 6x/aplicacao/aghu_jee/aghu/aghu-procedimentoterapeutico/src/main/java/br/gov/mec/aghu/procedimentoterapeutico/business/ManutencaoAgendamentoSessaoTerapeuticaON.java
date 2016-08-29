package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TransferirDiaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ON de #44228 – Realizar Manutenção deAgendamento de Sessão Terapêutica
 * 
 * @author aghu
 */
@Stateless
public class ManutencaoAgendamentoSessaoTerapeuticaON extends BaseBusiness {

	private static final long serialVersionUID = -3246934368277983507L;

	private static final Log LOG = LogFactory.getLog(ManutencaoAgendamentoSessaoTerapeuticaON.class);

	private static final String LABEL_INTERVALO_PERMITIDO = "Intervalo permitido ({0} a {1})";

	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * Ativa o botão Transferir Dia
	 * 
	 * @param dias
	 * @return
	 */
	public boolean ativarTransferirDia(List<DiasAgendadosVO> dias) {
		if (dias.size() == 1) { // Quando a lista possuir 1 registro
			return false;
		} else if (dias.size() > 1 && DateUtil.diffInDays((DateUtil.truncaData(dias.get(0).getDataInicio())), DateUtil.truncaData(dias.get(dias.size() - 1).getDataFim())) > 1) {
			// Quando a lista possuir 2 registros e a diferença entre dias é 1
			return true;
		}
		return false;
	}

	/**
	 * ON1 e ON2: Será permitido informar somente datas nas lacunas ente as datas do ciclo
	 * 
	 * @param dias
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public TransferirDiaVO obterLabelRestricaoDatas(List<DiasAgendadosVO> dias) throws ApplicationBusinessException {
		TransferirDiaVO retorno = new TransferirDiaVO();
		AghParametros parametroDiasTransferenciaAgenda = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_DIAS_TRANSFERENCIA_AGENDA);
		final int diasTransferenciaAgenda = parametroDiasTransferenciaAgenda.getVlrNumerico().intValue();
		String label = "";
		if (!dias.isEmpty() && dias.size() >= 2) {
			final Date dataFinal = interarCalendario(dias.get(dias.size() - 1).getDataInicio(), diasTransferenciaAgenda); // Avança 1 dia
			final Date dataInicial = interarCalendario(dias.get(0).getDataInicio(), -diasTransferenciaAgenda); // Retorna 1 dia
			
			retorno.setRestricaoInicio(DateUtil.truncaData(dataInicial));
			retorno.setRestricaoFim(DateUtil.truncaData(dataFinal));

			final String iniIntervalo = DateUtil.obterDataFormatada(dataInicial, DateConstants.DATE_PATTERN_DDMMYYYY);
			final String fimIntervalo = DateUtil.obterDataFormatada(dataFinal, DateConstants.DATE_PATTERN_DDMMYYYY);

			// O Bundle não está funcionando!
			label = LABEL_INTERVALO_PERMITIDO.replace("{0}", iniIntervalo).replace("{1}", fimIntervalo);
		}
		retorno.setRestricaoDatas(label);
		return retorno;
	}
	
	/**
	 * Intera em uma data
	 * 
	 * @param data
	 * @param dias
	 * @return
	 */
	private Date interarCalendario(Date data, int dias) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_YEAR, dias);
		return c.getTime();
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

}
