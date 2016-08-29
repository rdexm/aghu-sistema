package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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


public class ManterInsumosAtividadeController extends ActionController {

	private static final String ADICIONAR_INSUMOS_LOTE = "adicionarInsumosLote";

	private static final long serialVersionUID = -206381167870245671L;

	private static final Log LOG = LogFactory.getLog(ManterInsumosAtividadeController.class);
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private ManterAtividadesController manterAtividadesController;

	@Inject
	private AdicionarInsumosEmLoteController adicionarInsumosEmLoteController;

	
	private List<SigAtividadeInsumos> listAtividadeInsumos;
	private List<SigAtividadeInsumos> listAtividadeInsumosExclusao;

	private SigAtividadeInsumos atividadeInsumos;

	private static UnidadeMedidaComparator unidadeMedidaComparator = new UnidadeMedidaComparator();
	
	private boolean emEdicao;
	private Integer codigoMaterial;
	private boolean atividadeJaAdicionada;

	private boolean possuiAlteracao;

	private SigAtividades atividade;
	
	private Integer indexOfObjEdicao;
	
	private Long seqInsumoExclusao;
	private Integer codigoInsumoExclusao;
	
	private List<ScoUnidadeMedida> listUnidadeMedida = new ArrayList<ScoUnidadeMedida>();
	
	private String codigoUnidadeMedida;
	
	private static final Integer ABA_2 = 1; 
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciarAbaInsumos(Integer seqAtividade) {
		setAtividadeInsumos(new SigAtividadeInsumos());
		getAtividadeInsumos().setIndSituacao(DominioSituacao.A);
		setListAtividadeInsumos(new ArrayList<SigAtividadeInsumos>());
		setListAtividadeInsumosExclusao(new ArrayList<SigAtividadeInsumos>());
		setPossuiAlteracao(false);
		setEmEdicao(false);
		setCodigoMaterial(null);
		this.setAtividadeJaAdicionada(false);

		
		if(seqAtividade != null){
			atividade = custosSigFacade.obterAtividade(seqAtividade);
			setListAtividadeInsumos(custosSigFacade.pesquisarAtividadeInsumos(seqAtividade));
			this.setAtividadeJaAdicionada(true);

		}

	}
	
	public List<ScoMaterial> listarMaterial(String objPesquisa){
		return this.returnSGWithCount(comprasFacade.listarScoMateriaisAtiva(objPesquisa),listarMaterialCount(objPesquisa));
	}

	public Integer listarMaterialCount(String objPesquisa){
		return comprasFacade.listarScoMateriaisAtiva(objPesquisa).size();
	}
	
	public void carregarUnidadeMedida(){
		
		listUnidadeMedida = new ArrayList<ScoUnidadeMedida>(); 
		
		List<SceConversaoUnidadeConsumos> listConversaoUnidadeConsumos = new ArrayList<SceConversaoUnidadeConsumos>();
		
		if(atividadeInsumos != null &&
		   atividadeInsumos.getMaterial() != null &&
		   atividadeInsumos.getMaterial().getCodigo() != null){
			
			listUnidadeMedida.add(atividadeInsumos.getMaterial().getUnidadeMedida());
			listConversaoUnidadeConsumos = estoqueFacade.listarConversaoUnidadeConsumo(atividadeInsumos.getMaterial().getCodigo());
			
			for(SceConversaoUnidadeConsumos conversUniConsumo : listConversaoUnidadeConsumos){
				if(conversUniConsumo.getUnidadeMedida() != null){
					listUnidadeMedida.add(conversUniConsumo.getUnidadeMedida());
				}
			}
			
			if(listUnidadeMedida != null &&
			   listUnidadeMedida.size() > 0){
				Collections.sort(listUnidadeMedida, unidadeMedidaComparator);
			}
		}
	}
	
	public String adicionarInsumoLote(){
		adicionarInsumosEmLoteController.setAtividade(atividade);
		adicionarInsumosEmLoteController.setCentroCusto(getFccCentroCusto());
		return ADICIONAR_INSUMOS_LOTE;
	}
	
	/**
	 * Efetua a validação dos insumos adicionados na tela de lote, para ver se os mesmo já não estão vinculados a atividade,
	 * se os mesmo já estão não são adicionados.
	 * @param listInsumos
	 */
	public void adicionaEquipamentosEmLote(List<SigAtividadeInsumos> listInsumos){
		boolean isItemRepetido = false;
		for (SigAtividadeInsumos sigAtividadeInsumos : listInsumos) {
			try{
				custosSigFacade.validarInclusaoInsumoAtividade(sigAtividadeInsumos, getListAtividadeInsumos());
				getListAtividadeInsumos().add(sigAtividadeInsumos);
			}catch (ApplicationBusinessException e) {
				isItemRepetido = true;
			}
		}
		if(isItemRepetido){
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INSUMO_JA_ADICIONADO_LOTE_FAZ_PARTE_CADASTRO");

		}
	}
	
	public List<ScoUnidadeMedida> listarUnidadeMedida(Object objPesquisa){
		
		carregarUnidadeMedida();
		
		if(!listUnidadeMedida.isEmpty()){
		
			//Pesquisa pelo código ou descrição preenchida
			String srtPesquisa = (String) objPesquisa;
			
			if (StringUtils.isNotBlank(srtPesquisa)) {
				
				for(ScoUnidadeMedida unidadeMedida : listUnidadeMedida){
					
					if(unidadeMedida.getCodigo().equalsIgnoreCase(srtPesquisa) || 
					   unidadeMedida.getDescricao().equalsIgnoreCase(srtPesquisa)){
						
						List<ScoUnidadeMedida> list = new ArrayList<ScoUnidadeMedida>();
						list.add(unidadeMedida);
						return list;
					}
				}
			}
			
			List<ScoUnidadeMedida> list = new ArrayList<ScoUnidadeMedida>(listUnidadeMedida);
			return list;
		}
		
		return null;
	}
	
	public List<SelectItem> listarItensUnidadeMedida(){
		
		carregarUnidadeMedida();
		List<SelectItem> itens = new ArrayList<SelectItem>();
		
		for(ScoUnidadeMedida unidadeMedida : listUnidadeMedida){
			itens.add( new SelectItem(unidadeMedida.getCodigo(), unidadeMedida.getDescricao() ));
		}
		
		this.setCodigoUnidadeMedida(null);
		if(this.getAtividadeInsumos() != null && this.getAtividadeInsumos().getUnidadeMedida() != null ){
			this.setCodigoUnidadeMedida(this.getAtividadeInsumos().getUnidadeMedida().getCodigo());
		}
		
		if(listUnidadeMedida != null && listUnidadeMedida.size() == 1){
			this.setCodigoUnidadeMedida(listUnidadeMedida.get(0).getCodigo().toString());
			alterarUnidadeMedida();
		}
		
		return itens;
	}
	
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
	
	public List<SigDirecionadores> listarDirecionador(){
		return custosSigCadastrosBasicosFacade.pesquisarDirecionadoresTempoMaiorMes();
	}
	
	public void adicionarInsumo(){
		
		try{
			setPossuiAlteracao(true);
			
			custosSigFacade.validarInclusaoInsumoAtividade(getAtividadeInsumos(), getListAtividadeInsumos());
			
			if(getAtividadeInsumos().getSeq() == null){
				getAtividadeInsumos().setCriadoEm(new Date());
			}
			
			getAtividadeInsumos().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			
			getListAtividadeInsumos().add(getAtividadeInsumos());

			setAtividadeInsumos(new SigAtividadeInsumos());
			getAtividadeInsumos().setIndSituacao(DominioSituacao.A);
			manterAtividadesController.setTabSelecionada(ABA_2);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 		
	}
	
	public void editarInsumo(SigAtividadeInsumos atividadeInsumos, Integer indice){
		
		setPossuiAlteracao(true);
		setEmEdicao(true);
		setCodigoMaterial(atividadeInsumos.getMaterial().getCodigo());
		
		setIndexOfObjEdicao(indice);
		
		try{
			setAtividadeInsumos((SigAtividadeInsumos) atividadeInsumos.clone());
		}catch (CloneNotSupportedException e) {
			LOG.error("A classe SigAtividadeInsumos "
					+ "não implementa a interface Cloneable.", e);
		}
		
		manterAtividadesController.setTabSelecionada(ABA_2);
	}
	
	public Integer getIndexOfObjEdicao() {
		return indexOfObjEdicao;
	}

	public void setIndexOfObjEdicao(Integer indexOfObjEdicao) {
		this.indexOfObjEdicao = indexOfObjEdicao;
	}
	
	public void gravarInsumo(){
		try{
			setPossuiAlteracao(true);
			custosSigFacade.validarAlteracaoInsumoAtividade(getAtividadeInsumos(), getListAtividadeInsumos());
			setEmEdicao(false);
			setCodigoMaterial(null);
			getAtividadeInsumos().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			getListAtividadeInsumos().set(getIndexOfObjEdicao(), getAtividadeInsumos());
			setAtividadeInsumos(new SigAtividadeInsumos());
			getAtividadeInsumos().setIndSituacao(DominioSituacao.A);
			manterAtividadesController.setTabSelecionada(ABA_2);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicaoInsumo(){
		setEmEdicao(false);
		setCodigoMaterial(null);
		setAtividadeInsumos(new SigAtividadeInsumos());
		manterAtividadesController.setTabSelecionada(ABA_2);
		setIndexOfObjEdicao(null);
	}
	
	public void excluirInsumo(){
		manterAtividadesController.setTabSelecionada(ABA_2);
		setPossuiAlteracao(true);
		if(seqInsumoExclusao == null) {
			for(int i=0; i < this.getListAtividadeInsumos().size(); i++){
				SigAtividadeInsumos insumo = this.getListAtividadeInsumos().get(i);
				if(insumo.getMaterial() != null &&
						insumo.getMaterial().getCodigo().equals(codigoInsumoExclusao)){
						getListAtividadeInsumos().remove(i);
					break;
				}
			}
		} else {
			for(int i=0; i < this.getListAtividadeInsumos().size(); i++){
				SigAtividadeInsumos insumo = this.getListAtividadeInsumos().get(i);
				if(insumo.getSeq().equals(seqInsumoExclusao)){
					if(atividade != null && custosSigFacade.verificaAtividadeEstaVinculadaAoObjetoCusto(atividade)){
						seqInsumoExclusao = null;
						codigoInsumoExclusao = null;
						this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EXCLUSAO_INSUMO_ATIVIDADE_OBJETO_CUSTO");
						manterAtividadesController.setTabSelecionada(ABA_2);
						return;
					}
					getListAtividadeInsumos().remove(insumo);
					getListAtividadeInsumosExclusao().add(insumo);
					break;
				}
			}
		}
		
		manterAtividadesController.setTabSelecionada(ABA_2);
		seqInsumoExclusao = null;
		codigoInsumoExclusao = null;
		
	}
	
	/**
	 * Class MaterialComparator.
	 */
	private static class UnidadeMedidaComparator implements Comparator<ScoUnidadeMedida> {
		
		@Override
		public int compare(ScoUnidadeMedida e1, ScoUnidadeMedida e2) {
			return e1.getDescricao().compareToIgnoreCase(e2.getDescricao());
		}
	}
	
	
	public BigDecimal efetuarCalculoCustoMedioMaterial(SigAtividadeInsumos insumo){
		return custosSigFacade.efetuarCalculoCustoMedioMaterial(insumo.getMaterial());
	}

	
	public FccCentroCustos getFccCentroCusto(){
		return manterAtividadesController.getFccCentroCustos();
	}
	
	public ManterAtividadesController getManterAtividadesController() {
		return manterAtividadesController;
	}

	//Getters and Setters
	public List<SigAtividadeInsumos> getListAtividadeInsumos() {
		return listAtividadeInsumos;
	}

	public void setListAtividadeInsumos(List<SigAtividadeInsumos> listAtividadeInsumos) {
		this.listAtividadeInsumos = listAtividadeInsumos;
	}

	public SigAtividadeInsumos getAtividadeInsumos() {
		return atividadeInsumos;
	}

	public void setAtividadeInsumos(SigAtividadeInsumos atividadeInsumos) {
		this.atividadeInsumos = atividadeInsumos;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isPossuiAlteracao() {
		return possuiAlteracao;
	}

	public void setPossuiAlteracao(boolean possuiAlteracao) {
		this.possuiAlteracao = possuiAlteracao;
	}

	public Long getSeqInsumoExclusao() {
		return seqInsumoExclusao;
	}

	public void setSeqInsumoExclusao(Long seqInsumoExclusao) {
		this.seqInsumoExclusao = seqInsumoExclusao;
	}

	public Integer getCodigoInsumoExclusao() {
		return codigoInsumoExclusao;
	}

	public void setCodigoInsumoExclusao(Integer codigoInsumoExclusao) {
		this.codigoInsumoExclusao = codigoInsumoExclusao;
	}

	public List<SigAtividadeInsumos> getListAtividadeInsumosExclusao() {
		return listAtividadeInsumosExclusao;
	}

	public void setListAtividadeInsumosExclusao(
			List<SigAtividadeInsumos> listAtividadeInsumosExclusao) {
		this.listAtividadeInsumosExclusao = listAtividadeInsumosExclusao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public boolean isAtividadeJaAdicionada() {
		return atividadeJaAdicionada;
	}

	public void setAtividadeJaAdicionada(boolean atividadeJaAdicionada) {
		this.atividadeJaAdicionada = atividadeJaAdicionada;
	}
	
}
