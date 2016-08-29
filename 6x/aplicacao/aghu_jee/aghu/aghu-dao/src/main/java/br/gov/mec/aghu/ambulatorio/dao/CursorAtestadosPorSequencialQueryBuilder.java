package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorAtestadosPorSequencialQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -509634547432090379L;
	
	private final String PONTO = ".";
	private final String ALIAS_ATE = "ATE";
	private final String ALIAS_TAS = "TAS";
	private final String ALIAS_CID = "CID";
	private final String ALIAS_PAC = "PAC";
	
	private DetachedCriteria criteria;
	private Long seq;
	private boolean oracle;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MamAtestados.class, ALIAS_ATE);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setProjection();
		setJoin();
		setRestriction();
	}
	
	private void setProjection() {
		String sqlDescricaoCid = "COALESCE(cid2_.DESCRICAO_EDITADA, cid2_.DESCRICAO) descricaoCid";
		String sqlDescricaoCidOracle = "NVL(cid2_.DESCRICAO_EDITADA, cid2_.DESCRICAO) descricaoCid";
		
		String sqlIndMotivoUsoFGTS = "COALESCE(this_.IND_MOTIVO_USO_FGTS,'XX') indMotivoUsoFGTS";
		String sqlIndMotivoUsoFGTSOracle = "NVL(this_.IND_MOTIVO_USO_FGTS,'XX') indMotivoUsoFGTS";
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.SEQ.toString()), AtestadoVO.Fields.SEQ.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.DT_HR_CONS.toString()), AtestadoVO.Fields.DTHR_CONS.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.NOME_ACOMPANHANTE.toString()), AtestadoVO.Fields.NOME_ACOMPANHANTE.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.DATA_INICIAL.toString()), AtestadoVO.Fields.DATA_INICIAL.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.DATA_FINAL.toString()), AtestadoVO.Fields.DATA_FINAL.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.OBSERVACAO.toString()), AtestadoVO.Fields.OBSERVACAO.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.NRO_VIAS.toString()), AtestadoVO.Fields.NRO_VIAS.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.PERIODO.toString()), AtestadoVO.Fields.PERIODO.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.ACOMETIDO_DE.toString()), AtestadoVO.Fields.ACOMETIDO_DE.toString());
		projList.add(Projections.property(ALIAS_TAS + PONTO + MamTipoAtestado.Fields.LAYOUT.toString()), AtestadoVO.Fields.LAYOUT.toString());
		projList.add(Projections.property(ALIAS_TAS + PONTO + MamTipoAtestado.Fields.TITULO.toString()), AtestadoVO.Fields.TITULO.toString());
		projList.add(Projections.property(ALIAS_CID + PONTO + AghCid.Fields.CODIGO.toString()), AtestadoVO.Fields.CODIGO_CID.toString());
		projList.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.PRONTUARIO.toString()), AtestadoVO.Fields.PRONTUARIO.toString());
		projList.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.NOME.toString()), AtestadoVO.Fields.NOME_PACIENTE.toString());
		projList.add(Projections.property(ALIAS_ATE + PONTO + MamAtestados.Fields.ESTAGIO_CLINICO_GERAL.toString()), AtestadoVO.Fields.ESTAGIO_CLINICO_GERAL.toString());
		
		if (this.isOracle()) {
			projList.add(Projections.sqlProjection(sqlDescricaoCidOracle, new String [] {AtestadoVO.Fields.DESCRICAO_CID.toString()}, new Type[] {new StringType()}));
		} else {
			projList.add(Projections.sqlProjection(sqlDescricaoCid, new String [] {AtestadoVO.Fields.DESCRICAO_CID.toString()}, new Type[] {new StringType()}));
		}
		
		if (this.isOracle()) {
			projList.add(Projections.sqlProjection(sqlIndMotivoUsoFGTSOracle, new String [] {AtestadoVO.Fields.IND_MOTIVO_USO_FGTS.toString()}, new Type[] {new StringType()}));
		} else {
			projList.add(Projections.sqlProjection(sqlIndMotivoUsoFGTS, new String [] {AtestadoVO.Fields.IND_MOTIVO_USO_FGTS.toString()}, new Type[] {new StringType()}));
		}
		
		criteria.setProjection(projList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AtestadoVO.class));
	}
	
	private void setJoin() {
		criteria.createAlias(ALIAS_ATE + PONTO + MamAtestados.Fields.MAM_TIPO_ATESTADO.toString(), ALIAS_TAS, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATE + PONTO + MamAtestados.Fields.AGH_CID.toString(), ALIAS_CID, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_ATE + PONTO + MamAtestados.Fields.AIP_PACIENTES.toString(), ALIAS_PAC, JoinType.INNER_JOIN);
	}
	
	private void setRestriction() {
		criteria.add(Restrictions.eq(ALIAS_ATE + PONTO + MamAtestados.Fields.SEQ.toString(), seq));
	}
	
	public DetachedCriteria build(Long seq, boolean oracle) {
		this.seq = seq;
		this.oracle = oracle;
		return super.build();
	}

	public boolean isOracle() {
		return oracle;
	}

}
