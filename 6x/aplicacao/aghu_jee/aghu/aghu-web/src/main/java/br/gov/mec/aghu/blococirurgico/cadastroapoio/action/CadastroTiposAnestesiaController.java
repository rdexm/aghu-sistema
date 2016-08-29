package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTipoAnestesiaCombinada;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroTiposAnestesiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private Boolean desabilitarCampos = false;

	private MbcTipoAnestesias tipoAnestesia;
	private Boolean situacaoTipo;
	
	/*Itens*/
	private MbcTipoAnestesiaCombinada tipoAnesComb;
	private List<MbcTipoAnestesiaCombinada> anestesiasComb = new ArrayList<MbcTipoAnestesiaCombinada>();
	
	private static final String TIPOS_ANESTESIA_LIST = "pesquisaTiposAnestesia";
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 

		
		if(tipoAnestesia!=null && tipoAnestesia.getSeq()!=null){
			desabilitarCampos = true;
			situacaoTipo = tipoAnestesia.getSituacao().isAtivo();
			anestesiasComb = blocoCirurgicoCadastroApoioFacade.listarTiposAnestesiaCombinadas(tipoAnestesia);
			cancelarAnestesiaComb();
			
		}else{
			desabilitarCampos = false;
			tipoAnestesia = new MbcTipoAnestesias();
			tipoAnestesia.setIndNessAnest(true);
			tipoAnestesia.setIndCombinada(true);
			situacaoTipo = true;
			anestesiasComb=null;
			cancelarAnestesiaComb();
		}
	
	}
	

	public boolean isAtiva(MbcUnidadeNotaSala unidSala){
		return unidSala.getSituacao().isAtivo();
	}
	
	public void confirmar(){
		try {

			boolean inclusao = tipoAnestesia.getSeq()==null; 

			if(inclusao){
				tipoAnestesia.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));
			}
			tipoAnestesia.setSituacao(DominioSituacao.getInstance(this.situacaoTipo));			
			this.blocoCirurgicoCadastroApoioFacade.persistirTipoAnestesia(tipoAnestesia);

			if(inclusao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TIPO_ANESTESIA");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_TIPO_ANESTESIA");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void confirmarItem(){
		try {
			boolean inclusao = tipoAnesComb.getId()==null; 

			tipoAnesComb.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));

			tipoAnesComb.setMbcTipoAnestesiasByTanSeq(tipoAnestesia);

			this.blocoCirurgicoCadastroApoioFacade.persistirTipoAnestesiaComb(tipoAnesComb);
			anestesiasComb = blocoCirurgicoCadastroApoioFacade.listarTiposAnestesiaCombinadas(tipoAnestesia);
			cancelarAnestesiaComb();

			if(inclusao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TIPO_ANESTESIA_COMB");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_TIPO_ANESTESIA_COMB");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public boolean isTipoAnestEmEdicao(MbcTipoAnestesiaCombinada anestComb){
		return this.tipoAnesComb != null && this.tipoAnesComb.getId()!=null && 
				anestComb != null && anestComb !=null && this.tipoAnesComb.getId().equals(anestComb.getId());
	}

	public void editarTipoAnest(final MbcTipoAnestesiaCombinada anestComb){
		this.tipoAnesComb = new MbcTipoAnestesiaCombinada();
		this.tipoAnesComb.setId(anestComb.getId());
		this.tipoAnesComb.setMbcTipoAnestesiasByTanSeq(anestComb.getMbcTipoAnestesiasByTanSeq());
		this.tipoAnesComb.setMbcTipoAnestesiasByTanSeqCombina(anestComb.getMbcTipoAnestesiasByTanSeqCombina());
		this.tipoAnesComb.setRapServidores(anestComb.getRapServidores());
		this.tipoAnesComb.setSituacao(anestComb.getSituacao());
		this.tipoAnesComb.setCriadoEm(anestComb.getCriadoEm());
		this.tipoAnesComb.setVersion(anestComb.getVersion());
	}
	
	public void cancelarAnestesiaComb(){
		this.tipoAnesComb = new MbcTipoAnestesiaCombinada();
		this.tipoAnesComb.setSituacao(DominioSituacao.A);
	}
	
	public String cancelar(){
		return TIPOS_ANESTESIA_LIST;
	}

	public List<MbcTipoAnestesias> pequisarTiposAnestesiaSB(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSB(objPesquisa, false),pequisarTiposAnestesiaSBCount(objPesquisa));
	}

	public Long pequisarTiposAnestesiaSBCount(String objPesquisa){
		return blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSBCount(objPesquisa, false);
	}
	
	/*
	 * Getters and Setters abaixo...
	 */
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public Boolean getDesabilitarCampos() {
		return desabilitarCampos;
	}

	public void setDesabilitarCampos(Boolean desabilitarCampos) {
		this.desabilitarCampos = desabilitarCampos;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public MbcTipoAnestesias getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(MbcTipoAnestesias tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public Boolean getSituacaoTipo() {
		return situacaoTipo;
	}

	public void setSituacaoTipo(Boolean situacaoTipo) {
		this.situacaoTipo = situacaoTipo;
	}

	public MbcTipoAnestesiaCombinada getTipoAnesComb() {
		return tipoAnesComb;
	}

	public void setTipoAnesComb(MbcTipoAnestesiaCombinada tipoAnesComb) {
		this.tipoAnesComb = tipoAnesComb;
	}

	public List<MbcTipoAnestesiaCombinada> getAnestesiasComb() {
		return anestesiasComb;
	}

	public void setAnestesiasComb(List<MbcTipoAnestesiaCombinada> anestesiasComb) {
		this.anestesiasComb = anestesiasComb;
	}
}