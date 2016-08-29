package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.paciente.vo.AipEnderecoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe auxiliar responsável por manter as regras de negócio da EVOLUCAO.
 * @author pedro.santiago
 */
@Stateless
public class EvolucaoAuxiliarON extends BaseBusiness {


	private static final long serialVersionUID = -8732274482920571113L;

	private static final Log LOG = LogFactory.getLog(EvolucaoAuxiliarON.class);

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * @ORADB MAMC_EDITA_DTNAS_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMK_FUNCAO_EDICAO
	 */
	public String mamcEditaDtnasPac(Integer codigo) {

		String retorno = "";

		Date dataRetorno = this.ambulatorioFacade.obterDataNascimentoAnterior(codigo);
		if (dataRetorno != null) {
			retorno = DateUtil.obterDataFormatada(dataRetorno, "dd/MM/yyyy");
		}
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_CDD_PAC #52025 - FUNCTION DO PACKAGE MAMK_FUNCAO_EDICAO
	 */
	public String mamcEditaCddPac(Integer codigo) {
		String retorno = "";
		retorno = ambulatorioFacade.obterDescricaoCidCapitalizada(aipcProcedenciaPac(codigo));
		
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_COR_PAC #52025 - FUNCTION DO PACKAGE MAMK_FUNCAO_EDICAO
	 */
	public String mamcEditaCorPac(Integer codigo) {
		String retorno = "";

		DominioCor cor = this.ambulatorioFacade.obterCurCorPacPorCodigo(codigo);

		if (cor != null) {
			retorno = cor.getDescricao();
		}

		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_COR_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaGrauPac(Integer codigo) {
		String retorno = "";

		DominioGrauInstrucao grau = this.ambulatorioFacade
				.obterCurGrauPacPorCodigo(codigo);

		if (grau != null) {
			retorno = grau.getDescricao();
		}

		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_NAC_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaNacPac(Integer codigo) {
		String retorno = "";

		retorno = this.ambulatorioFacade.obterCurNacionalidadePorCodigo(codigo);

		if (retorno != null && !retorno.trim().isEmpty()) {
			retorno = StringUtils.lowerCase(retorno);
		}

		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_NATUR_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaNaturPac(Integer codigo) {
		String retorno = "";

		retorno = this.ambulatorioFacade.obterCurCidadePorCodigo(codigo);

		if (retorno != null && !retorno.trim().isEmpty()) {
			retorno = ambulatorioFacade.obterDescricaoCidCapitalizada(retorno);
		}

		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_PROF_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaProfPac(Integer codigo) {
		String retorno = "";

		retorno = this.ambulatorioFacade.obterCurProfissaoPorCodigo(codigo);

		if (retorno != null && !retorno.trim().isEmpty()) {
			retorno = StringUtils.lowerCase(retorno);
		}

		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_SEXO_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaSexoPac(Integer codigo) {
		String retorno = "";

		DominioSexo sexo = this.ambulatorioFacade.obterCurSexoPacPorCodigo(codigo);

		if (sexo != null) {
			retorno = sexo.getDescricao();
		}

		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_NOME_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaNomePac(Integer codigo) {
		String retorno = "";

		retorno = this.ambulatorioFacade.obterNomePacientePorCodigoPac(codigo);

		if (retorno != null && !retorno.trim().isEmpty()) {
			retorno = ambulatorioFacade.obterDescricaoCidCapitalizada(retorno);
		}
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_IDD_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaIddPac(Integer codigo) {
		String retorno = "";

		Date dataRetorno = this.ambulatorioFacade
				.obterDataNascimentoAnterior(codigo);

		if (dataRetorno != null) {
			retorno = DateUtil.obterQtdMesesEntreDuasDatas(dataRetorno,
					new Date()).toString();
		}
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_CIVIL_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaCivilPac(Integer codigo) {
		String retorno = "";

		CursorPacVO vo = this.ambulatorioFacade.obterCurPacPorCodigo(codigo);

		if (vo != null) {

			if (vo.getSexo() == DominioSexo.F) {
				if (vo.getEstadoCivil() == DominioEstadoCivil.C) {
					retorno = "casada";
				} else if (vo.getEstadoCivil() == DominioEstadoCivil.S) {
					retorno = "solteira";
				} else if (vo.getEstadoCivil() == DominioEstadoCivil.P) {
					retorno = "separada";
				} else if (vo.getEstadoCivil() == DominioEstadoCivil.D) {
					retorno = "divorciada";
				} else if (vo.getEstadoCivil() == DominioEstadoCivil.V) {
					retorno = "viúva";
				} else {
					retorno = "outros";
				}
			} else {
				if (vo.getEstadoCivil() == DominioEstadoCivil.C
						|| vo.getEstadoCivil() != DominioEstadoCivil.S
						|| vo.getEstadoCivil() != DominioEstadoCivil.P
						|| vo.getEstadoCivil() != DominioEstadoCivil.D
						|| vo.getEstadoCivil() != DominioEstadoCivil.V) {
					retorno = vo.getEstadoCivil().getDescricao();
				} else {
					retorno = "outros";
				}
			}
		}
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_IDD_2_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaIdd2Pac(Integer codigo) {
		StringBuilder retorno = new StringBuilder(500);
		String stringRetorno = "";
		Date dataRetorno = this.ambulatorioFacade
				.obterDataNascimentoAnterior(codigo);

		if (dataRetorno != null) {
			
			Integer parcialMod = DateUtil.obterQtdMesesEntreDuasDatas(DateUtil.truncaData(dataRetorno), DateUtil.truncaData(new Date()));
			
			//NVL(TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE),TRUNC(pac.dt_nascimento)) / 12),0)			
			Integer vAnos = parcialMod/12;
			//NVL(TRUNC(MOD(MONTHS_BETWEEN(TRUNC(SYSDATE), TRUNC(pac.dt_nascimento)),12)),0)
			Integer vMeses = parcialMod%12;
			//NVL(TRUNC(MOD(MONTHS_BETWEEN(TRUNC(SYSDATE),TRUNC(pac.dt_nascimento)),1) * 1000000) * TO_NUMBER(TO_CHAR(LAST_DAY(SYSDATE), 'dd')) / 1000000,0)
			Integer vDias = (parcialMod%1*1000000)*(Integer.valueOf((DateUtil.obterDataFormatada(DateUtil.obterDataFimCompetencia(new Date()),"dd")))/1000000);
			
			if (CoreUtil.maior(vAnos, 1)) {
				retorno.append(vAnos.toString().concat(" anos, "));
			} else {
				retorno.append(vAnos.toString().concat(" ano, "));
			}

			if (CoreUtil.maior(vMeses, 1)) {
				retorno.append(vMeses.toString().concat(" meses, "));
			} else {
				retorno.append(vMeses.toString().concat(" mês, "));
			}

			if (CoreUtil.maior(vDias, 1)) {
				retorno.append(vDias.toString().concat(" dias, "));
			} else {
				retorno.append(vDias.toString().concat(" dia, "));
			}

			stringRetorno = retorno.substring(0, retorno.length() - 2);
		}

		return stringRetorno;
	}

	/**
	 * @ORADB MAMC_EDITA_NOME_PAI #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaNomePai(Integer codigo) {
		String retorno = "";

		retorno = this.ambulatorioFacade.obterCurPacNomePaiPorCodigo(codigo);

		if (retorno != null && !retorno.trim().isEmpty()) {
			retorno = ambulatorioFacade.obterDescricaoCidCapitalizada(retorno);
		}
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_NOME_MAE #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaNomeMae(Integer codigo) {
		String retorno = "";

		retorno = this.ambulatorioFacade.obterCurPacNomeMaePorCodigo(codigo);

		if (retorno != null && !retorno.trim().isEmpty()) {
			retorno = ambulatorioFacade.obterDescricaoCidCapitalizada(retorno);
		}
		return retorno;
	}

	/**
	 * @ORADB MAMC_EDITA_IDD_3_PAC #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcEditaIdd3Pac(Integer codigo) {
		StringBuilder retorno = new StringBuilder(500);
		String stringRetorno = "";
		Date dataRetorno = this.ambulatorioFacade
				.obterDataNascimentoAnterior(codigo);

		if (dataRetorno != null) {

			Integer parcialMod = DateUtil.obterQtdMesesEntreDuasDatas(DateUtil.truncaData(dataRetorno), DateUtil.truncaData(new Date()));
			
			//NVL(TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE),TRUNC(pac.dt_nascimento)) / 12),0)			
			Integer vAnos = parcialMod/12;
			//NVL(TRUNC(MOD(MONTHS_BETWEEN(TRUNC(SYSDATE), TRUNC(pac.dt_nascimento)),12)),0)
			Integer vMeses = parcialMod%12;
			//NVL(TRUNC(MOD(MONTHS_BETWEEN(TRUNC(SYSDATE),TRUNC(pac.dt_nascimento)),1) * 1000000) * TO_NUMBER(TO_CHAR(LAST_DAY(SYSDATE), 'dd')) / 1000000,0)
			Integer vDias = (parcialMod%1*1000000)*(Integer.valueOf((DateUtil.obterDataFormatada(DateUtil.obterDataFimCompetencia(new Date()),"dd")))/1000000);

			if (CoreUtil.maior(vAnos, 1)) {
				retorno.append(vAnos.toString().concat(" anos e "));
			} else {
				retorno.append(vAnos.toString().concat(" ano e  "));
			}

			if (CoreUtil.maior(vMeses, 1)) {
				retorno.append(vMeses.toString().concat(" meses e "));
			} else {
				retorno.append(vMeses.toString().concat(" mes e "));
			}

			if (CoreUtil.maior(vDias, 1)) {
				retorno.append(vDias.toString().concat(" dias e "));
			} else {
				retorno.append(vDias.toString().concat(" dia e  "));
			}

			stringRetorno = retorno.substring(0, retorno.length() - 3);
		}

		return stringRetorno;
	}

	/**
	 * @ORADB AIPC_PROCEDENCIA_PAC #52025 - P9
	 * @return
	 */
	public String aipcProcedenciaPac(Integer codigo) {

		Integer wBclCloLgrCodigo;
		Integer wCddCodigo;
		String wCidade = "Sem endereço";

		List<AipEnderecoPacienteVO> listaRetorno = this.ambulatorioFacade.obterAipEnderecoVOPorCodigo(codigo);
		
		if (listaRetorno != null && !listaRetorno.isEmpty()) {
			for (AipEnderecoPacienteVO reg : listaRetorno) {
				if (reg.getTipoEndereco() == DominioTipoEndereco.R) {
					wBclCloLgrCodigo = reg.getBclCloLgrCodigo();
					wCddCodigo = reg.getCddCodigo();
					wCidade = reg.getCidade();

					if (wBclCloLgrCodigo != null || wCddCodigo != null) {
						if (wBclCloLgrCodigo != null) {
							wCddCodigo = this.ambulatorioFacade.obterCddCodigoPorCodigo(wBclCloLgrCodigo);
						}
						wCidade = this.ambulatorioFacade.obterNomeAipCidadesPorCodigo(wCddCodigo);
						return wCidade;
					}else{
						return wCidade;
					}
				}
			}
		}
		return wCidade;
	}
}