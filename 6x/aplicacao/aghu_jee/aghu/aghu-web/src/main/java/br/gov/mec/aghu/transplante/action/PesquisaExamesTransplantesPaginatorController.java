package br.gov.mec.aghu.transplante.action;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.AelExamesXAelParametroCamposLaudoVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaExamesTransplantesPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -6184280293842755499L;

	@EJB
	private ITransplanteFacade transplantes;
	
	@EJB
	private IExamesFacade exames;
	
	@Inject @Paginator
	private DynamicDataModel<MtxExameUltResults> dataModel;
	
	private MtxExameUltResults selecionado;
	private MtxExameUltResults mtxExameUltResults;
	private AelExames aelExames;
	private AelExamesXAelParametroCamposLaudoVO campoLaudo;	
	private DominioSimNao ativo;

	final private static String PAGE_CADASTRA_EXAMES_TRANSPLANTES = "transplantes-cadastraExamesTransplantes";
	private static final String PAGE_PESQUISA_EXAMES_TRANSPLANTES = "transplante-pesquisaExamesTransplantes";
	
	@PostConstruct 
	public void init(){
		begin(conversation, true);
	}
	
	public void pesquisar(){
		selecionado = null;
		mtxExameUltResults = null;
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true); 
	}
	
	public String inserir(){
		return PAGE_CADASTRA_EXAMES_TRANSPLANTES;
	}
	
	public String editar(){
		return PAGE_CADASTRA_EXAMES_TRANSPLANTES;
	}
	
	public void excluir(){
		try{
			transplantes.excluirMtxExameUltResults(mtxExameUltResults);
			apresentarMsgNegocio(Severity.INFO, "MSG_EXAMES_TRANSPLANTES_REMOCAO_SUCESSO", "");
		}catch(ApplicationBusinessException e){
			apresentarMsgNegocio("MSG__EXAMES_TRANSPLANTES_ERRO");
		}
		pesquisar();
	}
	
	public String limpar(){
		selecionado = null;
		mtxExameUltResults = null;
		aelExames = null;
		campoLaudo = null;
		ativo = null;
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
		return PAGE_PESQUISA_EXAMES_TRANSPLANTES;
	}
	
	public String obterHint(String _string, Short tamanho){
		if(_string.length() > tamanho){
			return StringUtils.abbreviate(_string, tamanho); 
		}
		return _string;
	}
	
	@Override
	public Long recuperarCount() {
		return transplantes.pesquisarExamesLaudosCampoCount((aelExames != null)? aelExames.getSigla() : null, (campoLaudo != null)? campoLaudo.getSeq() : null, returnAtivo());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtxExameUltResults> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return transplantes.pesquisarExamesLaudosCampo((aelExames != null)? aelExames.getSigla() : null, (campoLaudo != null)? campoLaudo.getSeq() : null, returnAtivo(),
													   firstResult, maxResult, orderProperty, asc);
	}
	
	public List<AelExames> listarExames(final String _filtro){
		return this.returnSGWithCount(exames.obterAelExamesPorSiglaDescricao(_filtro), 
				exames.obterAelExamesPorSiglaDescricaoCount(_filtro)); 
	}
	
	public List<AelExamesXAelParametroCamposLaudoVO> listarCamposLaudo(final String nome){
		return this.returnSGWithCount(exames.obterAelCampoLaudoPorNome(nome, (aelExames != null)? aelExames.getSigla() : ""), 
									  exames.obterAelCampoLaudoPorNomeCount(nome,(aelExames != null)? aelExames.getSigla() : ""));
	}
	
	public void limparCampoLaudo(){
		campoLaudo = null;
	}
	
	private DominioSituacao returnAtivo(){
		if(ativo != null){
			return DominioSituacao.getInstance(ativo.isSim());
		}
		return null;
	}
	
	//Getters and setters
	
	public DynamicDataModel<MtxExameUltResults> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MtxExameUltResults> dataModel) {
		this.dataModel = dataModel;
	}

	public MtxExameUltResults getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MtxExameUltResults selecionado) {
		this.selecionado = selecionado;
	}

	public AelExames getAelExames() {
		return aelExames;
	}

	public void setAelExames(AelExames aelExames) {
		this.aelExames = aelExames;
	}

	public AelExamesXAelParametroCamposLaudoVO getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelExamesXAelParametroCamposLaudoVO campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public DominioSimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(DominioSimNao ativo) {
		this.ativo = ativo;
	}
	
	public MtxExameUltResults getMtxExameUltResults() {
		return mtxExameUltResults;
	}

	public void setMtxExameUltResults(MtxExameUltResults mtxExameUltResults) {
		this.mtxExameUltResults = mtxExameUltResults;
	}
}
