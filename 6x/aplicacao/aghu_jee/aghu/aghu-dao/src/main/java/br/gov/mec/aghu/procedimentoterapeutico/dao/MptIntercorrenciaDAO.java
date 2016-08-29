package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptIntercorrencia;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistrarControlePacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;

public class MptIntercorrenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptIntercorrencia> {

	private static final String ALIAS_SES = "SES.";
	private static final String ALIAS_HOR = "HOR.";
	private static final String ALIAS_INT_COM_PONTO = "INT.";
	private static final String ALIAS_INT = "INT";
	private static final long serialVersionUID = 5033981611368152180L;
	
	public List<RegistroIntercorrenciaVO> obterRegistroIntercorrenciaPorPaciente(Integer codigoPaciente){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptIntercorrencia.class, ALIAS_INT);
		
		criteria.createAlias(ALIAS_INT_COM_PONTO + MptIntercorrencia.Fields.TIPO_INTERCORRENCIA.toString(), "TPI");
		criteria.createAlias(ALIAS_INT_COM_PONTO + MptIntercorrencia.Fields.SESSAO.toString(), "SES");
		criteria.createAlias(ALIAS_SES + MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CIC");
		criteria.createAlias("CIC." + MptPrescricaoCiclo.Fields.ATENDIMENTOS.toString(), "ATD");
		criteria.createAlias(ALIAS_SES + MptSessao.Fields.HORARIO_SESSAO.toString(), "HOR");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TPI." + MptTipoIntercorrencia.Fields.DESCRICAO.toString()),
						RegistroIntercorrenciaVO.Fields.DESCRICAO_TIPO_INTERCORRENCIA.toString())
				.add(Projections.property(ALIAS_INT_COM_PONTO + MptIntercorrencia.Fields.DESCRICAO.toString()),RegistroIntercorrenciaVO.Fields.DESCRICAO_INTERCORRENCIA.toString())
				.add(Projections.property(ALIAS_HOR + MptHorarioSessao.Fields.CICLO.toString()),RegistroIntercorrenciaVO.Fields.CICLO.toString())
				.add(Projections.property(ALIAS_HOR + MptHorarioSessao.Fields.DIA.toString()), RegistroIntercorrenciaVO.Fields.DIA.toString())
				.add(Projections.property(ALIAS_HOR + MptHorarioSessao.Fields.DATA_INICIO.toString()),RegistroIntercorrenciaVO.Fields.DATA_INICIO.toString())
				.add(Projections.property("CIC." + MptPrescricaoCiclo.Fields.SEQ.toString()), RegistroIntercorrenciaVO.Fields.CICLO_SEQ.toString())
				.add(Projections.property(ALIAS_INT_COM_PONTO + MptIntercorrencia.Fields.SEQ.toString()), RegistroIntercorrenciaVO.Fields.SEQ_INTERCORRENCIA.toString())
				.add(Projections.property(ALIAS_HOR + MptHorarioSessao.Fields.SEQ.toString()), RegistroIntercorrenciaVO.Fields.SEQ_HORARIO_SESSAO.toString())
				.add(Projections.property(ALIAS_SES + MptSessao.Fields.SEQ.toString()), RegistroIntercorrenciaVO.Fields.SEQ_SESSAO.toString())
				);
		
		if(codigoPaciente != null){
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_COD.toString(), codigoPaciente));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(RegistroIntercorrenciaVO.class));
		return executeCriteria(criteria);
		
	}
	
	public List<RegistrarControlePacienteVO> carregarRegistrosIntercorrencia(Integer pacCodigo, Integer sesSeq){
		DetachedCriteria criteria = criteriaCarregarRegistrosIntercorrencia(pacCodigo, sesSeq);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_INT_COM_PONTO+MptIntercorrencia.Fields.DESCRICAO.toString()).as(RegistrarControlePacienteVO.Fields.INT_DESCRICAO.toString()))
				.add(Projections.property("TPI."+MptTipoIntercorrencia.Fields.DESCRICAO.toString()).as(RegistrarControlePacienteVO.Fields.TPI_DESCRICAO.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RegistrarControlePacienteVO.class));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria criteriaCarregarRegistrosIntercorrencia(Integer pacCodigo, Integer sesSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptIntercorrencia.class, "INT");
		criteria.createAlias(ALIAS_INT_COM_PONTO+MptIntercorrencia.Fields.TIPO_INTERCORRENCIA.toString(), "TPI");
		criteria.createAlias(ALIAS_INT_COM_PONTO+MptIntercorrencia.Fields.SESSAO.toString(), "SES");
		criteria.createAlias(ALIAS_SES+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE");
		criteria.createAlias("PTE."+MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(ALIAS_SES+MptSessao.Fields.SEQ.toString(), sesSeq));
		return criteria;
	}

	
}
