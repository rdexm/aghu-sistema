package br.gov.mec.aghu.exames.action;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisarArquivoSecretariaNotificacaoController extends ActionController{

	private static final long serialVersionUID = -4649099003259060499L;
	private Date dtInicio;
	private Date dtFim;
	private Boolean gerouArquivo = false;
	private String fileName;
	private Boolean infantil;
	
	@Inject
	private SecurityController securityController;
	
	@EJB
	private IExamesFacade examesFacade;
	
	private boolean habilitarBotaoGerarArquivo;
	private boolean habilitarBotaoGerarArquivoCarga;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		habilitarBotaoGerarArquivo = securityController.usuarioTemPermissao("gerarArquivoSecretaria", "gerar");
		habilitarBotaoGerarArquivoCarga = securityController.usuarioTemPermissao("gerarArquivoCD4CargaViral", "gerar");
	}

	
	public void dispararDownload(){
		if(this.fileName != null){
			try {
				String nmFile = examesFacade.obterNomeArquivoSecretariaNotificacao(infantil, dtInicio);
				super.download(fileName, nmFile);
				gerouArquivo = Boolean.FALSE;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	public void gerarArquivoSecretaria() {
		
		try {
			this.fileName = null;
			infantil = Boolean.FALSE;
			this.examesFacade.setTimeout(300);//5 min
			this.fileName = examesFacade.gerarArquivoSecretaria(dtInicio, dtFim, infantil);
			gerouArquivo = Boolean.TRUE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String gerarArquivoCD4CargaSecretaria(){
		try {
			this.fileName = null;
			infantil = Boolean.TRUE;
			this.fileName = examesFacade.gerarArquivoSecretariaCarga(dtInicio, dtFim, infantil);
			gerouArquivo = Boolean.TRUE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
		
	}
	
	public Boolean habilitarBotaoGerarArquivo(){
		return habilitarBotaoGerarArquivo;
	}
	
	public Boolean habilitarBotaoGerarArquivoCarga(){
		return habilitarBotaoGerarArquivoCarga;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}
}
