package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipOrigemDocGEDs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterOrigemDocGEDsController extends ActionController {

	private static final long serialVersionUID = -5737269927004725375L;

	private static final String ORIGEM_DOC_GE_DS_LIST = "origemDocGEDsList";

	@EJB
	private IProntuarioOnlineFacade polFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	private Integer seq;
	private Boolean edicao;
	private AipOrigemDocGEDs origemDocGEDs;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		// Verifica se o identificador foi informado
		if (seq == null) {
			// Cria o novo elemento
			AipOrigemDocGEDs origem = new AipOrigemDocGEDs();
			origem.setIndSituacao(DominioSituacao.A);
			origem.setOrigem(DominioOrigemDocsDigitalizados.values()[0]);
			setOrigemDocGEDs(origem);
			this.setEdicao(false);
			
		} else {
			// Busca o elemento para editar
			origemDocGEDs = polFacade.obterAipOrigemDocGEDs(getSeq());
			
			if(origemDocGEDs == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();	
			}
			
			this.setEdicao(true);
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			// Persiste o objeto
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			this.polFacade.persistirAipOrigemDocGEDs(origemDocGEDs, servidorLogado);
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		seq = null;
		origemDocGEDs = null;
		return ORIGEM_DOC_GE_DS_LIST;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public AipOrigemDocGEDs getOrigemDocGEDs() {
		return origemDocGEDs;
	}

	public void setOrigemDocGEDs(AipOrigemDocGEDs origemDocGEDs) {
		this.origemDocGEDs = origemDocGEDs;
	}

}
