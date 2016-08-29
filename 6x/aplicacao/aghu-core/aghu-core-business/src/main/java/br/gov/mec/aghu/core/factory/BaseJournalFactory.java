package br.gov.mec.aghu.core.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.model.BaseJournal;


/**
 * Classe Factory utilizada para atribuir os valores inicias aos da
 * <class>BaseJournal</class>. Evitando assim que o desenvolvedor tenha que
 * sempre atribuí-los.
 * 
 * @author riccosta
 * @author rcorvalao
 */
public class BaseJournalFactory {
	
	private static final Log LOG = LogFactory.getLog(BaseJournalFactory.class);
	
	/**
	 * Retorna instancia da classe Journal já com os atributos da BaseJournal
	 * preenchidos.
	 * 
	 * @param <T>
	 * @param operacao
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T extends BaseJournal> T getBaseJournal(DominioOperacoesJournal operacao, Class<T> clazz, String usuarioLogado) {
		T t = null;

		try {
			t = clazz.newInstance();
			t.setNomeUsuario(usuarioLogado.toUpperCase());
			t.setOperacao(operacao);
			// O construtor da BaseJournal jah seta a dataAlteracao.
			//t.setDataAlteracao(new Date());
		} catch (InstantiationException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}

		return t;
	}

}
