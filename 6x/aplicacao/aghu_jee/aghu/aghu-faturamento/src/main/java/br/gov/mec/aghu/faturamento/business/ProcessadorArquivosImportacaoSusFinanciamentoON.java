package br.gov.mec.aghu.faturamento.business;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.AghArquivoProcessamento;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusFinanciamentoON  extends BaseBMTBusiness {
	

	private static final long serialVersionUID = -5611858458159466681L;

	@EJB
	private ProcesArqImportSusFinanciamentoON procesArqImportSusFinanciamentoON;

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFaturamentoFacade iFaturamentoFacade;
	
	@EJB
	private ProcesArqImportSusProcedHospitalarON procesArqImportSusProcedHospitalarON;

	protected static final int QT_MINIMA_ATUALIZAR_ARQUIVO = 500;

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusFinanciamentoON.class);	
	
	protected enum ProcessadorArquivosImportacaoSusFinanciamentoONExceptionCode implements BusinessExceptionCode {
		ERRO_ARQUIVO_FINANCIAMENTO_NAO_ENCONTRADO, ERRO_ARQUIVO_PROCEDIMENTO_REGISTRO_NAO_ENCONTRADO
	}

	
	public void atualizarFinanciamento(final ControleProcessadorArquivosImportacaoSus controle) {
		final StringBuilder logRetornoArquivoImportacao = new StringBuilder(200);
		String msg = "";
		AghArquivoProcessamento arquivoImportacaoFinanciamento = null;
		AghArquivoProcessamento arquivoImportacaoItensProcedimentoHospitalar = null;
		
		controle.iniciarLogRetorno();
		controle.setPartial(0);
		
		try { 
		    
			controle.setInicio(new Date());
			
			final String sgFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			arquivoImportacaoFinanciamento = aberturaOKFinanciamento(sgFaturamento);
			
			if(arquivoImportacaoFinanciamento != null){
				
					msg = "Iniciando execução de atualizar fat_financiamento em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
					arquivoImportacaoFinanciamento.setDthrInicioProcessamento(controle.getInicio());
			        this.atualizarLog(arquivoImportacaoFinanciamento, 
							          controle.getInicio(),
								          null,
								          100,
								          null,
								          controle,
								          msg,
								          logRetornoArquivoImportacao);
				    
			        //Processar Financiamento
			        procesArqImportSusFinanciamentoON.carregarFinanciamento(sgFaturamento, controle);

			        this.atualizarLog(arquivoImportacaoFinanciamento, 
					          controle.getInicio(),
					          null,
					          100,
					          null,
					          controle,
					          msg,
					          logRetornoArquivoImportacao);
				    msg = ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Tabela fat_financiamento lida e atualizada com sucesso.";
			        msg = msg.concat("Finalizada em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			        this.atualizarLog(arquivoImportacaoFinanciamento, 
   			                          controle.getInicio(),
			                          null,
			                          100,
			                          null,
			                          controle,
			                          msg,
			                          logRetornoArquivoImportacao);
			}else {
				logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
	   			                           .append("Erro na atualização da Tabela fat_financiamento. Arquivo Importação não encontrado.");
                controle.gravarLog("Erro na atualização da Tabela fat_financimento. Arquivo Importação não encontrado.");
            }
			
			arquivoImportacaoItensProcedimentoHospitalar = aberturaOKItensProcedHospitalar(sgFaturamento);
            if(arquivoImportacaoFinanciamento != null){
            	msg = "Iniciando execução de atualizar fat_procedimento para financiamento relacionado em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
				arquivoImportacaoFinanciamento.setDthrInicioProcessamento(controle.getInicio());
		        this.atualizarLog(arquivoImportacaoFinanciamento, 
						          controle.getInicio(),
							          null,
							          100,
							          null,
							          controle,
							          msg,
							          logRetornoArquivoImportacao);
		        procesArqImportSusProcedHospitalarON.carregarProcedimento(sgFaturamento, controle);
		        this.atualizarLog(arquivoImportacaoFinanciamento, 
				          controle.getInicio(),
				          null,
				          100,
				          null,
				          controle,
				          msg,
				          logRetornoArquivoImportacao);
			    msg = ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Tabela fat_procedimento lida e atualizada com sucesso para o tipo de financiamento relacionado.";
		        msg = msg.concat("Finalizada em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		        this.atualizarLog(arquivoImportacaoFinanciamento, 
			                          controle.getInicio(),
		                          null,
		                          100,
		                          null,
		                          controle,
		                          msg,
		                          logRetornoArquivoImportacao);
			}else {
				logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
	   			                           .append("Erro na atualização da Tabela fat_procedimento. Arquivo Importação não encontrado.");
                controle.gravarLog("Erro na atualização da Tabela fat_procedimento. Arquivo Importação não encontrado.");
            }
		} catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Erro inesperado " + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			final Date fim = new Date();
			if (arquivoImportacaoFinanciamento != null) {
				this.atualizarLog(arquivoImportacaoFinanciamento, 
						  fim,
				          100,
				          100,
				          fim,
				          controle,
				          msg,
				          logRetornoArquivoImportacao);
			}
			if (arquivoImportacaoItensProcedimentoHospitalar != null) {
				this.atualizarLog(arquivoImportacaoFinanciamento, 
						  fim,
				          100,
				          100,
				          fim,
				          controle,
				          msg,
				          logRetornoArquivoImportacao);
			}
		}
	}
	/** Atualizar log */
	private void atualizarLog(AghArquivoProcessamento arquivo,Date date,final Integer percent, final Integer peso, Date fimData,ControleProcessadorArquivosImportacaoSus controle, String msg, StringBuilder logRetorno){
        controle.gravarLog(msg);
        logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);
		controle.setLogRetorno(logRetorno);
		util.atualizarArquivo(arquivo, date, percent, peso, 0, fimData, controle);
		logRetorno.setLength(0);
		
	}
	/** Forms: abertura_OK  */
	private AghArquivoProcessamento aberturaOKItensProcedHospitalar( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase(); 
		AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivo);
		return aghArquivo;
	}
    private AghArquivoProcessamento aberturaOKFinanciamento( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_FINANCIAMENTO).toLowerCase(); 
		AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivo);
		return aghArquivo;
	}
	
	
	protected ProcesArqImportSusFinanciamentoON getProcesArqImportSusFinanciamentoON() {
		return procesArqImportSusFinanciamentoON;
	}
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	protected IFaturamentoFacade getiFaturamentoFacade() {
		return iFaturamentoFacade;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
}