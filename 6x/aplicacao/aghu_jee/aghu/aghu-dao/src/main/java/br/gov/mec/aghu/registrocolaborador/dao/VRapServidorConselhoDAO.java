package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.VRapServidorConselhoVO;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class VRapServidorConselhoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VRapServidorConselho>{
	
    private static final long serialVersionUID = -8143872015839489635L;

    private static final Log LOG = LogFactory.getLog(VRapServidorConselhoDAO.class);

    /**
     * ORADB: cursor: AELP_GET_PATOL_LAUDO.c_lui
     * @param seq
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<VRapServidorConselhoVO> obterVRapServidorConselhoVO(final Long seq){
    	final StringBuffer hql =  new StringBuffer(660);
		
    	hql.append(" select " )
    	   .append("   new br.gov.mec.aghu.exames.vo.VRapServidorConselhoVO( ")
												    	   .append("           lup.aelExameAps.seq ")
												    	   .append("         , lui.seq ")
												    	   .append("         , lui.nomeParaLaudo ") 
												    	   .append("         , lui.funcao ") 
												    	   .append("         , vcs.nroRegConselho ") 
												    	   .append("         , lui.servidor.id.matricula ") 
												    	   .append("         , lui.servidor.id.vinCodigo ") 
												    	   .append("        ) ") 
    	   
    	   .append(" from ")
    	   
		   .append("           VRapServidorConselho vcs, ")
		   .append("           AelPatologistaAps lup, ")
		   .append("           AelPatologista lui ")
		   
		   .append(" where 1=1 ")
		   .append("     and lup.aelExameAps.seq = :prmSeq ")
		   .append("     and lup.servidor =  lui.servidor ")
		   .append("     and lui.servidor.id.matricula =  vcs.id.matricula ")
		   .append("     and lui.servidor.id.vinCodigo =  vcs.id.vinCodigo ")
		   
		   .append(" order by lup.ordemMedicoLaudo asc ");
    	
		final Query query = createHibernateQuery(hql.toString());
		
		query.setLong("prmSeq", seq);
		
		return query.list();
    }
    
    
    
   /**
    * Obtem conselho servidor através dos atributos da ID
    * @param matricula matricula do servidor
    * @param vinCodigo código do servidor
    * @return
    */
    public VRapServidorConselho obterVRapServidorConselhoPeloId(int matricula, short vinCodigo, String sigla) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
	    if (sigla != null) {
	    	criteria.add(Restrictions.eq(VRapServidorConselho.Fields.SIGLA.toString(), sigla));
	    }
	    
	    return (VRapServidorConselho)this.executeCriteriaUniqueResult(criteria);
	}
    
	/**
	 * Obtem conselho servidor através dos atributos da ID
	 * @param matricula matricula do servidor
	 * @param vinCodigo código do servidor
	 * @return
	 */
	public String obterRegistroVRapServidorConselhoPeloId(int matricula, short vinCodigo, String sigla) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(VRapServidorConselho.Fields.SIGLA.toString(), sigla));
		criteria.add(Restrictions.isNotNull(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
		criteria.setProjection(Projections.property(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
		criteria.setProjection(Projections.max(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
	    return (String)this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem Map com o conselho servidor através dos atributos da ID atraves de listas.
	 * 
	 * map key: matricula-vinculo-sigla
	 * 
	 * @return
	 */
	public Map<String, String>  obterMapRegistroVRapServidorConselho(List<Integer> listMatricula, List<Short> listVinCodigo, List<String> listSigla) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
	    
	    criteria.add(Restrictions.in(VRapServidorConselho.Fields.MATRICULA.toString(), listMatricula));
	    criteria.add(Restrictions.in(VRapServidorConselho.Fields.VIN_CODIGO.toString(), listVinCodigo));
		criteria.add(Restrictions.in(VRapServidorConselho.Fields.SIGLA.toString(), listSigla));
		criteria.add(Restrictions.isNotNull(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
		
		criteria.setProjection(Projections.projectionList()
                  .add(Projections.groupProperty(VRapServidorConselho.Fields.MATRICULA.toString()))
                  .add(Projections.groupProperty(VRapServidorConselho.Fields.VIN_CODIGO.toString()))
                  .add(Projections.groupProperty(VRapServidorConselho.Fields.SIGLA.toString()))
                  .add(Projections.max(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()))
        );
		List<Object[]> lista = this.executeCriteria(criteria);
		
		Map<String, String> mapNroConselhoPorServidores = new HashMap<String, String>();
		
		for (Object[] objects : lista) {
			Integer matricula = (Integer)objects[0];
			Short vinculo = (Short)objects[1];
			String sigla = (String)objects[2];
			String nroRegConselho = (String)objects[3];
			
			String nroConselhoTmp = null;
			String key = matricula + "-" + vinculo + "-" + sigla;
			if (mapNroConselhoPorServidores.containsKey(key)) {
				nroConselhoTmp = mapNroConselhoPorServidores.get(key);
				nroConselhoTmp = nroConselhoTmp.concat("/").concat(nroRegConselho);
			} else {
				nroConselhoTmp = nroRegConselho;
			}
			mapNroConselhoPorServidores.put(key, nroConselhoTmp);
		}
	    
		return mapNroConselhoPorServidores;
	}
    
    

	/**
    * Obtem conselho servidor exames através dos atributos da ID
    * @param matricula matricula do servidor
    * @param vinCodigo código do servidor
    * @return
    */
	public Long obterVRapServidorConselhoExamePeloId(int matricula, short vinCodigo, String emaExaSigla, Integer emaExaManSeq) {
		StringBuffer hql = new StringBuffer(230);

		hql.append(" SELECT 	COUNT(*) AS counter ");
		hql.append(" FROM 		AelExameConselhoProfs ecp, ");
		hql.append(" 			VRapServidorConselho vcs ");
		hql.append(" WHERE 		vcs.cprCodigo = ecp.id.cprCodigo ");

	    hql.append(" AND vcs." + VRapServidorConselho.Fields.MATRICULA.toString() + " = :matricula");
	    hql.append(" AND vcs." + VRapServidorConselho.Fields.VIN_CODIGO.toString() + " = :vinCodigo");
	    hql.append(" AND ecp." + AelExameConselhoProfs.Fields.EMA_EXA_SIGLA.toString() + " = :emaExaSigla");
	    hql.append(" AND ecp." + AelExameConselhoProfs.Fields.EMA_MAN_SEQ.toString() + " = :emaExaManSeq");

	    Query query = createHibernateQuery(hql.toString());
	    query.setParameter("matricula", matricula);
	    query.setParameter("vinCodigo", vinCodigo);
	    query.setParameter("emaExaSigla", emaExaSigla);
	    query.setParameter("emaExaManSeq", emaExaManSeq);

	    Long counter = (Long)query.uniqueResult();

	    return counter;
	}
	
	public List<VRapServidorConselho> pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmer(Object strPesquisa, final Short unfSeq) {
		DetachedCriteria criteria = obterCriteriaProfssionaisAgendaProcedimentosEletUrgOuEmer(strPesquisa, unfSeq, true);
		return executeCriteria(criteria, 0, 200, VRapServidorConselho.Fields.NOME.toString(), true);
	}

	public Long pesquisarProfissionaisAgendaProcedimentosEletUrgOuEmerCount(Object strPesquisa, final Short unfSeq) {
		return executeCriteriaCount(obterCriteriaProfssionaisAgendaProcedimentosEletUrgOuEmer(strPesquisa, unfSeq, true));
	}
	
	private DetachedCriteria montarCriteriaPesquisaServidorConselho(List<String> listaSigla) {
		String aliasVcs = "vcs";
		String separador = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class, aliasVcs);
		criteria.setProjection(
				Projections.projectionList()
					.add(Projections.property(aliasVcs + separador + VRapServidorConselho.Fields.SIGLA.toString()), ProfDescricaoCirurgicaVO.Fields.SIGLA.toString())
					.add(Projections.property(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()), ProfDescricaoCirurgicaVO.Fields.NRO_REG_CONSELHO.toString())
					.add(Projections.property(aliasVcs + separador + VRapServidorConselho.Fields.NOME.toString()), ProfDescricaoCirurgicaVO.Fields.NOME.toString())
					.add(Projections.property(aliasVcs + separador + VRapServidorConselho.Fields.CONSELHO_SIGLA.toString()), ProfDescricaoCirurgicaVO.Fields.CONSELHO_SIGLA.toString())
					.add(Projections.property(aliasVcs + separador + VRapServidorConselho.Fields.MATRICULA.toString()), ProfDescricaoCirurgicaVO.Fields.SER_MATRICULA.toString())
					.add(Projections.property(aliasVcs + separador + VRapServidorConselho.Fields.VIN_CODIGO.toString()), ProfDescricaoCirurgicaVO.Fields.SER_VIN_CODIGO.toString()));
				
		criteria.add(Restrictions.in(aliasVcs + separador + VRapServidorConselho.Fields.SIGLA.toString(), listaSigla));
		return criteria;
	}
	
	public List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoNroRegNaoNuloListaSigla(List<String> listaSigla, Object objPesquisa) {
		String aliasVcs = "vcs";
		String separador = ".";
		
		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		DetachedCriteria criteria = montarCriteriaPesquisaServidorConselho(listaSigla);
		
		criteria.add(Restrictions.isNotNull(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(
					Restrictions.or(
						Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), strPesquisa, MatchMode.ANYWHERE), 
						Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));	
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProfDescricaoCirurgicaVO.class));

		return executeCriteria(criteria, 0, 100, VRapServidorConselho.Fields.NOME.toString(), true);
	}

	public Long pesquisarServidorConselhoNroRegNaoNuloListaSiglaCount(List<String> listaSigla, Object objPesquisa) {
		String aliasVcs = "vcs";
		String separador = ".";
		
		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		DetachedCriteria criteria = montarCriteriaPesquisaServidorConselho(listaSigla);
		
		criteria.add(Restrictions.isNotNull(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(
					Restrictions.or(
						Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), strPesquisa, MatchMode.ANYWHERE), 
						Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));	
		}

		return executeCriteriaCount(criteria);
	}

	public List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoNroRegNuloListaSigla(List<String> listaSigla, Object objPesquisa) {
		String aliasVcs = "vcs";
		String separador = ".";
		
		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		DetachedCriteria criteria = montarCriteriaPesquisaServidorConselho(listaSigla);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(
					Restrictions.or(
							Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), strPesquisa, MatchMode.ANYWHERE),
							Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		criteria.setResultTransformer(Transformers
				.aliasToBean(ProfDescricaoCirurgicaVO.class));

		return executeCriteria(criteria, 0, 100, VRapServidorConselho.Fields.NOME.toString(), true);
	}

	public Long pesquisarServidorConselhoNroRegNuloListaSiglaCount(List<String> listaSigla, Object objPesquisa) {
		String aliasVcs = "vcs";
		String separador = ".";
		
		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		DetachedCriteria criteria = montarCriteriaPesquisaServidorConselho(listaSigla);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(
					Restrictions.or(
							Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), strPesquisa, MatchMode.ANYWHERE),
							Restrictions.ilike(aliasVcs + separador + VRapServidorConselho.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		return executeCriteriaCount(criteria);
	}

	public List<ProfDescricaoCirurgicaVO> pesquisarServidorConselhoListaSiglaPorServidor(List<String> listaSigla, Integer serMatricula, Short serVinCodigo) {
		String aliasVcs = "vcs";
		String separador = ".";
		
		DetachedCriteria criteria = montarCriteriaPesquisaServidorConselho(listaSigla);
				
		criteria.add(Restrictions.eq(aliasVcs + separador + VRapServidorConselho.Fields.MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(aliasVcs + separador + VRapServidorConselho.Fields.VIN_CODIGO.toString(), serVinCodigo));
		
		criteria.setResultTransformer(Transformers
				.aliasToBean(ProfDescricaoCirurgicaVO.class));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaProfssionaisAgendaProcedimentosEletUrgOuEmer(Object filtro, final Short unfSeq, final boolean limitarFuncoes) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class, "vrap");
		String parametroString = (String) filtro;

		if (CoreUtil.isNumeroInteger(parametroString)) {

			Integer parametroNumerico = Integer.valueOf(parametroString);

			Criterion cMatricula = Restrictions.eq("vrap." + VRapServidorConselho.Fields.MATRICULA.toString(), parametroNumerico);
			Criterion cVinCodigo = Restrictions.eq("vrap." + VRapServidorConselho.Fields.VIN_CODIGO.toString(), parametroNumerico.shortValue());

			Criterion cMatriculaVincodigo = Restrictions.or(cMatricula, cVinCodigo);

			Criterion cNroConselho = Restrictions.eq("vrap." + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), parametroNumerico.toString());

			criteria.add(Restrictions.or(cMatriculaVincodigo, cNroConselho));

		} else if (StringUtils.isNotEmpty(parametroString)) {

			Criterion cNome = Restrictions.ilike("vrap." + VRapServidorConselho.Fields.NOME.toString(), parametroString, MatchMode.ANYWHERE);
			Criterion cSigla = Restrictions.ilike("vrap." + VRapServidorConselho.Fields.SIGLA.toString(), parametroString, MatchMode.ANYWHERE);

			criteria.add(Restrictions.or(cNome, cSigla));
		}

		DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");

		if(limitarFuncoes){
			Criterion criIn = Restrictions.in("puc." + MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), new Object[] { DominioFuncaoProfissional.ENF, DominioFuncaoProfissional.CIR,
				DominioFuncaoProfissional.INS, DominioFuncaoProfissional.ESE });
			subCriteria.add(Restrictions.not(criIn));			
		}

		subCriteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		subCriteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));

		subCriteria.add(Property.forName("puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()).eqProperty("vrap." + VRapServidorConselho.Fields.MATRICULA.toString()));
		subCriteria.add(Property.forName("puc." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()).eqProperty("vrap." + VRapServidorConselho.Fields.VIN_CODIGO.toString()));

		subCriteria.setProjection(Projections.property("puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()));

		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return criteria;
	}

	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaProcedimento(String strPesquisa, final Short unfSeq) {
		
		String parametroString = (String) strPesquisa;
		javax.persistence.Query query = this.queryHqlProfissionais(strPesquisa , parametroString, unfSeq);

		query.setMaxResults(100);
		List<Object[]> lista = query.getResultList();
		
		List<AgendaProcedimentoPesquisaProfissionalVO> returnList= new ArrayList<AgendaProcedimentoPesquisaProfissionalVO>(0);
		if (lista != null && !lista.isEmpty()) {
			for (Object[] listFileds : lista) {
				AgendaProcedimentoPesquisaProfissionalVO returnValue = new AgendaProcedimentoPesquisaProfissionalVO();
				returnValue.setVinCodigo((Short) listFileds[0]);
				returnValue.setMatricula((Integer) listFileds[1] );
				//returnValue.setNroRegConselho((String) listFileds[2]);
				//returnValue.setCprSigla((String) listFileds[3]);
				returnValue.setNome((String) listFileds[2]);
				returnValue.setFuncao((DominioFuncaoProfissional) listFileds[3]);
				
				Object[] uniqueVRap = obterConselhoESiglaVRapServidorConselho((Integer) listFileds[1], (Short) listFileds[0]);
				if(uniqueVRap != null){
					returnValue.setNroRegConselho((String) uniqueVRap[0]);
					returnValue.setCprSigla((String) uniqueVRap[1]);
				}
				
				returnList.add(returnValue);
			}
		}
		
		return returnList;
		
	}
	
	public Long pesquisarProfissionaisAgendaProcedimentoENotaConsumoCount(String strPesquisa, final Short unfSeq, List<String> listaConselhos, boolean agenda) {
		List<Object[]> lista = pesquisarProfissionaisAgendaProcedimentoENotaConsumoArray(strPesquisa, unfSeq, listaConselhos, agenda);
		
		Integer tamanho = lista.size();
		
		return tamanho.longValue();
	}
	
	public Long pesquisarProfissionaisAgendaProcedimentoENotaConsumoCount(String strPesquisa, final Short unfSeq, List<String> listaConselhos) {
		String parametroString = (String) strPesquisa;
		StringBuilder hql = this.queryHqlProfissionaisCount(strPesquisa , parametroString, listaConselhos);
//		Conforme solicitado pela Lisiane, trazer todos profissionais na SB - #41033
		
		LOG.info(hql.toString());
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		
		if (listaConselhos != null && listaConselhos.size() > 0) {
			query.setParameter("listaConselho", listaConselhos);
		}
		if (CoreUtil.isNumeroInteger(parametroString)) {
			query.setParameter("parametroInteger", Integer.valueOf(parametroString));
			query.setParameter("parametroString", parametroString);
		}	
		
		return Long.valueOf(query.getSingleResult().toString());
	}

	private  StringBuilder queryHqlProfissionaisCount(Object strPesquisa, String parametroString, List<String> listConselhosMedicos) {
		StringBuilder hql = new StringBuilder(150);
		hql.append("select count(vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString());
		hql.append(") ");
		hql.append("from ").append(MbcProfAtuaUnidCirgs.class.getSimpleName()).append(" puc , ").append(VRapServidorConselho.class.getSimpleName()).append(" vcs ");
		hql.append(" where vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" = ");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString());
		hql.append(" and vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString()).append(" = ");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString());
		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()).append(" =:unfSeq ");
		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()).append(" = ").append("'"+DominioSituacao.A+"'");
		
		if(listConselhosMedicos != null && listConselhosMedicos.size() > 0){
			hql.append(" and vcs.").append(VRapServidorConselho.Fields.SIGLA.toString()).append(" IN ").append(" ( :listaConselho ) ");
		}
		
		if (CoreUtil.isNumeroInteger(parametroString)) {
			hql.append(" and (vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" =:parametroInteger" );
			hql.append(" or vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString()).append(" =:parametroInteger " );
			hql.append(" or vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()).append(" =:parametroString )" );
			
		} else if (StringUtils.isNotEmpty(parametroString)) {
			hql.append(" and (lower(vcs.").append(VRapServidorConselho.Fields.NOME.toString()).append(") like lower('%").append(parametroString).append("%') ");
			hql.append(" or lower(vcs.").append(VRapServidorConselho.Fields.SIGLA.toString()).append(") like lower('%").append(parametroString).append("%')) ");
			
		}
		
		return hql;
	}
	
    public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisAgendaProcedimentoENotaConsumo(String strPesquisa, final Short unfSeq, List<String> listaConselhos, boolean agenda) {
		List<Object[]> lista = pesquisarProfissionaisAgendaProcedimentoENotaConsumoArray(strPesquisa, unfSeq, listaConselhos, agenda);
		
		List<AgendaProcedimentoPesquisaProfissionalVO> returnList= new ArrayList<AgendaProcedimentoPesquisaProfissionalVO>(0);
		if (lista != null && !lista.isEmpty()) {
			for (Object[] listFileds : lista) {
				AgendaProcedimentoPesquisaProfissionalVO returnValue = new AgendaProcedimentoPesquisaProfissionalVO();
				returnValue.setVinCodigo((Short) listFileds[0]);
				returnValue.setMatricula((Integer) listFileds[1] );
				//returnValue.setNroRegConselho((String) listFileds[2]);
				returnValue.setCprSigla((String) listFileds[2]);
				returnValue.setNome((String) listFileds[3]);
				returnValue.setFuncao((DominioFuncaoProfissional) listFileds[4]);

				returnList.add(returnValue);
			}
		}
		return returnList;
	}



	private List<Object[]> pesquisarProfissionaisAgendaProcedimentoENotaConsumoArray(String strPesquisa, final Short unfSeq, List<String> listaConselhos, boolean agenda) {
		String parametroString = strPesquisa;
		StringBuilder hql = this.queryHqlProfissionais(strPesquisa , parametroString, listaConselhos, agenda);
		// Conforme solicitado pela Lisiane, trazer todos profissionais na SB - #41033
		hql.append(" order by vcs.").append(VRapServidorConselho.Fields.NOME.toString());

		LOG.info(hql.toString());
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		
		if (listaConselhos != null && listaConselhos.size() > 0) {
			query.setParameter("listaConselho", listaConselhos);
		}
		if (CoreUtil.isNumeroInteger(parametroString)) {
				query.setParameter("parametroInteger", Integer.valueOf(parametroString));
				query.setParameter("parametroString", parametroString);
		}	
		if(agenda) {
			query.setParameter("listaFuncoes", Arrays.asList(DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANC,  DominioFuncaoProfissional.ANR, 
					DominioFuncaoProfissional.CIR,  DominioFuncaoProfissional.ENF, DominioFuncaoProfissional.INS, DominioFuncaoProfissional.MAX, 
					DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF,DominioFuncaoProfissional.MRE));
		}

		List<Object[]> lista = query.getResultList();
		return lista;
	}
      
    private  StringBuilder queryHqlProfissionais(Object strPesquisa, String parametroString, List<String> listConselhosMedicos, boolean agenda){
  		
  		StringBuilder hql = new StringBuilder(200);
  		hql.append("select distinct vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString());
  		hql.append(", vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString());
  		//hql.append(", vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString());
  		hql.append(", vcs.").append(VRapServidorConselho.Fields.SIGLA.toString());
  		hql.append(", vcs.").append(VRapServidorConselho.Fields.NOME.toString());
  		hql.append(", puc.").append(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString());
  		hql.append(" from ").append(MbcProfAtuaUnidCirgs.class.getSimpleName()).append(" puc , ").append(VRapServidorConselho.class.getSimpleName()).append(" vcs ");
  		hql.append(" where vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" = ");
  		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString());
  		hql.append(" and vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString()).append(" = ");
  		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString());
  		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()).append(" =:unfSeq ");
  		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()).append(" = ").append("'"+DominioSituacao.A+"'");
  		
  		if(listConselhosMedicos != null && listConselhosMedicos.size() > 0){
  			hql.append(" and vcs.").append(VRapServidorConselho.Fields.SIGLA.toString()).append(" IN ").append(" ( :listaConselho ) ");
  		}
  		
  		if (CoreUtil.isNumeroInteger(parametroString)) {
  			
  			hql.append(" and (vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" =:parametroInteger" );
  			hql.append(" or vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString()).append(" =:parametroInteger " );
  			hql.append(" or vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()).append(" =:parametroString )" );
  		

  		} else if (StringUtils.isNotEmpty(parametroString)) {

  			hql.append(" and (lower(vcs.").append(VRapServidorConselho.Fields.NOME.toString()).append(") like lower('%").append(parametroString).append("%') ");
  			hql.append(" or lower(vcs.").append(VRapServidorConselho.Fields.SIGLA.toString()).append(") like lower('%").append(parametroString).append("%')) ");
  			
  		}
  		
  		if(agenda) {
  			hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()).append(" IN ").append(" ( :listaFuncoes ) ");
  		}
  		
  		return hql;
  		
  	}  
	
	private  javax.persistence.Query queryHqlProfissionais(String strPesquisa, String parametroString, Short unfSeq){
		
		StringBuilder hql = new StringBuilder(200);
		hql.append("select distinct vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString());
		hql.append(", vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString());
		//hql.append(", vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString());
		//hql.append(", vcs.").append(VRapServidorConselho.Fields.SIGLA.toString());
		hql.append(", vcs.").append(VRapServidorConselho.Fields.NOME.toString());
		hql.append(", puc.").append(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString());
		hql.append(" from ").append(MbcProfAtuaUnidCirgs.class.getSimpleName()).append(" puc , ").append(VRapServidorConselho.class.getSimpleName()).append(" vcs ");
		hql.append(" where vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" = ");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString());
		hql.append(" and vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString()).append(" = ");
		hql.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString());
		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()).append(" =:unfSeq ");
		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()).append(" = ").append("'"+DominioSituacao.A+"'");
		
		if (CoreUtil.isNumeroInteger(parametroString)) {
			
			hql.append(" and (vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString()).append(" =:parametroInteger" );
			hql.append(" or vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString()).append(" =:parametroInteger " );
			hql.append(" or vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()).append(" =:parametroString )" );
		
	
		} else if (StringUtils.isNotEmpty(parametroString)) {
	
			hql.append(" and (lower(vcs.").append(VRapServidorConselho.Fields.NOME.toString()).append(") like lower('%").append(parametroString).append("%') ");
			hql.append(" or lower(vcs.").append(VRapServidorConselho.Fields.SIGLA.toString()).append(") like lower('%").append(parametroString).append("%')) ");
			
		}
		
//		Conforme solicitado pela Lisiane, trazer todos profissionais na SB - #41033
		hql.append(" order by vcs.").append(VRapServidorConselho.Fields.NOME.toString());
		
		LOG.info(hql.toString());
		javax.persistence.Query query = createQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		if (CoreUtil.isNumeroInteger(parametroString)) {
				query.setParameter("parametroInteger", Integer.valueOf(parametroString));
				query.setParameter("parametroString", parametroString);
		}	
		
		
		return query;
		
	}

	public Long pesquisarProfissionaisAgendaProcedimentoCount(String strPesquisa, final Short unfSeq) {
		String parametroString = (String) strPesquisa;
		javax.persistence.Query query = this.queryHqlProfissionais(strPesquisa , parametroString, unfSeq);

		List<Object[]> lista = query.getResultList();
		
		return Long.valueOf(lista.size());
	}

	//Método criado devido ao retorno da view V_RAP_SERVIDOR_CONSELHO, que retorna vários registros com mesmo "id".
	//Entre os resultados, alguns retornam o nroRegistro e outros não
	public Object[] obterConselhoESiglaVRapServidorConselho(int matricula, short vinCodigo) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class, "vcs");
	    
	    criteria.setProjection(
				Projections.distinct(
						Projections.projectionList()
						.add(Projections.property("vcs."+VRapServidorConselho.Fields.SIGLA.toString()), ProfDescricaoCirurgicaVO.Fields.SIGLA.toString())
						.add(Projections.property("vcs."+VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()), ProfDescricaoCirurgicaVO.Fields.NRO_REG_CONSELHO.toString())
					));
	    
	    
	    criteria.add(Restrictions.eq("vcs."+VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq("vcs."+VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
		
	    criteria.add(Restrictions.or(
	    		Restrictions.isNotNull("vcs."+VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()),
	    		Restrictions.isNotNull("vcs."+VRapServidorConselho.Fields.SIGLA.toString())
	    				));
	    List<Object[]> retorno = executeCriteria(criteria);
	    if(!retorno.isEmpty()){
	    	return retorno.get(0);
	    }else{
	    	return null;
	    }
	}
	
	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionaisRegistroCirurgiaRealizada(Object strPesquisa, final Short unfSeq) {
		DetachedCriteria criteria = this.obterCriteriaProfssionaisAgendaProcedimentosEletUrgOuEmer(strPesquisa, unfSeq, false);
		ProjectionList projecoesCriteria = Projections.projectionList();
		projecoesCriteria.add(Projections.property("vrap." + VRapServidorConselho.Fields.MATRICULA.toString()), "matricula");
		projecoesCriteria.add(Projections.property("vrap." + VRapServidorConselho.Fields.VIN_CODIGO.toString()), "vinCodigo");
		projecoesCriteria.add(Projections.property("vrap." + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()), "nroRegConselho");
		projecoesCriteria.add(Projections.property("vrap." + VRapServidorConselho.Fields.CPR_SIGLA.toString()), "cprSigla");
		projecoesCriteria.add(Projections.property("vrap." + VRapServidorConselho.Fields.NOME.toString()), "nome");
		criteria.setProjection(Projections.distinct(projecoesCriteria));
		criteria.setResultTransformer(Transformers.aliasToBean(AgendaProcedimentoPesquisaProfissionalVO.class));
		return executeCriteria(criteria, 0, 100, VRapServidorConselho.Fields.NOME.toString(), true);
	}

	public Long pesquisarProfissionaisRegistroCirurgiaRealizadaCount(Object strPesquisa, final Short unfSeq) {
		return this.executeCriteriaCount(this.obterCriteriaProfssionaisAgendaProcedimentosEletUrgOuEmer(strPesquisa, unfSeq, false));
	}
	
	/**
	 * #36698 - Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	public List<VRapServidorConselho> pesquisarServidoresConselho(final String strPesquisa,final List<Integer> centroCusto){
		DetachedCriteria criteria = montarCriteriaServidoresConselho(strPesquisa, centroCusto);
		return executeCriteria(criteria, 0, 100, VRapServidorConselho.Fields.NOME.toString(), true);
	}
	
	/**
	 * #36698 - Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	private DetachedCriteria montarCriteriaServidoresConselho(final String strPesquisa, final List<Integer> centroCusto){
		String[] listaSituacao =  new String[]{"P", "A", "S"};
		AghParametros parametroSigla = buscarParametros(AghuParametrosEnum.P_AGHU_CONSELHOS_PERINATOLOGIA_NASCIMENTO);
		if (parametroSigla == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório P_AGHU_CONSELHOS_PERINATOLOGIA_NASCIMENTO  não existe no banco");
		}
		List<String> siglas = tratarSiglas(parametroSigla.getVlrTexto());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
		criteria.add(Restrictions.in(VRapServidorConselho.Fields.IND_SITUACAO.toString(), listaSituacao));
		criteria.add(Restrictions.or(Restrictions.in(VRapServidorConselho.Fields.CCT_CODIGO.toString(), centroCusto),
				Restrictions.in(VRapServidorConselho.Fields.CCT_CODIGO_ATUA.toString(), centroCusto)));
		if (strPesquisa != null) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), strPesquisa));
			} else if (StringUtils.isNotEmpty(strPesquisa)) {
				criteria.add(Restrictions.ilike(VRapServidorConselho.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		if (siglas != null) {
			criteria.add(Restrictions.in(VRapServidorConselho.Fields.SIGLA.toString(), siglas));
		}
		return criteria;
	}
	
	private List<String> tratarSiglas(String parametro) {
		String[] siglas = parametro.split(",");
		List<String> listaSigla = new ArrayList<String>();
		for (String sigla : siglas) {
			if (StringUtils.isNotBlank(sigla)) {
				listaSigla.add(StringUtils.strip(sigla));
			}
		}
		return listaSigla;
	}
	
	private AghParametros buscarParametros(AghuParametrosEnum enumParametro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), enumParametro.toString()));
		return (AghParametros) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #36698 - Count Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	public Long pesquisarServidoresConselhoCount(final String strPesquisa, final List<Integer> centroCusto){
		DetachedCriteria criteria = montarCriteriaServidoresConselho(strPesquisa, centroCusto);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta a criteria de buscar profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @return
	 */
	private DetachedCriteria montarCriteriaServidoresConselhoPorSiglaCentroCusto(final List<String> siglas, final List<Integer> centroCusto){
		final String[] listaSituacao =  new String[]{"P", "A", "S"};
				
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
		criteria.add(Restrictions.in(VRapServidorConselho.Fields.IND_SITUACAO.toString(), listaSituacao));
		criteria.add(Restrictions.in(VRapServidorConselho.Fields.SIGLA.toString(), siglas));
		
		criteria.add(Restrictions.or(Restrictions.in(VRapServidorConselho.Fields.CCT_CODIGO.toString(), centroCusto),
				Restrictions.in(VRapServidorConselho.Fields.CCT_CODIGO_ATUA.toString(), centroCusto)));
		
		return criteria;
	}

	/**
	 * Buscar profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @param maxResults
	 * @return
	 */
	public List<VRapServidorConselho> pesquisarServidoresConselhoPorSiglaCentroCusto(final List<String> siglas, final List<Integer> centroCusto,
			Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaServidoresConselhoPorSiglaCentroCusto(siglas, centroCusto);
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Count de profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @return
	 */
	public Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(final List<String> siglas, final List<Integer> centroCusto) {
		DetachedCriteria criteria = this.montarCriteriaServidoresConselhoPorSiglaCentroCusto(siglas, centroCusto);
		return super.executeCriteriaCount(criteria);
	}

	/**
	 * Buscar profissional por matricula e vínculo
	 * 
	 * Web Service #38729
	 * 
	 * @param siglas
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	public VRapServidorConselho obterServidorConselhoPorSiglaMatriculaVinculo(final List<String> siglas, final Integer matricula, final Short vinculo) {
		final String[] listaSituacao = new String[] { "P", "A", "S" };
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
		criteria.add(Restrictions.in(VRapServidorConselho.Fields.IND_SITUACAO.toString(), listaSituacao));
		criteria.add(Restrictions.in(VRapServidorConselho.Fields.SIGLA.toString(), siglas));
		criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinculo));
		return (VRapServidorConselho) super.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Buscar profissional por matricula e vínculo
	 * 
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	public List<VRapServidorConselho> pesquisarConselhoPorMatriculaVinculo(final Integer matricula, final Short vinculo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
		criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinculo));
		return executeCriteria(criteria);
	}
	
	/**
	 * #5799
	 * C4 - Consulta para retornar o responsável do envio da informação ao prescribente (remetente)
	 */
	public VRapServidorConselho obterValoresPrescricaoMedica(Integer matricula, Short vinCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class, "VCS");

	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
		
	    return (VRapServidorConselho) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * F10 da estória 45269.
	 * Consulta para obter os servidores do conselho de acordo com a matrícula e vinculo passado por parâmetro.
	 * @param matricula
	 * @param vinCodigo
	 * @return lista
	 */
	public List<VRapServidorConselho> obterServidorConselhoPorMatriculaVinculo(Integer matricula, Short vinCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class);
		criteria.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), vinCodigo));
	    
	    criteria.add(Restrictions.or(
				Restrictions.eq(VRapServidorConselho.Fields.IND_SITUACAO.toString(), "A"), 
				Restrictions.and(Restrictions.eq(VRapServidorConselho.Fields.IND_SITUACAO.toString(), "P"), 
							Restrictions.ge(VRapServidorConselho.Fields.DT_FIM_VINCULO.toString(), DateUtil.truncaData(new Date())))));
	    criteria.add(Restrictions.isNotNull(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
	    
	    return executeCriteria(criteria);
	}
	/** #6810 SB3
	 * @param parametro
	 */
	public List<VRapServidorConselho> obterVRapServidorConselhoPorNumConselhoOuNome(String parametro){
		DetachedCriteria criteria = obterCriteriaVRapServidorConselhoPorNumConselhoOuNome(parametro);
		return executeCriteria(criteria, 0, 100, VRapServidorConselho.Fields.NOME.toString(), true);
	}
	
	/** #6810 SB3
	 * @param parametro
	 */
	public Long obterVRapServidorConselhoPorNumConselhoOuNomeCount(String parametro){
		DetachedCriteria criteria = obterCriteriaVRapServidorConselhoPorNumConselhoOuNome(parametro);
		return executeCriteriaCount(criteria);
	}

	/** #6810 SB3
	 * @param parametro
	 */
	private DetachedCriteria obterCriteriaVRapServidorConselhoPorNumConselhoOuNome(String parametro) {
			DetachedCriteria criteria = DetachedCriteria.forClass(VRapServidorConselho.class, "CONS");//alteração para sb3
			if(parametro != null && !parametro.isEmpty()){
				if(StringUtils.isNumeric(parametro)){
					criteria.add(Restrictions.eq(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString(), parametro));
				}else{
					criteria.add(Restrictions.ilike(VRapServidorConselho.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
				}
			}
			criteria.add(Restrictions.isNotNull(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
			//Projecoes para  o Vo de ambulatorio
			ProjectionList projecao = Projections.projectionList();
			projecao.add(Projections.property("CONS." + VRapServidorConselho.Fields.MATRICULA.toString())
												.as(br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO.Fields.MATRICULA.toString()), "matricula");
			projecao.add(Projections.property("CONS." + VRapServidorConselho.Fields.VIN_CODIGO.toString())
												.as(br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO.Fields.VIN_CODIGO.toString()), "vinCodigo");
			projecao.add(Projections.property("CONS." + VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString())
												.as(br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO.Fields.NRO_REG_CONSELHO.toString()), "nroRegConselho");
			projecao.add(Projections.property("CONS." + VRapServidorConselho.Fields.CPR_SIGLA.toString())
												.as(br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO.Fields.CPR_SIGLA.toString()), "cprSigla");
			projecao.add(Projections.property("CONS." + VRapServidorConselho.Fields.NOME.toString())
											.as(br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO.Fields.NOME.toString()), "nome");
		
		criteria.setProjection(projecao);
		criteria.setResultTransformer(Transformers.aliasToBean(br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO.class));
		
		return criteria;
	}
	
}