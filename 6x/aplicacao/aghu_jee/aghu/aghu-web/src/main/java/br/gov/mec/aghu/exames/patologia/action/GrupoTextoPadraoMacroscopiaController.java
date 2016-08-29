package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoTextoPadraoMacroscopiaController extends ActionController {

	private static final long serialVersionUID = 1986388899457928589L;

	private static final String TEXTO_PADRAO_MACROSCOPIA = "textoPadraoMacroscopia";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private boolean ativo;

	//usado para pesquisar
	private Short seq;
	
	private String descricao;
	
	private DominioSituacao situacao;
	
	private List<AelGrpTxtPadraoMacro> lista;
	
	//Para Adicionar itens
	private AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro;
	
	private boolean editando;

	private AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroExcluir;
	
	public GrupoTextoPadraoMacroscopiaController() {
		aelGrpTxtPadraoMacro = new AelGrpTxtPadraoMacro();
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarGrupoTextoPadraoMacro(seq, descricao, situacao);
		editando = false;
 		aelGrpTxtPadraoMacro = new AelGrpTxtPadraoMacro(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		editando = false;
 		aelGrpTxtPadraoMacro = new AelGrpTxtPadraoMacro(); 
	}
	
	public void gravar() {
		try {
			if(aelGrpTxtPadraoMacro.getSeq() != null){
				examesPatologiaFacade.alterarAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_MACROS_UPDATE_SUCESSO", aelGrpTxtPadraoMacro.getDescricao());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_MACROS_INSERT_SUCESSO", aelGrpTxtPadraoMacro.getDescricao());
			}
			
			aelGrpTxtPadraoMacro = new AelGrpTxtPadraoMacro();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) {
		this.editando = true;
		this.aelGrpTxtPadraoMacro = aelGrpTxtPadraoMacro;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		aelGrpTxtPadraoMacro = new AelGrpTxtPadraoMacro();
	}		
	
	public void excluir() {
		try {
			this.aelGrpTxtPadraoMacro = this.examesPatologiaFacade.obterAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacroExcluir.getSeq());
			aelGrpTxtPadraoMacroExcluir = null;
			
			if(aelGrpTxtPadraoMacro == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro.getSeq());
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_MACROS_DELETE_SUCESSO", aelGrpTxtPadraoMacro.getDescricao());
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}		
		pesquisar();
	}
	
	public void ativarInativar(final AelGrpTxtPadraoMacro elemento) {
		try {
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelGrpTxtPadraoMacro(elemento);
			
			apresentarMsgNegocio( Severity.INFO, 
											    ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
											    	? "MENSAGEM_AEL_GRP_TXT_PADRAO_MACROS_INATIVADO_SUCESSO" 
													: "MENSAGEM_AEL_GRP_TXT_PADRAO_MACROS_ATIVADO_SUCESSO" 
												), elemento.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public String detalharTextoPadraoMacroscopia(){
		return TEXTO_PADRAO_MACROSCOPIA;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
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

	public List<AelGrpTxtPadraoMacro> getLista() {
		return lista;
	}

	public void setLista(List<AelGrpTxtPadraoMacro> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoMacro getAelGrpTxtPadraoMacro() {
		return aelGrpTxtPadraoMacro;
	}

	public void setAelGrpTxtPadraoMacro(AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) {
		this.aelGrpTxtPadraoMacro = aelGrpTxtPadraoMacro;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AelGrpTxtPadraoMacro getAelGrpTxtPadraoMacroExcluir() {
		return aelGrpTxtPadraoMacroExcluir;
	}

	public void setAelGrpTxtPadraoMacroExcluir(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroExcluir) {
		this.aelGrpTxtPadraoMacroExcluir = aelGrpTxtPadraoMacroExcluir;
	}
}
