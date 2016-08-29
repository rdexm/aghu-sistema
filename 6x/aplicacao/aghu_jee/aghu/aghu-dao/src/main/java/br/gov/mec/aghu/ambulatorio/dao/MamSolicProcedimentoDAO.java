package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.RelatorioSolicitacaoProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MamSolicProcedimento;
import br.gov.mec.aghu.model.MamTipoSolProcedimento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamSolicProcedimentoDAO extends BaseDao<MamSolicProcedimento>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094859365670933889L;
	private static final String MSP = "MSP";
	private static final String TSP = "TSP";
	private static final String PONTO = ".";
	private static final String CON = "CON";
	/**
	 * C1
	 * Consulta para verificar se existe algum documento de solicitacao de procedimento a ser impresso para o registro selecionado
	 * #43087
	 */
	public List<MamSolicProcedimento> obterDocumentosSolicitacaoProcedimento(Integer conNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicProcedimento.class, MSP);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.SEQ.toString()),MamSolicProcedimento.Fields.SEQ.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.IND_PENDENTE.toString()),MamSolicProcedimento.Fields.IND_PENDENTE.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.NRO_VIAS.toString()),MamSolicProcedimento.Fields.NRO_VIAS.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.IND_IMPRESSO.toString()),MamSolicProcedimento.Fields.IND_IMPRESSO.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.CON_NUMERO.toString()),MamSolicProcedimento.Fields.CON_NUMERO.toString())
		);
		criteria.add(Restrictions.eq(MSP+PONTO+MamSolicProcedimento.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.isNull(MSP+PONTO+ MamSolicProcedimento.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.or(Restrictions.eq(MamSolicProcedimento.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.V.toString()), Restrictions.eq(MamSolicProcedimento.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.P.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(MamSolicProcedimento.class));	
		return  executeCriteria(criteria);
	}
	/**
	 * #43087
	 * Consulta C3 para retornar dados para popular o relatorio MAM_SOLIC_PROCEDIMENTOS
	 * @param seq corresponde ao seq da C1 obterDocumentosSolicitacaoProcedimento
	 * @return
	 */
	public RelatorioSolicitacaoProcedimentoVO obterDadosRelatorioSolicitacaoProcedimento(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicProcedimento.class, MSP);
		criteria.createAlias(MSP+PONTO+MamSolicProcedimento.Fields.MAM_TIPO_SOL_PROCEDIMENTOS.toString(),TSP,JoinType.INNER_JOIN);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.DESCRICAO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.DESCRICAO.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.JUSTIFICATIVA.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.JUSTIFICATIVA.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.SER_MATRICULA_VALIDA.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.SER_MATRICULA_VALIDA.toString())
				.add(Projections.property(MSP+PONTO+MamSolicProcedimento.Fields.SER_VIN_CODIGO_VALIDA.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.SER_VIN_CODIGO_VALIDA.toString())
				.add(Projections.property(TSP+PONTO+MamTipoSolProcedimento.Fields.DESCRICAO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.DESCRICAO_TIPO.toString())
				.add(Projections.property(TSP+PONTO+MamTipoSolProcedimento.Fields.SEQ.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.SEQ_TIPO.toString())
		);
		
		criteria.add(Restrictions.eq(MSP+PONTO+MamSolicProcedimento.Fields.SEQ.toString(), seq));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioSolicitacaoProcedimentoVO.class));
		return (RelatorioSolicitacaoProcedimentoVO) executeCriteriaUniqueResult(criteria);
	}
	public RelatorioSolicitacaoProcedimentoVO obterInformacoesRelatorioSolicitacaoProcedimento(Integer conNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, CON);
		criteria.createAlias(CON+PONTO+AacConsultas.Fields.PACIENTE.toString(),"PAC",JoinType.INNER_JOIN);
		criteria.createAlias(CON+PONTO+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(),"GRD",JoinType.INNER_JOIN);
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),"ESP",JoinType.INNER_JOIN);
		criteria.createAlias(CON+PONTO+AacConsultas.Fields.FAT_CONVENIO_SAUDE.toString(),"CNV",JoinType.INNER_JOIN);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.NOME.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_NASCIMENTO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.DT_NASCIMENTO.toString())
				.add(Projections.property(CON+PONTO+AacConsultas.Fields.GRD_SEQ.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.GRD_SEQ.toString())
				.add(Projections.property(CON+PONTO+AacConsultas.Fields.CSP_CNV_CODIGO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.CSP_CNV_CODIGO.toString())
				.add(Projections.property(CON+PONTO+AacConsultas.Fields.NUMERO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.CON_NUMERO.toString())
				.add(Projections.property(CON+PONTO+AacConsultas.Fields.DATA_CONSULTA.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.DATA_CONSULTA.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SIGLA.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.SIGLA.toString())
				.add(Projections.property("CNV."+FatConvenioSaude.Fields.DESCRICAO.toString()),RelatorioSolicitacaoProcedimentoVO.Fields.DESCRICAOCNV.toString())
		);
		
		criteria.add(Restrictions.eq(CON+PONTO+AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioSolicitacaoProcedimentoVO.class));
		return (RelatorioSolicitacaoProcedimentoVO)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamSolicProcedimento buscarSolicProcedimentoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicProcedimento.class);

		criteria.add(Restrictions.eq(MamSolicProcedimento.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamSolicProcedimento> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
