package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PlanosPosAltaController extends ActionController {

	private static final long serialVersionUID = 7585567508515988554L;

	private static final String PAGE_PESQUISA_PLANOS_POS_ALTA = "planosPosAltaList";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private MpmPlanoPosAlta plano = new MpmPlanoPosAlta();

	private DominioSituacao indSituacao;

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public String confirmar() {
		try {
			if (plano.getDescricao() != null) {
				plano.setDescricao(plano.getDescricao().trim());

				if (StringUtils.isBlank(plano.getDescricao())) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_DESCRICAO_BLANK");
					return "";
				}

			}
			boolean create = this.plano.getSeq() == null;
			try {
				this.plano.setSerMatricula(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			} catch (ApplicationBusinessException e) {
				this.plano.setSerMatricula(null);
			}

			this.cadastrosBasicosPrescricaoMedicaFacade.persistPlanoPosAlta(this.plano);

			if (create) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PLANO", this.plano.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_PLANO", this.plano.getDescricao());
			}

			this.plano = new MpmPlanoPosAlta();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_PESQUISA_PLANOS_POS_ALTA;
	}  
	
	public String cancelar() {
		this.plano = new MpmPlanoPosAlta();
		return PAGE_PESQUISA_PLANOS_POS_ALTA;
	}

	public MpmPlanoPosAlta getPlano() {
		return plano;
	}

	public void setPlano(MpmPlanoPosAlta plano) {
		this.plano = plano;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	} 
}