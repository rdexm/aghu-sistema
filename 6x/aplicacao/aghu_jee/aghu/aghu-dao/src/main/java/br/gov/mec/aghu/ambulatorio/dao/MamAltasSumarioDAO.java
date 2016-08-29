package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolImpressaoVO;

public class MamAltasSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAltaSumario> {
	
	private static final long serialVersionUID = 1358222334553319751L;

	public List<MamAltaSumario> pesquisarAltasSumariosParaAltasAmbulatoriais(Integer numeroConsulta) {
		DetachedCriteria criteria1 = getCriteria1PesquisarAltasSumariosParaAltasAmbulatoriais(numeroConsulta,
				DominioIndPendenteDiagnosticos.A,
				DominioIndPendenteDiagnosticos.E,
				DominioIndPendenteDiagnosticos.V);
		
		List<MamAltaSumario> list1 = executeCriteria(criteria1);
		
		DetachedCriteria criteria2 = getCriteria2PesquisarAltasSumariosParaAltasAmbulatoriais(numeroConsulta);
		List<MamAltaSumario> list2 = executeCriteria(criteria2);
		
		list1.addAll(list2);
		return list1;
	}

	private DetachedCriteria getCriteria2PesquisarAltasSumariosParaAltasAmbulatoriais(
			Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.P));
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.MAM_ALTA_SUMARIO.toString()));
		
		return criteria;
	}

	private DetachedCriteria getCriteria1PesquisarAltasSumariosParaAltasAmbulatoriais(
			Integer numeroConsulta, DominioIndPendenteDiagnosticos ... indPendentes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.CON_NUMERO.toString(), numeroConsulta));
		
		criteria.add(Restrictions.in(
					MamAltaSumario.Fields.IND_PENDENTE.toString(), 
						indPendentes));
		
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		return criteria;
	}

	public List<AltaAmbulatorialPolImpressaoVO> recuperarAltaAmbuPolImpressaoVO(Long seqMamAltaSumario) {
		
		DetachedCriteria criteria = getCriteriaRecuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property(MamAltaSumario.Fields.SEQ.toString()), AltaAmbulatorialPolImpressaoVO.Fields.ALS_SEQ.toString())
		.add(Projections.property(MamAltaSumario.Fields.CON_NUMERO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.CON_NUMERO.toString())
		.add(Projections.property(MamAltaSumario.Fields.NOME.toString()), AltaAmbulatorialPolImpressaoVO.Fields.NOME.toString())
		.add(Projections.property(MamAltaSumario.Fields.PRONTUARIO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.PRONTUARIO.toString())
		.add(Projections.property(MamAltaSumario.Fields.DT_NASCIMENTO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.DT_NASC.toString())
		.add(Projections.property(MamAltaSumario.Fields.IDADE_ANOS.toString()), AltaAmbulatorialPolImpressaoVO.Fields.IDADE_ANOS.toString())
		.add(Projections.property(MamAltaSumario.Fields.IDADE_MESES.toString()), AltaAmbulatorialPolImpressaoVO.Fields.IDADE_MESES.toString())
		.add(Projections.property(MamAltaSumario.Fields.IDADE_DIAS.toString()), AltaAmbulatorialPolImpressaoVO.Fields.IDADE_DIAS.toString())
		.add(Projections.property(MamAltaSumario.Fields.SEXO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.SEXO.toString())
		.add(Projections.property(MamAltaSumario.Fields.DESC_ESP.toString()), AltaAmbulatorialPolImpressaoVO.Fields.DESC_AGENDA.toString())
		.add(Projections.property(MamAltaSumario.Fields.DESC_ESP_PAI.toString()), AltaAmbulatorialPolImpressaoVO.Fields.DESC_ESPECIALIDADE.toString())
		.add(Projections.property(MamAltaSumario.Fields.DESC_EQUIPE.toString()), AltaAmbulatorialPolImpressaoVO.Fields.DESC_EQUIPE.toString())
		.add(Projections.property(MamAltaSumario.Fields.DESTINO_ALTA.toString()), AltaAmbulatorialPolImpressaoVO.Fields.DESTINO_ALTA.toString())
		.add(Projections.property(MamAltaSumario.Fields.RETORNO_AGENDA.toString()), AltaAmbulatorialPolImpressaoVO.Fields.RETORNO_AGENDA.toString())
		.add(Projections.property(MamAltaSumario.Fields.DESC_ESP_DESTINO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.DESC_ESP_DESTINO.toString())
		.add(Projections.property(MamAltaSumario.Fields.OBSERVACAO_DESTINO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.OBSERVACAO_DESTINO.toString())
		.add(Projections.property(MamAltaSumario.Fields.SERVIDOR_VALIDA_MATRICULA.toString()), AltaAmbulatorialPolImpressaoVO.Fields.MATRICULA.toString())
		.add(Projections.property(MamAltaSumario.Fields.SERVIDOR_VALIDA_VINCODIGO.toString()), AltaAmbulatorialPolImpressaoVO.Fields.VIN_CODIGO.toString());
		
		criteria.setProjection(projection);	
		
		criteria.setResultTransformer(Transformers.aliasToBean(AltaAmbulatorialPolImpressaoVO.class));
		
		return executeCriteria(criteria);	
		
	}

	private DetachedCriteria getCriteriaRecuperarAltaAmbuPolImpressaoVO(
			Long seqMamAltaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.SEQ.toString(), seqMamAltaSumario));
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		return criteria;
	}

	public Boolean verificarExibicaoNodoAltasAmbulatoriais(Integer codPaciente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.PACIENTE.toString() + "."+ AipPacientes.Fields.CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.in(MamAltaSumario.Fields.IND_PENDENTE
				.toString(), Arrays.asList(DominioIndPendenteDiagnosticos.A,
				DominioIndPendenteDiagnosticos.E,
				DominioIndPendenteDiagnosticos.V)));
		
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		
		boolean result = executeCriteriaExists(criteria);
		if(result) {
			return Boolean.TRUE;
		}else{
			DetachedCriteria criteria2 = getCriteriaMamAltaSumDtValidaNullAndAlsSeqNull(codPaciente, DominioIndPendenteDiagnosticos.P);
			
			return executeCriteriaExists(criteria2);
		}
	}

	private DetachedCriteria getCriteriaMamAltaSumDtValidaNullAndAlsSeqNull(
			Integer codPaciente, DominioIndPendenteDiagnosticos indPendenteDiag) {
		DetachedCriteria criteria2 = DetachedCriteria.forClass(MamAltaSumario.class);
		criteria2.add(Restrictions.eq(MamAltaSumario.Fields.PACIENTE.toString() + "."+ AipPacientes.Fields.CODIGO.toString(), codPaciente));
		criteria2.add(Restrictions.eq(MamAltaSumario.Fields.IND_PENDENTE.toString(), indPendenteDiag));
		
		criteria2.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria2.add(Restrictions.isNull(MamAltaSumario.Fields.MAM_ALTA_SUMARIO.toString()));
		return criteria2;
	}

	public List<MamAltaSumario> pesquisarMamAltaSumarioDtValidaNullByConsulta(Integer conNumero, DominioIndPendenteDiagnosticos ... indPendentes){
		DetachedCriteria criteria = getCriteria1PesquisarAltasSumariosParaAltasAmbulatoriais(conNumero, indPendentes);
		return executeCriteria(criteria);
	}

	public List<MamAltaSumario> pesquisarMamAltaSumarioDtValidaNullAndAlsSeqNull(
			Integer conNumero, DominioIndPendenteDiagnosticos indPendenteDiag) {
		DetachedCriteria criteria = getCriteriaMamAltaSumDtValidaNullAndAlsSeqNull(conNumero, indPendenteDiag);
		return executeCriteria(criteria);
	}

	public Long recuperarAltaAmbuPolImpressaoVOCount(Long seqMamAltaSumario) {
		
		DetachedCriteria criteria = getCriteriaRecuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);
		
		return executeCriteriaCount(criteria);	
	}
	
	public MamAltaSumario pesquisarAltasSumariosPorNumeroConsulta(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.or(
				Restrictions.in(MamAltaSumario.Fields.IND_PENDENTE.toString(),
						Arrays.asList(DominioIndPendenteDiagnosticos.A,
						DominioIndPendenteDiagnosticos.E,
						DominioIndPendenteDiagnosticos.V)), 
				Restrictions.and(
						Restrictions.eq(MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.P), 
						Restrictions.isNull(MamAltaSumario.Fields.MAM_ALTA_SUMARIO.toString()))));
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		return (MamAltaSumario) executeCriteriaUniqueResult(criteria);
	}

	public List<MamAltaSumario> verificarExisteAltaSumario(Integer conNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		
		List<DominioIndPendenteDiagnosticos> indPendente = new ArrayList<DominioIndPendenteDiagnosticos>();
		indPendente.add(DominioIndPendenteDiagnosticos.V);
		indPendente.add(DominioIndPendenteDiagnosticos.P);
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(MamAltaSumario.Fields.IND_PENDENTE.toString(), indPendente));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamAltaSumario buscarAltaSumarioPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);

		criteria.add(Restrictions.eq(MamAltaSumario.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamAltaSumario> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
