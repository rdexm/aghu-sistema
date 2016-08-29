package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.FatorPredisponenteVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author israel.haas
 */

public class PesquisaFatoresPredisponentesPaginatorController extends ActionController implements ActionPaginator {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3347360203777734262L;


	private static final String REDIRECIONA_MANTER_FATOR_PREDISP = "cadastroFatoresPredisponentes";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private CadastroFatoresPredisponentesController cadastroFatoresPredisponentesController;

	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;

	private FatorPredisponenteVO fatorSelecionado;
	
	@Inject @Paginator
	private DynamicDataModel<FatorPredisponenteVO> dataModel;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.dataModel.setPesquisaAtiva(false);
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return controleInfeccaoFacade.pesquisarFatorPredisponenteCount(codigo, descricao, situacao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FatorPredisponenteVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return controleInfeccaoFacade.pesquisarFatorPredisponente(codigo, descricao, situacao, firstResult, maxResults, orderProperty, asc);
	}
	
	public String editar() {
		this.cadastroFatoresPredisponentesController.setFatorSelecionado(this.fatorSelecionado);
		return REDIRECIONA_MANTER_FATOR_PREDISP;
	}
	
	public void excluir()  {
		try {
			controleInfeccaoFacade.removerFatorPredisponente(fatorSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_FATOR_PREDISP",fatorSelecionado.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	public String novo(){
		return REDIRECIONA_MANTER_FATOR_PREDISP;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}

	// ### GETs e SETs ###

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigoOrigem) {
		this.codigo = codigoOrigem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricaoOrigem) {
		this.descricao = descricaoOrigem;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacaoOrigem) {
		this.situacao = situacaoOrigem;
	}

	public FatorPredisponenteVO getFatorSelecionado() {
		return fatorSelecionado;
	}

	public void setFatorSelecionado(FatorPredisponenteVO fatorSelecionado) {
		this.fatorSelecionado = fatorSelecionado;
	}

	public DynamicDataModel<FatorPredisponenteVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatorPredisponenteVO> dataModel) {
		this.dataModel = dataModel;
	}
}
