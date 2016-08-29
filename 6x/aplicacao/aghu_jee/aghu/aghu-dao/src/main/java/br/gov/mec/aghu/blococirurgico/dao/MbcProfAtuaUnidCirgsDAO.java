package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasC2VO;
import br.gov.mec.aghu.blococirurgico.vo.MbcEquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProfAtuaUnidCirgsVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProfissionaisUnidadeCirurgicaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaEquipeAnestesia;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcRefCode;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class MbcProfAtuaUnidCirgsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProfAtuaUnidCirgs> {

	private static final long serialVersionUID = -7130781207607832441L;

	public List<PortalPesquisaCirurgiasC2VO> listarMbcProfAtuaUnidCirgsPorUnfSeq(Short unfSeq, String strPesquisa) {
		DetachedCriteria criteria = criarCriteriaMbcProfAtuaUnidCirgsPorUnfSeq(unfSeq, strPesquisa);

		criteria.setResultTransformer(Transformers.aliasToBean(PortalPesquisaCirurgiasC2VO.class));
		
		return executeCriteria(criteria);
	}

	public Long listarMbcProfAtuaUnidCirgsPorUnfSeqCount(Short unfSeq, String strPesquisa) {
		DetachedCriteria criteria = criarCriteriaMbcProfAtuaUnidCirgsPorUnfSeq(unfSeq, strPesquisa);
		List<PortalPesquisaCirurgiasC2VO> lista = executeCriteria(criteria);
		// Refeita a consulta pois o método executeCriteriaCount exclui as projections trazendo um valor diferente.
		Long count = Long.valueOf(0);
		if(lista != null){
			count = Long.valueOf(lista.size());
		}
		return count;
	}

	private DetachedCriteria criarCriteriaMbcProfAtuaUnidCirgsPorUnfSeq(Short unfSeq, String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");


		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString())), PortalPesquisaCirurgiasC2VO.Fields.SER_MATRICULA.toString())
				.add(Projections.property("puc." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), PortalPesquisaCirurgiasC2VO.Fields.SER_VIN_CODIGO.toString())
				.add(Projections.property("puc." + MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString()), PortalPesquisaCirurgiasC2VO.Fields.IND_FUNCAO_PROF.toString())
				.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()), PortalPesquisaCirurgiasC2VO.Fields.NOME_USUAL.toString())
				.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()), PortalPesquisaCirurgiasC2VO.Fields.NOME.toString())
		);

		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.MPF));
		if(unfSeq != null){
			criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if(strPesquisa != null && !strPesquisa.isEmpty()){
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.isNotNull("pes." + RapPessoasFisicas.Fields.NOME.toString()), 
							Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE))
							,
							Restrictions.and(Restrictions.isNotNull("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()), 
									Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString(), strPesquisa, MatchMode.ANYWHERE))));
		}
		return criteria;
	}

	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidor(final RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class);
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(final RapServidores servidor, final Short unf_Seq) {
		return this.pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(servidor, unf_Seq, true);
	}
	
	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(final RapServidores servidor, final Short unf_Seq, final boolean considerarFuncoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "PUC");

		if(servidor != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), servidor));
		} else {
			criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "SER");
			criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		}

		if(unf_Seq != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unf_Seq));
		}
		criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(considerarFuncoes){
			Criterion criIn = Restrictions.in("PUC." + MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), new Object[] { DominioFuncaoProfissional.ENF, DominioFuncaoProfissional.CIR,
				DominioFuncaoProfissional.INS, DominioFuncaoProfissional.ESE });
			criteria.add(Restrictions.not(criIn));			
		}
		return executeCriteria(criteria);
	}
	
	
	public MbcProfAtuaUnidCirgs pesquisarProfUnidCirgAtivoPorServidorUnfSeqFuncao(final RapServidores servidor, 
											final Short unf_Seq, final DominioFuncaoProfissional funcao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "PUC");
		criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("SER." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "CCL");

		if(servidor != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), servidor));
		} 

		if(unf_Seq != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unf_Seq));
		}
		
		if(funcao!=null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), funcao));
		}
		
		criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		
		return (MbcProfAtuaUnidCirgs) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Monta HQL equivalente a pesquisa realizada na view V_MBC_CONSELHO. O HQL
	 * retornado possui dois parâmetros :listaSigla (Collection de String) e :situacao
	 * (DominioSituacao).
	 * 
	 * @return
	 */
	private StringBuilder montarHqlPesquisaProfAtuaUnidCirgConselho(Boolean count, List<DominioFuncaoProfissional> listaIndFuncaoProf, String nome) {
		StringBuilder hql = new StringBuilder(200);

		if(Boolean.TRUE.equals(count)) {
			hql.append("select count(*)");
		} else {
			hql.append("select distinct")
			.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.SER_VIN_CODIGO.toString())
			.append(", puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.SER_MATRICULA.toString())
			.append(", puc.").append(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.IND_FUNCAO_PROF.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.SIGLA.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.SIGLA.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.NRO_REG_CONSELHO.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.NOME.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.NOME.toString())
			.append(", vcs.").append(VRapServidorConselho.Fields.CONSELHO_SIGLA.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.CONSELHO_SIGLA.toString())
			.append(", puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString())
			.append(" as ").append(ProfDescricaoCirurgicaVO.Fields.UNF_SEQ.toString());
		}
		
		hql.append(" from ")
		.append(MbcProfAtuaUnidCirgs.class.getName()).append(" puc, ")
		.append(VRapServidorConselho.class.getName()).append(" vcs")

		.append(" where")
		.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString())
		.append(" = vcs.").append(VRapServidorConselho.Fields.MATRICULA.toString())
		.append(" and")
		.append(" puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString())
		.append(" = vcs.").append(VRapServidorConselho.Fields.VIN_CODIGO.toString())

		.append(" and vcs.")
		.append(VRapServidorConselho.Fields.SIGLA.toString())
		.append(" in (:listaSigla)")
		.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()).append(" = :situacao ");
		if(listaIndFuncaoProf!=null){
			hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString()).append(" in (:listaIndFuncaoProf)");
		}

		return hql;
	}

	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	public List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(
			Integer serMatricula, Short serVinCodigo, Short unfSeq, List<String> listaSigla, DominioSituacao situacao,  List<DominioFuncaoProfissional> listaIndFuncaoProf, String nome) {
		
		StringBuilder hql = montarHqlPesquisaProfAtuaUnidCirgConselho(false, listaIndFuncaoProf,nome);
		
		if(serMatricula!=null){
			hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()).append(" = :serMatricula");
		}
		if(serVinCodigo!=null){
			hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()).append(" = :serVinCodigo");
		}
		if(unfSeq!=null){
			hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()).append(" = :unfSeq");
		}
		
		if(nome !=null){
			hql.append(" and vcs.").append(VRapServidorConselho.Fields.NOME.toString()).append(" like upper('%' || :nome || '%') ");
		}
		
		hql.append(" order by vcs." ).append( VRapServidorConselho.Fields.NOME.toString());

		final Query query = createHibernateQuery(hql.toString());
		if(serMatricula!=null){
			query.setParameter("serMatricula", serMatricula);
		}
		if(serVinCodigo!=null){
			query.setParameter("serVinCodigo", serVinCodigo);
		}
		if(unfSeq!=null){
			query.setParameter("unfSeq", unfSeq);
		}
		query.setParameterList("listaSigla", listaSigla);
		query.setParameter("situacao", situacao);		
		if(listaIndFuncaoProf!=null){
			query.setParameterList("listaIndFuncaoProf", listaIndFuncaoProf);
		}
		
		if(nome!=null){
			query.setParameter("nome", nome.toUpperCase());		
		}

		query.setResultTransformer(Transformers.aliasToBean(ProfDescricaoCirurgicaVO.class));
		
		return query.list();
	}

	private StringBuilder pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoHQL(Object objPesquisa,
			Short unfSeq, DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao, Boolean count) {
		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		StringBuilder hql = montarHqlPesquisaProfAtuaUnidCirgConselho(count, null, null);

		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString())
		.append(" = :unfSeq")
		.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString())
		.append(" = :indFuncaoProf");

		if (StringUtils.isNotBlank(strPesquisa)) {
			hql.append(" and ((upper(vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString())
			.append(" ) like upper('%' || :nroRegConselho || '%'))")
			.append(" or (upper(vcs.").append(VRapServidorConselho.Fields.NOME.toString())
			.append(" ) like upper('%' || :nome || '%')))");
		}

		return hql;
	}
	
	@SuppressWarnings("unchecked")
	public Long pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(Object objPesquisa,
			Short unfSeq, DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {

		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}

		StringBuilder hql = pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoHQL(objPesquisa, unfSeq, indFuncaoProf, listaSigla, situacao, true);

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("indFuncaoProf", indFuncaoProf);
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("nroRegConselho", strPesquisa.toUpperCase());
			query.setParameter("nome", strPesquisa.toUpperCase());			
		}
		query.setParameterList("listaSigla", listaSigla);
		query.setParameter("situacao", situacao);

		return (Long)query.uniqueResult();		
	}
	
	public List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(Integer firstResult,Integer maxResult,Object objPesquisa,
			Short unfSeq, DominioFuncaoProfissional indFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {

		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}

		StringBuilder hql = pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoHQL(objPesquisa, unfSeq, indFuncaoProf, listaSigla, situacao, false);

		hql.append(" order by vcs." ).append( VRapServidorConselho.Fields.NOME.toString());

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("indFuncaoProf", indFuncaoProf);
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("nroRegConselho", strPesquisa.toUpperCase());
			query.setParameter("nome", strPesquisa.toUpperCase());			
		}
		query.setParameterList("listaSigla", listaSigla);
		query.setParameter("situacao", situacao);

		query.setResultTransformer(Transformers
				.aliasToBean(ProfDescricaoCirurgicaVO.class));
		
		
		if (firstResult != null) {
		    query.setFirstResult(firstResult);
		}

		if (maxResult != null) {
		    query.setMaxResults(maxResult);
		}

		return query.list();
	    }


	private StringBuilder pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoHQL(Object objPesquisa,
			Short unfSeq, List<DominioFuncaoProfissional> listaIndFuncaoProf, List<String> listaSigla, DominioSituacao situacao, Boolean count) {

		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		StringBuilder hql = montarHqlPesquisaProfAtuaUnidCirgConselho(count, null, null);

		hql.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString())
		.append(" = :unfSeq")
		.append(" and puc.").append(MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString())
		.append(" in (:listaIndFuncaoProf)");

		if (StringUtils.isNotBlank(strPesquisa)) {
			hql.append(" and ((upper(vcs.").append(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString())
			.append(" ) like upper('%' || :nroRegConselho || '%'))")
			.append(" or (upper(vcs.").append(VRapServidorConselho.Fields.NOME.toString())
			.append(" ) like upper('%' || :nome || '%')))");
		}

		return hql;
	}

	@SuppressWarnings("unchecked")
	public Long pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoCount(Object objPesquisa,
			Short unfSeq, List<DominioFuncaoProfissional> listaIndFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {


		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		StringBuilder hql = pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoHQL(objPesquisa, unfSeq, listaIndFuncaoProf, listaSigla, situacao, true);

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		query.setParameterList("listaIndFuncaoProf", listaIndFuncaoProf);
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("nroRegConselho", strPesquisa.toUpperCase());
			query.setParameter("nome", strPesquisa.toUpperCase());
		}
		query.setParameterList("listaSigla", listaSigla);
		query.setParameter("situacao", situacao);

		return (Long)query.uniqueResult();		
	}

	@SuppressWarnings("unchecked")
	public List<ProfDescricaoCirurgicaVO> pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncao(Object objPesquisa,
			Short unfSeq, List<DominioFuncaoProfissional> listaIndFuncaoProf, List<String> listaSigla, DominioSituacao situacao) {


		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		
		StringBuilder hql = new StringBuilder(300); 
		hql = pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEListaFuncaoHQL(objPesquisa, unfSeq, listaIndFuncaoProf, listaSigla, situacao, false);
		
		hql.append(" order by vcs." ).append( VRapServidorConselho.Fields.NOME.toString());

		final Query query = createHibernateQuery(hql.toString());
		query.setMaxResults(100);
		query.setParameter("unfSeq", unfSeq);
		query.setParameterList("listaIndFuncaoProf", listaIndFuncaoProf);
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("nroRegConselho", strPesquisa.toUpperCase());
			query.setParameter("nome", strPesquisa.toUpperCase());
		}
		query.setParameterList("listaSigla", listaSigla);
		query.setParameter("situacao", situacao);

		query.setResultTransformer(Transformers
				.aliasToBean(ProfDescricaoCirurgicaVO.class));
		return query.list();		
	}

	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgUnfSeqFea(final Short unf_Seq, MbcFichaEquipeAnestesia fea) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class);

		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unf_Seq));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), fea.getServidorAnest().getId().getMatricula()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), fea.getServidorAnest().getId().getVinCodigo()));

		return executeCriteria(criteria);
	}

	public List<MbcProfAtuaUnidCirgs> buscarEquipeMedicaParaMudancaNaAgenda(String nome, Short unfSeq, Short espSeq) {
		DetachedCriteria criteria =  obterCriteriaEquipeMedicaParaMudancaNaAgenda(nome, unfSeq, espSeq);
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public Long buscarEquipeMedicaParaMudancaNaAgendaCount(String nome, Short unfSeq, Short espSeq) {
		return executeCriteriaCount(obterCriteriaEquipeMedicaParaMudancaNaAgenda(nome, unfSeq, espSeq));
	}

	private DetachedCriteria obterCriteriaEquipeMedicaParaMudancaNaAgenda(String nome, Short unfSeq, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");

		if(nome != null && !nome.isEmpty()) {
			criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNotNull("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()),
					Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString(), nome, MatchMode.ANYWHERE)),
					Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), nome, MatchMode.ANYWHERE)));
		}

		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (unfSeq != null) {
			criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		}

		criteria.add(Restrictions.in("puc." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), 
				new DominioFuncaoProfissional[] {DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO}));
		if(espSeq != null) {
			DetachedCriteria subCriteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "pre");

			subCriteria.setProjection(Projections.property("pre." + AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ));

			subCriteria.add(Restrictions.eqProperty("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(),
					"pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString()));
			subCriteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
			subCriteria.add(Restrictions.or(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true),
					Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S)));

			criteria.add(Subqueries.exists(subCriteria));
		}
		return criteria;
	}

	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais) {
		return pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, true);
	}

	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais, boolean matriculaLong) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.CARACT_SALA_ESP.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString(), "esp");

		getCriteriaPesquisarPuc(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, criteria, funcoesProfissionais);

		criteria.setProjection(Projections.distinct(getProjectionNomeMatVinCodUnf(matriculaLong)));

		return Long.parseLong(String.valueOf(executeCriteria(criteria).size()));		
	}

	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais) {
		return pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, true);
	}

	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais, boolean matriculaLong) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");

		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.CARACT_SALA_ESP.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString(), "esp");
		
		getCriteriaPesquisarPuc(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, criteria, funcoesProfissionais);

		criteria.setProjection(Projections.distinct(getProjectionNomeMatVinCodUnf(matriculaLong)));
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		criteria.addOrder(Order.asc(LinhaReportVO.Fields.TEXTO2.toString()));
		criteria.addOrder(Order.asc(LinhaReportVO.Fields.TEXTO1.toString()));
		return executeCriteria(criteria, 0, 100, null, true);

	}

	private void getCriteriaPesquisarPuc(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp, DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno,DominioSituacao situacao, DetachedCriteria criteria,
			DominioFuncaoProfissional... funcoesProfissionais) {

		getCriteriaPesquisaByMatriculaOrNomePessoa(pesquisaSuggestion, criteria);
		getCriteriaCondicaoPesquisaPuc(unfSeq, situacao, criteria, funcoesProfissionais);
		getCriteriaCondicaoPesquisaPucExist(unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, criteria);		
	}

	private void getCriteriaCondicaoPesquisaPucExist(Short unfSeq, Short sciSeqp, DominioDiaSemanaSigla dominioDiaSemanaSigla,
			String turno, DetachedCriteria criteria) {

		DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcMvtoSalaEspEquipe.class,"see");		

		subCriteria.createAlias("see." + MbcMvtoSalaEspEquipe.Fields.MBC_CARACT_SALA_ESPS.toString(), "cse");
		subCriteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");

		subCriteria.setProjection(Projections.property("cse."+ MbcCaractSalaEsp.Fields.CAS_SEQ.toString()));
		
		subCriteria.add(Restrictions.eqProperty("puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(),
				"cse." + MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString()));
		
		subCriteria.add(Restrictions.eqProperty("puc." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(),
				"cse." + MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString()));
		
		subCriteria.add(Restrictions.eqProperty("puc." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(),
				"cse." + MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString()));
		
		subCriteria.add(Restrictions.eqProperty("puc." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(),
				"cse." + MbcCaractSalaEsp.Fields.PUC_UNF_SEQ.toString()));		

		subCriteria.add(Restrictions.eq("cse."+ MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		Short percentual = 0;
		subCriteria.add(Restrictions.gt("see."+ MbcMvtoSalaEspEquipe.Fields.PERCENTUAL_RESERVA.toString(), percentual.shortValue()));

		if(unfSeq != null){
			subCriteria.add(Restrictions.eq("cas."+ MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA_ID_UNF_SEQ.toString(), unfSeq));
		}
		if(sciSeqp != null){
			subCriteria.add(Restrictions.eq("cas."+ MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA_ID_SEQP.toString(), sciSeqp));
		}
		if(dominioDiaSemanaSigla != null){
			subCriteria.add(Restrictions.eq("cas."+ MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), dominioDiaSemanaSigla));
		}
		if(turno != null){
			subCriteria.add(Restrictions.eq("cas."+ MbcCaracteristicaSalaCirg.Fields.TURNO_ID.toString(), turno));
		}			

		criteria.add(Subqueries.exists(subCriteria));		
	}

	private void getCriteriaCondicaoPesquisaPuc(Short unfSeq, DominioSituacao situacao, DetachedCriteria criteria,
			DominioFuncaoProfissional... funcoesProfissionais) {
		if(unfSeq != null){
			criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		}

		if(funcoesProfissionais != null && funcoesProfissionais.length > 0){
			criteria.add(Restrictions.in("puc." + MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), funcoesProfissionais));
		}

		if(situacao != null){
			criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), situacao));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	}

	private void getCriteriaPesquisaByMatriculaOrNomePessoa(Object pesquisaSuggestion, DetachedCriteria criteria) {
		if(pesquisaSuggestion != null && StringUtils.isNotBlank(pesquisaSuggestion.toString())){
			if (CoreUtil.isNumeroInteger(pesquisaSuggestion)) {
				criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), Integer.valueOf(pesquisaSuggestion.toString())));
			} else {
				criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), (String)pesquisaSuggestion, MatchMode.ANYWHERE));
			}
		}
	}

	public Projection getProjectionNomeMatVinCodUnf(){
		return getProjectionNomeMatVinCodUnf(true);
	}
	
	public Projection getProjectionNomeMatVinCodUnf(boolean matriculaLong){

		// Condicional criada devido a uma mudança na revisão 168094 da qual muda a propriedade numero6 da classe LinhaReportVO 
		// de Integer para Long. A mesma consiste na correção sob demanda do problema que ocorre devido a esta propriedade ser
		// do tipo Long e a propriedade serMatricula da entidade MbcProfAtuaUnidCirgsId ser do tipo Integer. Isto foi feito devido
		// ao período de estabilização do qual essa mudança é consideravelmente sensível e seria de grande impacto se corrigida
		// em uma única vez.
		if (matriculaLong) {
			return (Projections.projectionList()	
					.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()),		LinhaReportVO.Fields.TEXTO1.toString())
					.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()), LinhaReportVO.Fields.TEXTO2.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA	.toString()), 	LinhaReportVO.Fields.NUMERO6.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), 	LinhaReportVO.Fields.NUMERO4.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ		.toString()), 	LinhaReportVO.Fields.NUMERO5.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), 	LinhaReportVO.Fields.OBJECT.toString())
					.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()), 	LinhaReportVO.Fields.SIGLA_ESP.toString())
					.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), 	LinhaReportVO.Fields.SEQ_ESP.toString())
			);
		}
		else {
			return (Projections.projectionList()	
					.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()),		LinhaReportVO.Fields.TEXTO1.toString())
					.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()), LinhaReportVO.Fields.TEXTO2.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA	.toString()), 	LinhaReportVO.Fields.NUMERO11.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()), 	LinhaReportVO.Fields.NUMERO4.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ		.toString()), 	LinhaReportVO.Fields.NUMERO5.toString())
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), 	LinhaReportVO.Fields.OBJECT.toString())
					.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()), 	LinhaReportVO.Fields.SIGLA_ESP.toString())
					.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), 	LinhaReportVO.Fields.SEQ_ESP.toString())
			);
		}
	}

	public List<MbcProfAtuaUnidCirgs> buscarEquipeMedicaParaAgendamento(String nome, Short unfSeq, Short espSeq){
		DetachedCriteria criteria = obterCriteriaEquipeMedicaParaAgendamento(nome, unfSeq, espSeq);
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long buscarEquipeMedicaParaAgendamentoCount(String nome, Short unfSeq, Short espSeq) {
		return executeCriteriaCount(obterCriteriaEquipeMedicaParaAgendamento(nome, unfSeq, espSeq));
	}

	public DetachedCriteria obterCriteriaEquipeMedicaParaAgendamento(String nome, Short unfSeq, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "rap", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", CriteriaSpecification.LEFT_JOIN);

		if(nome != null && !nome.isEmpty()) {
			if (CoreUtil.isNumeroInteger(nome)) {
				criteria.add(Restrictions.eq("pes." + RapPessoasFisicas.Fields.CODIGO.toString(), Integer.valueOf(nome)));
			} else {
				criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.and(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A),
									  Restrictions.eq("rap." + RapServidores.Fields.IND_SITUACAO.toString() , DominioSituacao.A)));
		if(unfSeq!=null){
			criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		}
		criteria.add(Restrictions.in("puc." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), 
				new DominioFuncaoProfissional[] {DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO}));

		if(espSeq != null) {
			DetachedCriteria subCriteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "pre");

			subCriteria.setProjection(Projections.property("pre." + AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ));
			subCriteria.add(Restrictions.eqProperty("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(),
					"pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString()));
			subCriteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
			subCriteria.add(Restrictions.or(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true),
					Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S)));

			criteria.add(Subqueries.exists(subCriteria));
		}

		return criteria;
	}

	public List<MbcProfAtuaUnidCirgs> buscarEquipesPorUsuarioLogado(RapServidores usuarioLogado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.UNIDADE_FUNCIONAL.toString(), "unf", CriteriaSpecification.INNER_JOIN);
		criteria.createAlias("unf." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "car", CriteriaSpecification.INNER_JOIN);
		criteria.createAlias("ser." + RapServidores.Fields.EQUIPES.toString(), "eqp", CriteriaSpecification.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.like("car." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.MPF));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghEquipes.class, "eqp");
		subCriteria.createAlias("eqp." + AghEquipes.Fields.MPM_LISTA_SERV_EQUIPE.toString(), "lsq");
		
		subCriteria.setProjection(Projections.property("eqp." + AghEquipes.Fields.CODIGO.toString()));
		
		subCriteria.add(Restrictions.eq("eqp." + AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eq("lsq." + MpmListaServEquipe.Fields.SER_MATRICULA.toString(), usuarioLogado.getId().getMatricula()));
		subCriteria.add(Restrictions.eq("lsq." + MpmListaServEquipe.Fields.SER_VIN_CODIGO.toString(), usuarioLogado.getId().getVinCodigo()));
		subCriteria.add(Restrictions.eqProperty("eqp." + AghEquipes.Fields.RAP_SERVIDORES_ID_MATRICULA.toString(),
				"puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()));
		subCriteria.add(Restrictions.eqProperty("eqp." + AghEquipes.Fields.RAP_SERVIDORES_ID_VIN_CODIGO.toString(),
				"puc." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		return executeCriteria(criteria);	
	}	

	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisAtuaCirurgiaAgendaProcedimentos(MbcProfCirurgias profCirurgia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class);

		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), profCirurgia.getId().getPucSerMatricula()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), profCirurgia.getId().getPucSerVinCodigo()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), profCirurgia.getId().getPucUnfSeq()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), profCirurgia.getId().getPucIndFuncaoProf()));

		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), profCirurgia.getCirurgia().getUnidadeFuncional().getSeq()));

		//#41148 - Comentado pois não estava trazendo estes profissionais na Nota de Consumo e no Agendamento de Procedimentos
		//final DominioFuncaoProfissional[] funcoes = { DominioFuncaoProfissional.ENF, DominioFuncaoProfissional.CIR, DominioFuncaoProfissional.INS };
		//criteria.add(Restrictions.not(Restrictions.in(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), funcoes)));

		return executeCriteria(criteria);
	}
	
	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionalPorUnidade(Integer serMatricula, Short serVinCodigo, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class);
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), serMatricula));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), serVinCodigo));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), new DominioFuncaoProfissional[]{DominioFuncaoProfissional.MPF,
			DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MAX}));
		return executeCriteria(criteria);
	}

	public MbcProfAtuaUnidCirgs pesquisarMbcProfAtuaUnidCirgsPorMbcProfCirurgias(MbcProfCirurgias profCirurgia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class);

		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), profCirurgia.getId().getPucSerMatricula()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), profCirurgia.getId().getPucSerVinCodigo()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), profCirurgia.getId().getPucUnfSeq()));
		criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), profCirurgia.getId().getPucIndFuncaoProf()));

		return (MbcProfAtuaUnidCirgs)executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarProfissionalEstaAtivoEmOutraUnidade(MbcProfAtuaUnidCirgs profissional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "PUC");

		if(profissional.getUnidadeFuncional() != null){
			criteria.add(Restrictions.not(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), profissional.getUnidadeFuncional().getSeq())));
		}
		if(profissional.getRapServidores().getId().getMatricula() != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), profissional.getRapServidores().getId().getMatricula()));
		}
		if(profissional.getRapServidores().getId().getVinCodigo() != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), profissional.getRapServidores().getId().getVinCodigo()));
		}		

		criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in("PUC." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), new DominioFuncaoProfissional[]{DominioFuncaoProfissional.ANP, 
																																		DominioFuncaoProfissional.ANC,
																																		DominioFuncaoProfissional.ANR,
																																		DominioFuncaoProfissional.MAX,
																																		DominioFuncaoProfissional.MCO,
																																		DominioFuncaoProfissional.MPF,
																																		DominioFuncaoProfissional.ENF}));
		
		return executeCriteriaExists(criteria);
	}	

	private DetachedCriteria gerarCriteriaPesquisarFuncaoProfissionalEscala(Object objFuncaoProfissional, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "PUC");
		criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "RAP");
		criteria.createAlias("RAP." + RapServidores.Fields.PESSOA_FISICA.toString(), "PEF");


		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		} 

		if(!objFuncaoProfissional.toString().isEmpty()){

			if(CoreUtil.isNumeroInteger(objFuncaoProfissional)){
				criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), Integer.valueOf(objFuncaoProfissional.toString())));
			} else if (objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.ANC.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.ANP.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.ANR.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.CIR.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.ENF.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.INS.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.MAX.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.MCO.getCodigo()) ||
					objFuncaoProfissional.toString().equalsIgnoreCase(DominioFuncaoProfissional.MPF.getCodigo())){
				criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.valueOf(objFuncaoProfissional.toString().toUpperCase())));
				criteria.addOrder(Order.asc("PEF." + RapPessoasFisicas.Fields.NOME.toString()));
			} else {
				criteria.add(Restrictions.ilike("PEF." + RapPessoasFisicas.Fields.NOME.toString(), objFuncaoProfissional.toString(), MatchMode.ANYWHERE));
				criteria.addOrder(Order.asc("PUC." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()));
			}
		} else {
			criteria.addOrder(Order.asc("PEF." + RapPessoasFisicas.Fields.NOME.toString()));
		}
		
		criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in("PUC." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), new DominioFuncaoProfissional[]{DominioFuncaoProfissional.ANP, 
																																		DominioFuncaoProfissional.ANC,
																																		DominioFuncaoProfissional.ANR,
																																		DominioFuncaoProfissional.CIR,
																																		DominioFuncaoProfissional.ENF,
																																		DominioFuncaoProfissional.INS,
																																		DominioFuncaoProfissional.MAX,
																																		DominioFuncaoProfissional.MCO,
																																		DominioFuncaoProfissional.MPF}));
		return criteria;
	}
	
	
	public List<MbcProfAtuaUnidCirgs> pesquisarFuncaoProfissionalEscala(Object objFuncaoProfissional, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = this.gerarCriteriaPesquisarFuncaoProfissionalEscala(objFuncaoProfissional, unidadeFuncional);
		return executeCriteria(criteria);
	}

	public List<MbcProfCirurgias> pesquisarRelatorioPacientesEntrevistar(RapServidores servidorAnest, Date dataCirurgia, Short unfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCI");
		criteria.createAlias("PCI." + MbcProfCirurgias.Fields.UNID_CIRG.toString(), "PUC");
		criteria.createAlias("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("PCI." + MbcProfCirurgias.Fields.CIRURGIA.toString(), "CRG");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		final DominioFuncaoProfissional[] funcoes = { DominioFuncaoProfissional.ANC, DominioFuncaoProfissional.ANR, DominioFuncaoProfissional.ANP };
		criteria.add(Restrictions.in("PUC." + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), funcoes));
		criteria.add(Restrictions.eq("PUC." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), servidorAnest));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		/**
		 * Ordenar por
		 */
		criteria.addOrder(Order.asc("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()));
		criteria.addOrder(Order.asc("PUC." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()));
		criteria.addOrder(Order.asc("PES." + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("CRG." + MbcCirurgias.Fields.NUMERO_AGENDA.toString()));
		return executeCriteria(criteria);	
	}	
	
	public List<RelatorioProfissionaisUnidadeCirurgicaVO> listarProfissionaisPorUnidadeCirurgica(Short seqUnidadeCirurgica, 
			Boolean ativosInativos, Short espSeq, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica ordenacao) {
		/**
		 * Este método está utilizando SQL nativo devido a necessidade de fazer LEFT JOIN com tabelas que não possuem relacionamento.
		 */
		String schema = MbcProfAtuaUnidCirgs.class.getAnnotation(Table.class).schema() + ".";
		StringBuilder sqlQuery = new StringBuilder(800);
		
		if (ordenacao.equals(DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.NOME)) {
			sqlQuery.append(" SELECT PES.NOME  as ordem \n");
		} else if (ordenacao.equals(DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.CODIGO)) {
			sqlQuery.append(" SELECT PUC.SER_MATRICULA || ',' || PUC.SER_VIN_CODIGO  as ordem \n");
		} else if (ordenacao.equals(DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.FUNCAO)) {
			sqlQuery.append(" SELECT REF.RV_MEANING || ',' || PES.NOME as ordem \n");
		} 
		sqlQuery.append(", REF.RV_MEANING \n");
		sqlQuery.append(", PES.NOME \n");
		sqlQuery.append(", PUC.SER_MATRICULA \n");
		sqlQuery.append(", PUC.SER_VIN_CODIGO \n");
		sqlQuery.append(", ESP.SIGLA \n");
		sqlQuery.append(" FROM "+schema+MbcRefCode.class.getAnnotation(Table.class).name()+" REF \n");
		sqlQuery.append(" ,"+schema+RapPessoasFisicas.class.getAnnotation(Table.class).name()+" PES \n");
		sqlQuery.append(" ,"+schema+RapServidores.class.getAnnotation(Table.class).name()+" SER \n");
		sqlQuery.append(" ,"+schema+AghUnidadesFuncionais.class.getAnnotation(Table.class).name()+" UNF \n");
		sqlQuery.append(" ,"+schema+MbcProfAtuaUnidCirgs.class.getAnnotation(Table.class).name()+" PUC \n");
		sqlQuery.append(" LEFT JOIN "+schema+AghProfEspecialidades.class.getAnnotation(Table.class).name()+" PRE \n");
		sqlQuery.append(" ON PRE.SER_MATRICULA = PUC.SER_MATRICULA \n");
		sqlQuery.append(" AND PRE.SER_VIN_CODIGO = PUC.SER_VIN_CODIGO \n");
		sqlQuery.append(" LEFT JOIN "+schema+AghEspecialidades.class.getAnnotation(Table.class).name()+" ESP \n");
		sqlQuery.append(" ON PRE.ESP_SEQ = ESP.SEQ \n");
		sqlQuery.append(" WHERE UNF.SEQ = " + seqUnidadeCirurgica + " \n");
		if (espSeq != null) {
			sqlQuery.append(" AND ESP.SEQ = " + espSeq + " \n");
		}
		sqlQuery.append(" AND  (UNF.IND_SIT_UNID_FUNC = 'A' AND \n");
		sqlQuery.append(" UNF.SEQ IN (SELECT CUF.UNF_SEQ \n");
		sqlQuery.append(" FROM "+schema+AghCaractUnidFuncionais.class.getAnnotation(Table.class).name()+" CUF \n");
		sqlQuery.append(" WHERE CUF.CARACTERISTICA = 'Unid Executora Cirurgias')) \n");
		sqlQuery.append(" AND PUC.UNF_SEQ = UNF.SEQ \n");
		if (ativosInativos.equals(Boolean.TRUE)) {
			sqlQuery.append(" AND PUC.SITUACAO = UPPER('A') \n");
		} else {
			sqlQuery.append(" AND PUC.SITUACAO = UPPER('I') \n");
		}
		sqlQuery.append(" AND PUC.SER_VIN_CODIGO = SER.VIN_CODIGO \n");
		sqlQuery.append(" AND PUC.SER_MATRICULA = SER.MATRICULA \n");
		sqlQuery.append(" AND PES.CODIGO = SER.PES_CODIGO \n");
		sqlQuery.append(" AND REF.RV_ABBREVIATION = PUC.IND_FUNCAO_PROF \n");
		sqlQuery.append(" ORDER BY 1 ");
		
		javax.persistence.Query query = this.createNativeQuery(sqlQuery.toString());
		List<Object[]> camposList = query.getResultList();
		List<RelatorioProfissionaisUnidadeCirurgicaVO> listaVo = new ArrayList<RelatorioProfissionaisUnidadeCirurgicaVO>();
		if(camposList != null && camposList.size()>0) {
			for(Object[] campo : camposList) {
				RelatorioProfissionaisUnidadeCirurgicaVO vo = new RelatorioProfissionaisUnidadeCirurgicaVO();
				vo.setOrdem((String)campo[0]);
				vo.setFuncao((String)campo[1]);
				vo.setNome((String)campo[2]);
				vo.setSerMatricula(Integer.valueOf((String)campo[3].toString()));
				vo.setSerCodigo(Short.valueOf((String)campo[4].toString()));
				vo.setSigla((String)campo[5]);
				listaVo.add(vo);
			}
		}
		return listaVo;
	}

	public List<MbcProfAtuaUnidCirgs> listarProfissionaisUnidCirFiltro(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional,
			Integer matricula, Short vinCodigo,
			String nome, DominioFuncaoProfissional funcaoProfiss, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class,"pau");
		
		criteria.createAlias("pau."+MbcProfAtuaUnidCirgs.Fields.UNIDADE_FUNCIONAL.toString(), "unid" , JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pau."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes" , JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pau."+MbcProfAtuaUnidCirgs.Fields.PROF_ATUA_UNID_CIRGS.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pac."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "prof", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("prof."+RapServidores.Fields.PESSOA_FISICA.toString(), "prof_pes" , JoinType.LEFT_OUTER_JOIN);
		
		if(nome != null && !nome.equals("")){
			//criteria.createAlias("pau."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
			//criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		if(unidadeFuncional != null && unidadeFuncional.getSeq() != null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		if(matricula!= null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), matricula));
		}
		if(vinCodigo!= null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), vinCodigo));
		}
		if(funcaoProfiss != null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), funcaoProfiss));
		}
		if(situacao!= null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc("pau."+MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString()));
		criteria.addOrder(Order.asc("pau."+MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString()));
		criteria.addOrder(Order.asc("pau."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()));

		return executeCriteria(criteria,firstResult, maxResult, null, asc);
	}

	public Long listarProfissionaisUnidCirFiltroCount(
			AghUnidadesFuncionais unidadeFuncional, Integer matricula,
			Short vinCodigo, String nome, DominioFuncaoProfissional funcaoProfiss,
			DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class,"pau");

		if(nome != null && !nome.equals("")){
			criteria.createAlias("pau."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
			criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
			criteria.add(Restrictions.ilike("pes." + RapPessoasFisicas.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		if(unidadeFuncional != null && unidadeFuncional.getSeq() != null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		if(matricula!= null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString(), matricula));
		}
		if(vinCodigo!= null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString(), vinCodigo));
		}
		if(funcaoProfiss != null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString(), funcaoProfiss));
		}
		if(situacao!= null){
			criteria.add(Restrictions.eq("pau."+MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), situacao));
		}
		return executeCriteriaCount(criteria);
	}

	public MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgsId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class,"PAUC");
		
		criteria.createAlias("PAUC."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "RAP");
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS");
		
		criteria.add(Restrictions.eq("PAUC."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ, id.getUnfSeq()));
		criteria.add(Restrictions.eq("PAUC."+MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO, id.getSerVinCodigo()));
		criteria.add(Restrictions.eq("PAUC."+MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA, id.getSerMatricula()));
		criteria.add(Restrictions.eq("PAUC."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF, id.getIndFuncaoProf()));
		
		MbcProfAtuaUnidCirgs obj = (MbcProfAtuaUnidCirgs) executeCriteriaUniqueResult(criteria);
		super.initialize(obj.getRapServidores());
		super.initialize(obj.getRapServidores().getPessoaFisica().getNome());
		return obj;
	}
	
	public List<MbcEquipeVO> obterProfissionaisAtuamUnidCirurgica(String filtro){
		DetachedCriteria criteria = obterCriteriaProfissionaisAtuamUnidCirurgia(filtro);
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long obterProfissionaisAtuamUnidCirurgicaCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaProfissionaisAtuamUnidCirurgia(filtro);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria obterCriteriaProfissionaisAtuamUnidCirurgia(Object filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "rap", JoinType.INNER_JOIN);
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("puc." + MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA), MbcEquipeVO.Fields.MATRICULA.toString());
		projections.add(Projections.property("puc." + MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO), MbcEquipeVO.Fields.VIN_CODIGO.toString());
		projections.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME), MbcEquipeVO.Fields.NOME.toString());

		criteria.setProjection(Projections.distinct(projections));

		criteria.add(Restrictions.eq("puc." + MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
		getCriteriaPesquisaByMatriculaOrNomePessoa(filtro, criteria);
		criteria.setResultTransformer(Transformers.aliasToBean(MbcEquipeVO.class));
		return criteria;
	}
	
	public List<MbcProfAtuaUnidCirgsVO> obterMbcProfAtuaUnidCirgs(Integer equipeSeq, Short unfSeq){
//		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfAtuaUnidCirgs.class, "PUC");
//		criteria.createAlias("PUC."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "RAP");
//		criteria.createAlias("RAP."+RapServidores.Fields.EQUIPES.toString(), "EQP");
//		
//		criteria.add(Restrictions.eq("EQP."+AghEquipes.Fields.SEQ.toString(), equipeSeq));
//		criteria.add(Restrictions.eq("PUC."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString(), unfSeq));
//		criteria.add(Restrictions.eq("PUC."+MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), DominioSituacao.A));
//		criteria.addOrder(Order.desc("PUC."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()));
//
//		return executeCriteria(criteria);
		
		StringBuilder sql = new StringBuilder(400);
		sql.append(" SELECT PUC.SER_MATRICULA as matricula, ")
			.append(" PUC.SER_VIN_CODIGO as vinCodigo, ")
			.append(" PUC.IND_FUNCAO_PROF as indFuncao ")
			.append(" FROM agh.MBC_PROF_ATUA_UNID_CIRGS PUC, ")
			.append(" agh.AGH_EQUIPES EQP ")
			.append(" WHERE EQP.SEQ = "+equipeSeq+" ")
			.append(" AND PUC.SER_MATRICULA = EQP.SER_MATRICULA ")
			.append(" AND PUC.SER_VIN_CODIGO = EQP.SER_VIN_CODIGO ")
			.append(" AND PUC.UNF_SEQ = "+unfSeq+" ")
			.append(" AND PUC.SITUACAO = 'A' ")
			.append(" ORDER BY PUC.IND_FUNCAO_PROF DESC");
		
		SQLQuery query =  createSQLQuery(sql.toString());
		
		final List<MbcProfAtuaUnidCirgsVO> vos = query.addScalar("matricula", IntegerType.INSTANCE)
													.addScalar("vinCodigo", ShortType.INSTANCE)
													.addScalar("indFuncao", StringType.INSTANCE)
				 .setResultTransformer(Transformers.aliasToBean(MbcProfAtuaUnidCirgsVO.class)).list();

		return vos;

	}
}