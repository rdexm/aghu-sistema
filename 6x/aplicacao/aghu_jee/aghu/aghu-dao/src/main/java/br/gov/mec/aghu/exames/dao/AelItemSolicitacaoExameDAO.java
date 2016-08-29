package br.gov.mec.aghu.exames.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
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
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.ExamesLiberadosVO;
import br.gov.mec.aghu.ambulatorio.vo.VerificaItemSolicitacaoExamesVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioNaturezaExame;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaMaterialApVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaClinicaVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExameRelLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExamesVO;
import br.gov.mec.aghu.exames.vo.DataLiberacaoVO;
import br.gov.mec.aghu.exames.vo.DataRecebimentoSolicitacaoVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.exames.vo.ItemSolicitacaoExameAtendimentoVO;
import br.gov.mec.aghu.exames.vo.MascaraExameVO;
import br.gov.mec.aghu.exames.vo.NumeroApTipoVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.faturamento.vo.AtendPacExternPorColetasRealizadasVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDoadorRedome;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExameApItemSolicHist;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelExameInternetGrupo;
import br.gov.mec.aghu.model.AelExameInternetGrupoArea;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemResulImpressao;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;


@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount", "PMD.CouplingBetweenObjects"})
public class AelItemSolicitacaoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicitacaoExames> {

	private static final long serialVersionUID = 4131920585992370131L;

    @Inject
    private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;	
	
	private static final Log LOG = LogFactory.getLog(AelItemSolicitacaoExameDAO.class);

	private enum AelItemSolicitacaoExameDAOExceptionCode implements BusinessExceptionCode {
		FAT_00705,
		ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
	}

	private static final String DESCRICAO_SISMAMA = "SISMAMA";
	
	private final static String ISE = "ise.";
	private final static String SOE = "soe.";
	private final static String PUS = "pus.";
	private final static String TAE = "tae.";
	private final static String ATD = "atd.";
	private final static String ITS = "its.";
	private final static String ITRI = "itri.";
	private final static String SUB = "sub_";
	
	
	private DetachedCriteria obterCriteriaAelItemSolicitacaoExamesPorNumeroAP(final Long numeroAP, final Integer lu2Seq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		
		criteria.add(Restrictions.eq("lum." +  AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAP));
		criteria.add(Restrictions.eq("lum." +  AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		return criteria;
	}
	
	/**
	 * 
	 * @param numeroAP
	 * @param lu2Seq == seq configuracao do exame
	 * @return
	 */
	public List<AelItemSolicitacaoExames> obterAelItemSolicitacaoExamesPorNumeroAP(final Long numeroAP, final Integer lu2Seq){
		
		final DetachedCriteria criteria = obterCriteriaAelItemSolicitacaoExamesPorNumeroAP(numeroAP, lu2Seq);
		
		return executeCriteria(criteria);
	}
	

	public List<AelItemSolicitacaoExames> obterAelItemSolicitacaoExamesPorNumeroAPNaoCancelado(final Long numeroAP, final Integer lu2Seq, String sitCancelado){
		
		final DetachedCriteria criteria = obterCriteriaAelItemSolicitacaoExamesPorNumeroAP(numeroAP, lu2Seq);

		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), sitCancelado)); 
		
		return executeCriteria(criteria);
	}
	
	
	public List<AelItemSolicitacaoExames> obterListaItemSolicitacaoExamesPorNumeroAPGrupoPesquisaSISMAMA(final Long numeroAP, final Integer lu2Seq){
		List<AelAgrpPesquisaXExame> grupoPesquisaXExames = aelAgrpPesquisaXExameDAO.pesquisarAelAgrpPesquisaXExamePorNumeroApDescricao(numeroAP, DESCRICAO_SISMAMA, lu2Seq);

		if  (!verificarExisteGrupoPesquisa(grupoPesquisaXExames)){
			return null;
		} else {
			List<AelItemSolicitacaoExamesId> listaIdItens = obterListaIdItensAelUnfExecutaExames(grupoPesquisaXExames.get(0).getUnfExecutaExame().getId());
			
			final DetachedCriteria criteria = obterCriteriaAelItemSolicitacaoExamesPorNumeroAP(numeroAP, lu2Seq);
			
			criteria.add(Restrictions.in("ise." + AelItemSolicitacaoExames.Fields.ID.toString(), listaIdItens));
			
			return executeCriteria(criteria);
		}
	}
	
	private boolean verificarExisteGrupoPesquisa(List<AelAgrpPesquisaXExame> grupoPesquisaXExames){
		
		if (grupoPesquisaXExames == null || grupoPesquisaXExames.isEmpty()
				|| grupoPesquisaXExames.get(0) == null
				|| grupoPesquisaXExames.get(0).getUnfExecutaExame() == null) {
			return false;
		}
		
		return true;
	}	
	
	public List<AelItemSolicitacaoExamesId> obterListaIdItensAelUnfExecutaExames(AelUnfExecutaExamesId id){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "uee");
		criteria.add(Restrictions.eq("uee." + AelUnfExecutaExames.Fields.ID.toString(), id));
		
		criteria.setProjection( Projections.projectionList()
		        .add( Projections.distinct(Projections.property("ise." + AelItemSolicitacaoExames.Fields.ID.toString())))
		    );
		
		return executeCriteria(criteria);
	}
	
	public String obterTipoExamePatologico(Long numeroAp, Integer lu2Seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "lu2");

		criteria.setProjection( Projections.projectionList()
		        .add( Projections.distinct(Projections.property("lu2." + AelConfigExLaudoUnico.Fields.NOME.toString())))
		    );
		
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));

		return (String) executeCriteriaUniqueResult(criteria);
		
	}

	@Override
	protected void obterValorSequencialId(final AelItemSolicitacaoExames elemento) {
		if (elemento == null || elemento.getSolicitacaoExame() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		final AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();

		id.setSoeSeq(elemento.getSolicitacaoExame().getSeq());

		Integer seqp = 0;
		final Short maxSeqp = this.obterItemSolicitacaoExameSeqpMax(elemento.getSolicitacaoExame().getSeq());
		if (maxSeqp != null) {
			seqp = maxSeqp.intValue();
		}

		seqp = seqp + 1;
		id.setSeqp(seqp.shortValue());

		elemento.setId(id);
	}

	@SuppressWarnings("unchecked")
	public List<AelItemSolicitacaoExames> buscaItemSolicitacaoExamesComRespostaQuestao(final List<Integer> listaSoeSeq) {
		// TODO Colocar fileds.
		final StringBuilder hql = new StringBuilder(300);

		hql.append("select ise ");
		hql.append("from FatProcedHospInternos phi ");
		hql.append(", ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ");
		hql.append("where ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA).append(" = phi.emaExaSigla ");
		hql.append("and ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ).append(" = phi.emaManSeq ");
		hql.append("and ise.").append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ).append(" in ( :pListSoeSeq ) ");
		hql.append("and exists ( ");
		hql.append("	select 1 ");
		hql.append("	from AelRespostaQuestao rqu ");
		hql.append("	where rqu.id.iseSoeSeq = ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ);
		hql.append("	and rqu.id.iseSeqp = ise.").append(AelItemSolicitacaoExames.Fields.SEQP);//id.seqp ");
		hql.append(") ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameterList("pListSoeSeq", listaSoeSeq);

		return query.list();
	}
	
	
	public NumeroApTipoVO obterNumeroApTipoPorSolicitacao(Integer soeSeq, Short seqp, Boolean isHist) {

		DetachedCriteria criteria = null;
		
		if (isHist) {
			criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class, "ise");
			criteria.createAlias("ise." + AelItemSolicExameHist.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
			criteria.createAlias("lul." + AelExameApItemSolicHist.Fields.EXAME_AP.toString(), "lux");
			
			criteria.add(Restrictions.eq("ise." + AelItemSolicExameHist.Fields.SOE_SEQ.toString(), soeSeq));
			criteria.add(Restrictions.eq("ise." + AelItemSolicExameHist.Fields.SEQP.toString(), seqp));
			
		} else {
			criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
			
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		}
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.setProjection( Projections.projectionList()
		        .add( Projections.property("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()),"numeroAp" )
		        .add( Projections.property("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString()), "lu2Seq" )
		        .add( Projections.groupProperty("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()) )
		        .add( Projections.groupProperty("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString()) )
		    );
		
		criteria.setResultTransformer(Transformers.aliasToBean(NumeroApTipoVO.class));
		
		return (NumeroApTipoVO) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Busca exames liberados por paciente e situação.
	 * Filtro por Atendimento apenas.
	 * 
	 * @param {Integer} codigoPaciente
	 * @param {DominioSituacaoItemSolicitacaoExame} situacaoItemExame
	 * 
	 * @return Lista de objetos do tipo AelItemSolicitacaoExames
	 */
	@SuppressWarnings("unchecked")
	public List<ExamesPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimento(final Integer codigoPaciente, final Short unfSeq, 
			final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final List<Integer> gruposMateriaisAnalises) {
		
		final StringBuilder hql = montaHqlComunBuscaExamesPeloCodigoPacienteSituacao();
		hql.append(" inner join sol." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + " atd ");
		/*hql.append("where exists (");
		hql.append(montaHqlSolicitacaoExamesAtd(situacaoItemExame, unfSeq));		
		hql.append(") ");*/
		
		hql.append(" where atd.").append(AghAtendimentos.Fields.PAC_CODIGO).append(" = :pCodigoPaciente ");
		if (unfSeq != null) {
			hql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ).append(" = :pUfeUnfSeq ");
		}
		hql.append(montaHqlWhereCodigoSituacao(situacaoItemExame, "ise"));
		
		hql.append(" and (adre.indAnulacaoDoc is null or adre.indAnulacaoDoc = ").append("'N')");

		if(gruposMateriaisAnalises!=null && !gruposMateriaisAnalises.isEmpty()){
			hql.append(" and (man.seq in (").append(" :pMateriaisXGruposAnalises ))");
		}
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pCodigoPaciente", codigoPaciente);

		//verifica se o material análise pertence a lista de materiais análises
		//que são relacionados com gmaSeq
		if(gruposMateriaisAnalises!=null && !gruposMateriaisAnalises.isEmpty()){
			query.setParameterList("pMateriaisXGruposAnalises", gruposMateriaisAnalises);
		}
		
		if (unfSeq != null) {
			query.setParameter("pUfeUnfSeq", unfSeq);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ExamesPOLVO.class));

		return query.list();
	}

	/**
	 * Busca exames liberados por paciente e situação.
	 * Filtro por Atendimento apenas.
	 * 
	 * @param {Integer} codigoPaciente
	 * @param {DominioSituacaoItemSolicitacaoExame} situacaoItemExame
	 * 
	 * @return Lista de objetos do tipo AelItemSolicitacaoExames
	 */
	@SuppressWarnings("unchecked")
	public List<ExamesPOLVO> buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(final Integer codigoPaciente, final Short unfSeq, final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final List<Integer> gruposMateriaisAnalises) {
		final StringBuilder hql = montaHqlComunBuscaExamesPeloCodigoPacienteSituacao();
		hql.append(" inner join ise." + AelItemSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString() + " atv ");
		
		/*hql.append("where exists (");
		hql.append(montaHqlSolicitacaoExamesAtv(situacaoItemExame, unfSeq));
		hql.append(") ");*/
		hql.append(" WHERE atv.").append(AelAtendimentoDiversos.Fields.PAC_CODIGO).append(" = :pCodigoPaciente ");
		hql.append(" AND atv.").append(AelAtendimentoDiversos.Fields.PJQ_SEQ).append(" is not null ");
		if (unfSeq != null) {
			hql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ).append(" = :pUfeUnfSeq ");
		}
		hql.append(montaHqlWhereCodigoSituacao(situacaoItemExame, "ise"));
		
		hql.append(" and (adre.indAnulacaoDoc is null or adre.indAnulacaoDoc = ").append("'N')");

		if(gruposMateriaisAnalises!=null && !gruposMateriaisAnalises.isEmpty()){
			hql.append(" and (man.seq in (").append(" :pMateriaisXGruposAnalises ))");
		}

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pCodigoPaciente", codigoPaciente);

		//verifica se o material análise pertence a lista de materiais análises
		//que são relacionados com gmaSeq
		if(gruposMateriaisAnalises!=null && !gruposMateriaisAnalises.isEmpty()){
			query.setParameterList("pMateriaisXGruposAnalises", gruposMateriaisAnalises);
		}
		
		if (unfSeq != null) {
			query.setParameter("pUfeUnfSeq", unfSeq);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ExamesPOLVO.class));

		return query.list();		
	}

	private StringBuilder montaHqlComunBuscaExamesPeloCodigoPacienteSituacao() {
		final StringBuilder hql = new StringBuilder(250);
		hql.append("select distinct ise as " ).append( ExamesPOLVO.Fields.AEL_ITEM_SOLIC_EXAME.toString());
		hql.append(hqlMaxDtRecebimentoAelExtratoItemSolic());
		hql.append(hqlCountNotasAdicionais());
		//hql.append(", adre as " + ExamesHistoricoPOLVO.Fields.AEL_DOC_RESULTADO_EXAMES_HIST.toString()); //@FIXME:
		hql.append(" , man." + AelMateriaisAnalises.Fields.DESCRICAO.toString() + " as " +  ExamesPOLVO.Fields.DESCRICAO_MAT_ANALISE );
		hql.append(" , man." + AelMateriaisAnalises.Fields.SEQ.toString() + " as " +  ExamesPOLVO.Fields.SEQ_MAT_ANALISE );
		hql.append(" from ");
		hql.append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ");
		hql.append(" inner join ise." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString() + " man ");
		hql.append(" inner join ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString() + " exa ");
		hql.append(" inner join ise." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString() + " unf ");
		hql.append(" inner join ise." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString() + " sol ");
		hql.append(" left outer join ise." + AelItemSolicitacaoExames.Fields.DOC_RESULTADO_EXAME.toString() + " adre ");
		hql.append(" left outer join ise." + AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString() + " resExam ");
		/*hql.append(" inner join man."
				+ AelMateriaisAnalises.Fields.GRUPO_XMATERIAL.toString()
				+ " gxm ");
		
		hql.append(" inner join gxm."
				+ AelGrupoXMaterialAnalise.Fields.GRUPO_MATERIAL.toString()
				+ " gma ");*/
		
		return hql;
	}

	private String hqlCountNotasAdicionais() {
		StringBuilder str = new StringBuilder(150);
		str.append(" , ( ")
		.append("select count(aelNotas.")
		.append(AelNotaAdicional.Fields.SEQP)
		.append(") ")
		.append("from ")
		.append(AelNotaAdicional.class.getSimpleName())
		.append(" aelNotas ")
		.append(" where aelNotas.")		
		.append(AelNotaAdicional.Fields.ISE_SOE_SEQ.toString())
		.append(" = ")
		.append("ise." ).append( AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		.append(" and aelNotas.")
		.append(AelNotaAdicional.Fields.ISE_SEQP.toString())
		.append("  = ")
		.append("ise." ).append( AelItemSolicitacaoExames.Fields.SEQP.toString())
		.append(" ) ")
		.append(" as  " ).append( ExamesPOLVO.Fields.QTDE_NOTA_ADICIONAL.toString());
		
		return str.toString();
	}

	private String hqlMaxDtRecebimentoAelExtratoItemSolic() {
		StringBuilder str = new StringBuilder()
		.append(" , ( ")
		.append("select max(eis.")
		.append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO)
		.append(") ")
		.append("from ")
		.append(AelExtratoItemSolicitacao.class.getSimpleName())
		.append(" eis ")
		.append(" where eis.")
		.append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString())
		.append(" = ")
		.append("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		.append(" and eis.")
		.append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString())
		.append("  = ")
		.append("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString())
		.append(" and eis.")
		.append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString())
		.append(" = '")
		.append(DominioSituacaoItemSolicitacaoExame.AE.name())

		.append("' ) ")
		.append(" as  " + ExamesPOLVO.Fields.DTHR_EVENTO_EXTRATO_ITEM.toString());
		
		return str.toString();
	}


	/**
	 * .
	 * @param situacaoItemExame
	 * @param alias
	 * @return
	 */
	private StringBuilder montaHqlWhereCodigoSituacao(final DominioSituacaoItemSolicitacaoExame situacaoItemExame, final String alias) {

		final StringBuilder hql = new StringBuilder();
		String sinal = " = ";
		final List<DominioSituacaoItemSolicitacaoExame> listSitCodigo = new ArrayList<DominioSituacaoItemSolicitacaoExame>();

		switch (situacaoItemExame) {

		case LI:
			sinal = " = ";
			listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.LI);
			break;

		case PE:
			sinal = " <> ";
			listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.CA);
			listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.PE);
			listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.LI);
			break;

		case CA:
			listSitCodigo.add(DominioSituacaoItemSolicitacaoExame.CA);
			sinal = " = ";
			break;

		}


		for (final DominioSituacaoItemSolicitacaoExame sitCodigo : listSitCodigo) {

			hql.append(" AND ").append(alias).append('.').append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO).append(sinal).append(" '").append(sitCodigo.getCodigo()).append("' ");

		}

		return hql;

	}	

	protected DetachedCriteria obterCriteriaPorSolicitacao(
			final Integer solicitacaoExameSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
        result.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UNFEXEC");
		result.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString(), solicitacaoExameSeq));

		return result;
	}

	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSituacaoLiberado(final Object solicitacaoExameSeq, final String situacaoExameLiberado) {
		final DetachedCriteria criteria = obterCriteriaItemSolicitacaoExamePorSituacaoLiberado(solicitacaoExameSeq,situacaoExameLiberado);

		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString()));
		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		return this.executeCriteria(criteria, 0, 100, new HashMap<String, Boolean>());
	}
	
	public Long pesquisarItemSolicitacaoExamePorSituacaoLiberadoCount(final Object solicitacaoExameSeq, final String situacaoExameLiberado) {
		final DetachedCriteria criteria = obterCriteriaItemSolicitacaoExamePorSituacaoLiberado(solicitacaoExameSeq, situacaoExameLiberado);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaItemSolicitacaoExamePorSituacaoLiberado(final Object solicitacaoExameSeq, final String situacaoExameLiberado) {
		DetachedCriteria criteria = null;
		
		if(solicitacaoExameSeq != null && !StringUtils.isEmpty(solicitacaoExameSeq.toString())){
			criteria = obterCriteriaPorSolicitacao(Integer.valueOf(solicitacaoExameSeq.toString()));
		} else {
			criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		}
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacaoExameLiberado));
		
		return criteria;
	}

	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSolicitacaoExame(final Integer solicitacaoExameSeq) {
		final DetachedCriteria criteria = obterCriteriaPorSolicitacao(solicitacaoExameSeq);
		return this.executeCriteria(criteria);
	}
	
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSolicitacaoExameComHrColeta(Integer solicitacaoExameSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString());
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString());
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString(), solicitacaoExameSeq));
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelExameHorarioColeta.class, "ehc");
		subCriteria.createAlias(AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString(), AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString());
		subCriteria.add(Restrictions.eq(AelExameHorarioColeta.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eqProperty("ehc."+AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString()+"."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), 
				"ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString()+"."+AelExames.Fields.SIGLA.toString()));
		subCriteria.add(Restrictions.eqProperty("ehc."+AelExameHorarioColeta.Fields.EXAME_MATERIAL_ANALISE.toString()+"."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), 
				"ise."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString()+"."+AelMateriaisAnalises.Fields.SEQ.toString()));
		subCriteria.setProjection(
				Projections.projectionList().add(Projections.property(AelExameHorarioColeta.Fields.SIGLA.toString()))
				.add(Projections.property(AelExameHorarioColeta.Fields.SEQP.toString()))
				.add(Projections.property(AelExameHorarioColeta.Fields.MATERIAL.toString())));
		
		criteria.add(Subqueries.exists(subCriteria));
		return this.executeCriteria(criteria);
	}
	
	
	
	public Short obterItemSolicitacaoExameSeqpMax(final Integer solicitacaoExameSeq) {
		final DetachedCriteria criteria = obterCriteriaPorSolicitacao(solicitacaoExameSeq);

		criteria.setProjection(Projections.max(AelItemSolicitacaoExames.Fields.SEQP.toString()));
		final Object objMax = this.executeCriteriaUniqueResult(criteria);

		return (Short) objMax;
	}



	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameMonitoramento(final Integer solicitacaoExameSeq, final AghUnidadesFuncionais unidadeExecutora, final Date dataCalculadaAparecimentoSolicitacao, final String sitCodigo) {

		// Criteria SECUNDARIA de Item de Solicitacao de Exames - Alias ISE
		final DetachedCriteria criteriaIse = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,ISE.substring(0,3));
		// Alias para Solicitacao de Exame - Alias SOE1
		criteriaIse.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), SOE.substring(0,3), JoinType.INNER_JOIN); 
		//criteriaIse.setProjection(Projections.property(ISE + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString())); 
		// Restricoes de ISE
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacaoExameSeq));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigo));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora.getSeq()));
		criteriaIse.add(Restrictions.le(ISE + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), new Date(dataCalculadaAparecimentoSolicitacao.getTime())));

		// Restricoes aninhadas de ISE e SOE1
		final Criterion criterion1 = Restrictions.ltProperty(SOE + AelSolicitacaoExames.Fields.CRIADO_EM.toString(), ISE + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString());
		//Criterion criterion2 = Restrictions.isNotNull(SOE + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString()); // TODO
		//Criterion criterion3 = Restrictions.and(criterion1, criterion2);
		final Criterion criterion4 = Restrictions.or(criterion1, Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(), DominioTipoColeta.U));
		criteriaIse.add(criterion4);

		// SubCriteria/SubConsulta (EXISTS) de Permissao de Unid. Solicao - Alias PUS
		final DetachedCriteria subQueryPus = DetachedCriteria.forClass(AelPermissaoUnidSolic.class,PUS.substring(0,3));	
		subQueryPus.setProjection(Projections.property(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()));
		// Restricoes de PUS
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_UNF.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()).eqProperty(SOE + AelSolicitacaoExames.Fields.UNF_SEQ.toString()));
		subQueryPus.add(Restrictions.eq(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora));
		// Restricoes aninhadas de PUS, ISE e SOE1
		final Criterion criterion5 = Restrictions.eq(PUS + AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), DominioSimNaoRotina.S);
		final Criterion criterion6 = Restrictions.in(PUS + AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), new DominioSimNaoRotina[]{DominioSimNaoRotina.R,DominioSimNaoRotina.N});
		final Criterion criterion7 = Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(),DominioTipoColeta.U);
		final Criterion criterion8 = Restrictions.and(criterion6, criterion7);
		subQueryPus.add(Restrictions.or(criterion5, criterion8));
		// Adiciona SubCriteria de Permissao de Unid. Solicao - PUS na criteria SECUNDARIA
		criteriaIse.add(Subqueries.exists(subQueryPus));

		// SubCriteria/SubConsulta (EXISTS) de Tipo de Amostras de Exame - Alias TAE
		final DetachedCriteria subQueryTae = DetachedCriteria.forClass(AelTipoAmostraExame.class,TAE.substring(0,3));	
		subQueryTae.setProjection(Projections.property(AelTipoAmostraExame.Fields.NRO_AMOSTRAS.toString()));
		// Restricoes aninhadas de TAE - feitas fora na RN
		// Restricoes de TAE
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryTae.add(Restrictions.in(TAE + AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), new DominioResponsavelColetaExames[]{DominioResponsavelColetaExames.C,DominioResponsavelColetaExames.S}));

		return this.executeCriteria(criteriaIse);
	}

	/**
	 * Pesquisa itens de solicitacao de exames com busca urgente
	 * @param solicitacaoExame
	 * @param cSitCodigo
	 * @param unidadeExecutora
	 * @return
	 */
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameBuscaUrgente(
			final AelSolicitacaoExames solicitacaoExame, final String cSitCodigo,  final AghUnidadesFuncionais unidadeExecutora) {

		// Alias para Item de Solicitacao de Exame - Alias ise
		final DetachedCriteria criteriaIse = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), cSitCodigo));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora.getSeq()));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(), DominioTipoColeta.U));

		// SubCriteria/SubConsulta (EXISTS) de Permissao de Unid. Solicao - Alias PUS
		final DetachedCriteria subQueryPus = DetachedCriteria.forClass(AelPermissaoUnidSolic.class,"pus");
		subQueryPus.setProjection(Projections.property(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_UNF.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		subQueryPus.add(Restrictions.eq(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora));
		criteriaIse.add(Subqueries.exists(subQueryPus));

		// SubCriteria/SubConsulta (EXISTS) de Tipo de Amostras de Exame - Alias TAE
		final DetachedCriteria subQueryTae = DetachedCriteria.forClass(AelTipoAmostraExame.class,"tae");
		subQueryTae.setProjection(Projections.property(AelTipoAmostraExame.Fields.NRO_AMOSTRAS.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryTae.add(Restrictions.eq(TAE + AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), DominioResponsavelColetaExames.C));
		criteriaIse.add(Subqueries.exists(subQueryTae));

		return executeCriteria(criteriaIse);
	}

	/**
	 * Pesquisa itens de solicitacao de exames com busca programada
	 * @param solicitacaoExame
	 * @param cSitCodigo
	 * @param unidadeExecutora
	 * @param dataCalculadaAparecimentoSolicitacao
	 * @return
	 */
	public List<AelItemSolicitacaoExames> pesquisaItemSolicitacaoExameBuscaProgramada(
			final AelSolicitacaoExames solicitacaoExame, final String cSitCodigo,  final AghUnidadesFuncionais unidadeExecutora, final Date dataCalculadaAparecimentoSolicitacao) {

		// Alias para Item de Solicitacao de Exame - Alias ise
		final DetachedCriteria criteriaIse = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,ISE.substring(0,3));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		criteriaIse.add(Restrictions.gt(ISE + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), solicitacaoExame.getCriadoEm()));
		criteriaIse.add(Restrictions.le(ISE + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), new Date(Calendar.getInstance().getTimeInMillis() + dataCalculadaAparecimentoSolicitacao.getTime())));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), cSitCodigo));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora.getSeq()));

		// SubCriteria/SubConsulta (EXISTS) de Permissao de Unid. Solicao - Alias PUS
		final DetachedCriteria subQueryPus = DetachedCriteria.forClass(AelPermissaoUnidSolic.class,PUS.substring(0,3));
		subQueryPus.setProjection(Projections.property(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_UNF.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		subQueryPus.add(Restrictions.eq("pus.unfSeqAvisa", unidadeExecutora));
		criteriaIse.add(Subqueries.exists(subQueryPus));

		// SubCriteria/SubConsulta (EXISTS) de Tipo de Amostras de Exame - Alias TAE
		final DetachedCriteria subQueryTae = DetachedCriteria.forClass(AelTipoAmostraExame.class,TAE.substring(0,3));
		subQueryTae.setProjection(Projections.property(AelTipoAmostraExame.Fields.NRO_AMOSTRAS.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryTae.add(Restrictions.eq(TAE + AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), DominioResponsavelColetaExames.C));
		criteriaIse.add(Subqueries.exists(subQueryTae));

		return executeCriteria(criteriaIse);
	}

	public List<AelItemSolicitacaoExames> pesquisaMonitoramentoColetasEmergenciaItensProgramados(final AghUnidadesFuncionais unidadeExecutora, final VAelSolicAtendsVO vo, final String sitCodigo) {

		// Alias para Item de Solicitacao de Exame - Alias ise
		final DetachedCriteria criteriaIse = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, ISE.substring(0,3));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), vo.getNumero()));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigo));

		// SubCriteria/SubConsulta (EXISTS) de Permissao de Unid. Solicao - Alias PUS
		final DetachedCriteria subQueryPus = DetachedCriteria.forClass(AelPermissaoUnidSolic.class,PUS.substring(0,3));
		subQueryPus.setProjection(Projections.property(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_UNF.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		subQueryPus.add(Restrictions.eq(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora));
		criteriaIse.add(Subqueries.exists(subQueryPus));

		// SubCriteria/SubConsulta (EXISTS) de Tipo de Amostras de Exame - Alias TAE
		final DetachedCriteria subQueryTae = DetachedCriteria.forClass(AelTipoAmostraExame.class,TAE.substring(0,3));
		subQueryTae.setProjection(Projections.property(AelTipoAmostraExame.Fields.NRO_AMOSTRAS.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryTae.add(Restrictions.in(TAE + AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), new DominioResponsavelColetaExames[]{DominioResponsavelColetaExames.C,DominioResponsavelColetaExames.S}));
		criteriaIse.add(Subqueries.exists(subQueryTae));

		// Ordena a consula pelo SEQP
		criteriaIse.addOrder(Order.asc(ISE + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		
		criteriaIse.setFetchMode(AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(), FetchMode.JOIN);
		criteriaIse.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), FetchMode.JOIN);
		criteriaIse.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
		

		return executeCriteria(criteriaIse);
	}

	protected DetachedCriteria obterCriteriaPorSolicitacao(final Integer soeSeq, final String... sitCodigo) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorSolicitacao(soeSeq);		
		result.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), sitCodigo));

		return result;
	}


	public List<AelItemSolicitacaoExames> obterPorSolicitacaoSitCodigo(final Integer soeSeq, final String... sitCodigo) {

		List<AelItemSolicitacaoExames> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorSolicitacao(soeSeq, sitCodigo);
		result = this.executeCriteria(criteria);

		return result;
	}

	/**
	 * Pesquisa itens de solicitação de exame pela unidade executora e situacao AE (Area executora) ou LI (Liberado)
	 * @param solicitacaoExameSeq
	 * @return
	 */
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSituacaoAreaExecutoraLiberado(final Integer solicitacaoExameSeq, final Short amostraSeqp, final AghUnidadesFuncionais unidadeFuncional) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ISE");
		
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE",JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacaoExameSeq));
		
		if(amostraSeqp != null){
			criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.AMO_SEQP.toString(), amostraSeqp.intValue()));
		}
		
		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		criteria.add(Restrictions.in("ISE."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), new String[]{"AE","LI"}));
	
		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		
		return this.executeCriteria(criteria);

	}

	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorItemSolicitacaoExamePai(final AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "man");

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString(), aelItemSolicitacaoExames));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), aelItemSolicitacaoExames.getUnidadeFuncional()));
		//criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), aelItemSolicitacaoExames.getUnidadeFuncional()));

		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude, final String codigoPaciente, final Integer prontuario, final String nomePaciente){

		final DetachedCriteria criteria = montarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(aelUnfExecutaExames,aelSolicitacaoExames,aelSitItemSolicitacoes,fatConvenioSaude,codigoPaciente,prontuario,nomePaciente);
		return this.executeCriteriaCount(criteria);
	}

	public List<AelItemSolicitacaoExames> pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(
			final Integer firstResult, final Integer maxResult, final String ordem, final boolean b,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude, final String codigoPaciente, final Integer prontuario, final String nomePaciente) {

		final DetachedCriteria criteria = montarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(aelUnfExecutaExames,aelSolicitacaoExames,aelSitItemSolicitacoes,fatConvenioSaude,codigoPaciente,prontuario,nomePaciente);
		final List<AelItemSolicitacaoExames> lista = this.executeCriteria(criteria,firstResult,maxResult,"id",b);

		return lista;
	}

	public DetachedCriteria montarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude, final String codigoPaciente, final Integer prontuario, final String nomePaciente) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "aie");

		if(aelSolicitacaoExames != null){
			return realizaConsultaPorSolicitacao(criteria,aelSolicitacaoExames,aelUnfExecutaExames,aelSitItemSolicitacoes,fatConvenioSaude);
		}

		if(prontuario != null && !prontuario.equals("")){
			return realizaConsultaPorProntuario(criteria,prontuario,aelUnfExecutaExames,aelSitItemSolicitacoes,fatConvenioSaude);
		}

		if(codigoPaciente != null && !codigoPaciente.equals("")){
			return realizaConsultaPorCodigoPaciente(criteria,codigoPaciente,aelUnfExecutaExames,aelSitItemSolicitacoes,fatConvenioSaude);
		}

		if(nomePaciente != null && !nomePaciente.equals("")){
			return realizaConsultaPorNomePaciente(criteria,nomePaciente,aelUnfExecutaExames,aelSitItemSolicitacoes,fatConvenioSaude);
		}

		if(aelSitItemSolicitacoes != null){
			return realizaConsultaPorSituacao(criteria,aelSitItemSolicitacoes,aelUnfExecutaExames,fatConvenioSaude);
		}

		if(fatConvenioSaude != null){
			return realizaConsultaPorConvenio(criteria,fatConvenioSaude,aelUnfExecutaExames,aelSitItemSolicitacoes);
		}

		return realizaConsultaPorQualquer(criteria,aelUnfExecutaExames,aelSitItemSolicitacoes);
	}

	private DetachedCriteria realizaConsultaPorQualquer(final DetachedCriteria criteria,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes) {

		adicionarCriteriaSituacao(aelSitItemSolicitacoes, criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}

	private DetachedCriteria realizaConsultaPorConvenio(final DetachedCriteria criteria,
			final FatConvenioSaude fatConvenioSaude,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes) {


		final String sql = "(this_.soe_seq) in (select soe3.seq from agh.ael_solicitacao_exames soe3 where soe3.csp_cnv_codigo = ?)";
		final Object[] values = {fatConvenioSaude.getCodigo()};	        
		final Type[] types = {ShortType.INSTANCE};

		criteria.add(Restrictions.sqlRestriction(sql, values, types)) ;

		adicionarCriteriaSituacao(aelSitItemSolicitacoes, criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}

	private DetachedCriteria realizaConsultaPorSituacao(final DetachedCriteria criteria,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final FatConvenioSaude fatConvenioSaude) {
		criteria.add(Restrictions.eq("aie."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), aelSitItemSolicitacoes));

		adicionarCriteriaPorCodigoConvenio(fatConvenioSaude,criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}

	private DetachedCriteria realizaConsultaPorNomePaciente(final DetachedCriteria criteria,
			final String nomePaciente,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude) {

		final String sql = "( (this_.soe_seq) in (select soe2.seq from agh.ael_solicitacao_exames soe2, agh.agh_atendimentos atd99, agh.aip_pacientes pac where pac.nome like ? and  atd99.pac_codigo = pac.codigo and  soe2.atd_seq = atd99.seq) )";
		final Object[] values = {"%"+nomePaciente+"%"};	        
		final Type[] types = {StringType.INSTANCE};

		criteria.add(Restrictions.sqlRestriction(sql, values, types)) ;

		adicionarCriteriaPorCodigoConvenio(fatConvenioSaude,criteria);
		adicionarCriteriaSituacao(aelSitItemSolicitacoes, criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}

	private DetachedCriteria realizaConsultaPorCodigoPaciente(final DetachedCriteria criteria,
			final String codigoPaciente,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude) {

		final String sql = "( (this_.soe_seq) in (select soe4.seq from  agh.ael_solicitacao_exames soe4, agh.agh_atendimentos atd where atd.pac_codigo = ? and  soe4.atd_seq = atd.seq) )";
		final Object[] values = {Integer.valueOf(codigoPaciente)};	        
		final Type[] types = {IntegerType.INSTANCE};

		criteria.add(Restrictions.sqlRestriction(sql, values, types)) ;

		adicionarCriteriaPorCodigoConvenio(fatConvenioSaude,criteria);
		adicionarCriteriaSituacao(aelSitItemSolicitacoes, criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}

	private DetachedCriteria realizaConsultaPorProntuario(final DetachedCriteria criteria,
			final Integer prontuario, final AghUnidadesFuncionais aelUnfExecutaExames,final AelSitItemSolicitacoes aelSitItemSolicitacoes, final FatConvenioSaude fatConvenioSaude) {

		final String sql = "(this_.soe_seq) in (select soe2.seq from  agh.ael_solicitacao_exames soe2, agh.agh_atendimentos atd where atd.prontuario = ? and  soe2.atd_seq = atd.seq)";
		final Object[] values = {prontuario};	        
		final Type[] types = {IntegerType.INSTANCE};

		criteria.add(Restrictions.sqlRestriction(sql, values, types)) ;

		adicionarCriteriaPorCodigoConvenio(fatConvenioSaude,criteria);
		adicionarCriteriaSituacao(aelSitItemSolicitacoes, criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}


	private DetachedCriteria realizaConsultaPorSolicitacao(final DetachedCriteria criteria, 
			final AelSolicitacaoExames aelSolicitacaoExames,final AghUnidadesFuncionais aelUnfExecutaExames, final AelSitItemSolicitacoes aelSitItemSolicitacoes, final FatConvenioSaude fatConvenioSaude) {

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString(), aelSolicitacaoExames.getSeq()));

		adicionarCriteriaSituacao(aelSitItemSolicitacoes, criteria);
		adicionarCriteriaPorCodigoConvenio(fatConvenioSaude,criteria);
		adicionarCriteriaPossuiPaiControla(aelUnfExecutaExames, criteria);

		return criteria;
	}

	private void adicionarCriteriaPorCodigoConvenio(
			final FatConvenioSaude fatConvenioSaude, final DetachedCriteria criteria) {
		if(fatConvenioSaude != null){

			final String sql = "(this_.soe_seq) in (select soe3.seq from agh.ael_solicitacao_exames soe3 where soe3.csp_cnv_codigo = ?)";
			final Object[] values = {fatConvenioSaude.getCodigo()};	        
			final Type[] types = {ShortType.INSTANCE};

			criteria.add(Restrictions.sqlRestriction(sql, values, types)) ;
		}

	}

	private void adicionarCriteriaSituacao(
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final DetachedCriteria criteria) {

		if(aelSitItemSolicitacoes != null){
			criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), aelSitItemSolicitacoes));
		}		
		else if(aelSitItemSolicitacoes == null){
			final Criterion c1 = Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), new String[]{"AX","AE","AG"});
			final Criterion c21 = Restrictions.eq(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "CA");

			final String sql = "this_.moc_seq in (select seq from agh.ael_motivo_cancela_exames amce where amce.ind_permite_incluir_resultado = ?)";
			final Object[] values = {"S"};	        
			final Type[] types = {StringType.INSTANCE};

			final Criterion c22 = Restrictions.sqlRestriction(sql, values, types) ;
			final Criterion c2 = Restrictions.and(c21, c22);
			criteria.add(Restrictions.or(c1, c2));
		}

	}

	private void adicionarCriteriaPossuiPaiControla(
			final AghUnidadesFuncionais aelUnfExecutaExames, final DetachedCriteria criteria) {
		final StringBuffer subSql = new StringBuffer(750);
		subSql.append("( (this_.ufe_unf_seq)  in (select unf.seq from agh.agh_unidades_funcionais unf where unf.unf_seq in (select  unf1.unf_seq");                                    
		subSql.append("                         from    agh.agh_unidades_funcionais unf1");
		subSql.append("                         where   (unf1.unf_seq = ? or unf1.seq = ?)");
		subSql.append("                         and     exists(        ");
		subSql.append("                                       select  1");
		subSql.append("                                       from    agh.agh_caract_unid_funcionais cuf");
		subSql.append("                                       where   cuf.unf_seq = ?");
		subSql.append("                                       and     cuf.caracteristica = ?");
		subSql.append("                                       ) )) or this_.ufe_unf_seq = ? ) ");
		//subSql.append("                                            or this_.unf_seq_avisa = ? )");

		final Object[] values = {aelUnfExecutaExames.getSeq(), 
				                 aelUnfExecutaExames.getSeq(),
				                 aelUnfExecutaExames.getSeq(),
				                 ConstanteAghCaractUnidFuncionais.CONTROLA_UNID_PAI.getCodigo(),
				                 aelUnfExecutaExames.getSeq()};

		final Type[] types = {ShortType.INSTANCE, ShortType.INSTANCE,ShortType.INSTANCE,StringType.INSTANCE, ShortType.INSTANCE};
		
		criteria.add(Restrictions.sqlRestriction(subSql.toString(), values, types));
	}

	/**
	 * ATENÇÃO: quando usar esse método avaliar se realmente precisa desses joins para não deixar o sistema lento.
	 * 
	 * AelItemSolicExameHistDAO.obterPorId
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public AelItemSolicitacaoExames obterPorId(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

        criteria.createAlias(AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO.toString(), "iha", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ufe."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.SERVIDOR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.SERVIDOR.toString()+ "." + RapServidores.Fields.PESSOA_FISICA.toString(),FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), FetchMode.JOIN);		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString() +"." + AghAtendimentos.Fields.PACIENTE.toString(), FetchMode.JOIN);		
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), FetchMode.JOIN);		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString() +"." + AelAtendimentoDiversos.Fields.PACIENTE.toString(), FetchMode.JOIN);		
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.TIPO_TRANSPORTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "."+ AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()+ "." + AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString()+ "." + RapServidores.Fields.PESSOA_FISICA.toString(),FetchMode.JOIN);
		
		return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
	}
	
	
	
	public AelItemSolicitacaoExames obterPorId(AelItemSolicitacaoExamesId id) {
		return super.obterPorChavePrimaria(id);
	}

	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacoesExames(final Integer soeSeq, final Short seqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "uee", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("uee."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		
		if (soeSeq != null) {
			criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		}
		
		if (seqp != null) {
			criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		}

		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SEQP.toString()));
				
		return executeCriteria(criteria);
	}
	
	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacoesExamesPorSolicitacao(final Integer soeSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

        criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "unfEx", JoinType.INNER_JOIN);
        criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.INNER_JOIN);

		if (soeSeq != null) {
			criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		}
		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamesPorIdAColetar(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "AC"));

		return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
	}

	public List<AelItemSolicitacaoExames> pesquisarTodosItensDeSolicitacaoDiferentesDeItem(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.not(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp)));

		return this.executeCriteria(criteria);
	}

	public List<ItemSolicitacaoExameCancelamentoVO> listarItensExameCancelamentoSolicitante(final Integer solicitacaoExameSeq) throws BaseException {	

		final List<ItemSolicitacaoExameCancelamentoVO> listaRetorno = new ArrayList<ItemSolicitacaoExameCancelamentoVO>();
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");

		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		final Object[] objSituacao = {"AC","AG","AX","CA","CS","Y"};
		criteria.add(Restrictions.in(ISE+AelItemSolicitacaoExames.Fields.SIT_CODIGO, objSituacao));
		criteria.add(Restrictions.isNull(ISE+AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI));
		if(solicitacaoExameSeq!=null ){
			criteria.add(Restrictions.eq(SOE+AelSolicitacaoExames.Fields.SEQ.toString(), solicitacaoExameSeq));
		}

		// Ordena a consula pelo SEQP
		criteria.addOrder(Order.asc(ISE + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), FetchMode.JOIN);

		final List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = this.executeCriteria(criteria);

		for(final AelItemSolicitacaoExames aelItemSolic: listaItemSolicitacaoExame){
			final ItemSolicitacaoExameCancelamentoVO vo = new ItemSolicitacaoExameCancelamentoVO();
			vo.setAelItemSolicitacaoExames(aelItemSolic);
			listaRetorno.add(vo);
		}

		return listaRetorno;
	}

	/**
	 * Busca FatConvenioSaudePlano e AelSolicitacaoExames.
	 * 
	 * @param soeSeq
	 * @return List de Array contendo FatConvenioSaudePlano e AelSolicitacaoExames. 
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarDadosAtendimentoGerouExame(final Integer soeSeq) {
		final StringBuilder hql = new StringBuilder(200);

		hql.append(" select csp, soe")
		//		hql.append(" soe.").append(AelSolicitacaoExames.Fields.ATENDIMENTO_SEQ.toString()).append(", ");
		//		hql.append(" atd.").append(AghAtendimentos.Fields.CODIGO_PACIENTE.toString()).append(", ");
		//		hql.append(" soe.").append(AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO_CODIGO.toString()).append(", ");
		//		hql.append(" soe.").append(AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE_CODIGO.toString()).append(", ");
		//		hql.append(" atd.").append(AghAtendimentos.Fields.INT_SEQ.toString()).append(", ");
		//		hql.append(" soe.").append(AelSolicitacaoExames.Fields.CRIADO_EM.toString()).append(", ");
		//		hql.append(" COALESCE(csp.").append(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString()).append(",'A')").append(", ");
		//		hql.append(" csp.").append(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString()).append(", ");
		//		hql.append(" atd.").append(AghAtendimentos.Fields.ORIGEM.toString()).append(", ");
		//		hql.append(" atd.").append(AghAtendimentos.Fields.ATD_SEQ_MAE.toString());
		.append(" from ")
		.append(FatConvenioSaudePlano.class.getSimpleName()).append(" csp ")
		.append(" inner join csp.").append(FatConvenioSaudePlano.Fields.SOLICITACOES_EXAMES.toString()).append(" soe ")
		.append(" inner join csp.").append(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString()).append(" cnv ")
		.append(" inner join soe.").append(AelSolicitacaoExames.Fields.ATENDIMENTO.toString()).append(" atd ")

		.append(" where soe.").append(AelSolicitacaoExames.Fields.SEQ.toString()).append(" = :soeSeq ")
		.append(" and cnv.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("grupoConvenio", DominioGrupoConvenio.S);


		return query.list();
	}

	public List<AelAmostraItemExames> imprimirSolicitacoesColetar(
			final List<Integer> solicitacoes, final AghUnidadesFuncionais unidadeExecutora) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class,"aie");
		final DetachedCriteria criteriaPus = DetachedCriteria.forClass(
				AelPermissaoUnidSolic.class, "pus");

		criteria.createAlias("aie."+AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "amo");
		criteria.createAlias("amo."+AelAmostras.Fields.MATERIAL_ANALISE.toString(), "man");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa2");
		criteria.createAlias("aie."+AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS.toString(), "equ",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("amo."+AelAmostras.Fields.RECIPIENTE_COLETA.toString(), "rco");
		criteria.createAlias("amo."+AelAmostras.Fields.ANTICOAGULANTE.toString(), "atc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe");
		criteria.createAlias("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema");
		criteria.createAlias("ema."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
        criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(), "ico", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");

		criteria
		.add(Subqueries.exists(criteriaPus.setProjection(
				Property.forName("pus."
						+ AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("pus."+AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString(),"ise."+AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("pus."+AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString(),"ise."+AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString())).add(
												Restrictions.eqProperty("pus."+AelPermissaoUnidSolic.Fields.UFE_UNF.toString(),"ise."+AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString())).add(
														Restrictions.eqProperty("pus."+AelPermissaoUnidSolic.Fields.UNF_SOLICITANTE.toString(),"soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString())).add(
																Restrictions.eq("pus."+AelPermissaoUnidSolic.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora)).add(
																		Restrictions.or(
																				Restrictions.eq("pus."+AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), DominioSimNaoRotina.S),
																				Restrictions.and(
																						Restrictions.in("pus."+AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), new DominioSimNaoRotina[]{DominioSimNaoRotina.R,DominioSimNaoRotina.N}), 
																						Restrictions.eq("ise."+AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(), DominioTipoColeta.U)
																				)
																		))));

		criteria.add(Restrictions.in("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacoes));
		
		criteria.addOrder(Order.asc("aie."+AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("aie."+AelAmostraItemExames.Fields.AMO_SEQP.toString()));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()));

		return executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> obterSitCodigoPorNumeroConsultaESituacao(final Integer conNumero, final String situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");

		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");

		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), conNumero));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao));

		return executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameParaConclusao(final Integer conNumero, final Integer solicitacaoExameSeq, final Boolean usaDistinct) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria = criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(),"aliasExame");
		criteria = criteria.createAlias("aliasExame"+"."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(),"aliasAtendimento");
		criteria = criteria.createAlias("aliasAtendimento"+"."+AghAtendimentos.Fields.CONSULTA.toString(),"aliasConsulta");
		if(solicitacaoExameSeq!=null){
			criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString(), solicitacaoExameSeq));
		}
		criteria.add(Restrictions.eq("aliasConsulta"+"."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		if(usaDistinct){
			criteria.setProjection(Projections.distinct(Projections
					.projectionList().add(
							Projections.property(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME
									.toString()+"."+AelSolicitacaoExames.Fields.SEQ.toString()))));
		}
		return executeCriteria(criteria);
	}
	
	// ORADB Procedure FATP_VER_ITEM_SOLIC
	public void verificaItemSolicitacaoExames(
			final Integer soeSeq,
			final Short seqp, 
			final String usuarioLogado
	) throws ApplicationBusinessException {

		if (!isOracle()) {
			return;
		}
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.FATP_VER_ITEM_SOLIC;
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					cs = connection.prepareCall("{call " + nomeObjeto + "(?,?)}");
					CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, soeSeq);
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, seqp);
					cs.execute();
				} finally {
					if(cs != null){
						cs.close();
					}
				}
			}
		};
		try {
			this.doWork(work);
		} catch(final Exception e) {
			if (e.getCause().getMessage().indexOf("FAT-00705") > -1) {
				throw new ApplicationBusinessException(AelItemSolicitacaoExameDAOExceptionCode.FAT_00705);
			} else {
				final String valores = CoreUtil.configurarValoresParametrosCallableStatement(soeSeq, seqp);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
				throw new ApplicationBusinessException(
						AelItemSolicitacaoExameDAOExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores)
				);
			}
		}	
		
		if (work.getException() != null){
			if (work.getException().getMessage().indexOf("FAT-00705") > -1) {
				throw new ApplicationBusinessException(AelItemSolicitacaoExameDAOExceptionCode.FAT_00705);
			} else {
				final String valores = CoreUtil.configurarValoresParametrosCallableStatement(soeSeq, seqp);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores));
				throw new ApplicationBusinessException(
						AelItemSolicitacaoExameDAOExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores)
				);
			}
		}
	}



	public List<AelItemSolicitacaoExames> buscarRelatorioPacientesInternadosJejumNpo(
			final AghUnidadesFuncionais unidadeFuncional) {
		//P_INDICADOR ind_jejum = ''S'' OR ind_npo = ''S'
		//P_DATE sysdate + 1

		final DetachedCriteria c = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		realizarJoinsRelatorioPacientesInternados(c);

		c.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));

		final String[] codigos = {"AC","AG","AX"};
		c.add(Restrictions.in("sit."+AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigos));

		final Date dthrProgramada = DateUtil.adicionaHoras(new Date(), 12); //sysdate + to_number(12/24)
		c.add(Restrictions.le("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), dthrProgramada));

		c.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		final Criterion res1 = Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I);
		final Criterion res2 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE);

		final Date date = DateUtil.adicionaDias(new Date(), 1);
		final DetachedCriteria criteriaIha = DetachedCriteria.forClass(
				AelItemHorarioAgendado.class, "iha");		
		final Criterion res3 = Subqueries.exists(criteriaIha.setProjection(
				Property.forName("iha."
						+ AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString())).add(
								Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(),"ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())).add(
										Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SEQP.toString(),"ise."+AelItemSolicitacaoExames.Fields.SEQP.toString())).add(
												Restrictions.between("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), new Date(), date)));

		final Criterion or1 = Restrictions.and(res1, Restrictions.and(res2,res3));


		final Criterion res4 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.U,DominioOrigemAtendimento.C});

		final DetachedCriteria criteriaExd = DetachedCriteria.forClass(
				AelExamesDependentes.class, "exd");		
		final Criterion res5 = Subqueries.exists(criteriaExd.setProjection(
				Property.forName("exd."
						+ AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(),"ema."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(),"ema."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString())).add(
												Restrictions.eq("exd."+AelExamesDependentes.Fields.IND_OPCIONAL.toString(), DominioSimNao.N)));
		final Criterion or2 = Restrictions.and(res4, Restrictions.and(res2,res5));


		final Criterion res6 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.A,DominioOrigemAtendimento.X});
		final Criterion or3 = Restrictions.and(res6, res3);


		final Criterion res7 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.I,DominioOrigemAtendimento.U,DominioOrigemAtendimento.C});
		final Criterion res8 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.TRUE);
		final Criterion or4 = Restrictions.and(res7, res8);

		c.add( Restrictions.or(or1, Restrictions.or(or2,Restrictions.or(or3,or4))) );
			
		c.add( Restrictions.or(Restrictions.eq("ema."+AelExamesMaterialAnalise.Fields.IND_JEJUM.toString(), Boolean.TRUE),
                Restrictions.eq("ema."+AelExamesMaterialAnalise.Fields.IND_NPO.toString(), Boolean.TRUE)));

		return executeCriteria(c);
	}

	public List<AelItemSolicitacaoExames> buscarRelatorioPacientesInternadosPreparo(
			final AghUnidadesFuncionais unidadeFuncional) {
		//P_INDICADOR 'ind_preparo = ''S'''
		//P_DATE sysdate + 2 

		final DetachedCriteria c = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		realizarJoinsRelatorioPacientesInternados(c);

		c.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));

		final String[] codigos = {"AC","AG","AX","EC"};
		c.add(Restrictions.in("sit."+AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigos));

		c.add(Restrictions.le("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), new Date()));

		c.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		c.add(Restrictions.eq("ema."+AelExamesMaterialAnalise.Fields.IND_PREPARO.toString(), Boolean.TRUE));


		final Criterion res1 = Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I);
		final Criterion res2 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE);

		final Date date = DateUtil.adicionaDias(new Date(), 2);
		final DetachedCriteria criteriaIha = DetachedCriteria.forClass(
				AelItemHorarioAgendado.class, "iha");		
		final Criterion res3 = Subqueries.exists(criteriaIha.setProjection(
				Property.forName("iha."
						+ AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString())).add(
								Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(),"ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())).add(
										Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SEQP.toString(),"ise."+AelItemSolicitacaoExames.Fields.SEQP.toString())).add(
												Restrictions.between("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), new Date(), date)));

		final Criterion or1 = Restrictions.and(res1, Restrictions.and(res2,res3));


		final Criterion res4 = Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.U);
		final Criterion res5 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE);
		final DetachedCriteria criteriaExd = DetachedCriteria.forClass(
				AelExamesDependentes.class, "exd");		
		final Criterion res6 = Subqueries.exists(criteriaExd.setProjection(
				Property.forName("exd."
						+ AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(),"ema."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(),"ema."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString())).add(
												Restrictions.eq("exd."+AelExamesDependentes.Fields.IND_OPCIONAL.toString(), DominioSimNao.N)));

		final Criterion res7 = Subqueries.notExists(criteriaExd.setProjection(
				Property.forName("exd."
						+ AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(),"ema."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(),"ema."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString())));
		final Criterion or2 = Restrictions.and(res4, Restrictions.and(res5,Restrictions.or(res6, res7)));


		final Criterion res8 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.A,DominioOrigemAtendimento.X});
		final Criterion res9 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE);

		final Criterion res10 = Subqueries.exists(criteriaIha.setProjection(
				Property.forName("iha."
						+ AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString())).add(
								Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(),"ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())).add(
										Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SEQP.toString(),"ise."+AelItemSolicitacaoExames.Fields.SEQP.toString())).add(
												Restrictions.between("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), new Date(), date)));

		final Criterion or3 = Restrictions.and(res8, Restrictions.and(res9,res10));


		final Criterion or4 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.TRUE);

		c.add( Restrictions.or(or1, Restrictions.or(or2,Restrictions.or(or3,or4))) );

		return executeCriteria(c);
	}

	public List<AelItemSolicitacaoExames> buscarRelatorioPacientesInternadosDietaDiferenciada(
			final AghUnidadesFuncionais unidadeFuncional) {
		//P_INDICADOR 'ind_dieta_diferenciada = ''S'''
		//P_DATE sysdate + 1

		final DetachedCriteria c = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		realizarJoinsRelatorioPacientesInternados(c);

		c.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));

		final String[] codigos = {"AC","AG","AX"};
		c.add(Restrictions.in("sit."+AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigos));

		final Date dthrProgramada = DateUtil.adicionaHoras(new Date(), 12); //sysdate + to_number(12/24)
		c.add(Restrictions.le("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), dthrProgramada));

		c.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		final Criterion res1 = Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I);
		final Criterion res2 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE);

		final Date date = DateUtil.adicionaDias(new Date(), 1);
		final DetachedCriteria criteriaIha = DetachedCriteria.forClass(
				AelItemHorarioAgendado.class, "iha");		
		final Criterion res3 = Subqueries.exists(criteriaIha.setProjection(
				Property.forName("iha."
						+ AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString())).add(
								Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(),"ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())).add(
										Restrictions.eqProperty("iha."+AelItemHorarioAgendado.Fields.ISE_SEQP.toString(),"ise."+AelItemSolicitacaoExames.Fields.SEQP.toString())).add(
												Restrictions.between("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), new Date(), date)));

		final Criterion or1 = Restrictions.and(res1, Restrictions.and(res2,res3));


		final Criterion res4 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.U,DominioOrigemAtendimento.C});

		final DetachedCriteria criteriaExd = DetachedCriteria.forClass(
				AelExamesDependentes.class, "exd");		
		final Criterion res5 = Subqueries.exists(criteriaExd.setProjection(
				Property.forName("exd."
						+ AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(),"ema."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(),"ema."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString())).add(
												Restrictions.eq("exd."+AelExamesDependentes.Fields.IND_OPCIONAL.toString(), DominioSimNao.N)));
		final Criterion or2 = Restrictions.and(res4, Restrictions.and(res2,res5));


		final Criterion res6 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.A,DominioOrigemAtendimento.X});
		final Criterion or3 = Restrictions.and(res6, res3);


		final Criterion res7 = Restrictions.in("atd."+AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[]{DominioOrigemAtendimento.I,DominioOrigemAtendimento.U,DominioOrigemAtendimento.C});
		final Criterion res8 = Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE);
		final Criterion or4 = Restrictions.and(res7, res8);

		c.add( Restrictions.or(or1, Restrictions.or(or2,Restrictions.or(or3,or4))) );

		c.add(Restrictions.eq("ema."+AelExamesMaterialAnalise.Fields.IND_DIETA_DIFERENCIADA.toString(), Boolean.TRUE));

		return executeCriteria(c);
	}

	public List<AelItemSolicitacaoExames> buscarRelatorioPacientesInternadosUnidadeEmergencia(
			final AghUnidadesFuncionais unidadeFuncional) {
		//P_INDICADOR 'vuf.ind_unid_emergencia = ''S'''
		//P_DATE  sysdate - 5
		final DetachedCriteria c = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		realizarJoinsRelatorioPacientesInternados(c);

		c.add(Restrictions.eq("vuf."+AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString(), DominioSimNao.S));

		c.add(Restrictions.eq("sit."+AelSitItemSolicitacoes.Fields.CODIGO.toString(), "AX"));

		final Date date = DateUtil.adicionaDias(new Date(), -5);
		c.add(Restrictions.between("ise."+AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), date, new Date()));

		c.add(Restrictions.eq("man."+AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.FALSE));

		final DetachedCriteria criteriaExd = DetachedCriteria.forClass(
				AelExamesDependentes.class, "exd");		
		final Criterion res6 = Subqueries.exists(criteriaExd.setProjection(
				Property.forName("exd."
						+ AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(),"ema."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(),"ema."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString())).add(
												Restrictions.eq("exd."+AelExamesDependentes.Fields.IND_OPCIONAL.toString(), DominioSimNao.N)));

		final Criterion res7 = Subqueries.notExists(criteriaExd.setProjection(
				Property.forName("exd."
						+ AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString())).add(
								Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(),"ema."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString())).add(
										Restrictions.eqProperty("exd."+AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(),"ema."+AelExamesMaterialAnalise.Fields.MAN_SEQ.toString())));
		c.add(Restrictions.or(res6, res7));

		return executeCriteria(c);
	}

	public List<AelItemSolicitacaoExames> buscarRelatorioPacientesInternadosTodos(
			final AghUnidadesFuncionais unidadeFuncional) {

		final DetachedCriteria c = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		realizarJoinsRelatorioPacientesInternados(c);

		c.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));

		final String[] codigos = {"AC","AG","AX"};
		c.add(Restrictions.in("sit."+AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigos));

		c.add(Restrictions.eq("atd."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		return executeCriteria(c);
	}

	private void realizarJoinsRelatorioPacientesInternados(final DetachedCriteria c) {
		c.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		c.createAlias("soe."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "vuf");
		c.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		c.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		c.createAlias("ise."+AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "ran", JoinType.LEFT_OUTER_JOIN);
		c.createAlias("ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sit");
		c.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		c.createAlias("ise."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		c.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe");
		c.createAlias("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema");
		c.createAlias("ema."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man");
	}

	public List<AelItemSolicitacaoExames> obterItemSolicitacaoExamesPorNumeroConsultaESituacao(final Integer conNumero, final String vSituacaoPendente, final String vSituacaoCancelado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");

		criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ATENDIMENTO, "atd");
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.NUMERO_CONSULTA, conNumero));

		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), vSituacaoPendente));
		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), vSituacaoCancelado));

		return this.executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSituacaoSOE(final String situacao, final Integer soeSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao));

		return this.executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> buscarItensSolicitacaoExameNaoAgendados(final String situacao, final Integer soeSeq, final List<Short> listaSeqUnF) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		if (listaSeqUnF != null && !listaSeqUnF.isEmpty()){
			criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), listaSeqUnF));
		}
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao));

		return this.executeCriteria(criteria);
	}
	
	public boolean verificarExistenciaItensSolicitacaoExameNaoAgendados(final String situacao, final Integer soeSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao));

		return executeCriteriaExists(criteria);
	}
	
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorAtendimento(final Integer atdSeq, final Short unfSeq, final List<String> codigos) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ATENDIMENTO, "atd");
		
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.SEQ, atdSeq));
		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), codigos));
		
		// #52331 Exames dependentes sao atualizados quando o principal atualiza, nao precisa buscar
		criteria.add(Restrictions.isNull(AelItemSolicitacaoExames.Fields.ISE_SEQP.toString()));
		criteria.add(Restrictions.isNull(AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	public List<AelItemSolicitacaoExames> obterListaItensExame(final Integer atdSeq, final Short unfSeq, final Integer manSeq, final String siglaExame, String codigo) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ATENDIMENTO, "atd");
		
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.SEQ, atdSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), siglaExame));
		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), codigo));
		
		return executeCriteria(criteria);
		
	}

	public List<AelItemSolicitacaoExames> buscarItensPorMotivoCancelamento(final AelMotivoCancelaExames motivoCancelamento) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.MOC_SEQ.toString(), motivoCancelamento.getSeq()));
		
		return executeCriteria(criteria);
	
	}

	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacaoExameDependentes(final Integer soeSeq, final Short seqp) {
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString()+"."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString()+"."+AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

		return this.executeCriteria(criteria);
	}
	public List<AelItemSolicitacaoExames> obterSolicitacoesExamesPorConsultaESituacaoCancelado(
			final Integer numeroConsulta, final String vSituacaoCancelado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");

		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "sex");
		criteria.createAlias("sex." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "mat");
		
		criteria.add(Restrictions.eq("atd."+ AghAtendimentos.Fields.NUMERO_CONSULTA.toString(),numeroConsulta));
		criteria.add(Restrictions.ne("ise." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), vSituacaoCancelado));		
		
		criteria.addOrder(Order.asc("exa." + AelExames.Fields.DESCRICAO));
		return executeCriteria(criteria);
	}
	
	public boolean possuiExameCancelamentoSolicitante(final Integer soeSeq, final Short iseSoeSeq) {	

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		final String[] codigos = {"CS","AC","AX", "AG"};
		criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), codigos));
		
		return executeCriteria(criteria) != null;
		
	}

	/**
	 * ORADB: CURSOR: 		C_EXAMES 
	 * 		  PROCEDURE:	FATP_CARGA_COLETA_RD
	 */
	public List<AtendPacExternPorColetasRealizadasVO> listarAtendPacExternPorColetasRealizadas(final Date vDtHrInicio, final Date vDtHrFim, Integer codSangue, Integer codHemocentro, String situacaoCancelado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		//JOINS
		criteria.createCriteria("ATD." + AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO, "APE", JoinType.INNER_JOIN);
		criteria.createCriteria("APE." + AghAtendimentosPacExtern.Fields.DOADOR_REDOME, "DOR", JoinType.INNER_JOIN); // Marina 18/10/2012
		criteria.createCriteria("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO, "ATD", JoinType.INNER_JOIN);
		criteria.createCriteria("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME, "SOE", JoinType.INNER_JOIN);
		//PROJECTION
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.SEQP.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.SERVIDOR_RESPONSABILIDADE_MATRICULA.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.SERVIDOR_RESPONSABILIDADE_VIN_CODIGO.toString()))
				);
		//WHERE
		criteria.add(Restrictions.between("ISE." + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), vDtHrInicio, vDtHrFim));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), "HLE"));
		
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), codSangue));
		
		// Marina 18/10/2012
		criteria.add(Restrictions.eq("DOR." + AelDoadorRedome.Fields.LAE_SEQ.toString(), codHemocentro));
		
		criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacaoCancelado));
		criteria.add(Restrictions.sqlRestriction("atd3_.PAC_CODIGO = dor2_.PAC_CODIGO"));
				
		final List<Object[]> lista = this.executeCriteria(criteria);
		final List<AtendPacExternPorColetasRealizadasVO> resultList = new ArrayList<AtendPacExternPorColetasRealizadasVO>();
		for (final Object[] obj : lista) {
			final AtendPacExternPorColetasRealizadasVO atdPacExtVO = new AtendPacExternPorColetasRealizadasVO();
			atdPacExtVO.setSeq((Integer) obj[0]);
			atdPacExtVO.setDthrProgramada((Date) obj[1]);
			atdPacExtVO.setSeqp((Short) obj[2]);
			atdPacExtVO.setMatricula((Integer) obj[3]);
			atdPacExtVO.setVinCodigo((Short) obj[4]);
			resultList.add(atdPacExtVO);
		}
		
		return resultList;
	}
	
	public List<AelItemSolicitacaoExames> listarItemSolicitacaoExameMarcado(final Integer conNumero, final String situacaoCancelado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");

		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");

		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), conNumero));
		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacaoCancelado));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<MascaraExameVO> listarMascarasExamesVO(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Integer soeSeq, final Short seqp, final AelExames exame, final AelMateriaisAnalises materialAnalise, final AipPacientes paciente) {
		final org.hibernate.Query query = criarListaMascarasExamesVOQuery(soeSeq, seqp, exame, materialAnalise, paciente, orderProperty, asc, false);
		
		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}
		
		if (maxResult != null) {
			query.setMaxResults(maxResult);
		}
		
		return query.list();
	}

	public Long listarMascarasExamesVOCount(final Integer soeSeq, final Short seqp, final AelExames exame, final AelMateriaisAnalises materialAnalise,
			final AipPacientes paciente) {
		final org.hibernate.Query query = criarListaMascarasExamesVOQuery(soeSeq, seqp, exame, materialAnalise, paciente, null, false, true);
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private org.hibernate.Query criarListaMascarasExamesVOQuery(final Integer soeSeq, final Short seqp, final AelExames exame,
			final AelMateriaisAnalises materialAnalise, final AipPacientes paciente, final String orderProperty, final boolean asc, final boolean isCount) {
		final StringBuffer hql = new StringBuffer(250);

		hql.append(" select ");

		if (isCount) {
			hql.append("count(ise)");
		} else {
			hql.append(" new br.gov.mec.aghu.exames.vo.MascaraExameVO( ");
			hql.append("  ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
			hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
			hql.append(", pac.").append(AipPacientes.Fields.CODIGO.toString());
			hql.append(", pac.").append(AipPacientes.Fields.PRONTUARIO.toString());
			hql.append(", pac.").append(AipPacientes.Fields.NOME.toString());
			hql.append(", exa.").append(AelExames.Fields.SIGLA.toString());
			hql.append(", exa.").append(AelExames.Fields.DESCRICAO.toString());
			hql.append(", mtan.").append(AelMateriaisAnalises.Fields.SEQ.toString());
			hql.append(", mtan.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString());
			hql.append(" ) ");
		}
		
		hql.append(" from ");
		hql.append(AelItemSolicitacaoExames.class.getName());
		hql.append(" as ise ");
		hql.append(" join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString());
		hql.append(" as exa ");
		hql.append(" join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString());
		hql.append(" as mtan ");
		hql.append(" join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString());
		hql.append(" as se ");
		hql.append(" left join se.");
		hql.append(AelSolicitacaoExames.Fields.ATENDIMENTO.toString());
		hql.append(" as atd ");
		hql.append(" join atd.");
		hql.append(AghAtendimentos.Fields.PACIENTE.toString());
		hql.append(" as pac ");
		hql.append(" where 1 = 1 ");
		
		if (soeSeq != null) {
			hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = :soeSeq");
		}
		
		if (seqp != null) {
			hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" = :seqp");
		}
		
		if (exame != null) {
			hql.append(" and exa = :exame ");
		}
		
		if (materialAnalise != null) {
			hql.append(" and mtan = :materialAnalise ");
		}
		
		if (paciente != null) {
			hql.append(" and pac = :paciente ");
		}
		
		if (!isCount) {
			if (StringUtils.isNotBlank(orderProperty)) {
				hql.append(" ORDER BY ");
				hql.append(orderProperty);
				hql.append(asc ? " asc " : " desc ");
			} else {
				hql.append(" ORDER BY ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
				hql.append(" , ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
			}
		}
		
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		
		if (soeSeq != null) {
			query.setParameter("soeSeq", soeSeq);
		}
		
		if (seqp != null) {
			query.setParameter("seqp", seqp);
		}
		
		if (exame != null) {
			query.setParameter("exame", exame);
		}
		
		if (materialAnalise != null) {
			query.setParameter("materialAnalise", materialAnalise);
		}
		
		if (paciente != null) {
			query.setParameter("paciente", paciente);
		}
		
		return query;
	}

	/**
	 * Obtém AelItemSolicitacaoExames por id
	 * @param id
	 * @return
	 */
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(AelItemSolicitacaoExamesId id) {
		CoreUtil.validaParametrosObrigatorios(id);
		return this.obterItemSolicitacaoExamePorId(id.getSoeSeq(), id.getSeqp());
	}

	
	/**
	 * Obtém AelItemSolicitacaoExames por id
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.createAlias(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sit");
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		
		return (AelItemSolicitacaoExames)executeCriteriaUniqueResult(criteria);
	}
	
	public Date buscaMaiorDataLiberacao(Integer soeSeq, Short seqP) {
		final StringBuilder hql = new StringBuilder(150);
		hql.append("select max(ise.").append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA).append(") ")
		.append("from ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ")
		.append(" where ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = :soeSeq ")
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append("  = :seqP ")
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString())
		.append(" = :situacaoItemSolicitacaoCodigo ");

		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("seqP", seqP);
		query.setParameter("situacaoItemSolicitacaoCodigo", "LI");

		return (Date) query.uniqueResult();
	}
	
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorSituacaoSoeSeqp(final String situacao, final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		
		final List<AelItemSolicitacaoExames> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	
	/**
	 * Obtém Item de Solicitação de Exame para operação: Voltar Protocólo Único de Interfaceamento LWS
	 * @param soeSeq
	 * @param seqp
	 * @param siglaExame
	 * @param manSeq
	 * @param unfSeq
	 * @return
	 */
	public AelItemSolicitacaoExames obterItemSolicitacaoExameVoltarProtocoloUnicoLws(Integer soeSeq, Short seqp, 
			String siglaExame, Integer manSeq,  Short unfSeq){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "ema");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		
		criteria.add(Restrictions.eq("exa." + AelExames.Fields.SIGLA.toString(), siglaExame));
		criteria.add(Restrictions.eq("ema." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		
		return (AelItemSolicitacaoExames) this.executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Obtém Item de Solicitação de Exame para operação: Voltar Protocólo Único de Interfaceamento LWS
	 * @param soeSeq
	 * @param seqp
	 * @param siglaExame
	 * @param manSeq
	 * @param unfSeq
	 * @return
	 */
	public AelItemSolicitacaoExames obterItemSolicitacaoExameVoltarProtocoloUnicoLwsPorItemSolicitacaoExamePai(Integer iseSoeSeq, Short iseSeqp, 
			String siglaExame, Integer manSeq,  Short unfSeq){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "ema");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString(), "pai");
		
		criteria.add(Restrictions.eq("pai." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq("pai." + AelItemSolicitacaoExames.Fields.SEQP.toString(), iseSeqp));
		
		criteria.add(Restrictions.eq("exa." + AelExames.Fields.SIGLA.toString(), siglaExame));
		criteria.add(Restrictions.eq("ema." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		
		return (AelItemSolicitacaoExames) this.executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<Object[]> listarExamesAgendamentoSelecao(Integer soeSeq, Short unfSeq, DominioOrigemAtendimento origem, List<Short> unfHierarquico, String unidColetaUnf){
		//QUERY NATIVA
		StringBuffer sql = new StringBuffer(2600);
		
		sql.append("SELECT ise.soe_seq, ise.seqp ");
		sql.append("FROM agh.ael_item_solicitacao_exames ise ");
		sql.append("WHERE ise.soe_seq = :soeSeq ");
		sql.append("AND (( ");
		sql.append("   ise.sit_codigo in ( :situacoesItemSolExames ) ");
		sql.append("   AND cast(ise.soe_seq as varchar(30))||cast(ise.seqp as varchar(30)) in ");
		sql.append("   (SELECT ");
		sql.append("		 cast(i1.soe_seq as varchar(30))||cast(i1.seqp as varchar(30)) ");
		sql.append("	FROM agh.ael_unf_executa_exames u, ");
		sql.append("		 agh.ael_item_solicitacao_exames i1, ");
		sql.append("		 agh.agh_caract_unid_funcionais caract ");
		sql.append("    WHERE ");
		sql.append("		 i1.soe_seq = ise.soe_seq ");
		sql.append("		 AND (");
		if(!unfHierarquico.isEmpty()) {
			sql.append("               (i1.ufe_unf_seq)  in ");
			sql.append("		  		   (SELECT uf.seq from agh.agh_unidades_funcionais uf ");
			sql.append("		    	   WHERE uf.unf_seq in ( :listaUnfHierarquico )) ");
			sql.append("		  	  OR ");
		}
		sql.append("				i1.ufe_unf_seq = :aelfIdentifUnidadeSeq ) ");
		sql.append("         AND i1.ufe_unf_seq = caract.unf_seq" );
		sql.append("         AND caract.caracteristica = :unidExecutoraExames ");
		sql.append("         AND caract.caracteristica <> :unidColeta ");
		sql.append("         AND u.ema_exa_sigla = i1.ufe_ema_exa_sigla ");
		sql.append("         AND u.ema_man_seq = i1.ufe_ema_man_seq ");
		sql.append("         AND u.unf_seq = i1.ufe_unf_seq ");
		sql.append("         AND ((u.ind_agendam_previo_int in ( :dominioRestritoAreaExecutora ) ");
		sql.append("              AND coalesce( :origemAtendimento , :dominioOrigemX ) = :dominioOrigemI ) ");
		sql.append("			  OR (u.ind_agendam_previo_nao_int in ( :dominioRestritoAreaExecutora ) ");
		sql.append("              AND coalesce( :origemAtendimento , :dominioOrigemX ) <> :dominioOrigemI )) ");
		sql.append("   UNION ");
		sql.append("   		 SELECT ");
		sql.append("   		 	 	cast(i2.soe_seq as varchar(30))||cast(i2.seqp as varchar(30)) ");
		sql.append("   		 FROM ");
		sql.append("  				agh.ael_grupo_exames g, ");
		sql.append("  				agh.ael_grupo_exame_unid_exames ge, ");
		sql.append("  				agh.ael_tipos_amostra_exames t, ");
        sql.append("  				agh.ael_unf_executa_exames u, ");
        sql.append("  				agh.ael_item_solicitacao_exames i2 ");
		sql.append("   		 WHERE ");
        sql.append("  				i2.soe_seq = ise.soe_seq "); 
		sql.append("         		AND ( :unidColetaUnf ) = 'S' ");
		sql.append("         		AND u.ema_exa_sigla = i2.ufe_ema_exa_sigla ");
		sql.append("         		AND u.ema_man_seq = i2.ufe_ema_man_seq ");
		sql.append("         		AND u.unf_seq = i2.ufe_unf_seq ");
		sql.append("				AND ((u.ind_agendam_previo_int in ( :dominioRestritoAreaExecutora ) ");
		sql.append("              		 AND coalesce( :origemAtendimento , :dominioOrigemX ) = :dominioOrigemI ) ");
		sql.append("			         OR (u.ind_agendam_previo_nao_int in ( :dominioRestritoAreaExecutora ) ");
		sql.append("                     AND coalesce( :origemAtendimento , :dominioOrigemX ) <> :dominioOrigemI )) ");
		sql.append("         		AND t.ema_exa_sigla = u.ema_exa_sigla ");
		sql.append("         		AND t.ema_man_seq = u.ema_man_seq ");
		sql.append("         		AND t.origem_atendimento in (coalesce( :origemAtendimento , :dominioOrigemX ), :dominioOrigemT ) ");
		sql.append("         		AND t.responsavel_coleta = :dominioResponsavelColetaExamesC ");
		sql.append("         		AND t.unf_seq = :aelfIdentifUnidadeSeq ");
		sql.append("         		AND ge.ufe_ema_exa_sigla = u.ema_exa_sigla ");
		sql.append("         		AND ge.ufe_ema_man_seq = u.ema_man_seq ");
		sql.append("         		AND ge.ufe_unf_seq = u.unf_seq ");
		sql.append("         		AND g.seq = ge.gex_seq ");
		sql.append("         		and g.unf_seq = :aelfIdentifUnidadeSeq)) ");
		sql.append(')');
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setString("unidExecutoraExames", ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES.getCodigo());
		query.setString("unidColeta", ConstanteAghCaractUnidFuncionais.UNID_COLETA.getCodigo());
		query.setString("unidColetaUnf", unidColetaUnf);
		query.setString("dominioOrigemX", DominioOrigemAtendimento.X.toString());
		query.setString("dominioOrigemI", DominioOrigemAtendimento.I.toString());
		query.setString("dominioOrigemT", DominioOrigemAtendimento.T.toString());
		query.setString("dominioResponsavelColetaExamesC", DominioResponsavelColetaExames.C.toString());
		query.setInteger("soeSeq", soeSeq);
		query.setShort("aelfIdentifUnidadeSeq", unfSeq);
		query.setString("origemAtendimento", origem.toString());
		if(!unfHierarquico.isEmpty()) {
			query.setParameterList("listaUnfHierarquico", unfHierarquico);
		}
		query.setParameterList("dominioRestritoAreaExecutora", new String[]{DominioSimNaoRestritoAreaExecutora.S.toString(), DominioSimNaoRestritoAreaExecutora.R.toString()});
		query.setParameterList("situacoesItemSolExames", new String[]{DominioSituacaoItemSolicitacaoExame.AC.toString(), DominioSituacaoItemSolicitacaoExame.AX.toString(), DominioSituacaoItemSolicitacaoExame.AG.toString()});
		
		List<Object[]> results = query.list();
		
		return results;
	}

	public Boolean verificarExisteSolicitacaoExamePorSitucao(final Integer soeSeq, final Short seqp, final String situacao) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), situacao));
		
		final List<AelItemSolicitacaoExames> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	/***
	 * Pesquisa itens de situação de dependentes que são cancelados automaticamente 
	 * a situação não é obrigatória, mas se passada deve ser informada através da flag situacaoIgual se vai ser igual ou diferente
	 * @param soeSeq
	 * @param seqp
	 * @param situacaoIgual
	 * @param codigoLiberado
	 * @return
	 */
	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacaoExameDependentesCancelaAutomaticoPorSituacaoIgualOuDiferente(final Integer soeSeq, final Short seqp, final boolean situacaoIgual, final String codigoSituacao) {
		DetachedCriteria criteria = null;
		criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString()+"."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI.toString()+"."+AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

		if(codigoSituacao != null){
			if(situacaoIgual){
				criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), codigoSituacao));
			}else{
				criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), codigoSituacao));
			}
		}
		
		
		final StringBuilder sql = new StringBuilder(250).append(" exists(	select 1");
								sql.append("			from 	agh.ael_exames_dependentes exd");
								sql.append("			where 	exd.ind_cancela_automatico = ?");
								sql.append("			and 	exd.ema_exa_sigla_eh_dependente = {alias}.ufe_ema_exa_sigla");
								sql.append("			and		exd.ema_man_seq_eh_dependente = {alias}.ufe_ema_man_seq");
								sql.append("	)");
		
		final Object[] values = { DominioSimNao.S.toString()};
		final Type[] types = { StringType.INSTANCE};
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));
	
		return this.executeCriteria(criteria);
	}


	public List<AelItemSolicitacaoExames> buscarAelItemSolicitacaoExamesPorSoeSeqUnfSeq(final Integer soeSeq, final Short unfSeq) {
		return buscarAelItemSolicitacaoExamesPorSoeSeqUnfSeq(soeSeq, unfSeq, false);
		
	}
	
	public List<AelItemSolicitacaoExames> buscarAelItemSolicitacaoExamesPorSoeSeqUnfSeq(final Integer soeSeq, final Short unfSeq, final Boolean nrApNull) {
		return executeCriteria(this.obterbuscaAelItemSolicitacaoExamesPorSoeSeqUnfSeq(soeSeq, unfSeq, nrApNull));
	}

	protected DetachedCriteria obterbuscaAelItemSolicitacaoExamesPorSoeSeqUnfSeq(final Integer soeSeq, final Short unfSeq, final Boolean nrApNull) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		
		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		if (nrApNull) {
			criteria.add(Restrictions.isNull("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()));
		}

		return criteria;
	}

	public boolean existeNumeroApItemSolicitacaoExame(final Long numeroAp, final String codigo, final AelItemSolicitacaoExamesId id, final String situacaoCancelado, final String situacaoPendente, final Integer lu2Seq) {
		/*
		 * CURSOR c_ise (c_numero_ap ael_item_solicitacao_exames.numero_ap%type,
			c_soe_seq   ael_item_solicitacao_exames.soe_seq%type,
			c_seqp      ael_item_solicitacao_exames.seqp%type,
			c_sit_ca    ael_item_solicitacao_exames.sit_codigo%type,
			c_sit_pe    ael_item_solicitacao_exames.sit_codigo%type)
			IS
			SELECT soe_seq,
			seqp
			FROM ael_item_solicitacao_exames
			WHERE numero_ap = c_numero_ap
			AND ((soe_seq  = c_soe_seq AND seqp <> c_seqp) OR
			(soe_seq <> c_soe_seq))
			AND SUBSTR(sit_codigo,1,2) NOT IN (c_sit_ca,c_sit_pe);
		 */
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		

		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), id.getSoeSeq()),
						Restrictions.ne(AelItemSolicitacaoExames.Fields.SEQP.toString(), id.getSeqp())),
				Restrictions.ne(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), id.getSoeSeq())));

		criteria.add(Restrictions.sqlRestriction(" SUBSTR(sit_codigo,1,2) NOT IN (?,?)", new Object[] { situacaoCancelado, situacaoPendente },
				new Type[] { StringType.INSTANCE, StringType.INSTANCE }));

		return executeCriteriaCount(criteria) > 0;
	}

	public List<AelItemSolicitacaoExames> listarItemSolicitacaoExamePorSeqCirurgiaSitCodigo(
			final Integer seqCirurgia, final String[] codigos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.CRG_SEQ.toString(), seqCirurgia));
		criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), codigos));
		
		return executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> buscarItemSolicitacaoExamesPorCirurgia(
			Integer seqMbcCirurgia, String pSituacaoLiberado, String pSituacaoExecutado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		// Where
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.CRG_SEQ.toString(), seqMbcCirurgia));
		Object[] objSituacao = {pSituacaoLiberado,pSituacaoExecutado};
		criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), objSituacao));
		
		List<AelItemSolicitacaoExames> result = this.executeCriteria(criteria);
		
		return result;
	}
	
	//TODO: DiegoPacheco --> retirar o método abaixo 
	// qdo a estória #5494 que chama esta estória estiver implementada
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameAtendimentoPacientePorSoeSeqSeqp(
			final Integer soeSeq, final List<Short> listaSeqp) {
		String aliasIse = "ise";
		String aliasSis = "sis";
		String aliasSoe = "soe";
		String aliasAtd = "atd";
		String aliasPac = "pac";
		String separador = ".";

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, aliasIse);

		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "ise_exa", JoinType.INNER_JOIN);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "ise_mat", JoinType.INNER_JOIN);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "ise_unf", JoinType.INNER_JOIN);
		
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), aliasSoe, JoinType.INNER_JOIN);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), aliasSis, JoinType.INNER_JOIN);
		criteria.createAlias(aliasSoe + separador + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), aliasAtd, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasAtd + separador + AghAtendimentos.Fields.CONSULTA.toString(), "CNX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasAtd + separador + AghAtendimentos.Fields.PACIENTE.toString(), aliasPac, JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.in(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString(), listaSeqp));

		return executeCriteria(criteria);
	}

	public AelItemSolicitacaoExameRelLaudoUnicoVO obterAelItemSolicitacaoExameRelLaudoUnicoVOPorLuxSeq(final Long seq){
		final StringBuffer hql =  new StringBuffer(400);
		/*
		 select lul.lux_seq,
           ise.ufe_unf_seq unf_seq,
           ise.soe_seq       soe_seq
  from  ael_item_solicitacao_exames ise,
           ael_exame_ap_item_solics lul
where lul.lux_seq        = :lux_seq
  and   lul.ise_soe_seq= ise.soe_seq
  and   lul.ise_seqp      = ise.seqp

		 */
		
		
    	hql.append(" select " )
    	   .append("   new br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExameRelLaudoUnicoVO( ")
												    	   .append("           lul.id.luxSeq ")
												    	   .append("         , ise.unidadeFuncional ")
												    	   .append("         , ise.solicitacaoExame ") 
												    	   .append("        ) ") 
    	   
    	   .append(" from ")
    	   
		   .append("           AelItemSolicitacaoExames ise, ")
		   .append("           AelExameApItemSolic lul ")
		   
		   .append(" where 1=1 ")
		   .append("     and lul.id.luxSeq = :prmSeq ")
		   .append("     and lul.id.iseSoeSeq =  ise.id.soeSeq ")
		   .append("     and lul.id.iseSeqp =  ise.id.seqp ");
    	
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setLong("prmSeq", seq);
		query.setMaxResults(1);
		
		return (AelItemSolicitacaoExameRelLaudoUnicoVO) query.uniqueResult();		
	}
	
	public List<RelatorioFichaTrabalhoPatologiaExameVO> obterListaExames(Integer amoSoeSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(), "ICO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "RAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "VEM1");
		

		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "LUL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LUL." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "LUX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LUX." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "LUM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LUM." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "CON", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("AIE." + AelAmostraItemExames.Fields.SEQP.toString()), "amostraSeqP")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()),"itemSolExSeqP")
		
		.add(Projections.property("LUM." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()),"numeroAp")
		
		.add(Projections.property("CON." + AelConfigExLaudoUnico.Fields.SIGLA.toString()),"sigla")
		
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.PRIORIDADE_EXECUCAO.toString()),"prioridadeExecucao")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()),"tipoColeta")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()),"descMatAnalise")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString()),"itemRegiaoAnatomicaDescricao")
		.add(Projections.property("RAN." + AelRegiaoAnatomica.Fields.DESCRICAO.toString()),"regiaoAnatoDescricao")
		.add(Projections.property("ICO." + AelIntervaloColeta.Fields.DESCRICAO.toString()),"descIntervaloColeta")
		.add(Projections.property("VEM1." + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()),"nomeUsualMaterial");
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.IND_IMPRIME_FICHA.toString(), true));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(AghParametros.class);
		ProjectionList projectionListSubQuery = Projections.projectionList()
		.add(Projections.property(AghParametros.Fields.VLR_TEXTO.toString()));
		subQuery.setProjection(projectionListSubQuery);
		subQuery.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_SITUACAO_CANCELADO.toString()));
		
		criteria.add(Subqueries.propertyNotIn("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), subQuery));

		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioFichaTrabalhoPatologiaExameVO.class));
		
		criteria.addOrder(Order.asc("AIE." + AelAmostraItemExames.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioFichaTrabalhoPatologiaExameVO> obterListaExamesFichaTrabAmo(Integer amoSoeSeq, Short amoSeqP, Short unfSeq, String listaExames) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(), "ICO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "RAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "VEM1");
		
		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "LUL");
		criteria.createAlias("LUL." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "LUX");
		criteria.createAlias("LUX." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "LUM");		
		
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("AIE." + AelAmostraItemExames.Fields.SEQP.toString()), "amostraSeqP")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()),"itemSolExSeqP")
		
		.add(Projections.property("LUM." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()),"numeroAp")
		
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.PRIORIDADE_EXECUCAO.toString()),"prioridadeExecucao")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()),"tipoColeta")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()),"descMatAnalise")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString()),"itemRegiaoAnatomicaDescricao")
		.add(Projections.property("RAN." + AelRegiaoAnatomica.Fields.DESCRICAO.toString()),"regiaoAnatoDescricao")
		.add(Projections.property("ICO." + AelIntervaloColeta.Fields.DESCRICAO.toString()),"descIntervaloColeta")
		.add(Projections.property("VEM1." + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()),"nomeUsualMaterial");
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), Integer.valueOf(amoSeqP)));
		criteria.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.or(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.IND_IMPRIME_FICHA.toString(), true), 
				Restrictions.and(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.IND_IMPRIME_FICHA.toString(), false), Restrictions.in("UFE." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), Arrays.asList(listaExames.split("\\s*,\\s*"))))));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(AghParametros.class);
		ProjectionList projectionListSubQuery = Projections.projectionList()
		.add(Projections.property(AghParametros.Fields.VLR_TEXTO.toString()));
		subQuery.setProjection(projectionListSubQuery);
		subQuery.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_SITUACAO_CANCELADO.toString()));
		
		criteria.add(Subqueries.propertyNotIn("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), subQuery));
		
		criteria.setResultTransformer(Transformers
				.aliasToBean(RelatorioFichaTrabalhoPatologiaExameVO.class));
		
		return executeCriteria(criteria);
	}

	
	public List<RelatorioFichaTrabalhoPatologiaClinicaVO> obterListaExamesPatologiaClinica(Integer amoSoeSeq, Short amoSoeSeqP, Boolean recebeAmostra, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(), "ICO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "AMO");
		criteria.createAlias("AMO." + AelAmostras.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "RAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "VEM");
		
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.distinct(Projections.property("SOE." + AelSolicitacaoExames.Fields.SEQ.toString())),"soeSeq")
		.add(Projections.property("AMO." + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), "unfSeq")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),"unfSeqSolicitante")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),"unfDescricao")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.ANDAR.toString()),"andar")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.ALA.toString()),"ala")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE_CODIGO.toString()),"cspCnvCodigo")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO_SEQ.toString()),"cspSeq")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.CRIADO_EM.toString()),"dtSolic")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString() + "." + RapServidores.Fields.MATRICULA.toString()),"matriculaResponsavel")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString()),"vinCodigoResponsavel")
		.add(Projections.property("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString()),"leitoID")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.USA_ANTIMICROBIANOS.toString()),"usaAntimicrobianos")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString()),"informacoesClinicas")
		.add(Projections.property("AMO." + AelAmostras.Fields.TEMPO_INTERVALO_COLETA.toString()),"tempoIntervaloColeta")
		.add(Projections.property("AMO." + AelAmostras.Fields.UNID_TEMPO_INTERVALO_COLETA.toString()),"unidTempoIntervaloColeta")
		.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.RECEM_NASCIDO.toString()),"recemNascido")
		.add(Projections.property("ATD." + AghAtendimentos.Fields.NUMERO_CONSULTA.toString()),"atdConNumero")
		.add(Projections.property("AMO." + AelAmostras.Fields.SEQP.toString()), "amostraSeqP")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()),"itemSolExSeqP")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.PRIORIDADE_EXECUCAO.toString()),"prioridadeExecucao")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()),"tipoColeta")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString()),"descMatAnalise")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString()),"itemRegiaoAnatomicaDescricao")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.NRO_AMOSTRAS.toString()),"nroAmostras")
		.add(Projections.property("AMO." + AelAmostras.Fields.NRO_UNICO.toString()),"nroUnico")
		.add(Projections.property("AMO." + AelAmostras.Fields.DT_NUMERO_UNICO.toString()),"dtNumeroUnico")
		.add(Projections.property("AMO." + AelAmostras.Fields.NRO_FRASCO_FABRICANTE.toString()),"nroFrascoFabricante")
		.add(Projections.property("RAN." + AelRegiaoAnatomica.Fields.DESCRICAO.toString()),"regiaoAnatoDescricao")
		.add(Projections.property("ICO." + AelIntervaloColeta.Fields.DESCRICAO.toString()),"descIntervaloColeta")
		.add(Projections.property("VEM." + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()),"nomeUsualMaterial");
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq("AMO." + AelAmostras.Fields.SEQP.toString(), amoSoeSeqP));
		criteria.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		/*
			AND   ( NVL(:P_VEM_RECEB_AMOSTRA,'N')  = 'S'  AND VTL.IND_IMPRIME_FICHA = 'S'  OR
			             NVL(:P_VEM_RECEB_AMOSTRA,'N')  = 'N'  )
		 */
		
		if(recebeAmostra) {
			criteria.add(Restrictions.in("AIE." + AelAmostraItemExames.Fields.SITUACAO.toString(), new DominioSituacaoAmostra[] {DominioSituacaoAmostra.G, DominioSituacaoAmostra.C, 
				DominioSituacaoAmostra.M, DominioSituacaoAmostra.U, DominioSituacaoAmostra.R}));
			criteria.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.IND_IMPRIME_FICHA.toString(), true));
		}
		
		criteria.setResultTransformer(Transformers
				.aliasToBean(RelatorioFichaTrabalhoPatologiaClinicaVO.class));
		
		return executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> pesquisarSolicitacaoExames(Integer crgSeq, String[] situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao));
		
		return executeCriteria(criteria);
	}

	public List<ProcedimentosPOLVO> pesquisarExamesProcPOL(Integer codigo) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");

		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("atd" + "." + AghAtendimentos.Fields.PAC_CODIGO.toString()),
				ProcedimentosPOLVO.Fields.PAC_CODIGO.toString())						
		.add(Projections.property("exa."+ AelExames.Fields.DESCRICAO.toString()), 
				ProcedimentosPOLVO.Fields.DESCRICAO.toString())
		.add(Projections.property("soe."+ AelSolicitacaoExames.Fields.SEQ.toString()), 
				ProcedimentosPOLVO.Fields.SOE_SEQ.toString())
		.add(Projections.property("ise."+ AelItemSolicitacaoExames.Fields.PAC_ORU_ACC_NUMBER.toString()), 
				ProcedimentosPOLVO.Fields.PAC_ORU_ACC_NUMMER.toString())
		.add(Projections.property("ise."+ AelItemSolicitacaoExames.Fields.SEQP.toString()), 
				ProcedimentosPOLVO.Fields.SOE_SEQP.toString())
		.add(Projections.property("atd" + "." + AghAtendimentos.Fields.SEQ.toString()),ProcedimentosPOLVO.Fields.ATD_SEQ.toString());
		
		criteria.setProjection(projection);	
		
		//Joins com tabelas AelExames, AelExamesMaterialAnalise, AelSolicitacaoExames, AghAtendimentos 
		criteria.createAlias("ise."+ AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa");
		criteria.createAlias("ise."+ AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "ema");
		criteria.createAlias("ise."+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("ise."+ AelItemSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		
		// Where
		criteria.add(Restrictions.eq("atd" + "." + AghAtendimentos.Fields.PAC_CODIGO.toString(),codigo));
		criteria.add(Restrictions.eq("ema" + "." + AelExamesMaterialAnalise.Fields.NATUREZA.toString(),
					 DominioNaturezaExame.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));	
		criteria.add(Restrictions.eq("ise."+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), 
					 DominioSituacaoItemSolicitacaoExame.LI.toString()));	

	
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosPOLVO.class));
		
		return executeCriteria(criteria);
	}

	public List<AelItemSolicitacaoExames> pesquisarItensDeSolicitacaoProcResult(final Integer soeSeq, final Short seqp, String situacaoCancelado, String situacaoLiberado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.not(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp)));

		final Object[] objSituacao = {situacaoCancelado,situacaoLiberado};
		criteria.add(Restrictions.not(Restrictions.in(ISE+AelItemSolicitacaoExames.Fields.SIT_CODIGO, objSituacao)));
	
		
		return this.executeCriteria(criteria);
	}
	
	public Long quantidadeExamesPendentesLaudoAssiando(Integer soeSeq) {
		
		List<Object> result = null;
		StringBuffer hql = null;
		org.hibernate.Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select count(*)");


		//from
		hql.append(" from ");
		hql.append(AelItemSolicitacaoExames.class.getName());
		hql.append(" as ise ");
		hql.append(", ").append(AelExameApItemSolic.class.getName());
		hql.append(" as lul ");
		hql.append(", ").append(AelExameAp.class.getName());
		hql.append(" as lux ");
		hql.append(", ").append(AelAnatomoPatologico.class.getName());
		hql.append(" as lum ");
		
		//where
		hql.append(" where ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append("  = :soeSeq ");

		hql.append(" and lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		
		hql.append(" and lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
		
		hql.append(" and lux.").append(AelExameAp.Fields.SEQ.toString());
		hql.append(" = ");
		hql.append(" lul.").append(AelExameApItemSolic.Fields.LUX_SEQ.toString());
		
		hql.append(" and lux.").append(AelExameAp.Fields.LUM_SEQ.toString());
		hql.append(" = ");
		hql.append(" lum.").append(AelAnatomoPatologico.Fields.SEQ.toString());
		
		hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" != :situacao");

		hql.append(" and lux.").append(AelExameAp.Fields.ETAPAS_LAUDO.toString());
		hql.append(" = :etapaLaudo");
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("situacao", "LI");
		query.setParameter("etapaLaudo", DominioSituacaoExamePatologia.LA);
		
		result = query.list();
		
		if(result == null || result.isEmpty()) {
			return 0l; 
		}
		
		return (Long)result.get(0);
	}

	
	@SuppressWarnings("unchecked")
	public List<ConsultaMaterialApVO> listaMateriaisExameAp(Long luxSeq) {
		
		List<ConsultaMaterialApVO> result = null;
		StringBuffer hql = null;
		org.hibernate.Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select ");

		hql.append("ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(" as iseSoeSeq");

		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
		hql.append(" as iseSeqp");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.DESC_MATERIAL_ANALISE.toString());
		hql.append(" as descMaterialAnalise");
		
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.DESC_REGIAO_ANATOMICA.toString());
		hql.append(" as descRegiaoAnatomica");
		
		hql.append(", man.").append(AelMateriaisAnalises.Fields.SEQ.toString());
		hql.append(" as codMaterialAnalise");
		
		hql.append(", man.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString());
		hql.append(" as descricaoMaterial");
		
		hql.append(", exa.").append(AelExames.Fields.DESCRICAO_USUAL.toString());
		hql.append(" as descricaoUsual");
		
		hql.append(", ran.").append(AelRegiaoAnatomica.Fields.DESCRICAO.toString());
		hql.append(" as descricaoRegiaoAnat");
		
		//from
		hql.append(" from ");
		
		hql.append(AelItemSolicitacaoExames.class.getName());
		
		hql.append(" as ise ");
		hql.append(", ").append(AelMateriaisAnalises.class.getName());
		hql.append(" as man ");
		hql.append(", ").append(AelExames.class.getName());
		hql.append(" as exa ");
		hql.append(", ").append(AelExameApItemSolic.class.getName());
		hql.append(" as lul ");
		hql.append(" left join ise.").append(AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString()).append(" as ran ");		
		//where
		hql.append(" where lul.");
		hql.append(AelExameApItemSolic.Fields.LUX_SEQ.toString());
		hql.append("  = :luxSeq ");

		hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(" = ");
		hql.append(" lul.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString());
		
		hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
		hql.append(" = ");
		hql.append(" lul.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString());
		
		hql.append(" and exa.").append(AelExames.Fields.SIGLA.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		hql.append(" and man.").append(AelMateriaisAnalises.Fields.SEQ.toString());
		hql.append(" = ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		
		hql.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append("  not in (:situacao) ");
		
		hql.append(" order by ");
		hql.append(" ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString());
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("luxSeq", luxSeq);
		Object[] situacao = {"CA", "PE"};
		query.setParameterList("situacao", situacao);
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultaMaterialApVO.class));
		result = query.list();
		
		return result;
	}
	
	

	public List<AelItemSolicitacaoExames> buscarItensSolicitacaoExamePorAtendimentoParamentro(Integer atdSeq, List<String> situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacao.toArray()));
		
		return executeCriteria(criteria);
	}
	
	
	public List<AelItemSolicitacaoExamesVO> listarItemSolicitacaoExamesVO(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), 
				"EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), 
				"EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("EMA.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), 
				"MAT", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList();

		projection.add(Property.forName(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), 
				AelItemSolicitacaoExamesVO.Fields.ISE_SOE_SEQ.toString());
		projection.add(Property.forName(AelItemSolicitacaoExames.Fields.SEQP.toString()), 
				AelItemSolicitacaoExamesVO.Fields.ISE_SEQP.toString());
		projection.add(Property.forName(AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()), 
				AelItemSolicitacaoExamesVO.Fields.TIPO_COLETA.toString());
		projection.add(Property.forName(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()), 
				AelItemSolicitacaoExamesVO.Fields.SITUACAO_CODIGO.toString());
		projection.add(Property.forName(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()), 
				AelItemSolicitacaoExamesVO.Fields.UFE_UNF_SEQ.toString());
		projection.add(Property.forName(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()), 
				AelItemSolicitacaoExamesVO.Fields.UFE_EMA_EXA_SIGLA.toString());
		projection.add(Property.forName("EXA.".concat(AelExames.Fields.DESCRICAO_USUAL.toString())), 
						AelItemSolicitacaoExamesVO.Fields.DESCRICAO_USUAL.toString());
		projection.add(Property.forName("MAT.".concat(AelMateriaisAnalises.Fields.DESCRICAO.toString())), 
				AelItemSolicitacaoExamesVO.Fields.DESC_MATERIAL_ANALISE.toString());
				
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelItemSolicitacaoExamesVO.class));
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), 
				soeSeq));
		//criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Obtém AelItemSolicitacaoExames por seq material analise e seq item material exame
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelItemSolicitacaoExames> obterAelItemSolicitacaoExamesPorUfeEmaManSeqSoeSeq(Integer seq, Integer soeSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<ItemHorarioAgendadoVO> obterListaExamesParaAgendamentoEmGrupo(Integer soeSeq, List<Short> listaSeqp, Short hedGaeUnfSeq,
			Integer hedGaeSeqp, String[] codigos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),"soeSeq")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()),"seqp")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()),"sigla")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()),"seqMaterialAnalise")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()),"seqUnidade")
		.add(Projections.property("EXA." + AelExames.Fields.DESCRICAO.toString()),"descricaoExame")
		.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()),"descricaoMaterialAnalise")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),"descricaoUnidade")
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString()),"codigoSituacao");
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString(), listaSeqp));
		criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), codigos));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelGrupoExameUnidExame.class, "GEU");
		subCriteria.createAlias("GEU." + AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString(), "UFE");
		subCriteria.createAlias("GEU." + AelGrupoExameUnidExame.Fields.GRUPO_EXAME.toString(), "GEX");
		subCriteria.createAlias("GEX." + AelGrupoExames.Fields.GRADE_AGENDA_EXAME.toString(), "GAE");
		
		ProjectionList projectionListSubQuery = Projections.projectionList()
		.add(Projections.property("GEU." + AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString()))
		.add(Projections.property("GEU." + AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString()))
		.add(Projections.property("GEU." + AelGrupoExameUnidExame.Fields.UFE_UNF_SEQ.toString()));
		subCriteria.setProjection(projectionListSubQuery);
		
		subCriteria.add(Restrictions.eq("GAE." + AelGradeAgendaExame.Fields.UNF_SEQ.toString(), hedGaeUnfSeq));
		subCriteria.add(Restrictions.eq("GAE." + AelGradeAgendaExame.Fields.SEQP.toString(), hedGaeSeqp));
		subCriteria.add(Restrictions.eq("GEX." + AelGrupoExames.Fields.IND_AGENDA_EX_MESMO_HOR.toString(), Boolean.TRUE));
		subCriteria.add(Restrictions.eq("GEX." + AelGrupoExames.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eq("GEU." + AelGrupoExameUnidExame.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eqProperty("ISE." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(),
				"GEU." + AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString()));
		
		final Criterion c1 = Restrictions.and(Restrictions.in("UFE." + AelUnfExecutaExames.Fields.IND_AGENDAM_PREVIO_INT.toString(), new DominioSimNaoRestritoAreaExecutora[] {
			DominioSimNaoRestritoAreaExecutora.S, DominioSimNaoRestritoAreaExecutora.R}), Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento [] {
				DominioOrigemAtendimento.I, DominioOrigemAtendimento.H}));
				
		final Criterion c2 = Restrictions.and(Restrictions.in("UFE." + AelUnfExecutaExames.Fields.IND_AGENDAM_PREVIO_NAO_INT.toString(), new DominioSimNaoRestritoAreaExecutora[] {
			DominioSimNaoRestritoAreaExecutora.S, DominioSimNaoRestritoAreaExecutora.R}), Restrictions.not(Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento [] {
				DominioOrigemAtendimento.I, DominioOrigemAtendimento.H})));
		
		subCriteria.add(Restrictions.or(c1, c2));

		criteria.add(Subqueries.exists(subCriteria));

		criteria.setResultTransformer(Transformers
				.aliasToBean(ItemHorarioAgendadoVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	
	public List<AelItemSolicitacaoExamesVO> listarMascarasAtivasPorExame(String emaExaSigla, Integer emaManSeq,	Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");

		criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA");
		criteria.createCriteria("EMA.".concat(AelExamesMaterialAnalise.Fields.VERSAO_LAUDOS.toString()), "VEL");

		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),	AelItemSolicitacaoExamesVO.Fields.ISE_SOE_SEQ.toString())
		.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), AelItemSolicitacaoExamesVO.Fields.ISE_SEQP.toString())
		.add(Projections.property("VEL." + AelVersaoLaudo.Fields.SEQP.toString()), AelItemSolicitacaoExamesVO.Fields.VEL_SEQP.toString())
		.add(Projections.property("VEL." + AelVersaoLaudo.Fields.NOME_DESENHO.toString()), AelItemSolicitacaoExamesVO.Fields.DESC_MATERIAL_ANALISE.toString());

		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("EMA.".concat(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("EMA.".concat(AelExamesMaterialAnalise.Fields.MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("VEL.".concat(AelVersaoLaudo.Fields.SITUACAO.toString()), DominioSituacaoVersaoLaudo.A));

		criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), DominioSituacaoItemSolicitacaoExame.LI.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AelItemSolicitacaoExamesVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Estoria 5945
	 * CURSOR c_busca_pai  da procedure AELK_POL_SUM_EXAMES
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public AelItemSolicitacaoExames buscarExamePai(Integer soeSeq, Short seqp) {
		if(soeSeq != null && seqp != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));	
			return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
		}
		return null;
	}
	
	/**
	 * Obtém a criteria para pesquisas com itens de solicitação de exames associados a uma unidade funcional
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	private DetachedCriteria obterCriteriaUnidadeFuncionalItemSolicitacaoExame(Integer soeSeq, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "T1");
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "T2");
		criteria.add(Restrictions.eq("T1." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("T1." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		return criteria;
	}
	
	/**
	 * Verifica se a unidade funcional do item de solicitação de exames permite anexar documentos automaticamente
	 * @param seq
	 * @param soeSeq
	 * @return
	 */
	public Boolean verificarUnidadeFuncionalItemSolicitacaoExamePermiteAnexo(Integer soeSeq, Short seqp) {
		final DetachedCriteria criteria = this.obterCriteriaUnidadeFuncionalItemSolicitacaoExame(soeSeq, seqp);
		criteria.setProjection(Projections.property("T2." + AghUnidadesFuncionais.Fields.IND_ANEXA_DOC_AUTOMATICO.toString()));
		DominioSimNao resultado = (DominioSimNao)executeCriteriaUniqueResult(criteria);
		return resultado != null ? resultado.isSim() : false;
	}
	
	
	/**
	 * Obtém o local dos documentos que serão anexados automaticamente através da unidade funcional do item de solicitação de exame
	 * @param seq
	 * @param soeSeq
	 * @return
	 */
	public String obterLocalDocumentoAnexoUnidadeFuncionalItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		final DetachedCriteria criteria = this.obterCriteriaUnidadeFuncionalItemSolicitacaoExame(soeSeq, seqp);
		criteria.setProjection(Projections.property("T2." + AghUnidadesFuncionais.Fields.LOCAL_DOC_ANEXO.toString()));
		return (String)executeCriteriaUniqueResult(criteria);

	}
	
	
	public AelItemSolicitacaoExames pesquisarNumeroSolicitacaoExame(Short unfSeq, String exaSigla, Integer manSeq, Integer piuPacCodigo, Integer iseSoeSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createCriteria("SOE.".concat(AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE.toString()), "CNV");
		
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), iseSoeSeq));		
		
		if(seqp != null) {
			criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		}
		
		criteria.add(Restrictions.sqlRestriction(
				"(this_." +
				AelItemSolicitacaoExames.Fields.SOE_SEQ.name() +
				",this_." +
				AelItemSolicitacaoExames.Fields.SEQP.name() +
				") NOT IN (" +
				" SELECT " +
					AelPacUnidFuncionais.Fields.ISE_SOE_SEQ.name() + " , " 
					+ AelPacUnidFuncionais.Fields.ISE_SEQP.name() +
				" FROM " + 
					AelPacUnidFuncionais.class.getAnnotation(Table.class).schema() + "." +
					AelPacUnidFuncionais.class.getAnnotation(Table.class).name() +
                " WHERE " +
                AelPacUnidFuncionais.Fields.PIU_PAC_CODIGO.name() +
                " = " +  piuPacCodigo + ") ")
		);
		
		
		List<Integer> listaIseSoeSeq = this.listarSoeSeq(piuPacCodigo);
		List<Short> listaIseSeqp = this.listarSeqp(piuPacCodigo);
		
		criteria.add(Restrictions.and(
				Restrictions.in(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), listaIseSoeSeq), 
				Restrictions.in(AelItemSolicitacaoExames.Fields.SEQP.toString(), listaIseSeqp)));
		
		List<AelItemSolicitacaoExames> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	public List<Integer> listarSoeSeq(Integer piuPacCodigo) {
		DetachedCriteria criteria = this.obterCriterioConsulta(piuPacCodigo);
		criteria.setProjection(Projections.property(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		
		List<Integer> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			result = new LinkedList<Integer>();
			result.add(Integer.valueOf(0));
			return result;
		}
		
		return result;
	}
	
	public List<Short> listarSeqp(Integer piuPacCodigo) {
		DetachedCriteria criteria = this.obterCriterioConsulta(piuPacCodigo);
		criteria.setProjection(Projections.property(AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		List<Short> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			result = new LinkedList<Short>();
			result.add(Short.valueOf("0"));
			return result;
		}
		
		return result;
	}
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		return criteria;
	}
	
	private DetachedCriteria obterCriterioConsulta(Integer piuPacCodigo) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createCriteria("SOE.".concat(AelSolicitacaoExames.Fields.ATENDIMENTO.toString()), "ATD");
		criteria.add(Restrictions.eq("ATD.".concat(AghAtendimentos.Fields.PAC_CODIGO.toString()), piuPacCodigo));
		return criteria;
	}
	
	public List<AelResultadoExame> pesquisarDadosArquivoSecretariaCarga(Date dataInicio, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "ree");
		criteria.createCriteria("ree." + AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise", JoinType.INNER_JOIN);
		criteria.createCriteria("ise." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria("ise." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "man", JoinType.INNER_JOIN);
		criteria.createCriteria("ree." + AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "rco", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.createCriteria("atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createCriteria("pac." + AipPacientes.Fields.PACIENTE_DADO_CLINICOS.toString(), "pdc", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("pac." + AipPacientes.Fields.RECEM_NASCIDO.toString(), "pac1", JoinType.LEFT_OUTER_JOIN);
//		criteria.createCriteria("pac." + AipPacientes.Fields.PACIENTE_DADO_CLINICOS.toString(), "pdc2", JoinType.LEFT_OUTER_JOIN);
//		criteria.createCriteria("ise." + AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "vem", JoinType.INNER_JOIN);
		dataFim = DateUtil.adicionaDias(dataFim, 1);
		criteria.add(Restrictions.between("ise." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString(), dataInicio, dataFim));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "LI"));
		criteria.add(Restrictions.eq("ree." + AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		criteria.add(Restrictions.or(adicionarRestrictionsPrincipaisIseRee(criteria, "AHI", 67, 2829, 568, new Integer[]{2, 3}), 
					 Restrictions.or(adicionarRestrictionsPrincipaisIseRee(criteria, "AHI", 79, 2290, 460, new Integer[]{2, 3}), 
					 Restrictions.or(adicionarRestrictionsUfeEma(criteria, "CD3", 67), 
					 Restrictions.or(adicionarRestrictionsUfeEma(criteria, "CVH", 67), 
					 Restrictions.or(adicionarRestrictionsPrincipaisIseRee(criteria, "HIV", 67, 2829, 568, new Integer[]{2, 3}), 
					 Restrictions.or(adicionarRestrictionsPrincipaisIseRee(criteria, "HIV", 67, 2708, 541, new Integer[]{2, 3}), 
					 Restrictions.or(adicionarRestrictionsPrincipaisIseRee(criteria, "HIV", 79, 3151, 664, new Integer[]{2, 3}), 
							 adicionarRestrictionsPrincipaisIseRee(criteria, "IHI", 67, 21, 7, new Integer[]{2, 3})))))))));
		
//		criteria.setResultTransformer(Transformers.aliasToBean(ArquivoSecretariaVO.class));		
		criteria.addOrder(Order.asc("ise." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		return executeCriteria(criteria);
	}
	
	private Criterion adicionarRestrictionsUfeEma(DetachedCriteria criteria, String ufeEmaExaSigla, Integer ufeEmaManSeq) {
		return Restrictions.and(Restrictions.eq("exa." + AelExames.Fields.SIGLA.toString(), ufeEmaExaSigla), 
				Restrictions.eq("man." + AelMateriaisAnalises.Fields.SEQ.toString(), ufeEmaManSeq));
	}
	
	private Criterion adicionarRestrictionsPrincipaisIseRee(DetachedCriteria criteria, String ufeEmaExaSigla, Integer ufeEmaManSeq, 
			Integer pclCalSeq, Integer rcdGtcSeq, Integer[] rcdSeqp) {

		return Restrictions.and(Restrictions.eq("exa." + AelExames.Fields.SIGLA.toString(), ufeEmaExaSigla), 
			   Restrictions.and(Restrictions.eq("man." + AelMateriaisAnalises.Fields.SEQ.toString(), ufeEmaManSeq),
			   Restrictions.and(Restrictions.eq("ree." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), pclCalSeq), 
		       Restrictions.and(Restrictions.eq("rco." + AelResultadoCodificado.Fields.GTC_SEQ.toString(), rcdGtcSeq), 
		    		   Restrictions.in("rco." + AelResultadoCodificado.Fields.SEQ.toString(), rcdSeqp)))));		
	}

	public List<AelItemSolicitacaoExames> pesquisarExamesRealizadosAtendimentosDiversos(
			Date dataInicial, Date dataFinal, DominioSimNao grupoSus, FatConvenioSaude convenioSaude) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "vem");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE.toString(), "cnv");
		
		criteria.add(Restrictions.eq("ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));
		
		Date dataFinalMaisUm = DateUtils.addDays(dataFinal, 1);
		criteria.add(Restrictions.between("ise."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString(), dataInicial, dataFinalMaisUm));
		
		criteria.add(Restrictions.isNotNull("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString()));
		
		if(DominioSimNao.S.equals(grupoSus)){
			criteria.add(Restrictions.eq("cnv."+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		}else if(DominioSimNao.N.equals(grupoSus)){
			Criterion c1 = Restrictions.eq("cnv."+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.C);
			Criterion c2 = Restrictions.eq("cnv."+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.P);			
			criteria.add(Restrictions.or(c1, c2));
		}
		
		if(convenioSaude != null){
			criteria.add(Restrictions.eq("cnv."+FatConvenioSaude.Fields.CODIGO.toString(), convenioSaude.getCodigo()));
		}
		
		return executeCriteria(criteria);
	}

	public Boolean verificarTipoxConvPorConvCod(Short cnvCodigo, Boolean ispesquisarItemSolicitacaoExamePorSituacaoAreaExecutoraLiberadoOracle) {
		
		StringBuilder sqlSelect = new StringBuilder(150);
		
		sqlSelect.append("select tptab_Cod")
	 	.append(" from conv.tipo_x_conv")
	 	.append(" WHERE conv_Cod= ?")
	 	.append(" and tptab_cod in (28,29,30,56,57,58,59)");
	 	
	 	SQLQuery query = this.createSQLQuery(sqlSelect.toString());
	 	
	 	query.setParameter(0, cnvCodigo);
	 	
	 	@SuppressWarnings("unchecked")
	 	List<Object[]> lista = query.list();
	 	
	 	if(!lista.isEmpty()){
	 		return true;
	 	}else{
	 		return false;
	 	}
	}
	
	/**
	 * Consulta utilizada para estoria #5978 - 03-ISE-AEL_ITEM_SOLICITACAO_EXAMES-where.sql
	 * @param soeSeq
	 * @param ufeUnfSeq
	 * @return
	 */
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoPorSolicitacaoUnidadeExecutora(Integer soeSeq, Short ufeUnfSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "ISE");
		
		dc.createAlias("ISE.".concat(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()), "SOE", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("ISE.".concat(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString()), "EMA", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("ISE.".concat(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString()), "MAT", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("ISE.".concat(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString()), "EXA", JoinType.LEFT_OUTER_JOIN);

		dc.add(Restrictions.eq("ISE.".concat(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), soeSeq));
		dc.add(Restrictions.eq("ISE.".concat(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()), ufeUnfSeq));
		
		return executeCriteria(dc);
	}
	
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorNumeroApDescricaoEIseSeqp(Long numeroAp, Short iseSeqp, String descricao, Integer lu2Seq) {
		String aliasIse = "ise";
		String aliasAps = "aps";
		String aliasAxe = "axe";
		String aliasUee = "uee";
		String separador = ".";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, aliasIse);
		
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), aliasUee);
		criteria.createAlias(aliasUee + separador + AelUnfExecutaExames.Fields.AGRP_PESQUISA_X_EXAMES.toString(), aliasAxe);
		criteria.createAlias(aliasAxe + separador + AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA.toString(), aliasAps);

		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		
		criteria.add(Restrictions.eq(aliasAps + separador + AelAgrpPesquisas.Fields.DESCRICAO.toString(), descricao));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString(), iseSeqp));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Método que busca todas as informações do paciente, tem join soficiente para buscar tudo mesmo...tudo mesmo
	 * @param codigo
	 * @param seq_consulta
	 * @param filtro
	 * @return List<PesquisaExamesPacientesResultsVO>
	 */
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPaciente(Integer codigo, Integer seq_consulta, PesquisaExamesFiltroVO filtro) {
		ExameSolicitadoPorPacienteQueryBuilder builder = new ExameSolicitadoPorPacienteQueryBuilder();
		
		builder.setCodigo(codigo);
		builder.setSeqConsulta(seq_consulta);
		builder.setFiltro(filtro);

		DetachedCriteria criteria = builder.build();
		List<Object[]> results = executeCriteria(criteria);
		
		List<PesquisaExamesPacientesResultsVO> exames = new ArrayList<PesquisaExamesPacientesResultsVO>();
		for (Object[] record : results) {
			PesquisaExamesPacientesResultsVO exame = new PesquisaExamesPacientesResultsVO();
			
			exame.setCodigoSoe((Integer)record[8]);
			exame.setDtHrProgramada((Date)record[0]);
			exame.setDtHrEvento((Date)record[1]);
			exame.setIseSeq((Short)record[2]);
			exame.setSituacaoItem((String)record[3]);
			exame.setTipoColeta(((DominioTipoColeta)record[4]).getDescricao());
			if(record[5]!=null){
				exame.setEtiologia(((MciEtiologiaInfeccao)record[5]).getCodigo());
			}
			
			exame.setExame((String)record[6]);
			exame.setOrigemAtendimento((DominioOrigemAtendimento)record[7]);
			exame.setExisteDocAnexado(((Integer)record[10])>0);
			exame.setSituacaoCodigo((String)record[11]);
			
			exames.add(exame);
		}

		return exames;
	}

	public Integer buscarSeqExameInternetGrupo(Integer soeSeq, Short seqp) {
		
		final StringBuilder hql = new StringBuilder(100);
		
		hql.append("select ggu.").append(AelExameInternetGrupoArea.Fields.EXAME_INTERNET_GRUPO_SEQ)
		.append(" from ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ")
		.append(", ").append(AelExameInternetGrupoArea.class.getSimpleName()).append(" ggu ")
		.append(" where ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = :soeSeq ")
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append("  = :seqp ")
		.append(" and ggu.").append(AelExameInternetGrupoArea.Fields.UNIDADE_FUNCIONAL_SEQ.toString())
		.append(" = ")
		.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());

		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("seqp", seqp);		
        				
		return (Integer) query.uniqueResult();
	}


	@SuppressWarnings("unchecked")
	public List<AelItemSolicitacaoExames> buscarItemExamesLiberadosPorGrupo(Integer soeSeq, Integer grupo) {
		
//		final StringBuilder hql = new StringBuilder(200);
//		
//		hql.append("select distinct ise ")
//		.append(" from ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ")
//		.append(", ").append(AelExameInternetGrupoArea.class.getSimpleName()).append(" ggu ")
//		.append(" where ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = :soeSeq ")
//		.append(" and ggu.").append(AelExameInternetGrupoArea.Fields.EXAME_INTERNET_GRUPO_SEQ.toString()).append(" = :grupo ")
//		.append(" and ggu.").append(AelExameInternetGrupoArea.Fields.UNIDADE_FUNCIONAL_SEQ.toString())
//		.append(" = ")
//		.append(" ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString())
//		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()).append(" = :situacao");
//
//		final org.hibernate.Query query = createHibernateQuery(hql.toString());
//		query.setParameter("soeSeq", soeSeq);
//		query.setParameter("grupo", grupo);
//		query.setParameter("situacao", "LI");
//		
//		List<AelItemSolicitacaoExames> antigo = query.list();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "SIS",JoinType.INNER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE", JoinType.INNER_JOIN);
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.EXAME_INTERNET_GRUPO_AREA.toString(), "GGU",JoinType.INNER_JOIN);
		criteria.createAlias("GGU."+AelExameInternetGrupoArea.Fields.EXAME_INTERNET_GRUPO.toString(), "GEI",JoinType.INNER_JOIN);
		//criteria.createAlias("GGU."+AelExameInternetGrupoArea.Fields.EXAME_INTERNET_GRUPO.toString(), "GEI",JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("SIS."+AelSitItemSolicitacoes.Fields.CODIGO.toString(), "LI"));
		criteria.add(Restrictions.eq("GEI."+AelExameInternetGrupo.Fields.SEQ.toString(), grupo));
        				
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(), "ATE",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATE."+AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString(), "LBE",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATE."+AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString(), "MEX",JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		
		List<AelItemSolicitacaoExames> novo = executeCriteria(criteria);
		
		return novo;
	}
	
	/**
	 * ORADB AELK_EXAME_PCT.AELC_TEM_PROVA_A_COLETAR
	 * 
	 * Verificar se um exame de PCT pendente associado ao atendimento,
	 *  dentro da validade, conforme o parâmetro P_VALIDADE_AMOSTRA_PCT.
	 * 
	 * @param atdSeq
	 * @param exaSigla
	 * @param manSeq
	 * @param unfSeq
	 * @param situacaoCancelado
	 * @param validadeAmostra
	 * @return true se possuir exame
	 */
	public Boolean existeExamePendente(Integer atdSeq, String exaSigla,
			Integer manSeq, Short unfSeq, String situacaoCancelado,
			Integer validade) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), situacaoCancelado));		
		
		criteria.add(Restrictions.between("SOE."
				+ AelSolicitacaoExames.Fields.CRIADO_EM.toString(),
				DateUtil.adicionaDias(new Date(), -validade/24),
				DateUtil.adicionaDias(new Date(), validade/24)));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorAtendimentoCirurgico(
			Integer atdSeq, Integer crgSeq, String exaSigla, String[] situacao, Boolean geradoAutomatico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		criteria.add(Restrictions.eq("SOE."
				+ AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ISE."
				+ AelItemSolicitacaoExames.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("ISE."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), exaSigla));
		if(geradoAutomatico != null){
			criteria.add(Restrictions.eq("ISE."
						+ AelItemSolicitacaoExames.Fields.IND_GERADO_AUTOMATICO
								.toString(), geradoAutomatico));
		}
		criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO
								.toString(), situacao));
		criteria.addOrder(Order.desc("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		
		return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
	}
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorAtendimento(
			Integer atdSeq, String exaSigla, String[] situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("SOE."
				+ AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ISE."
				+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO
								.toString(), situacao));
		criteria.addOrder(Order.desc("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		
		List<AelItemSolicitacaoExames> retorno = this.executeCriteria(criteria);
		//Retorna a ultima solicitação para ser imprimida
		if(retorno != null && retorno.size() > 0) {
			return retorno.get(0);
		}else{
			return null;
		}
	}	
	
	public ImprimeEtiquetaVO obterSiglaNumeroExameAmostra(ImprimeEtiquetaVO imprimeEtiquetaVO) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "lu2");
	
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), imprimeEtiquetaVO.getSoeSeq()));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), imprimeEtiquetaVO.getAmoSeqP()));
	
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("lu2." + AelConfigExLaudoUnico.Fields.SIGLA.toString()), "sigla")
				.add(Projections.property("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()), "numeroAp");
		
		criteria.setProjection(projectionList);

		criteria.setResultTransformer(Transformers.aliasToBean(ImprimeEtiquetaVO.class));
	
		ImprimeEtiquetaVO imprimeEtiqueta = (ImprimeEtiquetaVO)executeCriteriaUniqueResult(criteria);
	
		return imprimeEtiqueta;
	}
	
	
	public List<AelItemSolicitacaoExames> buscarItensPorAmostra(Integer soeSeq, Integer amoSeqp) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "aie");

	
		criteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
	
	
		return executeCriteria(criteria);
	}
	
	public NumeroApTipoVO obterNumeroApTipoPorSolicitacao(Integer soeSeq, Short seqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.setProjection( Projections.projectionList()
		        .add( Projections.property("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()),"numeroAp" )
		        .add( Projections.property("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString()), "lu2Seq" )
		        .add( Projections.groupProperty("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()) )
		        .add( Projections.groupProperty("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString()) )
		    );
		
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

		criteria.setResultTransformer(Transformers.aliasToBean(NumeroApTipoVO.class));
		
		return (NumeroApTipoVO) executeCriteriaUniqueResult(criteria);
	}
	
	public List<DataRecebimentoSolicitacaoVO> listarDataRecebimentoTipoExamePatologico(Long numeroAp, Integer lu2Seq, String situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "lu2");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "ext", JoinType.LEFT_OUTER_JOIN);

		criteria.setProjection( Projections.projectionList()
		        .add( Projections.max("ext." + AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString()), "dataRecebimento" )
		        .add( Projections.property("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()),"soeSeq" )
		        .add( Projections.groupProperty("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()) )
		    );
		
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		criteria.add(Restrictions.eq("ext." + AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), situacao));

		criteria.setResultTransformer(Transformers.aliasToBean(DataRecebimentoSolicitacaoVO.class));
		
		return executeCriteria(criteria);
		
	}
	
	public List<DataLiberacaoVO> listarDataLiberacaoTipoExamePatologico(Long numeroAp, Integer lu2Seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "lu2");

		criteria.setProjection( Projections.projectionList()
		        .add( Projections.max("ise." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), "dataLiberacao" )
		    );
		
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));

		criteria.setResultTransformer(Transformers.aliasToBean(DataLiberacaoVO.class));
		
		return executeCriteria(criteria);
		
	}

	public AelItemSolicitacaoExames obterItemSolicitacaoExamesPorChavePrimaria(AelItemSolicitacaoExamesId id) {
		
		CoreUtil.validaParametrosObrigatorios(id);
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "SIT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.INTERVALO_COLETA.toString(), "INC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.REGIAO_ANATOMICA.toString(), "RAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.SERVIDOR_RESPONSABILIDADE.toString(), "SER_RESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.ETIOLOGIA.toString(), "ETI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), id.getSoeSeq()));
		criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), id.getSeqp()));
		
		return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Listar os itens de solicitação de exame do paciente.
	 * 
	 * Web Service #37238
	 * 
	 * @param siglaExame
	 * @param seqMaterial
	 * @param codPaciente
	 * @return
	 */
	public List<AelItemSolicitacaoExamesId> listarPorSiglaMaterialPaciente(String siglaExame, Integer seqMaterial, Integer codPaciente) {

		// FROM
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");

		// JOINS
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");

		// WHERE
		criteria.add(Restrictions.eq("EXA." + AelExames.Fields.SIGLA.toString(), siglaExame));
		criteria.add(Restrictions.eq("MAN." + AelMateriaisAnalises.Fields.SEQ.toString(), seqMaterial));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codPaciente));

		// SELECT
		criteria.setProjection(Projections.projectionList()//
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), "soeSeq")//
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), "seqp"));//
		criteria.setResultTransformer(Transformers.aliasToBean(AelItemSolicitacaoExamesId.class));

		return super.executeCriteria(criteria);
	}
	
	/**
	 * Web Service #38474
	 * Utilizado nas estórias #864 e #27542
	 * @param atdSeq
	 * @return Boolean
	 */
	public Boolean verificarExameVDRLnaoSolicitado(Integer atdSeq, String vlrParamExameVDRL){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.ATD_SEQ.toString(), atdSeq));
		
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString(), vlrParamExameVDRL));
		criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "CA"));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 * Serviço para verificar se o atendimento do Recém Nascido possui alguma
	 * solicitação de exame. Serviço #42021
	 * 
	 * @param atdSeq
	 * @param situacoes
	 * @return
	 */
	public List<AelSolicitacaoExames> listarSolicitacaoExamesPorSeqAtdSituacoes(Integer atdSeq, String ... situacoes) {
		SolicitacaoExamesPorSeqAtdSituacoesQueryBuilder builder =  new SolicitacaoExamesPorSeqAtdSituacoesQueryBuilder();
		return executeCriteria(builder.build(atdSeq, situacoes));
	}
		
	/**
	 * Pesquisa itens de solicitacao de exames com resultado não visualizado
	 * @param atdSeq
	 * @return List<AelItemSolicitacaoExames>
	 */
	public List<AelItemSolicitacaoExames> pesquisarExamesNaoVisualizados(final Integer atdSeq) {

		final DetachedCriteria criteria = obterCriteriaExamesComResultadoNaoVisualizado(atdSeq);
		return executeCriteria(criteria);
	}	
	
	/**
	 * Verifica se o atendimento possui exames cujo resultado não foi visualizado, 
	 * usado para exibição de icone na prescrição médica
	 * @param atdSeq
	 * @return boolean
	 */
	public Boolean pesquisarExamesResultadoNaoVisualizado(
			final Integer atdSeq) {

		final DetachedCriteria criteria = obterCriteriaExamesComResultadoNaoVisualizado(atdSeq);
		return executeCriteriaCount(criteria) > 0;
	}
	
	private DetachedCriteria obterCriteriaExamesComResultadoNaoVisualizado(final Integer atdSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		criteria.createAlias(ISE + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias(SOE + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq(ATD + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));

		// SubCriteria/SubConsulta (NOT EXISTS)
		final DetachedCriteria subQueryIts = DetachedCriteria.forClass(AelItemSolicConsultado.class,"its");
		subQueryIts.createAlias(ITS + AelItemSolicConsultado.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "sub_ise");
		subQueryIts.createAlias(SUB + ISE + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "sub_soe");
		subQueryIts.createAlias(SUB + SOE + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "sub_atd");
		subQueryIts.setProjection(Projections.property(ITS + AelItemSolicConsultado.Fields.ITEM_SOLICITACAO_EXAMES.toString()));
		subQueryIts.add(Property.forName(ITS + AelItemSolicConsultado.Fields.ISE_SOE_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		subQueryIts.add(Property.forName(ITS + AelItemSolicConsultado.Fields.ISE_SEQP.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		subQueryIts.add(Restrictions.eq(SUB + ATD + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		subQueryIts.add(Restrictions.eq(SUB + ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));
		criteria.add(Subqueries.notExists(subQueryIts));

		// SubCriteria/SubConsulta (NOT EXISTS)
		final DetachedCriteria subQueryItri = DetachedCriteria.forClass(AelItemResulImpressao.class,"itri");
		subQueryItri.setProjection(Projections.property(ITRI + AelItemResulImpressao.Fields.ITEM_SOLICITACAO_EXAMES.toString()));
		subQueryItri.add(Property.forName(ITRI + AelItemResulImpressao.Fields.ISE_SOE_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		subQueryItri.add(Property.forName(ITRI + AelItemResulImpressao.Fields.ISE_SEQP.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		criteria.add(Subqueries.notExists(subQueryItri));
		
		return criteria;
	}

	/**
	 * Pesquisa itens de solicitacao de exames não visualizados para exibição de icone na prescrição médica
	 * @param atdSeq
	 * @param servidor
	 * @return
	 */
	public Boolean pesquisarExamesResultadoVisualizadoOutroMedico(
			final Integer atdSeq,  final RapServidores servidor) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		criteria.createAlias(ISE + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias(SOE + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq(ATD + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));

		// SubCriteria/SubConsulta (NOT EXISTS)
		final DetachedCriteria subQueryIts = DetachedCriteria.forClass(AelItemSolicConsultado.class,"its");
		subQueryIts.createAlias(ITS + AelItemSolicConsultado.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "sub_ise");
		subQueryIts.createAlias(SUB + ISE + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "sub_soe");
		subQueryIts.createAlias(SUB + SOE + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "sub_atd");
		subQueryIts.setProjection(Projections.property(ITS + AelItemSolicConsultado.Fields.ITEM_SOLICITACAO_EXAMES.toString()));
		subQueryIts.add(Property.forName(ITS + AelItemSolicConsultado.Fields.ISE_SOE_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		subQueryIts.add(Property.forName(ITS + AelItemSolicConsultado.Fields.ISE_SEQP.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		subQueryIts.add(Restrictions.eq(SUB + ATD + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		subQueryIts.add(Restrictions.eq(SUB + ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));
		subQueryIts.add(Restrictions.eq(ITS + AelItemSolicConsultado.Fields.SERVIDOR_MATRICULA.toString(), servidor.getId().getMatricula()));
		subQueryIts.add(Restrictions.eq(ITS + AelItemSolicConsultado.Fields.SERVIDOR_CODIGO_VINCULO.toString(), servidor.getId().getVinCodigo()));
		criteria.add(Subqueries.notExists(subQueryIts));

		// SubCriteria/SubConsulta (NOT EXISTS)
		final DetachedCriteria subQueryItri = DetachedCriteria.forClass(AelItemResulImpressao.class,"itri");
		subQueryItri.setProjection(Projections.property(ITRI + AelItemResulImpressao.Fields.ITEM_SOLICITACAO_EXAMES.toString()));
		subQueryItri.add(Property.forName(ITRI + AelItemResulImpressao.Fields.ISE_SOE_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		subQueryItri.add(Property.forName(ITRI + AelItemResulImpressao.Fields.ISE_SEQP.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		criteria.add(Subqueries.notExists(subQueryItri));
		criteria.setResultTransformer(Transformers.aliasToBean(VerificaItemSolicitacaoExamesVO.class));

		return executeCriteriaCount(criteria) > 0;
	}

	public Boolean verificarSeItemTemExameDeImagem(AelItemSolicitacaoExamesId id) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
		criteria.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.ID.toString(), id));
		criteria.add(Restrictions.isNotNull(ISE + AelItemSolicitacaoExames.Fields.PAC_ORU_ACC_NUMBER.toString()));
		return executeCriteriaExists(criteria);
	}

	/**
     * #43044 - Cursor cur_ise - F1
     * @param soeSeq
     * @param situacaoCancelado
     * @param situacaoPendente
     * @return
     */
     public List<ItemSolicitacaoExameAtendimentoVO> obterCursorItemSolicitacaoExame(Integer soeSeq, String situacaoCancelado, String situacaoPendente) {
           
           DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE"); 
           
           ProjectionList projList = Projections.projectionList(); 
           projList.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), ItemSolicitacaoExameAtendimentoVO.Fields.SOE_SEQ.toString());
           projList.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), ItemSolicitacaoExameAtendimentoVO.Fields.SEQP.toString());
           projList.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFEEMAEXASIGLA.toString()), ItemSolicitacaoExameAtendimentoVO.Fields.UFE_EMA_EXA_SIGLA.toString());
           projList.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFEEMAMANSEQ.toString()), ItemSolicitacaoExameAtendimentoVO.Fields.UFE_EMA_MAN_SEQ.toString());
           projList.add(Projections.property("ATD." + AghAtendimentos.Fields.ORIGEM.toString()),ItemSolicitacaoExameAtendimentoVO.Fields.ORIGEM_ATENDIMENTO.toString());
           projList.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()),ItemSolicitacaoExameAtendimentoVO.Fields.ATD_SEQ.toString());
           projList.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO_CODIGO.toString()),ItemSolicitacaoExameAtendimentoVO.Fields.CSP_CNV_CODIGO.toString());
           projList.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.CSPSEQ.toString()),ItemSolicitacaoExameAtendimentoVO.Fields.CSP_SEQ.toString());          
           
           criteria.setProjection(projList);
           
           criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE", JoinType.INNER_JOIN);
           criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
           
           criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.SEQ.toString() , soeSeq));
           criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SITCODIGO.toString(), situacaoCancelado));
           criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SITCODIGO.toString(), situacaoPendente));
           
            criteria.setResultTransformer(Transformers.aliasToBean(ItemSolicitacaoExameAtendimentoVO.class));
           
           return executeCriteria(criteria);  
     }
     
     public DetachedCriteria obterCriteriaExamesLaudoAih(Integer conNumero, String cSituacaoItem){
 		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
 		
 		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE", JoinType.INNER_JOIN);
 		criteria.createAlias("SOE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
 		
 		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.CON_NUMERO.toString(), conNumero));
 		criteria.add(Restrictions.ne("ISE."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), cSituacaoItem));
 		
 		return criteria;
 	}
     
     public List<AelItemSolicitacaoExames> verificaExamePendente(Integer conNumero, String cSituacaoItem){
 		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
 		
 		criteria = obterCriteriaExamesLaudoAih(conNumero, cSituacaoItem);
 		
 		return executeCriteria(criteria);
 	}
     
     public List<VerificaItemSolicitacaoExamesVO> verificaSolicitacaoExame(Integer conNumero, String cSituacaoItem){
 		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
 		
 		criteria = obterCriteriaExamesLaudoAih(conNumero, cSituacaoItem);
 		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("ISE."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())),"soeSeq")
 														   .add(Projections.property("ISE."+AelItemSolicitacaoExames.Fields.IND_INF_COMPL_IMP.toString()),"indInfComplImp"));
 				
 		criteria.addOrder(Order.asc("ISE."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
 		criteria.setResultTransformer(Transformers.aliasToBean(VerificaItemSolicitacaoExamesVO.class));
 		
 		return executeCriteria(criteria);
 	}
    
    /**
     * #50701 - P2 parte1 - c_exames
     * @param pacCodigo
     * @return List<ExamesLiberadosVO>
     */
    public List<ExamesLiberadosVO> montarExamesLiberados01(Integer pacCodigo) {
    	
    	String projectionAmostra = "SUBSTR((case when man5_.SEQ = 7 then NULL"
                       	   .concat("			 when man5_.SEQ = 83 then NULL")
                           .concat("			 else man5_.DESCRICAO")
                   		   .concat("		end),1,15) amostra");
    	
    	DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISEX");
    	
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UFE", JoinType.INNER_JOIN);
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOEX", JoinType.INNER_JOIN);
    	criteria.createAlias("SOEX."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATDX", JoinType.INNER_JOIN);
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
    	
    	criteria.add(Restrictions.eq("ATDX."+AghAtendimentos.Fields.PAC_COD.toString(), pacCodigo));
    	criteria.add(Restrictions.sqlRestriction("SUBSTR(this_.sit_codigo,1,2) = ?", "LI", StringType.INSTANCE));
    	
    	ProjectionList projList = Projections.projectionList();
    	projList.add(Projections.property("EXA."+AelExames.Fields.DESCRICAO_USUAL.toString()), ExamesLiberadosVO.Fields.NOME_EXAME.toString());
    	projList.add(Projections.sqlProjection(projectionAmostra, new String[] {ExamesLiberadosVO.Fields.AMOSTRA.toString()}, new Type[] {StringType.INSTANCE}));
    	projList.add(Projections.property("UFE."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), ExamesLiberadosVO.Fields.LABORATORIO.toString());
    	projList.add(Projections.property("ISEX."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), ExamesLiberadosVO.Fields.SOE_SEQ.toString());
    	projList.add(Projections.property("ISEX."+AelItemSolicitacaoExames.Fields.SEQP.toString()), ExamesLiberadosVO.Fields.SEQP.toString());
    	if (isOracle()) {
    		projList.add(Projections.sqlProjection("sysdate dataEvento", new String[] {ExamesLiberadosVO.Fields.DATA_EVENTO.toString()}, new Type[] {DateType.INSTANCE}));
    	} else {
    		projList.add(Projections.sqlProjection("current_date dataEvento", new String[] {ExamesLiberadosVO.Fields.DATA_EVENTO.toString()}, new Type[] {DateType.INSTANCE}));
    	}
    	criteria.setProjection(projList);
    	
    	criteria.setResultTransformer(Transformers.aliasToBean(ExamesLiberadosVO.class));
    	
    	return executeCriteria(criteria);
    }
    
    /**
     * #50701 - P2 parte2 - c_exames
     * @param pacCodigo
     * @return List<ExamesLiberadosVO>
     */
    public List<ExamesLiberadosVO> montarExamesLiberados02(Integer pacCodigo) {
    	
    	String projectionAmostra = "SUBSTR((case when man5_.SEQ = 7 then NULL"
            	   			.concat("			 when man5_.SEQ = 83 then NULL")
            	   			.concat("			 else man5_.DESCRICAO")
            	   			.concat("		end),1,15) amostra");
    	
    	DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISEX");
    	
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UFE", JoinType.INNER_JOIN);
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOEX", JoinType.INNER_JOIN);
    	criteria.createAlias("SOEX."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV", JoinType.INNER_JOIN);
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
    	criteria.createAlias("ISEX."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
    	
    	criteria.add(Restrictions.eq("ATV."+AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), pacCodigo));
    	criteria.add(Restrictions.sqlRestriction("SUBSTR(this_.sit_codigo,1,2) = ?", "LI", StringType.INSTANCE));
    	
    	ProjectionList projList = Projections.projectionList();
    	projList.add(Projections.property("EXA."+AelExames.Fields.DESCRICAO_USUAL.toString()), ExamesLiberadosVO.Fields.NOME_EXAME.toString());
    	projList.add(Projections.sqlProjection(projectionAmostra, new String[] {ExamesLiberadosVO.Fields.AMOSTRA.toString()}, new Type[] {StringType.INSTANCE}));
    	projList.add(Projections.property("UFE."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), ExamesLiberadosVO.Fields.LABORATORIO.toString());    	
    	if (isOracle()) {
    		projList.add(Projections.sqlProjection("sysdate dataEvento", new String[] {ExamesLiberadosVO.Fields.DATA_EVENTO.toString()}, new Type[] {DateType.INSTANCE}));
    	} else {
    		projList.add(Projections.sqlProjection("current_date dataEvento", new String[] {ExamesLiberadosVO.Fields.DATA_EVENTO.toString()}, new Type[] {DateType.INSTANCE}));
    	}
    	criteria.setProjection(projList);
    	
    	criteria.setResultTransformer(Transformers.aliasToBean(ExamesLiberadosVO.class));
    	
    	return executeCriteria(criteria);    	
    }
    
    /**
     * #50701 - P2 parte3 - c_exames
     * @param pacCodigo
     * @param sitLiberado
     * @param sitExecutando
     * @param sitAreaExecutora
     * @param tempoMinutosSolic
     * @return List<ExamesLiberadosVO>
     */
    public List<ExamesLiberadosVO> montarExamesLiberados03(Integer pacCodigo, String sitLiberado, String sitExecutando, String sitAreaExecutora, BigDecimal tempoMinutosSolic) {
    	
    	String projectionAmostra = "SUBSTR((case when man8_.SEQ = 7 then NULL"
				   			.concat("			 when man8_.SEQ = 83 then NULL")
				   			.concat("			 else man8_.DESCRICAO")
				   			.concat("		end),1,15)");
    	
    	DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
    	
    	criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE", JoinType.INNER_JOIN);
    	criteria.createAlias("SOE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
    	criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "EIS", JoinType.INNER_JOIN);
    	criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString(), "REE", JoinType.INNER_JOIN);
    	criteria.createAlias("REE."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL", JoinType.INNER_JOIN);
    	criteria.createAlias("PCL."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "CAL", JoinType.INNER_JOIN);
    	criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UFE", JoinType.INNER_JOIN);
    	criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
    	
    	criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_COD.toString(), pacCodigo));
    	criteria.add(Restrictions.sqlRestriction("SUBSTR(this_.sit_codigo,1,2) IN (? , ?)", new String[] {sitLiberado, sitExecutando}, new Type[] {StringType.INSTANCE, StringType.INSTANCE}));
    	criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), Boolean.FALSE));
    	criteria.add(Restrictions.isNotNull("REE."+AelResultadoExame.Fields.VALOR.toString()));
    	criteria.add(Restrictions.in("CAL."+AelCampoLaudo.Fields.TIPO_CAMPO.toString(), new DominioTipoCampoCampoLaudo[] {DominioTipoCampoCampoLaudo.N, DominioTipoCampoCampoLaudo.E}));
    	criteria.add(Restrictions.eq("CAL."+AelCampoLaudo.Fields.FLUXO.toString(), Boolean.TRUE));
    	
    	ProjectionList projList = Projections.projectionList();
    	projList.add(Projections.groupProperty("ISE."+AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), ExamesLiberadosVO.Fields.DATA_EXAME.toString());
    	projList.add(Projections.sqlGroupProjection("SUBSTR(cal6_.nome_sumario,1,25) nomeExame", "SUBSTR(cal6_.nome_sumario,1,25)", new String[] {ExamesLiberadosVO.Fields.NOME_EXAME.toString()}, new Type[] {StringType.INSTANCE}));
    	projList.add(Projections.sqlGroupProjection(projectionAmostra+" amostra", projectionAmostra, new String[] {ExamesLiberadosVO.Fields.AMOSTRA.toString()}, new Type[] {StringType.INSTANCE}));
    	projList.add(Projections.groupProperty("UFE."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), ExamesLiberadosVO.Fields.LABORATORIO.toString());
    	projList.add(Projections.groupProperty("CAL."+AelCampoLaudo.Fields.DIVIDE_POR_MIL.toString()), ExamesLiberadosVO.Fields.IND_DIVIDE_POR_MIL.toString());
    	projList.add(Projections.groupProperty("PCL."+AelParametroCamposLaudo.Fields.QUANTIDADE_CASAS_DECIMAIS.toString()), ExamesLiberadosVO.Fields.QTDE_CASAS_DECIMAIS.toString());
    	projList.add(Projections.groupProperty("REE."+AelResultadoExame.Fields.VALOR.toString()), ExamesLiberadosVO.Fields.VALOR.toString());
    	projList.add(Projections.max("EIS."+AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()), ExamesLiberadosVO.Fields.DATA_EVENTO.toString());
    	criteria.setProjection(projList);
    	
    	criteria.setResultTransformer(Transformers.aliasToBean(ExamesLiberadosVO.class));
    	
    	return executeCriteria(criteria);
    }
     
     public AelItemSolicitacaoExames obterItemSolicitacaoExamesComExame(final Integer soeSeq, final Short seqp) {
       	 final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

       	 criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), FetchMode.JOIN);
       	 criteria.setFetchMode(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), FetchMode.JOIN);

       	 criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
       	 criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

       	 return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
     }
     
     public AelItemSolicitacaoExames obterItemSolicitacaoExamesComAtendimento(Integer soeSeq, Short seqp) {
    	 final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ItemSolic");
    	 
    	 criteria.createAlias("ItemSolic." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "Exam", JoinType.INNER_JOIN);
    	 criteria.createAlias("ItemSolic." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "Solic", JoinType.INNER_JOIN);
    	 criteria.createAlias("Solic." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "Atend", JoinType.LEFT_OUTER_JOIN);
    	 criteria.createAlias("Atend." + AghAtendimentos.Fields.PACIENTE.toString(), "Paci", JoinType.INNER_JOIN);
       	 
       	 criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
       	 criteria.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

       	 return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
     }
     
 	public AelItemSolicitacaoExames obterDadoItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp) {
		if(soeSeq != null && seqp != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));	
			return (AelItemSolicitacaoExames) executeCriteriaUniqueResult(criteria);
		}
		return null;
	}
 	
 	public List<AelItemSolicitacaoExames> obterDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp) {
		if(soeSeq != null && seqp != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,"ise");
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
			criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));	
			return executeCriteria(criteria);
		}
		return null;
	}

}