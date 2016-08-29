package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.FatTabIPHVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoRealizadoDadosOPMVO;
import br.gov.mec.aghu.faturamento.vo.ValoresPreviaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatVlrItemProcedHospCompsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatVlrItemProcedHospComps> {

	private static final long serialVersionUID = 7089565502061682062L;

	/**
	 * ORADB: Cursor: ATUALIZA_COMPATIBILIDADE.C_IPH
	 */
	public List<FatTabIPHVO> obterValoresCompatibilidade(final Short phoSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class,"VLR");

		criteria.createAlias(FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), "IPH");
		
		criteria.add(Restrictions.eq("VLR."+FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq("IPH."+FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull("VLR."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));
		
		criteria.addOrder(Order.asc("IPH."+FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		
		criteria.setProjection(Projections.projectionList()
								.add(Projections.property("IPH."+FatItensProcedHospitalar.Fields.SEQ.toString()), FatTabIPHVO.Fields.IPH_SEQ.toString())
								.add(Projections.property("IPH."+FatItensProcedHospitalar.Fields.PHO_SEQ.toString()), FatTabIPHVO.Fields.PHO_SEQ.toString())
								.add(Projections.property("IPH."+FatItensProcedHospitalar.Fields.COD_TABELA.toString()), FatTabIPHVO.Fields.COD_TABELA.toString())
								.add(Projections.property("VLR."+FatVlrItemProcedHospComps.Fields.QTD_MAXIMA_EXECUCAO.toString()), FatTabIPHVO.Fields.QTD_MAXIMA_EXECUCAO.toString())
								.add(Projections.property("VLR."+FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString()), FatTabIPHVO.Fields.DT_INICIO_COMPETENCIA.toString())
							  );

		criteria.setResultTransformer(Transformers.aliasToBean(FatTabIPHVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<FatVlrItemProcedHospComps> obterFatVlrItemProcedHospCompsPorProcedimentoECompetencia(final Short phoSeq, final Integer iphSeq, final Date pCompetencia){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class,"IPC");

		criteria.createAlias(FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), "IPH");

		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));

		String sql = " ? BETWEEN {alias}.DT_INICIO_COMPETENCIA AND COALESCE({alias}.DT_FIM_COMPETENCIA, ?)";

		Object[] values = {pCompetencia, DateUtil.truncaData(new Date())};
		Type[] types = { TimestampType.INSTANCE, TimestampType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sql, values, types));

		return executeCriteria(criteria);
	}
	

	protected DetachedCriteria obterCriteriaValorItemProcHospCompPorPhoIphParaCompetencia(final Short phoSeq, final Integer iphSeq,
			final Date dataCompetencia) {

		final DetachedCriteria result = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);
		result.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		result.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));
		result.add(Restrictions.le(FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), dataCompetencia));
		result.add(Restrictions.or(Restrictions.ge(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), dataCompetencia),
				Restrictions.isNull(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString())));

		return result;
	}

	protected DetachedCriteria obterCriteriaValorItemProcHospComp(final Short phoSeq, final Integer iphSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);
		result.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		result.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));

		return result;
	}

	protected DetachedCriteria obterCriteriaValorItemProcHospCompPorPhoIphAbertos(final Short phoSeq, final Integer iphSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);
		result.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		result.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));
		result.add(Restrictions.isNull(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));

		return result;
	}

	/**
	 * ORADB: FATK_CTHN_RN_UN cursor: c_max_conta
	 * @param phoSeq
	 * @param iphSeq
	 * @param dataCompetencia
	 * @return
	 */
	public Long obterQtdMaximaExecucaoPorPhoIphParaCompetencia(final Short phoSeq, final Integer iphSeq, final Date dataCompetencia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);
		criteria.setProjection(Projections.property(FatVlrItemProcedHospComps.Fields.QTD_MAXIMA_EXECUCAO.toString()));
		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));
		
		// Ney 14/09/2011 Portaria 203 terceira fase
		criteria.add(Restrictions
				.sqlRestriction(
						isOracle() ? " trunc(?) between {alias}.DT_INICIO_COMPETENCIA and nvl({alias}.DT_FIM_COMPETENCIA,trunc(sysdate)) "
								: " date_trunc('day', ?::timestamp) between {alias}.DT_INICIO_COMPETENCIA and case when {alias}.DT_FIM_COMPETENCIA is null then date_trunc('day', now()) else {alias}.DT_FIM_COMPETENCIA end ",
						dataCompetencia, DateType.INSTANCE));
		final Object obj = executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return 0l;
		}
		return ((Short) obj).longValue();
	}
	
	/**
	 * #36436 C7
	 * Método para obter lista de procedimentos realizados
	 */
	public List<ProcedimentoRealizadoDadosOPMVO> obterListaProcedimentoRealizados(Integer cthSeq){
		FatVlrItemProcHospCompsObterListaProcedimentosRealizadosQueryBuilder builder = new FatVlrItemProcHospCompsObterListaProcedimentosRealizadosQueryBuilder();
		return executeCriteria(builder.build(cthSeq));
	}

	public int obterQtdValorItemProcHospCompPorPhoIphParaCompetencia(final Short phoSeq, final Integer iphSeq, final Date dataCompetencia) {

		Long result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaValorItemProcHospCompPorPhoIphParaCompetencia(phoSeq, iphSeq, dataCompetencia);
		result = this.executeCriteriaCount(criteria);

		return (result != null ? result.intValue() : 0);
	}

	public FatVlrItemProcedHospComps obterPrimeiroValorItemProcHospCompPorPhoIphParaCompetencia(final Short phoSeq, final Integer iphSeq,
			final Date dataCompetencia) {

		final DetachedCriteria criteria = this.obterCriteriaValorItemProcHospCompPorPhoIphParaCompetencia(phoSeq, iphSeq, dataCompetencia);
		//final List<FatVlrItemProcedHospComps> list = executeCriteria(criteria, 0, 1, null, true);
		final List<FatVlrItemProcedHospComps> list = executeCriteria(criteria, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<FatVlrItemProcedHospComps> obterListaValorItemProcHospCompPorPhoIphAbertos(final Short phoSeq, final Integer iphSeq) {

		List<FatVlrItemProcedHospComps> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaValorItemProcHospCompPorPhoIphAbertos(phoSeq, iphSeq);
		result = this.executeCriteria(criteria);

		return result;
	}

	public FatVlrItemProcedHospComps buscaValorItemProcedimentoHospitalar(final Short iphPhoSeq, final Integer iphSeq, final Date dataCompetencia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);

		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));

		criteria.add(Restrictions.le(FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), dataCompetencia));

		criteria.add(Restrictions.or(Restrictions.isNull(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()),
				Restrictions.ge(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), dataCompetencia)));

		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		final List<FatVlrItemProcedHospComps> list = executeCriteria(criteria, 0, 1, null, true, CacheMode.NORMAL);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Long obterQuantidadeValorItemProcHospSemDataFimPorIphSeqEIphPhoSeq(final Integer iphSeq, final Short iphPhoSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);
		criteria.setProjection(Projections.projectionList().add(Projections.rowCount()));

		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.isNull(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));

		final Long result = (Long) executeCriteriaUniqueResult(criteria);
		return (result != null) ? result : 0l;
	}

	public List<FatVlrItemProcedHospComps> obterListaValorItemProcHospComp(final Short phoSeq, final Integer iphSeq) {
		final DetachedCriteria criteria = this.obterCriteriaValorItemProcHospComp(phoSeq, iphSeq);
		criteria.addOrder(Order.desc(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));

		return this.executeCriteria(criteria);
	}

	public List<FatVlrItemProcedHospComps> pesquisarProcedimentos(final Object strPesquisa, final Short parametroProcedimento, final AipPacientes paciente,
			final List<Long> listaProcedimentosAssociacao, final Integer clinExclusaoPed, final Integer clinExclusaoAdu, final Integer carctAceitaPedAdu, final Integer idadePediatrica)
			throws ApplicationBusinessException {

		final DetachedCriteria cri = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class);

		cri.add(Restrictions.le("id.dtInicioCompetencia", Calendar.getInstance().getTime()));
		cri.add(Restrictions.or(Restrictions.isNull("dtFimCompetencia"), Restrictions.ge("dtFimCompetencia", Calendar.getInstance().getTime())));

		cri.createAlias(FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), "PROCEDIMENTO");
		
		if(strPesquisa != null && !StringUtils.isEmpty(String.valueOf(strPesquisa))) {
			cri.add(Restrictions.or(Restrictions.sqlRestriction("cast(COD_TABELA as varchar(50)) like '%" + strPesquisa.toString() + "%'"), Restrictions
					.ilike("PROCEDIMENTO." + FatItensProcedHospitalar.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE)));
		}
		
		if (carctAceitaPedAdu == null) {
			throw new IllegalArgumentException("Parâmetro P_SEQ_FAT_CARACT_ACEITA_ADULTO_PED não existe no banco de dados.");
		}
		
		//#54009
		/*
		final DetachedCriteria cri2 = DetachedCriteria.forClass(FatCaractItemProcHosp.class, "CARAC_PROCEDIMENTO");
		cri2.add(Restrictions.eq("CARAC_PROCEDIMENTO.id.tctSeq", carctAceitaPedAdu));
		cri2.add(Property.forName("CARAC_PROCEDIMENTO.id.iphPhoSeq").eqProperty("PROCEDIMENTO.id.phoSeq"));
		cri2.add(Property.forName("CARAC_PROCEDIMENTO.id.iphSeq").eqProperty("PROCEDIMENTO.id.seq"));
		
		
		if (paciente.getIdade() >= idadePediatrica) {
			//é adulto - remove a clinica pediárica (7)
			Criterion criterionNuloOuSoAdulto = Restrictions.or(Restrictions.isNull("PROCEDIMENTO.clinica.codigo"),Restrictions.ne("PROCEDIMENTO.clinica.codigo", clinExclusaoAdu));
			Criterion criterionAceitaAlgumasCriancas = Restrictions.and(
					Restrictions.eq("PROCEDIMENTO.clinica.codigo", clinExclusaoAdu), 
					Subqueries.exists(cri2.setProjection(Projections.property("CARAC_PROCEDIMENTO.id.tctSeq"))));
			cri.add(Restrictions.or(criterionNuloOuSoAdulto, criterionAceitaAlgumasCriancas));
			
		} else {
			//é criança - remove a clinica médica (3)
			Criterion criterionNuloOuSoCrianca = Restrictions.or(Restrictions.isNull("PROCEDIMENTO.clinica.codigo"),Restrictions.ne("PROCEDIMENTO.clinica.codigo", clinExclusaoPed));
			Criterion criterionAceitaAlgunsAdultos = Restrictions.and(
					Restrictions.eq("PROCEDIMENTO.clinica.codigo", clinExclusaoPed), 
					Subqueries.exists(cri2.setProjection(Projections.property("CARAC_PROCEDIMENTO.id.tctSeq")))
					);
			cri.add(Restrictions.or(criterionNuloOuSoCrianca,criterionAceitaAlgunsAdultos));
		}
		*/
		
		cri.add(Restrictions.or(Restrictions.eq("PROCEDIMENTO.sexo", DominioSexoDeterminante.Q),
				Restrictions.eq("PROCEDIMENTO.sexo", paciente.getSexo())));

		cri.add(Restrictions.eq("PROCEDIMENTO.situacao", DominioSituacao.A));

		cri.add(Restrictions.eq("PROCEDIMENTO.id.phoSeq", parametroProcedimento));
		cri.add(Restrictions.eq("PROCEDIMENTO.internacao", true));

		cri.add(Restrictions.or(
				Restrictions.and(
						Restrictions.eq("PROCEDIMENTO.exigeValor", true),
						Restrictions.or(Restrictions.isNotNull("vlrServHospitalar"),
								Restrictions.or(Restrictions.isNotNull("vlrServProfissional"), Restrictions.isNotNull("vlrSadt")))),
				Restrictions.eq("PROCEDIMENTO.exigeValor", false)));

		if (listaProcedimentosAssociacao != null && !listaProcedimentosAssociacao.isEmpty()) {
			cri.add(Restrictions.in("PROCEDIMENTO." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), listaProcedimentosAssociacao));
		}

		cri.addOrder(Order.asc("PROCEDIMENTO.descricao"));

		return executeCriteria(cri, 0, 200, null, false);
	}
	
	public List<FatVlrItemProcedHospComps> pesquisarVlrItemProcedHospCompsPorDthrLiberadaExaSiglaManSeq(
			Date dthrLiberada, Date dataFinalMaisUm, String exaSigla,
			Integer manSeq, AghParametros pTipoGrupoContaSus) {
		final DetachedCriteria cri1 = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "vw");
		cri1.createAlias("vw."+VFatAssociacaoProcedimento.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi");
		cri1.add(Restrictions.eq("vw."+VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), Short.valueOf("1")));
		cri1.add(Restrictions.eq("vw."+VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), Byte.valueOf("2")));
		cri1.add(Restrictions.eq("vw."+VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), pTipoGrupoContaSus.getVlrNumerico().shortValue()));
		cri1.add(Restrictions.eq("phi."+FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		cri1.add(Restrictions.eq("phi."+FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString(), manSeq));
		
		List<VFatAssociacaoProcedimento> list1 = executeCriteria(cri1);
		List<FatVlrItemProcedHospComps> listVlrItem = null;
		for(VFatAssociacaoProcedimento vFat :list1){
			final DetachedCriteria cri2 = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class, "ipc");
			cri2.add(Restrictions.le("ipc."+FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), dthrLiberada));
			cri2.add(Restrictions.or(Restrictions.isNull("ipc."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()), Restrictions.ge("ipc."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), dataFinalMaisUm)));
			cri2.add(Restrictions.eq("ipc."+FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), vFat.getItemProcedimentoHospitalar()));
			listVlrItem = executeCriteria(cri2);
			if(listVlrItem != null && !listVlrItem.isEmpty()){
				return listVlrItem;
			}
		}
		return listVlrItem;
	}
	
	public List<FatVlrItemProcedHospComps> pesquisarGruposPopularProcedimentoHospitalarInterno(final Integer pciSeq, final Date cpeComp, final Short cnvCodigo, final Byte cspSeq,
			final Short tipoGrupoContaSus) {

		// FAT_VLR_ITEM_PROCED_HOSP_COMPS IPC
		DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class, "ipc");

		// FAT_ITENS_PROCED_HOSPITALAR IPH
		criteria.createAlias("ipc." + FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), "iph", DetachedCriteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("iph." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		// FAT_CONV_GRUPO_ITENS_PROCED CGI
		DetachedCriteria subQueryCgi = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "cgi");
		subQueryCgi.setProjection(Projections.property("cgi." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString()));

		// FAT_PROCED_HOSP_INTERNOS PHI
		subQueryCgi.createAlias("cgi." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "phi");

		// Necessário para WHERE phi.pci_seq = c_pci_seq
		subQueryCgi.createAlias("phi." + FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci");
		subQueryCgi.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq)); // WHERE phi.pci_seq = c_pci_seq

		subQueryCgi.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));

		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), tipoGrupoContaSus));

		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));

		subQueryCgi.add(Property.forName("cgi." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString()).eqProperty("iph." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		subQueryCgi.add(Property.forName("cgi." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString()).eqProperty("iph." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));

		criteria.add(Subqueries.exists(subQueryCgi)); // Fim da Subquerie de FAT_CONV_GRUPO_ITENS_PROCED CGI

		criteria.add(Restrictions.le("ipc." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), cpeComp));

		Criterion dtFimCometenciaMaiorIgual = Restrictions.ge("ipc." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), cpeComp);
		Criterion dtFimCometenciaMaiorNula = Restrictions.isNull("ipc." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString());
		criteria.add(Restrictions.or(dtFimCometenciaMaiorIgual, dtFimCometenciaMaiorNula));

		return executeCriteria(criteria);
	}
	
	public List<FatVlrItemProcedHospComps> pesquisarProcedimentosPopularProcedimentoHospitalarInterno(final Integer pciSeq, final Date cpeComp, final Integer phiSeq,
			final Short tipoGrupoContaSus) {
		return pesquisarProcedimentosConvenioPopularProcedimentoHospitalarInterno(pciSeq, cpeComp, null, null, phiSeq, tipoGrupoContaSus);
	}

	public List<FatVlrItemProcedHospComps> pesquisarProcedimentosConvenioPopularProcedimentoHospitalarInterno(final Integer pciSeq, final Date cpeComp, final Short cnvCodigo, final Byte cspSeq, final Integer phiSeq,
			final Short tipoGrupoContaSus) {

		// FAT_VLR_ITEM_PROCED_HOSP_COMPS IPC
		DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class, "ipc");

		// FAT_ITENS_PROCED_HOSPITALAR IPH
		criteria.createAlias("ipc." + FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), "iph", DetachedCriteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("iph." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		// FAT_CONV_GRUPO_ITENS_PROCED CGI
		DetachedCriteria subQueryCgi = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "cgi");
		subQueryCgi.setProjection(Projections.property("cgi." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString()));

		// FAT_PROCED_HOSP_INTERNOS PHI
		subQueryCgi.createAlias("cgi." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "phi");

		// Necessário para WHERE phi.pci_seq = c_pci_seq
		subQueryCgi.createAlias("phi." + FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci");
		subQueryCgi.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq)); // WHERE phi.pci_seq = c_pci_seq
		subQueryCgi.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.SEQ.toString(), phiSeq)); // and cgi.phi_seq = c_phi_seq

		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), tipoGrupoContaSus));
		
		if(cnvCodigo != null && cspSeq != null){ // CONSIDERA CONVENIO SOMENTE QUANDO AMBOS PARÂMETROS FOREM VÁLIDOS
			subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
			subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		}

		subQueryCgi.add(Property.forName("cgi." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString()).eqProperty("iph." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		subQueryCgi.add(Property.forName("cgi." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString()).eqProperty("iph." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));

		criteria.add(Subqueries.exists(subQueryCgi)); // Fim da Subquerie de FAT_CONV_GRUPO_ITENS_PROCED CGI

		criteria.add(Restrictions.le("ipc." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), cpeComp));

		Criterion dtFimCometenciaMaiorIgual = Restrictions.ge("ipc." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), cpeComp);
		Criterion dtFimCometenciaMaiorNula = Restrictions.isNull("ipc." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString());
		criteria.add(Restrictions.or(dtFimCometenciaMaiorIgual, dtFimCometenciaMaiorNula));

		return executeCriteria(criteria);
	}
	
	public List<FatVlrItemProcedHospComps> pesquisarVlrItemProcedHospCompsPorIphSeqDtComp(Integer iphSeq, Short iphPhoSeq, Date valorDtComp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatVlrItemProcedHospComps.class, "IPC");
		criteria.createAlias("IPC." + FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), "IPH");
		
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		Criterion dtIniCompMenorIgual = Restrictions.le("IPC." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), valorDtComp);
		Criterion dtIniCompMenorIgualDtAtual = Restrictions.le("IPC." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), new Date());
		criteria.add(Restrictions.or(dtIniCompMenorIgual, dtIniCompMenorIgualDtAtual));
		
		Criterion dtFimCompMaiorIgual = Restrictions.ge("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), valorDtComp);
		Criterion dtFimCompMaiorNula = Restrictions.isNull("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString());
		criteria.add(Restrictions.or(dtFimCompMaiorIgual, dtFimCompMaiorNula));
		
		return executeCriteria(criteria);
	}
	/**
	 * #36436 C8
	 * Método para obter lista de Dados OPM
	 */
	public List<ProcedimentoRealizadoDadosOPMVO> obterListaDadosOPM(Integer cthSeq){
		FatVlrItemProcHospCompsObterListaDadosOPMQueryBuilder builder = new FatVlrItemProcHospCompsObterListaDadosOPMQueryBuilder();
		return executeCriteria(builder.build(cthSeq, isOracle()));
	}
	
	/**
	 * #36436 C9
	 */
	public List<ValoresPreviaVO> obterValoresPreviaVO(Integer cthSeq, AghParametros faturamentoPadrao){
	
		ConsultaListaValoresPrevioQueryBuilder creator = new ConsultaListaValoresPrevioQueryBuilder(cthSeq, isOracle());
		
		Query query = createSQLQuery(creator.obterConsulta());
		
		query.setString("indSituacao", DominioSituacao.A.name());
		query.setShort("parametroSistema", Short.valueOf(faturamentoPadrao.getVlrNumerico().toString()));
		query.setInteger("cthSeq", cthSeq);
	
		List<ValoresPreviaVO> listaRetorno = creator.prepararListaValoresPreviaVO(query.list());
		
		return listaRetorno;
	}
}
