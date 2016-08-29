package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidMedicamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CuidadoMedicamentoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CuidadoMedicamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpePrescCuidMedicamentoDAO epePrescCuidMedicamentoDAO;

@EJB
private IFarmaciaFacade farmaciaFacade;

	private static final long serialVersionUID = -8820808693043397271L;

	public enum MedicamentoRNExceptionCode implements BusinessExceptionCode {
		EPE_00179, MENSAGEM_ERRO_EXCLUSAO_MANTER_MEDICAMENTOS_CUIDADOS
	}

	public List<AfaMedicamento> pesquisarMedicamentosParaMedicamentosCuidados(String parametro) {
		return getFarmaciaFacade().pesquisarMedicamentosParaMedicamentosCuidados(parametro);
	}

	public Long pesquisarMedicamentosParaMedicamentosCuidadosCount(String parametro) {
		return getFarmaciaFacade().pesquisarMedicamentosParaMedicamentosCuidadosCount(parametro);
	}

	public List<AfaMedicamento> pesquisarMedicamentosParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getFarmaciaFacade().pesquisarMedicamentosParaListagemMedicamentosCuidados(matCodigo, indSituacao, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(Integer matCodigo, DominioSituacaoMedicamento indSituacao) {
		return getFarmaciaFacade().pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(matCodigo, indSituacao);
	}

	public AfaMedicamento obterMedicamentoPorMatCodigo(Integer matCodigo) {
		return getFarmaciaFacade().obterMedicamentoPorMatCodigo(matCodigo);
	}

	/**
	 * @throws ApplicationBusinessException
	 *             #4961 RN1
	 */
	public void prePersistirValidarHoras(Integer horasAntes, Integer horasApos) throws ApplicationBusinessException {
		if (horasAntes == null && horasApos == null) {
			throw new ApplicationBusinessException(MedicamentoRNExceptionCode.EPE_00179);
		}
	}

	/**
	 * @throws ApplicationBusinessException
	 *             #4961 RN2
	 */
	public void preRemoverValidarCuidadoRelacionadoPrescricao(Short cuiSeq, Integer medMatCodigo) throws ApplicationBusinessException {
		if (getEpePrescCuidMedicamentoDAO().verificarCuidadoRelacionadoPrescricao(cuiSeq, medMatCodigo)) {
			throw new ApplicationBusinessException(MedicamentoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_MANTER_MEDICAMENTOS_CUIDADOS);
		}
	}

	protected EpePrescCuidMedicamentoDAO getEpePrescCuidMedicamentoDAO() {
		return epePrescCuidMedicamentoDAO;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

}
