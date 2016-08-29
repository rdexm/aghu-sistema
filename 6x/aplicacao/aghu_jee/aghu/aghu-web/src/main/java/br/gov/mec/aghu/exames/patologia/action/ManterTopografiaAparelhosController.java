package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaAparelhosId;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterTopografiaAparelhosController extends ActionController {

	private static final long serialVersionUID = 5162801196988353655L;

	private static final String MANTER_TOPOGRAFIA_SISTEMAS = "manterTopografiaSistemas";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private Integer seqAelTopografiaSistemas;
	
	private List<AelTopografiaAparelhos> lista;
	
	//Pai
	private AelTopografiaSistemas aelTopografiaSistemas;
	
	//Filho
	private AelTopografiaAparelhos aelTopografiaAparelhos;
	
	private AelTopografiaAparelhosId id;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		aelTopografiaSistemas = this.examesPatologiaFacade.obterAelTopografiaSistemasPorChavePrimaria(seqAelTopografiaSistemas);

		if(aelTopografiaSistemas == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criaObjetoInsercao();
		pesquisar();
		
		return null;
	
	}
	
	private void criaObjetoInsercao() {
		aelTopografiaAparelhos = new AelTopografiaAparelhos();
		AelTopografiaAparelhosId id = new AelTopografiaAparelhosId();
		id.setLutSeq(aelTopografiaSistemas.getSeq());
		aelTopografiaAparelhos.setId(id);
		aelTopografiaAparelhos.setAelTopografiaSistemas(aelTopografiaSistemas);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelTopografiaAparelhos(aelTopografiaSistemas);
	}
	
	public void gravar() {
		try {
			
			if(aelTopografiaAparelhos.getId().getSeqp() != null){
				examesPatologiaFacade.alterarAelTopografiaAparelhos(aelTopografiaAparelhos);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_TOPOGRAFIA_APARELHO_UPDATE_SUCESSO", aelTopografiaAparelhos.getDescricao());

			} else {
				examesPatologiaFacade.inserirAelTopografiaAparelhos(aelTopografiaAparelhos);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_TOPOGRAFIA_APARELHO_INSERT_SUCESSO", aelTopografiaAparelhos.getDescricao());
			}
			
			criaObjetoInsercao();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void excluir() {
		try {
			aelTopografiaAparelhos = examesPatologiaFacade.obterAelTopografiaAparelhosPorChavePrimaria(id);

			if(aelTopografiaAparelhos == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelTopografiaAparelhos(id);
			
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_TOPOGRAFIA_APARELHO_DELETE_SUCESSO", aelTopografiaAparelhos.getDescricao());
			criaObjetoInsercao();
			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_TOPOGRAFIA_DELETE_ERRO",aelTopografiaAparelhos.getDescricao());
				criaObjetoInsercao();
			}
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			criaObjetoInsercao();
		}
	}
	
	public void ativarInativar(final AelTopografiaAparelhos aelTopografiaAparelhos) {
		try {
			aelTopografiaAparelhos.setIndSituacao( (DominioSituacao.A.equals(aelTopografiaAparelhos.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelTopografiaAparelhos(aelTopografiaAparelhos);
			
			apresentarMsgNegocio( Severity.INFO, 
										       ( DominioSituacao.A.equals(aelTopografiaAparelhos.getIndSituacao()) 
										    	 ? "MENSAGEM_TOPOGRAFIA_APARELHO_INATIVADA_SUCESSO" 
												 : "MENSAGEM_TOPOGRAFIA_APARELHO_ATIVADA_SUCESSO" 
										       ), aelTopografiaAparelhos.getDescricao());
			
			criaObjetoInsercao();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}

	public String voltar(){
		return MANTER_TOPOGRAFIA_SISTEMAS;
	}
	
	public Integer getSeqAelTopografiaSistemas() {
		return seqAelTopografiaSistemas;
	}

	public void setSeqAelTopografiaSistemas(Integer seqAelTopografiaSistemas) {
		this.seqAelTopografiaSistemas = seqAelTopografiaSistemas;
	}

	public AelTopografiaSistemas getAelTopografiaSistemas() {
		return aelTopografiaSistemas;
	}

	public void setAelTopografiaSistemas(
			AelTopografiaSistemas aelTopografiaSistemas) {
		this.aelTopografiaSistemas = aelTopografiaSistemas;
	}

	public AelTopografiaAparelhos getAelTopografiaAparelhos() {
		return aelTopografiaAparelhos;
	}

	public void setAelTopografiaAparelhos(AelTopografiaAparelhos aelTopografiaAparelhos) {
		this.aelTopografiaAparelhos = aelTopografiaAparelhos;
	}

	public void setLista(List<AelTopografiaAparelhos> lista) {
		this.lista = lista;
	}

	public List<AelTopografiaAparelhos> getLista() {
		return lista;
	}

	public AelTopografiaAparelhosId getId() {
		return id;
	}

	public void setId(AelTopografiaAparelhosId id) {
		this.id = id;
	}
}