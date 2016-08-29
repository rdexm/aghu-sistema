package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.CacheMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;

@ApplicationScoped
public class AinDiariasAutorizadasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinDiariasAutorizadas> {

	private static final long serialVersionUID = -6693841808236287390L;

	public Byte obterSeqAinDiariaInternacao(Integer seqInternacao){
		List<AinDiariasAutorizadas>  lista = pesquisarDiariasAutorizadasOrdenadoCriadoEmDesc(seqInternacao);
		if(lista != null && lista.size() > 0){
			Byte seqLast = lista.get(0).getId().getSeq();
			return ++ seqLast;
		}
		return 1;
	}
		
	public List<AinDiariasAutorizadas> pesquisarDiariasAutorizadasOrdenadoCriadoEmDesc(Integer seqInternacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinDiariasAutorizadas.class);
		criteria.add(Restrictions.eq(AinDiariasAutorizadas.Fields.INT_SEQ.toString(), seqInternacao));
		criteria.addOrder(Order.desc(AinDiariasAutorizadas.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria, CacheMode.IGNORE);
	}

	public AinDiariasAutorizadas buscarPrimeiraDiariaAutorizada(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinDiariasAutorizadas.class);
		
		criteria.createAlias(AinDiariasAutorizadas.Fields.INTERNACAO.toString(), "int");
		criteria.createAlias("int." + AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(), "fci");
		criteria.createAlias("fci." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "cth");

		criteria.add(Restrictions.eq("cth." + FatContasHospitalares.Fields.SEQ.toString(), cthSeq));

		List<AinDiariasAutorizadas> list = executeCriteria(criteria, 0, 1, null, true, CacheMode.IGNORE);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * ORADB ainc_busca_senha_int
	 */
	public String buscaSenhaInternacao(Integer internacaoSeq) {
		String senha = null;
		StringBuilder hql = new StringBuilder(200);
		hql.append(" SELECT ");
		hql.append(" dau.senha ");
		hql.append(" FROM ");
		hql.append(" AinDiariasAutorizadas dau ");
		hql.append(" WHERE ");
		hql.append(" dau.id.intSeq = :internacaoSeq ");
		hql.append(" ORDER BY dau.criadoEm DESC ");

		javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("internacaoSeq", internacaoSeq);

		List<Object> result = query.getResultList();
		if (result != null && result.size() > 0) {
			Object senhaAux = result.get(0);
			if (senhaAux != null) {
				senha = (String) senhaAux;
			}
		}
		return senha;
	}

	public List<String> pesquisarCnracPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinDiariasAutorizadas.class);
		
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("cnrac")), "cnrac"));

		DetachedCriteria criteriaInternacao = criteria.createCriteria("internacao");
		DetachedCriteria criteriaPaciente = criteriaInternacao.createCriteria("paciente");
		DetachedCriteria criteriaAtendimento = criteriaInternacao.createCriteria("atendimento");

		criteriaPaciente.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteriaAtendimento.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		// String cnrac = (String)executeCriteriaUniqueResult(criteria);
		return executeCriteria(criteria, CacheMode.IGNORE);
	}

	/**
	 * Retorna uma lista de Diarias Hospitalares recebendo o codigo da internação
	 * como parametro
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public List<AinDiariasAutorizadas> pesquisarDiariaPorCodigoInternacao(Integer seqInternacao) {
		List<AinDiariasAutorizadas> listaResultado;
		DetachedCriteria criteria = DetachedCriteria.forClass(AinDiariasAutorizadas.class);
		
		if (seqInternacao != null) {
			criteria.add(Restrictions.eq(AinDiariasAutorizadas.Fields.INT_SEQ.toString(), seqInternacao));
		}

		listaResultado = this.executeCriteria(criteria, CacheMode.IGNORE);

		return listaResultado;
	}
    
}
