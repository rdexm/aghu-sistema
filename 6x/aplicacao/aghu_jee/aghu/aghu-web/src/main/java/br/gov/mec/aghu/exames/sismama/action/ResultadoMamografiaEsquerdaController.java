package br.gov.mec.aghu.exames.sismama.action;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioContornoNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLimiteNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLocalizacaoMamografia;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioTamanhoNoduloMamografia;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;

public class ResultadoMamografiaEsquerdaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 5128182247829657321L;

	private static final String DELIMITADOR = ",";

	@EJB
	private IParametroFacade parametroFacade;

	private boolean vNaoInformadoEOk; // v_nao_informado_e_ok

	private Integer noduloCount;

	private Integer microcalcificacaoCount;

	private Integer assimetriaDifusaCount;

	private Integer assimetriaFocalCount;

	// k_variaveis
	private String rxMamaEsquerda; // v_rx_mama_esquerda
	private String habilitaMamaEsquerda; // v_habilita_mama_esquerda

	@Inject
	private ResultadoMamografiaController resultadoMamografiaController;

	public void pesquisarParametrosEsquerda() throws ApplicationBusinessException {
		String sitCodigoMamaEsquerda = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_RX_MAMA_ESQUERDA).getVlrTexto();
		setRxMamaEsquerda(sitCodigoMamaEsquerda);
	}

	// ------- Nodulos -------//
	public void montaLinhasNodulos(Map<String, AelSismamaMamoResVO> mapMamae) {
		noduloCount = 0;
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_NOD_SIM_01E.toString()).isChecked()) {
			noduloCount++;
		}
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_NOD_SIM_02E.toString()).isChecked()) {
			noduloCount++;
		}
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_NOD_SIM_03E.toString()).isChecked()) {
			noduloCount++;
		}
	}

	public void incNoduloCount(String key) {
		resultadoMamografiaController.getMapMamaE().get(key).setChecked(true);
		noduloCount++;
	}

	public void decNoduloCount(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaE().get(key[0]).setChecked(false);
		voltaDefaultNodulo(key);
		noduloCount--;
	}

	public void noduloCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		if (resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked()) {
			if (noduloCount == 0) {
				noduloCount++;
			}
		} else {
			voltaDefaultNodulo(key);
			if (noduloCount == 1) {
				noduloCount--;
			}
		}
	}

	private void voltaDefaultNodulo(String[] key) {
		resultadoMamografiaController.getMapMamaE().get(key[1]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaE().get(key[2]).setTamanho(DominioTamanhoNoduloMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaE().get(key[3]).setContorno(DominioContornoNoduloMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaE().get(key[4]).setLimite(DominioLimiteNoduloMamografia.DEFAULT);
	}

	// ------- Microcalcificacoes -------//
	public void montaLinhasMicrocalcificacao(Map<String, AelSismamaMamoResVO> mapMamae) {
		microcalcificacaoCount = 0;
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_MICRO_SIM_01E.toString()).isChecked()) {
			microcalcificacaoCount++;
		}
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_MICRO_SIM_02E.toString()).isChecked()) {
			microcalcificacaoCount++;
		}
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_MICRO_SIM_03E.toString()).isChecked()) {
			microcalcificacaoCount++;
		}
	}

	public void incMicrocalcificacaoCount(String key) {
		resultadoMamografiaController.getMapMamaE().get(key).setChecked(true);
		microcalcificacaoCount++;
	}

	public void decMicrocalcificacaoCount(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaE().get(key[0]).setChecked(false);
		voltaDefaultMicrocalcificacao(key);
		microcalcificacaoCount--;
	}

	public void microcalcificacaoCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		if (resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked()) {
			if (microcalcificacaoCount == 0) {
				microcalcificacaoCount++;
			}
		} else {
			voltaDefaultMicrocalcificacao(key);
			if (microcalcificacaoCount == 1) {
				microcalcificacaoCount--;
			}
		}
	}

	private void voltaDefaultMicrocalcificacao(String[] key) {
		resultadoMamografiaController.getMapMamaE().get(key[1]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaE().get(key[2]).setForma(null);
		resultadoMamografiaController.getMapMamaE().get(key[3]).setDistribuicao(null);
	}

	// ------- Assimetria Focal -------//
	public void montaLinhasAssimetriaFocal(Map<String, AelSismamaMamoResVO> mapMamae) {
		assimetriaFocalCount = 0;
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01E.toString()).isChecked() || mapMamae.get(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01E.toString()).isChecked()) {
			assimetriaFocalCount++;
		}
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02E.toString()).isChecked() || mapMamae.get(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02E.toString()).isChecked()) {
			assimetriaFocalCount++;
		}
	}

	public void incAssimetriaFocalCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		for (String key : keys) {
			resultadoMamografiaController.getMapMamaE().get(key).setChecked(true);
		}
		assimetriaFocalCount++;
	}

	public void decAssimetriaFocalCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaE().get(keys[0]).setChecked(false);
		resultadoMamografiaController.getMapMamaE().get(keys[1]).setChecked(false);

		resultadoMamografiaController.getMapMamaE().get(keys[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaE().get(keys[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);

		assimetriaFocalCount--;
	}

	public void assimetriaFocalCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		if (!resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked()) {
			resultadoMamografiaController.getMapMamaE().get(key[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}
		if (!resultadoMamografiaController.getMapMamaE().get(key[1]).isChecked()) {
			resultadoMamografiaController.getMapMamaE().get(key[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}

		if (resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked() || resultadoMamografiaController.getMapMamaE().get(key[1]).isChecked()) {
			if (assimetriaFocalCount == 0) {
				assimetriaFocalCount++;
			}
		} else {
			if (assimetriaFocalCount == 1 && !resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked() && !resultadoMamografiaController.getMapMamaE().get(key[1]).isChecked()) {
				assimetriaFocalCount--;
			}
		}
	}

	// ------- Assimetria Difusa -------//
	public void montaLinhasAssimetriaDifusa(Map<String, AelSismamaMamoResVO> mapMamae) {
		assimetriaDifusaCount = 0;
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01E.toString()).isChecked() || mapMamae.get(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01E.toString()).isChecked()) {
			assimetriaDifusaCount++;
		}
		if (mapMamae.get(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02E.toString()).isChecked() || mapMamae.get(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02E.toString()).isChecked()) {
			assimetriaDifusaCount++;
		}
	}

	public void incAssimetriaDifusaCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		for (String key : keys) {
			resultadoMamografiaController.getMapMamaE().get(key).setChecked(true);
		}
		assimetriaDifusaCount++;
	}

	public void decAssimetriaDifusaCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaE().get(keys[0]).setChecked(false);
		resultadoMamografiaController.getMapMamaE().get(keys[1]).setChecked(false);

		resultadoMamografiaController.getMapMamaE().get(keys[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaE().get(keys[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);

		assimetriaDifusaCount--;
	}

	public void assimetriaDifusaCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		if (!resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked()) {
			resultadoMamografiaController.getMapMamaE().get(key[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}
		if (!resultadoMamografiaController.getMapMamaE().get(key[1]).isChecked()) {
			resultadoMamografiaController.getMapMamaE().get(key[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}

		if (resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked() || resultadoMamografiaController.getMapMamaE().get(key[1]).isChecked()) {
			if (assimetriaDifusaCount == 0) {
				assimetriaDifusaCount++;
			}
		} else {
			if (assimetriaDifusaCount == 1 && !resultadoMamografiaController.getMapMamaE().get(key[0]).isChecked() && !resultadoMamografiaController.getMapMamaE().get(key[1]).isChecked()) {
				assimetriaDifusaCount--;
			}
		}
	}

	public String getRxMamaEsquerda() {
		return rxMamaEsquerda;
	}

	public void setRxMamaEsquerda(String rxMamaEsquerda) {
		this.rxMamaEsquerda = rxMamaEsquerda;
	}

	public String getHabilitaMamaEsquerda() {
		return habilitaMamaEsquerda;
	}

	public void setHabilitaMamaEsquerda(String habilitaMamaEsquerda) {
		this.habilitaMamaEsquerda = habilitaMamaEsquerda;
	}

	public boolean isvNaoInformadoEOk() {
		return vNaoInformadoEOk;
	}

	public void setvNaoInformadoEOk(boolean vNaoInformadoEOk) {
		this.vNaoInformadoEOk = vNaoInformadoEOk;
	}

	public Integer getNoduloCount() {
		return noduloCount;
	}

	public void setNoduloCount(Integer noduloCount) {
		this.noduloCount = noduloCount;
	}

	public Integer getMicrocalcificacaoCount() {
		return microcalcificacaoCount;
	}

	public void setMicrocalcificacaoCount(Integer microcalcificacaoCount) {
		this.microcalcificacaoCount = microcalcificacaoCount;
	}

	public Integer getAssimetriaDifusaCount() {
		return assimetriaDifusaCount;
	}

	public void setAssimetriaDifusaCount(Integer assimetriaDifusaCount) {
		this.assimetriaDifusaCount = assimetriaDifusaCount;
	}

	public Integer getAssimetriaFocalCount() {
		return assimetriaFocalCount;
	}

	public void setAssimetriaFocalCount(Integer assimetriaFocalCount) {
		this.assimetriaFocalCount = assimetriaFocalCount;
	}

}
