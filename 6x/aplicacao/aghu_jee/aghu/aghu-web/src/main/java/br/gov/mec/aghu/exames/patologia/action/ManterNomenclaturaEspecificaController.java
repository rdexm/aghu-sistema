package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaEspecsId;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Textos padrão macroscopia
 * 
 */

public class ManterNomenclaturaEspecificaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 2262331435987402854L;

	private static final String PAGE_EXAMES_MANTER_NOMENCLATURA_GENERICA = "exames-manterNomenclaturaGenerica";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Integer seqAelNomenclaturaGenerics;

	private List<AelNomenclaturaEspecs> lista;

	// Pai
	private AelNomenclaturaGenerics aelNomenclaturaGenerics;

	// Filho
	private AelNomenclaturaEspecs aelNomenclaturaEspecs;

	private Short seqp;

	public void iniciar() {
	 

		aelNomenclaturaGenerics = this.examesPatologiaFacade.obterAelNomenclaturaGenericsPorChavePrimaria(seqAelNomenclaturaGenerics);
		criaObjetoInsercao();
		pesquisar();
	
	}

	private void criaObjetoInsercao() {
		aelNomenclaturaEspecs = new AelNomenclaturaEspecs();
		AelNomenclaturaEspecsId id = new AelNomenclaturaEspecsId();
		id.setLugSeq(aelNomenclaturaGenerics.getSeq());
		aelNomenclaturaEspecs.setId(id);
		aelNomenclaturaEspecs.setAelNomenclaturaGenerics(aelNomenclaturaGenerics);
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelNomenclaturaEspecsPorAelNomenclaturaGenerics(aelNomenclaturaGenerics);
	}

	public void gravar() {
		try {

			if (aelNomenclaturaEspecs.getId().getSeqp() != null) {
				examesPatologiaFacade.alterarAelNomenclaturaEspecs(aelNomenclaturaEspecs);

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOMENCLATURA_ESPECIFICA_UPDATE_SUCESSO", aelNomenclaturaEspecs.getDescricao());
				// examesPatologiaFacade.refresh(aelNomenclaturaEspecs);

			} else {
				examesPatologiaFacade.inserirAelNomenclaturaEspecs(aelNomenclaturaEspecs);

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOMENCLATURA_ESPECIFICA_INSERT_SUCESSO", aelNomenclaturaEspecs.getDescricao());
			}

			criaObjetoInsercao();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}

	public void excluir() {
		String descricao = null;
		try {
			aelNomenclaturaEspecs = examesPatologiaFacade.obterAelNomenclaturaEspecsPorChavePrimaria(new AelNomenclaturaEspecsId(aelNomenclaturaGenerics.getSeq(), seqp));
			descricao = aelNomenclaturaEspecs.getDescricao();
			examesPatologiaFacade.excluirAelNomenclaturaEspecs(aelNomenclaturaEspecs);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOMENCLATURA_ESPECIFICA_DELETE_SUCESSO", descricao);

			criaObjetoInsercao();
			pesquisar();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOMENCLATURA_DELETE_ERRO", descricao);
				criaObjetoInsercao();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			criaObjetoInsercao();
		} finally {
			this.seqp = null;
		}
	}

	public void cancelarExclusao() {
		this.seqp = null;
	}

	public void ativarInativar(final Short seqp) {

		try {

			if (aelNomenclaturaGenerics.getSeq() != null && seqp != null) {

				aelNomenclaturaEspecs = examesPatologiaFacade.obterAelNomenclaturaEspecsPorChavePrimaria(new AelNomenclaturaEspecsId(aelNomenclaturaGenerics.getSeq(), seqp));
				aelNomenclaturaEspecs.setIndSituacao((DominioSituacao.A.equals(aelNomenclaturaEspecs.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A));

				examesPatologiaFacade.alterarAelNomenclaturaEspecs(aelNomenclaturaEspecs);

				apresentarMsgNegocio(Severity.INFO, (DominioSituacao.A.equals(aelNomenclaturaEspecs.getIndSituacao()) ? "MENSAGEM_NOMENCLATURA_ESPECIFICA_INATIVADA_SUCESSO"
						: "MENSAGEM_NOMENCLATURA_ESPECIFICA_ATIVADA_SUCESSO"), aelNomenclaturaEspecs.getDescricao());

				criaObjetoInsercao();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}

	public String voltar() {

		this.seqAelNomenclaturaGenerics = null;
		this.lista = null;
		this.aelNomenclaturaGenerics = null;
		this.aelNomenclaturaEspecs = null;
		this.seqp = null;

		return PAGE_EXAMES_MANTER_NOMENCLATURA_GENERICA;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getSeqAelNomenclaturaGenerics() {
		return seqAelNomenclaturaGenerics;
	}

	public void setSeqAelNomenclaturaGenerics(Integer seqAelNomenclaturaGenerics) {
		this.seqAelNomenclaturaGenerics = seqAelNomenclaturaGenerics;
	}

	public AelNomenclaturaGenerics getAelNomenclaturaGenerics() {
		return aelNomenclaturaGenerics;
	}

	public void setAelNomenclaturaGenerics(AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		this.aelNomenclaturaGenerics = aelNomenclaturaGenerics;
	}

	public AelNomenclaturaEspecs getAelNomenclaturaEspecs() {
		return aelNomenclaturaEspecs;
	}

	public void setAelNomenclaturaEspecs(AelNomenclaturaEspecs aelNomenclaturaEspecs) {
		this.aelNomenclaturaEspecs = aelNomenclaturaEspecs;
	}

	public void setLista(List<AelNomenclaturaEspecs> lista) {
		this.lista = lista;
	}

	public List<AelNomenclaturaEspecs> getLista() {
		return lista;
	}
}