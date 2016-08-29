package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoPlacar;
import br.gov.mec.aghu.perinatologia.dao.McoPlacarDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
/**
 * @author israel.haas
 */
@Stateless
public class McoPlacarRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoPlacarDAO mcoPlacarDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum McoPlacarRNExceptionCode implements BusinessExceptionCode {
		ERRO_DATA_PREVISTA_24HORAS
	}
	
	public void atualizarPlacar(McoPlacar placar, McoPlacar placarOriginal) throws ApplicationBusinessException {
		this.preAtualizarMcoPlacar(placar, placarOriginal);
		this.mcoPlacarDAO.atualizar(placar);
	}
	
	/**
	 * @ORADB MCO_PLACARS.MCOT_PLR_BRU
	 * @param placar
	 * @param placarOriginal
	 * @throws ApplicationBusinessException
	 */
	public void preAtualizarMcoPlacar(McoPlacar placar, McoPlacar placarOriginal) throws ApplicationBusinessException {
		if (CoreUtil.modificados(placar.getIndSituacao(), placarOriginal.getIndSituacao())) {
			placar.setDthrExclusao(new Date());
			placar.setSerVinCodigoExcluido(usuario.getVinculo());
			placar.setSerMatriculaExcluido(usuario.getMatricula());
			
		} else {
			placar.setCriadoEm(new Date());
			placar.setSerVinCodigo(usuario.getVinculo());
			placar.setSerMatricula(usuario.getMatricula());
		}
		this.validarDtPrevisaoDtInterrupcao(placar, placarOriginal);
	}
	
	/**
	 * @ORADB MCOK_PLR_RN.RN_PLRP_VER_PREV_NAS
	 * @param placar
	 * @param placarOriginal
	 * @throws ApplicationBusinessException
	 */
	public void validarDtPrevisaoDtInterrupcao(McoPlacar placar, McoPlacar placarOriginal) throws ApplicationBusinessException {
		if (CoreUtil.modificados(placar.getPrevisaoNasc(), placarOriginal.getPrevisaoNasc())
				|| CoreUtil.modificados(placar.getDataProvInterrup(), placarOriginal.getDataProvInterrup())) {
			
			if (placar.getDataProvInterrup() == null && placar.getPrevisaoNasc().equals(">24")) {
				throw new ApplicationBusinessException(McoPlacarRNExceptionCode.ERRO_DATA_PREVISTA_24HORAS);
			}
		}
	}
}
