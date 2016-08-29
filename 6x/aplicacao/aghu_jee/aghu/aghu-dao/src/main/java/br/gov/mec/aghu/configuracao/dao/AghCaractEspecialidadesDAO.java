package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidades;

public class AghCaractEspecialidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCaractEspecialidades> {
	
	private static final long serialVersionUID = 5196929406706022155L;

	public Long listarCaracteristicasEspecialidadesCount(Short espSeq, DominioCaracEspecialidade caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractEspecialidades.class);

		criteria.createAlias(AghCaractEspecialidades.Fields.ESPECIALIDADE.toString(), "esp");

		criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), espSeq));

		criteria.add(Restrictions.eq(AghCaractEspecialidades.Fields.CARACTERISTICA.toString(), caracteristica));

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método para fazer busca de especialidade pelo seu seq e sua
	 * característica.
	 * 
	 * @author Stanley Araujo
	 * @param seq
	 *            - Código da
	 * @param caracteristica
	 * @return lista de unidades funcionais
	 */
	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(Short seq, DominioCaracEspecialidade caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractEspecialidades.class);
		criteria.add(Restrictions.eq(AghCaractEspecialidades.Fields.CODIGO.toString(), seq));
		criteria.add(Restrictions.eq(AghCaractEspecialidades.Fields.CARACTERISTICA.toString(), caracteristica));
		return executeCriteria(criteria);
	}
	
	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidadePorEspecialidade(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractEspecialidades.class);
		criteria.add(Restrictions.eq(AghCaractEspecialidades.Fields.ESPECIALIDADE.toString()+"."+AghEspecialidades.Fields.SEQ.toString(), espSeq));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisaCaracteristicaPorEspecialidade(AghEspecialidades especialidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractEspecialidades.class);
		criteria.createAlias(AghCaractEspecialidades.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), especialidade.getSeq()));
		return criteria;
	}
	
	public Long pesquisarCaracteristicasEspecialidadesCount(AghEspecialidades especialidade) {
		DetachedCriteria criteria = montarCriteriaPesquisaCaracteristicaPorEspecialidade(especialidade);
		return executeCriteriaCount(criteria);
	}


	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(Integer firstResult, Integer maxResult, String orderProperty, AghEspecialidades especialidade, boolean ordenacao) {
		DetachedCriteria criteria = montarCriteriaPesquisaCaracteristicaPorEspecialidade(especialidade);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, ordenacao);
	}
	
}
