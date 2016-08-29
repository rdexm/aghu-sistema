package br.gov.mec.aghu.casca.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CsePerfilProcessoEsps;
import br.gov.mec.aghu.model.CsePerfilProcessos;
import br.gov.mec.aghu.model.CseProcessos;
import br.gov.mec.aghu.model.RapServidores;


public class CseProcessosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CseProcessos> {

	private static final String ROC = " = roc.";
	private static final String SEQ_PROCESSO2 = " = :seqProcesso ";
	private static final String IND_ASSINA2 = " = :indAssina ";
	private static final String SITUACAO_PROCESSO2 = " = :situacaoProcesso ";
	private static final String PPP = " = ppp.";
	private static final String IN = " in (";
	private static final String SITUACAO_PERFIL_PROCESSO2 = " = :situacaoPerfilProcesso ";
	private static final String WHERE_PPP = " where ppp.";
	private static final String AND_PPP = " and ppp.";
	private static final String AND_ROC = " and roc.";
	private static final String AS_PPP = " as ppp ";
	private static final String AS_ROC = " as roc, ";
	private static final String SELECT_COUNT_FROM = " select count(*) from ";
	private static final String SITUACAO_PERFIL_PROCESSO = "situacaoPerfilProcesso";
	private static final String SITUACAO_PROCESSO = "situacaoProcesso";
	private static final String IND_ASSINA = "indAssina";
	private static final String SEQ_PROCESSO = "seqProcesso";
	
	private static final String ALIAS_PPP2_PONTO = "PPP.";
	private static final String ALIAS_RSE_PONTO = "RSE.";


	private static final long serialVersionUID = -4295280995191918366L;

	@Inject
	private PerfilDAO perfilDAO;

	public Boolean validarPermissaoPorServidorESeqProcesso(String login, Short seqProcesso) {
		List<String> perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}
		
		StringBuffer hql = new StringBuffer(200);

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_ASSINA.toString()).append(IND_ASSINA2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		Query query = createHibernateQuery(hql.toString());

		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter(IND_ASSINA, true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		Object resultadoUnico = query.uniqueResult();				
		if (resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0) {
			return true;
		}
		
		hql = new StringBuffer();

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_ASSINA.toString()).append(IND_ASSINA2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(ROC).append(CseProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		query = createHibernateQuery(hql.toString());
		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter(IND_ASSINA, true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		resultadoUnico = query.uniqueResult();
		return resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0;
	}
	
	
	public Boolean validarPermissaoConsultaPorServidorESeqProcesso(String login, Short seqProcesso) {
		List<String> perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}
		
		StringBuffer hql = new StringBuffer(200);

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString())
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_CONSULTA.toString()).append(" = :indConsulta ")
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
	
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter("indConsulta", true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);

		Object resultadoUnico = query.uniqueResult();
		if (resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0) {
			return true;
		}
		
		hql = new StringBuffer();

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)

		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_CONSULTA.toString()).append(" = :indConsulta ")
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(ROC).append(CseProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");		
		
		query = createHibernateQuery(hql.toString());
		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter("indConsulta", true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		resultadoUnico = query.uniqueResult();
		return resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0;
	}

	public Boolean validarPermissaoExecutaPorServidorESeqProcesso(String login, Short seqProcesso) {
		List<String> perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}		
		
		StringBuffer hql = new StringBuffer(200);

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_EXECUTA.toString()).append(" = :indExecuta ")
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter("indExecuta", true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		Object resultadoUnico = query.uniqueResult();
		if (resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0) {
			return true;
		}
		
		hql = new StringBuffer();

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)

		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_EXECUTA.toString()).append(" = :indExecuta ")
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(ROC).append(CseProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		query = createHibernateQuery(hql.toString());
		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter("indExecuta", true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		resultadoUnico = query.uniqueResult();
		return resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0;
	}

	
	public Boolean validarPermissaoPorServidorEProcessos(String login, Short pRocSeq, Short pEspSeq) {
		List<String> perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}
		
		StringBuffer hql = new StringBuffer(200);
		
		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessoEsps.class.getName())
		.append(" as epp where epp.").append(CsePerfilProcessoEsps.Fields.SITUACAO.toString()).append(" = :situacaoPerfilProcessoEsps and epp.")
		.append(CsePerfilProcessoEsps.Fields.ROC_SEQ.toString()).append(" = :pRocSeq and epp.")
		.append(CsePerfilProcessoEsps.Fields.ESP_SEQ.toString()).append(" = :pEspSeq ")
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(" = epp.").append(CsePerfilProcessoEsps.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(" and epp.").append(CsePerfilProcessoEsps.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacaoPerfilProcessoEsps", DominioSituacao.A);
		query.setParameter("pRocSeq", pRocSeq);
		query.setParameter("pEspSeq", pEspSeq);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		Object resultadoUnico = query.uniqueResult();
		if (resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0) {
			return true;
		}
		
		hql = new StringBuffer();

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessoEsps.class.getName())
		.append(" as epp  where epp.").append(CsePerfilProcessoEsps.Fields.SITUACAO.toString()).append(" = :situacaoPerfilProcessoEsps and epp.")
		.append(CsePerfilProcessoEsps.Fields.SITUACAO.toString()).append(" = :situacaoPerfilProcessoEsps and epp.")
		.append(CsePerfilProcessoEsps.Fields.ESP_SEQ.toString()).append(" = :pEspSeq ")
		.append(AND_ROC).append(CseProcessos.Fields.ROC_SEQ.toString()).append(" = epp.").append(CsePerfilProcessoEsps.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(" = :pRocSeq")
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(" and epp.").append(CsePerfilProcessoEsps.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		query = createHibernateQuery(hql.toString());
		query.setParameter("situacaoPerfilProcessoEsps", DominioSituacao.A);
		query.setParameter("pEspSeq", pEspSeq);
		query.setParameter("pRocSeq", pRocSeq);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		resultadoUnico = query.uniqueResult();
		return resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0;
	}
	
	public Boolean validarPermissaoPorServidorERocSeq(String login, Short cRoqSeq) {
		List<String> perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}		
		
		StringBuffer hql = new StringBuffer(180);

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(" = :cRoqSeq ")
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cRoqSeq", cRoqSeq);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		Object resultadoUnico = query.uniqueResult();
		if (resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0) {
			return true;
		}
		
		hql = new StringBuffer();

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)

		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		
		.append(AND_ROC).append(CseProcessos.Fields.ROC_SEQ.toString()).append(PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(" = :cRoqSeq ")
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)

		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		query = createHibernateQuery(hql.toString());
		query.setParameter("cRoqSeq", cRoqSeq);	
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		resultadoUnico = query.uniqueResult();
		return resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0;
	}
	
	private List<String> obterPerfisPorUsuario(String login)  {
		return perfilDAO.obterNomePerfisPorUsuario(login);		
	}
	
	private String converterComDelimitador(Collection<String> lista) {
		if (lista == null) {
			return "";
		}
		
		StringBuffer sb = new StringBuffer();		
		Iterator<String> it = lista.iterator();
		while (it.hasNext()) {
			sb.append("'"+it.next()+"'");
			
			if (it.hasNext()) {
				sb.append(',');
			}			
		}
		
		return sb.toString();
	}	
	
	/**
	 * Não considera os perfis que tenham prefixo passado como parametro
	 * @param login
	 * @param seqProcesso
	 * @param prefixoPerfil
	 * @return
	 */
	public Boolean validarPermissaoPorServidorESeqProcessoEPrefixoPerfil(String login, Short seqProcesso, String prefixoPerfil) {
		List<String> perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}
		Object perfisFiltro = converterComDelimitador(perfis, prefixoPerfil);
		if(perfisFiltro == null){
			return Boolean.FALSE;
		}
		
		StringBuffer hql = new StringBuffer(200);

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_ASSINA.toString()).append(IND_ASSINA2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(perfisFiltro).append(") ");
		
		Query query = createHibernateQuery(hql.toString());

		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter(IND_ASSINA, true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		Object resultadoUnico = query.uniqueResult();				
		if (resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0) {
			return true;
		}
		
		hql = new StringBuffer();

		hql.append(SELECT_COUNT_FROM)
		.append(CseProcessos.class.getName())
		.append(AS_ROC)
		.append(CsePerfilProcessos.class.getName())
		.append(AS_PPP)
		.append(WHERE_PPP).append(CsePerfilProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PERFIL_PROCESSO2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.IND_ASSINA.toString()).append(IND_ASSINA2)
		.append(AND_PPP).append(CsePerfilProcessos.Fields.ROC_SEQ.toString()).append(ROC).append(CseProcessos.Fields.ROC_SEQ.toString())
		.append(AND_ROC).append(CseProcessos.Fields.SEQ.toString()).append(SEQ_PROCESSO2)
		.append(AND_ROC).append(CseProcessos.Fields.SITUACAO.toString()).append(SITUACAO_PROCESSO2)
		
		.append(AND_PPP).append(CsePerfilProcessos.Fields.PER_NOME.toString())
			.append(IN).append(converterComDelimitador(perfis)).append(") ");
		
		query = createHibernateQuery(hql.toString());
		query.setParameter(SEQ_PROCESSO, seqProcesso);
		query.setParameter(IND_ASSINA, true);
		query.setParameter(SITUACAO_PERFIL_PROCESSO, DominioSituacao.A);
		query.setParameter(SITUACAO_PROCESSO, DominioSituacao.A);
		
		resultadoUnico = query.uniqueResult();
		return resultadoUnico != null && Long.valueOf(resultadoUnico.toString()) > 0;
	}


	private Object converterComDelimitador(List<String> lista,
			String prefixoPerfil) {
		if (lista == null) {
			return "";
		}
		
		StringBuffer sb = new StringBuffer();		
		Iterator<String> it = lista.iterator();
		while (it.hasNext()) {
			String perf = it.next();
				if(!perf.startsWith(prefixoPerfil)){
					sb.append("'"+perf+"'");
					
					if (it.hasNext()) {
						sb.append(',');
					}			
			}
		}
		if(sb.toString().endsWith("',")){
			sb= new StringBuffer(sb.toString().replace("',", "'"));
		}
		if("".equals(sb.toString())){
			return null;
		}
		return sb.toString();
	}
	
	private List<String> validarUsuarioExecutarProcesso(Short pSerVinCodigo, Integer pSerMatricula, Short pProcSeq, boolean testePai){
        DetachedCriteria criteria = DetachedCriteria.forClass(CseProcessos.class, "ROC");
        if(testePai){
               criteria.createAlias("ROC."+CseProcessos.Fields.PERFIL_PROCESSO.toString(), "PPP");
        }else{
               criteria.createAlias("ROC."+CseProcessos.Fields.PROCESSO_PAI.toString(), "PPP");
        }

        criteria.setProjection(Projections.sqlProjection(" 'S' ", new String[]{"S"}, new Type[]{StringType.INSTANCE}));
        
        if(pSerVinCodigo != null){
               criteria.add(Restrictions.eq(ALIAS_RSE_PONTO+RapServidores.Fields.VIN_CODIGO.toString(),pSerVinCodigo));
        }
        if(pSerMatricula != null){
               criteria.add(Restrictions.eq(ALIAS_RSE_PONTO+RapServidores.Fields.MATRICULA.toString(),pSerMatricula));
        }
        criteria.add(Restrictions.eq(ALIAS_PPP2_PONTO+CsePerfilProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
         criteria.add(Restrictions.or(Restrictions.eq(ALIAS_PPP2_PONTO+CsePerfilProcessos.Fields.IND_EXECUTA.toString(),Boolean.TRUE),
                      Restrictions.eq(ALIAS_PPP2_PONTO+CsePerfilProcessos.Fields.IND_ASSINA.toString(), Boolean.TRUE)));
        if(pProcSeq != null){
               criteria.add(Restrictions.eq("ROC."+CseProcessos.Fields.SEQ.toString(), pProcSeq));
        }
        criteria.add(Restrictions.eq("ROC."+CseProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
        
        DetachedCriteria subCriteriaPerfil = subCriteriaPerfil();
        
        DetachedCriteria subCriteria = subCriteria(pSerVinCodigo, pSerMatricula);
        
        subCriteriaPerfil.add(Subqueries.propertiesIn(new String[]{"USR."+Usuario.Fields.LOGIN.toString()}, 
                      subCriteria));
        
        criteria.add(Subqueries.propertiesIn(new String[]{ALIAS_PPP2_PONTO+CsePerfilProcessos.Fields.PER_NOME.toString()}, 
                      subCriteriaPerfil));
        return executeCriteria(criteria);
  }

  private DetachedCriteria subCriteriaPerfil() {
        DetachedCriteria subCriteriaPerfil = DetachedCriteria.forClass(Perfil.class,"PER");
        subCriteriaPerfil.createAlias("PER."+Perfil.Fields.PERFIS_USUARIOS.toString(), "PFU1");
        subCriteriaPerfil.createAlias("PFU1."+PerfisUsuarios.Fields.USUARIO.toString(), "USR");
        return subCriteriaPerfil;
  }

  private DetachedCriteria subCriteria(Short pSerVinCodigo,
               Integer pSerMatricula) {
        DetachedCriteria subCriteria = DetachedCriteria.forClass(RapServidores.class);
        subCriteria.setProjection(Projections.property(RapServidores.Fields.USUARIO.toString()));
        if(pSerVinCodigo != null){
               subCriteria.add(Restrictions.eq(RapServidores.Fields.VIN_CODIGO.toString(),pSerVinCodigo));
        }
        if(pSerMatricula != null){
               subCriteria.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(),pSerMatricula));
        }
        return subCriteria;
  }
  
  public String validarUsuarioExecutarProcessoUnion(Short pSerVinCodigo, Integer pSerMatricula, Short pProcSeq){
        List<String> lista = new ArrayList<String>();
        lista.addAll(validarUsuarioExecutarProcesso(pSerVinCodigo,pSerMatricula,pProcSeq, true));
        lista.addAll(validarUsuarioExecutarProcesso(pSerVinCodigo,pSerMatricula,pProcSeq, false));
        
        if(!lista.isEmpty() && lista != null){
               return lista.get(0);
        }
        return StringUtils.EMPTY;
  }

}