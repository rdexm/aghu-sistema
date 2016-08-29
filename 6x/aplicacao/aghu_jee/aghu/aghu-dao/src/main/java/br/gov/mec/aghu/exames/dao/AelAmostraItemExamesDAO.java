package br.gov.mec.aghu.exames.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.coleta.vo.AelExamesAmostraVO;
import br.gov.mec.aghu.exames.vo.AelAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesFiltrosPesquisaVO;
import br.gov.mec.aghu.exames.vo.ResultadoMonitorPendenciasExamesVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class AelAmostraItemExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAmostraItemExames> {
	
	private static final long serialVersionUID = -7056114584493244964L;
	private static final String AIE = "AIE." ;
	private static final String ISE = "ISE." ;
	private static final String MOC = "MOC." ;
	private static final String UNF = "UNF." ;
	
	@Override
	protected void obterValorSequencialId(AelAmostraItemExames elemento) {
		if (elemento == null || elemento.getAelAmostras() == null || elemento.getAelItemSolicitacaoExames() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		if (elemento.getAelAmostras() == null) {
			throw new IllegalArgumentException("Associacao com AelAmostras nao informado!!!");
		}
		if (elemento.getAelItemSolicitacaoExames() == null) {
			throw new IllegalArgumentException("Associacao com AelItemSolicitacaoExames nao informado!!!");
		}
		
		AelAmostraItemExamesId id = new AelAmostraItemExamesId(); 
		id.setAmoSeqp(elemento.getAelAmostras().getId().getSeqp().intValue());
		id.setAmoSoeSeq(elemento.getAelAmostras().getId().getSoeSeq());
		id.setIseSeqp(elemento.getAelItemSolicitacaoExames().getId().getSeqp());
		id.setIseSoeSeq(elemento.getAelItemSolicitacaoExames().getId().getSoeSeq());
		
		elemento.setId(id);
	}
		
	
	public AelAmostraItemExames obterPorChavePrimaria(Integer iseSoeSeq, Short iseSeqp, Integer amoSoeSeq, Integer amoSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		
		return (AelAmostraItemExames)executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelAmostraItemExames> buscarItensAmostraExame(Integer iseSoeSeq, Short iseSeqp,Integer amoSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		//criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		
		return executeCriteria(criteria);
	}
	
	
	public List<AelAmostraItemExames> listarTodos(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		
		return executeCriteria(criteria);
	}
	
	
	
	
	
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class,"aie");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		
		criteria.add(Restrictions.eq("aie."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), itemSolicitacao));
		
		return executeCriteria(criteria);
	}
	
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class,"aie");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "amo");
		criteria.createAlias("amo."+AelAmostras.Fields.MATERIAL_ANALISE.toString(), "man");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		
		criteria.add(Restrictions.eq("aie."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), itemSolicitacao));
		
		return executeCriteria(criteria);
	}

	
	/**
	 * Pesquisa itens de exame de amostra atraves da amostra
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostra(Integer amoSoeSeq, Integer amoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE", JoinType.INNER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "ISE_EXA", JoinType.INNER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "ISE_RAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UEX", JoinType.LEFT_OUTER_JOIN);
		
		if (amoSoeSeq != null) {
			criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		}
		
		if (amoSeqp != null) {
			criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		}
		
		criteria.addOrder(Order.asc(AelAmostraItemExames.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	

	public Long obterNroApComDistinct(Integer soeSeq, Short seqp, Short unfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "aie");
		
		criteria.createAlias("aie." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");

		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");		
		
		criteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), Integer.valueOf(seqp)));

		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.isNotNull("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()));
		
		criteria.setProjection(Projections.distinct(
				   Projections.projectionList()
				   .add(Projections.property("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()))));
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	

	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostraComApNotNull(Integer amoSoeSeq, Integer amoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "aie");
		
		criteria.createAlias("aie." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "ISE_EX",JoinType.INNER_JOIN);
		
		if (amoSoeSeq != null) {
			criteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		}
		
		if (amoSeqp != null) {
			criteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		}
		
		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		
		criteria.add(Restrictions.isNotNull("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()));
		
		criteria.addOrder(Order.asc("aie." + AelAmostraItemExames.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorAmostraPorItemSolicitacaoExame(
			Integer iseSoeSeq, Short iseSeqp, Integer amoSoeSeq, Integer amoSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.ne(AelAmostraItemExames.Fields.SEQP.toString(), iseSeqp));
		
		return executeCriteria(criteria);
		
		
	}
	
	/**
	 * @HIST AelAmostraItemExamesDAO.buscarAelAmostraItemExamesPorItemSolicitacaoExameHist
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public String buscarAelAmostraItemExamesPorItemSolicitacaoExame(Integer iseSoeSeq, Short iseSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.createAlias(AelAmostraItemExames.Fields.AEL_EQUIPAMENTO_EXECUTADO.toString(), "equ", DetachedCriteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SEQP.toString(), iseSeqp));

		criteria.setProjection(Projections.distinct(Projections.property("equ."+AelEquipamentos.Fields.DESCRICAO.toString())));
		
		return (String)executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorItemSolicitacaoExameIds(Integer iseSoeSeq, Short iseSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SEQP.toString(), iseSeqp));	
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem a criteria para listar amostras da solicitação para recebimento através da solicitação e seqp (Amostra) da amostra
	 * @param amostra
	 * @return
	 */
	public DetachedCriteria getCriteriaListarAmostrasSolicitacaoRecebimento(Integer amoSoeSeq, Short amoSeqp){
		
		// Obtem a criteria principal - Alias AIE
		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, AIE.substring(0,3));
		criteriaAie.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), ISE.substring(0,3));
		criteriaAie.createAlias(ISE + AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), MOC.substring(0,3),DetachedCriteria.LEFT_JOIN);
		
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		
		// Considera amoSeqp somente quando informado
		if(amoSeqp != null){
			criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp.intValue()));
		}
		
		Criterion lhs = Restrictions.in(AelAmostraItemExames.Fields.SITUACAO.toString(), new DominioSituacaoAmostra[]{DominioSituacaoAmostra.G,DominioSituacaoAmostra.C,DominioSituacaoAmostra.U,DominioSituacaoAmostra.M});
		
		Criterion c1 = Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.A);
		
		Criterion c2 = Restrictions.eqProperty(MOC+AelMotivoCancelaExames.Fields.SEQ.toString(), ISE + "aelMotivoCancelaExames.seq");
		
		Criterion c3 = Restrictions.and(c2, Restrictions.eq(MOC + AelMotivoCancelaExames.Fields.IND_PERMITE_INCLUIR_RESULTADO.toString(), Boolean.TRUE));

		Criterion rhs = Restrictions.and(c1, c3);
		
		criteriaAie.add(Restrictions.or(lhs, rhs));
		
		return criteriaAie;
	}
	
	
	/**
	 * Obtem a criteria para listar amostras da solicitação para recebimento através da solicitação
	 * @param amostra
	 * @return
	 */
	public DetachedCriteria getCriteriaBuscarReceberAmostrasSolicitacaoCentralRecebimentoMateriais(AelAmostras amostra){
		
		// Obtem a criteria principal - Alias AIE
		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, AIE.substring(0,3));
		criteriaAie.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), ISE.substring(0,3));
		criteriaAie.createAlias(ISE + AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), MOC.substring(0,3),DetachedCriteria.LEFT_JOIN);
		
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amostra.getId().getSoeSeq()));
		
		Criterion lhs = Restrictions.in(AelAmostraItemExames.Fields.SITUACAO.toString(), new DominioSituacaoAmostra[]{DominioSituacaoAmostra.G,DominioSituacaoAmostra.C,DominioSituacaoAmostra.U,DominioSituacaoAmostra.M});
		
		Criterion c1 = Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.A);
		
		Criterion c2 = Restrictions.eqProperty(MOC+AelMotivoCancelaExames.Fields.SEQ.toString(), ISE + "aelMotivoCancelaExames.seq");
		
		Criterion c3 = Restrictions.and(c2, Restrictions.eq(MOC + AelMotivoCancelaExames.Fields.IND_PERMITE_INCLUIR_RESULTADO.toString(), Boolean.TRUE));

		Criterion rhs = Restrictions.and(c1, c3);
		
		criteriaAie.add(Restrictions.or(lhs, rhs));
		
		return criteriaAie;
	}
	 
	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoRecebimento(AelAmostras amostra) {
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = getCriteriaListarAmostrasSolicitacaoRecebimento(amostra.getId().getSoeSeq(), amostra.getId().getSeqp());
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		// Distinct na criteria principal
		return executeCriteria(criteriaAie);
	}
	
	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoRecebimentoPorSolicitacao(AelAmostras amostra) {
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = getCriteriaListarAmostrasSolicitacaoRecebimento(amostra.getId().getSoeSeq(), null);
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		// Distinct na criteria principal
		return executeCriteria(criteriaAie);
	}
	
	
	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento de uma unidade com
	 * a caracteristica de "Central de Recebimento de Materiais"
	 * @param amostra
	 * @param unidadeExecutora
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoRecebimentoCentralRecebimentoMateriais(AelAmostras amostra, AghUnidadesFuncionais unidadeExecutora) {
		
		// Alias
		final String UNF1 = "UNF1.";
		final String UNF2 = "UNF2.";
		
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = getCriteriaListarAmostrasSolicitacaoRecebimento(amostra.getId().getSoeSeq(), amostra.getId().getSeqp());
		
		// SubCriteria da criteria principal para unidade funcional - Alias UNF
		DetachedCriteria subQueryUnf = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF.substring(0,3));
		subQueryUnf.setProjection(Projections.property(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		
		// SubCriteria de UNF(IN) na unidade funcional - Alias UNF1
		DetachedCriteria subQueryInUnf1 = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF1.substring(0,4));
		subQueryInUnf1.setProjection(Projections.property(UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));

		// SubCriteria de UNF1(EXISTS) na unidade funcional - Alias UNF2
		DetachedCriteria subQueryExistsUnf2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class,UNF2.substring(0,4));
		subQueryExistsUnf2.setProjection(Projections.property(UNF2 + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subQueryExistsUnf2.add(Restrictions.eqProperty(UNF2+AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		subQueryExistsUnf2.add(Restrictions.eq(UNF2+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS));
		subQueryInUnf1.add(Subqueries.exists(subQueryExistsUnf2));
		
		subQueryUnf.add(Property.forName(UNF + AghUnidadesFuncionais.Fields.UNF_SEQ.toString()).in(subQueryInUnf1));
		//subQueryUnf.addOrder(Order.asc(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaAie.add(Property.forName(ISE + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString()).in(subQueryUnf));
		
		// Distinct na criteria principal
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteriaAie);
		
	}
	
	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento de uma unidade com
	 * a caracteristica de "Central de Recebimento de Materiais"
	 * @param amostra
	 * @param unidadeExecutora
	 * @return
	 */
	public List<AelAmostraItemExames> buscarReceberAmostrasSolicitacaoCentralRecebimentoMateriais(AelAmostras amostra, AghUnidadesFuncionais unidadeExecutora) {
		
		// Alias
		final String UNF1 = "UNF1.";
		final String UNF2 = "UNF2.";
		
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = getCriteriaBuscarReceberAmostrasSolicitacaoCentralRecebimentoMateriais(amostra);
		
		// SubCriteria da criteria principal para unidade funcional - Alias UNF
		DetachedCriteria subQueryUnf = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF.substring(0,3));
		subQueryUnf.setProjection(Projections.property(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		
		// SubCriteria de UNF(IN) na unidade funcional - Alias UNF1
		DetachedCriteria subQueryInUnf1 = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF1.substring(0,4));
		subQueryInUnf1.setProjection(Projections.property(UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));

		// SubCriteria de UNF1(EXISTS) na unidade funcional - Alias UNF2
		DetachedCriteria subQueryExistsUnf2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class,UNF2.substring(0,4));
		subQueryExistsUnf2.setProjection(Projections.property(UNF2 + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subQueryExistsUnf2.add(Restrictions.eqProperty(UNF2+AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		subQueryExistsUnf2.add(Restrictions.eq(UNF2+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS));
		subQueryInUnf1.add(Subqueries.exists(subQueryExistsUnf2));
		
		subQueryUnf.add(Property.forName(UNF + AghUnidadesFuncionais.Fields.UNF_SEQ.toString()).in(subQueryInUnf1));
		//subQueryUnf.addOrder(Order.asc(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaAie.add(Property.forName(ISE + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString()).in(subQueryUnf));
		
		// Distinct na criteria principal
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		criteriaAie.addOrder(Order.asc(AIE + AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString()));
		criteriaAie.addOrder(Order.asc(AIE + AelAmostraItemExames.Fields.AMO_SEQP.toString()));
		
		return executeCriteria(criteriaAie);
		
	}

	/**
	 * Obtem a criteria para listar amostras da solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public DetachedCriteria getCriteriaListarAmostrasSolicitacaoVoltarAmostra(AelAmostras amostra){
		
		// Obtem a criteria principal - Alias AIE
		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, AIE.substring(0,3));
		criteriaAie.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), ISE.substring(0,3));
	
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amostra.getId().getSoeSeq()));
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amostra.getId().getSeqp().intValue()));
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.R));
		
		return criteriaAie;
		
		
	}
	
	
	/**
	 * Obtem a criteria para listar amostras da solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public DetachedCriteria getCriteriaListarAmostrasSolicitacaoVoltarAmostraPorSolicitacao(AelAmostras amostra){
		
		// Obtem a criteria principal - Alias AIE
		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, AIE.substring(0,3));
		criteriaAie.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), ISE.substring(0,3));
	
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amostra.getId().getSoeSeq()));
		criteriaAie.add(Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.R));
		
		return criteriaAie;
		
	
	}
	
	/**
	 * Pesquisa amostra item exame através da solicitação e situação informada
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> pesquisarAmostraItemExamePorSolicitacaoESituacao(AelAmostras amostra, DominioSituacaoAmostra situacaoIgual) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amostra.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), situacaoIgual));
		
		return executeCriteria(criteria);
	}
	
	
	
	/**
	 * Pesquisa amostra itens exame através da amostra com a situacao U (Recebida Unidade Coleta) para operação "voltar amostra na unidade de coleta"
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoVoltarAmostraUnidadeColeta(AelAmostras amostra) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amostra.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amostra.getId().getSeqp().intValue()));
		// Compara situação igual a U (Recebida Unidade Coleta)  
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.U));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtém amostra itens exame para voltar amostra na unidade executora de exames ou central de recebimento de materiais
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	public AelAmostraItemExames obterAmostraSolicitacaoVoltarAmostraUnidadeExecutoraExamesCentralERecebimentoMateriais(AelAmostraItemExames amostraItemExames, AelAmostras amostra) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(), amostraItemExames.getAelItemSolicitacaoExames().getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SEQP.toString(), amostraItemExames.getAelItemSolicitacaoExames().getId().getSeqp()));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(),  amostra.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), amostra.getId().getSeqp().intValue()));
		
		return (AelAmostraItemExames) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoVoltarAmostra(AelAmostras amostra) {
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = getCriteriaListarAmostrasSolicitacaoVoltarAmostra(amostra);
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		// Distinct na criteria principal
		return executeCriteria(criteriaAie);
	}

	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoVoltarAmostraCentralRecebimentoMateriais(AelAmostras amostra, AghUnidadesFuncionais unidadeExecutora) {
		// Alias
		final String UNF1 = "UNF1.";
		final String UNF2 = "UNF2.";
		
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = this.getCriteriaListarAmostrasSolicitacaoVoltarAmostra(amostra);
		
		// SubCriteria da criteria principal para unidade funcional - Alias UNF
		DetachedCriteria subQueryUnf = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF.substring(0,3));
		subQueryUnf.setProjection(Projections.property(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		
		// SubCriteria de UNF(IN) na unidade funcional - Alias UNF1
		DetachedCriteria subQueryInUnf1 = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF1.substring(0,4));
		subQueryInUnf1.setProjection(Projections.property(UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		//subQueryInUnf1.add(Restrictions.or(Restrictions.eq(UNF1 + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(),unidadeExecutora.getUnfSeq()), Restrictions.eq(UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(),unidadeExecutora.getSeq())));
		
		// SubCriteria de UNF1(EXISTS) na unidade funcional - Alias UNF2
		DetachedCriteria subQueryExistsUnf2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class,UNF2.substring(0,4));
		subQueryExistsUnf2.setProjection(Projections.property(UNF2 + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subQueryExistsUnf2.add(Restrictions.eqProperty(UNF2+AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		subQueryExistsUnf2.add(Restrictions.eq(UNF2+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS));
		subQueryInUnf1.add(Subqueries.exists(subQueryExistsUnf2));
		
		subQueryUnf.add(Property.forName(UNF + AghUnidadesFuncionais.Fields.UNF_SEQ.toString()).in(subQueryInUnf1));
		//subQueryUnf.addOrder(Order.asc(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaAie.add(Property.forName(ISE + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString()).in(subQueryUnf));
		
		// Distinct na criteria principal
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteriaAie);
	}
	
	
	/**
	 * Obtem uma listagem de amostras para solicitação para recebimento
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAmostrasSolicitacaoVoltarAmostraCentralRecebimentoMateriaisPorSolicitacao(AelAmostras amostra, AghUnidadesFuncionais unidadeExecutora) {
		// Alias
		final String UNF1 = "UNF1.";
		final String UNF2 = "UNF2.";
		
		// Obtem a criteria principal
		DetachedCriteria criteriaAie = this.getCriteriaListarAmostrasSolicitacaoVoltarAmostraPorSolicitacao(amostra);
		
		// SubCriteria da criteria principal para unidade funcional - Alias UNF
		DetachedCriteria subQueryUnf = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF.substring(0,3));
		subQueryUnf.setProjection(Projections.property(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		
		// SubCriteria de UNF(IN) na unidade funcional - Alias UNF1
		DetachedCriteria subQueryInUnf1 = DetachedCriteria.forClass(AghUnidadesFuncionais.class,UNF1.substring(0,4));
		subQueryInUnf1.setProjection(Projections.property(UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		//subQueryInUnf1.add(Restrictions.or(Restrictions.eq(UNF1 + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(),unidadeExecutora.getUnfSeq()), Restrictions.eq(UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(),unidadeExecutora.getSeq())));
		
		// SubCriteria de UNF1(EXISTS) na unidade funcional - Alias UNF2
		DetachedCriteria subQueryExistsUnf2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class,UNF2.substring(0,4));
		subQueryExistsUnf2.setProjection(Projections.property(UNF2 + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subQueryExistsUnf2.add(Restrictions.eqProperty(UNF2+AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), UNF1 + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		subQueryExistsUnf2.add(Restrictions.eq(UNF2+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS));
		subQueryInUnf1.add(Subqueries.exists(subQueryExistsUnf2));
		
		subQueryUnf.add(Property.forName(UNF + AghUnidadesFuncionais.Fields.UNF_SEQ.toString()).in(subQueryInUnf1));
		//subQueryUnf.addOrder(Order.asc(UNF + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaAie.add(Property.forName(ISE + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString()).in(subQueryUnf));
		
		// Distinct na criteria principal
		criteriaAie.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteriaAie);
	}
	
	/**
	 * Retorna AelAmostraItemExames original
	 * @param id
	 * @return
	 */
	/*public AelAmostraItemExames obterOriginal(AelAmostraItemExamesId id) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.DT_IMP_MAPA.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.EXECUTADO.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.IND_ENVIADO.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.NRO_MAPA.toString());
		hql.append(", o.").append(AelAmostraItemExames.Fields.ALTERADO_EM.toString());
		
		hql.append(" from ").append(AelAmostraItemExames.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString());
		hql.append(" left outer join o.").append(AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS.toString());
		hql.append(" left outer join o.").append(AelAmostraItemExames.Fields.SERVIDOR.toString());
		hql.append(" left outer join o.").append(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString());
		
		hql.append(" where o.").append(AelAmostraItemExames.Fields.ID.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		AelAmostraItemExames retorno = null;
		
		Object[] campos = null;
		@SuppressWarnings("rawtypes")
		List list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			campos = (Object[]) list.get(0);			
		}
		 
		if(campos != null) {
			retorno = new AelAmostraItemExames();

			retorno.setId(id);
			retorno.setAelAmostras((AelAmostras) campos[0]);
			retorno.setAelEquipamentos((AelEquipamentos) campos[1]);
			retorno.setAelItemSolicitacaoExames((AelItemSolicitacaoExames) campos[2]);
			retorno.setServidor((RapServidores) campos[3]);
			retorno.setSituacao((DominioSituacaoAmostra) campos[4]);
			retorno.setDtImpMapa((Date) campos[5]);
			retorno.setEquSeqExecutado((Short) campos[6]);
			retorno.setIndEnviado((Boolean) campos[7]);
			retorno.setNroMapa((Integer) campos[8]);
			retorno.setAlteradoEm((Date) campos[9]);
			
		}		
		
		return retorno;
		
	}*/
	
	/**
	 * Pesquisa Amostra Item Exame para #5849 - Informar Solicitação de Exame para Digitação
	 */
	public List<AelItemSolicitacaoExames> pesquisarInformarSolicitacaoExameDigitacaoController(final Integer solicitacaoExameSeq, final Integer amostraSeqp, final Short seqUnidadeFuncional) {

		CoreUtil.validaParametrosObrigatorios(solicitacaoExameSeq);

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		
		criteria.createAlias("ISE." + AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), "SER_RESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_RESP." + RapServidores.Fields.PESSOA_FISICA.toString(), "SER_RESP_PES", JoinType.LEFT_OUTER_JOIN);


		if(amostraSeqp != null){
			criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE", JoinType.INNER_JOIN);
		}
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), "MOC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UFE", JoinType.INNER_JOIN);
		
		// Migração
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "SIT", JoinType.LEFT_OUTER_JOIN);

		Criterion criterionA = Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), new String[]{"AE", "EX", "EO", "LI"});

		Criterion criterionB1 = Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), new String[]{"CA"});
		Criterion criterionB2 = Restrictions.eq("MOC." + AelMotivoCancelaExames.Fields.IND_PERMITE_INCLUIR_RESULTADO.toString(), Boolean.TRUE);
		Criterion criterionC = Restrictions.and(criterionB1, criterionB2);

		criteria.add(Restrictions.or(criterionA, criterionC));

		if(amostraSeqp != null){
			criteria.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), solicitacaoExameSeq));
			criteria.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), amostraSeqp.intValue()));
		}else{
			criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacaoExameSeq));
		}

		criteria.add(Restrictions.eq("UFE." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidadeFuncional));

		if(amostraSeqp != null){
			criteria.addOrder(Order.asc("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()));
			criteria.addOrder(Order.asc("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString()));
		}else{
			criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		}
		
		this.entityManagerClear();
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa Amostra Item Exame para tratamento de protocolo único temporário do interfaceamento no recebimento de amostras
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	public List<AelAmostraItemExames> pesquisarAmostraItemExamesTratarProtocoloUnicoTemporarios(final Integer amoSoeSeq, final Integer amoSeqp) {
		
		DetachedCriteria criteriaAie = this.getCriteriaPesquisarAmostraItemExamesTratarProtocoloUnico(amoSoeSeq, amoSeqp);
		criteriaAie.add(Restrictions.eq("EQU." + AelEquipamentos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteriaAie);
		
	}

	/**
	 * Pesquisa Amostra Item Exame para tratamento de protocolo único do interfaceamento no recebimento de amostras
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @return
	 */
	public List<AelAmostraItemExames> pesquisarAmostraItemExamesTratarProtocoloUnico(final Integer amoSoeSeq, final Integer amoSeqp) {
		return executeCriteria(this.getCriteriaPesquisarAmostraItemExamesTratarProtocoloUnico(amoSoeSeq, amoSeqp));
	}
	
	/**
	 * Pesquisa Amostra Item Exame para geração de de protocolo único com solicitação de exame
	 * @param amostra
	 * @return
	 */
	public List<AelAmostraItemExames> pesquisarAmostraItemExamesGerarProtocoloUnicoLwsComSolicitacaoExame(
			final Integer amoSoeSeq, final Integer amoSeqp, final String siglaExame) {
		
		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, "AIE");
		
		criteriaAie.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE", JoinType.INNER_JOIN);
		criteriaAie.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES_SOLIC.toString(), "SOE", JoinType.INNER_JOIN);
			
		criteriaAie.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteriaAie.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));

		if(StringUtils.isNotBlank(siglaExame)) {
			criteriaAie.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA, siglaExame));
		}
		
		return executeCriteria(criteriaAie);
		
	}
	
	public List<AelAmostraItemExames> pesquisarAmostraItemExameSituacaoGeradaPorItemHorarioAgendado(Short hedGaeUnfSeq, 
			Integer hedGaeSeqp, Date hedDthrAgenda) {
		String aliasIha = "iha";
		String aliasIse = "ise";
		String aliasAie = "aie";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, aliasAie);
		criteria.createAlias(aliasAie + separador + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES, aliasIse);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO, aliasIha);

		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ, hedGaeUnfSeq));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_SEQP, hedGaeSeqp));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA, hedDthrAgenda));
		criteria.add(Restrictions.eq(aliasAie + separador + AelAmostraItemExames.Fields.SITUACAO, DominioSituacaoAmostra.G));
		return executeCriteria(criteria);
	}
		
	
	public Long pesquisarAmostraItemExamesCount(Integer solicitacao, 
			Integer amostra, AelEquipamentos equipamento, String sigla, 
			Short unfSeq, List<Short> aghUnfSeqList, DominioSimNao enviado) {
	
		DetachedCriteria criteria = this.obterCriterioPesquisarAmostraItemExames(
				solicitacao, amostra, equipamento, sigla, aghUnfSeqList, enviado, unfSeq);
		
		return this.executeCriteriaCount(criteria);
		
	}
		
	
	public Long obterCountIse(Integer aghSolicitacao, Integer aghAmostra,List<String> listSitCodigo) {
		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class,"aie");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		
		criteria.add(Restrictions.eq("aie."+AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), aghSolicitacao)); //soeSeq
		criteria.add(Restrictions.eq("aie."+AelAmostraItemExames.Fields.AMO_SEQP.toString(), aghAmostra)); //seqp
		
		criteria.add(Restrictions.in("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), listSitCodigo)); //seqp
		return executeCriteriaCount(criteria);
	}
	
	/**
	 *  Obtém Amostra Item de Exame para operação: Voltar Protocólo Único de Interfaceamento LWS
	 * @param p_agh_solicitacao
	 * @param v_agh_item_sol
	 * @param p_agh_amostra
	 * @return
	 */
	public AelAmostraItemExames obterAmostraItemExamesVoltarProtocoloUnicoLws(Integer p_agh_solicitacao, Short v_agh_item_sol, Integer p_agh_amostra){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SOE_SEQ.toString(), p_agh_solicitacao));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SEQP.toString(), v_agh_item_sol));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), p_agh_solicitacao));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), p_agh_amostra));
		
		return (AelAmostraItemExames) this.executeCriteriaUniqueResult(criteria);
		
	}
	
	
	public  Long pesquisarAmostraItemExamesPorSoeSeqSeqp(Integer aghSolicitacao, Short aghItemSol){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(), aghSolicitacao));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SEQP.toString(), aghItemSol));
		
		return this.executeCriteriaCount(criteria);
		
	}
	
	
	public List<AelAmostraItemExamesVO> pesquisarAmostraItemExamesTodos(Integer solicitacao, 
			Integer amostra, AelEquipamentos equipamento, String sigla, DominioSimNao enviado,
			List<Short> aghUnfSeqList, Short unfSeq) {
		
		DetachedCriteria criteria = this.obterCriterioPesquisarAmostraItemExames(solicitacao, amostra, equipamento, 
				sigla, aghUnfSeqList, enviado, unfSeq);

		return this.executeCriteria(criteria);
	}
	
	protected DetachedCriteria obterCriterioPesquisarAmostraItemExames(Integer solicitacao, 
			Integer amostra, AelEquipamentos equipamento, String sigla, List<Short> aghUnfSeqList,
			DominioSimNao enviado, Short unfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		
		criteria.createAlias(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		criteria.createCriteria(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "AMO");
		criteria.createCriteria(AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS.toString(), "EQP", Criteria.LEFT_JOIN);
		criteria.createCriteria("ISE.".concat(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString()), 
				"EMA", Criteria.LEFT_JOIN);
		criteria.createCriteria("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), 
				"EXA", Criteria.LEFT_JOIN);
		criteria.createCriteria("EMA.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), 
				"MAT", Criteria.LEFT_JOIN);
		criteria.createCriteria("AMO.".concat(AelAmostras.Fields.CONFIG_MAPA.toString()), "CFM", Criteria.LEFT_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString()), 
				AelAmostraItemExamesVO.Fields.ISE_SOE_SEQ.toString());
		projection.add(Property.forName(AelAmostraItemExames.Fields.ISE_SEQP.toString()), 
				AelAmostraItemExamesVO.Fields.ISE_SEQP.toString());
		projection.add(Property.forName(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()), 
				AelAmostraItemExamesVO.Fields.AMO_SOE_SEQ.toString());
		projection.add(Property.forName(AelAmostraItemExames.Fields.AMO_SEQP.toString()), 
				AelAmostraItemExamesVO.Fields.AMO_SEQP.toString());
		projection.add(Property.forName(AelAmostraItemExames.Fields.NRO_MAPA.toString()), 
				AelAmostraItemExamesVO.Fields.NRO_MAPA.toString());
		projection.add(Property.forName(AelAmostraItemExames.Fields.IND_ENVIADO.toString()), 
				AelAmostraItemExamesVO.Fields.IND_ENVIADO.toString());
		projection.add(Property.forName("CFM.".concat(AelConfigMapa.Fields.SEQ.toString())), 
				AelAmostraItemExamesVO.Fields.CONFIG_MAPA_SEQ.toString());
		projection.add(Property.forName("EQP.".concat(AelEquipamentos.Fields.DESCRICAO.toString())), 
				AelAmostraItemExamesVO.Fields.EQUIPAMENTO_DESCRICAO.toString());
		projection.add(Property.forName("EXA.".concat(AelExames.Fields.DESCRICAO_USUAL.toString())), 
				AelAmostraItemExamesVO.Fields.DESCRICAO_USUAL.toString());
		projection.add(Property.forName("MAT.".concat(AelMateriaisAnalises.Fields.DESCRICAO.toString())), 
				AelAmostraItemExamesVO.Fields.DESC_MATERIAL_ANALISE.toString());
		projection.add(Property.forName("ISE.".concat(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())), 
				AelAmostraItemExamesVO.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		criteria.setProjection(projection);		
		
		if(solicitacao != null) {
			criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), 
					solicitacao));
		}
		
		if(amostra != null) {
			criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), 
					amostra));
		}
		
		if(equipamento != null) {
			criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS_SEQ.toString(), 
					equipamento.getSeq()));
		}
		
		if(StringUtils.isNotBlank(sigla)){
			criteria.add(Restrictions.eq("ISE.".concat(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()), 
					sigla));
		}
		
		if(enviado != null) {
			criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.IND_ENVIADO.toString(), 
					enviado.isSim()));
		}
		
		
		/*
		 * Equipamento
		 */
		
		DetachedCriteria subQueryEquipamento = DetachedCriteria.forClass(AelEquipamentos.class);

		ProjectionList pListsubQueryEquipamento = Projections.projectionList();
		pListsubQueryEquipamento.add(Projections.property(AelEquipamentos.Fields.SEQ.toString()));
		subQueryEquipamento.setProjection(pListsubQueryEquipamento);
		subQueryEquipamento.add(Restrictions.eq(AelEquipamentos.Fields.POSSUI_INTERFACE.toString(), Boolean.TRUE));
		
		criteria.add(Subqueries.propertyIn(AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS_SEQ.toString(),subQueryEquipamento));
		
		/*
		 * Situação
		 */
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.R));
		
		/*
		 *  PRIMEIRA PARTE da consulta secundária de amostras
		 *  Considera amostras com RECEBIDAS e com a unidade funcional IDENTIFICADA PELO USUÁRIO
		 */
		DetachedCriteria subQueryAmor = DetachedCriteria.forClass(AelAmostras.class, "AMOR");
		subQueryAmor.setProjection(Projections.property("AMOR." + AelAmostras.Fields.SOE_SEQ.toString()));
		subQueryAmor.add(Restrictions.eq("AMOR." + AelAmostras.Fields.SITUACAO.toString(), DominioSituacaoAmostra.R)); // RECEBIDA
		subQueryAmor.add(Restrictions.eq("AMOR." + AelAmostras.Fields.UNF_SEQ.toString(), unfSeq)); // Unidade funcional IDENTIFICADA PELO USUÁRIO
		
		subQueryAmor.add(Property.forName("AMOR." + AelAmostras.Fields.SOE_SEQ.toString()).eqProperty("AMO." + AelAmostras.Fields.SOE_SEQ.toString()));
		subQueryAmor.add(Property.forName("AMOR." + AelAmostras.Fields.SEQP.toString()).eqProperty("AMO." + AelAmostras.Fields.SEQP.toString()));

		/*
		 *  SEGUNDA PARTE da consulta secundária de amostras
		 *  Considera amostras com a unidade funcional na CENTRAL DE RECEBIMENTO DE MATERIAIS
		 */
		DetachedCriteria subQueryAmor2 = DetachedCriteria.forClass(AelAmostras.class, "AMOR2");
		subQueryAmor2.setProjection(Projections.property("AMOR2." + AelAmostras.Fields.SOE_SEQ.toString()));
		subQueryAmor2.createAlias("AMOR2." + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		subQueryAmor2.createAlias("UNF." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "UNF1");
		subQueryAmor2.createAlias("UNF1." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "UNF2");
		
		// Considera conjunto na lista de unidades funcionais na CENTRAL DE RECEBIMENTO DE MATERIAIS
		subQueryAmor2.add(Restrictions.in("UNF2." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), aghUnfSeqList));
		
		subQueryAmor2.add(Property.forName("AMOR2." + AelAmostras.Fields.SOE_SEQ.toString()).eqProperty("AMO." + AelAmostras.Fields.SOE_SEQ.toString()));
		subQueryAmor2.add(Property.forName("AMOR2." + AelAmostras.Fields.SEQP.toString()).eqProperty("AMO." + AelAmostras.Fields.SEQP.toString()));

		/*
		 * OR da PRIMEIRA e SEGUNDA PARTE da consulta secundária de amostras
		 */
		Criterion c1 = Subqueries.exists(subQueryAmor); // Critério da PRIMEIRA PARTE da consulta secundária de amostras
		Criterion c2 = Subqueries.exists(subQueryAmor2); // Critério da SEGUNDA PARTE da consulta secundária de amostras
		criteria.add(Restrictions.or(c1,c2)); // Cria o OR

		criteria.setResultTransformer(Transformers.aliasToBean(AelAmostraItemExamesVO.class));

		return criteria;
	}
	
	/**
	 * Retorna uma lista de AelAmostraItemExames
	 * 
	 * @param equipamentosSeqList
	 * @param amoSoeSeqList
	 * @param amoSeqpList
	 * @return
	 */
	public List<AelAmostraItemExamesVO> pesquisarAmostraItemExames(Integer solicitacao, 
			Integer amostra, AelEquipamentos equipamento, String sigla, List<Short> aghUnfSeqList, 
			DominioSimNao enviado,  Short unfSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = this.obterCriterioPesquisarAmostraItemExames(solicitacao, amostra, equipamento, 
				sigla, aghUnfSeqList, enviado, unfSeq);

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	/**
	 * Obtem a criteria para tratamento de protocolo único do interfaceamento no recebimento de amostras
	 * @param amostra
	 * @return
	 */
	public DetachedCriteria getCriteriaPesquisarAmostraItemExamesTratarProtocoloUnico(final Integer amoSoeSeq, final Integer amoSeqp){
		
		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, "AIE");
		
		criteriaAie.createAlias("AIE." + AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS.toString(), "EQU", Criteria.INNER_JOIN);
		criteriaAie.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE", Criteria.INNER_JOIN);
		
		criteriaAie.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteriaAie.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		
		criteriaAie.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.SITUACAO.toString(), DominioSituacaoAmostra.R));
		
		criteriaAie.add(Restrictions.eq("EQU." + AelEquipamentos.Fields.CARGA_AUTOMATICA.toString(), Boolean.TRUE));
		
		return criteriaAie;
		
		
	}
	
	/**
	 * Lista todas as unidades funcionais que contenham salas
	 * 
	 * @param parametro
	 * @return lista de unidades funcionais
	 */
	public List<AelAmostraItemExames> listarAmostrasComHorarioAgendado(final Short hedGaeUnfSeq, final Integer hedGaeSeqp, final Date hedDthrAgenda) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class,"aie");
		
		criteria.setProjection(Projections.property(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()));
		criteria.setProjection(Projections.property(AelAmostraItemExames.Fields.AMO_SEQP.toString()));

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		subCriteria.setProjection(Projections.property(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString()));
		subCriteria.setProjection(Projections.property(AelItemHorarioAgendado.Fields.ISE_SEQP.toString()));
		subCriteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(), hedGaeUnfSeq));
		subCriteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		subCriteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), hedDthrAgenda));
		
		subCriteria.add(Restrictions.eqProperty("aie." + AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(), "iha."+AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("aie." + AelAmostraItemExames.Fields.ISE_SEQP.toString(), "iha."+AelItemHorarioAgendado.Fields.ISE_SEQP.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
		return executeCriteria(criteria);
	}
	
	public List<AelExamesAmostraVO> obterAmostraItemExamesPorAmostra(Integer soeSeq, Short seqp) {
        String aliasAie = "aie";
        String aliasAmo = "amo";
        String aliasIse = "ise";
        String aliasVem = "vem";
        String separador = ".";

        DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, aliasAie);
        criteria.createAlias(aliasAie + separador + AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), aliasAmo, Criteria.INNER_JOIN);
        criteria.createAlias(aliasAie + separador + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), aliasIse, Criteria.INNER_JOIN);
        criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), aliasVem, Criteria.INNER_JOIN);
		ProjectionList projection = Projections.projectionList();
        	projection.add(Property.forName(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), 
        			AelExamesAmostraVO.Fields.ISE_SOE_SEQ.toString());
        	projection.add(Property.forName(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString()),
        			AelExamesAmostraVO.Fields.ISE_SEQP.toString());
        	projection.add(Property.forName(aliasVem + separador + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()),
        			AelExamesAmostraVO.Fields.DESCRICAO_USUAL.toString());
        	projection.add(Property.forName(aliasIse + separador + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()),
        			AelExamesAmostraVO.Fields.TIPO_COLETA.toString());
        	projection.add(Property.forName(aliasAmo + separador + AelAmostras.Fields.UNF_SEQ.toString()),
        			AelExamesAmostraVO.Fields.UNF_SEQ.toString());
        	projection.add(Property.forName(aliasAie + separador + AelAmostraItemExames.Fields.SITUACAO.toString()),
        			AelExamesAmostraVO.Fields.SITUACAO.toString());
        criteria.setProjection(projection);
        criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), soeSeq));
        criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SEQP.toString(), seqp.intValue()));
        
        criteria.addOrder(Order.asc(aliasAie + separador + AelAmostraItemExames.Fields.ISE_SEQP.toString()));
        
        criteria.setResultTransformer(Transformers.aliasToBean(AelExamesAmostraVO.class));

        return this.executeCriteria(criteria);

  }
	
	public List<AelAmostraItemExames> pesquisarPorItemSolicitacaoExame(AelItemSolicitacaoExamesId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "aie");
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SEQP.toString(), id.getSeqp()));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(), id.getSoeSeq()));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa amostra itens exame com situação G (Gerada) ou C (Coletada) através do número da solicitação
	 * @param soeSeq
	 * @return
	 */
	public List<AelAmostraItemExames> pesquisarAmostraItemExameGeradaColetadaPorSolicitacao(Integer soeSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.in(AelAmostraItemExames.Fields.SITUACAO.toString(), Arrays.asList(DominioSituacaoAmostra.G, DominioSituacaoAmostra.C)));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa amostra itens exame com situação G (Gerada) ou C (Coletada) através do número da solicitação
	 * @param soeSeq
	 * @return
	 */
	public List<AelAmostraItemExames> pesquisarReceberTodasAmostrasCentralRecebimentoMateriais(Integer amoSoeSeq, Integer iseSoeSeq, Short iseSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		
		return executeCriteria(criteria);
	}

	/**
	 * 	Pesquisa pendências de exames para o Monitor de Pendências de Exames
	 * @param filtrosPesquisa
	 * @param situacaoItemSolicitacaoCodigo 
	 * @param maxResult 
	 * @return
	 */
	public List<ResultadoMonitorPendenciasExamesVO> pesquisarMonitorPendenciasExames(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa, String situacaoItemSolicitacaoCodigo, Integer maxResult) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "AIE"); // ALIAS AIE

		// Aliases
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE"); // ALIAS ISE
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA"); // ALIAS EXA
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "SIT");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA");
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN"); // ALIAS MAN
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE"); // ALIAS SOE
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "AMO"); // ALIAS AMO
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "EXT");
		
		// Projections
		ProjectionList projection = Projections.projectionList()
		.add(Projections.max("EXT." +  AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()), ResultadoMonitorPendenciasExamesVO.Fields.DTHR_EVENTO.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.SOE_SEQ.toString()), ResultadoMonitorPendenciasExamesVO.Fields.SOE_SEQ.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.NRO_UNICO.toString()), ResultadoMonitorPendenciasExamesVO.Fields.NRO_UNICO.toString())
		.add(Projections.groupProperty("ISE." +  AelItemSolicitacaoExames.Fields.SEQP.toString()), ResultadoMonitorPendenciasExamesVO.Fields.ISE_SEQP.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.SEQP.toString()), ResultadoMonitorPendenciasExamesVO.Fields.AMO_SEQP.toString())
		.add(Projections.groupProperty("EXA." +  AelExames.Fields.DESCRICAO_USUAL.toString()), ResultadoMonitorPendenciasExamesVO.Fields.EXAME.toString())
		.add(Projections.groupProperty("MAN." +  AelMateriaisAnalises.Fields.DESCRICAO.toString()), ResultadoMonitorPendenciasExamesVO.Fields.MATERIAL.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.UNID_TEMPO_INTERVALO_COLETA.toString()), ResultadoMonitorPendenciasExamesVO.Fields.UNID_TEMPO_INTERVALO_COLETA.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.TEMPO_INTERVALO_COLETA.toString()), ResultadoMonitorPendenciasExamesVO.Fields.TEMPO_INTERVALO_COLETA.toString())
		.add(Projections.groupProperty("ISE." +  AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()), ResultadoMonitorPendenciasExamesVO.Fields.ISE_UFE_UNFSEQ.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.UNF_SEQ.toString()), ResultadoMonitorPendenciasExamesVO.Fields.AMO_UNFSEQ.toString())
		.add(Projections.groupProperty("AMO." +  AelAmostras.Fields.DT_NUMERO_UNICO.toString()), ResultadoMonitorPendenciasExamesVO.Fields.DT_NUMERO_UNICO.toString())
		.add(Projections.groupProperty("AIE." +  AelAmostraItemExames.Fields.IND_ENVIADO.toString()), ResultadoMonitorPendenciasExamesVO.Fields.IND_ENVIADO.toString())
		.add(Projections.groupProperty("AIE." +  AelAmostraItemExames.Fields.ORIGEM_MAPA.toString()), ResultadoMonitorPendenciasExamesVO.Fields.ORIGEM_MAPA.toString())
		.add(Projections.groupProperty("EXA." +  AelExames.Fields.SIGLA.toString()), ResultadoMonitorPendenciasExamesVO.Fields.UFE_EMA_EXA_SIGLA.toString())
		.add(Projections.groupProperty("MAN." +  AelMateriaisAnalises.Fields.SEQ.toString()), ResultadoMonitorPendenciasExamesVO.Fields.UFE_EMA_MAN_SEQ.toString());
		criteria.setProjection(projection);
		
		// Determina os critérios da situação da amostra através da aba selecionada e informada no filtro
		String codigoSituacaoItemSolicitacao = null;
		DominioSituacaoAmostra situacaoAmostra = null;
		
		switch (filtrosPesquisa.getViewMonitorPendenciasExames()) {
		case AREA_EXECUTORA:
			codigoSituacaoItemSolicitacao = DominioSituacaoItemSolicitacaoExame.AE.toString();
			situacaoAmostra = DominioSituacaoAmostra.R;
			break;
		case EXECUTANDO:
			codigoSituacaoItemSolicitacao = DominioSituacaoItemSolicitacaoExame.EX.toString();
			situacaoAmostra = DominioSituacaoAmostra.R;
			break;
		case COLETADO:
			codigoSituacaoItemSolicitacao = DominioSituacaoItemSolicitacaoExame.CO.toString();
			situacaoAmostra = DominioSituacaoAmostra.C;
			break;
		case EM_COLETA:
			codigoSituacaoItemSolicitacao = DominioSituacaoItemSolicitacaoExame.EC.toString();
			situacaoAmostra = DominioSituacaoAmostra.M;
			break;
		}
		
		// Critérios da situação da amostra
		criteria.add(Restrictions.ilike("SIT." + AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigoSituacaoItemSolicitacao));
		criteria.add(Restrictions.eq("AMO." + AelAmostras.Fields.SITUACAO.toString(), situacaoAmostra));
		
		/*
		 * Critérios genéricos e migrados de p_seta_default_where
		 */
		
		
		// Parte migrada de p_seta_default_where: Quando informados os filtros da data de referência
		if (filtrosPesquisa.getUnidadeFuncionalExames() != null) {
			
			criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), filtrosPesquisa.getUnidadeFuncionalExames()));
			criteria.add(Restrictions.eq("AMO." + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString(), filtrosPesquisa.getUnidadeFuncionalExames()));

			criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ISE_UFE");

			DetachedCriteria subQueryUfeExists = DetachedCriteria.forClass(AelUnfExecutaExames.class, "UFE");
			subQueryUfeExists.setProjection(Projections.property("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString()));
			subQueryUfeExists.add(Property.forName("UFE." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()).eqProperty("ISE_UFE." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()));
			subQueryUfeExists.add(Property.forName("UFE." + AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()).eqProperty("ISE_UFE." + AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()));
			subQueryUfeExists.add(Property.forName("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString()).eqProperty("ISE_UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString()));
			subQueryUfeExists.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.IND_MONITOR_PENDENCIA.toString(), Boolean.TRUE));
			
			criteria.add(Subqueries.exists(subQueryUfeExists));
		}

		if (filtrosPesquisa.getNumeroUnicoIni() != null && filtrosPesquisa.getNumeroUnicoFim() != null) {
			criteria.add(Restrictions.between("AMO." + AelAmostras.Fields.NRO_UNICO.toString(), filtrosPesquisa.getNumeroUnicoIni(), filtrosPesquisa.getNumeroUnicoFim()));
			criteria.add(Restrictions.eq("AMO." + AelAmostras.Fields.DT_NUMERO_UNICO.toString(), filtrosPesquisa.getDataDia()));
		}
		
		criteria.add(Restrictions.eq("EXT." + AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), situacaoItemSolicitacaoCodigo));
		if(filtrosPesquisa.getDataReferenciaIni() != null && filtrosPesquisa.getDataReferenciaFim() != null) {
			criteria.add(Restrictions.between("EXT." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), filtrosPesquisa.getDataReferenciaIni(), filtrosPesquisa.getDataReferenciaFim()));
		}
		
		/*
		 * Atenção! A ordenação adequada é feita na classe br.gov.mec.aghu.exames.business.MonitorPendenciasExamesON, 
		 * pois depende da função PROCEDURE AELC_GET_DTHR_RECEBE chamada no método "pesquisarMonitorPendenciasExames"
		 */

		// Distinct
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		// Seta a quantidade máxima de resultados através do filtro
		//criteria.getExecutableCriteria(getSession()).setMaxResults(filtrosPesquisa.getQuantidadeMaximaRegistros());
		
		// Transforma resultados no VO
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoMonitorPendenciasExamesVO.class));
		
		criteria.addOrder(Order.asc(ResultadoMonitorPendenciasExamesVO.Fields.ORIGEM_MAPA.toString()));
		criteria.addOrder(Order.asc(ResultadoMonitorPendenciasExamesVO.Fields.DTHR_EVENTO.toString()));
	
		return executeCriteria(criteria, 0, maxResult, null, false);
	}
	
	/**
	 * Recupera o Item de Amostra de acordo com a situacao da amostra
	 * @param iseSoeSeq
	 * @param situacaoAmostras
	 * @return
	 */
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesPorSituacaoAmostras(Object objPesquisa, List<DominioSituacaoAmostra> situacaoAmostras) {
						
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "aie");
		criteria.createAlias("aie." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise"); // ALIAS ISE
		criteria.createAlias("aie." + AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "amo");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "ise_ema", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "ise_exa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "ise_man", JoinType.LEFT_OUTER_JOIN);

		if (objPesquisa != null && !StringUtils.isEmpty(objPesquisa.toString())) {
			
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				
				criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
				
			}
			
		}
		
		criteria.add(Restrictions.in("amo." + AelAmostras.Fields.SITUACAO.toString(), situacaoAmostras));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Object[] buscaIdMateriasAnalizeParaGerarRelatorio(AelItemSolicitacaoExames s){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ais");
		criteria.createAlias("ais."+AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "aem", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), s.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), s.getId().getSeqp()));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString()+".id.exaSigla"))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString()+".id.manSeq"))
			);
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	public String buscaAmoSeqParaUmaSolicitacao(Integer iseSoeSeq, Short iseSeqp, Integer amoSoeSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class);
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		List<AelAmostraItemExames> lista = executeCriteria(criteria);
		String retorno = "";
		
		if(lista != null && lista.size() > 0){
			for (AelAmostraItemExames aelAmostraItemExames : lista) {
				if(retorno.equals("")){
					retorno = retorno.concat(aelAmostraItemExames.getId().getAmoSeqp().toString());
				}else{
					retorno = retorno.concat(", " +aelAmostraItemExames.getId().getAmoSeqp().toString());
				}
			}
		}
		
		return retorno;
	}
	
}
