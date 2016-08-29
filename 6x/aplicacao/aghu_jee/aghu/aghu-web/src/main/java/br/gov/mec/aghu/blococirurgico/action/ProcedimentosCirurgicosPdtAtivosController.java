package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ProcedimentosCirurgicosPdtAtivosController extends ActionController implements ActionPaginator { 

	private static final long serialVersionUID = 2632400007058708506L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private RelatorioProcedimentosCirurgicosPdtAtivosController relatorioProcedimentosCirurgicosPdtAtivosController;

	@Inject @Paginator
	private DynamicDataModel<ProcedimentosCirurgicosPdtAtivosVO> dataModel;
	
	private Integer numeroCopias = 1;
	
	private AghEspecialidades especialidade;
	private MbcProcedimentoCirurgicos procedimento;
	private Boolean ativo;
	
	private List<ProcedimentosCirurgicosPdtAtivosVO>  procedimentosCirurgicosPdtAtivosVOs;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public List<ProcedimentosCirurgicosPdtAtivosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		try {
			procedimentosCirurgicosPdtAtivosVOs = blocoCirurgicoFacade
						.obterProcedimentosCirurgicosPdtAtivosListaPaginada(
							firstResult, 
							maxResult, 
							orderProperty, 
							asc,
							obterEspSeq(), 
							obterProcedimentoSeq());
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return procedimentosCirurgicosPdtAtivosVOs;
	}	

	@Override
	public Long recuperarCount() {
		Long recuperarCount = 0L;
		
		try {
			recuperarCount =  blocoCirurgicoFacade.obterProcedimentosCirurgicosPdtAtivosCount(obterEspSeq(), obterProcedimentoSeq());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return recuperarCount;
	}

	public Short obterEspSeq() {
		return getEspecialidade() != null ? getEspecialidade().getSeq() : null;
	}
	
	public Integer obterProcedimentoSeq(){
		return getProcedimento() != null ? getProcedimento().getSeq() : null;
	}
	
	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidades((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return aghuFacade.pesquisarEspecialidadesCount((String) strPesquisa);
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisarSuggestionProcedimento(final String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoFacade.listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade((String) strPesquisa, obterEspSeq()),pesquisarSuggestionProcedimentoCount(strPesquisa));

	}
	
	public Long pesquisarSuggestionProcedimentoCount(final String strPesquisa) {
		return blocoCirurgicoFacade.listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount((String) strPesquisa, obterEspSeq());
	}
	
	private void limpar(){
		relatorioProcedimentosCirurgicosPdtAtivosController.setGerouArquivo(Boolean.FALSE);
		if (dataModel != null) {
			dataModel.setPesquisaAtiva(Boolean.FALSE);
		}
		setNumeroCopias(1);
		especialidade = null;
		procedimento = null;
		setAtivo(false);
	}
	
	public void iniciar(){
		this.ativo = Boolean.TRUE;		
	}
	
	public void  pesquisar(){
		this.setAtivo(true);
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa(){
		limpar();
	}
	
	//Getters and Setters
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public MbcProcedimentoCirurgicos getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(MbcProcedimentoCirurgicos procedimento) {
		this.procedimento = procedimento;
	}

	public Integer getNumeroCopias() {
		return numeroCopias;
	}

	public void setNumeroCopias(Integer numeroCopias) {
		this.numeroCopias = numeroCopias;
	}

	public List<ProcedimentosCirurgicosPdtAtivosVO> getProcedimentosCirurgicosPdtAtivosVOs() {
		return procedimentosCirurgicosPdtAtivosVOs;
	}

	public void setProcedimentosCirurgicosPdtAtivosVOs(
			List<ProcedimentosCirurgicosPdtAtivosVO> procedimentosCirurgicosPdtAtivosVOs) {
		this.procedimentosCirurgicosPdtAtivosVOs = procedimentosCirurgicosPdtAtivosVOs;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public DynamicDataModel<ProcedimentosCirurgicosPdtAtivosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ProcedimentosCirurgicosPdtAtivosVO> dataModel) {
		this.dataModel = dataModel;
	}

}
