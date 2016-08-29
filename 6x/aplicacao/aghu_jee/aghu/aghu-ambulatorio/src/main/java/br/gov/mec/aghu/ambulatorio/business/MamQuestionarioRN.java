package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEspQuestionarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituarioCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.vo.CurCutVO;
import br.gov.mec.aghu.ambulatorio.vo.CurEspecialidadeVO;
import br.gov.mec.aghu.ambulatorio.vo.PreGeraItemQuestVO;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MamRespostaEvolucoesId;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MamQuestionarioRN extends BaseBusiness {

	private static final long serialVersionUID = -4260894945736738855L;

	private static final Log LOG = LogFactory.getLog(MamQuestionarioRN.class);

	@Inject
	private MamQuestaoDAO mamQuestaoDAO;

	@Inject
	private MamEspQuestionarioDAO mamEspQuestionarioDAO;

	@Inject
	private MamReceituarioCuidadoDAO MamReceituarioCuidadoDAO;

	@Inject
	private MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO;

	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Override
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * @ORADB: P_PRE_GERA_ITEM_QUEST #49992 P1
	 */
	public List<PreGeraItemQuestVO> pPreGeraItemQuest(Long pEvoSeq,
			Short espSeq, Integer pTieSeq, Integer pPacCodigo, String indTipoPac) {

		List<PreGeraItemQuestVO> listaPreGErGeraItemQuestVO = new ArrayList<PreGeraItemQuestVO>();

		Short vSeqp;
		Boolean vAchouRea;
		Boolean vConformeSexoPac;
		Boolean vQuestEsp;
		Boolean vGeraResposta;
		Boolean vTemRotEspec;
		DominioSexo vSexo;
		String vResposta = "";
		Short vEspSeq;

		vSeqp = 0;

		vTemRotEspec = this.mamEspQuestionarioDAO.obterCursorCurRotEspec(
				espSeq, indTipoPac, pTieSeq);

		List<CurCutVO> listaCurCutVO = mamQuestaoDAO.obterListaCursorCutVO(
				pTieSeq, indTipoPac);

		for (CurCutVO registro : listaCurCutVO) {

			if (registro.getSexoQuestao().equals("Q")) {
				vConformeSexoPac = Boolean.TRUE;
			} else {
				vSexo = this.aipPacientesDAO.obterSexoPaciente(pPacCodigo);
				if (vSexo.equals(DominioSexo.getInstance(registro.getSexoQuestao()))) {
					vConformeSexoPac = Boolean.TRUE;
				} else {
					vConformeSexoPac = Boolean.FALSE;
				}
			}

			vAchouRea = this.mamRespostaEvolucoesDAO.obterBooleanCursorCurRea(
					pEvoSeq, registro.getQutSeq(), registro.getSeqp());

			if (!vAchouRea && vConformeSexoPac) {
				vSeqp = this.mamRespostaEvolucoesDAO.obterMaxShortPMaisUm(
						pEvoSeq, registro.getQutSeq(), registro.getSeqp());

				if (registro.getFueSeq() != null) {
					vResposta = this.ambulatorioFacade.mamcExecFncEdicao(
							registro.getFueSeq(), pPacCodigo);
				} else {
					if (registro.getTextoFormatado() != null
							&& !registro.getTextoFormatado().trim().isEmpty()) {
						vResposta = registro.getTextoFormatado();
					} else {
						vResposta = null;
					}
				}

				vQuestEsp = this.MamReceituarioCuidadoDAO
						.obterCurQuestEsp(registro.getQutSeq());

				if (vQuestEsp) {
					Boolean retornoEspSeq = this.MamReceituarioCuidadoDAO
							.obterCurEsp(registro.getQutSeq(), espSeq);
					if (!retornoEspSeq) {
						vEspSeq = null;
					} else {
						vEspSeq = espSeq;
					}
				} else {
					vQuestEsp = Boolean.FALSE;
					vEspSeq = null;
				}

				vGeraResposta = validarVGeraResposta(vTemRotEspec, vEspSeq,
						vQuestEsp);

				if (vGeraResposta) {

					PreGeraItemQuestVO novo = new PreGeraItemQuestVO();
					novo.setPergunta(registro.getDescricao());
					novo.setResposta(vResposta);
					novo.setOrdem(registro.getOrdemVisualizacao());
					novo.setQusQutSeq(registro.getQutSeq());
					novo.setQusSeqP(registro.getSeqp());
					novo.setSeqP(vSeqp);
					novo.setpEvoSeq(pEvoSeq);
					novo.setvEspSeq(vEspSeq);

					listaPreGErGeraItemQuestVO.add(novo);
					
					MamRespostaEvolucoes novaResposta = new MamRespostaEvolucoes();
					MamRespostaEvolucoesId id = new MamRespostaEvolucoesId(novo.getpEvoSeq(), novo.getQusQutSeq(), novo.getQusSeqP(), novo.getSeqP());
					novaResposta.setId(id);
					novaResposta.setResposta(novo.getResposta());
					novaResposta.setEspSeq(novo.getvEspSeq());
					
					this.mamRespostaEvolucoesDAO.persistir(novaResposta);
				}
			}
		}

		return listaPreGErGeraItemQuestVO;
	}

	public boolean validarVGeraResposta(Boolean vTemRotEspec, Short vEspSeq,
			Boolean vQuestEsp) {

		if (vTemRotEspec && vEspSeq == null) {
			return Boolean.TRUE;
		} else if (!vQuestEsp) {
			return Boolean.TRUE;
		} else if (vQuestEsp && vEspSeq != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * @ORADB: MAMK_GENERICA.MAMC_GET_TIPO_PAC #49992 P2
	 */
	public String mamCGetTipoPac(Integer conNumero) {

		Integer vClinicaPediatra = 0;
		String vMsg = "";

		Integer vClcCodigo;
		Boolean vIndEspPediatricaLido;

		if (StringUtils.isNotBlank(vMsg)) {
			vClinicaPediatra = 0;
		}

		CurEspecialidadeVO curEsp = this.aacConsultasDAO
				.obterCurEspPorNumeroConsulta(conNumero);
		vClcCodigo = curEsp.getClcCodigo();
		vIndEspPediatricaLido = curEsp.getIndEspPediatrica();

		if (vIndEspPediatricaLido || (vClcCodigo == vClinicaPediatra)) {
			return "P";
		} else {
			return "A";
		}
	}
}