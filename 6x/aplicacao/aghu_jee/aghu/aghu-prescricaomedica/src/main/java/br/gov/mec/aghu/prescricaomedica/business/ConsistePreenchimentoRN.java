package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAgravoOutrasDoencas;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseForma;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndAids;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndCutanea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGangPerif;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGenitoUrinaria;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGestante;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndLaringea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndMeningite;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndMiliar;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOcular;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOssea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOutraExtraPulmonar;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndPleural;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSexo;
import br.gov.mec.aghu.model.MpmNotificacaoTb;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @ORADB PROCEDURE MPMP_CONSISTE_PREENCHIMENTO
 * @author aghu
 *
 */
@Stateless
public class ConsistePreenchimentoRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 4112234773991816625L;

	private static final Log LOG = LogFactory.getLog(ConsistePreenchimentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum ConsistePreenchimentoRNExceptionCode implements BusinessExceptionCode {
		MPM_03807, MPM_03823, MPM_03824, MPM_04085, MPM_04095, MPM_03829, MPM_03808, MPM_03809, MPM_04086, MPM_03810, MPM_03811, MPM_03812, MPM_03813, MPM_03802, MPM_03804, MPM_03814, MPM_03816, MPM_03818, MPM_03819, MPM_03820, MPM_03821, MPM_04087, MPM_03822, MPM_03805;
	}

	/**
	 * @ORADB PROCEDURE MPMP_CONSISTE_PREENCHIMENTO
	 * @param ntb
	 */
	public void consistirPreenchimento(final MpmNotificacaoTb ntb) throws ApplicationBusinessException {
		validarCampoNulo(ntb.getDtDiagnostico(), ConsistePreenchimentoRNExceptionCode.MPM_03823);

		if (ntb.getIndGestante() == null) {
			if (DominioNotificacaoTuberculoseSexo.F.equals(ntb.getSexo())) {
				throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_04085);
			} else {
				ntb.setIndGestante(DominioNotificacaoTuberculoseIndGestante.NAO_SE_APLICA);
			}
		}

		validarCampoNulo(ntb.getEscolaridade(), ConsistePreenchimentoRNExceptionCode.MPM_04095);

		if (DateUtil.validaDataMaior(ntb.getDtDiagnostico(), new Date()) || DateUtil.validaDataMenor(ntb.getDtDiagnostico(), ntb.getDtNascimento())) {
			throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_03829);
		}
		
		validarCampoNulo(ntb.getMunicipioResidencia(), ConsistePreenchimentoRNExceptionCode.MPM_03808);
		validarCampoNulo(ntb.getTipoEntrada(), ConsistePreenchimentoRNExceptionCode.MPM_03809);
		validarCampoNulo(ntb.getRaioxTorax(), ConsistePreenchimentoRNExceptionCode.MPM_03810);
//		validarCampoNulo(ntb.getTesteTuberculinico(), ConsistePreenchimentoRNExceptionCode.MPM_03811);
		validarCampoNulo(ntb.getIndAids(), ConsistePreenchimentoRNExceptionCode.MPM_03812);
		validarCampoNulo(ntb.getIndAlcoolismo(), ConsistePreenchimentoRNExceptionCode.MPM_03812);
		validarCampoNulo(ntb.getIndDiabetes(), ConsistePreenchimentoRNExceptionCode.MPM_03812);
		validarCampoNulo(ntb.getIndDoencaMental(), ConsistePreenchimentoRNExceptionCode.MPM_03812);
		validarCampoNulo(ntb.getForma(), ConsistePreenchimentoRNExceptionCode.MPM_03813);
		
		if (DominioNotificacaoTuberculoseAgravoOutrasDoencas.SIM.equals(ntb.getIndOutrasDoencas()) && StringUtils.isBlank(ntb.getDescOutroAgravo())) {
			throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_03804);
		}

		if (DominioNotificacaoTuberculoseForma.PULMONAR.equals(ntb.getForma())) {
			if (DominioNotificacaoTuberculoseIndPleural.PLEURAL.equals(ntb.getIndPleural()) || DominioNotificacaoTuberculoseIndGangPerif.GANG_PERIF.equals(ntb.getIndGangPerif()) || DominioNotificacaoTuberculoseIndGenitoUrinaria.PLEURAL.equals(ntb.getIndGenitoUrinaria()) || DominioNotificacaoTuberculoseIndOssea.OSSEA.equals(ntb.getIndOssea()) || DominioNotificacaoTuberculoseIndOcular.OCULAR.equals(ntb.getIndOcular()) || DominioNotificacaoTuberculoseIndMiliar.MILIAR.equals(ntb.getIndMiliar()) || DominioNotificacaoTuberculoseIndMeningite.MENINGITE.equals(ntb.getIndMeningite()) || DominioNotificacaoTuberculoseIndCutanea.CUTANEA.equals(ntb.getIndCutanea()) || DominioNotificacaoTuberculoseIndLaringea.LARINGEA.equals(ntb.getIndLaringea()) || DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO.equals(ntb.getIndOutraExtrapulmonar())) {
				throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_03802);
			}
		}

		consistirPreenchimentoContinuacao(ntb); // Continuação
	}

	/**
	 * Continuação da procedure PROCEDURE MPMP_CONSISTE_PREENCHIMENTO
	 * 
	 * @param ntb
	 * @throws ApplicationBusinessException
	 */
	private void consistirPreenchimentoContinuacao(final MpmNotificacaoTb ntb) throws ApplicationBusinessException {
		if (DominioNotificacaoTuberculoseForma.EXTRAPULMONAR.equals(ntb.getForma()) || DominioNotificacaoTuberculoseForma.PULMONAR_EXTRAPULMONAR.equals(ntb.getForma())) {
			if (DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO.equals(ntb.getIndOutraExtrapulmonar()) && StringUtils.isBlank(ntb.getDescrOutraExtrapulmonar())) {
				throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_03804);
			}
			if (StringUtils.isNotBlank(ntb.getDescrOutraExtrapulmonar()) && (ntb.getIndOutraExtrapulmonar() == null || !DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO.equals(ntb.getIndOutraExtrapulmonar()))) {
				ntb.setIndOutraExtrapulmonar(DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO);
			}
			if (StringUtils.isBlank(ntb.getDescrOutraExtrapulmonar()) && DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO.equals(ntb.getIndOutraExtrapulmonar())) {
				ntb.setIndOutraExtrapulmonar(null);
			}

			int contador = 0;
			contador = incrementarContador(DominioNotificacaoTuberculoseIndPleural.PLEURAL, ntb.getIndPleural(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndGangPerif.GANG_PERIF, ntb.getIndGangPerif(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndGenitoUrinaria.PLEURAL, ntb.getIndGenitoUrinaria(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndOssea.OSSEA, ntb.getIndOssea(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndOcular.OCULAR, ntb.getIndOcular(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndMiliar.MILIAR, ntb.getIndMiliar(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndMeningite.MENINGITE, ntb.getIndMeningite(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndCutanea.CUTANEA, ntb.getIndCutanea(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndLaringea.LARINGEA, ntb.getIndLaringea(), contador);
			contador = incrementarContador(DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO, ntb.getIndOutraExtrapulmonar(), contador);

			if (contador == 0 || contador > 2) {
				throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_03805);
			}
		}

		validarCampoNulo(ntb.getBaciloscopiaEscarro(), ConsistePreenchimentoRNExceptionCode.MPM_03814);
//		validarCampoNulo(ntb.getBaciloscopiaEscarro2(), ConsistePreenchimentoRNExceptionCode.MPM_03814);
		validarCampoNulo(ntb.getCulturaEscarro(), ConsistePreenchimentoRNExceptionCode.MPM_03816);

		if (DominioNotificacaoTuberculoseIndAids.SIM.equals(ntb.getIndAids()) && ntb.getHiv() == null) {
			throw new ApplicationBusinessException(ConsistePreenchimentoRNExceptionCode.MPM_03818);
		}

		validarCampoNulo(ntb.getHistopatologia(), ConsistePreenchimentoRNExceptionCode.MPM_03819);
		validarCampoNulo(ntb.getDtInicioTratAtual(), ConsistePreenchimentoRNExceptionCode.MPM_03820);
//		validarCampoNulo(ntb.getTratSupervisionado(), ConsistePreenchimentoRNExceptionCode.MPM_03821);
//		validarCampoNulo(ntb.getContatosRegistrados(), ConsistePreenchimentoRNExceptionCode.MPM_04087);
//		validarCampoNulo(ntb.getIndDoencaRelTrabalho(), ConsistePreenchimentoRNExceptionCode.MPM_03822);

//		if (verificarCamposAtualizar(ntb.getTipoNotificacao(), ntb.getDtNotificacao(), ntb.getMunicipioNotificacao(), ntb.getUnidadeDeSaude(), ntb.getDtDiagnostico(), ntb.getTipoEntrada(), ntb.getRaioxTorax(), ntb.getTesteTuberculinico(), ntb.getBaciloscopiaEscarro(), ntb.getBaciloscopiaEscarro2(), ntb.getCulturaEscarro(), ntb.getHiv(), ntb.getHistopatologia(), ntb.getDtInicioTratAtual(), ntb.getTratSupervisionado(), ntb.getContatosRegistrados(), ntb.getIndDoencaRelTrabalho(), ntb.getNomePaciente(), ntb.getDtNascimento(), ntb.getIdade(), ntb.getIdade(), ntb.getEspecIdade(), ntb.getSexo(), ntb.getNomeMae())) {
//			return true;
//		}
	}

	/**
	 * Reuso de código para validar nulos: Método para reduzir a complexidade e
	 * evitar violações de PMD
	 * 
	 * @param t
	 * @param exceptionCode
	 * @throws ApplicationBusinessException
	 */
	private <T> void validarCampoNulo(final T t, final BusinessExceptionCode exceptionCode) throws ApplicationBusinessException {
		if (t == null) {
			throw new ApplicationBusinessException(exceptionCode);
		}
	}

	/**
	 * Reuso de código para incrementar o contador: Método para reduzir a
	 * complexidade e evitar violações de PMD
	 * 
	 * @param dominio1
	 * @param dominio2
	 * @param contador
	 * @return
	 */
	private int incrementarContador(Dominio dominio1, Dominio dominio2, int contador) {
		return dominio1.equals(dominio2) ? ++contador : contador;
	}

	/**
	 * Reuso de código para o teste com atributos NOT NULL: Método para reduzir
	 * a complexidade e evitar violações de PMD
	 * 
	 * @param t
	 * @return
	 */
	private <T> boolean verificarCamposAtualizar(T... t) {
		for (T campo : t) {
			if (campo == null) {
				return false;
			}
		}
		return true;
	}
}