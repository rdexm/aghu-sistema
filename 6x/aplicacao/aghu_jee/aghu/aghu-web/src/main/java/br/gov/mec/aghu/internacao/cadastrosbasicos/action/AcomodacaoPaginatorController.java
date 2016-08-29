package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AcomodacaoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229000718898060263L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinAcomodacoes> dataModel;	

	private AinAcomodacoes acomodacao;	
	private AinAcomodacoes acomodacaoSelecionada;
	
	private final String PAGE_CADASTRAR_ACOMODACAO = "acomodacaoCRUD";
	

	@PostConstruct
	public void init() {
		begin(conversation, true);
		acomodacao = new AinAcomodacoes();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setAcomodacao(new AinAcomodacoes());
		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade.pesquisaAcomodacoesCount(this.getAcomodacao().getSeq(),
						this.getAcomodacao().getDescricao());
		return count;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<AinAcomodacoes> result = this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoes(firstResult, maxResult, orderProperty,
						asc, this.getAcomodacao().getSeq(),
						this.getAcomodacao().getDescricao());

		if (result == null) {
			result = new ArrayList<AinAcomodacoes>();
		}
		return result;
	}

	public void excluir()  {
		try {
			if (this.getAcomodacaoSelecionada() != null) {
				this.cadastrosBasicosInternacaoFacade.removerAcomodacao(this.getAcomodacaoSelecionada().getSeq());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_ACOMODACAO",this.getAcomodacaoSelecionada().getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_REMOCAO_ACOMODACAO_INVALIDA");
			}
			this.setAcomodacaoSelecionada(null);
		}  catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar() {
		return PAGE_CADASTRAR_ACOMODACAO;
	}
	
	public AinAcomodacoes getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	public DynamicDataModel<AinAcomodacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinAcomodacoes> dataModel) {
		this.dataModel = dataModel;
	}

	public AinAcomodacoes getAcomodacaoSelecionada() {
		return acomodacaoSelecionada;
	}

	public void setAcomodacaoSelecionada(AinAcomodacoes acomodacaoSelecionada) {
		this.acomodacaoSelecionada = acomodacaoSelecionada;
	}
	
	
}
