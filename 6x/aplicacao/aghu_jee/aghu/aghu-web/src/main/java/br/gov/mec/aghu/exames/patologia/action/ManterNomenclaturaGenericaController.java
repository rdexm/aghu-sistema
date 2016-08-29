package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Grupos de textos padrão macroscopia
 * 
 */

public class ManterNomenclaturaGenericaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -5583529922679717998L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private boolean ativo;

	private Integer seq;

	private String descricao;

	private DominioSituacao situacao;

	private List<AelNomenclaturaGenerics> lista;

	// Para Adicionar itens
	private AelNomenclaturaGenerics aelNomenclaturaGenerics;

	private Integer seqExcluir;

	public ManterNomenclaturaGenericaController() {
		aelNomenclaturaGenerics = new AelNomenclaturaGenerics();
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelNomenclaturaGenerics(seq, descricao, situacao);
		aelNomenclaturaGenerics = new AelNomenclaturaGenerics();
		ativo = true;
	}

	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		situacao = null;
		lista = null;
		aelNomenclaturaGenerics = new AelNomenclaturaGenerics();
	}

	public void gravar() {
		try {

			if (aelNomenclaturaGenerics.getSeq() != null) {
				examesPatologiaFacade.alterarAelNomenclaturaGenerics(aelNomenclaturaGenerics);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOMENCLATURA_UPDATE_SUCESSO", aelNomenclaturaGenerics.getDescricao());

				// examesPatologiaFacade.refresh(aelNomenclaturaGenerics);

			} else {
				examesPatologiaFacade.inserirAelNomenclaturaGenerics(aelNomenclaturaGenerics);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOMENCLATURA_INSERT_SUCESSO", aelNomenclaturaGenerics.getDescricao());
			}

			aelNomenclaturaGenerics = new AelNomenclaturaGenerics();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}

	public void excluir() {
		try {
			this.aelNomenclaturaGenerics = this.examesPatologiaFacade.obterAelNomenclaturaGenericsPorChavePrimaria(seqExcluir);
			String descricao = aelNomenclaturaGenerics.getDescricao();
			examesPatologiaFacade.excluirAelNomenclaturaGenerics(aelNomenclaturaGenerics);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOMENCLATURA_DELETE_SUCESSO", descricao);

			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOMENCLATURA_DELETE_ERRO", descricao);
				aelNomenclaturaGenerics = new AelNomenclaturaGenerics();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			aelNomenclaturaGenerics = new AelNomenclaturaGenerics();
		}
	}

	public void ativarInativar(final Integer seq) {
		try {

			if (seq != null) {
				aelNomenclaturaGenerics = this.examesPatologiaFacade.obterAelNomenclaturaGenericsPorChavePrimaria(seq);
				aelNomenclaturaGenerics.setIndSituacao((DominioSituacao.A.equals(aelNomenclaturaGenerics.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A));

				examesPatologiaFacade.alterarAelNomenclaturaGenerics(aelNomenclaturaGenerics);

				this.apresentarMsgNegocio(Severity.INFO, (DominioSituacao.A.equals(aelNomenclaturaGenerics.getIndSituacao()) ? "MENSAGEM_NOMENCLATURA_INATIVADA_SUCESSO"
						: "MENSAGEM_NOMENCLATURA_ATIVADA_SUCESSO"), aelNomenclaturaGenerics.getDescricao());

				aelNomenclaturaGenerics = new AelNomenclaturaGenerics();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
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

	public List<AelNomenclaturaGenerics> getLista() {
		return lista;
	}

	public void setLista(List<AelNomenclaturaGenerics> lista) {
		this.lista = lista;
	}

	public Integer getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Integer seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public AelNomenclaturaGenerics getAelNomenclaturaGenerics() {
		return aelNomenclaturaGenerics;
	}

	public void setAelNomenclaturaGenerics(AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		this.aelNomenclaturaGenerics = aelNomenclaturaGenerics;
	}
}