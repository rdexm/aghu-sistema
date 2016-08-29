package br.gov.mec.aghu.faturamento.action;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar o cadastro de mensagem de erro
 * @author thiago.cortes
 * #2152
 *
 */

public class CadastroMensagemErroController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2489860116012060596L;

	private static final String PAGE_PESQUISA_MENSAGEM_ERRO = "cadastroMensagemErroList";

	private Boolean indSecretario = null;
	private FatMensagemLog fatMensagemErro;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void persistir(){	
		String indSec = indSecretario == true ? DominioSituacao.A.toString() : DominioSituacao.I.toString();
		fatMensagemErro.setIndSecretario(indSec);
		try{
			this.faturamentoFacade.alterarMensagemErro(fatMensagemErro);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM DE ERRO ALTERADA COM SUCESSO");
		}
		catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	public String voltar(){
		return PAGE_PESQUISA_MENSAGEM_ERRO;
	}
	
	//GETTS E SETTS

	public Boolean getIndSecretario() {
		return indSecretario;
	}

	public void setIndSecretario(Boolean indSecretario) {
		this.indSecretario = indSecretario;
	}

	public FatMensagemLog getFatMensagemErro() {
		return fatMensagemErro;
	}

	public void setFatMensagemErro(FatMensagemLog fatMensagemErro) {
		this.fatMensagemErro = fatMensagemErro;
		
		if(this.fatMensagemErro.getIndSecretario().equals("A")){
			
			this.indSecretario = Boolean.TRUE;
		}else{
			
			this.indSecretario = Boolean.FALSE;
		}
	}

	public DominioSituacaoMensagemLog getSituacaoNaoEnc() {
		return DominioSituacaoMensagemLog.NAOENC;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoCob() {
		return DominioSituacaoMensagemLog.NAOCOBR;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoIncons() {
		return DominioSituacaoMensagemLog.INCONS;
	}
	
}
