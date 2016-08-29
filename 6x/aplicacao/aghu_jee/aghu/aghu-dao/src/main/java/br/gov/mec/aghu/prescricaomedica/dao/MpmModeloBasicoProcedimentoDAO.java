package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimentoId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;

public class MpmModeloBasicoProcedimentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModeloBasicoProcedimento> {
	
	private static final long serialVersionUID = -6169565906844303699L;

	@Override
	protected void obterValorSequencialId(MpmModeloBasicoProcedimento modeloBasicoProcedimento) {
		
		if (modeloBasicoProcedimento == null || modeloBasicoProcedimento.getModeloBasicoPrescricao() == null) {
			
			throw new IllegalArgumentException("Parâmetro obrigatório");
		
		}
		
		MpmModeloBasicoProcedimentoId id = new MpmModeloBasicoProcedimentoId();
		id.setSeq(this.getNextVal(SequenceID.MPM_MBP_SQ1).shortValue());
		id.setModeloBasicoPrescricaoSeq(modeloBasicoProcedimento.getModeloBasicoPrescricao().getSeq());
		modeloBasicoProcedimento.setId(id);
		
	}

	public List<MpmModeloBasicoProcedimento> pesquisar(
			MpmModeloBasicoPrescricao par) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoProcedimento.class);
		criteria.add(Restrictions.eq("modeloBasicoPrescricao", par));
		return executeCriteria(criteria);
	}

	/**
	 * Retorna true quando existir modelo básico com o procedimento especial e false caso contrário.
	 * 
	 * @param procedimento
	 * @return
	 */
	public boolean existeModeloBasicoComProcedimentoEspecial(MpmProcedEspecialDiversos procedimento) {
		if(procedimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoProcedimento.class);
		criteria.add(Restrictions.eq(MpmModeloBasicoProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString(), procedimento));

		return executeCriteriaCount(criteria) > 0;
	}


    public MpmModeloBasicoProcedimento obterModeloBasicoProcedimentoPorChavePrimaria(MpmModeloBasicoProcedimentoId id, boolean left, Enum ...fields) {
        DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoProcedimento.class);
        criteria.createAlias(MpmModeloBasicoProcedimento.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(MpmModeloBasicoProcedimento.Fields.PROCEDIMENTO_CIRURGICO.toString(), "proc_cir", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(MpmModeloBasicoProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString(), "proc_esp", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq(MpmModeloBasicoProcedimento.Fields.ID.toString(), id));
        return (MpmModeloBasicoProcedimento) executeCriteriaUniqueResult(criteria);
    }
			
}