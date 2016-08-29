package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipAlturaPacientesId;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipPesoPacientesId;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricaoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParamCalculoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class ParametroCalculoPrescricaoON extends BaseBusiness {

	@EJB
	private ParametroCalculoPrescricaoRN parametroCalculoPrescricaoRN;

	private static final Log LOG = LogFactory
			.getLog(ParametroCalculoPrescricaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MpmParamCalculoPrescricaoDAO mpmParamCalculoPrescricaoDAO;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 6483787609437950032L;

	private enum ParametroCalculoPrescricaoONExceptionCode implements
			BusinessExceptionCode {
		MPM_03715, MPM_03716;
	}

	public Boolean possuiDadosPesoAlturaDia(Integer atdSeq) {
		MpmParamCalculoPrescricao paramCalculo = getMpmParamCalculoPrescricaoDAO()
				.obterParamCalculoPrescricoesCriadasHojePeloAtendimentoESituacao(
						atdSeq, DominioSituacao.A);
		if (paramCalculo != null) {
			return true;
		}
		return false;
	}

	/**
	 * @ORADB FORM - FUNCTION calcula_SC
	 */
	public BigDecimal calculaSC(Boolean pacientePediatrico, BigDecimal peso,
			BigDecimal altura) {
		BigDecimal sc = null;
		if (altura == null && peso != null && pacientePediatrico) {
			sc = (peso.multiply(BigDecimal.valueOf(4)).add(BigDecimal
					.valueOf(7))).divide(peso.add(BigDecimal.valueOf(90)), 2,
					RoundingMode.HALF_EVEN);
		} else if (altura != null && peso != null) {
			// POW = X^Y => X^(A+B) => X^A*X^B
			sc = (BigDecimal.valueOf(
					Math.pow(
							altura.divide(BigDecimal.valueOf(100), 5,
									RoundingMode.HALF_EVEN).doubleValue(),
							BigDecimal.valueOf(0.725).doubleValue())).multiply(
					BigDecimal.valueOf(0.20247)).multiply(BigDecimal
					.valueOf(Math.pow(peso.doubleValue(),
							BigDecimal.valueOf(0.425).doubleValue()))));

			sc = sc.setScale(2, RoundingMode.HALF_EVEN);
		}
		return sc;
	}

	/**
	 * @ORADB FORM - PRE-UPDATE e ON-UPDATE
	 */
	public void atualizar(Integer pacCodigo, Integer atdSeq, BigDecimal peso,
			DominioTipoMedicaoPeso tipoMedicaoPeso, BigDecimal altura,
			DominioTipoMedicaoPeso tipoMedicaoAltura, BigDecimal sc,
			BigDecimal scCalculada, DadosPesoAlturaVO vo)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (peso != null && tipoMedicaoPeso == null) {
			throw new ApplicationBusinessException(
					ParametroCalculoPrescricaoONExceptionCode.MPM_03715);
		}

		if (peso == null && tipoMedicaoPeso != null) {
			throw new ApplicationBusinessException(
					ParametroCalculoPrescricaoONExceptionCode.MPM_03716);
		}

		if (altura != null
				&& (vo.getAltura() == null
						|| CoreUtil.modificados(altura, vo.getAltura()) || CoreUtil
							.modificados(tipoMedicaoAltura,
									vo.getRealEstimadoAltura()))) {
			AipAlturaPacientes alturaPaciente = new AipAlturaPacientes();
			alturaPaciente.setId(new AipAlturaPacientesId(pacCodigo, null));
			alturaPaciente.setAltura(altura);
			alturaPaciente.setRealEstimado(tipoMedicaoAltura);
			getCadastroPacienteFacade().persistirAlturaPaciente(alturaPaciente);
			vo.setAlturaPaciente(alturaPaciente);
		} else {
			if (altura == null && vo.getAltura() != null) {
				vo.setAlturaPaciente(null);
			}
		}

		if (peso != null
				&& (vo.getPeso() == null
						|| CoreUtil.modificados(peso, vo.getPeso()) || CoreUtil
							.modificados(tipoMedicaoPeso,
									vo.getRealEstimadoPeso()))) {
			AipPesoPacientes pesoPaciente = new AipPesoPacientes();
			pesoPaciente.setId(new AipPesoPacientesId(pacCodigo, null));
			pesoPaciente.setPeso(peso);
			pesoPaciente.setRealEstimado(tipoMedicaoPeso);
			getCadastroPacienteFacade().persistirPesoPaciente(pesoPaciente, servidorLogado);
			vo.setPesoPaciente(pesoPaciente);
		} else {
			if (peso == null && vo.getPeso() != null) {
				vo.setPesoPaciente(null);
			}
		}
		// ON-UPDATE
		List<MpmParamCalculoPrescricao> lista = getMpmParamCalculoPrescricaoDAO()
				.listarParamCalculoPrescricoesAtivosPeloAtendimento(atdSeq);
		for (MpmParamCalculoPrescricao par : lista) {
			par.setIndSituacao(DominioSituacao.I);
			getParametroCalculoPrescricaoRN().persistir(par);
		}
		MpmParamCalculoPrescricao novoParCalculo = new MpmParamCalculoPrescricao();
		novoParCalculo.setId(new MpmParamCalculoPrescricaoId(atdSeq, null));
		novoParCalculo.setIdadeEmDias(vo.getIdadeEmDias());
		novoParCalculo.setIdadeEmMeses(vo.getIdadeEmMeses());
		novoParCalculo.setIdadeEmAnos(vo.getIdadeEmAnos());
		novoParCalculo.setAipAlturaPaciente(vo.getAlturaPaciente());
		novoParCalculo.setAipPesoPaciente(vo.getPesoPaciente());
		novoParCalculo.setSc(sc);
		novoParCalculo.setScCalculada(scCalculada);
		novoParCalculo.setIndSituacao(DominioSituacao.A);
		getParametroCalculoPrescricaoRN().persistir(novoParCalculo);
		getMpmParamCalculoPrescricaoDAO().flush();
	}

	/**
	 * @ORADB FORM - WHEN NEW FORM ISNTANCE
	 */

	public void inicializarCadastroPesoAltura(Integer atdSeq,
			DadosPesoAlturaVO vo) {
		// Integer vAtdSeq = null;
		AghAtendimentos atendimento = null;
		Integer idadeEmDias = null;
		Integer idadeEmMeses = null;
		Integer idadeEmAnos = null;

		if (vo == null) {
			vo = new DadosPesoAlturaVO();
		}

		MpmParamCalculoPrescricao paramCalculo = getMpmParamCalculoPrescricaoDAO()
				.obterParamCalculoPrescricoesCriadasHojePeloAtendimentoESituacao(
						atdSeq, null);
		if (paramCalculo != null) {
			// vAtdSeq = paramCalculo.getId().getAtdSeq();

			if (paramCalculo.getAipAlturaPaciente() != null) {
				vo.setAlturaPaciente(paramCalculo.getAipAlturaPaciente());
				vo.setAltura(paramCalculo.getAipAlturaPaciente().getAltura());
				vo.setRealEstimadoAltura(paramCalculo.getAipAlturaPaciente()
						.getRealEstimado());
			} else {
				vo.setAlturaPaciente(null);
				vo.setAltura(null);
				vo.setRealEstimadoAltura(null);
			}
			if (paramCalculo.getAipPesoPaciente() != null) {
				vo.setPesoPaciente(paramCalculo.getAipPesoPaciente());
				vo.setPeso(paramCalculo.getAipPesoPaciente().getPeso().doubleValue());
				vo.setRealEstimadoPeso(paramCalculo.getAipPesoPaciente()
						.getRealEstimado());
			} else {
				vo.setPesoPaciente(null);
				vo.setPeso(null);
				vo.setRealEstimadoPeso(null);
			}
		}

		atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		if (atendimento != null) {
			idadeEmDias = DateUtil.obterQtdDiasEntreDuasDatas(atendimento
					.getPaciente().getDtNascimento(), new Date());
			idadeEmMeses = DateUtil.obterQtdMesesEntreDuasDatas(atendimento
					.getPaciente().getDtNascimento(), new Date());
			idadeEmAnos = DateUtil.obterQtdAnosEntreDuasDatas(atendimento
					.getPaciente().getDtNascimento(), new Date());

			vo.setIdadeEmDias(idadeEmDias);
			vo.setIdadeEmMeses(idadeEmMeses);
			vo.setIdadeEmAnos(idadeEmAnos != null ? idadeEmAnos.shortValue()
					: null);
		}

		// if(vAtdSeq == null) {
		// AipAlturaPacientes altura =
		// getCadastroPacienteFacade().obterAlturarPacientesPorCodigoPacienteECriadoEm(atendimento.getPaciente().getCodigo(),
		// atendimento.getDthrInicio());
		// List<MpmParamCalculoPrescricao> lista =
		// getMpmParamCalculoPrescricaoDAO().listarParamCalculoPrescricoesAtivosPeloAtendimento(atdSeq);
		// for(MpmParamCalculoPrescricao par : lista) {
		// par.setIndSituacao(DominioSituacao.I);
		// getParametroCalculoPrescricaoRN().persistir(par);
		// }
		//
		// MpmParamCalculoPrescricao novoParCalculo = new
		// MpmParamCalculoPrescricao();
		// novoParCalculo.setId(new MpmParamCalculoPrescricaoId(atdSeq, null));
		// novoParCalculo.setIdadeEmDias(idadeEmDias);
		// novoParCalculo.setIdadeEmMeses(idadeEmMeses);
		// novoParCalculo.setIdadeEmAnos(idadeEmAnos!=null?idadeEmAnos.shortValue():null);
		// novoParCalculo.setAipAlturaPaciente(altura);
		// novoParCalculo.setIndSituacao(DominioSituacao.A);
		// getParametroCalculoPrescricaoRN().persistir(novoParCalculo);
		// getMpmParamCalculoPrescricaoDAO().flush();
		// DadosPesoAlturaVO vo =
		// (DadosPesoAlturaVO)obterContextoSessao("DADOS_PESO_ALTURA_PACIENTE);
		// if(vo == null) {
		// vo = new DadosPesoAlturaVO();
		// }
		// vo.setAltura(null);
		// vo.setRealEstimadoAltura(null);
		// vo.setPeso(null);
		// vo.setRealEstimadoPeso(null);
		// atribuirContextoSessao("DADOS_PESO_ALTURA_PACIENTE", vo);
		// }
	}

	private ParametroCalculoPrescricaoRN getParametroCalculoPrescricaoRN() {
		return parametroCalculoPrescricaoRN;
	}

	private MpmParamCalculoPrescricaoDAO getMpmParamCalculoPrescricaoDAO() {
		return mpmParamCalculoPrescricaoDAO;
	}

	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	private ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

}
