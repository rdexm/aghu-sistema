package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamAgrupGrvEsps;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class MamAgrupGrvEspsDAO extends BaseDao<MamAgrupGrvEsps> {
	
	private static final long serialVersionUID = 3799059482280357560L;

	/***
	 * C18
	 * Consulta utilizada para obtenção da previsão de atendimento para o
	 * acolhimento para gravidade e especialidade
	 * 
	 * SELECT gxe.ind_prev_atend FROM mam_agrup_grv_x_esps gxe WHERE gxe.agr_seq
	 * = <SEQ em MAM_AGRP_GRAVIDADES obtido na consulta C15> AND gxe.eep_esp_seq
	 * = <ESP_SEQ em AAC_GRD_AGENDAMEN_CONSULTAS obtido na consulta C15>
	 * --Serviço #34713 AND gxe.ind_situacao = 'A';
	 */
    public MamAgrupGrvEsps obterPrevAtendAcolhimentoGravEsp(Short seqAgrupamentoGravidade, Short espSeq){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamAgrupGrvEsps.class, "MamAgrupGrvEsps");
		
		criteria.add(Restrictions.eq("MamAgrupGrvEsps." + MamAgrupGrvEsps.Fields.AGR_SEQ.toString(), seqAgrupamentoGravidade));
		criteria.add(Restrictions.eq("MamAgrupGrvEsps." + MamAgrupGrvEsps.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("MamAgrupGrvEsps." + MamAgrupGrvEsps.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		
		
		return (MamAgrupGrvEsps) this.executeCriteriaUniqueResult(criteria);
	}

}