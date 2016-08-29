package br.gov.mec.aghu.business.jobs.manual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.exames.contratualizacao.business.IContratualizacaoFacade;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;

public class ProcessadorArquivosContratualizacaoJob extends AghuJob {
	
	public static final String DETALHES = "DETALHES";
	public static final String NOME_ARQUIVO = "NOME_ARQUIVO";
	public static final String NOME_ARQUIVO_ORIGINAL = "NOME_ARQUIVO_ORIGINAL";
	public static final String HEADER_INTEGRACAO = "HEADER_INTEGRACAO";
	public static final String CAMINHO_ABSOLUTO = "CAMINHO_ABSOLUTO";
	public static final String NOME_MICROCOMPUTADOR = "NOME_MICROCOMPUTADOR";
	
	
	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosContratualizacaoJob.class);
	
	private IContratualizacaoFacade contratualizacaoFacade = ServiceLocator.getBean(IContratualizacaoFacade.class, "aghu-exames");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		String nomeProcessoQuartz = this.getNomeProcessoQuartz(jobExecutionContext); 
		LOG.info("Executando processo de negocio: " + nomeProcessoQuartz);
		
		RapServidores servidorLogado = this.getServidorAgendador(jobExecutionContext);
		
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		Detalhes detalhes = (Detalhes) map.get(ProcessadorArquivosContratualizacaoJob.DETALHES);
		String nomeArquivo = (String) map.get(ProcessadorArquivosContratualizacaoJob.NOME_ARQUIVO);
		String nomeArquivoOriginal = (String) map.get(ProcessadorArquivosContratualizacaoJob.NOME_ARQUIVO_ORIGINAL);
		Header headerIntegracao = (Header) map.get(ProcessadorArquivosContratualizacaoJob.HEADER_INTEGRACAO);
		String caminhoAbsoluto = (String) map.get(ProcessadorArquivosContratualizacaoJob.CAMINHO_ABSOLUTO);
		String nomeMicrocomputador = (String) map.get(ProcessadorArquivosContratualizacaoJob.NOME_MICROCOMPUTADOR);
		
		
		this.contratualizacaoFacade.processar(detalhes, nomeArquivo, nomeArquivoOriginal, headerIntegracao, caminhoAbsoluto, servidorLogado, nomeMicrocomputador);
		
	}
	
}
