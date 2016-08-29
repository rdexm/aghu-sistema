package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterKitMaterialController extends ActionController {

	private static final long serialVersionUID = 6274619599402686574L;

	private static final String MANTER_KIT_ITEM_MATERIAL = "manterKitItemMaterial";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private boolean ativo;

	private Integer seq;
	
	private String descricao;
	
	private DominioSituacao situacao;
	
	private List<AelKitMatPatologia> lista;
	
	private AelKitMatPatologia materialSelecionado;
	
	//Para Adicionar itens
	private AelKitMatPatologia aelKitMatPatologia;
	
	private Integer seqExcluir;
	
	public ManterKitMaterialController() {
		aelKitMatPatologia = new AelKitMatPatologia();
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelKitMatPatologia(seq, descricao, situacao);
		aelKitMatPatologia = new AelKitMatPatologia(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		aelKitMatPatologia = new AelKitMatPatologia(); 
	}
	
	public void gravar() {
		try {
			
			if(aelKitMatPatologia.getSeq() != null){
				examesPatologiaFacade.alterarAelKitMatPatologia(aelKitMatPatologia);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_MATERIAL_UPDATE_SUCESSO", aelKitMatPatologia.getDescricao());
				
			} else {
				examesPatologiaFacade.inserirAelKitMatPatologia(aelKitMatPatologia);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_KIT_MATERIAL_INSERT_SUCESSO", aelKitMatPatologia.getDescricao());
			}
			
			aelKitMatPatologia = new AelKitMatPatologia();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void excluir() {
		try {
			examesPatologiaFacade.excluirAelKitMatPatologia(seqExcluir);
			this.apresentarMsgNegocio( Severity.INFO,"MENSAGEM_KIT_MATERIAL_DELETE_SUCESSO", aelKitMatPatologia.getDescricao());
			pesquisar();
			
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_KIT_MATERIAL_DELETE_ERRO",aelKitMatPatologia.getDescricao());
				aelKitMatPatologia = new AelKitMatPatologia();
			}
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			aelKitMatPatologia = new AelKitMatPatologia();
		} catch (Exception ex) {
			if (ex.getCause().getCause().getCause() instanceof ConstraintViolationException || ex.getCause().getCause().getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio( Severity.ERROR,"MENSAGEM_KIT_MATERIAL_DELETE_ERRO",aelKitMatPatologia.getDescricao());
				aelKitMatPatologia = new AelKitMatPatologia();
			}
		}
	}
	
	public void ativarInativar(final AelKitMatPatologia aelKitMatPatologia) {
		try {
			aelKitMatPatologia.setIndSituacao( (DominioSituacao.A.equals(aelKitMatPatologia.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			
			examesPatologiaFacade.alterarAelKitMatPatologia(aelKitMatPatologia);
			
			this.apresentarMsgNegocio( Severity.INFO, ( DominioSituacao.A.equals(aelKitMatPatologia.getIndSituacao()) 
														   	? "MENSAGEM_KIT_MATERIAL_INATIVADO_SUCESSO" 
															: "MENSAGEM_KIT_MATERIAL_ATIVADO_SUCESSO" 
													  ), aelKitMatPatologia.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public String detalhar(){
		return MANTER_KIT_ITEM_MATERIAL;
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

	public List<AelKitMatPatologia> getLista() {
		return lista;
	}

	public void setLista(List<AelKitMatPatologia> lista) {
		this.lista = lista;
	}

	public Integer getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Integer seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public AelKitMatPatologia getAelKitMatPatologia() {
		return aelKitMatPatologia;
	}

	public void setAelKitMatPatologia(
			AelKitMatPatologia aelKitMatPatologia) {
		this.aelKitMatPatologia = aelKitMatPatologia;
	}

	public AelKitMatPatologia getMaterialSelecionado() {
		return materialSelecionado;
	}

	public void setMaterialSelecionado(AelKitMatPatologia materialSelecionado) {
		this.materialSelecionado = materialSelecionado;
	}
}