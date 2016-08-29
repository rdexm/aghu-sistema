package br.gov.mec.aghu.business.jobs.manual;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.ProcessamentoCustoFinalizadoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

public class ProcessarCustoDiarioAutomatizadoJob extends AghuJob {

	private static final Log LOG = LogFactory.getLog(ProcessarCustoDiarioAutomatizadoJob.class);
	
	public static final String EXECUTAR = "EXECUTAR";
	public static final String DESCRICAO = "DESCRICAO";
	
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade = ServiceLocator.getBean(ICustosSigProcessamentoFacade.class, "aghu-sig");
	
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job,
			JobExecutionContext jobExecutionContext)
			throws ApplicationBusinessException {
		String nomeProcessoQuartz = getNomeProcessoQuartz(jobExecutionContext);
		LOG.info("Executando processo de negocio " + " ["+ nomeProcessoQuartz + "]"+ DateFormatUtil.formataTimeStamp(new Date()) + ";");
		
		LOG.info("Iniciando processamento de custo");
		List<ProcessamentoCustoFinalizadoVO> processamentosFinalizados = custosSigProcessamentoFacade.executarProcessamentoCustoAutomatizado();
		
		//Para os processamentos finalizados adiciona na central de pendência e envia emails
		if(processamentosFinalizados!= null && !processamentosFinalizados.isEmpty()){
			LOG.info("Criando pendências após executar processamento");
			for(ProcessamentoCustoFinalizadoVO vo :processamentosFinalizados){
				custosSigProcessamentoFacade.adicionarPendenciaProcessamentoFinalizado(vo);
			}
		}
	}

}
