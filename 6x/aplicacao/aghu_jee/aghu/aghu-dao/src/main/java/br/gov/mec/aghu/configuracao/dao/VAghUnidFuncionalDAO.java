package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.farmacia.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.prescricaomedica.vo.LocalPacienteVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;

public class VAghUnidFuncionalDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAghUnidFuncional>{
	
	private static final long serialVersionUID = -34834643857796392L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(VAghUnidFuncional.class);
    }
	
	public String obterDescricaoVAghUnidFuncional(Short seq) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria.setProjection(Projections.property(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()));
		criteria.add(Restrictions.eq(VAghUnidFuncional.Fields.SEQ.toString(), seq));
		return (String)executeCriteriaUniqueResult(criteria);
	}
	
	public List<VAghUnidFuncional> obterUnidFuncionalPorCaracteristicaInternacaoOuEmergencia(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class, "view");
		ConstanteAghCaractUnidFuncionais[] caracteristicas = {ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA};
		criteria.add(Property.forName("view." + VAghUnidFuncional.Fields.SEQ.toString()).in(montarSubQueryCaracteristicasUnidadeFuncional(caracteristicas)));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("view." + VAghUnidFuncional.Fields.SEQ.toString()), VAghUnidFuncional.Fields.SEQ.toString());
		p.add(Projections.property("view." + VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()), VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString());
		criteria.setProjection(p);
		
		if (CoreUtil.isNumeroInteger(parametro)) {
			criteria.add(Restrictions.eq("view." + VAghUnidFuncional.Fields.SEQ.toString(), parametro.toString()));
		} else if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.eq("view." + VAghUnidFuncional.Fields.UNF_DESCRICAO.toString(), parametro.toString()));
		}		
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarSubQueryCaracteristicasUnidadeFuncional(ConstanteAghCaractUnidFuncionais[] caracteristicas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "aghUnidadeFuncional");
		criteria.add(Restrictions.in("aghUnidadeFuncional." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristicas));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("aghUnidadeFuncional." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		criteria.setProjection(p);
		
		return criteria;
	}
	
	public List<LocalPacienteVO> pesquisarUnidFuncionalPorCaracteristica(String param) {
		DetachedCriteria criteria = obterCriteriaUnidFuncionalPorCaracteristica(param);
		
		criteria.addOrder(Order.asc("VUF." + VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarUnidFuncionalPorCaracteristicaCount(String param) {
		DetachedCriteria criteria = obterCriteriaUnidFuncionalPorCaracteristica(param);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaUnidFuncionalPorCaracteristica(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class, "VUF");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VUF." + VAghUnidFuncional.Fields.SEQ.toString())
						, LocalPacienteVO.Fields.SEQ.toString())
				.add(Projections.property("VUF." + VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString())
						, LocalPacienteVO.Fields.ANDAR_ALA_DESCRICAO.toString()));
		
		criteria.add(Restrictions.eq("VUF." + VAghUnidFuncional.Fields.IND_SIT_UNID_FUNC.toString(), "A"));
		
		ConstanteAghCaractUnidFuncionais[] caracteristicas = {ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA};
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUF");
		subCriteria.add(Restrictions.in("CUF." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristicas));
		subCriteria.setProjection(Projections.property("CUF." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		
		criteria.add(Subqueries.propertyIn("VUF." + VAghUnidFuncional.Fields.SEQ.toString(), subCriteria));
		
		if (StringUtils.isNumeric(param)) {
			Criterion crit1 = Restrictions.eq("VUF." + VAghUnidFuncional.Fields.SEQ.toString(), Short.valueOf(param));
			
			if (Integer.valueOf(param) <= 127) {
				Criterion crit2 = Restrictions.eq("VUF." + VAghUnidFuncional.Fields.ANDAR.toString(), Byte.valueOf(param));
				criteria.add(Restrictions.or(crit1, crit2));
			} else {
				criteria.add(crit1);
			}
			
		} else if (StringUtils.isNotEmpty(param)) {
			Criterion crit1 = Restrictions.ilike("VUF." + VAghUnidFuncional.Fields.ALA.toString(), param, MatchMode.ANYWHERE);
			Criterion crit2 = Restrictions.ilike("VUF." + VAghUnidFuncional.Fields.UNF_DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(crit1, crit2));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(LocalPacienteVO.class));
		
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<VAghUnidFuncionalVO> listarUnidadesFuncionaisMae(String seqOuAndarAlaDescricao){
		
		Query query = this.queryListarUnidadesFuncionaisMae(seqOuAndarAlaDescricao);
		
		List<Object[]> valores = query.getResultList();
		List<VAghUnidFuncionalVO> unidadesMae = new ArrayList<VAghUnidFuncionalVO>();
		
		if(valores != null && valores.size() > 0){

			for (Object[] objects : valores) {
				
				VAghUnidFuncionalVO unidVO = new VAghUnidFuncionalVO();
				
				if(objects[0] != null){
					unidVO.setSeq(Short.parseShort(objects[0].toString()));
				}
				
				if(objects[1] != null){
					unidVO.setAndarAlaDescricao(objects[1].toString());
				}
				
				unidadesMae.add(unidVO);
			}
		}
		
		return unidadesMae;
	}
	
private Query queryListarUnidadesFuncionaisMae(String seqOuAndarAlaDescricao){
		
		StringBuilder hql = new StringBuilder(400).append("SELECT DISTINCT ");
		hql.append(" UNM." ).append( VAghUnidFuncional.Fields.SEQ.toString())
		.append(", UNM." ).append( VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString())
		.append(" FROM " ).append( VAghUnidFuncional.class.getSimpleName() ).append( " UNM, ")
		.append(VAghUnidFuncional.class.getSimpleName() ).append( " UNF")
		.append(" WHERE")
		.append(" (UNM." ).append( VAghUnidFuncional.Fields.IND_UNID_INTERNACAO.toString() ).append( " = 'S'")
		.append(" OR UNM." ).append( VAghUnidFuncional.Fields.IND_UNID_EMERGENCIA.toString() ).append( " = 'S')")
		.append(" AND UNM." ).append( VAghUnidFuncional.Fields.SEQ.toString() ).append( " = " ).append( "UNF." ).append( VAghUnidFuncional.Fields.VUF_SEQ.toString())
		.append(" AND UNM." ).append( VAghUnidFuncional.Fields.IND_SIT_UNID_FUNC.toString() ).append( " = 'A'")
		.append(" AND UNF." ).append( VAghUnidFuncional.Fields.IND_SIT_UNID_FUNC.toString() ).append( " = 'A'");
		
		if(!seqOuAndarAlaDescricao.isEmpty()){
			if(CoreUtil.isNumeroShort(seqOuAndarAlaDescricao)){
				hql.append(" AND UNM." + VAghUnidFuncional.Fields.SEQ.toString() + " = :unidSeq");
			} else {
				hql.append(" AND UNM." + VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString() + " LIKE :unidDescricao");
			}
		}
		
		hql.append(" ORDER BY UNM." ).append( VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString());
		
		Query query = createQuery(hql.toString());
		
		if(!seqOuAndarAlaDescricao.isEmpty()){
			if(CoreUtil.isNumeroShort(seqOuAndarAlaDescricao)){
				query.setParameter("unidSeq", Short.valueOf(seqOuAndarAlaDescricao));
			} else {
				query.setParameter("unidDescricao", "%" + seqOuAndarAlaDescricao.toUpperCase() + "%");
			}
		}

		return query;
	}

	/**
 	 * #5795 - C3
	 * @return
	 */
	public List<UnidadeFuncionalVO> obterUnidadeFuncionalOrigemInformacaoPrescribente(String descricao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class, "VUF");
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUF");
		
		subCriteria.setProjection(Projections.property("CUF."+AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.in("CUF."+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), new ConstanteAghCaractUnidFuncionais[] {ConstanteAghCaractUnidFuncionais.PERMITE_INF_PRESCRIBENTE}));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.SEQ.toString()),
						UnidadeFuncionalVO.Fields.SEQ.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.ANDAR.toString()),
						UnidadeFuncionalVO.Fields.ANDAR.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.ALA.toString()),
						UnidadeFuncionalVO.Fields.ALA.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.UNF_DESCRICAO.toString()),
						UnidadeFuncionalVO.Fields.UNF_DESCRICAO.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()),
								UnidadeFuncionalVO.Fields.ANDAR_ALA_DESCRICAO.toString())
				);
		
		criteria.add(Subqueries.propertyIn("VUF."+VAghUnidFuncional.Fields.SEQ.toString(), subCriteria));
		
		if(CoreUtil.isNumeroByte(descricao)){
			criteria.add(Restrictions.eq("VUF."+VAghUnidFuncional.Fields.ANDAR.toString(), Byte.valueOf(descricao)));
		}else if(StringUtils.isNotEmpty(descricao)){
			Criterion criterion1 = Restrictions.like("VUF."+VAghUnidFuncional.Fields.ALA.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE);
			Criterion criterion2 = Restrictions.like("VUF."+VAghUnidFuncional.Fields.UNF_DESCRICAO.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(criterion1, criterion2));
		}
		
		criteria.addOrder(Order.asc("VUF."+VAghUnidFuncional.Fields.ANDAR.toString()));
		criteria.addOrder(Order.asc("VUF."+VAghUnidFuncional.Fields.ALA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(UnidadeFuncionalVO.class));
		
		return executeCriteria(criteria);
	}
	
	public UnidadeFuncionalVO obterPorUnfSeq(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class, "VUF");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.SEQ.toString()),
						UnidadeFuncionalVO.Fields.SEQ.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.ANDAR.toString()),
						UnidadeFuncionalVO.Fields.ANDAR.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.ALA.toString()),
						UnidadeFuncionalVO.Fields.ALA.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.UNF_DESCRICAO.toString()),
						UnidadeFuncionalVO.Fields.UNF_DESCRICAO.toString())
				.add(Projections.property("VUF."+VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()),
						UnidadeFuncionalVO.Fields.ANDAR_ALA_DESCRICAO.toString())
				);
		criteria.add(Restrictions.eq("VUF."+VAghUnidFuncional.Fields.SEQ.toString(), unfSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(UnidadeFuncionalVO.class));
		return (UnidadeFuncionalVO) executeCriteriaUniqueResult(criteria);
	}
	
	/** Obtém criteria para consulta de VUnidadeFuncional para Suggestion Box 04.
	 * #1291
	 * @param parametro {@link Object}
	 * @return {@link List<AfaMedicamento>} */
	public List<VAghUnidFuncional> pesquisarVUnidFuncionalSB4(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class);
		
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if(CoreUtil.isNumeroShort(strPesquisa)){
				criteria.add(Restrictions.eq(VAghUnidFuncional.Fields.SEQ.toString(),  Short.valueOf(strPesquisa.toString())));
				if(executeCriteria(criteria) != null && executeCriteria(criteria).size() == 1){
					ProjectionList p = Projections.projectionList();
					p.add(Projections.property(VAghUnidFuncional.Fields.SEQII.toString()).as(VAghUnidFuncional.Fields.SEQII.toString()));
					p.add(Projections.property(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()).as(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()));
					criteria.setProjection(p);
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(VAghUnidFuncional.class);
					criteria.add((Restrictions.ilike(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString(), (String) strPesquisa,MatchMode.ANYWHERE)));
					ProjectionList p = Projections.projectionList();
					p.add(Projections.property(VAghUnidFuncional.Fields.SEQII.toString()).as(VAghUnidFuncional.Fields.SEQII.toString()));
					p.add(Projections.property(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()).as(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()));
					criteria.setProjection(p);
				}
			}else{
				criteria = null;
				criteria = DetachedCriteria.forClass(VAghUnidFuncional.class);
				criteria.add((Restrictions.ilike(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString(), (String) strPesquisa,MatchMode.ANYWHERE)));
				ProjectionList p = Projections.projectionList();
				p.add(Projections.property(VAghUnidFuncional.Fields.SEQII.toString()).as(VAghUnidFuncional.Fields.SEQII.toString()));
				p.add(Projections.property(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()).as(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()));
				criteria.setProjection(p);
			}
				
		}
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VAghUnidFuncional.Fields.SEQII.toString()).as(VAghUnidFuncional.Fields.SEQII.toString()));
		p.add(Projections.property(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()).as(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString()));
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(VAghUnidFuncional.class));
		return this.executeCriteria(criteria, 0, 100, VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAOII.toString(), true);
	}
	
	public Long pesquisarVUnidFuncionalSB4Count(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class);

		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if(CoreUtil.isNumeroShort(strPesquisa)){
				criteria.add(Restrictions.eq(VAghUnidFuncional.Fields.SEQ.toString(),  Short.valueOf(strPesquisa.toString())));
				if(executeCriteria(criteria) != null && executeCriteria(criteria).size() == 1){
					return executeCriteriaCount(criteria);
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(VAghUnidFuncional.class);
					criteria.add((Restrictions.ilike(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString(), (String) strPesquisa,MatchMode.ANYWHERE)));
				}
			}else{
				criteria = null;
				criteria = DetachedCriteria.forClass(VAghUnidFuncional.class);
				criteria.add((Restrictions.ilike(VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString(), (String) strPesquisa,MatchMode.ANYWHERE)));
			}
				
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public String obterUnidFuncionalDescAndar(Short  unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class, "view");
		
		criteria.add(Restrictions.eq("view." + VAghUnidFuncional.Fields.SEQ.toString(), unfSeq));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("view." + VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()), VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString());
		criteria.setProjection(p);
			
		return (String) executeCriteriaUniqueResult(criteria);
	}

}
