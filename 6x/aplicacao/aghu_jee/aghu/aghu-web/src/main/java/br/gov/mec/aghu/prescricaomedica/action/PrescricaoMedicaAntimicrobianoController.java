package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;

public class PrescricaoMedicaAntimicrobianoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6094282462052947267L;
	@EJB
	private IParametroFacade parametroFacade;
	
	
	public boolean doVerificarMedicamentosAntimicrobianos(PrescricaoMedicaVO prescricaoMedicaVO, List<ItemPrescricaoMedicaVO> listaAntimicrobianos)
			throws ApplicationBusinessException {
		boolean apresentaModalFormularioAntimicrobianos = false;
		
		if (exibirFormularioAntiMicrobiano()) {
			for (ItemPrescricaoMedicaVO item : prescricaoMedicaVO.getItens()) {
				MpmPrescricaoMdto prescricaoMedicamento = item
						.getPrescricaoMedicamento();
				if (medicamentoAntiMicrobianoEconfirmado(prescricaoMedicamento)) {
					preencheDadosAntimicrobianos(item);
					listaAntimicrobianos.add(item);
					apresentaModalFormularioAntimicrobianos = true;
				}

			}
		}

		return apresentaModalFormularioAntimicrobianos;
	}

	private Boolean exibirFormularioAntiMicrobiano()
			throws ApplicationBusinessException {
		if (parametroFacade
				.verificarExisteAghParametro(AghuParametrosEnum.P_FORMULARIO_ANTIMICROBIANOS)) {
			AghParametros aghParam = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_FORMULARIO_ANTIMICROBIANOS);

			return aghParam.getVlrNumerico() != null
					&& aghParam.getVlrNumerico().equals(BigDecimal.ONE);
		}

		return Boolean.FALSE;
	}

	private boolean medicamentoAntiMicrobianoEconfirmado(
			MpmPrescricaoMdto prescricaoMedicamento) {
		return prescricaoMedicamento != null
				&& prescricaoMedicamento.getIndAntiMicrobiano()
				&& prescricaoMedicamento.getIndPendente().equals(
						DominioIndPendenteItemPrescricao.N);
	}

	/**
	 * Preenche as informaÃ§Ãµes inerentes aos medicamentos antimicrobianos de
	 * uso restrito.
	 * 
	 * @param prescricaoMedicamento
	 * @param item
	 */
	private void preencheDadosAntimicrobianos(ItemPrescricaoMedicaVO item) {
		// TODO: Mudar essa regra e utilizar o do método
		// ManterPrescricaoMedicaON.preencheDadosAntimicrobianos()
		if (item.getPrescricaoMedicamento().getIndAntiMicrobiano()) {
			MpmItemPrescricaoMdto itemPrescricaoMdto = null;

			// Quantidade de dias
			item.setDias(calculaQuantidadeDiasPrescricaoMedica(item).toString());

			itemPrescricaoMdto = calculaDiluente(item);
			// Dose
			if (StringUtils.isNotBlank(itemPrescricaoMdto.getFormaDosagem()
					.getDescricaoUnidadeMedidaMedica())) {
				item.setDosagem(itemPrescricaoMdto.getDoseFormatada()
						+ " "
						+ itemPrescricaoMdto.getFormaDosagem()
								.getDescricaoUnidadeMedidaMedica());
			} else if (itemPrescricaoMdto.getMedicamento()
					.getTipoApresentacaoMedicamento() != null) {
				item.setDosagem(itemPrescricaoMdto.getDoseFormatada()
						+ " "
						+ itemPrescricaoMdto.getMedicamento()
								.getTipoApresentacaoMedicamento().getSigla());
			}
			// Nome Medicamento
			if (itemPrescricaoMdto.getMedicamento() != null) {
				item.setNomeMedicamento(itemPrescricaoMdto.getMedicamento()
						.getDescricaoEditada());
			}
			// Via de administração
			if (item.getPrescricaoMedicamento().getViaAdministracao() != null) {
				item.setVia(item.getPrescricaoMedicamento()
						.getViaAdministracao().getSigla());
			}
			// Intervalo
			if (StringUtils.isNotBlank(item.getPrescricaoMedicamento()
					.getTipoFreqAprazamento().getSintaxe())) {
				item.setIntervalo(item
						.getPrescricaoMedicamento()
						.getTipoFreqAprazamento()
						.getSintaxeFormatada(
								item.getPrescricaoMedicamento().getFrequencia()));
			} else {
				item.setIntervalo(item.getPrescricaoMedicamento()
						.getTipoFreqAprazamento().getDescricao());
			}
			// Data fim de Duração
			// TODO
		}
	}

	private Integer calculaQuantidadeDiasPrescricaoMedica(
			ItemPrescricaoMedicaVO item) {
		Date dtPmeInicioVigencia = item.getPrescricaoMedicamento()
				.getPrescricaoMedica().getDtReferencia();
		Date dataInicioTratamento = item.getPrescricaoMedicamento()
				.getDthrInicioTratamento();
		Integer tempoDuracao = DateUtil.obterQtdDiasEntreDuasDatas(
				dtPmeInicioVigencia, dataInicioTratamento);
		if (dataInicioTratamento == null || tempoDuracao < 0) {
			tempoDuracao = 0;
		}
		return tempoDuracao;
	}

	private MpmItemPrescricaoMdto calculaDiluente(ItemPrescricaoMedicaVO item) {
		MpmItemPrescricaoMdto itemPrescricaoMdto;
		MpmPrescricaoMdto prescMedicamento = item.getPrescricaoMedicamento();
		if (prescMedicamento.getDiluente() != null
				&& prescMedicamento.getItensPrescricaoMdtos().size() > 1) {
			if (!prescMedicamento.getItensPrescricaoMdtos().get(0)
					.getMedicamento().getIndDiluente()) {
				itemPrescricaoMdto = prescMedicamento.getItensPrescricaoMdtos()
						.get(0);
			} else {
				itemPrescricaoMdto = prescMedicamento.getItensPrescricaoMdtos()
						.get(1);
			}
		} else {
			itemPrescricaoMdto = prescMedicamento.getItensPrescricaoMdtos()
					.get(0);
		}
		return itemPrescricaoMdto;
	}

}
