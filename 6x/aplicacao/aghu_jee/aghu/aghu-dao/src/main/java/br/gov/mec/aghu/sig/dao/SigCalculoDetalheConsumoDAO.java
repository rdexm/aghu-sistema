package br.gov.mec.aghu.sig.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.DetalheConsumoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemPrescricaoNptVO;

public class SigCalculoDetalheConsumoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoDetalheConsumo> {

	private static final long serialVersionUID = 7903453458349087L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(200);
		sql.append(" DELETE ").append(SigCalculoDetalheConsumo.class.getSimpleName().toString()).append(" caaa ");
		sql.append(" WHERE caaa.").append(SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO.toString()).append('.').append(SigCalculoAtdConsumo.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT caa.").append(SigCalculoAtdConsumo.Fields.SEQ.toString());
			sql.append(" FROM ").append(SigCalculoAtdConsumo.class.getSimpleName().toString()).append(" caa ");
			sql.append(" WHERE caa.").append(SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString()).append('.').append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
			sql.append(" IN ( ");
				sql.append(" SELECT ca.").append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
				sql.append(" FROM ").append(SigCalculoAtdPermanencia.class.getSimpleName().toString()).append(" ca ");
				sql.append(" WHERE ca.").append(SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
				sql.append(" IN ( ");
					sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
					sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
					sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
				sql.append(" ) ");
			sql.append(" ) ");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	
	/**
	* Busca detalhe de item de consumo 
	* @param calculoAtdConsumo Calculo Atendimento Consumo
	* @param procedHospInternos Procedimento Hospitalar Interno
	* @return buscarItemConsumo Detalhe do item de insumo
	* @author rhrosa
	*/
	public SigCalculoDetalheConsumo buscarItemConsumo(SigCalculoAtdConsumo calculoAtdConsumo, FatProcedHospInternos procedHospInternos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoDetalheConsumo.class);

		criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO.toString(), calculoAtdConsumo));
		criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), procedHospInternos));

		return (SigCalculoDetalheConsumo) this.executeCriteriaUniqueResult(criteria);
	}
	
	public SigCalculoDetalheConsumo buscarItemConsumo(SigCalculoAtdConsumo calculoAtdConsumo, FatProcedHospInternos procedHospInternos, SigGrupoOcupacoes grupoOcupacao, RapOcupacaoCargo ocupacaoCargo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoDetalheConsumo.class);

		criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO.toString(), calculoAtdConsumo));
		
		if(procedHospInternos != null){
			criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), procedHospInternos));
		}
		else if(grupoOcupacao != null){
			criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.GRUPO_OCUPACAO.toString(), grupoOcupacao));
		}
		else{
			criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.RAP_OCUPACAO_CARGO.toString(), ocupacaoCargo));
		}
		return (SigCalculoDetalheConsumo) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	* Retorna os itens de uma nutricao parenteral atraves da seq da prescricao medica
	* @param pnpSeq                Seq da prescricao Medica 
	* @return ItemPrescricaoNptVO  Lista de  VO de itens de prescricao
	*/
	@SuppressWarnings("unchecked")
	public List<ItemPrescricaoNptVO> buscarItensNutricaoParenteral(Integer pnpSeq) {
			
		StringBuilder hql = new StringBuilder(300);
		hql.append(" SELECT ")
		.append(" cnpt." ).append( MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ ).append( ", ")
		.append(" inpt." ).append( MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString() ).append( '.' ).append( AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString() ).append( ", ")
		.append(" coalesce(inpt." ).append( MpmItemPrescricaoNpt.Fields.QTDE_PRESCRITA ).append( ",0), ")
		.append(" phi." ).append( FatProcedHospInternosPai.Fields.SEQ ).append( ", ")
		.append(" afd." ).append( AfaFormaDosagem.Fields.FATOR_CONVERSAO_UP)

		.append(" FROM ")

		.append(MpmComposicaoPrescricaoNpt.class.getSimpleName() ).append( " cnpt, ")
		.append(MpmItemPrescricaoNpt.class.getSimpleName() ).append( " inpt, ")
		.append(ScoMaterial.class.getSimpleName() ).append( " mat, ")
		.append(FatProcedHospInternos.class.getSimpleName() ).append( " phi, ")
		.append(AfaFormaDosagem.class.getSimpleName() ).append( " afd ")

		.append(" WHERE ")
		.append(" cnpt. " ).append( MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ ).append( " = :pnpSeq ")
		.append(" AND cnpt." ).append( MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ.toString() ).append( " = inpt." ).append( MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_SEQ.toString())
		.append(" AND cnpt." ).append( MpmComposicaoPrescricaoNpt.Fields.ID_PNP_ATD_SEQ.toString() ).append( " = inpt."
				).append( MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_ATD_SEQ.toString())
		.append(" AND cnpt." ).append( MpmComposicaoPrescricaoNpt.Fields.ID_SEQP.toString() ).append( " = inpt." ).append( MpmItemPrescricaoNpt.Fields.ID_CPT_SEQP.toString())

		.append(" AND inpt." ).append( MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString() ).append( '.' ).append( AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString() ).append( " = mat." ).append( ScoMaterial.Fields.CODIGO.toString())
		.append(" AND mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = phi." ).append( FatProcedHospInternos.Fields.MAT_CODIGO.toString())
		.append(" AND inpt." ).append( MpmItemPrescricaoNpt.Fields.FORMA_DOSAGENS.toString() ).append( '.' ).append( AfaFormaDosagem.Fields.SEQ.toString() ).append( " = afd."
				).append( AfaFormaDosagem.Fields.SEQ.toString());

		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("pnpSeq", pnpSeq);
		query.setResultTransformer(new ItemPrescricaoNptVO());

		return (List<ItemPrescricaoNptVO>) query.list();
	}
	
	public List<SigCalculoDetalheConsumo> obterPorSigCalculoAtdConsumo(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoDetalheConsumo.class);
		criteria.add(Restrictions.eq(SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
	
	public List<DetalheConsumoVO> listarDetalheConsumo(Integer atdSeq, Integer pmuSeq, Integer cctCodigo, String nomeObjetoCusto){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
	
		criteria.setProjection(Projections.projectionList()
			.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.SEQ.toString()), "phiSeq")
			.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "phiDescricao")
			.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()), "matCodigo")
			.add(Projections.sum("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), "quantidade")
			.add(Projections.sum("cdc." + SigCalculoDetalheConsumo.Fields.VALOR_CONSUMIDO.toString()), "valor")
		);
				
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO, "cdc", JoinType.INNER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO, "ocv", JoinType.INNER_JOIN);
		criteria.createAlias("ocv."+SigObjetoCustoVersoes.Fields.OBJETO_CUSTO, "obj", JoinType.INNER_JOIN);
		criteria.createAlias("cdc."+SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO, "phi", JoinType.INNER_JOIN);
	
		if(pmuSeq != null){
			criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));		
		}
		
		if(cctCodigo != null){
			criteria.add(Restrictions.eq("cca."+SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString(), cctCodigo));		
		}
		
		criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		
		criteria.add(Restrictions.ilike("obj."+SigObjetoCustos.Fields.NOME.toString(), nomeObjetoCusto, MatchMode.EXACT));
		criteria.add(Restrictions.eq("ocv."+SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		criteria.add(Restrictions.in("cac."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), new DominioSituacaoCalculoPaciente[]{DominioSituacaoCalculoPaciente.I, DominioSituacaoCalculoPaciente.IA}));
		criteria.add(Restrictions.eq("cpp."+SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		
		criteria.addOrder(Order.asc("phi."+FatProcedHospInternos.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DetalheConsumoVO.class));
		return executeCriteria(criteria);
	}	
}
