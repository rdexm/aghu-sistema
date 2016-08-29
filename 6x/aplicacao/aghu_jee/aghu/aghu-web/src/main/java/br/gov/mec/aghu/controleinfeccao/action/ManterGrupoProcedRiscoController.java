package br.gov.mec.aghu.controleinfeccao.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterGrupoProcedRiscoController extends ActionController {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6349282134074081561L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	private static final String PAGINA_TIPO_GRUPO_LISTA = "grupoProcedRiscoList";

	private Boolean modoEdicao;

	private MciTipoGrupoProcedRisco entidade;

	private Boolean ativo;



	public String cancelar() {
		limparParametros();
		return PAGINA_TIPO_GRUPO_LISTA;
	}


	public Boolean getAtivo() {
		return ativo;
	}

	public MciTipoGrupoProcedRisco getEntidade() {
		return entidade;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}


	public String gravar() {
		try {

			entidade.setIndSituacao(DominioSituacao.getInstance(ativo));
			if(getModoEdicao()) {
				controleInfeccaoFacade.atualizarMciTipoGrupoProcedRisco(entidade, servidorLogadoFacade.obterServidorLogado());
				apresentarMsgNegocio(Severity.INFO, "AGPR_MENSAGEM_SUCESSO_ALTERACAO",entidade.getDescricao());
			} else {
				controleInfeccaoFacade.inserirMciTipoGrupoProcedRisco(entidade, servidorLogadoFacade.obterServidorLogado());
				apresentarMsgNegocio(Severity.INFO, "AGPR_MENSAGEM_SUCESSO_CADASTRO",entidade.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cancelar();
	}

	public void iniciar() {
	 

	 

		if(entidade == null || entidade.getSeq() == null) {
			entidade = new MciTipoGrupoProcedRisco();
			entidade.setIndSituacao(DominioSituacao.A);
			modoEdicao = false;
			ativo = true;
		} else {
			setModoEdicao(true);
			setEntidade(controleInfeccaoFacade.obterMciTipoGrupoProcedRiscoPorSeq(entidade.getSeq()));
			ativo = entidade.getIndSituacao().isAtivo();
		}
	
	}
	


	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}


	public void setEntidade(MciTipoGrupoProcedRisco entidade) {
		this.entidade = entidade;
	}


	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}


	private void limparParametros() {
		setModoEdicao(false);
		entidade = null;
		ativo = false;
	}

}
