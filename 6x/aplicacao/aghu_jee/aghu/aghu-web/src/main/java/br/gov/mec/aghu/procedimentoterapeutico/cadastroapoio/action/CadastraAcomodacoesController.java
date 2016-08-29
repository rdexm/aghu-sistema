package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastraAcomodacoesController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4092008608929881010L;

	private static final String PAGE_PESQUISAR_ACOMODACOES = "pesquisaAcomodacoes";
	private static final String PAGE_HISTORICO_LOCAL_ATENDIMENTO = "visualizaHistoricoAcomodacao";

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MptLocalAtendimento> dataModel;	

	private MptSalas mptSala;
	private Boolean indSituacaoSala;
	private MptLocalAtendimento mptLocalAtendimento;
	private MptLocalAtendimento mptLocalAtendimentoSelecionado;
	private Boolean indSituacaoLocalAtendimento;
	private Boolean modoEdicao = Boolean.FALSE;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
		if (this.mptSala != null) {
			this.mptSala = this.procedimentoTerapeuticoFacade.obterMptSalaPorSeq(this.mptSala.getSeq());
			this.indSituacaoSala = this.mptSala.getIndSituacao().isAtivo();
			this.carregarLocalAtendimento();
			this.pesquisar();
		}else{
			this.indSituacaoSala = Boolean.TRUE;
			this.mptSala = new MptSalas();
		}
	}

	public void carregarLocalAtendimento() {
		if(this.mptLocalAtendimento != null  && this.mptLocalAtendimento.getSeq() != null){
			this.mptLocalAtendimento = this.procedimentoTerapeuticoFacade.obterMptLocalAtendimentoPorSeq(this.mptLocalAtendimento.getSeq());
			this.indSituacaoLocalAtendimento = this.mptLocalAtendimento.getIndSituacao().isAtivo();
		}else{
			this.indSituacaoLocalAtendimento = Boolean.TRUE;
			this.mptLocalAtendimento = new MptLocalAtendimento();
			this.mptLocalAtendimento.setSala(this.mptSala);
			this.mptLocalAtendimento.setTipoLocal(DominioTipoAcomodacao.P);
		}
	}

	public void alterarLocalAtendimento(){
		this.gravarLocalAtendimento();
	}

	public void editarLocalAtendimento() {
		this.modoEdicao = Boolean.TRUE;
		this.indSituacaoLocalAtendimento = this.mptLocalAtendimento.getIndSituacao().isAtivo();
	}

	public void cancelarEdicaoLocalAtendimento() {
		this.limparLocalAtendimento();
		this.modoEdicao = Boolean.FALSE;
		this.pesquisar();
	}

	public void gravarLocalAtendimento() {
		try{
			String descricao = this.mptLocalAtendimento.getDescricao();
			this.mptLocalAtendimento.setIndSituacao(this.indSituacaoLocalAtendimento.equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);
			this.procedimentoTerapeuticoFacade.persistirMptLocalAtendimento(this.mptLocalAtendimento);
			
			if(this.modoEdicao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_LOCAL_ATENDIMENTO_ACOMODACOES", descricao);
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_LOCAL_ATENDIMENTO_ACOMODACOES", descricao);
			}
			
			limparLocalAtendimento();
			this.modoEdicao = Boolean.FALSE;
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirLocalAtendimento() {
		try {
			if (this.mptLocalAtendimento != null) {
				String descricao = this.mptLocalAtendimento.getDescricao();
				this.procedimentoTerapeuticoFacade.excluirMptLocalAtendimento(this.mptLocalAtendimento);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_LOCAL_ATENDIMENTO_ACOMODACOES", descricao);
				this.limparLocalAtendimento();
				this.pesquisar();
			}
		} catch (ApplicationBusinessException e) {
			this.limparLocalAtendimento();
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravarSala() {
		try{
			Short seqSala = (this.mptSala.getSeq() != null ? this.mptSala.getSeq() : null);
			String descricao = this.mptSala.getDescricao();
			this.mptSala.setIndSituacao(this.indSituacaoSala.equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);			
			this.procedimentoTerapeuticoFacade.persistirMptSala(this.mptSala);

			if(seqSala != null) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_SALA_ACOMODACOES", descricao);
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_SALA_ACOMODACOES", descricao);
			}

			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public String obterDominioSimNaoIndReserva(Boolean indReserva) {
		return DominioSimNao.getInstance(indReserva).getDescricao();
	}
	
	public String obterDescricaoTruncada(String descricao) {
		if (descricao.length() > 30) {
			return StringUtils.substring(descricao, 0, 30).concat("...");
		}
		return descricao;
	}
	
	public List<AghEspecialidades> pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(parametro),
		this.aghuFacade.pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEspCount(parametro));
	}
	
	public List<MptTipoSessao> listarTiposSessao(final String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarTiposSessao(strPesquisa),
				this.procedimentoTerapeuticoFacade.listarTiposSessaoCount(strPesquisa));
	}
		
	public String historicoLocalAtendimento() {
		return PAGE_HISTORICO_LOCAL_ATENDIMENTO;
	}

	public String cancelar() {
		limpar();
		this.modoEdicao = Boolean.FALSE;
		this.dataModel.limparPesquisa();
		return PAGE_PESQUISAR_ACOMODACOES;
	}
	
	private void limparSala() {
		this.mptSala = null;
		this.indSituacaoSala = null;
	}

	private void limparLocalAtendimento() {
		this.indSituacaoLocalAtendimento = Boolean.TRUE;
		this.mptLocalAtendimento = null;
		this.mptLocalAtendimento = new MptLocalAtendimento();
		this.mptLocalAtendimento.setTipoLocal(DominioTipoAcomodacao.P);
		this.mptLocalAtendimento.setSala(this.mptSala);
	}
	
	private void limpar() {
		this.limparSala();
		this.limparLocalAtendimento();
	}
	
	// getters & setters
	
	public DynamicDataModel<MptLocalAtendimento> getDataModel() {
		return dataModel;
	}	

	public MptSalas getMptSala() {
		return mptSala;
	}

	public void setMptSala(MptSalas mptSala) {
		this.mptSala = mptSala;
	}

	@Override
	public Long recuperarCount() {
		return this.procedimentoTerapeuticoFacade.listarMptLocaisAtendimentoPorSalaCount(this.mptSala.getSeq());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MptLocalAtendimento> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.procedimentoTerapeuticoFacade.listarMptLocaisAtendimentoPorSala(this.mptSala.getSeq(), firstResult, maxResult, orderProperty, asc);
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getIndSituacaoSala() {
		return indSituacaoSala;
	}

	public void setIndSituacaoSala(Boolean indSituacaoSala) {
		this.indSituacaoSala = indSituacaoSala;
	}

	public Boolean getIndSituacaoLocalAtendimento() {
		return indSituacaoLocalAtendimento;
	}

	public void setIndSituacaoLocalAtendimento(Boolean indSituacaoLocalAtendimento) {
		this.indSituacaoLocalAtendimento = indSituacaoLocalAtendimento;
	}

	public MptLocalAtendimento getMptLocalAtendimento() {
		return mptLocalAtendimento;
	}

	public void setMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) {
		this.mptLocalAtendimento = mptLocalAtendimento;
	}

	public MptLocalAtendimento getMptLocalAtendimentoSelecionado() {
		return mptLocalAtendimentoSelecionado;
	}

	public void setMptLocalAtendimentoSelecionado(
			MptLocalAtendimento mptLocalAtendimentoSelecionado) {
		this.mptLocalAtendimentoSelecionado = mptLocalAtendimentoSelecionado;
	}
}
