package br.gov.mec.aghu.exames.agendamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.vo.GradeExameVO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;

@Stateless
public class GradeAgendaExameON extends BaseBusiness {


@EJB
private GradeAgendaExameRN gradeAgendaExameRN;

private static final Log LOG = LogFactory.getLog(GradeAgendaExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;

@Inject
private AelExamesDAO aelExamesDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5192430750205334921L;

	public enum GradeAgendaExameONExceptionCode implements BusinessExceptionCode {
		ERRO_GRAVAR_AGENDA_APENAS_UM_PODE_SER_SELECIONADO, ERRO_GRAVAR_AGENDA_UM_DEVE_SER_SELECIONADO, 
		ERRO_EXCLUIR_GRADE_EXAME_COM_HORARIO, ERRO_EXCLUIR_GRADE_EXAME_COM_HORARIO_DISP, AEL_00687, AEL_00689, ERRO_GRADE_INATIVA;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	public void persistirGradeAgendaExame(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		if (grade.getGrupoExame()!=null && grade.getExame()!=null){
			GradeAgendaExameONExceptionCode.ERRO_GRAVAR_AGENDA_APENAS_UM_PODE_SER_SELECIONADO.throwException();
		}else if (grade.getGrupoExame()==null && grade.getExame()==null){
			GradeAgendaExameONExceptionCode.ERRO_GRAVAR_AGENDA_UM_DEVE_SER_SELECIONADO.throwException();
		}
		if (grade.getId()==null){
			getGradeAgendaExameRN().inserirGradeAgendaExame(grade);
		}else{
			getGradeAgendaExameRN().atualizarGradeAgendaExame(grade);
		}
	}
	
	public void removerGradeAgendaExame(AelGradeAgendaExame gradeAgendaExame) throws ApplicationBusinessException{
		if (gradeAgendaExame!=null){
			gradeAgendaExame = this.getAelGradeAgendaExameDAO().merge(gradeAgendaExame);
			if (gradeAgendaExame.getHoraiosGradeExame()!=null && !gradeAgendaExame.getHoraiosGradeExame().isEmpty()){
				GradeAgendaExameONExceptionCode.ERRO_EXCLUIR_GRADE_EXAME_COM_HORARIO.throwException();
			}else if (gradeAgendaExame.getHoraiosExameDisp()!=null && !gradeAgendaExame.getHoraiosExameDisp().isEmpty()){
				GradeAgendaExameONExceptionCode.ERRO_EXCLUIR_GRADE_EXAME_COM_HORARIO_DISP.throwException();
			}else{
				this.getAelGradeAgendaExameDAO().remover(gradeAgendaExame);
				this.getAelGradeAgendaExameDAO().flush();
			}
		}
	}
	
	public List<GradeExameVO> pesquisarGradeExame(String orderProperty, boolean asc,
			Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, 
			RapServidores responsavel){
		List<AelGradeAgendaExame> listaGrade = getAelGradeAgendaExameDAO().pesquisarGradeExame(orderProperty, asc, seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
		List<GradeExameVO> listaRetorno = new ArrayList<GradeExameVO>();
		for(AelGradeAgendaExame grade: listaGrade){
			GradeExameVO gradeExameVO = new GradeExameVO();
			gradeExameVO.setSeq(grade.getId().getSeqp());
			gradeExameVO.setGrade(grade.getId().getUnfSeq());
			if (grade.getSalaExecutoraExames()!=null){
				gradeExameVO.setNumero(grade.getSalaExecutoraExames().getNumero());
			}
			if (grade.getGrupoExame()!=null){
				gradeExameVO.setDescricaoGrupo(grade.getGrupoExame().getDescricao());
			}
			if(grade.getExame()!=null){
				AelExames exameAux = this.getAelExamesDAO().obterPeloId(grade.getExame().getId().getEmaExaSigla());
				gradeExameVO.setDescricaoExame(exameAux.getSigla()+" - "+exameAux.getDescricaoUsual());
			}
			if (grade.getServidor()!=null){
				gradeExameVO.setResponsavel(grade.getServidor().getPessoaFisica().getNome());
			}
			if(grade.getSituacao()!=null){
				gradeExameVO.setSituacao(grade.getSituacao().getDescricao());	
			}
			listaRetorno.add(gradeExameVO);
		}
		return listaRetorno;
	}
	
	public Integer gerarDisponibilidadeHorarios(AelGradeAgendaExame grade, Date dataInicio, Date dataFim, Date dataHoraUltimaGrade) throws BaseException{
		if (dataHoraUltimaGrade!=null && !dataInicio.after(dataHoraUltimaGrade)){
			GradeAgendaExameONExceptionCode.AEL_00687.throwException();
		}
		if (dataInicio.after(dataFim)){
			GradeAgendaExameONExceptionCode.AEL_00689.throwException();
		}
		
		if (grade.getSituacao().equals(DominioSituacao.I)){
			GradeAgendaExameONExceptionCode.ERRO_GRADE_INATIVA.throwException();
		}
		return getGradeAgendaExameRN().gerarDisponibilidadeHorarios(grade, dataInicio, dataFim);
	}
	
	protected GradeAgendaExameRN getGradeAgendaExameRN(){
		return gradeAgendaExameRN;
	}
	
	protected AelExamesDAO getAelExamesDAO(){
		return aelExamesDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO(){
		return aelGradeAgendaExameDAO;
	}

	
}
