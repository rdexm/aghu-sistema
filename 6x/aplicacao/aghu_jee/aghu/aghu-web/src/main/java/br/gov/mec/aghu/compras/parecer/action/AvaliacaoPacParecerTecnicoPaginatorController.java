package br.gov.mec.aghu.compras.parecer.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AvaliacaoPacParecerTecnicoPaginatorController extends ActionController implements ActionPaginator {

	

	private static final String AVALIACAO_PROPOSTAS_PARECER_TECNICO_LIST = "compras-avaliacaoPropostasParecerTecnicoList";
	private static final String ANALISE_TECNICA_ITEM_PROPOSTA_FORNECEDOR = "analiseTecnicaItemPropostaFornecedorCRUD";
	@Inject @Paginator
	private DynamicDataModel<ScoLicitacao> dataModel;

	private static final long serialVersionUID = -729445225588120797L;

	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	
	// filtros
	Integer numeroPac;
	String descricaoPac;
	ScoModalidadeLicitacao modalidadePac;
	Boolean vencida;
	
	// checkbox
	private Map<Integer, Boolean> pacsSelecionados = new HashMap<Integer, Boolean>();	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 
      		
		//this.limpar(false);
		//this.//setIgnoreInitPageConfig(true);
		
	
	}
	
	// botoes
	public void pesquisar() {
		this.pacsSelecionados = new HashMap<Integer, Boolean>();
		this.reiniciarPaginator();
	}
	
	public void encaminharComprador() {
		try {			
			Set<Integer> numerosSelecionados = new HashSet<Integer>();
			for (Integer num : this.pacsSelecionados.keySet()) {
				if (this.pacsSelecionados.get(num)) {
					numerosSelecionados.add(num);
				}
			}
			pacFacade.encaminharComprador(numerosSelecionados, true);

			if (this.numeroPac != null) {
				this.numeroPac = null;
			}
			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_AVALIACAOPACPT_MSG01");
			
			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar(Boolean updAtivo) {
		if (updAtivo) {
			this.setAtivo(false);
		}
		this.numeroPac = null;
		this.descricaoPac = null;
		this.modalidadePac = null;
		this.vencida = Boolean.FALSE;
		this.pacsSelecionados = new HashMap<Integer, Boolean>();
	}
	

	// suggestions
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter);
	}
	
	
	
	// metodos auxilio
	
	public void togglePacSelecionado(Integer pacId) {
		if (this.pacsSelecionados.containsKey(pacId) && !this.pacsSelecionados.get(pacId)) {
			this.pacsSelecionados.remove(pacId);
		} else {
			this.pacsSelecionados.put(pacId, true);
		}
	}
	
	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public String verificarParecerTecnico(){
		return AVALIACAO_PROPOSTAS_PARECER_TECNICO_LIST;
	}
	
	public String avaliarMarcaItem(){
		return ANALISE_TECNICA_ITEM_PROPOSTA_FORNECEDOR;
	}
	// paginator

	@Override
	public Long recuperarCount() {
		return this.pacFacade.contarLicitacaoParecerTecnico(this.numeroPac, this.descricaoPac, this.modalidadePac, this.vencida);	
	}

	@Override
	public List<ScoLicitacao> recuperarListaPaginada(Integer first, Integer max,
			String order, boolean asc) {		
		return this.pacFacade.pesquisarLicitacaoParecerTecnico(first, max, order, asc, 
				this.numeroPac, this.descricaoPac, this.modalidadePac, this.vencida);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	
	// Getters/Setters
	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IPacFacade getPacFacade() {
		return pacFacade;
	}

	public void setPacFacade(IPacFacade pacFacade) {
		this.pacFacade = pacFacade;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}

	public ScoModalidadeLicitacao getModalidadePac() {
		return modalidadePac;
	}

	public void setModalidadePac(ScoModalidadeLicitacao modalidadePac) {
		this.modalidadePac = modalidadePac;
	}

	public Boolean getVencida() {
		return vencida;
	}

	public void setVencida(Boolean vencida) {
		this.vencida = vencida;
	}

	public void setPacsSelecionados(Map<Integer, Boolean> pacsSelecionados) {
		this.pacsSelecionados = pacsSelecionados;
	}
	
	public Map<Integer, Boolean> getPacsSelecionados() {
		return pacsSelecionados;
	} 

	public DynamicDataModel<ScoLicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLicitacao> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}