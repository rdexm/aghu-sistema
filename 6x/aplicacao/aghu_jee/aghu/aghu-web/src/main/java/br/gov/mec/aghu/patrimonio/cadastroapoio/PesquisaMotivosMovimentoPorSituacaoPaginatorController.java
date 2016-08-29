package br.gov.mec.aghu.patrimonio.cadastroapoio;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.PtmDescMotivoMovimentos;
import br.gov.mec.aghu.model.PtmSituacaoMotivoMovimento;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaMotivosMovimentoPorSituacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5764206753340154929L;
	
	@EJB
	private IPatrimonioFacade patrimonio;
	
	@Inject @Paginator
	private DynamicDataModel<PtmDescMotivoMovimentos> dataModel;
	
	private List<PtmSituacaoMotivoMovimento> ptmSituacaoMotivoMovimentoList;
	private PtmDescMotivoMovimentos ptmDescMotivoMovimentos;
	private PtmDescMotivoMovimentos ptmDescMotivoMovimentosSelecionado;
	private PtmDescMotivoMovimentos selecionado;
	private DominioSimNao ativo;
	private DominioSimNao justObrigatoria;
		
	final private static String PAGE_CADASTRA_DESCRICAO_MOTIVO_MOVIMENTO = "patrimonio-cadastraMotivosMovimentoPorSituacao";
	private static final String PAGE_PESQUISA_MOTIVOS_MOVIMENTO_POR_SITUACAO = "patrimonio-pesquisaMotivosMovimentoPorSituacao";

	@PostConstruct 
	public void init(){
		begin(conversation, true);
		instanciarObjs();
	}
	
	public void iniciar(){
		ptmSituacaoMotivoMovimentoList = patrimonio.obterTodasSituacoesMotivoMovimento();
		instanciarObjs();
	}
	
	public void pesquisar(){
		instanciarObjs();
		selecionado = null;
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true);  
	}
	
	public String inserir(){
		return PAGE_CADASTRA_DESCRICAO_MOTIVO_MOVIMENTO;
	}
	
	public String editar(){
		return PAGE_CADASTRA_DESCRICAO_MOTIVO_MOVIMENTO;
	}
	
	public void excluir(){
		if(ptmDescMotivoMovimentosSelecionado != null){
			try {
				patrimonio.excluirMotivoSituacaoDescricao(ptmDescMotivoMovimentosSelecionado);
				apresentarMsgNegocio(Severity.INFO, "MOTIVO_REMOVIDO_COM_SUCESSO", "");
			} catch (ApplicationBusinessException e) {
				apresentarMsgNegocio(Severity.INFO, "MOTIVO_REMOVIDO_SEM_SUCESSO", ptmDescMotivoMovimentosSelecionado.getDescricao());
			}
			
			pesquisar();
		}
	}
	
	public String limpar(){
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
		ptmDescMotivoMovimentos = new PtmDescMotivoMovimentos();
		ptmDescMotivoMovimentos.setPtmSituacaoMotivoMovimento(new PtmSituacaoMotivoMovimento());
		ativo = null;
		justObrigatoria = null;
		selecionado = null;
		return PAGE_PESQUISA_MOTIVOS_MOVIMENTO_POR_SITUACAO;
	}
	
	@Override
	public Long recuperarCount() {
		return patrimonio.pesquisarDescricoesSituacaoMovimentoCount(ptmDescMotivoMovimentos.getPtmSituacaoMotivoMovimento().getSeq(), DominioSimNao.getBooleanInstance(ativo), 
				DominioSimNao.getBooleanInstance(justObrigatoria), ptmDescMotivoMovimentos.getDescricao());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PtmDescMotivoMovimentos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return patrimonio.pesquisarDescricoesSituacaoMovimento(ptmDescMotivoMovimentos.getPtmSituacaoMotivoMovimento().getSeq(), DominioSimNao.getBooleanInstance(ativo), 
															   DominioSimNao.getBooleanInstance(justObrigatoria), ptmDescMotivoMovimentos.getDescricao(),
															   firstResult, maxResult, orderProperty, asc);
	}
	
	public String obterHint(String _string, Short tamanho){
		if(_string.length() > tamanho){
			return StringUtils.abbreviate(_string, tamanho); 
		}
		return _string;
	} 
	
	private void instanciarObjs(){
		if(ptmDescMotivoMovimentos == null){
			ptmDescMotivoMovimentos = new PtmDescMotivoMovimentos();
		}else{
			if(ptmDescMotivoMovimentos.getPtmSituacaoMotivoMovimento() == null){
				ptmDescMotivoMovimentos.setPtmSituacaoMotivoMovimento(new PtmSituacaoMotivoMovimento());
			}
		}
	}
	
	//Getters and setters
	public DynamicDataModel<PtmDescMotivoMovimentos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PtmDescMotivoMovimentos> dataModel) {
		this.dataModel = dataModel;
	}

	public List<PtmSituacaoMotivoMovimento> getPtmSituacaoMotivoMovimentoList() {
		return ptmSituacaoMotivoMovimentoList;
	}

	public void setPtmSituacaoMotivoMovimentoList(
			List<PtmSituacaoMotivoMovimento> ptmSituacaoMotivoMovimentoList) {
		this.ptmSituacaoMotivoMovimentoList = ptmSituacaoMotivoMovimentoList;
	}

	public PtmDescMotivoMovimentos getPtmDescMotivoMovimentos() {
		return ptmDescMotivoMovimentos;
	}

	public void setPtmDescMotivoMovimentos(
			PtmDescMotivoMovimentos ptmDescMotivoMovimentos) {
		this.ptmDescMotivoMovimentos = ptmDescMotivoMovimentos;
	}
	
	public DominioSimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(DominioSimNao ativo) {
		this.ativo = ativo;
	}

	public DominioSimNao getJustObrigatoria() {
		return justObrigatoria;
	}

	public void setJustObrigatoria(DominioSimNao justObrigatoria) {
		this.justObrigatoria = justObrigatoria;
	}

	public PtmDescMotivoMovimentos getPtmDescMotivoMovimentosSelecionado() {
		return ptmDescMotivoMovimentosSelecionado;
	}

	public void setPtmDescMotivoMovimentosSelecionado(
			PtmDescMotivoMovimentos ptmDescMotivoMovimentosSelecionado) {
		this.ptmDescMotivoMovimentosSelecionado = ptmDescMotivoMovimentosSelecionado;
	}

	public PtmDescMotivoMovimentos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(PtmDescMotivoMovimentos selecionado) {
		this.selecionado = selecionado;
	}
}
