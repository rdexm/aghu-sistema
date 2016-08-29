package br.gov.mec.aghu.prescricaomedica.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioTipoComponenteNpt;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoAdultoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * #989 - Calcular Nutrição Parenteral Total
 * 
 * @author aghu
 *
 */
public class CalculoNptController extends ActionController {

	private static final long serialVersionUID = -5102526199042928122L;
	private static final String PAGE_CADASTRO_PRESCRICAO_NPT = "prescricaomedica-cadastroPrescricaoNpt";
	private static final String MODAL_PESO_ALTURA = "modalPesoAlturaWG";
	private static final String MODAL_CANCELAR = "modalCancelarWG";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private MpmPrescricaoNptVO prescricaoNptVo = new MpmPrescricaoNptVO();

	private CalculoAdultoNptVO calculoAdultoNptVO = new CalculoAdultoNptVO();

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void iniciar() {

		if (this.prescricaoNptVo == null) {
			throw new IllegalArgumentException();
		}
		try {
			this.calculoAdultoNptVO = this.prescricaoMedicaFacade.iniciarCalculoNpt(this.prescricaoNptVo);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*
	 * Eventos dos campos
	 */

	/**
	 * PC09: Chamada para PROCEDURE MPMP_CALCULA_A
	 * 
	 * @param tipo
	 *            Tipo de componente NPT
	 */
	public void calcular(final String tipoValor) {
		if (StringUtils.isBlank(tipoValor)) {
			throw new IllegalArgumentException();
		}
		final DominioTipoComponenteNpt tipo = DominioTipoComponenteNpt.getInstance(tipoValor.trim().toUpperCase());
		try {

			if (DominioTipoComponenteNpt.TIG.equals(tipo)) { // Proporção Glicose 50%/10% (ID11/ID12)
				this.prescricaoMedicaFacade.validarProporcaoGlicose50Menor100(this.calculoAdultoNptVO.getParamPercGlic50()); // ON1
				this.prescricaoMedicaFacade.validarProporcaoGlicose10Menor100(this.calculoAdultoNptVO.getParamPercGlic10()); // ON2
				this.prescricaoMedicaFacade.validarSomaProporcaoGlicose(this.calculoAdultoNptVO.getParamPercGlic50(), this.calculoAdultoNptVO.getParamPercGlic10()); // ON3
			}

			if (DominioTipoComponenteNpt.TEMPO_INFUSAO_SOLUCAO.equals(tipo)) { // Tempo Infusão Solução (ID35)
				this.prescricaoMedicaFacade.validarIntervaloTempoInfusaoSolucao(this.calculoAdultoNptVO.getParamTempInfusaoSol()); // ON4
			}

			if (DominioTipoComponenteNpt.TEMPO_INFUSAO_LIPIDIOS.equals(tipo)) { // Tempo Infusão Lipídios (ID46)
				this.prescricaoMedicaFacade.validarIntervaloTempoInfusaoLipidios(this.calculoAdultoNptVO.getParamTempInfusaoLip()); // ON5
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*
	 * Botões de ação
	 */

	public String gravar() {
		try {
			this.prescricaoMedicaFacade.gravarCalculoNpt(this.prescricaoNptVo, this.calculoAdultoNptVO);
			apresentarMsgNegocio("NPT_CALCULO_ADULTO_SUCESSO");
			return redirecionarCadastroPrescricaoNpt();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "NPT_CALCULO_ADULTO_ERRO", e.getMessage());
		}
		return null;
	}

	/**
	 * Modal de Dados Peso/Altura
	 */
	public void atualizarPesoAltura() {
		try {
			this.prescricaoMedicaFacade.atualizarPesoAlturaCalculoNpt(this.prescricaoNptVo, this.calculoAdultoNptVO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		openDialog(MODAL_PESO_ALTURA);
	}

	/**
	 * Modal Cancelar
	 */
	public void cancelar() {
		openDialog(MODAL_CANCELAR);
	}

	/**
	 * ON06: Limpar VOs e redirecionar para tela anterior (#990: Prescrever NPT)
	 * 
	 * @return
	 */
	public String redirecionarCadastroPrescricaoNpt() {
		this.calculoAdultoNptVO = new CalculoAdultoNptVO();
		this.prescricaoNptVo = null;
		return PAGE_CADASTRO_PRESCRICAO_NPT;
	}

	/*
	 * Getters and Setters
	 */

	public MpmPrescricaoNptVO getPrescricaoNptVo() {
		return prescricaoNptVo;
	}

	public void setPrescricaoNptVo(MpmPrescricaoNptVO prescricaoNptVo) {
		this.prescricaoNptVo = prescricaoNptVo;
	}

	public CalculoAdultoNptVO getCalculoAdultoNptVO() {
		return calculoAdultoNptVO;
	}
}