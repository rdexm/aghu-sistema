package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterTituloResultadoPadraoController extends ActionController {

	private static final long serialVersionUID = -4837672518112929297L;

	private static final String PESQUISA_TITULO_RESULTADO_PADRAO = "exames-pesquisaTituloResultadoPadrao";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private String voltarPara; 
	
	private AelResultadosPadrao resultadoPadrao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 


		if (resultadoPadrao != null && resultadoPadrao.getSeq() != null) { 
			resultadoPadrao = this.examesFacade.obterResultadosPadraoPorSeq(resultadoPadrao.getSeq());

			if(resultadoPadrao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
		} else { 
			this.resultadoPadrao = new AelResultadosPadrao();
			this.resultadoPadrao.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}
	
	public String confirmar() {
		
		try {
			
			String mensagem = null;
			if (resultadoPadrao.getSeq() != null) {
				mensagem = "MENSAGEM_SUCESSO_ALTERAR_TITULO_RESULTADO_PADRAO";
			} else {
				mensagem = "MENSAGEM_SUCESSO_INSERIR_TITULO_RESULTADO_PADRAO";
			}
			
			cadastrosApoioExamesFacade.persistirResultadoPadrao(resultadoPadrao);
			apresentarMsgNegocio(Severity.INFO, mensagem, resultadoPadrao.getDescricao());
			
			return voltar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}
	
	public String voltar() {
		resultadoPadrao = null;
		if(voltarPara != null){
			return voltarPara;
		}
		
		return PESQUISA_TITULO_RESULTADO_PADRAO;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelResultadosPadrao getResultadoPadrao() {
		return resultadoPadrao;
	}

	public void setResultadoPadrao(AelResultadosPadrao resultadoPadrao) {
		this.resultadoPadrao = resultadoPadrao;
	}
}