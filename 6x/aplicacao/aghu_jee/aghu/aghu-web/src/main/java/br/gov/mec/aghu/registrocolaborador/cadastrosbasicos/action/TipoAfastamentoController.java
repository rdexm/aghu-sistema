package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSexoAfastamento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapTipoAfastamento;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class TipoAfastamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}	

	private static final long serialVersionUID = 7968392235260186294L;

	private static final String TIPO_AFASTAMENTO_PESQUISA = "pesquisarTipoAfastamento";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapTipoAfastamento tipoAfastamento;
	private boolean altera;

	public String inicio() {
	 

		
		if (tipoAfastamento != null && tipoAfastamento.getCodigo() != null) {
			try {
				tipoAfastamento = cadastrosBasicosFacade.obterTipoAfastamento(tipoAfastamento.getCodigo());
				
				if(tipoAfastamento == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
				
				altera = Boolean.TRUE;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return cancelar();
			}
			
		} else{
			tipoAfastamento = new RapTipoAfastamento();
			tipoAfastamento.setSexoAfast(DominioSexoAfastamento.A);
			tipoAfastamento.setIndExigeConsulta(DominioSimNao.S);
			tipoAfastamento.setIndHorarioInicio(DominioSimNao.N);
			tipoAfastamento.setIndHorarioFim(DominioSimNao.N);
			tipoAfastamento.setIndPerfilEpidemiologico(false);
			tipoAfastamento.setIndAbsentGeral(false);
			tipoAfastamento.setIndSituacao(DominioSituacao.A);
		}
		return null;
	
	}

	public String salvar() {
		try {
			cadastrosBasicosFacade.salvar(tipoAfastamento, altera);
			if (altera) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_TIPO_AFASTAMENTO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_TIPO_AFASTAMENTO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		limparCadastro();
		return TIPO_AFASTAMENTO_PESQUISA;
	}

	private void limparCadastro() {
		tipoAfastamento = new RapTipoAfastamento();
		altera = false;
	}

	public String cancelar() {
		limparCadastro();
		return TIPO_AFASTAMENTO_PESQUISA;
	}

	public RapTipoAfastamento getTipoAfastamento() {
		return tipoAfastamento;
	}

	public void setTipoAfastamento(RapTipoAfastamento tipoAfastamento) {
		this.tipoAfastamento = tipoAfastamento;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}
}