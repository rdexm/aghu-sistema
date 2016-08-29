package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaProcedimentosUsadosEquipePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcProcPorEquipe> dataModel;

	private static final String TOP_N_PROCS_CRUD = "manterProcedimentosUsadosEquipe";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;

	/*
	 * Injeções
	 */
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/*
	 * Campos do filtro
	 */
	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores equipe;
	private MbcProcedimentoCirurgicos procedimento;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;


	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {

		// Garante que os resultados da pesquisa serão mantidos ao retonar na tela
		if (this.dataModel.getPesquisaAtiva()) {
			this.pesquisar();
		}
	}

	public String iniciarInclusao() {
		return TOP_N_PROCS_CRUD;
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		// Limpa filtro
		this.unidadeFuncional = null;
		this.equipe = null;
		this.procedimento = null;
		this.exibirBotaoNovo = false;

		// Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);

	}

	/**
	 * Obtem o elemento/filtro da pesquisa paginada
	 * 
	 * @return
	 */
	private MbcProcPorEquipe getElemento() {
		MbcProcPorEquipe elemento = new MbcProcPorEquipe();
		elemento.setAghUnidadesFuncionais(this.unidadeFuncional);
		elemento.setRapServidoresByMbcPxqSerFk1(this.equipe);
		elemento.setMbcProcedimentoCirurgicos(this.procedimento);
		return elemento;
	}

	@Override
	public List<MbcProcPorEquipe> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarProcedimentosUsadosEquipe(firstResult, maxResult, orderProperty, asc, this.getElemento());
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarProcedimentosUsadosEquipeCount(this.getElemento());
	}

	/**
	 * Excluir item
	 */
	public void excluir(MbcProcPorEquipe itemExclusao) {
		if (itemExclusao != null) {
			try {
				this.blocoCirurgicoCadastroApoioFacade.removerProcedimentoUsadoPorEquipe(itemExclusao);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_PROCEDIMENTOS_MAIS_USADOS_EQUIPE");

			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
	}


	/*
	 * Pesquisas das suggestion box
	 */

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeFuncional(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(filtro),obterUnidadeFuncionalCount(filtro));
	}
	
    public Long obterUnidadeFuncionalCount(String filtro) {
        return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro);
    }
    
	/**
	 * Obtem servidores das equipes
	 */
	public List<RapServidores> obterEquipe(String filtro) {
		try {
			return this.returnSGWithCount(registroColaboradorFacade
				.pesquisarServidorPorSituacaoAtivoParaProcedimentos(
						filtro, this.obterUnfSeq()),obterEquipeCount(filtro));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	/**
	 * Obtem quantidade de servidores das equipes
	 * 
	 * @param param
	 * @return
	 * @throws BaseException
	 */
	public Integer obterEquipeCount(String param) throws BaseException {
		try {
			return this.registroColaboradorFacade
				.pesquisarServidorPorSituacaoAtivoParaProcedimentosCount(
						param, this.obterUnfSeq());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return Integer.valueOf(0);
	}

	/**
	 * Obtem procedimentos cirurgicos ativos
	 */
	public List<MbcProcedimentoCirurgicos> obterProcedimento(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro, 
				MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), 100, DominioSituacao.A),obterProcedimentoCount(filtro));
	}

    public Long obterProcedimentoCount(String filtro) {
        return this.blocoCirurgicoCadastroApoioFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(filtro, DominioSituacao.A);
    }
	
	private Short obterUnfSeq() {
		if(this.unidadeFuncional != null) {
			return this.unidadeFuncional.getSeq();
		}
		return null;
	}
	
	public void limparSuggestionEquipe() {
		this.equipe = null;
	}
	
	/*
	 * Getters e Setters
	 */

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public RapServidores getEquipe() {
		return equipe;
	}

	public void setEquipe(RapServidores equipe) {
		this.equipe = equipe;
	}

	public MbcProcedimentoCirurgicos getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(MbcProcedimentoCirurgicos procedimento) {
		this.procedimento = procedimento;
	}

	public DynamicDataModel<MbcProcPorEquipe> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcProcPorEquipe> dataModel) {
	 this.dataModel = dataModel;
	}
}