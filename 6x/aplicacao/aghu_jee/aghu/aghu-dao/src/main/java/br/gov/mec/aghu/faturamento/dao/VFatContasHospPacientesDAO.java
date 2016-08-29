package br.gov.mec.aghu.faturamento.dao;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarInformarSolicitadoVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.model.VFatContasHospPacientes;

public class VFatContasHospPacientesDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<VFatContasHospPacientes> {

	private static final long serialVersionUID = 4702880961383390826L;
	
	private DetachedCriteria createCriteria(Integer pacProntuario,
			Long cthNroAih, Integer pacCodigo, Integer cthSeq,
			DominioSituacaoConta[] situacoes, Byte seqTipoAih) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(VFatContasHospPacientes.class);

		if (pacProntuario != null) {
			criteria.add(Restrictions.eq(
					VFatContasHospPacientes.Fields.PAC_PRONTUARIO.toString(),
					pacProntuario));
		}
		if (cthNroAih != null) {
			criteria.add(Restrictions.eq(
					VFatContasHospPacientes.Fields.CTH_NRO_AIH.toString(),
					cthNroAih));
		}
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(
					VFatContasHospPacientes.Fields.PAC_CODIGO.toString(),
					pacCodigo));
		}
		if (cthSeq != null) {
			criteria.add(Restrictions.eq(VFatContasHospPacientes.Fields.CTH_SEQ
					.toString(), cthSeq));
		}

		if (situacoes != null) {
			criteria.add(Restrictions.in(
					VFatContasHospPacientes.Fields.CTH_IND_SITUACAO.toString(),
					situacoes));
		}
		
		criteria.add(Restrictions.or(
				Restrictions.ne(VFatContasHospPacientes.Fields.CTH_TAH_SEQ.toString(), seqTipoAih),
				Restrictions.and(
						Restrictions.eq(VFatContasHospPacientes.Fields.CTH_TAH_SEQ.toString(), seqTipoAih), 
						Restrictions.isNull(VFatContasHospPacientes.Fields.CTH_CTH_SEQ.toString())
						)));
		
		DetachedCriteria subCriteria = DetachedCriteria
				.forClass(FatConvenioSaude.class);
		
		subCriteria.add(Restrictions.eq(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		
		subCriteria.setProjection(Projections.property(FatConvenioSaude.Fields.CODIGO.toString()));
		
		criteria.add(Subqueries.propertyIn(VFatContasHospPacientes.Fields.CTH_CSP_CNV_CODIGO.toString(), subCriteria));
		
		return criteria;
	}

	public List<ContaHospitalarInformarSolicitadoVO> pesquisarContaHospitalarInformarSolicitadoVO(Integer pacProntuario,
			Long cthNroAih, Integer pacCodigo, Integer cthSeq, DominioSituacaoConta[] situacoes, Byte seqTipoAih, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes, seqTipoAih);

		criteria.createAlias(VFatContasHospPacientes.Fields.INTERNACAO.toString(),
				VFatContasHospPacientes.Fields.INTERNACAO.toString(), Criteria.LEFT_JOIN);

		criteria.setProjection(Projections
				.projectionList()
				.add(Property.forName(VFatContasHospPacientes.Fields.PAC_PRONTUARIO.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.PAC_PRONTUARIO.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.CTH_SEQ.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.CTH_SEQ.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.INT_SEQ.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.INT_SEQ.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.PAC_CODIGO.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.PAC_CODIGO.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.PAC_NOME.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.PAC_NOME.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.CTH_NRO_AIH.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.CTH_NRO_AIH.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.INT_LTO_ID.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.INT_LTO_ID.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.CTH_IND_SITUACAO.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.CTH_IND_SITUACAO.toString())
				.add(Property.forName(VFatContasHospPacientes.Fields.DTHR_INTERNACAO.toString()),
						ContaHospitalarInformarSolicitadoVO.Fields.DTHR_INTERNACAO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ContaHospitalarInformarSolicitadoVO.class));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarContaHospitalarInformarSolicitadoVOCount(Integer pacProntuario, Long cthNroAih, Integer pacCodigo, Integer cthSeq,
			DominioSituacaoConta[] situacoes, Byte seqTipoAih) {
		DetachedCriteria criteria = this.createCriteria(pacProntuario, cthNroAih, pacCodigo, cthSeq, situacoes, seqTipoAih);
		
		return executeCriteriaCount(criteria);
	}

	public List<VFatContasHospPacientes> listarVFatContasHospPacientesPorPacCodigo(Integer pacCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.createCriteriaPorPacCodigo(pacCodigo);
		
		criteria.addOrder(Order.desc(VFatContasHospPacientes.Fields.CTH_DT_INT_ADMINISTRATIVA.toString()));
		
		criteria.addOrder(Order.desc(VFatContasHospPacientes.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()));
		
		criteria.addOrder(Order.desc(VFatContasHospPacientes.Fields.CTH_SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);

	}

	public Long listarVFatContasHospPacientesPorPacCodigoCount(Integer pacCodigo) {
		DetachedCriteria criteria = this.createCriteriaPorPacCodigo(pacCodigo);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createCriteriaPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContasHospPacientes.class);

		criteria.add(Restrictions.eq(VFatContasHospPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return criteria;
	}

	public List<Integer> pesquisarContasParaValidarRegrasFatDadosContaSemInt(final Integer pacCodigo, final Integer pSeq, final Date dtInicial, Date dtFinal){
		if(dtFinal == null){
			dtFinal = new Date();
		}
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(VFatContasHospPacientes.class);
		criteria.setProjection(Projections.property(VFatContaHospitalarPac.Fields.CTH_SEQ.toString()));
		criteria.add(Restrictions.eq(VFatContaHospitalarPac.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(
		  			Restrictions.or(
		  							Restrictions.and(
		  											Restrictions.le(VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA.toString(), dtInicial), 
		  											Restrictions.or(
		  															Restrictions.and(
		  																			Restrictions.isNotNull(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()),
		  																			Restrictions.ge(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(), dtInicial)
		  															),
		  															Restrictions.and(
		  																			Restrictions.isNull(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()),
		  																			Restrictions.sqlRestriction(" ? <= ?", new Object[]{dtInicial, new Date()}, new Type[]{TimestampType.INSTANCE, TimestampType.INSTANCE})
		  															)
		  											)		  															
		  							),
		  							Restrictions.and(
		  											Restrictions.le(VFatContaHospitalarPac.Fields.CTH_DT_INT_ADMINISTRATIVA.toString(), dtFinal), 
		  											Restrictions.or(
		  															Restrictions.and(
		  																			Restrictions.isNotNull(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()),
		  																			Restrictions.ge(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString(), dtFinal)
		  															),
		  															Restrictions.and(
		  																			Restrictions.isNull(VFatContaHospitalarPac.Fields.CTH_DT_ALTA_ADMINISTRATIVA.toString()),
		  																			Restrictions.sqlRestriction(" ? <= ?", new Object[]{dtFinal, new Date()}, new Type[]{TimestampType.INSTANCE, TimestampType.INSTANCE})
		  															)
		  											)		  															
		  							)
		  			)

		);
		criteria.add(Restrictions.ne(VFatContasHospPacientes.Fields.CTH_IND_SITUACAO.toString(), DominioSituacaoConta.C));
		criteria.add(Restrictions.ne(VFatContasHospPacientes.Fields.DCS_SEQ.toString(), pSeq));
		
		
		return executeCriteria(criteria);
	}
	
	
	
	public List<VFatContasHospPacientes> listarContasPorPacCodigoDthrRealizadoESituacaoCth(Integer pacCodigo, Date dthrRealizado, DominioSituacaoConta[] cthIndSituacao, String colunaOrder, Boolean order){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContasHospPacientes.class);
		
		//O between foi implementado com "or" devido ao NVL.
		//AND V_DTHR_REALIZADO BETWEEN CTH_DT_INT_ADMINISTRATIVA AND NVL(CTH_DT_ALTA_ADMINISTRATIVA,SYSDATE)

		criteria.createAlias(VFatContasHospPacientes.Fields.PACIENTE.toString(), "pac");
		
		criteria.add(Restrictions.eq("pac."+ AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		
		criteria.add(Restrictions.or(
				Restrictions.sqlRestriction(" ? between CTH_DT_INT_ADMINISTRATIVA and CTH_DT_ALTA_ADMINISTRATIVA  ",  dthrRealizado , TimestampType.INSTANCE), 
				Restrictions.sqlRestriction(" ? between CTH_DT_INT_ADMINISTRATIVA and ? ",  new Object[]{dthrRealizado, new Date()} , new Type[] { TimestampType.INSTANCE, TimestampType.INSTANCE})
				));
		
		criteria.add(Restrictions.in(VFatContasHospPacientes.Fields.CTH_IND_SITUACAO.toString(), cthIndSituacao));
		
		if(StringUtils.isNotBlank(colunaOrder) && order != null){
			if(order){
				criteria.addOrder(Order.asc(colunaOrder));
			}else{
				criteria.addOrder(Order.desc(colunaOrder));
			}
		}
		
		return executeCriteria(criteria);
	}

	public VFatContasHospPacientes obterVFatContasHospPacientes(Integer cthSeq, BigDecimal intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatContasHospPacientes.class);
		criteria.createAlias(
				VFatContasHospPacientes.Fields.PACIENTE.toString(),
				VFatContasHospPacientes.Fields.PACIENTE.toString(),
				JoinType.INNER_JOIN);
		
		criteria.createAlias(
				VFatContasHospPacientes.Fields.CONTA_HOSPITALAR.toString(),
				VFatContasHospPacientes.Fields.CONTA_HOSPITALAR.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				VFatContasHospPacientes.Fields.AIH.toString(),
				VFatContasHospPacientes.Fields.AIH.toString(),
				JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(VFatContasHospPacientes.Fields.AIH.toString()
				+ "." + FatAih.Fields.SERVIDOR.toString(),
				FatAih.Fields.SERVIDOR.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatAih.Fields.SERVIDOR.toString() + "."
				+ RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString(),
				JoinType.LEFT_OUTER_JOIN);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(VFatContasHospPacientes.Fields.CTH_SEQ.toString(), cthSeq));
		}

		if (intSeq != null) {
			criteria.add(Restrictions.eq(VFatContasHospPacientes.Fields.INT_SEQ.toString(), intSeq));
		}
		
		List<VFatContasHospPacientes> lista = executeCriteria(criteria);
		return lista != null && !lista.isEmpty() ? lista.get(0) : null;
	}

}
