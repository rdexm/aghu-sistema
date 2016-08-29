package br.gov.mec.aghu.controleinfeccao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.FatorPredisponenteVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author israel.haas
 */

public class CadastroFatoresPredisponentesController extends ActionController {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8741985642144797236L;

	private static final String REDIRECIONA_LISTAR_FATOR_PREDISPONENTE = "pesquisaFatoresPredisponentes";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private PesquisaFatoresPredisponentesPaginatorController pesquisaFatoresPredisponentesPaginatorController;
	
	private FatorPredisponenteVO fatorSelecionado;
	
	private MciFatorPredisponentes fatorPredisponente;
	
	private Boolean situacao;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		if (getFatorSelecionado() == null || getFatorSelecionado().getSeq() == null) {
			fatorPredisponente = new MciFatorPredisponentes();
			this.situacao = true;
		} else {
			fatorPredisponente = controleInfeccaoFacade.obterFatorPredisponentesPorSeq(getFatorSelecionado().getSeq());
			this.situacao = this.fatorPredisponente.getIndSituacao().isAtivo();
		}
	}
	
	public String gravar() {
		try {
			this.fatorPredisponente.setIndSituacao(DominioSituacao.getInstance(this.situacao));
			controleInfeccaoFacade.gravarFatorPredisponente(fatorPredisponente);
			
			if (fatorSelecionado == null || fatorSelecionado.getSeq() == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_FATOR_PREDISPONENTE", fatorPredisponente.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_FATOR_PREDISPONENTE", fatorPredisponente.getDescricao());
			}
			
			//Zera os dados e retorna para a tela de pesquisa efetuando a pesquisa
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		fatorPredisponente = new MciFatorPredisponentes();
		fatorSelecionado = new FatorPredisponenteVO();
		this.situacao = true;
		
		this.pesquisaFatoresPredisponentesPaginatorController.reiniciarPaginator();
		return REDIRECIONA_LISTAR_FATOR_PREDISPONENTE;
	}
	
	// ### GETs e SETs ###

	public FatorPredisponenteVO getFatorSelecionado() {
		return fatorSelecionado;
	}

	public void setFatorSelecionado(FatorPredisponenteVO origemSelecionada) {
		this.fatorSelecionado = origemSelecionada;
	}

	public MciFatorPredisponentes getFatorPredisponente() {
		return fatorPredisponente;
	}

	public void setFatorPredisponente(MciFatorPredisponentes fatorPredisponente) {
		this.fatorPredisponente = fatorPredisponente;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}
}
