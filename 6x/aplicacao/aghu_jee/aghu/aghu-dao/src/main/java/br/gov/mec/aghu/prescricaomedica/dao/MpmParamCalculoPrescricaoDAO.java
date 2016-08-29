package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.prescricaomedica.vo.MpmVerificaParamCalculoVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MpmParamCalculoPrescricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmParamCalculoPrescricao> {

	private static final long serialVersionUID = 2880437058654674167L;

	public List<MpmParamCalculoPrescricao> pesquisarMpmParamCalculoPrescricoes(Integer pesoPacCodigo, Integer alturaPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParamCalculoPrescricao.class);

		if (pesoPacCodigo != null) {
			criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.PESO_PACIENTE_CODIGO.toString(), pesoPacCodigo));
		}
		if (alturaPacCodigo != null) {
			criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.ALTURA_PACIENTE_CODIGO.toString(), alturaPacCodigo));
		}

		return executeCriteria(criteria);
	}

	public List<MpmParamCalculoPrescricao> listarParamCalculoPrescricoesAtivosPeloAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParamCalculoPrescricao.class);

		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}

	public MpmParamCalculoPrescricao obterParamCalculoPrescricoesCriadasHojePeloAtendimentoESituacao(Integer atdSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParamCalculoPrescricao.class);
			
		
		criteria.setFetchMode(MpmParamCalculoPrescricao.Fields.PESO_PACIENTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(MpmParamCalculoPrescricao.Fields.ALTURA_PACIENTE.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.ATD_SEQ.toString(), atdSeq));
		if(situacao != null) {
			criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.SITUACAO.toString(), situacao));
		}
		criteria.add(Restrictions.sqlRestriction(
				this.isOracle() ? " trunc({alias}.CRIADO_EM) = trunc(sysdate) "
						: "  date_trunc('day', {alias}.CRIADO_EM)= date_trunc('day', now()) "));
		List<MpmParamCalculoPrescricao> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);	
		}
		return null;
	}
	
	
	public MpmVerificaParamCalculoVO obterParametrosCalculo(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParamCalculoPrescricao.class);
		criteria.createAlias(MpmParamCalculoPrescricao.Fields.PESO_PACIENTE.toString(), "pep");
		criteria.createAlias(MpmParamCalculoPrescricao.Fields.ALTURA_PACIENTE.toString(), "atp");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pep." + AipPesoPacientes.Fields.PESO.toString()), MpmVerificaParamCalculoVO.Fields.PESO.toString())
				.add(Projections.property("atp." + AipAlturaPacientes.Fields.ALTURA.toString()), MpmVerificaParamCalculoVO.Fields.ALTURA.toString())
				.add(Projections.property(MpmParamCalculoPrescricao.Fields.SC.toString()), MpmVerificaParamCalculoVO.Fields.SC.toString())
				.add(Projections.property(MpmParamCalculoPrescricao.Fields.CRIADOEM.toString()), MpmVerificaParamCalculoVO.Fields.CRIADO_EM.toString()));
		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.CRIADOEM.toString(), DateUtil.truncaDataFim(new Date())));
		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.SITUACAO.toString(), DominioSituacao.A.toString()));
		criteria.addOrder(Order.desc(MpmParamCalculoPrescricao.Fields.CRIADOEM.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MpmVerificaParamCalculoVO.class));
		return (MpmVerificaParamCalculoVO) executeCriteriaUniqueResult(criteria);
	}

	
	public List<MpmParamCalculoPrescricao> pesquisarMoverParametroCalculo(final Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParamCalculoPrescricao.class);
		criteria.add(Restrictions.eq(MpmParamCalculoPrescricao.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.sqlRestriction(this.isOracle() ? " trunc({alias}.CRIADO_EM) = trunc(sysdate) " : "  date_trunc('day', {alias}.CRIADO_EM)= date_trunc('day', now()) "));
		criteria.addOrder(Order.desc(MpmParamCalculoPrescricao.Fields.CRIADOEM.toString()));
		return executeCriteria(criteria);
	}

}