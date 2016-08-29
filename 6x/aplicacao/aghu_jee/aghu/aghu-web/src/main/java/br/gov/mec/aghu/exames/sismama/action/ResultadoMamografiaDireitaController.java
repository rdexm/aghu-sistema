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

public class ResultadoMamografiaDireitaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 5128182247822157321L;

	private static final String DELIMITADOR = ",";

	@EJB
	private IParametroFacade parametroFacade;

	private Integer noduloCount;

	private Integer microcalcificacaoCount;

	private Integer assimetriaDifusaCount;

	private Integer assimetriaFocalCount;

	// k_variaveis
	private String rxMamaDireita; // v_rx_mama_direita
	private String habilitaMamaDireita; // v_habilita_mama_direita
	private boolean vNaoInformadoDOk; // v_nao_informado_d_ok

	@Inject
	private ResultadoMamografiaController resultadoMamografiaController;

	public void pesquisarParametrosDireita() throws ApplicationBusinessException {
		String sitCodigoMamaDireita = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_RX_MAMA_DIREITA).getVlrTexto();
		setRxMamaDireita(sitCodigoMamaDireita);
	}

	// ------- Nodulos -------//
	public void montaLinhasNodulos(Map<String, AelSismamaMamoResVO> mapMamaD) {
		noduloCount = 0;
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_NOD_SIM_01D.toString()).isChecked()) {
			noduloCount++;
		}
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_NOD_SIM_02D.toString()).isChecked()) {
			noduloCount++;
		}
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_NOD_SIM_03D.toString()).isChecked()) {
			noduloCount++;
		}
	}

	public void incNoduloCount(String key) {
		resultadoMamografiaController.getMapMamaD().get(key).setChecked(true);
		noduloCount++;
	}

	public void decNoduloCount(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaD().get(key[0]).setChecked(false);
		voltaDefaultNodulo(key);
		noduloCount--;
	}

	public void noduloCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		if (resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked()) {
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
		resultadoMamografiaController.getMapMamaD().get(key[1]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaD().get(key[2]).setTamanho(DominioTamanhoNoduloMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaD().get(key[3]).setContorno(DominioContornoNoduloMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaD().get(key[4]).setLimite(DominioLimiteNoduloMamografia.DEFAULT);
	}

	// ------- Microcalcificacoes -------//
	public void montaLinhasMicrocalcificacao(Map<String, AelSismamaMamoResVO> mapMamaD) {
		microcalcificacaoCount = 0;
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_MICRO_SIM_01D.toString()).isChecked()) {
			microcalcificacaoCount++;
		}
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_MICRO_SIM_02D.toString()).isChecked()) {
			microcalcificacaoCount++;
		}
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_MICRO_SIM_03D.toString()).isChecked()) {
			microcalcificacaoCount++;
		}
	}

	public void incMicrocalcificacaoCount(String key) {
		resultadoMamografiaController.getMapMamaD().get(key).setChecked(true);
		microcalcificacaoCount++;
	}

	public void decMicrocalcificacaoCount(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaD().get(key[0]).setChecked(false);
		voltaDefaultMicrocalcificacao(key);
		microcalcificacaoCount--;
	}

	public void microcalcificacaoCheckBox(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		if (resultadoMamografiaController.getMapMamaD().get(keys[0]).isChecked()) {
			if (microcalcificacaoCount == 0) {
				microcalcificacaoCount++;
			}
		} else {
			voltaDefaultMicrocalcificacao(keys);
			if (microcalcificacaoCount == 1) {
				microcalcificacaoCount--;
			}
		}
	}

	private void voltaDefaultMicrocalcificacao(String[] key) {
		resultadoMamografiaController.getMapMamaD().get(key[1]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaD().get(key[2]).setForma(null);
		resultadoMamografiaController.getMapMamaD().get(key[3]).setDistribuicao(null);
	}

	// ------- Assimetria Focal -------//
	public void montaLinhasAssimetriaFocal(Map<String, AelSismamaMamoResVO> mapMamaD) {
		assimetriaFocalCount = 0;
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01D.toString()).isChecked() || mapMamaD.get(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01D.toString()).isChecked()) {
			assimetriaFocalCount++;
		}
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02D.toString()).isChecked() || mapMamaD.get(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02D.toString()).isChecked()) {
			assimetriaFocalCount++;
		}
	}

	public void incAssimetriaFocalCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		for (String key : keys) {
			resultadoMamografiaController.getMapMamaD().get(key).setChecked(true);
		}
		assimetriaFocalCount++;
	}

	public void decAssimetriaFocalCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaD().get(keys[0]).setChecked(false);
		resultadoMamografiaController.getMapMamaD().get(keys[1]).setChecked(false);

		resultadoMamografiaController.getMapMamaD().get(keys[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaD().get(keys[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);

		assimetriaFocalCount--;
	}

	public void assimetriaFocalCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);
		if (!resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked()) {
			resultadoMamografiaController.getMapMamaD().get(key[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}
		if (!resultadoMamografiaController.getMapMamaD().get(key[1]).isChecked()) {
			resultadoMamografiaController.getMapMamaD().get(key[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}

		if (resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked() || resultadoMamografiaController.getMapMamaD().get(key[1]).isChecked()) {
			if (assimetriaFocalCount == 0) {
				assimetriaFocalCount++;
			}
		} else {
			if (assimetriaFocalCount == 1 && !resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked() && !resultadoMamografiaController.getMapMamaD().get(key[1]).isChecked()) {
				assimetriaFocalCount--;
			}
		}
	}

	// ------- Assimetria Difusa -------//
	public void montaLinhasAssimetriaDifusa(Map<String, AelSismamaMamoResVO> mapMamaD) {
		assimetriaDifusaCount = 0;
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01D.toString()).isChecked() || mapMamaD.get(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01D.toString()).isChecked()) {
			assimetriaDifusaCount++;
		}
		if (mapMamaD.get(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02D.toString()).isChecked() || mapMamaD.get(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02D.toString()).isChecked()) {
			assimetriaDifusaCount++;
		}
	}

	public void incAssimetriaDifusaCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		for (String key : keys) {
			resultadoMamografiaController.getMapMamaD().get(key).setChecked(true);
		}
		assimetriaDifusaCount++;
	}

	public void decAssimetriaDifusaCount(String params) {
		String[] keys = StringUtils.split(params, DELIMITADOR);
		resultadoMamografiaController.getMapMamaD().get(keys[0]).setChecked(false);
		resultadoMamografiaController.getMapMamaD().get(keys[1]).setChecked(false);

		resultadoMamografiaController.getMapMamaD().get(keys[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		resultadoMamografiaController.getMapMamaD().get(keys[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);

		assimetriaDifusaCount--;
	}

	public void assimetriaDifusaCheckBox(String params) {
		String[] key = StringUtils.split(params, DELIMITADOR);

		if (!resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked()) {
			resultadoMamografiaController.getMapMamaD().get(key[2]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}
		if (!resultadoMamografiaController.getMapMamaD().get(key[1]).isChecked()) {
			resultadoMamografiaController.getMapMamaD().get(key[3]).setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
		}

		if (resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked() || resultadoMamografiaController.getMapMamaD().get(key[1]).isChecked()) {
			if (assimetriaDifusaCount == 0) {
				assimetriaDifusaCount++;
			}
		} else {
			if (assimetriaDifusaCount == 1 && !resultadoMamografiaController.getMapMamaD().get(key[0]).isChecked() && !resultadoMamografiaController.getMapMamaD().get(key[1]).isChecked()) {
				assimetriaDifusaCount--;
			}
		}
	}

	public String getRxMamaDireita() {
		return rxMamaDireita;
	}

	public void setRxMamaDireita(String rxMamaDireita) {
		this.rxMamaDireita = rxMamaDireita;
	}

	public String getHabilitaMamaDireita() {
		return habilitaMamaDireita;
	}

	public void setHabilitaMamaDireita(String habilitaMamaDireita) {
		this.habilitaMamaDireita = habilitaMamaDireita;
	}

	public boolean isvNaoInformadoDOk() {
		return vNaoInformadoDOk;
	}

	public void setvNaoInformadoDOk(boolean vNaoInformadoDOk) {
		this.vNaoInformadoDOk = vNaoInformadoDOk;
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
