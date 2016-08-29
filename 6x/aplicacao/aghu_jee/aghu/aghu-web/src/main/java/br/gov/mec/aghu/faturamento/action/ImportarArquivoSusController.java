package br.gov.mec.aghu.faturamento.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.util.ZipUtil;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;


public class ImportarArquivoSusController extends ActionController {

	private static final long serialVersionUID = -6633877920713795731L;

	private static final int MAX_TAM_LOG = 5000;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private String nomeZip = "";
	private String nomeArquivo;
	private List<String> lista;
	private int uploadsAvailable = 1;
	private boolean importacaoGeral = false;
	private List<AghArquivoProcessamento> arquivosProcessando;

	private Boolean importando = false;
	private String logImportacao; 
	private String parametroFaturamento = null;
	private DominioSimNao parametroValidaArquivo = DominioSimNao.S;
	private int arquivosSendoProcessados;
	private int totalProcessado;
	private int totalLog = 0;

	private Integer percentualProgressBar;
	
	private String file;

	private boolean gerouArquivo;

	private enum ImportarArquivoSusControllerExceptionCode {
		TITLE_IMPORTAR_ARQUIVO_SIGTAB;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 
		if (parametroFaturamento == null) {
			nomeZip = "";
			importando = false;
			logImportacao = getMensagem(ImportarArquivoSusControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SIGTAB);
			uploadsAvailable = 1;
			arquivosSendoProcessados = 0;
			totalProcessado = 0;
			percentualProgressBar = 0;
			setGerouArquivo(false);
	
			try {
				parametroFaturamento = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO).getVlrTexto();
			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
			try {
				parametroValidaArquivo = DominioSimNao.valueOf(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VALIDA_ARQUIVO_SUS).getVlrTexto());
			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}		
		}
	
	}

	public void listener(final FileUploadEvent event) {
		final UploadedFile item = event.getFile();

		try{
			nomeZip = item.getFileName();
			validaCarregamentoArquivo(item);
		}catch(Exception e){
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	/**
	 * @param item
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void validaCarregamentoArquivo(final UploadedFile item)
			throws FileNotFoundException, IOException {
		if(parametroValidaArquivo.equals(DominioSimNao.S)) {
			if (validarArquivo()){
			carregarArquivo(item);
			}
		}
		if(parametroValidaArquivo.equals(DominioSimNao.N)){
			carregarArquivo(item);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALIDACAO_ARQUIVO_SUS");
		}
	}

	private void carregarArquivo(final UploadedFile item) throws FileNotFoundException, IOException {
		uploadsAvailable--;
		logImportacao = getMensagem(ImportarArquivoSusControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SIGTAB);
		lista = ZipUtil.unZip(item.getInputstream());
	}

	public boolean validarArquivo() {
		try {
			faturamentoFacade.verificaNomeArquivoZip(nomeZip);
			return true;
		} catch (final ApplicationBusinessException e) {
			clearUploadData(false);
			apresentarExcecaoNegocio(e);
		} catch (final Exception e) {
			clearUploadData(false);
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
		return false;
	}

	public void clearUploadData(final boolean limpaLog) {

		setUploadsAvailable(1);
		setGerouArquivo(false);
		if (limpaLog) {
			logImportacao = getMensagem(ImportarArquivoSusControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SIGTAB);
			setPercentualProgressBar(0);
		}
	}

	public void importarGeral() {
		atualizarGeral();
	}

	@SuppressWarnings("unchecked")
	private void atualizarGeral() {
		try {
			clearComponents();
			importacaoGeral = true;
			final Map<String, Object> retorno = faturamentoFacade.atualizarGeral(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_GERAL + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void atualizarFinanciamento() {
		try {
			clearComponents();
			//importacaoGeral = true;
			final Map<String, Object> retorno = faturamentoFacade.atualizarFinanciamento(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_GERAL + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void atualizarProcedimento() {
		try {
			clearComponents();
			final Map<String, Object> retorno = faturamentoFacade.atualizarItensProcedHosp(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj !=  null) {
				file = (String) obj;
				nomeArquivo = IFaturamentoFacade.NOME_ARQUIVO_LOG_PROCEDIMENTOS + DateUtil.obterDataFormatada(new Date(),DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void atualizarCboProcedimento() {
		try {
			clearComponents();
			final Map<String, Object> retorno = faturamentoFacade.atualizarCboProcedimento(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_CBO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void importarCidProcedimento() {
		atualizarCidProcedimento();
	}

	@SuppressWarnings("unchecked")
	private void atualizarCidProcedimento() {
		try {
			clearComponents();
			final Map<String, Object> retorno = faturamentoFacade.atualizarCidProcedimentoNovo(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_CID + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void importarCompatibilidade() {
		try {
			clearComponents();
			final Map<String, Object> retorno = faturamentoFacade.atualizarCompatibilidade(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_COMPATIBILIDADE + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void importarServicoClassificacao() {
		try {
			clearComponents();
			final Map<String, Object> retorno = faturamentoFacade.atualizarServicoClassificacao(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_SERVICO_CLASSIFICACAO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
    public void importarInstrumentoRegistro(){
    	this.atualizarInstrumentoRegistro();
    }
    
    @SuppressWarnings("unchecked")
	private void atualizarInstrumentoRegistro(){
    	try {
			clearComponents();
			final Map<String, Object> retorno = faturamentoFacade.atualizarInstrumentoRegistro(lista);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS));
			final Object obj = retorno.get(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS);
			if (obj != null) {
				file = (String) obj;
				nomeArquivo= IFaturamentoFacade.NOME_ARQUIVO_LOG_INSTRUMENTO_REGISTRO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM) + IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG;
			}
			setImportando(true);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
    }
    
	public void baixarLog() {
		if (this.file != null) {
			try {
				super.download(this.file, nomeArquivo, "application/download");
				//faturamentoFacade.downloadedFaturamento(this.file, nomeArquivo, "application/download");
			} catch (final IOException e) {
				apresentarMsgNegocio("ERRO_ABRIR_ARQUIVO_LOG");
			}
		}
	}
	
	private void clearComponents(){
		logImportacao = null;
		setTotalLog(0);
		setPercentualProgressBar(0);
		setGerouArquivo(false);
	}

	public String getLogImportacao() {
		if (getImportando()) {
			boolean continua = false;

			// já sabe os arquivos. Buscar status atual.
			if (getArquivosProcessando() != null) {
				final List<AghArquivoProcessamento> arquivos = aghuFacade.pesquisarArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(parametroFaturamento, obterIds(getArquivosProcessando()));
				obterLog();
				if (processamentoFinalizado(arquivos)) {
					// finalizou! obter arquivos
					setArquivosProcessando(null);
					setPercentualProgressBar(100);
					if (file != null) {
						setGerouArquivo(true);
					}
				} else {
					// atualiza percentual
					atualizarLog(arquivos);
					continua = true;
				}
			}
			// }
			setImportando(continua);
		}
		return logImportacao;
	}

	private void obterLog() {
		String log = aghuFacade.pesquisarLogsPorArquivosIds(obterIds(getArquivosProcessando()), MAX_TAM_LOG);
		if (log.length() > MAX_TAM_LOG) {
			String lineSeparator = System.getProperty("line.separator");
			if (lineSeparator == null) {
				lineSeparator = "\n";
			}
			log = log.substring(log.length() - MAX_TAM_LOG);
			if(log.indexOf(lineSeparator) != -1) {
				log = log.substring(log.indexOf(lineSeparator));
			}
		}
		setLogImportacao(log);
	}

	private List<Integer> obterIds(final List<AghArquivoProcessamento> arquivos) {
		if (arquivos == null || arquivos.isEmpty()) {
			return new ArrayList<Integer>(0);
		}
		final List<Integer> retorno = new ArrayList<Integer>();
		for (final AghArquivoProcessamento arquivo : arquivos) {
			retorno.add(arquivo.getSeq());
		}
		return retorno;
	}

	private void atualizarLog(final List<AghArquivoProcessamento> arquivos) {
		if (arquivos == null || arquivos.isEmpty()) {
			setPercentualProgressBar(0);
		} else {

			totalProcessado = 0;
			//double totalPeso = 0;
//			int nroArquivosJaProcessados = 1;
			int i = 1;
			for (final AghArquivoProcessamento arquivo : arquivos) {
				double peso = 1;
				if(importacaoGeral){
					if(i <= 4){
						//importando grupo, subgrupo, forma organizacao e modalidade
						//peso calculado pela media de tempo da importacao do arquivo
						peso = 0.0025;
					}else if (i == 5){
						//importando procedimento
						//peso calculado pela media de tempo da importacao do arquivo
						peso = 0.27;
					}else if (i == 8){
						//importando cids
						//peso calculado pela media de tempo da importacao do arquivo
						peso = 0.62;
					}else{
						//importando cbo
						//peso calculado pela media de tempo da importacao do arquivo
						peso = 0.05;
					}
				}

				totalProcessado += arquivo.getPercentualProcessado() * peso;
				//totalPeso += peso;

//				if (arquivo.getPercentualProcessado() == 100) {
//					nroArquivosJaProcessados++;
//				}
				
				i++;
			}

			//setPercentualProgressBar(new Double((totalProcessado / totalPeso)).intValue());
			if(totalLog != 100){
				setPercentualProgressBar(totalLog++);
			}
			
		}
	}

	private boolean processamentoFinalizado(final List<AghArquivoProcessamento> arquivos) {
		for (final AghArquivoProcessamento arquivoProcessamento : arquivos) {
			if (arquivoProcessamento.getDthrFimProcessamento() == null) {
				return false;
			}
		}
		return true;
	}

	public void setLogImportacao(final String logImportacao) {
		this.logImportacao = logImportacao;
	}

	public Boolean getImportando() {
		if (importando == null) {
			return false;
		}
		return importando;
	}
	public void setImportando(final Boolean importando) {
		this.importando = importando;
	}

	protected String getMensagem(final ImportarArquivoSusControllerExceptionCode key, final Object... args) {
		String val = super.getBundle().getString(key.toString()); //this.faturamentoFacade.obterMensagemResourceBundle(key.toString());
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				val = val.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}
		return val;
	}

	public long getTimeStamp() {
		return System.currentTimeMillis();
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(final int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public List<String> getLista() {
		return lista;
	}

	public void setLista(final List<String> lista) {
		this.lista = lista;
	}

	public void setArquivosProcessando(final List<AghArquivoProcessamento> arquivosProcessando) {
		this.arquivosProcessando = arquivosProcessando;
	}

	public List<AghArquivoProcessamento> getArquivosProcessando() {
		return arquivosProcessando;
	}

	public int getArquivosSendoProcessados() {
		return arquivosSendoProcessados;
	}

	public void setArquivosSendoProcessados(final int arquivosSendoProcessados) {
		this.arquivosSendoProcessados = arquivosSendoProcessados;
	}

	public int getProcessado() {
		return totalProcessado;
	}

	public void setProcessado(final int processado) {
		this.totalProcessado = processado;
	}

	public Integer getPercentualProgressBar() {
		return percentualProgressBar;
	}

	public void setPercentualProgressBar(final Integer percentualProgressBar) {
		this.percentualProgressBar = percentualProgressBar;
	}

	public void setGerouArquivo(final boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public boolean isGerouArquivo() {
		return gerouArquivo;
	}
	
	public int getTotalLog() {
		return totalLog;
	}

	public void setTotalLog(int totalLog) {
		this.totalLog = totalLog;
	}
}
