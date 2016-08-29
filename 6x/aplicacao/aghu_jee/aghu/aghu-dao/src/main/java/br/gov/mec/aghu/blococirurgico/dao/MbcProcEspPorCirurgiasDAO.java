package br.gov.mec.aghu.blococirurgico.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.jdbc.Work;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.CursorCirurgiaSusProceHospitalarInternoVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoLateralidadeProcCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcEspPorCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentoCirurgicoPacienteVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioTransplantesRealizTMOOutrosVO;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioRegistroDaNotaDeSalaProcedimentosVO;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.exames.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcProcEspPorCirurgiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProcEspPorCirurgias> {

	private static final long serialVersionUID = 3168031757059633262L;
	
	private static final Log LOG = LogFactory.getLog(MbcProcEspPorCirurgiasDAO.class);
	
	private static final String PONTO = ".";
	private static final String ALIAS_PPC = "PPC";
	private static final String ALIAS_PHI = "PHI";
	private static final String ALIAS_CGI = "CGI";
	private static final String ALIAS_IPH = "IPH";
	

	/** ORADB: CURSOR C_PPC_PHI TELA listarCirurgias */
	public List<Integer> obterPhisSeqPorMbcProcEspPorCirurgias(final Integer crgSeq, final DominioIndRespProc indRespProc) {
		final StringBuilder hql = new StringBuilder(180);

		hql.append("SELECT ").append(" PHI.").append(FatProcedHospInternos.Fields.SEQ.toString())

		.append(" FROM ").append(MbcProcEspPorCirurgias.class.getSimpleName()).append(" PPC, ").append(FatProcedHospInternos.class.getSimpleName()).append(" PHI ")

		.append(" WHERE 1=1 ").append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()).append(" = PHI.")
		.append(FatProcedHospInternos.Fields.PCI_SEQ.toString())

		.append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.SITUACAO.toString()).append(" = :PRM_SITUACAO").append("  AND PPC.")
		.append(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()).append(" = :PRM_IND_RESP_PROC").append("  AND PPC.")
		.append(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()).append(" = :PRM_CRG_SEQ").append("  AND PHI.")
		.append(FatProcedHospInternos.Fields.SITUACAO.toString()).append(" = :PRM_PHI_SITUACAO");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("PRM_SITUACAO", DominioSituacao.A);
		query.setParameter("PRM_PHI_SITUACAO", DominioSituacao.A);
		query.setParameter("PRM_IND_RESP_PROC", indRespProc);
		query.setParameter("PRM_CRG_SEQ", crgSeq);

		return query.list();
	}

	public List<MbcProcEspPorCirurgias> obterProcedimentosCirurgicos(Integer atdSeq, Integer pacCodigo) throws ApplicationBusinessException {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);

		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString());
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString() + "." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString() + "." + MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString() + "." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString() + "." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), true));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));

		return executeCriteria(criteria);
	}

	public Short buscarRnCthcAtuEncPrv(Integer pacCodigo, Date dthrPci, DominioIndRespProc indRespProc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));

		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "CIRURGIA");
		criteria.add(Restrictions.eq("CIRURGIA." + MbcCirurgias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("CIRURGIA." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString(), dthrPci));

		criteria.createAlias("CIRURGIA." + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), "CONVENIO");
		criteria.add(Restrictions.eq("CONVENIO." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));

		criteria.setProjection(Projections.property(MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString()));

		List<Short> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<DominioIndRespProc> pesquisarStatusResponsabilidadeProcedimentosCirurgicos(Integer seq, String stringSeparator) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "cirurgia");
		criteria.setProjection(Projections.projectionList().add(Projections.property(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString())));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA.toString() + stringSeparator + MbcCirurgias.Fields.SEQ, seq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));

		return executeCriteria(criteria);
	}

	public List<String> pesquisaProcedimentoCirurgico(Integer seq, String stringSeparator, DominioIndRespProc indRespProc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString());
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "cirurgia");
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString() + stringSeparator + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS, "pc");
		criteria.setProjection(Projections.projectionList().add(Projections.property("pc.descricao")));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA.toString() + stringSeparator + MbcCirurgias.Fields.SEQ, seq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));

		return executeCriteria(criteria);
	}

	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspPorCirurgias(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq,
			final DominioIndRespProc indRespProc) {
		List<MbcProcEspPorCirurgias> result = null;

		final DetachedCriteria criteria = getCriteriaMbcProcEspPorCirurgias(objPesquisa, ppcEprPciSeq, ppcEprEspSeq, indRespProc);
		result = executeCriteria(criteria, 0, 100, new HashMap<String, Boolean>());

		return result;
	}

	public Long pesquisarMbcProcEspPorCirurgiasCount(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq, final DominioIndRespProc indRespProc) {
		final DetachedCriteria criteria = getCriteriaMbcProcEspPorCirurgias(objPesquisa, ppcEprPciSeq, ppcEprEspSeq, indRespProc);
		Long result = executeCriteriaCount(criteria);

		return result;
	}

	private DetachedCriteria getCriteriaMbcProcEspPorCirurgias(final Object objPesquisa, final String ppcEprPciSeq, final String ppcEprEspSeq,
			final DominioIndRespProc indRespProc) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);

		if (indRespProc != null) {
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));
		}

		if (!StringUtils.isEmpty(objPesquisa.toString()) && CoreUtil.isNumeroInteger(objPesquisa.toString())) {
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
		}

		if (!StringUtils.isEmpty(ppcEprPciSeq)) {
			if (CoreUtil.isNumeroInteger(ppcEprPciSeq)) {
				criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString(), Integer.valueOf(ppcEprPciSeq)));
			} else {
				criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "PROCEDIMENTO");
				criteria.add(Restrictions.ilike("PROCEDIMENTO." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), ppcEprPciSeq, MatchMode.ANYWHERE));

			}
		}

		if (!StringUtils.isEmpty(ppcEprEspSeq)) {
			if (CoreUtil.isNumeroShort(ppcEprEspSeq)) {
				criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString(), Integer.valueOf(ppcEprEspSeq)));
			} else {
				criteria.createAlias(MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), "PROC_ESP");
				criteria.createAlias(MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "ESPECIALIDADE");
				criteria.add(Restrictions.ilike("PROC_ESP.ESPECIALIDADE." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), ppcEprEspSeq, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	public List<MbcProcEspPorCirurgias> retornaProcEspCirurgico(MbcCirurgias cirurgia) {
		DominioIndRespProc[] indRespPrc = { DominioIndRespProc.DESC, DominioIndRespProc.NOTA };

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class).add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA.toString(), cirurgia))
		.add(Restrictions.in(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespPrc))
		.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));

		List<MbcProcEspPorCirurgias> profCirurgias = executeCriteria(criteria);

		return profCirurgias;
	}

	public List<MbcProcEspPorCirurgias> obterProcedimentosEspeciaisPorCirurgia(Integer codigoPaciente) {
		DominioIndRespProc[] indRespPrc = { DominioIndRespProc.DESC, DominioIndRespProc.NOTA };

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "procedimento");
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg");
		
		criteria.add(Restrictions.eq("crg" + "." + MbcCirurgias.Fields.PAC_CODIGO.toString(), codigoPaciente))
		.add(Restrictions.ne("crg" + "." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC))
		.add(Restrictions.in(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespPrc))
		.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A)).addOrder(Order.desc("crg" + "." + MbcCirurgias.Fields.DATA.toString()));

		List<MbcProcEspPorCirurgias> profCirurgias = executeCriteria(criteria);

		return profCirurgias;
	}

	public List<ProcedimentosPOLVO> pesquisarProcedimentosPOL(Integer codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");

		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("crg" + "." + MbcCirurgias.Fields.PAC_CODIGO.toString()), ProcedimentosPOLVO.Fields.PAC_CODIGO.toString())
		.add(Projections.property("crg" + "." + MbcCirurgias.Fields.SEQ.toString()), ProcedimentosPOLVO.Fields.SEQ.toString())
		.add(Projections.property("crg" + "." + MbcCirurgias.Fields.SITUACAO.toString()), ProcedimentosPOLVO.Fields.SITUACAO.toString())
		.add(Projections.property("crg" + "." + MbcCirurgias.Fields.DATA.toString()), ProcedimentosPOLVO.Fields.DATA.toString())
		.add(Projections.property("crg" + "." + MbcCirurgias.Fields.TEM_DESCRICAO.toString()), ProcedimentosPOLVO.Fields.TEM_DESCRICAO.toString())
		.add(Projections.property("crg" + "." + MbcCirurgias.Fields.DIGITA_NOTA_SALA.toString()), ProcedimentosPOLVO.Fields.DIGITA_NOTA_SALA.toString())
		.add(Projections.property(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()), CirurgiasInternacaoPOLVO.Fields.IND_RESP_PROC.toString())
		.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), ProcedimentosPOLVO.Fields.DESCRICAO.toString())
		.add(Projections.property(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()), ProcedimentosPOLVO.Fields.EPR_PCI_SEQ.toString());

		criteria.setProjection(projection);

		// Joins com tabelas MbcCirurgias, MbcProcedimentoCirurgicos,
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg");
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");

		// Where
		criteria.add(Restrictions.eq("crg" + "." + MbcCirurgias.Fields.PAC_CODIGO.toString(), codigo));
		// criteria.add(Restrictions.isNotNull("crg" + "." + MbcCirurgias.Fields.TEM_DESCRICAO.toString()));

		criteria.add(Restrictions.eq("ppc" + "." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("pci" + "." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));

		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosPOLVO.class));

		return executeCriteria(criteria);

	}

	public String obterPacOruAccNummer(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppcx");

		ProjectionList projection = Projections.projectionList()

		.add(Projections.max(MbcProcEspPorCirurgias.Fields.PAC_ORU_ACC_NUMMER.toString()));

		criteria.setProjection(projection);

		// Where
		criteria.add(Restrictions.eq("ppcx" + "." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), seq));
		criteria.add(Restrictions.eq("ppcx" + "." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		

		return (String) executeCriteriaUniqueResult(criteria);

	}

	public List<MbcProcEspPorCirurgias> pesquisarProcEspCirurgico(Integer crgSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);

		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "proc");

		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), true));

		return executeCriteria(criteria);

	}

	public List<MbcProcEspPorCirurgias> pesquisarProcEspCirurgicoPrincipalAgendamentoPorCrgSeq(Integer crgSeq, DominioSituacao situacao, Boolean indPrincipal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		if(situacao!=null){
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), situacao));
		}
		
		if(indPrincipal != null){
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), indPrincipal));
		}	
		return executeCriteria(criteria);
	}

	/**
	 * Atualiza a cota de procedimentos da cirurgia no cadastro de projetos qdo cirurgia cancelada ou o projeto é limpado
	 * 
	 * @param cirurgiaSeq
	 * @param pjq_seq
	 * @throws ApplicationBusinessException
	 */
	public void atualizarCotaProcedimetosCirurgiaCallableStatement(final Integer cirurgiaSeq, final Integer pjq_seq) throws ApplicationBusinessException {
		if (isOracle()) {

			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MBCK_CRG_RN2_RN_CRGP_ATU_COTA_PJQ;
			try {
				this.doWork(new Work() {
					public void execute(Connection connection) throws SQLException {
						CallableStatement cs = null;
						try {
							cs = connection.prepareCall("{call " + nomeObjeto + "(?,?)}");

							CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, cirurgiaSeq == null ? null : cirurgiaSeq);
							CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, pjq_seq == null ? null : pjq_seq);

							// Registro de parametro OUT
							// cs.registerOutParameter(1, Types.VARCHAR);
							cs.execute();
							// String retorno = cs.getString(1);

						} finally {
							if (cs != null) {
								cs.close();
							}
						}
					}
				});
			} catch (Exception e) {
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(cirurgiaSeq == null ? null : cirurgiaSeq, pjq_seq == null ? null : pjq_seq);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Integer buscarQuantidadeCirurgiasComDetProc(Integer agdSeq, Short unfSeq, Date dtAgenda, Short gpcSeq) {
		StringBuffer hql = new StringBuffer(300);

		hql.append(" select ppc.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ);
		hql.append(" from ").append(MbcProcEspPorCirurgias.class.getSimpleName()).append(" ppc ");
		hql.append(" 	inner join ppc.").append(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString()).append(" crg ");
		hql.append(" 	inner join crg.").append(MbcCirurgias.Fields.AGENDA.toString()).append(" agd, ");
		hql.append(MbcProcedimentoPorGrupo.class.getName()).append(" pgr ");
		hql.append(" where ppc.").append(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()).append(" = pgr.").append(MbcProcedimentoPorGrupo.Fields.PCI_SEQ);
		hql.append("    and pgr.").append(MbcProcedimentoPorGrupo.Fields.GPC_SEQ).append(" = :gpcSeq ");
		hql.append(" 	and agd.").append(MbcAgendas.Fields.SEQ.toString()).append(" <> :agdSeq ");
		hql.append(" 	and agd.").append(MbcAgendas.Fields.UNF_SEQ.toString()).append(" = :unfSeq ");
		hql.append(" 	and agd.").append(MbcAgendas.Fields.DT_AGENDA.toString()).append(" = :dtAgenda ");
		hql.append(" 	and agd.").append(MbcAgendas.Fields.DTHR_PREV_INICIO.toString()).append(" is not null ");
		hql.append(" 	and agd.").append(MbcAgendas.Fields.IND_SITUACAO.toString()).append(" = :indSituacao ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("gpcSeq", gpcSeq);
		query.setParameter("agdSeq", agdSeq);
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("dtAgenda", dtAgenda);
		query.setParameter("indSituacao", DominioSituacaoAgendas.ES);

		List<Object> list = query.list();
		return list.size();
	}

	public List<MbcCirurgiaVO> listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(Integer ppcCrgSeq, Integer phiSeq, Boolean isItemCirg) {
		final StringBuilder hql = new StringBuilder(400);

		hql.append("SELECT ").append(" CRG.").append(MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()).append(" as dataFimCirg ").append(",PPC.")
		.append(MbcProcEspPorCirurgias.Fields.QTD.toString()).append(" as quantidade ").append(", PHI.").append(FatProcedHospInternos.Fields.SEQ.toString())
		.append(" as phiSeq ").append(", CRG.").append(MbcCirurgias.Fields.PAC_CODIGO.toString()).append(" as pacCodigo ").append(", CRG.")
		.append(MbcCirurgias.Fields.UNF_SEQ.toString()).append(" as unfSeq ").append(",PPC.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString())
		.append(" as crgSeq ").append(",PPC.").append(MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString()).append(" as eprEspSeq ").append(",PPC.")
		.append(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()).append(" as eprPciSeq ").append(",PPC.")
		.append(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()).append(" as indRespProc ").append(" ,CRG.").append(MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString())
		.append(" as atdSeq ")

		.append(" FROM ").append(FatProcedHospInternos.class.getSimpleName()).append(" PHI, ").append(MbcProcEspPorCirurgias.class.getSimpleName()).append(" PPC, ")
		.append(MbcCirurgias.class.getSimpleName()).append(" CRG ")

		.append(" WHERE ")

		.append("CRG.").append(MbcCirurgias.Fields.SEQ.toString()).append(" = ").append("PPC.").append(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()).append(" AND ")
		.append("PPC.").append(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()).append(" = ").append("PHI.")
		.append(FatProcedHospInternos.Fields.PCI_SEQ.toString())

		.append("  AND PPC.").append(MbcProcEspPorCirurgias.Fields.SITUACAO.toString()).append(" = :situacao").append("  AND PPC.")
		.append(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()).append(" = :respProc").append("  AND CRG.").append(MbcCirurgias.Fields.SITUACAO.toString())
		.append(" = :situacaoCrg").append("  AND CRG.").append(MbcCirurgias.Fields.SEQ.toString()).append(" = :ppcPrgseq");

		if (phiSeq != null) {
			hql.append(" AND PHI.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" = :phiSeq");
		}

		if (isItemCirg) {
			hql.append(" AND PHI.").append(FatProcedHospInternos.Fields.SITUACAO.toString()).append(" = :indSituacao");
		}

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("respProc", DominioIndRespProc.NOTA);
		query.setParameter("situacaoCrg", DominioSituacaoCirurgia.RZDA);
		query.setParameter("ppcPrgseq", ppcCrgSeq);
		if (phiSeq != null) {
			query.setParameter("phiSeq", phiSeq);
		}
		if (isItemCirg) {
			query.setParameter("indSituacao", DominioSituacao.A);
		}

		query.setResultTransformer(Transformers.aliasToBean(MbcCirurgiaVO.class));

		return query.list();
	}

	/**
	 * Pesquisa MbcProcEspCirurgico em agendamento por cirurgia
	 * 
	 * @param crgSeq
	 * @return
	 */
	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspCirurgicoAgendamentoPorCirurgia(Integer crgSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);

		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg");

		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));

		return executeCriteria(criteria);

	}
	
	/**
	 * Pesquisa MbcProcEspCirurgico em agendamento por cirurgia
	 * 
	 * @param crgSeq
	 * @return
	 */
	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspCirurgicoAgendamentoPorCirurgiaAtivo(Integer crgSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);

		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg");

		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);

	}

	public List<DescricaoLateralidadeProcCirurgicoVO> listarProcedimentosComLateralidadePorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), "epr");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg");
		criteria.createAlias("crg." + MbcCirurgias.Fields.AGENDA.toString(), "agd", DetachedCriteria.LEFT_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()),
						DescricaoLateralidadeProcCirurgicoVO.Fields.PROC_SEQ.toString())
				.add(Projections.property("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()),
						DescricaoLateralidadeProcCirurgicoVO.Fields.DESCRICAO_PROC.toString())
				.add(Projections.property("agd." + MbcAgendas.Fields.LADO_CIRURGIA.toString()),
						DescricaoLateralidadeProcCirurgicoVO.Fields.LADO_CIRURGIA.toString()));
		
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.desc(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()));
		
		/*criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		criteria.getExecutableCriteria(getSession()).setMaxResults(3);*/
		
		criteria.setResultTransformer(Transformers.aliasToBean(DescricaoLateralidadeProcCirurgicoVO.class));
				
		return executeCriteria(criteria, 0, 3, null, false);
	}
	
	public List<MbcProcEspPorCirurgias> listarMbcProcEspPorCirurgiasPorCrgSeq(Integer crgSeq, Boolean somenteAtivos,
			Boolean ordenado, Boolean limitarResultados) {
		DetachedCriteria criteria = criarCriteriaListarMbcProcEspPorCirurgiasPorCrgSeq(
				crgSeq, somenteAtivos, ordenado);
		
		if (limitarResultados.equals(Boolean.TRUE)) {
		   return executeCriteria(criteria, 0, 3, null, false);
		}
		else {
		   return executeCriteria(criteria);
		}
	}
	
	public List<MbcProcEspPorCirurgias> listarMbcProcEspPorCirurgiasPorCrgSeqComOrder(Integer crgSeq, Boolean somenteAtivos,
			Boolean ordenado, Boolean limitarResultados) {
		DetachedCriteria criteria = criarCriteriaListarMbcProcEspPorCirurgiasPorCrgSeq(
				crgSeq, somenteAtivos, ordenado);
		criteria.addOrder(Order.asc("pci." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		if (limitarResultados.equals(Boolean.TRUE)) {
			return executeCriteria(criteria, 0, 3, null, false);
		}
		else {
			return executeCriteria(criteria);
		}
		
	}

	private DetachedCriteria criarCriteriaListarMbcProcEspPorCirurgiasPorCrgSeq(
			Integer crgSeq, Boolean somenteAtivos, Boolean ordenado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), "epr");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");

		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		if (somenteAtivos.equals(Boolean.TRUE)) {
			criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		if (ordenado.equals(Boolean.TRUE)) {
			criteria.addOrder(Order.desc(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()));
		}
				
		return criteria;
	}

	/**
	 * Segunda parte do cursor CUR_PPC em MBCK_PPC.RN_PPCP_VER_UTLZ_EQU
	 * 
	 * @param crgSeq
	 * @param unfSeq
	 * @param data
	 * @param listaPciSeq
	 * @return
	 */
	public List<MbcProcEspPorCirurgias> pesquisarVerificarUtilizacaoEquipamento(Integer crgSeq, Short unfSeq, Date data, List<Integer> listaPciSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");

		criteria.createAlias("ppc.".concat(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString()), "crg");
		criteria.createAlias("crg.".concat(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString()), "unf");

		criteria.add(Restrictions.in(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString(), listaPciSeq));

		criteria.add(Restrictions.ne("crg.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		criteria.add(Restrictions.eq("unf.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), unfSeq));
		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.DATA.toString()), data));
		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.SITUACAO.toString()), DominioSituacaoCirurgia.AGND));

		return executeCriteria(criteria);

	}

	public List<MbcProcEspPorCirurgias> pesquisarProcEspPorCirurgiasAtivaPrincipalPorCrgSeq(Integer crgSeq) {
		String aliasPci = "pci";
		String aliasPpc = "ppc";
		String separador = ".";

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, aliasPpc);

		criteria.createAlias(aliasPpc + separador + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPci);

		criteria.add(Restrictions.eq(aliasPpc + separador + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(aliasPpc + separador + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPpc + separador + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));

		criteria.addOrder(Order.desc(aliasPpc + separador + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Segunda parte do cursor CUR_CIRURGIAS em MBCK_PPC.RN_PPCP_VER_UTLZ_EQU
	 * 
	 * @param crgSeq
	 * @param unfSeq
	 * @param data
	 * @param listaPciSeq
	 * @return
	 */
	public List<MbcProcEspPorCirurgias> pesquisarCirurgiasAgendadasColisaoHorario(Integer crgSeq, Short unfSeq, Date data, List<Integer> listaPciSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");

		criteria.createAlias("ppc.".concat(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString()), "crg", DetachedCriteria.INNER_JOIN);
		criteria.createAlias("crg.".concat(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString()), "unf", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.DATA.toString()), data));
		criteria.add(Restrictions.eq("unf.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), unfSeq));
		criteria.add(Restrictions.ne("crg.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		criteria.add(Restrictions.isNotNull("crg.".concat(MbcCirurgias.Fields.DTHR_PREV_INICIO.toString())));
		criteria.add(Restrictions.isNotNull("crg.".concat(MbcCirurgias.Fields.DTHR_PREVISAO_FIM.toString())));
		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.SITUACAO.toString()), DominioSituacaoCirurgia.AGND));

		criteria.add(Restrictions.in(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString(), listaPciSeq));

		return executeCriteria(criteria);

	}


	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspPorCirurgias(Integer crgSeq, DominioIndRespProc indRespProc, DominioSituacao situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "PPC");

		getCriteria(crgSeq, indRespProc, situacao, criteria);

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcProcDescricoes.class, "PDF");
		subCriteria.add(Restrictions.eqProperty("PPC." + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), "PDF." + MbcProcDescricoes.Fields.DCG_CRG_SEQ.toString()));

		subCriteria.add(Restrictions.eqProperty("PPC." + MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString(), "PDF." + MbcProcDescricoes.Fields.PCI_SEQ.toString()));
		subCriteria.setProjection(Projections.property("PDF." + MbcProcDescricoes.Fields.SEQP.toString()));

		criteria.add(Subqueries.notExists(subCriteria));

		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarProcEspSemProcDiagTerapPorDdtSeqEDdtCrgSeq(Integer ddtSeq, Integer ddtCrgSeq) {
		String aliasPpc = "ppc";
		String aliasDpc = "dpc";
		String aliasDpt = "dpt";
		String ponto = ".";

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, aliasPpc);
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), ddtCrgSeq));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.DESC));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtProc.class, aliasDpc);
		subCriteria.createAlias(aliasDpc + ponto + PdtProc.Fields.PDT_PROC_DIAG_TERAPS, aliasDpt);
		
		subCriteria.add(Restrictions.eq(aliasDpc + ponto + PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		subCriteria.add(Restrictions.eqProperty(aliasDpt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO_SEQ.toString(), 
				aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()));
		
		subCriteria.setProjection(Projections.property(aliasDpc + ponto + PdtProc.Fields.DDT_SEQ));

		criteria.add(Subqueries.notExists(subCriteria));

		return executeCriteria(criteria);
	}
	
	public List<ProcEspPorCirurgiaVO> pesquisarProcEspCirurgiaRealizadaPorCrgSeqEIndRespProc(Integer crgSeq, DominioIndRespProc indRespProc) {
		String aliasPpc = "ppc";
		String aliasDpt = "dpt";
		String aliasPci = "pci";
		String ponto = ".";

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, aliasPpc);
		
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO, aliasPci);
		criteria.createAlias(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.PROCS_DIAGS_TERAPS, aliasDpt);
		
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		Projection proj = 
				Projections.projectionList().add(Projections.property(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()), ProcEspPorCirurgiaVO.Fields.CRG_SEQ.toString())
											.add(Projections.property(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString()), ProcEspPorCirurgiaVO.Fields.PCI_SEQ.toString())
											.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.IND_CONTAMINACAO.toString()), ProcEspPorCirurgiaVO.Fields.IND_CONTAMINACAO.toString())
											.add(Projections.property(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), ProcEspPorCirurgiaVO.Fields.IND_PRINCIPAL.toString())
											.add(Projections.property(aliasDpt + ponto + PdtProcDiagTerap.Fields.SEQ.toString()), ProcEspPorCirurgiaVO.Fields.DPT_SEQ.toString());
		
		criteria.setProjection(Projections.distinct(proj));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcEspPorCirurgiaVO.class));
		
		return executeCriteria(criteria);
	}

	private void getCriteria(Integer crgSeq, DominioIndRespProc indRespProc,
			DominioSituacao situacao, final DetachedCriteria criteria) {
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));
	}

	/**
	 * Obtém MbcProcEspPorCirurgias para escala cirúrgica
	 * 
	 * @param crgSeq
	 * @param indRespProc
	 * @return
	 */
	public List<MbcProcEspPorCirurgias> getPesquisarProcedimentoCirurgicoEscalaCirurgica(Integer crgSeq, DominioIndRespProc indRespProc) {

		/*
		 * TODO Seguindo a orientação da arquitetura: Provisóriamente a assinatura do método utiliza "get". Será refatorada posteriormente!
		 * A medida foi tomada devido a exigência do commit. 
		 */
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "PPC");
		criteria.createAlias("PPC.".concat(MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString()), "EPR", JoinType.INNER_JOIN);
		criteria.createAlias("PPC.".concat(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString()), "PCI", JoinType.INNER_JOIN);
		criteria.createAlias("PPC.".concat(MbcProcEspPorCirurgias.Fields.CIRURGIA.toString()), "CIR", JoinType.INNER_JOIN);
		criteria.createAlias("EPR.".concat(MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString()), "ESP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()), indRespProc));
		criteria.add(Restrictions.eq("PCI.".concat(MbcProcedimentoCirurgicos.Fields.SITUACAO.toString()), DominioSituacao.A));
		criteria.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()), crgSeq));
		criteria.addOrder(Order.desc(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()));
		return executeCriteria(criteria);
	}

	// # 24929 C3
	public List<MbcProcEspPorCirurgias> listarMbcProcEspPorCirurgiasPorDataCrgEUnidadeCrg(Short seqUnidadeCirurgica, Short seqUnidadeInternacao, Date dataCirurgia, Integer prontuario) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), "epr");
		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "crg");
		criteria.createAlias("crg." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.createAlias("crg." + MbcCirurgias.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("pac." + AipPacientes.Fields.ATENDIMENTOS.toString(), "atd");
		criteria.createAlias("epr." + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "pci");
		
		if (dataCirurgia != null) {
			criteria.add(Restrictions.eq("crg." + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		}	
		if (seqUnidadeCirurgica != null) {
			criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidadeCirurgica));
		}
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if (prontuario != null) {
			criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		criteria.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ne("crg." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		
		if (seqUnidadeInternacao != null) {
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.UNF_SEQ.toString(), seqUnidadeInternacao));
		}
		
		criteria.addOrder(Order.asc(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()));
		//criteria.addOrder(Order.asc(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()));
		criteria.addOrder(Order.asc(MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString()));
		//criteria.addOrder(Order.asc(MbcProcEspPorCirurgias.Fields.ID_EPR_ESP_SEQ.toString()));

		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspPorCirurgiasCadastrados(Integer crgSeq, DominioIndRespProc indRespProc) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "PPC");
		getCriteria(crgSeq, indRespProc, DominioSituacao.A, criteria);
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
	}
	
	public Integer obterMbcProcEspPorCirurgiaPorCrgSeq(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "PPC");
		criteria.setProjection(Projections.property("PPC." + MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString()));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("PPC." + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		
		final List<Integer> retorno = executeCriteria(criteria);
		if (retorno != null && !retorno.isEmpty()) {
			return retorno.get(retorno.size()-1);
		}
		return null;
	}
	
	/* CURSOR c_cirurgia(c_pac_codigo  NUMBER, c_unf_seq  NUMBER, c_pci NUMBER, c_data date) IS
	 * @param atdSeq
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MbcProcEspPorCirurgias> obterProcedimentoEspVinculaFicha(MbcCirurgias cirurgia, Integer pciSeq) throws ApplicationBusinessException {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");

		criteria.createAlias("ppc." + MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "CRG");
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.PAC_CODIGO.toString(), cirurgia.getPaciente().getCodigo()));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.UNF_SEQ.toString(), cirurgia.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.ne("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), DateUtil.adicionaDias(cirurgia.getData(), -1), DateUtil.adicionaDias(cirurgia.getData(), 1)));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), cirurgia.getSeq()));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_EPR_PCI_SEQ.toString(), pciSeq));
		
		criteria.addOrder(Order.desc(MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString()));

		
		return executeCriteria(criteria);
	}
	
	// #22464
	public Boolean obterTipagemSanguineaSolicitada(Integer crgSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		
		criteria.setProjection(Projections.property("pci.".concat(MbcProcedimentoCirurgicos.Fields.IND_TIPAGEM_SANGUE.toString())));
		
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		
		return (Boolean) executeCriteriaUniqueResult(criteria);
		
		
	}
	
	public List<SubRelatorioRegistroDaNotaDeSalaProcedimentosVO> pesquisarProcedimentosAtivosPorCirurgia(final Integer crgSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pci."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), 
						SubRelatorioRegistroDaNotaDeSalaProcedimentosVO.Fields.PROCEDIMENTO_DESCRICAO.toString()));
		
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioRegistroDaNotaDeSalaProcedimentosVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarProcedimentosAgendadosPorCirurgia(Integer crgSeq, DominioIndRespProc indRespProc){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "A");
		criteria.createAlias("A."+MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "PRC");
		criteria.createAlias("A."+MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), "MESP");
		criteria.createAlias("MESP."+MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.add(Restrictions.eq("A."+MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("A."+MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));
		return executeCriteria(criteria);
	}
	
	public List<ProcedimentoCirurgicoPacienteVO> buscarCirurgiasDoPacienteDuranteUmaInternacao(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, "crg");
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("crg." + MbcCirurgias.Fields.SEQ.toString()), ProcedimentoCirurgicoPacienteVO.Fields.CRG_SEQ.toString())
			.add(Projections.property("ppc." + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString()), ProcedimentoCirurgicoPacienteVO.Fields.PCI_SEQ.toString())
			.add(Projections.property("ppc." + MbcProcEspPorCirurgias.Fields.PHI_SEQ.toString()), ProcedimentoCirurgicoPacienteVO.Fields.PHI_SEQ.toString()) //Alterado "and ppc.epr_pci_seq = phi.pci_seq" para o phi direto da cirurgia, já que estava retornando mais de um phi para o mesmo pci_seq
			.add(Projections.property("ppc." + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), ProcedimentoCirurgicoPacienteVO.Fields.IND_PRINCIPAL.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_ENTRADA_SALA.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_ENTRADA_SALA.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_SAIDA_SALA.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_SAIDA_SALA.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DATA_INICIO_ANESTESIA.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_INICIO_ANESTESIA.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DATA_FIM_ANESTESIA.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_FIM_ANESTESIA.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_INICIO_CIRURGIA.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_FIM_CIRG.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_FIM_CIRURGIA.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_ENTRADA_SR.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_ENTRADA_SR.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.DTHR_SAIDA_SR.toString()), ProcedimentoCirurgicoPacienteVO.Fields.DATA_SAIDA_SR.toString())
			.add(Projections.property("crg." + MbcCirurgias.Fields.ESP_SEQ.toString()), ProcedimentoCirurgicoPacienteVO.Fields.ESP_SEQ.toString())
			.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString()), ProcedimentoCirurgicoPacienteVO.Fields.CCT_CODIGO.toString())
			.add(Projections.property("pcg." + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()), ProcedimentoCirurgicoPacienteVO.Fields.PUC_SER_VIN_CODIGO.toString())
			.add(Projections.property("pcg." + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()), ProcedimentoCirurgicoPacienteVO.Fields.PUC_SER_MATRICULA.toString())
		);
		criteria.createAlias("crg."+MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), "ppc", JoinType.INNER_JOIN);
		criteria.createAlias("crg."+MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);
		criteria.createAlias("crg."+MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), "pcg", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("crg" + "." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq("crg" + "." + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.between("crg."+MbcCirurgias.Fields.DATA.toString(), dtInicioProcessamento, dtFimProcessamento));
		criteria.add(Restrictions.eq("crg" + "." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ppc."+MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq("ppc."+MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));		
		criteria.add(Restrictions.eq("pcg."+MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		criteria.addOrder(Order.asc("crg."+MbcCirurgias.Fields.SEQ.toString()));
		criteria.addOrder(Order.desc("ppc."+MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoCirurgicoPacienteVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarProcedimentosAtivosPorCirurgiaResposta(final Integer crgSeq, final DominioIndRespProc respProc){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), respProc));
		return executeCriteria(criteria);
	}

	public List<MbcProcEspPorCirurgias> pesquisarProcedimentosEspecialidadePrincipalCirurgia(final Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarCidPorPhiEscolhido(final Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		return executeCriteria(criteria);
	}

	public List<MbcProcEspPorCirurgias> pesquisarProcedimentosPartoCesarea(final Integer crgSeq, final Integer partoEprPciSeq, final Integer cesareaEprPciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString(), new Integer[] { partoEprPciSeq, cesareaEprPciSeq }));
		return executeCriteria(criteria);
	}

	public List<MbcProcEspPorCirurgias> pesquisarProcedimentosNotaAtivosPorCirurgiaProcedimento(final Integer crgSeq, Integer seqProcedimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), "pci");
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(seqProcedimento != null) {
			criteria.add(Restrictions.eq("pci."+MbcProcedimentoCirurgicos.Fields.SEQ.toString(), seqProcedimento));
		}
		criteria.addOrder(Order.desc(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarPorMbcCirurgiaSituacaoIndResponsavel(final MbcCirurgias crgSeq, DominioSituacao situacao, DominioIndRespProc indResponsavel) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		if(crgSeq != null && crgSeq.getSeq() != null){
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq.getSeq()));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), situacao));
		}
		
		if(indResponsavel != null){
			criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indResponsavel));
		}
		
		return executeCriteria(criteria);
	}

	public List<RelatorioTransplantesRealizTMOOutrosVO> pesquisarTransplantesPorDtInicioDtFimEListaProcedTransplante(
			Date dtInicio, Date dtFim, List<Integer> listaProcedTransplante) {
		
		String aliasPpc = "ppc";
		String aliasPci = "pci";
		String aliasCrg = "crg";
		String aliasPcg = "pcg";
		String aliasPac = "pac";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, aliasPpc);
		Projection p = Projections.projectionList()
				.add(Projections.distinct(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString())), RelatorioTransplantesRealizTMOOutrosVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.NOME.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.DATA_NASCIMENTO.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.DT_NASCIMENTO.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.DT_TRANSPLANTE.toString())
				.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.PROCEDIMENTO.toString())
				.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.EQUIPE.toString());
		criteria.setProjection(p);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString(), aliasPci);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), aliasCrg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPcg);
		criteria.createAlias(aliasPcg + ponto + MbcProfCirurgias.Fields.SERVIDOR_PUC.toString(), aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dtInicio, dtFim));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.RZDA));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.IND_DIGT_NOTA_SALA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), listaProcedTransplante));
		criteria.add(Restrictions.eq(aliasPcg + ponto + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		
		criteria.addOrder(Order.asc(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioTransplantesRealizTMOOutrosVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisarProcEspPorCrgSeq(Integer crgSeq) {
		String aliasPpc = "ppc";
		String aliasPci = "pci";
		String aliasEpr = "epr";
		String ponto = ".";

		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, aliasPpc);
		criteria.createAlias(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), aliasEpr);
		criteria.createAlias(aliasEpr + ponto + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), aliasPci);
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.AGND));
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		return executeCriteria(criteria);
	}
	
	public List<CursorCirurgiaSusProceHospitalarInternoVO> pesquisarCirurgiaSusProcedimentoHospitalarInterno(
			final Integer pCrgSeq, 
			final Byte pCspSeq, 
			final Short pCnvCodigo,
			final DominioSituacao dominioSituacao,
			final DominioIndRespProc dominioIndRespProc,
			final Short pTipoGrupoContaSus) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, ALIAS_PPC);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PPC + PONTO + MbcProcEspPorCirurgias.Fields.PHI_SEQ.toString()), 
						CursorCirurgiaSusProceHospitalarInternoVO.Fields.PHI_SEQ.toString()));
		criteria.createAlias(ALIAS_PPC + PONTO + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_HOSP_INTERNO.toString(), ALIAS_PHI); // FatProcedHospInternos
		criteria.createAlias(ALIAS_PHI + PONTO + FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString(), ALIAS_CGI); // FatConvGrupoItemProced
		criteria.createAlias(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), ALIAS_IPH); // FatItensProcedHospitalar
		criteria.add(Restrictions.eq(ALIAS_PPC + PONTO + MbcProcEspPorCirurgias.Fields.CRG_SEQ.toString(), pCrgSeq));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), pCspSeq));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), pCnvCodigo));
		criteria.add(Restrictions.eq(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), dominioSituacao));
		criteria.add(Restrictions.eq(ALIAS_PHI + PONTO + FatProcedHospInternos.Fields.SITUACAO.toString(), dominioSituacao));
		criteria.add(Restrictions.eq(ALIAS_PPC + PONTO + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), dominioSituacao));
		criteria.add(Restrictions.eq(ALIAS_PPC + PONTO + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), dominioIndRespProc));
		criteria.add(Restrictions.isNull(ALIAS_PPC + PONTO + MbcProcEspPorCirurgias.Fields.PHI_SEQ.toString()));
		criteria.add(Restrictions.eq(ALIAS_CGI + PONTO + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), pTipoGrupoContaSus));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorCirurgiaSusProceHospitalarInternoVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MbcProcEspPorCirurgias> pesquisaProcedimentoCompleto(Integer seq, String stringSeparator, DominioIndRespProc indRespProc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class);
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString(), MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString());
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.CIRURGIA2.toString(), "cirurgia");
		criteria.createAlias(MbcProcEspPorCirurgias.Fields.ESPECIALIDADE.toString() + stringSeparator + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS, "pc");
		criteria.createAlias("cirurgia" + stringSeparator + MbcCirurgias.Fields.AGENDA.toString(), "agenda", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.CIRURGIA.toString() + stringSeparator + MbcCirurgias.Fields.SEQ, seq));
		criteria.add(Restrictions.eq(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), indRespProc));
		return executeCriteria(criteria);
	}

	
}