package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class VelocidadeAdministracaoController extends ActionController  {

	private static final long serialVersionUID = -6415917368454892218L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	private AfaTipoVelocAdministracoes tipoVelocidadeAdministracao = new AfaTipoVelocAdministracoes();

	public String confirmar() {
		try {
			
			this.farmaciaFacade.persistirAfaTipoVelocAdministracoes(this.tipoVelocidadeAdministracao);
			
			if (tipoVelocidadeAdministracao.getSeq() == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TIPO_VELOCIDADE_ADMINISTRACAO", tipoVelocidadeAdministracao.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TIPO_VELOCIDADE_ADMINISTRACAO", tipoVelocidadeAdministracao.getDescricao());
			}
			
			tipoVelocidadeAdministracao = new AfaTipoVelocAdministracoes();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return "velocidadeAdministracaoList";
	}
	
	public String cancelar() {
		tipoVelocidadeAdministracao = new AfaTipoVelocAdministracoes();
		return "velocidadeAdministracaoList";
	}

	public AfaTipoVelocAdministracoes getTipoVelocidadeAdministracao() {
		return tipoVelocidadeAdministracao;
	}

	public void setTipoVelocidadeAdministracao(AfaTipoVelocAdministracoes tipoVelocidadeAdministracao) {
		this.tipoVelocidadeAdministracao = tipoVelocidadeAdministracao;
	}
}
