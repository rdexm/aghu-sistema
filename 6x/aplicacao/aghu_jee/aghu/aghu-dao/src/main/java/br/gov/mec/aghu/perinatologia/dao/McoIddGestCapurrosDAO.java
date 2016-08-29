package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.emergencia.vo.CalculoCapurroVO;
import br.gov.mec.aghu.model.McoIddGestCapurros;

public class McoIddGestCapurrosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIddGestCapurros> {

	private static final long serialVersionUID = 4229974624765912120L;

	public List<McoIddGestCapurros> listarIddGestCapurrosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIddGestCapurros.class);
		criteria.add(Restrictions.eq(McoIddGestCapurros.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		return executeCriteria(criteria);
	}
	
	/**
	 * #27482 - C5
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public Byte obterIdadeGestacionalPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoIddGestCapurros.class);
		criteria.add(Restrictions.eq(McoIddGestCapurros.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
	   	criteria.addOrder(Order.asc(McoIddGestCapurros.Fields.CRIADO_EM.toString()));
	   	List<McoIddGestCapurros> lista = executeCriteria(criteria);
	   	if (lista != null) {
			return lista.get(0).getIgSemanas();
		}
		return null;
	}
	
	public List<CalculoCapurroVO> listarCalculoCapurrosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIddGestCapurros.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(McoIddGestCapurros.Fields.CODIGO_PACIENTE.toString())
						, CalculoCapurroVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.IG_SEMANAS.toString())
						, CalculoCapurroVO.Fields.IG_SEMANAS.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.IG_DIAS.toString())
						, CalculoCapurroVO.Fields.IG_DIAS.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.TEXTURA_PELE.toString())
						, CalculoCapurroVO.Fields.TEXTURA_PELE.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.PREGAS_PLANTARES.toString())
						, CalculoCapurroVO.Fields.PREGAS_PLANTARES.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.FORMACAO_MAMILO.toString())
						, CalculoCapurroVO.Fields.FORMACAO_MAMILO.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.FORMA_ORELHA.toString())
						, CalculoCapurroVO.Fields.FORMA_ORELHA.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.GLANDULA_MAMARIA.toString())
						, CalculoCapurroVO.Fields.GLANDULA_MAMARIA.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.SER_MATRICULA.toString())
						, CalculoCapurroVO.Fields.SER_MATRICULA.toString())
				.add(Projections.property(McoIddGestCapurros.Fields.SER_VIN_CODIGO.toString())
						, CalculoCapurroVO.Fields.SER_VIN_CODIGO.toString()));
		
		criteria.add(Restrictions.eq(McoIddGestCapurros.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoCapurroVO.class));
		
		return executeCriteria(criteria);
	}

}