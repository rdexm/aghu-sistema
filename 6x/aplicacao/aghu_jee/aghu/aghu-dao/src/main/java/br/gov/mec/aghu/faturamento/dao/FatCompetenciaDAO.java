package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.vo.FaturaAmbulatorioVO;
import br.gov.mec.aghu.model.FatArqEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.core.utils.DateUtil;


public class FatCompetenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCompetencia> {
	
	private static final long serialVersionUID = -3907422276948076573L;
	
	protected DetachedCriteria obterCriteriaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia modulo) {
		
		final DetachedCriteria result = DetachedCriteria.forClass(FatCompetencia.class);
		
		result.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		result.add(Restrictions.isNull(FatCompetencia.Fields.DT_HR_FIM.toString()));
		
		return result;		
	}
	
	public List<FatCompetencia> obterCompetenciasPorModuloESituacoes(final DominioModuloCompetencia modulo, final DominioSituacaoCompetencia... situacoes ) {
		return obterCompetenciasPorModuloESituacoes(modulo, null, situacoes);
	}
	
	public List<FatCompetencia> obterCompetenciasPorModuloESituacoes(final DominioModuloCompetencia modulo, final Order[] ordens, final DominioSituacaoCompetencia... situacoes ) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.in(FatCompetencia.Fields.IND_SITUACAO.toString(), situacoes));
		
		if(ordens != null){
			for (Order ordem : ordens) {
				criteria.addOrder(ordem);
			}
		}

		return this.executeCriteria(criteria);
	}
	
	public List<FatCompetencia> obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia modulo) {
		return this.executeCriteria(this.obterCriteriaCompetenciaSemDataFimPorModulo(modulo));
	}
	
	protected DetachedCriteria obterCriteriaAtivaPorModulo(DominioModuloCompetencia modulo) {
		final DetachedCriteria result = DetachedCriteria.forClass(FatCompetencia.class);
		
		result.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		result.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.A));
		
		return result;		
	}
	
	public List<FatCompetencia> obterListaAtivaPorModulo(final DominioModuloCompetencia modulo) {
		return this.executeCriteria(this.obterCriteriaAtivaPorModulo(modulo));
	}
	
	public List<FatCompetencia> obterListaAtivaAbertaNaoFaturadaPorModulo(final DominioModuloCompetencia modulo) {
		
		final DetachedCriteria result = this.obterCriteriaAtivaPorModulo(modulo);
		result.add(Restrictions.eq(FatCompetencia.Fields.IND_FATURADO.toString(), Boolean.FALSE));
		
		return executeCriteria(result);		
	}	
	
	public List<FatCompetencia> listarFatCompetencia( final FatCompetencia competencia, final Integer firstResult,  
													  final Integer maxResult, final String orderProperty, final boolean asc) {
		return executeCriteria(getCriteriaListarCompetencia(competencia),firstResult,maxResult,orderProperty,asc);
	}
	
	public Long listarFatCompetenciaCount(final FatCompetencia competencia) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		
		
		if(competencia.getId().getModulo() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), competencia.getId().getModulo()));
		}
		
		if(competencia.getId().getMes() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), competencia.getId().getMes()));
		}
		
		if(competencia.getId().getAno() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), competencia.getId().getAno()));
		}
		
		if(competencia.getId().getDtHrInicio() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_INICIO.toString(), competencia.getId().getDtHrInicio()));
		}
		
		if(competencia.getDtHrFim() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_FIM.toString(), competencia.getDtHrFim()));
		}
		
		if(competencia.getDthrLiberadoEmerg() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_LIBERADO_EMERG.toString(), competencia.getDthrLiberadoEmerg()));
		}
		
		if(competencia.getDthrLiberadoCo() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_LIBERADO_CO.toString(), competencia.getDthrLiberadoCo()));
		}
		
		if(competencia.getIndFaturado() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_FATURADO.toString(), competencia.getIndFaturado()));
		}
		
		if(competencia.getIndSituacao() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), competencia.getIndSituacao()));
		}
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaListarCompetencia(final FatCompetencia competencia) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		criteria.addOrder(Order.asc(FatCompetencia.Fields.MODULO.toString()));
		criteria.addOrder(Order.desc(FatCompetencia.Fields.ANO.toString()));
		criteria.addOrder(Order.desc(FatCompetencia.Fields.MES.toString()));
		criteria.addOrder(Order.desc(FatCompetencia.Fields.DT_HR_INICIO.toString()));
		
		
		if(competencia.getId().getModulo() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), competencia.getId().getModulo()));
		}
		
		if(competencia.getId().getMes() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), competencia.getId().getMes()));
		}
		
		if(competencia.getId().getAno() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), competencia.getId().getAno()));
		}
		
		if(competencia.getId().getDtHrInicio() != null){
			criteria.add(Restrictions.ge(FatCompetencia.Fields.DT_HR_INICIO.toString(), competencia.getId().getDtHrInicio()));
			criteria.add(Restrictions.le(FatCompetencia.Fields.DT_HR_INICIO.toString(), DateUtil.obterDataComHoraFinal(competencia.getId().getDtHrInicio())));
		}
		
		if(competencia.getDtHrFim() != null){
			criteria.add(Restrictions.ge(FatCompetencia.Fields.DT_HR_FIM.toString(), competencia.getDtHrFim()));
			criteria.add(Restrictions.le(FatCompetencia.Fields.DT_HR_FIM.toString(), DateUtil.obterDataComHoraFinal(competencia.getDtHrFim())));
		}
		
		if(competencia.getDthrLiberadoEmerg() != null){
			criteria.add(Restrictions.ge(FatCompetencia.Fields.DT_HR_LIBERADO_EMERG.toString(), competencia.getDthrLiberadoEmerg()));
			criteria.add(Restrictions.le(FatCompetencia.Fields.DT_HR_LIBERADO_EMERG.toString(), DateUtil.obterDataComHoraFinal(competencia.getDthrLiberadoEmerg())));
		}
		
		if(competencia.getDthrLiberadoCo() != null){
			criteria.add(Restrictions.ge(FatCompetencia.Fields.DT_HR_LIBERADO_CO.toString(), competencia.getDthrLiberadoCo()));
			criteria.add(Restrictions.le(FatCompetencia.Fields.DT_HR_LIBERADO_CO.toString(), DateUtil.obterDataComHoraFinal(competencia.getDthrLiberadoCo())));
		}
		
		if(competencia.getIndFaturado() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_FATURADO.toString(), competencia.getIndFaturado()));
		}
		
		if(competencia.getIndSituacao() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), competencia.getIndSituacao()));
		}
		return criteria;
	}
	

	public FatCompetencia buscarCompetenciasDataHoraFimNula(final DominioModuloCompetencia modulo, final DominioSituacaoCompetencia situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), situacao));

		criteria.add(Restrictions.isNull(FatCompetencia.Fields.DT_HR_FIM.toString()));

		List<FatCompetencia> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	public FatCompetencia obterCompetenciaModuloMesAno(final DominioModuloCompetencia modulo, final Integer mes, final Integer ano) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), mes));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), ano));

		return (FatCompetencia) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria criarCriteriaCompetenciaModuloMesAnoDtHoraInicioSemHora(final DominioModuloCompetencia modulo, final Integer mes, final Integer ano, final Date dtHoraInicio){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), mes));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), ano));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_INICIO.toString(), dtHoraInicio));
		return criteria;
	}
	
	public FatCompetencia obterCompetenciaModuloMesAnoDtHoraInicioSemHora(final DominioModuloCompetencia modulo, final Integer mes, final Integer ano, final Date dtHoraInicio){
		return (FatCompetencia) executeCriteriaUniqueResult(criarCriteriaCompetenciaModuloMesAnoDtHoraInicioSemHora(modulo, mes, ano, dtHoraInicio));
	}

	private DetachedCriteria criarCriteriaCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao(final DominioModuloCompetencia modulo, final Integer mes, final Integer ano, final Date dtHoraInicio){
		final DetachedCriteria criteria = criarCriteriaCompetenciaModuloMesAnoDtHoraInicioSemHora(modulo, mes, ano, dtHoraInicio);
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.M));
		criteria.add(Restrictions.isNotNull(FatCompetencia.Fields.DT_HR_FIM.toString()));
		
		return criteria;
	}
	
	public List<FatCompetencia> obterCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao(final DominioModuloCompetencia modulo, final Integer mes, final Integer ano, final Date dtHoraInicio){
		return executeCriteria(criarCriteriaCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao(modulo, mes, ano, dtHoraInicio));
	}

	
	public Long listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(final FatCompetenciaId id){
		return executeCriteriaCount(getCriteriaListarCompetenciaModuloMesAnoDtHoraInicioSemHora(id, true));
	}
	
	public List<FatCompetencia> listarCompetenciaModuloMesAnoDtHoraInicioSemHora(final FatCompetenciaId id){
		return executeCriteria(getCriteriaListarCompetenciaModuloMesAnoDtHoraInicioSemHora(id, false));
	}

	private DetachedCriteria getCriteriaListarCompetenciaModuloMesAnoDtHoraInicioSemHora(
			final FatCompetenciaId id, final Boolean isCount) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), (id.getModulo() != null) ? id.getModulo() : DominioModuloCompetencia.INT));
		
		if(id.getMes() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), id.getMes()));
		}
		
		if(id.getAno() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), id.getAno()));
		}
		
		if(id.getDtHrInicio() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_INICIO.toString(), id.getDtHrInicio()));
		}
		
		if(!isCount){
			criteria.addOrder(Order.desc(FatCompetencia.Fields.ANO.toString()));
			criteria.addOrder(Order.desc(FatCompetencia.Fields.MES.toString()));
		}
		return criteria;
	}	

	//#2280 C1
	 public List<FatCompetencia> listarCompetenciaModuloMesAnoDtHoraInicioComHora(final FatCompetenciaId id) {
		final DetachedCriteria criteria = criarCriteriaCompetencia(id);
		criteria.addOrder(Order.desc(FatCompetencia.Fields.ANO.toString()));
		criteria.addOrder(Order.desc(FatCompetencia.Fields.MES.toString()));
		criteria.addOrder(Order.desc(FatCompetencia.Fields.DT_HR_INICIO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false); 
	}

	private DetachedCriteria criarCriteriaCompetencia(final FatCompetenciaId id) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), DominioModuloCompetencia.INT));
		
		if(id.getMes() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), id.getMes()));
		}
		
		if(id.getAno() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), id.getAno()));
		}
		
		if(id.getDtHrInicio() != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.DT_HR_INICIO.toString(), id.getDtHrInicio()));
		}
		return criteria;
	}
	
	
	
	public FatCompetencia buscarPrimeiraCompetenciasOrdenadoPorDataHoraFimDataHoraInicio(final DominioModuloCompetencia modulo, final Boolean faturado, final DominioSituacaoCompetencia situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_FATURADO.toString(), faturado));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), situacao));
		criteria.addOrder(Order.asc(FatCompetencia.Fields.DT_HR_FIM.toString()));
		criteria.addOrder(Order.asc(FatCompetencia.Fields.DT_HR_INICIO.toString()));
		
		List<FatCompetencia> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	public Object[] listarDataInicioEFimCompModAMB(){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FatCompetencia.Fields.DT_HR_INICIO.toString()))
				.add(Projections.property(FatCompetencia.Fields.DT_HR_INICIO.toString())));

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), DominioModuloCompetencia.AMB));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.A));

		final Object[] obj = (Object[])executeCriteriaUniqueResult(criteria);
		
		if(obj[0] != null && obj[1] != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) obj[0]);
			cal.add(Calendar.DATE, -(1*((1/24)/60)));
			obj[1] = cal.getTime();
		}
		
		return obj;
	}

	public List<FatCompetencia> obterCompetenciasPorModuloESituacaoEFaturado(final DominioModuloCompetencia modulo, final DominioSituacaoCompetencia situacao, final Boolean faturado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_FATURADO.toString(), faturado));

		return this.executeCriteria(criteria);
	}
	
	public List<FatCompetencia> obterCompetenciasPorModuloESituacaoEFaturadoEMesEAno(final DominioModuloCompetencia modulo, 
			final DominioSituacaoCompetencia situacao, final Boolean faturado, final String order, final Boolean asc,
			Integer mes, Integer ano) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		if (modulo != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		}
		if (situacao != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (faturado != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_FATURADO.toString(), faturado));
		}
		if (mes != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), mes));
		}
		if (ano != null){
			criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), ano));
		}
		if (order != null && !order.isEmpty()) {
			if (asc){
				criteria.addOrder(Order.asc(order));
			} else {
				criteria.addOrder(Order.desc(order));
			}
		}

		return this.executeCriteria(criteria);
	}
	
	public List<FatCompetencia> pesquisarCompetenciasPorModulo(DominioModuloCompetencia modulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));

		return this.executeCriteria(criteria);
	}
	
	public List<FatCompetencia> listarCompetenciasPorModulosESituacao(final DominioSituacaoCompetencia situacao, final DominioModuloCompetencia... modulos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);

		criteria.add(Restrictions.in(FatCompetencia.Fields.MODULO.toString(), modulos));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), situacao));

		return this.executeCriteria(criteria);
	}
	
	public List<FaturaAmbulatorioVO> listarFaturamentoAmbulatorioPorCompetencia(final DominioModuloCompetencia modulo, final Integer mes, 
			final Integer ano, final Date dtHoraInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class, "CPE");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlGroupProjection(
						(isOracle()?"LAST_DAY(TO_DATE('01'||TO_CHAR({alias}.MES)||TO_CHAR({alias}.ANO,'0000'),'DDMMYYYY')) as dtFim"
							:"(DATE_TRUNC('MONTH', TO_DATE('01'||TO_CHAR({alias}.MES, '00')||TO_CHAR({alias}.ANO,'0000'),'DDMMYYYY')) + INTERVAL '1 MONTH - 1 day') as dtFim"),
						(isOracle()?"LAST_DAY(TO_DATE('01'||TO_CHAR({alias}.MES)||TO_CHAR({alias}.ANO,'0000'),'DDMMYYYY'))"
								:"(DATE_TRUNC('MONTH', TO_DATE('01'||TO_CHAR({alias}.MES, '00')||TO_CHAR({alias}.ANO,'0000'),'DDMMYYYY')) + INTERVAL '1 MONTH - 1 day')"),
					    new String[]{"dtFim"}, 
					    new Type[] {DateType.INSTANCE}), "dtFim")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.ID_GRP_SEQ.toString()), "grupoSeq")
				.add(Projections.groupProperty("GRP."+FatGrupo.Fields.DESCRICAO.toString()), "grupo")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.ID_SUB_GRUPO.toString()), "subGrupoSeq")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.DESCRICAO.toString()), "subGrupo")
				.add(Projections.groupProperty("FOG."+FatFormaOrganizacao.Fields.ID_CODIGO.toString()), "formaOrganizacaoCodigo")
				.add(Projections.groupProperty("FOG."+FatFormaOrganizacao.Fields.DESCRICAO.toString()), "formaOrganizacao")
				.add(Projections.groupProperty("AEA."+FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()), "procedimentoHospitalar")
				.add(Projections.groupProperty("IPH."+FatItensProcedHospitalar.Fields.SEQ.toString()), "iphSeq")
				.add(Projections.groupProperty("IPH."+FatItensProcedHospitalar.Fields.PHO_SEQ.toString()), "iphPhoSeq")
				.add(Projections.groupProperty("IPH."+FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO_SEQ.toString()), "caracteristicaFinanciamentoSeq")
				.add(Projections.groupProperty("FIN."+FatCaractFinanciamento.Fields.DESCRICAO.toString()), "caracteristicaFinanciamento")
				.add(Projections.sum("AEA."+FatArqEspelhoProcedAmb.Fields.VLR_PROCED.toString()), "valorProcedimento")
				.add(Projections.sum("AEA."+FatArqEspelhoProcedAmb.Fields.QUANTIDADE.toString()), "quantidade")
		);

		
		criteria.createAlias("CPE."+FatCompetencia.Fields.FAT_ARQ_ESPELHO_PROCED_AMB.toString(), "AEA", Criteria.INNER_JOIN);
		criteria.createAlias("AEA."+FatArqEspelhoProcedAmb.Fields.IPH.toString(), "IPH", Criteria.INNER_JOIN);
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO.toString(), "FOG", Criteria.INNER_JOIN);
		criteria.createAlias("FOG."+FatFormaOrganizacao.Fields.FAT_SUB_GRUPO.toString(), "SGR", Criteria.INNER_JOIN);
		criteria.createAlias("SGR."+FatSubGrupo.Fields.FAT_GRUPO.toString(), "GRP", Criteria.INNER_JOIN);
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO.toString(), "FIN", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("CPE."+FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq("CPE."+FatCompetencia.Fields.MES.toString(), mes));
		criteria.add(Restrictions.eq("CPE."+FatCompetencia.Fields.ANO.toString(), ano));
		criteria.add(Restrictions.eq("CPE."+FatCompetencia.Fields.DT_HR_INICIO.toString(), dtHoraInicio));
		
		criteria.addOrder(Order.asc("IPH."+FatItensProcedHospitalar.Fields.FAT_CARACTERISTICA_FINANCIAMENTO_SEQ.toString()));
		criteria.addOrder(Order.asc("AEA."+FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()));
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(FaturaAmbulatorioVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<FatCompetencia> pesquisarCompetenciaProcedimentoHospitalarInternoPorModulo(DominioModuloCompetencia modulo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.A));
		criteria.add(Restrictions.isNull(FatCompetencia.Fields.DT_HR_FIM.toString()));
		return this.executeCriteria(criteria);
	}
	/**
	 * #2199 - C1
	 * @param modulo
	 * @return
	 */
	public Date obterDataInicioPorCompetencia(Integer mes, Integer ano) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetencia.class);
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MODULO.toString(), DominioModuloCompetencia.INT));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.ANO.toString(), ano));
		criteria.add(Restrictions.eq(FatCompetencia.Fields.MES.toString(), mes));
		criteria.setProjection(Projections.projectionList().add(Projections.property(FatCompetencia.Fields.DT_HR_INICIO.toString())));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public Long listarCompetenciaModuloMesAnoDtHoraInicioComHoraCount(FatCompetenciaId id) {
		final DetachedCriteria criteria = criarCriteriaCompetencia(id);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Consulta para o Suggestion Box da estoria #2600
	 * 
	 * @param id
	 * @return
	 */
	public List<FatCompetencia> listarCompetenciaModuloParaSuggestionBox(FatCompetenciaId id) {
		return executeCriteria(getCriteriaListarCompetenciaModuloMesAnoDtHoraInicioSemHora(id, false), 0, 100, null);
	}

	/**
	 * Consulta count para o Suggestion Box da estoria #2600
	 * 
	 * @param id
	 * @return
	 */
	public Long listarCompetenciaModuloParaSuggestionBoxCount(FatCompetenciaId id) {
		return executeCriteriaCount(getCriteriaListarCompetenciaModuloMesAnoDtHoraInicioSemHora(id, true));
	}
}

