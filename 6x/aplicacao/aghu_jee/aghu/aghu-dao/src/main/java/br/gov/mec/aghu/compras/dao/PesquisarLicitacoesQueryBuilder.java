package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Query Builder para a Consulta Licitações pregão eletrônico do Banco do Brasil
 * #5550
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class PesquisarLicitacoesQueryBuilder extends QueryBuilder<DetachedCriteria> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1879846389956697144L;
	
	private ScoLicitacaoVO scoLicitacaoVOFiltro;
	
	private Date dataInicio, dataFim, dataFimGerArqRem;
	
	private Date dataInicioGerArqRem;
	
	private Boolean pacPendenteEnvio;
	
	private Boolean pacPendenteRetorno;
	
	@Override
	protected DetachedCriteria createProduct() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		if(scoLicitacaoVOFiltro.getMatriculaGestor() != null){
			criteria.createAlias("LCT."+ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), "RSE", JoinType.INNER_JOIN);
	    }else{
	    	criteria.createAlias("LCT."+ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), "RSE", JoinType.LEFT_OUTER_JOIN);
	    }
		criteria.createAlias("RSE."+RapServidores.Fields.PESSOA_FISICA.toString(), "RPF", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if(scoLicitacaoVOFiltro.getNumeroPAC() != null){
			criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.NUMERO.toString(), scoLicitacaoVOFiltro.getNumeroPAC()));	}
		if(scoLicitacaoVOFiltro.getModalidadeLicitacao() != null && StringUtils.isNotBlank(scoLicitacaoVOFiltro.getModalidadeLicitacao())){
			criteria.add(Restrictions.ilike("LCT."+ScoLicitacao.Fields.MLCCODIGO.toString(), scoLicitacaoVOFiltro.getModalidadeLicitacao()));	}
		if(scoLicitacaoVOFiltro.getSituacao() != null){
			criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.IND_SITUACAO.toString(), scoLicitacaoVOFiltro.getSituacao()));}
		if(scoLicitacaoVOFiltro.getMatriculaGestor() != null){
			criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.SERVIDOR_GESTOR_MATRICULA.toString(), scoLicitacaoVOFiltro.getMatriculaGestor()));}
		
		if (dataInicio != null) {
			criteria.add(Restrictions.ge("LCT."+ScoLicitacao.Fields.DT_DIGITACAO, DateUtil.obterDataComHoraInical(dataInicio)));
		}
		if (dataFim != null) {
			criteria.add(Restrictions.le("LCT."+ScoLicitacao.Fields.DT_DIGITACAO, DateUtil.obterDataComHoraFinal(dataFim)));
		}
		
		if (dataInicioGerArqRem != null) {
			criteria.add(Restrictions.ge("LCT."+ScoLicitacao.Fields.DT_GERACAO_ARQ_REMESSA, DateUtil.obterDataComHoraInical(dataInicioGerArqRem)));
		}
		if (dataFimGerArqRem != null) {
			criteria.add(Restrictions.le("LCT."+ScoLicitacao.Fields.DT_GERACAO_ARQ_REMESSA, DateUtil.obterDataComHoraFinal(dataFimGerArqRem)));
		}

		verificaPacPendente(criteria);
		
		return criteria;
	}
	
	private void verificaPacPendente(DetachedCriteria criteria){
		if(pacPendenteEnvio){
			List <String> list = new ArrayList<String>();
			list.add("PG");	
			list.add("DI");	
			list.add("RD");
			criteria.add(Restrictions.and(Restrictions.in("LCT."+ScoLicitacao.Fields.MLC_CODIGO.toString(), list), Restrictions.isNull("LCT."+ScoLicitacao.Fields.DT_GERACAO_ARQ_REMESSA.toString())));
		}
		
		if(pacPendenteRetorno){
			List <String> list = new ArrayList<String>();
			list.add("PG");	
			list.add("DI");	
			list.add("RD");
			criteria.add(Restrictions.and(Restrictions.in("LCT."+ScoLicitacao.Fields.MLC_CODIGO.toString(), list), Restrictions.isNull("LCT."+ScoLicitacao.Fields.DT_LEITURA_ARQ_RETORNO.toString())));
		}
	}
	
	@Override
	protected void doBuild(DetachedCriteria criteria) {
		if(dataInicio != null && dataFim != null){
			criteria.add(Restrictions.between("LCT."+ScoLicitacao.Fields.DT_DIGITACAO.toString(), dataInicio, dataFim));
		}
	}

	public DetachedCriteria build(ScoLicitacaoVO scoLicitacaoVOFiltro, Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno) {
		
		this.scoLicitacaoVOFiltro = scoLicitacaoVOFiltro;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.dataInicioGerArqRem = dataInicioGerArqRem;
		this.dataFimGerArqRem = dataFimGerArqRem;
		this.pacPendenteEnvio = pacPendenteEnvio;
		this.pacPendenteRetorno = pacPendenteRetorno;
		return super.build();
	}
}
