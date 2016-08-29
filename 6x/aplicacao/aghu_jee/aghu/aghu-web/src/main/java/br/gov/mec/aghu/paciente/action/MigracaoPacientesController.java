package br.gov.mec.aghu.paciente.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.vo.FileVO;


public class MigracaoPacientesController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(MigracaoPacientesController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4662874433116198677L;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	private List<FileVO> files = new ArrayList<FileVO>();
	private String nomeTxt = "";
	private List<String> listaLinhas;
	private int uploadsAvailable = 1;
	private boolean autoUpload = false;
	private boolean useFlash = false;

	private Boolean importando = false;
	private StringBuilder logImportacao; 
	private int index = 0;
	private double size;
	private Boolean habilitaImportarArquivo;
	
	private Integer arquivosSendoProcessados;
	private Integer totalProcessado;
	
	private String separador;
	private Boolean anularProntuarios = false;
	private Boolean nomeMaeNaoInformado = false;
	private Boolean gerarFonemas = false;
	private Boolean migrarEnderecos = false;
	private Boolean migrarCodigos = false;
	private Boolean naoTrataProntuario = false;
	

	//private static final String NOME_LOG_EXPORTACAO = "Log_SISREG";
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private enum MigracaoPacientesControllerExceptionCode implements BusinessExceptionCode {
		TITLE_IMPORTAR_ARQUIVO_PACIENTES, SUCESSO_IMPORTAR_ARQUIVO_PACIENTES, ERRO_ARQUIVO_NAO_ENCONTRADO,
		ERRO_IMPORTAR_ARQUIVO_PACIENTES_VERIFICAR_LOG;
	}

	public void inicio() {
		files = new ArrayList<FileVO>();
		nomeTxt = "";
		importando = false;
		logImportacao = new StringBuilder(getMensagem(MigracaoPacientesControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_PACIENTES));
		uploadsAvailable = 1;
		autoUpload = false;
		useFlash = false;
		separador = null;
		anularProntuarios = false;
		nomeMaeNaoInformado = false;
		gerarFonemas = false;
		migrarEnderecos = false;
		migrarCodigos = false;
		naoTrataProntuario = false;
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

	public void listener(final FileUploadEvent event) throws ApplicationBusinessException {
		try {
		final UploadedFile item = event.getFile();
		nomeTxt = item.getFileName();
		final FileVO file = new FileVO();
		file.setLength(item.getSize());
		file.setName(item.getFileName());
		file.setData(item.getContents());
		files.add(file);
		uploadsAvailable--;
		logImportacao.delete(0, logImportacao.length());
		logImportacao.append(getMensagem(MigracaoPacientesControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_PACIENTES));
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sucesso", "Upload de arquivo "+event.getFile().getFileName() + " realizado com sucesso."));
		carregarArquivo(event);
		} catch (Exception e) {
			clearUploadData(false);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}

	}


	public String carregarArquivo(FileUploadEvent event) {
		try {
			InputStream arquivoPacientes = event.getFile().getInputstream();

			pacienteFacade.verificarExtensaoArquivoCSV(nomeTxt);
			listaLinhas = lerArquivoPacientes(arquivoPacientes);
			size = listaLinhas.size();
			habilitaImportarArquivo = true;
		} catch (FileNotFoundException e) {
			clearUploadData(false);
			apresentarExcecaoNegocio(new ApplicationBusinessException(MigracaoPacientesControllerExceptionCode.ERRO_ARQUIVO_NAO_ENCONTRADO));
			LOG.error(e.getMessage());
			return "erro";
		} catch (ApplicationBusinessException e) {
			clearUploadData(false);
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage());
			return "erro";
		} catch (Exception e) {
			clearUploadData(false);
			LOG.error(e.getMessage());
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			return "erro";
		}
		return null;
	}

	
	public List<String> lerArquivoPacientes(InputStream arquivoPacientes) throws IOException {
		if (arquivoPacientes == null) {
        	throw new FileNotFoundException("Não foi possível encontrar o arquivo.");
        }
        
		List<String> listaPacientes = IOUtils.readLines(arquivoPacientes);
     
		return listaPacientes;
	}
	
	public String clearUploadData(final boolean limpaLog) {
		index = 0;
		files.clear();
		setUploadsAvailable(1);
		if (limpaLog) {
			logImportacao.delete(0, logImportacao.length());
			logImportacao.append(getMensagem(MigracaoPacientesControllerExceptionCode.TITLE_IMPORTAR_ARQUIVO_PACIENTES));
			habilitaImportarArquivo = false;
		}
		return null;
	}
	
	public String limpar() {
		this.inicio();
		this.clearUploadData(true);
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

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
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

	public Boolean getImportando() {
		if (importando == null) {
			return false;
		}
		return importando.booleanValue();
	}

	public void setImportando(Boolean importando) {
		this.importando = importando;
	}

	protected String getMensagem(final MigracaoPacientesControllerExceptionCode key, final Object... args) {
		String val = getBundle().getString(key.toString());
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				val = val.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}
		return val;
	}
	
	public void importarArquivo() {
		final String msgSucesso = "Pacientes migrados com sucesso!";
		this.logImportacao.append(NEWLINE);

		setImportando(true);
		try{
			this.pacienteFacade.importarArquivo(listaLinhas, nomeTxt,
					separador, anularProntuarios, nomeMaeNaoInformado,
					gerarFonemas, migrarEnderecos, migrarCodigos, naoTrataProntuario, obterLoginUsuarioLogado());
		} 
		catch(ApplicationBusinessException e) {
			logImportacao.delete(0, logImportacao.length());
			logImportacao.append(e.getLocalizedMessage());
			habilitaImportarArquivo = false;
			setImportando(false);
			apresentarExcecaoNegocio(e);
			return;
		}

		logImportacao.delete(0, logImportacao.length());
		logImportacao.append(msgSucesso);
		this.apresentarMsgNegocio(Severity.INFO,"SUCESSO_IMPORTAR_ARQUIVO_PACIENTES", nomeTxt);
		habilitaImportarArquivo = false;
		setImportando(false);

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
		if (importando){ 
			return index/size*100;
		} else if(index==0){
			return 0.0;
		} else{
			return 100.0;	
		}
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

	public String getSeparador() {
		return separador;
	}

	public void setSeparador(String separador) {
		this.separador = separador;
	}

	public Boolean getAnularProntuarios() {
		return anularProntuarios;
	}

	public void setAnularProntuarios(Boolean anularProntuarios) {
		this.anularProntuarios = anularProntuarios;
	}

	public Boolean getNomeMaeNaoInformado() {
		return nomeMaeNaoInformado;
	}

	public void setNomeMaeNaoInformado(Boolean nomeMaeNaoInformado) {
		this.nomeMaeNaoInformado = nomeMaeNaoInformado;
	}

	public Boolean getGerarFonemas() {
		return gerarFonemas;
	}

	public void setGerarFonemas(Boolean gerarFonemas) {
		this.gerarFonemas = gerarFonemas;
	}
	
	public Boolean getMigrarEnderecos() {
		return migrarEnderecos;
	}

	public void setMigrarEnderecos(Boolean migrarEnderecos) {
		this.migrarEnderecos = migrarEnderecos;
	}

	public Boolean getMigrarCodigos() {
		return migrarCodigos;
	}

	public void setMigrarCodigos(Boolean migrarCodigos) {
		this.migrarCodigos = migrarCodigos;
	}

	public Boolean getNaoTrataProntuario() {
		return naoTrataProntuario;
	}

	public void setNaoTrataProntuario(Boolean naoTrataProntuario) {
		this.naoTrataProntuario = naoTrataProntuario;
	}

	
	
}
