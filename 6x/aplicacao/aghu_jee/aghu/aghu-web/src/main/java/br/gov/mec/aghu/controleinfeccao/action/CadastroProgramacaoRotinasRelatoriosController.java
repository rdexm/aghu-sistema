package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.ExportacaoDadoVO;
import br.gov.mec.aghu.controleinfeccao.vo.GrupoReportRotinaCciVO;
import br.gov.mec.aghu.controleinfeccao.vo.ParamReportGrupoVO;
import br.gov.mec.aghu.controleinfeccao.vo.ParamReportUsuarioVO;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciExportacaoDado;
import br.gov.mec.aghu.model.MciGrupoReportRotinaCci;
import br.gov.mec.aghu.model.MciParamReportGrupo;
import br.gov.mec.aghu.model.MciParamReportGrupoId;
import br.gov.mec.aghu.model.MciParamReportUsuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 */

public class CadastroProgramacaoRotinasRelatoriosController extends ActionController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8741985642144797236L;

	private static final String REDIRECIONA_LISTAR_PROGRAMACAO_ROTINAS_RELATORIOS = "pesquisaProgramacaoRotinasRelatorios";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private PesquisaProgramacaoRotinasRelatoriosController pesquisaProgramacaoRotinasRelatoriosController;
	
	private GrupoReportRotinaCciVO grupoReportRotinaCciVOSelecionado;
	
	private MciGrupoReportRotinaCci grupoReportRotinaCci;
	
	private Boolean situacao;
	
	private Boolean impressao;
	
	private DominioPeriodicidade periodicidade;
	
	private List<ParamReportGrupoVO> listaParametros;
	
	private ParamReportGrupoVO paramReportGrupoVOSelecionado;
	
	private MciParamReportGrupo mciParamReportGrupo; 
	
	private Boolean modoEdicaoParamReportGrupo;
	
	private ParamReportUsuarioVO suggestionParamReportUsuarioVO;
	private ExportacaoDadoVO suggestionExportacaoDadoVO;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		if (getGrupoReportRotinaCciVOSelecionado() == null || getGrupoReportRotinaCciVOSelecionado().getSeq() == null) {
			grupoReportRotinaCci = new MciGrupoReportRotinaCci();
			this.situacao = true;
			this.listaParametros = null;
			this.periodicidade = null;
		} else {
			grupoReportRotinaCci = controleInfeccaoFacade.obterGrupoReportRotinaCciPorSeq(getGrupoReportRotinaCciVOSelecionado().getSeq());
			this.situacao = this.grupoReportRotinaCci.getIndSituacao().isAtivo();
			if (grupoReportRotinaCci.getIndMensal() != null || grupoReportRotinaCci.getIndSemanal() != null) {
				this.periodicidade = (this.grupoReportRotinaCci.getIndMensal() ? DominioPeriodicidade.M : (this.grupoReportRotinaCci.getIndSemanal() ? DominioPeriodicidade.S : null));
			} else {
				this.periodicidade = null;
			}
			pesquisarParametros(grupoReportRotinaCci.getSeq());
			cancelarEdicao();
		}
	}
	
	public void cancelarEdicao() {
		modoEdicaoParamReportGrupo = false;
		suggestionParamReportUsuarioVO = null;
		suggestionExportacaoDadoVO = null;
		paramReportGrupoVOSelecionado = null;
		impressao = null;
		mciParamReportGrupo = new MciParamReportGrupo();
	}
	public void pesquisarParametros(Short seqGrupoReport) {
		listaParametros = controleInfeccaoFacade.pesquisarParamReportGrupoPorSeqGrupo(seqGrupoReport);
	}
	
	public String gravar() {
		try {
			this.grupoReportRotinaCci.setIndSituacao(DominioSituacao.getInstance(this.situacao));
		
			if (getPeriodicidade() == null) {
				grupoReportRotinaCci.setIndMensal(Boolean.FALSE);
				grupoReportRotinaCci.setIndSemanal(Boolean.FALSE);
			} else {
				grupoReportRotinaCci.setIndMensal(getPeriodicidade().compareTo(DominioPeriodicidade.M) == 0 ? Boolean.TRUE : Boolean.FALSE);
				grupoReportRotinaCci.setIndSemanal(getPeriodicidade().compareTo(DominioPeriodicidade.S) == 0 ? Boolean.TRUE : Boolean.FALSE);
			}
			
			controleInfeccaoFacade.gravarGrupoReportRotinaCci(grupoReportRotinaCci);
			
			//Atualiza o objeto
			grupoReportRotinaCci = controleInfeccaoFacade.obterGrupoReportRotinaCciPorSeq(grupoReportRotinaCci.getSeq());
			pesquisarParametros(grupoReportRotinaCci.getSeq());
			
			if (grupoReportRotinaCciVOSelecionado == null || grupoReportRotinaCciVOSelecionado.getSeq() == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_GRUPO_ROTINA", grupoReportRotinaCci.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_ROTINA", grupoReportRotinaCci.getDescricao());
			}
			cancelarEdicao();
		} 
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		//Fica na tela para adicionar parametros
		return null;
	}
	
	public void editar() {
		if (paramReportGrupoVOSelecionado != null && paramReportGrupoVOSelecionado.getPruSeq() != null && paramReportGrupoVOSelecionado.getGrrSeq() != null) {
			MciParamReportGrupoId id = new MciParamReportGrupoId(paramReportGrupoVOSelecionado.getPruSeq(), paramReportGrupoVOSelecionado.getGrrSeq());
			mciParamReportGrupo = controleInfeccaoFacade.obterParamReportGrupoPorId(id);
			if (paramReportGrupoVOSelecionado.getPruSeq() != null) {
				MciParamReportUsuario mciParamReportUsuario = controleInfeccaoFacade.obterParamReportUsuarioPorId(paramReportGrupoVOSelecionado.getPruSeq()); 
				mciParamReportGrupo.setMciParamReportUsuario(mciParamReportUsuario);
				suggestionParamReportUsuarioVO = new ParamReportUsuarioVO();
				suggestionParamReportUsuarioVO.setSeq(mciParamReportUsuario.getSeq());
				suggestionParamReportUsuarioVO.setNomeParamPermanente(mciParamReportUsuario.getNomeParamPermanente());
			}
			if (paramReportGrupoVOSelecionado.getEdaSeq() != null) {
				MciExportacaoDado mciExportacaoDado = controleInfeccaoFacade.obterExportacaoDadoPorId(paramReportGrupoVOSelecionado.getEdaSeq());
				mciParamReportGrupo.setMciExportacaoDado(mciExportacaoDado);
				suggestionExportacaoDadoVO = new ExportacaoDadoVO();
				suggestionExportacaoDadoVO.setSeq(mciExportacaoDado.getSeq());
				suggestionExportacaoDadoVO.setDescricao(mciExportacaoDado.getDescricao());
			}
			this.setImpressao(mciParamReportGrupo.getIndImpressao());
			modoEdicaoParamReportGrupo = true;
		}
	}
	
	public void excluir()  {
		try {
			if (paramReportGrupoVOSelecionado != null && paramReportGrupoVOSelecionado.getPruSeq() != null && paramReportGrupoVOSelecionado.getGrrSeq() != null) {
				controleInfeccaoFacade.removerMciParamReportGrupo(paramReportGrupoVOSelecionado.getPruSeq(), paramReportGrupoVOSelecionado.getGrrSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PARAMETRO_GRUPO_ROTINA",paramReportGrupoVOSelecionado.getPruSeq() + "-" + (paramReportGrupoVOSelecionado.getNomeParamPermanente() ==  null ? "" : paramReportGrupoVOSelecionado.getNomeParamPermanente()));
				pesquisarParametros(grupoReportRotinaCci.getSeq());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	public String gravarMciParamReportGrupo() {
		try {
			this.mciParamReportGrupo.setIndImpressao(this.impressao);
			if (suggestionParamReportUsuarioVO != null && suggestionParamReportUsuarioVO.getSeq() != null ) {
				MciParamReportUsuario mciParamReportUsuario = controleInfeccaoFacade.obterParamReportUsuarioPorId(suggestionParamReportUsuarioVO.getSeq());
				mciParamReportGrupo.setMciParamReportUsuario(mciParamReportUsuario);
			}
			if (suggestionExportacaoDadoVO != null && suggestionExportacaoDadoVO.getSeq() != null ) {
				MciExportacaoDado exportacaoDado = controleInfeccaoFacade.obterExportacaoDadoPorId(suggestionExportacaoDadoVO.getSeq());
				mciParamReportGrupo.setMciExportacaoDado(exportacaoDado);
			}
			mciParamReportGrupo.setMciGrupoReportRotinaCci(grupoReportRotinaCci);
			
			controleInfeccaoFacade.gravarParamReportGrupo(mciParamReportGrupo);
			if (paramReportGrupoVOSelecionado == null || paramReportGrupoVOSelecionado.getPruSeq() == null || paramReportGrupoVOSelecionado.getGrrSeq() == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_PARAMETRO_GRUPO_ROTINA", mciParamReportGrupo.getId().getPruSeq() + "-" + (mciParamReportGrupo.getMciParamReportUsuario().getNomeParamPermanente() ==  null ? "" : mciParamReportGrupo.getMciParamReportUsuario().getNomeParamPermanente()));
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_PARAMETRO_GRUPO_ROTINA", paramReportGrupoVOSelecionado.getPruSeq() + "-" + (paramReportGrupoVOSelecionado.getNomeParamPermanente() ==  null ? "" : paramReportGrupoVOSelecionado.getNomeParamPermanente()));
			}
		} 
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisarParametros(grupoReportRotinaCci.getSeq());
		cancelarEdicao();
		return null;
	}
	
	public void selecionar() {
		if (suggestionParamReportUsuarioVO != null) {
			if (suggestionParamReportUsuarioVO.getNroCopias() != null && CoreUtil.isNumeroShort(suggestionParamReportUsuarioVO.getNroCopias())) { 
				mciParamReportGrupo.setNroCopias(Short.valueOf(suggestionParamReportUsuarioVO.getNroCopias()));
			}
		}
	}

	public String cancelar() {
		grupoReportRotinaCci = new MciGrupoReportRotinaCci();
		grupoReportRotinaCciVOSelecionado = new GrupoReportRotinaCciVO();
		this.situacao = true;
		cancelarEdicao();		
		this.pesquisaProgramacaoRotinasRelatoriosController.pesquisar();
		return REDIRECIONA_LISTAR_PROGRAMACAO_ROTINAS_RELATORIOS;
	}
	
	public List<ParamReportUsuarioVO> pesquisarParamsReportUsuario(final String pesquisa) {
		return this.returnSGWithCount(controleInfeccaoFacade.pesquisarParamsReportUsuario(pesquisa, 0, 100, null, true),controleInfeccaoFacade.pesquisarParamsReportUsuarioCount(pesquisa));
	}
	public List<ExportacaoDadoVO> pesquisarExpDados(final String pesquisa) {
		return this.returnSGWithCount(controleInfeccaoFacade.pesquisarExpDados(pesquisa, 0, 100, null, true),controleInfeccaoFacade.pesquisarExpDadosCount(pesquisa));
	}
//	
	// ### GETs e SETs ###

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public GrupoReportRotinaCciVO getGrupoReportRotinaCciVOSelecionado() {
		return grupoReportRotinaCciVOSelecionado;
	}

	public void setGrupoReportRotinaCciVOSelecionado(
			GrupoReportRotinaCciVO grupoReportRotinaCciVOSelecionado) {
		this.grupoReportRotinaCciVOSelecionado = grupoReportRotinaCciVOSelecionado;
	}

	public MciGrupoReportRotinaCci getGrupoReportRotinaCci() {
		return grupoReportRotinaCci;
	}

	public void setGrupoReportRotinaCci(MciGrupoReportRotinaCci grupoReportRotinaCci) {
		this.grupoReportRotinaCci = grupoReportRotinaCci;
	}

	public DominioPeriodicidade getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(DominioPeriodicidade periodicidade) {
		this.periodicidade = periodicidade;
	}

	public List<ParamReportGrupoVO> getListaParametros() {
		return listaParametros;
	}

	public void setListaParametros(List<ParamReportGrupoVO> listaParametros) {
		this.listaParametros = listaParametros;
	}

	public PesquisaProgramacaoRotinasRelatoriosController getPesquisaProgramacaoRotinasRelatoriosController() {
		return pesquisaProgramacaoRotinasRelatoriosController;
	}

	public void setPesquisaProgramacaoRotinasRelatoriosController(
			PesquisaProgramacaoRotinasRelatoriosController pesquisaProgramacaoRotinasRelatoriosController) {
		this.pesquisaProgramacaoRotinasRelatoriosController = pesquisaProgramacaoRotinasRelatoriosController;
	}

	public Boolean getImpressao() {
		return impressao;
	}

	public void setImpressao(Boolean impressao) {
		this.impressao = impressao;
	}

	public ParamReportGrupoVO getParamReportGrupoVOSelecionado() {
		return paramReportGrupoVOSelecionado;
	}

	public void setParamReportGrupoVOSelecionado(
			ParamReportGrupoVO paramReportGrupoVOSelecionado) {
		this.paramReportGrupoVOSelecionado = paramReportGrupoVOSelecionado;
	}

	public MciParamReportGrupo getMciParamReportGrupo() {
		return mciParamReportGrupo;
	}

	public void setMciParamReportGrupo(MciParamReportGrupo mciParamReportGrupo) {
		this.mciParamReportGrupo = mciParamReportGrupo;
	}

	public Boolean getModoEdicaoParamReportGrupo() {
		return modoEdicaoParamReportGrupo;
	}

	public void setModoEdicaoParamReportGrupo(Boolean modoEdicaoParamReportGrupo) {
		this.modoEdicaoParamReportGrupo = modoEdicaoParamReportGrupo;
	}

	public IControleInfeccaoFacade getControleInfeccaoFacade() {
		return controleInfeccaoFacade;
	}

	public void setControleInfeccaoFacade(
			IControleInfeccaoFacade controleInfeccaoFacade) {
		this.controleInfeccaoFacade = controleInfeccaoFacade;
	}

	public ParamReportUsuarioVO getSuggestionParamReportUsuarioVO() {
		return suggestionParamReportUsuarioVO;
	}

	public void setSuggestionParamReportUsuarioVO(
			ParamReportUsuarioVO suggestionParamReportUsuarioVO) {
		this.suggestionParamReportUsuarioVO = suggestionParamReportUsuarioVO;
	}

	public ExportacaoDadoVO getSuggestionExportacaoDadoVO() {
		return suggestionExportacaoDadoVO;
	}

	public void setSuggestionExportacaoDadoVO(
			ExportacaoDadoVO suggestionExportacaoDadoVO) {
		this.suggestionExportacaoDadoVO = suggestionExportacaoDadoVO;
	}
}
