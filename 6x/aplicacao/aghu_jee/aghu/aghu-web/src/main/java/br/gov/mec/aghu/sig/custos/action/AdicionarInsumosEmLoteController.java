package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * Controller que gerencia a inclusão de multiplos insumos(materiais e/ou medicamentos) na atividade.
 * 
 * estoria redmine #26619
 * 
 * @author jgugel
 *
 */

public class AdicionarInsumosEmLoteController extends ActionController {

	private static final String MANTER_ATIVIDADES = "manterAtividades";
	private static final long serialVersionUID = 6649612877148582990L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@Inject
	private ManterInsumosAtividadeController insumosController;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	private SigAtividades atividade;
	private FccCentroCustos centroCusto;

	private String nomeInsumo;
	
	private boolean marcarLote;
	private boolean marcouTodos;
	private boolean ativo = false;

	private List<SigAtividadeInsumos> listAtividadeInsumos;
	private List<SigAtividadeInsumos> listAtividadeInsumosAdicionadoLote;
	
	
	private List<ScoUnidadeMedida> listUnidadeMedida = new ArrayList<ScoUnidadeMedida>();
	private String codigoUnidadeMedida;

	private SigAtividadeInsumos atividadeInsumos;



	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		this.setNomeInsumo("");
		this.listAtividadeInsumos = new ArrayList<SigAtividadeInsumos>();
		this.listAtividadeInsumosAdicionadoLote = null;
		this.atividadeInsumos = new SigAtividadeInsumos();
		this.marcarLote = false;
		this.setMarcouTodos(false);
		this.ativo = false;
	}

	/**
	 * Busca os Materiais ativos, pesquisa limitada a 100 e seta os itens de gerenciamento da controller.
	 * @throws ApplicationBusinessException
	 */
	public void pesquisar() throws ApplicationBusinessException {
		this.marcarLote = false;
		this.ativo = true;
		this.setListAtividadeInsumos(new ArrayList<SigAtividadeInsumos>());
		List<ScoMaterial> listRetorno = this.comprasFacade.listarScoMateriaisAtivos(nomeInsumo);
		for (ScoMaterial scoMaterial : listRetorno) {
			SigAtividadeInsumos atividadeInsumo = new SigAtividadeInsumos();
			atividadeInsumo.setMaterial(scoMaterial);
			atividadeInsumo.setIndSituacao(DominioSituacao.A);
			getListAtividadeInsumos().add(atividadeInsumo);
		}
	}

	/**
	 * Retorna para a tela de manter atividades sem incluir nenhum insumo.
	 * 
	 */
	public String cancelar() {
		insumosController.getManterAtividadesController().setAdicionadoInsumosLote(true);
		return MANTER_ATIVIDADES;
	}

	/**
	 *
	 * Adiciona os insumos do selecionados no lote.
	 * 
	 * @return Redireciona para a tela de atividades, caso não ocorra nenhum erro ao validar os insumos.
	 * @throws ApplicationBusinessException
	 * @author jgugel
	 */
	public String adicionar() throws ApplicationBusinessException {
		listAtividadeInsumosAdicionadoLote = new ArrayList<SigAtividadeInsumos>();
		
		for (SigAtividadeInsumos insumoIt : listAtividadeInsumos) {
			if(insumoIt.getSelected()){
				insumoIt.setCriadoEm(new Date());
				insumoIt.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				insumoIt.setIndSituacao(DominioSituacao.A);
				insumoIt.setQtdeUso(atividadeInsumos.getQtdeUso());
				insumoIt.setUnidadeMedida(atividadeInsumos.getUnidadeMedida());
				insumoIt.setVidaUtilQtde(atividadeInsumos.getVidaUtilQtde());
				insumoIt.setVidaUtilTempo(atividadeInsumos.getVidaUtilTempo());
				insumoIt.setDirecionadores(atividadeInsumos.getDirecionadores());
				listAtividadeInsumosAdicionadoLote.add(insumoIt);
			}
		}
		
		if(listAtividadeInsumosAdicionadoLote != null && !listAtividadeInsumosAdicionadoLote.isEmpty()){
			try{
				custosSigFacade.validarAdicaoDeInsumosEmLote(atividadeInsumos);
			}catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}	
			
			insumosController.adicionaEquipamentosEmLote(listAtividadeInsumosAdicionadoLote);
			insumosController.getManterAtividadesController().setAdicionadoInsumosLote(true);
			insumosController.setPossuiAlteracao(true);
			
			return MANTER_ATIVIDADES;
			
		}else{
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_INCLUSAO_LOTE_SEM_INSUMOS");
			return null;
			
		}
		
	}
	
	/**
	 * Metodo que faz o calculo do custo médio do material
	 * @param insumo
	 * @return BigDecimal com o calculo.
	 */
	public BigDecimal efetuarCalculoCustoMedioMaterial(SigAtividadeInsumos insumo){
		return custosSigFacade.efetuarCalculoCustoMedioMaterial(insumo.getMaterial());
	}
	
	/**
	 * Atualiza na tela as possiveis unidades de medidas que o usuário pode vincular com o material
	 * 
	 */
	public void alterarUnidadeMedida(){
		getAtividadeInsumos().setUnidadeMedida(null);
		if( this.getCodigoUnidadeMedida() != null && !this.getCodigoUnidadeMedida().equals("")){
			for(ScoUnidadeMedida unidadeMedida : listUnidadeMedida){
				if(unidadeMedida.getCodigo().equals(this.getCodigoUnidadeMedida())){
					getAtividadeInsumos().setUnidadeMedida(unidadeMedida);
				}
			}
		}
	}

	/**
	 * Gerenciamento interno da controller, insumos marcados ou desmarcados
	 * @param insumo Insumo que foi marcado ou desmarcado
	 */
	public void selectedAdicionarInsumoAtividade(SigAtividadeInsumos insumo) {
		for (SigAtividadeInsumos insumoIt : listAtividadeInsumos) {
			if (insumoIt.getMaterial().getCodigo() == insumo.getMaterial().getCodigo()) {
				insumoIt.setSelected(insumo.getSelected());
				return;
			}
		}
	}

	public void selecionaLote() {
		marcarLote = !this.marcarLote;//Necessário já que o p:ajax não estava enviando o valor atual do checkbox
		marcouTodos = this.marcarLote;
		for (SigAtividadeInsumos insumoIt : listAtividadeInsumos) {
			insumoIt.setSelected(marcouTodos);
		}
	}
	
	public List<SigDirecionadores> listarDirecionador(){
		return custosSigCadastrosBasicosFacade.pesquisarDirecionadoresTempoMaiorMes();
	}

	/**
	 * Atualiza na tela as possiveis unidades de medidas que o usuário pode vincular com o material
	 * 
	 */
	public List<SelectItem> listarItensUnidadeMedida(){
		carregarUnidadeMedida();
		List<SelectItem> itens = new ArrayList<SelectItem>();
		HashSet<ScoUnidadeMedida> hash = new HashSet<ScoUnidadeMedida>(listUnidadeMedida);  
		for(ScoUnidadeMedida unidadeMedida : hash){
			itens.add( new SelectItem(unidadeMedida.getCodigo(), unidadeMedida.getDescricao() ));
		}
		this.setCodigoUnidadeMedida(null);
		for (SigAtividadeInsumos insumoIt : listAtividadeInsumos) {
			if(insumoIt.getSelected()){
				if(insumoIt != null && insumoIt.getUnidadeMedida() != null ){
					this.setCodigoUnidadeMedida(insumoIt.getUnidadeMedida().getCodigo());
				}
			}
		}
		return itens;
	}
	
	/**
	 * Atualiza na tela as possiveis unidades de medidas que o usuário pode vincular com o material
	 * 
	 */
	public void carregarUnidadeMedida(){
		setListUnidadeMedida(new ArrayList<ScoUnidadeMedida>()); 
		List<SceConversaoUnidadeConsumos> listConversaoUnidadeConsumos = new ArrayList<SceConversaoUnidadeConsumos>();
		for (SigAtividadeInsumos insumoIt : listAtividadeInsumos) {
			if(insumoIt.getSelected()){
				if(insumoIt != null && insumoIt.getMaterial() != null && insumoIt.getMaterial().getCodigo() != null){
					getListUnidadeMedida().add(insumoIt.getMaterial().getUnidadeMedida());
					listConversaoUnidadeConsumos = estoqueFacade.listarConversaoUnidadeConsumo(insumoIt.getMaterial().getCodigo());
					for(SceConversaoUnidadeConsumos conversUniConsumo : listConversaoUnidadeConsumos){
						if(conversUniConsumo.getUnidadeMedida() != null && !getListUnidadeMedida().contains(conversUniConsumo.getUnidadeMedida())){
							getListUnidadeMedida().add(conversUniConsumo.getUnidadeMedida());
						}
					}
					if(getListUnidadeMedida() != null &&
					   getListUnidadeMedida().size() > 0){
						Collections.sort(getListUnidadeMedida(), unidadeMedidaComparator);
					}
				}
			}
		}
	}
	
	private static UnidadeMedidaComparator unidadeMedidaComparator = new UnidadeMedidaComparator();

	/**
	 * Utilizado para controlar as unidades de medidas disponiveis para o usuario
	 * @author jgugel
	 *
	 */
	private static class UnidadeMedidaComparator implements Comparator<ScoUnidadeMedida> {
		@Override
		public int compare(ScoUnidadeMedida e1, ScoUnidadeMedida e2) {
			return e1.getDescricao().compareToIgnoreCase(e2.getDescricao());
		}
	}

	// Getters and Setters
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getNomeInsumo() {
		return nomeInsumo;
	}

	public void setNomeInsumo(String nomeInsumo) {
		this.nomeInsumo = nomeInsumo;
	}

	public List<SigAtividadeInsumos> getListAtividadeInsumos() {
		return listAtividadeInsumos;
	}

	public void setListAtividadeInsumos(List<SigAtividadeInsumos> listAtividadeInsumos) {
		this.listAtividadeInsumos = listAtividadeInsumos;
	}

	public List<SigAtividadeInsumos> getListAtividadeInsumosAdicionadoLote() {
		return listAtividadeInsumosAdicionadoLote;
	}

	public void setListAtividadeInsumosAdicionadoLote(List<SigAtividadeInsumos> listAtividadeInsumosAdicionadoLote) {
		this.listAtividadeInsumosAdicionadoLote = listAtividadeInsumosAdicionadoLote;
	}

	public List<ScoUnidadeMedida> getListUnidadeMedida() {
		return listUnidadeMedida;
	}

	public void setListUnidadeMedida(List<ScoUnidadeMedida> listUnidadeMedida) {
		this.listUnidadeMedida = listUnidadeMedida;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public SigAtividadeInsumos getAtividadeInsumos() {
		return atividadeInsumos;
	}

	public void setAtividadeInsumos(SigAtividadeInsumos atividadeInsumos) {
		this.atividadeInsumos = atividadeInsumos;
	}

	public boolean isMarcouTodos() {
		return marcouTodos;
	}

	public void setMarcouTodos(boolean marcouTodos) {
		this.marcouTodos = marcouTodos;
	}

}
