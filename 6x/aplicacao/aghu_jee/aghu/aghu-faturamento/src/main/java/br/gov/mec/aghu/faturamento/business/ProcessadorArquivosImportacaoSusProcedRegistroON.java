package br.gov.mec.aghu.faturamento.business;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

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
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusProcedRegistroON  extends BaseBMTBusiness {
	

	private static final long serialVersionUID = -5611858458159466681L;

	@EJB
	private ProcesArqImportSusRegistroON procesArqImportSusRegistroON;

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFaturamentoFacade iFaturamentoFacade;
	
	@EJB
	private ProcesArqImportSusRegistroProcedON procesArqImportSusRegistroProcedON;

	protected static final int QT_MINIMA_ATUALIZAR_ARQUIVO = 500;

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusProcedRegistroON.class);	
	
	protected enum ProcessadorArquivosImportacaoSusProcedRegistroONExceptionCode implements BusinessExceptionCode {
		ERRO_ARQUIVO_REGISTRO_NAO_ENCONTRADO, ERRO_ARQUIVO_PROCEDIMENTO_REGISTRO_NAO_ENCONTRADO
	}

	
	public void atualizarInstrumentoRegistro(final ControleProcessadorArquivosImportacaoSus controle) {
		
		controle.iniciarLogRetorno();
		controle.setPartial(0);
		
		AghArquivoProcessamento arquivoImportacaoRegistro = null;
		AghArquivoProcessamento arquivoImportacaoProcedRegistro = null;
		try { 
		    
			controle.setInicio(new Date());
			
			final String sgFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			arquivoImportacaoRegistro = aberturaOKRegistro(sgFaturamento);
			arquivoImportacaoProcedRegistro = aberturaOKProcedRegistro(sgFaturamento);
			
			if(arquivoImportacaoRegistro != null && arquivoImportacaoProcedRegistro != null){
				arquivoImportacaoRegistro.setDthrInicioProcessamento(controle.getInicio());
				arquivoImportacaoProcedRegistro.setDthrInicioProcessamento(controle.getInicio());

				String msg = "Iniciando execução de atualizar instrumento de registro e procedimento_registro em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
				controle.gravarLog(msg);
				controle.getLogRetorno().append(msg);
				util.atualizarArquivo(arquivoImportacaoRegistro, controle.getInicio(), null, 100, 0, null, controle);
				util.atualizarArquivo(arquivoImportacaoProcedRegistro, controle.getInicio(), null, 100, 0, null, controle);
				controle.getLogRetorno().setLength(0);
				
				final Map<String, FatTabRegistroVO> tabRegistro = this.procesArqImportSusRegistroON.carregaRegistro(sgFaturamento, controle);
				this.procesArqImportSusRegistroProcedON.carregaRegistroProced(sgFaturamento, tabRegistro, controle);
				
				util.atualizarArquivo(arquivoImportacaoRegistro, controle.getInicio(), null, 100, 0, null, controle);
				util.atualizarArquivo(arquivoImportacaoProcedRegistro, controle.getInicio(), null, 100, 0, null, controle);
				
				controle.getLogRetorno().setLength(0);
					
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   		    .append("Tabela FAT_PROCEDIMENTO_REGISTRO lida e atualizada com sucesso.");
	
				controle.gravarLog("Finalizada em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
	   			                           .append("Finalizada em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

				util.atualizarArquivo(arquivoImportacaoRegistro,  controle.getInicio(), null, 100, 0, null, controle);
				util.atualizarArquivo(arquivoImportacaoProcedRegistro,  controle.getInicio(), null, 100, 0, null, controle);
				
				controle.getLogRetorno().setLength(0);
				
			} else {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   			   .append("Erro na atualização da Tabela fat_procedimentos_registro. Arquivo Importação não encontrado.");
				controle.gravarLog("Erro na atualização da Tabela fat_procedimentos_registro. Arquivo Importação não encontrado.");
			}
			
		} catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Erro inesperado " + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			final Date fim = new Date();
			if (arquivoImportacaoRegistro != null) {
				util.atualizarArquivo(arquivoImportacaoRegistro, fim, 100, 100, 0, fim, controle);
				controle.getLogRetorno().setLength(0);
			}
			if (arquivoImportacaoProcedRegistro != null) {
				util.atualizarArquivo(arquivoImportacaoProcedRegistro, fim, 100, 100, 0, fim, controle);
				controle.getLogRetorno().setLength(0);
			}
		}
	}
	/** Forms: abertura_OK  */
	private AghArquivoProcessamento aberturaOKProcedRegistro( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO).toLowerCase(); 
		//final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO).toLowerCase(); 
		AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivo);
		return aghArquivo;
	}
    private AghArquivoProcessamento aberturaOKRegistro( final String sgFaturamento) throws ApplicationBusinessException{
    	final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_REGISTRO).toLowerCase();
		//final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_REGISTRO).toLowerCase(); 
		AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivo);
		return aghArquivo;
	}
	
	protected ProcesArqImportSusRegistroON getProcesArqImportSusRegistroON() {
		return procesArqImportSusRegistroON;
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
	protected ProcesArqImportSusRegistroProcedON getProcesArqImportSusRegistroProcedON() {
		return procesArqImportSusRegistroProcedON;
	}
	@Override
	protected Log getLogger() {
		return LOG;
	}
}