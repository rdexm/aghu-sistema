package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.VMbcProcEspVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

public class MbcEspecialidadeProcCirgsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcEspecialidadeProcCirgs> {

	private static final long serialVersionUID = 2378257069301336456L;
	
	/** 
	 * View V_MBC_PROC_ESP
	 * Retorna o código HQL da view permitindo que outro método adicione restrições específicas da consulta.
	 * A execução é feita no método executeHqlVMbcProcEsp() que retorna uma lista de VMbcProcEspVO com campos originais da view.
	 * @author Cristiano Alexandre Moretti
	 */
	public StringBuffer getHqlVMbcProcEsp() {
		StringBuffer hql = new StringBuffer(1000);
		hql.append(" select");
		hql.append("	epr.especialidade.seq as espSeq,");
		hql.append("	esp.sigla as sigla,");
		hql.append("	epr.mbcProcedimentoCirurgicos.seq as pciSeq,");
		hql.append("	snp.id.seqp as seqp,");
		hql.append("	snp.descricao as descricao,");
		hql.append("	pci.descricao as descProc,");
		hql.append("	pci.indContaminacao as indContaminacao,");
		hql.append("	pci.indProcMultiplo as indProcMultiplo,");
		hql.append("	pci.tempoMinimo as tempoMinimo,");
		hql.append("	snp.situacao as situacaoSinonimo,");
		hql.append("	pci.indSituacao as situacaoProc,");
		hql.append("	epr.situacao as situacaoEspProc,");
		hql.append("	esp.indSituacao as situacaoEsp,");
		hql.append("	pci.tipo as tipo,");
		hql.append("	pci.regimeProcedSus as regimeProcedSus,");
		hql.append("	pci.ladoCirurgia as indLadoCirurgico");
		hql.append(" from");
		hql.append("	AghEspecialidades as esp,");
		hql.append("	MbcEspecialidadeProcCirgs as epr,");
		hql.append("	MbcProcedimentoCirurgicos as pci,");
		hql.append("	MbcSinonimoProcCirg as snp");
		hql.append(" where");
		hql.append("	esp.seq = epr.especialidade.seq");
		hql.append("	and snp.mbcProcedimentoCirurgicos.seq = epr.mbcProcedimentoCirurgicos.seq");
		hql.append("	and pci.seq = snp.mbcProcedimentoCirurgicos.seq");
		return hql;
	}
	
	/**
	 * Executa a Query relativa à view V_MBC_PROC_ESP e retorna uma lista de VMbcProcEspVO com as colunas definidas na view original. 
	 * @author Cristiano Alexandre Moretti
	 */
	public List<VMbcProcEspVO> executeHqlVMbcProcEsp(Query query) {
		return query.list();
	}
	
	/**
	 * #22382 C5 
	 * @author Cristiano Alexandre Moretti
	 */
	public String getVpeDescricao(Short eprEspSeq, Integer eprPciSeq) {
		StringBuffer hql = getHqlVMbcProcEsp();
		hql.append("	and epr.especialidade.seq = :eprEspSeq");
		hql.append("	and epr.mbcProcedimentoCirurgicos.seq = :eprPciSeq");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("eprEspSeq",eprEspSeq);
		query.setParameter("eprPciSeq",eprPciSeq);
		query.setResultTransformer(Transformers.aliasToBean(VMbcProcEspVO.class));
		List<VMbcProcEspVO> lista = executeHqlVMbcProcEsp(query);
		String vpeDescricao;
		if (lista != null) {
			vpeDescricao = (String) ((VMbcProcEspVO) lista.get(0)).getDescricao();
		} else {
			vpeDescricao = null;
		}
		return vpeDescricao;
	}
	
	/**
	 * #22382 C5 
	 * @author Cristiano Alexandre Moretti
	 */
	public String getVpeDescricao(MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs) {
		return getVpeDescricao(mbcEspecialidadeProcCirgs.getId().getEspSeq(),mbcEspecialidadeProcCirgs.getId().getPciSeq());
	}

	public List<MbcEspecialidadeProcCirgs> buscaEspeciadadesPeloSeqProcedimento(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEspecialidadeProcCirgs.class);
		criteria.createAlias(MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "ESP");
		
		criteria.add(Restrictions.eq(MbcEspecialidadeProcCirgs.Fields.PCI_SEQ.toString(), pciSeq));
		
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.NOME.toString()));
		
		return super.executeCriteria(criteria);
	}
	
	/**
	 * #24711 C1 
	 */
	public List<LinhaReportVO> pesquisarEspecialidadePorTipoProcCirgs(String strPesquisa) {
		
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(strPesquisa);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(
						Projections.property("especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString())), LinhaReportVO.Fields.TEXTO2.toString())
				.add(	Projections.property("especialidade." + AghEspecialidades.Fields.SIGLA.toString()), LinhaReportVO.Fields.TEXTO1.toString())
				.add(	Projections.property("especialidade." + AghEspecialidades.Fields.SEQ.toString()), LinhaReportVO.Fields.NUMERO4.toString())
		);		
				
		criteria.addOrder(Order.asc("especialidade." + AghEspecialidades.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarEspecialidadePorTipoProcCirgsCount(String strPesquisa) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(strPesquisa);
		
		//criteria.setProjection(Projections.countDistinct("especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));

		return executeCriteriaCountDistinct(criteria,
				"especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), true);		
	}

	public List<MbcEspecialidadeProcCirgs> pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(Short espSeq, Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEspecialidadeProcCirgs.class);
		criteria.add(Restrictions.eq(MbcEspecialidadeProcCirgs.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(MbcEspecialidadeProcCirgs.Fields.PCI_SEQ.toString(), pciSeq));
		return executeCriteria(criteria);
	}

	public List<MbcEspecialidadeProcCirgs> pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq2(Short espSeq, Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEspecialidadeProcCirgs.class, "PCI");
		criteria.createAlias(MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "ESP");
		
		criteria.add(Restrictions.eq("ESP."+AghEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("PCI."+MbcEspecialidadeProcCirgs.Fields.PCI_SEQ.toString(), pciSeq));
		return executeCriteria(criteria);
	}
	private DetachedCriteria montarCriteriaParaNomeOuSigla(String strPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEspecialidadeProcCirgs.class);
		
		criteria.createAlias(MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "procedimentoCirurgicos");
		criteria.createAlias(MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "especialidade");
		
		String nomeOuSigla = StringUtils.trimToNull(strPesquisa);		
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			Criterion lhs = Restrictions.ilike("especialidade." + AghEspecialidades.Fields.SIGLA.toString(),
					nomeOuSigla, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike("especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(),
					nomeOuSigla, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		}
		
		criteria.add(Restrictions.eq("procedimentoCirurgicos." + MbcProcedimentoCirurgicos.Fields.TIPO.toString(), DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO));
		criteria.add(Restrictions.eq("especialidade." + AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull("especialidade." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()));
		return criteria;
	}

}
