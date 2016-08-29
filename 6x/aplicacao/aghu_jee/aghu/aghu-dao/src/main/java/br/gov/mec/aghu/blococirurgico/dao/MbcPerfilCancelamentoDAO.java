package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcPerfilCancelamento;
import br.gov.mec.aghu.model.MbcPerfilCancelamentoId;

public class MbcPerfilCancelamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcPerfilCancelamento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1617371392815657633L;

	
	@Override
	protected void obterValorSequencialId(MbcPerfilCancelamento elemento) {
		if (elemento == null 
				|| elemento.getMbcMotivoCancelamento() == null
				|| elemento.getPerfil() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		MbcPerfilCancelamentoId id = new MbcPerfilCancelamentoId();
		id.setMtcSeq(elemento.getMbcMotivoCancelamento().getSeq());
		id.setPerNome(elemento.getPerfil().getNome());
		
		elemento.setId(id);
	}
	
	
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(MbcPerfilCancelamento.class);
	}
	
	
	protected DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, Short mtcSeq) {
		criteria.add(Restrictions.eq(
				MbcPerfilCancelamento.Fields.MTC_SEQ.toString(), mtcSeq));
		
		return criteria;
	}
	
	
	public List<MbcPerfilCancelamento> listarPerfisCancelamentos(Short mtcSeq) {
		DetachedCriteria criteria = this.obterCriteria();
		this.obterCriterioConsulta(criteria, mtcSeq);
		
		return this.executeCriteria(criteria);
	}

	
	public MbcPerfilCancelamento obterPerfilCancelamento(Short mtcSeq, String nomePerfil) {
		DetachedCriteria criteria = this.obterCriteria();
		this.obterCriterioConsulta(criteria, mtcSeq);
		criteria.add(Restrictions.eq(MbcPerfilCancelamento.Fields.PER_NOME.toString(), nomePerfil));
		
		List<MbcPerfilCancelamento> result = this.executeCriteria(criteria);
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
}
