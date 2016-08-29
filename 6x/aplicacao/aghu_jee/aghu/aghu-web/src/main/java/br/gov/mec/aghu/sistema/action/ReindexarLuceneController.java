package br.gov.mec.aghu.sistema.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioReindexaveis;
import br.gov.mec.aghu.sistema.bussiness.ISistemaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ReindexarLuceneController extends ActionController {

	private static final long serialVersionUID = -6213075843832772197L;

	private static final Log LOG = LogFactory
			.getLog(ReindexarLuceneController.class);

	public enum ReindexarLuceneControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SUCCESSO_REINDEXAR_LUCENE, MENSAGEM_ERRO_CLASSE_REINDEXAR_LUCENE, MENSAGEM_ERRO_REINDEXAR_LUCENE, MENSAGEM_ERRO_CARREGAR_DIRETORIO_LUCENE;
	}

	private DominioReindexaveis reindexar;

	@EJB
	private ISistemaFacade sistemaFacade;

	@PostConstruct
	public void iniciar() {
		this.begin(conversation);
	}

	public String reindexar() {
		String className = null;
		if (DominioReindexaveis.TODOS.equals(reindexar)) {
			try {
				for (DominioReindexaveis reindexando : DominioReindexaveis.values()) {
					if (DominioReindexaveis.TODOS.equals(reindexando)) {
						continue;
					}
					className = reindexando.getDescricao();
					sistemaFacade
							.indexar(Class.forName(reindexando.toString()));
				}
				this.apresentarMsgNegocio(
						Severity.INFO,
						ReindexarLuceneControllerExceptionCode.MENSAGEM_SUCCESSO_REINDEXAR_LUCENE
								.toString(), DominioReindexaveis.TODOS);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(
						Severity.ERROR,
						ReindexarLuceneControllerExceptionCode.MENSAGEM_ERRO_CLASSE_REINDEXAR_LUCENE
								.toString(),
						className == null ? DominioReindexaveis.TODOS : className);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(
						Severity.ERROR,
						ReindexarLuceneControllerExceptionCode.MENSAGEM_ERRO_REINDEXAR_LUCENE
								.toString(), e.getMessage());
			}
		} else {
			try {
				sistemaFacade.indexar(Class.forName(reindexar.toString()));
				this.apresentarMsgNegocio(
						Severity.INFO,
						ReindexarLuceneControllerExceptionCode.MENSAGEM_SUCCESSO_REINDEXAR_LUCENE
								.toString(), reindexar);
			} catch (ClassNotFoundException e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(
						Severity.ERROR,
						ReindexarLuceneControllerExceptionCode.MENSAGEM_ERRO_CLASSE_REINDEXAR_LUCENE
								.toString(), reindexar);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(
						Severity.ERROR,
						ReindexarLuceneControllerExceptionCode.MENSAGEM_ERRO_REINDEXAR_LUCENE
								.toString(), e.getMessage());
			}
		}
		return null;
	}

	public DominioReindexaveis getReindexar() {
		return reindexar;
	}

	public void setReindexar(DominioReindexaveis reindexar) {
		this.reindexar = reindexar;
	}

}
