package br.gov.mec.aghu.ambulatorio.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacConsultasSisreg;
import br.gov.mec.aghu.vo.FileVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;




public class ImportarArquivoSisregController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ImportarArquivoSisregController.class);

	private static final long serialVersionUID = 6349539681642559804L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
//	@SuppressWarnings("unused")
//	@EJB
//	private Pessoa pessoa;
	// WARN: NÃO retirar a chamada acima, pois é necessária carregar a pessoa no contexto para posterior utilização na ON
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private List<FileVO> files = new ArrayList<FileVO>();
	private String nomeTxt = "";
	private List<String> listaLinhas;
	private boolean autoUpload = false;
	private boolean useFlash = false;

	private Boolean importando = false;
	private StringBuilder logImportacao; 
	private int index = 0;
	private double size;
	private Boolean habilitaImportarArquivo;
	private Boolean habilitaMarcarConsultas;
	private Boolean habilitaExportarLog;
	
	private Integer arquivosSendoProcessados;
	private Integer totalProcessado;
	private Integer totalConsultas;

	private static final String NOME_LOG_EXPORTACAO = "Log_SISREG";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private enum ImportarArquivoSisregControllerExceptionCode implements BusinessExceptionCode {
		TITLE_IMPORTAR_ARQUIVO_SISREG, SUCESSO_IMPORTAR_ARQUIVO_SISREG, ERRO_ARQUIVO_NAO_ENCONTRADO,
		ERRO_IMPORTAR_ARQUIVO_SISREG_VERIFICAR_LOG,EXISTEM_REGISTROS_QUE_NAO_FORAM_IMPORTADOS_DO_SISREG_VERIFICAR_LOG;
	}

	public void inicio() {
	 

	 

		files = new ArrayList<FileVO>();
		nomeTxt = "";
		importando = false;
		logImportacao = new StringBuilder(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SISREG));
		autoUpload = false;
		useFlash = false;
	
	}
	

	public int getSize() {
		if (getFiles().size() > 0) {
			return getFiles().size();
		} else {
			return 0;
		}
	}

	public void paint(final OutputStream stream, final Object object) throws IOException {
		stream.write(getFiles().get((Integer) object).getData());
	}

	public void listener(final FileUploadEvent event) {
		try {
			final UploadedFile item = event.getFile();
			nomeTxt = item.getFileName();
			//caminhoAbsolutoTxt = item.getFile().getPath();
			final FileVO file = new FileVO();
			file.setLength(item.getSize());
			file.setName(item.getFileName());
			file.setData(item.getContents());
			files.add(file);
			
			String linha = null;
			
			BufferedReader br = new  BufferedReader(new InputStreamReader(item.getInputstream()));
			listaLinhas = new ArrayList<String>();
			while( (linha = br.readLine()) != null){
				listaLinhas.add(linha);
			}
			size = listaLinhas.size();
			habilitaImportarArquivo = true;
			
			
			logImportacao.delete(0, logImportacao.length());
			logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SISREG));
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sucesso", "Upload de arquivo "+event.getFile().getFileName() + " realizado com sucesso."));
			
		} catch (IOException e) {
			clearUploadData(false);
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	public String clearUploadData(final boolean limpaLog) {
		index = 0;
		files.clear();
		if (limpaLog) {
			logImportacao.delete(0, logImportacao.length());
			logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SISREG));
			habilitaImportarArquivo = false;
		}
		return null;
	}
	
	public String exportarLog() {
		File file = ambulatorioFacade.exportarLogSisreg(logImportacao);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.setContentType("application/x-zip-compressed");
		response.addHeader("Content-disposition", "attachment; filename=" + NOME_LOG_EXPORTACAO + ".zip");

		try {
			ServletOutputStream os = response.getOutputStream();

			if (file != null) {
				os.write(FileUtils.readFileToByteArray(file));
				file.delete();
			}
			
			os.flush();
			os.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			LOG.error(e.getClass().getName(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_EXPORTACAO_LOG_ZIPADO");
		}
		return null;
	}
	
	public String limpar() {
		this.inicio();
		this.clearUploadData(true);
		this.habilitaMarcarConsultas=false;
		this.habilitaExportarLog=false;
		return null;
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
	
	public void importarArquivo() {
		StringBuilder msgErroProcedimento = new StringBuilder();
		this.ambulatorioFacade.limparConsultasSisreg();
		this.logImportacao.delete(0, logImportacao.length());
		this.logImportacao.append(NEWLINE);
		for(index=1; index <listaLinhas.size(); index++){
			String consulta = listaLinhas.get(index);
			try{
				this.ambulatorioFacade.importarArquivo(consulta, nomeTxt, index+1, msgErroProcedimento);
			} catch(ApplicationBusinessException e){
				executarOperacoesAposErroImportacao(e);
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
				return;
			}
			setImportando(true);
		}
		
		if (msgErroProcedimento.length() > 0) {
			try {
				this.ambulatorioFacade.tratarErrosImportacaoConsultas(nomeTxt, msgErroProcedimento);
			} catch (ApplicationBusinessException e) {
				ApplicationBusinessException novaExcecao = new ApplicationBusinessException(
						ImportarArquivoSisregControllerExceptionCode.EXISTEM_REGISTROS_QUE_NAO_FORAM_IMPORTADOS_DO_SISREG_VERIFICAR_LOG, e, nomeTxt);
				apresentarExcecaoNegocio(novaExcecao);
				LOG.error(e.getMessage(), e);
			}
			
			totalConsultas = listaLinhas.size() - 1; // cabeçalho não conta como linha de registro de consulta
			this.logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.SUCESSO_IMPORTAR_ARQUIVO_SISREG, nomeTxt));
			this.logImportacao.append(NEWLINE);
			this.logImportacao.append(NEWLINE);
			this.logImportacao.append("Itens não importados do arquivo " + nomeTxt + ":");	
			this.logImportacao.append(NEWLINE);
			this.logImportacao.append(msgErroProcedimento);
			
		}else{
			totalConsultas = listaLinhas.size() - 1; // cabeçalho não conta como linha de registro de consulta
			this.logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.SUCESSO_IMPORTAR_ARQUIVO_SISREG, nomeTxt));
			apresentarMsgNegocio(Severity.INFO,
			"SUCESSO_IMPORTAR_ARQUIVO_SISREG", nomeTxt);
		}
		
		habilitaImportarArquivo = false;
		habilitaMarcarConsultas = true;
		setImportando(false);
	}
	
	public void executarOperacoesAposErroImportacao(ApplicationBusinessException e) {
		logImportacao.delete(0, logImportacao.length());
		logImportacao.append(getMensagem(ImportarArquivoSisregControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_SISREG));
		this.logImportacao.append(NEWLINE);
		this.logImportacao.append(NEWLINE);
		logImportacao.append(e.getMessage());
		habilitaImportarArquivo = true;
		habilitaMarcarConsultas = false;
		setImportando(false);
		this.clearUploadData(false);	
	}
	
	public void marcarConsultas(){
		List<AacConsultasSisreg> consultasSisreg = this.ambulatorioFacade.obterConsultasSisreg();
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e1) {
			LOG.error("Exceção capturada:", e1);
		}
		this.logImportacao.delete(0, logImportacao.length());
		logImportacao.append(this.ambulatorioFacade.marcarConsultas(consultasSisreg, totalConsultas, nomeMicrocomputador));
		habilitaMarcarConsultas = false;
		habilitaExportarLog = true;
	}

	public Double getPercentualProgressBar() {
		if (importando){ 
			return index/size*100;
		} else if(index==0){
			return 0.0;
		} else{
			return 100.0;	
		}
	}

	public Boolean getImportando() {
		if (importando == null) {
			return false;
		}
		return importando.booleanValue();
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

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public List<String> getLista() {
		return listaLinhas;
	}

	public void setLista(final List<String> lista) {
		this.listaLinhas = lista;
	}

	
	public StringBuilder getLogImportacao() {
		return logImportacao;
	}

	public void setLogImportacao(StringBuilder logImportacao) {
		this.logImportacao = logImportacao;
	}

	public void setImportando(Boolean importando) {
		this.importando = importando;
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Boolean getHabilitaImportarArquivo() {
		return habilitaImportarArquivo;
	}

	public void setHabilitaImportarArquivo(Boolean habilitaImportarArquivo) {
		this.habilitaImportarArquivo = habilitaImportarArquivo;
	}

	public Boolean getHabilitaMarcarConsultas() {
		return habilitaMarcarConsultas;
	}

	public void setHabilitaMarcarConsultas(Boolean habilitaMarcarConsultas) {
		this.habilitaMarcarConsultas = habilitaMarcarConsultas;
	}

	public Boolean getHabilitaExportarLog() {
		return habilitaExportarLog;
	}

	public void setHabilitaExportarLog(Boolean habilitaExportarLog) {
		this.habilitaExportarLog = habilitaExportarLog;
	}

	public Integer getTotalLinhas() {
		return totalConsultas;
	}

	public void setTotalLinhas(Integer totalLinhas) {
		this.totalConsultas = totalLinhas;
	}
	
}
