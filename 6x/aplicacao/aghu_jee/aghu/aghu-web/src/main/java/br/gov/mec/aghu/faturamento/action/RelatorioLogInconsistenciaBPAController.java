package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatMensagemLogId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;


public class RelatorioLogInconsistenciaBPAController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6913105915670662054L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioLogInconsistenciaBPAController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private RelatorioLogInconsistenciaBPAControllerPdf reportController;
	
	private DominioModuloMensagem modulo = DominioModuloMensagem.AMB;
	private FatMensagemLogId erro;
	private DominioSituacaoMensagemLog situacao;
	private Boolean gerouArquivo;
	private String fileName;

	
	@PostConstruct
	protected void init(){
		begin(conversation,true);
	}

	public String imprimirRelatorio(){
		inicializaReportController(true);
		return "";		
	}

	public String visualizarRelatorio(){
		inicializaReportController(false);
		return "relatorioLogInconsistenciaBPAPdf";
	}
	
	private void inicializaReportController(boolean isDirectPrint) {
		reportController.setIsDirectPrint(isDirectPrint);
		reportController.setSituacao(situacao);
		reportController.setModulo(modulo);	
		reportController.setErro(erro);
		reportController.iniciar();
	}

	public void gerarCSV() {
		try {
			fileName = faturamentoFacade.geraCSVRelatorioLogInconsistenciaBPA( modulo, DominioSituacao.A);
			gerouArquivo = true;
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	public void dispararDownload(){
		if(fileName != null){
			try {
				download(fileName, DominioNomeRelatorio.FATR_AMB_LOG_MSG.toString() + ".csv");
				gerouArquivo = false;
				fileName = null;
			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}

	public void limpar() {
		erro = null;
		fileName = null;
		situacao = null;
		gerouArquivo = false;
	}
	

	public List<FatMensagemLogId> pesquisarMensagemErro(String objPesquisa) {
		return this.returnSGWithCount(faturamentoFacade.listarMensagensErro(objPesquisa, modulo),pesquisarMensagemErroCount(objPesquisa));
	}
	
	public Long pesquisarMensagemErroCount(String objPesquisa) {
		return faturamentoFacade.listarMensagensErroCount(objPesquisa,modulo);
	}

	public DominioModuloMensagem getModulo() {
		return modulo;
	}

	public void setModulo(DominioModuloMensagem modulo) {
		this.modulo = modulo;
	}

	public FatMensagemLogId getErro() {
		return erro;
	}

	public void setErro(FatMensagemLogId erro) {
		this.erro = erro;
	}

	public DominioSituacaoMensagemLog getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoMensagemLog situacao) {
		this.situacao = situacao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}