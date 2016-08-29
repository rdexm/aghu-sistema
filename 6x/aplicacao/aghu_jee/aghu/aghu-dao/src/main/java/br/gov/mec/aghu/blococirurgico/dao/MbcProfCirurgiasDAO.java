package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaProcedProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEscalaCirurgiaAghuResponsavelVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaConsultaVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class MbcProfCirurgiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProfCirurgias> {
    @Inject
    private MbcAgendasDAO aMbcAgendasDAO;

	private static final long serialVersionUID = -4537155418120986971L;
	
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionEquipe(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final boolean indResponsavel){
		List<SuggestionListaCirurgiaVO> lista = listaSuggestionEquipe(unidade, dtProcedimento, filtro, indResponsavel);
		CoreUtil.ordenarLista(lista,SuggestionListaCirurgiaVO.Fields.NOME.toString(),true);
		return lista; 
	}
	
	public Long pesquisarSuggestionEquipeCount(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final boolean indResponsavel){
		List<SuggestionListaCirurgiaVO> lista = listaSuggestionEquipe(unidade, dtProcedimento, filtro, indResponsavel);
		return (long) lista.size();
	}

	private List<SuggestionListaCirurgiaVO> listaSuggestionEquipe(final AghUnidadesFuncionais unidade, final Date dtProcedimento, final String filtro, final boolean indResponsavel) {
		final StringBuffer hql = new StringBuffer(410);

		hql.append("SELECT ")
			.append(" DISTINCT COALESCE(PES.").append(RapPessoasFisicas.Fields.NOME_USUAL.toString())
			.append(",PES.").append(RapPessoasFisicas.Fields.NOME.toString())
			.append(" ) as ").append(SuggestionListaCirurgiaVO.Fields.NOME.toString())			
			.append(" , SER.").append(RapServidores.Fields.MATRICULA.toString()).append(" as ").append(SuggestionListaCirurgiaVO.Fields.MATRICULA.toString())
			.append(" , SER.").append(RapServidores.Fields.VIN_CODIGO.toString()).append(" as ").append(SuggestionListaCirurgiaVO.Fields.VIN_CODIGO.toString())
			.append(" FROM ")
			.append(MbcProfCirurgias.class.getSimpleName()).append(" PCG ")
			.append(" INNER JOIN PCG.").append(MbcProfCirurgias.Fields.CIRURGIA.toString()).append(" CRG ")
			.append(" INNER JOIN PCG.").append(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString()).append(" SER ")
			.append(" INNER JOIN SER.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append(" PES ")
			.append(" where CRG.").append(MbcCirurgias.Fields.DATA.toString()).append(" = :PRM_DATA ")
			.append(" 	AND CRG.").append(MbcCirurgias.Fields.UNF_SEQ.toString()).append(" = :PRM_UNF_SEQ ")	
			.append(" 	AND PCG.").append(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString()).append(" = :PRM_IND_RESPONSAVEL ");		
		   
		if(StringUtils.isNotEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				hql.append(" AND (SER.").append(RapServidores.Fields.MATRICULA.toString()).append(" = :PRM_MATRICULA OR")
				   .append("      SER.").append(RapServidores.Fields.VIN_CODIGO.toString()).append(" = :PRM_VIN_CODIGO) ");
				
			} else {
				hql.append(" AND (LOWER(PES.").append(RapPessoasFisicas.Fields.NOME_USUAL.toString()).append(") LIKE :PRM_NOME_USUAL OR")
				   .append("      LOWER(PES.").append(RapPessoasFisicas.Fields.NOME.toString()).append(") LIKE :PRM_NOME) ");
			}
		}
		Query query = createHibernateQuery(hql.toString());
		if(StringUtils.isNotEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				query.setParameter("PRM_MATRICULA", Integer.valueOf(filtro));
				query.setParameter("PRM_VIN_CODIGO", Short.valueOf(filtro));
			} else {
				query.setParameter("PRM_NOME_USUAL", "%"+filtro.toLowerCase()+"%");
				query.setParameter("PRM_NOME", "%"+filtro.toLowerCase()+"%");
			}
		}
		query.setParameter("PRM_DATA", dtProcedimento);
		query.setParameter("PRM_UNF_SEQ", unidade.getSeq());
		query.setParameter("PRM_IND_RESPONSAVEL", indResponsavel);		
		query.setResultTransformer(Transformers.aliasToBean(SuggestionListaCirurgiaVO.class));
		List<SuggestionListaCirurgiaVO> result = query.list();
		return result;
	}

	public List<String> obterNomeEquipeCirurgica(final Integer grcSeq){
		final StringBuffer hql = new StringBuffer(410);
		hql.append("SELECT ")
		   .append(" COALESCE(PES.").append(RapPessoasFisicas.Fields.NOME_USUAL.toString())
		   					  .append(",PES.").append(RapPessoasFisicas.Fields.NOME.toString())
		   					.append(" )  ")
		   .append(" FROM ")
		   .append(MbcProfCirurgias.class.getSimpleName()).append(" PCG ")
		   .append(" INNER JOIN PCG.").append(MbcProfCirurgias.Fields.CIRURGIA.toString()).append(" CRG ")
		   .append(" INNER JOIN PCG.").append(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString()).append(" SER ")
		   .append(" INNER JOIN SER.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append(" PES ");
		hql.append(" where PCG.").append(MbcProfCirurgias.Fields.CRG_SEQ.toString()).append(" = :PRM_CRG_SEQ ");	
		hql.append(" 	AND PCG.").append(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString()).append(" = :PRM_IND_RESPONSAVEL ");		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("PRM_CRG_SEQ", grcSeq);
		query.setParameter("PRM_IND_RESPONSAVEL", Boolean.TRUE);
		return query.list();
	}
	/**
	 * Implementa o cursor <code>c_get_cbo_cirg</code>
	 * 
	 * @param crgSeq
	 * @param resp
	 * @return
	 */
	public RapServidores buscaRapServidor(Integer crgSeq, DominioSimNao resp){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		if (resp != null && resp.equals(DominioSimNao.S)) {
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		} else if (resp != null && resp.equals(DominioSimNao.N)) {
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.FALSE));
		}
		criteria.addOrder(Order.asc(MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()));
		List<MbcProfCirurgias> result = executeCriteria(criteria, 0, 1, null, false);
		if(result != null && !result.isEmpty()){
			return result.get(0).getServidorPuc();
		}
		return null;
	}
	
	/**
	 * Implementa o cursor <code>c_get_cbo_cirg</code>
	 * OBS.: Tem o mesmo nome do cursor acima, mas é diferente
	 * 
	 * @param crgSeq
	 * @param resp
	 * @return
	 */
	public MbcProfCirurgias buscaRapServidorPorCrgSeqEIndResponsavel(Integer crgSeq, DominioSimNao resp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.createAlias(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), MbcProfCirurgias.Fields.SERVIDOR_PUC.toString());
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString() , crgSeq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), resp.isSim()));
		return (MbcProfCirurgias) executeCriteriaUniqueResult(criteria);
	}
		
	/**
	 * Implementa o cursor <code>c_get_cbo_anest</code>
	 * 
	 * @param crgSeq
	 * @param funcoes
	 * @return
	 */
	public RapServidores buscaRapServidor(Integer crgSeq, DominioFuncaoProfissional[] funcoes){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), funcoes));
		criteria.addOrder(Order.asc(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()));
		List<MbcProfCirurgias> result = executeCriteria(criteria);
		Collections.sort(result, new Comparator<MbcProfCirurgias>() {
			@Override
			public int compare(final MbcProfCirurgias o1, final MbcProfCirurgias o2) {
				if(!o1.getFuncaoProfissional().equals(o2.getFuncaoProfissional())){
					if(DominioFuncaoProfissional.ANP.equals(o1.getFuncaoProfissional())){
						return -1;
					} else if(DominioFuncaoProfissional.ANP.equals(o2.getFuncaoProfissional())){
						return 1;
					} else if(DominioFuncaoProfissional.ANC.equals(o1.getFuncaoProfissional())){
						return -1;
					} else if(DominioFuncaoProfissional.ANC.equals(o2.getFuncaoProfissional())){
						return 1;
					} else {
						return 0;
					}
				}				
				return 0;
			}
		});
		if(result != null && !result.isEmpty()){
			return result.get(0).getServidorPuc();
		}
		return null;
	}
	
	public List<Object[]> pesquisaCirurgiao(Integer seq, String stringSeparator) {
		DetachedCriteria criteria = obterCriteriaPrincipalEquipe(stringSeparator);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA
						.toString()
						+ stringSeparator
						+ RapPessoasFisicas.Fields.NOME.toString()))
				.add(Projections.property(RapServidores.Fields.PESSOA_FISICA
						.toString()
						+ stringSeparator
						+ RapPessoasFisicas.Fields.NOME_USUAL.toString())));
		ArrayList<DominioFuncaoProfissional> listaFuncaoProf = new ArrayList<DominioFuncaoProfissional>();
		listaFuncaoProf.add(DominioFuncaoProfissional.MCO);
		listaFuncaoProf.add(DominioFuncaoProfissional.MPF);
		listaFuncaoProf.add(DominioFuncaoProfissional.MAX);
		criteria.add(Restrictions.eq(
				MbcProfCirurgias.Fields.CIRURGIA.toString() + stringSeparator
						+ MbcCirurgias.Fields.SEQ, seq));
		criteria.add(Restrictions.in(
				MbcProfCirurgias.Fields.UNID_CIRG.toString()
						+ stringSeparator
						+ MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF
								.toString(), listaFuncaoProf));

		criteria.addOrder(Order.desc(MbcProfCirurgias.Fields.IND_RESPONSAVEL
				.toString()));
		criteria.addOrder(Order.asc(RapServidores.Fields.PESSOA_FISICA
				.toString()
				+ stringSeparator
				+ RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * Monta a consulta de acordo com o tipoEquipe.
	 * Foi passado apenas 1 valor de {@link DominioFuncaoProfissional} para identificar cada equipe.
	 * 
	 * @param crgSeq
	 * @param tipoEquipe
	 * @return
	 */
	public List<Object[]> obterEquipePorCrgSeqAgrupado(Integer crgSeq, DominioFuncaoProfissional tipoEquipe) {
		String stringSeparador = ".";
		DetachedCriteria criteria = obterCriteriaPrincipalEquipe(stringSeparador);
		
		StringBuilder sqlProjection1 = new StringBuilder(30);
		StringBuilder sqlProjection2 = new StringBuilder(30);
		StringBuilder sqlProjection3 = new StringBuilder(30);
		if (tipoEquipe.equals(DominioFuncaoProfissional.MPF)) {
			sqlProjection1.append(" MAX(CASE WHEN IND_FUNCAO_PROF = 'MPF' THEN                     ");
			formataCodigoMatriculaNome(sqlProjection1);
			sqlProjection1.append("     END)            " ).append( DominioFuncaoProfissional.MPF.toString());
			sqlProjection2.append(" MAX(CASE WHEN IND_FUNCAO_PROF = 'MCO' THEN                     ");
			formataCodigoMatriculaNome(sqlProjection2);
			sqlProjection2.append("     END)            " ).append( DominioFuncaoProfissional.MCO.toString());
			sqlProjection3.append(" MAX(CASE WHEN IND_FUNCAO_PROF = 'MAX' THEN                     ");
			formataCodigoMatriculaNome(sqlProjection3);
			sqlProjection3.append("     END)            " ).append( DominioFuncaoProfissional.MAX.toString());
		} else if (tipoEquipe.equals(DominioFuncaoProfissional.ANP)) {
			sqlProjection1.append(" MAX(CASE WHEN IND_FUNCAO_PROF = 'ANP' THEN                     ");
			formataCodigoMatriculaNome(sqlProjection1);
			sqlProjection1.append("     END)            " ).append( DominioFuncaoProfissional.ANP.toString());
			sqlProjection2.append(" MAX(CASE WHEN IND_FUNCAO_PROF = 'ANC' THEN                     ");
			formataCodigoMatriculaNome(sqlProjection2);
			sqlProjection2.append("     END)            " ).append( DominioFuncaoProfissional.ANC.toString());
			sqlProjection3.append(" MAX(CASE WHEN IND_FUNCAO_PROF = 'ANR' THEN                     ");
			formataCodigoMatriculaNome(sqlProjection3);
			sqlProjection3.append("     END)            " ).append( DominioFuncaoProfissional.ANR.toString());
		}
		obterProjectionEquipe(stringSeparador, criteria, sqlProjection1,
				sqlProjection2, sqlProjection3, tipoEquipe);
		obterFiltrosPesquisa(criteria, stringSeparador, crgSeq, tipoEquipe);
		return executeCriteria(criteria);
	}

	/**
	 * @param stringSeparador
	 * @return
	 */
	private DetachedCriteria obterCriteriaPrincipalEquipe(String stringSeparador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.createAlias(MbcProfCirurgias.Fields.UNID_CIRG.toString(),
				MbcProfCirurgias.Fields.UNID_CIRG.toString());
		criteria.createAlias(MbcProfCirurgias.Fields.UNID_CIRG.toString()
				+ stringSeparador + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR,
				MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString());
		criteria.createAlias(MbcProfCirurgias.Fields.UNID_CIRG.toString()
				+ stringSeparador + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR
				+ stringSeparador + RapServidores.Fields.PESSOA_FISICA,
				RapServidores.Fields.PESSOA_FISICA.toString());
		return criteria;
	}

	/**
	 * @param sqlProjection
	 */
	private void formataCodigoMatriculaNome(StringBuilder sqlProjection) {
		sqlProjection.append("     LPAD(TO_CHAR(VIN_CODIGO, 'FM999'),3,'0') ||                ");
		sqlProjection.append("     LPAD(TO_CHAR(MATRICULA, 'FM9999999'),7,'0') || '-' ||      ");
		sqlProjection.append("     CASE WHEN NOME_USUAL IS NULL THEN                          ");
		sqlProjection.append("     SUBSTR(NOME,1,15) ELSE NOME_USUAL END                      ");
	}
	
	/**
	 * @param stringSeparador
	 * @param criteria
	 * @param sqlProjection1
	 * @param sqlProjection2
	 * @param sqlProjection3
	 * @param tipoEquipe
	 */
	private void obterProjectionEquipe(String stringSeparador,
			DetachedCriteria criteria, StringBuilder sqlProjection1,
			StringBuilder sqlProjection2, StringBuilder sqlProjection3, DominioFuncaoProfissional tipoEquipe) {
		Type[] type = new Type[] { StringType.INSTANCE };
		// Cirurgião
		String[] columnAliasMpf = new String[]{DominioFuncaoProfissional.MPF.toString()};
		String[] columnAliasMco = new String[]{DominioFuncaoProfissional.MCO.toString()};
		String[] columnAliasMax = new String[]{DominioFuncaoProfissional.MAX.toString()};
		// Anestesista
		String[] columnAliasAnp = new String[]{DominioFuncaoProfissional.ANP.toString()};
		String[] columnAliasAnc = new String[]{DominioFuncaoProfissional.ANC.toString()};
		String[] columnAliasAnr = new String[]{DominioFuncaoProfissional.ANR.toString()};
		if (tipoEquipe.equals(DominioFuncaoProfissional.MPF)) {
			criteria.setProjection(Projections
					.projectionList()
					.add(Projections.sqlProjection(sqlProjection1.toString(),
							columnAliasMpf, type)) // 0 - String
					.add(Projections.sqlProjection(sqlProjection2.toString(),
							columnAliasMco, type)) // 1 - String
					.add(Projections.sqlProjection(sqlProjection3.toString(),
							columnAliasMax, type)) // 2 - String

					.add(Projections.groupProperty(MbcProfCirurgias.Fields.CIRURGIA
							.toString()
							+ stringSeparador
							+ MbcCirurgias.Fields.SEQ))); // Group by CRG_SEQ
		} else if (tipoEquipe.equals(DominioFuncaoProfissional.ANP)) {
			criteria.setProjection(Projections
					.projectionList()
					.add(Projections.sqlProjection(sqlProjection1.toString(),
							columnAliasAnp, type)) // 0 - String
					.add(Projections.sqlProjection(sqlProjection2.toString(),
							columnAliasAnc, type)) // 1 - String
					.add(Projections.sqlProjection(sqlProjection3.toString(),
							columnAliasAnr, type)) // 2 - String

					.add(Projections.groupProperty(MbcProfCirurgias.Fields.CIRURGIA
							.toString()
							+ stringSeparador
							+ MbcCirurgias.Fields.SEQ))); // Group by CRG_SEQ
			
		} else if (tipoEquipe.equals(DominioFuncaoProfissional.ENF)
				|| tipoEquipe.equals(DominioFuncaoProfissional.CIR)
				|| tipoEquipe.equals(DominioFuncaoProfissional.INS)) {
			
			criteria.setProjection(Projections
					.projectionList()
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()+ stringSeparador
							+ RapServidores.Fields.VIN_CODIGO.toString())) // 0 - Short
					.add(Projections.property(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()+ stringSeparador
							+ RapServidores.Fields.MATRICULA.toString())) // 1 - Integer
					.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()+ stringSeparador
							+ RapPessoasFisicas.Fields.NOME_USUAL.toString())) // 2 - String
					.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()	+ stringSeparador
							+ RapPessoasFisicas.Fields.NOME.toString()))); // 3 - String
		}
	}
	
	/**
	 * 
	 * @param criteria
	 * @param crgSeq
	 * @param tipoEquipe
	 */
	private void obterFiltrosPesquisa (DetachedCriteria criteria, String stringSeparador, 
			Integer crgSeq, DominioFuncaoProfissional tipoEquipe) {
		
		ArrayList<DominioFuncaoProfissional> listaFuncaoProf = new ArrayList<DominioFuncaoProfissional>();
		if (tipoEquipe.equals(DominioFuncaoProfissional.MPF)) {
			listaFuncaoProf.add(DominioFuncaoProfissional.MPF);
			listaFuncaoProf.add(DominioFuncaoProfissional.MCO);
			listaFuncaoProf.add(DominioFuncaoProfissional.MAX);
			
		} else if (tipoEquipe.equals(DominioFuncaoProfissional.ANP)) {
			listaFuncaoProf.add(DominioFuncaoProfissional.ANP);
			listaFuncaoProf.add(DominioFuncaoProfissional.ANC);
			listaFuncaoProf.add(DominioFuncaoProfissional.ANR);
			
		} else if (tipoEquipe.equals(DominioFuncaoProfissional.ENF)
				|| tipoEquipe.equals(DominioFuncaoProfissional.CIR)
				|| tipoEquipe.equals(DominioFuncaoProfissional.INS)) {
			
			criteria.add(Restrictions.eq(
					MbcProfCirurgias.Fields.CIRURGIA.toString() + stringSeparador
					+ MbcCirurgias.Fields.SEQ, crgSeq));
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.UNID_CIRG.toString() + stringSeparador + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF .toString(), tipoEquipe));
		}
		if (!listaFuncaoProf.isEmpty()) {
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CIRURGIA.toString() + stringSeparador + MbcCirurgias.Fields.SEQ, crgSeq));
			criteria.add(Restrictions.in(MbcProfCirurgias.Fields.UNID_CIRG.toString() + stringSeparador + MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString(), listaFuncaoProf));
		}
	}

	public MbcProfCirurgias retornaResponsavelCirurgia(MbcCirurgias cirurgia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.createAlias(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(),"servidorPuc",  JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidorPuc."+RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(),"CCT_LOTACAO", JoinType.LEFT_OUTER_JOIN);
				
				
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CIRURGIA.toString(), cirurgia));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(),Boolean.TRUE));
		
		
		MbcProfCirurgias profCirurgias = (MbcProfCirurgias) executeCriteriaUniqueResult(criteria);
		return profCirurgias;
	}
	
	public MbcProfCirurgias obterEquipePorCirurgia(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.createAlias(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(),"servidorPuc");
		criteria.createAlias("servidorPuc."+RapServidores.Fields.PESSOA_FISICA.toString(),"pessoaFisica");
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		List <MbcProfCirurgias> profCirurgias = executeCriteria(criteria);
		if (!profCirurgias.isEmpty()) {
			return profCirurgias.get(0);			
		}
		return null;
	}
	
	public List <MbcProfCirurgias> obterProfCirurgiasPorCrgSeq(Integer seq,DominioFuncaoProfissional[] funcoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		if(funcoes!=null){
			criteria.add(Restrictions.in(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), funcoes));
		}
		return executeCriteria(criteria);
	}
	
	public MbcProfCirurgias obterEquipePorCirurgiaUnidade(Integer seq, Short unf_seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.createAlias(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(),"servidorPuc");
		criteria.createAlias("servidorPuc."+RapServidores.Fields.PESSOA_FISICA.toString(),"pessoaFisica");
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString(), unf_seq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		List <MbcProfCirurgias> profCirurgias = executeCriteria(criteria);
		if (!profCirurgias.isEmpty()) {
			return profCirurgias.get(0);			
		}
		return null;
	}
	
	/**
	 * Lista profissionais incluidos pela escala menos anestesistas prof e contr
	 */
	public List<MbcProfCirurgias> listarMbcProfCirurgiasControleEscala(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);		
		DominioFuncaoProfissional[] funcoes = {DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANC};
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_INCLUI_ESCALA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.not(Restrictions.in(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), funcoes)));
		return executeCriteria(criteria);
	}
	
	public List<MbcProfCirurgias> listarMbcProfCirurgiasControleEscalaPorCrgSeq(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);		
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_INCLUI_ESCALA.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}
	
	/**
	 *  Busca profissionais da cirurgia por função 
	 */
	public List<MbcProfCirurgias> listarMbcProfCirurgiasPorFuncao(Integer crgSeq, DominioFuncaoProfissional funcao ){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);		
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));		
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), funcao));
		return executeCriteria(criteria);
	}
	
	public List<MbcProfCirurgias> pesquisarProfCirurgiasPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class,"mbc");		
		criteria.createAlias("mbc." + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "ccl");
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		return executeCriteria(criteria);
	}	
	
	public List<MbcProfCirurgias> pesquisarProfCirurgiasAnestesistaPorCrgSeq(Integer crgSeq) {
		DominioFuncaoProfissional[] arrayFuncaoProfissional = {
				DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANR,
				DominioFuncaoProfissional.ANC };
		String aliasSer = "ser";
		String aliasPes = "pes"; 
		String separador = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);		
		criteria.createAlias(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSer);
		criteria.createAlias(aliasSer + separador + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), arrayFuncaoProfissional));
		return executeCriteria(criteria);
	}
	
	//# 24939 C5 C6
	public List<MbcProfCirurgias> listarMbcProfCirurgiasPorCrgSeqFuncaoProfissional(Integer crgSeq, DominioFuncaoProfissional[] listFuncaoProfissional,Boolean indResponsavel){
		DetachedCriteria criteria = getCriteriaMbcProfCirurgiasPorCrgSeqFuncaoProfissional(crgSeq, listFuncaoProfissional,indResponsavel);
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("ser." + RapServidores.Fields.MATRICULA.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MbcProfCirurgias> listarMbcProfCirurgiasPorCrgSeqFuncaoProfissionalOrder(Integer crgSeq, DominioFuncaoProfissional[] listFuncaoProfissional,Boolean indResponsavel){
		DetachedCriteria criteria = getCriteriaMbcProfCirurgiasPorCrgSeqFuncaoProfissional(crgSeq, listFuncaoProfissional,indResponsavel);
		criteria.addOrder(Order.asc("ser." + RapServidores.Fields.VIN_CODIGO.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("ser." + RapServidores.Fields.MATRICULA.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria getCriteriaMbcProfCirurgiasPorCrgSeqFuncaoProfissional(Integer crgSeq, DominioFuncaoProfissional[] listFuncaoProfissional, Boolean indResponsavel) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		criteria.createAlias("pcg." + MbcProfCirurgias.Fields.UNID_CIRG.toString(), "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), listFuncaoProfissional));
		if(indResponsavel!=null){
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), indResponsavel));
		}
		return criteria;
	}
	
	
	/**
	 * Obtém o nome do cirurgião da escala cirúrgica através da cirurgia
	 * @param crgSeq
	 * @param unfSeq
	 * @return
	 */
	public MbcProfCirurgias obterCirurgiaoEscalaCirurgicaPorCirurgia(Integer crgSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");		
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.UNID_CIRG.toString()), "PUC");
		getCriteriaCirurgiaoEscalaCirurgicaPorCirurgia(crgSeq, unfSeq, criteria);
		return (MbcProfCirurgias) executeCriteriaUniqueResult(criteria);
	}
	
	public MbcProfCirurgias pesquisarCirurgiaoEscalaCirurgicaPorCirurgiaPorSituacao(Integer crgSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");		
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.UNID_CIRG.toString()), "PUC");
		getCriteriaCirurgiaoEscalaCirurgicaPorCirurgia(crgSeq, unfSeq, criteria);
		criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString()), DominioSituacao.A));
		return (MbcProfCirurgias) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcProfCirurgias> pesquisarNomeCirurgiaoRelatorioEscalaCirurgiasAghu(Integer crgSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");	
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.UNID_CIRG.toString()), "PUC");
		criteria.createAlias("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "SER");
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES");
		
		DominioFuncaoProfissional[] indFuncaoProf = {DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MAX};
		criteria.add(Restrictions.in("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), indFuncaoProf));
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.CRG_SEQ.toString()), crgSeq));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return executeCriteria(criteria);
	}
	
	
	public List<RelatorioEscalaCirurgiaAghuResponsavelVO> pesquisarMedicosResponsaveisRelatorioEscalaCirurgiasAghu(final Integer crgSeq, final DominioFuncaoProfissional indFuncaoProf) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.UNID_CIRG.toString()), "PUC");
		criteria.createAlias("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "SER");
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("PCG.".concat(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString())), RelatorioEscalaCirurgiaAghuResponsavelVO.Fields.PUC_IND_FUNCAO_PROF.toString());
		p.add(Projections.property("SER.".concat(RapServidores.Fields.VIN_CODIGO.toString())), RelatorioEscalaCirurgiaAghuResponsavelVO.Fields.VIN_CODIGO.toString());
		p.add(Projections.property("SER.".concat(RapServidores.Fields.MATRICULA.toString())), RelatorioEscalaCirurgiaAghuResponsavelVO.Fields.MATRICULA.toString());
		p.add(Projections.property("PES.".concat(RapPessoasFisicas.Fields.NOME.toString())), RelatorioEscalaCirurgiaAghuResponsavelVO.Fields.NOME.toString());
		p.add(Projections.property("PES.".concat(RapPessoasFisicas.Fields.NOME_USUAL.toString())), RelatorioEscalaCirurgiaAghuResponsavelVO.Fields.NOME_USUAL.toString());
		criteria.setProjection(p);
		// ATENÇÃO: O critério que considera um conjunto de funções foi individualizado. Ao invés de conjuntos será considerado uma função por vez.
		criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), indFuncaoProf));
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.CRG_SEQ.toString()), crgSeq));

		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioEscalaCirurgiaAghuResponsavelVO.class));
		return executeCriteria(criteria);

	}
	

	private void getCriteriaCirurgiaoEscalaCirurgicaPorCirurgia(Integer crgSeq, Short unfSeq, DetachedCriteria criteria) {
		criteria.createAlias("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "SER_PUC");		
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.SERVIDOR.toString()), "SER");
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES");
		if(unfSeq != null){
			criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), unfSeq));
		}
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString()), Boolean.TRUE));
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.CRG_SEQ.toString()), crgSeq));
	}
	
	/**
	 * Obtém o nome do anestesista professor escala cirúrgica através da cirurgia
	 * @param crgSeq
	 * @param unfSeq
	 * @return
	 */
	public MbcProfCirurgias obterAnestesistaProfessorEscalaCirurgicaPorCrgSeq(Integer crgSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCG");		
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.MBC_PROF_ATUA_UNID_CIRGS_VINC.toString()), "PUC");
		criteria.createAlias("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "SER");
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES");
		criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), DominioFuncaoProfissional.ANP));
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.CRG_SEQ.toString()), crgSeq));
		return (MbcProfCirurgias) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcProfCirurgias> pesquisarMbcProfCirurgiasByCirurgia(Integer crgSeq) {		
		DetachedCriteria criteria = getCriteriaPesquisarMbcProfCirurgiasByCirurgia(crgSeq);		
		criteria.addOrder(Order.asc("puc." + MbcProfAtuaUnidCirgs.Fields.FUNCAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarMbcProfCirurgiasByCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		criteria.createAlias("pcg." + MbcProfCirurgias.Fields.UNID_CIRG.toString(), "puc");
		criteria.createAlias("puc." + MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		return criteria;
	}	
	
	/**
	 * Busca MbcProfCirurgias por matrícula, código, unidade e função
	 * 
	 * @param matricula
	 * @param codigo
	 * @param unidade
	 * @param funcao
	 * @return
	 */
	public Long pesquisarMbcProfCirurgiasPorMatriculaCodigoUnidFuncaoCount(Integer matricula, Short codigo, Short unidade, DominioFuncaoProfissional funcao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(), funcao));
		return executeCriteriaCount(criteria);
	}
	
	public List<RelatorioCirurgiaProcedProfissionalVO> pesquisarProfPorCodigoPessoaFisicaDthrInicioEDthrFim(
			Integer codigoPessoaFisica, Date dthrInicio, Date dthrFim) {
		String aliasPcg = "pcg";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String aliasCrg = "crg";
		String aliasPpc = "ppc";
		String aliasPac = "pac";
		String aliasPci = "pci";
		String aliasEpc = "epc";
		String aliasEsp = "esp";
		String aliasPfd = "pfd";
		String aliasDdt = "ddt";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, aliasPcg);
		Projection p = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.DATA.toString())
				.add(Projections.property(aliasPcg + ponto + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SER_MATRICULA_PROF.toString())
				.add(Projections.property(aliasPcg + ponto + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SER_VIN_CODIGO_PROF.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.NOME.toString())
				.add(Projections.property(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.IND_PRINCIPAL.toString())
				.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.PROCEDIMENTO.toString())
				.add(Projections.property(aliasPcg + ponto + MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.FUNCAO_PROFISSIONAL.toString())
				.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioCirurgiaProcedProfissionalVO.Fields.SIGLA_ESP.toString());
		criteria.setProjection(p);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.CIRURGIA.toString(), aliasCrg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), aliasEpc);
		criteria.createAlias(aliasEpc + ponto + MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPci);
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), 
				DateUtil.obterDataComHoraInical(dthrInicio), DateUtil.obterDataComHoraFinal(dthrFim)));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPes + ponto + RapPessoasFisicas.Fields.CODIGO.toString(), codigoPessoaFisica));
		DetachedCriteria subCriteriaPfd = DetachedCriteria.forClass(MbcProfDescricoes.class, aliasPfd);
		subCriteriaPfd.setProjection(Projections.property(aliasPfd + ponto + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString()));
		subCriteriaPfd.add(Restrictions.eqProperty(aliasPfd + ponto + MbcProfDescricoes.Fields.DCG_CRG_SEQ.toString(), 
				aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()));
		
		DetachedCriteria subCriteriaDdt = DetachedCriteria.forClass(PdtDescricao.class, aliasDdt);
		subCriteriaDdt.setProjection(Projections.property(aliasDdt + ponto + PdtDescricao.Fields.CRG_SEQ.toString()));
		subCriteriaDdt.add(Restrictions.eqProperty(aliasDdt + ponto + PdtDescricao.Fields.CRG_SEQ.toString(), 
				aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()));		
		criteria.add(Subqueries.notExists(subCriteriaPfd));	
		criteria.add(Subqueries.notExists(subCriteriaDdt));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioCirurgiaProcedProfissionalVO.class));
		return executeCriteria(criteria);
	}

	public List<MbcProfCirurgias> pesquisarResponsavelRealizacaoCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_REALIZOU.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
	}
	
	public List<MbcProfCirurgias> pesquisarProfissionalCirurgiaPorFuncao(Integer crgSeq, DominioFuncaoProfissional[] funcoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(), funcoes));
		return executeCriteria(criteria);
	}
	
	public List<MbcProfCirurgias> pesquisarResponsavelRealizacaoCirurgiaPorFuncao(Integer crgSeq, DominioFuncaoProfissional[] funcoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.IND_REALIZOU.toString(), Boolean.TRUE));
		criteria.add(Restrictions.in(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(), funcoes));
		return executeCriteria(criteria);
	}

	public List<MbcProfCirurgias> pesquisarProfissionalEnfermagem(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.ENF));
		return executeCriteria(criteria);
	}
	
	public List<MbcProfCirurgias> pesquisarProfissionalPorCrgSeqEFuncoes(Integer crgSeq, DominioFuncaoProfissional[] arrayFuncaoProfissional) {
		String aliasSer = "ser";
		String aliasPes = "pes";
		String ponto = ".";
		DetachedCriteria criteria = getCriteriaMbcProfCirurgiasPorCrgSeqFuncaoProfissional(crgSeq, arrayFuncaoProfissional,null);
		criteria.addOrder(Order.asc(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.addOrder(Order.asc(aliasSer + ponto + RapServidores.Fields.MATRICULA.toString()));
		criteria.addOrder(Order.asc(aliasPes + ponto + RapPessoasFisicas.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()));
		return executeCriteria(criteria);
	}

	public List<MbcProfCirurgias> pesquisarMonitorPacientesSalaCirurgia(final Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "PCC");
		criteria.add(Restrictions.eq("PCC." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.createAlias("CRG." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "SLC");
		criteria.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("PCC." + MbcProfCirurgias.Fields.CIRURGIA.toString(), "CRG");
		Date hoje = DateUtil.truncaData(new Date());
		Date ontem = DateUtil.adicionaDias(hoje, -1);
		criteria.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), ontem, hoje));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.TRAN));
		criteria.add(Restrictions.eq("SLC." + MbcSalaCirurgica.Fields.VISIVEL_MONITOR.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));		
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtém o nome do anestesista professor escala cirúrgica através da cirurgia
	 * @param crgSeq
	 * @param unfSeq
	 * @return
	 */
	public List<MbcProfCirurgias> pesquisarProfCirurgiasAnestesistaPorUnfSeqPeriodoCrg(Integer crgSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class);
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.MBC_PROF_ATUA_UNID_CIRGS_VINC.toString()), "PUC");
		criteria.createAlias("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()), "SER");
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES");
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.CIRURGIA.toString()), "CRG");
		criteria.add(Restrictions.eq("CRG.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("PUC.".concat(MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()), DominioFuncaoProfissional.ANP));
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.CRG_SEQ.toString()), crgSeq));
		return executeCriteria(criteria);
	}
	
	public List<PortalPesquisaCirurgiasAgendaEscalaVO> pesquisarAgendasEscalaCirurgiasUnion1(PortalPesquisaCirurgiasParametrosVO parametros, Date diaAtual, Boolean reverse){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class, "agd");
		criteria.createAlias("agd." + MbcAgendas.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.createAlias("agd." + MbcAgendas.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("agd." + MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString(), "prof");
		criteria.createAlias("prof." + MbcProfAtuaUnidCirgs.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("agd." + MbcAgendas.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("agd." + MbcAgendas.Fields.FAT_CONVENIO_SAUDE.toString(), "cnv");
		criteria.createAlias("agd." + MbcAgendas.Fields.SALA_CIRURGICA.toString(), "sci", Criteria.LEFT_JOIN);
		criteria.setProjection(Projections
						.projectionList()
						.add(Projections.property("agd." + MbcAgendas.Fields.SEQ.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.AGD_SEQ.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.DT_AGENDA.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.DATA.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.DTHR_PREV_INICIO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.DT_PREV_INICIO.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.DTHR_PREV_FIM.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.DT_PREV_FIM.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.IND_SITUACAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.SITUACAO_AGENDA.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.SCI_SEQP.toString())
						.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PROCEDIMENTO.toString())
						.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.ESPECIALIDADE.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.PUC_SER_MATRICULA.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PUC_SER_MATRICULA.toString())
						.add(Projections.property("agd." + MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PUC_SER_VIN_CODIGO.toString())
						.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.NOME_PACIENTE.toString())
						.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PRONTUARIO.toString())
						.add(Projections.property("cnv." + FatConvenioSaude.Fields.DESCRICAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.CONVENIO.toString())
						.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.UNIDADE_FUNCIONAL.toString())
		);
		if(diaAtual != null && !reverse){
			criteria.add(Restrictions.ge("agd." + MbcAgendas.Fields.DT_AGENDA.toString(), DateUtil.truncaData(diaAtual)));
		} else if(diaAtual != null && reverse){
			criteria.add(Restrictions.lt("agd." + MbcAgendas.Fields.DT_AGENDA.toString(), DateUtil.truncaData(diaAtual)));
		}
		criteria = getMbcAgendasDAO().createFiltersEquipeAgendamentos(criteria, parametros);
		criteria = getMbcAgendasDAO().createFiltersAgendamentos(criteria, parametros);
		if(parametros.getSala() != null){
			criteria.add(Restrictions.eq("agd." + MbcAgendas.Fields.SALA_CIRURGICA_SEQP.toString(), parametros.getSala()));
		}
		if(parametros.getUnfSeq() != null){
			criteria.add(Restrictions.eq("agd." + MbcAgendas.Fields.UNF_SEQ.toString(), parametros.getUnfSeq()));
		}
		if(parametros.getConvenio() != null){
			criteria.add(Restrictions.eq("cnv." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), parametros.getConvenio()));
		}
		if(parametros.getDataInicio() != null && parametros.getDataFim() != null){
			criteria.add(Restrictions.ge("agd." + MbcAgendas.Fields.DT_AGENDA.toString(), parametros.getDataInicio()));
			criteria.add(Restrictions.le("agd." + MbcAgendas.Fields.DT_AGENDA.toString(), parametros.getDataFim()));
		}
		criteria.add(Restrictions.ne("agd." + MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.LE));
		criteria.add(Restrictions.eq("agd." + MbcAgendas.Fields.IND_EXCLUSAO.toString(), false));
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPesquisaCirurgiasAgendaEscalaVO.class));
		return executeCriteria(criteria);
	}
	
	public List<PortalPesquisaCirurgiasAgendaEscalaVO> pesquisarAgendasEscalaCirurgiasUnion2(PortalPesquisaCirurgiasParametrosVO parametros, Date diaAtual, Short parametrosSistema[], Boolean reverse){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		criteria.createAlias("pcg." + MbcProfCirurgias.Fields.CIRURGIA.toString(), "crg");
		criteria.createAlias("crg." + MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("pcg." + MbcProfCirurgias.Fields.UNID_CIRG.toString(), "prof");
		criteria.createAlias("prof." + MbcProfAtuaUnidCirgs.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("crg." + MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("crg." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "cnv");
		criteria.createAlias("crg." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "sci", Criteria.LEFT_JOIN);
		
		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property("crg." + MbcCirurgias.Fields.DATA.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.DATA.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.SEQ.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.CRG_SEQ.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.AGD_SEQ.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.AGD_SEQ.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.SITUACAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.SITUACAO_CIRURGIA.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.NATUREZA_AGEND.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.NATUREZA_AGENDAMENTO.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.SCI_SEQP.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.SCI_SEQP.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_PREV_INICIO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.DT_PREV_INICIO.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_PREVISAO_FIM.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.DT_PREV_FIM.toString())
						.add(Projections.property("crg." + MbcCirurgias.Fields.UTILIZACAO_SALA.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.UTILIZACAO_SALA.toString())
						.add(Projections.property("pcg." + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PUC_SER_MATRICULA.toString())
						.add(Projections.property("pcg." + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PUC_SER_VIN_CODIGO.toString())
						.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.ESPECIALIDADE.toString())
						.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.NOME_PACIENTE.toString())
						.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.PRONTUARIO.toString())
						.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.UNIDADE_FUNCIONAL.toString())
						.add(Projections.property("cnv." + FatConvenioSaude.Fields.DESCRICAO.toString()), PortalPesquisaCirurgiasAgendaEscalaVO.Fields.CONVENIO.toString()));		

		if(diaAtual != null && !reverse){
			criteria.add(Restrictions.ge("crg." + MbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(diaAtual)));
		} if(diaAtual != null && reverse) {
			criteria.add(Restrictions.lt("crg." + MbcCirurgias.Fields.DATA.toString(), DateUtil.truncaData(diaAtual)));
		}
		if(parametros.getSala() != null){
			criteria.add(Restrictions.eq("crg." +MbcCirurgias.Fields.SCI_SEQP.toString(), parametros.getSala()));
		}
		criteria = createFiltersCirurgia(criteria, parametros);
		criteria = createFiltersEquipeCirurgia(criteria, parametros);
		if(parametros.getConvenio() != null){
			criteria.add(Restrictions.eq("cnv." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), parametros.getConvenio()));
		}
		if(parametros.getDataInicio() != null && parametros.getDataFim() != null){
			criteria.add(Restrictions.ge("crg." + MbcCirurgias.Fields.DATA.toString(), parametros.getDataInicio()));
			criteria.add(Restrictions.le("crg." + MbcCirurgias.Fields.DATA.toString(), parametros.getDataFim()));
		}
		criteria.add(Restrictions.eq("pcg."+ MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), true));
		if(parametros != null){
			criteria.add(Restrictions.or(Restrictions.isNull("crg." + MbcCirurgias.Fields.MTC_SEQ.toString()), 
					Restrictions.not(Restrictions.in("crg." + MbcCirurgias.Fields.MTC_SEQ.toString(), parametrosSistema))));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PortalPesquisaCirurgiasAgendaEscalaVO.class));
		return executeCriteria(criteria);
	}
	
	public List<Date> buscarDatasAgendaEscala(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO, Short parametrosSistema[]){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		criteria.createAlias("pcg." + MbcProfCirurgias.Fields.CIRURGIA.toString(), "crg");
		criteria.createAlias("crg." + MbcCirurgias.Fields.AGENDA.toString(), "agd");
		criteria = this.createFiltersEquipeCirurgia(criteria, portalPesquisaCirurgiasParametrosVO);
		criteria = this.createFiltersCirurgia(criteria, portalPesquisaCirurgiasParametrosVO);
		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.ne("agd."+MbcAgendas.Fields.IND_SITUACAO.toString(), DominioSituacaoAgendas.LE));
		criteria.setProjection(Projections.property("crg."+MbcCirurgias.Fields.DATA.toString()));
		criteria.addOrder(Order.asc("crg."+MbcCirurgias.Fields.DATA.toString()));
		if(parametrosSistema != null){
			criteria.add(Restrictions.or(Restrictions.isNull("crg." + MbcCirurgias.Fields.MTC_SEQ.toString()), 
					Restrictions.not(Restrictions.in("crg." + MbcCirurgias.Fields.MTC_SEQ.toString(), parametrosSistema))));
		}
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria createFiltersEquipeCirurgia(DetachedCriteria criteria, PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO ){
		if (portalPesquisaCirurgiasParametrosVO.getPucSerMatricula() != null &&
				portalPesquisaCirurgiasParametrosVO.getPucSerMatricula() != 0	){
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(),portalPesquisaCirurgiasParametrosVO.getPucSerMatricula()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo() != null &&
				portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo() != 0	){
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(),portalPesquisaCirurgiasParametrosVO.getPucSerVinCodigo()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucUnfSeq() != null &&
				portalPesquisaCirurgiasParametrosVO.getPucUnfSeq() != 0	){
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString(),portalPesquisaCirurgiasParametrosVO.getPucUnfSeq()));
		}
		if (portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf() != null &&
				portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf().getCodigo() != null	){
			criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(),portalPesquisaCirurgiasParametrosVO.getPucIndFuncaoProf()));
		}
		return criteria;
	}
	
	private DetachedCriteria createFiltersCirurgia(DetachedCriteria criteria, PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO ){
		if (portalPesquisaCirurgiasParametrosVO.getUnfSeq() != null &&
				portalPesquisaCirurgiasParametrosVO.getUnfSeq() != 0	){
			criteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.UNF_SEQ.toString(),portalPesquisaCirurgiasParametrosVO.getUnfSeq()));
		}
		//Esp SEQ  
		if (portalPesquisaCirurgiasParametrosVO.getEspSeq() != null &&
				portalPesquisaCirurgiasParametrosVO.getEspSeq() != 0	){
			criteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.ESP_SEQ.toString(),portalPesquisaCirurgiasParametrosVO.getEspSeq()));
		}
		//Paciente 
		if (portalPesquisaCirurgiasParametrosVO.getPacCodigo() != null &&
				portalPesquisaCirurgiasParametrosVO.getPacCodigo() != 0	){
			criteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.PAC_CODIGO.toString(),portalPesquisaCirurgiasParametrosVO.getPacCodigo()));
		}
		return criteria;
	}
	
	private MbcAgendasDAO getMbcAgendasDAO() {
		return aMbcAgendasDAO;
	}
	
	public Long obterQtdAtosAnestesicosExecutados(Short unfSeq, Date dataInicio, Date dataFim, DominioFuncaoProfissional dominioFuncaoProfissional, Short vinCodigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "mpc");
		criteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.ANR));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		subCriteria.setProjection(Projections.property(MbcProfCirurgias.Fields.CRG_SEQ.toString()));
		subCriteria.createAlias(MbcProfCirurgias.Fields.CIRURGIA.toString(), "crg");
		subCriteria.add(Restrictions.ne(MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString(), DominioFuncaoProfissional.ANR));
		subCriteria.add(Restrictions.ne("crg." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		subCriteria.add(Restrictions.between("crg."+MbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim));
		subCriteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		subCriteria.add(Restrictions.eq("pcg." + MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), dominioFuncaoProfissional));
		if(vinCodigo != null && matricula != null){
			subCriteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigo));
			subCriteria.add(Restrictions.eq(MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(), matricula));
		}
		criteria.add(Subqueries.propertyIn("mpc." + MbcProfCirurgias.Fields.CRG_SEQ.toString(), subCriteria));
		return executeCriteriaCount(criteria);
	}
	
	//Utilizado hql pela impossibilidade de resolver o calculo com datas da consulta original
	//SUM((24 * (CRG.DTHR_FIM_ANEST - CRG.DTHR_INICIO_ANEST) * 60)) HORAS_1
	public List<RelatorioProdutividadePorAnestesistaConsultaVO> listarCirurgiasProdutividadePorAnestesista(Short unfSeq, Date dataInicio, Date dataFim, List<DominioFuncaoProfissional> listDominioFuncaoProfissional){
		StringBuffer hql = new StringBuffer(895);
		hql.append("select 	case 	when puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+" = '"+DominioFuncaoProfissional.ANP.toString()+"' then 1 ");
		hql.append("when puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+" = '"+DominioFuncaoProfissional.ANC.toString()+"' then 2 ");
		hql.append("else 3 ");
		hql.append("end as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.ORDEM.toString()+", ");
		hql.append("puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.IND_FUNCAO_PROF.toString()+", ");
		hql.append("ser."+RapServidores.Fields.VIN_CODIGO.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.VIN_CODIGO.toString()+", ");
		hql.append("ser."+RapServidores.Fields.MATRICULA.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.MATRICULA.toString()+", ");
		hql.append("puc."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.PUC_UNF_SEQ.toString()+", ");
		hql.append("pes."+RapPessoasFisicas.Fields.NOME.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.NOME.toString()+", ");
		if(isOracle()){
			hql.append("SUM((crg."+MbcCirurgias.Fields.DATA_FIM_ANESTESIA.toString()+" - crg."+MbcCirurgias.Fields.DATA_INICIO_ANESTESIA.toString()+")*60*24) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_HORA_DOUBLE.toString()+", ");
		}else{
			hql.append("SUM((extract(epoch from crg."+MbcCirurgias.Fields.DATA_FIM_ANESTESIA.toString()+" - crg."+MbcCirurgias.Fields.DATA_INICIO_ANESTESIA.toString()+"))/60) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_HORA.toString()+", ");
		}
		hql.append("count(*) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_CIRURGIA_OBJECT.toString()+" ");
		hql.append("from 	"+MbcProfCirurgias.class.getSimpleName()+" pcg ");
		hql.append("inner join pcg."+MbcProfCirurgias.Fields.CIRURGIA.toString()+" crg ");
		hql.append("inner join pcg."+MbcProfCirurgias.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()+" puc ");
		hql.append("inner join puc."+MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()+" ser ");
		hql.append("inner join ser."+RapServidores.Fields.PESSOA_FISICA.toString()+" pes ");
		hql.append("where 	crg."+MbcCirurgias.Fields.SITUACAO.toString()+" <> 'CANC' ");
		hql.append("and pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+" in (:listDominioFuncaoProfissional) ");
		hql.append("and crg."+MbcCirurgias.Fields.UNF_SEQ.toString()+" = :unfSeq ");
		hql.append("and crg."+MbcCirurgias.Fields.DATA.toString()+" between :dataInicio and :dataFim ");
		hql.append("group by puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+", ");
		hql.append("ser."+RapServidores.Fields.VIN_CODIGO.toString()+", ");
		hql.append("ser."+RapServidores.Fields.MATRICULA.toString()+", ");
		hql.append("puc."+MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString()+", ");
		hql.append("pes."+RapPessoasFisicas.Fields.NOME.toString()+" ");
		hql.append("order by case 	when puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+" = '"+DominioFuncaoProfissional.ANP.toString()+"' then 1 ");
		hql.append("when puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+" = '"+DominioFuncaoProfissional.ANC.toString()+"' then 2 ");
		hql.append("else 3 ");
		hql.append("end, ");
		hql.append("puc."+MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString()+", ");
		hql.append("ser."+RapServidores.Fields.VIN_CODIGO.toString()+", ");
		hql.append("ser."+RapServidores.Fields.MATRICULA.toString()+" ");	
		   
		Query query = createHibernateQuery(hql.toString());
		query.setShort("unfSeq", unfSeq);
		query.setDate("dataInicio", dataInicio);
		query.setDate("dataFim", dataFim);
		query.setParameterList("listDominioFuncaoProfissional", listDominioFuncaoProfissional);

		query.setResultTransformer(Transformers.aliasToBean(RelatorioProdutividadePorAnestesistaConsultaVO.class));
		
		return query.list();
	}
	
	//Utilizado hql pela impossibilidade de resolver um decodo no groupby
	public List<RelatorioProdutividadePorAnestesistaConsultaVO> listarProdutividadeAnestesistaPorCaracteristica(Short unfSeq, Date dataInicio, Date dataFim, DominioFuncaoProfissional dominioFuncaoProfissional, Integer matricula, Short vinCodigo){
		StringBuffer hql = new StringBuffer(1078);
		hql	.append("select pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.IND_FUNCAO_PROF.toString()+", ");
		if(vinCodigo != null && matricula != null){
			hql	.append("pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.VIN_CODIGO.toString()+", ");
			hql	.append("pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.MATRICULA.toString()+", ");
		}
		hql	.append("crg."+MbcCirurgias.Fields.UNF_SEQ.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.PUC_UNF_SEQ.toString()+", ");
		hql	.append("case when crg."+MbcCirurgias.Fields.NATUREZA_AGEND.toString()+" = '"+DominioNaturezaFichaAnestesia.ELE.toString()+"' then ");
		hql	.append("(case 	when crg."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()+" = '"+DominioOrigemPacienteCirurgia.I.toString()+"' then 'CIRURGIAS ELETIVAS PACIENTE HOSPITALIZADO' ");
		hql	.append("when crg."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()+" = '"+DominioOrigemPacienteCirurgia.A.toString()+"' then 'CIRURGIAS ELETIVAS PACIENTE AMBULATÓRIO' ");
		hql	.append("end) ");
		hql	.append("else 'EMERGENCIAS CIRURGICAS' ");
		hql	.append("end as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.DESCRICAO.toString()+", ");
		hql	.append("count(*) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_CIRURGIA_OBJECT.toString()+" ");
		hql	.append("from "+MbcProfCirurgias.class.getSimpleName()+" pcg ");
		hql	.append("inner join pcg."+MbcProfCirurgias.Fields.CIRURGIA.toString()+" crg ");
		hql	.append("where crg."+MbcCirurgias.Fields.SITUACAO.toString()+" <> 'CANC' ");
		hql	.append("and pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+" in (:dominioFuncaoProfissional) ");
		hql	.append("and crg."+MbcCirurgias.Fields.UNF_SEQ.toString()+" = :unfSeq ");
		hql	.append("and crg."+MbcCirurgias.Fields.DATA.toString()+" between :dataInicio and :dataFim ");
		if(vinCodigo != null && matricula != null){
			hql	.append("and pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()+" = :vinCodigo ");
			hql	.append("and pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()+" = :matricula ");
		}
		hql	.append("group by pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+", ");
		if(vinCodigo != null && matricula != null){
			hql	.append("pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()+", ");
			hql	.append("pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()+", ");
		}
		hql	.append("crg."+MbcCirurgias.Fields.UNF_SEQ.toString()+", ");
		hql	.append("case when crg."+MbcCirurgias.Fields.NATUREZA_AGEND.toString()+" = '"+DominioNaturezaFichaAnestesia.ELE.toString()+"' then ");
		hql	.append("(case 	when crg."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()+" = '"+DominioOrigemPacienteCirurgia.I.toString()+"' then 'CIRURGIAS ELETIVAS PACIENTE HOSPITALIZADO' ");
		hql	.append("when crg."+MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString()+" = '"+DominioOrigemPacienteCirurgia.A.toString()+"' then 'CIRURGIAS ELETIVAS PACIENTE AMBULATÓRIO' ");
		hql	.append("end) ");
		hql	.append("else 'EMERGENCIAS CIRURGICAS' ");
		hql	.append("end ");
		   
		Query query = createHibernateQuery(hql.toString());
		query.setShort("unfSeq", unfSeq);
		query.setDate("dataInicio", dataInicio);
		query.setDate("dataFim", dataFim);
		query.setParameter("dominioFuncaoProfissional", dominioFuncaoProfissional);
		if(vinCodigo != null && matricula != null){
			query.setShort("vinCodigo",vinCodigo);
			query.setInteger("matricula", matricula);
		}
		query.setResultTransformer(Transformers.aliasToBean(RelatorioProdutividadePorAnestesistaConsultaVO.class));
		return query.list();
	}
	
	//Utilizado hql pela impossibilidade de resolver o calculo com datas da consulta original
	//SUM((24 * (CRG.DTHR_FIM_ANEST - CRG.DTHR_INICIO_ANEST) * 60)) HORAS_1
	public RelatorioProdutividadePorAnestesistaConsultaVO listarCirurgiasProdutividadeAnestesiaQuantidade(Short unfSeq, Date dataInicio, Date dataFim, DominioFuncaoProfissional dominioFuncaoProfissional){
		StringBuffer hql = new StringBuffer(470);
		hql.append("select pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.IND_FUNCAO_PROF.toString()+", ");
		hql.append("pcg."+MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString()+" as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.PUC_UNF_SEQ.toString()+", ");
		if(isOracle()){
			hql.append("SUM((crg."+MbcCirurgias.Fields.DATA_FIM_ANESTESIA.toString()+" - crg."+MbcCirurgias.Fields.DATA_INICIO_ANESTESIA.toString()+")*60*24) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_HORA_DOUBLE.toString()+", ");
		}else{
			hql.append("SUM((extract(epoch from crg."+MbcCirurgias.Fields.DATA_FIM_ANESTESIA.toString()+" - crg."+MbcCirurgias.Fields.DATA_INICIO_ANESTESIA.toString()+"))/60) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_HORA.toString()+", ");
		}
		hql.append("count(*) as "+RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QTD_CIRURGIA_OBJECT.toString()+" ");
		hql.append("from "+MbcProfCirurgias.class.getSimpleName()+" pcg ");
		hql.append("inner join pcg."+MbcProfCirurgias.Fields.CIRURGIA.toString()+" crg ");
		hql.append("where 	crg."+MbcCirurgias.Fields.SITUACAO.toString()+" <> 'CANC' ");
		hql.append("and pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+" in ('ANP','ANC','ANR') ");
		hql.append("and pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+" = :dominioFuncaoProfissional ");
		hql.append("and crg."+MbcCirurgias.Fields.UNF_SEQ.toString()+" = :unfSeq ");
		hql.append("and crg."+MbcCirurgias.Fields.DATA.toString()+" between :dataInicio and :dataFim ");
		hql.append("group by pcg."+MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF.toString()+", ");
		hql.append("pcg."+MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString()+" ");	
		   
		Query query = createHibernateQuery(hql.toString());
		query.setShort("unfSeq", unfSeq);
		query.setDate("dataInicio", dataInicio);
		query.setDate("dataFim", dataFim);
		query.setParameter("dominioFuncaoProfissional", dominioFuncaoProfissional);

		query.setResultTransformer(Transformers.aliasToBean(RelatorioProdutividadePorAnestesistaConsultaVO.class));
		
		List<RelatorioProdutividadePorAnestesistaConsultaVO> listaResultado = query.list();
		return listaResultado.get(0);
	}
	
	public List<RelatorioProdutividadePorAnestesistaConsultaVO> listarCirurgiasProdutividadeAnestesiaEspecialidade(Short unfSeq, Date dataInicio, Date dataFim, DominioFuncaoProfissional dominioFuncaoProfissional, Integer matricula, Short vinCodigo){
		ProjectionList projection = Projections.projectionList()
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.IND_FUNCAO_PROF.toString())
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_UNF_SEQ.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.PUC_UNF_SEQ.toString())
			.add(Projections.groupProperty("esp."+AghEspecialidades.Fields.SIGLA.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.ESP_SIGLA.toString())
			.add(Projections.groupProperty("esp."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.NOME.toString())
			.add(Projections.rowCount(), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.QUANTIDADE.toString());
		if(matricula != null && vinCodigo!= null){
			projection.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.VIN_CODIGO.toString())
			.add(Projections.groupProperty("pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), RelatorioProdutividadePorAnestesistaConsultaVO.Fields.MATRICULA.toString());
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		criteria.createAlias("pcg."+MbcProfCirurgias.Fields.CIRURGIA.toString(), "crg");
		criteria.createAlias("crg."+MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");
 		criteria.add(Restrictions.ne("crg."+MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
 		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.FUNCAO_PROFISSIONAL.toString(), dominioFuncaoProfissional));
 		criteria.add(Restrictions.eq("crg."+MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
 		criteria.add(Restrictions.between("crg." + MbcCirurgias.Fields.DATA.toString(), dataInicio, dataFim));
 		if(matricula != null && vinCodigo!= null){
	 		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString(), matricula));
	 		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigo));
 		}
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioProdutividadePorAnestesistaConsultaVO.class));
 		return executeCriteria(criteria);
	}
	
	public MbcProfCirurgias obterOriginalCompleto(MbcProfCirurgiasId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProfCirurgias.class, "pcg");
		criteria.createAlias("pcg." + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), "spuc");
		criteria.createAlias("spuc." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "ccl");
		criteria.add(Restrictions.eq("pcg." + MbcProfCirurgias.Fields.ID.toString(),id));
		return (MbcProfCirurgias) executeCriteriaUniqueResult(criteria);
	}
}