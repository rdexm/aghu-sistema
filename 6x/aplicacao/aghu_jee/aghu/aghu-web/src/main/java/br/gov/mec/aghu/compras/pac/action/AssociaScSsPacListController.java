package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AssociaScSsPacListController extends ActionController {

	private static final long serialVersionUID = -8991130576592663740L;
	
	private static final String PAGE_ASSOCIA_SCSS_PAC = "associaScSsPac";

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Boolean pesquisou = false;
	private ScoPontoParadaSolicitacao caixa;
	private ScoMaterial material;
	private ScoServico servico;
	private RapServidores funcionarioComprador;

	private Boolean voltarPanel = false;

	private List<PreItemPacVO> listaPreItensPac = new ArrayList<PreItemPacVO>();

	private List<PreItemPacVO> listaScSsAssocPreItensPac = new ArrayList<PreItemPacVO>();

	private List<PreItemPacVO> listaItensAdicionados = new ArrayList<PreItemPacVO>();

	private ScoSolicitacaoDeCompra solicitacaoCompra = null;
	private ScoSolicitacaoServico solicitacaoServico = null;
	
	@Inject
	private AssociaScSsPacController associaScSsPacController;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	// Método para pesquisa
	protected List<PreItemPacVO> listarItens() {

		try {
			this.listaPreItensPac = this.pacFacade.preSelecionarItensPac(this.caixa, this.funcionarioComprador, this.servico,
					this.material);

			if (listaPreItensPac == null) {
				listaPreItensPac = new ArrayList<PreItemPacVO>();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return listaPreItensPac;
	}

	// botões
	public void pesquisar() {
		this.setPesquisou(true);
		this.listarItens();
	}

	public void limparPesquisa() {
		this.setPesquisou(false);
		this.funcionarioComprador = null;
		this.listaPreItensPac = new ArrayList<PreItemPacVO>();
		this.caixa = null;
		this.material = null;
		this.servico = null;
		this.setListaScSsAssocPreItensPac(new ArrayList<PreItemPacVO>());
		this.setSolicitacaoCompra(null);
		this.setSolicitacaoServico(null);
		this.setListaItensAdicionados(new ArrayList<PreItemPacVO>());

	}

	public void abrirModalListarAssociadas(PreItemPacVO item) {
		this.limparAssociadas();
		if (item.isMarked()) {

			try {
				if (item.getQtdSC() != null) {
					this.setSolicitacaoCompra(this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(item.getNumero()));
					this.setSolicitacaoServico(null);
					this.getListaScSsAssocPreItensPac().addAll(this.pacFacade.listaAssociadasSCItensPac(item.getNumero(), null));
				} else {
					this.setSolicitacaoServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(item.getNumero()));
					this.setSolicitacaoCompra(null);
					this.getListaScSsAssocPreItensPac().addAll(this.pacFacade.listaAssociadasSSItensPac(item.getNumero(), null));
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}

	}

	public void limparAssociadas() {
		this.setListaScSsAssocPreItensPac(new ArrayList<PreItemPacVO>());
		this.setVoltarPanel(false);
	}

	public void adicionarScSSAssociadas() {
		for (PreItemPacVO preItemPacVo : this.getListaScSsAssocPreItensPac()) {
			if (preItemPacVo.isMarked()) {
				if (!this.listaPreItensPac.contains(preItemPacVo)) {
					this.listaPreItensPac.add(preItemPacVo);

				} else {
					this.listaPreItensPac.get(this.listaPreItensPac.indexOf(preItemPacVo)).setMarked(preItemPacVo.isMarked());
				}
			}
		}
		this.limparAssociadas();
	}

	// Métodos para carregar suggestions
	// Suggestion Caixa
	public List<ScoPontoParadaSolicitacao> listarCaixas(String pontoParada) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos(
				(String) pontoParada, false),listarCaixasCount(pontoParada));
	}

	public Integer listarCaixasCount(String pontoParada) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos(
				(String) pontoParada, false).size();
	}

	// Suggestion Material
	public Long listarMateriaisCount(String param) {
		return this.comprasFacade.listarScoMateriaisCount(param);
	}

	public List<ScoMaterial> listarMateriais(String param) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(param, null),listarMateriaisCount(param));
	}

	// Suggestion Serviço
	public List<ScoServico> listarServicos(String param) {
		return this.returnSGWithCount(this.solicitacaoServicoFacade.listarSuggestionServicos(param), listarServicosCount(param));
	}

	public Long listarServicosCount(String param) {
		return this.solicitacaoServicoFacade.listarSuggestionServicosCount(param);
	}
	
	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}

	public String cancelar() {
		return PAGE_ASSOCIA_SCSS_PAC;

	}

	public String adicionar() {
		for (PreItemPacVO preItemPacVo : this.listaPreItensPac) {
			if (preItemPacVo.isMarked()) {
				this.listaItensAdicionados.add(preItemPacVo);
			}
		}
		associaScSsPacController.setListaItensAdicionados(listaItensAdicionados);
		return PAGE_ASSOCIA_SCSS_PAC;

	}

	// gets and sets
	public ScoPontoParadaSolicitacao getCaixa() {
		return caixa;
	}

	public void setCaixa(ScoPontoParadaSolicitacao caixa) {
		this.caixa = caixa;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public List<PreItemPacVO> getListaPreItensPac() {
		return listaPreItensPac;
	}

	public void setListaPreItensPac(List<PreItemPacVO> listaPreItensPac) {
		this.listaPreItensPac = listaPreItensPac;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public List<PreItemPacVO> getListaScSsAssocPreItensPac() {
		return listaScSsAssocPreItensPac;
	}

	public void setListaScSsAssocPreItensPac(List<PreItemPacVO> listaScSsAssocPreItensPac) {
		this.listaScSsAssocPreItensPac = listaScSsAssocPreItensPac;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public String getMensAssocScSSPacO1() {
		String msg = getBundle().getString("MENSAGEM_ASSOCIAR_SC_SS_PAC_SC01");

		if (this.getSolicitacaoCompra() != null) {
			msg = msg.replace("{0}", this.getSolicitacaoCompra().getNumero().toString());
			msg = msg.replace("{1}", this.getSolicitacaoCompra().getDescricao());
		}

		if (this.getSolicitacaoServico() != null) {
			msg = msg.replace("{0}", this.getSolicitacaoServico().getNumero().toString());
			msg = msg.replace("{1}", this.getSolicitacaoServico().getDescricao());
		}

		return msg;
	}

	public List<PreItemPacVO> getListaItensAdicionados() {
		return listaItensAdicionados;
	}

	public void setListaItensAdicionados(List<PreItemPacVO> listaItensAdicionados) {
		this.listaItensAdicionados = listaItensAdicionados;
	}

	public RapServidores getFuncionarioComprador() {
		return funcionarioComprador;
	}

	public void setFuncionarioComprador(RapServidores funcionarioComprador) {
		this.funcionarioComprador = funcionarioComprador;
	}
}