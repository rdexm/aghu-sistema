package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioAdequacaoPesoRN;
import br.gov.mec.aghu.emergencia.vo.RegistrarExameFisicoVO;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.perinatologia.dao.McoExameFisicoRnsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestBallardDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestCapurrosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTabAdequacaoPesoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Regras de negócio relacionadas da aba Ext Fisico RN
 * 
 * @author felipe rocha
 * 
 */
@Stateless
public class RegistrarGestacaoAbaExtFisicoRNON extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;


	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	@Inject
	private McoTabAdequacaoPesoDAO mcoTabAdequacaoPesoDAO;
	@Inject
	private McoExameFisicoRnsDAO mcoExameFisicoRnsDAO;
	@Inject
	private IPacienteService pacienteService;
	@Inject
	private McoIddGestCapurrosDAO mcoIddGestCapurrosDAO;
	@Inject
	private McoIddGestBallardDAO mcoIddGestBallardDAO;
	
	private final Integer PESO_METODO = 1800;

	public enum RegistrarGestacaoAbaExtFisicoRNONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, MCO_00579, MCO_00580, MCO_00581, MCO_00647, MCO_00640;
	}
	
	
	public List<RegistrarExameFisicoVO> obterExameFisicoRN(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException{
		Integer seq = 0;
		List<RegistrarExameFisicoVO> listaVO = new LinkedList<RegistrarExameFisicoVO>();
		List<McoRecemNascidos> listarPorGestacao = mcoRecemNascidosDAO.listarPorGestacao(pacCodigo, gsoSeqp);
		for (McoRecemNascidos recemNascidos : listarPorGestacao) {
			RegistrarExameFisicoVO vo = new RegistrarExameFisicoVO();
			Paciente paciente =  obterPacientePorCodigo(recemNascidos.getPaciente().getCodigo());
			McoExameFisicoRns exameFisicoRns = mcoExameFisicoRnsDAO.obterMcoExameFisicoRnPorId(recemNascidos.getId().getGsoPacCodigo(), recemNascidos.getId().getGsoSeqp(), recemNascidos.getId().getSeqp());
			
			if (exameFisicoRns != null) {
				vo.setSeq(seq++);
				vo.setSeqp(exameFisicoRns.getId().getSeqp());
				vo.setPacCodigo(recemNascidos.getPaciente().getCodigo());
				vo.setProntuario(paciente.getProntuario());
				vo.setNome(paciente.getNome());
				vo.setDtHrExameFisico(paciente.getDtNascimento());
				vo.setComp(obterAlturasPaciente(recemNascidos.getPaciente().getCodigo()));
				vo.setPc(exameFisicoRns.getPerCefalico());
				vo.setPt(exameFisicoRns.getPerToracico());
				vo.setCa(exameFisicoRns.getCirAbdominal());
				vo.setFc(exameFisicoRns.getFreqCardiaca());
				vo.setFr(exameFisicoRns.getFreqRespiratoria());
				vo.setTemp(exameFisicoRns.getTemperatura());
				vo.setIdadeGestacionalFinal(exameFisicoRns.getIgFinal());
				vo.setAspGeral(exameFisicoRns.getAspectoGeral());
				vo.setSdm(exameFisicoRns.getSdmSeq());
				vo.setMoro(exameFisicoRns.getMoro());
				vo.setSuccao(exameFisicoRns.getSuccao());
				vo.setFugaAsfixia(exameFisicoRns.getFugaAAsfixia());
				vo.setReptacao(exameFisicoRns.getReptacao());
				vo.setMarcha(exameFisicoRns.getMarcha());
				vo.setCrede(Boolean.valueOf(exameFisicoRns.getIndCrede()));
				vo.setReflexoVermelho(exameFisicoRns.getReflexoVermelho());
				vo.setSaturacaoDiferencial(exameFisicoRns.getSaturacaoDiferencial());
				vo.setSemParticularidade(Boolean.valueOf(exameFisicoRns.getIndSemParticularidades()));
				vo.setTempoRetornoMae(exameFisicoRns.getTempoRetornoMae());
				vo.setTempoRetornoMin(exameFisicoRns.getTempoRetornoMin());
				
				if (exameFisicoRns.getAdequacaoPeso() != null) {
					vo.setAdqPeso(DominioAdequacaoPesoRN.valueOf(exameFisicoRns.getAdequacaoPeso()));
				}
				
				listaVO.add(vo);
			}
		}
		return listaVO;
	}

	
	
	public RegistrarExameFisicoVO validarRegistroExameFisico(RegistrarExameFisicoVO vo) throws ApplicationBusinessException{
		vo = validarAdequacaoPeso(vo);
		if (vo.getTempoRetornoMin() != null ) {
			validarMinutos(vo.getTempoRetornoMin());
		}
		return vo;
	}
	
	/**
	 * ON03
	 * @param pacCodigo
	 * @throws ApplicationBusinessException 
	 */
	private RegistrarExameFisicoVO validarAdequacaoPeso(RegistrarExameFisicoVO vo) throws ApplicationBusinessException{
		BigDecimal peso = obterPesoPacientesPorCodigoPaciente(vo.getPacCodigo());
		if (peso != null) {
			validarPeso(vo.getPacCodigo(), vo.getIdadeGestacionalFinal(), peso);
			McoTabAdequacaoPeso tabAdequacaoPeso = mcoTabAdequacaoPesoDAO.obterMcoTabAdequacaoPesoPorSemanasGestacionais(vo.getIdadeGestacionalFinal().shortValue());
			if (tabAdequacaoPeso != null) {
				if (peso.shortValue() < tabAdequacaoPeso.getPercentil10()) {
					vo.setAdqPeso(DominioAdequacaoPesoRN.PIG);
					vo.setDesabilitarAdqPeso(true);
					return vo;
				} else if (peso.shortValue() >= tabAdequacaoPeso.getPercentil10() && peso.shortValue() <= tabAdequacaoPeso.getPercentil90()) {
					vo.setAdqPeso(DominioAdequacaoPesoRN.AIG);
					vo.setDesabilitarAdqPeso(true);
					return vo;
				} else if (peso.shortValue() > tabAdequacaoPeso.getPercentil90()) {
					vo.setAdqPeso(DominioAdequacaoPesoRN.GIG);
					vo.setDesabilitarAdqPeso(true);
					return vo;
				} else {
					return null;
				}
			} else {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MCO_00581);
			}	
		}
		return null;
	}
	
	
	
	/**
	 * ON05
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	private void validarMinutos(Short tempoRetornoMin) throws ApplicationBusinessException{
		if (!(tempoRetornoMin >= 0 && tempoRetornoMin <= 59)) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MCO_00647);
		}
	}
	
	public void validarExclusao(Integer gsoPacCodigo, Short gsoSeqp, RegistrarExameFisicoVO vo) throws ApplicationBusinessException {
		Byte seqpMax = mcoExameFisicoRnsDAO.obterMaxSeqRecemNascido(gsoPacCodigo, gsoSeqp);
		if (seqpMax!= null && !vo.getSeqp().equals(seqpMax) ) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MCO_00640);
		}
	}
	/**
	 * ON03
	 * @param pacCodigo
	 * @param idadeGestacionalFinal
	 * @param peso
	 * @throws ApplicationBusinessException
	 */
	private void validarPeso(Integer pacCodigo, Byte idadeGestacionalFinal, BigDecimal peso) throws ApplicationBusinessException {
		// CAPURROS
		if (peso.intValue() >= PESO_METODO) {
			Byte idadeCapurros = mcoIddGestCapurrosDAO.obterIdadeGestacionalPorPacCodigo(pacCodigo);
			if (idadeCapurros != null) {
				if (!CoreUtil.igual(idadeCapurros, idadeGestacionalFinal)) {
					throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MCO_00579, idadeCapurros);
				}
			}
			// BALLARD
		} else {
			Byte idadeBallard = mcoIddGestBallardDAO.obterIdadeGestacionalPorPacCodigo(pacCodigo);
			if (idadeBallard != null) {
				if (!CoreUtil.igual(idadeBallard, idadeGestacionalFinal)) {
					throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MCO_00580, idadeBallard);
				}
			}
		}
	}
		
	/**
	 * 
	 * @param voAlterado
	 * @param voOriginal
	 * @return
	 */
	public boolean alterarRegistroExameFisico(RegistrarExameFisicoVO voAlterado, RegistrarExameFisicoVO voOriginal ){
		if (voAlterado != null && voOriginal != null) {
			if(CoreUtil.igual(voAlterado.getAdqPeso(), voOriginal.getAdqPeso()) || 
			    CoreUtil.igual(voAlterado.getAspGeral(), voOriginal.getAspGeral()) || 
			    CoreUtil.igual(voAlterado.getCa(), voOriginal.getCa()) || 
			    CoreUtil.igual(voAlterado.getComp(), voOriginal.getComp()) ||
			    CoreUtil.igual(voAlterado.getCrede(), voOriginal.getCrede()) ||
			    CoreUtil.igual(voAlterado.getFc(), voOriginal.getFc()) ||
			    CoreUtil.igual(voAlterado.getFr(), voOriginal.getFr()) ||
			    CoreUtil.igual(voAlterado.getFugaAsfixia(), voOriginal.getFugaAsfixia()) ||
			    CoreUtil.igual(voAlterado.getIdadeGestacionalFinal(), voOriginal.getIdadeGestacionalFinal()) ||
			    CoreUtil.igual(voAlterado.getMarcha(), voOriginal.getMarcha()) ||
			    CoreUtil.igual(voAlterado.getMoro(), voOriginal.getMoro()) ||
			    CoreUtil.igual(voAlterado.getObservacao(), voOriginal.getObservacao()) ||
			    CoreUtil.igual(voAlterado.getParticularidade(), voOriginal.getParticularidade()) ||
			    CoreUtil.igual(voAlterado.getPc(), voOriginal.getPc()) ||
			    CoreUtil.igual(voAlterado.getPt(), voOriginal.getPt()) ||
			    CoreUtil.igual(voAlterado.getReflexoVermelho(), voOriginal.getReflexoVermelho()) ||
			    CoreUtil.igual(voAlterado.getReptacao(), voOriginal.getReptacao()) ||
			    CoreUtil.igual(voAlterado.getSaturacaoDiferencial(), voOriginal.getSaturacaoDiferencial()) ||
			    CoreUtil.igual(voAlterado.getSdm(), voOriginal.getSdm()) ||
			    CoreUtil.igual(voAlterado.getSemParticularidade(), voOriginal.getSemParticularidade()) ||
			    CoreUtil.igual(voAlterado.getSuccao(), voOriginal.getSuccao()) ||
			    CoreUtil.igual(voAlterado.getTemp(), voOriginal.getTemp()) ||
			    CoreUtil.igual(voAlterado.getTempoRetornoMae(), voOriginal.getTempoRetornoMae()) ||
			    CoreUtil.igual(voAlterado.getTempoRetornoMin(), voOriginal.getTempoRetornoMin())){
				return true;
			}
			
		}
		return false;
	}
	
	
	public Paciente obterPacientePorCodigo(Integer pacCodigo) throws ApplicationBusinessException {
		Paciente result = null;
		PacienteFiltro filtro = new PacienteFiltro();
		filtro.setCodigo(pacCodigo);
		try {
			result = pacienteService.obterPacientePorCodigoOuProntuario(filtro);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	private String obterAlturasPaciente(Integer pacCodigo)throws ApplicationBusinessException {
		BigDecimal altura;
		try {
			altura = pacienteService.obterAlturasPaciente(pacCodigo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return altura == null ? null: altura.toString();
	}

	private BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo)throws ApplicationBusinessException {
	BigDecimal peso;
	try {
		peso = pacienteService.obterPesoPacientesPorCodigoPaciente(pacCodigo);
	} catch (ServiceException e) {
		throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
	}
		return peso;
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	


}