package br.gov.mec.aghu.perinatologia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.perinatologia.vo.RelatorioSnappeVO;
import org.hibernate.sql.JoinType;

public class McoSnappesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoSnappes> {

	private static final long serialVersionUID = -8833648137197077975L;

	/**
	 * #27490 - C3
	 * @param pacCodigo
	 * @return
	 */
	public List<McoSnappes> listarSnappesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoSnappes.class);
		criteria.add(Restrictions.eq(McoSnappes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		return executeCriteria(criteria);
	}
	
	/**
	 * #27490 - C8
	 * @param pacCodigo
	 * @return
	 */
	public Short obterMaxSeqpSnappesPorCodigoPaciente(Integer pacCodigo){
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoSnappes.class);
		criteria.add(Restrictions.eq(McoSnappes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.setProjection(Projections.max(McoSnappes.Fields.SEQUENCE.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	

	/**
	 *
	 * @param pacCodigo
	 * @return
	 */
	public McoSnappes obterMcoSnappePorId(Integer pacCodigo, Short seqp){
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoSnappes.class);
		criteria.add(Restrictions.eq(McoSnappes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoSnappes.Fields.SEQUENCE.toString(), seqp));
		return (McoSnappes) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean isSnappesPreenchido(Integer pacCodigo, Date inicioAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoSnappes.class);

		criteria.add(Restrictions.eq(McoSnappes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.gt(McoSnappes.Fields.CRIADO_EM.toString(), inicioAtendimento));
		
		return executeCriteriaCount(criteria) > 0 ? true : false;
	}

    public List<McoSnappes> listarSnappesPorCodigoPacienteDthrInternacao(Integer pacCodigo,Date dthrInternacao) {
        DetachedCriteria criteria = DetachedCriteria.forClass(McoSnappes.class);
        criteria.add(Restrictions.eq(McoSnappes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
        criteria.add(Restrictions.gt(McoSnappes.Fields.CRIADO_EM.toString(), dthrInternacao));
        criteria.add(Restrictions.isNull(McoSnappes.Fields.DTHR_SUMARIO_ALTA.toString()));

        return executeCriteria(criteria);
    }
    
    public RelatorioSnappeVO obterRegistroSnappeImpressao(Integer pacCodigo, Short seqp){
    	
    	Type[] type = new Type[] { StringType.INSTANCE };
    	String[] sexo = new String[]{"sexoIdentificacao"};
    	String[] pressaoArterialMedia = new String[]{"pressaoArterialMediaFator"};
    	String[] apgar5 = new String[]{"apgarFator"};
    	String[] convulsoesMultiplas = new String[]{"convulsoesMultiplasFator"};
    	String[] pesoAoNascer = new String[]{"pesoNascimentoFator"};
    	String[] phDoSangue = new String[]{"phSangueFator"};
    	String[] pig = new String[]{"pigFator"};
    	String[] razaoPo2PorFio2 = new String[]{"razaoPo2Fi2Fator"};
    	String[] temperatura = new String[]{"temperaturaFator"};
    	String[] volumeUrinario = new String[]{"volumeUrinarioFator"};
    	String[] alturaNascimentoIdentificacao = new String[]{"alturaNascimentoIdentificacao"};
    	
    	
    	StringBuilder sqlProjection1 = new StringBuilder(300);
    	sqlProjection1.append(" CASE WHEN pac1_.SEXO = 'F' THEN 'Feminino' ")
		    .append("                WHEN pac1_.SEXO = 'M' THEN 'Masculino' ")
		    .append("                ELSE null END sexoIdentificacao ");
    	
    	StringBuilder sqlProjection2 = new StringBuilder(300);
    	sqlProjection2.append(" CASE WHEN {alias}.PRESSAO_ART_MEDIA = '0' THEN '> 29 mmHg' ")
            .append("                WHEN {alias}.PRESSAO_ART_MEDIA = '9' THEN '20 - 29 mmHg' ")
            .append("                WHEN {alias}.PRESSAO_ART_MEDIA = '19' THEN '< 20mmHg' ")
            .append("                ELSE null END pressaoArterialMediaFator ");
    	
    	StringBuilder sqlProjection3 = new StringBuilder(300);
    	sqlProjection3.append(" CASE WHEN {alias}.APGAR5 = '0' THEN '>= 7' ")
            .append("                WHEN {alias}.APGAR5 = '18' THEN '<7' ")
            .append("                ELSE null END apgarFator ");
    	
    	StringBuilder sqlProjection4 = new StringBuilder(300);
    	sqlProjection4.append(" CASE WHEN {alias}.CONVULSOES_MULTIPLAS = '0' THEN 'Não' ")
            .append("                WHEN {alias}.CONVULSOES_MULTIPLAS = '19' THEN 'Sim' ")
            .append("                ELSE null END convulsoesMultiplasFator ");
    	
    	StringBuilder sqlProjection5 = new StringBuilder(300);
    	sqlProjection5.append(" CASE WHEN {alias}.PESO_AO_NASCER = '0' THEN '> 999 g' ")
            .append("                WHEN {alias}.PESO_AO_NASCER = '10' THEN '750 - 999 g' ")
            .append("                WHEN {alias}.PESO_AO_NASCER = '17' THEN '< 750 g' ")
            .append("                ELSE null END pesoNascimentoFator ");
    	
    	StringBuilder sqlProjection6 = new StringBuilder(300);
    	sqlProjection6.append(" CASE WHEN {alias}.PH_DO_SANGUE = '0' THEN '> 7,19' ")
            .append("                WHEN {alias}.PH_DO_SANGUE = '7' THEN '7,10 - 7,19' ")
            .append("                WHEN {alias}.PH_DO_SANGUE = '16' THEN '< 7,10' ")
            .append("                ELSE null END phSangueFator ");
    	
    	StringBuilder sqlProjection7 = new StringBuilder(300);
    	sqlProjection7.append(" CASE WHEN {alias}.PIG = '0' THEN 'Não' ")
            .append("                WHEN {alias}.PIG = '12' THEN 'Sim' ")
            .append("                ELSE null END pigFator ");
    	
    	StringBuilder sqlProjection8 = new StringBuilder(400);
    	sqlProjection8.append(" CASE WHEN {alias}.RAZAO_PO2_POR_FIO2 = '0' THEN '> 2,49' ")
            .append("                WHEN {alias}.RAZAO_PO2_POR_FIO2 = '5' THEN '1 - 2,49' ")
            .append("                WHEN {alias}.RAZAO_PO2_POR_FIO2 = '16' THEN '0,3 - 0,99' ")
            .append("                WHEN {alias}.RAZAO_PO2_POR_FIO2 = '28' THEN '< 0,3' ")
            .append("                ELSE null END razaoPo2Fi2Fator  ");
    	
    	StringBuilder sqlProjection9 = new StringBuilder(300);
    	sqlProjection9.append(" CASE WHEN {alias}.TEMPERATURA = '0' THEN '> 35,6 ºC' ")
            .append("                WHEN {alias}.TEMPERATURA = '8' THEN '35 - 35,6 ºC' ")
            .append("                WHEN {alias}.TEMPERATURA = '15' THEN '< 35 ºC' ")
            .append("                ELSE null END temperaturaFator ");
    	
    	StringBuilder sqlProjection10 = new StringBuilder(300);
    	sqlProjection10.append(" CASE WHEN {alias}.VOL_URINARIO = '0' THEN '> 0,9 ml / kg / h' ")
            .append("                 WHEN {alias}.VOL_URINARIO = '5' THEN '0,1 - 0,9 ml / kg / h' ")
            .append("                 WHEN {alias}.VOL_URINARIO = '18' THEN '< 0,1 ml / kg / h' ")
            .append("                 ELSE null END volumeUrinarioFator ");
    	
    	StringBuilder sqlProjection11 = new StringBuilder(300);
    	sqlProjection11.append(" COALESCE(atp2_.ALTURA,0)||' cm'  alturaNascimentoIdentificacao ");

    	
         
    	DetachedCriteria criteria = DetachedCriteria.forClass(McoSnappes.class, "SNA");
    	criteria.createAlias(McoSnappes.Fields.PACIENTE.toString(), "PAC");
    	criteria.createAlias("PAC."+AipPacientes.Fields.AIP_ALTURA_PACIENTESES.toString(), "ATP", JoinType.LEFT_OUTER_JOIN);
    	
    	criteria.add(Restrictions.eq(McoSnappes.Fields.SEQUENCE.toString(), seqp));
    	criteria.add(Restrictions.eq(McoSnappes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

    	criteria.setProjection(Projections.distinct(Projections.projectionList()
    			.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()), 
    					RelatorioSnappeVO.Fields.NOME_IDENTIFICACAO.toString())
    			.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), 
    					RelatorioSnappeVO.Fields.PRONTUARIO.toString())
    			.add(Projections.property("PAC."+AipPacientes.Fields.DATA_NASCIMENTO.toString()), 
    					RelatorioSnappeVO.Fields.DATA_NASCIMENTO.toString())
    			.add(Projections.property("PAC."+AipPacientes.Fields.QRT_NUMERO.toString()),
    					RelatorioSnappeVO.Fields.LEITO_IDENTIFICACAO.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.CRIADO_EM.toString()), 
    					RelatorioSnappeVO.Fields.CRIADO_EM.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.PRESSAO_ART_MEDIA_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.PRESSAO_ARTERIAL_MEDIA_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.APGAR5_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.APGAR_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.CONVULSOES_MULTIPLAS_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.CONVULSOES_MULTIPLAS_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.PESO_AO_NASCER_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.PESO_NASCIMENTO_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.PH_DO_SANGUE_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.PH_SANGUE_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.PIG_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.PIG_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.TEMPERATURA_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.TEMPERATURA_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.RAZAO_PO2_POR_FIO2_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.RAZAO_PO2_FI2_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.VOL_URINARIO_CODIGO.toString()), 
    					RelatorioSnappeVO.Fields.VOLUME_URINARIO_ESCORE.toString())
    			.add(Projections.property("SNA."+McoSnappes.Fields.ESCORE_SNAPPE_II.toString()), 
    					RelatorioSnappeVO.Fields.TOTAL_ESCORE.toString())
    			.add(Projections.sqlProjection(sqlProjection1.toString(),
						sexo, type)) 
				.add(Projections.sqlProjection(sqlProjection2.toString(),
						pressaoArterialMedia, type)) 
				.add(Projections.sqlProjection(sqlProjection3.toString(),
						apgar5, type))
				.add(Projections.sqlProjection(sqlProjection4.toString(),
						convulsoesMultiplas, type))
				.add(Projections.sqlProjection(sqlProjection5.toString(),
						pesoAoNascer, type))
    			.add(Projections.sqlProjection(sqlProjection6.toString(),
    					phDoSangue, type))
    			.add(Projections.sqlProjection(sqlProjection7.toString(),
    					pig, type))
				.add(Projections.sqlProjection(sqlProjection8.toString(),
						razaoPo2PorFio2, type))
				.add(Projections.sqlProjection(sqlProjection9.toString(),
						temperatura, type))
				.add(Projections.sqlProjection(sqlProjection10.toString(),
						volumeUrinario, type))
				.add(Projections.sqlProjection(sqlProjection11.toString(),
						alturaNascimentoIdentificacao, type))
    			));
    	
    	criteria.setResultTransformer(Transformers.aliasToBean(RelatorioSnappeVO.class));
    	return (RelatorioSnappeVO) executeCriteriaUniqueResult(criteria);
    }

}
