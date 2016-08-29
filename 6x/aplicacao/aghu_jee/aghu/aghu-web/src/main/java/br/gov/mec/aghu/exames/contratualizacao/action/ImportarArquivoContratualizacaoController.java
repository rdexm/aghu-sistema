package br.gov.mec.aghu.exames.contratualizacao.action;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.business.IContratualizacaoFacade;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.vo.FileVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ImportarArquivoContratualizacaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6349539681642559804L;

	@EJB
	private IContratualizacaoFacade contratualizacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private List<AghArquivoProcessamento> arquivosProcessando;
	
	private List<FileVO> files = new ArrayList<FileVO>();
	private String nomeXml = "";
	private String caminhoAbsolutoXml = "";
	private List<SolicitacaoExame> listaSolicitacoes;
	private Detalhes detalhes;
	private Header headerIntegracao;
	private int uploadsAvailable = 1;
	private boolean autoUpload = false;
	private boolean useFlash = false;
	private static final int MAX_TAM_LOG = 5000;
	
	private Boolean importando = false;
	private StringBuilder logImportacao; 
	private Double percentualProgressBar;
	private Boolean habilitaImportarArquivo;
	private Boolean habilitaExportarLog;
	
	private String parametroExames = null;
	
	private Integer arquivosSendoProcessados;
	private Integer totalProcessado;

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private enum ImportarArquivoSisregControllerExceptionCode implements BusinessExceptionCode {
		TITLE_IMPORTAR_ARQUIVO_CONTRATUALIZACAO, SUCESSO_IMPORTAR_ARQUIVO_CONTRATUALIZACAO, ERRO_ARQUIVO_NAO_ENCONTRADO;
	}

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void inicio() {
		files = new ArrayList<FileVO>();
		nomeXml = "";
		caminhoAbsolutoXml = "";
		importando = false;
		logImportacao = new StringBuilder(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_CONTRATUALIZACAO));
		uploadsAvailable = 1;
		autoUpload = false;
		useFlash = false;
		percentualProgressBar = 0.0;
		
		try {
			parametroExames = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_EXAME).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void paint(final OutputStream stream, final Object object) throws IOException {
		stream.write(getFiles().get((Integer) object).getData());
	}

	public void listener(final FileUploadEvent event) {
		final UploadedFile item = event.getFile();
		nomeXml = item.getFileName();
		//caminhoAbsolutoXml = item.getFile().getPath();
		final FileVO file = new FileVO();
		file.setLength(item.getSize());
		file.setName(item.getFileName());
		file.setData(item.getContents());
		files.add(file);
		uploadsAvailable--;
		logImportacao.delete(0, logImportacao.length());
		logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_CONTRATUALIZACAO));
	}
	
	public String diretorioPadraoContratualizacao() {
		String caminhoPadrao = "";
		AghParametros parametro;
		try {
			parametro = getAghuParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_ENTRADA);
			if (parametro != null && parametro.getVlrTexto() != null) {
				caminhoPadrao = parametro.getVlrTexto();
			}
		} catch (ApplicationBusinessException e) {
			caminhoPadrao = "";
		}
		return caminhoPadrao;
	}

	private IParametroFacade getAghuParametroFacade() {
		return parametroFacade;
	}

	public String carregarArquivo() {
		try {
			contratualizacaoFacade.verificarFormatoNomeArquivoXml(nomeXml);
			Detalhes detalhes = contratualizacaoFacade.verificarEstruturaArquivoXml(caminhoAbsolutoXml);
			if (detalhes.getSolicitacoes() != null && detalhes.getSolicitacoes().getSolicitacaoExame() != null) {
				listaSolicitacoes = detalhes.getSolicitacoes().getSolicitacaoExame();
			}
			if(detalhes.getHeader() != null){
				headerIntegracao = detalhes.getHeader();
			}
			setDetalhes(detalhes);
			habilitaImportarArquivo = true;
		} catch (final BaseException e) {
			clearUploadData(false);
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String clearUploadData(final boolean limpaLog) {
		files.clear();
		setUploadsAvailable(1);
		if (limpaLog) {
			logImportacao.delete(0, logImportacao.length());
			logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_CONTRATUALIZACAO));
			setPercentualProgressBar(0.0);
			habilitaImportarArquivo = false;
		}
		return null;
	}
	
	public String limpar() {
		this.inicio();
		this.clearUploadData(true);
		this.habilitaExportarLog=false;
		return null;
	}

	public long getTimeStamp() {
		return System.currentTimeMillis();
	}

	public List<FileVO> getFiles() {
		return files;
	}

	public void setFiles(final List<FileVO> files) {
		this.files = files;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(final int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(final boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isUseFlash() {
		return useFlash;
	}

	public void setUseFlash(final boolean useFlash) {
		this.useFlash = useFlash;
	}

	public List<SolicitacaoExame> getLista() {
		return listaSolicitacoes;
	}

	public void setLista(final List<SolicitacaoExame> lista) {
		this.listaSolicitacoes = lista;
	}

	
	public StringBuilder getLogImportacao() {
		if (getImportando()) {
			boolean continua = false;
			// já sabe os arquivos. Buscar status atual.
			if (getArquivosProcessando() != null) {
				List<AghArquivoProcessamento> arquivos = aghuFacade.pesquisarArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(parametroExames, obterIds(getArquivosProcessando()));
				obterLog();
				if (processamentoFinalizado(arquivos)) {
					setPercentualProgressBar(new Double(100));
				} else {
					// atualiza percentual
					atualizarProgressBar(arquivos);
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
			log = log.substring(log.indexOf(lineSeparator));
		}
		this.setLogImportacao(new StringBuilder(log));
	}
	
	private List<Integer> obterIds(List<AghArquivoProcessamento> arquivos) {
		if (arquivos == null || arquivos.isEmpty()) {
			return new ArrayList<Integer>(0);
		}
		List<Integer> retorno = new ArrayList<Integer>();
		for (AghArquivoProcessamento arquivo : arquivos) {
			retorno.add(arquivo.getSeq());
		}
		return retorno;
	}
	
	private void atualizarProgressBar(List<AghArquivoProcessamento> arquivos) {
		if (arquivos == null || arquivos.isEmpty()) {
			setPercentualProgressBar(new Double(1));
		} else {
			totalProcessado = 0;
			if (arquivos != null && !arquivos.isEmpty()) {
				totalProcessado = arquivos.get(0).getPercentualProcessado();
			}
			setPercentualProgressBar(new Double((totalProcessado)));
		}
	}
	
	private boolean processamentoFinalizado(List<AghArquivoProcessamento> arquivos) {
		for (AghArquivoProcessamento arquivoProcessamento : arquivos) {
			if (arquivoProcessamento.getDthrFimProcessamento() == null) {
				return false;
			}
		}
		return true;
	}

	public void setLogImportacao(StringBuilder logImportacao) {
		this.logImportacao = logImportacao;
	}

	public Boolean getImportando() {
		if (importando == null) {
			return false;
		}
		return importando.booleanValue();
	}

	public void setImportando(Boolean importando) {
		this.importando = importando;
	}

	protected String getMensagem(final ImportarArquivoSisregControllerExceptionCode key, final Object... args) {
		String val = getBundle().getString(key.toString());
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				val = val.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}
		return val;
	}
	
	public void importarArquivo() throws UnknownHostException {
		//#14594 - verifica permissão de escrita nas pastas
		try {
			contratualizacaoFacade.verificarPermissaoDeEscritaPastasDestino();
		} catch (final BaseException e) {
			clearUploadData(true);
			apresentarExcecaoNegocio(e);
			return;
		}  catch (final Exception e) {
			clearUploadData(true);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			return;
		}
		
//		for(index=1; index <= listaSolicitacoes.size(); index++){
		try{
			setImportando(true);
			
			String nomeMicrocomputador = null;
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			final Map<String, Object> retorno = contratualizacaoFacade.importarArquivoContratualizacao(getDetalhes(), caminhoAbsolutoXml, nomeXml, headerIntegracao, nomeMicrocomputador);
			setArquivosProcessando((List<AghArquivoProcessamento>) retorno.get(IExamesFacade.ARQUIVOS_IMPORTACAO_EXAMES));
			
		} catch(BaseException e){
			logImportacao.delete(0, logImportacao.length());
			logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_CONTRATUALIZACAO));
			this.logImportacao.append(NEWLINE);
			this.logImportacao.append(NEWLINE);
			logImportacao.append(e.getMessage());
			apresentarExcecaoNegocio(e);
			habilitaImportarArquivo = true;
			setImportando(false);
			this.clearUploadData(false);
			return;
		}
			
//		}
//		this.logImportacao.append(NEWLINE);
//		this.logImportacao.append(NEWLINE);
//		this.logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.SUCESSO_IMPORTAR_ARQUIVO_CONTRATUALIZACAO, nomeXml));
//		this.logImportacao.append(NEWLINE);
//		this.logImportacao.append(NEWLINE);		
//		this.apresentarMsgNegocio(Severity.INFO,
//		"SUCESSO_IMPORTAR_ARQUIVO_CONTRATUALIZACAO", nomeXml);
		habilitaImportarArquivo = false;
//		setImportando(false);
	}
	
	public void marcarConsultas(){
//		List<AacConsultasSisreg> consultasSisreg = this.ambulatorioFacade.obterConsultasSisreg();
//		logImportacao.append(this.ambulatorioFacade.marcarConsultas(consultasSisreg, totalConsultas));
		habilitaExportarLog = true;
	}
	
	public int getArquivosSendoProcessados() {
		return arquivosSendoProcessados;
	}

	public void setArquivosSendoProcessados(int arquivosSendoProcessados) {
		this.arquivosSendoProcessados = arquivosSendoProcessados;
	}

	public int getProcessado() {
		return totalProcessado;
	}

	public void setProcessado(int processado) {
		this.totalProcessado = processado;
	}

	public Double getPercentualProgressBar() {
		return percentualProgressBar;
	}

	public void setPercentualProgressBar(Double percentualProgressBar) {
		this.percentualProgressBar = percentualProgressBar;
	}

	public Boolean getHabilitaImportarArquivo() {
		return habilitaImportarArquivo;
	}

	public void setHabilitaImportarArquivo(Boolean habilitaImportarArquivo) {
		this.habilitaImportarArquivo = habilitaImportarArquivo;
	}

	public Boolean getHabilitaExportarLog() {
		return habilitaExportarLog;
	}

	public void setHabilitaExportarLog(Boolean habilitaExportarLog) {
		this.habilitaExportarLog = habilitaExportarLog;
	}

	public List<AghArquivoProcessamento> getArquivosProcessando() {
		return arquivosProcessando;
	}

	public void setArquivosProcessando(
			List<AghArquivoProcessamento> arquivosProcessando) {
		this.arquivosProcessando = arquivosProcessando;
	}
	public Detalhes getDetalhes() {
		return detalhes;
	}
	public void setDetalhes(Detalhes detalhes) {
		this.detalhes = detalhes;
	}
}