package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatModalidadeAtendimento;

public class FatModalidadeAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatModalidadeAtendimento> {

	private static final long serialVersionUID = -935569514963847001L;

	public FatModalidadeAtendimento buscarFatModalidadeAtendimento(final Short codigo) {
		final FatModalidadeAtendimento fatModalidadeAtendimento = findByPK(FatModalidadeAtendimento.class, codigo);
		return fatModalidadeAtendimento;
	}

	public List<FatModalidadeAtendimento> listarFatModalidadeAtendimentoSemExcluidos(final List<Short> excluidos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatModalidadeAtendimento.class);
		criteria.add(Restrictions.not(Restrictions.in(FatModalidadeAtendimento.Fields.CODIGO.toString(), excluidos)));
		return executeCriteria(criteria);
	}
	
	/**
	 * #36463 - C11 
	 * @param codigo
	 * @return
	 */
	public FatModalidadeAtendimento obterModalidadeAtivaPorCodigo(Short codigo) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatModalidadeAtendimento.class);
		criteria.add(Restrictions.eq(FatModalidadeAtendimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatModalidadeAtendimento.Fields.CODIGO.toString(), codigo));
		
		return (FatModalidadeAtendimento) executeCriteriaUniqueResult(criteria);
	}
}
