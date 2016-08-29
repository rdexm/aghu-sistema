package br.gov.mec.aghu.paciente.dao;

import java.util.Date;

import org.hibernate.Query;

import br.gov.mec.aghu.model.MptLaudo;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptLaudoDAO extends BaseDao<MptLaudo>{

	private static final long serialVersionUID = 3523983763380240196L;
	
	/**
	 * #43089 - C2
	 * @param conNumero
	 * @param tptSeqFis
	 * @return MptLaudo
	 */
	public MptLaudo obterLaudoPorNumConsultaTipoTratamento(Integer conNumero, Integer tptSeqFis) {
		
		String hql = "SELECT lau "
				.concat(" FROM MptLaudo lau, MptPrescricaoPaciente pte INNER JOIN pte.atendimento atd ")
				.concat(" WHERE lau.id.trpSeq = atd.trpSeq ")
				.concat(" AND pte.conNumero = :conNumero ")
				.concat(" AND atd.tptSeq = :tptSeqFis ")
				.concat(" AND lau.dtPrevFimValidade > :dtPrevFimValidade ")
				.concat(" AND lau.indConcluido = :indConcluido ");		
		
		Query query = createHibernateQuery(hql);
		
		query.setParameter("conNumero", conNumero);
		query.setParameter("tptSeqFis", tptSeqFis);
		query.setParameter("dtPrevFimValidade", new Date());
		query.setParameter("indConcluido", "S");
		
		return (MptLaudo) query.uniqueResult();
	}


}
