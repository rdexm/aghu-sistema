package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.faturamento.dao.AghCidObterDescricaoPorCodigoQueryBuilder;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSinonimoCid;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatEspecialidadeTratamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcCidUsualEquipe;
import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.VFatAssocProcCids;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.vo.DescricaoCodigoComplementoCidVO;
import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class AghCidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCid> {

	private static final long serialVersionUID = -3115380824021713432L;
	
	private static final String CPT_PONTO = "CPT.";
	private static final String CID_PONTO = "CID."; 
	
	

	@SuppressWarnings("unchecked")
	public String pesquisarDescricaoCidPrincipal(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		StringBuffer hql = new StringBuffer(180);
		hql.append(" select '(' || cid.");
		hql.append(AghCid.Fields.CODIGO.toString());
		hql.append(" || ') ' || cid.");
		hql.append(AghCid.Fields.DESCRICAO.toString());
		hql.append(' ');
		hql.append(" from MpmAltaDiagPrincipal adp ");
		hql.append(" 	join adp.");
		hql.append(MpmAltaDiagPrincipal.Fields.CID.toString());
		hql.append(" cid ");
		hql.append(" where adp.");
		hql.append(MpmAltaDiagPrincipal.Fields.APA_ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and adp.");
		hql.append(MpmAltaDiagPrincipal.Fields.APA_SEQ.toString());
		hql.append(" = :apaSeq ");
		hql.append(" 	and adp.");
		hql.append(MpmAltaDiagPrincipal.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" 	and adp.");
		hql.append(MpmAltaDiagPrincipal.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao ");

		Query query = createQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("apaSeq", apaSeq);
		query.setParameter("seqp", seqp);
		query.setParameter("situacao", DominioSituacao.A);

		List<String> descricoes = query.getResultList();

		String descricao = null;
		if (descricoes != null && !descricoes.isEmpty()) {
			descricao = descricoes.get(0);
		}

		return descricao;
	}

	@SuppressWarnings("unchecked")
	public List<String> pesquisarDescricaoCidSecundario(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		StringBuffer hql = new StringBuffer(180);
		hql.append(" select '(' || cid.");
		hql.append(AghCid.Fields.CODIGO.toString());
		hql.append(" || ') ' || cid.");
		hql.append(AghCid.Fields.DESCRICAO.toString());
		hql.append(' ');
		hql.append(" from MpmAltaDiagSecundario ads ");
		hql.append(" 	join ads.");
		hql.append(MpmAltaDiagSecundario.Fields.CID_SEQ.toString());
		hql.append(" cid ");
		hql.append(" where ads.");
		hql.append(MpmAltaDiagSecundario.Fields.ASU_APA_ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and ads.");
		hql.append(MpmAltaDiagSecundario.Fields.ASU_APA_SEQ.toString());
		hql.append(" = :apaSeq ");
		hql.append(" 	and ads.");
		hql.append(MpmAltaDiagSecundario.Fields.ASU_SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" 	and ads.");
		hql.append(MpmAltaDiagSecundario.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao ");

		Query query = createQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("apaSeq", apaSeq);
		query.setParameter("seqp", seqp);
		query.setParameter("situacao", DominioSituacao.A);

		return query.getResultList();
	}

	public List<AghCid> pesquisarPorNomeCodigoAtiva(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param String parametro
	 * @return List<AghCid>
	 */	
	public List<AghCid> pesquisarPorNomeCodigoAtivaPaginado(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, "seq", true);
	}
	
	
	public Long pesquisarPorNomeCodigoAtivaCount(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		return executeCriteriaCount(criteria);
	}
	
		
		
	public AghCid pesquisarPorNomeCodigoAtivaPaginadoUnico(Integer param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.SEQ.toString(), param));
		return (AghCid) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria montarCriteriaParaNomeOuCodigo(String parametro) {
		String descricaoOuCodigo = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.createAlias(AghCid.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghCid.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		if (StringUtils.isNotEmpty(descricaoOuCodigo)) {
			Criterion c1 = Restrictions.ilike(AghCid.Fields.CODIGO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
			Criterion c2 = Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(c1, c2));
		}
		return criteria;
	}

	public String buscaCodigoCidSecundarioConta(Integer cthSeq, DominioPrioridadeCid prioridade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));

		DetachedCriteria subquery = createSubQueryBuscaCidSecundarioConta(cthSeq, prioridade);

		criteria.add(Subqueries.propertyIn(AghCid.Fields.SEQ.toString(), subquery));
		criteria.setProjection(Projections.property(AghCid.Fields.CODIGO.toString()));

		List<String> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private DetachedCriteria createSubQueryBuscaCidSecundarioConta(Integer cthSeq, DominioPrioridadeCid prioridade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCidContaHospitalar.class);

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString(), prioridade));

		criteria.setProjection(Projections.property(FatCidContaHospitalar.Fields.CID_SEQ.toString()));

		return criteria;
	}

	public String buscaPrimeiroCidContaSituacao(Integer seq, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		criteria.add(Restrictions.eq(AghCid.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), situacao));
		
		criteria.setProjection(Projections.property(AghCid.Fields.CODIGO.toString()));

		List<String> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Integer buscaCidPorCodigoComPonto(String codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.CODIGO.toString(), codigo));
		criteria.setProjection(Projections.property(AghCid.Fields.SEQ.toString()));
		List<Integer> result = executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * @param codDesc
	 * @return
	 */
	public List<AghCid> obterCids(String codDesc, boolean filtroSituacaoAtiva) {
		DetachedCriteria criteria = createPesquisaCriteria(codDesc, filtroSituacaoAtiva);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long obterCidsCount(String codDesc, boolean filtroSituacaoAtiva) {
		DetachedCriteria criteria = createPesquisaCriteria(codDesc, filtroSituacaoAtiva);
		return executeCriteriaCount(criteria);
	}	
	
	/**
	 * @param codDesc
	 * @return
	 */
	public AghCid obterCidSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.SEQ.toString(), seq));
		return (AghCid)executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir de parâmetros.
	 * 
	 * @param codigo
	 *            ou descrição.
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaCriteria(String codDesc, boolean filtroSituacaoAtiva) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (StringUtils.isNotBlank(codDesc)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AghCid.Fields.CODIGO.toString(), codDesc, MatchMode.ANYWHERE),
					Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), codDesc, MatchMode.ANYWHERE)));
		}

		if (filtroSituacaoAtiva) {
			criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		}

		return criteria;
	}

	/**
	 * @param codigo
	 * @return
	 */
	public AghCid obterCid(String codigo) {
		DetachedCriteria criteria = createPesquisaPorCodigoCriteria(codigo);

		return (AghCid) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir do codigo. Código é AK
	 * da tabela AGH_CIDS.
	 * 
	 * @param codigo
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaPorCodigoCriteria(String codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (StringUtils.isNotBlank(codigo)) {
			criteria.add(Restrictions.eq(AghCid.Fields.CODIGO.toString(), codigo.toUpperCase()));
		}

		return criteria;
	}

	/**
	 * @param cidCodigo
	 * @return
	 */
	public Long pesquisarProcedimentosParaCidCount(String cidCodigo) {
		return executeCriteriaCount(createPesquisaProcedimentosParaCidCriteria(cidCodigo));
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir de parâmetros.
	 * 
	 * @param codigo
	 * @return DetachedCriteria
	 */
	private DetachedCriteria createPesquisaProcedimentosParaCidCriteria(String codCid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssocProcCids.class);

		if (StringUtils.isNotBlank(codCid)) {
			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.CID_CODIGO.toString(), codCid.toUpperCase()));

			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), (short) 1));

			criteria.setProjection(Projections.projectionList().add(Projections.property(VFatAssocProcCids.Fields.PHI_SEQ.toString()))
					.add(Projections.property(VFatAssocProcCids.Fields.COD_TABELA.toString()))
					.add(Projections.property(VFatAssocProcCids.Fields.IPH_DESCRICAO.toString())));
		}

		return criteria;
	}

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codCid
	 * @return
	 */
	public List<CidVO> pesquisarProcedimentosParaCid(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, String codCid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssocProcCids.class);

		if (StringUtils.isNotBlank(codCid)) {
			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.CID_CODIGO.toString(), codCid.toUpperCase()));
		}

		criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), (short) 1));

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(VFatAssocProcCids.Fields.PHI_SEQ.toString()), "codHcpa")
				.add(Projections.property(VFatAssocProcCids.Fields.COD_TABELA.toString()), "codSus")
				.add(Projections.property(VFatAssocProcCids.Fields.IPH_DESCRICAO.toString()), "descricao"));

		criteria.setResultTransformer(Transformers.aliasToBean(CidVO.class));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * @param seqCid
	 * @return
	 */
	public List<AghCid> pesquisarCidsRelacionados(Integer seqCid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghCid.Fields.CID_SEQ.toString(), seqCid));

		return executeCriteria(criteria);
	}

	/**
	 * @param seqGrupoCid
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorGrupo(Integer seqGrupoCid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghCid.Fields.GCD_SEQ.toString(), seqGrupoCid));
		criteria.add(Restrictions.isNull(AghCid.Fields.CID_SEQ.toString()));
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	public List<AghCid> pesquisarCidsPorDescricaoOuId(String descricao,
			Integer limiteRegistros) {
		return pesquisarCidsPorDescricaoOuId(descricao, limiteRegistros, false);
	}
	
	/**
	 * Pesquisa os CIDs pelo ID, ou pelo código ou pela descrição
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorDescricaoOuId(String descricao,
			Integer limiteRegistros, Boolean semJoin) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (descricao != null && !"".equals(descricao)) {
			descricao = descricao.toUpperCase();
			Criterion criterionCodigo = Restrictions.eq(AghCid.Fields.CODIGO.toString(), descricao);

			// Se for numero, tenta pesquisar pelo ID, senão tenta
			// pesquisar primeiramente pelo código e caso não
			// encontre nada tenta por fim pela descrição
			if (NumberUtils.isNumber(descricao)) {
				Criterion criterionId = Restrictions.idEq(NumberUtils.toInt(descricao));
				criteria.add(criterionId);
			} else {
				criteria.add(criterionCodigo);
			}
		}
		
		if(Boolean.FALSE.equals(semJoin)) {
			criteria.setFetchMode(AghCid.Fields.GRUPO_CID.toString(), FetchMode.JOIN);
			criteria.setFetchMode(AghCid.Fields.GRUPO_CID.toString() +"."+AghGrupoCids.Fields.CAPITULO_CID.toString() , FetchMode.JOIN);
		}
		
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		List<AghCid> listaResultados = new ArrayList<AghCid>();
		if (limiteRegistros != null) {
			listaResultados = executeCriteria(criteria, 0, limiteRegistros.intValue(),
					null, true);
		} else {
			listaResultados = executeCriteria(criteria);
		}
		
		if (listaResultados != null && !listaResultados.isEmpty()){
			return listaResultados;
		}
		else{
			//Tenta obter pela descrição
			return pesquisarCidsPorDescricao(descricao, limiteRegistros, semJoin);
		}
	}
	
	public List<AghCid> pesquisarCidsPorDescricao(String descricao,Integer limiteRegistros){
		return pesquisarCidsPorDescricao(descricao, limiteRegistros, false);
	}
	
	/**
	 * Pesquisa CIDs somente pela descrição
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorDescricao(String descricao,Integer limiteRegistros, Boolean semJoin){
		
		//Tenta obter pela descrição
		Criterion criterionDescricao = Restrictions.ilike(
				AghCid.Fields.DESCRICAO.toString(), descricao,
				MatchMode.ANYWHERE);
		DetachedCriteria criteriaPorDesc = DetachedCriteria.forClass(AghCid.class);
		
		if(Boolean.FALSE.equals(semJoin)) {
			criteriaPorDesc.setFetchMode(AghCid.Fields.GRUPO_CID.toString(), FetchMode.JOIN);
			criteriaPorDesc.setFetchMode(AghCid.Fields.GRUPO_CID.toString() +"."+AghGrupoCids.Fields.CAPITULO_CID.toString() , FetchMode.JOIN);
		}

		criteriaPorDesc.add(criterionDescricao);
		criteriaPorDesc.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		if (limiteRegistros != null) {
			return executeCriteria(criteriaPorDesc, 0, limiteRegistros.intValue(),
					null, true);
		} else {
			return executeCriteria(criteriaPorDesc);
		}
	}

	public DetachedCriteria criarPesquisaCidsProcedimentoAmbulatorioPorDescricaoOuId(Object parametro, Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class, "cid");
		String strParam = (String) parametro;

		if (strParam != null && !"".equals(strParam)) {
			Criterion criterionCodigo = Restrictions.ilike(AghCid.Fields.CODIGO.toString(), strParam, MatchMode.ANYWHERE);
			Criterion criterionDescricao = Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), strParam, MatchMode.ANYWHERE);

			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		String separador = ".";
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "proced");
		subCriteria.setProjection(Projections.distinct(Projections.property("proced" + separador + FatProcedHospIntCid.Fields.CID_SEQ.toString())));
		subCriteria.add(Restrictions.eq("proced" + separador + FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		subCriteria.add(Restrictions.or(Restrictions.isNull("proced" + separador + FatProcedHospIntCid.Fields.VALIDADE.toString()),
				Restrictions.eq("proced" + separador + FatProcedHospIntCid.Fields.VALIDADE.toString(), DominioTipoPlano.A)));

		criteria.add(Subqueries.propertyIn("cid" + separador + AghCid.Fields.SEQ.toString(), subCriteria));

		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));

		return criteria;
	}

	/**
	 * @param parametro
	 * @param phiSeq
	 * @return
	 */
	public List<AghCid> pesquisarCidsProcedimentoAmbulatorioPorDescricaoOuId(Object parametro, Integer phiSeq) {
		DetachedCriteria criteria = criarPesquisaCidsProcedimentoAmbulatorioPorDescricaoOuId(parametro, phiSeq);
		criteria.addOrder(Order.asc(AghCid.Fields.CODIGO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * @param parametro
	 * @param phiSeq
	 * @return
	 */
	public Long pesquisarCidsProcedimentoAmbulatorioPorDescricaoOuIdCount(Object parametro, Integer phiSeq) {
		DetachedCriteria criteria = criarPesquisaCidsProcedimentoAmbulatorioPorDescricaoOuId(parametro, phiSeq);
		return executeCriteriaCount(criteria);
	}

	/**
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(String descricao, Integer limiteRegistros) {
		StringBuilder sb = new StringBuilder(150);

		// Utilizado HQL para essa consulta, pois não foi encontrada uma forma
		// de fazer "NOT IN" e "NOT EXISTS" utilizando Criteria/Restrictions há
		sb.append("from AghCid as cid1 where not exists (").append("from AghCid as cid2 where cid1.").append(AghCid.Fields.SEQ.toString())
				.append(" = cid2.").append(AghCid.Fields.CID_SEQ.toString()).append(") ");

		if (descricao != null && !"".equals(descricao)) {

			sb.append("and (upper(cid1.").append(AghCid.Fields.CODIGO.toString()).append(") = '").append(descricao.trim().toUpperCase()).append("' ");

			if (NumberUtils.isNumber(descricao) && (descricao.charAt(0) != '0')) {
				sb.append("or cid1.").append(AghCid.Fields.SEQ.toString()).append(" = ").append(descricao).append(')');
			} else {
				sb.append("or upper(cid1.").append(AghCid.Fields.DESCRICAO.toString()).append(") like '%").append(descricao.trim().toUpperCase())
						.append("%')");
			}
		}

		sb.append("and cid1.").append(AghCid.Fields.SITUACAO.toString()).append(" = '").append(DominioSituacao.A).append('\'');

		Query q = createQuery(sb.toString());

		if (limiteRegistros != null) {
			q.setMaxResults(limiteRegistros);
		}

		return q.getResultList();
	}

	/**
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(String descricao, Integer limiteRegistros) {

		StringBuilder sb = new StringBuilder(150);

		// Utilizado HQL para essa consulta, pois não foi encontrada uma forma
		// de fazer "NOT IN" e "NOT EXISTS" utilizando Criteria/Restrictions há
		sb.append("from AghCid as cid1 where not exists (").append("from AghCid as cid2 where cid1.").append(AghCid.Fields.SEQ.toString())
				.append(" = cid2.").append(AghCid.Fields.CID_SEQ.toString()).append(") ");

		if (descricao != null && !"".equals(descricao)) {

			sb.append("and (upper(cid1.").append(AghCid.Fields.CODIGO.toString()).append(") = '").append(descricao.trim().toUpperCase()).append("' ");

			if (NumberUtils.isNumber(descricao) && (descricao.charAt(0) != '0')) {
				sb.append("or cid1.").append(AghCid.Fields.SEQ.toString()).append(" = ").append(descricao).append(')');
			} else {
				sb.append("or upper(cid1.").append(AghCid.Fields.DESCRICAO.toString()).append(") like '%").append(descricao.trim().toUpperCase())
						.append("%')");
			}
		}

		sb.append("and cid1.").append(AghCid.Fields.SITUACAO.toString()).append(" = '").append(DominioSituacao.A).append("' ");

		sb.append("order by cid1.").append(AghCid.Fields.DESCRICAO.toString());

		Query q = createQuery(sb.toString());
		if (limiteRegistros != null) {
			q.setMaxResults(limiteRegistros);
		}

		return q.getResultList();
	}

	public List<AinCidsInternacao> pesquisarCidsInternacao(Integer seqInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinCidsInternacao.class);

		criteria.add(Restrictions.eq(AinCidsInternacao.Fields.INT_SEQ.toString(), seqInternacao));

		return executeCriteria(criteria);
	}

	public List<AghCid> pesquisarSubCid(String codigoCid) {
		if (!codigoCid.contains(".")) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
			Criterion criterion1 = Restrictions.ge(AghCid.Fields.CODIGO.toString(), codigoCid + ".1");
			Criterion criterion2 = Restrictions.le(AghCid.Fields.CODIGO.toString(), codigoCid + ".9");
			criteria.add(Restrictions.and(criterion1, criterion2));
			return this.executeCriteria(criteria);

		}
		return null;
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir do codigo. Código é AK
	 * da tabela AGH_CIDS.
	 * 
	 * @param codigo
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaPorCidCriteria(Integer seq, String codigo, String descricao, DominioSituacao situacaoPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(AghCid.Fields.SEQ.toString(), seq));
		}
		if (StringUtils.isNotBlank(codigo)) {
			criteria.add(Restrictions.ilike(AghCid.Fields.CODIGO.toString(), codigo, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacaoPesquisa != null) {
			criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), situacaoPesquisa));
		}

		return criteria;
	}

	public Long pesquisaPorCidCount(Integer seq, String codigo, String descricao, DominioSituacao situacaoPesquisa) {
		DetachedCriteria criteria = createPesquisaPorCidCriteria(seq, codigo, descricao, situacaoPesquisa);
		return executeCriteriaCount(criteria);
	}

	public List<AghCid> pesquisaPorCid(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer seq, String codigo,
			String descricao, DominioSituacao situacaoPesquisa) {
		DetachedCriteria criteria = createPesquisaPorCidCriteria(seq, codigo, descricao, situacaoPesquisa);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public DominioSexoDeterminante obterRestricaoSexo(final String codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		if (codigo == null) {
			return null;
		}
		criteria.add(Restrictions.eq(AghCid.Fields.CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.setProjection(Projections.property(AghCid.Fields.RESTRICAO_SEXO.toString()));
		List<Object> result = executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return DominioSexoDeterminante.valueOf(result.get(0).toString());
	}

	@SuppressWarnings("unchecked")
	public List<AghCid> pesquisarCidsSemSubCategoriaPorCodigoDescricaoOuId(String descricao, Integer limiteRegistros) {
		StringBuilder sb = new StringBuilder(100);

		// Utilizado HQL para essa consulta, pois não foi encontrada uma forma
		// de fazer "NOT IN" e "NOT EXISTS" utilizando Criteria/Restrictions há
		// sb.append("from AghCids as cid1 where ").append(
		sb.append(" from ").append(AghCid.class.getSimpleName()).append(" as cid1 where ").append(AghCid.Fields.CID_SEQ.toString())
				.append(" is null ");

		if (descricao != null && !"".equals(descricao)) {

			sb.append("and upper(cid1.").append(AghCid.Fields.CODIGO.toString()).append(") like '%").append(descricao.trim().toUpperCase())
					.append("%' ");

			if (NumberUtils.isNumber(descricao)) {
				sb.append("or cid1.").append(AghCid.Fields.SEQ.toString()).append(" = ").append(descricao);
			} else {
				sb.append("or upper(cid1.").append(AghCid.Fields.DESCRICAO.toString()).append(") like '%").append(descricao.trim().toUpperCase())
						.append("%' ");
			}
		}
		sb.append("  order by ").append(AghCid.Fields.CODIGO.toString()).append(" asc");

		Query q = this.createQuery(sb.toString());

		if (limiteRegistros != null) {
			q.setMaxResults(limiteRegistros);
		}

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<AghCid> pesquisarCidsComSubCategoriaCodigoPorDescricaoOuId(String descricao, Integer limiteRegistros) {
		StringBuilder sb = new StringBuilder(100);

		// Utilizado HQL para essa consulta, pois não foi encontrada uma forma
		// de fazer "NOT IN" e "NOT EXISTS" utilizando Criteria/Restrictions há
		// sb.append("from AghCids as cid1  ");
		sb.append(" from ").append(AghCid.class.getSimpleName()).append(" as cid1 ");

		if (descricao != null && !"".equals(descricao)) {
			sb.append(" where ");
		}

		if (descricao != null && !"".equals(descricao)) {

			sb.append(" upper(cid1.").append(AghCid.Fields.CODIGO.toString()).append(") like '%").append(descricao.trim().toUpperCase())
					.append("%' ");

			if (NumberUtils.isNumber(descricao)) {
				sb.append("or cid1.").append(AghCid.Fields.SEQ.toString()).append(" = ").append(descricao);
			} else {
				sb.append("or upper(cid1.").append(AghCid.Fields.DESCRICAO.toString()).append(") like '%").append(descricao.trim().toUpperCase())
						.append("%' ");
			}
		}

		sb.append("  order by ").append(AghCid.Fields.CODIGO.toString()).append(" asc");
		Query q = this.createQuery(sb.toString());

		if (limiteRegistros != null) {
			q.setMaxResults(limiteRegistros);
		}

		return q.getResultList();
	}

	public List<AghCid> listarCids(Integer gcdCpcSeq, Integer gcdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.GCD_CPC_SEQ.toString(), gcdCpcSeq));
		criteria.add(Restrictions.eq(AghCid.Fields.GCD_SEQ.toString(), gcdSeq));
		return this.executeCriteria(criteria);
	}

	public String obterCodigoAghCidsPorPhiSeq(final Integer phiSeq) {
		List<Object> retorno = executeCriteria(criarCriteriaBuscarAghCidsPorPhiSeq(phiSeq));
		if (retorno == null || retorno.isEmpty()) {
			return null;
		}
		return retorno.get(0).toString();
	}

	protected DetachedCriteria criarCriteriaBuscarAghCidsPorPhiSeq(Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class);
		criteria.setProjection(Projections.property(FatProcedHospIntCid.Fields.CID.toString() + "." + AghCid.Fields.CODIGO.toString()));
		criteria.createAlias(FatProcedHospIntCid.Fields.CID.toString(), FatProcedHospIntCid.Fields.CID.toString());
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.addOrder(Order.desc(FatProcedHospIntCid.Fields.CID.toString() + "." + AghCid.Fields.CODIGO.toString()));

		return criteria;

	}

	@SuppressWarnings("unchecked")
	public String obterCidExamePorItemSolicitacaoExames(final Integer iseSoeSeq, final Short iseSeqp, final Integer qquQaoSeq) {
		StringBuffer hql = new StringBuffer("SELECT cid.codigo FROM ").append(AghCid.class.getName()).append(" as cid, ")
				.append(AelRespostaQuestao.class.getName()).append(" as rqu WHERE cid.").append(AghCid.Fields.CODIGO.toString())
				.append(" = SUBSTR(rqu.").append(AelRespostaQuestao.Fields.RESPOSTA.toString()).append(",1,5) and rqu.")
				.append(AelRespostaQuestao.Fields.ISE_SOE_SEQ.toString()).append(" = :iseSoeSeq and rqu.")
				.append(AelRespostaQuestao.Fields.ISE_SEQP.toString()).append(" = :iseSeqp and rqu.")
				.append(AelRespostaQuestao.Fields.QQU_QAO_SEQ.toString()).append(" = :qquQaoSeq ");

		Query query = createQuery(hql.toString());
		query.setParameter("iseSoeSeq", iseSoeSeq);
		query.setParameter("iseSeqp", iseSeqp);
		query.setParameter("qquQaoSeq", qquQaoSeq);

		List<Object> obj = query.getResultList();
		
		if (!obj.isEmpty()) {
			return (String) obj.get(0);
		}
		
		return null;
	}
	
	public List<AghCid> buscarCidsPorExameMaterial(final String sigla, final Integer manSeq) {
		return executeCriteria(criarCriteriaBuscarCidsPorExameMaterial(sigla, manSeq));
	}

	private DetachedCriteria criarCriteriaBuscarCidsPorExameMaterial(final String sigla, final Integer manSeq) {
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		subCriteria.createAlias(FatProcedHospInternosPai.Fields.FAT_PROCED_INT_CIDS.toString(), FatProcedHospInternosPai.Fields.FAT_PROCED_INT_CIDS.toString());
		subCriteria.setProjection(Projections.property(FatProcedHospInternosPai.Fields.FAT_PROCED_INT_CIDS.toString() + "."
				+ FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		subCriteria.add(Restrictions.eq(subCriteria.getAlias() + "." + FatProcedHospInternosPai.Fields.EMA_EXA_SIGLA.toString(), sigla));
		subCriteria.add(Restrictions.eq(subCriteria.getAlias() + "." + FatProcedHospInternosPai.Fields.EMA_MAN_SEQ.toString(), manSeq));

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Subqueries.propertyIn(AghCid.Fields.SEQ.toString(), subCriteria));

		return criteria;
	}

		
	private DetachedCriteria criarCriteriaBuscarCidsCompativeisExameMaterial(final String sigla, final Integer manSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "PIC");
		      criteria.createAlias(FatProcedHospIntCid.Fields.PROCED_HOSPITALAR_INTERNO.toString(), "PHI");
		      criteria.setProjection(Projections.property("PIC." + FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		 
		    criteria.add(Restrictions.eq("PHI" + "." + FatProcedHospInternosPai.Fields.EMA_EXA_SIGLA.toString(), sigla));
			criteria.add(Restrictions.eq("PHI" + "." + FatProcedHospInternosPai.Fields.EMA_MAN_SEQ.toString(), manSeq));

		return criteria;
	}
		
	
	public List<AghCid> pesquisarCidsPorDescricaoOuId(final String descricao, final Integer limiteRegistros, final DominioSexoDeterminante sexoPac,final String sigla, final Integer manSeq) {
		final DetachedCriteria criteria = criarCriteriaPesquisarCidsPorDescricaoOuId(descricao, sexoPac, sigla, manSeq);
		
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		final List<AghCid> listaResultados = executeCriteria(criteria, 0, limiteRegistros, null, false);

		if (listaResultados == null || listaResultados.isEmpty()) {
			// Tenta obter pela descrição
			return pesquisarCidsPorDescricao(descricao, limiteRegistros, sexoPac, sigla, manSeq);
		} else {
			return listaResultados;
		}
	}
	
	private DetachedCriteria criarCriteriaPesquisarCidsPorDescricaoOuId(final String descricao, final DominioSexoDeterminante sexoPac, final String sigla, final Integer manSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (descricao != null && !"".equals(descricao)) {

			if (NumberUtils.isNumber(descricao)) {
				criteria.add(Restrictions.idEq(NumberUtils.toInt(descricao)));
			} else {
				criteria.add(Restrictions.eq(AghCid.Fields.CODIGO.toString(), descricao.toUpperCase()));
			}
		}
		if (sexoPac != null) {
			criteria.add(Restrictions.in(AghCid.Fields.RESTRICAO_SEXO.toString(), new DominioSexoDeterminante[] { sexoPac, DominioSexoDeterminante.Q }));
		}
		
		if (sigla != null && manSeq != null){
			DetachedCriteria subCriteria = criarCriteriaBuscarCidsCompativeisExameMaterial(sigla,manSeq);
						 
				if ( executeCriteria(subCriteria).size() > 0){
			
						criteria.add(Subqueries.propertyIn(AghCid.Fields.SEQ.toString(), subCriteria));
				}
					
		}
				
		return criteria;
	}

	private DetachedCriteria criarCriteriaPesquisarCidsPorDescricao(String descricao, DominioSexoDeterminante sexoPac,final String sigla, final Integer manSeq) {
		final DetachedCriteria criteriaPorDesc = DetachedCriteria.forClass(AghCid.class);

		criteriaPorDesc.add(Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		if (sexoPac != null) {
			criteriaPorDesc.add(Restrictions.in(AghCid.Fields.RESTRICAO_SEXO.toString(), new DominioSexoDeterminante[]{sexoPac,DominioSexoDeterminante.Q}));
		}
		
		if (sigla != null && manSeq != null){
			DetachedCriteria subCriteria = criarCriteriaBuscarCidsCompativeisExameMaterial(sigla,manSeq);
						 
				if ( executeCriteria(subCriteria).size() > 0){
			
					criteriaPorDesc.add(Subqueries.propertyIn(AghCid.Fields.SEQ.toString(), subCriteria));
				}
					
		}
				
		return criteriaPorDesc;
	}

	public List<AghCid> pesquisarCidsPorDescricao(final String descricao, final Integer limiteRegistros, final DominioSexoDeterminante sexoPac, final String sigla, final Integer manSeq) {

		// Tenta obter pela descrição
		final DetachedCriteria criteriaPorDesc = criarCriteriaPesquisarCidsPorDescricao(descricao, sexoPac, sigla, manSeq);

		criteriaPorDesc.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		if (limiteRegistros != null) {
			return executeCriteria(criteriaPorDesc, 0, limiteRegistros.intValue(), null, true);
		} else {
			return executeCriteria(criteriaPorDesc);
		}
	}
		
	private DetachedCriteria criarCriteriaPesquisarCidsPorDescricao(String descricao, DominioSexoDeterminante sexoPac) {
		final DetachedCriteria criteriaPorDesc = DetachedCriteria.forClass(AghCid.class);

		criteriaPorDesc.add(Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		if (sexoPac != null) {
			criteriaPorDesc.add(Restrictions.in(AghCid.Fields.RESTRICAO_SEXO.toString(), new DominioSexoDeterminante[]{sexoPac,DominioSexoDeterminante.Q}));
		}
		return criteriaPorDesc;
	}

	public List<AghCid> pesquisarCidsPorDescricao(final String descricao, final Integer limiteRegistros, final DominioSexoDeterminante sexoPac) {

		// Tenta obter pela descrição
		final DetachedCriteria criteriaPorDesc = criarCriteriaPesquisarCidsPorDescricao(descricao, sexoPac);

		criteriaPorDesc.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		if (limiteRegistros != null) {
			return executeCriteria(criteriaPorDesc, 0, limiteRegistros.intValue(), null, true);
		} else {
			return executeCriteria(criteriaPorDesc);
		}
	}

	public Long contarCidsPorDescricaoOuId(String descricao, DominioSexoDeterminante sexoPac) {
		  return contarCidsPorDescricaoOuIdCount(descricao, sexoPac, null, null);
	}
		
	public Long contarCidsPorDescricaoOuId(String descricao, DominioSexoDeterminante sexoPac, String sigla, Integer manSeq) {
		  return contarCidsPorDescricaoOuIdCount(descricao, sexoPac, sigla, manSeq);	
	}
	
	public Long contarCidsPorDescricaoOuIdCount(String descricao, DominioSexoDeterminante sexoPac, String sigla, Integer manSeq) {
		final Long ret = executeCriteriaCount(this.criarCriteriaPesquisarCidsPorDescricaoOuId(descricao, sexoPac, sigla, manSeq));
		if(ret == 0){
			return executeCriteriaCount(this.criarCriteriaPesquisarCidsPorDescricao(descricao, sexoPac));
		}
		return ret;
	}

	public List<AghCid> pesquisarCidPorCodigoDescricao(final Integer maxResult, final Object objParam) {
		final String param;
		if (objParam == null) {
			param = "";
		} else {
			param = objParam.toString();
		}
		List<AghCid> retorno = executeCriteria(criarCriteriaPesquisarCidsPorCodigo(param), 0, maxResult, null, false);
		if (retorno == null || retorno.isEmpty()) {
			retorno = executeCriteria(criarCriteriaPesquisarCidsPorDescricao(param), 0, maxResult, null, false);
		}
		return retorno;
	}

	public Long pesquisarCidPorCodigoDescricaoCount(final Object objParam) {
		final String param;
		if (objParam == null) {
			param = "";
		} else {
			param = objParam.toString();
		}
		Long count = executeCriteriaCount(criarCriteriaPesquisarCidsPorCodigo(param));
		if (count == 0) {
			count = executeCriteriaCount(criarCriteriaPesquisarCidsPorDescricao(param));
		}
		return count;
	}

	private DetachedCriteria criarCriteriaPesquisarCidsPorCodigo(final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.CODIGO.toString(), descricao.toUpperCase()));
		return criteria;
	}

	private DetachedCriteria criarCriteriaPesquisarCidsPorDescricao(final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		return criteria;
	}

	public List<AghCid> pesquisarCidAtivosUsuaisEquipe(final Integer matriculaEquipe, final Short vinCodigoEquipe) {
		return executeCriteria(criarCriteriaPesquisarCidAtivosUsuaisEquipe(matriculaEquipe, vinCodigoEquipe));
	}
	
	private DetachedCriteria criarCriteriaPesquisarCidAtivosUsuaisEquipe(final Integer matriculaEquipe, final Short vinCodigoEquipe) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class, "cid");
		criteria.createAlias("cid." + AghCid.Fields.CID_USUAL_EQUIPES.toString(), "cuq");
		criteria.createAlias("cuq." + MbcCidUsualEquipe.Fields.AGH_EQUIPES.toString(), "eqp");
		
		criteria.add(Restrictions.eq("eqp." + AghEquipes.Fields.SER_MATRICULA.toString(), matriculaEquipe));
		criteria.add(Restrictions.eq("eqp." + AghEquipes.Fields.SER_VIN_CODIGO.toString(), vinCodigoEquipe));
		criteria.add(Restrictions.eq("cid." + AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	private DetachedCriteria montaCriteriaAghCidPorCodigoDescricao(String codigoOuDescricao, 
			Integer seq, DominioSituacao situacao,	boolean somenteCodigo,	boolean somenteDescricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		
		if(somenteCodigo){criteria.add(Restrictions.ilike(AghCid.Fields.CODIGO.toString(), codigoOuDescricao, MatchMode.ANYWHERE));}
		if(somenteDescricao){criteria.add(Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), codigoOuDescricao, MatchMode.ANYWHERE));}
		if(!somenteCodigo && !somenteDescricao){//se os dois falses, então irá pesquisar pelo codigo OU descricao
		criteria.add(Restrictions.or(
				Restrictions.ilike(AghCid.Fields.CODIGO.toString(), codigoOuDescricao, MatchMode.ANYWHERE), 
				Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), codigoOuDescricao, MatchMode.ANYWHERE)));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	public List<AghCid> listarAghCidPorCodigoDescricao(String codigoOuDescricao, Integer seq, DominioSituacao situacao,
			boolean somenteCodigo, boolean somenteDescricao,Integer firstResult, Integer maxResults, boolean asc, AghCid.Fields... ordersProperty) {
		DetachedCriteria criteria = montaCriteriaAghCidPorCodigoDescricao(
				codigoOuDescricao, seq, situacao, somenteCodigo, somenteDescricao);
		for(AghCid.Fields order : ordersProperty){
			if(asc){criteria.addOrder(Order.asc(order.toString()));
			}else{criteria.addOrder(Order.desc(order.toString()));}
		}
		if(firstResult!=null && maxResults!=null){
			return executeCriteria(criteria, firstResult, maxResults, null, asc);	
		} else {
			return executeCriteria(criteria);	
		}
		
	}
	public Long listarAghCidPorCodigoDescricaoCount(String codigoOuDescricao, Integer seq, DominioSituacao situacao,	boolean somenteCodigo, boolean somenteDescricao) {
		DetachedCriteria criteria = montaCriteriaAghCidPorCodigoDescricao(codigoOuDescricao, seq, situacao, somenteCodigo, somenteDescricao);
		return executeCriteriaCount(criteria);
	}
	

	public List<AghCid> listarAghCidPorIdadeSexo(List<Long> listaCodSsm, Integer idade, String cidCodigo,
			DominioSexoDeterminante sexo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class, "CID");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("CID." + AghCid.Fields.SEQ.toString())))
				.add(Projections.property("CID." + AghCid.Fields.CODIGO.toString()))
				.add(Projections.property("CID." + AghCid.Fields.DESCRICAO.toString())));
		
		criteria.add(Restrictions.eq("CID." + AghCid.Fields.CODIGO.toString(), cidCodigo));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "TPC");
		subCriteria.setProjection(Projections.property("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString(),
				"CID." + AghCid.Fields.SEQ.toString()));
		
		DetachedCriteria subCriteriaVap = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAP");
		subCriteriaVap.setProjection(Projections.property("VAP." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteriaVap.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(),
				"VAP." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));	
		subCriteriaVap.add(Restrictions.eq("VAP." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), (short) 1));
		subCriteriaVap.add(Restrictions.eq("VAP." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), (byte) 1));
		subCriteriaVap.add(Restrictions.in("VAP." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), listaCodSsm));
		
		subCriteria.add(Subqueries.exists(subCriteriaVap));
		criteria.add(Subqueries.exists(subCriteria));
		return executeCriteria(criteria);
	}
	
	public List<AghCid> listarCidPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao) {
		DetachedCriteria criteria = this.montarCriteriaCidPorIdadeSexoProcedimento(strPesquisa, sexo, idade, codTabela, paramTabelaFaturPadrao);
		criteria.addOrder(Order.asc("CID." + AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long listarCidPorIdadeSexoProcedimentoCount(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao) {
		DetachedCriteria criteria = this.montarCriteriaCidPorIdadeSexoProcedimento(strPesquisa, sexo, idade, codTabela, paramTabelaFaturPadrao);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaCidPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao) {

		String stParametro = (String) strPesquisa;
		Integer cidSeq = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			cidSeq = Integer.valueOf(stParametro);
		}		

		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class, "CID");
		criteria.createAlias("CID." + AghCid.Fields.SINONIMO_CID.toString(), "SCI", JoinType.LEFT_OUTER_JOIN);
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("COALESCE(sci1_.DESCRICAO, this_.DESCRICAO) as descricao");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CID." + AghCid.Fields.SEQ.toString()), AghCid.Fields.SEQ.toString())
				.add(Projections.property("CID." + AghCid.Fields.CODIGO.toString()), AghCid.Fields.CODIGO.toString())
				.add(Projections.sqlProjection(sqlProjection.toString(), 
						new String[]{AghCid.Fields.DESCRICAO.toString()}, 
									new Type[] { StringType.INSTANCE }), AghCid.Fields.DESCRICAO.toString()));

		if (cidSeq != null) {
			criteria.add(Restrictions.eq("CID." + AghCid.Fields.SEQ.toString(), cidSeq));
		} else if (StringUtils.isNotBlank(stParametro)) {
			criteria.add(Restrictions.or(Restrictions.ilike("CID." + AghCid.Fields.CODIGO.toString(), stParametro, MatchMode.ANYWHERE), 
					Restrictions.or(Restrictions.ilike("CID." + AghCid.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE), Restrictions.ilike("CID." + AghSinonimoCid.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE))));
		}

		//subQuery FatProcedHospIntCid
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "TPC");
		subCriteria.setProjection(Projections.property("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString(),
				"CID." + AghCid.Fields.SEQ.toString()));		

		//subQuery VFatAssociacaoProcedimento
		DetachedCriteria subCriteriaVap = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAP");
		subCriteriaVap.setProjection(Projections.property("VAP." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteriaVap.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(),
				"VAP." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteriaVap.add(Restrictions.eq("VAP." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), (short) 1));
		subCriteriaVap.add(Restrictions.eq("VAP." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), (byte) 1));

		//subQuery VFatSsmInternacao
		DetachedCriteria subQuerySsmInternacao = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		subQuerySsmInternacao.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_SINONIMO_PROCED_HOSPITALAR.toString(), "IPS", JoinType.LEFT_OUTER_JOIN);
		subQuerySsmInternacao.createAlias("IPH." + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC");
		subQuerySsmInternacao.setProjection(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		subQuerySsmInternacao.add(Restrictions.eqProperty("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), 
		"VAP." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()));
		subQuerySsmInternacao.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), paramTabelaFaturPadrao));
		subQuerySsmInternacao.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), Boolean.TRUE));
		subQuerySsmInternacao.add(Restrictions.or(
		Restrictions.and(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.TRUE),
		Restrictions.and(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString()),
		Restrictions.or(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()),
		Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SADT.toString())))),
		Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.FALSE)));
		subQuerySsmInternacao.add(Restrictions.le("IPC." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), new Date()));
		subQuerySsmInternacao.add(Restrictions.or(
		Restrictions.isNull("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()),
		Restrictions.ge("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), new Date())));
		subQuerySsmInternacao.add(Restrictions.in(
		"IPH." + FatItensProcedHospitalar.Fields.SEXO.toString(), new DominioSexoDeterminante[]{DominioSexoDeterminante.Q, sexo}));
		subQuerySsmInternacao.add(Restrictions.and(Restrictions.le("IPH." + FatItensProcedHospitalar.Fields.IDADE_MIN.toString(), idade), 
		Restrictions.ge("IPH." + FatItensProcedHospitalar.Fields.IDADE_MAX.toString(), idade)));
		subQuerySsmInternacao.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (codTabela != null) {
			subQuerySsmInternacao.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		}

		criteria.add(Restrictions.or(Restrictions.eq("CID." + AghCid.Fields.RESTRICAO_SEXO.toString(), DominioSexoDeterminante.Q), 
				Restrictions.eq("CID." + AghCid.Fields.RESTRICAO_SEXO.toString(), DominioSexoDeterminante.F)));
		subCriteriaVap.add(Subqueries.exists(subQuerySsmInternacao));
		subCriteria.add(Subqueries.exists(subCriteriaVap));
		criteria.add(Subqueries.exists(subCriteria));
		criteria.setResultTransformer(Transformers.aliasToBean(AghCid.class));
		return criteria;
	}
	
	public AghCid obterCidPermiteCidSecundario (Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.isNotNull(AghCid.Fields.CID_INICIAL_SECUNDARIO.toString()));
		criteria.add(Restrictions.isNotNull(AghCid.Fields.CID_FINAL_SECUNDARIO.toString()));
		criteria.add(Restrictions.eq(AghCid.Fields.SEQ.toString(), cidSeq));
		return (AghCid) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghCid> listarCidSecundarioPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, String cidCodigo,
			Short paramTabelaFaturPadrao, Integer caracteristica) {
		DetachedCriteria criteria = this.montarCriteriaCidSecundarioPorIdadeSexoProcedimento(strPesquisa, sexo, idade, cidCodigo, paramTabelaFaturPadrao, caracteristica);
		criteria.addOrder(Order.asc("CID." + AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long listarCidSecundarioPorIdadeSexoProcedimentoCount(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, String cidCodigo,
			Short paramTabelaFaturPadrao, Integer caracteristica) {
		DetachedCriteria criteria = this.montarCriteriaCidSecundarioPorIdadeSexoProcedimento(strPesquisa, sexo, idade, cidCodigo, paramTabelaFaturPadrao, caracteristica);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaCidSecundarioPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, String cidCodigo,
			Short paramTabelaFaturPadrao, Integer caracteristica) {
		String stParametro = (String) strPesquisa;
		Integer cidSeq = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			cidSeq = Integer.valueOf(stParametro);
		}		

		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class, "CID");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("CID." + AghCid.Fields.SEQ.toString())), AghCid.Fields.SEQ.toString())
				.add(Projections.property("CID." + AghCid.Fields.CODIGO.toString()), AghCid.Fields.CODIGO.toString())
				.add(Projections.property("CID." + AghCid.Fields.DESCRICAO.toString()), AghCid.Fields.DESCRICAO.toString()));

		if (cidSeq != null) {
			criteria.add(Restrictions.eq("CID." + AghCid.Fields.SEQ.toString(), cidSeq));
		} else if (StringUtils.isNotBlank(stParametro)) {
			criteria.add(Restrictions.or(Restrictions.ilike("CID." + AghCid.Fields.CODIGO.toString(), stParametro, MatchMode.ANYWHERE), 
			Restrictions.or(Restrictions.ilike("CID." + AghCid.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE), Restrictions.ilike("SCI." + AghSinonimoCid.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE))));
		}

		//subQuery FatProcedHospIntCid
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "TPC");
		subCriteria.setProjection(Projections.property("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString(),
				"CID." + AghCid.Fields.SEQ.toString()));

		//subQuery VFatAssociacaoProcedimento
		DetachedCriteria subCriteriaVap = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAP");
		subCriteriaVap.setProjection(Projections.property("VAP." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteriaVap.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(),
				"VAP." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteriaVap.add(Restrictions.eq("VAP." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), (short) 1));
		subCriteriaVap.add(Restrictions.eq("VAP." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), (byte) 1));

		//subQuery fatCaractItemProcHosp
		DetachedCriteria subQueryCaractItemProcHops = DetachedCriteria.forClass(FatCaractItemProcHosp.class, "FAT");
		subQueryCaractItemProcHops.setProjection(Projections.property("FAT." + FatCaractItemProcHosp.Fields.VALOR_NUMERICO.toString()));
		subQueryCaractItemProcHops.add(Restrictions.eqProperty("FAT." + FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(),
				"VAP." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()));
		subQueryCaractItemProcHops.add(Restrictions.eqProperty("FAT." + FatCaractItemProcHosp.Fields.IPH_SEQ.toString(),
				"VAP." + VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString()));
		subQueryCaractItemProcHops.add(Restrictions.eq("FAT." + FatCaractItemProcHosp.Fields.SEQ_TIPO_CARACTERISTICA_ITEM.toString(), caracteristica));

		//subQuery VFatSsmInternacao
		DetachedCriteria subQuerySsmInternacao = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		subQuerySsmInternacao.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_SINONIMO_PROCED_HOSPITALAR.toString(), "IPS", JoinType.LEFT_OUTER_JOIN);
		subQuerySsmInternacao.createAlias("IPH." + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC");
		subQuerySsmInternacao.setProjection(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		subQuerySsmInternacao.add(Restrictions.eqProperty("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), 
				"VAP." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()));
		subQuerySsmInternacao.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), paramTabelaFaturPadrao));
		subQuerySsmInternacao.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), Boolean.TRUE));
		subQuerySsmInternacao.add(Restrictions.or(
				Restrictions.and(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.TRUE),
				Restrictions.and(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString()),
				Restrictions.or(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()),
				Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SADT.toString())))),
				Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.FALSE)));
		subQuerySsmInternacao.add(Restrictions.le("IPC." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), new Date()));
		subQuerySsmInternacao.add(Restrictions.or(
				Restrictions.isNull("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()),
				Restrictions.ge("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), new Date())));
		subQuerySsmInternacao.add(Restrictions.in(
				"IPH." + FatItensProcedHospitalar.Fields.SEXO.toString(), new DominioSexoDeterminante[]{DominioSexoDeterminante.Q, sexo}));
		subQuerySsmInternacao.add(Restrictions.and(Restrictions.le("IPH." + FatItensProcedHospitalar.Fields.IDADE_MIN.toString(), idade), 
				Restrictions.ge("IPH." + FatItensProcedHospitalar.Fields.IDADE_MAX.toString(), idade)));

		//subQuery cidInicialSec
		DetachedCriteria subCidInicialSec = DetachedCriteria.forClass(AghCid.class, "CIS");
		subCidInicialSec.setProjection(Projections.property("CIS." + AghCid.Fields.CID_INICIAL_SECUNDARIO.toString()));
		subCidInicialSec.add(Restrictions.eq("CIS." + AghCid.Fields.CODIGO.toString(), cidCodigo));

		//subQuery cidFinalSec
		DetachedCriteria subCidFinalSec = DetachedCriteria.forClass(AghCid.class, "CIS");
		subCidFinalSec.setProjection(Projections.property("CIS." + AghCid.Fields.CID_FINAL_SECUNDARIO.toString()));
		subCidFinalSec.add(Restrictions.eq("CIS." + AghCid.Fields.CODIGO.toString(), cidCodigo));
		subCriteriaVap.add(Subqueries.exists(subQuerySsmInternacao));
		subCriteriaVap.add(Subqueries.notExists(subQueryCaractItemProcHops));
		subCriteria.add(Subqueries.exists(subCriteriaVap));
		criteria.add(Subqueries.exists(subCriteria));
		criteria.add(Subqueries.propertyLe("CID." + AghCid.Fields.CODIGO, subCidFinalSec));
		criteria.add(Subqueries.propertyGe("CID." + AghCid.Fields.CODIGO, subCidInicialSec));
		criteria.setResultTransformer(Transformers.aliasToBean(AghCid.class));

		return criteria;
	}

	public List<AghCid> buscarCidsVinculadosCategoria(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.CID_SEQ.toString(), codigo));
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	/** C2 - 45902 Consulta CIDs por sexo SEXO_BIOLOGICO de AipPacientes
	 * @param sexo
	 * @return DetachedCriteria
	 */
	public DetachedCriteria obterCriteriabuscarCidsPorSexo(DominioSexo sexo, String pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		if (sexo != null) {
			if((DominioSexo.F.equals(sexo) || DominioSexo.M.equals(sexo)) && !DominioSexo.I.equals(sexo)){
				criteria.add(Restrictions.in(AghCid.Fields.RESTRICAO_SEXO.toString(), 
						new DominioSexoDeterminante[] { DominioSexoDeterminante.valueOf(sexo.toString()), DominioSexoDeterminante.Q }));
			}
		}
		
		criteria.add(Restrictions.or(
				Restrictions.ilike(AghCid.Fields.CODIGO.toString(), pesquisa.toUpperCase(), MatchMode.ANYWHERE), 
				Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), pesquisa.toUpperCase(), MatchMode.ANYWHERE)));
		
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	public List<AghCid> pesquisarCidPorCodDescricaoPorSexo(String param, DominioSexo sexo){
		DetachedCriteria criteria  = obterCriteriabuscarCidsPorSexo(sexo, param);
		return executeCriteria(criteria, 0, 100, AghCid.Fields.DESCRICAO.toString(), true);
	}
	
	public Long pesquisarCidPorCodDescricaoPorSexoCount(String param, DominioSexo sexo){
		DetachedCriteria criteria  = obterCriteriabuscarCidsPorSexo(sexo, param);
		return executeCriteriaCount(criteria);
	}

	
	/**
	 * Retornar os Cids pesquisados por seq
	 * 
	 * Web Service #36118
	 * 
	 * @param listSeq
	 * @return
	 */
	
	public List<AghCid> pesquisarCidPorSeq(List<Integer> listSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.in(AghCid.Fields.SEQ.toString(), listSeq));
		return executeCriteria(criteria);
	}

	/**
	 * Retornar os Cids pesquisados por codigo e/ou descricao
	 * 
	 * Web Service #36117
	 * 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<AghCid> pesquisarCidPorCodDescricao(String param){
		DetachedCriteria criteria  = montaCriteriaCidAtivoPorCodDescricao(param);
		return executeCriteria(criteria, 0, 100, AghCid.Fields.DESCRICAO.toString(), true);
	}
	
	public Long pesquisarCidPorCodDescricaoCount(String param){
		DetachedCriteria criteria  = montaCriteriaCidAtivoPorCodDescricao(param);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montaCriteriaCidAtivoPorCodDescricao(String param){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.or(
				Restrictions.ilike(AghCid.Fields.CODIGO.toString(), param.toUpperCase(), MatchMode.ANYWHERE), 
				Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), param.toUpperCase(), MatchMode.ANYWHERE)));
		return criteria;
	}
	/**
	 * Retornar os Cids ativos por seq
	 * 
	 * Web Service #37939
	 * 	
	 * @param listSeq
	 * @return
	 */
	public List<AghCid> pesquisarCidAtivosPorSeq(List<Integer> listSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		criteria.add(Restrictions.in(AghCid.Fields.SEQ.toString(), listSeq));
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<CidVO> obterCidPorDominioVivoMorto(DominioVivoMorto vivo, DominioVivoMorto morto){
		StringBuffer sql = new StringBuffer()
		
		.append(" SELECT CID.SEQ as seq ")
		.append(" FROM FAT_CAD_CID_NASCIMENTOS CCN ") 
		.append(" ,AGH.AGH_CIDS CID ")
		.append(" WHERE CCN.CID = CID.CODIGO ")
		.append(" AND CCN.VIVO = '"+vivo.toString()+"'")
		.append(" AND CCN.MORTO = '"+morto.toString()+"'");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setResultTransformer(Transformers.aliasToBean(CidVO.class));
		
		query.addScalar("seq", IntegerType.INSTANCE);
		
		return query.list();
	} 
	
	//#41768 - Consultar Critérios de Priorização de Atendimento
	public DetachedCriteria criteriaCidPorSeqCodDescricao(String pesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);
		
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(),DominioSituacao.A));
		
		if(StringUtils.isNotEmpty(pesquisa)){
			if(CoreUtil.isNumeroInteger(pesquisa)){
				criteria.add(Restrictions.or(Restrictions.eq(AghCid.Fields.SEQ.toString(), Integer.valueOf(pesquisa)),
						Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE),
						Restrictions.ilike(AghCid.Fields.CODIGO.toString(), pesquisa, MatchMode.ANYWHERE)));
			}else{
				criteria.add(Restrictions.or(Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE),
				Restrictions.ilike(AghCid.Fields.CODIGO.toString(), pesquisa, MatchMode.ANYWHERE)));
			}		
		}
		
		return criteria;			
	}
	/**
	 * #41768 C1 - Realiza a pesquisa de CIDS por sequencia, codigo ou descricao. Retornando uma lista
	 * com os 100 primeiros registros em ordem crescente por descricao.	 
	 * **/
	public List<AghCid> pesquisarCidPorSeqCodDescricao(String pesquisa){
		DetachedCriteria criteria = criteriaCidPorSeqCodDescricao(pesquisa);
		return executeCriteria(criteria, 0, 100, AghCid.Fields.DESCRICAO.toString(), true);
	}
	
	public Long pesquisarCidPorSeqCodDescricaoCount(String pesquisa){
		DetachedCriteria criteria = criteriaCidPorSeqCodDescricao(pesquisa);
		return executeCriteriaCount(criteria);
	}

	/**
	 * #43089 - C3
	 * @param atdSeq
	 * @param pteSeq
	 * @return List<DescricaoCodigoComplementoCidVO>
	 */
	@SuppressWarnings("unchecked")
	public List<DescricaoCodigoComplementoCidVO> obterDescricaoCidPorAtendimentoPrescricaoPaciente(Integer atdSeq, Integer pteSeq) {
		
		String hql = "SELECT new br.gov.mec.aghu.paciente.vo.DescricaoCodigoComplementoCidVO (atd.seq, pte.id.seq, trp.pacCodigo, cid.descricao, cid.codigo, ctt.complemento) "
				.concat("FROM MptPrescricaoPaciente pte ")
				.concat("INNER JOIN pte.atendimento atd ")
				.concat("LEFT JOIN atd.tratamentoTerapeutico trp ")
				.concat("INNER JOIN trp.listaMptCidTratTerapeutico ctt ")
				.concat("INNER JOIN ctt.aghCid cid ")
				.concat("LEFT JOIN cid.cid cid1 ")
				.concat("WHERE atd.seq = :atdSeq ")
				.concat("AND pte.id.seq = :pteSeq ");
		
		org.hibernate.Query query = createHibernateQuery(hql);
		
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("pteSeq", pteSeq);
		
		return query.list();

	}
	
	/**
	 * #41768 C2 - Realiza a consulta dos critérios de priorização de atendimento
	 * **/
	public DetachedCriteria criteriaCriteriosPriorizacaoAtendimento(AghCid cid, CriteriosPriorizacaoAtendVO filtro){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxCriterioPriorizacaoTmo.class,"CPT");
//		criteria.createAlias("CPT."+MtxCriterioPriorizacaoTmo.Fields.CID.toString(), "CID", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(CID_PONTO+AghCid.Fields.CODIGO.toString()),
				CriteriosPriorizacaoAtendVO.Fields.CODIGO.toString());
//		projList.add(Projections.property(CID_PONTO+AghCid.Fields.SEQ.toString()),
//				CriteriosPriorizacaoAtendVO.Fields.CID_SEQ.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.SEQ.toString()),
				CriteriosPriorizacaoAtendVO.Fields.SEQ.toString());
		projList.add(Projections.property(CID_PONTO+AghCid.Fields.DESCRICAO.toString()),
				CriteriosPriorizacaoAtendVO.Fields.DESCRICAO.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE.toString()),
				CriteriosPriorizacaoAtendVO.Fields.GRAVIDADE.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE.toString()), 
				CriteriosPriorizacaoAtendVO.Fields.CRITICIDADE.toString());
		projList.add(Projections.property(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.IND_SITUACAO.toString()), 
				CriteriosPriorizacaoAtendVO.Fields.SITUACAO.toString());
		criteria.setProjection(projList);
		
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));			
		}
		
		if(filtro.getCriticidade() != null ){
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE.toString(), filtro.getCriticidade()));
		}
		
		if(filtro.getGravidade() != null){
			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE.toString(), filtro.getGravidade()));
		}
		
//		if (cid != null && cid.getSeq() != null) {
//			criteria.add(Restrictions.eq(CPT_PONTO+MtxCriterioPriorizacaoTmo.Fields.CID_SEQ.toString(),cid.getSeq()));
//		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(CriteriosPriorizacaoAtendVO.class));
	
		return criteria;
	}
	
	public List<CriteriosPriorizacaoAtendVO> pesquisarCriteriosPriorizacaoAtendimento(AghCid cid, 
			CriteriosPriorizacaoAtendVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){		
		DetachedCriteria criteria = criteriaCriteriosPriorizacaoAtendimento(cid, filtro);		

		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.asc(CID_PONTO+AghCid.Fields.CODIGO.toString()));
			criteria.addOrder(Order.asc(CID_PONTO+AghCid.Fields.DESCRICAO.toString()));
		}

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	

	public Long pesquisarCriteriosPriorizacaoAtendimentoCount(AghCid cid, CriteriosPriorizacaoAtendVO filtro){
		DetachedCriteria criteria = criteriaCriteriosPriorizacaoAtendimento(cid, filtro);
		return executeCriteriaCount(criteria);
	}

	public List<AghCid> pesquisarCidPorCodDescricaoSGB(String param){
		DetachedCriteria criteria  = montaCriteriaCidAtivoPorCodDescricao(param);
		criteria.addOrder(Order.asc(AghCid.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * #36463 C4 
	 * Consulta para obter dados descricao de AghCid utilizado na
	 * criaca£o do relatorio ESPELHO DA AIH
	 */
	public String obterDescricaoCidPorCodigo(String codigo){		
		AghCidObterDescricaoPorCodigoQueryBuilder builder = new AghCidObterDescricaoPorCodigoQueryBuilder();
		return (String) executeCriteriaUniqueResult(builder.build(codigo));
	}

	/**
	 * C16 #47668
	 * @param conNumero
	 * @return
	 */
	public CidVO pesquisarJustificativaFoto(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class,"cid");
		criteria.setProjection(Projections.projectionList().add(Projections.property("cid."+AghCid.Fields.SEQ.toString()),CidVO.Fields.SEQ.toString())
				.add(Projections.property("cid."+AghCid.Fields.DESCRICAO.toString()),CidVO.Fields.DESCRICAO.toString())
				.add(Projections.property("cid."+AghCid.Fields.CODIGO.toString()),CidVO.Fields.CODIGO.toString()));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class,"cph");
		subCriteria.add(Restrictions.eq("cph."+AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), conNumero));
		subCriteria.setProjection(Projections.property("cph."+AacConsultaProcedHospitalar.Fields.CID_SEQ.toString()));
		criteria.add(Subqueries.propertyIn("cid."+AghCid.Fields.SEQ.toString(),subCriteria));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CidVO.class));
		List<CidVO> cid= executeCriteria(criteria);
		
		if(cid!=null && !cid.isEmpty()){
			return cid.get(0);
		}
		
		return null;
	}
	
	/**
	 * C15 #47668
	 * @param conNumero
	 * @return
	 */
	public CidVO pesquisarCodigoDescricaoCidPorAghParametro(Integer conNumero,String[] parametros) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class,"cid");
		criteria.setProjection(Projections.projectionList()
											.add(Projections.property("cid."+AghCid.Fields.DESCRICAO.toString()),CidVO.Fields.DESCRICAO.toString())
											.add(Projections.property("cid."+AghCid.Fields.CODIGO.toString()),CidVO.Fields.CODIGO.toString())
											.add(Projections.property("cid."+AghCid.Fields.SEQ.toString()),CidVO.Fields.SEQ.toString()));
		

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghParametros.class,"param");
		subCriteria.setProjection(Projections.property("param."+AghParametros.Fields.VLR_TEXTO.toString()));
		subCriteria.add(Restrictions.in("param."+AghParametros.Fields.NOME.toString(), parametros));

		
		DetachedCriteria subSubCriteria = DetachedCriteria.forClass(AacConsultas.class,"C");
		subSubCriteria.createAlias("C." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G");
		subSubCriteria.createAlias("G." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "E");
		subSubCriteria.createAlias("E." + AghEspecialidades.Fields.ESPECIALIDADE_TRATAMENTO.toString(), "ET");
		subSubCriteria.createAlias("ET." + FatEspecialidadeTratamento.Fields.TIPO_TRATAMENTO.toString(), "TT");
		subSubCriteria.setProjection(Projections.property("TT." + FatTipoTratamentos.Fields.CODIGO.toString()));
		
		subSubCriteria.add(Restrictions.eq("C."+AacConsultas.Fields.NUMERO.toString(), conNumero));
         
		criteria.add(Subqueries.propertyEq("cid."+AghCid.Fields.CODIGO.toString(),subCriteria));
		subCriteria.add(Subqueries.propertyIn("param."+AghParametros.Fields.VLR_NUMERICO.toString(),subSubCriteria));
		criteria.setResultTransformer(Transformers.aliasToBean(CidVO.class));
		
		List<CidVO> cid= executeCriteria(criteria);
		
		if(cid!=null && !cid.isEmpty()){
			return (CidVO)cid.get(0);
		}		    
		
		return null;
	}

	 public List<AghCid> pesquisarPorNomeCodigoAtivaPaginado(String parametro, SigProcessamentoCusto competencia) {
			String descricaoOuCodigo = StringUtils.trimToNull(parametro);
			DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdCIDS.class);
			criteria.createAlias(SigCalculoAtdCIDS.Fields.CID.toString(), "CID", JoinType.INNER_JOIN);
			if(competencia!=null){
				criteria.createAlias(SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString(), "CAC", JoinType.INNER_JOIN);
				criteria.createAlias("CAC."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "PMU", JoinType.INNER_JOIN);	
			}
			criteria.createAlias("CID."+AghCid.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
			if (StringUtils.isNotEmpty(descricaoOuCodigo)) {
				Criterion c1 = Restrictions.ilike("CID."+AghCid.Fields.CODIGO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
				Criterion c2 = Restrictions.ilike("CID."+AghCid.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(c1, c2));
			}
			criteria.add(Restrictions.eq("CID."+AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
			if(competencia!=null){
				criteria.add(Restrictions.eq("PMU."+SigProcessamentoCusto.Fields.SEQ.toString(), competencia.getSeq()));	
			}
			criteria.addOrder(Order.asc("CID."+AghCid.Fields.DESCRICAO.toString()));
			criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("CID."+AghCid.Fields.SEQ.toString())),"seq")
					.add(Projections.property("CID." + AghCid.Fields.CODIGO.toString()), "codigo")
					.add(Projections.property("CID." + AghCid.Fields.DESCRICAO.toString()), "descricao"));
			criteria.setResultTransformer(Transformers.aliasToBean(AghCid.class));
			return executeCriteria(criteria, 0, 100, "seq", true);
		}
	  
	  public Long pesquisarPorNomeCodigoAtivaCount(String parametro, SigProcessamentoCusto competencia) {
			String descricaoOuCodigo = StringUtils.trimToNull(parametro);
			DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdCIDS.class);
			criteria.createAlias(SigCalculoAtdCIDS.Fields.CID.toString(), "CID", JoinType.INNER_JOIN);
			if(competencia!=null){
				criteria.createAlias(SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString(), "CAC", JoinType.INNER_JOIN);
				criteria.createAlias("CAC."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "PMU", JoinType.INNER_JOIN);	
			}
			criteria.createAlias("CID."+AghCid.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
			if (StringUtils.isNotEmpty(descricaoOuCodigo)) {
				Criterion c1 = Restrictions.ilike("CID."+AghCid.Fields.CODIGO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
				Criterion c2 = Restrictions.ilike("CID."+AghCid.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(c1, c2));
			}
			criteria.add(Restrictions.eq("CID."+AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
			if(competencia!=null){
				criteria.add(Restrictions.eq("PMU."+SigProcessamentoCusto.Fields.SEQ.toString(), competencia.getSeq()));	
			}
			criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("CID."+AghCid.Fields.SEQ.toString())),"seq")
					.add(Projections.property("CID." + AghCid.Fields.CODIGO.toString()), "codigo")
					.add(Projections.property("CID." + AghCid.Fields.DESCRICAO.toString()), "descricao"));
			criteria.setResultTransformer(Transformers.aliasToBean(AghCid.class));
		
			return executeCriteriaCount(criteria);
		}

	public List<MpmCidAtendimento> pesquisarPacientesPorIdadeECID(Date dataInicial, Date dataFinal, AghCid cid)  throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidAtendimento.class, "CID");
	
		criteria.createAlias("CID."+MpmCidAtendimento.Fields.CID, "CI");
		criteria.createAlias("CID."+MpmCidAtendimento.Fields.ATENDIMENTO.toString(), "ATD",JoinType.INNER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC",JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("CI."+AghCid.Fields.CODIGO, cid.getCodigo().toString()));
		
		if (dataInicial != null && dataFinal != null) {
			Date dtInicio = DateUtil.obterDataComHoraInical(dataInicial);
			Date dtFim = DateUtil.obterDataComHoraFinal(dataFinal);
			criteria.add(Restrictions.between("PAC."+AipPacientes.Fields.DATA_NASCIMENTO.toString(), dtFim, dtInicio));
		}
		
		return executeCriteria(criteria); 
		
	}
}