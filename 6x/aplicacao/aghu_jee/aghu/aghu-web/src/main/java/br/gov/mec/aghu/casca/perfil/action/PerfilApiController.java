package br.gov.mec.aghu.casca.perfil.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PerfilApiController extends ActionController {

	private static final long serialVersionUID = 5353572500931197357L;

	private static final String REDIRECIONA_PESQUISAR_PERFIL = "pesquisarPerfilApi";

	@EJB
	private ICascaFacade cascaFacade;
	
	private PerfilApi perfilApi;
	
	private Integer seqPerfil;

	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public void iniciar() {
		if (seqPerfil != null) {
			perfilApi = cascaFacade.obterPerfilApi(seqPerfil);
		} else {
			perfilApi = new PerfilApi();
			perfilApi.setSituacao(DominioSituacao.A);
		}
	}
	
	public String salvar() {
		try {
			boolean indicaEdicao = perfilApi.getId() != null;
			cascaFacade.salvarPerfilApi(perfilApi);

			if (indicaEdicao) {
				this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_EDICAO_PERFIL");			
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_CRIACAO_PERFIL");			
			}
			perfilApi = new PerfilApi();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return REDIRECIONA_PESQUISAR_PERFIL;
	}
	
	public String cancelar() {
		return REDIRECIONA_PESQUISAR_PERFIL;
	}

	public PerfilApi getPerfilApi() {
		return perfilApi;
	}

	public void setPerfilApi(PerfilApi perfilApi) {
		this.perfilApi = perfilApi;
	}

	public Integer getSeqPerfil() {
		return seqPerfil;
	}

	public void setSeqPerfil(Integer seqPerfil) {
		this.seqPerfil = seqPerfil;
	}
}