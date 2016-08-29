package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatTiposDocumento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatTiposDocumentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTiposDocumento> {

	private static final long serialVersionUID = -4921988434974337737L;

	/**
	 * Método auxiliar que cria DetachedCriteria a partir do codigo. Código é
	 * AK da tabela AAC_PAGADORES.
	 * 
	 * @param codigo
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaPorCodigoCriteria(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatTiposDocumento.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(FatTiposDocumento.Fields.SEQ
					.toString(), codigo));
		}

		return criteria;
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir de parâmetros.
	 * 
	 * @param codigo
	 *            ou descrição.
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaCriteria(String codDesc) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatTiposDocumento.class);

		if (StringUtils.isNotBlank(codDesc)) {
			if (CoreUtil.isNumeroShort(codDesc)) {
				criteria.add(Restrictions.eq(FatTiposDocumento.Fields.SEQ
						.toString(), Short.parseShort(codDesc)));
			} else {
				criteria.add(Restrictions.ilike(
						FatTiposDocumento.Fields.DESCRICAO.toString(), codDesc,
						MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	/**
	 * Obtem Tipo de documento a partir de código.
	 * 
	 * @param seqTipoDoc
	 * @return FatTiposDocumento
	 */
	public FatTiposDocumento obterTipoDoc(Short seqTipoDoc) {
		DetachedCriteria criteria = createPesquisaPorCodigoCriteria(seqTipoDoc);

		return (FatTiposDocumento) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método para listar os Tipo de Docs de acordo com parametro de pesquisa.
	 * 
	 * @param paramPesquisa
	 * @return List
	 */
	public List<FatTiposDocumento> obterTiposDocs(String seqDesc) {
		DetachedCriteria criteria = createPesquisaCriteria(seqDesc);

		return executeCriteria(criteria);
	}

}
