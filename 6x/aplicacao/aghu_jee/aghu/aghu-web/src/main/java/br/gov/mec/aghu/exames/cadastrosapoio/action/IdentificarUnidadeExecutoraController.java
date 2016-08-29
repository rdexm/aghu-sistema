package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class IdentificarUnidadeExecutoraController extends ActionController {

    private static final Log LOG = LogFactory.getLog(IdentificarUnidadeExecutoraController.class);
    private static final long serialVersionUID = 5457727202226360198L;

    private static final String ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA = "elaborarSolicitacaoExameConsultaAntiga";

    @EJB
    private IExamesFacade examesFacade;

    @EJB
    private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;

    @EJB
    private IServidorLogadoFacade servidorLogadoFacade;

    @EJB
    private ICascaFacade cascaFacade;

    private AelUnidExecUsuario aelUnidExecUsuario;
    private AghUnidadesFuncionais unidadeExecutora;

    private boolean abriuTela = false;

    @PostConstruct
    protected void inicializar() {
	this.begin(conversation);
	inicio();
    }

    /**
     * Chamado no inicio da conversação
     */
    public void inicio() {

	abriuTela = false;

	try {
	    if (verificaServidorLogadoPossuiPermissaoExecutor(obterServidorLogado())) {
                this.aelUnidExecUsuario = examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
	    }
	} catch (Exception e) {
	    LOG.error("Exceção capturada: ", e);
	}
    }

    /**
     * Edita uma unidade executora para o usuário logado.
     */
    public String editarUnidadeExecutora(AghUnidadesFuncionais unidFuncional) {
	try {
	    // Submete a unidade executora para ser persistido
	    cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(aelUnidExecUsuario, unidFuncional);

	    // Apresenta as mensagens de acordo
	    apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IDENTIF_UNID_EXEC", unidFuncional.getDescricao());
	} catch (BaseException e) {
	    apresentarExcecaoNegocio(e);
	}

	return null;
    }

    /**
     * Verifica se a unidade funcional está ativa para o usuário logado.
     * 
     * @param aghUnidadesFuncionais
     * @return
     */
    public boolean isActive(AghUnidadesFuncionais aghUnidadesFuncionais) {
	try {
			aelUnidExecUsuario = examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
	} catch (Exception e) {
	    LOG.error("Exceção capturada: ", e);
	    return false;
	}
	return (aelUnidExecUsuario != null && aelUnidExecUsuario.getUnfSeq().getSeq().equals(aghUnidadesFuncionais.getSeq()));
    }

    /**
     * Persiste uma unidade executora.
     * 
     * @return
     */
    public String persistirAghUnidadesFuncionaisExecutoras() {
	return editarUnidadeExecutora(unidadeExecutora);
    }

    // Metódo para SB
    public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {

		return this.returnSGWithCount(this.aghuFacade
				.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa),
		this.obterAghUnidadesFuncionaisExecutorasCount(objPesquisa));
    }

    public Long obterAghUnidadesFuncionaisExecutorasCount(String objPesquisa) {
	return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(objPesquisa);
    }

    // Metódo para SB
    public void limparAghUnidadesFuncionaisExecutoras() {
	unidadeExecutora = null;
    }

    public AghUnidadesFuncionais getUnidadeExecutora() {
	if (!this.abriuTela) {
	    this.abriuTela = true;
	    try {
            aelUnidExecUsuario = examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
	    } catch (Exception e) {
		LOG.error("Exceção capturada: ", e);
		unidadeExecutora = null;
	    }

	    if (aelUnidExecUsuario != null && aelUnidExecUsuario.getUnfSeq() != null) {
		unidadeExecutora = aelUnidExecUsuario.getUnfSeq();
	    }
	}

	return this.unidadeExecutora;
    }

	public void setUnidadeExecutora(
			AghUnidadesFuncionais unidadeExecutora) {
	this.unidadeExecutora = unidadeExecutora;
    }

    protected boolean verificaServidorLogadoPossuiPermissaoExecutor(String usuario) {
	return cascaFacade.temPermissao(usuario, ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA, "executor");
    }

    protected IServidorLogadoFacade getServidorLogadoFacade() {
	return this.servidorLogadoFacade;
    }

    public String obterServidorLogado() {
	RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

	return servidorLogado.getUsuario();
    }

}
