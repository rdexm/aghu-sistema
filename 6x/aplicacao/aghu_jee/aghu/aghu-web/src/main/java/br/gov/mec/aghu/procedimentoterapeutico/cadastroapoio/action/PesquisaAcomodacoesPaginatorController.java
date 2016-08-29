package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaAcomodacoesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 749484173380840753L;

	private static final String PAGE_CADASTRA_TIPOS_SESSAO = "cadastraAcomodacoes";
	private static final String PAGE_HISTORICO_SALA = "visualizaHistoricoSala";	

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject @Paginator
	private DynamicDataModel<MptSalas> dataModel;
	
	private String descricao;
	private AghEspecialidades especialidade;
	private MptTipoSessao tipoSessao;
	private DominioSituacao situacao;
	private MptSalas itemSelecionado;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public List<AghEspecialidades> pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(parametro),
		this.aghuFacade.pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEspCount(parametro));
	}
	
	public List<MptTipoSessao> listarTiposSessao(final String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarTiposSessao(strPesquisa),
				this.procedimentoTerapeuticoFacade.listarTiposSessaoCount(strPesquisa));
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		Short espSeq = this.especialidade != null ? this.especialidade.getSeq() : null;
		Short tpsSeq = this.tipoSessao != null ? this.tipoSessao.getSeq() : null;
		return this.procedimentoTerapeuticoFacade.listarMptSalasCount(tpsSeq, espSeq, descricao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MptSalas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short espSeq = this.especialidade != null ? this.especialidade.getSeq() : null;
		Short tpsSeq = this.tipoSessao != null ? this.tipoSessao.getSeq() : null;
		return this.procedimentoTerapeuticoFacade.listarMptSalas(tpsSeq, espSeq, descricao, firstResult, maxResult, orderProperty, asc);
	}
	
	public String obterDescricaoTruncada(String descricao) {
		if (descricao.length() > 30) {
			return StringUtils.substring(descricao, 0, 30).concat("...");
		}
		return descricao;
	}

	public void excluir() {
		try {
			if (this.itemSelecionado != null) {
				String descricao = this.itemSelecionado.getDescricao();
				this.procedimentoTerapeuticoFacade.excluirMptSala(this.itemSelecionado);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_SALA_ACOMODACOES", descricao);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparCampos() {
		this.especialidade = null;
		this.tipoSessao = null;
		this.descricao = null;
		this.situacao = null;
		this.dataModel.limparPesquisa();
	}
	
	public String historicoSala() {
		return PAGE_HISTORICO_SALA;
	}

	public String inserirEditar() {
		return PAGE_CADASTRA_TIPOS_SESSAO;
	}

	// getters & setters
	
	public DynamicDataModel<MptSalas> getDataModel() {
		return dataModel;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public void setDataModel(DynamicDataModel<MptSalas> dataModel) {
		this.dataModel = dataModel;
	}

	public MptSalas getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MptSalas itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}
}
