package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapTipoAtuacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class RapTipoAtuacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapTipoAtuacao> {
	
	private static final long serialVersionUID = 2647033686179335810L;

	public enum TipoAtuacaoDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TIPO_ATUACAO_DISCENTE_INEXISTENTE;
	}
	
	/**
	 * Retorna o tipo de atuação discente.<br>
	 * Se encontrado mais de um retorna o que possui a chave primária menor.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 *             se não encontrada
	 */
	public RapTipoAtuacao getTipoAtuacaoDiscente() throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapTipoAtuacao.class);
		criteria.add(Restrictions.eq(RapTipoAtuacao.Fields.DISCENTE.toString(),
				Boolean.TRUE));
		criteria.addOrder(Order.asc(RapTipoAtuacao.Fields.CODIGO.toString()));

		List<RapTipoAtuacao> list = executeCriteria(criteria);
		if (list.isEmpty()) {
			throw new ApplicationBusinessException(
					TipoAtuacaoDAOExceptionCode.MENSAGEM_TIPO_ATUACAO_DISCENTE_INEXISTENTE);
		}
		return list.get(0);
	}
}
