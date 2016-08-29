package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatProcedHospInternoON extends BaseBusiness {


@EJB
private ManterFatProcedHospInternosRN manterFatProcedHospInternosRN;

private static final Log LOG = LogFactory.getLog(FatProcedHospInternoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatProcedHospInternosDAO fatProcedHospInternosDAO;

	public static final Integer TAM_MAXIMO_DESCRICAO = 100;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2662097217325895799L;

	public enum FatProcedHospInternoONExceptionCode implements
			BusinessExceptionCode {
		LABEL_PHI_FATOR_CONVERSAO_ENTRE, ERRO_INCLUSAO_PROCEDIMENTOS_HOSPITALARES_INTERNOS;
	}

	/**
	 * Persiste Cadastro de Procedimentos Hospitalares Internos
	 * 
	 * @param procedHospInterno
	 * @throws BaseException
	 */
	public void persistirFatCompatExclusItem(
			FatProcedHospInternos procedHospInterno) throws BaseException {
		validarFormulario(procedHospInterno);

		getManterFatProcedHospInternosRN().validarConstraintAntesPersistir(
				procedHospInterno);

		if (procedHospInterno.getSeq() == null) {// insert
			getManterFatProcedHospInternosRN().inserirFatProcedHospInternos(
					procedHospInterno);
		} else { // update
			getManterFatProcedHospInternosRN().atualizarFatProcedHospInternos(
					procedHospInterno);
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void validarFormulario(FatProcedHospInternos procedHospInterno)
			throws BaseException {

		Integer totalNaoNulos = 0;
		if (procedHospInterno.getMaterial() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getProcedimentoCirurgico() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getProcedimentoEspecialDiverso() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getComponenteSanguineo() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getProcedHemoterapico() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getExameMaterialAnalise() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getItemMedicacao() != null) {
			totalNaoNulos++;
		}
		if (procedHospInterno.getItemExame() != null) {
			totalNaoNulos++;
		}

		if (totalNaoNulos > 1) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.FAT_00290);
		}

		// Somente um procedimento hospitalar interno pode ter o tipo de
		// nutrição enteral (TIPO_NUTRICAO_ENTERAL) do tipo Adulto (‘A’).
		if (DominioTipoNutricaoParenteral.A.equals(procedHospInterno
				.getTipoNutricaoEnteral())) {
			if (getFatProcedHospInternosDAO()
					.contaProcedimentoHospitalarInternoPorNutricaoEnteral(
							procedHospInterno.getTipoNutricaoEnteral()) > 0) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_01120);
			}
		}

		// Somente um procedimento hospitalar interno pode ter o tipo de
		// nutrição enteral (TIPO_NUTRICAO_ENTERAL) do tipo Neonatal (‘N’)
		if (DominioTipoNutricaoParenteral.N.equals(procedHospInterno
				.getTipoNutricaoEnteral())) {
			if (getFatProcedHospInternosDAO()
					.contaProcedimentoHospitalarInternoPorNutricaoEnteral(
							procedHospInterno.getTipoNutricaoEnteral()) > 0) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_01120);
			}
		}

		// Somente um procedimento hospitalar interno pode ter o tipo de
		// nutrição enteral (TIPO_NUTRICAO_ENTERAL) do tipo Neonatal (‘P’)
		if (DominioTipoNutricaoParenteral.P.equals(procedHospInterno
				.getTipoNutricaoEnteral())) {
			if (getFatProcedHospInternosDAO()
					.contaProcedimentoHospitalarInternoPorNutricaoEnteral(
							procedHospInterno.getTipoNutricaoEnteral()) > 0) {
				throw new ApplicationBusinessException(
						FaturamentoExceptionCode.FAT_01120);
			}
		}

		// Ao informar o tipo de operação e não informar o fator de conversão
		if (procedHospInterno.getTipoOperConversao() != null
				&& procedHospInterno.getFatorConversao() == null) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.FAT_00878);
		}

		// Ao informar o fator de conversão e não informar o tipo de operação
		if (procedHospInterno.getTipoOperConversao() == null
				&& procedHospInterno.getFatorConversao() != null) {
			throw new ApplicationBusinessException(
					FaturamentoExceptionCode.FAT_00879);
		}

		if (procedHospInterno.getFatorConversao() != null
				&& (procedHospInterno.getFatorConversao().doubleValue() < -9999.999 || procedHospInterno
						.getFatorConversao().doubleValue() > 9999.999)) {
			throw new ApplicationBusinessException(
					FatProcedHospInternoONExceptionCode.LABEL_PHI_FATOR_CONVERSAO_ENTRE,
					-9999.999, 9999.999);
		}
	}

	public void insereFaturamentoHospitalInternoParaMpmCuidadoUsual(
			MpmCuidadoUsual cuidadoUsual) throws ApplicationBusinessException {
		FatProcedHospInternos procedimentos = new FatProcedHospInternos();

		try {
			if (cuidadoUsual.getDescricao().length() > TAM_MAXIMO_DESCRICAO) {
				procedimentos.setDescricao(cuidadoUsual.getDescricao()
						.substring(1, TAM_MAXIMO_DESCRICAO));
			} else {
				procedimentos.setDescricao(cuidadoUsual.getDescricao());
			}

			procedimentos.setSituacao(cuidadoUsual.getIndSituacao());

			if ((procedimentos.getTidSeq() == null)
					&& (procedimentos.getCuiSeq() == null)
					&& (procedimentos.getCduSeq() == null)
					&& (procedimentos.getEuuSeq() == null)) {
				procedimentos.setIndOrigemPresc(true);
			} else {
				procedimentos.setIndOrigemPresc(false);
			}

			procedimentos.setCduSeq(cuidadoUsual.getSeq());

			this.getFatProcedHospInternosDAO().persistir(procedimentos);
			this.getFatProcedHospInternosDAO().flush();

		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					FatProcedHospInternoONExceptionCode.ERRO_INCLUSAO_PROCEDIMENTOS_HOSPITALARES_INTERNOS);
		}

	}

	public void atualizaFaturamentoInternoParaMpmCuidadoUsual(MpmCuidadoUsual cuidadoUsual)
			throws ApplicationBusinessException {

		FatProcedHospInternos procedimentos = getFatProcedHospInternosDAO()
				.obterPorCuidadoUsual(cuidadoUsual.getSeq());

		if (procedimentos != null) {

			String descProcedimento = procedimentos.getDescricao() != null ? procedimentos
					.getDescricao() : "";

			if (!cuidadoUsual.getIndSituacao().equals(
					procedimentos.getSituacao())
					|| !cuidadoUsual.getDescricao().equals(descProcedimento)) {

				procedimentos.setDescricao(cuidadoUsual.getDescricao());
				procedimentos.setSituacao(cuidadoUsual.getIndSituacao());
				this.getFatProcedHospInternosDAO().atualizar(
						procedimentos);
				this.getFatProcedHospInternosDAO().flush();

			}
		}
	}

	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected ManterFatProcedHospInternosRN getManterFatProcedHospInternosRN() {
		return manterFatProcedHospInternosRN;
	}

}
