package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghArquivoProcessamentoLog;

/**
 * 
 * @author Geraldo Maciel
 * 
 */
public class AghArquivoProcessamentoLogDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghArquivoProcessamentoLog> {


	private static final long serialVersionUID = -2101723848977887210L;

	private DetachedCriteria criarCriteria() {
		return DetachedCriteria.forClass(AghArquivoProcessamentoLog.class);
	}

	private DetachedCriteria criarCriteriaLogPorPorArquivosIds(List<Integer> arquivos) {
		DetachedCriteria criteria = criarCriteria();
		criteria.add(Restrictions.in(AghArquivoProcessamentoLog.Fields.ID_ARQUIVO.toString(), arquivos));
		return criteria;
	}

	private DetachedCriteria criarCriteriaLogPorPorArquivosIdsProjetado(List<Integer> arquivos) {
		DetachedCriteria criteria = criarCriteriaLogPorPorArquivosIds(arquivos);
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghArquivoProcessamentoLog.Fields.MENSAGEM.toString())));
		//criteria.addOrder(Order.asc(AghArquivoProcessamentoLog.Fields.ID_ARQUIVO.toString()));
		criteria.addOrder(Order.asc(AghArquivoProcessamentoLog.Fields.DTHR_CRIADO_EM.toString()));
		return criteria;
	}

	public String pesquisarLogsPorArquivosIds(List<Integer> arquivos, Integer maxLength) {
		DetachedCriteria criteria = criarCriteriaLogPorPorArquivosIdsProjetado(arquivos);
		List<Object> result = executeCriteria(criteria);
		return tratarResultLog(result, maxLength);
	}

	private String tratarResultLog(List<Object> result, Integer maxLength) {
		StringBuilder sb = new StringBuilder();
		if (result == null || result.isEmpty()) {
			return sb.toString();
		}
		for (Object ret : result) {
			if (ret != null) {
				sb.append(ret.toString());
				if (maxLength != null && sb.length() >= maxLength.intValue()) {
					break;
				}
			}
		}
		return sb.toString();
	}
}
