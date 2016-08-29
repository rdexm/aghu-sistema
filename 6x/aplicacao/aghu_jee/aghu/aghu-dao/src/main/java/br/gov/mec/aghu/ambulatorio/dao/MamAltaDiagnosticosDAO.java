package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamAltaDiagnosticos;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolDiagnosticoVO;

public class MamAltaDiagnosticosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAltaDiagnosticos> {

	private static final long serialVersionUID = -607123134911338653L;

	public List<MamAltaDiagnosticos> procurarAltaDiagnosticosBySumarioAltaEIndSelecionado(
			MamAltaSumario altaSumario, DominioSimNao indSelecionado) {
		
		DetachedCriteria criteria = getCriteriaProcurarAltaDiagnosticosBySumarioAltaEIndSelecionado(
				altaSumario, indSelecionado);
		
		return executeCriteria(criteria);
	}
	
	public List<MamAltaDiagnosticos> pesquisarAltaDiagnosticosPorSumarioAlta(Long altaSumarioSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaDiagnosticos.class);
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.ALTA_SUMARIO_SEQ.toString(), altaSumarioSeq));
		return executeCriteria(criteria);
	}

	public List<MamAltaDiagnosticos> pesquisarAltaDiagnosticosPorSumarioAltaECid(Long altaSumarioSeq, Integer cidSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaDiagnosticos.class);
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.ALTA_SUMARIO_SEQ.toString(), altaSumarioSeq));
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.CID_SEQ.toString(), cidSeq));
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaProcurarAltaDiagnosticosBySumarioAltaEIndSelecionado(
			MamAltaSumario altaSumario, DominioSimNao indSelecionado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaDiagnosticos.class);
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.ALTA_SUMARIO.toString(), altaSumario));
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.IND_SELECIONADO.toString(), DominioSimNao.S.equals(indSelecionado)));
		return criteria;
	}

	public List<AltaAmbulatorialPolDiagnosticoVO> recuperarAltaAmbPolDiagnosticoList(Long seqMamAltaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaDiagnosticos.class);
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MamAltaDiagnosticos.Fields.SEQ.toString()), AltaAmbulatorialPolDiagnosticoVO.Fields.SEQ.toString())
		.add(Projections.property(MamAltaDiagnosticos.Fields.DESCRICAO.toString()), AltaAmbulatorialPolDiagnosticoVO.Fields.DESCRICAO.toString())
		.add(Projections.property(MamAltaDiagnosticos.Fields.COMPLEMENTO.toString()), AltaAmbulatorialPolDiagnosticoVO.Fields.COMPLEMENTO.toString());

		criteria.setProjection(projection);	
		
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.ALTA_SUMARIO_SEQ.toString(), seqMamAltaSumario));
		criteria.add(Restrictions.eq(MamAltaDiagnosticos.Fields.IND_SELECIONADO.toString(), true));
		
		criteria.addOrder(Order.asc(AltaAmbulatorialPolDiagnosticoVO.Fields.SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AltaAmbulatorialPolDiagnosticoVO.class));

		return executeCriteria(criteria);
	}
	
}
