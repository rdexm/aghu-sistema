package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterKitIndiceBlocoController extends ActionController {

	private static final long serialVersionUID = 3742085444090483116L;

	private static final String MANTER_KIT_ITEM_INDICE_BLOCO = "manterKitItemIndiceBloco";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private boolean ativo;

	private Integer seq;
	
	private String descricao;
	
	private DominioSituacao situacao;
	
	private List<AelKitIndiceBloco> lista;
	
	//Para Adicionar itens
	private AelKitIndiceBloco aelKitIndiceBloco;
	
	private Integer seqExcluir;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		aelKitIndiceBloco = new AelKitIndiceBloco();
	}
	
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelKitIndiceBloco(seq, descricao, situacao);
		aelKitIndiceBloco = new AelKitIndiceBloco(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		aelKitIndiceBloco = new AelKitIndiceBloco(); 
	}
	
	public void gravar() {
		try {
			
			if(aelKitIndiceBloco.getSeq() != null){
				examesPatologiaFacade.alterarAelKitIndiceBloco(aelKitIndiceBloco);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_INDICE_BLOCO_UPDATE_SUCESSO", aelKitIndiceBloco.getDescricao());

			} else {
				examesPatologiaFacade.inserirAelKitIndiceBloco(aelKitIndiceBloco);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_INDICE_BLOCO_INSERT_SUCESSO", aelKitIndiceBloco.getDescricao());
			}
			
			aelKitIndiceBloco = new AelKitIndiceBloco();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void excluir() {
		try {

			aelKitIndiceBloco = examesPatologiaFacade.obterAelKitIndiceBlocoPorChavePrimaria(seqExcluir);
			
			if(aelKitIndiceBloco == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelKitIndiceBloco(seqExcluir);
			this.apresentarMsgNegocio( Severity.INFO,"MENSAGEM_KIT_INDICE_BLOCO_DELETE_SUCESSO", aelKitIndiceBloco.getDescricao());
			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_KIT_INDICE_DELETE_ERRO",aelKitIndiceBloco.getDescricao());
				aelKitIndiceBloco = new AelKitIndiceBloco();
			}
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			aelKitIndiceBloco = new AelKitIndiceBloco();
		}		
	}
	
	public void ativarInativar(final AelKitIndiceBloco elemento) {
		try {
			elemento.setIndSituacao( (DominioSituacao.A.equals(aelKitIndiceBloco.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
				
			examesPatologiaFacade.alterarAelKitIndiceBloco(elemento);
				
			this.apresentarMsgNegocio( Severity.INFO, 
												    ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
												    	? "MENSAGEM_KIT_INDICE_BLOCO_INATIVADO_SUCESSO" 
														: "MENSAGEM_KIT_INDICE_BLOCO_ATIVADO_SUCESSO" 
													), elemento.getDescricao());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public String detalhar(){
		return MANTER_KIT_ITEM_INDICE_BLOCO;
	}
	
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AelKitIndiceBloco> getLista() {
		return lista;
	}

	public void setLista(List<AelKitIndiceBloco> lista) {
		this.lista = lista;
	}

	public Integer getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Integer seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public AelKitIndiceBloco getAelKitIndiceBloco() {
		return aelKitIndiceBloco;
	}

	public void setAelKitIndiceBloco(
			AelKitIndiceBloco aelKitIndiceBloco) {
		this.aelKitIndiceBloco = aelKitIndiceBloco;
	}
}