package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterTopografiaSistemasController extends ActionController {

	private static final long serialVersionUID = 3742085444090483116L;

	private static final String MANTER_TOPOGRAFIA_APARELHOS = "manterTopografiaAparelhos";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private boolean ativo;

	private Integer seq;
	
	private String descricao;
	
	private DominioSituacao situacao;
	
	private List<AelTopografiaSistemas> lista;
	
	//Para Adicionar itens
	private AelTopografiaSistemas aelTopografiaSistemas;
	
	private Integer seqExcluir;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelTopografiaSistemas(seq, descricao, situacao);
		aelTopografiaSistemas = new AelTopografiaSistemas(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		aelTopografiaSistemas = new AelTopografiaSistemas(); 
	}
	
	public void gravar() {
		try {
			if(aelTopografiaSistemas.getSeq() != null){
				examesPatologiaFacade.alterarAelTopografiaSistemas(aelTopografiaSistemas);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_TOPOGRAFIA_SISTEMA_UPDATE_SUCESSO", aelTopografiaSistemas.getDescricao());

			} else {
				examesPatologiaFacade.inserirAelTopografiaSistemas(aelTopografiaSistemas);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_TOPOGRAFIA_SISTEMA_INSERT_SUCESSO", aelTopografiaSistemas.getDescricao());
			}
			
			aelTopografiaSistemas = new AelTopografiaSistemas();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void excluir() {
		try {

			aelTopografiaSistemas = examesPatologiaFacade.obterAelTopografiaSistemasPorChavePrimaria(seqExcluir);
			
			if(aelTopografiaSistemas == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelTopografiaSistemas(seqExcluir);
			apresentarMsgNegocio( Severity.INFO, "MENSAGEM_TOPOGRAFIA_SISTEMA_DELETE_SUCESSO", aelTopografiaSistemas.getDescricao());
			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_TOPOGRAFIA_DELETE_ERRO",aelTopografiaSistemas.getDescricao());
				aelTopografiaSistemas = new AelTopografiaSistemas();
			}
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			aelTopografiaSistemas = new AelTopografiaSistemas();
		}		
	}
	
	public void ativarInativar(final AelTopografiaSistemas aelTopografiaSistemas) {
		try {
			aelTopografiaSistemas.setIndSituacao( (DominioSituacao.A.equals(aelTopografiaSistemas.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelTopografiaSistemas(aelTopografiaSistemas);
			apresentarMsgNegocio( Severity.INFO, 
											    ( DominioSituacao.A.equals(aelTopografiaSistemas.getIndSituacao()) 
											    	? "MENSAGEM_TOPOGRAFIA_SISTEMA_INATIVADA_SUCESSO" 
													: "MENSAGEM_TOPOGRAFIA_SISTEMA_ATIVADA_SUCESSO" 
												), aelTopografiaSistemas.getDescricao());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}

	public String detalhar(){
		aelTopografiaSistemas = new AelTopografiaSistemas();
		return MANTER_TOPOGRAFIA_APARELHOS;
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

	public List<AelTopografiaSistemas> getLista() {
		return lista;
	}

	public void setLista(List<AelTopografiaSistemas> lista) {
		this.lista = lista;
	}

	public Integer getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Integer seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public AelTopografiaSistemas getAelTopografiaSistemas() {
		return aelTopografiaSistemas;
	}

	public void setAelTopografiaSistemas(
			AelTopografiaSistemas aelTopografiaSistemas) {
		this.aelTopografiaSistemas = aelTopografiaSistemas;
	}
}