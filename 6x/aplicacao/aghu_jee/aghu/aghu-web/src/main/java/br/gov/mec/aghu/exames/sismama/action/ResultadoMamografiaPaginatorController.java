package br.gov.mec.aghu.exames.sismama.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class ResultadoMamografiaPaginatorController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 5128182244602157321L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	List<AelItemSolicitacaoExames> listaItensSolicitacoesExames = new ArrayList<AelItemSolicitacaoExames>();

	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AghUnidadesFuncionais unidadeExecutora;

	private Integer solicitacao;
	private String prontuario;
	private String nomePaciente;
	private Integer soeSeq;
	private Short seqp;
	private Boolean ativo;

	private static final String PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD = "exames-resultadoMamografiaCRUD";

	public void iniciar() {
	 

		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			this.usuarioUnidadeExecutora = null;
		}

		// Resgata a unidade executora associada ao usuario
		if (this.usuarioUnidadeExecutora != null) {
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		setAtivo(false);
	
	}

	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparPesquisa() {

		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuario
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		setSolicitacao(null);
		setProntuario(null);
		setNomePaciente(null);
		setListaItensSolicitacoesExames(null);
		setAtivo(false);
	}

	public String pesquisar() {
		AelSolicitacaoExames soe = examesFacade.obterAelSolicitacaoExamePorChavePrimaria(this.solicitacao);

		setAtivo(true);

		if (soe != null) {

			setNomePaciente(examesFacade.buscarLaudoNomePaciente(soe));
			setProntuario(examesFacade.buscarLaudoProntuarioPaciente(soe));

			try {
				listaItensSolicitacoesExames = examesLaudosFacade.pesquisarItemSolicitacaoExames(getSolicitacao(), getUnidadeExecutora().getSeq());

				if (!listaItensSolicitacoesExames.isEmpty() && listaItensSolicitacoesExames.size() == 1) {
					return editar(listaItensSolicitacoesExames.get(0));
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

		}

		return null;
	}

	public String editar(AelItemSolicitacaoExames ise) {
		setSoeSeq(ise.getId().getSoeSeq());
		setSeqp(ise.getId().getSeqp());
		return PAGE_EXAMES_RESULTADO_MAMOGRAFIA_CRUD;
	}

	public String obterRespostaMamo(AelItemSolicitacaoExames ise) {
		return examesLaudosFacade.obterRespostaMamo(ise);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String codigo) {
		return examesFacade.pesquisarUnidadeFuncionalPorSeqDescricao((String) codigo);
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public List<AelItemSolicitacaoExames> getListaItensSolicitacoesExames() {
		return listaItensSolicitacoesExames;
	}

	public void setListaItensSolicitacoesExames(List<AelItemSolicitacaoExames> listaItensSolicitacoesExames) {
		this.listaItensSolicitacoesExames = listaItensSolicitacoesExames;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

}