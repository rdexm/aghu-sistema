package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
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

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCompProd;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.dominio.DominioTipoItemConta;
import br.gov.mec.aghu.faturamento.vo.AihsFaturadasPorClinicaVO;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.ContaNaoReapresentadaCPFVO;
import br.gov.mec.aghu.faturamento.vo.ContaNptVO;
import br.gov.mec.aghu.faturamento.vo.ContaRepresentadaVO;
import br.gov.mec.aghu.faturamento.vo.ContasPreenchimentoLaudosVO;
import br.gov.mec.aghu.faturamento.vo.FatMotivoRejeicaoContasVO;
import br.gov.mec.aghu.faturamento.vo.FaturamentoPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.MesAnoVO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSituacaoContaVO;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.faturamento.vo.ProtocoloAihVO;
import br.gov.mec.aghu.faturamento.vo.ProtocolosAihsVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOPMNaoFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.TotaisPorDCIHVO;
import br.gov.mec.aghu.faturamento.vo.ValoresAIHPorDCIHVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaProd;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimentoId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
public class FatContasHospitalaresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatContasHospitalares> {
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = -3002327260033807388L;

	public FatContasHospitalares pesquisaFatContasHospitalaresAbertaOuFechadaGrupoSUS(final Integer pCthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);

		criteria.createAlias(
				FatContasHospitalares.Fields.CONVENIO_SAUDE.toString(), 
				FatContasHospitalares.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), pCthSeq));
		
		criteria.add(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), new DominioSituacaoConta[] {
			DominioSituacaoConta.A, DominioSituacaoConta.F }));

		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CONVENIO_SAUDE.toString() + "."
				+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(),DominioGrupoConvenio.S));

		return (FatContasHospitalares) this.executeCriteriaUniqueResult(criteria);
	}
	
	public FatContasHospitalares obterFatContaHospitalarParaEstornoAIH(final Integer seq, final Long nrAIH) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.ne(FatContasHospitalares.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.NRO_AIH.toString(), nrAIH));		
		
		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ParCthSeqSsmVO> listarSsmParaListaCthSeq(final List<Integer> listaCthSeq, final Short cnvCodigo,
			final Byte cspSeq, final Short tipoGrupoContaSUS, DominioSituacaoSSM sitSsm) {
		StringBuilder hql = new StringBuilder(275);
		String phi = null;
		
		switch (sitSsm) {
		case S:
			phi = FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString();
			break;
		case R:
			phi = FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString();
			break;
		default:
			throw new IllegalArgumentException("Situacao SSM desconhecida: " + sitSsm);
		}
		
		hql.append(" select cthS.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" as cthSeq");
		// solicitado
		hql.append(" , iphS.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" || ' ' || substring(iphS.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(",1,35) ");
		hql.append(" as ssmStr");
		//solicitado
		hql.append(" from ");
		hql.append(FatContasHospitalares.class.getName());
		hql.append(" as cthS ");
		hql.append(" left join cthS.");
		hql.append(phi);
		hql.append(" as phiS ");
		hql.append(" left join phiS.");
		hql.append(FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString());
		hql.append(" as cgiS with ( ");
		hql.append(" 	cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" left join cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iphS ");
		//where
		//solicitado
		hql.append(" where cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append("  = :tipoGrupoContaSUS ");
		// cth
		hql.append(" and cthS.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" in (:cthSeq)");
		//query
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo).setParameter("cspSeq", cspSeq).setParameterList("cthSeq", listaCthSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS).setResultTransformer(Transformers.aliasToBean(ParCthSeqSsmVO.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoSolicitado(final Integer cthSeq, final Short cnvCodigo,
			final Byte cspSeq, final Short tipoGrupoContaSUS) {
		final StringBuilder hql = new StringBuilder(240);

		hql.append(" select iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" || ' ' || substring(iph.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(",1,35) ");
		hql.append(" from ");
		hql.append(FatContasHospitalares.class.getName());
		hql.append(" as cth ");
		hql.append(" left join cth.");
		hql.append(FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		hql.append(" as phi ");
		hql.append(" left join phi.");
		hql.append(FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" left join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iph ");
		hql.append(" where cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append("  = :tipoGrupoContaSUS ");
		hql.append(" and cth.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		query.setMaxResults(1);

		final List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoRealizado(final Integer cthSeq, final Short cnvCodigo,
			final Byte cspSeq, final Short tipoGrupoContaSUS) {
		final StringBuilder hql = new StringBuilder(240);

		hql.append(" select iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" || ' ' || substring(iph.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(",1,35) ");
		hql.append(" from ");
		hql.append(FatContasHospitalares.class.getName());
		hql.append(" as cth ");
		hql.append(" left join cth.");
		hql.append(FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString());
		hql.append(" as phi ");
		hql.append(" left join phi.");
		hql.append(FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" left join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iph ");
		hql.append(" where cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append("  = :tipoGrupoContaSUS ");
		hql.append(" and cth.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		query.setMaxResults(1);

		final List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	public FatContasHospitalares buscaContaBebe(final Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "fci");
		criteria.createAlias("fci."+ FatContasInternacao.Fields.INTERNACAO.toString(), "int");
		criteria.createAlias("int."+ AinInternacao.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd."+ AghAtendimentos.Fields.ATD_SEQ_MAE.toString(), "atdMae");
		criteria.add(Restrictions.eq("atdMae."+ AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(FatContasHospitalares.Fields.AIH.toString()));

		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public FatContasHospitalares buscaContaMae(final Integer atdSeqMae) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);

		criteria.createAlias(FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "fci");
		criteria.createAlias("fci." + FatContasInternacao.Fields.INTERNACAO.toString(), "int");
		criteria.createAlias("int." + AinInternacao.Fields.ATENDIMENTO.toString(), "atd");
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.SEQ.toString(), atdSeqMae));
		criteria.add(Restrictions.isNotNull(FatContasHospitalares.Fields.AIH.toString()));

		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	/**
	 * Verifica se a conta hospitalar possui os dados de parto preenchidos
	 * 
	 * @param cthSeq
	 * @return
	 */
	public Boolean isDadosPartoPreenchidos(final Integer cthSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.or(
				Restrictions.isNotNull(FatContasHospitalares.Fields.RN_NASCIDO_VIVO.toString()),
				Restrictions.isNotNull(FatContasHospitalares.Fields.RN_NASCIDO_MORTO.toString())));
		criteria.add(Restrictions.or(
				Restrictions.isNotNull(FatContasHospitalares.Fields.RN_SAIDA_ALTA.toString()),
				Restrictions.or(
						Restrictions.isNotNull(FatContasHospitalares.Fields.RN_SAIDA_TRANSFERENCIA.toString()),
						Restrictions.isNotNull(FatContasHospitalares.Fields.RN_SAIDA_OBITO.toString()))));

		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), cthSeq));

		return executeCriteriaExists(criteria);
	}

	/**
	 * Verifica se a conta hospitalar possui os dados de nro sisprenatal preenchidos
	 * @param cthSeq
	 * @return
	 */
	public Boolean isDadosSisprenatalPreenchidos(final Integer cthSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.isNotNull(FatContasHospitalares.Fields.NRO_SISPRENATAL.toString()));
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), cthSeq));
		return executeCriteriaExists(criteria);
	}

	/**
	 * Método para buscar conta hospitalar ativa vonculada a atendimento. Usado
	 * HQL por causa da falta de relação direta entre POJOS AghAtendimentos e
	 * FatContasHospitalares/FatContasInternacao.
	 * 
	 * Esse método representa o cursor "conta" de
	 * AINK_INT_RN.RN_INTP_ATU_SSM_CNTA
	 * 
	 * @param seqInternacao
	 */
	@SuppressWarnings("unchecked")
	public List<FatContasInternacao> pesquisarNumeroAihContaHospitalarAtendimento(final Integer seqInternacao) {
		final StringBuilder hql = new StringBuilder(200);

		hql.append("select cti ");
		hql.append("from FatContasInternacao cti, AghAtendimentos atd ");

		// WHERE atd.int_Seq =  p_int_seq
		hql.append("where atd.")
		.append(AghAtendimentos.Fields.INTERNACAO.toString())
		.append('.').append(AinInternacao.Fields.SEQ.toString())
		.append(" = :seqInternacao ");

		// AND  cti.int_seq = p_int_seq
		hql.append("and cti.")
		.append(FatContasInternacao.Fields.INTERNACAO.toString())
		.append('.').append(AinInternacao.Fields.SEQ.toString())
		.append(" = :seqInternacao ");

		// AND cth.dt_int_administrativa = atd.dthr_inicio
		hql.append("and cti.")
		.append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append('.')
		.append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA
				.toString()).append(" = atd.").append(AghAtendimentos.Fields.DTHR_INICIO.toString()).append(' ');

		// AND cth.ind_situacao = 'A'
		hql.append("and cti.")
		.append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append('.').append(FatContasHospitalares.Fields.IND_SITUACAO.toString()).append(" = '").append(DominioSituacaoConta.A.toString()).append('\'');
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqInternacao", seqInternacao);

		return query.list();
	}

	/**
	 * Método para obter o da view V_FAT_ASSOCIACAO_PROCEDIMENTOS o procedimento
	 * hospitalar inserido.
	 * 
	 * Esse método representa o cursor "c_phi" de
	 * AINK_INT_RN.RN_INTP_ATU_SSM_CNTA
	 * 
	 * @param seqPho
	 * @param seqIph
	 * @return
	 */
	public VFatAssociacaoProcedimento obterAssociacaoProcedimento(final Short seqPho, final Integer seqIph) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), seqPho));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString(), seqIph));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), (short) 1));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), (byte) 1));

		final List<VFatAssociacaoProcedimento> lista = executeCriteria(criteria, 0, 1, null, true);

		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	/**
		 * @return somente os campos: iphPhoSeq e iphSeq
		 */
		public VFatAssociacaoProcedimentoId obterIphPhoPorPhiRealizado(Integer cthSeq, Short seqPho){
			
			StringBuilder hql = new StringBuilder(230);
			hql	
				.append("select ")
				.append("v_fat.iph_seq as iphSeq,")
				.append("v_fat.iph_pho_seq as iphPhoSeq ")
				.append(" from ")
				.append("FAT_CONTAS_HOSPITALARES cth,")
				.append("V_FAT_ASSOCIACAO_PROCEDIMENTOS v_fat  ")
				.append(" where ")
				.append(" cth.seq =  :pCthSeq")
				.append(" and cth.phi_seq = v_fat.phi_seq ")
				.append(" and v_fat.iph_pho_seq = :pIphPhoSeq");
			
			
			
			SQLQuery query = createSQLQuery(hql.toString());
			query.setParameter("pCthSeq", cthSeq);
			query.setParameter("pIphPhoSeq", seqPho);
			
			List<VFatAssociacaoProcedimentoId> list = query.addScalar("iphSeq", IntegerType.INSTANCE)
				 .addScalar("iphPhoSeq", ShortType.INSTANCE).setResultTransformer(Transformers.aliasToBean(VFatAssociacaoProcedimentoId.class)).list();
			
			if(!list.isEmpty()){
				return list.get(0);
			} else {
				return null;
			}
		}
	
	

	/**
	 * Lista conta por conta reapresentada e situacao
	 * 
	 * @param cthSeq
	 * @param situacao
	 * @return
	 */
	public List<FatContasHospitalares> listarPorCthReapresentadaSituacao(Integer cthSeq, DominioSituacaoConta[] situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString(), FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacao));
		return executeCriteria(criteria);
	}
	
	/**
	 * Recupera todas as contas hospitalares de acordo com uma determinada competencia
	 * 
	 * @param competencia
	 * @param dtInicial
	 * @param dtFinal
	 * @param procedimentoSUS
	 * @return
	 */
	public List<Integer> obterContasHospitalaresPorCompetenciaEProcedimentoSUS(final FatCompetencia competencia, final Date dtInicial, final Date dtFinal, final Long procedimentoSUS){
		final DetachedCriteria subCteria = DetachedCriteria.forClass(FatDocumentoCobrancaAihs.class);
		subCteria.createAlias(FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString(), FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString());
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), competencia.getId().getDtHrInicio()));
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.INT));
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), competencia.getId().getMes()));
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), competencia.getId().getAno()));
		subCteria.setProjection(Projections.property(FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString()));
		
		final List<String> subResult = executeCriteria(subCteria);
		
		if(subResult != null && !subResult.isEmpty()){
			final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "cth");
			
			if(dtInicial != null){
				criteria.add(Restrictions.ge(FatContasHospitalares.Fields.DT_ENCERRAMENTO.toString(), dtInicial));
			}
			
			if(dtFinal != null){
				criteria.add(Restrictions.lt(FatContasHospitalares.Fields.DT_ENCERRAMENTO.toString(), DateUtil.adicionaDias(dtFinal, 1)));
			}
			
			criteria.add(Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.E));
			criteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));
			criteria.createAlias(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString());
			criteria.add(Restrictions.in(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString()+"."+FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), subResult ));
			
			if(procedimentoSUS != null){
				final DetachedCriteria subCteria1 = DetachedCriteria.forClass(FatEspelhoAih.class, "esp");
				subCteria1.add(Restrictions.eqProperty("cth."+FatContasHospitalares.Fields.SEQ.toString(), "esp."+FatEspelhoAih.Fields.CTH_SEQ.toString()));
				subCteria1.setProjection(Projections.property(FatEspelhoAih.Fields.CTH_SEQ.toString()));
				subCteria1.add( Restrictions.or(
													Restrictions.eq(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString(), procedimentoSUS),	
													Restrictions.eq(FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.toString(), procedimentoSUS)
												) 
							   );
				
				final DetachedCriteria subCteria2 = DetachedCriteria.forClass(FatAtoMedicoAih.class, "atm");
				subCteria2.add(Restrictions.eqProperty("atm."+FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), "cth."+FatContasHospitalares.Fields.SEQ.toString()));
				subCteria2.setProjection(Projections.property(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()));
				subCteria2.add(Restrictions.eq(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString(), procedimentoSUS));
				criteria.add(Restrictions.or(Subqueries.exists(subCteria1),Subqueries.exists(subCteria2)));
			}
			return executeCriteria(criteria); 
		}
		return null;
	}
	

	/**
	 * Recupera todas as contas hospitalares cobradas para competencias encerradas
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public List<FatContasHospitalares> obterContasHospitalaresCobradasParaCompetenciasEncerradas(final FatCompetencia competencia){
		final DetachedCriteria subCteria = DetachedCriteria.forClass(FatDocumentoCobrancaAihs.class);
		subCteria.setProjection(Projections.property(FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString()));
		subCteria.add(Restrictions.ge(FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), DateUtil.obterDataComHoraInical(competencia.getId().getDtHrInicio())));
		subCteria.add(Restrictions.le(FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), DateUtil.obterDataComHoraFinal(competencia.getId().getDtHrInicio())));
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.INT));
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), competencia.getId().getMes()));
		subCteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), competencia.getId().getAno()));
		subCteria.createAlias(FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString(), FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString());
		
		final List<String> subResult = executeCriteria(subCteria);
		
		if(subResult != null && !subResult.isEmpty()){
			final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "cth");
			
			criteria.add(Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.E));
			criteria.add(Restrictions.eq(FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString(), DominioSimNao.S.toString()));
			criteria.add(Restrictions.isNotNull(FatContasHospitalares.Fields.NRO_AIH.toString()));
			criteria.createAlias(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString());
			criteria.add(Restrictions.in(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString()+"."+FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), subResult ));
			
			return executeCriteria(criteria);
		}
		return null;
	}
	
	public List<Integer> obterFatContasHospitalaresNaoAutorizadas() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.E));
		criteria.add(Restrictions.or(
				Restrictions.eq(FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString(), DominioSimNao.N.toString()), 
				Restrictions.isNull(FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString())));
		return this.executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPorContaFilha(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CTH.toString(), FatContasHospitalares.Fields.CTH.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CTH_SEQ.toString(), cthSeq));
		return criteria;
	}
	
	protected DetachedCriteria obterCriteriaListaCthSeqPorContaFilha(Integer... cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CTH.toString(), FatContasHospitalares.Fields.CTH.toString());
		criteria.add(Restrictions.in(FatContasHospitalares.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.setProjection(Projections.property(FatContasHospitalares.Fields.CTH_SEQ.toString()));
		return criteria;
	}
	
	public List<Integer> listarCthSeqPorFilha(Integer... cthSeq) {
		DetachedCriteria criteria = this.obterCriteriaListaCthSeqPorContaFilha(cthSeq);
		return this.executeCriteria(criteria);
	}

	/**
	 * Primeira conta conta por conta filha
	 * 
	 * @param cthSeq
	 * @return
	 */
	public FatContasHospitalares primeiraContaPorFilha(Integer cthSeq) {
		DetachedCriteria criteria = obterCriteriaPorContaFilha(cthSeq);
		
		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	/**
	 * Parte do cursor <code>FATF_ARQ_TXT_INT.BUSCA_CONTA.C_CTH_PER</code>
	 * @return
	 */
	protected DetachedCriteria obterCriteriaPorConvenioListaIndSituacao(Short cnvCodigo, Byte cspSeq, DominioSituacaoConta... indSituacao) {
		DetachedCriteria result = DetachedCriteria.forClass(FatContasHospitalares.class);
		result.add(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), indSituacao));
		result.add(Restrictions.eq(FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString(), cnvCodigo));
		result.add(Restrictions.eq(FatContasHospitalares.Fields.CSP_SEQ.toString(), cspSeq));
		return result;
	}

	protected DetachedCriteria obterCriteriaPorConvenioIntAltaDataListaSeqListaSit(List<Integer> listaCthSeq, Short cnvCodigo, Byte cspSeq, Date data, DominioSituacaoConta... indSituacao) {
		DetachedCriteria result = this.obterCriteriaPorConvenioListaIndSituacao(cnvCodigo, cspSeq, indSituacao);
		result.add(Restrictions.in(FatContasHospitalares.Fields.SEQ.toString(), listaCthSeq));
		result.add(Restrictions.le(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString(), data));
		result.add(Restrictions.or(
				Restrictions.isNull(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()),
				Restrictions.ge(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(), data)));
		return result;		
	}

	public FatContasHospitalares obterListaPorConvenioIntAltaDataListaSeqListaSitOrdenadoPorSeqPrimeiraEntrada(List<Integer> listaCthSeq, Short cnvCodigo, Byte cspSeq, Date data, DominioSituacaoConta... indSituacao) {
		List<FatContasHospitalares> partial = null;
		DetachedCriteria criteria = this.obterCriteriaPorConvenioIntAltaDataListaSeqListaSit(listaCthSeq, cnvCodigo, cspSeq, data, indSituacao);
		partial = this.executeCriteria(criteria, 0, 1, FatContasHospitalares.Fields.SEQ.toString(), false);
		if ((partial != null) && !partial.isEmpty()) {
			return partial.get(0);
		}
		return null;
	}
	
	/**
	 * Seleciona todas as contas hospitalares do grupo SUS, cuja data da 
     * alta administrativa não é nula e a situação está fechada, ordenadas pelo seu número 
     * (sequencia)
	 * @return
	 */
	public List<Integer> obterContasGrupoSUSComDataAltaNaoNula() {
		final DetachedCriteria subCriteria  = DetachedCriteria.forClass(FatConvenioSaude.class);
		subCriteria.setProjection(Projections.property(FatConvenioSaude.Fields.CODIGO.toString()));
		subCriteria.add(Restrictions.eq(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		final List<Short> codigos = executeCriteria(subCriteria);
		
		if(codigos != null && !codigos.isEmpty()){
			final DetachedCriteria criteria  = DetachedCriteria.forClass(FatContasHospitalares.class);
			criteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));
			criteria.add(Restrictions.isNotNull(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()));
			criteria.add(Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.F));
			criteria.add(Restrictions.in(FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString(), codigos));
			criteria.addOrder(Order.asc(FatContasHospitalares.Fields.SEQ.toString()));
			return executeCriteria(criteria);
		}
		return null;
	}

	public Long countContasHospitalaresQuePossuemAIH(Long nroAih) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.AIH.toString(), FatContasHospitalares.Fields.AIH.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.NRO_AIH.toString(), nroAih));
		return executeCriteriaCount(criteria);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void pesquisarMensagensErroQueryOracle(StringBuilder sql, DominioSituacaoMensagemLog grupo, String erro, String nome, Boolean reapresentada) {
				sql.append("SELECT");
				sql.append(" DISTINCT");
				sql.append(" LER.CTH_SEQ as cthseq");
				sql.append(" ,PAC.PRONTUARIO as prontuario");
				sql.append(" ,PAC.NOME as nome");
				sql.append(" ,COALESCE(INTER.LTO_LTO_ID,COALESCE(QRT.DESCRICAO,CAST(UNF.ANDAR AS VARCHAR2(30))||' '||UNF.IND_ALA)) as leito");
				sql.append(" ,CTH.DT_INT_ADMINISTRATIVA as dtIntAdm");
				sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA as dtAltAdm");
				sql.append(" ,CTH.NRO_AIH as nroAih");
				sql.append(" ,CAST(MSP.CODIGO_SUS AS VARCHAR2(30))||CAST(SIA.CODIGO_SUS AS VARCHAR2(30)) as mspSia");
				sql.append(" ,MAX(INTER.SEQ) as intseq");
				sql.append(" ,LER.ERRO as erro");
				sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END as desdobr");
				sql.append(" ,CTH.PHI_SEQ_REALIZADO as phirealizado");
				sql.append(" ,CTH.CSP_CNV_CODIGO as cspcnvcodigo");
				sql.append(" ,CTH.CSP_SEQ as cspseq");
			sql.append(" FROM agh.FAT_CONTAS_HOSPITALARES CTH");
			sql.append(" LEFT JOIN agh.FAT_SITUACOES_SAIDA_PACIENTE SIA");
				sql.append(" ON SIA.SEQ = CTH.SIA_SEQ");
				sql.append(" AND SIA.MSP_SEQ = CTH.SIA_MSP_SEQ");
			sql.append(" LEFT JOIN agh.FAT_MOTIVOS_SAIDA_PACIENTE MSP");
				sql.append(" ON MSP.SEQ = SIA.MSP_SEQ");
			sql.append(" LEFT JOIN agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS ASP");
				sql.append(" ON ASP.PHI_SEQ  = COALESCE(CTH.PHI_SEQ_REALIZADO, COALESCE(CTH.PHI_SEQ,1))");
				sql.append(" AND ASP.CPG_CPH_CSP_CNV_CODIGO = CTH.CSP_CNV_CODIGO");
				sql.append(" AND ASP.CPG_CPH_CSP_SEQ = CTH.CSP_SEQ");
				sql.append(" AND ASP.IPH_IND_SITUACAO = :situacaoProcedimento ");
				sql.append(" AND ASP.IPH_IND_TIPO_AIH5 <> :tipoAIH5 ");
			sql.append(" ,agh.FAT_CONTAS_INTERNACAO COI");
			sql.append(" ,agh.AIP_PACIENTES PAC");
			sql.append(" ,agh.FAT_LOG_ERRORS LER");
			sql.append(" ,agh.AIN_INTERNACOES INTER");
			sql.append(" LEFT JOIN agh.AGH_UNIDADES_FUNCIONAIS UNF");
				sql.append(" ON UNF.SEQ = INTER.UNF_SEQ");
			sql.append(" LEFT JOIN agh.AIN_QUARTOS QRT");
				sql.append(" ON INTER.QRT_NUMERO = QRT.NUMERO");
			sql.append(" WHERE");
			sql.append(" LER.MODULO = :modulo");
			sql.append(" AND PAC.CODIGO = INTER.PAC_CODIGO");
			sql.append(" AND INTER.SEQ = COI.INT_SEQ");		
			sql.append(" AND COI.CTH_SEQ = CTH.SEQ");
			sql.append(" AND CTH.SEQ = LER.CTH_SEQ");
			sql.append(" AND CTH.IND_SITUACAO IN(:indSituacaoConta)");
			sql.append(" AND EXISTS");
				sql.append(" (");
					sql.append(" SELECT 1");
					sql.append(" FROM agh.FAT_MENSAGENS_LOG MSL");
					sql.append(" WHERE");
					sql.append(" LER.ERRO LIKE MSL.ERRO || '%' ");
					if(grupo != null) {
						sql.append(" AND MSL.SITUACAO = :grupo");
						sql.append(" AND MSL.ERRO LIKE '%'");
		
					}
					else {
						sql.append(" AND MSL.SITUACAO = MSL.SITUACAO");
						if(erro != null) {
							sql.append(" AND MSL.ERRO LIKE '%'||:erro||'%'");
						}
						else {
							sql.append(" AND MSL.ERRO LIKE '%'");
						}
					}
				sql.append(" )");
			sql.append(" AND LER.CRIADO_EM > :dataInicial");
			sql.append(" AND LER.CRIADO_EM < :dataFinal");
			if(reapresentada) {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NOT NULL");
			}
			else {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NULL");
			}
			if(StringUtils.isNotBlank(nome)) {
				sql.append(" and upper(substr(PAC.NOME,1,1)) in (:letrasNome)");
			}
			sql.append(" GROUP BY");
			sql.append(" LER.CTH_SEQ");
			sql.append(" ,PAC.PRONTUARIO");
			sql.append(" ,PAC.NOME");
			sql.append(" ,COALESCE(INTER.LTO_LTO_ID,COALESCE(QRT.DESCRICAO,CAST(UNF.ANDAR AS VARCHAR2(30))||' '||UNF.IND_ALA))");
			sql.append(" ,CTH.DT_INT_ADMINISTRATIVA");
			sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA");
			sql.append(" ,CTH.NRO_AIH");
			sql.append(" ,CAST(MSP.CODIGO_SUS AS VARCHAR2(30))||CAST(SIA.CODIGO_SUS AS VARCHAR2(30))");
			sql.append(" ,LER.ERRO");
			sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END");
			sql.append(" ,CTH.PHI_SEQ_REALIZADO");
			sql.append(" ,CTH.CSP_CNV_CODIGO");
			sql.append(" ,CTH.CSP_SEQ");
			
			sql.append(" UNION ");
			
		
			sql.append("SELECT");
				sql.append(" DISTINCT");
				sql.append(" LER.CTH_SEQ as cthseq");
				sql.append(" ,PAC.PRONTUARIO as prontuario");
				sql.append(" ,PAC.NOME as nome");
				sql.append(" ,'S/INT' as leito");
				sql.append(" ,CTH.DT_INT_ADMINISTRATIVA as dtIntAdm");
				sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA as dtAltAdm");
				sql.append(" ,CTH.NRO_AIH as nro_aih");
				sql.append(" ,CAST(MSP.CODIGO_SUS AS VARCHAR2(30))||CAST(SIA.CODIGO_SUS AS VARCHAR2(30)) as mspSia");
				sql.append(" ,MAX(DCS.SEQ) as intseq");
				sql.append(" ,LER.ERRO as erro");
				sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END as desdobr");
				sql.append(" ,CTH.PHI_SEQ_REALIZADO as phirealizado");
				sql.append(" ,CTH.CSP_CNV_CODIGO as cspcnvcodigo");
				sql.append(" ,CTH.CSP_SEQ as cspseq");
			sql.append(" FROM agh.FAT_CONTAS_HOSPITALARES CTH");
			sql.append(" LEFT JOIN agh.FAT_SITUACOES_SAIDA_PACIENTE SIA");
				sql.append(" ON SIA.SEQ = CTH.SIA_SEQ");
				sql.append(" AND SIA.MSP_SEQ = CTH.SIA_MSP_SEQ");
			sql.append(" LEFT JOIN agh.FAT_MOTIVOS_SAIDA_PACIENTE MSP");
				sql.append(" ON MSP.SEQ = SIA.MSP_SEQ");
			sql.append(" LEFT JOIN agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS ASP");
				sql.append(" ON ASP.PHI_SEQ  = COALESCE(CTH.PHI_SEQ_REALIZADO, COALESCE(CTH.PHI_SEQ,1))");
				sql.append(" AND ASP.CPG_CPH_CSP_CNV_CODIGO = CTH.CSP_CNV_CODIGO");
				sql.append(" AND ASP.CPG_CPH_CSP_SEQ = CTH.CSP_SEQ");
				sql.append(" AND ASP.IPH_IND_SITUACAO = :situacaoProcedimento ");
				sql.append(" AND ASP.IPH_IND_TIPO_AIH5 <> :tipoAIH5 ");
			sql.append(" ,agh.AIP_PACIENTES PAC");
			sql.append(" ,agh.FAT_CONTAS_INTERNACAO COI");
			sql.append(" LEFT JOIN agh.FAT_DADOS_CONTA_SEM_INT DCS");
				sql.append(" ON DCS.SEQ = COI.DCS_SEQ");
			sql.append(" LEFT JOIN agh.AGH_UNIDADES_FUNCIONAIS UNF");
				sql.append(" ON UNF.SEQ = DCS.UNF_SEQ");
			sql.append(" ,agh.FAT_LOG_ERRORS LER");
			sql.append(" WHERE");
			sql.append(" LER.MODULO = :modulo");
			sql.append(" AND PAC.CODIGO = DCS.PAC_CODIGO");
			sql.append(" AND DCS.SEQ = COI.DCS_SEQ");		
			sql.append(" AND COI.CTH_SEQ = CTH.SEQ");
			sql.append(" AND CTH.SEQ = LER.CTH_SEQ");
			sql.append(" AND CTH.IND_SITUACAO IN(:indSituacaoConta)");
			sql.append(" AND EXISTS");
				sql.append(" (");
					sql.append(" SELECT 1");
					sql.append(" FROM agh.FAT_MENSAGENS_LOG MSL");
					sql.append(" WHERE");
					sql.append(" LER.ERRO LIKE MSL.ERRO || '%' ");
					if(grupo != null) {
						sql.append(" AND MSL.SITUACAO = :grupo");
						sql.append(" AND MSL.ERRO LIKE '%'");
		
					}
					else {
						sql.append(" AND MSL.SITUACAO = MSL.SITUACAO");
						if(erro != null) {
							sql.append(" AND MSL.ERRO LIKE '%'||:erro||'%'");
						}
						else {
							sql.append(" AND MSL.ERRO LIKE '%'");
						}
					}
				sql.append(" )");
			sql.append(" AND LER.CRIADO_EM > :dataInicial");
			sql.append(" AND LER.CRIADO_EM < :dataFinal");
			if(reapresentada) {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NOT NULL");
			}
			else {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NULL");
			}
			if(StringUtils.isNotBlank(nome)) {
				sql.append(" and upper(substr(PAC.NOME,1,1)) in (:letrasNome)");
			}
			sql.append(" GROUP BY");
			sql.append(" LER.CTH_SEQ");
			sql.append(" ,PAC.PRONTUARIO");
			sql.append(" ,PAC.NOME");
			sql.append(" ,'S/INT'");
			sql.append(" ,CTH.DT_INT_ADMINISTRATIVA");
			sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA");
			sql.append(" ,CTH.NRO_AIH");
			sql.append(" ,CAST(MSP.CODIGO_SUS AS VARCHAR2(30))||CAST(SIA.CODIGO_SUS AS VARCHAR2(30))");
			sql.append(" ,LER.ERRO");
			sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END");
			sql.append(" ,CTH.PHI_SEQ_REALIZADO");
			sql.append(" ,CTH.CSP_CNV_CODIGO");
			sql.append(" ,CTH.CSP_SEQ");
			sql.append(" ORDER BY erro, dtAltAdm, nome");
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PendenciaEncerramentoVO> pesquisarMensagensErro(Date dtInicial, Date dtFinal, DominioSituacaoMensagemLog grupo, String erro, String nome, Boolean reapresentada) {

		//QUERY NATIVA - POSTGRESQL
		StringBuilder sql = new StringBuilder(4000);
		if(isOracle()) {
			this.pesquisarMensagensErroQueryOracle(sql, grupo, erro, nome, reapresentada);
		}
		else {
			sql.append("SELECT");
				sql.append(" DISTINCT");
				sql.append(" LER.CTH_SEQ as cthseq");
				sql.append(" ,PAC.PRONTUARIO as prontuario");
				sql.append(" ,PAC.NOME as nome");
				sql.append(" ,COALESCE(INTER.LTO_LTO_ID,COALESCE(QRT.DESCRICAO,CAST(UNF.ANDAR AS TEXT)||' '||UNF.IND_ALA)) as leito");
				sql.append(" ,CTH.DT_INT_ADMINISTRATIVA as dtIntAdm");
				sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA as dtAltAdm");
				sql.append(" ,CTH.NRO_AIH as nroAih");
				sql.append(" ,CAST(MSP.CODIGO_SUS AS TEXT)||CAST(SIA.CODIGO_SUS AS TEXT) as mspSia");
				sql.append(" ,MAX(INTER.SEQ) as intseq");
				sql.append(" ,LER.ERRO as erro");
				sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END as desdobr");
				sql.append(" ,CTH.PHI_SEQ_REALIZADO as phirealizado");
				sql.append(" ,CTH.CSP_CNV_CODIGO as cspcnvcodigo");
				sql.append(" ,CTH.CSP_SEQ as cspseq");
			sql.append(" FROM agh.FAT_CONTAS_HOSPITALARES as CTH");
			sql.append(" LEFT JOIN agh.FAT_SITUACOES_SAIDA_PACIENTE as SIA");
				sql.append(" ON SIA.SEQ = CTH.SIA_SEQ");
				sql.append(" AND SIA.MSP_SEQ = CTH.SIA_MSP_SEQ");
			sql.append(" LEFT JOIN agh.FAT_MOTIVOS_SAIDA_PACIENTE as MSP");
				sql.append(" ON MSP.SEQ = SIA.MSP_SEQ");
			sql.append(" LEFT JOIN agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS as ASP");
				sql.append(" ON ASP.PHI_SEQ  = COALESCE(CTH.PHI_SEQ_REALIZADO, COALESCE(CTH.PHI_SEQ,1))");
				sql.append(" AND ASP.CPG_CPH_CSP_CNV_CODIGO = CTH.CSP_CNV_CODIGO");
				sql.append(" AND ASP.CPG_CPH_CSP_SEQ = CTH.CSP_SEQ");
				sql.append(" AND ASP.IPH_IND_SITUACAO = :situacaoProcedimento ");
				sql.append(" AND ASP.IPH_IND_TIPO_AIH5 <> :tipoAIH5 ");
			sql.append(" ,agh.FAT_CONTAS_INTERNACAO as COI");
			sql.append(" ,agh.AIP_PACIENTES as PAC");
			sql.append(" ,agh.FAT_LOG_ERRORS as LER");
			sql.append(" ,agh.AIN_INTERNACOES as INTER");
			sql.append(" LEFT JOIN agh.AGH_UNIDADES_FUNCIONAIS as UNF");
				sql.append(" ON UNF.SEQ = INTER.UNF_SEQ");
			sql.append(" LEFT JOIN agh.AIN_QUARTOS as QRT");
				sql.append(" ON INTER.QRT_NUMERO = QRT.NUMERO");
			sql.append(" WHERE");
			sql.append(" LER.MODULO = :modulo");
			sql.append(" AND PAC.CODIGO = INTER.PAC_CODIGO");
			sql.append(" AND INTER.SEQ = COI.INT_SEQ");		
			sql.append(" AND COI.CTH_SEQ = CTH.SEQ");
			sql.append(" AND CTH.SEQ = LER.CTH_SEQ");
			sql.append(" AND CTH.IND_SITUACAO IN(:indSituacaoConta)");
			sql.append(" AND EXISTS");
				sql.append(" (");
					sql.append(" SELECT 1");
					sql.append(" FROM agh.FAT_MENSAGENS_LOG as MSL");
					sql.append(" WHERE");
					sql.append(" LER.ERRO LIKE MSL.ERRO || '%' ");
					if(grupo != null) {
						sql.append(" AND MSL.SITUACAO = :grupo");
						sql.append(" AND MSL.ERRO LIKE '%'");
	
					}
					else {
						sql.append(" AND MSL.SITUACAO = MSL.SITUACAO");
						if(erro != null) {
							sql.append(" AND MSL.ERRO LIKE '%'||:erro||'%'");
						}
						else {
							sql.append(" AND MSL.ERRO LIKE '%'");
						}
					}
				sql.append(" )");
			sql.append(" AND LER.CRIADO_EM > :dataInicial");
			sql.append(" AND LER.CRIADO_EM < :dataFinal");
			if(reapresentada) {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NOT NULL");
			}
			else {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NULL");
			}
			if(StringUtils.isNotBlank(nome)) {
				sql.append("  AND UPPER(SUBSTRING(PAC.NOME FROM 1 FOR 1)) IN (:letrasNome)");
			}
			sql.append(" GROUP BY");
			sql.append(" LER.CTH_SEQ");
			sql.append(" ,PAC.PRONTUARIO");
			sql.append(" ,nome");
			sql.append(" ,leito");
			sql.append(" ,dtIntAdm");
			sql.append(" ,dtAltAdm");
			sql.append(" ,nroAih");
			sql.append(" ,mspSia");
			sql.append(" ,erro");
			sql.append(" ,desdobr");
			sql.append(" ,phirealizado");
			sql.append(" ,cspcnvcodigo");
			sql.append(" ,cspseq");
			
			sql.append(" UNION ");
			
	
			sql.append("SELECT");
				sql.append(" DISTINCT");
				sql.append(" LER.CTH_SEQ as cthseq");
				sql.append(" ,PAC.PRONTUARIO as prontuario");
				sql.append(" ,PAC.NOME as nome");
				sql.append(" ,'S/INT' as leito");
				sql.append(" ,CTH.DT_INT_ADMINISTRATIVA as dt_int_adm");
				sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA as dt_alt_adm");
				sql.append(" ,CTH.NRO_AIH as nro_aih");
				sql.append(" ,CAST(MSP.CODIGO_SUS AS TEXT)||CAST(SIA.CODIGO_SUS AS TEXT) as msp_sia");
				sql.append(" ,MAX(DCS.SEQ) as intseq");
				sql.append(" ,LER.ERRO as erro");
				sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END as desdobr");
				sql.append(" ,CTH.PHI_SEQ_REALIZADO as phirealizado");
				sql.append(" ,CTH.CSP_CNV_CODIGO as cspcnvcodigo");
				sql.append(" ,CTH.CSP_SEQ as cspseq");
			sql.append(" FROM agh.FAT_CONTAS_HOSPITALARES as CTH");
			sql.append(" LEFT JOIN agh.FAT_SITUACOES_SAIDA_PACIENTE as SIA");
				sql.append(" ON SIA.SEQ = CTH.SIA_SEQ");
				sql.append(" AND SIA.MSP_SEQ = CTH.SIA_MSP_SEQ");
			sql.append(" LEFT JOIN agh.FAT_MOTIVOS_SAIDA_PACIENTE as MSP");
				sql.append(" ON MSP.SEQ = SIA.MSP_SEQ");
			sql.append(" LEFT JOIN agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS as ASP");
				sql.append(" ON ASP.PHI_SEQ  = COALESCE(CTH.PHI_SEQ_REALIZADO, COALESCE(CTH.PHI_SEQ,1))");
				sql.append(" AND ASP.CPG_CPH_CSP_CNV_CODIGO = CTH.CSP_CNV_CODIGO");
				sql.append(" AND ASP.CPG_CPH_CSP_SEQ = CTH.CSP_SEQ");
				sql.append(" AND ASP.IPH_IND_SITUACAO = :situacaoProcedimento ");
				sql.append(" AND ASP.IPH_IND_TIPO_AIH5 <> :tipoAIH5 ");
			sql.append(" ,agh.AIP_PACIENTES as PAC");
			sql.append(" ,agh.FAT_CONTAS_INTERNACAO as COI");
			sql.append(" LEFT JOIN agh.FAT_DADOS_CONTA_SEM_INT as DCS");
				sql.append(" ON DCS.SEQ = COI.DCS_SEQ");
			sql.append(" LEFT JOIN agh.AGH_UNIDADES_FUNCIONAIS as UNF");
				sql.append(" ON UNF.SEQ = DCS.UNF_SEQ");
			sql.append(" ,agh.FAT_LOG_ERRORS as LER");
			sql.append(" WHERE");
			sql.append(" LER.MODULO = :modulo");
			sql.append(" AND PAC.CODIGO = DCS.PAC_CODIGO");
			sql.append(" AND DCS.SEQ = COI.DCS_SEQ");		
			sql.append(" AND COI.CTH_SEQ = CTH.SEQ");
			sql.append(" AND CTH.SEQ = LER.CTH_SEQ");
			sql.append(" AND CTH.IND_SITUACAO IN(:indSituacaoConta)");
			sql.append(" AND EXISTS");
				sql.append(" (");
					sql.append(" SELECT 1");
					sql.append(" FROM agh.FAT_MENSAGENS_LOG as MSL");
					sql.append(" WHERE");
					sql.append(" LER.ERRO LIKE MSL.ERRO || '%' ");
					if(grupo != null) {
						sql.append(" AND MSL.SITUACAO = :grupo");
						sql.append(" AND MSL.ERRO LIKE '%'");
		
					}
					else {
						sql.append(" AND MSL.SITUACAO = MSL.SITUACAO");
						if(erro != null) {
							sql.append(" AND MSL.ERRO LIKE '%'||:erro||'%'");
						}
						else {
							sql.append(" AND MSL.ERRO LIKE '%'");
						}
					}
				sql.append(" )");
			sql.append(" AND LER.CRIADO_EM > :dataInicial");
			sql.append(" AND LER.CRIADO_EM < :dataFinal");
			if(reapresentada) {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NOT NULL");
			}
			else {
				sql.append(" AND CTH.CTH_SEQ_REAPRESENTADA IS NULL");
			}
			if(StringUtils.isNotBlank(nome)) {
				sql.append("  AND UPPER(SUBSTRING(PAC.NOME FROM 1 FOR 1)) IN (:letrasNome)");
			}
			sql.append(" GROUP BY");
			sql.append(" LER.CTH_SEQ");
			sql.append(" ,PAC.PRONTUARIO");
			sql.append(" ,nome");
			sql.append(" ,leito");
			sql.append(" ,dt_int_adm");
			sql.append(" ,dt_alt_adm");
			sql.append(" ,nro_aih");
			sql.append(" ,msp_sia");
			sql.append(" ,erro");
			sql.append(" ,desdobr");
			sql.append(" ,phirealizado");
			sql.append(" ,cspcnvcodigo");
			sql.append(" ,cspseq");
			sql.append(" ORDER BY erro, dtAltAdm, nome");
		}
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameterList("indSituacaoConta", new String[]{DominioSituacaoConta.F.toString(), DominioSituacaoConta.E.toString()});
		query.setString("situacaoProcedimento", DominioSituacao.A.toString());
		query.setString("tipoAIH5", DominioSimNao.S.toString());
		query.setString("modulo", DominioModuloMensagem.INT.toString());
		if(grupo != null) {
			query.setString("grupo", grupo.toString());
		}
		if(erro != null) {
			query.setString("erro", erro);
		}
		if(StringUtils.isNotBlank(nome)) {
			query.setParameterList("letrasNome", StringUtils.split(nome,","));
		}
		
		query.setDate("dataInicial", dtInicial);
		Calendar dtFim = Calendar.getInstance();
		dtFim.setTime(dtFinal);
		dtFim.add(Calendar.DATE, 1);
		query.setDate("dataFinal", dtFim.getTime());
		
		List<PendenciaEncerramentoVO> listaVO = query.
			addScalar("cthseq",IntegerType.INSTANCE).
			addScalar("prontuario",IntegerType.INSTANCE).
			addScalar("nome",StringType.INSTANCE).
			addScalar("leito",StringType.INSTANCE).
			addScalar("dtIntAdm",DateType.INSTANCE).
			addScalar("dtAltAdm",DateType.INSTANCE).
			addScalar("nroAih",LongType.INSTANCE).
			addScalar("mspSia",StringType.INSTANCE).
			addScalar("intseq",IntegerType.INSTANCE).
			addScalar("erro",StringType.INSTANCE).
			addScalar("desdobr",StringType.INSTANCE).
			addScalar("phirealizado",IntegerType.INSTANCE).
			addScalar("cspcnvcodigo",ShortType.INSTANCE).
			addScalar("cspseq",ByteType.INSTANCE).
			setResultTransformer(Transformers.aliasToBean(PendenciaEncerramentoVO.class)).list();
		
		if(listaVO != null && !listaVO.isEmpty()){
			PendenciaEncerramentoVO vo = listaVO.get(listaVO.size() - 1);
			vo.setuErro(vo.getErro());
			vo.setuLeito(vo.getLeito());
			vo.setuNome(vo.getNome());
		}
		
		return listaVO;
		
	}
	
	public FatContasHospitalares buscarCthGerada(Integer cthSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "CTH1");
		criteria.createAlias(FatContasHospitalares.Fields.CTH.toString(), FatContasHospitalares.Fields.CTH.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), new DominioSituacaoConta[]{DominioSituacaoConta.A, DominioSituacaoConta.F}));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatContasHospitalares.class, "CTH2");
		subCriteria.add(Restrictions.eqProperty(subCriteria.getAlias()+"."+FatContasHospitalares.Fields.CTH_SEQ.toString(), criteria.getAlias()+"."+FatContasHospitalares.Fields.SEQ.toString()));
		subCriteria.setProjection(Projections.property(subCriteria.getAlias()+"."+FatContasHospitalares.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.notExists(subCriteria));
		
		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}	
	
	public Boolean existsFatContasHospitalares(Long nroAih, DominioSituacaoConta situacaoExcluded){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.AIH.toString(), FatContasHospitalares.Fields.AIH.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.NRO_AIH.toString(), nroAih));
		criteria.add(Restrictions.ne(FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacaoExcluded));
		return executeCriteriaExists(criteria);
	}

	public Long validarInformarSolicitado(DominioGrupoConvenio grupoConvenio, Integer cthSeq, Integer prontuario, Integer intSeq,
			DominioSituacaoConta[] situacoes, Date dtInicio, Date dtFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);

		criteria.createAlias(FatContasHospitalares.Fields.CONVENIO_SAUDE.toString(), "cnv");
		criteria.createAlias(FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "coi");
		criteria.createAlias("coi." + FatContasInternacao.Fields.INTERNACAO.toString(), "int");
		criteria.createAlias("int." + AinInternacao.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.ne("int." + AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq("cnv." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenio));
		criteria.add(Restrictions.ne(FatContasHospitalares.Fields.SEQ.toString(), cthSeq));
		criteria.add(Restrictions.isNotNull(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()));
		criteria.add(Restrictions.ge(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(), dtInicio));
		criteria.add(Restrictions.le(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(), dtFim));
		criteria.add(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacoes));
		return executeCriteriaCount(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public Integer buscaCodigoCentral(Integer pCthSeq, Short cnvCodigo, DominioIndAbsenteismo indAbsenteismo) {
		final StringBuilder sql = new StringBuilder(1300);
		
		sql.append(" SELECT COALESCE(con.cod_central,0), con.dt_consulta ");
		sql.append(" FROM agh.ain_internacoes int , ");
		sql.append("      agh.aac_consultas con , ");
		sql.append("      agh.aac_retornos ret , ");
		sql.append("      agh.fat_contas_internacao coi , ");
		sql.append("      agh.fat_contas_hospitalares cth ");
		sql.append(" WHERE cth.seq          = :pCthSeq ");
		sql.append("   AND cth.seq            = coi.cth_seq ");
		sql.append("   AND coi.int_seq        = int.seq ");
		sql.append("   AND int.pac_codigo     = con.pac_codigo ");
		sql.append("   AND con.ret_seq        = ret.seq ");
		sql.append("   AND con.CSP_CNV_CODIGO = :pCnvCod ");

		//sql.append("   AND con.dt_consulta <= int.dthr_internacao ");
		// AND trunc(con.dt_consulta) <= trunc(int.dthr_internacao) -- marina 26/04/2011 
		sql.append("   AND to_char(con.dt_consulta,'YYYYMMDD') <= to_char(int.dthr_internacao,'YYYYMMDD') ");

		sql.append("   AND ret.ind_absenteismo = :indAbsenteismo ");
		sql.append("   AND con.cod_central is not null ");	    
		sql.append(" UNION ");	    
		sql.append(" SELECT COALESCE(con.cod_central,0), con.dt_consulta ");
		sql.append(" FROM agh.FAT_DADOS_CONTA_SEM_INT dcs , ");
		sql.append("   	  agh.aac_consultas con , ");
		sql.append("      agh.aac_retornos ret , ");
		sql.append("      agh.fat_contas_internacao coi , ");
		sql.append("      agh.fat_contas_hospitalares cth ");
		sql.append(" WHERE cth.seq          = :pCthSeq ");
		sql.append("   AND cth.seq            = coi.cth_seq ");
		sql.append("   AND coi.dcs_seq        = dcs.seq ");
		sql.append("   AND dcs.pac_codigo     = con.pac_codigo ");
		sql.append("   AND con.ret_seq        = ret.seq ");
		sql.append("   AND con.CSP_CNV_CODIGO = :pCnvCod ");
		
		//sql.append("   AND con.dt_consulta <= dcs.data_inicial ");
		// AND trunc(con.dt_consulta) <= trunc(dcs.data_inicial) -- marina 26/04/2011
		sql.append("   AND to_char(con.dt_consulta,'YYYYMMDD') <= to_char(dcs.data_inicial,'YYYYMMDD') ");

		sql.append("   AND ret.ind_absenteismo = :indAbsenteismo ");
		sql.append("   AND con.cod_central is not null ");	    
		sql.append(" ORDER BY 2 DESC ");
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setInteger("pCthSeq", pCthSeq);
		query.setShort("pCnvCod", cnvCodigo);
		query.setString("indAbsenteismo", indAbsenteismo.toString());
		query.setMaxResults(1);
		
		List<Object[]> result = query.list();
		if(result!=null && !result.isEmpty()){
			Number codResult = (Number)result.get(0)[0];
			return codResult != null ? codResult.intValue() : null;
		}
		
		return null;
	}
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<FaturamentoPorProcedimentoVO> obterFaturamentoPorProcedimento(final Date dtHrInicio, final Integer ano, final Integer mes){
		final StringBuilder sql = new StringBuilder(3400);
		
		final String quantidade = " (CASE WHEN IPH.IND_PROC_ESPECIAL = 'S' THEN " +
			  					  " 	(CASE WHEN IPH.QTD_PROCEDIMENTOS_ITEM IS NOT NULL THEN IPH.QTD_PROCEDIMENTOS_ITEM ELSE 1 END) " +
			  					  "   ELSE " +
			  					  "		1 " +
			  					  "   END " +
			  					  " )" ;
		
		final String ordem = " (CASE WHEN IPH.IND_INTERNACAO = 'S' THEN 1 ELSE 2 END) ";
		
		
		// Atenção com o order by ao se adicionar campos na consulta 
		
		sql.append(" SELECT ")
		   .append("        IPH.DESCRICAO  AS descricao ")
		   .append("	  , IPH.COD_TABELA AS codSus ")
		   .append("      , (COUNT(*) * ").append(quantidade).append(" ) AS qtd ")
		   .append("      , SUM(EAI.VALOR_SH_REALIZ)     as hosp ")
		   .append("	  , SUM(EAI.VALOR_SP_REALIZ)     as prof ")
		   .append("	  , 0 as qtdProc ")
		   .append("	  , 0 as servHospProc ")
		   .append("	  , 0 as servProfProc ")
		   .append("	  , COUNT(EAI.IPH_COD_SUS_REALIZ) as qtdAih ")
		   .append("	  , SUM(EAI.VALOR_SH_REALIZ)      as hospAih ")
		   .append("	  , SUM(EAI.VALOR_SP_REALIZ)      as profAih ")
		   .append("      , ").append(ordem).append(" as ordem ")
		   
		   .append(" FROM ")
		   .append("        AGH.FAT_ITENS_PROCED_HOSPITALAR IPH ")
		   .append("	  , AGH.FAT_ESPELHOS_AIH            EAI ")
		   .append("      , AGH.FAT_CONTAS_HOSPITALARES     CTH ")
		   
		   .append(" WHERE ")
		   .append(" 	    EAI.IPH_PHO_SEQ_REALIZ = IPH.PHO_SEQ ")
		   .append("	AND EAI.CTH_SEQ 		   = CTH.SEQ ")
		   .append(" 	AND EAI.IPH_SEQ_REALIZ     = IPH.SEQ ")
		   .append("    AND EAI.SEQP               = :PRM_EAI_SEQP ")	
		   .append("	AND EAI.CTH_SEQ            = CTH.SEQ ")
		   .append("	AND CTH.CTH_SEQ_REAPRESENTADA IS NULL ")
		   .append("	AND EAI.DCI_CPE_DT_HR_INICIO = :PRM_DTHR_INICIO ")
		   .append("	AND EAI.DCI_CPE_MODULO		 = :PRM_MODULO ")
		   .append("	AND EAI.DCI_CPE_MES          = :PRM_MES ")
		   .append("	AND EAI.DCI_CPE_ANO          = :PRM_ANO ")
		   .append("	AND CTH.IND_SITUACAO         <> :SIT_CONTA ")
		   
		   .append(" GROUP BY ")
		   .append("	  IPH.DESCRICAO ")
		   .append("	, IPH.COD_TABELA ")
		   .append("	, ").append(quantidade)
		   .append("	, ").append(ordem)
		   
		   .append(" UNION ALL ")
		   .append(" SELECT ")
	   	   .append("        IPH.DESCRICAO ")
	   	   .append("	  , IPH.COD_TABELA ")
		   .append("      , (COUNT(*) * ").append(quantidade).append(" ) ")
		   .append("	  , SUM(CAH.VALOR_SERV_HOSP) ")
		   .append("      , SUM(CAH.VALOR_SERV_PROF) ")
		   .append("	  , COUNT(CAH.IPH_COD_SUS) ")
		   .append("	  , SUM(CAH.VALOR_SERV_HOSP) ")
		   .append("	  , SUM(CAH.VALOR_SERV_PROF) ")
		   .append("	  , 0 ")
		   .append("	  , 0 ")
		   .append("	  , 0")
		   .append("      , ").append(ordem)

		   .append(" FROM ")
		   .append("        AGH.FAT_CONTAS_HOSPITALARES 	CTH ")
		   .append("	  , AGH.FAT_ESPELHOS_AIH 	    	EAI ")
		   .append("      , AGH.FAT_CAMPOS_MEDICO_AUDIT_AIH CAH ")
		   .append("      , AGH.FAT_ITENS_PROCED_HOSPITALAR IPH ")
		   
		   .append(" WHERE ")
		   .append(" 	    EAI.CTH_SEQ            = CAH.EAI_CTH_SEQ ")
		   .append("	AND EAI.SEQP               = :PRM_EAI_SEQP ")		
		   .append("	AND EAI.IPH_COD_SUS_REALIZ <> CAH.IPH_COD_SUS ")
		   .append("	AND (CASE WHEN CAH.IND_CONSISTENTE IS NOT NULL THEN CAH.IND_CONSISTENTE ELSE '")
		   										.append(DominioTipoItemConta.D.toString()).append("' END) <> '")
		   										.append(DominioTipoItemConta.R.toString()).append("' ") 
		   .append("	AND CAH.IND_MODO_COBRANCA  = '").append(DominioModoCobranca.V.toString()).append("' ")
		   .append("	AND CAH.IPH_SEQ            = IPH.SEQ ")
		   .append("	AND CAH.IPH_PHO_SEQ        = IPH.PHO_SEQ ")
		   .append("	AND EAI.CTH_SEQ 		   = CTH.SEQ")
		   .append("	AND CTH.CTH_SEQ_REAPRESENTADA IS NULL ")
   		   .append("	AND EAI.DCI_CPE_DT_HR_INICIO = :PRM_DTHR_INICIO ")
		   .append("	AND EAI.DCI_CPE_MODULO		 = :PRM_MODULO ")
		   .append("	AND EAI.DCI_CPE_MES          = :PRM_MES ")
		   .append("	AND EAI.DCI_CPE_ANO          = :PRM_ANO ")
			   
		   .append(" GROUP BY ")
		   .append("	  IPH.DESCRICAO ")
		   .append("	, IPH.COD_TABELA ")
		   .append("	, ").append(quantidade)
		   .append("	, ").append(ordem)
		   
		   .append(" UNION ALL ")
		   .append(" SELECT ")
	   	   .append("        IPH.DESCRICAO ")
	   	   .append("	  , IPH.COD_TABELA ")
		   .append("      ,( SUM(AAM.QUANTIDADE) * ").append(quantidade).append(" ) ")
		   .append("      , SUM(AAM.VALOR_SERV_HOSP)")
		   .append("	  , SUM(AAM.VALOR_SERV_PROF) ")
		   .append("	  ,  SUM( (CASE WHEN AAM.QUANTIDADE is not null THEN AAM.QUANTIDADE ELSE 0  END) ) ")
		   .append("	  ,  SUM(AAM.VALOR_SERV_HOSP) ")
		   .append("	  ,  SUM(AAM.VALOR_SERV_PROF) ")
		   .append("	  , 0 ")
		   .append("	  , 0 ")
		   .append("	  , 0")
		   .append("      , ").append(ordem) 
		   
		   .append(" FROM ")
		   .append("        AGH.FAT_CONTAS_HOSPITALARES 	CTH ")
		   .append("	  , AGH.FAT_ESPELHOS_AIH 	    	EAI ")
		   .append("      , AGH.FAT_ATOS_MEDICOS_AIH        AAM ")
		   .append("      , AGH.FAT_ITENS_PROCED_HOSPITALAR IPH ")
		   
		   .append(" WHERE ")
		   .append(" 	    EAI.CTH_SEQ            = AAM.EAI_CTH_SEQ ")
		   .append("	AND EAI.SEQP               = :PRM_EAI_SEQP ")	   
		   .append("	AND EAI.IPH_COD_SUS_REALIZ <> AAM.IPH_COD_SUS ")
		   .append("	AND (CASE WHEN AAM.IND_CONSISTENTE IS NOT NULL THEN AAM.IND_CONSISTENTE ELSE '")
		   										.append(DominioTipoItemConta.D.toString()).append("' END) <> '")
		   										.append(DominioTipoItemConta.R.toString()).append("' ") 
		   
		   .append("	AND AAM.IND_MODO_COBRANCA  = '").append(DominioModoCobranca.V.toString()).append("' ")
		   .append("	AND AAM.IPH_PHO_SEQ        = IPH.PHO_SEQ ")
		   .append("	AND AAM.IPH_SEQ            = IPH.SEQ ")
		   .append("	AND AAM.TAO_SEQ 		   <> :PRM_TAO_SEQ ")
		   .append("	AND EAI.CTH_SEQ 			= CTH.SEQ")
		   .append("	AND CTH.CTH_SEQ_REAPRESENTADA IS NULL ")
		   .append("	AND EAI.DCI_CPE_DT_HR_INICIO = :PRM_DTHR_INICIO ")
		   .append("	AND EAI.DCI_CPE_MODULO		 = :PRM_MODULO ")
		   .append("	AND EAI.DCI_CPE_MES          = :PRM_MES ")
		   .append("	AND EAI.DCI_CPE_ANO          = :PRM_ANO ")
			   
		   .append(" GROUP BY ")
		   .append("	  IPH.DESCRICAO ")
		   .append("	, IPH.COD_TABELA ")
		   .append("	, ").append(quantidade)
		   .append("	, ").append(ordem)
		   
		   .append(" ORDER BY ")
		   .append("	12, 1 ");
		
		SQLQuery query = createSQLQuery(sql.toString());
		
		query.setString("SIT_CONTA", DominioSituacaoConta.R.toString());
		query.setString("PRM_MODULO", DominioModuloCompetencia.INT.toString());
		query.setTimestamp("PRM_DTHR_INICIO", dtHrInicio);
		query.setInteger("PRM_MES", mes); 
		query.setInteger("PRM_ANO", ano);
		
		query.setInteger("PRM_EAI_SEQP", 1); 
		query.setByte("PRM_TAO_SEQ", Byte.valueOf("19"));
		
		final List<FaturamentoPorProcedimentoVO> result =  query.addScalar("descricao", StringType.INSTANCE)
															    .addScalar("codSus",LongType.INSTANCE)
															    .addScalar("qtd",LongType.INSTANCE)
															    .addScalar("hosp",DoubleType.INSTANCE)
															    .addScalar("prof",DoubleType.INSTANCE)
															    .addScalar("qtdProc",LongType.INSTANCE)
															    .addScalar("servHospProc",DoubleType.INSTANCE)
															    .addScalar("servProfProc",DoubleType.INSTANCE)
															    .addScalar("qtdAih",LongType.INSTANCE)
															    .addScalar("hospAih",DoubleType.INSTANCE)
															    .addScalar("profAih",DoubleType.INSTANCE)
															    .addScalar("ordem",IntegerType.INSTANCE)
															    .setResultTransformer(Transformers.aliasToBean(FaturamentoPorProcedimentoVO.class)).list();

		return result;
	}
	
	/**
	 * Deve ser chamado por ContaHospitalarON.obterContasPreenchimentoLaudos, nunca diretamente,
	 * pois há lógica de negócio implatada no método da ON. 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<ContasPreenchimentoLaudosVO> obterContasPreenchimentoLaudos(final Date dtUltimaPrevia, final String iniciaisPaciente ){
		final StringBuilder sql = new StringBuilder(3550);
		
		sql.append(" SELECT ")
		   .append("        cth.seq as conta ")
		   .append("      , pac.nome as paciente ")
		   .append("      , pac.codigo as codPaciente ")
		   .append("      , pac.prontuario	as prontuario ")
		   
		   .append("      , cth.phi_seq_realizado as phiRealizado ")
		   .append("      , iph.cod_tabela as ssmRealizado ")
		   .append("      , iphs.cod_tabela as ssmSolicitado ")
		   .append("      , cth.usuario_previa as usuario ")
		   .append("      , ain.dthr_alta_medica as dtAlta ")
		   .append("	  , (case when ( ")
		   .append("	           		 (case when cth.valor_utie is not null then cth.valor_utie else 0 end) + ")
		   .append("	          		 (case when cth.valor_uti  is not null then cth.valor_uti  else 0 end) ")
		   .append("	   			   ) = 0 then  ")
		   .append("	   			   			  null  ")
		   .append("	   			   else  ")
		   .append("	   			   			  'LU'  ")
		   .append("	  		 end  ")
		   .append("	  	) as uti ")

   		   .append("	  , (case when ")
		   .append("	           	  (case when cth.diarias_acompanhante is not null then cth.diarias_acompanhante else 0 end) ")
		   .append("	   			   = 0 then  ")
		   .append("	   			   	  null  ")
		   .append("	   			   else  ")
		   .append("	   			   	  'AC'  ")
		   .append("	  		 end  ")
		   .append("	  	) as acomp ")
		   
   		   .append("	  , (case when iphs.cod_tabela = iph.cod_tabela then null else 'MP' end) as mproced ")
		   
   		   .append("	  , (case when ")
		   .append("	           	  (case when cth.dias_permanencia_maior is not null then cth.dias_permanencia_maior else 0 end) ")
		   .append("	   			   = 0 then  ")
		   .append("	   			   	  null  ")
		   .append("	   			   else  ")
		   .append("	   			   	  'PM'  ")
		   .append("	  		 end  ")
		   .append("	  	) as pmaior ")
		   
   		   .append("	  , (case ")
		   .append("	        (case when cth.ind_codigo_exclusao_critica is not null then cast(cth.ind_codigo_exclusao_critica as INTEGER) else 0 end) ")
		   .append("	   			  when 0 then null ")
		   .append("	   			  when 1 then 'p. menor' ")
		   .append("	   			  when 2 then 'id menor' ")
		   .append("	   			  when 3 then 'id maior' ")
		   .append("	   			  else null ")
		   .append("	  		 end  ")
		   .append("	  	) as excluCrit ")		   
		   
   		   .append("	  , ( case when (case when cth.cth_seq is not null then cth.cth_seq else 0 end) = 0 then null  ")
		   .append("	   	      else '***'  ")
		   .append("	  	  end  ")
		   .append("	  	) as des ")		   
		   .append("	  , ain.unf_seq as unfSeq ")		   
		   .append("	  , ain.QRT_NUMERO as nrQuarto ")		   
		   .append("	  , ain.LTO_LTO_ID as leitoId ")
		   
		   .append(" FROM ")
		   .append("        agh.aip_pacientes               pac  ")
		   .append("	  , agh.agh_atendimentos            atd  ")
		   .append("	  , agh.ain_internacoes             ain  ")
		   .append("	  , agh.fat_contas_internacao       coi  ")
		   .append("	  , agh.fat_itens_proced_hospitalar iphs ")
		   .append("	  , agh.fat_conv_grupo_itens_proced cgis ")
		   .append("	  , agh.fat_itens_proced_hospitalar iph  ")
		   .append("	  , agh.fat_conv_grupo_itens_proced cgi  ")
		   .append("	  , agh.fat_contas_hospitalares     cth  ")
		   
		   .append(" WHERE ")
		   .append(" 	    cth.data_previa between :prmDtInicial and :prmDtFinal ")
		   .append("	and cth.ind_situacao in (:indSituacaoConta) ")
		   .append(" 	and cth.seq = coi.cth_seq ")
		   .append(" 	and coi.int_seq = atd.int_seq ")
		   .append(" 	and coi.int_seq = ain.seq ")
		   .append(" 	and cgi.phi_seq = cth.phi_seq_realizado ")
		   .append(" 	and cgis.phi_seq = cth.phi_seq ")
		   .append(" 	and cth.csp_cnv_codigo = 1 ")
		   .append(" 	and iph.seq	= cgi.iph_seq ")
		   .append(" 	and iph.pho_seq = cgi.iph_pho_seq ")
		   .append(" 	and cgi.cpg_cph_csp_cnv_codigo = 1 ")
		   .append(" 	and cgi.cpg_cph_csp_seq = 1 ")
		   .append(" 	and cgi.iph_pho_seq	= 1 ")
		   .append(" 	and iphs.seq	= cgis.iph_seq ")
		   .append(" 	and iphs.pho_seq = cgis.iph_pho_seq ")
		   .append(" 	and cgis.cpg_cph_csp_cnv_codigo = 1 ")
		   .append(" 	and cgis.cpg_cph_csp_seq = 1 ")
		   .append(" 	and cgis.iph_pho_seq	= 1 ")
		   .append(" 	and pac.codigo = ain.pac_codigo ")
		   .append(" 	and ( ")
		   .append(" 	      (case when cth.diarias_acompanhante is not null then cth.diarias_acompanhante else 0 end) <> 0 ")
		   .append(" 	      or (case when iphs.cod_tabela = iph.cod_tabela then 0 else 1 end) <> 0 ")
		   .append(" 	      or (case when cth.dias_permanencia_maior is not null then cth.dias_permanencia_maior else 0 end) <> 0 ")
		   .append(" 	      or (case when cth.ind_codigo_exclusao_critica is not null then cast(cth.ind_codigo_exclusao_critica as INTEGER) else 0 end) <> 0 ")
		   .append(" 	      or ( (case when VALOR_UTI is not null then VALOR_UTI else 0 end) + (case when VALOR_UTIE is not null then VALOR_UTIE else 0 end) ) > 0 ")
		   .append(" 	    ) ");
		
		if(StringUtils.isNotBlank(iniciaisPaciente)) {                        
            sql.append(" and upper(substr(pac.nome,1,1)) in (:prm_iniciais_paciente)");
		}
		
		sql.append(" 	order by paciente, usuario  ");
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameterList("indSituacaoConta", new String[]{DominioSituacaoConta.E.toString(), DominioSituacaoConta.F.toString()});
		
		if(iniciaisPaciente != null && StringUtils.isNotBlank(iniciaisPaciente)) {
			final List<String> lst = new ArrayList<String>();
			for(char a : iniciaisPaciente.toCharArray()){
				lst.add(Character.toString(a));
			}
			
			query.setParameterList("prm_iniciais_paciente", lst);
		}
		
		query.setTimestamp("prmDtInicial", DateUtil.obterDataComHoraInical(dtUltimaPrevia));
		query.setTimestamp("prmDtFinal", DateUtil.obterDataComHoraFinal(dtUltimaPrevia));
		
		final List<ContasPreenchimentoLaudosVO> result =  query.addScalar("paciente", StringType.INSTANCE)
															    .addScalar("conta",IntegerType.INSTANCE)
															    .addScalar("prontuario",IntegerType.INSTANCE)
															    .addScalar("codPaciente",IntegerType.INSTANCE)
															    .addScalar("phiRealizado",IntegerType.INSTANCE)
															    .addScalar("ssmRealizado",LongType.INSTANCE)
															    .addScalar("ssmSolicitado",LongType.INSTANCE)
															    .addScalar("usuario",StringType.INSTANCE)
															    .addScalar("dtAlta",DateType.INSTANCE)
															    .addScalar("uti",StringType.INSTANCE)
															    .addScalar("acomp",StringType.INSTANCE)
															    .addScalar("mProced",StringType.INSTANCE)
															    .addScalar("pMaior",StringType.INSTANCE)
															    .addScalar("excluCrit",StringType.INSTANCE)
															    .addScalar("des",StringType.INSTANCE)
															    .addScalar("unfSeq",ShortType.INSTANCE)
															    .addScalar("nrQuarto",LongType.INSTANCE)
															    .addScalar("leitoId",StringType.INSTANCE)
															    .setResultTransformer(Transformers.aliasToBean(ContasPreenchimentoLaudosVO.class)).list();
		return result;
	}
	
	
	
	public FaturamentoPorProcedimentoVO obterFaturamentoPorProcedimentoUTIEspelho(final Date dtHrInicio, final Integer ano, final Integer mes){
		final StringBuilder sql = new StringBuilder(1550);
		
		sql.append(" SELECT ")
		   .append("        sum(VALOR_UTI)            as valorUTI ")
		   .append("      , sum(VALOR_ACOMP)  	  	  as valorAcomp ")
		   .append("      , sum(DIARIAS_ACOMPANHANTE) as diasAcomp ")
		   
		   .append("	  , sum( CASE WHEN (CASE WHEN VALOR_UTI IS NOT NULL THEN VALOR_UTI ELSE 0 END) = 0 then ")
		   .append("	            0 ")
		   .append("	          ELSE ")
		   .append("	   			(CASE WHEN DIAS_UTI_MES_ALTA IS NOT NULL THEN DIAS_UTI_MES_ALTA ELSE 0 END)  ")
		   .append("	  		 END + ")
		   
		   .append("	  	 	 CASE WHEN (CASE WHEN VALOR_UTI IS NOT NULL THEN VALOR_UTI ELSE 0 END) = 0 THEN ")
		   .append("	            0 ")
		   .append("	          ELSE ")
		   .append("	   			(CASE WHEN DIAS_UTI_MES_ANTERIOR IS NOT NULL THEN DIAS_UTI_MES_ANTERIOR ELSE 0 END) ")
		   .append("	  		 END + ")
		   
		   .append("	  	 	 CASE WHEN (CASE WHEN VALOR_UTI IS NOT NULL THEN VALOR_UTI ELSE 0 END) = 0 THEN ")
		   .append("	            0 ")
		   .append("	          ELSE ")
		   .append("	   			(CASE WHEN DIAS_UTI_MES_INICIAL IS NOT NULL THEN DIAS_UTI_MES_INICIAL ELSE 0 END) ")
		   .append("	  		 END ")
		   .append(" 			) as diasUTI ")
		   
		   .append("	  , sum(V.VALOR_SH_UTI)      as diariaUtiHosp ")
		   .append("	  , sum(V.VALOR_SP_UTI)      as diariaUtiProf ")
		   .append("	  , sum(V.VALOR_SH_ACOMP)    as diariaAcompServHosp ")
		   .append("	  , sum(V.VALOR_SP_ACOMP)    as diariaAcompServProf ")
		   
		   .append(" FROM ")
		   .append("        AGH.FAT_VALORES_CONTA_HOSPITALAR V ")
		   .append("	  , AGH.FAT_CONTAS_HOSPITALARES C ")
		   
		   .append(" WHERE ")
		   .append(" 	    C.SEQ = V.CTH_SEQ ")
		   .append("	AND C.CTH_SEQ_REAPRESENTADA IS NULL ")
		   .append("	AND C.IND_SITUACAO <> :SIT_CONTA ")
		   .append(" 	AND DCI_CODIGO_DCIH IN ( ")
		   .append("    						 SELECT CODIGO_DCIH FROM agh.FAT_DOCUMENTO_COBRANCA_AIHS ")		
		   .append("							 WHERE 1=1 ")
		   .append("								AND CPE_DT_HR_INICIO = :PRM_DTHR_INICIO ")
		   .append("								AND CPE_MODULO		 = :PRM_MODULO ")
		   .append("								AND CPE_MES          = :PRM_MES ")
		   .append("								AND CPE_ANO          = :PRM_ANO ")
		   
		   .append(" 							) ");
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setString("SIT_CONTA", DominioSituacaoConta.R.toString());
		query.setString("PRM_MODULO", DominioModuloCompetencia.INT.toString());
		query.setTimestamp("PRM_DTHR_INICIO", dtHrInicio);
		query.setInteger("PRM_MES", mes); 
		query.setInteger("PRM_ANO", ano);
		
		final List<FaturamentoPorProcedimentoVO> result =  query.addScalar("diariaAcompServHosp", DoubleType.INSTANCE)
															    .addScalar("diariaAcompServProf",DoubleType.INSTANCE)
															    .addScalar("diariaUtiHosp",DoubleType.INSTANCE)
															    .addScalar("diariaUtiProf",DoubleType.INSTANCE)
															    .addScalar("diasAcomp",LongType.INSTANCE)
															    .addScalar("diasUTI",LongType.INSTANCE)
															    .addScalar("valorUTI",DoubleType.INSTANCE)
															    .addScalar("valorAcomp",DoubleType.INSTANCE)
															    .setResultTransformer(Transformers.aliasToBean(FaturamentoPorProcedimentoVO.class)).list();
	
		if(result != null && !result.isEmpty()){
			return result.get(0);
		} else {
			return new FaturamentoPorProcedimentoVO();
		}
		
	}
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<TotaisPorDCIHVO> obterTotaisPorDCIH(final Date dtHrInicio, final Integer ano, final Integer mes){
		
		final StringBuilder sql = new StringBuilder(5500);
		
		final String truncDate;
		if(isOracle()) {
			truncDate = " AND TRUNC(CTHIN.JN_DATE_TIME) <= TRUNC(CPEIN.DT_HR_FIM) ";
		} else {
			truncDate = " AND DATE_TRUNC('DAY',CTHIN.JN_DATE_TIME) <= DATE_TRUNC('DAY', CPEIN.DT_HR_FIM) ";
		}
		
		final String rowNum;
		if(isOracle()) {
			rowNum = " AND ROWNUM < 1 ";
		} else {
			rowNum = " LIMIT 1 ";
		}
		
		final String fatc_busca_situacao_cta = " SELECT CTHIN.IND_SITUACAO " +
											   "  FROM " +
											   "    AGH.FAT_COMPETENCIAS CPEIN, " +
											   "    AGH.FAT_CONTAS_HOSPITALARES_JN CTHIN " +
											   "  WHERE " +
											   "         CTHIN.SEQ = CTH.SEQ " +
											   "	 AND CTHIN.IND_SITUACAO <> :prm_indSituacao1 " +
											   "	 AND CPEIN.MODULO = 'INT' " +
											   "	 AND CPEIN.ANO = DCI.CPE_ANO " +
											   "	 AND CPEIN.MES = DCI.CPE_MES " +
											         truncDate + rowNum;
		
											   
											 //  "  ORDER BY CTHIN.JN_DATE_TIME DESC " ;
		
		final String fatc_busca_situacao_cta_cond = " CASE WHEN CTH.IND_SITUACAO = :prm_indSituacao1 THEN " + 
														" CASE WHEN (" + fatc_busca_situacao_cta + ") in ('O','E') then " +
															" ( "+fatc_busca_situacao_cta +" ) "+
														"  ELSE CTH.IND_SITUACAO " +
														" END "+
													" ELSE CTH.IND_SITUACAO " +
													" END ";
													
		
		sql.append(" select 'CONTAS APRESENTADAS'  situacao ")
	       .append("         ,dci.codigo_dcih  	   dcih ")
	       .append("		 ,case when fcf.seq = 2 then fcf.descricao else fcc.DESCRICAO end descricao")
	       .append("         ,count(cth.seq) 	   qtd ")
	       
	       .append("         ,sum( COALESCE(cth.valor_sh,0) + vct.valor_sh_uti + vct.valor_sh_utie   + ") 
	       .append("               vct.valor_sh_acomp   + vct.valor_sh_transp + vct.valor_sh_rn  )      hosp ")
	             
	       .append("         ,sum( COALESCE(cth.valor_sp,0) + vct.valor_sp_uti    + vct.valor_sp_utie   + ") 
	       .append("               vct.valor_sp_acomp   + vct.valor_sp_rn     + vct.valor_sp_transp )   prof ")
	             
//	       REMOVIDA DO RELATORIO
//	       .append("         ,sum( COALESCE(cth.valor_sadt,0) + vct.valor_sadt_uti  + vct.valor_sadt_utie +  ")
//	       .append("               vct.valor_sadt_acomp + vct.valor_sadt_rn   + vct.valor_sadt_transp ) sadt ")
	       
	       .append("         ,sum(COALESCE(cth.valor_hemat,0)) hemat ")
	       
	       .append("         ,sum(COALESCE(cth.valor_opm,0))   prote  ")
	       
	       .append("         ,sum( COALESCE(cth.valor_sh,0)   +  vct.valor_sh_uti      +  vct.valor_sh_utie   	      + vct.valor_sh_acomp   + ")
	       .append("               vct.valor_sh_transp  	  +  vct.valor_sh_rn       +  COALESCE(cth.valor_sp,0)    + vct.valor_sp_uti     + ")
	       .append("               vct.valor_sp_utie    	  +  vct.valor_sp_acomp    +  vct.valor_sp_rn     	      + vct.valor_sp_transp  + ")
	       .append("               COALESCE(cth.valor_sadt,0) +  vct.valor_sadt_uti    +  vct.valor_sadt_utie         + vct.valor_sadt_acomp + ") 
	       .append("               vct.valor_sadt_rn    	  +  vct.valor_sadt_transp +  COALESCE(cth.valor_hemat,0) + COALESCE(cth.valor_opm,0) ) total  ")              

	       .append("  from  " )
	       .append("        agh.fat_caracts_financiamento    fcf ")
	       .append("       ,agh.fat_caracts_complexidade     fcc ")
	       .append("       ,agh.fat_itens_proced_hospitalar  iph ")
	       .append("       ,agh.fat_espelhos_aih             eai ")
	       .append("       ,agh.fat_tipos_classif_sec_saude  tcs ")
	       .append("       ,agh.fat_contas_hospitalares      cth ")
	       .append("       ,agh.fat_valores_conta_hospitalar vct ")
	       .append("       ,agh.fat_documento_cobranca_aihs  dci ")
	       
	       .append(" where ") 
	       .append("       tcs.seq = dci.tcs_seq ")
	       .append("       and dci.codigo_dcih = cth.dci_codigo_dcih ")
	       .append("       and cth.seq = vct.cth_seq ")
	       .append("       and cth.cth_seq_reapresentada is null  ")
	       .append("       and cth.ind_autorizado_sms  = :prm_ind_autorizado_sms1 ")
	       
	       .append("       and dci.cpe_dt_hr_inicio   >= :prm_dt_hr_inicio_ini1 ")
	       .append("       and dci.cpe_dt_hr_inicio   <  :prm_dt_hr_inicio_fim1 ")
	       
	       .append("       and dci.cpe_modulo          = :prm_modulo1 ")
	       .append("       and dci.cpe_mes             = :prm_mes1 ")
	       .append("       and dci.cpe_ano             = :prm_ano1 ")
	       
	       						// 01/07/2011 Marina
	       .append("       and (cth.ind_situacao <> :prm_indSituacao1 or ")
	       .append("				( cth.ind_situacao = :prm_indSituacao1 and (")
	       														   // Ney 12/12/12
	       .append(												 " (").append(fatc_busca_situacao_cta_cond ).append(") <> :prm_indSituacao1")
       	   .append("											 )")
	       .append("                ) ")
	       .append("           ) ")
	       
	       .append("       and eai.cth_seq = cth.seq ")
	       .append("       and eai.seqp = :prm_eai_seqp ")
	       .append("       and iph.pho_seq = eai.iph_pho_seq_realiz ")
	       .append("       and iph.seq = eai.iph_seq_realiz ")
	       .append("       and fcc.seq = iph.fcc_Seq ")
	       .append("       and fcf.seq=iph.fcf_Seq ")
		
	       .append(" group by  dci.codigo_dcih, case when fcf.seq = 2 then fcf.descricao else fcc.DESCRICAO end ")

	       .append(" union ")

	       .append(" select 'CONTAS REAPRESENTADAS' situacao ") 
	       .append("        ,dci.codigo_dcih        dcih ")
	       .append("        ,'REAPRESENTADAS - '|| case when fcf.seq = 2 then fcf.descricao else fcc.DESCRICAO end descricao   ")   
	       .append("        ,count(cth.seq) 		qtd ")
	        
	       .append("        ,sum( COALESCE(cth.valor_sh,0) + vct.valor_sh_uti    + vct.valor_sh_utie + ") 
	       .append("                  vct.valor_sh_acomp   + vct.valor_sh_transp + vct.valor_sh_rn )       hosp ")

	       .append("        ,sum( COALESCE(cth.valor_sp,0) + vct.valor_sp_uti     + vct.valor_sp_utie   + ")
	       .append("              vct.valor_sp_acomp       + vct.valor_sp_rn      + vct.valor_sp_transp )       prof ")
		   
//	       REMOVIDA DO RELATORIO
//	       .append("        ,sum( COALESCE(cth.valor_sadt,0) + vct.valor_sadt_uti   + vct.valor_sadt_utie +  ")
//	       .append("                    vct.valor_sadt_acomp + vct.valor_sadt_rn    + vct.valor_sadt_transp ) sadt ")

	       .append("        ,sum(COALESCE(cth.valor_hemat,0))  hemat ")
	       
	       .append("        ,sum(COALESCE(cth.valor_opm,0))    prote ")

	       .append("        ,sum( COALESCE(cth.valor_sh,0)   + vct.valor_sh_uti      + vct.valor_sh_utie           + vct.valor_sh_acomp   + ")
	       .append("              vct.valor_sh_transp        + vct.valor_sh_rn       + COALESCE(cth.valor_sp,0)    + vct.valor_sp_uti     + ")
	       .append("              vct.valor_sp_utie          + vct.valor_sp_acomp    + vct.valor_sp_rn             + vct.valor_sp_transp  + ")
	       .append("              COALESCE(cth.valor_sadt,0) + vct.valor_sadt_uti    + vct.valor_sadt_utie         + vct.valor_sadt_acomp + ") 
	       .append("              vct.valor_sadt_rn          + vct.valor_sadt_transp + COALESCE(cth.valor_hemat,0) + COALESCE(cth.valor_opm,0) ) total ")              

	       .append(" from  " )
	       .append("        agh.fat_caracts_financiamento    fcf ")
	       .append("       ,agh.fat_caracts_complexidade     fcc ")
	       .append("       ,agh.fat_itens_proced_hospitalar  iph ")
	       .append("       ,agh.fat_espelhos_aih             eai ")
       	   .append("       ,agh.fat_tipos_classif_sec_saude  tcs ")
	       .append("       ,agh.fat_contas_hospitalares      cth ")
	       .append("       ,agh.fat_valores_conta_hospitalar vct ")
	       .append("       ,agh.fat_documento_cobranca_aihs  dci ")
	         
	       .append(" where ")  
	       .append("       tcs.seq = dci.tcs_seq ")
	       .append("       and dci.codigo_dcih = cth.dci_codigo_dcih ")
	       .append("       and cth.seq = vct.cth_seq ")
	       .append("       and cth.cth_seq_reapresentada is not null ") 
	       .append("       and cth.ind_autorizado_sms  = :prm_ind_autorizado_sms2 ")
	       
	       .append("       and dci.cpe_dt_hr_inicio   >= :prm_dt_hr_inicio_ini2 ")
	       .append("       and dci.cpe_dt_hr_inicio    < :prm_dt_hr_inicio_fim2 ")
	       
	       .append("       and dci.cpe_modulo          = :prm_modulo2 ")
	       .append("       and dci.cpe_mes             = :prm_mes2 ")
	       .append("       and dci.cpe_ano             = :prm_ano2 ")
	       
	      						// 01/07/2011 Marina
	       .append("       and (cth.ind_situacao <> :prm_indSituacao1 or ")
	       .append("				( cth.ind_situacao = :prm_indSituacao1 and (")
	       														   // Ney 12/12/12
	       .append(												 " (").append(fatc_busca_situacao_cta_cond).append(") <> :prm_indSituacao1")
       	   .append("											              )")
	       .append("                ) ")
	       .append("           ) ")
	       
   	       .append("       and eai.cth_seq = cth.seq ")
	       .append("       and eai.seqp = :prm_eai_seqp ")
	       .append("       and iph.pho_seq = eai.iph_pho_seq_realiz ")
	       .append("       and iph.seq = eai.iph_seq_realiz ")
	       .append("       and fcc.seq = iph.fcc_Seq ")
	       .append("       and fcf.seq=iph.fcf_Seq ")

	       .append("group by  dci.codigo_dcih, ('REAPRESENTADAS - '|| case when fcf.seq = 2 then fcf.descricao else fcc.DESCRICAO end) ");
//	       .append(" order by situacao ") ;
		
		final SQLQuery q = createSQLQuery(sql.toString());

		q.setString("prm_modulo1", DominioModuloCompetencia.INT.toString());
		q.setString("prm_modulo2", DominioModuloCompetencia.INT.toString());
		
		q.setString("prm_ind_autorizado_sms1", DominioSimNao.S.toString());
		q.setString("prm_ind_autorizado_sms2", DominioSimNao.S.toString());

		if(dtHrInicio != null){     	
			q.setDate("prm_dt_hr_inicio_ini1", DateUtil.obterDataComHoraFinal(dtHrInicio));
			q.setDate("prm_dt_hr_inicio_fim1", DateUtil.adicionaDias(dtHrInicio,1));
			q.setDate("prm_dt_hr_inicio_ini2", DateUtil.obterDataComHoraFinal(dtHrInicio));
			q.setDate("prm_dt_hr_inicio_fim2", DateUtil.adicionaDias(dtHrInicio,1));
		}

		q.setInteger("prm_mes1", mes); 
		q.setInteger("prm_mes2", mes); 
		q.setInteger("prm_ano1", ano);
		q.setString("prm_indSituacao1", "R");
		
		q.setInteger("prm_ano2", ano); 
		q.setInteger("prm_eai_seqp", 1);
		
		final List<TotaisPorDCIHVO> result =  q.addScalar("situacao",StringType.INSTANCE).addScalar("dcih",StringType.INSTANCE)
		   									   .addScalar("descricao", StringType.INSTANCE).addScalar("qtd",LongType.INSTANCE)
		   									   .addScalar("hosp",DoubleType.INSTANCE).addScalar("prof",DoubleType.INSTANCE)
											   .addScalar("hemat",DoubleType.INSTANCE).addScalar("prote",DoubleType.INSTANCE)
											   .addScalar("total",DoubleType.INSTANCE).setResultTransformer(Transformers.aliasToBean(TotaisPorDCIHVO.class)).list();
		return result;
	}
	
	
	public List<ValoresAIHPorDCIHVO> obterValoresAIHPorDCIH(final Integer ano, final Integer mes){
		ValoresAIHPorDCIHVOQueryBuilder sqlBuilder =  new ValoresAIHPorDCIHVOQueryBuilder();
		final SQLQuery q = createSQLQuery(sqlBuilder.builder());

		q.setString("prm_modulo", DominioModuloCompetencia.INT.toString());
		q.setInteger("prm_mes", mes); 
		q.setInteger("prm_ano", ano); 
		
		final List<ValoresAIHPorDCIHVO> result =  q.addScalar("dcih",StringType.INSTANCE)
												   .addScalar("tipo", StringType.INSTANCE).addScalar("descricao", StringType.INSTANCE)
												   .addScalar("nroaih",LongType.INSTANCE).addScalar("prontuario", IntegerType.INSTANCE)
												   .addScalar("procedimento", IntegerType.INSTANCE).addScalar("alta", TimestampType.INSTANCE)
												   .addScalar("dtaltaadm", DateType.INSTANCE).addScalar("dtintadm", DateType.INSTANCE)
												   .addScalar("uti", IntegerType.INSTANCE).addScalar("acomp", IntegerType.INSTANCE)
												   .addScalar("hem",DoubleType.INSTANCE).addScalar("servhosp",DoubleType.INSTANCE)
												   .addScalar("servprof",DoubleType.INSTANCE).addScalar("sadt",DoubleType.INSTANCE)
												   .addScalar("protese",DoubleType.INSTANCE).addScalar("cthseq", IntegerType.INSTANCE)
												   .addScalar("eaiseqp", IntegerType.INSTANCE).addScalar("reapresentada", IntegerType.INSTANCE)
												   .addScalar("iphcodsus", IntegerType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ValoresAIHPorDCIHVO.class)).list();
		return result;
	}

	protected DetachedCriteria obterCriteriaSeqIndSituacao(List<Integer> listaCthSeq) {
		DetachedCriteria result = DetachedCriteria.forClass(FatContasHospitalares.class, "CTH");
		result.add(Restrictions.in("CTH." + FatContasHospitalares.Fields.SEQ.toString(), listaCthSeq));
		result.setProjection(Projections.projectionList().add(Projections.property("CTH." + FatContasHospitalares.Fields.SEQ.toString()), "cthSeq")
				.add(Projections.property("CTH." + FatContasHospitalares.Fields.IND_SITUACAO.toString()), "situacaoConta"));
		result.setResultTransformer(Transformers.aliasToBean(ParCthSeqSituacaoContaVO.class));
		return result;
	}
	
	public List<ParCthSeqSituacaoContaVO> listarSeqIndSituacao(List<Integer> listaCthSeq) {
		DetachedCriteria criteria = this.obterCriteriaSeqIndSituacao(listaCthSeq);
		List<ParCthSeqSituacaoContaVO> result = this.executeCriteria(criteria);
		
		return result;
	}

	public FatContasHospitalares buscarPrimeiraContaHospitalar(String codigoDcih, DominioSituacaoConta[] situacoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);

		if (StringUtils.isNotBlank(codigoDcih)) {
			criteria.createAlias(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString());
			criteria.add(Restrictions.eq(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString() + "." + FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), codigoDcih));
		}

		if (situacoes != null) {
			criteria.add(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacoes));
		}

		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public List<FatContasHospitalares> listarContasHospitalaresIgnorandoSituacoes(String codigoDcih, DominioSituacaoConta[] situacoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);

		if (StringUtils.isNotBlank(codigoDcih)) {
			criteria.createAlias(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString());
			criteria.add(Restrictions.eq(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString() + "." + FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), codigoDcih));
		}

		if (situacoes != null) {
			criteria.add(Restrictions.not(Restrictions.in(FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacoes)));
		}

		return this.executeCriteria(criteria);
	}

	public Integer obtemPrimeiroSeqContaHospitalarFilhaDesdobramento(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CTH.toString(), FatContasHospitalares.Fields.CTH.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CTH_SEQ.toString(), seq));
		criteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));

		List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	public Integer obtemPrimeiroSeqContaHospitalarReapresentada(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString(),FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString());
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString(), seq));
		criteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));

		List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<RelacaoDeOPMNaoFaturadaVO> obterRelacaoDeOPMNaoFaturada(final Long procedimento, final Integer ano, final Integer mes, final Date dtHrInicio, final Long SSM, final String iniciaisPaciente, final Boolean reapresentada) throws ApplicationBusinessException{
		
		final StringBuilder sql = new StringBuilder(5300);
				
		sql.append(" select ")
		   .append("		  esp.sigla                as sigla ")
		   .append("		 ,esp.nome_especialidade   as especialidade ")
		   .append("		 ,eai.pac_prontuario	   as prontuario ")
		   .append("		 ,eai.pac_nome             as pacnome ")
		   .append("		 ,eai.enfermaria || eai.leito as leito ")
		   .append("		 ,eai.numero_aih           as nroaih ")
		   .append("		 ,eai.iph_cod_sus_realiz   as ssm ")
		   .append("		 ,asp.cod_tabela           as codtabela ")
		   .append("		 ,asp.iph_descricao        as descricao ")
		   .append("		 ,ich.unf_seq              as unfseq ")
		   .append("		 ,ich.dthr_realizado       as datautl ")
		   .append("		 ,ich.quantidade_realizada as quantidade ")
		   .append("		 ,ich.quantidade_realizada * ( ")
		   .append(" 										case when vic.vlr_serv_hospitalar   is null then 0 else vic.vlr_serv_hospitalar   end + " )
		   .append(" 										case when vic.vlr_serv_profissional is null then 0 else vic.vlr_serv_profissional end + " )
		   .append(" 										case when vic.vlr_sadt 				is null then 0 else vic.vlr_sadt 			  end + " )
		   .append(" 										case when vic.vlr_anestesista       is null then 0 else vic.vlr_anestesista       end " )
	   	   .append("                                     )  as valorapres")
		   
		   .append(" from ")
		   
		   .append("		  agh.fat_vlr_item_proced_hosp_comps  vic")
		   .append("		 ,agh.v_fat_associacao_procedimentos  asp ")
		   .append("		 ,agh.fat_espelhos_aih                eai")
		   .append("		 ,agh.fat_contas_hospitalares         cth")
		   .append("		 ,agh.fat_itens_conta_hospitalar      ich")
		   .append("		 ,agh.agh_especialidades              esp")
		   
		   .append(" where ")
		   
		   .append("		 eai.cth_seq  = cth.seq ")
		   .append("		 and   eai.seqp   = 1")
		   .append("		 and  cth.seq     = ich.cth_seq")
		   .append("		 and  esp.seq     = cth.esp_seq")
		   .append("		 and  cth.csp_cnv_codigo   = asp.cpg_cph_csp_seq")
		   .append("		 and  cth.csp_seq          = asp.cpg_cph_csp_cnv_codigo")
		   .append("		 and  ich.phi_seq          = asp.phi_seq")
		   .append("		 and  asp.cpg_grc_seq 	   = :prmCpgGrcSeq ")
		   .append("		 and  asp.iph_ind_situacao = :prmIphIndSituacao ");
		
		
		if(procedimento != null){
			sql.append(" and  asp.cod_tabela = :prmProcedimento");
		}
		
	   sql.append("			 and  asp.iph_pho_seq      = vic.iph_pho_seq")
	   	  .append("			 and  asp.iph_seq          = vic.iph_seq")
	   	  .append("			 and  cth.dt_encerramento >= vic.dt_inicio_competencia ")
	   	  .append("			 and  cth.dt_encerramento  <   ")	//  AND  CTH.DT_ENCERRAMENTO  < NVL(VIC.DT_FIM_COMPETENCIA, sysdate) 
	   	  .append("			      case when VIC.DT_FIM_COMPETENCIA is not null then VIC.DT_FIM_COMPETENCIA else :prmDateAtual end ")
	   	  .append("			 and eai.dci_cpe_ano 		  = :prmAno")
	   	  .append(" 		 and to_char(eai.dci_cpe_dt_hr_inicio, 'DD/MM/YYYY')   = :prmDtHrInicio ")
	   	  .append("			 and eai.dci_cpe_mes 		  = :prmMes ")
	   	  .append("			 and eai.dci_cpe_modulo       = :prmModulo ");
	   
	   if(SSM != null){
		   sql.append(" and eai.iph_cod_sus_realiz = :prmPSSM ");
	   }
	   
   	  sql.append("			 and ich.ips_numero   is not null ")
	   	  .append("			 and ich.ips_rmp_seq  is not null ")
	   	  .append("			 and ich.ind_situacao = :prmIndSituacao ");
   	
   	  if(StringUtils.isNotBlank(iniciaisPaciente)) {			
   		  sql.append(" and upper(substr(eai.pac_nome,1,1)) in (:prm_iniciais_paciente)");
   	  }
	
   	  if(reapresentada){
   		  sql.append(" and cth.cth_seq_reapresentada is not null");
   	  } else {
   		  sql.append(" and cth.cth_seq_reapresentada is null"); 
   	  }
		sql.append(" union all ")
		   .append(" select ")
		   .append("		  esp.sigla                as sigla ")
		   .append("		 ,esp.nome_especialidade   as especialidade ")
		   .append("		 ,eai.pac_prontuario	   as prontuario ")
		   .append("		 ,eai.pac_nome             as pacnome ")
		   .append("		 ,eai.enfermaria || eai.leito as leito ")
		   .append("		 ,eai.numero_aih           as nroaih ")
		   .append("		 ,eai.iph_cod_sus_realiz   as ssm ")
		   .append("		 ,pit.iph_cod_tabela       as codtabela ")
		   .append("		 ,iph.descricao       	   as descricao ")
		   .append("		 ,0			               as unfseq ")
		   .append("		 ,eai.data_internacao      as datautl ")
		   .append("		 ,sum( pit.quantidade_perdida ) as quantidade ")
		   .append("		 ,sum( ")
		   .append("			    case when pit.valor_sh     is null then 0 else pit.valor_sh     end + ")
		   .append(" 			    case when pit.valor_sp     is null then 0 else pit.valor_sp     end + ")
		   .append("			    case when pit.valor_sadt   is null then 0 else pit.valor_sadt   end + ")
		   .append(" 				case when pit.valor_anest  is null then 0 else pit.valor_anest  end ")
		   .append(" 			 )  as valorapres")
		   .append(" from ")
		   .append("		  agh.fat_espelhos_aih              eai")
		   .append("		 ,agh.fat_contas_hospitalares       cth ")
		   .append("		 ,agh.fat_perda_itens_conta         pit")
		   .append("		 ,agh.fat_itens_proced_hospitalar   iph")
		   .append("		 ,agh.fat_conv_grupo_itens_proced   cgi")
		   .append("		 ,agh.fat_proced_hosp_internos      phi")
		   .append("		 ,agh.agh_especialidades            esp")
		   .append(" where ")
		   .append("		 phi.seq = ( ")
		   .append("					 select min(cgi2.phi_seq) from agh.fat_conv_grupo_itens_proced cgi2")
		   .append("					 where     cgi2.iph_pho_seq            = cgi.iph_pho_seq")
		   .append("				 		   and cgi2.iph_seq                = cgi.iph_seq")
		   .append("						   and cgi2.cpg_cph_csp_seq        = cgi.cpg_cph_csp_seq")
		   .append("						   and cgi2.cpg_cph_csp_cnv_codigo = cgi.cpg_cph_csp_cnv_codigo")
		   .append("				   )")
		   .append("		 and phi.seq  = cgi.phi_seq ")
		   .append("		 and  pit.quantidade_perdida <> pit.quantidade_realizada ");
		if(procedimento != null){
			sql.append(" and pit.iph_cod_tabela = :prmProcedimento ");
		}
		sql.append(" and cgi.iph_pho_seq            = pit.iph_pho_seq")
		   .append(" and cgi.iph_seq                = pit.iph_seq")
		   .append(" and cgi.cpg_cph_csp_seq        = cth.csp_cnv_codigo")
		   .append(" and cgi.cpg_cph_csp_cnv_codigo = cth.csp_seq ")
		   .append(" and iph.fog_sgr_grp_seq 		= :prmGrupoOPM")
		   .append(" and iph.pho_seq		 		= :prmTabelaFaturPadrao")
	   	   .append(" and iph.pho_seq      			= pit.iph_pho_seq")
		   .append(" and iph.seq          			= pit.iph_seq")
		   .append(" and pit.cth_seq      			= cth.seq")
		   .append(" and cth.seq          			= eai.cth_seq")
   		   .append(" and eai.seqp 			        = 1 ")
		   .append(" and eai.dci_cpe_ano  			= :prmAno")
	   	   .append(" and to_char(eai.dci_cpe_dt_hr_inicio, 'DD/MM/YYYY')   = :prmDtHrInicio")
	   	   .append(" and eai.dci_cpe_mes 		 	= :prmMes ")
	   	   .append(" and eai.dci_cpe_modulo        	= :prmModulo ");
		if(SSM != null){
			sql.append(" and eai.iph_cod_sus_realiz = :prmPSSM");
		}  
		sql.append(" and esp.seq                   = cth.esp_seq");
		if(iniciaisPaciente != null && StringUtils.isNotBlank(iniciaisPaciente)) {			
			sql.append(" and upper(substr(eai.pac_nome,1,1)) in (:prm_iniciais_paciente)");
		}
   	  	if(reapresentada){
   	  		sql.append(" and cth.cth_seq_reapresentada is not null");
   	  	} else {
   	  		sql.append(" and cth.cth_seq_reapresentada is null"); 
   	  	}
   	   sql.append(" group by esp.sigla ")
	   	  .append(" ,esp.nome_especialidade")
	   	  .append(" ,eai.pac_prontuario")
	   	  .append(" ,eai.pac_nome")
	   	  .append(" ,eai.enfermaria||eai.leito")
	   	  .append(" ,eai.data_internacao")
	   	  .append(" ,eai.data_saida")
	   	  .append(" ,eai.numero_aih")
	   	  .append(" ,eai.iph_cod_sus_realiz")
	   	  .append(" ,pit.iph_cod_tabela")
	   	  .append(" ,iph.descricao")
	   	  .append(" order by 1, 3, 4, 5, 10 ");
		
		final SQLQuery q = createSQLQuery(sql.toString());
		q.setShort("prmCpgGrcSeq", getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue());
		q.setShort("prmTabelaFaturPadrao", getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO).getVlrNumerico().shortValue());
		q.setShort("prmGrupoOPM", getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPO_OPM).getVlrNumerico().shortValue());
		q.setString("prmIphIndSituacao", DominioSituacaoConta.A.toString());
		q.setString("prmIndSituacao", DominioSituacaoItenConta.N.toString());
		q.setString("prmModulo", DominioModuloCompetencia.INT.toString());
		q.setInteger("prmAno", ano); 
		q.setInteger("prmMes", mes);
		q.setString("prmDtHrInicio", DateUtil.dataToString(dtHrInicio, "dd/MM/yyyy"));
		q.setTimestamp("prmDateAtual", new Date());
		if(procedimento != null){
			q.setLong("prmProcedimento", procedimento);
		}
		if(SSM != null){
			q.setLong("prmPSSM", SSM);
		}  
		if(iniciaisPaciente != null && StringUtils.isNotBlank(iniciaisPaciente)) {
			final List<String> lst = new ArrayList<String>();
			for(char a : iniciaisPaciente.toCharArray()){
				lst.add(Character.toString(a));
			}
			q.setParameterList("prm_iniciais_paciente", lst);
		}
		final List<RelacaoDeOPMNaoFaturadaVO> result =  q.addScalar("sigla",StringType.INSTANCE)
														 .addScalar("especialidade", StringType.INSTANCE).addScalar("prontuario", IntegerType.INSTANCE)
														 .addScalar("pacnome", StringType.INSTANCE).addScalar("leito", StringType.INSTANCE)
														 .addScalar("nroaih",LongType.INSTANCE).addScalar("ssm",IntegerType.INSTANCE)
														 .addScalar("codtabela",LongType.INSTANCE).addScalar("descricao", StringType.INSTANCE)
														 .addScalar("unfseq",IntegerType.INSTANCE).addScalar("datautl", TimestampType.INSTANCE)
														 .addScalar("quantidade",LongType.INSTANCE).addScalar("valorapres",DoubleType.INSTANCE)
														 .setResultTransformer(Transformers.aliasToBean(RelacaoDeOPMNaoFaturadaVO.class)).list();
		return result;
	}
		
	public List<AihsFaturadasPorClinicaVO> obterAihsFaturadasPorClinica(final Integer ano, final Integer mes, 
			final Date dtHrInicio, final String iniciaisPaciente) throws ApplicationBusinessException{
		StringBuilder SQL = new StringBuilder(5300);
		FatContasHospitalaresQueryBuilder builder = new FatContasHospitalaresQueryBuilder();
		SQL = builder.getQUeryAihsFat(SQL,ano,mes,dtHrInicio,iniciaisPaciente);
		final SQLQuery q = createSQLQuery(SQL.toString());
		q.setInteger("pAno", ano); 
		q.setInteger("pMes", mes);
		q.setTimestamp("pDtHr", dtHrInicio);
		if(iniciaisPaciente != null && StringUtils.isNotBlank(iniciaisPaciente)) {
			final List<String> lst = new ArrayList<String>();
			for(char a : iniciaisPaciente.toCharArray()){
				lst.add(Character.toString(a));
			}
			q.setParameterList("prm_iniciais_paciente", lst);
		}
		q.addScalar("dcih",StringType.INSTANCE)
		 .addScalar("dcihlabel",StringType.INSTANCE).addScalar("codcli", IntegerType.INSTANCE)
		 .addScalar("desccli", StringType.INSTANCE).addScalar("prontuario", IntegerType.INSTANCE)
		 .addScalar("pacnome",StringType.INSTANCE).addScalar("aih", LongType.INSTANCE)
		 .addScalar("ssmrealiz", LongType.INSTANCE).addScalar("dtint", TimestampType.INSTANCE)
		 .addScalar("dtalta", TimestampType.INSTANCE).addScalar("aih",LongType.INSTANCE);
		 q.setResultTransformer(Transformers.aliasToBean(AihsFaturadasPorClinicaVO.class));
		return  q.list();
	}
	
	

	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> listarContasHospitalaresParaGerarSugestoesDesdobramento(DominioSituacaoConta[] situacoesContasHospitalares,
			DominioGrupoConvenio grupoConvenio) {
		StringBuilder hql = new StringBuilder(150);
		hql.append(" select fch.").append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" from ").append(FatContasHospitalares.class.getSimpleName()).append(" as fch ");
		hql.append(" 	join fch.").append(FatContasHospitalares.Fields.CONVENIO_SAUDE.toString()).append(" as fcs ");
		hql.append(" where fch.").append(FatContasHospitalares.Fields.IND_SITUACAO.toString()).append(" in (:situacoesContasHospitalares)");
		hql.append(" 	and fch.").append(FatContasHospitalares.Fields.MOTIVO_DESDOBRAMENTO.toString()).append(" is null ");
		hql.append(" 	and fcs.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("grupoConvenio", grupoConvenio);
		query.setParameterList("situacoesContasHospitalares", situacoesContasHospitalares);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ContaHospitalarCadastroSugestaoDesdobramentoVO> listarContasHospitalaresParaCadastrarSugestoesDesdobramento(Integer cthSeq, Byte mdsSeq,
			DominioSituacaoConta[] situacoesContas, DominioGrupoConvenio grupoConvenio) {
		StringBuilder hql = new StringBuilder(400);
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.ContaHospitalarCadastroSugestaoDesdobramentoVO(");
		hql.append("	cth.").append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.CSP_CNV_CODIGO_FIELD.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.CSP_SEQ_FIELD.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.TAH_SEQ.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.PHI_SEQ_REALIZADO_FIELD.toString());
		hql.append("	, cth.").append(FatContasHospitalares.Fields.PHI_SEQ_FIELD.toString());
		hql.append(" ) from ").append(FatContasHospitalares.class.getSimpleName()).append(" as cth ");
		hql.append(" 	join cth.").append(FatContasHospitalares.Fields.CONVENIO_SAUDE.toString()).append(" as fcs ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.IND_SITUACAO.toString()).append(" in (:situacoesContas)");
		hql.append(" 	and fcs.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio ");
		hql.append(" 	and (");
		hql.append(" 		cth.").append(FatContasHospitalares.Fields.MDS_SEQ.toString()).append(" is null ");
		hql.append(" 		or cth.").append(FatContasHospitalares.Fields.MDS_SEQ.toString()).append(" = :mdsSeq ");
		hql.append(" 	)");
		hql.append(" 	and not exists(");
		hql.append("		select 1 ");
		hql.append("		from ").append(FatContasHospitalares.class.getSimpleName()).append(" as cth2 ");
		hql.append(" 		where cth2.").append(FatContasHospitalares.Fields.CTH_SEQ_FIELD.toString()).append(" = cth.").append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" 	)");
		if (cthSeq != null) {
			hql.append(" 	and cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :cthSeq ");
		}
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("mdsSeq", mdsSeq);
		query.setParameter("grupoConvenio", grupoConvenio);
		query.setParameterList("situacoesContas", situacoesContas);
		if (cthSeq != null) {
			query.setParameter("cthSeq", cthSeq);
		}
		return query.list();
	}
	
	/**
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public FatContasHospitalares obterContaHospitalarPaciente(Integer pacCodigo, Date pDthrMovimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "coi");
		criteria.createAlias("coi." + FatContasInternacao.Fields.INTERNACAO.toString(), "int");
		criteria.createAlias("int." + AinInternacao.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.le(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString(), pDthrMovimento));
		criteria.add(Restrictions.or(
				Restrictions.isNull(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()),
				Restrictions.ge(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(), pDthrMovimento)));
		criteria.add(Restrictions.ne(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.C));
		List<FatContasHospitalares> result = executeCriteria(criteria, 0, 1, null, true);
		if (!result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public FatContasHospitalares obterFatContaHospitalar(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), seq));
		return (FatContasHospitalares) this.executeCriteriaUniqueResult(criteria);
	}

	public FatContasHospitalares obterFatContaHospitalarCompleto(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(FatContasHospitalares.Fields.SERVIDOR_MANUSEADO.toString(), "servMan", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.SERVIDOR_TEM_PROF_RESPONSAVEL.toString(), "servTPR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.ACOMODACAO.toString(), "acm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO.toString(), "cvn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.SERVIDOR.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString(), "phiRealizado", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.CTH.toString(), "cth", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.AIH.toString(), "aih", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.SITUACAO_SAIDA_PACIENTE.toString(), "sitSaidaPac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.EXCLUSAO_CRITICA.toString(), "excCrit", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), "dcih", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.MOTIVO_DESDOBRAMENTO.toString(), "motivoDesd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.TIPO_CLASSIF_SEC_SAUDE.toString(), "tpClassSecSaude", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.TIPO_AIH.toString(), "tipoAih", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString(), "cthReap", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContasHospitalares.Fields.MOTIVO_REJEICAO.toString(), "motRej", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), seq));
		return (FatContasHospitalares) this.executeCriteriaUniqueResult(criteria, true);
		
	}

	public Long listarContasHospitalaresParaGerarSugestoesDesdobramentoCount(DominioSituacaoConta[] situacoesContasHospitalares,
			DominioGrupoConvenio grupoConvenio) {
		StringBuilder hql = new StringBuilder(150);
		hql.append(" select count(fch.").append(FatContasHospitalares.Fields.SEQ.toString()).append(") ");
		hql.append(" from ").append(FatContasHospitalares.class.getSimpleName()).append(" as fch ");
		hql.append(" 	join fch.").append(FatContasHospitalares.Fields.CONVENIO_SAUDE.toString()).append(" as fcs ");
		hql.append(" where fch.").append(FatContasHospitalares.Fields.IND_SITUACAO.toString()).append(" in (:situacoesContasHospitalares)");
		hql.append(" 	and fch.").append(FatContasHospitalares.Fields.MOTIVO_DESDOBRAMENTO.toString()).append(" is null ");
		hql.append(" 	and fcs.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :grupoConvenio ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("grupoConvenio", grupoConvenio);
		query.setParameterList("situacoesContasHospitalares", situacoesContasHospitalares);
		return (Long) query.uniqueResult();
	}

	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 2) Obtém a conta
	 * hospitalar para encerramento
	 * 
	 * @param intSeq
	 * @return
	 */
	public FatContasHospitalares obterContaHospitalarPorInternacao(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		DetachedCriteria criteriaContasInt = criteria.createCriteria(FatContasHospitalares.Fields.CONTA_INTERNACAO.toString());
		criteriaContasInt.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		criteria.addOrder(Order.desc(FatContasHospitalares.Fields.PHI_SEQ_REALIZADO.toString()));
		List<FatContasHospitalares> listaContasHospitalares = executeCriteria(criteria, 0, 1, null, true);
		if (listaContasHospitalares != null && !listaContasHospitalares.isEmpty()) {
			return listaContasHospitalares.get(0);
		}
		return null;
	}
	
	public FatContasHospitalares obterContaHospitalarAbertaOuFechada(Integer seqContaHospitalarOld) {
		DetachedCriteria cri = DetachedCriteria.forClass(FatContasHospitalares.class);
		cri.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), seqContaHospitalarOld));
		cri.add(Restrictions.or(Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.A),
				Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.F)));
		return (FatContasHospitalares) executeCriteriaUniqueResult(cri);
	}
	
	/**
	 * Busca todas as contas que tiveram alta dentro do período da competência de produção em aberto e foram encerradas.
	 * 
	 * @param competenciaProducao
	 * @return
	 */
	public List<FatContasHospitalares> listarContasHospitalares(){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class,"conta");
		criteria.add(Restrictions.eq("conta."+FatContasHospitalares.Fields.IND_SITUACAO.toString(),DominioSituacaoConta.E));
		DetachedCriteria competencia = criteria.createCriteria(FatContasHospitalares.Fields.COMPETENCIA_PROD.toString(),"competencia");
		competencia.add(Restrictions.eq("competencia."+FatCompetenciaProd.Fields.IND_SITUACAO.toString(), DominioSituacaoCompProd.A));
		criteria.add(Restrictions.leProperty("conta."+FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(),
				"competencia."+ FatCompetenciaProd.Fields.DTHR_FIM_PROD.toString()));		
		return this.executeCriteria(criteria);
	}

	public FatContasHospitalares obterContaHospitalarPorCompetenciaProd(FatCompetenciaProd competenciaAnterior) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.COMPETENCIA_PROD.toString(),competenciaAnterior));
		criteria.add(Restrictions.ge(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(),competenciaAnterior.getDthrFimProd()));
		return (FatContasHospitalares) this.executeCriteriaUniqueResult(criteria);
	}

	//#2196 C1
	@SuppressWarnings("unchecked")
	public List<ContaNaoReapresentadaCPFVO> buscarContasNaoReapresentadasCPF(List<Long> cpfs, Short ano, Byte mes, Date dataHrInicio){
		StringBuilder hql = new StringBuilder(300);
		hql.append(" select distinct new br.gov.mec.aghu.faturamento.vo.ContaNaoReapresentadaCPFVO(");
		hql.append("	cth.").append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append("  , aam.").append(FatAtoMedicoAih.Fields.CPF_CNS.toString());		
		hql.append("  , cth.").append(FatContasHospitalares.Fields.IND_CONTA_REAPRESENTADA.toString());
		hql.append("  , aam.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString());
		hql.append(") from ").append(FatContasHospitalares.class.getSimpleName()).append(" as cth ");
		hql.append(" , ").append(FatAtoMedicoAih.class.getSimpleName()).append(" as aam ");
		hql.append(" , ").append(FatEspelhoAih.class.getSimpleName()).append(" as eai ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = aam.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString());
		hql.append(" and aam.").append(FatAtoMedicoAih.Fields.CPF_CNS.toString()).append(" in (:cpfs) ");
		hql.append(" and aam.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append(" = eai.").append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" and eai.").append(FatEspelhoAih.Fields.DCI_CPE_ANO.toString()).append(" = :ano ");
		hql.append(" and eai.").append(FatEspelhoAih.Fields.DCI_CPE_MES.toString()).append(" = :mes ");
		hql.append(" and eai.").append(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString()).append(" = :dataHrInicio ");
		hql.append(" and eai.").append(FatEspelhoAih.Fields.SEQP.toString()).append(" = :seqp ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameterList("cpfs", cpfs);
		query.setParameter("ano", ano);
		query.setParameter("mes", mes);
		query.setParameter("dataHrInicio", dataHrInicio);
		query.setParameter("seqp", 1);
		return query.list();
	}

	public List<ProtocolosAihsVO> getProtocolosAihs(final Integer prontuario, final String nomePaciente, final Integer codpaciente,
			final String leito, final Integer conta,final Date dtInternacao,final Date dtAlta,final Date dtEnvio,final String envio) throws ApplicationBusinessException{
		StringBuilder SQL = new StringBuilder(5300);
		FatContasHospitalaresQueryBuilder builder = new FatContasHospitalaresQueryBuilder();
		SQL = builder.getQueryProtocolosAIh(SQL,prontuario,nomePaciente,codpaciente,leito,conta,dtInternacao,dtAlta,dtEnvio,envio);
		SQLQuery q = createSQLQuery(SQL.toString());
		q = builder.getClausulasProtocolosAIh(q,prontuario,nomePaciente,codpaciente,leito,conta,dtInternacao,dtAlta,dtEnvio,envio);
		q.setResultTransformer(Transformers.aliasToBean(ProtocolosAihsVO.class));
		q.setMaxResults(600);
		return q.list();
	}

	/**
	 * #2194 - C1
	 * @return
	 */
	public List<ContaNptVO> obterContasComNPT(){
		final Short valor = 1;
		final Byte valorByte = 1;
		final DetachedCriteria criteria	= DetachedCriteria.forClass(FatContasHospitalares.class, "conta");
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "itens");
		final DetachedCriteria subDigCriteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, "itens2");
		criteria.createAlias("conta." +	FatContasHospitalares.Fields.AIH.toString(), "aih", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.in("conta." +	FatContasHospitalares.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoConta.F, DominioSituacaoConta.E)));
		criteria.add(Restrictions.eq("conta." +	FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString(), valor));
		criteria.add(Restrictions.eq("conta." +	FatContasHospitalares.Fields.CSP_SEQ.toString(), valorByte));
		criteria.add(Subqueries.propertyIn("conta."+ FatContasHospitalares.Fields.SEQ.toString(), subCriteria));
		criteria.setProjection(Projections.projectionList().add(Projections.property("conta." +	FatContasHospitalares.Fields.SEQ.toString()), ContaNptVO.Fields.SEQ.toString()).add(Projections.property("conta." + FatContasHospitalares.Fields.DT_ENCERRAMENTO.toString()), ContaNptVO.Fields.DT_ENCERRAMENTO.toString()).add(Projections.property("conta." +	FatContasHospitalares.Fields.NRO_AIH.toString()), ContaNptVO.Fields.NRO_AIH.toString()).add(Projections.property("conta." +	FatContasHospitalares.Fields.IND_SITUACAO.toString()), ContaNptVO.Fields.SITUACAO.toString()));
		subCriteria.createAlias("itens."+ FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "proc");
		subCriteria.add(Restrictions.eq("itens." + FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), DominioIndOrigemItemContaHospitalar.MPM));
		subCriteria.add(Restrictions.isNotNull("proc."+FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL));
		subCriteria.setProjection(Projections.projectionList().add(Projections.property("itens." + FatItemContaHospitalar.Fields.CTH_SEQ.toString())));
		subCriteria.add(Subqueries.propertyIn("itens."+FatItemContaHospitalar.Fields.CTH_SEQ.toString(), subDigCriteria));
		subDigCriteria.createAlias("itens2."+ FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "proc2");
		subDigCriteria.add(Restrictions.eq("itens2." + FatItemContaHospitalar.Fields.IND_ORIGEM.toString(), DominioIndOrigemItemContaHospitalar.DIG));
		subDigCriteria.add(Restrictions.isNotNull("proc2."+FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL));
		subDigCriteria.setProjection(Projections.projectionList().add(Projections.property("itens2." + FatItemContaHospitalar.Fields.CTH_SEQ.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(ContaNptVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 *
	 * @param cthSeq
	 * @return
	 */
	public MesAnoVO obterCompetenciaContaRepresentadaMeAno(Integer cthSeq){
		final DetachedCriteria criteria	= DetachedCriteria.forClass(FatContasHospitalares.class, "conta");
		criteria.createAlias("conta."+ FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), "dchi");
		criteria.createAlias("dchi."+ FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString(), "comp");
		criteria.add(Restrictions.eq("conta."+FatContasHospitalares.Fields.SEQ.toString() , cthSeq));
		criteria.add(Restrictions.isNull("conta."+FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("comp." + FatCompetencia.Fields.MES.toString()), MesAnoVO.Fields.MES.toString()).add(Projections.property("comp." + FatCompetencia.Fields.ANO.toString()), MesAnoVO.Fields.ANO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MesAnoVO.class));
		return  (MesAnoVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #2199 - C2
	 * @param mes
	 * @param ano
	 * @param dataInicio
	 * @return
	 */
	public  List<ContaRepresentadaVO> obterContasRepresentadas(Integer mes, Integer ano, Date dataInicio ){
		final DetachedCriteria criteria	= DetachedCriteria.forClass(FatEspelhoAih.class, "eai");
		criteria.createAlias("eai."+FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString() , "cth");
		criteria.createAlias("cth."+FatContasHospitalares.Fields.ESPECIALIDADE.toString() , "esp");
		criteria.createAlias("cth."+FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString() , "cth1");
		criteria.createAlias("eai."+FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString() , "iph");
		criteria.createAlias("cth1."+FatContasHospitalares.Fields.MOTIVO_REJEICAO.toString() , "rjc");
		criteria.add(Restrictions.eq("eai."+FatEspelhoAih.Fields.SEQP.toString(), 1));
		criteria.add(Restrictions.eq("eai."+FatEspelhoAih.Fields.DCI_CPE_MES.toString(), mes.byteValue()));
		criteria.add(Restrictions.eq("eai."+FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), ano.shortValue()));
		criteria.add(Restrictions.isNotNull("cth."+FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()));
		criteria.add(Restrictions.eq("eai."+FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), DominioModuloCompetencia.INT));
		criteria.add(Restrictions.eq("eai."+FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), dataInicio));
		ProjectionList p= Projections.projectionList();	
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.SEQ.toString()), ContaRepresentadaVO.Fields.SEQ.toString());
		p.add(Projections.property("eai."+FatEspelhoAih.Fields.PAC_PRONTUARIO.toString()), ContaRepresentadaVO.Fields.PAC_PRONTUARIO.toString());
		p.add(Projections.property("eai."+FatEspelhoAih.Fields.PAC_NOME.toString()), ContaRepresentadaVO.Fields.PAC_NOME.toString());
		p.add(Projections.property("esp."+AghEspecialidades.Fields.CENTRO_CUSTO_CODIGO.toString()), ContaRepresentadaVO.Fields.CCT_CODIGO.toString());
		p.add(Projections.property("eai."+FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), ContaRepresentadaVO.Fields.IPH_COD_SUS_REALIZ.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.DCI_COD_DCIH.toString()), ContaRepresentadaVO.Fields.DCI_COD_DCIH.toString());
		p.add(Projections.property("iph."+FatItensProcedHospitalar.Fields.DCIH_TRANSPLANTE.toString()), ContaRepresentadaVO.Fields.IND_DCIH_TRANSPLANTE.toString());
		p.add(Projections.property("iph."+FatItensProcedHospitalar.Fields.FAEC.toString()), ContaRepresentadaVO.Fields.IND_FAEC.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.NRO_AIH.toString()), ContaRepresentadaVO.Fields.NRO_AIH.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString()), ContaRepresentadaVO.Fields.CTH_REPRESENTADA_SEQ.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_SH.toString()), ContaRepresentadaVO.Fields.VALOR_SH.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_UTI.toString()), ContaRepresentadaVO.Fields.VALOR_UTI.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_UTIE.toString()), ContaRepresentadaVO.Fields.VALOR_UTIE.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_SP.toString()), ContaRepresentadaVO.Fields.VALOR_SP.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_ACOMP.toString()), ContaRepresentadaVO.Fields.VALOR_ACOMP.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_RN.toString()), ContaRepresentadaVO.Fields.VALOR_RN.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_SADT.toString()), ContaRepresentadaVO.Fields.VALOR_SADT.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_HEMAT.toString()), ContaRepresentadaVO.Fields.VALOR_HEMAT.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_TRANSP.toString()), ContaRepresentadaVO.Fields.VALOR_TRANSP.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_OPM.toString()), ContaRepresentadaVO.Fields.VALOR_OPM.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_ANESTESISTA.toString()), ContaRepresentadaVO.Fields.VALOR_ANESTESISTA.toString());
		p.add(Projections.property("cth."+FatContasHospitalares.Fields.VALOR_PROCEDIMENTO.toString()), ContaRepresentadaVO.Fields.VALOR_PROCEDIMENTO.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_SH.toString()), ContaRepresentadaVO.Fields.VALOR_SH1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_UTI.toString()), ContaRepresentadaVO.Fields.VALOR_UTI1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_UTIE.toString()), ContaRepresentadaVO.Fields.VALOR_UTIE1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_SP.toString()), ContaRepresentadaVO.Fields.VALOR_SP1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_ACOMP.toString()), ContaRepresentadaVO.Fields.VALOR_ACOMP1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_RN.toString()), ContaRepresentadaVO.Fields.VALOR_RN1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_SADT.toString()), ContaRepresentadaVO.Fields.VALOR_SADT1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_HEMAT.toString()), ContaRepresentadaVO.Fields.VALOR_HEMAT1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_TRANSP.toString()), ContaRepresentadaVO.Fields.VALOR_TRANSP1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_OPM.toString()), ContaRepresentadaVO.Fields.VALOR_OPM1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_ANESTESISTA.toString()), ContaRepresentadaVO.Fields.VALOR_ANESTESISTA1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_PROCEDIMENTO.toString()), ContaRepresentadaVO.Fields.VALOR_PROCEDIMENTO1.toString());
		p.add(Projections.property("cth1."+FatContasHospitalares.Fields.VALOR_PROCEDIMENTO.toString()), ContaRepresentadaVO.Fields.VALOR_PROCEDIMENTO1.toString());
		p.add(Projections.property("rjc."+FatMotivoRejeicaoConta.Fields.DESCRICAO.toString()), ContaRepresentadaVO.Fields.DESCRICAO.toString());
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ContaRepresentadaVO.class));
		return executeCriteria(criteria);
		}
	
	public List<ProtocoloAihVO> listaProtocolosAih(Date data) {
		
		ListaProtocolosAihQueryBuilder builder = new ListaProtocolosAihQueryBuilder();
		
		SQLQuery q = createSQLQuery(builder.build());
		q.setParameter("situacao", DominioSituacaoConta.E.toString());
		q.setParameter("data", data);
		q.setParameter("autSms", DominioSimNao.N.toString());
		final List<ProtocoloAihVO> result =  q.addScalar(ProtocoloAihVO.Fields.TIPO.toString(), StringType.INSTANCE)
			    .addScalar(ProtocoloAihVO.Fields.CTH_SEQ.toString(), IntegerType.INSTANCE).addScalar(ProtocoloAihVO.Fields.DATA_INTERNACAO.toString(), DateType.INSTANCE)
			    .addScalar(ProtocoloAihVO.Fields.DATA_ALTA.toString(),DateType.INSTANCE).addScalar(ProtocoloAihVO.Fields.PRONTUARIO.toString(), IntegerType.INSTANCE)
			    .addScalar(ProtocoloAihVO.Fields.NOME.toString(), StringType.INSTANCE).addScalar(ProtocoloAihVO.Fields.COD_TABELA.toString(),LongType.INSTANCE)
			    .addScalar(ProtocoloAihVO.Fields.PROCEDIMENTO.toString(), StringType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ProtocoloAihVO.class)).list();
		return result;
	}

	public List<ContaApresentadaPacienteProcedimentoVO> obterContasPorEspecialidade(Short codigoEspecialidade, Date dtHoraInicio, Byte mes, Short ano){
		StringBuilder sql = new StringBuilder(35);
		obterContasPorEsquecialidadeQuery1(sql, codigoEspecialidade);
		sql.append(" union ");
		obterContasPorEsquecialidadeQuery2(sql, codigoEspecialidade);
		sql.append(" ORDER BY 1   ");
		SQLQuery q = createSQLQuery(sql.toString());
		q.setParameter("grupoConvenio", "S");
		q.setParameter("seqp", 1);
		q.setParameter("modulo", "INT");
		q.setParameterList("situacoes", new String[]{DominioSituacaoConta.E.toString(), DominioSituacaoConta.O.toString()});
		q.setParameter("ano", ano); 
		q.setParameter("mes", mes);
		q.setParameter("dtInicio", dtHoraInicio);
		if (codigoEspecialidade != null) {
			q.setParameter("especialidade", codigoEspecialidade);
		}
		final List<ContaApresentadaPacienteProcedimentoVO> result =  q.addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.ESPECIALIDADE.toString(), StringType.INSTANCE)
			    .addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.ESP_SEQ.toString(), ShortType.INSTANCE).addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.NRO_AIH.toString(), LongType.INSTANCE)
			    .addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.NOME.toString(),StringType.INSTANCE).addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.PROCEDIMENTO.toString(), StringType.INSTANCE)
			    .addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.CTH_SEQ.toString(), IntegerType.INSTANCE).addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.PRONTUARIO.toString(),IntegerType.INSTANCE)
			    .addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.COD_SUS.toString(), LongType.INSTANCE).addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.DATA_INTERNACAO.toString(), DateType.INSTANCE)
			    .addScalar(ContaApresentadaPacienteProcedimentoVO.Fields.TOTAL.toString(), BigDecimalType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ContaApresentadaPacienteProcedimentoVO.class)).list();

		return result;
	}

	private void obterContasPorEsquecialidadeQuery1(StringBuilder sql, Short codigoEspecialidade) {
		sql.append("SELECT DISTINCT ESP.NOME_ESPECIALIDADE ESPECIALIDADE, \n");
		sql.append("  ESP.SEQ SEQESPECIALIDADE, \n");
		sql.append("  CTH.NRO_AIH NROAIH, \n");
		sql.append("  PAC.NOME NOME, \n");
		sql.append("  IPH.DESCRICAO PROCEDIMENTO, \n");
		sql.append("  EAI.CTH_SEQ CTHSEQ, \n");
		sql.append("  EAI.PAC_PRONTUARIO PRONTUARIO, \n");
		sql.append("  EAI.IPH_COD_SUS_REALIZ CODSUS, \n");
		sql.append("  CTH.DT_INT_ADMINISTRATIVA DATAINTERNACAO, \n");
		sql.append("  COALESCE(cth.VALOR_SH,0) + COALESCE(cth.VALOR_UTI,0) + COALESCE(cth.VALOR_UTIE,0) + COALESCE(cth.VALOR_SP,0) + COALESCE(cth.VALOR_ACOMP,0) + COALESCE(cth.VALOR_RN,0) + COALESCE(cth.VALOR_SADT,0) + COALESCE(cth.VALOR_HEMAT,0) + COALESCE(cth.VALOR_TRANSP,0) + COALESCE(cth.VALOR_ANESTESISTA,0) + COALESCE(cth.VALOR_PROCEDIMENTO,0) TOTAL \n");
		sql.append("FROM AGH_ESPECIALIDADES ESP, \n");
		sql.append("  FAT_ITENS_PROCED_HOSPITALAR IPH, \n");
		sql.append("  FAT_ESPELHOS_AIH EAI, \n");
		sql.append("  AIP_PACIENTES PAC, \n");
		sql.append("  FAT_DADOS_CONTA_SEM_INT DCS, \n");
		sql.append("  FAT_CONTAS_INTERNACAO COI, \n");
		sql.append("  FAT_CONVENIOS_SAUDE CNV, \n");
		sql.append("  FAT_CONTAS_HOSPITALARES CTH \n");
		sql.append("WHERE Cth.ESP_SEQ                = ESP.SEQ \n");
		if (codigoEspecialidade != null) {
			sql.append(" AND ESP.SEQ = :especialidade \n ");
		}
		sql.append("AND CNV.CODIGO                 = CTH.CSP_CNV_CODIGO \n");
		sql.append("AND CNV.GRUPO_CONVENIO         = :grupoConvenio  \n");
		sql.append("AND COI.CTH_SEQ                = CTH.SEQ \n");
		sql.append("AND DCS.SEQ                    = COI.DCS_SEQ \n");
		sql.append("AND PAC.CODIGO                 = dcs.PAC_CODIGO \n");
		sql.append("AND IPH.PHO_SEQ                = EAI.IPH_PHO_SEQ_REALIZ \n");
		sql.append("AND IPH.SEQ                    = EAI.IPH_SEQ_REALIZ \n");
		sql.append("AND EAI.SEQP                   = :seqp  \n");
		sql.append("AND EAI.CTH_SEQ                = CTH.SEQ \n");
		sql.append("AND EAI.DCI_CPE_MODULO         = :modulo \n");
		sql.append("AND EAI.DCI_CPE_DT_HR_INICIO   = :dtInicio \n");
		sql.append("AND EAI.DCI_CPE_MES            = :mes         \n");
		sql.append("AND EAI.DCI_CPE_ANO            = :ano         \n");
		sql.append("AND CTH.IND_SITUACAO          IN (:situacoes) \n");
		sql.append("AND CTH.CTH_SEQ_REAPRESENTADA IS NULL");
	}

	private void obterContasPorEsquecialidadeQuery2(StringBuilder sql, Short codigoEspecialidade) {
		sql.append("SELECT DISTINCT ESP.NOME_ESPECIALIDADE ESPECIALIDADE, \n");
		sql.append("  ESP.SEQ SEQESPECIALIDADE, \n");
		sql.append("  CTH.NRO_AIH NROAIH, \n");
		sql.append("  PAC.NOME NOME, \n");
		sql.append("  IPH.DESCRICAO PROCEDIMENTO, \n");
		sql.append("  EAI.CTH_SEQ CTHSEQ, \n");
		sql.append("  EAI.PAC_PRONTUARIO PRONTUARIO, \n");
		sql.append("  EAI.IPH_COD_SUS_REALIZ CODSUS, \n");
		sql.append("  CTH.DT_INT_ADMINISTRATIVA DATAINTERNACAO, \n");
		sql.append("  COALESCE(cth.VALOR_SH,0) + COALESCE(cth.VALOR_UTI,0) + COALESCE(cth.VALOR_UTIE,0) + COALESCE(cth.VALOR_SP,0) + COALESCE(cth.VALOR_ACOMP,0) + COALESCE(cth.VALOR_RN,0) + COALESCE(cth.VALOR_SADT,0) + COALESCE(cth.VALOR_HEMAT,0) + COALESCE(cth.VALOR_TRANSP,0) + COALESCE(cth.VALOR_ANESTESISTA,0) + COALESCE(cth.VALOR_PROCEDIMENTO,0) TOTAL \n");
		sql.append("FROM AIP_PACIENTES PAC, \n");
		sql.append("  AIN_INTERNACOES INT, \n");
		sql.append("  AGH_ESPECIALIDADES ESP, \n");
		sql.append("  FAT_ITENS_PROCED_HOSPITALAR IPH, \n");
		sql.append("  FAT_ESPELHOS_AIH EAI, \n");
		sql.append("  FAT_CONTAS_INTERNACAO COI, \n");
		sql.append("  FAT_CONVENIOS_SAUDE CNV, \n");
		sql.append("  FAT_CONTAS_HOSPITALARES CTH \n");
		sql.append("WHERE CTH.ESP_SEQ              = ESP.SEQ \n");
		if (codigoEspecialidade != null) {
			sql.append(" AND ESP.SEQ = :especialidade \n ");
		}
		sql.append("AND CNV.CODIGO                 = CTH.CSP_CNV_CODIGO \n");
		sql.append("AND CNV.GRUPO_CONVENIO         = :grupoConvenio \n");
		sql.append("AND COI.CTH_SEQ                = CTH.SEQ \n");
		sql.append("AND INT.SEQ                    = COI.INT_SEQ \n");
		sql.append("AND PAC.CODIGO                 = INT.PAC_CODIGO \n");
		sql.append("AND IPH.PHO_SEQ                = EAI.IPH_PHO_SEQ_REALIZ \n");
		sql.append("AND IPH.SEQ                    = EAI.IPH_SEQ_REALIZ \n");
		sql.append("AND EAI.SEQP                   = :seqp \n");
		sql.append("AND EAI.CTH_SEQ                = CTH.SEQ \n");
		sql.append("AND EAI.DCI_CPE_MODULO         = :modulo \n");
		sql.append("AND EAI.DCI_CPE_DT_HR_INICIO   = :dtInicio \n");
		sql.append("AND EAI.DCI_CPE_MES            = :mes \n");
		sql.append("AND EAI.DCI_CPE_ANO            = :ano \n");
		sql.append("AND CTH.IND_SITUACAO          IN (:situacoes)\n");
		sql.append("AND CTH.CTH_SEQ_REAPRESENTADA IS NULL \n");
		sql.append("AND INT.SEQ                    = \n");
		sql.append("  (SELECT MAX(CIN.INT_SEQ) \n");
		sql.append("  FROM FAT_CONTAS_INTERNACAO CIN \n");
		sql.append("  WHERE CIN.CTH_SEQ = CTH.SEQ \n");
		sql.append("  )");
	}
	
	/**
	 * @param dciCpeDtHrInicio
	 * @param dciCpeModulo
	 * @param dciCpeMes
	 * @param dciCpeAno
	 * @param codAtoOPM
	 * @return
	 */
	public List<ClinicaPorProcedimentoVO> listarClinicaPorProcedimento(
			final Date dciCpeDtHrInicio,
			final DominioModuloCompetencia dciCpeModulo, 
			final Byte dciCpeMes,
			final Short dciCpeAno,
			final Byte codAtoOPM ) {
		ClinicaPorProcedimentoQueryBuilder builder =  new ClinicaPorProcedimentoQueryBuilder();
		final SQLQuery sqlQuery = createSQLQuery(builder.builder());
		sqlQuery.setTimestamp(ClinicaPorProcedimentoQueryBuilder.Parameter.P_DT_HR_INICIO.name(), dciCpeDtHrInicio);
		sqlQuery.setParameter(ClinicaPorProcedimentoQueryBuilder.Parameter.P_INT.name(), dciCpeModulo.name());
		sqlQuery.setParameter(ClinicaPorProcedimentoQueryBuilder.Parameter.P_MES.name(), dciCpeMes);
		sqlQuery.setParameter(ClinicaPorProcedimentoQueryBuilder.Parameter.P_ANO.name(), dciCpeAno);
		sqlQuery.setParameter(ClinicaPorProcedimentoQueryBuilder.Parameter.P_COD_ATO_OPM.name(), codAtoOPM);
		sqlQuery.addScalar(ClinicaPorProcedimentoVO.Fields.ESPECIALIDADE_AIH.toString(), ByteType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.CLINICA_DESCRICAO.toString(), StringType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.PROCEDIMENTO_DESCRICAO.toString(), StringType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.QUANTIDADE.toString(), LongType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SADT.toString(), BigDecimalType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_HOSP.toString(), BigDecimalType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_PROF.toString(), BigDecimalType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.QUANT_PROC.toString(), LongType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.SADT_PROC.toString(), BigDecimalType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.SERV_HOSP_PROC.toString(), BigDecimalType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.SERV_PROF_PROC.toString(), BigDecimalType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.QUANT_AIH.toString(), LongType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.SADT_AIH.toString(), BigDecimalType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.HOSP_AIH.toString(), BigDecimalType.INSTANCE).addScalar(ClinicaPorProcedimentoVO.Fields.PROF_AIH.toString(), BigDecimalType.INSTANCE)
			.addScalar(ClinicaPorProcedimentoVO.Fields.ORDEM.toString(), ShortType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ClinicaPorProcedimentoVO.class));
		return sqlQuery.list();
	}

	public FatContasHospitalares obterPorContaRepresentada(Integer cthRepresentadaSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.CTH_REAPRESENTADA_SEQ.toString(), cthRepresentadaSeq));
		return (FatContasHospitalares) executeCriteriaUniqueResult(criteria);
	}
	
	public FatContasHospitalares obterCursorContaHCPA(Integer seqConta, String indAutorizadoSms){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), seqConta));
		
		String sql =  " COALESCE( " + FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.name() + ", '" + indAutorizadoSms + "') = ? ";
		Object[] values = { indAutorizadoSms };
		Type[] types = { StringType.INSTANCE};
		
		criteria.add(Restrictions.sqlRestriction(sql, values, types));
		
		return (FatContasHospitalares) executeCriteriaUniqueResult(criteria);
	}
	
	public FatContasHospitalares obterRefreshContaHospitalar(Integer seq){
		FatContasHospitalares conta = super.obterPorChavePrimaria(seq);
		refresh(conta);
		return conta;

	}
	public List<FatContasHospitalares> obterCursorContaFilha(Integer seqConta, Byte tahSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), seqConta));
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.TAH_SEQ.toString(), tahSeq));
		return executeCriteria(criteria);
	}
	/**
	 * #36436 C10 Método para obter descrição de Motivos Rejeição Conta por fatContasHospitalares.Seq
	 */
	public List<FatMotivoRejeicaoContasVO> obterDescricaoMotivosRejeicaoContaPorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "CTH");
		criteria.setProjection(Projections.projectionList().add(Projections.property("RJC."+FatMotivoRejeicaoConta.Fields.DESCRICAO.toString()),
				FatMotivoRejeicaoContasVO.Fields.DESCRICAO.toString())).createAlias("CTH"+"."+FatContasHospitalares.Fields.MOTIVO_REJEICAO.toString(), "RJC", JoinType.INNER_JOIN)
			.add(Restrictions.eq("CTH."+FatContasHospitalares.Fields.SEQ.toString(), seq));

		criteria.setResultTransformer(Transformers.aliasToBean(FatMotivoRejeicaoContasVO.class));
		return executeCriteria(criteria);
	}


	public List<FatContasHospitalares> obterContasDataEnvioSms(Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.E));
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.DT_ENVIO_SMS.toString(), data));
		
		return executeCriteria(criteria);
}
	
	public BigDecimal contaLaudoPGerarArquivoSms(Date data) {
		StringBuilder sql = new StringBuilder(600);
		sql.append("SELECT COUNT(*)  ");
		sql.append("FROM FAT_CONTAS_HOSPITALARES cth  ");
		sql.append("WHERE IND_SITUACAO = 'E'  ");
		sql.append("AND DT_ENVIO_SMS   = :p_dt_envio  ");
		sql.append("AND NOT EXISTS  ");
		sql.append("  (SELECT 1  ");
		sql.append("  FROM fat_autorizados_cma fcma ,  ");
		sql.append("    fat_itens_proced_hospitalar iph ,  ");
		sql.append("    fat_caract_item_proc_hosp cih ,  ");
		sql.append("    fat_tipo_caract_itens tct  ");
		sql.append("  WHERE fcma.cth_seq     = cth.seq  ");
		sql.append("  AND fcma.cod_sus_cma   = iph.cod_tabela  ");
		sql.append("  AND iph.seq            = cih.iph_seq  ");
		sql.append("  AND iph.pho_seq        = cih.iph_pho_seq  ");
		sql.append("  AND tct.seq            = cih.tct_seq  ");
		sql.append("  AND tct.caracteristica = 'Deve ser autorizado pela SMS'  ");
		sql.append("  )");
		
		SQLQuery q = createSQLQuery(sql.toString());
		q.setParameter("p_dt_envio", DateUtil.truncaData(data));

		return (BigDecimal)q.uniqueResult();
	}

	public BigDecimal contaLaudoMPGerarArquivoSms(Date data) {
		StringBuilder sql = new StringBuilder(800);
		sql.append("SELECT COUNT(*) ");
		sql.append("FROM FAT_CONTAS_HOSPITALARES cth,  ");
		sql.append("  FAT_ESPELHOS_AIH eai  ");
		sql.append("WHERE cth.IND_SITUACAO      = 'E'  ");
		sql.append("AND cth.dt_envio_sms        = :p_dt_envio  ");
		sql.append("AND cth.seq                 = eai.cth_seq  ");
		sql.append("AND eai.iph_cod_sus_realiz <> eai.iph_cod_sus_solic  ");
		sql.append("AND eai.SEQP                = 1  ");
		sql.append("AND NOT EXISTS  ");
		sql.append("  (SELECT 1  ");
		sql.append("  FROM fat_autorizados_cma fcma ,  ");
		sql.append("    fat_itens_proced_hospitalar iph ,  ");
		sql.append("    fat_caract_item_proc_hosp cih ,  ");
		sql.append("    fat_tipo_caract_itens tct  ");
		sql.append("  WHERE fcma.cth_seq     = cth.seq  ");
		sql.append("  AND fcma.cod_sus_cma   = iph.cod_tabela  ");
		sql.append("  AND iph.seq            = cih.iph_seq  ");
		sql.append("  AND iph.pho_seq        = cih.iph_pho_seq  ");
		sql.append("  AND tct.seq            = cih.tct_seq  ");
		sql.append("  AND tct.caracteristica = 'Deve ser autorizado pela SMS'  ");
		sql.append("  )");
		
		SQLQuery q = createSQLQuery(sql.toString());
		q.setParameter("p_dt_envio", DateUtil.truncaData(data));

		return (BigDecimal)q.uniqueResult();
	}

	public BigDecimal contaLaudSGerarArquivoSms(Date data, Short grupoOpm) {
		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT COUNT(*)  ");
		sql.append("FROM FAT_ITENS_PROCED_HOSPITALAR iph,  ");
		sql.append("  FAT_ATOS_MEDICOS_AIH aam,  ");
		sql.append("  FAT_CONTAS_HOSPITALARES CTH  ");
		sql.append("WHERE dt_envio_sms = :p_dt_envio  ");
		sql.append("AND cth.ind_situacao = 'E'  ");
		sql.append("AND cth.seq = aam.eai_cth_seq  ");
		sql.append("AND iph.fog_sgr_grp_seq <> :P_GRUPO_OPM  ");
		sql.append("AND iph.seq = aam.iph_seq  ");
		sql.append("AND iph.pho_seq = aam.iph_pho_seq  ");
		sql.append("AND (NVL(aam.valor_anestesista,0) + NVL(aam.valor_procedimento,0) + NVL(aam.valor_sadt,0) + NVL(aam.valor_serv_hosp,0) + NVL(aam.valor_serv_prof,0)) > 0  ");
		sql.append("AND NOT EXISTS  ");
		sql.append("  (SELECT 1  ");
		sql.append("  FROM fat_autorizados_cma fcma ,  ");
		sql.append("    fat_itens_proced_hospitalar iph ,  ");
		sql.append("    fat_caract_item_proc_hosp cih ,  ");
		sql.append("    fat_tipo_caract_itens tct  ");
		sql.append("  WHERE fcma.cth_seq     = cth.seq  ");
		sql.append("  AND fcma.cod_sus_cma   = iph.cod_tabela  ");
		sql.append("  AND iph.seq            = cih.iph_seq  ");
		sql.append("  AND iph.pho_seq        = cih.iph_pho_seq  ");
		sql.append("  AND tct.seq            = cih.tct_seq  ");
		sql.append("  AND tct.caracteristica = 'Deve ser autorizado pela SMS'  ");
		sql.append("  )");
		
		SQLQuery q = createSQLQuery(sql.toString());
		q.setParameter("p_dt_envio", DateUtil.truncaData(data));
		q.setParameter("P_GRUPO_OPM", grupoOpm);

		return (BigDecimal)q.uniqueResult();
	}

	public Object[] obterAtendimentoemergencia(Integer cthSeq) {
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT atd.atu_seq , ");
		sql.append("  cth.valor_utie ");
		sql.append("FROM fat_contas_hospitalares cth , ");
		sql.append("  fat_contas_internacao coi , ");
		sql.append("  agh_atendimentos atd ");
		sql.append("WHERE cth.seq   = :p_cth_seq ");
		sql.append("AND cth.seq     = coi.cth_seq ");
		sql.append("AND coi.int_seq = atd.int_seq");
		
		SQLQuery q = createSQLQuery(sql.toString());
		q.setParameter("p_cth_seq", cthSeq);

		List<Object[]> lista = (List<Object[]>)q.list();
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public Integer persistirContasCobradasEmLote(List<String> listCodigoDCHI, String servidorLogado, RapServidores servidorManuseado) {
		javax.persistence.Query query = createQuery(
				"   UPDATE " + FatContasHospitalares.class.getSimpleName() +" "
						+ "  SET " + FatContasHospitalares.Fields.IND_SITUACAO +" = :situacaoUpdate " 
						+ ",  " + FatContasHospitalares.Fields.ALTERADO_EM +" = :alteradoEm " 
						+ ",  " + FatContasHospitalares.Fields.ALTERADO_POR +" = :alteradoPor " 
						+ ",  " + FatContasHospitalares.Fields.SERVIDOR_MANUSEADO +"."+RapServidores.Fields.MATRICULA+" = :servidorManuseadoMatricula "
						+ ",  " + FatContasHospitalares.Fields.SERVIDOR_MANUSEADO +"."+RapServidores.Fields.VIN_CODIGO+" = :servidorManuseadoCodigo "
				+ " WHERE " 
						+ FatContasHospitalares.Fields.IND_SITUACAO +" = :situacaoAtual AND "
						+ FatContasHospitalares.Fields.IND_AUTORIZADO_SMS +" = :indAutorizadoSMS AND "
						+ FatContasHospitalares.Fields.NRO_AIH +" IS NOT NULL AND "
						+ FatContasHospitalares.Fields.DCI_COD_DCIH +" IN  (:listCodigoDCHI)" );

		query.setParameter("situacaoUpdate", DominioSituacaoConta.O);
		query.setParameter("alteradoEm", new Date());
		query.setParameter("alteradoPor", servidorManuseado.getUsuario());
		query.setParameter("servidorManuseadoMatricula", servidorManuseado.getId().getMatricula());
		query.setParameter("servidorManuseadoCodigo", servidorManuseado.getId().getVinCodigo());
		query.setParameter("situacaoAtual", DominioSituacaoConta.E);
		query.setParameter("indAutorizadoSMS", "S");
		query.setParameter("listCodigoDCHI", listCodigoDCHI );
		
		return query.executeUpdate();

	}

	public FatContasHospitalares obterFatContaHospitalares(FatContasInternacao fatContasInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "CHP");
		
		criteria.createAlias("CHP."+FatContasHospitalares.Fields.CONTA_INTERNACAO.toString() , "CIN", JoinType.INNER_JOIN);
		criteria.createAlias("CIN."+FatContasInternacao.Fields.INTERNACAO.toString() , "INT", JoinType.INNER_JOIN);
		
		criteria.setFetchMode(FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(),FetchMode.JOIN);
		criteria.setFetchMode(FatContasInternacao.Fields.INTERNACAO.toString(),FetchMode.JOIN);
		
		
		criteria.add(Restrictions.eq(FatContasHospitalares.Fields.SEQ.toString(), fatContasInternacao.getContaHospitalar().getSeq()));
		criteria.addOrder(Order.asc(FatContasHospitalares.Fields.CRIADO_EM.toString()));
		
		if(!executeCriteria(criteria).isEmpty()){
			return (FatContasHospitalares) executeCriteria(criteria).get(0);
		}else{ 
			return null;
		}
		
	}
	
}
