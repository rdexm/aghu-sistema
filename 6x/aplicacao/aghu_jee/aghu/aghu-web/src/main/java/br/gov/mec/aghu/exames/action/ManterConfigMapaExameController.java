package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelConfigMapaExames;
import br.gov.mec.aghu.model.AelConfigMapaExamesId;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterConfigMapaExameController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5681680006736564753L;

	private static final String MANTER_CONFIGURACAO_MAPA = "manterConfiguracaoMapa";

	@EJB
	private IExamesFacade examesFacade;

	private AelConfigMapa pai;
	
	//Para Adicionar itens
	private AelConfigMapaExames aelConfigMapaExame;

	private String sigla;
	private String exame;
	private String material;
	private DominioSituacao situacao;
	
	private VAelUnfExecutaExames unidadeExecutora;

	@Inject @Paginator
	private DynamicDataModel<AelConfigMapaExames> dataModel;
	
	private AelConfigMapaExames selecionado;
	
	private boolean iniciouTela;

	private static final Enum[] fetchArgsInnerJoin= {AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS};

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
	 

		if(iniciouTela){
			return null;
		}
		
		if(pai != null && pai.getSeq() != null){
			pai = this.examesFacade.obterAelConfigMapa(pai.getSeq(), fetchArgsInnerJoin, null);
					

			if(pai == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
		}
		
		limpar();
		
		return null;
	
	}

	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}

	public void limpar(){
		iniciouTela = true;
		unidadeExecutora = null;
		sigla = null;
		exame = null;
		material = null;
		situacao = null; 
		
		aelConfigMapaExame = new AelConfigMapaExames();
		aelConfigMapaExame.setIndSituacao(DominioSituacao.A);
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarAelConfigMapaExamesPorAelConfigMapaCount(pai, sigla, exame, material, situacao);
	}

	@Override
	public List<AelConfigMapaExames> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisarAelConfigMapaExamesPorAelConfigMapa(pai, sigla, exame, material, situacao, firstResult, maxResult, orderProperty, asc);
	}

	public List<VAelUnfExecutaExames> pesquisarAelUnfExecutaExamesPorUnfExameEMaterialAnalise(String filtro){
		return examesFacade.pesquisarPorSiglaMaterialOuExame((String) filtro);
	}
	
	public Long pesquisarPorSiglaMaterialOuExameCount(Object filtro){
		return examesFacade.pesquisarPorSiglaMaterialOuExameCount((String) filtro);
	}
	
	
	public String voltar(){
		pai  = null;
		iniciouTela = false;
		return MANTER_CONFIGURACAO_MAPA;
	}
	
	public void gravar() {
		try {
			aelConfigMapaExame.setId(new AelConfigMapaExamesId());
			aelConfigMapaExame.getId().setCgmSeq(pai.getSeq());
			aelConfigMapaExame.getId().setUfeEmaExaSigla(unidadeExecutora.getId().getSigla());
			aelConfigMapaExame.getId().setUfeEmaManSeq(unidadeExecutora.getId().getManSeq());
			aelConfigMapaExame.getId().setUfeUnfSeq(unidadeExecutora.getId().getUnfSeq());
			
			examesFacade.persistirAelConfigMapaExames(aelConfigMapaExame);
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_EXAMES_MAPA_INSERT_SUCESSO",
														unidadeExecutora.getDescricaoExame() + " - " +
														unidadeExecutora.getDescricaoMaterial(), pai.getNomeMapa() );
			
			limpar();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir() {
		try {
			
			final String descricao = selecionado.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelExames().getDescricao() + " - " +
					selecionado.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao();
						   
			examesFacade.removerAelConfigMapaExames(selecionado.getId());
			
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_EXAMES_MAPA_DELETE_SUCESSO", descricao, pai.getNomeMapa());
			limpar();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public void ativarInativar(final AelConfigMapaExames aelConfigMapaExame) {
		try {
			aelConfigMapaExame.setIndSituacao( (DominioSituacao.A.equals(aelConfigMapaExame.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			
			examesFacade.persistirAelConfigMapaExames(aelConfigMapaExame);
			
			final String descricao = aelConfigMapaExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelExames().getDescricao() + " - " +
       								 aelConfigMapaExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao();
   
			this.apresentarMsgNegocio( Severity.INFO, 
												    ( DominioSituacao.A.equals(aelConfigMapaExame.getIndSituacao()) 
												    	? "MENSAGEM_EXAMES_MAPA_ATIVADO_SUCESSO" 
														: "MENSAGEM_EXAMES_MAPA_INATIVADO_SUCESSO" 
													), descricao, pai.getNomeMapa());
			limpar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public AelConfigMapa getPai() {
		return pai;
	}

	public void setPai(AelConfigMapa pai) {
		this.pai = pai;
	}

	public AelConfigMapaExames getAelConfigMapaExame() {
		return aelConfigMapaExame;
	}

	public void setAelConfigMapaExame(AelConfigMapaExames aelConfigMapaExame) {
		this.aelConfigMapaExame = aelConfigMapaExame;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public VAelUnfExecutaExames getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(VAelUnfExecutaExames unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public DynamicDataModel<AelConfigMapaExames> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelConfigMapaExames> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

	public AelConfigMapaExames getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelConfigMapaExames selecionado) {
		this.selecionado = selecionado;
	}
}