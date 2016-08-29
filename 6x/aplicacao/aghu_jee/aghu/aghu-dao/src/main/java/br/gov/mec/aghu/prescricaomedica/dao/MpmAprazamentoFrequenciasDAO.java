package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmAprazamentoFrequenciaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;

public class MpmAprazamentoFrequenciasDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAprazamentoFrequencia> {

	private static final long serialVersionUID = 7781168332346515246L;

	public Long pesquisarQuantidadeTipoFreequencia(Short tfqSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAprazamentoFrequencia.class);

		criteria.add(Restrictions.eq("id.tfqSeq", tfqSeq));

		return executeCriteriaCount(criteria);
	}

	public List<MpmAprazamentoFrequencia> listarAprazamentosFrequenciaPorTipo(MpmTipoFrequenciaAprazamento tipo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAprazamentoFrequencia.class);

		criteria.add(Restrictions.eq(MpmAprazamentoFrequencia.Fields.TIPO_FREQUENCIA_APRAZAMENTO.toString(), tipo));
		
		criteria.createAlias(MpmAprazamentoFrequencia.Fields.RAP_SERVIDOR.toString(), "MAF_SER", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(MpmAprazamentoFrequencia.Fields.RAP_SERVIDOR_PESSOA_FISICA.toString(), "MAF_SER_PF", JoinType.LEFT_OUTER_JOIN);
		
		return this.executeCriteria(criteria);
	}
	
	public MpmAprazamentoFrequenciaId criaId(Short tfqSeq){
		DetachedCriteria criteria = DetachedCriteria
			.forClass(MpmAprazamentoFrequencia.class);
		criteria.setProjection(Projections.max(MpmAprazamentoFrequencia.Fields.SEQP.toString()));
		Short seqp= (Short) this.executeCriteriaUniqueResult(criteria);
		if (seqp==null){
			seqp=1;
		}else{
			seqp++;
		}
		MpmAprazamentoFrequenciaId id= new MpmAprazamentoFrequenciaId();
		id.setSeqp(seqp);
		id.setTfqSeq(tfqSeq);
		
		return id;
	}

}