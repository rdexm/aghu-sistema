package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.ByteType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.hibernate.type.YesNoType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.vo.AihFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.AihPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.CmceCthSeqVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosMedVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosProtVO;
import br.gov.mec.aghu.faturamento.vo.CursorCBuscaRegCivilVO;
import br.gov.mec.aghu.faturamento.vo.CursorCEAIVO;
import br.gov.mec.aghu.faturamento.vo.CursorGrupo4VO;
import br.gov.mec.aghu.faturamento.vo.DemonstrativoFaturamentoInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoEncerramentoPreviaVO;
import br.gov.mec.aghu.faturamento.vo.ItemProcedimentoHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoNaoFaturadoVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteServicosVO;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteVO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihVO;
import br.gov.mec.aghu.faturamento.vo.TotaisAIHGeralVO;
import br.gov.mec.aghu.faturamento.vo.TotaisProcedEspHemotGeralVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipTipoLogradourosLog1;
import br.gov.mec.aghu.model.AipTipoLogradourosLog2;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.model.FatPerdaItemConta;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.sig.custos.vo.SigValorReceitaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
public class FatEspelhoAihDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatEspelhoAih> {

	@Inject
	private IAghuFacade aIAghuFacade;

	@Inject
	private IPacienteFacade aIPacienteFacade;

	@Inject
	private IParametroFacade aIParametroFacade;

	@Inject
	FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

	private static final long serialVersionUID = -1549130109902517788L;
	private static final String MAGIC_STRING_IND_SITUACAO_SMS_EQ_S = "S";
	private static final String ALIAS_IPH 	= "IPH"; 		// FAT_ITENS_PROCED_HOSPITALAR 		IPH ,
	private static final String ALIAS_AMA 	= "AMA"; 		// FAT_ATOS_MEDICOS_AIH 			AMA,
	private static final String ALIAS_IPHR 	= "IPHR"; 		// fat_itens_proced_hospitalar 		iph_r,
	private static final String ALIAS_EAI 	= "EAI"; 		// fat_espelhos_aih 				eai,
	private static final String ALIAS_CTH 	= "CTH"; 		// FAT_CONTAS_hospitalares 			CTH
	private static final String PONTO 		= ".";
	private static final String ALIAS_FPR   = "FPR";
	private static final String ALIAS_AMA3  = "ama3_";

	protected DetachedCriteria obterCriteriaPorCth(Integer cthSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatEspelhoAih.class);
		result.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));

		return result;
	}

	protected DetachedCriteria obterCriteriaPorCthDataPreviaNotNull(Integer cthSeq) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorCth(cthSeq);
		result.add(Restrictions.isNotNull(FatEspelhoAih.Fields.DATA_PREVIA.toString()));

		return result;
	}

	public List<FatEspelhoAih> listarPorCthDataPreviaNotNull(Integer cthSeq) {

		List<FatEspelhoAih> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorCthDataPreviaNotNull(cthSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	protected DetachedCriteria obterCriteriaPorCthSeqp(Integer cthSeq, Integer seqp) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorCth(cthSeq);
		result.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), seqp));

		return result;
	}

	protected DetachedCriteria obterCriteriaPorCthSeqpDataPreviaNotNull(Integer cthSeq, Integer seqp) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorCthSeqp(cthSeq, seqp);
		result.add(Restrictions.isNotNull(FatEspelhoAih.Fields.DATA_PREVIA.toString()));

		return result;
	}

	public List<FatEspelhoAih> listarPorCthSeqpDataPreviaNotNull(Integer cthSeq, Integer seqp) {

		List<FatEspelhoAih> result = null;
		DetachedCriteria criteria = null;

		if (cthSeq == null) {
			throw new IllegalArgumentException("cthSeq nÃ£o pode ser nulo.");
		}
		if (seqp == null) {
			throw new IllegalArgumentException("seqp nÃ£o pode ser nulo.");
		}
		criteria = this.obterCriteriaPorCthSeqpDataPreviaNotNull(cthSeq, seqp);
		result = this.executeCriteria(criteria);

		return result;
	}

	public Long countPorCthSeqpDataPreviaNotNull(Integer cthSeq, Integer seqp) {

		DetachedCriteria criteria = null;

		if (cthSeq == null) {
			throw new IllegalArgumentException("cthSeq nÃ£o pode ser nulo.");
		}
		if (seqp == null) {
			throw new IllegalArgumentException("seqp nÃ£o pode ser nulo.");
		}
		criteria = this.obterCriteriaPorCthSeqpDataPreviaNotNull(cthSeq, seqp);
		return this.executeCriteriaCount(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<ParCthSeqSsmVO> listarSsmParaListaCthSeq(final List<Integer> listaCthSeq, Short cnvCodigo, Byte cspSeq,
			DominioSituacaoSSM sitSsm) {

		List<ParCthSeqSsmVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String iphCod = null;
		String iph = null;

		switch (sitSsm) {
		case S:
			iphCod = FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.toString();
			iph = FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_SOLICITADO.toString();
			break;
		case R:
			iphCod = FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString();
			iph = FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString();
			break;
		default:
			throw new IllegalArgumentException("Situacao SSM desconhecida: " + sitSsm);
		}
		hql = new StringBuffer();
		hql.append(" select eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" as cthSeq");
		hql.append(" , eaiS.");
		hql.append(iphCod);
		hql.append(" || ' ' || substring(iphS.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(",1,35) as ssmStr");
		// solicitado
		hql.append(" from ");
		hql.append(FatEspelhoAih.class.getName());
		hql.append(" as eaiS ");
		hql.append(" left join eaiS.");
		hql.append(iph);
		hql.append(" as iphS left join iphS.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString());
		hql.append(" as cgiS with ( ");
		hql.append(" 	cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		// solicitado
		hql.append(" where eaiS.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" in (:cthSeq)");
		// realizado
		query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameterList("cthSeq", listaCthSeq);
		query.setParameter("seqp", Integer.valueOf(1));
		// vo
		query.setResultTransformer(Transformers.aliasToBean(ParCthSeqSsmVO.class));
		// result
		result = query.list();

		return result;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoRealizado(Integer cthSeq, Short cnvCodigo, Byte cspSeq) {
		StringBuffer hql = new StringBuffer(200);

		hql.append(" select eai.");
		hql.append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString());
		hql.append(" || ' ' || substring(iph.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(",1,35) ");
		hql.append(" from ");
		hql.append(FatEspelhoAih.class.getName());
		hql.append(" as eai ");
		hql.append(" left join eai.");
		hql.append(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString());
		hql.append(" as iph left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" where eai.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eai.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("seqp", 1);
		query.setMaxResults(1);

		List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoSolicitado(Integer cthSeq, Short cnvCodigo, Byte cspSeq) {
		StringBuffer hql = new StringBuffer(200);

		hql.append(" select eai.");
		hql.append(FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.toString());
		hql.append(" || ' ' || substring(iph.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(",1,35) ");
		hql.append(" from ");
		hql.append(FatEspelhoAih.class.getName());
		hql.append(" as eai ");
		hql.append(" left join eai.");
		hql.append(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_SOLICITADO.toString());
		hql.append(" as iph left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" where eai.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eai.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("seqp", 1);
		query.setMaxResults(1);

		List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	/**
	 * Lista de <code>FatEspelhoAih</code> pelo campo <code>CTH_SEQ</code>
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatEspelhoAih> listarPorCth(Integer cthSeq) {

		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorCth(cthSeq);

		return executeCriteria(criteria);
	}

	/**
	 * Primeiro <code>FatEspelhoAih</code> pelo campo <code>CTH_SEQ</code>
	 * 
	 * @param cthSeq
	 * @return
	 */
	public FatEspelhoAih buscarPrimeiroPorCthOrderBySeqp(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);
		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.addOrder(Order.asc(FatEspelhoAih.Fields.SEQP.toString()));
		List<FatEspelhoAih> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<AihFaturadaVO> listarEspelhoAihFaturada(Integer cthSeq, Integer prontuario, Integer mes, Integer ano, Date dthrInicio,
			Long codTabelaIni, Long codTabelaFim, String iniciais, Boolean reapresentada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.distinct(Projections.property("IPH_REALZ." + FatItensProcedHospitalar.Fields.DESCRICAO.toString())),
						"descricaoProcedimento").add(Projections.property(FatEspelhoAih.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property(FatEspelhoAih.Fields.NOME_PACIENTE.toString()), "pacNome")
				.add(Projections.property(FatEspelhoAih.Fields.CIDADE.toString()), "cidade")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()), "dataIternacao")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()), "dataAlta")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.NRO_AIH.toString()), "nroAih")
				.add(Projections.property(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), "iphCodSusRealiz"));

		criteria.createAlias(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), "IPH_REALZ");
		criteria.createAlias(FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), "CTH");

		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), Integer.valueOf(1)));
		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), DominioModuloCompetencia.INT));

		if (cthSeq != null) {
			criteria.add(Restrictions.eq("CTH." + FatContasHospitalares.Fields.SEQ.toString(), cthSeq));
		}
		if (prontuario != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}
		if (dthrInicio != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), dthrInicio));
		}
		if (mes != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_MES.toString(), mes.byteValue()));
		}
		if (ano != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), ano.shortValue()));
		}
		if (StringUtils.isNotBlank(iniciais)) {
			StringBuffer sql = new StringBuffer();
			for (String s : StringUtils.split(iniciais, ",")) {
				sql.append(",'").append(s).append('\'');
			}
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.PAC_NOME,1,1) in (" + sql.substring(1) + ")"));
		}
		if (reapresentada) {
			criteria.add(Restrictions.isNotNull("CTH." + FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		} else {
			criteria.add(Restrictions.isNull("CTH." + FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		}
		if (codTabelaFim != null) {
			criteria.add(Restrictions.between("IPH_REALZ." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabelaIni,
					codTabelaFim));
		} else {
			criteria.add(Restrictions.eq("IPH_REALZ." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabelaIni));
		}

		criteria.addOrder(Order.asc("IPH_REALZ." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FatEspelhoAih.Fields.NOME_PACIENTE.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AihFaturadaVO.class));

		return executeCriteria(criteria);
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<ResumoCobrancaAihVO> pesquisarEspelhoAih(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.IND_IMPRESSAO_ESPELHO.toString()), "indImprimeEspelho")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.SEQ.toString()), "cthSeq")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()), "cthSeqReapresentada")
				.add(Projections.property(FatEspelhoAih.Fields.SEQP.toString()), "seqp")
				.add(Projections.property(FatEspelhoAih.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property(FatEspelhoAih.Fields.NOME_PACIENTE.toString()), "nome")
				.add(Projections.property(FatEspelhoAih.Fields.DATA_NASCIMENTO.toString()), "dtNascimento")
				.add(Projections.property(FatEspelhoAih.Fields.SEXO.toString()), "pacSexo")
				.add(Projections.property(FatEspelhoAih.Fields.DCI_CPE_MES.toString()), "dciCpeMes")
				.add(Projections.property(FatEspelhoAih.Fields.DCI_CPE_ANO.toString()), "dciCpeAno")
				.add(Projections.property(FatEspelhoAih.Fields.DCIH.toString()), "codigoDcih")
				.add(Projections.property(FatEspelhoAih.Fields.EXCL_CRITICA.toString()), "exclusaoCritica")
				.add(Projections.property(FatEspelhoAih.Fields.COD_CENTRAL.toString()), "conCodCentral")
				.add(Projections.property(FatEspelhoAih.Fields.DAU_SENHA.toString()), "dauSenha")
				.add(Projections.property(FatEspelhoAih.Fields.NUMERO_AIH.toString()), "numeroAih")
				.add(Projections.property(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.toString()), "especialidadeAih")
				.add(Projections.property(FatEspelhoAih.Fields.TAH_SEQ.toString()), "tahSeq")
				.add(Projections.property(FatEspelhoAih.Fields.TCI_COD_SUS.toString()), "tciCodSus")
				.add(Projections.property(FatEspelhoAih.Fields.CPF_MEDC_AUDIT.toString()), "cpfMedicoAuditor")
				.add(Projections.property(FatEspelhoAih.Fields.CNS_MEDICO_AUDITOR.toString()), "cnsMedicoAuditor")
				.add(Projections.property(FatEspelhoAih.Fields.DATA_INTERNACAO.toString()), "dtInternacao")
				.add(Projections.property(FatEspelhoAih.Fields.DATA_SAIDA.toString()), "dtSaida")
				.add(Projections.property(FatEspelhoAih.Fields.MOTIVO_COBRANCA.toString()), "motivoCobranca")
				.add(Projections.property(FatEspelhoAih.Fields.CID_PRIMARIO.toString()), "cidPrimario")
				.add(Projections.property(FatEspelhoAih.Fields.CID_SECUNDARIO.toString()), "cidSecundario")
				.add(Projections.property(FatEspelhoAih.Fields.AIH_ANTERIOR.toString()), "numeroAihAnterior")
				.add(Projections.property(FatEspelhoAih.Fields.AIH_POSTERIOR.toString()), "numeroAihPosterior")
				.add(Projections.property(FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.toString()), "iphCodSusSolic")
				.add(Projections.property(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), "iphCodSusRealz")
				.add(Projections.property("IPH_REALZ." + FatItensProcedHospitalar.Fields.SEQ.toString()), "seqRealz")
				.add(Projections.property("IPH_REALZ." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()), "phoSeqRealz")
				.add(Projections.property(FatEspelhoAih.Fields.LEITO.toString()), "leito")
				.add(Projections.property(FatEspelhoAih.Fields.ENFERMARIA.toString()), "enfermaria")
				.add(Projections.property("IPH_SOL." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), "descricaoProcedimentoSolic")
				.add(Projections.property("IPH_REALZ." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()),
						"descricaoProcedimentoRealz")
				.add(Projections.property(FatEspelhoAih.Fields.VALOR_SADT_REALIZ.toString()), "valorSADT")
				.add(Projections.property(FatEspelhoAih.Fields.VALOR_SH_REALIZ.toString()), "valorSH")
				.add(Projections.property(FatEspelhoAih.Fields.VALOR_SP_REALIZ.toString()), "valorSP")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.VALOR_ACOMP.toString()), "valorAcomp")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.USUARIO_PREVIA.toString()), "usuarioPrevia")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.DATA_PREVIA.toString()), "dtPrevia")
				.add(Projections.property(FatEspelhoAih.Fields.CRIADO_POR.toString()), "criadoPor")
				.add(Projections.property(FatEspelhoAih.Fields.CRIADO_EM.toString()), "criadoEm")

		);

		criteria.createAlias(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_SOLICITADO.toString(), "IPH_SOL");
		criteria.createAlias(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), "IPH_REALZ");
		criteria.createAlias(FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), "CTH");
		criteria.add(Restrictions.eq("CTH." + FatContasHospitalares.Fields.SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), Integer.valueOf(1)));

		criteria.setResultTransformer(Transformers.aliasToBean(ResumoCobrancaAihVO.class));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<ProcedimentoNaoFaturadoVO> pesquisarEspelhoAihProcedimentosNaoFaturados(Long iphCodTabela, Long iphCodSusRealiz,
			Short espSeq, Integer mes, Integer ano, Date dthrInicio, DominioGrupoProcedimento grupoProcedimento, String iniciais,
			Boolean reapresentada) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class, "MIR");

		
		String valorTotal = " SUM((COALESCE(ipc6_.VLR_SERV_HOSPITALAR,0) + COALESCE(ipc6_.VLR_SERV_PROFISSIONAL,0) + COALESCE(ipc6_.VLR_SADT,0) + COALESCE(ipc6_.VLR_ANESTESISTA,0)) * pit2_.QUANTIDADE_PERDIDA) valorTotal";
		
			
			
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("ESP." + AghEspecialidades.Fields.SIGLA.toString()), "siglaEspecialidade")
				.add(Projections.groupProperty("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.NOME_PACIENTE.toString()), "nome")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.LEITO.toString()), "leito")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.ENFERMARIA.toString()), "enfermaria")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.NUMERO_AIH.toString()), "nroAih")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.DATA_INTERNACAO.toString()), "dtInternacao")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.DATA_SAIDA.toString()), "dtSaida")
				.add(Projections.groupProperty("MIR." + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), "iphCodSusRealiz")
				.add(Projections.groupProperty("PIT." + FatPerdaItemConta.Fields.COD_TABELA.toString()), "codTabela")
				.add(Projections.groupProperty("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), "descricaoProcedimento")
				.add(Projections.sum("PIT." + FatPerdaItemConta.Fields.QUANTIDADE_PERDIDA.toString()), "quantidadePerdida")
				.add(Projections.sqlProjection(valorTotal, new String [] {"valorTotal"}, new Type[] {new BigDecimalType()}))
				.add(Projections.sum("PIT." + FatPerdaItemConta.Fields.VALOR_SH.toString()), "valorServHosp")
				.add(Projections.sum("PIT." + FatPerdaItemConta.Fields.VALOR_SP.toString()), "valorServProf")
				.add(Projections.sum("PIT." + FatPerdaItemConta.Fields.VALOR_SADT.toString()), "valorSADT")
				.add(Projections.sum("PIT." + FatPerdaItemConta.Fields.VALOR_ANEST.toString()), "valorAnest")
				.add(Projections.sum("PIT." + FatPerdaItemConta.Fields.VALOR_PROCED.toString()), "valorProcedimento"));
		
				
		
		criteria.createAlias("MIR." + FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), "CTH");
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.PERDA_ITENS_CONTA.toString(), "PIT");
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");
		criteria.createAlias("CSP." + FatConvenioSaudePlano.Fields.CONVENIO_GRUPO_ITEM_PROCED.toString(), "CGI");
		criteria.createAlias("PIT." + FatPerdaItemConta.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC",JoinType.INNER_JOIN);
		
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "PHI");
		//Modificado para innner join conforme consulta original
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);


		AghParametros tabelaFaturadaPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);

		criteria.add(Restrictions.eqProperty("PIT." + FatPerdaItemConta.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), "CGI."
				+ FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString()));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), tabelaFaturadaPadrao.getVlrNumerico()
				.shortValue()));

		// OK - ITEM PROCEDIMENTO
		if (iphCodTabela != null) {
			criteria.add(Restrictions.eq("PIT." + FatPerdaItemConta.Fields.COD_TABELA.toString(), iphCodTabela));
		}
		if (espSeq != null) {
			criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		}
		// OK - PROCEDIMENTO
		if (iphCodSusRealiz != null) {
			criteria.add(Restrictions.eq("MIR." + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString(), iphCodSusRealiz));
		}
		if (grupoProcedimento != null) {
			if (DominioGrupoProcedimento.M.equals(grupoProcedimento)) {
				criteria.add(Restrictions.isNotNull("PHI." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()));
			} else if (DominioGrupoProcedimento.C.equals(grupoProcedimento)) {
				criteria.add(Restrictions.isNotNull("PHI." + FatProcedHospInternos.Fields.PCI_SEQ.toString()));
			} else if (DominioGrupoProcedimento.D.equals(grupoProcedimento)) {
				criteria.add(Restrictions.isNotNull("PHI." + FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()));
			} else if (DominioGrupoProcedimento.S.equals(grupoProcedimento)) {
				criteria.add(Restrictions.isNotNull("PHI." + FatProcedHospInternos.Fields.CSA_CODIGO.toString()));
			} else if (DominioGrupoProcedimento.H.equals(grupoProcedimento)) {
				criteria.add(Restrictions.isNotNull("PHI." + FatProcedHospInternos.Fields.PHE_CODIGO.toString()));
			} else if (DominioGrupoProcedimento.E.equals(grupoProcedimento)) {
				criteria.add(Restrictions.isNotNull("PHI." + FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString()));
			}

		}

		if (reapresentada) {
			criteria.add(Restrictions.isNotNull("CTH." + FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		} else {
			criteria.add(Restrictions.isNull("CTH." + FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		}

		if (StringUtils.isNotBlank(iniciais)) {
			StringBuffer sql = new StringBuffer();
			for (String s : StringUtils.split(iniciais, ",")) {
				sql.append(",'").append(s).append('\'');
			}
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.PAC_NOME,1,1) in (" + sql.substring(1) + ")"));
		}

		criteria.add(Restrictions.eq("MIR." + FatEspelhoAih.Fields.SEQP.toString(), Integer.valueOf(1)));
		criteria.add(Restrictions.eq("MIR." + FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), DominioModuloCompetencia.INT));

		criteria.add(Restrictions.eq("MIR." + FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), dthrInicio));
		criteria.add(Restrictions.eq("MIR." + FatEspelhoAih.Fields.DCI_CPE_MES.toString(), mes.byteValue()));
		criteria.add(Restrictions.eq("MIR." + FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), ano.shortValue()));

		criteria.add(Restrictions.isNull("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));
		
		AghParametros medSemRelTabelaSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MEDICACAO_SEM_RELAC_TABELA_SUS);
		criteria.add(Restrictions.ne("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), medSemRelTabelaSus.getVlrNumerico()
				.longValue()));

		DetachedCriteria subQuery = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "CGI1");
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.min("CGI1." + FatConvGrupoItemProced.Fields.PHI_SEQ.toString())));
		subQuery.add(Restrictions.eqProperty("CGI." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "CGI1."
				+ FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString()));
		subQuery.add(Restrictions.eqProperty("CGI." + FatConvGrupoItemProced.Fields.CONVENIO_SAUDE_PLANO.toString(), "CGI1."
				+ FatConvGrupoItemProced.Fields.CONVENIO_SAUDE_PLANO.toString()));

		criteria.add(Subqueries.propertyEq("PHI." + FatProcedHospInternos.Fields.SEQ.toString(), subQuery));

		AghParametros naoCobra = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CARCACT_NAO_COBRA_SE_HOUVER_TRAT_AMB);
		DetachedCriteria subQueryCaract = DetachedCriteria.forClass(FatCaractItemProcHosp.class, "CIP");
		subQueryCaract.setProjection(Projections.property("CIP." + FatCaractItemProcHosp.Fields.IPH_SEQ.toString()));
		subQueryCaract.add(Restrictions.eq("CIP." + FatCaractItemProcHosp.Fields.TCT_SEQ.toString(), naoCobra.getVlrNumerico().intValue()));
		subQueryCaract.add(Restrictions.eqProperty("CIP." + FatCaractItemProcHosp.Fields.IPH_SEQ.toString(), "PIT."
				+ FatPerdaItemConta.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString() + "." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		subQueryCaract.add(Restrictions.eqProperty(
				"CIP." + FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(),
				"PIT." + FatPerdaItemConta.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString() + "."
						+ FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));

		AghParametros nefrologia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_ESPECIALIDADE_NEFROLOGIA);
		criteria.add(Restrictions.and(Restrictions.or(Restrictions.gt("PIT." + FatPerdaItemConta.Fields.VALOR_SH.toString(),
				BigDecimal.valueOf(0)), Restrictions.or(Restrictions.gt("PIT." + FatPerdaItemConta.Fields.VALOR_SP.toString(),
				BigDecimal.valueOf(0)), Restrictions.or(Restrictions.gt("PIT." + FatPerdaItemConta.Fields.VALOR_SADT.toString(),
				BigDecimal.valueOf(0)), Restrictions.or(
				Restrictions.gt("PIT." + FatPerdaItemConta.Fields.VALOR_PROCED.toString(), BigDecimal.valueOf(0)),
				Restrictions.gt("PIT." + FatPerdaItemConta.Fields.VALOR_ANEST.toString(), BigDecimal.valueOf(0)))))), Restrictions.or(
				Restrictions.ne("CTH." + FatContasHospitalares.Fields.ESPECIALIDADE_SEQ, nefrologia.getVlrNumerico().shortValue()),
				Restrictions.and(
						Restrictions.eq("CTH." + FatContasHospitalares.Fields.ESPECIALIDADE_SEQ, nefrologia.getVlrNumerico().shortValue()),
						Restrictions.and(Restrictions.isNull("PIT." + FatPerdaItemConta.Fields.COD_TABELA.toString()),
								Subqueries.exists(subQueryCaract))))));

		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc("MIR." + FatEspelhoAih.Fields.NOME_PACIENTE.toString()));
		criteria.addOrder(Order.asc("MIR." + FatEspelhoAih.Fields.DATA_INTERNACAO.toString()));
		criteria.addOrder(Order.asc("MIR." + FatEspelhoAih.Fields.NUMERO_AIH.toString()));
		criteria.addOrder(Order.asc("PIT." + FatPerdaItemConta.Fields.COD_TABELA.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoNaoFaturadoVO.class));

		return executeCriteria(criteria);
	}

	public List<DemonstrativoFaturamentoInternacaoVO> pesquisarEspelhoAih(Integer prontuario, Date dtInternacaoAdm, final Byte taoSeq)
			throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.SEQ.toString()), "cthSeq")
				.add(Projections.property("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property(FatEspelhoAih.Fields.NOME_PACIENTE.toString()), "nome")
				.add(Projections.property("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString()), "leito")
				.add(Projections.property(FatEspelhoAih.Fields.NUMERO_AIH.toString()), "nroAih")
				.add(Projections.property(FatEspelhoAih.Fields.DATA_INTERNACAO.toString()), "dtInternacao")
				.add(Projections.property(FatEspelhoAih.Fields.DATA_SAIDA.toString()), "dtSaida")
				.add(Projections.property("PEF." + RapPessoasFisicas.Fields.NOME.toString()), "equipeResposavel")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), "motivoInternacao")
				.add(Projections.property(FatEspelhoAih.Fields.VALOR_SADT_REALIZ.toString()), "totalSADT")
				.add(Projections.property(FatEspelhoAih.Fields.VALOR_SH_REALIZ.toString()), "totalServHosp")
				.add(Projections.property(FatEspelhoAih.Fields.VALOR_SP_REALIZ.toString()), "totalServProf"));

		criteria.createAlias(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), "IPH");
		criteria.createAlias(FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), "CTH");
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "COI");
		criteria.createAlias("COI." + FatContasInternacao.Fields.INTERNACAO.toString(), "INT");
		criteria.createAlias("INT." + AinInternacao.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("INT." + AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "PROF");
		criteria.createAlias("PROF." + RapServidores.Fields.PESSOA_FISICA.toString(), "PEF");

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));

		Calendar cInicio = Calendar.getInstance();
		cInicio.setTime(dtInternacaoAdm);
		cInicio.set(Calendar.HOUR_OF_DAY, 0);
		cInicio.set(Calendar.MINUTE, 0);
		cInicio.set(Calendar.SECOND, 0);
		cInicio.set(Calendar.MILLISECOND, 0);

		Calendar cFim = Calendar.getInstance();
		cFim.setTime(dtInternacaoAdm);
		cFim.set(Calendar.HOUR_OF_DAY, 23);
		cFim.set(Calendar.MINUTE, 59);
		cFim.set(Calendar.SECOND, 59);
		cFim.set(Calendar.MILLISECOND, 0);

		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), Integer.valueOf(1)));
		criteria.add(Restrictions.between("ATD." + AghAtendimentos.Fields.DATA_HORA_INICIO.toString(), cInicio.getTime(), cFim.getTime()));

		criteria.setResultTransformer(Transformers.aliasToBean(DemonstrativoFaturamentoInternacaoVO.class));

		List<DemonstrativoFaturamentoInternacaoVO> listaVO = executeCriteria(criteria);
		if (listaVO != null && !listaVO.isEmpty()) {
			BigDecimal totalInternacao = BigDecimal.valueOf(0);
			int indice = 1;
			for (DemonstrativoFaturamentoInternacaoVO vo : listaVO) {
				if (vo != null) {
					BigDecimal total = BigDecimal.valueOf(0);
					if (vo.getTotalServProf() != null) {
						total = total.add(vo.getTotalServProf());
					}
					if (vo.getTotalServHosp() != null) {
						total = total.add(vo.getTotalServHosp());
					}
					if (vo.getTotalSADT() != null) {
						total = total.add(vo.getTotalSADT());
					}

					if (vo.getDtSaida() != null && vo.getDtInternacao() != null) {
						vo.setTempoPermanencia(CoreUtil.diferencaEntreDatasEmDias(vo.getDtSaida(), vo.getDtInternacao()).intValue());
					}
					List<ItemProcedimentoHospitalarVO> lista = new ArrayList<ItemProcedimentoHospitalarVO>();
					final BeanComparator sorter = new BeanComparator("descricao", new NullComparator(false));
					List<ItemProcedimentoHospitalarVO> listaParcial = getFatItensProcedHospitalarDAO().obterProcedimentosMedicoAudtAIH(
							vo.getCthSeq());
					if (listaParcial != null && !listaParcial.isEmpty()) {
						for (ItemProcedimentoHospitalarVO item : listaParcial) {
							total = total.add(item.getValor());
						}
						Collections.sort(listaParcial, sorter);
						lista.addAll(listaParcial);
					}
					listaParcial = getFatItensProcedHospitalarDAO().obterProcedimentosAtosMedicosAIH(vo.getCthSeq(), taoSeq);
					if (listaParcial != null && !listaParcial.isEmpty()) {
						for (ItemProcedimentoHospitalarVO item : listaParcial) {
							total = total.add(item.getValor());
						}
						Collections.sort(listaParcial, sorter);
						lista.addAll(listaParcial);
					}
					totalInternacao = totalInternacao.add(total);
					vo.setTotalAIH(total);
					vo.setTotalInternacao(totalInternacao);
					vo.setListaProcedimento(lista);
					vo.setIndiceConta(indice);
					vo.setNumeroContas(listaVO.size());
				}
				indice++;
			}
		}

		return listaVO;
	}

	public Long listarFatEspelhoAihCount(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));
		}

		return executeCriteriaCount(criteria);
	}

	protected IParametroFacade getParametroFacade() {
		// return (ParametroFacade)Component.getInstance(ParametroFacade.class,
		// true);
		return aIParametroFacade;
	}

	// protected IFaturamentoFacade getFaturamentoFacade() {
	// return aIFaturamentoFacade;
	// }

	protected DetachedCriteria obterCriteriaPorCompetencia(Short ano, Byte mes, Date dtHrInicio, String alias) {

		DetachedCriteria result = null;

		if (alias != null) {
			result = DetachedCriteria.forClass(FatEspelhoAih.class, alias);
		} else {
			result = DetachedCriteria.forClass(FatEspelhoAih.class);
		}
		result.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), ano));
		result.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_MES.toString(), mes));

		result.add(Restrictions.ge(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), DateUtil.obterDataComHoraInical(dtHrInicio)));
		result.add(Restrictions.le(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), DateUtil.obterDataComHoraFinal(dtHrInicio)));

		return result;
	}

	protected DetachedCriteria obterCriteriaPorCompetenciaSeqp1DeInternacao(Short ano, Byte mes, Date dtHrInicio, String alias) {

		DetachedCriteria result = null;

		result = this.obterCriteriaPorCompetencia(ano, mes, dtHrInicio, alias);
		// eai.seqp = 1
		result.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), Integer.valueOf(1)));
		// eai.dci_cpe_modulo = 'INT'
		result.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), DominioModuloCompetencia.INT));

		return result;
	}

	protected DetachedCriteria obterCriteriaCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradas(Short ano, Byte mes,
			Date dtHrInicio, String alias) {

		DetachedCriteria result = null;
		String cth = null;

		result = this.obterCriteriaPorCompetenciaSeqp1DeInternacao(ano, mes, dtHrInicio, alias);
		cth = FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString();
		result.createAlias(FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), cth);
		// cth.dci_codigo_dcih = eai.dci_codigo_dcih
		result.add(Restrictions.eqProperty(cth + "." + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString() + "."
				+ FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), FatEspelhoAih.Fields.DCI_CODIGO_DCIH.toString()));
		// cth.ind_autorizado_sms = 'S'
		result.add(Restrictions.eq(cth + "." + FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString(),
				MAGIC_STRING_IND_SITUACAO_SMS_EQ_S));
		// cth.ind_situacao = 'E'
		result.add(Restrictions.eq(cth + "." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.E));
		// cth.nro_aih is not null
		result.add(Restrictions.isNotNull(cth + "." + FatContasHospitalares.Fields.AIH.toString()));

		// Utilizado para testar a virada de competencia por uma faixa de
		// contas...
		// Integer []cths =
		// {431962,431960,431958,431954,374864,374864,374864,374864,374864,374864};
		// result.add(Restrictions.in(cth +"."+
		// FatContasHospitalares.Fields.SEQ.toString() , Arrays.asList(cths)));

		return result;
	}

	public List<FatEspelhoAih> listarCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradas(Short ano, Byte mes, Date dtHrInicio) {
		final DetachedCriteria criteria = this.obterCriteriaCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradas(ano, mes,
				dtHrInicio, null);
		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradasEntreDatas(Short ano, Byte mes,
			Date dtHrInicio, Date dataEncIni, Date dataEncFinal) {

		DetachedCriteria result = null;
		DetachedCriteria subCriteria = null;
		String cthSub = null;
		String aih = null;

		aih = "AIH";
		result = this.obterCriteriaCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradas(ano, mes, dtHrInicio, aih);
		// sub select
		cthSub = "SUB_CTH";
		subCriteria = DetachedCriteria.forClass(FatContasHospitalares.class, cthSub);
		// dci_codigo_dcih = eai.dci_codigo_dcih
		subCriteria.add(Restrictions.eqProperty(subCriteria.getAlias() + "." + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString()
				+ "." + FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), result.getAlias() + "."
				+ FatEspelhoAih.Fields.DCI_CODIGO_DCIH.toString()));
		// and dt_encerramento >= p_dt_enc_inicial
		// and dt_encerramento <= p_dt_enc_final + 1
		subCriteria.add(Restrictions.between(FatContasHospitalares.Fields.DT_ENCERRAMENTO.toString(), dataEncIni, dataEncFinal));
		subCriteria.setProjection(Projections.property(subCriteria.getAlias() + "." + FatContasHospitalares.Fields.SEQ.toString()));
		// exists
		result.add(Subqueries.exists(subCriteria));

		return result;
	}

	public List<FatEspelhoAih> listarCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradasEntreDatas(Short ano, Byte mes,
			Date dtHrInicio, Date dataEncIni, Date dataEncFinal) {
		final DetachedCriteria criteria = this.obterCriteriaCompetenciaSeqp1DeInternacaoComContasAutorizadasEncerradasEntreDatas(ano, mes,
				dtHrInicio, dataEncIni, dataEncFinal);
		return executeCriteria(criteria);
	}

	public FatEspelhoAih buscarPrimeiroEspelhoAIH(Integer cthSeq, Integer seqp, DominioModuloCompetencia modulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));
		}

		if (seqp != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), seqp));
		}

		if (modulo != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), modulo));
		}

		List<FatEspelhoAih> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public FatEspelhoAih buscarPrimeiroEspelhoAIHIndBcoCapacNulo(Integer cthSeq, Integer seqp, DominioModuloCompetencia modulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));
		}

		if (seqp != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.SEQP.toString(), seqp));
		}

		if (modulo != null) {
			criteria.add(Restrictions.eq(FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), modulo));
		}

		criteria.add(Restrictions.isNull(FatEspelhoAih.Fields.IND_BCO_CAPAC.toString()));

		List<FatEspelhoAih> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Integer buscaProximaSeqTabelaEspelho(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);

		criteria.setProjection(Projections.max(FatEspelhoAih.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));

		Integer seqp = (Integer) this.executeCriteriaUniqueResult(criteria);

		if (seqp == null) {
			seqp = 1;
		} else {
			seqp++;
		}

		return seqp;
	}

	/**
	 * Remove os FatEspelhoAih relacionados a uma conta hospitalar e data de
	 * prÃ©via nÃ£o nula.
	 * 
	 * Feito com HQL por motivo de performance, jÃ¡ que a entidade nÃ£o possui
	 * trigger de deleÃ§Ã£o
	 * 
	 * @param cthSeq
	 *            conta hospitalar
	 * @return nÃºmero de registros deletados
	 * @return
	 */
	public Integer removerRnCthcAtuEncPrv(Integer cthSeq) {
		Query query = createHibernateQuery("delete " + FatEspelhoAih.class.getName() + " where " + FatEspelhoAih.Fields.CTH_SEQ.toString()
				+ " = :cthSeq " + "   and " + FatEspelhoAih.Fields.DATA_PREVIA.toString() + " is not null ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}

	public Integer removerPorCthSeqs(List<Integer> cthSeqs) {		
		Query query = createHibernateQuery("delete " + FatEspelhoAih.class.getName() + 
				 " where " + FatEspelhoAih.Fields.CTH_SEQ.toString() + " in (:cthSeqs) ");
		query.setParameterList("cthSeqs", cthSeqs);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatEspelhoAih relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, jÃ¡ que a entidade nÃ£o possui
	 * trigger de deleÃ§Ã£o
	 * 
	 * @param cthSeq
	 *            conta hospitalar
	 * @return nÃºmero de registros deletados
	 * @return
	 */
	public Integer removerPorCth(Integer cthSeq) {
		Query query = createHibernateQuery("delete " + FatEspelhoAih.class.getName() + " where " + FatEspelhoAih.Fields.CTH_SEQ.toString()
				+ " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}

	/**
	 * Retorna uma lista de FatEspelhoAih pelo Cth
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatEspelhoAih> obterFatEspelhoAihPorCth(Integer cthSeq) {
		DetachedCriteria criteria = obterCriteriaPorCth(cthSeq);
		criteria.createAlias(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_SOLICITADO.toString(), "IPHS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), "IPHR", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}

	public List<AihPorProcedimentoVO> obterAihsPorProcedimentoVO(final Long procedimentoInicial, final Long procedimentoFinal,
			final Date dtHrInicio, final Integer mes, final Integer ano, final String iniciaisPaciente, final boolean reapresentada) {

		final StringBuffer sql = new StringBuffer(1900);

		sql.append(" select ")
				.append("         distinct eai.pac_prontuario as ")
				.append(AihPorProcedimentoVO.Fields.PRONTUARIO.toString())
				.append("       , pac.codigo 	             as ")
				.append(AihPorProcedimentoVO.Fields.CODIGO.toString())
				.append("       , eai.pac_nome                as ")
				.append(AihPorProcedimentoVO.Fields.NOME.toString())
				.append("       , eai.end_cidade_pac          as ")
				.append(AihPorProcedimentoVO.Fields.CIDADE.toString())
				.append("       , eai.data_internacao         as ")
				.append(AihPorProcedimentoVO.Fields.INTERNACAO.toString())
				.append("       , eai.data_saida              as ")
				.append(AihPorProcedimentoVO.Fields.ALTA.toString())
				.append("       , eai.numero_aih              as ")
				.append(AihPorProcedimentoVO.Fields.AIH.toString())
				.append("       , eai.iph_cod_sus_realiz      as ")
				.append(AihPorProcedimentoVO.Fields.PROCEDIMENTO.toString())

				.append("  from  ")
				.append("        agh.fat_espelhos_aih        eai ")
				.append("       ,agh.fat_contas_hospitalares cth ")
				.append("       ,agh.aip_pacientes           pac ")

				.append(" where ")
				.append("           eai.seqp   = 1 ")
				.append("       and eai.dci_codigo_dcih = cth.dci_codigo_dcih ")
				.append("	   and eai.dci_cpe_dt_hr_inicio = :prm_dthr_inicio ")
				.append("	   and eai.dci_cpe_modulo	    = :prm_modulo ")
				.append("       and eai.dci_cpe_mes          = :prm_mes ")
				.append("	   and eai.dci_cpe_ano          = :prm_ano ")

				.append("       and ( eai.cth_seq in ( select distinct  aam.eai_cth_seq ")
				.append("                              from agh.fat_atos_medicos_aih  aam ")
				.append("                              where aam.iph_cod_sus between  :procedimentoInicial  and :procedimentoFinal ")
				.append("                                    and aam.eai_cth_seq = cth.seq ")
				.append("                            )")
				.append("             or ")

				.append("             eai.cth_seq in (select cah.eai_cth_seq ")
				.append("                              from  agh.fat_campos_medico_audit_aih cah ")
				.append("                              where cah.iph_cod_sus  between  :procedimentoInicial  and  :procedimentoFinal ")
				.append("                                    and cah.eai_cth_seq = cth.seq ")
				.append("                            )")
				.append("             or ")

				.append("             (eai.cth_seq = cth.seq and eai.iph_cod_sus_realiz  between  :procedimentoInicial  and  :procedimentoFinal )")
				.append("           ) ")

				.append("       and pac.nome       = eai.pac_nome ").append("       and pac.prontuario = eai.pac_prontuario ");

		if (StringUtils.isNotBlank(iniciaisPaciente)) {
			sql.append(" and upper(substr(pac.nome,1,1)) in (:prm_iniciais_paciente)");
		}

		if (reapresentada) {
			sql.append(" and cth.cth_seq_reapresentada is not null");
		} else {
			sql.append(" and cth.cth_seq_reapresentada is null");
		}

		sql.append(" order by eai.pac_nome ").append("        , eai.pac_prontuario ").append("        , eai.data_internacao ")
				.append("        , EAI.DATA_SAIDA ");

		final SQLQuery query = createSQLQuery(sql.toString());
		query.setString("prm_modulo", DominioModuloCompetencia.INT.toString());
		query.setTimestamp("prm_dthr_inicio", dtHrInicio);
		query.setInteger("prm_mes", mes);
		query.setInteger("prm_ano", ano);

		query.setLong("procedimentoInicial", procedimentoInicial);
		query.setLong("procedimentoFinal", procedimentoFinal);

		if (iniciaisPaciente != null && StringUtils.isNotBlank(iniciaisPaciente)) {
			final List<String> lst = new ArrayList<String>();
			for (char a : iniciaisPaciente.toCharArray()) {
				lst.add(Character.toString(a));
			}

			query.setParameterList("prm_iniciais_paciente", lst);
		}

		final List<AihPorProcedimentoVO> result = query.addScalar(AihPorProcedimentoVO.Fields.PRONTUARIO.toString(), IntegerType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.CODIGO.toString(), IntegerType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.NOME.toString(), StringType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.CIDADE.toString(), StringType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.AIH.toString(), LongType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.PROCEDIMENTO.toString(), StringType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.INTERNACAO.toString(), TimestampType.INSTANCE)
				.addScalar(AihPorProcedimentoVO.Fields.ALTA.toString(), TimestampType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(AihPorProcedimentoVO.class)).list();

		return result;
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<ResumoAIHEmLoteVO> pesquisarResumoAIHEmLote(final Integer cthSeq, final Date dtInicial, final Date dtFinal,
			final Boolean indAutorizadoSSM, final String iniciaisPaciente, final Boolean reapresentada) {

		final StringBuffer sql = new StringBuffer(3000);

		sql.append(" SELECT ")
				.append("          FE.CON_COD_CENTRAL    AS ")
				.append(ResumoAIHEmLoteVO.Fields.CON_COD_CENTRAL.toString())
				.append("        , FE.DAU_SENHA          AS ")
				.append(ResumoAIHEmLoteVO.Fields.DAU_SENHA.toString())
				.append("        , FE.CTH_SEQ            AS ")
				.append(ResumoAIHEmLoteVO.Fields.CTH_SEQ.toString())
				.append("        , FE.SEQP               AS ")
				.append(ResumoAIHEmLoteVO.Fields.SEQ_P.toString())
				.append("        , FE.DCI_CODIGO_DCIH    AS ")
				.append(ResumoAIHEmLoteVO.Fields.CODIGO_DCIH.toString())
				.append("        , FE.NUMERO_AIH         AS ")
				.append(ResumoAIHEmLoteVO.Fields.NUMERO_AIH.toString())
				.append("        , FE.PAC_NOME           AS ")
				.append(ResumoAIHEmLoteVO.Fields.NOME.toString())
				.append("        , FE.PAC_DT_NASCIMENTO  AS ")
				.append(ResumoAIHEmLoteVO.Fields.DT_NASCIMENTO.toString())
				.append("        , FE.PAC_SEXO           AS ")
				.append(ResumoAIHEmLoteVO.Fields.PAC_SEXO.toString())
				.append("        , FE.TAH_SEQ            AS ")
				.append(ResumoAIHEmLoteVO.Fields.TAH_SEQ.toString())
				.append("        , FE.PAC_PRONTUARIO     AS ")
				.append(ResumoAIHEmLoteVO.Fields.PRONTUARIO.toString())
				.append("        , AC.DESCRICAO          AS ")
				.append(ResumoAIHEmLoteVO.Fields.ESPECIALIDADE.toString())
				.append("        , FE.LEITO              AS ")
				.append(ResumoAIHEmLoteVO.Fields.LEITO.toString())
				.append("        , FE.IPH_COD_SUS_SOLIC  AS ")
				.append(ResumoAIHEmLoteVO.Fields.IPH_COD_SUS_SOLIC.toString())
				.append("        , FE.TCI_COD_SUS        AS ")
				.append(ResumoAIHEmLoteVO.Fields.TCI_COD_SUS.toString())
				.append("        , FE.IPH_COD_SUS_REALIZ AS ")
				.append(ResumoAIHEmLoteVO.Fields.IPH_COD_SUS_REALZ.toString())
				.append("        , FE.CPF_MEDICO_AUDITOR AS ")
				.append(ResumoAIHEmLoteVO.Fields.CPF_MEDICO_AUDITOR.toString())
				.append("        , FE.DATA_INTERNACAO    AS ")
				.append(ResumoAIHEmLoteVO.Fields.DT_INTERNACAO.toString())
				.append("        , FE.DATA_SAIDA         AS ")
				.append(ResumoAIHEmLoteVO.Fields.DT_SAIDA.toString())
				.append("        , FE.CID_PRIMARIO       AS ")
				.append(ResumoAIHEmLoteVO.Fields.CID_PRIMARIO.toString())
				.append("        , FE.CID_SECUNDARIO     AS ")
				.append(ResumoAIHEmLoteVO.Fields.CID_SECUNDARIO.toString())
				.append("        , FE.MOTIVO_COBRANCA    AS ")
				.append(ResumoAIHEmLoteVO.Fields.MOTIVO_COBRANCA.toString())
				.append("        , FE.NUMERO_AIH_ANTERIOR  AS ")
				.append(ResumoAIHEmLoteVO.Fields.NUMERO_AIH_ANTERIOR.toString())
				.append("        , FE.NUMERO_AIH_POSTERIOR AS ")
				.append(ResumoAIHEmLoteVO.Fields.NUMERO_AIH_POSTERIOR.toString())
				.append("        , (CASE WHEN FE.EXCLUSAO_CRITICA IS NULL THEN '0' ELSE FE.EXCLUSAO_CRITICA END) AS ")
				.append(ResumoAIHEmLoteVO.Fields.EXCLUSAO_CRITICA.toString())
				.append("        , (CASE WHEN CTH.IND_IMPRESSAO_ESPELHO = 'S' THEN 'Y' ELSE CTH.IND_IMPRESSAO_ESPELHO END) AS ")
				.append(ResumoAIHEmLoteVO.Fields.IND_IMPRIME_ESPELHO.toString())
				.append("        , CTH.USUARIO_PREVIA        AS ")
				.append(ResumoAIHEmLoteVO.Fields.USUARIO_PREVIA.toString())
				.append("        , CTH.DATA_PREVIA           AS ")
				.append(ResumoAIHEmLoteVO.Fields.DT_PREVIA.toString())
				.append("        , CTH.CTH_SEQ_REAPRESENTADA AS ")
				.append(ResumoAIHEmLoteVO.Fields.CTH_SEQ_REAPRESENTADA.toString())
				.append("        , FE.VALOR_SH_REALIZ        AS ")
				.append(ResumoAIHEmLoteVO.Fields.VALOR_SH.toString())
				.append("        , FE.VALOR_SP_REALIZ        AS ")
				.append(ResumoAIHEmLoteVO.Fields.VALOR_SP.toString())
				.append("        , FE.VALOR_SADT_REALIZ      AS ")
				.append(ResumoAIHEmLoteVO.Fields.VALOR_SADT.toString())
				.append("        , IPHR.DESCRICAO            AS ")
				.append(ResumoAIHEmLoteVO.Fields.DESCRICAO_PROCEDIMENTO_REALZ.toString())
				.append("        , IPHS.DESCRICAO            AS ")
				.append(ResumoAIHEmLoteVO.Fields.DESCRICAO_PROCEDIMENTO_SOLIC.toString())
				.append("        , (CASE WHEN CTH.VALOR_ACOMP IS NULL THEN 0 ELSE CTH.VALOR_ACOMP END) AS ")
				.append(ResumoAIHEmLoteVO.Fields.VALOR_ACOMP.toString())
				.append("        , PAC.CODIGO                AS ")
				.append(ResumoAIHEmLoteVO.Fields.COD_PACIENTE.toString())
				.append("        , FE.DCI_CPE_MES            AS ")
				.append(ResumoAIHEmLoteVO.Fields.DCI_CPE_MES.toString())
				.append("        , FE.DCI_CPE_ANO            AS ")
				.append(ResumoAIHEmLoteVO.Fields.DCI_CPE_ANO.toString())
				.append("        , FE.ESPECIALIDADE_AIH      AS ")
				.append(ResumoAIHEmLoteVO.Fields.ESPECIALIDADE_AIH.toString())
				.append("        , FE.ENFERMARIA             AS ")
				.append(ResumoAIHEmLoteVO.Fields.ENFERMARIA.toString())
				.append("        , FE.IPH_PHO_SEQ_REALIZ     AS ")
				.append(ResumoAIHEmLoteVO.Fields.PHO_SEQ_REALZ.toString())
				.append("        , IPHR.SEQ                  AS ")
				.append(ResumoAIHEmLoteVO.Fields.SEQ_REALZ.toString())
				.append("        , FE.CRIADO_POR		    AS ")
				.append(ResumoAIHEmLoteVO.Fields.CRIADO_POR.toString())
				.append("        , FE.CRIADO_EM    	    AS ")
				.append(ResumoAIHEmLoteVO.Fields.CRIADO_EM.toString())

				.append("        , (SELECT FCF.DESCRICAO FROM AGH.")
				.append(FatCaractFinanciamento.class.getAnnotation(Table.class).name())
				.append(" FCF INNER JOIN AGH.")
				.append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name())
				.append(" IPH1_  ON FCF.SEQ=IPH1_.FCF_SEQ WHERE IPH1_.SEQ=IPHR.SEQ AND IPH1_.PHO_SEQ= IPHR.PHO_SEQ AND IPH1_.COD_TABELA=FE.IPH_COD_SUS_REALIZ ")
				.append(" ) AS ").append(ResumoAIHEmLoteVO.Fields.DETALHE1.toString())

				.append("        , ( SELECT MRC.DESCRICAO FROM AGH.")
				.append(FatMotivoRejeicaoConta.class.getAnnotation(Table.class).name()).append(" MRC WHERE MRC.SEQ = CTH.RJC_SEQ ")
				.append(" ) AS ").append(ResumoAIHEmLoteVO.Fields.DETALHE2.toString())

				.append("        , ( SELECT ESP.NOME_REDUZIDO FROM AGH.").append(AghEspecialidades.class.getAnnotation(Table.class).name())
				.append(" ESP WHERE ESP.SEQ = CTH.ESP_SEQ ").append(" )     AS ").append(ResumoAIHEmLoteVO.Fields.DETALHE3.toString())

				.append(" FROM ").append("       AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name())
				.append(" IPHR ").append("     , AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name())
				.append(" IPHS ").append("     , AGH.").append(FatEspelhoAih.class.getAnnotation(Table.class).name()).append(" FE ")
				.append("      LEFT JOIN AGH.").append(AghClinicas.class.getAnnotation(Table.class).name())
				.append("  AC  ON AC.CODIGO = FE.ESPECIALIDADE_AIH ").append("      LEFT JOIN AGH.")
				.append(AipPacientes.class.getAnnotation(Table.class).name()).append(" PAC ON PAC.PRONTUARIO = FE.PAC_PRONTUARIO ")
				.append("     , AGH.").append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(" CTH ")

				.append(" WHERE 1=1 ").append("       AND FE.CTH_SEQ = CTH.SEQ ").append("       AND IPHS.PHO_SEQ = FE.IPH_PHO_SEQ_SOLIC ")
				.append("       AND IPHS.SEQ = FE.IPH_SEQ_SOLIC ").append("       AND IPHR.PHO_SEQ = FE.IPH_PHO_SEQ_REALIZ ")
				.append("       AND IPHR.SEQ = FE.IPH_SEQ_REALIZ  ").append(" 	   AND FE.SEQP = :prmSeqP ")
				.append("       AND CTH.IND_SITUACAO in (:prmIndSituacao) ");

		if (cthSeq != null) {
			sql.append(" AND FE.CTH_SEQ = :prmCthSeq ");

		} else {
			sql.append(" AND CTH.DT_ENCERRAMENTO >= :prmDtEncerramentoInicial ")
					.append(" AND CTH.DT_ENCERRAMENTO <= :prmDtEncerramentoFinal ")
					.append(" AND ( IND_AUTORIZADO_SMS is null or IND_AUTORIZADO_SMS = :prmIndAutorizadoSMS )");
		}

		/*
		 * 
		 * and (:P_CTH_SEQ is not null or ( :P_CTH_SEQ is null and
		 * CTH.DT_ENCERRAMENTO >= to_date('01/05/2011 09:36:00', 'dd/mm/yyyy
		 * hh24:mi:ss') and CTH.DT_ENCERRAMENTO <= to_date('29/09/2011
		 * 09:36:00', 'dd/mm/yyyy hh24:mi:ss') ) )
		 * 
		 * AND (:P_CTH_SEQ is not null or (:P_CTH_SEQ is null and
		 * NVL(IND_AUTORIZADO_SMS,'N') = :P_AUTORIZADA) )
		 */

		if (StringUtils.isNotBlank(iniciaisPaciente)) {
			sql.append(" AND UPPER(SUBSTR(PAC.NOME,1,1)) IN (:prm_iniciais_paciente)");
		}

		if (reapresentada) {
			sql.append(" and CTH.CTH_SEQ_REAPRESENTADA IS NOT NULL ");
		} else {
			sql.append(" and CTH.CTH_SEQ_REAPRESENTADA IS NULL ");
		}

		sql.append(" ORDER BY        ").append("       FE.PAC_NOME, FE.CTH_SEQ, FE.SEQP ");

		SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("prmSeqP", 1);

		final String[] situacoes = { DominioSituacaoConta.E.toString(), DominioSituacaoConta.O.toString(),
				DominioSituacaoConta.R.toString() };
		query.setParameterList("prmIndSituacao", situacoes);

		if (cthSeq != null) {
			query.setInteger("prmCthSeq", cthSeq);

		} else {
			query.setTimestamp("prmDtEncerramentoInicial", dtInicial);
			query.setTimestamp("prmDtEncerramentoFinal", dtFinal);
			query.setString("prmIndAutorizadoSMS", indAutorizadoSSM ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
		}

		if (iniciaisPaciente != null && StringUtils.isNotBlank(iniciaisPaciente)) {
			final List<String> lst = new ArrayList<String>();
			for (char a : iniciaisPaciente.toCharArray()) {
				lst.add(Character.toString(a));
			}

			query.setParameterList("prm_iniciais_paciente", lst);
		}

		final List<ResumoAIHEmLoteVO> vos = query.addScalar(ResumoAIHEmLoteVO.Fields.CON_COD_CENTRAL.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DAU_SENHA.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CTH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.SEQ_P.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CODIGO_DCIH.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.NUMERO_AIH.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.NOME.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DT_NASCIMENTO.toString(), DateType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.PAC_SEXO.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.TAH_SEQ.toString(), ShortType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.PRONTUARIO.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.ESPECIALIDADE.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.LEITO.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.IPH_COD_SUS_SOLIC.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.TCI_COD_SUS.toString(), ByteType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.IPH_COD_SUS_REALZ.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CPF_MEDICO_AUDITOR.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DT_INTERNACAO.toString(), DateType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DT_SAIDA.toString(), DateType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CID_PRIMARIO.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CID_SECUNDARIO.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.MOTIVO_COBRANCA.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.NUMERO_AIH_ANTERIOR.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.NUMERO_AIH_POSTERIOR.toString(), LongType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.EXCLUSAO_CRITICA.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.IND_IMPRIME_ESPELHO.toString(), YesNoType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.USUARIO_PREVIA.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DT_PREVIA.toString(), DateType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CTH_SEQ_REAPRESENTADA.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.VALOR_SH.toString(), BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.VALOR_SP.toString(), BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.VALOR_SADT.toString(), BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DESCRICAO_PROCEDIMENTO_REALZ.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DESCRICAO_PROCEDIMENTO_SOLIC.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.VALOR_ACOMP.toString(), BigDecimalType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.COD_PACIENTE.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DCI_CPE_MES.toString(), ByteType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DCI_CPE_ANO.toString(), ShortType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.ESPECIALIDADE_AIH.toString(), ByteType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.ENFERMARIA.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.PHO_SEQ_REALZ.toString(), ShortType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.SEQ_REALZ.toString(), IntegerType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DETALHE1.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DETALHE2.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.DETALHE3.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CRIADO_POR.toString(), StringType.INSTANCE)
				.addScalar(ResumoAIHEmLoteVO.Fields.CRIADO_EM.toString(), DateType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(ResumoAIHEmLoteVO.class)).list();

		final List<ResumoAIHEmLoteVO> result = new ArrayList<ResumoAIHEmLoteVO>();

		final List<Integer> seqs = new ArrayList<Integer>();
		for (ResumoAIHEmLoteVO vo : vos) {
			if (vo != null) {
				vo.setProntuarioFormatado(formataProntuarioBarcode(vo.getProntuario()));
				seqs.add(vo.getCthSeq());

				if (vo.getIndImprimeEspelho() != null && vo.getIndImprimeEspelho()) {
					vo.setReimpressao("REIMPRESSÃO");
				}

				if (vo.getDciCpeMes() != null && vo.getDciCpeAno() != null) {
					String mesAno = StringUtils.leftPad(vo.getDciCpeMes().toString(), 2, "0") + vo.getDciCpeAno().toString();
					vo.setDtApresentacao(mesAno);
				}

				if (vo.getDciCpeAno() != null) {
					String modulo = vo.getDciCpeAno() + "02"
							+ StringUtils.leftPad(((vo.getCthSeq() != null) ? vo.getCthSeq().toString() : "0"), 6, "0");
					Integer dv = CoreUtil.calculaModuloOnze(Long.valueOf(modulo));
					vo.setDv(dv);

				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					Date dataAtual = new Date();
					String modulo = sdf.format(dataAtual) + "02"
							+ StringUtils.leftPad(((vo.getCthSeq() != null) ? vo.getCthSeq().toString() : "0"), 6, "0");
					Integer dv = CoreUtil.calculaModuloOnze(Long.valueOf(modulo));
					vo.setDv(dv);
				}
			}
		}

		Map<Integer, List<ResumoAIHEmLoteServicosVO>> resultServisos = null;

		if (seqs != null && !seqs.isEmpty()) {
			resultServisos = getFatItensProcedHospitalarDAO().listarAtosMedicosResumoAihEmLote(seqs);

			for (ResumoAIHEmLoteVO vo : vos) {
				vo.setListaServicos(resultServisos.get(vo.getCthSeq()));

				result.add(vo);
			}
		}

		return result;
	}
	
	/**
	 * Formata o prontuário da paciente para ficar com 12 digitos complementando com zeros
	 * function CF_BARRASFormula 
	 * @param prontuarioPaciente
	 * @return
	 */
	private String formataProntuarioBarcode(Integer prontuarioPaciente){
		String prontFormatado = StringUtils.leftPad(String.valueOf(prontuarioPaciente), 9, '0');
		prontFormatado = StringUtils.rightPad(prontFormatado, 12, '0');
		return prontFormatado;
	}
	
	private FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	public FatEspelhoAih obterPorCthSeqSeqp(Integer cthSeq, Integer seqp) {

		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorCthSeqp(cthSeq, seqp);

		return (FatEspelhoAih) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * ORADB: C_BUSCA_CONTA
	 */
	@SuppressWarnings("unchecked")
	public List<CursorBuscaContaVO> obterResultadoCursorBuscaConta( final Date  dtHrInicio, final Integer mes, final Integer ano,
													  final Boolean indAutorizadoSSM, final DominioSituacaoConta indSituacao,
													  final Date dtEncInicial, final Date dtEncFinal, final DominioModuloCompetencia modulo){
		
		final StringBuilder sql = new StringBuilder(1600);
		
		// Marina 28/08/2008
		sql.append(" SELECT ")
			.append("  ATD.").append(AghAtendimentos.Fields.UNF_SEQ.name())
							.append(" as ").append(AghAtendimentos.Fields.UNF_SEQ.name()).append(", ")
			.append("  FCF.").append(FatCaractFinanciamento.Fields.CODIGO.name())
							 .append(" as ").append(FatCaractFinanciamento.Fields.CODIGO.name()).append(", ")
			.append("  EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name())
		   					.append(" as ").append(CursorBuscaContaVO.Fields.CTH_SEQ.toString()).append(", ")
		   	.append("  EAI.").append(FatEspelhoAih.Fields.DATA_INTERNACAO.name())
		   					.append(" as ").append(CursorBuscaContaVO.Fields.DATA_INTERNACAO.toString()).append(", ")
		   	.append("  EAI.").append(FatEspelhoAih.Fields.DATA_SAIDA.name())
		   					.append(" as ").append(CursorBuscaContaVO.Fields.DATA_SAIDA.toString()).append(", ")
		   	.append(" CASE WHEN EAI.").append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name()).append(" = 5 THEN 87")
		   							  .append(" ELSE EAI.").append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name())		   	
		    .append(" END as ").append(CursorBuscaContaVO.Fields.ESPECIALIDADE_AIH.toString()).append(", ")
		   
		   .append(obterCoalesceCBuscaConta()).append(" as ")
		   .append(CursorBuscaContaVO.Fields.VALOR_CONTA.toString())
		   .append(", ")

		   .append("  EAI.").append(FatEspelhoAih.Fields.IND_BCO_CAPAC.name())
		   					.append(" as ").append(CursorBuscaContaVO.Fields.IND_BCO_CAPAC.toString()).append(", ")
		   
		   .append(obterCaseCBuscaConta())
		   
		   .append(" FROM ")
		   .append("    AGH.").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF, ")
		   .append("    AGH.").append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(" ATD, ")
		   .append("    AGH.").append(FatContasInternacao.class.getAnnotation(Table.class).name()).append(" COI, ")
		   .append("    AGH.").append(FatEspelhoAih.class.getAnnotation(Table.class).name()).append(" EAI, ")
		   .append("    AGH.").append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(" CTH, ")
		   .append("    AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH, ")
		   .append("    AGH.").append(FatCaractFinanciamento.class.getAnnotation(Table.class).name()).append(" FCF ")
		   
		   .append(" WHERE 1=1 ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_ANO.name()).append(" = :P_CPE_ANO ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_MES.name()).append(" = :P_CPE_MES ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.name()).append(" = :P_DT_HR_INICIO ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_MODULO.name()).append(" = :P_CPE_MODULO ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.SEQP.name()).append(" = :P_SEQP ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(" = ")
		   					   .append(" COI.").append(FatContasInternacao.Fields.CTH_SEQ.name())
		   
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.name()).append(" = ")
		   					   .append(" EAI.").append(FatEspelhoAih.Fields.DCI_CODIGO_DCIH.name())
		   					   
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.SEQ.name()).append(" = ")
		   					   .append(" EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name())
		   
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.name()).append(" = :P_IND_AUTORIZADO_SMS ")
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.IND_SITUACAO.name()).append(" = :P_IND_SITUACAO ")
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.NRO_AIH.name()).append(" IS NOT NULL ")
		   
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = ")
			   				   .append(" EAI.").append(FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.name())
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = ")
			   				   .append(" EAI.").append(FatEspelhoAih.Fields.IPH_SEQ_REALIZ.name())
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.COD_TABELA.name()).append(" = ")
			   				   .append(" EAI.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name())
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(" = ")
			   				   .append(" FCF.").append(FatCaractFinanciamento.Fields.SEQ.name())
			 
	   	   .append(" AND COI.").append(FatContasInternacao.Fields.INT_SEQ.name()).append(" = ")
	   							.append(" ATD.").append(AghAtendimentos.Fields.INT_SEQ.name())
	   							
	   	   .append(" AND ATD.").append(AghAtendimentos.Fields.UNF_SEQ.name()).append(" = ")
	   	   					   .append(" UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
	   							
//	   	   	and unf.unf_seq = cnes.unf_seq(+) conforme conversado com Marina, ignorar as partes onde esta tabela(agh_cnes) é chamada - ndeitch
	   	   					   
		   .append(" AND EXISTS( ")
		   						.append("SELECT 1 FROM AGH.").append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(" CTH_IN ")
		   						.append(" WHERE CTH_IN.").append(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.name())
		   										  	     .append(" = EAI.").append(FatEspelhoAih.Fields.DCI_CODIGO_DCIH.name())
					  	        .append("  AND CTH_IN.").append(FatContasHospitalares.Fields.DT_ENCERRAMENTO.name()).append(" >= :P_DT_ENC_INICIAL ")
					  	        .append("  AND CTH_IN.").append(FatContasHospitalares.Fields.DT_ENCERRAMENTO.name()).append(" <= :P_DT_ENC_FINAL ")
		   .append(" )")
		   
		   .append(" UNION ")
		   .append(" SELECT ")
		   .append(" UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
		   				   .append(" as ").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).append(", ")
		   .append(" FCF.").append(FatCaractFinanciamento.Fields.CODIGO.name())
		   				   .append(" as ").append(FatCaractFinanciamento.Fields.CODIGO.toString()).append(", ")
		   .append(" EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name())
		   				   .append(" as ").append(CursorBuscaContaVO.Fields.CTH_SEQ.toString()).append(", ")
		   .append(" EAI.").append(FatEspelhoAih.Fields.DATA_INTERNACAO.name())
		   				   .append(" as ").append(FatEspelhoAih.Fields.DATA_INTERNACAO.toString()).append(", ")
		   .append(" EAI.").append(FatEspelhoAih.Fields.DATA_SAIDA.name())
		   				   .append(" as ").append(FatEspelhoAih.Fields.DATA_SAIDA.toString()).append(", ")
		   				   
		   	.append(" CASE WHEN EAI.").append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name()).append(" = 5 THEN 87")
		   							  .append(" ELSE EAI.").append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name())	
		    .append(" END as ").append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.toString()).append(", ")
           
           .append(obterCoalesceCBuscaConta()).append(" as ")
		   .append(CursorBuscaContaVO.Fields.VALOR_CONTA.toString())
		   .append(", ")
           
           .append(" EAI.").append(FatEspelhoAih.Fields.IND_BCO_CAPAC.name())
           				   .append(" as ").append(FatEspelhoAih.Fields.IND_BCO_CAPAC.toString()).append(", ")
		   	
           .append(obterCaseCBuscaConta())
           
           .append(" FROM ")
           .append(" AGH.").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF")
           .append(" RIGHT JOIN ")
           .append(" AGH.").append(FatDadosContaSemInt.class.getAnnotation(Table.class).name()).append(" DCS")
           					.append(" ON ").append(" UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).append(" = ")
           					.append(" DCS.").append(FatDadosContaSemInt.Fields.UNF_SEQ.name()).append(", ")
           .append(" AGH.").append(FatContasInternacao.class.getAnnotation(Table.class).name()).append(" COI").append(", ")
           .append(" AGH.").append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(" CTH").append(", ")
           .append(" AGH.").append(FatEspelhoAih.class.getAnnotation(Table.class).name()).append(" EAI").append(", ")
           .append("    AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH").append(", ")
           .append(" AGH.").append(FatCaractFinanciamento.class.getAnnotation(Table.class).name()).append(" FCF")
           
           .append(" WHERE ")
           .append("     EAI.").append(FatEspelhoAih.Fields.DCI_CPE_ANO.name()).append(" = :P_CPE_ANO ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_MES.name()).append(" = :P_CPE_MES ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.name()).append(" = :P_DT_HR_INICIO ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.DCI_CPE_MODULO.name()).append(" = :P_CPE_MODULO ")
		   .append(" AND EAI.").append(FatEspelhoAih.Fields.SEQP.name()).append(" = :P_SEQP ")
		   
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.SEQ.name()).append(" = ")
		   					   .append(" EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name())
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.name()).append(" = ")
		   					   .append(" EAI.").append(FatEspelhoAih.Fields.DCI_CODIGO_DCIH.name())
           .append(" AND CTH.").append(FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.name()).append(" = :P_IND_AUTORIZADO_SMS ")
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.IND_SITUACAO.name()).append(" = :P_IND_SITUACAO ")
		   .append(" AND CTH.").append(FatContasHospitalares.Fields.NRO_AIH.name()).append(" IS NOT NULL ")
		   
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = ")
			   				   .append(" EAI.").append(FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.name())
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = ")
			   				   .append(" EAI.").append(FatEspelhoAih.Fields.IPH_SEQ_REALIZ.name())
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.COD_TABELA.name()).append(" = ")
			   				   .append(" EAI.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name())
		   .append(" AND IPH.").append(FatItensProcedHospitalar.Fields.FCF_SEQ.name()).append(" = ")
			   				   .append(" FCF.").append(FatCaractFinanciamento.Fields.SEQ.name())
		   
		   .append(" AND DCS.").append(FatDadosContaSemInt.Fields.SEQ.name()).append(" = ")
		   					   .append(" COI.").append(FatContasInternacao.Fields.DCS_SEQ.name())
		   .append(" AND COI.").append(FatContasInternacao.Fields.CTH_SEQ.name()).append(" = ")
		   					   .append(" CTH.").append(FatContasHospitalares.Fields.SEQ.name())
		   .append(" AND EXISTS ( ")
		   					   .append("SELECT 1 FROM AGH.").append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(" CTH_IN ")
		   						.append(" WHERE CTH_IN.").append(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.name())
		   										  	     .append(" = EAI.").append(FatEspelhoAih.Fields.DCI_CODIGO_DCIH.name())
					  	        .append("  AND CTH_IN.").append(FatContasHospitalares.Fields.DT_ENCERRAMENTO.name()).append(" >= :P_DT_ENC_INICIAL ")
					  	        .append("  AND CTH_IN.").append(FatContasHospitalares.Fields.DT_ENCERRAMENTO.name()).append(" <= :P_DT_ENC_FINAL ")
           
		   .append(") ORDER BY ").append(FatCaractFinanciamento.Fields.CODIGO.name());
		
		SQLQuery query = createSQLQuery(sql.toString());
		
		query.setInteger("P_SEQP", 1);
		query.setString("P_IND_AUTORIZADO_SMS", indAutorizadoSSM ? DominioSimNao.S.toString(): DominioSimNao.N.toString() );
		query.setString("P_IND_SITUACAO", indSituacao.toString() );
		query.setDate("P_DT_ENC_INICIAL", dtEncInicial);
		
						// para suprimir CTH_IN.DT_ENCERRAMENTO  <= ? + 1
		query.setDate("P_DT_ENC_FINAL", DateUtil.adicionaDias(dtEncFinal, 1));
		query.setString("P_CPE_MODULO", modulo.toString());
		query.setTimestamp("P_DT_HR_INICIO", dtHrInicio);
		query.setInteger("P_CPE_MES", mes); 
		query.setInteger("P_CPE_ANO", ano);
		
		final List<CursorBuscaContaVO> vos =  query.addScalar(CursorBuscaContaVO.Fields.CTH_SEQ.toString(), IntegerType.INSTANCE)
												   .addScalar(CursorBuscaContaVO.Fields.DATA_INTERNACAO.toString(), DateType.INSTANCE)
												   .addScalar(CursorBuscaContaVO.Fields.DATA_SAIDA.toString(), DateType.INSTANCE)
												   .addScalar(CursorBuscaContaVO.Fields.ESPECIALIDADE_AIH.toString(), ByteType.INSTANCE)
												   .addScalar(CursorBuscaContaVO.Fields.VALOR_CONTA.toString(), BigDecimalType.INSTANCE)
												   .addScalar(CursorBuscaContaVO.Fields.IND_BCO_CAPAC.toString(), StringType.INSTANCE)
												   .addScalar(CursorBuscaContaVO.Fields.ORDEM.toString(), IntegerType.INSTANCE)
												  
												  .setResultTransformer(Transformers.aliasToBean(CursorBuscaContaVO.class)).list();
		
		return vos;
	}
	
	/**
	 * Método criado devido a repetitivas declarações do SQL abaixo
	 * na criação da consulta
	 * @author ndeitch 
	 * @return {@link StringBuilder}
	 */
	private StringBuilder obterCaseCBuscaConta(){
		StringBuilder sb = new StringBuilder(150);
		sb.append("  CASE WHEN FCF.")
				.append(FatCaractFinanciamento.Fields.CODIGO.name())
				.append(" = 'EST' THEN 1 ").append("       WHEN FCF.")
				.append(FatCaractFinanciamento.Fields.CODIGO.name())
				.append(" = 'ALT' THEN 2 ").append("       WHEN FCF.")
				.append(FatCaractFinanciamento.Fields.CODIGO.name())
				.append(" = 'MDO' THEN 3 ").append("  	   ELSE 99 ")
				.append("  END ").append(" as ")
				.append(CursorBuscaContaVO.Fields.ORDEM.toString());
		return sb;
	}
	
	/**
	 * Método criado devido a repetitivas declarações do SQL abaixo
	 * na criação da consulta
	 * @author ndeitch 
	 * @return {@link StringBuilder}
	 */
	private StringBuilder obterCoalesceCBuscaConta(){
		
		StringBuilder sb = new StringBuilder(300);
		sb.append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_SH.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_UTI.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_UTIE.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_SP.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_ACOMP.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_RN.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_SADT.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_HEMAT.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_TRANSP.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_OPM.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_ANESTESISTA.name())
				.append(",0) + ").append("COALESCE( CTH.")
				.append(FatContasHospitalares.Fields.VALOR_PROCEDIMENTO.name())
				.append(",0)");
		return sb;
	}

	private String getPrimeiraParteSQLCursorCEAI() {
		final StringBuilder sql = new StringBuilder(300);

		sql.append(" SELECT ").append("   EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(" AS ")
				.append(CursorCEAIVO.Fields.CTH_SEQ.toString())

				.append("  ,EAI.").append(FatEspelhoAih.Fields.SEQP.name()).append(" AS ").append(CursorCEAIVO.Fields.SEQP.toString())

				// nu_lote
				.append("  ,COALESCE( EAI.").append(FatEspelhoAih.Fields.DCI_CODIGO_DCIH.name()).append(", '0') ").append(" AS  ")
				.append(CursorCEAIVO.Fields.DCI_CODIGO_DCIH.toString())

				// qt_lote
				.append("  ,'0' AS ").append(CursorCEAIVO.Fields.QT_LOTE.toString());

		// apres_lote
		if (isOracle()) {
			sql.append(" ,To_Char(").append("          Add_Months(").append("                      To_Date(Lpad(TRIM(To_Char(EAI.")
					.append(FatEspelhoAih.Fields.DCI_CPE_MES.name()).append(") ),2,'0')").append("							    || To_Char(EAI.")
					.append(FatEspelhoAih.Fields.DCI_CPE_ANO.name()).append("),'MMYYYY'").append("                             ),1")
					.append("                     )").append("       	  ,'YYYYMM' ").append("        ) AS ")
					.append(CursorCEAIVO.Fields.APRES_LOTE.toString());

		} else {
			sql.append(" ,To_Char(").append("          ( To_Date(").append("                     Lpad(TRIM(To_Char(EAI.")
					.append(FatEspelhoAih.Fields.DCI_CPE_MES.name()).append(",'99')),2,'0') ||").append("					 trim(To_Char(EAI.")
					.append(FatEspelhoAih.Fields.DCI_CPE_ANO.name()).append(",'9999')),").append("                    'mmYYYY'")
					.append("                   ) + interval '1 month' ").append("       	  ),'YYYYMM' ").append("        ) AS ")
					.append(CursorCEAIVO.Fields.APRES_LOTE.toString());
		}

		// seq_lote
		sql.append(" ,'0' AS ")
				.append(CursorCEAIVO.Fields.SEQ_LOTE.toString())

				// org_emis_aih
				.append(" ,Coalesce(:P_ORG_LOC_REC,'0') AS ")
				.append(CursorCEAIVO.Fields.ORG_EMIS_AIH.toString())

				// CNES do HCPA
				.append(" ,COALESCE(:P_CNES_HCPA,0) AS ")
				.append(CursorCEAIVO.Fields.CNES_HCPA.toString())

				// Municipio do HCPA
				.append(" ,:P_MUNICIPIO_INSTITUICAO AS ")
				.append(CursorCEAIVO.Fields.MUNICIPIO_INSTITUICAO.toString())

				// AIH
				.append("  ,COALESCE( EAI.").append(FatEspelhoAih.Fields.NUMERO_AIH.name()).append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NUMERO_AIH.toString())

				// Tipo AIH
				.append("  ,COALESCE( EAI.").append(FatEspelhoAih.Fields.TAH_SEQ.name()).append(", 0) ").append(" AS ")
				.append(CursorCEAIVO.Fields.TAH_SEQ.toString())

				// Esp da AIH
				.append("  ,COALESCE( EAI.").append(FatEspelhoAih.Fields.ESPECIALIDADE_DCIH.name()).append(", 0) ").append(" AS ")
				.append(CursorCEAIVO.Fields.ESPECIALIDADE_DCIH.toString());

		return sql.toString();
	}

	private String getSegundaParteSQLCursorCEAI() {
		final StringBuilder sql = new StringBuilder(800);

		// lpad(to_char(fatc_busca_modalidade(iph_pho_seq_realiz,
		// iph_seq_realiz,data_internacao, data_saida)),2,'0')

		// CAMPOS NECESSÃ�RIOS PARA FUNCTION Fatc_Busca_Modalidade
		sql.append("  ,EAI.")
				.append(FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.name())
				.append(" AS ")
				.append(CursorCEAIVO.Fields.IPH_PHO_SEQ_REALIZ.toString())
				.append("  ,EAI.")
				.append(FatEspelhoAih.Fields.IPH_SEQ_REALIZ.name())
				.append(" AS ")
				.append(CursorCEAIVO.Fields.IPH_SEQ_REALIZ.toString())
				.append("  ,EAI.")
				.append(FatEspelhoAih.Fields.DATA_INTERNACAO.name())
				.append(" AS ")
				.append(CursorCEAIVO.Fields.DATA_INTERNACAO_TIPO_DATE.toString())
				.append("  ,EAI.")
				.append(FatEspelhoAih.Fields.DATA_SAIDA.name())
				.append(" AS ")
				.append(CursorCEAIVO.Fields.DATA_SAIDA_TIPO_DATE.toString())

				// seq da AIH5
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NRO_SEQAIH5.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NRO_SEQAIH5.toString())

				// AIH Post
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NUMERO_AIH_POSTERIOR.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NUMERO_AIH_POSTERIOR.toString())

				// AIH ant
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NUMERO_AIH_ANTERIOR.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NUMERO_AIH_ANTERIOR.toString())

				// Data AIH
				.append("  ,COALESCE( TO_CHAR( EAI.")
				.append(FatEspelhoAih.Fields.AIH_DTHR_EMISSAO.name())
				.append(", 'YYYYMMDD'),'0') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.AIH_DTHR_EMISSAO.toString())

				// Data Int
				.append("  ,COALESCE( TO_CHAR( EAI.")
				.append(FatEspelhoAih.Fields.DATA_INTERNACAO.name())
				.append(", 'YYYYMMDD'),'0') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.DATA_INTERNACAO.toString())

				// Data saida
				.append("  ,COALESCE( TO_CHAR( EAI.")
				.append(FatEspelhoAih.Fields.DATA_SAIDA.name())
				.append(", 'YYYYMMDD'),'0') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.DATA_SAIDA.toString())

				// Proc solicitado
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.IPH_COD_SUS_SOLIC.toString())

		// MudanÃ§a
				.append("  ,CASE WHEN COALESCE( EAI.").append(FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.name()).append(", 0) = ")
				.append("            COALESCE( EAI.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(", 0) ")
				.append("    THEN '2' ")
				.append("    ELSE '1' ")
				.append("  END AS ")
				.append(CursorCEAIVO.Fields.MUDANCA.toString())

				// Proc realizado
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name())
				.append(", 0) ")
				.append("AS ")
				.append(CursorCEAIVO.Fields.IPH_COD_SUS_REALIZ.toString())

		// carÃ¡ter internaÃ§Ã£o
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.TCI_COD_SUS.name()).append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.TCI_COD_SUS.toString())

				// motivo de saÃ­da ( Pos final 185)
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.MOTIVO_COBRANCA.name()).append(", '0') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.MOTIVO_COBRANCA.toString())

				// Marina 18/01/2012

				// tipo documento
				.append(" ,CASE WHEN GREATEST(TO_CHAR(EAI.").append(FatEspelhoAih.Fields.DATA_INTERNACAO.name())
				.append(",'YYYYMM'),'201112') = '201112' THEN 1 ELSE 2 END AS ").append(CursorCEAIVO.Fields.TIPO_DOCUMENTO.toString());

		String p1 = " ,CASE WHEN GREATEST(TO_CHAR(EAI." + FatEspelhoAih.Fields.DATA_INTERNACAO.name() + ",'YYYYMM'),'201112') = '201112'"
				+ "         THEN COALESCE( CAST(";
		String p2 = " AS VARCHAR(15)), '0')" + "         ELSE" + "             COALESCE(" + "                      ("
				+ "                        CASE WHEN ";
		String p3 = " IS NULL " + "                          THEN NULL" + "                          ELSE ("
				+ "                                CASE WHEN LENGTH( ";
		String p4 = " ) = 15" + "                                 THEN CAST(";
		String p5 = "                                  AS VARCHAR(15)) ELSE" + "                                     (SELECT  TII."
				+ RapPessoaTipoInformacoes.Fields.VALOR.name() + "                                       FROM AGH."
				+ RapPessoaTipoInformacoes.class.getAnnotation(Table.class).name() + " TII"
				+ "                                           ,AGH." + RapPessoasFisicas.class.getAnnotation(Table.class).name() + " PES"
				+ "                                       WHERE PES." + RapPessoasFisicas.Fields.CODIGO.name() + " = TII."
				+ RapPessoaTipoInformacoes.Fields.PES_CODIGO.name() + "                                         AND TII."
				+ RapPessoaTipoInformacoes.Fields.TII_SEQ.name() + " = 9" + "                                         AND PES."
				+ RapPessoasFisicas.Fields.CPF.name() + "= ";
		String p6 = "                                     )" + "                                 END" + "                                )"
				+ "                            END" + "                       ), '0'" + "                      )" + "           END";
		String p7 = " AS VARCHAR(15)), '0')" + "         ELSE" + "             COALESCE( CAST(";
		String p8 = " AS VARCHAR(15)), '0') END";

		String p9 = " TO_CHAR( ";
		String p10 = " , '999999999999999') ";

		// cns medico auditor
		sql.append(p1).append(" NULL").append(p7).append(" EAI.").append(FatEspelhoAih.Fields.CNS_MEDICO_AUDITOR.name()).append(p8)
				.append(" AS ").append(CursorCEAIVO.Fields.CNS_MEDICO_AUDITOR.toString());

		// cpf mÃ©dico solicit.
		sql.append(p1).append(" EAI.").append(FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.name()).append(p2).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.name()).append(p3).append(p9).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.name()).append(p10).append(p4).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.name()).append(p5).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.name()).append(p6).append(" AS ")
				.append(CursorCEAIVO.Fields.CPF_MEDICO_SOLIC_RESPONS.toString());

		// cpf diretor clÃ­nico
		sql.append(p1).append(" :P_CPF_DIR_CLI ").append(p2).append(" :P_CPF_DIR_CLI ").append(p3).append(p9).append(" :P_CPF_DIR_CLI ")
				.append(p10).append(p4).append(" :P_CPF_DIR_CLI ").append(p5).append(" :P_CPF_DIR_CLI ").append(p6).append(" AS ")
				.append(CursorCEAIVO.Fields.CPF_DIR_CLI.toString());

		sql.append(p1).append(" EAI.").append(FatEspelhoAih.Fields.CPF_MEDICO_AUDITOR.name()).append(p2).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_AUDITOR.name()).append(p3).append(p9).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_AUDITOR.name()).append(p10).append(p4).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_AUDITOR.name()).append(p5).append(" EAI.")
				.append(FatEspelhoAih.Fields.CPF_MEDICO_AUDITOR.name()).append(p6).append(" AS ")
				.append(CursorCEAIVO.Fields.CPF_MEDICO_AUDITOR.toString());

		return sql.toString();
	}

	private String getTerceiraParteSQLCursorCEAI() {
		final StringBuilder sql = new StringBuilder(1000);

		// DiagnÃ³stico Principal
		sql.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.CID_PRIMARIO.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.CID_PRIMARIO.toString())

				// DiagnÃ³stico SecundÃ¡rio
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.CID_SECUNDARIO.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.CID_SECUNDARIO.toString())

				// Cid Ã“bito
				.append(" ,Case When Cast(Substr( EAI.")
				.append(FatEspelhoAih.Fields.MOTIVO_COBRANCA.name())
				.append(",1,1) As Int ) = 4  ")
				.append("  	  Then Coalesce( EAI.")
				.append(FatEspelhoAih.Fields.CID_PRIMARIO.name())
				.append(",' ')  ")
				.append("      else ' ' ")
				.append("  End AS ")
				.append(CursorCEAIVO.Fields.CID_OBITO.toString())

				// Marina 21/08/2012
				// filler
				.append(" ,'000' AS ")
				.append(CursorCEAIVO.Fields.FILLER.toString())

				// Nome
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.PAC_NOME.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.PAC_NOME.toString())

				// Data Nasc
				.append("  ,COALESCE( TO_CHAR(EAI.")
				.append(FatEspelhoAih.Fields.PAC_DT_NASCIMENTO.name())
				.append(", 'YYYYMMDD'),' ')")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.PAC_DT_NASCIMENTO.toString())

				// Sexo
				.append("  ,Case When EAI.")
				.append(FatEspelhoAih.Fields.PAC_SEXO.name())
				.append(" = '1' Then 'M' Else 'F' End ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.PAC_SEXO.toString())

		// Cor/RaÃ§a
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.PAC_COR.name())
				.append(", '99') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.PAC_COR.toString())

		// Nome mÃ£e
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.PAC_NOME_MAE.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.PAC_NOME_MAE.toString())

				// Nome responsÃ¡vel
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NOME_RESPONSAVEL_PAC.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NOME_RESPONSAVEL_PAC.toString())

				// Marina 10/03/2010
				// Tipo doc
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.IND_DOC_PAC.name())
				.append(", 5) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.IND_DOC_PAC.toString())

				// Marina 26/10/2010 - Etnia Indigena -- '00000000000'
				.append(" ,'0000' AS ")
				.append(CursorCEAIVO.Fields.ETNIA_INDIGENA.toString())

				// Marina 21/08/2012
				.append("  ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.EXCLUSAO_CRITICA.name())
				.append(", '0') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.EXCLUSAO_CRITICA.toString())

				// Cartão SUS
				.append(",COALESCE((SELECT PAC_IN.")
		   				.append(AipPacientes.Fields.NRO_CARTAO_SAUDE.name()).append(" FROM AGH.")
		   				.append(AipPacientes.class.getAnnotation(Table.class).name()).append(" PAC_IN ")
		   				.append(" WHERE PAC_IN.").append(AipPacientes.Fields.PRONTUARIO.name())
		   				.append("   = EAI.").append(FatEspelhoAih.Fields.PAC_PRONTUARIO.name())
		   		.append("),0) AS ").append(CursorCEAIVO.Fields.CARTAO_SUS.toString())

				// Nacionalidade
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NACIONALIDADE_PAC.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NACIONALIDADE_PAC.toString())

				// Tipo de Logradouro (END_TIP_CODIGO) recuperado de
				// AIPP_INS_TIPO_TITULO_LOG

				// ESCHWEIGERT 31/01/2013 : A tabela
				// "AGH.AIPP_INS_TIPO_TITULO_LOG" não será migrada para o
				// AGHU,
				// devendo ficar o campo correspondente da tabela espelho
				//.append(" ,COALESCE( EAI.")
				//.append(FatEspelhoAih.Fields.END_TIP_CODIGO.name())
				//.append(", 0) ")
				//.append(" AS ")
				//.append(CursorCEAIVO.Fields.END_TIP_CODIGO.toString())

				// Tipo de Logradouro
							
				// FBRAGANCA 10/07/2014 Tabelas agh.aip_tipo_logradouros_log1 e agh.aip_tipo_logradouros_log2
				// foram migradas para substituir as tabelas da function AGH.FATC_TIPO_LOGRADOURO_SUS
				.append(" ,COALESCE(  (SELECT TL1.").append(AipTipoLogradourosLog1.Fields.CODIGO_ANTIGO.name()).append(" FROM AGH.")
	   			.append(AipTipoLogradourosLog1.class.getAnnotation(Table.class).name()).append(" TL1 LEFT JOIN AGH.")
	   			.append(AipTipoLogradourosLog2.class.getAnnotation(Table.class).name()).append(" TL2 ON TL1.")
	   			.append(AipTipoLogradourosLog1.Fields.TABELA.name()).append(" = 'AIP_TIPO_LOGRADOUROS' ")
	   			.append("AND TL1.").append(AipTipoLogradourosLog1.Fields.CODIGO_NOVO.name()).append(" = TL2.")
	   				.append(AipTipoLogradourosLog2.Fields.CODIGO.name())
	   			.append(" AND TL1.").append(AipTipoLogradourosLog1.Fields.MENSAGEM.name()).append(" = 'Criando registro' ")
	   			.append("WHERE TL2.").append(AipTipoLogradourosLog2.Fields.NOVO_CODIGO.name()).append(" = ")
	   				.append(FatEspelhoAih.Fields.END_TIP_CODIGO.name()).append("), 0) ")
	   			.append(" AS ").append(CursorCEAIVO.Fields.END_TIP_CODIGO.toString())
			
				
				// Logradouro
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.END_LOGRADOURO_PAC.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.END_LOGRADOURO_PAC.toString())

				// Número
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.END_NRO_LOGRADOURO_PAC.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.END_NRO_LOGRADOURO_PAC.toString())

				// Complemento
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.END_CMPL_LOGRADOURO_PAC.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.END_CMPL_LOGRADOURO_PAC.toString())

				// Bairro
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.END_BAIRRO_PAC.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.END_BAIRRO_PAC.toString())

				// Cidade IBGE
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.COD_IBGE_CIDADE_PAC.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.COD_IBGE_CIDADE_PAC.toString())

				// UF
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.END_UF_PAC.name())
				.append(", ' ') ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.END_UF_PAC.toString())

				// CEP
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.END_CEP_PAC.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.END_CEP_PAC.toString())
				// Prontuario
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.PAC_PRONTUARIO.name()).append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.PAC_PRONTUARIO.toString())

				// Enfermaria
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.ENFERMARIA.name()).append(", '0001') ").append(" AS ")
				.append(CursorCEAIVO.Fields.ENFERMARIA.toString())

				// Leito
				.append(" ,COALESCE( EAI.").append(FatEspelhoAih.Fields.LEITO.name()).append(", '0099') ").append(" AS ")
				.append(CursorCEAIVO.Fields.LEITO.toString());

		return sql.toString();
	}

	private String getQuartaParteSQLCursorCEAI() {
		final StringBuilder sql = new StringBuilder(1300);

		// saida utineo,peso,meses
		sql.append(" ,(CASE WHEN  ")
				.append(" ( SELECT DISTINCT AMA_IN.")
				.append(FatAtoMedicoAih.Fields.NOTA_FISCAL.name())
				.append(" FROM AGH.")
				.append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
				.append(" AMA_IN ")
				.append(" WHERE AMA_IN.")
				.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
				.append(" = :P_CTH_SEQ ")
				.append("   AND AMA_IN.")
				.append(FatAtoMedicoAih.Fields.EAI_SEQ.name())
				.append(" = 1 ")
				// TODO PARAMETRIZAR
				.append("   AND AMA_IN.")
				.append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
				.append(" = 802010130 ")
				.append(" ) IS NOT NULL THEN ")

				.append(" ( SELECT DISTINCT AMA_IN.")
				.append(FatAtoMedicoAih.Fields.NOTA_FISCAL.name())
				.append(" FROM AGH.")
				.append(FatAtoMedicoAih.class.getAnnotation(Table.class).name())
				.append(" AMA_IN ")
				.append(" WHERE AMA_IN.")
				.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
				.append(" = :P_CTH_SEQ ")
				.append("   AND AMA_IN.")
				.append(FatAtoMedicoAih.Fields.EAI_SEQ.name())
				.append(" = 1 ")
				// TODO PARAMETRIZAR
				.append("   AND AMA_IN.")
				.append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
				.append(" = 802010130 ")
				.append(" ) ELSE 0 END ")

				.append(" ) AS ")
				.append(CursorCEAIVO.Fields.SAIDA_UTINEO.toString())

				// Qt_vivos
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NASCIDOS_VIVOS.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NASCIDOS_VIVOS.toString())

				// Qt_mortos
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NASCIDOS_MORTOS.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NASCIDOS_MORTOS.toString())

				// Qt_alta
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.SAIDAS_ALTA.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.SAIDAS_ALTA.toString())

				// Qt_transf
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.SAIDAS_TRANSFERENCIA.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.SAIDAS_TRANSFERENCIA.toString())

				// Qt Obito
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.SAIDAS_OBITO.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.SAIDAS_OBITO.toString())

				// qtde filhos
				.append(" ,'0' AS ")
				.append(CursorCEAIVO.Fields.QTE_FILHOS.toString())

		// Grau de instruÃ§Ã£o
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.GRAU_INSTRUCAO_PAC.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.GRAU_INSTRUCAO_PAC.toString())

		// Cid IndicaÃ§Ã£o
				.append(" ,'0' AS ")
				.append(CursorCEAIVO.Fields.CID_INDICACAO.toString())

		// MÃ©todo contraceptivo
				.append(" ,' ' AS ")
				.append(CursorCEAIVO.Fields.METODO_CONTRACEPTIVO.toString())

		// GestaÃ§Ã£o Alto Risco
				.append(" ,'1' AS ")
				.append(CursorCEAIVO.Fields.GESTACAO_ALTO_RISCO.toString())

				// Marina 11/11/2012 - alterado o tamanho de 11 para 12
		// posiÃ§oes - chamado: 84028
				.append(" ,COALESCE( EAI.")
				.append(FatEspelhoAih.Fields.NRO_SISPRENATAL.name())
				.append(", 0) ")
				.append(" AS ")
				.append(CursorCEAIVO.Fields.NRO_SISPRENATAL.toString())

				// eSchweigert: formatação fica em LayoutArquivoSusRN.inicializaVParte3
				.append(" ,EAI.").append(FatEspelhoAih.Fields.PAC_NRO_CARTAO_SAUDE.name()).append(" AS ").append(CursorCEAIVO.Fields.PAC_NRO_CARTAO_SAUDE_2.toString())
				.append(" ,EAI.").append(FatEspelhoAih.Fields.PAC_RG.name()).append(" AS ").append(CursorCEAIVO.Fields.PAC_RG.toString())				

				// lpad(to_char(nvl(AIPC_GET_FONE_SISAIH(pac_prontuario),0)),
				// 11, '0')|| -- telefone paciente
				.append(" ,CASE WHEN ").append("		   ( SELECT (CASE WHEN AIP_IN1.").append(AipPacientes.Fields.DDD_FONE_RESIDENCIAL.name())
				.append(" IS NULL ").append("   				      THEN (").append(" 							CASE WHEN AIP_IN1.")
				.append(AipPacientes.Fields.DDD_FONE_RECADO.name()).append(" IS NULL ").append(" 							   THEN '51033598000' ")
				.append(" 							   ELSE AIP_IN1.").append(AipPacientes.Fields.DDD_FONE_RECADO.name()).append(" || '0' || ")
				.append(" 							        AIP_IN1.").append(AipPacientes.Fields.FONE_RECADO.name()).append(" 							END ")
				.append(" 					      ) ELSE  ").append(" 							    AIP_IN1.").append(AipPacientes.Fields.DDD_FONE_RESIDENCIAL.name())
				.append(" || '0' || ").append(" 							    AIP_IN1.").append(AipPacientes.Fields.FONE_RESIDENCIAL.name())
				.append(" 				     END) ").append(" 			  FROM  AGH.").append(AipPacientes.class.getAnnotation(Table.class).name())
				.append(" AIP_IN1 ").append("   			  WHERE AIP_IN1.").append(AipPacientes.Fields.PRONTUARIO.name()).append(" =  EAI.")
				.append(FatEspelhoAih.Fields.PAC_PRONTUARIO.name()).append("            ) IS NOT NULL THEN ")
				.append("		   ( SELECT (CASE WHEN AIP_IN1.").append(AipPacientes.Fields.DDD_FONE_RESIDENCIAL.name()).append(" IS NULL ")
				.append("   				      THEN (").append(" 							CASE WHEN AIP_IN1.").append(AipPacientes.Fields.DDD_FONE_RECADO.name())
				.append(" IS NULL ").append(" 							   THEN '51033598000' ").append(" 							   ELSE AIP_IN1.")
				.append(AipPacientes.Fields.DDD_FONE_RECADO.name()).append(" || '0' || ").append(" 							        AIP_IN1.")
				.append(AipPacientes.Fields.FONE_RECADO.name()).append(" 							END ").append(" 					      ) ELSE  ")
				.append(" 							    AIP_IN1.").append(AipPacientes.Fields.DDD_FONE_RESIDENCIAL.name()).append(" || '0' || ")
				.append(" 							    AIP_IN1.").append(AipPacientes.Fields.FONE_RESIDENCIAL.name()).append(" 				     END) ")
				.append(" 			  FROM  AGH.").append(AipPacientes.class.getAnnotation(Table.class).name()).append(" AIP_IN1 ")
				.append("   			  WHERE AIP_IN1.").append(AipPacientes.Fields.PRONTUARIO.name()).append(" =  EAI.")
				.append(FatEspelhoAih.Fields.PAC_PRONTUARIO.name()).append("            ) ELSE '0' END ").append("          AS ")
				.append(CursorCEAIVO.Fields.PAC_TELEFONE.toString());

		return sql.toString();
	}

	public List<CursorCEAIVO> obterResultadoCursorCEAI(final Integer cthSeq, final Long cpfDirCli, final String municipioInstituicao,
			final String orgEmisAih, final Integer cnesHCPA) {

		final StringBuilder sql = new StringBuilder(100);

		sql.append(getPrimeiraParteSQLCursorCEAI()).append(getSegundaParteSQLCursorCEAI()).append(getTerceiraParteSQLCursorCEAI())
				.append(getQuartaParteSQLCursorCEAI()).append(" FROM  AGH.").append(FatEspelhoAih.class.getAnnotation(Table.class).name())
				.append(" EAI ").append("  WHERE EAI.").append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(" = :P_CTH_SEQ ")
				.append("    AND EAI.").append(FatEspelhoAih.Fields.SEQP.name()).append(" = :P_SEQP")

				.append(" ORDER BY EAI.").append(FatEspelhoAih.Fields.SEQP.name());

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("P_CTH_SEQ", cthSeq);
		query.setInteger("P_SEQP", 1);
		query.setLong("P_CPF_DIR_CLI", cpfDirCli);
		query.setString("P_MUNICIPIO_INSTITUICAO", municipioInstituicao);
		query.setString("P_ORG_LOC_REC", orgEmisAih);
		query.setInteger("P_CNES_HCPA", cnesHCPA);

		@SuppressWarnings("unchecked")
		final List<CursorCEAIVO> vos = query.addScalar(CursorCEAIVO.Fields.CTH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.SEQP.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.DCI_CODIGO_DCIH.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.QT_LOTE.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.APRES_LOTE.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.SEQ_LOTE.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.ORG_EMIS_AIH.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CNES_HCPA.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NUMERO_AIH.toString(), LongType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.TAH_SEQ.toString(), ShortType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.ESPECIALIDADE_DCIH.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.MUNICIPIO_INSTITUICAO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.IPH_PHO_SEQ_REALIZ.toString(), ShortType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.IPH_SEQ_REALIZ.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.DATA_INTERNACAO_TIPO_DATE.toString(), DateType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.DATA_SAIDA_TIPO_DATE.toString(), DateType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.DATA_INTERNACAO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.DATA_SAIDA.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.IPH_COD_SUS_SOLIC.toString(), LongType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NRO_SEQAIH5.toString(), ShortType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NUMERO_AIH_POSTERIOR.toString(), LongType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NUMERO_AIH_ANTERIOR.toString(), LongType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.AIH_DTHR_EMISSAO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CNS_MEDICO_AUDITOR.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CPF_MEDICO_SOLIC_RESPONS.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CPF_MEDICO_AUDITOR.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CPF_DIR_CLI.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_NOME.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_DT_NASCIMENTO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_SEXO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_COR.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_NOME_MAE.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NOME_RESPONSAVEL_PAC.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.IND_DOC_PAC.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.EXCLUSAO_CRITICA.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_PRONTUARIO.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NACIONALIDADE_PAC.toString(), ShortType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_LOGRADOURO_PAC.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_NRO_LOGRADOURO_PAC.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_CMPL_LOGRADOURO_PAC.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_BAIRRO_PAC.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.COD_IBGE_CIDADE_PAC.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_UF_PAC.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_CEP_PAC.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NASCIDOS_VIVOS.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NASCIDOS_MORTOS.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.SAIDAS_ALTA.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.SAIDAS_TRANSFERENCIA.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.SAIDAS_OBITO.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.GRAU_INSTRUCAO_PAC.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.NRO_SISPRENATAL.toString(), LongType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.END_TIP_CODIGO.toString(), ShortType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.MUDANCA.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.IPH_COD_SUS_REALIZ.toString(), LongType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.TCI_COD_SUS.toString(), ByteType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.MOTIVO_COBRANCA.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.TIPO_DOCUMENTO.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CID_PRIMARIO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CID_SECUNDARIO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.FILLER.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CID_OBITO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.ETNIA_INDIGENA.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CARTAO_SUS.toString(), BigIntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.ENFERMARIA.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.LEITO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.SAIDA_UTINEO.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.QTE_FILHOS.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_TELEFONE.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.CID_INDICACAO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.METODO_CONTRACEPTIVO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.GESTACAO_ALTO_RISCO.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_NRO_CARTAO_SAUDE_2.toString(), StringType.INSTANCE)
				.addScalar(CursorCEAIVO.Fields.PAC_RG.toString(), StringType.INSTANCE)				

				.setResultTransformer(Transformers.aliasToBean(CursorCEAIVO.class)).list();

		return vos;
	}

	public List<CursorCAtosMedVO> obterResultadoCursorCAtosMed(final Integer cthSeq, final String codigoDaUPS, final Short fogSgrGrpSeq) {
		final StringBuilder sql = new StringBuilder(1300);

		sql.append(" SELECT ")
				.append("    CASE WHEN GREATEST(AAM.")
				.append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name())
				.append(",'201112') = '201112'")
				.append("      THEN 1 ELSE 2 ")
				.append("  	END AS ")
				.append(CursorCAtosMedVO.Fields.INDICADOR_DOCUMENTO.toString())

				.append("    ,CASE WHEN GREATEST(AAM.")
				.append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name())
				.append(",'201112') = '201112'")
				.append("      THEN COALESCE(CAST(AAM.")
				.append(FatAtoMedicoAih.Fields.CPF_CNS.name())
				.append(" AS VARCHAR(15)), '0') ")
				.append("      ELSE ")
				.append("        COALESCE( ")
				.append("                  (")
				.append("        			 CASE WHEN AAM.")
				.append(FatAtoMedicoAih.Fields.CPF_CNS.name())
				.append(" IS NULL")
				.append("        			  THEN NULL")
				.append("          			  ELSE (")
				.append("                  			CASE WHEN LENGTH( TO_CHAR( AAM.")
				.append(FatAtoMedicoAih.Fields.CPF_CNS.name())
		.append(" )) = 15")
				.append("        					  THEN CAST(AAM.")
				.append(FatAtoMedicoAih.Fields.CPF_CNS.name())
				.append(" AS VARCHAR(15)) ")
				.append("        					  ELSE")
				.append("        						(SELECT TII_IN.")
				.append(RapPessoaTipoInformacoes.Fields.VALOR.name())
				.append("        							FROM AGH.")
				.append(RapPessoaTipoInformacoes.class.getAnnotation(Table.class).name())
				.append(" TII_IN")
				.append("        								,AGH.")
				.append(RapPessoasFisicas.class.getAnnotation(Table.class).name())
				.append(" PES_IN")
				.append("        							WHERE PES_IN.")
				.append(RapPessoasFisicas.Fields.CODIGO.name())
				.append(" = TII_IN.")
				.append(RapPessoaTipoInformacoes.Fields.PES_CODIGO.name())

				// TODO verificar este parametro
				.append("        								AND TII_IN.")
				.append(RapPessoaTipoInformacoes.Fields.TII_SEQ.name())
				.append(" = 9")
				.append("        								AND PES_IN.")
				.append(RapPessoasFisicas.Fields.CPF.name())
				.append(" = AAM.")
				.append(FatAtoMedicoAih.Fields.CPF_CNS.name())
				.append("        						)")
				.append("        					END")
				.append("        	  			  )")
				.append("        			  END ")
				.append("        	  	   ), '0'")
				.append("        	  	 )")
				.append("    END AS ")
				.append(CursorCAtosMedVO.Fields.CPF.toString())

				// CAMPOS PARA CONSULTA FATC_BUSCA_CBO
				.append(" , IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.IPH_SEQ.toString())

				.append(" , IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.PHO_SEQ.toString())

				.append(" , AAM.").append(FatAtoMedicoAih.Fields.CBO.name()).append(" AS ").append(CursorCAtosMedVO.Fields.CBO.toString())

				.append(" , COALESCE(")
				.append(FatAtoMedicoAih.Fields.IND_EQUIPE.name())
				.append(",0) ")
				.append(" AS ")
				.append(CursorCAtosMedVO.Fields.EQUIPE.toString())

				.append(" , CASE WHEN IPH.")
				.append(FatItensProcedHospitalar.Fields.FOG_SGR_GRP_SEQ.name())
				.append(" = :P_GRUPO_OPM ")
				.append("    THEN '3' || LPAD(COALESCE( CAST( AAM.")
				.append(FatAtoMedicoAih.Fields.CGC.name())
				.append(" AS VARCHAR(14)), '0' ),14,'0')")
				.append("    ELSE '5' || LPAD(COALESCE(:P_CODIGO_DA_UPS, '0'), 14, '0')")
				.append("   END")
				.append(" AS ")
				.append(CursorCAtosMedVO.Fields.CGC.toString())

				.append(" , '5' AS ")
				.append(CursorCAtosMedVO.Fields.DOCUMENTO_EXECUTOR.toString())

				.append(" , COALESCE(:P_CODIGO_DA_UPS,'0') AS ")
				.append(CursorCAtosMedVO.Fields.CNES_HCPA.toString())

				.append(" , COALESCE(AAM.")
				.append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
				.append(",0) AS ")
				.append(CursorCAtosMedVO.Fields.COD_PROCEDIMENTO.toString())

				.append(" , COALESCE(AAM.")
				.append(FatAtoMedicoAih.Fields.QUANTIDADE.name())
				.append(",0) ")
				.append(" AS ")
				.append(CursorCAtosMedVO.Fields.QUANTIDADE.toString())

				.append(" , COALESCE(AAM.")
				.append(FatAtoMedicoAih.Fields.COMPETENCIA_UTI.name())
				.append(",' ') ")
				.append(" AS ")
				.append(CursorCAtosMedVO.Fields.COMPETENCIA_UTI.toString())

				// BUSCAR VIA JAVA
				// FATC_BUSCA_SERV_CLASS(P_CTH_SEQ,AAM.IPH_SEQ,AAM.IPH_PHO_SEQ)
				// ATOS_MED,
				.append(" , AAM.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.EAI_CTH_SEQ.toString())

				.append(" , AAM.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.AMA_IPH_SEQ.toString())

				.append(" , AAM.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.AMA_IPH_PHO_SEQ.toString())

				.append(" , AAM.").append(FatAtoMedicoAih.Fields.EAI_SEQ.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.EAI_SEQ.toString())

				.append(" , AAM.").append(FatAtoMedicoAih.Fields.SEQP.name()).append(" AS ")
				.append(CursorCAtosMedVO.Fields.SEQP.toString())

				.append(" FROM ").append("   AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name())
				.append(" IPH ").append(" , AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AAM")

				.append(" WHERE ").append("      IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append("= AAM.")
				.append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append("  AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
				.append(" = AAM.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append("  AND AAM.")
				.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = :P_CTH_SEQ")
				// AND EAI_SEQ = EAI_SEQ --P_SEQP

				// NEY 20110228
				.append(" ORDER BY AAM.").append(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.name());

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("P_CTH_SEQ", cthSeq);
		query.setString("P_CODIGO_DA_UPS", codigoDaUPS);
		query.setShort("P_GRUPO_OPM", fogSgrGrpSeq);

		final List<CursorCAtosMedVO> vos = query.addScalar(CursorCAtosMedVO.Fields.INDICADOR_DOCUMENTO.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.CPF.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.PHO_SEQ.toString(), ShortType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.IPH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.CBO.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.EQUIPE.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.CGC.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.DOCUMENTO_EXECUTOR.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.CNES_HCPA.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.COD_PROCEDIMENTO.toString(), LongType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.QUANTIDADE.toString(), ShortType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.COMPETENCIA_UTI.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.EAI_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.EAI_CTH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.AMA_IPH_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCAtosMedVO.Fields.AMA_IPH_PHO_SEQ.toString(), ShortType.INSTANCE)

				.addScalar(CursorCAtosMedVO.Fields.SEQP.toString(), ByteType.INSTANCE)

				.setResultTransformer(Transformers.aliasToBean(CursorCAtosMedVO.class)).list();

		return vos;
	}

	public List<CursorCAtosProtVO> obterResultadoCursorCAtosProt(final Integer cthSeq, final Short fogSgrGrpSeq) {
		final StringBuilder sql = new StringBuilder(400);

		sql.append(" SELECT ").append("      COALESCE(AAM.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(",0) ")
				.append(" AS ").append(CursorCAtosProtVO.Fields.CODIGO_OPM.toString())

				.append("    , COALESCE(AAM.").append(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.name()).append(",0) ").append(" AS ")
				.append(CursorCAtosProtVO.Fields.LINHA_PROCEDIMENTO.toString())

				.append("  	, COALESCE(AAM.").append(FatAtoMedicoAih.Fields.REG_ANVISA_OPM.name()).append(",' ')").append(" AS ")
				.append(CursorCAtosProtVO.Fields.REG_ANVISA_OPM.toString())

				.append("    , COALESCE(AAM.").append(FatAtoMedicoAih.Fields.SERIE_OPM.name()).append(",' ') ").append(" AS ")
				.append(CursorCAtosProtVO.Fields.SERIE_OPM.toString())

				.append("    , COALESCE(AAM.").append(FatAtoMedicoAih.Fields.LOTE_OPM.name()).append(",' ') ").append(" AS ")
				.append(CursorCAtosProtVO.Fields.LOTE_OPM.toString())

				.append("  	, COALESCE(AAM.").append(FatAtoMedicoAih.Fields.NOTA_FISCAL.name()).append(",0) ").append(" AS ")
				.append(CursorCAtosProtVO.Fields.NOTA_FISCAL.toString())

				.append("    , COALESCE(AAM.").append(FatAtoMedicoAih.Fields.CGC.name()).append(",0) ").append(" AS ")
				.append(CursorCAtosProtVO.Fields.CNPJ_FORNECEDOR.toString())

				.append("    , COALESCE(AAM.").append(FatAtoMedicoAih.Fields.CNPJ_REG_ANVISA.name()).append(", 0) ").append(" AS ")
				.append(CursorCAtosProtVO.Fields.CNPJ_FABRICANTE.toString())

				.append(" FROM ").append("   AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name())
				.append(" IPH ").append(" , AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AAM")

				.append(" WHERE ").append("      IPH.").append(FatItensProcedHospitalar.Fields.FOG_SGR_GRP_SEQ.name())
				.append("= :P_GRUPO_OPM ").append("  AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append("= AAM.")
				.append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append("  AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
				.append(" = AAM.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name())

				// AND EAI_SEQ = EAI_SEQ --P_SEQP

				.append("  AND AAM.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = :P_CTH_SEQ")

				// NEY 20110228
				.append(" ORDER BY AAM.").append(FatAtoMedicoAih.Fields.SEQP.name());

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("P_CTH_SEQ", cthSeq);
		query.setShort("P_GRUPO_OPM", fogSgrGrpSeq);

		final List<CursorCAtosProtVO> vos = query.addScalar(CursorCAtosProtVO.Fields.CODIGO_OPM.toString(), LongType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.LINHA_PROCEDIMENTO.toString(), ShortType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.REG_ANVISA_OPM.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.SERIE_OPM.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.LOTE_OPM.toString(), StringType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.NOTA_FISCAL.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.CNPJ_FORNECEDOR.toString(), LongType.INSTANCE)
				.addScalar(CursorCAtosProtVO.Fields.CNPJ_FABRICANTE.toString(), LongType.INSTANCE)

				.setResultTransformer(Transformers.aliasToBean(CursorCAtosProtVO.class)).list();

		return vos;
	}

	public List<CursorCBuscaRegCivilVO> obterResultadoCursorCBuscaRegCivil(final Integer cthSeq) {
		final StringBuilder sql = new StringBuilder(900);

		// Marina 28/06/2011 - Chamado: 49693
		sql.append(" SELECT DISTINCT ")

		.append("      COALESCE(PDS.").append(AipPacientesDadosCns.Fields.NUMERO_DN.name()).append(",0) ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.NUMERO_DN.toString())

				.append("    , CASE WHEN GREATEST (TO_CHAR(PDS.").append(AipPacientesDadosCns.Fields.DATA_EMISSAO.name())
				.append(", 'YYYY'),'2009') = TO_CHAR(PDS.").append(AipPacientesDadosCns.Fields.DATA_EMISSAO.name()).append(", 'YYYY')")
				.append("         THEN COALESCE(' ',' ')").append("         ELSE").append("           COALESCE(PAC.")
				.append(AipPacientes.Fields.NOME.name()).append(",' ')").append("         END ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.NOME_RECEM_NASCIDO.toString())

				.append("    , COALESCE(PDS.").append(AipPacientesDadosCns.Fields.NOME_CARTORIO.name()).append(",' ') ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.RAZAO_SOCIAL_CARTORIO.toString())

				.append("    , COALESCE(PDS.").append(AipPacientesDadosCns.Fields.LIVRO.name()).append(",' ') ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.LIVRO.toString())

				.append("    , COALESCE(PDS.").append(AipPacientesDadosCns.Fields.FOLHAS.name()).append(",0) ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.FOLHAS.toString())

				.append("    , COALESCE(PDS.").append(AipPacientesDadosCns.Fields.TERMO.name()).append(",0) ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.TERMO.toString())

				.append("    , COALESCE(TO_CHAR(PDS.").append(AipPacientesDadosCns.Fields.DATA_EMISSAO.name())
				.append(", 'YYYYMMDD'),'0')  ").append(" AS ").append(CursorCBuscaRegCivilVO.Fields.DATA_EMISSAO.toString())

				.append("    , COALESCE(AAM.").append(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.name()).append(",0) ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.SEQ_ARQ_SUS.toString())

				.append("    , CASE WHEN LENGTH( CAST(PAC.").append(AipPacientes.Fields.REG_NASCIMENTO.name())
				.append(" AS VARCHAR(50)) ) = 30  ").append("       THEN 0 ").append("       ELSE COALESCE(PAC.")
				.append(AipPacientes.Fields.REG_NASCIMENTO.name()).append(",0) ").append("      END ").append(" AS ")
				.append(CursorCBuscaRegCivilVO.Fields.REG_CIVIL.toString())

				.append(" FROM ").append("     AGH.").append(FatContasInternacao.class.getAnnotation(Table.class).name()).append(" COI ")
				.append("   , AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH ")
				.append("   , AGH.").append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(" AAM ").append("   , AGH.")
				.append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(" ATD ").append("   , AGH.")
				.append(McoRecemNascidos.class.getAnnotation(Table.class).name()).append(" RNA ")
				.append("   , AGH.")
				.append(AipPacientesDadosCns.class.getAnnotation(Table.class).name())
				.append(" PDS ")
				.append("   , AGH.")
				.append(AipPacientes.class.getAnnotation(Table.class).name())
				.append(" PAC ")

				.append(" WHERE COI.")
				.append(FatContasInternacao.Fields.CTH_SEQ.name())
				.append(" = :P_CTH_SEQ   ")
				.append("   AND ICH.")
				.append(FatItemContaHospitalar.Fields.CTH_SEQ.name())
				.append(" =  COI.")
				.append(FatContasInternacao.Fields.CTH_SEQ.name())
				.append("   AND AAM.")
				.append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
				.append(" =  COI.")
				.append(FatContasInternacao.Fields.CTH_SEQ.name())
				// TODO parametrizar
				.append("   AND ICH.")
				.append(FatItemContaHospitalar.Fields.PHI_SEQ.name())
				.append(" = 28619 ")

				// MARINA 28/06/2011 - CHAMADO: 49693
				.append("   AND ICH.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.name()).append(" <> 'C'  ").append("   AND COI.")
				.append(FatContasInternacao.Fields.INT_SEQ.name()).append(" = ATD.").append(AghAtendimentos.Fields.INT_SEQ.name())
				.append("   AND ATD.").append(AghAtendimentos.Fields.GSO_PAC_CODIGO.name()).append(" = RNA.")
				.append(McoRecemNascidos.Fields.GSO_PAC_CODIGO.name()).append("   AND ATD.").append(AghAtendimentos.Fields.GSO_SEQP.name())
				.append(" = RNA.").append(McoRecemNascidos.Fields.GSO_SEQP.name()).append("   AND PDS.")
				.append(AipPacientesDadosCns.Fields.PAC_CODIGO.name()).append(" = RNA.").append(McoRecemNascidos.Fields.PAC_CODIGO.name())
				.append("   AND PDS.").append(AipPacientesDadosCns.Fields.PAC_CODIGO.name()).append(" = PAC.")
				.append(AipPacientes.Fields.CODIGO.name()).append("   AND AAM.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
				.append(" = 801010047	 ");

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setInteger("P_CTH_SEQ", cthSeq);

		final List<CursorCBuscaRegCivilVO> vos = query.addScalar(CursorCBuscaRegCivilVO.Fields.NUMERO_DN.toString(), LongType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.NOME_RECEM_NASCIDO.toString(), StringType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.RAZAO_SOCIAL_CARTORIO.toString(), StringType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.LIVRO.toString(), StringType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.FOLHAS.toString(), ShortType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.TERMO.toString(), IntegerType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.DATA_EMISSAO.toString(), StringType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.SEQ_ARQ_SUS.toString(), ShortType.INSTANCE)
				.addScalar(CursorCBuscaRegCivilVO.Fields.REG_CIVIL.toString(), BigIntegerType.INSTANCE)

				.setResultTransformer(Transformers.aliasToBean(CursorCBuscaRegCivilVO.class)).list();

		return vos;
	}
	
	private DetachedCriteria obterCriteriaDoCursorBuscCodTabela(
			final Integer pCthSeq,
			final DominioSituacao situacao,
			final String codRegistro,
			final Byte ... codigoSusTaoSeq ){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class, ALIAS_EAI);
		
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), ALIAS_CTH);
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), ALIAS_IPHR);
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.ATOS_MEDICOS.toString(), ALIAS_AMA);
		criteria.createAlias(ALIAS_AMA + PONTO + FatAtoMedicoAih.Fields.IPH.toString(), ALIAS_IPH);
		criteria.createAlias(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.FAT_PROCEDIMENTO_REGISTRO.toString(), ALIAS_FPR);
		
		criteria.add(Restrictions.eq(ALIAS_CTH + PONTO + FatContasHospitalares.Fields.SEQ.toString(), pCthSeq));
		criteria.add(Restrictions.eq(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq(ALIAS_FPR + PONTO + FatProcedimentoRegistro.Fields.COD_REGISTRO.toString(), codRegistro));
		criteria.add(Restrictions.in(ALIAS_AMA + PONTO + FatAtoMedicoAih.Fields.TAO_SEQ.toString(), codigoSusTaoSeq));
		
		criteria.addOrder(Order.asc(ALIAS_AMA + PONTO + FatAtoMedicoAih.Fields.SEQP.toString()));

		return criteria;
	}
	
	public List<Long> obterCursorCodigoTabela(
			final Integer pCthSeq,
			final DominioSituacao situacao,
			final String codRegistro,
			final Byte ... codigoSusTaoSeq ){
		
		String sql = " COALESCE (" + ALIAS_AMA3 + PONTO + FatAtoMedicoAih.Fields.IPH_COD_SUS.name() + ", 0) as " + FatAtoMedicoAih.Fields.IPH_COD_SUS.name();
		String[] columnAliases = { FatAtoMedicoAih.Fields.IPH_COD_SUS.name() };
		Type[] types = { LongType.INSTANCE };
		
		DetachedCriteria criteria = obterCriteriaDoCursorBuscCodTabela(pCthSeq, situacao, codRegistro, codigoSusTaoSeq);
		
		Projection projection = Projections.projectionList().add(Projections.sqlProjection(sql, columnAliases, types));
		criteria.setProjection(projection);	
		
		return executeCriteria(criteria);
	}
	
	public List<CursorGrupo4VO> obterCursorGrupo4(
			final Integer pCthSeq, 
			final Short quantidade, 
			final Boolean cirurgiaMultipla, 
			final DominioSituacao situacao, 
			final Short iphCodSus,
			final String codRegistro,
			final Byte ... codigoSusTaoSeq){
		
		Object[] values = { iphCodSus };
		Type[] types = { ShortType.INSTANCE };
		
		DetachedCriteria criteria = obterCriteriaDoCursorBuscCodTabela(pCthSeq, situacao, codRegistro, codigoSusTaoSeq);
		
		criteria.add(Restrictions.gt(ALIAS_AMA + PONTO + FatAtoMedicoAih.Fields.QUANTIDADE.toString(), quantidade));
		criteria.add(Restrictions.eq(ALIAS_IPHR + PONTO + FatItensProcedHospitalar.Fields.IND_CIRURGIA_MULTIPLA.toString(), cirurgiaMultipla));
		
		if(this.isOracle()){
			criteria.add(Restrictions.sqlRestriction("SUBSTR( " + ALIAS_AMA3 + PONTO + FatAtoMedicoAih.Fields.IPH_COD_SUS.name() +", 1, 1) = ? ", values, types));
		} else {
			criteria.add(Restrictions.sqlRestriction("TO_NUMBER(SUBSTR(TO_CHAR(" + ALIAS_AMA3 + PONTO + FatAtoMedicoAih.Fields.IPH_COD_SUS.name() +", 'FM9999999999'), 1, 1), 'FM9999999999') = ? ", values, types));
		}
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(ALIAS_AMA + PONTO + FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()), CursorGrupo4VO.Fields.IPH_COD_SUS.toString())
				.add(Projections.property(ALIAS_AMA + PONTO + FatAtoMedicoAih.Fields.QUANTIDADE.toString()), CursorGrupo4VO.Fields.QUANTIDADE.toString());
		
		criteria.setProjection(projection);			
		
		criteria.setResultTransformer(Transformers.aliasToBean(CursorGrupo4VO.class));
		
		return executeCriteria(criteria);		
	}
	
	public List<CmceCthSeqVO> listarCmcePorChtSeq(List<Integer> listaCthSeq,
			Byte magicByteCspSeqEq1) {

		List<CmceCthSeqVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" as cthSeq");
		hql.append(" , eaiS.");		
		hql.append(FatEspelhoAih.Fields.CON_COD_CENTRAL.toString());
		hql.append(" as conCodCentral");
		hql.append(" from ");
		hql.append(FatEspelhoAih.class.getName());
		hql.append(" as eaiS ");
		hql.append(" where eaiS.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" in (:cthSeq)");

		query = createHibernateQuery(hql.toString());

		query.setParameterList("cthSeq", listaCthSeq);
		query.setParameter("seqp", Integer.valueOf(magicByteCspSeqEq1));

		query.setResultTransformer(Transformers.aliasToBean(CmceCthSeqVO.class));

		result = query.list();
		
		return result;
	}
	
	// #32238 C3
	public List<SigValorReceitaVO> obterValorEspelhoPelaInternacao(Integer intSeq, DominioSituacaoConta situacao, Integer eaiSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class, "eai");
		criteria.createAlias("eai." + FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), "cth");
		criteria.createAlias("cth." + FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "coi");
		
		Projection projection = Projections.projectionList()
				.add(Projections.property("eai." + FatEspelhoAih.Fields.CTH_SEQ.toString()), SigValorReceitaVO.Fields.CTH_SEQ.toString())
				.add(Projections.property("eai." + FatEspelhoAih.Fields.SEQP.toString()), SigValorReceitaVO.Fields.EAI_SEQP.toString())
				.add(Projections.property("eai." + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), SigValorReceitaVO.Fields.IPH_COD_SUS_REALIZ.toString())
				.add(Projections.property("eai." + FatEspelhoAih.Fields.VALOR_PROCED_REALIZ.toString()), SigValorReceitaVO.Fields.VALOR_PROCED_REALIZ.toString())
				.add(Projections.property("eai." + FatEspelhoAih.Fields.VALOR_SH_REALIZ.toString()), SigValorReceitaVO.Fields.VALOR_SH_REALIZ.toString())
				.add(Projections.property("eai." + FatEspelhoAih.Fields.VALOR_SP_REALIZ.toString()), SigValorReceitaVO.Fields.VALOR_SP_REALIZ.toString())
				.add(Projections.property("coi." + FatContasInternacao.Fields.INT_SEQ.toString()), SigValorReceitaVO.Fields.INT_SEQ.toString());
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("coi." + FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq("cth." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq("eai." + FatEspelhoAih.Fields.SEQP.toString(), eaiSeqp));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SigValorReceitaVO.class));
		
		return executeCriteria(criteria);
	}

	protected IPacienteFacade getPacienteFacade() {
		return aIPacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aIAghuFacade;
	}
	
	public TotaisAIHGeralVO obterTotaisAIHGeralVO(DominioModuloCompetencia modulo, Byte mes, Short ano, Date dtHrInicio){
		TotaisAIHGerarlQueryBuilder builder = new TotaisAIHGerarlQueryBuilder();
		return (TotaisAIHGeralVO) executeCriteriaUniqueResult(builder.build(modulo, mes, ano, dtHrInicio));
	}


	public TotaisProcedEspHemotGeralVO obterTotaisProcedEspHemotGeralConsultaUm(DominioModuloCompetencia modulo, Byte mes, Short ano, Date dtHrInicio){
		TotaisProcedEspHemotGeralConsultaUmQueryBuilder builder = new TotaisProcedEspHemotGeralConsultaUmQueryBuilder();
		return (TotaisProcedEspHemotGeralVO) executeCriteriaUniqueResult(builder.build(modulo, mes, ano, dtHrInicio));
	}

	public TotaisProcedEspHemotGeralVO obterTotaisProcedEspHemotGeralConsultaDois(DominioModuloCompetencia modulo, Byte mes, Short ano, Date dtHrInicio, Byte codAtoOPM){
		TotaisProcedEspHemotGeralConsultaDoisQueryBuilder builder = new TotaisProcedEspHemotGeralConsultaDoisQueryBuilder();
		return (TotaisProcedEspHemotGeralVO) executeCriteriaUniqueResult(builder.build(modulo, mes, ano, dtHrInicio, codAtoOPM));
	}

	/**
	 * #36463 C1 
	 * Consulta para obter dados FatEspelhoEncerramentoPreviaVO utilizado na
	 * criação do relatório ESPELHO DA AIH
	 */
	public FatEspelhoEncerramentoPreviaVO obterFatEspelhoEncerramentoPreviaVO(Integer cthSeq){
		FatEspelhoEncerramentoPreviaVOQueryBuilder builder = new FatEspelhoEncerramentoPreviaVOQueryBuilder();
		return (FatEspelhoEncerramentoPreviaVO) executeCriteriaUniqueResult(builder.build(cthSeq));
	}

	public List<RelacaoDeOrtesesProtesesVO> obterRelacaoDeOrtesesProteses(final Long procedimento, final Integer ano, final Integer mes, final Date dtHrInicio, final String iniciaisPaciente,final Date dtIni,final Date dtFim) throws ApplicationBusinessException{
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class, "EAI");
		criteria.createAlias("EAI." + FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), "CTH", JoinType.INNER_JOIN);
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.ATOS_MEDICOS.toString(), "AAM", JoinType.INNER_JOIN);
		StringBuilder projectionSumValorApres = new StringBuilder(500);
		projectionSumValorApres.append("sum(COALESCE(aam2_.VALOR_ANESTESISTA,0)+COALESCE(aam2_.VALOR_PROCEDIMENTO,0)+COALESCE(aam2_.VALOR_SADT,0) +COALESCE(aam2_.VALOR_SERV_HOSP,0) +COALESCE(aam2_.VALOR_SERV_PROF,0)) as VALOR_APRES");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.PAC_NOME.toString()), RelacaoDeOrtesesProtesesVO.Fields.PAC_NOME.toString())
				.add(Projections.groupProperty("AAM." + FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()), RelacaoDeOrtesesProtesesVO.Fields.COD_ROPM.toString())
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), RelacaoDeOrtesesProtesesVO.Fields.COD_PROC.toString())
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.DCI_CODIGO_DCIH.toString()), RelacaoDeOrtesesProtesesVO.Fields.DCIH.toString())
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.NUMERO_AIH.toString()), RelacaoDeOrtesesProtesesVO.Fields.AIH.toString())
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.ENFERMARIA.toString()), RelacaoDeOrtesesProtesesVO.Fields.ENFERMARIA.toString())
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.LEITO.toString()), RelacaoDeOrtesesProtesesVO.Fields.NUMERO_LEITO.toString())
				.add(Projections.groupProperty("EAI." + FatEspelhoAih.Fields.PRONTUARIO.toString()), RelacaoDeOrtesesProtesesVO.Fields.PRONTUARIO.toString())
				.add(Projections.groupProperty("AAM." + FatAtoMedicoAih.Fields.QUANTIDADE.toString()), RelacaoDeOrtesesProtesesVO.Fields.QUANTIDADE.toString())
				.add(Projections.sum("AAM." + FatAtoMedicoAih.Fields.QUANTIDADE.toString()), RelacaoDeOrtesesProtesesVO.Fields.QTDE.toString())
				.add(Projections.sqlProjection(projectionSumValorApres.toString(), new String[]{"VALOR_APRES"}, new Type[]{BigDecimalType.INSTANCE}), RelacaoDeOrtesesProtesesVO.Fields.VALOR_APRES.toString())
				.add(Projections.groupProperty("CTH." + FatContasHospitalares.Fields.SEQ.toString()), RelacaoDeOrtesesProtesesVO.Fields.CTH_SEQ.toString())
				.add(Projections.groupProperty("CTH." + FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString()), RelacaoDeOrtesesProtesesVO.Fields.CPG_CPH_CSP_CNV_CODIGO.toString())
				.add(Projections.groupProperty("CTH." + FatContasHospitalares.Fields.CSP_SEQ.toString()), RelacaoDeOrtesesProtesesVO.Fields.CPG_CPH_CSP_SEQ.toString())
				.add(Projections.groupProperty("AAM." + FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString()), RelacaoDeOrtesesProtesesVO.Fields.IPH_PHO_SEQ.toString())
				.add(Projections.groupProperty("AAM." + FatAtoMedicoAih.Fields.IPH_SEQ.toString()), RelacaoDeOrtesesProtesesVO.Fields.IPH_SEQ.toString())
				);
		criteria.add(Restrictions.eq("EAI." + FatEspelhoAih.Fields.SEQP.toString(), 1));
		if (procedimento != null) {
			criteria.add(Restrictions.eq("EAI." + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString(), procedimento));
		} 
		criteria.add(Restrictions.sqlRestriction(obterSqlRestrictionSubQueryRelacaoOrtProt()));
		if (ano != null) {
			criteria.add(Restrictions.eq("EAI." + FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), Short.valueOf(ano.shortValue())));
		}
		if (dtHrInicio != null) {
			criteria.add(Restrictions.eq("EAI." + FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), dtHrInicio));
		}
		if (mes != null) {
			criteria.add(Restrictions.eq("EAI." + FatEspelhoAih.Fields.DCI_CPE_MES.toString(), Byte.valueOf(mes.byteValue())));
		}
		criteria.add(Restrictions.eq("EAI." + FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), DominioModuloCompetencia.INT));
		Integer atoMedico = buscarParametroAtoMedico();
		if (atoMedico != null) {
			criteria.add(Restrictions.eq("AAM." + FatAtoMedicoAih.Fields.TAO_SEQ.toString(), Byte.valueOf(atoMedico.byteValue())));
		}
		criteria.add(Restrictions.isNull("CTH." + FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		if (dtIni != null || dtFim != null) {
			criteria.add(Restrictions.sqlRestriction(getSubqueryCriteriaDatas(dtIni, dtFim)));
		}
		if (iniciaisPaciente != null && !iniciaisPaciente.isEmpty()) {
			criteria.add(Restrictions.sqlRestriction(" upper(substr({alias}.pac_nome,1,1) ) in ("+getIniciaisPaciente(iniciaisPaciente)+")"));
		}
		criteria.addOrder(Order.asc("EAI." + FatEspelhoAih.Fields.PAC_NOME.toString()));
		criteria.addOrder(Order.asc("EAI." + FatEspelhoAih.Fields.ENFERMARIA.toString()));
		criteria.addOrder(Order.asc("EAI." + FatEspelhoAih.Fields.LEITO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelacaoDeOrtesesProtesesVO.class));
		return executeCriteria(criteria);
	}

	private String getIniciaisPaciente(String iniciaisPaciente) {
		StringBuilder iniciais = new StringBuilder();

		String[] arrayIniciais = iniciaisPaciente.split(",");

		int count = 0;
		for (String s : arrayIniciais) {
			if (count > 0) {
				iniciais.append(',');
			}
			iniciais.append('\'').append(s).append('\'');
			count++;
		}
		return iniciais.toString();
	}

	private String getSubqueryCriteriaDatas(Date dtIni, Date dtFim) {
		//Query modificada, pois análise estava incorreta.
		//		Só deve fazer between se os dois estiverem preenchidos na tela. Senão faz >= ou <= dependendo de qual foi preenchido.
		StringBuilder query = new StringBuilder(300);
		if (dtIni != null && dtFim != null) {
			if (this.isOracle()) {
				query.append(" ( trunc(cth1_.DT_ALTA_ADMINISTRATIVA) between ");
				query.append(" trunc(to_date('").append(DateUtil.dataToString(dtIni, "ddMMyyyy")).append("', 'DDMMYYYY')) ");
				query.append(" AND trunc(to_date('").append(DateUtil.dataToString(dtFim, "ddMMyyyy")).append("', 'DDMMYYYY'))) ");
			} else {
				query.append(" ( date_trunc('day', cth1_.DT_ALTA_ADMINISTRATIVA) between");
				query.append(" date_trunc('day', to_date('").append(DateUtil.dataToString(dtIni, "ddMMyyyy")).append( "', 'DDMMYYYY')) ");
				query.append(" AND date_trunc('day', to_date('").append(DateUtil.dataToString(dtFim, "ddMMyyyy")).append( "', 'DDMMYYYY')))");
			}
		} else if (dtIni == null && dtFim !=null) {
			if (this.isOracle()) {
				query.append(" ( trunc(cth1_.DT_ALTA_ADMINISTRATIVA) <= ");
				query.append(" trunc(to_date('").append(DateUtil.dataToString(dtFim, "ddMMyyyy")).append("', 'DDMMYYYY'))) ");
			} else {
				query.append(" ( date_trunc('day', cth1_.DT_ALTA_ADMINISTRATIVA) <=");
				query.append(" date_trunc('day', to_date('").append(DateUtil.dataToString(dtFim, "ddMMyyyy")).append( "', 'DDMMYYYY'))) ");
			}
		} else if (dtIni != null && dtFim ==null) {
			if (this.isOracle()) {
				query.append(" ( trunc(cth1_.DT_ALTA_ADMINISTRATIVA) >= ");
				query.append(" trunc(to_date('").append(DateUtil.dataToString(dtIni, "ddMMyyyy")).append("', 'DDMMYYYY'))) ");
			} else {
				query.append(" ( date_trunc('day', cth1_.DT_ALTA_ADMINISTRATIVA) >=");
				query.append(" date_trunc('day', to_date('").append(DateUtil.dataToString(dtIni, "ddMMyyyy")).append( "', 'DDMMYYYY'))) ");
			}
		}
		return query.toString();
	}

	private Integer buscarParametroAtoMedico() throws ApplicationBusinessException {
		AghParametros parametro = aIParametroFacade.buscarAghParametro(AghuParametrosEnum.P_COD_ATO_OPM);
		return parametro == null ? null : (parametro.getVlrNumerico() == null ? null : parametro.getVlrNumerico().intValue()); 
	}

	private String obterSqlRestrictionSubQueryRelacaoOrtProt() {
		StringBuilder query = new StringBuilder(430);
		query.append(" (cth1_.ind_situacao <> '").append(DominioSituacaoConta.R).append('\'');
		query.append(" or (cth1_.ind_situacao='").append(DominioSituacaoConta.R).append('\'');
		
		query.append(" AND EXISTS "); 
		query.append(" (SELECT max(cthjn.jn_date_time) from agh.fat_competencias cpe, ");
		query.append("		agh.FAT_CONTAS_HOSPITALARES_jn cthjn");
		query.append(" WHERE cthjn.seq = cth1_.seq");
		query.append(" and cthjn.ind_situacao <> '").append(DominioSituacaoConta.R).append('\'');
		query.append(" and CPE.MODULO = '").append(DominioModuloCompetencia.INT).append('\'');
		query.append(" and CPE.ANO = {alias}.dci_cpe_ano");
		query.append(" and CPE.MES =  {alias}.dci_cpe_mes");
		if (this.isOracle()) {
			query.append(" and trunc(cthjn.jn_date_time) = trunc(CPE.DT_HR_FIM) ");
		} else {
			query.append(" and date_trunc('day', cthjn.jn_date_time)= date_trunc('day', CPE.DT_HR_FIM)");
		}		  
		query.append("  group by cthjn.jn_date_time))) ");
		
		return query.toString();
	}
	
	public List<FatEspelhoAih> obterPorCthSeq(Integer cthSeq) throws ApplicationBusinessException{
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoAih.class);
		criteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), cthSeq));

		return executeCriteria(criteria);
	}

}