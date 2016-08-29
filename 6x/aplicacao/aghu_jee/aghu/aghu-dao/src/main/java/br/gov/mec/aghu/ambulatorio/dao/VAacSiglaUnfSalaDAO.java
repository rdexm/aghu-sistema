package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;

import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;

public class VAacSiglaUnfSalaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<VAacSiglaUnfSala> {


	private static final long serialVersionUID = -6154327845368080813L;

	/**
	 * Obtem sala através dos atributos da ID
	 * 
	 * @param sala
	 * @param unfSeq
	 * @return
	 */
	public VAacSiglaUnfSala obterVAacSiglaUnfSalaPeloId(byte sala, short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(VAacSiglaUnfSala.class);
		criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.SALA.toString(),
				sala));
		criteria.add(Restrictions.eq(
				VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), unfSeq));
		return (VAacSiglaUnfSala) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * Retorna lista contendo valores distintos para unfSeq, zona e descricao
	 * 
	 * @param objPesquisa
	 * @param situacao
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public List<VAacSiglaUnfSalaVO> pesquisarTodasZonas(String objPesquisa) {
		return pesquisarZonas(objPesquisa, false);
	}

	@SuppressWarnings("unchecked")
	public List<VAacSiglaUnfSalaVO> pesquisarZonas(String objPesquisa) {
		return pesquisarZonas(objPesquisa, true);
	}

	@SuppressWarnings("unchecked")
	public List<VAacSiglaUnfSalaVO> pesquisarZonas(String objPesquisa,
			boolean ativas) {


		// Consulta escrita em HQL devido a necessidade de distinct em mais de
		// uma coluna
		StringBuffer hql = montarHqlPesquisarZonas(ativas);

		if (StringUtils.isNotBlank(objPesquisa)) {
			hql.append("and upper(sigla) = :srtPesquisa ");
		}
		hql.append("order by sigla asc");

		Query query = createHibernateQuery(hql.toString());

		if (StringUtils.isNotBlank(objPesquisa)) {
			query.setParameter("srtPesquisa", objPesquisa.toUpperCase());
		}

		List<Object[]> resultList = query.list();

		if (resultList.isEmpty()) {
			StringBuffer hql2 = montarHqlPesquisarZonas(ativas);
			if (StringUtils.isNotBlank(objPesquisa)) {
				hql2.append("and upper(descricao) like :srtPesquisa ");
			}
			hql2.append("order by sigla asc");

			Query query2 = createHibernateQuery(hql2.toString());

			if (StringUtils.isNotBlank(objPesquisa)) {
				query2.setParameter("srtPesquisa",
						"%" + objPesquisa.toUpperCase() + "%");
			}
			resultList = query2.list();
		}

		List<VAacSiglaUnfSalaVO> zonas = new ArrayList<VAacSiglaUnfSalaVO>(0);

		for (Object[] object : resultList) {
			VAacSiglaUnfSalaVO vo = new VAacSiglaUnfSalaVO();

			if (object[0] != null) {
				vo.setUnfSeq(Short.valueOf(object[0].toString()));
			}

			if (object[1] != null) {
				vo.setSigla(object[1].toString());
			}

			if (object[2] != null) {
				vo.setDescricao(object[2].toString());
			}

			zonas.add(vo);
		}

		return zonas;
	}

	private StringBuffer montarHqlPesquisarZonas(boolean ativas) {

		StringBuffer hql = new StringBuffer(110);
		hql.append("select distinct v.id.unfSeq, v.sigla, v.descricao ");
		hql.append("from VAacSiglaUnfSala v ");
		hql.append("where 1=1 ");
		if (ativas) {
			hql.append("and indSitUndFunc = 'A' ");
		}

		return hql;
	}

	/**
	 * Executa o cursor de solicitante
	 * 
	 * @param unfSeq
	 * @param sala
	 * @param dtConsulta
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public List<VAacSiglaUnfSala> executaCursorSolicitante(Short unfSeq,
			Byte sala, Date dtConsulta) throws ApplicationBusinessException,
			ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(VAacSiglaUnfSala.class);
		criteria.add(Restrictions.eq(
				VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.SALA.toString(),
				sala));

		List<VAacSiglaUnfSala> listaCriteria = executeCriteria(criteria);	

		return listaCriteria;
	}
	
	/**
	 * @author thiago.cortes
	 * C1 # 6807
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<VAacSiglaUnfSalaVO> pesquisarListaSetorSala(String pesquisa) throws ApplicationBusinessException{
		DetachedCriteria criteria = filtroPesquisarListaSetorSala(pesquisa);
		criteria.addOrder(Order.asc(VAacSiglaUnfSala.Fields.SIGLA.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarListaSetorSalaCount(String pesquisa) throws ApplicationBusinessException{
		DetachedCriteria criteria = filtroPesquisarListaSetorSala(pesquisa);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria filtroPesquisarListaSetorSala(String pesquisa) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAacSiglaUnfSala.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(VAacSiglaUnfSala.Fields.UNF_SEQ.toString()),VAacSiglaUnfSalaVO.Fields.SEQ.toString())
				.add(Projections.property(VAacSiglaUnfSala.Fields.SIGLA.toString()),VAacSiglaUnfSalaVO.Fields.SETOR.toString())
				.add(Projections.property(VAacSiglaUnfSala.Fields.SALA.toString()),VAacSiglaUnfSalaVO.Fields.SALA.toString()));
				
		if(StringUtils.isNotBlank(pesquisa)){
			if(CoreUtil.isNumeroByte(pesquisa)){
				criteria.add(Restrictions.or(
						 Restrictions.eq(VAacSiglaUnfSala.Fields.UNF_SEQ.toString(),Short.valueOf(pesquisa)),
						 Restrictions.eq(VAacSiglaUnfSala.Fields.SALA.toString(),Byte.valueOf(pesquisa))
						,Restrictions.ilike(VAacSiglaUnfSala.Fields.SIGLA.toString(),pesquisa,MatchMode.ANYWHERE)));
			} else if (CoreUtil.isNumeroShort(pesquisa)) {
				criteria.add(Restrictions.or(Restrictions.ilike(VAacSiglaUnfSala.Fields.SIGLA.toString(),pesquisa,MatchMode.ANYWHERE), 
						Restrictions.eq(VAacSiglaUnfSala.Fields.UNF_SEQ.toString(),Short.valueOf(pesquisa))));
			} else {
				criteria.add(Restrictions.ilike(VAacSiglaUnfSala.Fields.SIGLA.toString(),pesquisa,MatchMode.ANYWHERE));
			}				
		}try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(
					VAacSiglaUnfSalaVO.class.getConstructor(Short.class, String.class, Byte.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		return criteria;
	}

}
