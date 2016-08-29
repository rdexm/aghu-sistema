package br.gov.mec.aghu.exames.agendamento.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManutencaoTipoMarcacaoExameController extends ActionController {

	private static final long serialVersionUID = -159476204593452669L;

	private static final String REDIRECT_PESQUISA_MANUTENCAO_TIPO_MARCACAO_EXAME = "manterTipoMarcacaoExamePesquisa";

	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;

	private Short seq;
	private AelTipoMarcacaoExame tipoMarcacaoExame;

	public String iniciar() {
	 

		if (seq != null) {
			tipoMarcacaoExame = agendamentoExamesFacade
					.obterTipoMarcacaoExamePorSeq(seq);

			if (tipoMarcacaoExame == null) {
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}

		} else {
			tipoMarcacaoExame = new AelTipoMarcacaoExame();
		}

		return null;
	
	}

	public String gravar() {
		try {
			agendamentoExamesFacade.persistirTipoMarcacaoExame(tipoMarcacaoExame);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SALVAR_TIPO_MARCACAO_EXAME");
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		seq = null;
		tipoMarcacaoExame = null;
		return REDIRECT_PESQUISA_MANUTENCAO_TIPO_MARCACAO_EXAME;
	}

	public AelTipoMarcacaoExame getTipoMarcacaoExame() {
		return tipoMarcacaoExame;
	}

	public void setTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame) {
		this.tipoMarcacaoExame = tipoMarcacaoExame;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

}
