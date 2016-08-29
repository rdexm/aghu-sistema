package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapAfastamento;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @modulo registrocolaborador
 */
public class RapAfastamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapAfastamento> {
	
	private static final long serialVersionUID = 1242138682567075807L;
	
	/**
	 * Consultar afastamentos por matrícula e vínculo
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	public boolean verificarAfastamento(Integer matricula, Short vinCodigo) {

		if (matricula == null || vinCodigo == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios.");
		}

		Date dataAtual = DateUtil.obterDataComHoraInical(null);
		boolean existeAfastamento = false;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapAfastamento.class);
		criteria.add(Restrictions.eq(
				RapAfastamento.Fields.MATRICULA_SERVIDOR.toString(), matricula));
		criteria.add(Restrictions.eq(
				RapAfastamento.Fields.CODIGO_VINCULO_SERVIDOR.toString(),
				vinCodigo));
		criteria.add(Restrictions.le(
				RapAfastamento.Fields.DT_INICIO.toString(), dataAtual));
		criteria.add(Restrictions.or(Restrictions
				.isNotNull(RapAfastamento.Fields.DT_FIM.toString()),
				Restrictions.ge(RapAfastamento.Fields.DT_FIM.toString(),
						dataAtual)));

		if (executeCriteriaCount(criteria) > 0) {
			existeAfastamento = true;
		}

		return existeAfastamento;
	}
}
