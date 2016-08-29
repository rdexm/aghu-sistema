package br.gov.mec.aghu.business.jobs.auto;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SolicitacaoCompraAlmoxarifadoJob extends AghuJob {
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoCompraAlmoxarifadoJob.class);
	
	public static final String EXECUTAR = "EXECUTAR";
	public static final String DESCRICAO = "DESCRICAO";
	
	ISolicitacaoComprasFacade solicitacaoComprasFacade = ServiceLocator.getBean(ISolicitacaoComprasFacade.class, "aghu-compras");
	IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String nomeProcessoQuartz = getNomeProcessoQuartz(jobExecutionContext);
		LOG.info(String.format("Iniciando processo de geração automática de SC por almoxarifado. Nome do processo: %s", nomeProcessoQuartz));

		Date analise = new Date();
		try {
			solicitacaoComprasFacade.gerarSolicitacaoCompraAlmox(analise);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}
		atualizaParametro(analise);
		
		// Job Concluído.
		LOG.info(String.format("Processo %s concluído", nomeProcessoQuartz));
	}

	/**
	 * Atualiza parâmetro responsável pela hora do último agendamento de geração
	 * automática de SC.
	 * 
	 * @param analise Data de análise.
	 * @throws AGHUNegocioException
	 */
	private void atualizaParametro(Date analise) throws ApplicationBusinessException {
		String data = DateUtil.obterDataFormatadaHoraMinutoSegundo(analise);
		AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORA_AGENDA_SC);
		param.setVlrTexto(getMessage("P_HORA_AGENDA_SC_MENSAGEM", data));
		parametroFacade.setAghpParametro(param);
	}
}
