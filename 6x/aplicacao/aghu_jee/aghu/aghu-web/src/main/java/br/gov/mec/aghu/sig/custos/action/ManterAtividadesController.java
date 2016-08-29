package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.patrimonio.IPatrimonioService;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;

/**
 * Classe para o Controller do cadastro das atividade e seus ítens.
 * 
 * @author agerling
 * 
 */


public class ManterAtividadesController extends ActionController {
	
	private static final String PESQUISAR_ATIVIDADES = "pesquisarAtividades";

	private static final long serialVersionUID = -1502017164335662569L;

	private static final String VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS = "visualizarObjetosCustoAssociados";

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;		

	@Inject
	private ManterEquipamentosAtividadeController equipamentosController;

	@Inject
	private ManterPessoasAtividadeController pessoasController;

	@Inject
	private ManterInsumosAtividadeController insumosController;

	@Inject
	private ManterServicosAtividadeController servicosController;

	private Integer seqAtividade;
	private SigAtividades atividade;
	private Map<String, Object> clone;
	private Integer tabSelecionada;
	private FccCentroCustos fccCentroCustos;
	
	private boolean adicionadoEquipamentoLote = false;
	private boolean adicionadoInsumosLote =  false;

	private static final Integer ABA_1 = 0;
	private static final Integer ABA_2 = 1;
	private static final Integer ABA_3 = 2;	

	private boolean retornoVisualizacaoObjCustoAssoc = false;
	
	private List<SigAtividadeCentroCustos> listaAtividadesCentroCustoExclusao;
	
	private boolean possuiAlteracao;
	
	private boolean mostrarBotaoVoltar = true;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 
		if(adicionadoEquipamentoLote){
			setTabSelecionada(ABA_3);
			return;
		}
		if(isAdicionadoInsumosLote()){
			setTabSelecionada(ABA_2);
			return;
		}
		
		if(retornoVisualizacaoObjCustoAssoc){
			return;
		}
		
		this.atividade = new SigAtividades();
		this.atividade.setIndSituacao(DominioSituacao.A);
		this.fccCentroCustos = null;
		
		Map<String, String[]> mapParams =   FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		if(mapParams.containsKey("seqAtividade")){
			this.seqAtividade = Integer.parseInt(mapParams.get("seqAtividade")[0]);
			this.mostrarBotaoVoltar = false;
		}
		
		if (this.seqAtividade != null) {
			this.clone = this.custosSigFacade.obterAtividadeDesatachado(this.seqAtividade);
			this.atividade = this.custosSigFacade.obterAtividade(this.seqAtividade);
			SigAtividadeCentroCustos atividadeCentroCustos = custosSigFacade.obterCentroCustoPorAtividade(seqAtividade);
			if(atividadeCentroCustos != null){
				this.fccCentroCustos = atividadeCentroCustos.getFccCentroCustos();
			}
		} else {
			try {
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				this.fccCentroCustos = servidorLogado.getCentroCustoAtuacao();
				if (fccCentroCustos == null) {
					this.fccCentroCustos = servidorLogado.getCentroCustoLotacao();
				}
			} catch (ApplicationBusinessException e) {
				fccCentroCustos=null;
			}
				
		}
		this.iniciaAbas();

		this.tabSelecionada = ABA_1;
	
	}

	public boolean verificaSePodeGravar(SigAtividades atividade, FccCentroCustos fccCentroCustos) {
		boolean retorno = true;
		List<SigAtividades> listaAtividades = this.custosSigFacade.pesquisarAtividades(fccCentroCustos);
		if (listaAtividades == null || listaAtividades.size() == 0) {
			retorno = true;
			return retorno;
		}
		for (SigAtividades sigAtividades : listaAtividades) {
			if (atividade.getSeq() == null && atividade.getNome().equalsIgnoreCase(sigAtividades.getNome())) {
				retorno = false;
				break;
			}
			if (atividade.getSeq() != null && atividade.getSeq().intValue() != sigAtividades.getSeq().intValue()
					&& atividade.getNome().equalsIgnoreCase(sigAtividades.getNome())) {
				retorno = false;
				break;
			}
		}
		return retorno;
	}
	
	private void setaCentroCusto(SigAtividades atividade, boolean isAddAtividade) {
		
		if(this.getFccCentroCustos() != null){
			
			listaAtividadesCentroCustoExclusao = null;
			
			//Somente cria os objetos para vincular a atividade ao centro de custo, se o mesmo foi selecionado
			if (isAddAtividade || atividade.getListSigAtividadeCentroCustos().isEmpty()) {
				SigAtividadeCentroCustos sig = new SigAtividadeCentroCustos();
				sig.setCriadoEm(new Date());
				sig.setFccCentroCustos(this.fccCentroCustos);
				sig.setIndSituacao(atividade.getIndSituacao());
				sig.setRapServidores(atividade.getRapServidores());
				sig.setSigAtividades(atividade);
				atividade.getListSigAtividadeCentroCustos().add(sig);
			} else {
	
				for (SigAtividadeCentroCustos sigAtividadeCentroCustos : atividade.getListSigAtividadeCentroCustos()) {
					// se alterou o Centro de Custo, atualiza criadoEm e rapServidor
					if (sigAtividadeCentroCustos.getFccCentroCustos() != null && this.fccCentroCustos != null && !sigAtividadeCentroCustos.getFccCentroCustos().getCodigo().equals(this.fccCentroCustos.getCodigo())) {
						sigAtividadeCentroCustos.setRapServidores(atividade.getRapServidores());
						sigAtividadeCentroCustos.setCriadoEm(new Date());
					}
					sigAtividadeCentroCustos.setFccCentroCustos(this.fccCentroCustos);
					break;
				}
			}
		}
		else{
			listaAtividadesCentroCustoExclusao = new ArrayList<SigAtividadeCentroCustos>(atividade.getListSigAtividadeCentroCustos());
			atividade.getListSigAtividadeCentroCustos().clear();
		}
	}
	

	private void preparaAtividadeParaGravar(SigAtividades atividade, boolean isAddAtividade) throws ApplicationBusinessException {
		atividade.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		if (isAddAtividade) {
			atividade.setCriadoEm(new Date());
			setaCentroCusto(atividade, isAddAtividade);
		} else {
			setaCentroCusto(atividade, isAddAtividade);
		}

	}

	/**
	 * Valida se os equipamentos pertencem ao mesmo centro de custo da atividade caso a atividade possua um centro de custo,
	 * essa validação eh feita atraves da consulta dos equipamentos no sistema de patrimonio.
	 * @return true se os equipamentos pertencem, false caso contrário
	 * @throws ApplicationBusinessException - Exeção de negócio
	 */
	private boolean validaEquipamentoPertenceAoCentroCustoAtividade() throws ApplicationBusinessException {
		boolean retorno = true;
		if(fccCentroCustos != null){
			List<SigAtividadeEquipamentos> equipamentosTesteCentroCusto = this.equipamentosController.getListEquipamentoAtividade();
			if(equipamentosTesteCentroCusto != null && this.equipamentosController.isIntegracaoPatrimonioOnline()){
				for (SigAtividadeEquipamentos sigAtividadeEquipamentos : equipamentosTesteCentroCusto) {
					EquipamentoSistemaPatrimonioVO equipamento =  custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(sigAtividadeEquipamentos.getCodPatrimonio(), fccCentroCustos.getCodigo());
				
					if(equipamento.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_NAO_ENCONTRADO 
							|| equipamento.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_PERTENCE_OUTRO_CC) {
						
						retorno = false;
						break;
					}
				}
			}
		}
		return retorno;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void gravar() throws ApplicationBusinessException {
		
		if(!validaEquipamentoPertenceAoCentroCustoAtividade()){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EQUIPAMENTO_ASSOCIADO_DIFERENTE_CC");
			return;
		}

		boolean isAddAtividade = (this.seqAtividade == null);
		if (isAddAtividade) {
			preparaAtividadeParaGravar(this.atividade, isAddAtividade);
			if (verificaSePodeGravar(this.atividade, this.fccCentroCustos)) {
				this.custosSigFacade.persist(this.atividade);
				this.seqAtividade = atividade.getSeq();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICIONAR_ATIVIDADE", this.atividade.getNome());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_JA_EXISTE_CENTRO_CUSTO", this.fccCentroCustos.getDescricao());
				return;
			}
		} else {
			preparaAtividadeParaGravar(this.atividade, isAddAtividade);
			if (verificaSePodeGravar(this.atividade, this.fccCentroCustos)) {
				this.custosSigFacade.persist(this.atividade);
				this.custosSigFacade.iniciaProcessoHistoricoAtividade(this.atividade, this.clone, 
						this.custosSigFacade.pesquisarObjetoCustoComposicaoAtivoObjetoCustoVersaoAtivo(this.atividade), 
						this.pessoasController.getListaPessoas(), this.equipamentosController.getListEquipamentoAtividade(), 
						this.insumosController.getListAtividadeInsumos(), this.servicosController.getListaServicos(), 
						this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_ATIVIDADE", this.atividade.getNome());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_JA_EXISTE_CENTRO_CUSTO", fccCentroCustos.getDescricao());
				return;
			}
		}

		//Exclui os centros de custo da atividade, se eles existirem
		this.custosSigFacade.removerAtividadeCentroCustos(this.listaAtividadesCentroCustoExclusao);
		
		// Alteração / Inclusão Pessoas
		List<SigAtividadePessoas> pessoas = pessoasController.getListaPessoas();
		for (SigAtividadePessoas sigAtividadePessoas : pessoas) {
			sigAtividadePessoas.setSigAtividades(atividade);
			this.custosSigFacade.persistPessoa(sigAtividadePessoas);
		}

		// Exclusão Pessoas
		List<SigAtividadePessoas> pessoasExcluir = pessoasController.getListaPessoasExcluir();
		for (SigAtividadePessoas sigAtividadePessoas : pessoasExcluir) {
			this.custosSigFacade.excluirPessoa(sigAtividadePessoas);
		}

		// Alteração / Inclusão Equipamentos
		List<SigAtividadeEquipamentos> equipamentos = equipamentosController.getListEquipamentoAtividade();
		for (SigAtividadeEquipamentos sigAtividadeEquipamentos : equipamentos) {
			sigAtividadeEquipamentos.setSigAtividades(atividade);
			this.custosSigFacade.persistEquipamento(sigAtividadeEquipamentos);
		}

		// Exclusão Equipamentos
		equipamentos = equipamentosController.getListEquipamentoAtividadeExclusao();
		for (SigAtividadeEquipamentos sigAtividadeEquipamentos : equipamentos) {
			sigAtividadeEquipamentos.setSigAtividades(atividade);
			this.custosSigFacade.excluirEquipamentoAtividade(sigAtividadeEquipamentos);
		}

		// Alteração / Inclusão Insumos
		List<SigAtividadeInsumos> listInsumos = insumosController.getListAtividadeInsumos();
		for (SigAtividadeInsumos insumo : listInsumos) {
			insumo.setSigAtividades(atividade);
			this.custosSigFacade.persistInsumo(insumo);
		}

		// Exclusão Insumos
		List<SigAtividadeInsumos> listInsumosExcluir = insumosController.getListAtividadeInsumosExclusao();
		for (SigAtividadeInsumos insumo : listInsumosExcluir) {
			this.custosSigFacade.excluirInsumo(insumo);
		}

		// Alteração / Inclusão Serviços
		List<SigAtividadeServicos> listServicos = servicosController.getListaServicos();
		for (SigAtividadeServicos servico : listServicos) {
			servico.setSigAtividades(atividade);
			this.custosSigFacade.persistServico(servico);
		}

		// Exclusão Serviços
		List<SigAtividadeServicos> listServicosExcluir = servicosController.getListaServicosExcluir();
		for (SigAtividadeServicos servico : listServicosExcluir) {
			this.custosSigFacade.excluirServico(servico);
		}

		this.servicosController.setPossuiAlteracao(false);
		this.insumosController.setPossuiAlteracao(false);
		this.equipamentosController.setPossuiAlteracao(false);
		this.pessoasController.setPossuiAlteracao(false);
		this.clone = this.custosSigFacade.obterAtividadeDesatachado(this.seqAtividade);
		this.iniciar();
	}

	private void iniciaAbas() {
		this.setPossuiAlteracao(false);
		this.inicializarPessoal();
		this.inicializarInsumos();
		this.inicializarEquipamentos();
		this.incializarServicos();
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, DominioSituacao.A);
	}

	public void limparCentroCusto() {
		this.setFccCentroCustos(null);
		this.marcarAlteracao();
	}

	// Aba Pessoal
	private void inicializarPessoal() {
		this.pessoasController.iniciarAbaPessoal(this.seqAtividade);
	}

	// Aba Insumos
	private void inicializarInsumos() {
		this.insumosController.iniciarAbaInsumos(this.seqAtividade);
	}

	// Aba Equipamentos
	private void inicializarEquipamentos() {
		this.equipamentosController.iniciarAbaEquipamentos(this.seqAtividade);
	}

	// Aba Serviços
	private void incializarServicos() {
		this.servicosController.iniciarAbaServicos(this.seqAtividade);
	}

	public String verificaAlteracaoNaoSalva() {
		if (this.isPossuiAlteracao() || pessoasController.isPossuiAlteracao() || equipamentosController.isPossuiAlteracao() || insumosController.isPossuiAlteracao()
				|| servicosController.isPossuiAlteracao()) {
			this.openDialog("modalConfirmacaoVoltarWG");
			return null;
		} else {
			return this.voltar();
		}
	}

	public String voltar() {
		this.seqAtividade = null;
		this.adicionadoEquipamentoLote = false;
		this.setAdicionadoInsumosLote(false);
		return PESQUISAR_ATIVIDADES;
	}
	
	public String visualizarObjetosCustoAssociados(){
		return VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS;
	}
	
	public void marcarAlteracao(){
		this.setPossuiAlteracao(true);
	}

	// Getters and Setters

	public ManterEquipamentosAtividadeController getEquipamentosController() {
		return equipamentosController;
	}

	public void setEquipamentosController(ManterEquipamentosAtividadeController equipamentosController) {
		this.equipamentosController = equipamentosController;
	}

	public ManterPessoasAtividadeController getPessoasController() {
		return pessoasController;
	}

	public void setPessoasController(ManterPessoasAtividadeController pessoasController) {
		this.pessoasController = pessoasController;
	}

	public Integer getSeqAtividade() {
		return seqAtividade;
	}

	public void setSeqAtividade(Integer seqAtividade) {
		this.seqAtividade = seqAtividade;
	}

	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}

	public Integer getTabSelecionada() {
		return tabSelecionada;
	}

	public void setTabSelecionada(Integer tabSelecionada) {
		this.tabSelecionada = tabSelecionada;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public Map<String, Object> getClone() {
		return clone;
	}

	public void setClone(Map<String, Object> clone) {
		this.clone = clone;
	}

	public boolean isAdicionadoEquipamentoLote() {
		return adicionadoEquipamentoLote;
	}

	public void setAdicionadoEquipamentoLote(boolean adicionadoEquipamentoLote) {
		this.adicionadoEquipamentoLote = adicionadoEquipamentoLote;
	}

	public boolean isRetornoVisualizacaoObjCustoAssoc() {
		return retornoVisualizacaoObjCustoAssoc;
	}

	public void setRetornoVisualizacaoObjCustoAssoc(
			boolean retornoVisualizacaoObjCustoAssoc) {
		this.retornoVisualizacaoObjCustoAssoc = retornoVisualizacaoObjCustoAssoc;
	}
	
	public boolean isPossuiAlteracao() {
		return possuiAlteracao;
	}

	public void setPossuiAlteracao(boolean possuiAlteracao) {
		this.possuiAlteracao = possuiAlteracao;
	}

	public boolean isAdicionadoInsumosLote() {
		return adicionadoInsumosLote;
	}

	public void setAdicionadoInsumosLote(boolean adicionadoInsumosLote) {
		this.adicionadoInsumosLote = adicionadoInsumosLote;
	}

	public boolean isMostrarBotaoVoltar() {
		return mostrarBotaoVoltar;
	}

	public void setMostrarBotaoVoltar(boolean mostrarBotaoVoltar) {
		this.mostrarBotaoVoltar = mostrarBotaoVoltar;
	}

}
