package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.MamConsultorAmbulatorioVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamConsultorAmbulatorio;
import br.gov.mec.aghu.model.RapServidores;

public class MamConsultorAmbulatorioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamConsultorAmbulatorio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1795078299279188809L;

	public List<MamConsultorAmbulatorioVO> pesquisarConsultorAmbulatorioPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamConsultorAmbulatorio.class, "mca");
		criteria.createAlias("mca." + MamConsultorAmbulatorio.Fields.AGH_ESPECIALIDADES_BY_ESP_SEQ.toString(), "esp");
		criteria.createAlias("mca." + MamConsultorAmbulatorio.Fields.AGH_EQUIPES.toString(), "eqp", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("mca." + MamConsultorAmbulatorio.Fields.SEQ.toString()), MamConsultorAmbulatorioVO.Fields.SEQ.toString())
				.add(Projections.property("mca." + MamConsultorAmbulatorio.Fields.IND_SITUACAO.toString()), MamConsultorAmbulatorioVO.Fields.SITUACAO.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()), MamConsultorAmbulatorioVO.Fields.SIGLA.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), MamConsultorAmbulatorioVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property("eqp." + AghEquipes.Fields.SEQ.toString()), MamConsultorAmbulatorioVO.Fields.EQP_SEQ.toString())
				.add(Projections.property("eqp." + AghEquipes.Fields.NOME.toString()), MamConsultorAmbulatorioVO.Fields.EQP_NOME.toString())
				);
		
		criteria.add(Restrictions.eq("mca." + MamConsultorAmbulatorio.Fields.RAP_SERVIDORES_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("mca." + MamConsultorAmbulatorio.Fields.RAP_SERVIDORES_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
	    
		criteria.addOrder(Order.asc("mca." + MamConsultorAmbulatorio.Fields.SEQ.toString()));
		
    	criteria.setResultTransformer(Transformers.aliasToBean(MamConsultorAmbulatorioVO.class));
	    
    	return executeCriteria(criteria);
	}
	
	
	
	public List<MamConsultorAmbulatorio> pesquisarMamConsultorAmbulatorioPorServidorEspSeq(RapServidores servidor, Short espSeq, Integer eqpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamConsultorAmbulatorio.class, "mca");
		
		criteria.add(Restrictions.eq(MamConsultorAmbulatorio.Fields.RAP_SERVIDORES_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq(MamConsultorAmbulatorio.Fields.RAP_SERVIDORES_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		criteria.add(Restrictions.eq(MamConsultorAmbulatorio.Fields.ESP_SEQ.toString(), espSeq));
		if (eqpSeq != null) {
			criteria.add(Restrictions.eq(MamConsultorAmbulatorio.Fields.EQP_SEQ.toString(), eqpSeq));
		}
	    
    	return executeCriteria(criteria);
	}

	/**
	 * 
	 * @param matricula
	 * @param vin_codigo
	 * @param esp_seq
	 * @param esp_seq_agenda
	 * @return
	 * 
	 * Estoria 40229: C9
	 */
	
	// verificar o que é recebido como parâmatro
	public List<MamConsultorAmbulatorio> pesquisarMamConsultorAmbulatorioSeqPorServidor(
			RapServidores servidores){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamConsultorAmbulatorio.class, "CAB");
		
		criteria.createAlias("CAB." + MamConsultorAmbulatorio.Fields.AGH_ESPECIALIDADES_BY_ESP_SEQ.toString(), "ESP");
		criteria.createAlias("CAB." + MamConsultorAmbulatorio.Fields.AGH_ESPECIALIDADES_BY_ESP_SEQ_AGENDA.toString(), "AGD");

		criteria.add(Restrictions.eq(MamConsultorAmbulatorio.Fields.RAP_SERVIDORES.toString(), servidores));
		criteria.add(Restrictions.eq(MamConsultorAmbulatorio.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Estoria 40229: c10
	 * @param cEspSeq
	 * @return
	 *  
	 */
	public List<MamConsultorAmbulatorio> pesquisarMamConsultorAmbulatorioPorEspecialidade(
			Short cEspSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamConsultorAmbulatorio.class, "CAB");
	
		criteria.add(Restrictions.eq("CAB." + MamConsultorAmbulatorio.Fields.AGH_ESPECIALIDADES_BY_ESP_SEQ.toString() + "." + 
				AghEspecialidades.Fields.SEQ, cEspSeq));
		
		return executeCriteria(criteria);
		
	}
	
}
