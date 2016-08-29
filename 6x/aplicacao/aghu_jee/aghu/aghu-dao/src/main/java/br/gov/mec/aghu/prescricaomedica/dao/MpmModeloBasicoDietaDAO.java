package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;

public class MpmModeloBasicoDietaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModeloBasicoDieta> {

	private static final long serialVersionUID = -3442474284587551600L;

	/**
	 * chave composta não deve ser atribuida por sequence.
	 * 
	 * @param servidor
	 */
	@Override
	protected void obterValorSequencialId(MpmModeloBasicoDieta dieta) {
		
		if (dieta == null || dieta.getModeloBasicoPrescricao() == null) {
			
			throw new IllegalArgumentException("Parâmetro obrigatório");
		
		}
		
		MpmModeloBasicoDietaId id = new MpmModeloBasicoDietaId();
		
		id.setModeloBasicoPrescricaoSeq(dieta.getModeloBasicoPrescricao().getSeq());
		id.setSeq(this.getNextVal(SequenceID.MPM_MBD_SQ1));
		dieta.setId(id);

	}

	public List<MpmModeloBasicoDieta> pesquisar(MpmModeloBasicoPrescricao par) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmModeloBasicoDieta.class);

		criteria.add(Restrictions.eq("modeloBasicoPrescricao", par));
		return executeCriteria(criteria);
	}

	/**
	 * Retorna os valotres do banco!
	 * 
	 * @param dieta
	 * @return
	 */
	public MpmModeloBasicoDieta obterOld(MpmModeloBasicoDieta dieta) {
		MpmModeloBasicoDieta result = null;
		if (dieta != null && dieta.getId() != null
				&& dieta.getId().getSeq() != null) {
			this.desatachar(dieta);
			result = this.obterPorChavePrimaria(dieta.getId());
			if (result == null || result.getId() == null
					|| result.getId().getSeq() == null) {
				result = null;
			}
		}
		return result;
	}
}
