package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoTextoPadraoDiagnosticoController extends ActionController {

	private static final long serialVersionUID = -966313022600395008L;

	private static final String TEXTO_PADRAO_DIAGNOSTICO = "textoPadraoDiagnostico";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	
	private boolean ativo;

	// usado para pesquisar
	private Short seq;

	private String descricao;

	private DominioSituacao situacao;

	private List<AelGrpTxtPadraoDiags> lista;

	// Para Adicionar itens
	private AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags;

	private boolean editando;

	private AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsExcluir;

	public GrupoTextoPadraoDiagnosticoController() {
		this.aelGrpTxtPadraoDiags = new AelGrpTxtPadraoDiags();
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		this.lista = this.examesPatologiaFacade.pesquisarAelGrpTxtPadraoDiags(seq, descricao, situacao);
		this.editando = false;
		this.aelGrpTxtPadraoDiags = new AelGrpTxtPadraoDiags();
		this.ativo = true;
	}

	public void limpar() {
		this.ativo = false;
		this.seq = null;
		this.descricao = null;
		this.situacao = null;
		this.lista = null;
		this.editando = false;
		this.aelGrpTxtPadraoDiags = new AelGrpTxtPadraoDiags();
	}

	public void gravar() {
		try {
			final boolean novo = this.aelGrpTxtPadraoDiags.getSeq() == null;
			this.examesPatologiaFacade.persistirAelGrpTxtPadraoDiags(this.aelGrpTxtPadraoDiags);
			
			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_AEL_GRP_TXT_PADRAO_DIAGNOSTICO_INSERT_SUCESSO" : "MENSAGEM_AEL_GRP_TXT_PADRAO_DIAGNOSTICO_UPDATE_SUCESSO",
							this.aelGrpTxtPadraoDiags.getDescricao());
			this.cancelarEdicao();

			this.aelGrpTxtPadraoDiags = new AelGrpTxtPadraoDiags();

		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}

	public void editar(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		this.editando = true;
		this.aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiags;
	}

	public void cancelarEdicao() {
		this.editando = false;
		this.aelGrpTxtPadraoDiags = new AelGrpTxtPadraoDiags();
	}

	public void excluir() {
		try {
			this.examesPatologiaFacade.excluirAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiagsExcluir.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_TXT_PADRAO_DIAGNOSTICO_DELETE_SUCESSO", aelGrpTxtPadraoDiagsExcluir.getDescricao());
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}

	public void ativarInativar(final AelGrpTxtPadraoDiags elemento) {
		try {
			String msg;
			if(DominioSituacao.A.equals(elemento.getIndSituacao())){
				elemento.setIndSituacao(DominioSituacao.I);
				msg = "MENSAGEM_AEL_GRP_TXT_PADRAO_DIAGNOSTICO_INATIVADO_SUCESSO";
				
			} else {
				elemento.setIndSituacao(DominioSituacao.A);
				msg = "MENSAGEM_AEL_GRP_TXT_PADRAO_DIAGNOSTICO_ATIVADO_SUCESSO";
			}
			
			examesPatologiaFacade.persistirAelGrpTxtPadraoDiags(elemento);
			apresentarMsgNegocio(Severity.INFO, msg, elemento.getDescricao());

		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		this.pesquisar();
	}

	public String detalharTextoPadraoDiagnostico(){
		return TEXTO_PADRAO_DIAGNOSTICO;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(final boolean ativo) {
		this.ativo = ativo;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(final Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(final DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AelGrpTxtPadraoDiags> getLista() {
		return lista;
	}

	public void setLista(final List<AelGrpTxtPadraoDiags> lista) {
		this.lista = lista;
	}

	public AelGrpTxtPadraoDiags getAelGrpTxtPadraoDiags() {
		return aelGrpTxtPadraoDiags;
	}

	public void setAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		this.aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiags;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(final boolean editando) {
		this.editando = editando;
	}

	public AelGrpTxtPadraoDiags getAelGrpTxtPadraoDiagsExcluir() {
		return aelGrpTxtPadraoDiagsExcluir;
	}

	public void setAelGrpTxtPadraoDiagsExcluir(
			AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsExcluir) {
		this.aelGrpTxtPadraoDiagsExcluir = aelGrpTxtPadraoDiagsExcluir;
	}

}
