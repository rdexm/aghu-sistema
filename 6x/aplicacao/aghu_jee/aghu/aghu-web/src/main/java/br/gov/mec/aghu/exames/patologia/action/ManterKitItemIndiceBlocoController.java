package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBlocoId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterKitItemIndiceBlocoController extends ActionController {


	private static final long serialVersionUID = 5162801196988353655L;

	private static final String MANTER_KIT_INDICE_BLOCO = "manterKitIndiceBloco";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Integer seqAelKitIndiceBloco;
	
	private List<AelKitItemIndiceBloco> lista;
	
	//Pai
	private AelKitIndiceBloco aelKitIndiceBloco;
	
	//Filho
	private AelKitItemIndiceBloco aelKitItemIndiceBloco;
	
	private AelKitItemIndiceBlocoId id;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		aelKitIndiceBloco = this.examesPatologiaFacade.obterAelKitIndiceBlocoPorChavePrimaria(seqAelKitIndiceBloco);

		if(aelKitIndiceBloco == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelKitItemIndiceBloco = new AelKitItemIndiceBloco();
		AelKitItemIndiceBlocoId id = new AelKitItemIndiceBlocoId();
		id.setLo9Seq(aelKitIndiceBloco.getSeq());
		aelKitItemIndiceBloco.setId(id);
		aelKitItemIndiceBloco.setAelKitIndiceBloco(aelKitIndiceBloco);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelKitItemIndiceBloco(aelKitIndiceBloco);
	}
	
	public void gravar() {
		try {
			
			if(aelKitItemIndiceBloco.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelKitItemIndiceBloco(aelKitItemIndiceBloco);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_INDICE_ITEM_BLOCO_UPDATE_SUCESSO", aelKitItemIndiceBloco.getDescricao());

			} else {
				examesPatologiaFacade.inserirAelKitItemIndiceBloco(aelKitItemIndiceBloco);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_INDICE_ITEM_BLOCO_INSERT_SUCESSO", aelKitItemIndiceBloco.getDescricao());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void excluir() {
		try {
			aelKitItemIndiceBloco = examesPatologiaFacade.obterAelKitItemIndiceBlocoPorChavePrimaria(id);
			
			if(aelKitItemIndiceBloco == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelKitItemIndiceBloco(id);
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_INDICE_ITEM_BLOCO_DELETE_SUCESSO", aelKitItemIndiceBloco.getDescricao());
			criaObjetoInsercao();
			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_KIT_INDICE_DELETE_ERRO",aelKitItemIndiceBloco.getDescricao());
				criaObjetoInsercao();
			}
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			criaObjetoInsercao();
		}
	}
	
	public void ativarInativar(final Short seqp) {
		
		try {
			
			if(aelKitIndiceBloco.getSeq() != null && seqp != null){
				aelKitItemIndiceBloco = examesPatologiaFacade.obterAelKitItemIndiceBlocoPorChavePrimaria(new AelKitItemIndiceBlocoId(aelKitIndiceBloco.getSeq(), seqp));
				aelKitItemIndiceBloco.setIndSituacao( (DominioSituacao.A.equals(aelKitItemIndiceBloco.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
				
				examesPatologiaFacade.alterarAelKitItemIndiceBloco(aelKitItemIndiceBloco);
				apresentarMsgNegocio( Severity.INFO, 
											       ( DominioSituacao.A.equals(aelKitItemIndiceBloco.getIndSituacao()) 
											    	 ? "MENSAGEM_KIT_INDICE_ITEM_BLOCO_INATIVADO_SUCESSO" 
													 : "MENSAGEM_KIT_INDICE_ITEM_BLOCO_ATIVADO_SUCESSO" 
											       ), aelKitItemIndiceBloco.getDescricao());
				
				criaObjetoInsercao();
			}
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}

	public String voltar() {
		return MANTER_KIT_INDICE_BLOCO;
	}

	public Integer getSeqAelKitIndiceBloco() {
		return seqAelKitIndiceBloco;
	}

	public void setSeqAelKitIndiceBloco(Integer seqAelKitIndiceBloco) {
		this.seqAelKitIndiceBloco = seqAelKitIndiceBloco;
	}

	public AelKitIndiceBloco getAelKitIndiceBloco() {
		return aelKitIndiceBloco;
	}

	public void setAelKitIndiceBloco(AelKitIndiceBloco aelKitIndiceBloco) {
		this.aelKitIndiceBloco = aelKitIndiceBloco;
	}

	public AelKitItemIndiceBloco getAelKitItemIndiceBloco() {
		return aelKitItemIndiceBloco;
	}

	public void setAelKitItemIndiceBloco(
			AelKitItemIndiceBloco aelKitItemIndiceBloco) {
		this.aelKitItemIndiceBloco = aelKitItemIndiceBloco;
	}

	public void setLista(List<AelKitItemIndiceBloco> lista) {
		this.lista = lista;
	}

	public List<AelKitItemIndiceBloco> getLista() {
		return lista;
	}

	public AelKitItemIndiceBlocoId getId() {
		return id;
	}

	public void setId(AelKitItemIndiceBlocoId id) {
		this.id = id;
	}
}