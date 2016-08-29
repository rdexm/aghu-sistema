package br.gov.mec.aghu.casca.aplicacao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class AplicacaoController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -5450994554389731659L;
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Aplicacao> dataModel;
	
	private Aplicacao aplicacao;
	private Aplicacao aplicacaoFiltro;
	private DominioSimNao flagExterno;
	
	private final String PAGE_CADASTRAR_APLICACAO = "cadastrarAplicacao";
	private final String PAGE_PESQUISAR_APLICACAO = "pesquisarAplicacoes";

	@PostConstruct	
	public void init() {
		begin(conversation, true);		
		aplicacaoFiltro = new Aplicacao();
	}
	
	public String novo() {
		aplicacao = new Aplicacao();
		return PAGE_CADASTRAR_APLICACAO;
	}
	
	
	public String editar() {
		this.flagExterno = DominioSimNao.getInstance(aplicacao.getExterno());
		return PAGE_CADASTRAR_APLICACAO;
	}
	

	public void pesquisar() {
		if (this.flagExterno != null){
			aplicacaoFiltro.setExterno(flagExterno.isSim());
		}else{
			aplicacaoFiltro.setExterno(null);
		}
		this.dataModel.reiniciarPaginator();
	}
	
	
	@Override
	public List<Aplicacao> recuperarListaPaginada(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarAplicacoes(aplicacaoFiltro.getServidor(), aplicacaoFiltro.getPorta(), aplicacaoFiltro.getContexto(), aplicacaoFiltro.getNome(),
				aplicacaoFiltro.getExterno(), firstResult, maxResult, orderProperty, asc);
	}
	
	
	@Override	
	public Long recuperarCount() {		
		return cascaFacade.pesquisarAplicacoesCount(aplicacaoFiltro.getServidor(), aplicacaoFiltro.getPorta(), aplicacaoFiltro.getContexto(), aplicacaoFiltro.getNome(), aplicacaoFiltro.getExterno());
	}	
	
	
	public void limparPesquisa() {
		aplicacaoFiltro = new Aplicacao();
		this.flagExterno = null;
		this.dataModel.limparPesquisa();
	}

	
	public String salvar() {
		boolean externo = false;
		if (flagExterno == DominioSimNao.S){
			externo = true;
		}
		aplicacao.setExterno(externo);
		
		try {
			boolean novo=aplicacao.getId()==null;
			
			cascaFacade.salvar(aplicacao);
			if (novo) {
				apresentarMsgNegocio("CASCA_MENSAGEM_APLICACAO_INCLUIDA_SUCESSO");
			} else {
				apresentarMsgNegocio("CASCA_MENSAGEM_APLICACAO_ALTERADA_SUCESSO");			
			}
			aplicacao = new Aplicacao();
			this.flagExterno = null;
			
			return PAGE_PESQUISAR_APLICACAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	
	public void excluir() {
		try {
			cascaFacade.excluirAplicacao(aplicacao.getId());
			apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_EXCLUSAO_APLICACAO");
			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	
	public String cancelar() {
		this.dataModel.reiniciarPaginator();
		
		return PAGE_PESQUISAR_APLICACAO;
	}

	public DynamicDataModel<Aplicacao> getDataModel() {
		return dataModel;
	}

	
	public void setDataModel(DynamicDataModel<Aplicacao> dataModel) {
		this.dataModel = dataModel;
	}	
	
	public Aplicacao getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(Aplicacao aplicacao) {
		this.aplicacao = aplicacao;
	}

	public DominioSimNao getFlagExterno() {
		return flagExterno;
	}

	public void setFlagExterno(DominioSimNao flagExterno) {
		this.flagExterno = flagExterno;
	}

	public Aplicacao getAplicacaoFiltro() {
		return aplicacaoFiltro;
	}

	public void setAplicacaoFiltro(Aplicacao aplicacaoFiltro) {
		this.aplicacaoFiltro = aplicacaoFiltro;
	}
}