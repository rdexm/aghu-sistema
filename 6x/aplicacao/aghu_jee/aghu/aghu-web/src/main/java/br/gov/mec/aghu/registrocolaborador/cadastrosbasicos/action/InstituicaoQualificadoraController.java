package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class InstituicaoQualificadoraController extends ActionController {

	private static final long serialVersionUID = 3930576979787451229L;
	
	private static final String PESQUISAR_INSTITUICAO_QUALIFICADORA = "pesquisarInstituicaoQualificadora";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapInstituicaoQualificadora instituicaoQualificadora;

	private boolean altera;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if (instituicaoQualificadora != null && instituicaoQualificadora.getCodigo() != null) {
			altera = true;
			instituicaoQualificadora = cadastrosBasicosFacade.obterInstituicaoQualificadora(instituicaoQualificadora.getCodigo());
			
			if(instituicaoQualificadora == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			instituicaoQualificadora = new RapInstituicaoQualificadora();
			instituicaoQualificadora.setIndInterno(DominioSimNao.N);
		}
		
		return null;
	
	}

	public String salvar() {
		try {
			cadastrosBasicosFacade.salvar(instituicaoQualificadora, altera);
			
			if (altera) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_INSTITUICAO_QUALIFICADORA");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_INSTITUICAO_QUALIFICADORA");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return cancelar();
	}

	public String cancelar() {
		instituicaoQualificadora = null;
		altera = false;
		return PESQUISAR_INSTITUICAO_QUALIFICADORA;
	}

	public RapInstituicaoQualificadora getInstituicaoQualificadora() {
		return instituicaoQualificadora;
	}

	public void setInstituicaoQualificadora(RapInstituicaoQualificadora instituicaoQualificadora) {
		this.instituicaoQualificadora = instituicaoQualificadora;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}

}