package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSolicitacaoHemoterapicaPendente;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.faturamento.vo.BuscarJustificativasLaudoProcedimentoSusVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedHospInternosVO;
import br.gov.mec.aghu.faturamento.vo.ContasNPTVO;
import br.gov.mec.aghu.faturamento.vo.NutricaoEnteralDigitadaVO;
import br.gov.mec.aghu.faturamento.vo.ProcedHospInternosTriagemClinicaVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FatProcedHospInternosPai.Fields;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MptProcedimentoInternacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoCirurgiaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;

@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class FatProcedHospInternosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedHospInternos> {
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = 1264687248845290362L;

	/**
	 * Busca um FatProcedHospInternos conforme o atributo informado em
	 * MpmPrescricaoProcedimento<br>
	 * ORADB package MPMK_RN.RN_MPMP_VER_PROC_HOS<br>
	 * 
	 * @return um <code>MpmPrescricaoProcedimento</code>
	 */
	public List<FatProcedHospInternos> buscaProcedimentoHospitalarInterno(
			Integer matCodigo, Integer pciSeq, Short pedSeq) {
		if (matCodigo == null && pciSeq == null && pedSeq == null) {
			throw new IllegalArgumentException(
					"Parametro Invalido: Pelo menos um dos parametros deve ser informado!");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedHospInternos.class);

		if (matCodigo != null) {
			criteria.add(Restrictions.eq(
					FatProcedHospInternos.Fields.MAT_CODIGO.toString(),
					matCodigo));
		}
		if (pciSeq != null) {
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PCI_SEQ
					.toString(), pciSeq));
		}
		if (pedSeq != null) {
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ
					.toString(), pedSeq));
		}

		return this.executeCriteria(criteria);
	}

	/**
	 * Pesquisa FatProcedHospInternos por material
	 * 
	 * @param material
	 * @return
	 */
	public  List<FatProcedHospInternos> listarFatProcedHospInternosPorMaterial(
			ScoMaterial material) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.MAT_CODIGO
				.toString(), material.getCodigo()));

		return this.executeCriteria(criteria);

	}
	
	/**
	 * Retorna uma nova referência ao registro informado por parâmetro com os
	 * valores atuais do banco, utilizando o comando evict para não buscar do
	 * cache.
	 * 
	 * @param {FatProcedHospInternos} fatProcedHospInternos
	 * @return
	 */
	public FatProcedHospInternos obterFatProcedHospInternos(FatProcedHospInternos fatProcedHospInternos) {
		this.desatachar(fatProcedHospInternos);
		return obterPorChavePrimaria(fatProcedHospInternos.getSeq());
	}
	
	/**
	 * Executa as querys, nas entidades:<br>
	 * <b>mpm_tipo_laudos</b>, <b>fat_proced_hosp_internos</b>.<br>
	 * Retorna uma lista NAO nula e NAO vazia, 
	 * caso alguma query encontre entidades.<br>
	 * Se alguma query encontrar entidades associadas,
	 * jah retorna esta entidades e NAO executa as outra querys.<br>
	 * As entidades da lista de retorno NAO podem ser utilizados.
	 * 
	 * @param atendimento
	 * @param parametos
	 * @return
	 */
	public List<FatProcedHospInternos> buscaProcedimentosComLaudoJustificativaParaImpressao(AghAtendimentos atendimento) {
		List<FatProcedHospInternos> list;
		javax.persistence.Query query;
		StringBuilder hql;
		
		hql = new StringBuilder();
		hql.append("select lau." + MpmLaudo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO + " ");
		hql.append("from " + MpmLaudo.class.getSimpleName() + " lau ");
		hql.append("where lau." + MpmLaudo.Fields.TIPO_LAUDO + " is null ");
		hql.append("and lau." + MpmLaudo.Fields.ATENDIMENTO + " = :pAtendimento ");
		
		query = createQuery(hql.toString());
		query.setParameter("pAtendimento", atendimento);
		list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			return list;
		}
		
		hql = new StringBuilder();
		hql.append("select phi ");
		hql.append("from " + AbsSolicitacoesHemoterapicas.class.getSimpleName() + " she ");
		hql.append("inner join she." + AbsSolicitacoesHemoterapicas.Fields.ITENS_SOLICITACOES_HEMOTERAPICAS + " ish ");
		hql.append(", " + FatProcedHospInternos.class.getSimpleName() + " phi ");
		hql.append("where ish." + AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS_CODIGO + " = phi.csaCodigo "); 
		hql.append("and she." + AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ + " = :pAtendimento ");
		hql.append("and she." + AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE + " = 'N' ");

		query = createQuery(hql.toString());
		query.setParameter("pAtendimento", atendimento.getSeq());
		list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			return list;
		}
		
		hql = new StringBuilder();
		hql.append("select phi  ");
		hql.append("from " + AbsSolicitacoesHemoterapicas.class.getSimpleName() + " she "); 
		hql.append("inner join she.itensSolHemoterapicas ish  ");
		hql.append(", " + FatProcedHospInternos.class.getSimpleName() + " phi  ");
		hql.append("where ish." + AbsItensSolHemoterapicas.Fields.ABS_PROCED_HEMOTERAPICOS_CODIGO + " = phi.pheCodigo ");
		hql.append("and she." + AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ + " = :pAtendimento ");
		hql.append("and she." + AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE + " = 'N'  ");

		query = createQuery(hql.toString());
		query.setParameter("pAtendimento", atendimento.getSeq());
		list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			return list;
		}
		
		hql = new StringBuilder();
		hql.append("select lau ");
		hql.append("from " + MpmLaudo.class.getSimpleName() + " lau ");
		hql.append("inner join lau." + MpmLaudo.Fields.TIPO_LAUDO + " ");
		hql.append("where lau." + MpmLaudo.Fields.ATENDIMENTO + " = :pAtendimento ");
		hql.append("and lau." + MpmLaudo.Fields.TIPO_LAUDO + " is not null ");
		
		query = createQuery(hql.toString());
		query.setParameter("pAtendimento", atendimento);
		List<MpmLaudo> listLaudo = query.getResultList();
		if (listLaudo != null && !listLaudo.isEmpty()) {
			List<FatProcedHospInternos> listAdd = new LinkedList<FatProcedHospInternos>();
			listAdd.add(new FatProcedHospInternos());
			return listAdd;
		}
		
		return null;
	}
	
	

	/**
	 * Pesquisa FatProcedHospInternos por procedimento cirurgico.
	 * 
	 * @param material
	 * @return
	 */
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedimentoCirurgicos(Integer pciSeq) {
		DetachedCriteria criteria = obterCriteriaListarFatProcedHospInternosPorProced(pciSeq);

		return this.executeCriteria(criteria);
	}

	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedComFiltro(Integer pciSeq) {
		DetachedCriteria criteria = obterCriteriaListarFatProcedHospInternosPorProced(pciSeq);
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaListarFatProcedHospInternosPorProced(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PCI_SEQ.toString(), pciSeq));
		return criteria;
	}
	
	/**
	 * Pesquisa FatProcedHospInternos por procedimento especial diverso.
	 * 
	 * @param material
	 * @return
	 */
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedEspecialDiversos(
			MpmProcedEspecialDiversos procedimentoEspecialDiverso) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ
				.toString(), procedimentoEspecialDiverso.getSeq()));

		return this.executeCriteria(criteria);

	}
	
	public List<FatProcedHospInternos> pesquisarPorProcedimentoEspecialDiverso(
			MpmProcedEspecialDiversos procedimentoEspecial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(
				FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ
						.toString(), procedimentoEspecial.getSeq()));
		return this.executeCriteria(criteria);
	}

	public List<FatProcedHospInternos> pesquisarFatProcedHospInternos(
			Integer codigoMaterial, Integer seqProcedimentoCirurgico,
			Short seqProcedimentoEspecialDiverso) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedHospInternos.class);

		if (codigoMaterial != null) {
			DetachedCriteria criteriaMaterial = criteria
					.createCriteria(FatProcedHospInternos.Fields.MATERIAIS
							.toString());
			criteriaMaterial.add(Restrictions.eq(ScoMaterial.Fields.CODIGO
					.toString(), codigoMaterial));
		}
		if (seqProcedimentoCirurgico != null) {
			DetachedCriteria criteriaProcCirurgico = criteria
					.createCriteria(FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO
							.toString());
			criteriaProcCirurgico.add(Restrictions.eq(
					MbcProcedimentoCirurgicos.Fields.SEQ.toString(),
					seqProcedimentoCirurgico));
		}
		if (seqProcedimentoEspecialDiverso != null) {
			DetachedCriteria criteriaProcEspecialDiverso = criteria
					.createCriteria(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO
							.toString());
			criteriaProcEspecialDiverso.add(Restrictions.eq(
					MpmProcedEspecialDiversos.Fields.SEQ.toString(),
					seqProcedimentoEspecialDiverso));
		}

		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<BuscarJustificativasLaudoProcedimentoSusVO> buscarJustificativasLaudoProcedimentoSUSSolicitacaoExame(
			Integer seqAtendimento,
			String situacaoLiberadoItensSolicitacaoExames,
			String situacaoNaAreaExecutoraItensSolicitacaoExames,
			String situacaoExecutandoItensSolicitacaoExames, Short tipoGrupoContaSUS) {
		StringBuffer hql = new StringBuffer(700);
		hql
				.append(" select distinct new br.gov.mec.aghu.faturamento.vo.BuscarJustificativasLaudoProcedimentoSusVO( ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(", ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(", ");
		hql.append(" 	soe.");
		hql.append(AelSolicitacaoExames.Fields.ATD_SEQ.toString());
		hql.append(", ");
		hql.append(" 	soe.");
		hql.append(AelSolicitacaoExames.Fields.SEQ.toString());
		hql.append(" ) ");
		hql.append(" from FatProcedHospInternos phi, ");
		hql.append(" 	AelItemSolicitacaoExames ise ");
		hql.append(" 		join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString());
		hql.append(" as soe ");
		hql.append(" where phi.");
		hql.append(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString());
		hql.append(" = ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" 	and phi.");
		hql.append(FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString());
		hql.append(" = ise.");
		hql.append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" 	and soe.");
		hql.append(AelSolicitacaoExames.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql
				.append(" 	and substr(ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString());
		hql.append(",1,2) in (:situacaoLiberadoItensSolicitacaoExames, ");
		hql
				.append(" 		:situacaoNaAreaExecutoraItensSolicitacaoExames, :situacaoExecutandoItensSolicitacaoExames) ");
		hql.append(" 	and exists(");
		hql.append(" select cgi. ");
		hql.append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString());
		hql.append(" from ");
		hql.append(FatConvGrupoItemProced.class.getSimpleName());
		hql.append(" as cgi ");
		hql.append(" join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO);
		hql.append(" as phi1 ");
		hql.append(" join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR);
		hql.append(" as iph ");
     	hql.append(" where ");
     	hql.append(" iph.");
     	hql.append(FatItensProcedHospitalar.Fields.PROCEDIMENTO_ESPECIAL);
     	hql.append(" = :procedimentoEspecial ");
     	hql.append(" and cgi.");
     	hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
     	hql.append(" = :cpgCphCspCnvCodigo ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cpgCphCspSeq ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString());
		hql.append(" >= :cpgCphPhoSeq ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSUS ");
		hql.append(" and phi1.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" )");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("situacaoLiberadoItensSolicitacaoExames",
				situacaoLiberadoItensSolicitacaoExames);
		query.setParameter("situacaoNaAreaExecutoraItensSolicitacaoExames",
				situacaoNaAreaExecutoraItensSolicitacaoExames);
		query.setParameter("situacaoExecutandoItensSolicitacaoExames",
				situacaoExecutandoItensSolicitacaoExames);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		query.setParameter("procedimentoEspecial", true);
		query.setParameter("cpgCphCspCnvCodigo", (short) 1);
		query.setParameter("cpgCphCspSeq", (byte) 1);
		query.setParameter("cpgCphPhoSeq", (short) 0);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<BuscarJustificativasLaudoProcedimentoSusVO> buscarJustificativasLaudoProcedimentoSUSComponentesSanguineos(
			Integer seqAtendimento) {
		StringBuffer hql = new StringBuffer(330);
		hql
				.append(" select distinct new br.gov.mec.aghu.faturamento.vo.BuscarJustificativasLaudoProcedimentoSusVO( ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(", ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(", ");
		hql.append(" 	ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString());
		hql.append(" ) ");
		hql.append(" from FatProcedHospInternos phi, ");
		hql.append(" 	AbsItensSolHemoterapicas ish, ");
		hql.append(" 	AbsSolicitacoesHemoterapicas she ");
		hql.append(" where she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString());
		hql.append(" = :indPendente ");
		hql
				.append(" 	and phi.");
		hql.append(FatProcedHospInternos.Fields.CSA_CODIGO.toString());
		hql.append(" = ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS_CODIGO.toString());
		hql.append(" 	and ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString());
		hql.append("");
		hql.append(" = she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString());
		hql.append(" 	and ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString());
		hql.append(" = she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.SEQ.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("indPendente",
				DominioSolicitacaoHemoterapicaPendente.N);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscarJustificativasLaudoProcedimentoSusVO> buscarJustificativasLaudoProcedimentoSUSProcedHemoterapicos(
			Integer seqAtendimento) {
		StringBuffer hql = new StringBuffer(330);
		hql
				.append(" select distinct new br.gov.mec.aghu.faturamento.vo.BuscarJustificativasLaudoProcedimentoSusVO( ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(", ");
		hql.append(" 	phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(", ");
		hql.append(" 	ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString());
		hql.append(" ) ");
		hql.append(" from FatProcedHospInternos phi, ");
		hql.append(" 	AbsItensSolHemoterapicas ish, ");
		hql.append(" 	AbsSolicitacoesHemoterapicas she ");
		hql.append(" where she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString());
		hql.append(" = :seqAtendimento ");
		hql.append(" 	and she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString());
		hql.append(" = :indPendente ");
		hql
				.append(" 	and phi.");
		hql.append(FatProcedHospInternos.Fields.PHE_CODIGO.toString());
		hql.append(" = ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.ABS_PROCED_HEMOTERAPICOS_CODIGO.toString());
		hql.append(" 	and ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString());
		hql.append(" = she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString());
		hql.append(" 	and ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString());
		hql.append(" = she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.SEQ.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqAtendimento", seqAtendimento);
		query.setParameter("indPendente",
				DominioSolicitacaoHemoterapicaPendente.N);

		return query.list();
	}

	public FatProcedHospInternos obterPorCuidadoUsual(Integer seqCuidado) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.CDU_SEQ
				.toString(), seqCuidado));

		return (FatProcedHospInternos) this.executeCriteriaUniqueResult(criteria);
	}

	public FatProcedHospInternos pesquisarPorChaveGenericaFatProcedHospInternos(
			Object seq, Fields field) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(field.toString(), seq));
		List<FatProcedHospInternos> fatProcedHospInternos = this.executeCriteria(criteria);
		//return fatProcedHospInternos.get(0);
		return fatProcedHospInternos != null && !fatProcedHospInternos.isEmpty() ? fatProcedHospInternos.get(0) : null;
	}
	
	public List<FatProcedHospInternos> pesquisarListaPorChaveGenericaFatProcedHospInternos(
			Object seq, Fields field) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(field.toString(), seq));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma nova referência ao registro informado por parâmetro com os
	 * valores atuais do banco por emaManSeq e emaExaSigla
	 * 
	 * @param {FatProcedHospInternos} fatProcedHospInternos
	 * @return
	 */
	public void removerFatProcedHospInternosPorMaterial(AelExamesMaterialAnaliseId exaManId){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString(), exaManId.getExaSigla()));
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString(), exaManId.getManSeq()));
		FatProcedHospInternos fatProcedHospInternos = (FatProcedHospInternos) this.executeCriteriaUniqueResult(criteria);
		
		if(fatProcedHospInternos != null){
			this.remover(fatProcedHospInternos);
			this.flush();
		}
	}
	
	public FatProcedHospInternos obterFatProcedHospInternosPorMaterial(AelExamesMaterialAnaliseId exaManId){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString(), exaManId.getExaSigla()));
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString(), exaManId.getManSeq()));
		List<FatProcedHospInternos> lista = this.executeCriteria(criteria);
		
		return lista != null && !lista.isEmpty() ? lista.get(0) : null;
	}
	
	public void removerFatProcedHospInternos(Object seq, FatProcedHospInternos.Fields field, Boolean flush) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(field.toString(), seq));
		
		List<FatProcedHospInternos> listFatProcedHospInternos = executeCriteria(criteria);
		
		for (FatProcedHospInternos fatProcedHospInternos : listFatProcedHospInternos) {
			if(fatProcedHospInternos != null){
				remover(fatProcedHospInternos);
				if(flush){
					this.flush();
				}
			}
		}
	}

	/**
	 * Verificar a existência de registros de tipo item de dieta em outras entidades
	 * @param elemento
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeItem(FatProcedHospInternos elemento, Class class1, Enum field) {

		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),elemento));
		
		return (executeCriteriaCount(criteria) > 0);
	}
	
	public Integer buscarPrimeiroSeqProcedHospInternoPorTipoNutricaoEnteral(DominioTipoNutricaoParenteral tipoNutricaoEnteral){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FatProcedHospInternos.Fields.SEQ.toString())));
		
		
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL.toString(), tipoNutricaoEnteral));
		
		List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * Lista de FatProcedHospInternos por um PHI_SEQ
	 * 
	 * @param phiSeq
	 * @return
	 */
	public List<FatProcedHospInternos> listarPorPhi(Integer phiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.createAlias(FatProcedHospInternos.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), FatProcedHospInternos.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString());
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString(), phiSeq));		
		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPorExaSiglaMatSeq(String exameSigla, Integer materialAnaliseSeq) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(FatProcedHospInternos.class);
		result.add(Restrictions.eq(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString(), exameSigla));
		result.add(Restrictions.eq(FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString(), materialAnaliseSeq));
		
		return result;		
	}

	public List<FatProcedHospInternos> obterListaPorExaSiglaMatSeq(String exameSigla, Integer materialAnaliseSeq) {
		
		List<FatProcedHospInternos> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorExaSiglaMatSeq(exameSigla, materialAnaliseSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
		
	private DetachedCriteria montaCriteriaListarPhisPorSeqEDescricao(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		String strPesquisa = (String) objPesquisa;
		
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatProcedHospInternos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por PHIs ativos,
	 * filtrando pela descricao ou pelo seq.
	 * @param objPesquisa
	 * @return List<FatProcedHospInternos>
	 */
	public List<FatProcedHospInternos> listarPhisAtivosPorSeqEDescricao(Object objPesquisa){
		List<FatProcedHospInternos> lista = null;
		DetachedCriteria criteria = montaCriteriaListarPhisPorSeqEDescricao(objPesquisa);
		
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString() , DominioSituacao.A));
		criteria.addOrder(Order.asc(FatProcedHospInternos.Fields.SEQ.toString()));		
		
		lista = executeCriteria(criteria, 0, 100, null, true);
		
		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por PHIs ativos,
	 * filtrando pela descricao ou pelo seq.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarPhisAtivosPorSeqEDescricaoCount(Object objPesquisa){
		DetachedCriteria criteria = montaCriteriaListarPhisPorSeqEDescricao(objPesquisa);
		
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString() , DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por PHIs,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return List<FatProcedHospInternos>
	 */
	public List<FatProcedHospInternos> listarPhis(Object objPesquisa){
		List<FatProcedHospInternos> lista = null;
		DetachedCriteria criteria = montaCriteriaListarPhisPorSeqEDescricao(objPesquisa);
		
		criteria.addOrder(Order.asc(FatProcedHospInternos.Fields.SEQ.toString()));
		
		lista = executeCriteria(criteria, 0, 100, null, true);
		
		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por PHIs,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarPhisCount(Object objPesquisa){
		DetachedCriteria criteria = montaCriteriaListarPhisPorSeqEDescricao(objPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	public List<FatProcedHospInternos> listarPhi(Integer seq, DominioSituacao situacao, Integer matCodigo, Integer pciSeq, 
			Short pedSeq, String csaCodigo, String pheCodigo, String emaExaSigla, Integer emaManSeq, Integer maxResult){
		
		DetachedCriteria  criteria = montaCriteriaListarPhis(seq, situacao, matCodigo, pciSeq, 
				pedSeq, csaCodigo, pheCodigo, emaExaSigla, emaManSeq);
		
		if(maxResult != null) {
			return executeCriteria(criteria, 0, maxResult, null);
		} else {
			return executeCriteria(criteria);
		}
	}
	

	/** Lista os Procedimentos Hospitalares Internos conforme os filtros passados por parâmetro
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param situacao
	 * @param indOrigemPresc
	 * @param tipoNutricaoEnteral
	 * @param pciSeq
	 * @param phiSeq
	 * @return
	 */
	public List<FatProcedHospInternos> listarFatProcedHospInternos(
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc,			
			DominioSituacao situacao, Boolean indOrigemPresc, DominioTipoNutricaoParenteral tipoNutricaoEnteral, Integer pciSeq, 
			Integer phiSeq, Integer phiSeqAgrupado) {
		
		DetachedCriteria criteria = montarCriteriaFatProcedHospInternos(situacao, indOrigemPresc, 
				tipoNutricaoEnteral, pciSeq, phiSeq, phiSeqAgrupado);
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/** Conta o número de registros conforme os filtros passados por parâmetro
	 * 
	 * @param situacao
	 * @param indOrigemPresc
	 * @param tipoNutricaoEnteral
	 * @param pciSeq
	 * @param phiSeq
	 * @return
	 */
	public Long listarFatProcedHospInternosCount(			
			DominioSituacao situacao, Boolean indOrigemPresc, DominioTipoNutricaoParenteral tipoNutricaoEnteral, Integer pciSeq, 
			Integer phiSeq, Integer phiSeqAgrupado) {
		
		DetachedCriteria criteria = montarCriteriaFatProcedHospInternos(situacao, indOrigemPresc, 
				tipoNutricaoEnteral, pciSeq, phiSeq, phiSeqAgrupado);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaFatProcedHospInternos(
			DominioSituacao situacao, Boolean indOrigemPresc,
			DominioTipoNutricaoParenteral tipoNutricaoEnteral, Integer pciSeq, Integer phiSeq, Integer phiSeqAgrupado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		
		criteria.createAlias(FatProcedHospInternos.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), 
				FatProcedHospInternos.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), 
				FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatProcedHospInternos.Fields.ITEM_EXAME.toString(), 
				FatProcedHospInternos.Fields.ITEM_EXAME.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatProcedHospInternos.Fields.ITEM_MEDICACAO.toString(), 
				FatProcedHospInternos.Fields.ITEM_MEDICACAO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		if (phiSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));
		}
		if (situacao != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString(), situacao));
		}
		if (indOrigemPresc != false){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.IND_ORIGEM_PRESC.toString(), indOrigemPresc));
		}
		if (tipoNutricaoEnteral != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.TIPO_NUTRICAO_ENTERAL.toString(), tipoNutricaoEnteral));
		}
		if (pciSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PCI_SEQ.toString(), pciSeq));
		}
		if (phiSeqAgrupado != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PHI_SEQ.toString(), phiSeqAgrupado));
		}
		
		return criteria;
	}

	private DetachedCriteria montaCriteriaListarPhis(Integer seq, DominioSituacao situacao, Integer matCodigo, Integer pciSeq, 
			Short pedSeq, String csaCodigo, String pheCodigo, String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.EXAME_MATERIAL_ANALISE.toString(),
				FatProcedHospInternos.Fields.EXAME_MATERIAL_ANALISE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.EXAME_MATERIAL_ANALISE.toString()
						+ "."
						+ AelExamesMaterialAnalise.Fields.EXAME.toString(),
				AelExamesMaterialAnalise.Fields.EXAME.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.EXAME_MATERIAL_ANALISE.toString()
						+ "."
						+ AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(),
				AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.PROCED_HEMOTERAPICO.toString(),
				FatProcedHospInternos.Fields.PROCED_HEMOTERAPICO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.COMPONENTE_SANGUINEO.toString(),
				FatProcedHospInternos.Fields.COMPONENTE_SANGUINEO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO.toString(),
				FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(),
				FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				FatProcedHospInternos.Fields.MATERIAIS.toString(),
				FatProcedHospInternos.Fields.MATERIAIS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		if(seq != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), seq));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString(), situacao));
		}
		
		if(matCodigo != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.MATERIAIS.toString() + "."+ ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		}
		if(pciSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString()+ "."+ MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq));
		}
		if(pedSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO.toString()+ "."+ MpmProcedEspecialDiversos.Fields.SEQ.toString(), pedSeq));
		}
		if(csaCodigo != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.COMPONENTE_SANGUINEO.toString()+ "."+ AbsComponenteSanguineo.Fields.CODIGO.toString(), csaCodigo));
		}
		if(pheCodigo != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PROCED_HEMOTERAPICO.toString()+ "."+ AbsProcedHemoterapico.Fields.CODIGO.toString(), pheCodigo));
		}
		if(emaExaSigla != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.EXAME_MATERIAL_ANALISE.toString()+ "."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), emaExaSigla));
		}
		if(emaManSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.EXAME_MATERIAL_ANALISE.toString()+ "."+ AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), emaManSeq));
		}

		return criteria;
	}
	
	
	/**
	 * Metodo para listar PHIs filtrando por: 
	 * seq,
	 * ativo/inativo (IND_SITUACAO), 
	 * material (MAT_CODIGO), 
	 * proc. cirúrgico (PCI_SEQ),
	 * proc. especial (PED_SEQ),
	 * comp. sanguíneo (CSA_CODIGO),
	 * proc. hemoterápico (PHE_CODIGO),
	 * sigla exame (EMA_EXA_SIGLA),
	 * mat. análise (EMA_MAN_SEQ).
	 *
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param situacao
	 * @param matCodigo
	 * @param pciSeq
	 * @param pedSeq
	 * @param csaCodigo
	 * @param pheCodigo
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return List<FatProcedHospInternos>
	 */
	public List<FatProcedHospInternos> listarPhis(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq, DominioSituacao situacao, Integer matCodigo, Integer pciSeq, 
			Short pedSeq, String csaCodigo, String pheCodigo, String emaExaSigla, Integer emaManSeq){
		
		//Montagem da consulta
		DetachedCriteria  criteria = montaCriteriaListarPhis(seq, situacao, matCodigo, pciSeq, 
				pedSeq, csaCodigo, pheCodigo, emaExaSigla, emaManSeq);
		
		//Ordenacao
		criteria.addOrder(Order.asc(FatProcedHospInternos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	/**
	 * Método auxiliar do método listarPhis, utilizado em lista paginada.
	 * @param seq
	 * @param situacao
	 * @param matCodigo
	 * @param pciSeq
	 * @param pedSeq
	 * @param csaCodigo
	 * @param pheCodigo
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return count
	 */
	public Long listarPhisCount(Integer seq, DominioSituacao situacao, Integer matCodigo, Integer pciSeq, 
			Short pedSeq, String csaCodigo, String pheCodigo, String emaExaSigla, Integer emaManSeq){
		
		DetachedCriteria  criteria = montaCriteriaListarPhis(seq, situacao, matCodigo, pciSeq, 
				pedSeq, csaCodigo, pheCodigo, emaExaSigla, emaManSeq);
		
		return executeCriteriaCount(criteria);
	}

	public List<FatProcedHospInternos> listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(
			Object param, String ordem, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montaCriteriaListarPhisPorSeqEDescricao(param);
		
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString(), situacao));
		criteria.addOrder(Order.asc(ordem));		
		
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricaoCount(
			Object param, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montaCriteriaListarPhisPorSeqEDescricao(param);
		
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString(), situacao));
		
		return executeCriteriaCount(criteria);
	}	
	
	/** Conta os procedimentos hospitalares internos que tenham uma nutricao enteral passada
	 * por parâmetro
	 * 
	 * @param tipoNutricaoEnteral
	 * @return
	 */
	public Long contaProcedimentoHospitalarInternoPorNutricaoEnteral(DominioTipoNutricaoParenteral tipoNutricaoEnteral) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		
		if (tipoNutricaoEnteral != null){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.TIPO_NUTRICAO_ENTERAL.toString(), tipoNutricaoEnteral));
		}
		return executeCriteriaCount(criteria);
	}

	public List<FatProcedHospInternos> obterProcedimentoLimitadoPeloMaterial(Object objPesquisa) throws BaseException{
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		
		AghParametros rangeMin = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_RANGE_INICIO_PHI_SCO_MAT);
		AghParametros rangeMax = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_RANGE_FIM_PHI_SCO_MAT);
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "SCO");
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.property("SCO."+ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString())));
		subQuery.add(Restrictions.between("SCO."+ScoMateriaisClassificacoes.Fields.CN5.toString(), rangeMin.getVlrNumerico().longValue(), rangeMax.getVlrNumerico().longValue()));

		if(CoreUtil.isNumeroInteger(objPesquisa)){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
		}else if(StringUtils.isNotBlank((String) objPesquisa)){
			criteria.add(Restrictions.ilike(FatProcedHospInternos.Fields.DESCRICAO.toString(), (String) objPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.and(
				Restrictions.or(
						Restrictions.and(Restrictions.or(Restrictions.isNull(FatProcedHospInternos.Fields.MAT_CODIGO.toString()), Subqueries.propertyIn(FatProcedHospInternos.Fields.MAT_CODIGO.toString(), subQuery))
								, Restrictions.and(
										Restrictions.isNull(FatProcedHospInternos.Fields.PCI_SEQ.toString()),
										Restrictions.and(Restrictions.isNull(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()),
												Restrictions.and(Restrictions.isNull(FatProcedHospInternos.Fields.CSA_CODIGO.toString()),
														Restrictions.and(Restrictions.isNull(FatProcedHospInternos.Fields.PHE_CODIGO.toString())
																,Restrictions.isNull(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString()))))))
						,
						Restrictions.isNotNull(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()))
				,
				Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A)));

		criteria.addOrder(Order.asc(FatProcedHospInternos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria,0, 100, null, false);
	}

	public Long obterProcedimentoLimitadoPeloMaterialCount(Object objPesquisa) throws BaseException{
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		
		AghParametros rangeMin = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_RANGE_INICIO_PHI_SCO_MAT);
		AghParametros rangeMax = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_RANGE_FIM_PHI_SCO_MAT);
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "SCO");
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.property("SCO."+ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString())));
		subQuery.add(Restrictions.between("SCO."+ScoMateriaisClassificacoes.Fields.CN5.toString(), rangeMin.getVlrNumerico().longValue(), rangeMax.getVlrNumerico().longValue()));

		if(CoreUtil.isNumeroInteger(objPesquisa)){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
		}else if(StringUtils.isNotBlank((String) objPesquisa)){
			criteria.add(Restrictions.ilike(FatProcedHospInternos.Fields.DESCRICAO.toString(), (String) objPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.and(
				Restrictions.or(
						Restrictions.and(Restrictions.or(Restrictions.isNull(FatProcedHospInternos.Fields.MAT_CODIGO.toString()), Subqueries.propertyIn(FatProcedHospInternos.Fields.MAT_CODIGO.toString(), subQuery))
								, Restrictions.and(
										Restrictions.isNull(FatProcedHospInternos.Fields.PCI_SEQ.toString()),
										Restrictions.and(Restrictions.isNull(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()),
												Restrictions.and(Restrictions.isNull(FatProcedHospInternos.Fields.CSA_CODIGO.toString()),
														Restrictions.and(Restrictions.isNull(FatProcedHospInternos.Fields.PHE_CODIGO.toString())
																,Restrictions.isNull(FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString()))))))
						,
						Restrictions.isNotNull(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()))
				,
				Restrictions.eq(FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A)));
		
		return executeCriteriaCount(criteria);
	}

	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	public FatProcedHospInternos obterProcedHospInternoPorTipoNutricaoEnteral(DominioTipoNutricaoParenteral tipoNutricaoEnteral){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FatProcedHospInternos.Fields.SEQ.toString()).as("seq") ));
		
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.TIPO_NUTR_PARENTERAL.toString(), tipoNutricaoEnteral));
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedHospInternos.class));
		
		List<FatProcedHospInternos> result = executeCriteria(criteria, 0, 1, null, true);
		
		if (!result.isEmpty()) {
			
			return result.get(0);
			
		}
		return null;
	}
	
	/**
	 * Pesquisa FatProcedHospInternos por procedimento especial diverso.
	 * 
	 * @param material
	 * @return
	 */
	public FatProcedHospInternos obterFatProcedHospInternosPorProcedEspecialDiversos(Short pedSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString(), pedSeq));

		List<FatProcedHospInternos> result = executeCriteria(criteria, 0, 1, null, true);
		
		if (!result.isEmpty()) {
			
			return result.get(0);
			
		}
		return null;
	}
	
	/**
	 * Pesquisa FatProcedHospInternos por material
	 * 
	 * @param material
	 * @return
	 */
	public  FatProcedHospInternos obterFatProcedHospInternosPorMaterial(Integer matCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.MAT_CODIGO.toString(), matCodigo));

		List<FatProcedHospInternos> result = executeCriteria(criteria, 0, 1, null, true);
		
		if (!result.isEmpty()) {
			
			return result.get(0);
			
		}
		return null;

	}
	
	/**
	 * Pesquisa FatProcedHospInternos por procedimento cirurgico.
	 * 
	 * @param material
	 * @return
	 */
	public FatProcedHospInternos obterFatProcedHospInternosPorProcedimentoCirurgicos(Integer pciSeq) {
		DetachedCriteria criteria = obterCriteriaListarFatProcedHospInternosPorProced(pciSeq);

		List<FatProcedHospInternos> result = executeCriteria(criteria, 0, 1, null, true);
		if (!result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	public List<FatProcedHospInternos> listarProcedHospInternoPorSeqOuDescricao(Object param, Integer maxResults, String order) {

		DetachedCriteria criteria = obterCriteriaListarProcedHospInternoPorSeqOuDescricao(param);
		
		if (CoreUtil.isNumeroInteger(param)){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), (Integer.valueOf(param.toString()))));
		} else if (param != null && !param.toString().equals("")) {
			criteria.add(Restrictions.ilike(FatProcedHospInternos.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(order));
		return executeCriteria(criteria, 0, maxResults, null, true);
	}	

	public Long listarProcedHospInternoPorSeqOuDescricaoCount(Object param) {
		DetachedCriteria criteria = obterCriteriaListarProcedHospInternoPorSeqOuDescricao(param);

		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaListarProcedHospInternoPorSeqOuDescricao(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);

		if (CoreUtil.isNumeroInteger(param)){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.SEQ.toString(), (Integer.valueOf(param.toString()))));
		} else if (param != null && !param.toString().equals("")){
			criteria.add(Restrictions.ilike(FatProcedHospInternos.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	public Long verificaProcedimentoHospitalarInternoCount(Integer matCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.MAT_CODIGO.toString(), matCodigo));
		return executeCriteriaCount(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcedHospInternosTriagemClinicaVO> listarProcedHospInternosTriagemClinica(Date vDtHrInicio, Date vDtHrFim, Integer matricula, Short serVinCodigo) {
		
		Query q = this.obterQuerySession(vDtHrInicio, vDtHrFim, this.obterHqlUnion1(matricula, serVinCodigo));
		List<ProcedHospInternosTriagemClinicaVO> listaVO = q.list();
		
		q = this.obterQuerySession(vDtHrInicio, vDtHrFim, this.obterHqlUnion2(matricula, serVinCodigo));
		listaVO.addAll(q.list());
		
		return listaVO;
	}
	
	private Query obterQuerySession(Date vDtHrInicio, Date vDtHrFim, StringBuilder query) {
		Query q = createHibernateQuery(query.toString());
		q.setDate("vDtHrInicio", vDtHrInicio);
		q.setDate("vDtHrFim", vDtHrFim);
		q.setResultTransformer(Transformers.aliasToBean(ProcedHospInternosTriagemClinicaVO.class));
		
		return q;
	}
	
	private StringBuilder obterHqlUnion1(Integer matricula, Short serVinCodigo) {
		
		StringBuilder hql = new StringBuilder(500);
		hql.append(" SELECT ");
		hql.append("  TDC.").append(MamTrgMedicacoes.Fields.DTHR_CONSISTENCIA_OK.toString()).append(" as dthrConsistenciaOk, ");
		hql.append("  PHI.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" as phiSeq, ");
		hql.append("CASE WHEN TDC.").append(MamTrgMedicacoes.Fields.SER_MATRICULA.toString()).
		append(" is null THEN " + matricula + " ELSE TDC.").append(MamTrgMedicacoes.Fields.SER_MATRICULA.toString()).
		append(" END as serMatricula, ");
		hql.append("CASE WHEN TDC.").append(MamTrgMedicacoes.Fields.SER_VIN_CODIGO.toString())
		.append(" is null THEN " + serVinCodigo + " ELSE TDC.").append(MamTrgMedicacoes.Fields.SER_VIN_CODIGO.toString()).
		append(" END as serVinCodigo, ");

		hql.append("  TRG.").append(MamTriagens.Fields.UNF_SEQ.toString()).append(" as unfSeq, ");
		hql.append("  TRG.").append(MamTriagens.Fields.PAC_CODIGO.toString()).append(" as pacCodigo, ");
		
		hql.append("  TDC.").append(MamTrgMedicacoes.Fields.TRG_SEQ.toString()).append(" as trgSeq, ");
		hql.append("  TDC.").append(MamTrgMedicacoes.Fields.SEQP.toString()).append(" as seqp ");
		
		hql.append(" FROM ");
		hql.append(FatProcedHospInternos.class.getSimpleName()).append(" PHI ");
		hql.append(" 	,").append(MamItemMedicacao.class.getSimpleName()).append(" MDM ");
		hql.append(" 	,").append(MamTriagens.class.getSimpleName()).append(" TRG ");
		hql.append(" 	,").append(MamTrgMedicacoes.class.getSimpleName()).append(" TDC ");
		hql.append(" WHERE ");
		hql.append("  TDC.").append(MamTrgMedicacoes.Fields.TRG_SEQ.toString()).append(" > 0 ");
		hql.append(" AND TDC.").append(MamTrgMedicacoes.Fields.DTHR_CONSISTENCIA_OK.toString()).append(" BETWEEN :vDtHrInicio AND :vDtHrFim ");
		hql.append(" 	AND TRG.").append(MamTriagens.Fields.SEQ.toString()).append("= TDC.").append(MamTrgMedicacoes.Fields.TRG_SEQ.toString());
		hql.append(" 	AND MDM.").append(MamItemMedicacao.Fields.SEQ.toString()).append("= TDC.").append(MamTrgMedicacoes.Fields.MDM_SEQ.toString());
		hql.append(" 	AND PHI.").append(FatProcedHospInternos.Fields.MDM_SEQ.toString()).append("= MDM.").append(MamItemMedicacao.Fields.SEQ.toString());
		
		return hql;
	}
	
	private StringBuilder obterHqlUnion2(Integer matricula, Short serVinCodigo) {
		StringBuilder hql = new StringBuilder(500);
		hql.append(" SELECT ");
		hql.append("  TXA.").append(MamTrgExames.Fields.DTHR_CONSISTENCIA_OK.toString()).append(" as dthrConsistenciaOk, ");
		hql.append("  PHI.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" as phiSeq, ");
		hql.append("CASE WHEN TXA.").append(MamTrgExames.Fields.SER_MATRICULA.toString()).
		append(" is null THEN  " + matricula + " ELSE TXA.").append(MamTrgExames.Fields.SER_MATRICULA.toString()).
		append(" END as serMatricula, ");
		hql.append("CASE WHEN TXA.").append(MamTrgExames.Fields.SER_VIN_CODIGO.toString())
		.append(" is null THEN " + serVinCodigo + " ELSE TXA.").append(MamTrgExames.Fields.SER_VIN_CODIGO.toString()).
		append(" END as serVinCodigo, ");

		hql.append("  TRG.").append(MamTriagens.Fields.UNF_SEQ.toString()).append(" as unfSeq, ");
		hql.append("  TRG.").append(MamTriagens.Fields.PAC_CODIGO.toString()).append(" as pacCodigo, ");

		hql.append("  TXA.").append(MamTrgExames.Fields.TRG_SEQ.toString()).append(" as txaTrgSeq, ");
		hql.append("  TXA.").append(MamTrgExames.Fields.SEQP.toString()).append(" as txaSeqp ");
		
		hql.append(" FROM ");
		hql.append(FatProcedHospInternos.class.getSimpleName()).append(" PHI ");
		hql.append(" 	,").append(MamItemExame.class.getSimpleName()).append(" EMS ");
		hql.append(" 	,").append(MamTriagens.class.getSimpleName()).append(" TRG ");
		hql.append(" 	,").append(MamTrgExames.class.getSimpleName()).append(" TXA ");
		hql.append(" WHERE ");
		hql.append("  TXA.").append(MamTrgExames.Fields.TRG_SEQ.toString()).append(" > 0 ");
		hql.append(" AND TXA.").append(MamTrgExames.Fields.DTHR_CONSISTENCIA_OK.toString()).append(" BETWEEN :vDtHrInicio AND :vDtHrFim ");
		hql.append(" 	AND TRG.").append(MamTriagens.Fields.SEQ.toString()).append("= TXA.").append(MamTrgExames.Fields.TRG_SEQ.toString());
		hql.append(" 	AND EMS.").append(MamItemExame.Fields.SEQ.toString()).append("= TXA.").append(MamTrgExames.Fields.EMS_SEQ.toString());
		hql.append(" 	AND PHI.").append(FatProcedHospInternos.Fields.EMS_SEQ.toString()).append("= EMS.").append(MamItemExame.Fields.SEQ.toString());
		
		return hql;
	}
	
	public FatProcedHospInternos obterFatProcedHospInternosPorCodigoProcedimentoHemoterapico(String pheCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "PHI");
		dc.add(Restrictions.eq(FatProcedHospInternos.Fields.PHE_CODIGO.toString(), pheCodigo));
		List<FatProcedHospInternos> listResult = executeCriteria(dc);
		return listResult != null && !listResult.isEmpty() ? listResult.get(0) : null;
	}
	
	/*
	 * Busca o PHI para a insercao de objeto de custo no modulo de Custos.
	 */
	public List<FatProcedHospInternos> listaPhiAtivosExame(DominioSituacao situacao, String exameSigla){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class);
		criteria.add(Restrictions.eq(FatProcedHospInternosPai.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq(FatProcedHospInternosPai.Fields.EMA_EXA_SIGLA.toString(), exameSigla));
		criteria.add(Restrictions.isNull(FatProcedHospInternosPai.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO.toString()));
		return executeCriteria(criteria);
	}

	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloSeqProcCirg(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "PHI");
		criteria.createAlias(FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PCI", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PHE_CODIGO.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.CSA_CODIGO.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()));
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloMatCodigo(Integer matCod) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "PHI");
		criteria.createAlias(FatProcedHospInternos.Fields.MATERIAIS.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.MAT_CODIGO.toString(), matCod));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PHE_CODIGO.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.CSA_CODIGO.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PCI_SEQ.toString()));
		criteria.addOrder(Order.asc("PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<EquipamentoCirurgiaVO> buscarEquipamentos(Integer crgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "phi");
		criteria.createAlias("phi."+FatProcedHospInternos.Fields.EQUIPAMENTO_CIRURGICOS.toString(), "euu", Criteria.INNER_JOIN);
		criteria.createAlias("euu."+MbcEquipamentoCirurgico.Fields.LIST_EQUIPAMENTOS_CIRURGICOS.toString(), "eqc", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("eqc."+MbcEquipamentoUtilCirg.Fields.IND_USO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("eqc."+MbcEquipamentoUtilCirg.Fields.MBC_CIRURGIAS.toString()+"."+MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("phi."+FatProcedHospInternos.Fields.SEQ.toString()), EquipamentoCirurgiaVO.Fields.SEQ.toString() )
		.add(Projections.sum("eqc."+MbcEquipamentoUtilCirg.Fields.QUANTIDADE.toString()), EquipamentoCirurgiaVO.Fields.SOMATORIO.toString() )
		.add(Projections.groupProperty("phi."+FatProcedHospInternos.Fields.SEQ.toString()), EquipamentoCirurgiaVO.Fields.SEQ.toString() );
		
		criteria.setProjection(projection);
		criteria.addOrder(Order.asc("phi."+FatProcedHospInternos.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(EquipamentoCirurgiaVO.class));
	    return executeCriteria(criteria);
	}
	
	public List<Integer> buscarPhiSeqPorPciSeqOrigemPacienteCodigo(Integer pciSeq, Byte origemCodigo, Short grupoSUS) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "PHI");
		criteria.setProjection(Projections.property("PHI." + FatProcedHospInternos.Fields.SEQ.toString()));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAPR");
		subCriteria.setProjection(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("PHI." + FatProcedHospInternos.Fields.SEQ.toString(), 
				"VAPR." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		subCriteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS));
		subCriteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), origemCodigo));
		subCriteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.exists(subCriteria));
		
		return executeCriteria(criteria);
	}
	
	
	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPorTipoEtiqueta(String tipoEtiqueta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "PHI");
		criteria.createAlias(FatProcedHospInternosPai.Fields.OBJETO_CUSTO_PHI.toString(), "OPHI");
		criteria.createAlias(SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO.toString(), "OCV");
		criteria.createAlias(SigObjetoCustoVersoes.Fields.COMPOSICOES.toString(), "OCC");
		
		criteria.add(Restrictions.eq("PHI."+FatProcedHospInternosPai.Fields.TIPO_ETIQUETA, tipoEtiqueta));
		criteria.add(Restrictions.eq("OPHI."+SigObjetoCustoPhis.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}	

	public List<RapServidores> buscaUsuariosPorCCusto(Integer cCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);
		criteria.add(Restrictions.isNotNull(RapServidores.Fields.USUARIO.toString()));
		if (cCusto != null) {
			criteria.add(Restrictions.eq(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(), cCusto));
		} else {
			criteria.add(Restrictions.isNull(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString()));
		}
		return this.executeCriteria(criteria);
	}

	public List<FatProcedHospInternos> pesquisarProcedimentosInternosPeloCodProcHem(
			String codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "PHI");
		criteria.createAlias(FatProcedHospInternos.Fields.PROCED_HEMOTERAPICO.toString(), "PHE", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("PHI." + FatProcedHospInternos.Fields.PHE_CODIGO.toString(), codigo));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PCI_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.CSA_CODIGO.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ.toString()));
		criteria.add(Restrictions.isNull("PHI." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()));
		criteria.addOrder(Order.asc("PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<FatProcedHospInternos> listarFatProcedHospInternosPorProcedDesc(Integer pciSeq, String desc){
		DetachedCriteria criteria = obterCriteriaListarFatProcedHospInternosPorProced(pciSeq);
		if (desc!=null && !"".equalsIgnoreCase(desc)){
			criteria.add(Restrictions.eq(FatProcedHospInternos.Fields.DESCRICAO.toString(), desc));
		}	

		return executeCriteria(criteria);

	}

	
	/**
	 * #43342 SB1
	 * 
	 * @param grdSeq, especialidade, parametro
	 */
	public List<FatProcedHospInternosVO> listarFatProcedHospInternosPorEspOuEspGrad(Integer grdSeq, AghEspecialidades especialidade, String parametro){
		DetachedCriteria criteria = criteriaListarFatProcedHospInternosPorEspOuEspGrad(grdSeq, especialidade, parametro, false);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	/**
	 * #43342 SB1
	 * 
	 * @param grdSeq, especialidade, parametro
	 */
	public Long listarFatProcedHospInternosPorEspOuEspGradCount(Integer grdSeq, AghEspecialidades especialidade, String parametro){
		DetachedCriteria criteria = criteriaListarFatProcedHospInternosPorEspOuEspGrad(grdSeq, especialidade, parametro, true);
		return (long) (executeCriteria(criteria)).size();
	}
	
	/**
	 * #43342 SB1
	 * 
	 * @param grdSeq, especialidade, parametro
	 */
	private DetachedCriteria criteriaListarFatProcedHospInternosPorEspOuEspGrad(Integer grdSeq, AghEspecialidades especialidade, String parametro, boolean count) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedAmbRealizado.class, "R");
		criteria.createAlias("R."+FatProcedAmbRealizado.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI", JoinType.LEFT_OUTER_JOIN);
		
		if(grdSeq != null){
			criteria.createAlias("R."+FatProcedAmbRealizado.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
			criteria.createAlias("ESP."+AghEspecialidades.Fields.GRADE_AGENDAMENTO.toString(), "GRD", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("GRD."+AacGradeAgendamenConsultas.Fields.SEQ.toString(), grdSeq));			
		}else if(especialidade != null){
			criteria.add(Restrictions.eq("R."+FatProcedAmbRealizado.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		
		if (parametro != null && !parametro.isEmpty()) {
			criteria.add(Restrictions.ilike("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
		}
			
		criteria.setProjection(Projections.projectionList()
	    			.add(Projections.distinct(Projections.property("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString())
	    					.as(FatProcedHospInternosVO.Fields.DESCRICAO.toString()))));
		
		criteria.addOrder(Order.asc("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString()));
	    	
    	if(!count){
    		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedHospInternosVO.class));
    	}
		
		return criteria;
	}
	
	/**
	 * #43342 Coluna Procedimentos
	 * 
	 * @param numero
	 */
	public List<FatProcedHospInternos> obterDescricaoProcColunaArq(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		criteria.createAlias("C."+AacConsultas.Fields.PROCEDIMENTOS_HOSPITALARES.toString(), "CPH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("C."+AacConsultas.Fields.PROCEDIMENTO_AMB_REALIZADOS.toString(), "PAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CPH."+AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString(), "PHI", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("C."+AacConsultas.Fields.NUMERO.toString(), numero));
		//criteria.add(Restrictions.eqProperty("PAR."+FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), "CPH."+AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString()));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString())
						.as(FatProcedHospInternos.Fields.DESCRICAO.toString()))
		);
		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedHospInternos.class));
		return executeCriteria(criteria);
	}
	
	public FatProcedHospInternos obterCodigoDescricaoProcedimentoProTransplante(Long ps7, Long ps8, Integer codigoPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class,"PHI");
		
		StringBuilder sql = new StringBuilder(350);
		
		sql.append("( 'CORNEA' = (select ttr_codigo from (select t.ttr_codigo from fat_paciente_transplantes t where t.pac_codigo = ? order by t.dt_transplante desc) ");
		aicionarLimite(sql);
		sql.append(" and this_.seq = ?)");
		sql.append("or ('CORNEA' <> (select ttr_codigo from (select t.ttr_codigo from fat_paciente_transplantes t where t.pac_codigo = ? order by t.dt_transplante desc) ");
		aicionarLimite(sql);
		sql.append(" and this_.seq = ?)");
		
		criteria.add(Restrictions.sqlRestriction(sql.toString(), new Object []{codigoPaciente, ps7, codigoPaciente, ps8}, new Type[] {IntegerType.INSTANCE, LongType.INSTANCE, IntegerType.INSTANCE, LongType.INSTANCE}));
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("PHI."+FatProcedHospInternos.Fields.SEQ.toString()).as(FatProcedHospInternos.Fields.SEQ.toString()));
		projectionList.add(Projections.property("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString()).as(FatProcedHospInternos.Fields.DESCRICAO.toString()));
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(FatProcedHospInternos.class));
		return (FatProcedHospInternos) executeCriteriaUniqueResult(criteria);
	}

	private void aicionarLimite(StringBuilder sql) {
		if(isOracle()){
			sql.append("where rownum = 1)");
	    } else {
	    	sql.append("limit 1)");
	    }
	}
	
	/**
	 * ORADB AINC_BUSCA_UNF_MVI_DT
	 * @param dataInicial
	 * @param dataFinal
	 * @return Dados de produtividade fisiatria.
	 */
	public List<Object[]> pesquisarDadosGeracaoArquivoProdutividadeFisiatria(Date dataInicial, Date dataFinal) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghUnidFuncional.class, "VUF1");
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()));
		projList.add(Projections.property("PIF." + MptProcedimentoInternacao.Fields.CRIADO_EM.toString()));
		projList.add(Projections.property("PF." + RapPessoasFisicas.Fields.NOME.toString()));
		projList.add(Projections.property("PFSOL." + RapPessoasFisicas.Fields.NOME.toString()));
		projList.add(Projections.property("PHI." + FatProcedHospInternos.Fields.SEQ.toString()));
		projList.add(Projections.property("PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		projList.add(Projections.property("VUF1." + VAghUnidFuncional.Fields.ANDAR_ALA_DESCRICAO.toString()));
		projList.add(Projections.property("PIF." + MptProcedimentoInternacao.Fields.TIPO_PROCEDIMENTO.toString()));
		projList.add(Projections.property("PIF." +MptProcedimentoInternacao.Fields.DATA_REALIZADO.toString()));
		criteria.setProjection(Projections.distinct(projList));
		criteria.createAlias("VUF1." + VAghUnidFuncional.Fields.AIN_MOVIMENTOS_INTERNACAO.toString(), "MI", JoinType.INNER_JOIN);
		criteria.createAlias("MI." + AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT", JoinType.INNER_JOIN);
		criteria.createAlias("INT." + AinInternacao.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.LISTA_MPT_PROCED_INTERNACAO.toString(), "PIF", JoinType.INNER_JOIN);
		criteria.createAlias("PIF." + MptProcedimentoInternacao.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI", JoinType.INNER_JOIN);
		criteria.createAlias("PIF." + MptProcedimentoInternacao.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.VINCULO.toString(), "VINC", JoinType.INNER_JOIN);
		criteria.createAlias("PIF." + MptProcedimentoInternacao.Fields.SERVIDOR_SOLICITADO.toString(), "SERSOL", JoinType.INNER_JOIN);
		criteria.createAlias("SERSOL." + RapServidores.Fields.PESSOA_FISICA.toString(), "PFSOL", JoinType.INNER_JOIN);
		criteria.createAlias("SERSOL." + RapServidores.Fields.VINCULO.toString(), "VINCSOL", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eqProperty("MI."+AinMovimentosInternacao.Fields.INT_SEQ.toString(), "ATD."+AghAtendimentos.Fields.INTSEQ.toString()));
		criteria.add(Restrictions.between("PIF." + MptProcedimentoInternacao.Fields.CRIADO_EM.toString(), dataInicial, dataFinal));
		criteria.add(Restrictions.leProperty("MI." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), "PIF." + MptProcedimentoInternacao.Fields.CRIADO_EM.toString()));
		
		//ORADB AINC_BUSCA_UNF_MVI_DT
		if (!isOracle()) {
		DetachedCriteria subQuery = DetachedCriteria.forClass(AinMovimentosInternacao.class, "MVI");		
		subQuery.setProjection(Projections.property("MVI." + AinMovimentosInternacao.Fields.UNF_SEQ.toString()));
		subQuery.add(Restrictions.eqProperty("MVI." + AinMovimentosInternacao.Fields.INT_SEQ.toString(), "ATD." + AghAtendimentos.Fields.INTSEQ.toString()));
		subQuery.add(Restrictions.leProperty("MVI." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), "PIF." + MptProcedimentoInternacao.Fields.CRIADO_EM.toString()));
			subQuery.addOrder(OrderBySql
					.sql("MVI_.DTHR_LANCAMENTO desc LIMIT 1 "));
				criteria.add(Property.forName("VUF1." + VAghUnidFuncional.Fields.SEQ.toString()).eq(subQuery));
		} else {
			criteria.add(Restrictions.sqlRestriction("this_.seq = AINC_BUSCA_UNF_MVI_DT(atd3_.int_seq,pif4_.criado_em) "));
		}
		
		criteria.addOrder(Order.asc("ATD." + AghAtendimentos.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	public List<NutricaoEnteralDigitadaVO> getNutricaoEnteralDigitada() throws ApplicationBusinessException{
		
		StringBuilder SQL = new StringBuilder(5300);
		FatContasHospitalaresQueryBuilder builder = new FatContasHospitalaresQueryBuilder();
		SQL = builder.getNutricaoEnteralDigitada(SQL);
		SQLQuery q = createSQLQuery(SQL.toString());
		
		q.addScalar("seq",IntegerType.INSTANCE)
		 .addScalar("dtencerramento", TimestampType.INSTANCE)
		 .addScalar("nroaih", LongType.INSTANCE)
		 .addScalar("indsituacao", StringType.INSTANCE);
		
		q.setResultTransformer(Transformers.aliasToBean(NutricaoEnteralDigitadaVO.class));
		 
		final List<NutricaoEnteralDigitadaVO> result = q.list();
		return result;
	}
	
	public List<ContasNPTVO> getContasNPT(String data1, String data2) throws ApplicationBusinessException{
		
		StringBuilder SQL = new StringBuilder(5300);
		
		SQL.append("SELECT fch.seq                                  AS conta, \n");
		SQL.append("  COALESCE(fch.ind_autorizado_sms,'N')          AS autorizadasms, \n");
		SQL.append("  pac.prontuario                                AS prontuario, \n");
		SQL.append("  pac.nome                                      AS nome, \n");
		SQL.append("  COALESCE(DT_ALTA_ADMINISTRATIVA,CURRENT_DATE) AS dtalta, \n");
		SQL.append("  DT_INT_ADMINISTRATIVA                         AS dtint, \n");
		SQL.append("  SUM(quantidade_realizada)                     AS qtde \n");
		SQL.append("FROM AGH.aip_pacientes pac, \n");
		SQL.append("  AGH.ain_internacoes inte, \n");
		SQL.append("  AGH.fat_contas_internacao coi, \n");
		SQL.append("  AGH.FAT_CONTAS_HOSPITALARES FCH, \n");
		SQL.append("  AGH.fat_itens_conta_hospitalar ich \n");
		SQL.append("WHERE pac.codigo      = inte.pac_codigo \n");
		SQL.append("AND inte.seq          = coi.int_seq \n");
		SQL.append("AND coi.cth_seq       = fch.seq \n");
		SQL.append("AND fch.seq           = ich.cth_seq \n");
		SQL.append("AND ich.ind_situacao IN ('V','P') \n");
		
		SQL.append("AND   ich.phi_seq IN ("+getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPLETA_PHI1).getVlrNumerico()+", "+getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPLETA_PHI2).getVlrNumerico()+") \n");
		if(data1 != null){
			SQL.append(" and DCI_CODIGO_DCIH  BETWEEN '"+data1+"' AND '"+data2+"' \n");
		}
		//SQL.append("  --and DCI_CODIGO_DCIH  BETWEEN <início competencia> AND <fim competencia> \n");
		SQL.append("GROUP BY fch.seq, \n");
		SQL.append("  fch.ind_autorizado_sms, \n");
		SQL.append("  pac.prontuario, \n");
		SQL.append("  pac.nome, \n");
		SQL.append("  SIA_MSP_SEQ, \n");
		SQL.append("  SIA_SEQ, \n");
		SQL.append("  COALESCE(DT_ALTA_ADMINISTRATIVA,CURRENT_DATE) , \n");
		SQL.append("  DT_INT_ADMINISTRATIVA, \n");
		SQL.append("  DCI_CODIGO_DCIH \n");
		SQL.append("ORDER BY fch.seq");
		
		SQLQuery q = createSQLQuery(SQL.toString());
		
		q.addScalar("conta",IntegerType.INSTANCE)
		 .addScalar("autorizadasms", StringType.INSTANCE)
		 .addScalar("nome", StringType.INSTANCE)
		 .addScalar("prontuario",IntegerType.INSTANCE)
		 //.addScalar("nroaih", LongType.INSTANCE)
		 .addScalar("dtalta", TimestampType.INSTANCE)
		 .addScalar("dtint", TimestampType.INSTANCE)
		 .addScalar("qtde",IntegerType.INSTANCE);
		 
		
		q.setResultTransformer(Transformers.aliasToBean(ContasNPTVO.class));
		 
		final List<ContasNPTVO> result = q.list();
		return result;
	}
	
}