package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatSinonimoItemProcedHosp;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatSinonimoItemProcedHospDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatSinonimoItemProcedHosp> {

	private static final long serialVersionUID = -1642290362119563841L;

	public List<Long> obterListaCodTabelaFatSsm(Long codTabela, Integer idade, DominioSexoDeterminante sexo, Short paramTabelaFaturPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_SINONIMO_PROCED_HOSPITALAR.toString(), "IPS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC");
		
		criteria.setProjection(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), paramTabelaFaturPadrao));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.TRUE),
				Restrictions.and(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString()),
				Restrictions.or(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()),
				Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SADT.toString())))),
				Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.FALSE)));
		
		criteria.add(Restrictions.le("IPC." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), new Date()));
		criteria.add(Restrictions.or(
				Restrictions.isNull("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()),
				Restrictions.ge("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), new Date())));
		criteria.add(Restrictions.in(
				"IPH." + FatItensProcedHospitalar.Fields.SEXO.toString(), new DominioSexoDeterminante[]{DominioSexoDeterminante.Q, sexo}));
		criteria.add(Restrictions.sqlRestriction("? " + " BETWEEN {alias}.IDADE_MIN and {alias}.IDADE_MAX ",
				new Object[]{idade}, new Type[]{IntegerType.INSTANCE}));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		
		return executeCriteria(criteria);
	}
	
	public List<VFatSsmInternacaoVO> buscarVFatSsmInternacaoPorIphPho(Short phoSeq, Integer iphSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_SINONIMO_PROCED_HOSPITALAR.toString(), "IPS", JoinType.LEFT_OUTER_JOIN);
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("COALESCE(ips1_.DESCRICAO, this_.DESCRICAO) as DESCRICAO");
		
		criteria.setProjection(Projections.projectionList()
				
			.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()),
				VFatSsmInternacaoVO.Fields.COD_TABELA.toString())
			.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()),
						VFatSsmInternacaoVO.Fields.PHO_SEQ.toString())
			.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString()),
								VFatSsmInternacaoVO.Fields.IPH_SEQ.toString())
			.add(Projections.sqlProjection(sqlProjection.toString(), 
						new String[]{FatSinonimoItemProcedHosp.Fields.DESCRICAO.toString()}, 
									new Type[] { StringType.INSTANCE }), VFatSsmInternacaoVO.Fields.DESC_SINONIMO.toString()));
		
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatSsmInternacaoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<VFatSsmInternacaoVO> obterListaVFatSssInternacao(Object strPesquisa, Integer idade, DominioSexoDeterminante sexo, 
			Short paramTabelaFaturPadrao, Integer cidSeq, Integer caracteristica) {
		DetachedCriteria criteria = this.montarCriteriaListaVFatSssInternacao(strPesquisa, idade, sexo, paramTabelaFaturPadrao, cidSeq, caracteristica);
		
		criteria.addOrder(Order.asc(FatSinonimoItemProcedHosp.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long obterListaVFatSssInternacaoCount(Object strPesquisa, Integer idade, DominioSexoDeterminante sexo, 
			Short paramTabelaFaturPadrao, Integer cidSeq, Integer caracteristica) {
		DetachedCriteria criteria = this.montarCriteriaListaVFatSssInternacao(strPesquisa, idade, sexo, paramTabelaFaturPadrao, cidSeq, caracteristica);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaListaVFatSssInternacao(Object strPesquisa, Integer idade, DominioSexoDeterminante sexo, 
			Short paramTabelaFaturPadrao, Integer cidSeq, Integer caracteristica) {
		
		String stParametro = (String) strPesquisa;
		Long codTabela = null;
		
		if (CoreUtil.isNumeroLong(stParametro)) {
			codTabela = Long.valueOf(stParametro);
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "IPH");
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.FAT_SINONIMO_PROCED_HOSPITALAR.toString(), "IPS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPH." + FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC");
		
		if (codTabela != null) {
			criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		} else if (!stParametro.isEmpty()) {
			criteria.add(Restrictions.or(Restrictions.ilike("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE), 
					Restrictions.ilike("IPS." + FatSinonimoItemProcedHosp.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE)));
		}
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("COALESCE(ips1_.DESCRICAO, this_.DESCRICAO) as DESCRICAO");
		
		criteria.setProjection(Projections.projectionList()
				
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()),
				VFatSsmInternacaoVO.Fields.COD_TABELA.toString())
				
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()),
						VFatSsmInternacaoVO.Fields.PHO_SEQ.toString())
				
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString()),
								VFatSsmInternacaoVO.Fields.IPH_SEQ.toString())
				
				.add(Projections.sqlProjection(sqlProjection.toString(), 
						new String[]{FatSinonimoItemProcedHosp.Fields.DESCRICAO.toString()}, 
									new Type[] { StringType.INSTANCE }), VFatSsmInternacaoVO.Fields.DESC_SINONIMO.toString()));
		
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), paramTabelaFaturPadrao));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.TRUE),
				Restrictions.and(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString()),
				Restrictions.or(Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString()),
				Restrictions.isNotNull("IPC." + FatVlrItemProcedHospComps.Fields.VLR_SADT.toString())))),
				Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_VALOR.toString(), Boolean.FALSE)));
		
		criteria.add(Restrictions.le("IPC." + FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), new Date()));
		criteria.add(Restrictions.or(
				Restrictions.isNull("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()),
				Restrictions.ge("IPC." + FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), new Date())));
		criteria.add(Restrictions.in(
				"IPH." + FatItensProcedHospitalar.Fields.SEXO.toString(), new DominioSexoDeterminante[]{DominioSexoDeterminante.Q, sexo}));
		criteria.add(Restrictions.and(Restrictions.le("IPH." + FatItensProcedHospitalar.Fields.IDADE_MIN.toString(), idade), 
				Restrictions.ge("IPH." + FatItensProcedHospitalar.Fields.IDADE_MAX.toString(), idade)));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAPR");
		subQuery.setProjection(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()));
		subQuery.add(Restrictions.eqProperty("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), 
				"IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));
		subQuery.add(Restrictions.eqProperty("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString(), 
				"IPH." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		
		//subQuery fatCaractItemProcHosp
		DetachedCriteria subQueryCaractItemProcHops = DetachedCriteria.forClass(FatCaractItemProcHosp.class, "FAT");
		subQueryCaractItemProcHops.setProjection(Projections.property("FAT." + FatCaractItemProcHosp.Fields.VALOR_NUMERICO.toString()));
		subQueryCaractItemProcHops.add(Restrictions.eqProperty("FAT." + FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(),
				"IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));
		subQueryCaractItemProcHops.add(Restrictions.eqProperty("FAT." + FatCaractItemProcHosp.Fields.IPH_SEQ.toString(),
				"IPH." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		subQueryCaractItemProcHops.add(Restrictions.eq("FAT." + FatCaractItemProcHosp.Fields.SEQ_TIPO_CARACTERISTICA_ITEM.toString(), caracteristica));
		
		DetachedCriteria subQueryTpc = DetachedCriteria.forClass(FatProcedHospIntCid.class, "TPC");
		subQueryTpc.setProjection(Projections.property("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString()));
		subQueryTpc.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(), 
				"VAPR." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()));
		
		
		
		if (cidSeq != null) {
			subQueryTpc.add(Restrictions.eq("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString(), cidSeq));
		}
		

		
		subQuery.add(Subqueries.exists(subQueryTpc));
		
		criteria.add(Subqueries.exists(subQuery));
		
		criteria.add(Subqueries.notExists(subQueryCaractItemProcHops));
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatSsmInternacaoVO.class));

		return criteria;
	}
	
}
