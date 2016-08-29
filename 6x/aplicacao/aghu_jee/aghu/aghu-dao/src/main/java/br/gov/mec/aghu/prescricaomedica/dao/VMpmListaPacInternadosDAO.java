package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusAltaObito;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusAnamneseEvolucao;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusCertificaoDigital;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusEvolucao;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusExamesNaoVistos;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusPacientePesquisa;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusPendenciaDocumento;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusPrescricao;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusSumarioAlta;
import br.gov.mec.aghu.view.VMpmListaPacInternados;

public class VMpmListaPacInternadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmListaPacInternados> {

    /**
     * '
     */
    private static final long serialVersionUID = -4402738573350988242L;

    /**
     * @param filtro
     * @return
     */
    public List<VMpmListaPacInternados> pesquisaVMpmListaPacInternadosPorServidor(RapServidores servidor) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(VMpmListaPacInternados.class);
    	criteria.add(Restrictions.eq(VMpmListaPacInternados.Fields.SERVIDOR.toString(), servidor));
    	
    	return executeCriteria(criteria);
    }
    
    @SuppressWarnings("PMD.NPathComplexity")
    public List<PacienteListaProfissionalVO> buscaListaPacientesInternados(RapServidores servidor) {
    	String sqlNative = nativeQuery(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
    	SQLQuery query = this.createSQLQuery(sqlNative);
    	    	
		@SuppressWarnings("unchecked")
		List<Object[]> listOfArray = query.list();
		List<PacienteListaProfissionalVO> listReturn = new LinkedList<>();
    	
		if (listOfArray != null && listOfArray.size() > 0) {
			Iterator<Object[]> itRowOfResultSet = listOfArray.iterator();
			
			while (itRowOfResultSet.hasNext()) {
				Object[] row = itRowOfResultSet.next();
				PacienteListaProfissionalVO vo = new PacienteListaProfissionalVO();
				
//				x.ATD_SEQ
				vo.setAtdSeq((Integer)row[0]);
//				x.PRONTUARIO
				vo.setProntuario(String.valueOf(row[1]));
//				x.PAC_CODIGO
				vo.setPacCodigo((Integer)row[2]);
//				x.NOME
				vo.setNome((String)row[3]);
//				x.nome_social
				if (StringUtils.isNotBlank((String)row[4])) {
					vo.setNomeSocial((String)row[4]);
				}
//				x.LOCAL
				if (StringUtils.isNotBlank((String)row[5])) {
					vo.setLocal(StringUtils.substring((String)row[5], 0, 8));
				}
//				x.DATA_NASCIMENTO
				vo.setDataNascimento((Date)row[6]);
//				x.NOME_RESPONSAVEL
				vo.setNomeResponsavel((String)row[7]);
//				x.DATA_INICIO_ATENDIMENTO
				vo.setDataInicioAtendimento((Date)row[8]);
//				x.DATA_FIM_ATENDIMENTO
				vo.setDataFimAtendimento((Date)row[9]);
//				x.STATUS_PRESCRICAO
				if (StringUtils.isNotBlank((String)row[10])){
					vo.setStatusPrescricao(StatusPrescricao.valueOf((String)row[10]));
	        	}
//				x.STATUS_SUMARIO_ALTA StatusSumarioAlta
				if (StringUtils.isNotBlank((String)row[11])) {
					vo.setStatusSumarioAlta(StatusSumarioAlta.valueOf((String)row[11]));
				}
//				x.STATUS_PENDENCIA_DOCUMENTO StatusPendenciaDocumento
				if (StringUtils.isNotBlank((String)row[12])) {
					vo.setStatusPendenciaDocumento(StatusPendenciaDocumento.valueOf((String)row[12]));
				}
//				x.STATUS_CERTIFICACAO_DIGITAL 
				if (StringUtils.isNotBlank((String)row[13])) {
					vo.setStatusCertificacaoDigital(StatusCertificaoDigital.valueOf((String)row[13]));
				}
//				x.STATUS_EXAMES_NAO_VISTOS 
				if (StringUtils.isNotBlank((String)row[14])) {
					vo.setStatusExamesNaoVistos(StatusExamesNaoVistos.valueOf((String)row[14]));
				}
//				x.STATUS_PACIENTE_PESQUISA 
				if (StringUtils.isNotBlank((String)row[15])) {
					vo.setStatusPacientePesquisa(StatusPacientePesquisa.valueOf((String)row[15]));
				}
//				x.STATUS_EVOLUCAO 
				if (StringUtils.isNotBlank((String)row[16])) {
					vo.setStatusEvolucao(StatusEvolucao.valueOf((String)row[16]));
				}
//				x.LABEL_ALTA 
				if (StringUtils.isNotBlank((String)row[17])) {
					vo.setLabelAlta(StatusAltaObito.valueOf((String)row[17]));
				}
//				x.LABEL_OBITO StatusAltaObito
				if (StringUtils.isNotBlank((String)row[18])) {
					vo.setLabelObito(StatusAltaObito.valueOf((String)row[18]));
				}
//				x.DISABLE_BUTTON_ALTA_OBITO
				if (StringUtils.isNotBlank((String)row[19])) {
					vo.setDisableButtonAltaObito(Boolean.valueOf((String)row[19]));
				}
//				x.DISABLE_BUTTON_PRESCREVER
				if (StringUtils.isNotBlank((String)row[20])) {
					vo.setDisableButtonPrescrever(Boolean.valueOf((String)row[20]));
				}
//				x.POSSUI_PLANO_ALTAS
				if (StringUtils.isNotBlank((String)row[21])) {
					vo.setPossuiPlanoAltas(Boolean.valueOf((String)row[21]));
				}
//				x.IND_GMR
				if (StringUtils.isNotBlank((String)row[22])) {
					vo.setIndGmr(Boolean.valueOf((String)row[22]));
				}
//				x.SER_MATRICULA, x.SER_VIN_CODIGO, x.ATD_SER_MATRICULA, x.ATD_SER_VIN_CODIGO				
//				x.ENABLE_BUTTON_ANAMNESE_EVOLUCAO
				if (StringUtils.isNotBlank((String)row[27])) {
					vo.setEnableButtonAnamneseEvolucao(Boolean.valueOf((String)row[27]));
				}				
//				x.STATUS_ANAMNESE_EVOLUCAO
				if (StringUtils.isNotBlank((String)row[28])) {
					vo.setStatusAnamneseEvolucao(StatusAnamneseEvolucao.valueOf((String)row[28]));
				}
				
				listReturn.add(vo);
			}// while
		}// IF
		
    	return listReturn;
    }
    
    //TODO corrigir SQL para usar fields
    private String nativeQuery(Integer matricula, Short vinculo) {
    	StringBuilder strBuilder = new StringBuilder(3000);
    	
    	strBuilder.append("select \n");
    	strBuilder.append("   x.ATD_SEQ \n");
    	strBuilder.append(",x.PRONTUARIO \n");
    	strBuilder.append(",x.PAC_CODIGO \n");
    	strBuilder.append(",x.NOME \n");
    	strBuilder.append(",x.nome_social \n");
    	strBuilder.append(",x.LOCAL \n");
    	strBuilder.append(",x.DATA_NASCIMENTO \n");
    	strBuilder.append(",x.NOME_RESPONSAVEL \n");
    	strBuilder.append(",x.DATA_INICIO_ATENDIMENTO \n");
    	strBuilder.append(",x.DATA_FIM_ATENDIMENTO \n");
    	strBuilder.append(",x.STATUS_PRESCRICAO \n");
    	strBuilder.append(",x.STATUS_SUMARIO_ALTA \n");
    	strBuilder.append(",x.STATUS_PENDENCIA_DOCUMENTO \n");
    	strBuilder.append(",x.STATUS_CERTIFICACAO_DIGITAL \n");
    	strBuilder.append(",x.STATUS_EXAMES_NAO_VISTOS \n");
    	strBuilder.append(",x.STATUS_PACIENTE_PESQUISA \n");
    	strBuilder.append(",x.STATUS_EVOLUCAO \n");
    	strBuilder.append(",x.LABEL_ALTA \n");
    	strBuilder.append(",x.LABEL_OBITO \n");
    	strBuilder.append(",x.DISABLE_BUTTON_ALTA_OBITO \n");
    	strBuilder.append(",x.DISABLE_BUTTON_PRESCREVER \n");
    	strBuilder.append(",x.POSSUI_PLANO_ALTAS \n");
    	strBuilder.append(",x.IND_GMR \n");
    	strBuilder.append(",x.SER_MATRICULA \n");
    	strBuilder.append(",x.SER_VIN_CODIGO \n");
    	strBuilder.append(",x.ATD_SER_MATRICULA \n");
    	strBuilder.append(",x.ATD_SER_VIN_CODIGO \n");
    	strBuilder.append(", case when (x.IND_PAC_ATENDIMENTO = 'S' and x.DISABLE_BUTTON_ALTA_OBITO = 'false' and x.DISABLE_BUTTON_PRESCREVER = 'false' \n");
    	strBuilder.append("    and x.TEM_UNF_CARACT_ANAMNESE_EVOLUCAO = 'true') then 'true' else 'false' end as ENABLE_BUTTON_ANAMNESE_EVOLUCAO \n");
    	strBuilder.append(", CASE WHEN (x.IND_PAC_ATENDIMENTO = 'S' AND x.DISABLE_BUTTON_ALTA_OBITO = 'false' AND x.DISABLE_BUTTON_PRESCREVER = 'false' \n");
    	strBuilder.append("    AND x.TEM_UNF_CARACT_ANAMNESE_EVOLUCAO = 'true') THEN x.STATUS_ANAMNESE_EVOLUCAO ELSE null \n");
    	strBuilder.append(" end as STATUS_ANAMNESE_EVOLUCAO \n");
    	strBuilder.append("from (	\n");
    	strBuilder.append("		select * from agh.v_mpm_lista_pac_internados atd ");
    	strBuilder.append("		WHERE atd.ind_sit_sumario_alta = 'P' \n");
    	strBuilder.append("		AND atd.origem IN ('I','H','U','N') \n");
    	strBuilder.append("		AND ( \n");
    	//strBuilder.append(" -- and dos exists")
    	strBuilder.append("			(\n");
    	strBuilder.append("				(\n");
    	strBuilder.append("					(EXISTS");
    	strBuilder.append("						(SELECT 1");
    	strBuilder.append("	FROM AGH.MPM_LISTA_SERV_ESPECIALIDADES lse");
    	strBuilder.append("	WHERE ( lse.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	AND lse.SER_VIN_CODIGO=").append(vinculo);
    	strBuilder.append("	AND atd.ESP_SEQ=lse.ESP_SEQ )");
    	strBuilder.append("	)");
    	strBuilder.append("	)\n");
    	strBuilder.append("					OR (EXISTS");
    	strBuilder.append("	(SELECT  1");
    	strBuilder.append("	FROM AGH.MPM_SERVIDOR_UNID_FUNCIONAIS suf");
    	strBuilder.append("	WHERE ( suf.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	 AND suf.SER_VIN_CODIGO=").append(vinculo);
    	strBuilder.append("	AND atd.UNF_SEQ=suf.UNF_SEQ)");
    	strBuilder.append("	) ");
    	strBuilder.append("	)\n");
    	strBuilder.append("					OR (EXISTS");
    	strBuilder.append("	(SELECT 1");
    	strBuilder.append("	FROM AGH.MPM_LISTA_SERV_EQUIPES lsq");
    	strBuilder.append("	LEFT OUTER JOIN AGH.AGH_EQUIPES aghequipes ON lsq.EQP_SEQ=aghequipes.SEQ");
    	strBuilder.append("	LEFT OUTER JOIN AGH.RAP_SERVIDORES rsv ON lsq.SER_MATRICULA=rsv.MATRICULA ");
    	strBuilder.append("	AND lsq.SER_VIN_CODIGO =rsv.VIN_CODIGO");
    	strBuilder.append("	WHERE lsq.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	AND lsq.SER_VIN_CODIGO=").append(vinculo);
    	strBuilder.append("	AND lsq.SER_MATRICULA=atd.ser_matricula ");
    	strBuilder.append("	AND lsq.SER_VIN_CODIGO=atd.ser_vin_codigo");
    	strBuilder.append("	UNION");
    	strBuilder.append("	SELECT 	1");
    	strBuilder.append("	FROM AGH.MPM_LISTA_SERV_RESPONSAVEIS lsr");
    	strBuilder.append("	WHERE lsr.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	AND lsr.SER_VIN_CODIGO =").append(vinculo);
    	strBuilder.append("	AND lsr.SER_MATRICULA=atd.ser_matricula ");
    	strBuilder.append("	AND lsr.SER_VIN_CODIGO=atd.ser_vin_codigo");
    	strBuilder.append("	) ");
    	strBuilder.append("	)\n");
    	strBuilder.append("					OR ( EXISTS");
    	strBuilder.append("	(SELECT 1");
    	strBuilder.append("	FROM AGH.MPM_PAC_ATEND_PROFISSIONAIS pap");
    	strBuilder.append("	WHERE ( pap.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	AND pap.SER_VIN_CODIGO=").append(vinculo);
    	strBuilder.append("	AND pap.ATD_SEQ=atd.atd_SEQ )");
    	strBuilder.append("	) ");
    	strBuilder.append("	)\n");
    	strBuilder.append("					OR ( EXISTS");
    	strBuilder.append("	(SELECT 1");
    	strBuilder.append("	FROM AGH.MPM_LISTA_PAC_CPAS lpca");
    	strBuilder.append("	WHERE lpca.IND_PAC_CPA  = 'S' ");
    	strBuilder.append("	AND lpca.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	AND lpca.SER_VIN_CODIGO =").append(vinculo);
    	strBuilder.append("	AND lpca.IND_PAC_CPA = atd.ind_pac_cpa");
    	strBuilder.append("	)");
    	strBuilder.append("	)\n");
    	strBuilder.append("				)");
    	strBuilder.append(" AND atd.IND_PAC_ATENDIMENTO='S' ");
    	strBuilder.append("			)\n");
    	strBuilder.append("			OR EXISTS (	");
    	strBuilder.append(" SELECT 1");
    	strBuilder.append(" FROM AGH.MPM_LISTA_SERV_SUMR_ALTAS lssa");
    	strBuilder.append(" WHERE ( lssa.SER_MATRICULA=").append(matricula);
    	strBuilder.append("	AND lssa.SER_VIN_CODIGO=").append(vinculo);
    	strBuilder.append("	AND lssa.ATD_SEQ=atd.atd_SEQ)");
    	strBuilder.append("	) \n");
    	strBuilder.append("		) \n");
    	//strBuilder.append(" -- and dos exists");
    	strBuilder.append("		ORDER BY atd.IND_PAC_ATENDIMENTO ASC,");
    	strBuilder.append(" atd.LTO_LTO_ID ASC,");
    	strBuilder.append(" atd.QRT_NUMERO ASC,");
    	strBuilder.append(" atd.UNF_SEQ ASC \n");
    	strBuilder.append(") x ");
    	
    	return strBuilder.toString();
    }
    

}
