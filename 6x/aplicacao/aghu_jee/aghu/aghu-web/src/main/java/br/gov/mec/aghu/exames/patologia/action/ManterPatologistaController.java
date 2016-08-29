package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class ManterPatologistaController extends ActionController {

	private static final long serialVersionUID = 3124300537037208760L;

	private static final String MANTER_PATOLOGISTA_LIST = "manterPatologistaList";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	private AelPatologista patologista;
	
	private RapServidores servidor;
	
	private boolean iniciouTela;
	
	public enum ManterPatologistaPaginatorControllerExceptionCode implements BusinessExceptionCode {
		ERRO_GENERICO_PATOLOGISTA;
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(iniciouTela){
			return null;
		}
		
		iniciouTela = true; 
		
		if(patologista != null && patologista.getSeq() != null) {
			this.patologista = this.examesPatologiaFacade.obterPatologista(patologista.getSeq());

			if(patologista == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
						
		} else {
			this.patologista = new AelPatologista();
			this.patologista.setFuncao(DominioFuncaoPatologista.R);			
			this.patologista.setPermiteLibLaudo(true);
			this.patologista.setSituacao(DominioSituacao.A);			
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {

			/**
			 * Será colocado valor default 'true' aqui pelo fato de o campo ser not null. O acesso a liberação de laudos
			 * deve ser controlada pelas permissões de perfis.
			 */
			patologista.setPermiteLibLaudo(true);
			patologista.setServidor(servidor);
			patologista.setNome(servidor.getPessoaFisica().getNome());
			patologista.setNomeParaLaudo(servidor.getPessoaFisica().getNome());
			
			boolean inclusao = patologista.getSeq() == null;
			this.examesPatologiaFacade.persistirPatologista(patologista);
			
			if(inclusao) {
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_PATOLOGISTA_INCLUSAO_SUCESSO");
			} else {
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_PATOLOGISTA_EDICAO_SUCESSO");				
			}

			return cancelar();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public String cancelar() {
		iniciouTela = false;
		patologista = null;
		servidor = null;
		return MANTER_PATOLOGISTA_LIST;
	}

	public List<RapServidores> buscarServidor(String servidor) {
		return this.registroColaboradorFacade.pesquisarServidoresPorCodigoOuDescricao(servidor);
	}

	public AelPatologista getPatologista() {
		return patologista;
	}

	public void setPatologista(AelPatologista patologista) {
		this.patologista = patologista;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}
	
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
}