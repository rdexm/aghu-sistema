package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioIndUsoCm;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceTipoMovimentosRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceTipoMovimentosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private SceTipoMovimentosDAO sceTipoMovimentosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8910932375236591327L;

	public enum SceTipoMovimentosRNExceptionCode implements BusinessExceptionCode {
		SCE_00371, SCE_00282,SCE_00702, SCE_00440,SCE_00420,SCE_00421,SCE_00423,SCE_00424,ALMOXARIFADO_NAO_INFORMADO,SCE_00427,SCE_00439;
	}

	/**
	 * ORADB scec_tmv_cmpl_ativ
	 * Verifica se o SceTipoMovimentos recebido não está ativo, gerando erro.
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	public SceTipoMovimento verificarTipoMovimentoAtivo(Short seq) throws ApplicationBusinessException {

		SceTipoMovimento sceTipoMovimentosAtivo = getSceTipoMovimentosDAO().obterSceTipoMovimentosAtivoPorSeq(seq);

		if (sceTipoMovimentosAtivo == null) {

			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00371);

		}

		return sceTipoMovimentosAtivo;

	}

	/**
	 * ORADB  SCEK_MMT_RN.RN_MMTP_CONSIST_MVTO
	 * Verifica se o SceTipoMovimentos recebido
	 * @param long1
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public SceTipoMovimento verificarTipoMovimento(SceTipoMovimentoId sceTipoMovimentosId, SceMovimentoMaterial sceMovimentoMaterial ) throws BaseException {

		SceTipoMovimento sceTipoMovimentosAtivo = getSceTipoMovimentosDAO().obterPorChavePrimaria(sceTipoMovimentosId);

		if (sceTipoMovimentosAtivo == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00282);
		}

		//Msg: Tipo de Movimento inativo
		if (sceTipoMovimentosAtivo.getIndSituacao()!=null && sceTipoMovimentosAtivo.getIndSituacao().equals(DominioSituacao.I) && sceMovimentoMaterial.getIndEstorno().equals(DominioSimNao.N)) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00702);

		}

		//Msg: Tipo de Movimento não deve gerar registro de movimento
		if (sceTipoMovimentosAtivo.getIndGeraRegMovimento()!=null && sceTipoMovimentosAtivo.getIndGeraRegMovimento() == false) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00440);

		}

		//Msg: Motivo deve ser informado
		if (sceMovimentoMaterial.getMotivoMovimento() != null) {
			if(sceTipoMovimentosAtivo.getIndExigeMotivo() != null  && sceTipoMovimentosAtivo.getIndExigeMotivo() == true && sceMovimentoMaterial.getMotivoMovimento().getId().getTmvSeq() == null && sceMovimentoMaterial.getHistorico() == null) {
				throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00420);
			}
		}

		//Msg: Histórico deve ser informado
		if (sceTipoMovimentosAtivo.getIndGuardaHistorico() == true && sceMovimentoMaterial.getHistorico() == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00421);

		}

		//Msg: Número do Documento deve ser informado
		if (sceTipoMovimentosAtivo.getIndNroDocGeracao() == true && sceMovimentoMaterial.getNroDocGeracao() == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00423);

		}

		//Msg: Centro de Custo Requisição deve ser informado
		if (sceTipoMovimentosAtivo.getIndCcustoRequisita() == true && sceMovimentoMaterial.getCentroCustoRequisita() == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00424);

		}

		//Msg: Almoxarifado deve ser informado
		if (sceTipoMovimentosAtivo.getIndAlmox() == true && sceMovimentoMaterial.getAlmoxarifado() == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.ALMOXARIFADO_NAO_INFORMADO);

		}

		//Msg: Almoxarifado Complementar deve ser informado
		if (sceTipoMovimentosAtivo.getIndAlmoxComplemento() == true && sceMovimentoMaterial.getAlmoxarifadoComplemento() == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00427);

		}

		//Msg: Almoxarifado Complementar deve ser informado
		if (sceTipoMovimentosAtivo.getIndAlmoxComplemento() == true && sceMovimentoMaterial.getAlmoxarifadoComplemento() == null) {
			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00427);

		}

		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_PI);

		if (sceMovimentoMaterial.getTipoMovimento() != null && aghParametro.equals(sceMovimentoMaterial.getTipoMovimento().getId().getSeq())) {

			if (sceMovimentoMaterial.getValor() == null) {

				sceTipoMovimentosAtivo.setIndUsoCm(DominioIndUsoCm.U);

			} else {

				sceTipoMovimentosAtivo.setIndUsoCm(DominioIndUsoCm.C);

			}

		}

		if (sceTipoMovimentosAtivo.getIndUsoCm().equals(DominioIndUsoCm.C) && sceMovimentoMaterial.getValor() == null) {

			throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00439);

		}

		return sceTipoMovimentosAtivo;

	}



	/**
	 * ORADB SCEK_SCE_RN.RN_SCEP_VER_TMV_ATIV
	 * Verifica se o SceTipoMovimentos recebido não está ativo, gerando erro.
	 * @param SceTipoMovimento
	 * @throws ApplicationBusinessException
	 */
	public SceTipoMovimento verificarTipoMovimentoAtivo(SceTipoMovimento sceTipoMovimentos) throws ApplicationBusinessException{

		SceTipoMovimento sceTipoMovimentosAtivo =null;

		if(sceTipoMovimentos!=null){

			sceTipoMovimentosAtivo = getSceTipoMovimentosDAO().obterSceTipoMovimentosAtivoPorSceTipoMovimentos(sceTipoMovimentos);

			if(sceTipoMovimentosAtivo == null) {

				throw new ApplicationBusinessException(SceTipoMovimentosRNExceptionCode.SCE_00282);

			}

		}
		return sceTipoMovimentosAtivo;
	}

	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}

	public String obterSiglaTipoMovimento(AghuParametrosEnum pSiglaTipoMovimento) throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(pSiglaTipoMovimento).getVlrTexto();
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

}
