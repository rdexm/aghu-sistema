package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

public class AelEquipamentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelEquipamentos> {

	private static final long serialVersionUID = 7456952067490066172L;



	public List<AelEquipamentos> pesquisaEquipamentosPorUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelEquipamentos.class,"EQU");

		criteria.add(Restrictions.eq("EQU."+AelEquipamentos.Fields.UNF_SEQ.toString(), unidadeExecutora.getSeq()));

		criteria.addOrder(Order.asc("EQU."+AelEquipamentos.Fields.DESCRICAO.toString()));


		return executeCriteria(criteria);
	}


	/**
	 * Verifica a existência de alguma dependência
	 * @param elemento
	 * @param classeDependente
	 * @param fieldChaveEstrangeira
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("rawtypes")
	public final boolean existeDependencia(Object elemento, Class classeDependente, Enum fieldChaveEstrangeira) throws BaseException{

		CoreUtil.validaParametrosObrigatorios(elemento,classeDependente, fieldChaveEstrangeira);

		DetachedCriteria criteria = DetachedCriteria.forClass(classeDependente);
		criteria.add(Restrictions.eq(fieldChaveEstrangeira.toString(), elemento));

		return executeCriteriaCount(criteria) > 0;

	}

	/**
	 * Retorna AelMetodo original
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AelEquipamentos obterOriginal(AelEquipamentos elementoModificado) {

		final Short id = elementoModificado.getSeq();

		StringBuilder hql = new StringBuilder(100);

		hql.append("select o.").append(AelEquipamentos.Fields.SEQ.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.DRIVER_ID.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.UNIDADE_FUNCIONAL.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.POSSUI_INTERFACE.toString());
		hql.append(", o.").append(AelEquipamentos.Fields.CARGA_AUTOMATICA.toString());


		hql.append(" from ").append(AelEquipamentos.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelEquipamentos.Fields.SEQ.toString()).append(" = :entityId ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);

		AelEquipamentos original = null;

		List<Object[]> camposList = (List<Object[]>) query.getResultList();

		if(camposList != null && camposList.size()>0) {

			Object[] campos = camposList.get(0);
			original = new AelEquipamentos();

			original.setSeq(id);
			original.setDescricao((String)campos[1]);
			original.setSituacao((DominioSituacao)campos[2]);
			original.setCriadoEm((Date)campos[3]);
			original.setServidor((RapServidores)campos[4]);
			original.setDriverId((String)campos[5]);
			original.setUnidadeFuncional((AghUnidadesFuncionais)campos[6]);
			original.setPossuiInterface((Boolean)campos[7]);
			original.setCargaAutomatica((Boolean)campos[8]);
		}
		return original;
	}

	public List<AelEquipamentos> pesquisaEquipamentosPorSeqDescricao(Object parametro) {

		final String srtPesquisa = (String) parametro;

		DetachedCriteria criteria = DetachedCriteria.forClass(AelEquipamentos.class,"EQU");
		if (CoreUtil.isNumeroInteger(srtPesquisa)) {
			criteria.add(Restrictions.eq("EQU."+AelEquipamentos.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike("EQU."+AelEquipamentos.Fields.DESCRICAO.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
		criteria.addOrder(Order.asc("EQU."+AelEquipamentos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	

	public List<AelEquipamentos> pesquisaEquipamentosPorEmaExaSiglaManSeq(String emaExaSigla, Integer emaManSeq, DominioProgramacaoExecExames programacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelEquipamentos.class,"equ");
		criteria.createCriteria("equ."+AelEquipamentos.Fields.EXEC_EXAMES_MAT_ANALISE.toString(), "eem", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("eem."+ AelExecExamesMatAnalise.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq("eem."+ AelExecExamesMatAnalise.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq("eem."+ AelExecExamesMatAnalise.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		Object[] valores = new Object[]{DominioProgramacaoExecExames.A, programacao};
		criteria.add(Restrictions.in("eem."+ AelExecExamesMatAnalise.Fields.PROGRAMACAO.toString(), valores));
		
		criteria.addOrder(Order.asc("eem."+AelExecExamesMatAnalise.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}
	
	
	public List<AelEquipamentos> pesquisarEquipamentosPorSiglaOuDescricao(Object parametro, Short unfSeq) {
		final String srtPesquisa = (String) parametro;

		DetachedCriteria criteria = DetachedCriteria.forClass(AelEquipamentos.class);
		if (CoreUtil.isNumeroInteger(srtPesquisa)) {
			criteria.add(Restrictions.eq(AelEquipamentos.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike(AelEquipamentos.Fields.DESCRICAO.toString(), 
					srtPesquisa , MatchMode.ANYWHERE ));
		}
		
		criteria.add(Restrictions.eq(AelEquipamentos.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelEquipamentos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AelEquipamentos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa equipamentos ativos no interfaceamento GESTam
	 * @param setorLis
	 * @param driverId
	 * @return
	 */
	public List<AelEquipamentos> pesquisarEquipamentosProcResult(Short setorLis, String driverId) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelEquipamentos.class,"EQU");

		criteria.add(Restrictions.eq("EQU."+AelEquipamentos.Fields.UNF_SEQ.toString(), setorLis));
		criteria.add(Restrictions.eq("EQU."+AelEquipamentos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EQU."+AelEquipamentos.Fields.DRIVER_ID, driverId));

		return executeCriteria(criteria);
	}
	
	
}