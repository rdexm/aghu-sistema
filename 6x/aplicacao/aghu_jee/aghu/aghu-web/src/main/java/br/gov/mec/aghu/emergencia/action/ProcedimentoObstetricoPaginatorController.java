package br.gov.mec.aghu.emergencia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.McoProcedimentoObstetricosVO;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ProcedimentoObstetricoPaginatorController extends ActionController
		implements ActionPaginator {

	private static final String CONSULTAR = "consultar";

	private static final String ALTERAR = "alterar";

	private static final String PESQUISAR_PROCEDIMENTO_OBSTETRICO = "pesquisarProcedimentoObstetrico";

	private static final String MANTER_PROCEDIMENTO_OBSTETRICO = "manterProcedimentoObstetrico";

	private static final String MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_OBSTETRICO = "MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_OBSTETRICO";

	private static final String MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_OBSTETRICO_SITUACAO = "MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_OBSTETRICO_SITUACAO";

	private static final String MENSAGEM_PROCEDIMENTO_OBSTETRICO_INSERIDO_SUCESSO = "MENSAGEM_PROCEDIMENTO_OBSTETRICO_INSERIDO_SUCESSO";

	private static final long serialVersionUID = 6589546988357451479L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private Short seq;

	private String descricao;

	private Integer codigoPHI;

	private DominioSituacao dominioSituacao;

	private boolean situacao;

	private boolean habilitarEdicao;

	private boolean mostrarAcoes;

	private boolean permissaoManterProcedimentoObstetrico;
	
	private boolean permissaoPesquisarProcedimentoObstetrico;

	@Inject @Paginator
	private DynamicDataModel<McoProcedimentoObstetricos> procedimentos;
	
	private McoProcedimentoObstetricos selecao;

	private Boolean mostrarPanelMsgNaoEncontradoInterno = Boolean.FALSE;

	private McoProcedimentoObstetricosVO procedimentoVO;

	private McoProcedimentoObstetricos procedimentoSelecionado;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.procedimentoVO = new McoProcedimentoObstetricosVO();
		 
		this.setPermissaoManterProcedimentoObstetrico(getPermissionService()
		  .usuarioTemPermissao(obterLoginUsuarioLogado(),
		  MANTER_PROCEDIMENTO_OBSTETRICO, ALTERAR));

		this.setPermissaoPesquisarProcedimentoObstetrico(getPermissionService()
				  .usuarioTemPermissao(obterLoginUsuarioLogado(),
				  PESQUISAR_PROCEDIMENTO_OBSTETRICO, CONSULTAR));
		
		this.mostrarAcoes = permissaoManterProcedimentoObstetrico;
	}

	public void pesquisarProcedimento() {
		this.procedimentos.reiniciarPaginator();
		this.procedimentoVO.setSituacaoChkBox(true);
	}

	public void limparPesquisaProcedimento() {
		this.seq = null;
		this.descricao = null;
		this.codigoPHI = null;
		this.dominioSituacao = null;
		this.selecao = null;
		this.procedimentos.limparPesquisa();
	}

	public void cancelarEdicaoProcedimento() {
		limparCamposCadastroProcedimento();
		pesquisarProcedimento();
		this.habilitarEdicao = Boolean.FALSE;
		this.mostrarAcoes = Boolean.TRUE;
	}

	public void ativarDesativarProcedimento(
			McoProcedimentoObstetricos procedimentoObstetrico) {
		try {

			DominioSituacao situacao = procedimentoObstetrico.getSituacao();
			procedimentoObstetrico.setSituacao(situacao
					.equals(DominioSituacao.A) ? DominioSituacao.I
					: DominioSituacao.A);

			emergenciaFacade
					.ativarDesativarProcedimentoObstetrico(procedimentoObstetrico);

			apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_OBSTETRICO_SITUACAO);
			this.selecao = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editarProcedimento(McoProcedimentoObstetricos procedimento) {
		this.procedimentoVO = new McoProcedimentoObstetricosVO();
		this.procedimentoSelecionado = new McoProcedimentoObstetricos();
		this.procedimentoSelecionado.setSeq(procedimento.getSeq());
		
		this.procedimentoVO.setSeq(procedimento.getSeq());
		this.procedimentoVO.setDescricao(procedimento.getDescricao());
		this.procedimentoVO.setCodigoPHI(procedimento.getCodigoPHI());
		boolean situacaoChkBox = procedimento.getSituacao().equals(DominioSituacao.A);
		this.procedimentoVO.setSituacaoChkBox(situacaoChkBox);

		this.habilitarEdicao = Boolean.TRUE;
		this.mostrarAcoes = Boolean.FALSE;
	}

	public void adicionarProcedimento() {
		try {
			McoProcedimentoObstetricos procedimento = new McoProcedimentoObstetricos();
			procedimento.setDescricao(procedimentoVO.getDescricao());
			procedimento.setCodigoPHI(procedimentoVO.getCodigoPHI());
			procedimento.setSituacao(procedimentoVO.isSituacaoChkBox() ? DominioSituacao.A: DominioSituacao.I);
			emergenciaFacade.cadastrarProcedimento(procedimento);
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_PROCEDIMENTO_OBSTETRICO_INSERIDO_SUCESSO);
			limparCamposCadastroProcedimento();
			pesquisarProcedimento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void limparCamposCadastroProcedimento() {
		this.procedimentoVO =  new McoProcedimentoObstetricosVO();
		this.procedimentoVO.setSituacaoChkBox(true);
		this.procedimentoSelecionado = null;
	}

	public void confirmarAlteracaoProcedimento() {

		try {
			McoProcedimentoObstetricos procedimento = emergenciaFacade
					.buscarMcoProcedimentoObstetricosPorSeq(procedimentoVO
							.getSeq());
			procedimento.setSeq(procedimentoVO.getSeq());
			procedimento.setDescricao(procedimentoVO.getDescricao());
			procedimento.setCodigoPHI(procedimentoVO.getCodigoPHI());
			procedimento
					.setSituacao(procedimentoVO.isSituacaoChkBox() ? DominioSituacao.A
							: DominioSituacao.I);
			emergenciaFacade.alterarProcedimento(procedimento);
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_OBSTETRICO);
			this.mostrarAcoes = Boolean.TRUE;
			
			limparCamposCadastroProcedimento();

			pesquisarProcedimento();
			
			this.habilitarEdicao = Boolean.FALSE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return emergenciaFacade.pesquisarMcoProcedimentoObstetricosCount(
				seq, descricao,
				codigoPHI, dominioSituacao);
	}

	@Override
	public List<McoProcedimentoObstetricos> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return emergenciaFacade.pesquisarMcoProcedimentoObstetricos(
				firstResult, maxResults, orderProperty ,asc, seq, descricao,
				codigoPHI, dominioSituacao);
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getCodigoPHI() {
		return codigoPHI;
	}

	public void setCodigoPHI(Integer codigoPHI) {
		this.codigoPHI = codigoPHI;
	}

	public DominioSituacao getDominioSituacao() {
		return dominioSituacao;
	}

	public void setDominioSituacao(DominioSituacao dominioSituacao) {
		this.dominioSituacao = dominioSituacao;
	}

	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<McoProcedimentoObstetricos> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(
			DynamicDataModel<McoProcedimentoObstetricos> procedimentos) {
		this.procedimentos = procedimentos;
	}
 
	public McoProcedimentoObstetricos getProcedimentoSelecionado() {
		return procedimentoSelecionado;
	}

	public void setProcedimentoSelecionado(
			McoProcedimentoObstetricos procedimentoSelecionado) {
		this.procedimentoSelecionado = procedimentoSelecionado;
	}

	public boolean isHabilitarEdicao() {
		return habilitarEdicao;
	}

	public void setHabilitarEdicao(boolean habilitarEdicao) {
		this.habilitarEdicao = habilitarEdicao;
	}

	public McoProcedimentoObstetricosVO getProcedimentoVO() {
		return procedimentoVO;
	}

	public void setProcedimentoVO(McoProcedimentoObstetricosVO procedimentoVO) {
		this.procedimentoVO = procedimentoVO;
	}

	public boolean isMostrarAcoes() {
		return mostrarAcoes;
	}

	public void setMostrarAcoes(boolean mostrarAcoes) {
		this.mostrarAcoes = mostrarAcoes;
	}

	public Boolean getMostrarPanelMsgNaoEncontradoInterno() {
		return mostrarPanelMsgNaoEncontradoInterno;
	}

	public void setMostrarPanelMsgNaoEncontradoInterno(
			Boolean mostrarPanelMsgNaoEncontradoInterno) {
		this.mostrarPanelMsgNaoEncontradoInterno = mostrarPanelMsgNaoEncontradoInterno;
	}

	public boolean isPermissaoManterProcedimentoObstetrico() {
		return permissaoManterProcedimentoObstetrico;
	}

	public void setPermissaoManterProcedimentoObstetrico(
			boolean permissaoManterProcedimentoObstetrico) {
		this.permissaoManterProcedimentoObstetrico = permissaoManterProcedimentoObstetrico;
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class,"aghu-casca");
	}

	public boolean isPermissaoPesquisarProcedimentoObstetrico() {
		return permissaoPesquisarProcedimentoObstetrico;
	}

	public void setPermissaoPesquisarProcedimentoObstetrico(
			boolean permissaoPesquisarProcedimentoObstetrico) {
		this.permissaoPesquisarProcedimentoObstetrico = permissaoPesquisarProcedimentoObstetrico;
	}

	public McoProcedimentoObstetricos getSelecao() {
		return selecao;
	}

	public void setSelecao(McoProcedimentoObstetricos selecao) {
		this.selecao = selecao;
	}
}
