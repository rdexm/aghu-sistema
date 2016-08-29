package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterRegiaoAnatomicaController extends ActionController {

	private static final long serialVersionUID = 1207436927955543248L;

	private static final String MANTER_REGIAO_ANATOMICA_PESQUISA = "manterRegiaoAnatomicaPesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelRegiaoAnatomica regiao;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(regiao != null && regiao.getSeq() != null) {
			regiao = examesFacade.obterRegiaoAnatomicaPeloId(regiao.getSeq());
			
			if(regiao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			limpar();
		}
		
		return null;
	
	}

	public void limpar() {
		regiao = new AelRegiaoAnatomica();
		regiao.setIndSituacao(DominioSituacao.A);
	}

	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de regiões anatômicas
	 */
	public String gravar() {

		try {
			//Converte para maiúsculo e remove espaços da descrição
			regiao.setDescricao(regiao.getDescricao().trim().toUpperCase());
			
			//Submete o procedimento para ser persistido
			cadastrosApoioExamesFacade.persistirRegiaoAnatomica(regiao);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_REGIAO_ANATOMICA", regiao.getDescricao());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar();
	}

	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de regiões anatômicas
	 */
	public String cancelar() {
		regiao = null;
		return MANTER_REGIAO_ANATOMICA_PESQUISA;
	}

	public AelRegiaoAnatomica getRegiao() {
		return regiao;
	}
	
	public void setRegiao(AelRegiaoAnatomica regiao) {
		this.regiao = regiao;
	}
}