package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.paciente.vo.ImpressaoPIM2VO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ImpressaoPIM2ON extends BaseBusiness {


@EJB
private SumarioAdmissaoObstetricaNotasAdRN sumarioAdmissaoObstetricaNotasAdRN;

private static final Log LOG = LogFactory.getLog(ImpressaoPIM2ON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2478412641020770853L;

	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}	
	
	private SumarioAdmissaoObstetricaNotasAdRN getSumarioAdmissaoObstetricaNotasAdRN() {
		return sumarioAdmissaoObstetricaNotasAdRN;
	}	
	
	public ImpressaoPIM2VO montarRelatorio(Long seqPim2) throws ApplicationBusinessException {
		MpmPim2 mpmPim2 = getPrescricaoMedicaFacade().obterMpmPim2PorChavePrimaria(seqPim2);

		if (mpmPim2 == null) {
			return null;
		}
		
		ImpressaoPIM2VO vo = new ImpressaoPIM2VO();

		preencherDadosMpmPim2(vo, mpmPim2);
		preencherDadosAtendimento(vo, mpmPim2.getAtendimento());
		preencherDadosPaciente(vo, mpmPim2.getAtendimento().getPaciente());
		vo.setQualificacoesProf(getSumarioAdmissaoObstetricaNotasAdRN().
				buscarQualificacoesDoProfissional(vo.getSerMatricula(), vo.getSerVinCodigo()));

		return vo;
	}

	private void preencherDadosMpmPim2(ImpressaoPIM2VO vo, MpmPim2 mpmPim2) {
		vo.setEscorePim2(mpmPim2.getEscorePim2());
		vo.setDthrIngressoUnidade(mpmPim2.getDthrIngressoUnidade());
		vo.setAdmissaoEletiva(mpmPim2.getAdmissaoEletiva());
		vo.setAdmissaoRecuperaCirProc(mpmPim2.getAdmissaoRecuperaCirProc());
		vo.setAdmissaoPosBypass(mpmPim2.getAdmissaoPosBypass());
		vo.setDiagAltoRisco(mpmPim2.getDiagAltoRisco());
		vo.setDiagBaixoRisco(mpmPim2.getDiagBaixoRisco());
		vo.setFaltaRespostaPupilar(mpmPim2.getFaltaRespostaPupilar());
		vo.setVentilacaoMecanica(mpmPim2.getVentilacaoMecanica());
		
		if (mpmPim2.getExcessoBase() != null) {
			vo.setExcessoBase(mpmPim2.getExcessoBase().toString() + "  nmol/L");
		}
		
		if (mpmPim2.getPao2() != null) {
			vo.setPao2(mpmPim2.getPao2().toString() + " mmHg");
		}
		
		vo.setFio2(mpmPim2.getFio2());
		vo.setPressaoSistolica(mpmPim2.getPressaoSistolica());
		vo.setEscorePim2(mpmPim2.getEscorePim2());

		if (mpmPim2.getProbabilidadeMorte() != null) {
			vo.setProbabilidadeMorte(mpmPim2.getProbabilidadeMorte().toString() + "%");
		}
		
		vo.setSerMatricula(mpmPim2.getServidor().getId().getMatricula());
		vo.setSerVinCodigo(mpmPim2.getServidor().getId().getVinCodigo());
		vo.setDthrRealizacao(mpmPim2.getDthrRealizacao());
	}

	private void preencherDadosAtendimento(ImpressaoPIM2VO vo, AghAtendimentos atendimento) {
		vo.setSeq(atendimento.getSeq());
		vo.setAtdSeq(atendimento.getSeq()); /*Apesar de estranho, o AtdSeq é recuperado duas vezes na query original
											  (via atd.seq e via pm2.atd_seq, que é a mesma coisa) 	
		 									 */		
		if (atendimento.getLeito() != null) {
			vo.setQuarto(atendimento.getLeito().getLeitoID());
		} else if (atendimento.getQuarto() != null) {			
			vo.setQuarto(atendimento.getQuarto().getNumero().toString()); 
		} else {
			vo.setQuarto("0");
		}
	}

	private void preencherDadosPaciente(ImpressaoPIM2VO vo, AipPacientes paciente) {
		vo.setNome(paciente.getNome());
		vo.setCodigo(paciente.getCodigo());
		vo.setIdade(getAmbulatorioFacade().obterIdadeMesDias(paciente.getDtNascimento(), new Date())); 
		vo.setProntuario(paciente.getProntuario());
		vo.setSexo(paciente.getSexo());
	}
}
