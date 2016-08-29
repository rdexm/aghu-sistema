package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.VisualizaDadosSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #1000 Prescrição: Visualizar Dados da Solicitação de Consultoria
 * 
 * @author aghu
 *
 */
@Stateless
public class VisualizaDadosSolicitacaoConsultoriaON extends BaseBusiness {

	private static final long serialVersionUID = 1141752067756732866L;

	private static final Log LOG = LogFactory.getLog(VisualizaDadosSolicitacaoConsultoriaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	public VisualizaDadosSolicitacaoConsultoriaVO obterDadosSolicitacaoConsultoria(final Integer atdSeq, final Integer seq) throws ApplicationBusinessException {
		final VisualizaDadosSolicitacaoConsultoriaVO vo = this.mpmSolicitacaoConsultoriaDAO.obterDadosSolicitacaoConsultoria(atdSeq, seq);

		if (vo != null) {

			// RF02
			vo.setIdade(this.blocoCirurgicoFacade.obterIdadePorDataNascimento(vo.getDataNascimento()));

			// RF03
			vo.setLocalizacao(this.prescricaoMedicaFacade.buscarResumoLocalPaciente(this.aghuFacade.obterAghAtendimentoPorChavePrimaria(vo.getIdAtdSeq())));

			// RF04
			vo.setDataAtendimento(this.prescricaoMedicaFacade.obterDataInternacao(vo.getInternacao(), vo.getAtendimentoUrgencia(), null));

			// VCS
			List<VRapServidorConselho> vcs = this.registroColaboradorFacade.pesquisarConselhoPorMatriculaVinculo(vo.getVcsMatricula(), vo.getVcsVinculo());
			if (!vcs.isEmpty()) {
				VRapServidorConselho conselho = vcs.get(0);
				vo.setEquipe(conselho.getNome());
			}

			// VCS2
			List<VRapServidorConselho> vcs2 = this.registroColaboradorFacade.pesquisarConselhoPorMatriculaVinculo(vo.getVcs2Matricula(), vo.getVcs2Vinculo());

			if (!vcs2.isEmpty()) {

				VRapServidorConselho conselho = null;

				for (VRapServidorConselho item : vcs2) {
					if (StringUtils.isNotBlank(item.getSigla()) && StringUtils.isNotBlank(item.getNroRegConselho())) {
						conselho = item;
						break;
					}
				}

				if (conselho != null) {
					vo.setSolicitante(conselho.getNome());
					vo.setConselho(conselho.getSigla());
					vo.setNumeroConselho(conselho.getNroRegConselho());
				} else {
					conselho = vcs2.get(0);
					vo.setSolicitante(conselho.getNome());
					vo.setConselho(conselho.getSigla());
					vo.setNumeroConselho(conselho.getNroRegConselho());
				}

			}

		}

		return vo;

	}

}
