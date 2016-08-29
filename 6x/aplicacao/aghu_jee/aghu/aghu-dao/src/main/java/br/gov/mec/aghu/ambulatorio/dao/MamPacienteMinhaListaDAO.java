package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.MamPacienteMinhaLista;
import br.gov.mec.aghu.model.MamTriagens;

public class MamPacienteMinhaListaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPacienteMinhaLista> {

	private static final long serialVersionUID = 5749331210834902942L;
	
	public List<Object[]> listarAtendimentosPacienteTriagemPorCodigo(Integer pacCodigo) { 
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTriagens.class);

		criteria.add(Restrictions.eq(MamTriagens.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MamTriagens.Fields.DTHR_INICIO.toString()));
		p.add(Projections.property(MamTriagens.Fields.UNF_SEQ.toString()));

		criteria.setProjection(p);
				
		return executeCriteria(criteria);
	}
	
	public List<MamPacienteMinhaLista> listarTriagensPorSeq(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamPacienteMinhaLista.class);

		criteria.add(Restrictions.eq(MamPacienteMinhaLista.Fields.TRG_SEQ.toString(), trgSeq));

		return executeCriteria(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamPacienteMinhaLista buscarPacMinhaListaPorPacCodigoSeq(Integer pacCodigo, Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamPacienteMinhaLista.class);

		criteria.add(Restrictions.ne(MamPacienteMinhaLista.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MamPacienteMinhaLista.Fields.TRG_SEQ.toString(), trgSeq));
		List<MamPacienteMinhaLista> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
