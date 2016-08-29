package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class SinaisSintomasController extends ActionController {

	private static final long serialVersionUID = -9080940970337693983L;

	private static final String PAGE_SINAIS_SINTOMAS_LIST = "sinaisSintomasList";
	private static final String PAGE_CADASTRO_SINONIMOS = "cadastroSinonimoSinaisSintomas";

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	@Inject
	private SinaisSintomasPaginatorController sinaisSintomasPaginatorController;

	private EpeCaractDefinidora sinaisSintomas;
	private Integer codigo = null;
	private Boolean ativo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

		if (codigo != null && codigo > 0) {
			sinaisSintomas = prescricaoEnfermagemApoioFacade.obterSinaisSintomasPorCodigo(codigo);
			if (sinaisSintomas.getSituacao().equals(DominioSituacao.A)) {
				ativo = true;
			} else {
				ativo = false;
			}
		} else {
			sinaisSintomas = new EpeCaractDefinidora();
			codigo = null;
			ativo = true;
		}
	
	}

	public void limpar() {
		inicio();
	}

	public String gravar() {
		sinaisSintomasPaginatorController.getDataModel().reiniciarPaginator();
		try {
			boolean novo = sinaisSintomas.getCodigo() == null;
			if (ativo) {
				sinaisSintomas.setSituacao(DominioSituacao.A);
			} else {
				sinaisSintomas.setSituacao(DominioSituacao.I);
			}
			if (!novo) {
				prescricaoEnfermagemApoioFacade.verificaAlteracaoEpeCaractDefinidora(sinaisSintomas);
			}
			prescricaoEnfermagemApoioFacade.persistirSinaisSintomas(sinaisSintomas);
			if (novo) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_SINAIS_SINTOMAS", sinaisSintomas.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SINAIS_SINTOMAS", sinaisSintomas.getDescricao());
			}
			sinaisSintomas = new EpeCaractDefinidora();
			ativo = true;
			codigo = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_SINAIS_SINTOMAS_LIST;
	}
	
	public String cadastrarSinonimos(){
		return PAGE_CADASTRO_SINONIMOS;
	}

	public String cancelar() {
		codigo = null;
		ativo = true;
		return PAGE_SINAIS_SINTOMAS_LIST;
	}

	public EpeCaractDefinidora getSinaisSintomas() {
		return sinaisSintomas;
	}

	public void setSinaisSintomas(EpeCaractDefinidora sinaisSintomas) {
		this.sinaisSintomas = sinaisSintomas;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

}