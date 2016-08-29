package br.gov.mec.aghu.farmacia.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MedicamentosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.view.VMpmViaAdm;

public class AfaViaAdministracaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaViaAdministracao> {
	


	private static final long serialVersionUID = -8761341936785916133L;


	/**
	 * /** Lista somente as vias cadastradas para os medicamentos, conforme seus
	 * códigos e conforme o código da unidade de internação do paciente.
	 * 
	 * @param strPesquisa
	 * @param medMatCodigo
	 * @param (opcional) seqUnidadeFuncional
	 * @return
	 */
	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa,
			List<Integer> medMatCodigo, Short seqUnidadeFuncional) {
		List<AfaViaAdministracao> lista = null;

		if (StringUtils.isNotBlank(strPesquisa)) {
			DetachedCriteria cri = DetachedCriteria
					.forClass(AfaViaAdministracao.class);
	
			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					JoinType.LEFT_OUTER_JOIN);
	
			if (seqUnidadeFuncional != null) {
				cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						JoinType.LEFT_OUTER_JOIN);
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
								+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
						DominioSituacao.A));
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
								+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
						seqUnidadeFuncional));
			}
	
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(),
					strPesquisa, MatchMode.EXACT));
	
			cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));
	
			lista = executeCriteria(cri);
		}
		
		if (lista == null || lista.isEmpty()) {
			DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracao.class);

			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					JoinType.LEFT_OUTER_JOIN);

			if (seqUnidadeFuncional != null) {
				cri.createAlias(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						JoinType.LEFT_OUTER_JOIN);
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
								+ "."
								+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
						DominioSituacao.A));
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
								+ "." + AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
						seqUnidadeFuncional));
			}

			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			
			if (StringUtils.isNotBlank(strPesquisa)) {
				cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO
						.toString(), strPesquisa, MatchMode.ANYWHERE));
			}

			cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));

			lista = executeCriteria(cri);
		}

		return lista;
	}

	/**
	 * 
	 * @param medMatCodigo
	 * @param (opcional) seqUnidadeFuncional
	 * @return
	 */
	public Long listarViasMedicamentoCount(String strPesquisa,
			List<Integer> medMatCodigo, Short seqUnidadeFuncional) {

		Long count = 0L;
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			DetachedCriteria cri = DetachedCriteria
					.forClass(AfaViaAdministracao.class);
	
			cri.setProjection(Projections.distinct(Projections
					.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));
	
			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					Criteria.INNER_JOIN);
	
			if (seqUnidadeFuncional != null) {
				cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						Criteria.INNER_JOIN);
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
								+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
						DominioSituacao.A));
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
								+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
						seqUnidadeFuncional));
			}
	
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(),
					strPesquisa, MatchMode.EXACT));
	
			count = (Long) executeCriteriaUniqueResult(cri);
		}
		
		if (count.equals(0L)) {
			DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracao.class);

			cri.setProjection(Projections.distinct(Projections
					.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));
			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					Criteria.INNER_JOIN);

			if (seqUnidadeFuncional != null) {
				cri.createAlias(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						Criteria.INNER_JOIN);
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
								+ "."
								+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
						DominioSituacao.A));
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
								+ "." + AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
						seqUnidadeFuncional));
			}

			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			
			if (StringUtils.isNotBlank(strPesquisa)) {
				cri.add(Restrictions.ilike(
						AfaViaAdministracao.Fields.DESCRICAO.toString(),
						strPesquisa, MatchMode.ANYWHERE));
			}
			
			count = (Long) executeCriteriaUniqueResult(cri);
		}
		
		return count;
	}

	/**
	 * Busca as vias de administração não utilizadas em uma forma de dosagem
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AfaViaAdministracao> listarViasAdmNaoUtilizadas(String strPesquisa, Set<AfaViaAdministracaoMedicamento> listaViasAdm) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracao.class);

//		cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
//				 AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
//				  Criteria.LEFT_JOIN);
		
		if (!StringUtils.isEmpty(strPesquisa)) {
			cri.add(Restrictions.ilike(
					AfaViaAdministracao.Fields.SIGLA.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}
		cri.add(Restrictions.eq(
				AfaViaAdministracao.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));
		
		List<AfaViaAdministracao> lista = executeCriteria(cri);
		
		return removerViasAdministracaoExistentes(lista, listaViasAdm);
	}
	
	private List<AfaViaAdministracao> removerViasAdministracaoExistentes(List<AfaViaAdministracao> lista, Set<AfaViaAdministracaoMedicamento> listaViasAdm) {
		List<AfaViaAdministracao> listAfaViaAdministracaoMedicamento = getListaAfaViaAdministracaoMedicamento(listaViasAdm);
		lista.removeAll(listAfaViaAdministracaoMedicamento);
		return lista;
	}

	private List<AfaViaAdministracao> getListaAfaViaAdministracaoMedicamento(Set<AfaViaAdministracaoMedicamento> listaViasAdm) {
		List<AfaViaAdministracao> retorno = new ArrayList<AfaViaAdministracao>();
		
		for (AfaViaAdministracaoMedicamento viaAdmMed : listaViasAdm) {
			retorno.add(viaAdmMed.getViaAdministracao());
		}
		
		return retorno;
	}

	/**
	 * @param strPesquisa
	 * @param (opcional) unfSeq
	 * @return
	 */
	public List<AfaViaAdministracao> listarTodasAsVias(String strPesquisa,
			Short unfSeq) {

		List<AfaViaAdministracao> lista = null;

		DetachedCriteria cri = DetachedCriteria
				.forClass(AfaViaAdministracao.class);

		if (unfSeq != null) {
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					Criteria.INNER_JOIN);
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
							+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (!StringUtils.isEmpty(strPesquisa)) {
			cri.add(Restrictions.ilike(
					AfaViaAdministracao.Fields.SIGLA.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}
		cri.add(Restrictions.eq(
				AfaViaAdministracao.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));

		lista = executeCriteria(cri);
	
		if(lista.isEmpty()){
			cri = DetachedCriteria.forClass(AfaViaAdministracao.class);
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(), AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(), Criteria.INNER_JOIN);

			if(!StringUtils.isEmpty(strPesquisa)) {
				cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}		
			cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() +"."+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
			cri.add(Restrictions.eq(AfaViaAdministracao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			
			cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));
			
			lista = executeCriteria(cri);
		}

		return lista;
	}

	public Long listarTodasAsViasCount(String strPesquisa, Short unfSeq) {
		
		Long count = 0L;
		
		DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracao.class);
		cri.setProjection(Projections.distinct(Projections.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));

		
		if(!StringUtils.isEmpty(strPesquisa)) {
			cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		if(unfSeq!=null){
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					Criteria.INNER_JOIN);
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
							+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
		}


		cri.add(Restrictions.eq(AfaViaAdministracao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		count = (Long) executeCriteriaUniqueResult(cri);
		
		if(count.equals(0L)){
			cri = DetachedCriteria.forClass(AfaViaAdministracao.class);
			cri.setProjection(Projections.distinct(Projections.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));

			if(!StringUtils.isEmpty(strPesquisa)) {
				cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}

			if(unfSeq!=null){
				cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
						Criteria.INNER_JOIN);
				cri.add(Restrictions.eq(
						AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
								+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
			}

			cri.add(Restrictions.eq(AfaViaAdministracao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

			count = (Long) executeCriteriaUniqueResult(cri);
		}
		
		return count;
	}

	/**
	 * Lista somente as vias cadastradas para os medicamentos, conforme seus
	 * códigos e SEM utilizar o código da unidade de internação do paciente.
	 * 
	 * @param strPesquisa
	 * @param medMatCodigo
	 * @return
	 */
	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa,
			List<Integer> medMatCodigo) {
		List<AfaViaAdministracao> lista = null;

		if (StringUtils.isNotBlank(strPesquisa)) {
			DetachedCriteria cri = DetachedCriteria
					.forClass(AfaViaAdministracao.class);
			
			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					Criteria.INNER_JOIN);
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					Criteria.INNER_JOIN);
	
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
							+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			// cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
			// + "." + AfaViaAdmUnf.Fields.UNF_SEQ.toString() ,
			// seqUnidadeFuncional));
			cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(),
					strPesquisa, MatchMode.EXACT));
	
			cri.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));
	
			lista = executeCriteria(cri);
		}
		
		if (lista == null || lista.isEmpty()) {
			DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracao.class);

			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					Criteria.INNER_JOIN);
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					Criteria.INNER_JOIN);

			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
							+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			// cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
			// + "." + AfaViaAdmUnf.Fields.UNF_SEQ.toString() ,
			// seqUnidadeFuncional));
			if (StringUtils.isNotBlank(strPesquisa)) {
				cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO
						.toString(), strPesquisa, MatchMode.ANYWHERE));
			}

			cri.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));

			lista = executeCriteria(cri);
		}

		return lista;
	}

	/**
	 * 
	 * @return
	 */
	public Long listarViasMedicamentoCount(String strPesquisa,
			List<Integer> medMatCodigo) {
		Long count = 0L;

		if (StringUtils.isNotBlank(strPesquisa)) {
			DetachedCriteria cri = DetachedCriteria
					.forClass(AfaViaAdministracao.class);
	
			cri.setProjection(Projections.distinct(Projections
					.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));
	
			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					Criteria.INNER_JOIN);
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					Criteria.INNER_JOIN);
	
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString() + "."
							+ VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
							+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			// cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
			// + "." + AfaViaAdmUnf.Fields.UNF_SEQ.toString() ,
			// seqUnidadeFuncional));
			cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(),
					strPesquisa, MatchMode.EXACT));
	
			count = (Long) executeCriteriaUniqueResult(cri);
		}
	
		if (count.equals(0)) {
			DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracao.class);
			
			cri.setProjection(Projections.distinct(Projections
					.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));
			cri.createAlias(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString(),
					Criteria.INNER_JOIN);
			cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
					Criteria.INNER_JOIN);

			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			cri.add(Restrictions.in(
					AfaViaAdministracao.Fields.VIEW_MPM_VIAS_ADM.toString()
							+ "." + VMpmViaAdm.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
			cri.add(Restrictions.eq(
					AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString() + "."
							+ AfaViaAdmUnf.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
			// cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
			// + "." + AfaViaAdmUnf.Fields.UNF_SEQ.toString() ,
			// seqUnidadeFuncional));
			
			if (StringUtils.isNotBlank(strPesquisa)) {
				cri.add(Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO
						.toString(), strPesquisa, MatchMode.ANYWHERE));
			}

			count = (Long) executeCriteriaUniqueResult(cri);
		}
		return count;
	}

	/**
	 * Busca Todas as vias sem filtro por unidade de internação
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AfaViaAdministracao> listarTodasAsVias(String strPesquisa) {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AfaViaAdministracao.class);
		// cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
		// AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
		// Criteria.INNER_JOIN);

		if (!StringUtils.isEmpty(strPesquisa)) {
			cri.add(Restrictions.ilike(
					AfaViaAdministracao.Fields.SIGLA.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}
		// cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
		// +"."+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
		cri.add(Restrictions.eq(
				AfaViaAdministracao.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));

		return executeCriteria(cri);
	}

	public Long listarTodasAsViasCount(String strPesquisa) {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AfaViaAdministracao.class);
		cri.setProjection(Projections.distinct(Projections
				.countDistinct(AfaViaAdministracao.Fields.SIGLA.toString())));
		// cri.createAlias(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
		// AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString(),
		// Criteria.INNER_JOIN);

		if (!StringUtils.isEmpty(strPesquisa)) {
			cri.add(Restrictions.ilike(
					AfaViaAdministracao.Fields.SIGLA.toString(), strPesquisa,
					MatchMode.ANYWHERE));
		}
		// cri.add(Restrictions.eq(AfaViaAdministracao.Fields.VIAS_ADM_UNF.toString()
		// +"."+ AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
		cri.add(Restrictions.eq(
				AfaViaAdministracao.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		// cri.addOrder(Order.asc(AfaViaAdministracao.Fields.SIGLA.toString()));

		return (Long) executeCriteriaUniqueResult(cri); // executeCriteria(cri);
	}

	public Long pesquisarViasAdministracaoCount(String sigla,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = montarConsultaViasAdministracao(sigla,
				descricao, situacao);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarConsultaViasAdministracao(String sigla,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaViaAdministracao.class);

		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA
					.toString(), sigla, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AfaViaAdministracao.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					AfaViaAdministracao.Fields.IND_SITUACAO.toString(),
					situacao));
		}

		return criteria;
	}

	public List<AfaViaAdministracao> pesquisarViasAdministracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String sigla, String descricao,
			DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaViaAdministracao.class);

		criteria.createAlias(AfaViaAdministracao.Fields.SERVIDOR.toString(), "SER1", JoinType.INNER_JOIN);
		criteria.createAlias("SER1." + RapServidores.Fields.SERVIDOR.toString(), "SER2", JoinType.INNER_JOIN);
		criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);

		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA
					.toString(), sigla.toUpperCase(), MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO
					.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AfaViaAdministracao.Fields.IND_SITUACAO
					.toString(), situacao));
		}

		criteria.addOrder(Order
				.asc(AfaViaAdministracao.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	/**
	 * Verificar a existência de registros de tipo item de dieta em outras entidades
	 * @param tipoDieta
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeItemViaAdministracao(AfaViaAdministracao viaAdministracao, Class class1, Enum field) {

		if (viaAdministracao == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),viaAdministracao));
		
		return (executeCriteriaCount(criteria) > 0);
	}
	
	/**
	 * Valida se já existe via com a mesma sigla.
	 * @param sigla
	 */
	public boolean existeSiglaCadastrada(String sigla) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaViaAdministracao.class);
		criteria.add(Restrictions.eq(AfaViaAdministracao.Fields.SIGLA.toString(), sigla.trim()));
		
		return (executeCriteriaCount(criteria) > 0);
		
	}
	
	
	public List<AfaViaAdministracao> obterViaAdminstracaoSiglaouDescricao(Object param){
		return executeCriteria(criarCriteriaComFiltrosViaAdminstracaoSiglaouDescricao(param));
	}

	private DetachedCriteria criarCriteriaComFiltrosViaAdminstracaoSiglaouDescricao(
			Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaViaAdministracao.class);
		
		if(param != null){
			criteria.add(Restrictions.or(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(), param.toString(), MatchMode.ANYWHERE), 
						Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE)));
		}
		return criteria;
	}

	public Long obterViaAdminstracaoSiglaouDescricaoCount(String param) {
		return executeCriteriaCount(criarCriteriaComFiltrosViaAdminstracaoSiglaouDescricao(param));
	}
	
	/**
	 * @author marcelo.deus
	 * #44281 - C3
	 */
	public List<AfaViaAdministracao> listarSbViaAdministracaoMedicamento(String param, List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias){
		
		DetachedCriteria criteria = montarCriteriaSbViaMedicamentos(param, listaMedicamentos, todasVias);
		
		criteria.addOrder(Order.asc("VAD." + AfaViaAdministracao.Fields.SIGLA.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false);

	}
	
	public Long listarSbViaAdministracaoMedicamentoCount(String param, List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias){
		
		DetachedCriteria criteria = montarCriteriaSbViaMedicamentos(param, listaMedicamentos, todasVias);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaSbViaMedicamentos(String param,	List<ProtocoloItensMedicamentoVO> listaMedicamentos, Boolean todasVias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaViaAdministracao.class, "VAD");
		
		criteria.createAlias("VAD." + AfaViaAdministracao.Fields.VIA_ADM_MEDICAMENTOS.toString(), "VAM");
		
		criteria.add(Restrictions.eq("VAM." + AfaViaAdministracaoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(listaMedicamentos != null && !listaMedicamentos.isEmpty() && !todasVias){
			List<Integer> listaCodigoMedicamento = new ArrayList<Integer>();
			if(listaMedicamentos != null && !listaMedicamentos.isEmpty()){
				for(int i = 0; i < listaMedicamentos.size(); i ++){
					listaCodigoMedicamento.add(listaMedicamentos.get(i).getMedMatCodigo());
				}
				criteria.add(Restrictions.in("VAM." + AfaViaAdministracaoMedicamento.Fields.MED_MAT_CODIGO.toString(), listaCodigoMedicamento));
			}
		}
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.groupProperty("VAD." + AfaViaAdministracao.Fields.DESCRICAO.toString()), AfaViaAdministracao.Fields.DESCRICAO.toString())
				.add(Projections.groupProperty("VAD." + AfaViaAdministracao.Fields.SIGLA.toString()), AfaViaAdministracao.Fields.SIGLA.toString())));
		
		if(param != null){
			criteria.add(Restrictions.or(Restrictions.ilike(AfaViaAdministracao.Fields.SIGLA.toString(), param, MatchMode.ANYWHERE), 
						Restrictions.ilike(AfaViaAdministracao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE)));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(AfaViaAdministracao.class));
		return criteria;
	}
	
	
	private DetachedCriteria pesquisarVia(String objPesquisa, Boolean todasVias, MedicamentosVO medMatCodigo){
	
		Integer matCodigo = null;
		
		if(medMatCodigo != null && medMatCodigo.getMedMatCodigo() != null){
			matCodigo = medMatCodigo.getMedMatCodigo();
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaViaAdministracaoMedicamento.class);
		
		criteria.createAlias(AfaViaAdministracaoMedicamento.Fields.VIA_ADMINISTRACAO.toString(), "VAD" , JoinType.INNER_JOIN);
		
		if (objPesquisa != null) {
			criteria.add(Restrictions.or(Restrictions.ilike("VAD." + AfaViaAdministracao.Fields.SIGLA.toString(), objPesquisa, MatchMode.ANYWHERE), 
					Restrictions.ilike("VAD." + AfaViaAdministracao.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE)));		
		}
	
		if(todasVias != null && !todasVias){
			criteria.add(Restrictions.eq(AfaViaAdministracaoMedicamento.Fields.MED_MAT_CODIGO.toString(), matCodigo));
		}
			
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("VAD." + AfaViaAdministracao.Fields.DESCRICAO.toString()).as(AfaViaAdministracao.Fields.DESCRICAO.toString()))
				.add(Projections.property("VAD." + AfaViaAdministracao.Fields.SIGLA.toString()).as(AfaViaAdministracao.Fields.SIGLA.toString()))));
	
		criteria.setResultTransformer(Transformers.aliasToBean(AfaViaAdministracao.class));
		
		return criteria;
	}
	
	public List<AfaViaAdministracao> pesquisaVia(String objPesquisa, Boolean todasVias, MedicamentosVO medMatCodigo){
		DetachedCriteria criteria = pesquisarVia(objPesquisa, todasVias, medMatCodigo);
		criteria.addOrder(Order.asc("VAD." + AfaViaAdministracao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisaViaCount(String objPesquisa, Boolean todasVias, MedicamentosVO medMatCodigo){
		DetachedCriteria criteria = pesquisarVia(objPesquisa, todasVias, medMatCodigo);
		return executeCriteriaCountDistinct(criteria, "VAD." + AfaViaAdministracao.Fields.DESCRICAO.toString(), true);
	}
	
}
