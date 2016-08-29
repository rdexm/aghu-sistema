package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de FORMS para #24297 - Mapeamento de salas
 * @author fpalma
 *
 */
@Stateless
public class MapeamentoSalasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MapeamentoSalasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;


	@EJB
	private MbcCaractSalaEspRN mbcCaractSalaEspRN;

	@EJB
	private MbcCaracteristicaSalaCirgRN mbcCaracteristicaSalaCirgRN;

	private static final long serialVersionUID = -6914505075715519852L;
	
	public enum MapeamentoSalasONExceptionCode implements BusinessExceptionCode {
		MBC_01350, MBC_01351_1, MBC_00021_1, MENSAGEM_ERRO_SOMA_PERCENTUAL, MBC_01351, MBC_00407
	}

	public void gravarMbcCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) throws BaseException {
		//ON7
		validarChecksInstOperacAntesGravar(caracteristicaSalaCirg);
		//ON8
		validarConfiguracaoDuplicada(caracteristicaSalaCirg);
		
		if(caracteristicaSalaCirg.getSeq() == null) {
			getMbcCaracteristicaSalaCirgRN().inserir(caracteristicaSalaCirg);
		} else {
			getMbcCaracteristicaSalaCirgRN().atualizar(caracteristicaSalaCirg);
		}
	}
	
	public void gravarMbcCaractSalaEsp(MbcCaractSalaEsp caractSalaEsp) throws BaseException {
		//ON9
		validarPercentuais(caractSalaEsp);
		//ON11
		validarSalaOperacional(caractSalaEsp);
		
		//melhoria
		validarSalaUrgencias(caractSalaEsp);
		
		if(caractSalaEsp.getId() == null) {
			MbcCaractSalaEspId id = new MbcCaractSalaEspId();
			id.setCasSeq(caractSalaEsp.getMbcCaracteristicaSalaCirg().getSeq());
			id.setEspSeq(caractSalaEsp.getAghEspecialidades().getSeq());
			id.setSeqp(getMbcCaractSalaEspDAO().buscarProximoSeqp(id.getCasSeq(), id.getEspSeq()));
			caractSalaEsp.setId(id);
			getMbcCaractSalaEspRN().inserir(caractSalaEsp);
		} else {
			getMbcCaractSalaEspRN().atualizar(caractSalaEsp);
		}
	}
	
	/**
	 * ON7
	 * @param caracteristicaSalaCirg
	 * @throws ApplicationBusinessException
	 */
	protected void validarChecksInstOperacAntesGravar(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) throws ApplicationBusinessException {
		if(!caracteristicaSalaCirg.getSituacao().isAtivo() && caracteristicaSalaCirg.getIndDisponivel()) {
			throw new ApplicationBusinessException(MapeamentoSalasONExceptionCode.MBC_01350);
		} else if(!caracteristicaSalaCirg.getIndDisponivel() && (caracteristicaSalaCirg.getSeq() != null
				&& getMbcCaractSalaEspDAO().pesquisarQtdeCaractSalaEspAtivaPorCaractSalaCirg(caracteristicaSalaCirg.getSeq()) > 0)) {
			//Não é possível atualizar enquanto existir especialidades vinculadas ativas.
			throw new ApplicationBusinessException(MapeamentoSalasONExceptionCode.MBC_01351_1);
		}
	}
	
	/**
	 * ON8
	 * @param caracteristicaSalaCirg
	 * @throws ApplicationBusinessException
	 */
	protected void validarConfiguracaoDuplicada(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) throws ApplicationBusinessException {
		MbcCaracteristicaSalaCirg mbcExiste = getMbcCaracteristicaSalaCirgDAO().buscarCaracteristicaComMesmaConfiguracao(caracteristicaSalaCirg);
		if(mbcExiste != null) {
			//Já existe característica com esta configuração de turno, dia da semana e sala.
			throw new ApplicationBusinessException(MapeamentoSalasONExceptionCode.MBC_00021_1);
		}
	}
	
	/**
	 * ON9
	 * @param caractSalaEsp
	 * @throws ApplicationBusinessException
	 */
	protected void validarPercentuais(MbcCaractSalaEsp caractSalaEsp) throws ApplicationBusinessException {
		List<MbcCaractSalaEsp> listaEsp = getMbcCaractSalaEspDAO().pesquisarOutrasEspecialidadesAtivasPorCaract(caractSalaEsp);
		Integer somaPercentuais = caractSalaEsp.getPercentualReserva().intValue();
		for(MbcCaractSalaEsp item : listaEsp) {
			somaPercentuais = somaPercentuais + item.getPercentualReserva();
		}
		if(somaPercentuais < 0 || somaPercentuais > 100) {
			throw new ApplicationBusinessException(MapeamentoSalasONExceptionCode.MENSAGEM_ERRO_SOMA_PERCENTUAL);
		}
	}
	
	/**
	 * ON11
	 * @param caractSalaEsp
	 * @throws ApplicationBusinessException
	 */
	protected void validarSalaOperacional(MbcCaractSalaEsp caractSalaEsp) throws ApplicationBusinessException {
		if(DominioSituacao.A.equals(caractSalaEsp.getIndSituacao()) &&
				!caractSalaEsp.getMbcCaracteristicaSalaCirg().getIndDisponivel()) {
			throw new ApplicationBusinessException(MapeamentoSalasONExceptionCode.MBC_01351);
		}
	}
	
	
	/**
	 * Melhoria validar se a sala não é de urgencias 
	 * se for sala de urgencia  a situação da caract sala deve ser Inativa
	 * @param caractSalaEsp
	 * @throws ApplicationBusinessException
	 */
	protected void validarSalaUrgencias(MbcCaractSalaEsp caractSalaEsp) throws ApplicationBusinessException {
		if(caractSalaEsp.getMbcCaracteristicaSalaCirg().getIndUrgencia()) {
			if(DominioSituacao.A.equals(caractSalaEsp.getIndSituacao())) {
				throw new ApplicationBusinessException(MapeamentoSalasONExceptionCode.MBC_00407);
			}
		}
	}
	
	
	/*
	 * Getters Facades, RNs e DAOs
	 */
	protected MbcCaracteristicaSalaCirgRN getMbcCaracteristicaSalaCirgRN() {
		return mbcCaracteristicaSalaCirgRN;
	}
	
	protected MbcCaractSalaEspRN getMbcCaractSalaEspRN() {
		return mbcCaractSalaEspRN;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}
	
}
