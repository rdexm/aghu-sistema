package br.gov.mec.aghu.internacao.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.action.PesquisaSolicitacaoInternacaoPaginatorController;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class AtenderSolicitacaoInternacaoController extends ActionController {
	
	private static final long serialVersionUID = -8018013695656331905L;
	private static final Log LOG = LogFactory.getLog(AtenderSolicitacaoInternacaoController.class);
	private static final String REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO = "internacao-pesquisaSolicitacaoInternacao";

	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private PesquisaSolicitacaoInternacaoPaginatorController pesquisaSolicitacaoInternacaoPaginatorController;
	
	private AinSolicitacoesInternacao solicitacaoInternacao;

	private Integer seqSolicitacaoInternacao;

	public void inicio() {
		this.solicitacaoInternacao = solicitacaoInternacaoFacade.obterAinSolicitacoesInternacao(seqSolicitacaoInternacao);
	}
	
	public String confirmar() {
		this.pesquisaSolicitacaoInternacaoPaginatorController.reiniciarPaginator();
		try {
			this.solicitacaoInternacaoFacade.atenderSolicitacaoInternacao(this.solicitacaoInternacao, false);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SOLICITACAO_INTERNACAO");
			return REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return null;
		}
	}
	
	public List<AinLeitos> pesquisarLeitosAtivosDesocupados(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarLeitosAtivosDesocupados((String) param);
	}

	public List<AinQuartos> pesquisarQuartoPorCodigoEDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarQuartoSolicitacaoInternacao(
				(String) param, this.solicitacaoInternacao.getPaciente()
						.getSexo());
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade
				.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergencia((String) param);
	}

	public void selecionouLeito() {
		this.solicitacaoInternacao.setQuarto(null);
		this.solicitacaoInternacao.setUnidadeFuncional(null);
	}

	public void selecionouQuarto() {
		this.solicitacaoInternacao.setLeito(null);
		this.solicitacaoInternacao.setUnidadeFuncional(null);
	}

	public void selecionouUnidadeFuncional() {
		this.solicitacaoInternacao.setLeito(null);
		this.solicitacaoInternacao.setQuarto(null);
	}
	
	public String cancelar() {
		LOG.info("Cancelado");
		return REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO;
	}

	public Integer getSeqSolicitacaoInternacao() {
		return seqSolicitacaoInternacao;
	}

	public void setSeqSolicitacaoInternacao(Integer seqSolicitacaoInternacao) {
		this.seqSolicitacaoInternacao = seqSolicitacaoInternacao;
	}

	public AinSolicitacoesInternacao getSolicitacaoInternacao() {
		return solicitacaoInternacao;
	}

	public void setSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao) {
		this.solicitacaoInternacao = solicitacaoInternacao;
	}

}
