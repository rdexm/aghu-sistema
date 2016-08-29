package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudoId;
import br.gov.mec.aghu.model.AghEspecialidades;

public class AelEspecialidadeCampoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelEspecialidadeCampoLaudo> {
	
	private static final long serialVersionUID = 8535669809373559373L;

	@Override
	protected void obterValorSequencialId(AelEspecialidadeCampoLaudo elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		if (elemento.getCampoLaudo() == null) {
			throw new IllegalArgumentException("Associacao com AelCampoLaudo nao esta corretamente informada!");
		}
		
		if (elemento.getEspecialidade() == null) {
			throw new IllegalArgumentException("Associacao com AghEspecialidades nao esta corretamente informada!");
		}
		
		final Integer calSeq = elemento.getCampoLaudo().getSeq();
		final Short espSeq = elemento.getEspecialidade().getSeq();

		AelEspecialidadeCampoLaudoId id = new AelEspecialidadeCampoLaudoId();
		id.setCalSeq(calSeq);
		id.setEspSeq(espSeq);
		
		elemento.setId(id);
		
	}
	
	/**
	 * Pesquisa Especialidade Campo Laudo por Especialidade
	 * @param especialidade
	 * @return
	 */
	public List<AelEspecialidadeCampoLaudo> pesquisarEspecialidadeCampoLaudoPorEspecialidade(AghEspecialidades especialidade) {
		return this.pesquisarEspecialidadeCampoLaudoPorEspecialidade(especialidade.getSeq());
	}

	/**
	 * Pesquisa Especialidade Campo Laudo por Especialidade
	 * @param espSeq
	 * @return
	 */
	public List<AelEspecialidadeCampoLaudo> pesquisarEspecialidadeCampoLaudoPorEspecialidade(Short espSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelEspecialidadeCampoLaudo.class, "ECL");
		criteria.createAlias("ECL." + AelEspecialidadeCampoLaudo.Fields.CAMPO_LAUDO.toString(), "CAL_ECL", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ECL." + AelEspecialidadeCampoLaudo.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("CAL_ECL." + AelCampoLaudo.Fields.FLUXO.toString(), Boolean.TRUE));

		criteria.addOrder(Order.asc("ECL." + AelEspecialidadeCampoLaudo.Fields.ORDEM.toString()));

		return executeCriteria(criteria);
	}

}
