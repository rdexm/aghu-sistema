package br.gov.mec	.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaMultirresistenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.CriteriosBacteriaAntimicrobianoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.model.MciCriterioGmrId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroCriteriosSelecaoBacteriasController extends ActionController  implements ActionPaginator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -10732480824669675L;

	private boolean edicaoBacteria = false;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	private Integer codigoBacteria;
	private String descricaoBacteria;
	private DominioSituacao situacaoBacteria;
	private BacteriaMultirresistenteVO bacteriaSelecionada;
	
	private MciAntimicrobianos mciAntimicrobiano;
	private MciCriterioGmr mciCriterioGMR;
	
	@Inject @Paginator
	private DynamicDataModel<BacteriaMultirresistenteVO> dataModel;
	private List<CriteriosBacteriaAntimicrobianoVO> listaCriterios;
	private Integer ambSeqSelecionado;
	
	private Boolean edicaoCriterio = Boolean.FALSE;
	
	private Boolean situacaoCriterio = true;
	
	private String descricaoExclusao;
	
	@Override
	public Long recuperarCount() {
		return this.controleInfeccaoFacade.listarBacteriasMultirCount(codigoBacteria == null ? null : codigoBacteria.toString(), descricaoBacteria, situacaoBacteria);
	}

	@Override
	public List<BacteriaMultirresistenteVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		
		List<BacteriaMultirresistenteVO> listaBacterias = this.controleInfeccaoFacade.listarBacteriasMultir(firstResult, maxResults, orderProperty,
				asc, codigoBacteria == null ? null : codigoBacteria.toString(), this.descricaoBacteria, this.situacaoBacteria);

		if (listaBacterias == null) {
			listaBacterias = new ArrayList<BacteriaMultirresistenteVO>();
		}
		return listaBacterias;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}

	public void editarCriterio(CriteriosBacteriaAntimicrobianoVO vo){
		mciAntimicrobiano = this.controleInfeccaoFacade.obterAntimicrobianoPorChavePrimaria(vo.getAmbSeq());
		mciCriterioGMR =  this.controleInfeccaoFacade.obterCriterioGmr(new MciCriterioGmrId(bacteriaSelecionada.getCodigoBacteria(), mciAntimicrobiano.getSeq()));
		edicaoCriterio = Boolean.TRUE;
	}
	
	public void cancelarEdicao(){
		edicaoCriterio = Boolean.FALSE;
		mciAntimicrobiano = null;
		mciCriterioGMR = new MciCriterioGmr();
	}
	
	@PostConstruct
	public void iniciar() {
		this.begin(conversation);
		edicaoBacteria = false;
		listaCriterios = null;
		mciCriterioGMR = new MciCriterioGmr();
		situacaoCriterio = false;
	
	}
	

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		bacteriaSelecionada = null;
		listaCriterios = null;
		edicaoBacteria = false;
	}

	public void limparPesquisa() {
		this.codigoBacteria = null;
		this.descricaoBacteria = null;
		this.situacaoBacteria = null;
		this.dataModel.setPesquisaAtiva(false);
		this.edicaoBacteria = false;
		this.listaCriterios = null;
		this.bacteriaSelecionada = null;
		this.situacaoCriterio = true;
		this.mciAntimicrobiano = null;
		this.edicaoCriterio = false;
	}
	
	public void gravarCriterio() {
		try {
			mciCriterioGMR.setSituacao(DominioSituacao.getInstance(situacaoCriterio));
			this.controleInfeccaoFacade.persistirCriterioGmr(mciCriterioGMR, mciAntimicrobiano.getSeq(), bacteriaSelecionada.getCodigoBacteria());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_CRITERIO", mciAntimicrobiano.getDescricao());
			mciCriterioGMR = new MciCriterioGmr();
			situacaoCriterio = true;
			listaCriterios = this.controleInfeccaoFacade.pesquisarCriterioGrmPorBmrSeq(bacteriaSelecionada.getCodigoBacteria());
			edicaoCriterio = Boolean.FALSE;
			mciAntimicrobiano = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirCriterio(){
		try {
			this.controleInfeccaoFacade.excluirCriterioGmr(ambSeqSelecionado,bacteriaSelecionada.getCodigoBacteria());
			listaCriterios = this.controleInfeccaoFacade.pesquisarCriterioGrmPorBmrSeq(bacteriaSelecionada.getCodigoBacteria());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CRITERIO");
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionaRegistro() {
		listaCriterios = this.controleInfeccaoFacade.pesquisarCriterioGrmPorBmrSeq(bacteriaSelecionada.getCodigoBacteria());
		mciCriterioGMR = new MciCriterioGmr();
		mciCriterioGMR.setSituacao(DominioSituacao.I);
		mciAntimicrobiano = null;
		situacaoCriterio = Boolean.TRUE;
		edicaoBacteria = true;
	}
	
	public List<MciAntimicrobianos> pesquisarAntiMicrobianosPorSeqDescricao(String param){
		return this.returnSGWithCount(this.controleInfeccaoFacade.pesquisarAntiMicrobianosAtivosPorSeqDescricao(param),pesquisarAntiMicrobianosPorSeqDescricaoCount(param));
	}
	
	public Long pesquisarAntiMicrobianosPorSeqDescricaoCount(String param){
		return this.controleInfeccaoFacade.pesquisarAntiMicrobianosAtivosPorSeqDescricaoCount(param);
	}
	
	public Integer getCodigoBacteria() {
		return codigoBacteria;
	}

	public void setCodigoBacteria(Integer codigoBacteria) {
		this.codigoBacteria = codigoBacteria;
	}

	public String getDescricaoBacteria() {
		return descricaoBacteria;
	}

	public void setDescricaoBacteria(String descricaoBacteria) {
		this.descricaoBacteria = descricaoBacteria;
	}

	public DominioSituacao getSituacaoBacteria() {
		return situacaoBacteria;
	}

	public void setSituacaoBacteria(DominioSituacao situacaoBacteria) {
		this.situacaoBacteria = situacaoBacteria;
	}

	public DynamicDataModel<BacteriaMultirresistenteVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<BacteriaMultirresistenteVO> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isEdicaoBacteria() {
		return edicaoBacteria;
	}

	public void setEdicaoBacteria(Boolean edicaoBacteria) {
		this.edicaoBacteria = edicaoBacteria;
	}

	public IControleInfeccaoFacade getControleInfeccaoFacade() {
		return controleInfeccaoFacade;
	}

	public void setControleInfeccaoFacade(
			IControleInfeccaoFacade controleInfeccaoFacade) {
		this.controleInfeccaoFacade = controleInfeccaoFacade;
	}

	public BacteriaMultirresistenteVO getBacteriaSelecionada() {
		return bacteriaSelecionada;
	}

	public void setBacteriaSelecionada(
			BacteriaMultirresistenteVO bacteriaSelecionada) {
		this.bacteriaSelecionada = bacteriaSelecionada;
	}

	public MciAntimicrobianos getMciAntimicrobiano() {
		return mciAntimicrobiano;
	}

	public void setMciAntimicrobiano(MciAntimicrobianos mciAntimicrobiano) {
		this.mciAntimicrobiano = mciAntimicrobiano;
	}

	public MciCriterioGmr getMciCriterioGMR() {
		return mciCriterioGMR;
	}

	public void setMciCriterioGMR(MciCriterioGmr mciCriterioGMR) {
		this.mciCriterioGMR = mciCriterioGMR;
	}

	public Boolean getEdicaoCriterio() {
		return edicaoCriterio;
	}

	public void setEdicaoCriterio(Boolean edicaoCriterio) {
		this.edicaoCriterio = edicaoCriterio;
	}

	public List<CriteriosBacteriaAntimicrobianoVO> getListaCriterios() {
		return listaCriterios;
	}

	public void setListaCriterios(
			List<CriteriosBacteriaAntimicrobianoVO> listaCriterios) {
		this.listaCriterios = listaCriterios;
	}

	public boolean isSituacaoCriterio() {
		return situacaoCriterio;
	}

	public void setSituacaoCriterio(boolean situacaoCriterio) {
		this.situacaoCriterio = situacaoCriterio;
	}

	public Integer getAmbSeqSelecionado() {
		return ambSeqSelecionado;
	}

	public void setAmbSeqSelecionado(Integer ambSeqSelecionado) {
		this.ambSeqSelecionado = ambSeqSelecionado;
	}

	public String getDescricaoExclusao() {
		return descricaoExclusao;
	}

	public void setDescricaoExclusao(String descricaoExclusao) {
		this.descricaoExclusao = descricaoExclusao;
	}
}
