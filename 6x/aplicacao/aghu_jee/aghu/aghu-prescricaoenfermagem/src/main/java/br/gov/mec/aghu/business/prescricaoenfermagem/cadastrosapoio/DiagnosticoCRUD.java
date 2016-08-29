package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DiagnosticoCRUD extends BaseBusiness {

	@EJB
	private DiagnosticoRN diagnosticoRN;

	private static final Log LOG = LogFactory.getLog(DiagnosticoCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private EpeDiagnosticoDAO epeDiagnosticoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1925619591771334384L;

	public enum DiagnosticoCRUDExceptionCode implements BusinessExceptionCode {

		MENSAGEM_DIAGNOSTICO_JA_EXISTENTE

	}

	public void persistirDiagnostico(EpeDiagnostico diagnostico)
			throws ApplicationBusinessException {

		// Verificar se o registro já existe com a mesma desrição
		List<EpeDiagnostico> diagList = getEpeDiagnosticoDAO()
				.pesquisarDiagnosticosTodos(diagnostico.getDescricao(),
						diagnostico.getId().getSnbGnbSeq(),
						diagnostico.getId().getSnbSequencia());

		if (diagList != null && diagList.size() > 0) {//1 porque tem o registro que está sendo alterado.
			for (EpeDiagnostico epeDiag : diagList) {
				if (!epeDiag.getId().getSequencia().equals(diagnostico.getId().getSequencia()) && epeDiag.getDescricao().equals(diagnostico.getDescricao())) {
					throw new ApplicationBusinessException(
							DiagnosticoCRUDExceptionCode.MENSAGEM_DIAGNOSTICO_JA_EXISTENTE);
				}
			}
		}

		if (diagnostico.getId().getSequencia() != null) { // edição

			diagnosticoRN.prePersistirValidarEpeSubgrupoNecesBasica(diagnostico
					.getSubgrupoNecesBasica().getId()); // RN5

			if (diagnostico.getSituacao().equals(DominioSituacao.I)) {
				diagnosticoRN.prePersistirValidarEpeCaractDefDiagnostico(
						diagnostico.getId().getSnbGnbSeq(), diagnostico.getId()
								.getSnbSequencia(), diagnostico.getId()
								.getSequencia()); // RN6
				diagnosticoRN.prePersistirValidarEpeFatRelDiagnostico(
						diagnostico.getId().getSnbGnbSeq(), diagnostico.getId()
								.getSnbSequencia(), diagnostico.getId()
								.getSequencia()); // RN7
			}
			epeDiagnosticoDAO.merge(diagnostico);

		} else { // insert

			diagnosticoRN.prePersistirValidarEpeSubgrupoNecesBasica(diagnostico
					.getSubgrupoNecesBasica().getId()); // RN5
			Short sequencia = epeDiagnosticoDAO
					.pesquisarMaxSequenciaDiagnosticos(diagnostico.getId()
							.getSnbGnbSeq(), diagnostico.getId()
							.getSnbSequencia());
			diagnostico.getId().setSequencia((short) (sequencia + 1));
			epeDiagnosticoDAO.persistir(diagnostico);
		}
	}

	public void excluirDiagnostico(EpeDiagnosticoId diagnosticoId)
			throws ApplicationBusinessException {
		EpeDiagnostico diagnostico = epeDiagnosticoDAO
				.obterPorChavePrimaria(diagnosticoId);
		diagnosticoRN
				.preRemoverValidarEpeCaractDefDiagnosticoEpeFatRelDiagnostico(
						diagnostico.getId().getSnbGnbSeq(), diagnostico.getId()
								.getSnbSequencia(), diagnostico.getId()
								.getSequencia()); // RN8
		epeDiagnosticoDAO.removerPorId(diagnosticoId);
	}

	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}

	protected DiagnosticoRN getDiagnosticoRN() {
		return diagnosticoRN;
	}

}
