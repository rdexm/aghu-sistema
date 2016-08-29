package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MptTurnoTipoSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptTurnoTipoSessaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;

	private static final long serialVersionUID = 2797194818319163103L;
	
	public enum MptTurnoTipoSessaoRNExceptionCode implements BusinessExceptionCode {
		HFINAL_MAIOR_HINICIAL, CONFLITO_HORARIO_TURNOS;
	}
	
	public void validarMptTurnoTipoSessao(Boolean manha, Boolean tarde, Boolean noite, Date horaInicialManha, Date horaFinalManha,
			Date horaInicialTarde, Date horaFinalTarde, Date horaInicialNoite, Date horaFinalNoite, Short tpsSeq) throws ApplicationBusinessException {
		
		if (manha.equals(Boolean.TRUE)) {
			this.inserirAtualizarMptTurnoTipoSessao(DominioTurno.M, horaInicialManha, horaFinalManha, tpsSeq);
		} else {
			excluirTurnoTipoSessaoEspecifico(DominioTurno.M, tpsSeq);
		}
		
		if (tarde.equals(Boolean.TRUE)) {
			this.inserirAtualizarMptTurnoTipoSessao(DominioTurno.T, horaInicialTarde, horaFinalTarde, tpsSeq);
		} else {
			excluirTurnoTipoSessaoEspecifico(DominioTurno.T, tpsSeq);
		}
		
		if (noite.equals(Boolean.TRUE)) {
			this.inserirAtualizarMptTurnoTipoSessao(DominioTurno.N, horaInicialNoite, horaFinalNoite, tpsSeq);
		} else {
			excluirTurnoTipoSessaoEspecifico(DominioTurno.N, tpsSeq);
		}
	}
	
	private void validarPeriodo(Date horaInicio, Date horaFim) throws ApplicationBusinessException {
		if (DateUtil.validaDataMaiorIgual(horaInicio, horaFim)) {
			throw new ApplicationBusinessException(MptTurnoTipoSessaoRNExceptionCode.HFINAL_MAIOR_HINICIAL);
		}
	}
	
	private void validarConflito(Date horaFinal, Date horaInicialProximoTurno) throws ApplicationBusinessException {
		if (DateUtil.validaDataMaior(horaFinal, horaInicialProximoTurno)) {
			throw new ApplicationBusinessException(MptTurnoTipoSessaoRNExceptionCode.CONFLITO_HORARIO_TURNOS);
		}
	}
	
	public void validarConflitosPeriodo(Boolean manha, Boolean tarde, Boolean noite, Date horaInicialManha, Date horaFinalManha,
			Date horaInicialTarde, Date horaFinalTarde, Date horaInicialNoite, Date horaFinalNoite) throws ApplicationBusinessException {
		
		if (manha.equals(Boolean.TRUE) && tarde.equals(Boolean.TRUE) && noite.equals(Boolean.TRUE)) {
			this.validarConflito(horaFinalManha, horaInicialTarde);
			this.validarConflito(horaFinalManha, horaInicialNoite);
			this.validarConflito(horaFinalTarde, horaInicialNoite);
			
		} else if (manha.equals(Boolean.TRUE) && tarde.equals(Boolean.TRUE)) {
			this.validarConflito(horaFinalManha, horaInicialTarde);
			
		} else if (manha.equals(Boolean.TRUE) && noite.equals(Boolean.TRUE)) {
			this.validarConflito(horaFinalManha, horaInicialNoite);
			
		} else if (tarde.equals(Boolean.TRUE) && noite.equals(Boolean.TRUE)) {
			this.validarConflito(horaFinalTarde, horaInicialNoite);
		}
		
		if (manha.equals(Boolean.TRUE)) {
			this.validarPeriodo(horaInicialManha, horaFinalManha);
		}
		if (tarde.equals(Boolean.TRUE)) {
			this.validarPeriodo(horaInicialTarde, horaFinalTarde);
		}
		if (noite.equals(Boolean.TRUE)) {
			this.validarPeriodo(horaInicialNoite, horaFinalNoite);
		}
	}
	
	public void inserirAtualizarMptTurnoTipoSessao(DominioTurno turno, Date horaInicio, Date horaFim, Short tpsSeq) {
		
		MptTipoSessao tipoSessao = getMptTipoSessaoDAO().obterPorChavePrimaria(tpsSeq);
		if (!getMptTurnoTipoSessaoDAO().verificarExistenciaTurnoTipoSessaoPorTurnoTpsSeq(turno, tpsSeq)) {
			MptTurnoTipoSessao mptTurnoTipoSessao = new MptTurnoTipoSessao();
			mptTurnoTipoSessao.setTipoSessao(tipoSessao);
			mptTurnoTipoSessao.setCriadoEm(new Date());
			mptTurnoTipoSessao.setTurno(turno);
			mptTurnoTipoSessao.setHoraInicio(horaInicio);
			mptTurnoTipoSessao.setHoraFim(horaFim);
			
			getMptTurnoTipoSessaoDAO().persistir(mptTurnoTipoSessao);
			
		} else {
			MptTurnoTipoSessao mptTurnoTipoSessao = getMptTurnoTipoSessaoDAO().obterTurnoTipoSessaoPorTurnoTpsSeq(turno, tpsSeq);
			mptTurnoTipoSessao.setHoraInicio(horaInicio);
			mptTurnoTipoSessao.setHoraFim(horaFim);
			
			getMptTurnoTipoSessaoDAO().merge(mptTurnoTipoSessao);
		}
	}
	
	public void excluirTurnoTipoSessaoEspecifico(DominioTurno turno, Short tpsSeq) {
		MptTurnoTipoSessao mptTurnoTipoSessao = getMptTurnoTipoSessaoDAO().obterTurnoTipoSessaoPorTurnoTpsSeq(turno, tpsSeq);
		if (mptTurnoTipoSessao != null) {
			this.getMptTurnoTipoSessaoDAO().remover(mptTurnoTipoSessao);
		}
	}
	
	public void excluirMptTurnoTipoSessao(Short tpsSeq) {
		List<MptTurnoTipoSessao> listaTurnos = this.getMptTurnoTipoSessaoDAO().obterTurnosPorTipoSessao(tpsSeq);
		
		for (MptTurnoTipoSessao item : listaTurnos) {
			this.getMptTurnoTipoSessaoDAO().remover(item);
		}
	}
	
	public MptTipoSessaoDAO getMptTipoSessaoDAO() {
		return mptTipoSessaoDAO;
	}

	public MptTurnoTipoSessaoDAO getMptTurnoTipoSessaoDAO() {
		return mptTurnoTipoSessaoDAO;
	}
}
