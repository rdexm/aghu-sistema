package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidadoId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;

public class MpmModeloBasicoCuidadoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModeloBasicoCuidado> {

	private static final long serialVersionUID = -385490045112522541L;

	/**
	 * @see GenericDAO#obterValorSequencialId(Object)
	 * 
	 * @param MpmModeloBasicoCuidado
	 */
	@Override
	protected void obterValorSequencialId(MpmModeloBasicoCuidado elemento) {
		
		if (elemento == null || elemento.getModeloBasicoPrescricao() == null) {
			
			throw new IllegalArgumentException("Parâmetro obrigatório");
		
		}
		
		int value = this.getNextVal(SequenceID.MPM_MCU_SQ1).intValue();
		
		MpmModeloBasicoCuidadoId id = new MpmModeloBasicoCuidadoId();
		id.setSeq(value);
		id.setModeloBasicoPrescricaoSeq(elemento.getModeloBasicoPrescricao().getSeq());
		
		elemento.setId(id);

	}

	public List<MpmModeloBasicoCuidado> listar(
			MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoCuidado.class);

		criteria.add(Restrictions.eq(
				MpmModeloBasicoCuidado.Fields.MODELO_BASICO_PRESCRICAO_SEQ
						.toString(), mpmModeloBasicoPrescricao.getSeq()));
		return executeCriteria(criteria);
	}
	
	public List<MpmModeloBasicoCuidado> listar(
			Integer modeloBasicoPrescricaoSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoCuidado.class);
		
		criteria.createAlias(MpmModeloBasicoCuidado.Fields.TIPO_FREQUENCIA_APRAZAMENTO.toString(), "TFP", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(
				MpmModeloBasicoCuidado.Fields.MODELO_BASICO_PRESCRICAO_SEQ
						.toString(), modeloBasicoPrescricaoSeq));
				
		return executeCriteria(criteria);
	}

}
