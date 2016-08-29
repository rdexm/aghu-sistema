package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AipOcupacoesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de ocupações.
 * 
 * @author david.laks
 */


public class OcupacaoPaginatorController extends ActionController implements ActionPaginator {

	
	private static final long serialVersionUID = 6574049177451674175L;
	
	private static final String REDIRECT_MANTER_OCUPACAO = "ocupacaoCRUD";

	private static final Log LOG = LogFactory.getLog(OcupacaoPaginatorController.class);
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AipOcupacoesVO> dataModel;
	
	private List<AipSinonimosOcupacao> sinonimosOcupacao = new ArrayList<AipSinonimosOcupacao>();

	private boolean exibirBotaoIncluirOcupacao;

	private String descricaoPesquisaOcupacao;

	private Integer codigoPesquisaOcupacao;

	private AipOcupacoesVO ocupacao;

	private static final Comparator<AipSinonimosOcupacao> COMPARATOR_SINONIMOS_OCUPACAO = new Comparator<AipSinonimosOcupacao>() {

		@Override
		public int compare(AipSinonimosOcupacao o1, AipSinonimosOcupacao o2) {
			return o1.getDescricao().toUpperCase().compareTo(
					o2.getDescricao().toUpperCase());
		}
	};

	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}

	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
		
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirOcupacao = true;
	}

	public void limparPesquisa() {
		this.codigoPesquisaOcupacao = null;
		this.descricaoPesquisaOcupacao = null;
		this.exibirBotaoIncluirOcupacao = false;
		this.dataModel.limparPesquisa();
	}
	
	public String iniciarInclusao(){
		return REDIRECT_MANTER_OCUPACAO;
	}

	public String editar(){
		return REDIRECT_MANTER_OCUPACAO;
	}
	
	public void exibeSinonimos(AipOcupacoesVO _ocupacao) {
		this.sinonimosOcupacao = cadastrosBasicosPacienteFacade.pesquisarSinonimosPorOcupacao(new AipOcupacoes(_ocupacao.getCodigo(), null));
		Collections.sort(this.sinonimosOcupacao, COMPARATOR_SINONIMOS_OCUPACAO);
	}

	public void excluir() {
		this.dataModel.reiniciarPaginator();
	
		try {
			if (ocupacao != null) {
				this.cadastrosBasicosPacienteFacade.removerOcupacao(ocupacao.getCodigo());
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_OCUPACAO",ocupacao.getDescricao());			
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_REMOCAO_OCUPACAO_INVALIDA");
			}

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.pesquisaOcupacoesCount(this.codigoPesquisaOcupacao,this.descricaoPesquisaOcupacao);
	}

	@Override
	public List<AipOcupacoesVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		List<AipOcupacoesVO> result = cadastrosBasicosPacienteFacade.pesquisarOcupacoes(firstResult,
				maxResults, orderProperty, asc, this.codigoPesquisaOcupacao,
				this.descricaoPesquisaOcupacao);

		if (result == null) {
			result = new ArrayList<AipOcupacoesVO>();
		}

		return result;
	}
	
	public Integer obterCountSinonimos(AipOcupacoes ocupacao){
		return ocupacao.getSinonimos().size();
	}

	public List<AipSinonimosOcupacao> getSinonimosOcupacao() {
		return sinonimosOcupacao;
	}

	public void setSinonimosOcupacao(
			List<AipSinonimosOcupacao> sinonimosOcupacao) {
		this.sinonimosOcupacao = sinonimosOcupacao;
	}

	public boolean isExibirBotaoIncluirOcupacao() {
		return exibirBotaoIncluirOcupacao;
	}

	public void setExibirBotaoIncluirOcupacao(boolean exibirBotaoIncluirOcupacao) {
		this.exibirBotaoIncluirOcupacao = exibirBotaoIncluirOcupacao;
	}

	public String getDescricaoPesquisaOcupacao() {
		return descricaoPesquisaOcupacao;
	}

	public void setDescricaoPesquisaOcupacao(String descricaoPesquisaOcupacao) {
		this.descricaoPesquisaOcupacao = descricaoPesquisaOcupacao;
	}

	public Integer getCodigoPesquisaOcupacao() {
		return codigoPesquisaOcupacao;
	}

	public void setCodigoPesquisaOcupacao(Integer codigoPesquisaOcupacao) {
		this.codigoPesquisaOcupacao = codigoPesquisaOcupacao;
	}

	public AipOcupacoesVO getOcupacao() {
		return ocupacao;
	}

	public void setOcupacao(AipOcupacoesVO ocupacao) {
		this.ocupacao = ocupacao;
	}

	public DynamicDataModel<AipOcupacoesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipOcupacoesVO> dataModel) {
		this.dataModel = dataModel;
	}
}
