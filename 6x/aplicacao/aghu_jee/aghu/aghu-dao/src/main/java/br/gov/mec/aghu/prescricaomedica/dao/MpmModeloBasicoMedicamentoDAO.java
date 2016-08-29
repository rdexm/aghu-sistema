package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;

public class MpmModeloBasicoMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModeloBasicoMedicamento> {

	private static final long serialVersionUID = 2423912838382104454L;

	@Override
	/**
	 * @see GenericDAO#obterValorSequencialId(Object)
	 * 
	 * @param MpmModeloBasicoMedicamento
	 */
	
	protected void obterValorSequencialId(MpmModeloBasicoMedicamento elemento) {
		if (elemento == null || elemento.getModeloBasicoPrescricao() == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		int value = this.getNextVal(SequenceID.MPM_MBM_SQ1).intValue();
		
		MpmModeloBasicoMedicamentoId id = new MpmModeloBasicoMedicamentoId();
		id.setModeloBasicoPrescricaoSeq(elemento.getModeloBasicoPrescricao().getSeq());
		id.setSeq(value);
		elemento.setId(id);
	}
	
	public List<MpmModeloBasicoMedicamento> pesquisarSolucoes(MpmModeloBasicoPrescricao par) {
		if (par == null || par.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio invalido");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoMedicamento.class);

		criteria.add(Restrictions.eq(MpmModeloBasicoMedicamento.Fields.MOD_BASIC_PRESCRICAO.toString(), par));
		criteria.add(Restrictions.eq(MpmModeloBasicoMedicamento.Fields.IND_SOLUCAO.toString(), Boolean.TRUE));
		
		return executeCriteria(criteria);
	}

	public List<MpmModeloBasicoMedicamento> pesquisar(
			MpmModeloBasicoPrescricao par) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoMedicamento.class);

		criteria.add(Restrictions.eq("modeloBasicoPrescricao", par));
		return executeCriteria(criteria);
	}

	/**
	 * @param modeloBasicoPrescricaoSeq
	 * @return
	 */
	public List<MpmModeloBasicoMedicamento> listarMedicamentos(
			Integer modeloBasicoPrescricaoSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoMedicamento.class);
		criteria.add(Restrictions.eq(
				MpmModeloBasicoMedicamento.Fields.MODELO_BASICO_PRESCRICAO_SEQ
						.toString(), modeloBasicoPrescricaoSeq));
		criteria.add(Restrictions.eq(
					MpmModeloBasicoMedicamento.Fields.IND_SOLUCAO
							.toString(), false));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoMedicamentoSeq
	 * @return
	 */
	public MpmModeloBasicoMedicamento obterModeloBasicoMedicamento(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoMedicamentoSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoMedicamento.class);
		criteria.add(Restrictions.eq(MpmModeloBasicoMedicamento.Fields.MODELO_BASICO_PRESCRICAO_SEQ.toString(), modeloBasicoPrescricaoSeq));
		criteria.add(Restrictions.eq(MpmModeloBasicoMedicamento.Fields.SEQ.toString(), modeloBasicoMedicamentoSeq));
		return (MpmModeloBasicoMedicamento) executeCriteriaUniqueResult(criteria);
		
	}
    public MpmModeloBasicoMedicamento obterModeloBasicoSolucao(Integer seqModelo, Integer seqItemModelo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(MpmModeloBasicoMedicamento.class);

        criteria.createAlias(MpmModeloBasicoMedicamento.Fields.VIA_ADMINISTRACAO.toString(), "via", JoinType.INNER_JOIN);
        criteria.createAlias(MpmModeloBasicoMedicamento.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "tfa", JoinType.INNER_JOIN);
        criteria.createAlias(MpmModeloBasicoMedicamento.Fields.TIPO_VELOCIDADE_ADMINISTRACAO.toString(), "tva", JoinType.LEFT_OUTER_JOIN);


        criteria.add(Restrictions.eq(MpmModeloBasicoMedicamento.Fields.MODELO_BASICO_PRESCRICAO_SEQ.toString(), seqModelo));
        criteria.add(Restrictions.eq(MpmModeloBasicoMedicamento.Fields.SEQ.toString(), seqItemModelo));
        return (MpmModeloBasicoMedicamento) executeCriteriaUniqueResult(criteria);


    }
}
