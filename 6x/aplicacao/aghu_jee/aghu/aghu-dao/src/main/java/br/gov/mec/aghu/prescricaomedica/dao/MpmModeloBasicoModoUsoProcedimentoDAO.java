package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;

public class MpmModeloBasicoModoUsoProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModeloBasicoModoUsoProcedimento> {
	
	private static final long serialVersionUID = -7960605284817252627L;

	@Override
	protected void obterValorSequencialId(MpmModeloBasicoModoUsoProcedimento elemento) {

		if (elemento == null || elemento.getModeloBasicoProcedimento() == null) {
			
			throw new IllegalArgumentException("Parâmetro obrigatório");
		
		}
		
		MpmModeloBasicoModoUsoProcedimentoId id = new MpmModeloBasicoModoUsoProcedimentoId();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoModoUsoProcedimento.class);
		criteria.add(Restrictions.eq(MpmModeloBasicoModoUsoProcedimento.Fields.MBP_MDB_SEQ.toString(), elemento.getModeloBasicoProcedimento().getModeloBasicoPrescricao().getSeq()));
		criteria.add(Restrictions.eq(MpmModeloBasicoModoUsoProcedimento.Fields.MBP_SEQ.toString(), elemento.getModeloBasicoProcedimento().getId().getSeq()));
		criteria.add(Restrictions.eq(MpmModeloBasicoModoUsoProcedimento.Fields.TUP_PED_SEQ.toString(), elemento.getTipoModoUsoProcedimento().getId().getPedSeq()));
		criteria.setProjection(Projections.max(MpmModeloBasicoModoUsoProcedimento.Fields.TUP_SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		
		id.setModeloBasicoPrescricaoSeq(elemento.getModeloBasicoProcedimento().getModeloBasicoPrescricao().getSeq());
		id.setModeloBasicoProcedimentoSeq(elemento.getModeloBasicoProcedimento().getId().getSeq());
		id.setTipoModoUsoProcedimentoSeq(elemento.getTipoModoUsoProcedimento().getId().getPedSeq());
		id.setTipoModoUsoSeqp(++seqp);
		
		elemento.setId(id);
	
	}

	public List<MpmModeloBasicoModoUsoProcedimento> pesquisar(
			MpmModeloBasicoProcedimento par) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoModoUsoProcedimento.class);
        criteria.createAlias(MpmTipoModoUsoProcedimento.Fields.SERVIDOR.toString(),"SERV", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(MpmModeloBasicoModoUsoProcedimento.Fields.TIPO_MODO_USO_PROCEDIMENTO.toString(),"TMU", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("TMU.".concat(MpmTipoModoUsoProcedimento.Fields.UNIDADE_MEDIDA_MEDICA.toString()),"UMD", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("modeloBasicoProcedimento", par));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna true quando existir modelo básico com o modo de uso, e false caso contrário.
	 * 
	 * @param tipoModoUso
	 * @return
	 */
	public boolean existeModeloBasicoComModoUso(Short pedSeq, Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoModoUsoProcedimento.class);

		criteria.add(Restrictions.eq(MpmModeloBasicoModoUsoProcedimento.Fields.TIPO_MOD_USO_PROCEDIMENTO_PED_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.eq(MpmModeloBasicoModoUsoProcedimento.Fields.TIPO_MOD_USO_PROCEDIMENTO_SEQP.toString(), seqp));

		return executeCriteriaCount(criteria) > 0;
	}
		
}
