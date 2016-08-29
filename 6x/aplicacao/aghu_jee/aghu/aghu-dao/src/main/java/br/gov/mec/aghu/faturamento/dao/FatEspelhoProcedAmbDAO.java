package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.ByteType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTipoFormularioDataSus;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoBpiVO;
import br.gov.mec.aghu.faturamento.vo.CursorEspelhoVO;
import br.gov.mec.aghu.faturamento.vo.ItensRealizadosIndividuaisVO;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatEspecGrupoAtendimento;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatGrupoAtendimento;
import br.gov.mec.aghu.model.FatItemGrupoAtend;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.core.utils.DateMaker;
import br.gov.mec.aghu.core.utils.StringUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class FatEspelhoProcedAmbDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatEspelhoProcedAmb> {

	private static final long serialVersionUID = -6566184135424136344L;

	private final String separador = ".";
	
	private DetachedCriteria criarCriteriaPorPmrSeq(final Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoProcedAmb.class);
		criteria.add(Restrictions.eq(FatEspelhoProcedAmb.Fields.PMR_SEQ.toString(), seq));
		return criteria;
	}

	public List<FatEspelhoProcedAmb> buscarPorPmrSeq(Long seq) {
		return executeCriteria(criarCriteriaPorPmrSeq(seq));
	}

	/**
	 * Remove registro do FatEspelhoProcedAmb
	 * 
	 * Usado HQL para melhorar desempenho pq pode ter grande nro de registro, é
	 * uma tabela temporária para encerramento do faturamento do ambulatório,
	 * não possui triggers
	 * 
	 * @param cpeDtHrInicio
	 * @param ano
	 * @param mes
	 * @param modulo
	 * @param tipoFormulario
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void removeEspelhoProcedAmb(Date cpeDtHrInicio, Integer ano, Integer mes, DominioModuloCompetencia modulo,
			DominioTipoFormularioDataSus tipoFormulario) {
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();

		hql.append("delete from ");
		hql.append(FatEspelhoProcedAmb.class.getName());
		hql.append(" where 1 = 1");

		if (cpeDtHrInicio != null) {
			hql.append(" and ");
			hql.append(FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString()).append(" = :cpeDtHrInicio");
		}
		if (ano != null) {
			hql.append(" and ");
			hql.append(FatEspelhoProcedAmb.Fields.CPE_ANO.toString()).append(" = :ano");
		}
		if (mes != null) {
			hql.append(" and ");
			hql.append(FatEspelhoProcedAmb.Fields.CPE_MES.toString()).append(" = :mes");
		}
		if (modulo != null) {
			hql.append(" and ");
			hql.append(FatEspelhoProcedAmb.Fields.CPE_MODULO.toString()).append(" = :modulo");
		}
		if (tipoFormulario != null) {
			hql.append(" and ");
			hql.append(FatEspelhoProcedAmb.Fields.TIPO_FORMULARIO.toString()).append(" = :tipoFormulario");
		}

		query = createHibernateQuery(hql.toString());
		if (cpeDtHrInicio != null){
			query.setParameter("cpeDtHrInicio", cpeDtHrInicio);
		}
		if (ano != null){
			query.setParameter("ano", ano);
		}
		if (mes != null){
			query.setParameter("mes", mes);
		}
		if (modulo != null){
			query.setParameter("modulo", modulo);
		}
		if (tipoFormulario != null){
			query.setParameter("tipoFormulario", tipoFormulario);
		}
		query.executeUpdate();
	}

	/**
	 * Implementa cursor <code>c_espelho</code>
	 * 
	 * @param previa
	 * @param indConsistente
	 * @param cpeDtHrInicio
	 * @param ano
	 * @param mes
	 * @param modulo
	 * @param tipoFormulario
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<CursorEspelhoVO> listarEspelho(boolean previa, Boolean indConsistente, Date cpeDtHrInicio, Integer ano, Integer mes,
			DominioModuloCompetencia modulo, DominioTipoFormularioDataSus tipoFormulario) {

		List<CursorEspelhoVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		DominioSituacaoProcedimentoAmbulatorio[] indSituacao = new DominioSituacaoProcedimentoAmbulatorio[2];
		if (previa) {
			indSituacao[0] = DominioSituacaoProcedimentoAmbulatorio.ABERTO;
			indSituacao[1] = DominioSituacaoProcedimentoAmbulatorio.ABERTO;
		} else {
			indSituacao[0] = DominioSituacaoProcedimentoAmbulatorio.ENCERRADO;
			indSituacao[1] = DominioSituacaoProcedimentoAmbulatorio.APRESENTADO;
		}

		hql = new StringBuffer();
		hql.append(" select ");
		hql.append("  epa.").append(FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()).append(" as procedimentoHosp");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.COD_ATV_PROF.toString()).append(" as codAtvProf");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.IDADE.toString()).append(" as idade");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.COMPETENCIA.toString()).append(" as competencia");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.IPH_PHO_SEQ.toString()).append(" as iphPhoSeq");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.IPH_SEQ.toString()).append(" as iphSeq");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.FCC_SEQ.toString()).append(" as fccSeq");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.FCF_SEQ.toString()).append(" as fcfSeq");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.ORIGEM_INF.toString()).append(" as origemInf");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.SERVICO.toString()).append(" as servico ");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.CLASSIFICACAO.toString()).append(" as classificacao ");
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.UNF_SEQ.toString()).append(" as unidadeFuncional ");
		
		hql.append(", sum(epa.").append(FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()).append(") as quantidade");
		hql.append(", sum(epa.").append(FatEspelhoProcedAmb.Fields.VLR_ANESTES.toString()).append(") as vlrAnestes");
		hql.append(", sum(epa.").append(FatEspelhoProcedAmb.Fields.VLR_PROC.toString()).append(") as vlrProc");
		hql.append(", sum(epa.").append(FatEspelhoProcedAmb.Fields.VLR_SADT.toString()).append(") as vlrSadt");
		hql.append(", sum(epa.").append(FatEspelhoProcedAmb.Fields.VLR_SERV_HOSP.toString()).append(") as vlrServHosp");
		hql.append(", sum(epa.").append(FatEspelhoProcedAmb.Fields.VLR_SERV_PROF.toString()).append(") as vlrServProf");

		// from
		hql.append(" from ");
		hql.append(FatEspelhoProcedAmb.class.getName()).append(" as epa");
		hql.append(", ").append(FatProcedAmbRealizado.class.getName()).append(" as pmr ");

		// where
		hql.append(" where ")
		.append(" epa.").append(FatEspelhoProcedAmb.Fields.PMR_SEQ.toString())
				.append(" = pmr.").append(FatProcedAmbRealizado.Fields.SEQ.toString());

		hql.append(" and (");
		hql.append(" (:previa = 'N'");
		hql.append(" and epa. ").append(FatEspelhoProcedAmb.Fields.DATA_PREVIA.toString()).append(" is null)");
		hql.append(" or ");
		hql.append(" (:previa = 'S'");
		hql.append(" and epa. ").append(FatEspelhoProcedAmb.Fields.DATA_PREVIA.toString()).append(" is not null)");
		hql.append(')');

		hql.append(" and pmr.").append(FatProcedAmbRealizado.Fields.SITUACAO.toString()).append(" in (:indSituacao)");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.toString()).append(" = :indConsistente");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString()).append(" = :cpeDtHrInicio");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.CPE_ANO.toString()).append(" = :ano");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.CPE_MES.toString()).append(" = :mes");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.CPE_MODULO.toString()).append(" = :modulo");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.TIPO_FORMULARIO.toString()).append(" = :tipoFormulario");
		hql.append(" and epa.").append(FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()).append(" <> 1");

		// group by
		hql.append(" group by");
		hql.append("  epa.").append(FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.COD_ATV_PROF.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.IDADE.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.COMPETENCIA.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.IPH_PHO_SEQ.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.IPH_SEQ.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.FCC_SEQ.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.FCF_SEQ.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.ORIGEM_INF.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.SERVICO.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.CLASSIFICACAO.toString());
		hql.append(", epa.").append(FatEspelhoProcedAmb.Fields.UNF_SEQ.toString());

		// query
		query = createHibernateQuery(hql.toString());
		query.setParameter("previa", previa ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
		query.setParameterList("indSituacao", indSituacao);
		query.setParameter("indConsistente", indConsistente);
		query.setParameter("cpeDtHrInicio", cpeDtHrInicio);
		query.setParameter("ano", ano);
		query.setParameter("mes", mes);
		query.setParameter("modulo", modulo);
		query.setParameter("tipoFormulario", tipoFormulario.toString());

		query.setResultTransformer(Transformers.aliasToBean(CursorEspelhoVO.class));
		result = query.list();

		return result;
	}

	/**
	 * Implementa cursor <code>c_itens_esp</code>
	 * 
	 * @param cpeDtHrInicio
	 * @param ano
	 * @param mes
	 * @param modulo
	 * @return
	 */
	public List<Long> obterPmrSeq(Date cpeDtHrInicio, Integer ano, Integer mes, DominioModuloCompetencia modulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoProcedAmb.class);

		ProjectionList projLst = Projections.projectionList();
		projLst.add(Projections.property(FatEspelhoProcedAmb.Fields.PMR_SEQ.toString()));
		criteria.setProjection(projLst);

		criteria.add(Restrictions.isNull(FatEspelhoProcedAmb.Fields.DATA_PREVIA.toString()));
		criteria.add(Restrictions.eq(FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString(), cpeDtHrInicio));
		criteria.add(Restrictions.eq(FatEspelhoProcedAmb.Fields.CPE_ANO.toString(), ano));
		criteria.add(Restrictions.eq(FatEspelhoProcedAmb.Fields.CPE_MES.toString(), mes));
		criteria.add(Restrictions.eq(FatEspelhoProcedAmb.Fields.CPE_MODULO.toString(), modulo));

		return executeCriteria(criteria);

	}

	/**
	 * Implementa cursor <code>c_espelho_bpi</code>
	 * 
	 * @param previa
	 * @param cpeDtHrInicio
	 * @param ano
	 * @param mes
	 * @param modulo
	 * @param tipoFormulario
	 * @param procedimentoHosp
	 * @return
	 */
	public List<CursorEspelhoBpiVO> listarEspelhoBpi(final boolean previa, final Calendar cpeDtHrInicio, final Integer ano, final Integer mes,
			final DominioModuloCompetencia modulo, final DominioTipoFormularioDataSus tipoFormulario) {

		final StringBuilder sql = new StringBuilder(2000);
		final boolean isOracle = isOracle();

		final String cnscbo = isOracle ? " LPAD(NVL(EPA.CNS_MEDICO,0),15,'0') || LPAD(NVL(EPA.COD_ATV_PROF,0),6,'0') "
	               				  	   : " LPAD(case when EPA.CNS_MEDICO is null then '0' else CAST(EPA.CNS_MEDICO AS TEXT) end,15,'0') " +
	               				  	   		"    ||LPAD(case when EPA.COD_ATV_PROF is null then '0' else EPA.COD_ATV_PROF end,6,'0')"; 
		sql.append("SELECT ")
		   .append( cnscbo ).append(" as cnscbo")
		   .append(", EPA.PROCEDIMENTO_HOSP as procedimentohosp")
		   .append(", EPA.COD_ATV_PROF as codatvprof")
		   .append(", EPA.CNS_MEDICO as cnsmedico")
		   .append(", EPA.DATA_ATENDIMENTO as dataatendimento")
		   .append(", EPA.CNS_PACIENTE as cnspaciente")
		   .append(", EPA.NOME_PACIENTE as nomepaciente")
		   .append(", EPA.DT_NASCIMENTO as dtnascimento")
		   .append(", EPA.RACA as raca")
		   .append(", EPA.NACIONALIDADE as nacionalidade") // -- Marina 24/01/2011
		   .append(", EPA.CARATER_ATENDIMENTO as carateratendimento")
		   .append(", EPA.NRO_AUTORIZACAO as nroautorizacao")
		   .append(", EPA.SEXO as sexo")
		   .append(", EPA.COD_IBGE as codibge")
		   .append(", EPA.CID10 as cid10")
		   .append(", EPA.IDADE as idade")
		   .append(", EPA.COMPETENCIA as competencia")
		   .append(", EPA.IPH_PHO_SEQ as iphphoseq")
		   .append(", EPA.IPH_SEQ as iphseq")
		   .append(", EPA.FCC_SEQ as fccseq")
		   .append(", EPA.FCF_SEQ as fcfseq")
		   .append(", EPA.ORIGEM_INF as origeminf")
		   .append(", SUM(EPA.QUANTIDADE) as quantidadesumarizada")
		   .append(", SUM(EPA.VLR_ANESTES) as vlranestes")
		   .append(", SUM(EPA.VLR_PROC) as vlrproc")
		   .append(", SUM(EPA.VLR_SADT) as vlrsadt")
		   .append(", SUM(EPA.VLR_SERV_HOSP) as vlrservhosp")
		   .append(", SUM(EPA.VLR_SERV_PROF) as vlrservprof")
		   .append(", PMR.PAC_CODIGO as pacCodigo ") // Marina 29/05/2013
		   // Marina 27/05/2013
		   // Ney 01/09/2012
		   .append(",EPA.SERVICO as servico ")
		   .append(",EPA.CLASSIFICACAO as classificacao ")
		   
		   // eSchweigert: 
		   // chamada a: FATC_BUSCA_serv_class (NULL ,epa.IPH_SEQ ,epa.IPH_PHO_SEQ ,Epa.Unidade_Funcional) 
		   // será executada via JAVA
		   .append(",EPA.UNIDADE_FUNCIONAL as unidadefuncional");
		
		sql.append(" FROM agh.fat_espelhos_proced_amb epa")
		   .append(", agh.fat_proced_amb_realizados pmr");
		
		sql.append(" WHERE EPA.PMR_SEQ          = pmr.seq")
		
		   .append(" AND ( (:previa = 'N' AND EPA.data_previa IS NULL) ")
		   .append(" 		OR (:previa = 'S' AND EPA.data_previa IS NOT NULL) ) ")
		   
		   .append(" AND PMR.IND_SITUACAO IN ( case when :previa = 'S' then 'A' else 'E' end, case when :previa = 'S' then 'A' else 'P' end) ")
		   
		   .append(" AND EPA.IND_CONSISTENTE = 'S' ")
		   .append(" AND EPA.CPE_ANO          = :ano")
		   .append(" AND EPA.CPE_MES          = :mes")
		   .append(" AND EPA.CPE_MODULO       = :modulo") // AMB
		   .append(" AND EPA.TIPO_FORMULARIO  = :tipoFormulario") // I
		   .append(" AND EPA.PROCEDIMENTO_HOSP <> 1"); // 1
		   
		sql.append( isOracle ? " AND EPA.CPE_DT_HR_INICIO = to_date(:cpeDtHrInicio, 'ddmmyyyyhh24miss')" 
							 : " AND EPA.CPE_DT_HR_INICIO = to_timestamp(:cpeDtHrInicio, 'ddmmyyyyhh24miss')");
		
		
		sql.append(" GROUP BY ")
		   .append( 	cnscbo )
		   .append(",  EPA.PROCEDIMENTO_HOSP")
		   .append(", EPA.COD_ATV_PROF")
		   .append(", EPA.CNS_MEDICO")
		   .append(", EPA.DATA_ATENDIMENTO")
		   .append(", EPA.CNS_PACIENTE")
		   .append(", EPA.NOME_PACIENTE")
		   .append(", EPA.DT_NASCIMENTO")
		   .append(", EPA.RACA")
		   .append(", EPA.NACIONALIDADE") // -- Marina 24/01/2011
		   .append(", EPA.CARATER_ATENDIMENTO")
		   .append(", EPA.NRO_AUTORIZACAO")
		   .append(", EPA.SEXO")
		   .append(", EPA.COD_IBGE")
		   .append(", EPA.CID10")
		   .append(", EPA.IDADE")
		   .append(", EPA.COMPETENCIA")
		   .append(", EPA.IPH_PHO_SEQ")
		   .append(", EPA.IPH_SEQ")
		   .append(", EPA.FCC_SEQ")
		   .append(", EPA.FCF_SEQ")
		   .append(", EPA.ORIGEM_INF")
		   .append(", PMR.PAC_CODIGO") //  Marina 29/05/2013
		   .append(", EPA.SERVICO ")
		   .append(", EPA.CLASSIFICACAO ")
		   .append(", EPA.UNIDADE_FUNCIONAL ");		
		
		SQLQuery q = createSQLQuery(sql.toString());
		q.setString("previa", previa ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
		q.setString("cpeDtHrInicio", getData(cpeDtHrInicio));
		q.setInteger("ano", ano);
		q.setInteger("mes", mes);
		q.setString("modulo", modulo.toString());
		q.setString("tipoFormulario", tipoFormulario.toString());
		// q.setParameterList("procedimentoHosp", procedimentoHosp);

		List<CursorEspelhoBpiVO> listaVO = q.addScalar("cnscbo", StringType.INSTANCE).addScalar("procedimentohosp", LongType.INSTANCE)
				.addScalar("codatvprof", StringType.INSTANCE).addScalar("cnsmedico", LongType.INSTANCE).addScalar("dataatendimento", DateType.INSTANCE)
				.addScalar("cnspaciente", LongType.INSTANCE).addScalar("nomepaciente", StringType.INSTANCE).addScalar("dtnascimento", DateType.INSTANCE)
				.addScalar("raca", ByteType.INSTANCE).addScalar("nacionalidade", IntegerType.INSTANCE).addScalar("carateratendimento", ByteType.INSTANCE)
				.addScalar("nroautorizacao", LongType.INSTANCE).addScalar("sexo", StringType.INSTANCE).addScalar("codibge", IntegerType.INSTANCE)
				.addScalar("cid10", StringType.INSTANCE).addScalar("idade", ShortType.INSTANCE).addScalar("competencia", IntegerType.INSTANCE)
				.addScalar("iphphoseq", ShortType.INSTANCE).addScalar("iphseq", IntegerType.INSTANCE).addScalar("fccseq", IntegerType.INSTANCE)
				.addScalar("fcfseq", IntegerType.INSTANCE).addScalar("origeminf", StringType.INSTANCE).addScalar("quantidadesumarizada", IntegerType.INSTANCE)
				.addScalar("vlranestes", DoubleType.INSTANCE).addScalar("vlrproc", DoubleType.INSTANCE).addScalar("vlrsadt", DoubleType.INSTANCE)
				.addScalar("vlrservhosp", DoubleType.INSTANCE).addScalar("vlrservprof", DoubleType.INSTANCE)
				.addScalar("servico",StringType.INSTANCE).addScalar("classificacao", StringType.INSTANCE).addScalar("unidadefuncional", ShortType.INSTANCE).addScalar("pacCodigo", IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(CursorEspelhoBpiVO.class)).list();

		return listaVO;
	}
	
	private String getData(final Calendar c){
		//'ddmmyyyyhh24miss'
		return StringUtil.adicionaZerosAEsquerda(c.get(Calendar.DAY_OF_MONTH), 2) + StringUtil.adicionaZerosAEsquerda(c.get(Calendar.MONTH) +1, 2) + StringUtil.adicionaZerosAEsquerda(c.get(Calendar.YEAR), 4) + StringUtil.adicionaZerosAEsquerda(c.get(Calendar.HOUR_OF_DAY),2) + StringUtil.adicionaZerosAEsquerda(c.get(Calendar.MINUTE), 2) + StringUtil.adicionaZerosAEsquerda(c.get(Calendar.SECOND), 2);
	}

	public Byte obterGrupoAtendimento(final Integer iphSeq, final Short iphPhoSeq, final Short ctcCnvCodigo, final Short espSeq) {
		final Byte retorno = obterGrupoAtendimento1(iphSeq, iphPhoSeq, ctcCnvCodigo, espSeq);
		if (retorno == null) {
			return obterGrupoAtendimento2(iphSeq, iphPhoSeq, ctcCnvCodigo);
		}
		return retorno;
	}

	private Byte obterGrupoAtendimento1(final Integer iphSeq, final Short iphPhoSeq, final Short ctcCnvCodigo, final Short espSeq) {
		/*
		 * CURSOR c_grupo_atendimento ( p_cnv_codigo IN NUMBER, p_esp_seq IN
		 * NUMBER, p_iph_pho_seq IN NUMBER, p_iph_seq IN NUMBER ) IS SELECT '1',
		 * gra.codigo_sus FROM fat_itens_grupos_atend iga,
		 * fat_espec_grupos_atendimento gte, fat_grupos_atendimentos gra WHERE
		 * iga.gra_cnv_codigo = gra.cnv_codigo AND iga.gra_seqp = gra.seqp AND
		 * gte.gra_cnv_codigo = gra.cnv_codigo AND gte.gra_seqp = gra.seqp AND
		 * gte.esp_seq = p_esp_seq AND iga.gra_cnv_codigo = p_cnv_codigo AND
		 * iga.iph_pho_seq = p_iph_pho_seq AND iga.iph_seq = p_iph_seq AND
		 * gra.ind_situacao_registro = 'A' UNION ...
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(FatGrupoAtendimento.class, "gra");
		criteria.createAlias(FatGrupoAtendimento.Fields.FAT_ESPEC_GRUPO_ATENDIMENTOS.toString(), "gte");
		criteria.createAlias(FatGrupoAtendimento.Fields.FAT_ITEM_GRUPO_ATENDS.toString(), "iga");
		criteria.setProjection(Projections.property("gra."+FatGrupoAtendimento.Fields.CODIGO_SUS.toString()));
		criteria.add(Restrictions.eq("gte." + FatEspecGrupoAtendimento.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("iga." + FatItemGrupoAtend.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("iga." + FatItemGrupoAtend.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("iga." + FatItemGrupoAtend.Fields.GRA_CNV_CODIGO.toString(), ctcCnvCodigo));
		criteria.add(Restrictions.eq("gra." + FatGrupoAtendimento.Fields.IND_SITUACAO_REGISTRO.toString(), DominioSituacao.A));
		List<Object> result = executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return (Byte) result.get(0);
	}

	private Byte obterGrupoAtendimento2(final Integer iphSeq, final Short iphPhoSeq, final Short ctcCnvCodigo) {
		/*
		 * ... UNION SELECT '2', gra.codigo_sus FROM fat_itens_grupos_atend iga,
		 * fat_grupos_atendimentos gra WHERE iga.gra_cnv_codigo = gra.cnv_codigo
		 * AND iga.gra_seqp = gra.seqp AND iga.gra_cnv_codigo = p_cnv_codigo AND
		 * iga.iph_pho_seq = p_iph_pho_seq AND iga.iph_seq = p_iph_seq AND
		 * gra.ind_situacao_registro = 'A' ORDER BY 1;
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(FatGrupoAtendimento.class, "gra");
		criteria.createAlias(FatGrupoAtendimento.Fields.FAT_ITEM_GRUPO_ATENDS.toString(), "iga");
		criteria.setProjection(Projections.property("gra."+FatGrupoAtendimento.Fields.CODIGO_SUS.toString()));
		criteria.add(Restrictions.eq("iga." + FatItemGrupoAtend.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("iga." + FatItemGrupoAtend.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("iga." + FatItemGrupoAtend.Fields.GRA_CNV_CODIGO.toString(), ctcCnvCodigo));
		criteria.add(Restrictions.eq("gra." + FatGrupoAtendimento.Fields.IND_SITUACAO_REGISTRO.toString(), DominioSituacao.A));
		List<Object> result = executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return (Byte) result.get(0);
	}
	
	public List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuais(Date cpeDtHrInicio, Integer ano, Integer mes, Long procedInicial, Long procedFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoProcedAmb.class, "EPA");
		criteria.createAlias(FatEspelhoProcedAmb.Fields.ITENS_PROCED_HOSPITALAR.toString(), "IPH");
		criteria.createAlias(FatEspelhoProcedAmb.Fields.PROCED_AMB_REALIZADO.toString(), "PMR");
		criteria.createAlias("PMR."+FatProcedAmbRealizado.Fields.PACIENTE.toString(), "PAC", Criteria.LEFT_JOIN);
		criteria.createAlias("PAC."+AipPacientes.Fields.ENDERECOS.toString(), "END", Criteria.LEFT_JOIN);
		criteria.createAlias("END."+AipEnderecosPacientes.Fields.CIDADE.toString(), "CID", Criteria.LEFT_JOIN);
		criteria.createAlias("CID."+AipCidades.Fields.UF.toString(), "UF", Criteria.LEFT_JOIN);
		criteria.createAlias("END."+AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString(), "LGD", Criteria.LEFT_JOIN);
		criteria.createAlias("LGD."+AipLogradouros.Fields.CIDADE.toString(), "CID1", Criteria.LEFT_JOIN);
		criteria.createAlias("CID1."+AipCidades.Fields.UF.toString(), "UF1", Criteria.LEFT_JOIN);
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC");
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO.toString(), "FOG");
		criteria.createAlias("FOG."+FatFormaOrganizacao.Fields.FAT_SUB_GRUPO.toString(), "SGR");
		criteria.createAlias("SGR."+FatSubGrupo.Fields.FAT_GRUPO.toString(), "GRP");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.ID_GRP_SEQ.toString()), "grupoSeq")
				.add(Projections.groupProperty("GRP."+FatGrupo.Fields.DESCRICAO.toString()), "grupo")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.ID_SUB_GRUPO.toString()), "subGrupoSeq")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.DESCRICAO.toString()), "subGrupo")
				.add(Projections.groupProperty("FOG."+FatFormaOrganizacao.Fields.ID_CODIGO.toString()), "formaOrganizacaoCodigo")
				.add(Projections.groupProperty("FOG."+FatFormaOrganizacao.Fields.DESCRICAO.toString()), "formaOrganizacao")
				.add(Projections.groupProperty("EPA."+FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()), "procedimentoHospitalarCod")
				.add(Projections.groupProperty("IPH."+FatItensProcedHospitalar.Fields.DESCRICAO.toString()), "procedimentoHospitalarDesc")
				.add(Projections.sqlGroupProjection(
					"TO_CHAR(pmr2_.DTHR_REALIZADO,'DD/MM/YYYY') as dthrRealz",
					"TO_CHAR(pmr2_.DTHR_REALIZADO,'DD/MM/YYYY')",
				    new String[]{"dthrRealz"}, 
				    new Type[] {StringType.INSTANCE}), "dthrRealz")
				.add(Projections.groupProperty("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.groupProperty("PAC."+AipPacientes.Fields.CODIGO.toString()), "codigo")
				.add(Projections.groupProperty("PAC."+AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.groupProperty("CID."+AipCidades.Fields.NOME.toString()), "cidade")
				.add(Projections.groupProperty("UF."+AipUfs.Fields.SIGLA.toString()), "estado")
				.add(Projections.groupProperty("CID1."+AipCidades.Fields.NOME.toString()), "cidadeLgd")
				.add(Projections.groupProperty("UF1."+AipUfs.Fields.SIGLA.toString()), "estadoLgd")
				.add(Projections.sqlProjection("SUM(ipc10_.VLR_PROCEDIMENTO*this_.QUANTIDADE) as valorProcedimento",  new String[]{"valorProcedimento"}, new Type[] {BigDecimalType.INSTANCE}), "valorProcedimento")
				.add(Projections.sum("EPA."+FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()), "quantidade")
		);

		if(procedInicial != null || procedFinal != null) {
			criteria.add(Restrictions.between("EPA."+FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString(), procedInicial!=null?Long.valueOf(procedInicial):0l, procedFinal!=null?Long.valueOf(procedFinal):9999999999l));	
		}
		
		StringBuffer sql = new StringBuffer(500);
		if(isOracle()) {
			sql.append("( end4_.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where ");
			sql.append("(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac3_.CODIGO and rownum = 1)");
			sql.append("or (rownum = 1 and (select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac3_.CODIGO) = 0))");
			sql.append("and endx_.pac_codigo = pac3_.CODIGO");
			sql.append(") or end4_.seqp is null )");
		}
		else {
			sql.append("( end4_.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where ");
			sql.append("(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac3_.CODIGO limit 1)");
			sql.append("or ((select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac3_.CODIGO) = 0))");
			sql.append("and endx_.pac_codigo = pac3_.CODIGO ");
			sql.append("limit 1) or end4_.seqp is null )");
		}
		/*
and (



end4_.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where 
(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac3_.CODIGO and rownum = 1)
or (rownum = 1 and (select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac3_.CODIGO) = 0))
and endx_.pac_codigo = pac3_.CODIGO
) or end4_.seqp is null)
		 */

		criteria.add(Restrictions.sqlRestriction(sql.toString()));
		criteria.add(Restrictions.le("IPC."+FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), DateMaker.obterData(ano, mes, 1, 0, 0)));
		criteria.add(Restrictions.or(Restrictions.ge("IPC."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), DateMaker.obterData(ano, mes, 1, 0, 0)), Restrictions.isNull("IPC."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString())));
		criteria.add(Restrictions.eq("EPA."+FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.toString(), true));
		criteria.add(Restrictions.eq("EPA."+FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString(), cpeDtHrInicio));
		criteria.add(Restrictions.eq("EPA."+FatEspelhoProcedAmb.Fields.CPE_ANO.toString(), ano));
		criteria.add(Restrictions.eq("EPA."+FatEspelhoProcedAmb.Fields.CPE_MES.toString(), mes));
		criteria.add(Restrictions.eq("EPA."+FatEspelhoProcedAmb.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.AMB));

		criteria.setResultTransformer(Transformers.aliasToBean(ItensRealizadosIndividuaisVO.class));
		
		return executeCriteria(criteria);

	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssMethodCount"})
	public List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuaisEAPAC(final Date cpeDtHrInicio, final Integer ano, final Integer mes, final Long procedInicial, final Long procedFinal) {

		final StringBuffer sql = new StringBuffer(620);
		if (isOracle()) {
			// QUERY NATIVA - ORACLE
			sql.append("SELECT ");
			sql.append(" SGR.GRP_SEQ as grupoSeq");
			sql.append(", GRP.DESCRICAO as grupo");
			sql.append(", SGR.SUB_GRUPO as subGrupoSeq");
			sql.append(", SGR.DESCRICAO as subGrupo");
			sql.append(", FOG.CODIGO as formaOrganizacaoCodigo");
			sql.append(", FOG.DESCRICAO as formaOrganizacao");
			sql.append(", EPA.PROCEDIMENTO_HOSP as procedimentoHospitalarCod");
			sql.append(", IPH.DESCRICAO as procedimentoHospitalarDesc");
			sql.append(", TO_CHAR(PMR.DTHR_REALIZADO,'DD/MM/YYYY') as dthrRealz");
			sql.append(", NULL as apac");
			sql.append(", PAC.PRONTUARIO as prontuario");
			sql.append(", PMR.PAC_CODIGO as codigo");
			sql.append(", PAC.NOME as nome");
			sql.append(", CID.NOME as cidade");
			sql.append(", UF.NOME as estado");
			sql.append(", CID1.NOME as cidadeLgd");
			sql.append(", UF1.NOME as estadoLgd");
			sql.append(", SUM(IPC.VLR_PROCEDIMENTO	* NVL(EPA.QUANTIDADE,0)) as valorProcedimento");
			sql.append(", SUM(NVL(EPA.QUANTIDADE,0)) as quantidade");

			sql.append(" FROM " + FatGrupo.class.getAnnotation(Table.class).schema()  + separador + FatGrupo.class.getAnnotation(Table.class).name() + " GRP");
			sql.append(", " + FatSubGrupo.class.getAnnotation(Table.class).schema()  + separador + FatSubGrupo.class.getAnnotation(Table.class).name() + " SGR");
			sql.append(", " + FatFormaOrganizacao.class.getAnnotation(Table.class).schema()  + separador + FatFormaOrganizacao.class.getAnnotation(Table.class).name() + " FOG");
			sql.append(", HIST.FAT_ESPELHOS_PROCED_AMB EPA"); //NAO HÁ POJO PRA TABELA NESSE SCHEMA
			sql.append(", " + FatItensProcedHospitalar.class.getAnnotation(Table.class).schema()  + separador + FatItensProcedHospitalar.class.getAnnotation(Table.class).name() + " IPH");
			sql.append(", HIST.FAT_PROCED_AMB_REALIZADOS PMR"); //NAO HÁ POJO PRA TABELA NESSE SCHEMA
			sql.append(", " + FatVlrItemProcedHospComps.class.getAnnotation(Table.class).schema()  + separador + FatVlrItemProcedHospComps.class.getAnnotation(Table.class).name() + " IPC");
			sql.append(", " + AipPacientes.class.getAnnotation(Table.class).schema()  + separador + AipPacientes.class.getAnnotation(Table.class).name() + " PAC");
			sql.append(", " + AipEnderecosPacientes.class.getAnnotation(Table.class).schema()  + separador + AipEnderecosPacientes.class.getAnnotation(Table.class).name() + " END");
			sql.append(", " + AipCidades.class.getAnnotation(Table.class).schema()  + separador + AipCidades.class.getAnnotation(Table.class).name() + " CID");
			sql.append(", " + AipUfs.class.getAnnotation(Table.class).schema()  + separador + AipUfs.class.getAnnotation(Table.class).name() + " UF");
			sql.append(", " + AipLogradouros.class.getAnnotation(Table.class).schema()  + separador + AipLogradouros.class.getAnnotation(Table.class).name() + " LGD");
			sql.append(", " + AipCidades.class.getAnnotation(Table.class).schema()  + separador + AipCidades.class.getAnnotation(Table.class).name() + " CID1");
			sql.append(", " + AipUfs.class.getAnnotation(Table.class).schema()  + separador + AipUfs.class.getAnnotation(Table.class).name() + " UF1");
			
			sql.append(" WHERE EPA.CPE_MES = :mes");
			sql.append(" AND EPA.CPE_ANO          = :ano");
			sql.append(" AND EPA.CPE_MODULO       = :modulo"); // AMB
			sql.append(" AND EPA.IND_CONSISTENTE  = 'S'");
			sql.append(" AND EPA.CPE_DT_HR_INICIO = :cpeDtHrInicio");
			sql.append(" AND EPA.PROCEDIMENTO_HOSP BETWEEN NVL(:procedIni,0) AND NVL(:procedFim,9999999999)");
			sql.append(" AND IPH.PHO_SEQ          = EPA.IPH_PHO_SEQ");
			sql.append(" AND IPH.SEQ          = EPA.IPH_SEQ");
			sql.append(" AND PMR.SEQ          = EPA.PMR_SEQ");
			sql.append(" AND PAC.CODIGO(+)          = PMR.PAC_CODIGO");
			sql.append(" AND IPC.IPH_PHO_SEQ          = IPH.PHO_SEQ");
			sql.append(" AND IPC.IPH_SEQ          = IPH.SEQ");
			sql.append(" AND TO_CHAR(IPC.DT_INICIO_COMPETENCIA,'YYYYMMDD') <= to_char(:ano)||lpad(to_char(:mes),2,'0')||'01'");
			sql.append(" AND ( (TO_CHAR(IPC.DT_FIM_COMPETENCIA,'YYYYMMDD') >= to_char(:ano)||lpad(to_char(:mes),2,'0')||'01')");
			sql.append(" OR (IPC.DT_FIM_COMPETENCIA IS NULL) )");
			sql.append(" AND FOG.SGR_GRP_SEQ = IPH.FOG_SGR_GRP_SEQ");
			sql.append(" AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO");
			sql.append(" AND FOG.CODIGO = IPH.FOG_CODIGO");
			sql.append(" AND SGR.GRP_SEQ = FOG.SGR_GRP_SEQ");
			sql.append(" AND SGR.SUB_GRUPO = FOG.SGR_SUB_GRUPO");
			sql.append(" AND GRP.SEQ = SGR.GRP_SEQ");
			sql.append(" AND END.PAC_CODIGO(+) = PAC.CODIGO");
			sql.append(" AND CID.CODIGO(+) = END.CDD_CODIGO");
			sql.append(" AND UF.SIGLA(+) = CID.UF_SIGLA");
			sql.append(" AND LGD.CODIGO(+) = END.BCL_CLO_LGR_CODIGO");
			sql.append(" AND CID1.CODIGO(+) = LGD.CDD_CODIGO");
			sql.append(" AND UF1.SIGLA(+) = CID1.UF_SIGLA");
			sql.append(" AND ( END.seqp = (select ENDX.seqp from AGH.AIP_ENDERECOS_PACIENTES ENDX where");
			sql.append(" (seqp = (select ENDX1.seqp from AGH.AIP_ENDERECOS_PACIENTES ENDX1 where ENDX1.ind_padrao = 'S' and ENDX1.pac_codigo = PAC.CODIGO and rownum = 1)");
			sql.append(" or (rownum = 1 and (select count(*) from AGH.AIP_ENDERECOS_PACIENTES ENDX1 where ENDX1.ind_padrao = 'S' and ENDX1.pac_codigo = PAC.CODIGO) = 0))");
			sql.append(" and ENDX.pac_codigo = PAC.CODIGO");
			sql.append(" ) or END.seqp is null )");

			
			
			sql.append(" GROUP BY SGR.GRP_SEQ");
			sql.append(", GRP.DESCRICAO");
			sql.append(", SGR.SUB_GRUPO");
			sql.append(", SGR.DESCRICAO ");
			sql.append(", FOG.CODIGO");
			sql.append(", FOG.DESCRICAO");
			sql.append(", EPA.PROCEDIMENTO_HOSP");
			sql.append(", IPH.DESCRICAO");
			sql.append(", TO_CHAR(PMR.DTHR_REALIZADO,'DD/MM/YYYY')");
			sql.append(", PAC.PRONTUARIO");
			sql.append(", PMR.PAC_CODIGO");
			sql.append(", PAC.NOME");
			sql.append(", CID.NOME");
			sql.append(", UF.NOME");
			sql.append(", CID1.NOME");
			sql.append(", UF1.NOME");

			sql.append(" UNION ");
			
			sql.append("SELECT ");
			sql.append(" SGR.GRP_SEQ as grupoSeq");
			sql.append(", GRP.DESCRICAO as grupo");
			sql.append(", SGR.SUB_GRUPO as subGrupoSeq");
			sql.append(", SGR.DESCRICAO as subGrupo");
			sql.append(", FOG.CODIGO as formaOrganizacaoCodigo");
			sql.append(", FOG.DESCRICAO as formaOrganizacao");
			sql.append(", IEC.PROCEDIMENTO_HOSP as procedimentoHospitalarCod");
			sql.append(", IPH.DESCRICAO as procedimentoHospitalarDesc");
			sql.append(", TO_CHAR(ICA.DTHR_REALIZADO,'DD/MM/YYYY') as dthrRealz");
			sql.append(", TO_CHAR(ECA.CAP_ATM_NUMERO)  as apac");
			sql.append(", PAC.PRONTUARIO as prontuario");
			sql.append(", PAC.CODIGO as codigo");
			sql.append(", PAC.NOME as nome");
			sql.append(", CID.NOME as cidade");
			sql.append(", UF.NOME as estado");
			sql.append(", CID1.NOME as cidadeLgd");
			sql.append(", UF1.NOME as estadoLgd");
			sql.append(", SUM(IPC.VLR_PROCEDIMENTO	* NVL(ICA.QUANTIDADE,0)) as valorProcedimento");
			sql.append(", SUM(NVL(ICA.QUANTIDADE,0)) as quantidade");

			sql.append(" FROM AGH.FAT_GRUPOS GRP");
			sql.append(", AGH.FAT_SUB_GRUPOS SGR");
			sql.append(", AGH.FAT_FORMAS_ORGANIZACAO FOG");
			sql.append(", HIST.FAT_ITENS_ESPELHOS_CONTA_APAC IEC");
			sql.append(", AGH.FAT_ITENS_PROCED_HOSPITALAR IPH");
			sql.append(", HIST.FAT_ITENS_CONTA_APAC ICA");
			sql.append(", HIST.FAT_ESPELHOS_CONTA_APAC ECA");
			sql.append(", AGH.FAT_VLR_ITEM_PROCED_HOSP_COMPS IPC");
			sql.append(", AGH.AIP_PACIENTES PAC");			
			sql.append(", AGH.AIP_ENDERECOS_PACIENTES END");
			sql.append(", AGH.AIP_CIDADES CID");
			sql.append(", AGH.AIP_UFS UF");
			sql.append(", AGH.AIP_LOGRADOUROS LGD");
			sql.append(", AGH.AIP_CIDADES CID1");
			sql.append(", AGH.AIP_UFS UF1");
			
			sql.append(" WHERE ECA.CPE_MES = :mes");
			sql.append(" AND ECA.CPE_ANO          = :ano");
			sql.append(" AND ECA.CPE_MODULO       IN (:moduloApac)"); 
			sql.append(" AND ECA.CAP_ATM_NUMERO        = ICA.CAP_ATM_NUMERO ");
			sql.append(" AND ECA.CAP_SEQP              = ICA.CAP_SEQP ");
			sql.append(" AND IEC.CAP_ATM_NUMERO    = ICA.CAP_ATM_NUMERO ");
			sql.append(" AND IEC.CAP_SEQP          = ICA.CAP_SEQP ");
			sql.append(" AND IEC.CAP_ICA_SEQP      = ICA.SEQP ");
			sql.append(" AND IEC.IND_CONSISTENTE  = 'S'");
			sql.append(" AND NVL(IEC.QUANTIDADE,0) > 0");
			sql.append(" AND IEC.PROCEDIMENTO_HOSP BETWEEN NVL(:procedIni,0) AND NVL(:procedFim,9999999999)");
			sql.append(" AND IPH.PHO_SEQ          = IEC.IPH_PHO_SEQ");
			sql.append(" AND IPH.SEQ          = IEC.IPH_SEQ");
			sql.append(" AND IPC.IPH_PHO_SEQ          = IPH.PHO_SEQ");
			sql.append(" AND IPC.IPH_SEQ          = IPH.SEQ");
			sql.append(" AND TO_CHAR(IPC.DT_INICIO_COMPETENCIA,'YYYYMMDD') <= to_char(:ano)||lpad(to_char(:mes),2,'0')||'01'");
			sql.append(" AND ( (TO_CHAR(IPC.DT_FIM_COMPETENCIA,'YYYYMMDD') >= to_char(:ano)||lpad(to_char(:mes),2,'0')||'01')");
			sql.append(" OR (IPC.DT_FIM_COMPETENCIA IS NULL) )");
			sql.append(" AND PAC.PRONTUARIO(+)          = ECA.PRONTUARIO");
			sql.append(" AND FOG.SGR_GRP_SEQ = IPH.FOG_SGR_GRP_SEQ");
			sql.append(" AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO");
			sql.append(" AND FOG.CODIGO = IPH.FOG_CODIGO");
			sql.append(" AND SGR.GRP_SEQ = FOG.SGR_GRP_SEQ");
			sql.append(" AND SGR.SUB_GRUPO = FOG.SGR_SUB_GRUPO");
			sql.append(" AND GRP.SEQ = SGR.GRP_SEQ");
			sql.append(" AND END.PAC_CODIGO(+) = PAC.CODIGO");
			sql.append(" AND CID.CODIGO(+) = END.CDD_CODIGO");
			sql.append(" AND UF.SIGLA(+) = CID.UF_SIGLA");
			sql.append(" AND LGD.CODIGO(+) = END.BCL_CLO_LGR_CODIGO");
			sql.append(" AND CID1.CODIGO(+) = LGD.CDD_CODIGO");
			sql.append(" AND UF1.SIGLA(+) = CID1.UF_SIGLA");
			sql.append(" AND ( END.seqp = (select ENDX.seqp from AGH.AIP_ENDERECOS_PACIENTES ENDX where");
			sql.append(" (seqp = (select ENDX1.seqp from AGH.AIP_ENDERECOS_PACIENTES ENDX1 where ENDX1.ind_padrao = 'S' and ENDX1.pac_codigo = PAC.CODIGO and rownum = 1)");
			sql.append(" or (rownum = 1 and (select count(*) from AGH.AIP_ENDERECOS_PACIENTES ENDX1 where ENDX1.ind_padrao = 'S' and ENDX1.pac_codigo = PAC.CODIGO) = 0))");
			sql.append(" and ENDX.pac_codigo = PAC.CODIGO");
			sql.append(" ) or END.seqp is null )");

			
			sql.append(" GROUP BY SGR.GRP_SEQ");
			sql.append(", GRP.DESCRICAO");
			sql.append(", SGR.SUB_GRUPO");
			sql.append(", SGR.DESCRICAO ");
			sql.append(", FOG.CODIGO");
			sql.append(", FOG.DESCRICAO");
			sql.append(", IEC.PROCEDIMENTO_HOSP");
			sql.append(", IPH.DESCRICAO");
			sql.append(", TO_CHAR(ICA.DTHR_REALIZADO,'DD/MM/YYYY')");
			sql.append(", PAC.PRONTUARIO");
			sql.append(", PAC.CODIGO");
			sql.append(", PAC.NOME");
			sql.append(", TO_CHAR(ECA.CAP_ATM_NUMERO)");
			sql.append(", CID.NOME");
			sql.append(", UF.NOME");
			sql.append(", CID1.NOME");
			sql.append(", UF1.NOME");
		} else {
			// QUERY NATIVA - POSTGRESQL
			sql.append("SELECT ");
			sql.append(" SGR.GRP_SEQ as grupoSeq");
			sql.append(", GRP.DESCRICAO as grupo");
			sql.append(", SGR.SUB_GRUPO as subGrupoSeq");
			sql.append(", SGR.DESCRICAO as subGrupo");
			sql.append(", FOG.CODIGO as formaOrganizacaoCodigo");
			sql.append(", FOG.DESCRICAO as formaOrganizacao");
			sql.append(", EPA.PROCEDIMENTO_HOSP as procedimentoHospitalarCod");
			sql.append(", IPH.DESCRICAO as procedimentoHospitalarDesc");
			sql.append(", TO_CHAR(PMR.DTHR_REALIZADO,'DD/MM/YYYY') as dthrRealz");
			sql.append(", NULL as apac");
			sql.append(", PAC.PRONTUARIO as prontuario");
			sql.append(", PMR.PAC_CODIGO as codigo");
			sql.append(", PAC.NOME as nome");
			sql.append(", CID.NOME as cidade");
			sql.append(", UF.NOME as estado");
			sql.append(", CID1.NOME as cidadeLgd");
			sql.append(", UF1.NOME as estadoLgd");
			sql.append(", SUM(IPC.VLR_PROCEDIMENTO	* COALESCE(EPA.QUANTIDADE,0)) as valorProcedimento");
			sql.append(", SUM(COALESCE(EPA.QUANTIDADE,0)) as quantidade");

			sql.append(" FROM AGH.FAT_GRUPOS GRP");
			sql.append(", AGH.FAT_SUB_GRUPOS SGR");
			sql.append(", AGH.FAT_FORMAS_ORGANIZACAO FOG");
			sql.append(", HIST.FAT_ESPELHOS_PROCED_AMB EPA");
			sql.append(", AGH.FAT_ITENS_PROCED_HOSPITALAR IPH");
			sql.append(", AGH.FAT_VLR_ITEM_PROCED_HOSP_COMPS IPC");
			sql.append(", HIST.FAT_PROCED_AMB_REALIZADOS PMR LEFT JOIN AGH.AIP_PACIENTES PAC ON PAC.CODIGO = PMR.PAC_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_ENDERECOS_PACIENTES ENDR ON ENDR.PAC_CODIGO = PAC.CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_CIDADES CID ON CID.CODIGO = ENDR.CDD_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_UFS UF ON UF.SIGLA = CID.UF_SIGLA");
			sql.append("  LEFT JOIN AGH.AIP_LOGRADOUROS LGD ON LGD.CODIGO = ENDR.BCL_CLO_LGR_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_CIDADES CID1 ON CID1.CODIGO = LGD.CDD_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_UFS UF1 ON UF1.SIGLA = CID1.UF_SIGLA");
			
			sql.append(" WHERE EPA.CPE_MES = :mes");
			sql.append(" AND EPA.CPE_ANO          = :ano");
			sql.append(" AND EPA.CPE_MODULO       = :modulo"); // AMB
			sql.append(" AND EPA.IND_CONSISTENTE  = 'S'");
			sql.append(" AND EPA.CPE_DT_HR_INICIO = :cpeDtHrInicio");
			sql.append(" AND EPA.PROCEDIMENTO_HOSP BETWEEN COALESCE(:procedIni,0) AND COALESCE(:procedFim,9999999999)");
			sql.append(" AND IPH.PHO_SEQ          = EPA.IPH_PHO_SEQ");
			sql.append(" AND IPH.SEQ          = EPA.IPH_SEQ");
			sql.append(" AND PMR.SEQ          = EPA.PMR_SEQ");
			sql.append(" AND IPC.IPH_PHO_SEQ          = IPH.PHO_SEQ");
			sql.append(" AND IPC.IPH_SEQ          = IPH.SEQ");
			sql.append(" AND IPC.DT_INICIO_COMPETENCIA <= TO_DATE(:ano||lpad(to_char(:mes,'FM'),2,'0')||'01', 'yyyymmdd')");
			sql.append(" AND ( (IPC.DT_FIM_COMPETENCIA >= TO_DATE(:ano||lpad(to_char(:mes, 'FM'),2,'0')||'01', 'yyyymmdd'))");
			sql.append(" OR (IPC.DT_FIM_COMPETENCIA IS NULL) )");
			sql.append(" AND FOG.SGR_GRP_SEQ = IPH.FOG_SGR_GRP_SEQ");
			sql.append(" AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO");
			sql.append(" AND FOG.CODIGO = IPH.FOG_CODIGO");
			sql.append(" AND SGR.GRP_SEQ = FOG.SGR_GRP_SEQ");
			sql.append(" AND SGR.SUB_GRUPO = FOG.SGR_SUB_GRUPO");
			sql.append(" AND GRP.SEQ = SGR.GRP_SEQ");
			sql.append(" AND ( ENDR.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where ");
			sql.append("(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = PAC.CODIGO limit 1)");
			sql.append("or ((select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = PAC.CODIGO) = 0))");
			sql.append("and endx_.pac_codigo = PAC.CODIGO ");
			sql.append("limit 1) or ENDR.seqp is null )");		
			
			sql.append(" GROUP BY SGR.GRP_SEQ");
			sql.append(", GRP.DESCRICAO");
			sql.append(", SGR.SUB_GRUPO");
			sql.append(", SGR.DESCRICAO ");
			sql.append(", FOG.CODIGO");
			sql.append(", FOG.DESCRICAO");
			sql.append(", EPA.PROCEDIMENTO_HOSP");
			sql.append(", IPH.DESCRICAO");
			sql.append(", TO_CHAR(PMR.DTHR_REALIZADO,'DD/MM/YYYY')");
			sql.append(", PAC.PRONTUARIO");
			sql.append(", PMR.PAC_CODIGO");
			sql.append(", PAC.NOME");
			sql.append(", CID.NOME");
			sql.append(", UF.NOME");
			sql.append(", CID1.NOME");
			sql.append(", UF1.NOME");

			sql.append(" UNION ");
			
			sql.append("SELECT ");
			sql.append(" SGR.GRP_SEQ as grupoSeq");
			sql.append(", GRP.DESCRICAO as grupo");
			sql.append(", SGR.SUB_GRUPO as subGrupoSeq");
			sql.append(", SGR.DESCRICAO as subGrupo");
			sql.append(", FOG.CODIGO as formaOrganizacaoCodigo");
			sql.append(", FOG.DESCRICAO as formaOrganizacao");
			sql.append(", IEC.PROCEDIMENTO_HOSP as procedimentoHospitalarCod");
			sql.append(", IPH.DESCRICAO as procedimentoHospitalarDesc");
			sql.append(", TO_CHAR(ICA.DTHR_REALIZADO,'DD/MM/YYYY') as dthrRealz");
			sql.append(", TO_CHAR(ECA.CAP_ATM_NUMERO, 'fm9999999999999999999999999999999999')  as apac");
			sql.append(", PAC.PRONTUARIO as prontuario");
			sql.append(", PAC.CODIGO as codigo");
			sql.append(", PAC.NOME as nome");
			sql.append(", CID.NOME as cidade");
			sql.append(", UF.NOME as estado");
			sql.append(", CID1.NOME as cidadeLgd");
			sql.append(", UF1.NOME as estadoLgd");
			sql.append(", SUM(IPC.VLR_PROCEDIMENTO	* COALESCE(ICA.QUANTIDADE,0)) as valorProcedimento");
			sql.append(", SUM(COALESCE(ICA.QUANTIDADE,0)) as quantidade");

			sql.append(" FROM AGH.FAT_GRUPOS GRP");
			sql.append(", AGH.FAT_SUB_GRUPOS SGR");
			sql.append(", AGH.FAT_FORMAS_ORGANIZACAO FOG");
			sql.append(", HIST.FAT_ITENS_ESPELHOS_CONTA_APAC IEC");
			sql.append(", AGH.FAT_ITENS_PROCED_HOSPITALAR IPH");
			sql.append(", HIST.FAT_ITENS_CONTA_APAC ICA");
			sql.append(", AGH.FAT_VLR_ITEM_PROCED_HOSP_COMPS IPC");
			sql.append(", HIST.FAT_ESPELHOS_CONTA_APAC ECA LEFT JOIN AGH.AIP_PACIENTES PAC ON PAC.PRONTUARIO = ECA.PRONTUARIO");
			sql.append("  LEFT JOIN AGH.AIP_ENDERECOS_PACIENTES ENDR ON ENDR.PAC_CODIGO = PAC.CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_CIDADES CID ON CID.CODIGO = ENDR.CDD_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_UFS UF ON UF.SIGLA = CID.UF_SIGLA");
			sql.append("  LEFT JOIN AGH.AIP_LOGRADOUROS LGD ON LGD.CODIGO = ENDR.BCL_CLO_LGR_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_CIDADES CID1 ON CID1.CODIGO = LGD.CDD_CODIGO");
			sql.append("  LEFT JOIN AGH.AIP_UFS UF1 ON UF1.SIGLA = CID1.UF_SIGLA");
			sql.append(" WHERE ECA.CPE_MES = :mes");
			sql.append(" AND ECA.CPE_ANO          = :ano");
			sql.append(" AND ECA.CPE_MODULO       IN (:moduloApac)"); 
			sql.append(" AND ECA.CAP_ATM_NUMERO        = ICA.CAP_ATM_NUMERO ");
			sql.append(" AND ECA.CAP_SEQP              = ICA.CAP_SEQP ");
			sql.append(" AND IEC.CAP_ATM_NUMERO    = ICA.CAP_ATM_NUMERO ");
			sql.append(" AND IEC.CAP_SEQP          = ICA.CAP_SEQP ");
			sql.append(" AND IEC.CAP_ICA_SEQP      = ICA.SEQP ");
			sql.append(" AND IEC.IND_CONSISTENTE  = 'S'");
			sql.append(" AND COALESCE(IEC.QUANTIDADE,0) > 0");
			sql.append(" AND IEC.PROCEDIMENTO_HOSP BETWEEN COALESCE(:procedIni,0) AND COALESCE(:procedFim,9999999999)");
			sql.append(" AND IPH.PHO_SEQ          = IEC.IPH_PHO_SEQ");
			sql.append(" AND IPH.SEQ          = IEC.IPH_SEQ");
			sql.append(" AND IPC.IPH_PHO_SEQ          = IPH.PHO_SEQ");
			sql.append(" AND IPC.IPH_SEQ          = IPH.SEQ");
			sql.append(" AND IPC.DT_INICIO_COMPETENCIA <= TO_DATE(:ano||lpad(to_char(:mes,'FM'),2,'0')||'01', 'yyyymmdd')");
			sql.append(" AND ( (IPC.DT_FIM_COMPETENCIA >= TO_DATE(:ano||lpad(to_char(:mes, 'FM'),2,'0')||'01', 'yyyymmdd'))");
			sql.append(" OR (IPC.DT_FIM_COMPETENCIA IS NULL) )");
			sql.append(" AND FOG.SGR_GRP_SEQ = IPH.FOG_SGR_GRP_SEQ");
			sql.append(" AND FOG.SGR_SUB_GRUPO = IPH.FOG_SGR_SUB_GRUPO");
			sql.append(" AND FOG.CODIGO = IPH.FOG_CODIGO");
			sql.append(" AND SGR.GRP_SEQ = FOG.SGR_GRP_SEQ");
			sql.append(" AND SGR.SUB_GRUPO = FOG.SGR_SUB_GRUPO");
			sql.append(" AND GRP.SEQ = SGR.GRP_SEQ");
			sql.append(" AND ( ENDR.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where ");
			sql.append("(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = PAC.CODIGO limit 1)");
			sql.append("or ((select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = PAC.CODIGO) = 0))");
			sql.append("and endx_.pac_codigo = PAC.CODIGO ");
			sql.append("limit 1) or ENDR.seqp is null )");		
			
			sql.append(" GROUP BY SGR.GRP_SEQ");
			sql.append(", GRP.DESCRICAO");
			sql.append(", SGR.SUB_GRUPO");
			sql.append(", SGR.DESCRICAO ");
			sql.append(", FOG.CODIGO");
			sql.append(", FOG.DESCRICAO");
			sql.append(", IEC.PROCEDIMENTO_HOSP");
			sql.append(", IPH.DESCRICAO");
			sql.append(", TO_CHAR(ICA.DTHR_REALIZADO,'DD/MM/YYYY')");
			sql.append(", PAC.PRONTUARIO");
			sql.append(", PAC.CODIGO");
			sql.append(", PAC.NOME");
			sql.append(", TO_CHAR(ECA.CAP_ATM_NUMERO, 'fm9999999999999999999999999999999999')");
			sql.append(", CID.NOME");
			sql.append(", UF.NOME");
			sql.append(", CID1.NOME");
			sql.append(", UF1.NOME");
		}

		SQLQuery q = createSQLQuery(sql.toString());
		q.setParameter("procedIni", procedInicial, LongType.INSTANCE);
		q.setParameter("procedFim", procedFinal, LongType.INSTANCE);
		q.setParameter("cpeDtHrInicio", cpeDtHrInicio, TimestampType.INSTANCE);
		q.setInteger("ano", ano);
		q.setInteger("mes", mes);
		q.setString("modulo", "AMB");
		q.setParameterList("moduloApac", new String[] {DominioModuloCompetencia.APAC.toString(),DominioModuloCompetencia.APEX.toString()
		,DominioModuloCompetencia.APAP.toString(),DominioModuloCompetencia.APAF.toString(),DominioModuloCompetencia.APAT.toString()
		,DominioModuloCompetencia.APAN.toString()});

		List<ItensRealizadosIndividuaisVO> listaVO = q.addScalar("grupoSeq", ShortType.INSTANCE).addScalar("grupo", StringType.INSTANCE)
				.addScalar("subGrupoSeq", ByteType.INSTANCE).addScalar("subGrupo", StringType.INSTANCE).addScalar("formaOrganizacaoCodigo", ByteType.INSTANCE)
				.addScalar("formaOrganizacao", StringType.INSTANCE).addScalar("procedimentoHospitalarCod", LongType.INSTANCE).addScalar("procedimentoHospitalarDesc", StringType.INSTANCE)
				.addScalar("dthrRealz", StringType.INSTANCE).addScalar("apac", LongType.INSTANCE).addScalar("prontuario", IntegerType.INSTANCE).addScalar("codigo", IntegerType.INSTANCE)
				.addScalar("nome", StringType.INSTANCE).addScalar("cidade", StringType.INSTANCE).addScalar("estado", StringType.INSTANCE)
				.addScalar("cidadeLgd", StringType.INSTANCE).addScalar("estadoLgd", StringType.INSTANCE).addScalar("valorProcedimento", BigDecimalType.INSTANCE)
				.addScalar("quantidade", IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(ItensRealizadosIndividuaisVO.class)).list();

		return listaVO;
	}

}