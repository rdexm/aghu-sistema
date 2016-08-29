package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class ManterMaterialJustificativaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterMaterialJustificativaController.class);

	private static final long serialVersionUID = -539894874954388678L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	protected IParametroFacade parametroFacade;

	@Inject
	protected ManterMaterialController manterMaterialController;
	
	@Inject
	private SecurityController securityController;

	// #34760
	private Integer serMatriculaJusProcRel;
	private String justificativaProcRel;
	private Date dataJusProcRel;
	private Short serVinCodigoJusProcRel;
	private String senhaUser;
	private String login;
	protected Integer phiSeq;

	public enum ManterMaterialJustificativaControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_USUARIO_NAO_AUTENTICADO_JUSTIFICATIVA, MENSAGEM_USUARIO_SEM_PERMISSAO_JUSTIFICATIVA;
	}

	/**
	 * Chamado no inicio de "cada conversação"
	 */

	public String justificar() {
		// Verificar usuário e senha e permissao 'ordenadorDespesas'
		// caso não ok, mostra mensagem
		boolean usuarioAutenticado;
		try {
			usuarioAutenticado = cascaFacade.verificarSenha(login, senhaUser);
			validaUsuario(usuarioAutenticado);

			// Caso tudo ok, retorna valores para controller de materiais e
			// chama gravar materiais
			manterMaterialController.setSerMatriculaJusProcRel(serMatriculaJusProcRel);
			manterMaterialController.setJustificativaProcRel(justificativaProcRel);
			manterMaterialController.setDataJusProcRel(dataJusProcRel);
			manterMaterialController.setSerVinCodigoJusProcRel(serVinCodigoJusProcRel);
			manterMaterialController.setSenhaUser(senhaUser);
			manterMaterialController.setJustificado(true);
			manterMaterialController.setRetornouProcedimentos(true);
			serMatriculaJusProcRel = null;
			serVinCodigoJusProcRel = null;
			dataJusProcRel = null;
			justificativaProcRel = null;
			senhaUser = null;
			login = null;

			return "estoque-manterMaterialCRUD";

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e, e.getCause());
		}
		return null;
	}

	public String voltar() {
		return "prescricaomedica-procedimentosRelacionados";
	}

	/**
	 * Confirma a operacao de gravar/alterar um material
	 * 
	 * @return
	 */
	public Integer getSerMatriculaJusProcRel() {
		return serMatriculaJusProcRel;
	}

	public void setSerMatriculaJusProcRel(Integer serMatriculaJusProcRel) {
		this.serMatriculaJusProcRel = serMatriculaJusProcRel;
	}

	public String getJustificativaProcRel() {
		return justificativaProcRel;
	}

	public void setJustificativaProcRel(String justificativaProcRel) {
		this.justificativaProcRel = justificativaProcRel;
	}

	public Date getDataJusProcRel() {
		return dataJusProcRel;
	}

	public void setDataJusProcRel(Date dataJusProcRel) {
		this.dataJusProcRel = dataJusProcRel;
	}

	public Short getSerVinCodigoJusProcRel() {
		return serVinCodigoJusProcRel;
	}

	public void setSerVinCodigoJusProcRel(Short serVinCodigoJusProcRel) {
		this.serVinCodigoJusProcRel = serVinCodigoJusProcRel;
	}

	public String getSenhaUser() {
		return senhaUser;
	}

	public void setSenhaUser(String senhaUser) {
		this.senhaUser = senhaUser;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	private void validaUsuario(boolean usuarioAutenticado) throws BaseException {
		boolean usuarioPossuiPermnissao = securityController.usuarioTemPermissao("ordenadorDespesas", "gravar");
		if (usuarioAutenticado && usuarioPossuiPermnissao) {
			RapServidores servidorJustificativa = registroColaboradorFacade.obterServidorPorUsuario(login);
			serMatriculaJusProcRel = servidorJustificativa.getId().getMatricula();
			serVinCodigoJusProcRel = servidorJustificativa.getId().getVinCodigo();
			dataJusProcRel = new Date(System.currentTimeMillis());
		} else if (!usuarioAutenticado) {
			throw new BaseException(ManterMaterialJustificativaControllerExceptionCode.MENSAGEM_USUARIO_NAO_AUTENTICADO_JUSTIFICATIVA);
		} else {
			throw new BaseException(ManterMaterialJustificativaControllerExceptionCode.MENSAGEM_USUARIO_SEM_PERMISSAO_JUSTIFICATIVA);
		}

	}
}