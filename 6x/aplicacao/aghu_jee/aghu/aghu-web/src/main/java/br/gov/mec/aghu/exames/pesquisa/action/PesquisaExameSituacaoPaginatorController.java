package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExameSituacaoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.model.VAelExamesSolicitacaoPacAtd;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisaExameSituacaoPaginatorController extends ActionController implements ActionPaginator{


	private static final long serialVersionUID = -6504633042794201820L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController; 

	@Inject
	private PesquisaExameSituacaoController pesquisaExameSituacaoController;  //Relatório
	
	// campos filtro
	private Date dataReferenciaIni;
	private Date dataReferenciaFim;
	private Date dataProgramada;
	private DominioOrigemAtendimento origemAtendimento;
	private DominioConvenioExameSituacao tipoConvenio;
	private DominoOrigemMapaAmostraItemExame origemMapaTrabalho;
	private AelSitItemSolicitacoes situacao;
	private VAelExamesSolicitacao nomeExame;
	private Boolean exibirBotaoImprime = false;

	@Inject @Paginator
	private DynamicDataModel<PesquisaExameSituacaoVO> dataModel;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Metodo que realiza a acao de pesquisar.
	 */
	public void pesquisar() {
		try {
			this.pesquisaExamesFacade.validarFiltroPesquisaExameSolicitacaoPacAtend(this.dataReferenciaIni, 
					this.dataReferenciaFim, this.dataProgramada, this.situacao, this.nomeExame);
			
			dataModel.reiniciarPaginator();
			this.exibirBotaoImprime = true;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
	}
	
	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de recipiente de coleta.
	 */
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		unidadeExecutoraController.setUnidadeExecutora(null);
		this.dataReferenciaIni = null;
		this.dataReferenciaFim = null;
		this.dataProgramada = null;
		this.origemAtendimento = null;
		this.tipoConvenio = null;
		this.origemMapaTrabalho = null;
		this.situacao = null;
		this.nomeExame = null;
		this.exibirBotaoImprime = false;
	}
	
	@Override
	public List<PesquisaExameSituacaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<PesquisaExameSituacaoVO> listaExames = null;
		try {
			
			if(this.dataReferenciaIni != null){
				this.dataReferenciaIni.setSeconds(00);
			}
			if(this.dataReferenciaFim != null){
				this.dataReferenciaFim.setSeconds(59);
			}
			
			if(orderProperty == null){
				orderProperty = VAelExamesSolicitacaoPacAtd.Fields.DTHR_EVENTO.toString();
				asc = true;
			}
			
			listaExames = this.pesquisaExamesFacade.pesquisaExameSolicitacaoPacAtend(this.unidadeExecutoraController.getUnidadeExecutora(), 
					this.dataReferenciaIni, this.dataReferenciaFim, this.dataProgramada, this.tipoConvenio, this.situacao, this.nomeExame, origemAtendimento, origemMapaTrabalho, 
					firstResult, maxResult, orderProperty, asc);
			
			if(listaExames!=null && listaExames.size()>0){
				setDadosRelatorio();
			}else{
				this.exibirBotaoImprime = false;
			}
			
			
			return listaExames;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<PesquisaExameSituacaoVO>();
		}
	}
	
	
	@Override
	public Long recuperarCount() {
		if(this.dataReferenciaIni != null){
			this.dataReferenciaIni.setSeconds(00);
		}
		if(this.dataReferenciaFim != null){
			this.dataReferenciaFim.setSeconds(59);
		}
		return pesquisaExamesFacade.countExameSolicitacaoPacAtend(this.unidadeExecutoraController.getUnidadeExecutora(), this.dataReferenciaIni, 
				this.dataReferenciaFim, this.dataProgramada, this.tipoConvenio, this.situacao, this.nomeExame, origemAtendimento, origemMapaTrabalho);
	}

	
	
	public List<VAelExamesSolicitacao> sbObterExames(String objPesquisa) {
		
		try {
			return pesquisaExamesFacade.pesquisaExameSolicitacao((String) objPesquisa, this.unidadeExecutoraController.getUnidadeExecutora());
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<VAelExamesSolicitacao>();
		}
	}
	
	
	public List<AacConsultas> obtemListSituacaoExames() {
		return examesFacade.listarSituacaoExames();
	}
	
	public void setDadosRelatorio(){
		if(nomeExame!=null)	{
			pesquisaExameSituacaoController.setExame(nomeExame.getDescricaoExame());
		}else{
			pesquisaExameSituacaoController.setExame(null);
		}
		
		pesquisaExameSituacaoController.setDataReferenciaIni(dataReferenciaIni);
		pesquisaExameSituacaoController.setDataReferenciaFim(dataReferenciaFim);
		pesquisaExameSituacaoController.setDataProgramada(dataProgramada);
		pesquisaExameSituacaoController.setSituacao(situacao);
		pesquisaExameSituacaoController.setTipoConvenio(tipoConvenio);
		pesquisaExameSituacaoController.setNomeExame(nomeExame);
		pesquisaExameSituacaoController.setOrigemAtendimento(origemAtendimento);
		pesquisaExameSituacaoController.setOrigemMapaTrabalho(origemMapaTrabalho);
	}
	

	public Date getDataReferenciaIni() {
		return dataReferenciaIni;
	}

	public void setDataReferenciaIni(Date dataReferenciaIni) {
		this.dataReferenciaIni = dataReferenciaIni;
	}

	public Date getDataReferenciaFim() {
		return dataReferenciaFim;
	}

	public void setDataReferenciaFim(Date dataReferenciaFim) {
		this.dataReferenciaFim = dataReferenciaFim;
	}

	public Date getDataProgramada() {
		return dataProgramada;
	}

	public void setDataProgramada(Date dataProgramada) {
		this.dataProgramada = dataProgramada;
	}

	public DominioOrigemAtendimento getOrigemAtendimento() {
		return origemAtendimento;
	}

	public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}

	public DominioConvenioExameSituacao getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(DominioConvenioExameSituacao tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public DominoOrigemMapaAmostraItemExame getOrigemMapaTrabalho() {
		return origemMapaTrabalho;
	}

	public void setOrigemMapaTrabalho(
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho) {
		this.origemMapaTrabalho = origemMapaTrabalho;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public VAelExamesSolicitacao getNomeExame() {
		return nomeExame;
	}

	public void setNomeExame(VAelExamesSolicitacao nomeExame) {
		this.nomeExame = nomeExame;
	}

	public Boolean getExibirBotaoImprime() {
		return exibirBotaoImprime;
	}

	
		 


	public DynamicDataModel<PesquisaExameSituacaoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisaExameSituacaoVO> dataModel) {
	 this.dataModel = dataModel;
	}
}
