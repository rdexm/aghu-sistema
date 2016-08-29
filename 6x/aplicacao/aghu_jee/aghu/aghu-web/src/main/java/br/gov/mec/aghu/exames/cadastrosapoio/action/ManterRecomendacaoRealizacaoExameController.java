package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioAbrangenciaGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioResponsavelGrupoRecomendacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author lalegre
 * 
 */

public class ManterRecomendacaoRealizacaoExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -8136728331648764102L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	/**
	 * Exame Material de Análise
	 */
	private AelExamesMaterialAnalise examesMaterialAnalise;

	/**
	 * Recomendação para realização do exame
	 */
	private AelRecomendacaoExame recomendacaoExame;

	/**
	 * Lista de Recomendações
	 */
	private List<AelRecomendacaoExame> listaRecomendacoesExames;

	/**
	 * Sigla do Exame
	 */
	private String sigla;

	/**
	 * Seq material de análise
	 */
	private Integer manSeq;

	/**
	 * Seqp de AelRecomendacaoExame
	 */
	private Integer seqp;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
		if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {
			this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
			atualizarLista(sigla, manSeq);
		}

		recomendacaoExame = new AelRecomendacaoExame();
		recomendacaoExame.setAbrangencia(DominioAbrangenciaGrupoRecomendacao.I);
		recomendacaoExame.setResponsavel(DominioResponsavelGrupoRecomendacao.C);
	}

	/**
	 * Atualiza a lista de recomendações de exames
	 * 
	 * @param sigla
	 * @param manSeq
	 */
	private void atualizarLista(String sigla, Integer manSeq) {
		this.listaRecomendacoesExames = this.examesFacade.obterRecomendacoesExames(sigla, manSeq);
	}

	/**
	 * Confirma operação
	 */
	public void confirmar() {
		try {
			if (this.recomendacaoExame.getId() == null) {

				recomendacaoExame.setExamesMaterialAnalise(examesMaterialAnalise);
				cadastrosApoioExamesFacade.inserirAelRecomendacaoExame(recomendacaoExame);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_RECOMENDACAO");
			} else {
				if (verificarAlteradoOutroUsuario(recomendacaoExame)) {
					return;
				}
				cadastrosApoioExamesFacade.atualizarAelRecomendacaoExame(recomendacaoExame);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_RECOMENDACAO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		limpar();
	}

	/**
	 * Edita o registro
	 * 
	 * @param recomendacaoExame
	 */
	public void editar(AelRecomendacaoExame recomendacaoExame) {

		if (verificarAlteradoOutroUsuario(recomendacaoExame)) {
			return;
		}

		this.seqp = recomendacaoExame.getId().getSeqp();
		this.recomendacaoExame.setAbrangencia(recomendacaoExame.getAbrangencia());
		this.recomendacaoExame.setDescricao(recomendacaoExame.getDescricao());
		this.recomendacaoExame.setExamesMaterialAnalise(recomendacaoExame.getExamesMaterialAnalise());
		this.recomendacaoExame.setId(recomendacaoExame.getId());
		this.recomendacaoExame.setResponsavel(recomendacaoExame.getResponsavel());
		this.recomendacaoExame.setServidor(recomendacaoExame.getServidor());
		this.recomendacaoExame.setServidorAlterado(recomendacaoExame.getServidorAlterado());
		this.recomendacaoExame.setVersion(recomendacaoExame.getVersion());
		this.recomendacaoExame.setIndAvisaResp(recomendacaoExame.getIndAvisaResp());

	}

	public void limpar() {

		this.seqp = null;
		this.recomendacaoExame = new AelRecomendacaoExame();
		this.recomendacaoExame.setAbrangencia(DominioAbrangenciaGrupoRecomendacao.I);
		this.recomendacaoExame.setResponsavel(DominioResponsavelGrupoRecomendacao.C);
		atualizarLista(sigla, manSeq);

	}

	/**
	 * Exclui o registro
	 */
	public void excluir(AelRecomendacaoExame recomendacaoExclusao) {
		if (verificarAlteradoOutroUsuario(recomendacaoExclusao)) {
			return;
		}

        cadastrosApoioExamesFacade.removerAelRecomendacaoExame(recomendacaoExclusao);
        apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_RECOMENDACAO");
        atualizarLista(this.sigla, this.manSeq);
	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {

		this.examesMaterialAnalise = null;
		this.recomendacaoExame = null;
		this.listaRecomendacoesExames = null;
		this.sigla = null;
		this.manSeq = null;
		this.seqp = null;

		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	private boolean verificarAlteradoOutroUsuario(AelRecomendacaoExame entidade) {
		if (entidade == null || this.examesFacade.obterAelRecomendacaoExamePorID(entidade.getId()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			this.limpar();
			return true;
		}
		return false;
	}

	// GETS AND SETS

	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public AelRecomendacaoExame getRecomendacaoExame() {
		return recomendacaoExame;
	}

	public void setRecomendacaoExame(AelRecomendacaoExame recomendacaoExame) {
		this.recomendacaoExame = recomendacaoExame;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public List<AelRecomendacaoExame> getListaRecomendacoesExames() {
		return listaRecomendacoesExames;
	}

	public void setListaRecomendacoesExames(List<AelRecomendacaoExame> listaRecomendacoesExames) {
		this.listaRecomendacoesExames = listaRecomendacoesExames;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
}
