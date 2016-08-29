package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoTextoPadraoMicroscopiaController extends ActionController {


	private static final long serialVersionUID = 620354237466776635L;

	private static final String TEXTO_PADRAO_MICROSCOPIA = "textoPadraoMicroscopia";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private boolean ativo;

	//usado para pesquisar
	private Short seq;
	
	private String descricao;
	
	private DominioSituacao situacao;
	
	private List<AelGrpTxtPadraoMicro> lista;
	
	//Para Adicionar itens
	private AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro;
	
	private boolean editando;

	private Short seqExcluir;
	
	public GrupoTextoPadraoMicroscopiaController() {
		aelGrpTxtPadraoMicro = new AelGrpTxtPadraoMicro();
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarGrupoTextoPadraoMicro(seq, descricao, situacao);
		editando = false;
 		aelGrpTxtPadraoMicro = new AelGrpTxtPadraoMicro(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		editando = false;
 		aelGrpTxtPadraoMicro = new AelGrpTxtPadraoMicro(); 
	}
	
	public void gravar() {
		try {
			if(aelGrpTxtPadraoMicro.getSeq() != null){
				examesPatologiaFacade.alterarAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_MICROS_UPDATE_SUCESSO", aelGrpTxtPadraoMicro.getDescricao());
				cancelarEdicao();

			} else {
				examesPatologiaFacade.inserirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_MICROS_INSERT_SUCESSO", aelGrpTxtPadraoMicro.getDescricao());
			}
			
			aelGrpTxtPadraoMicro = new AelGrpTxtPadraoMicro();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar(final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) {
		this.editando = true;
		this.aelGrpTxtPadraoMicro = aelGrpTxtPadraoMicro;
	}
		
	public void cancelarEdicao() {
		editando = false;
		pesquisar();
		aelGrpTxtPadraoMicro = new AelGrpTxtPadraoMicro();
	}		
	
	public void excluir() {
		try {
			this.aelGrpTxtPadraoMicro = this.examesPatologiaFacade.obterAelGrpTxtPadraoMicro(seqExcluir);
			seqExcluir = null;
			
			if(aelGrpTxtPadraoMicro == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			examesPatologiaFacade.excluirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro.getSeq());
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_MICROS_DELETE_SUCESSO", aelGrpTxtPadraoMicro.getDescricao());
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}		
		pesquisar();
	}
	
	public void ativarInativar(final AelGrpTxtPadraoMicro elemento) {
		try {
			elemento.setIndSituacao( (DominioSituacao.A.equals(elemento.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesPatologiaFacade.alterarAelGrpTxtPadraoMicro(elemento);
			
			this.apresentarMsgNegocio( Severity.INFO, ( DominioSituacao.A.equals(elemento.getIndSituacao()) 
														 ? "MENSAGEM_AEL_GRP_TXT_PADRAO_MICROS_INATIVADO_SUCESSO" 
														 : "MENSAGEM_AEL_GRP_TXT_PADRAO_MICROS_ATIVADO_SUCESSO" 
													  ), elemento.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public String detalharTextoPadraoMicroscopia(){
		return TEXTO_PADRAO_MICROSCOPIA;
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

	public List<AelGrpTxtPadraoMicro> getLista() {
		return lista;
	}

	public void setLista(List<AelGrpTxtPadraoMicro> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoMicro getAelGrpTxtPadraoMicro() {
		return aelGrpTxtPadraoMicro;
	}

	public void setAelGrpTxtPadraoMicro(AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) {
		this.aelGrpTxtPadraoMicro = aelGrpTxtPadraoMicro;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public Short getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Short seqExcluir) {
		this.seqExcluir = seqExcluir;
	}
}